// java-app/src/main/java/com/lab/ObsLabApplication.java
package com.lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@RestController
public class ObsLabApplication {

    private static final Logger log = LoggerFactory.getLogger(ObsLabApplication.class);
    private final RestTemplate restTemplate = new RestTemplate();

    // Obtenemos el nombre del servicio desde variables de entorno (A, B o C)
    private String getServiceId() {
        return System.getenv().getOrDefault("SERVICE_ID", "A");
    }

    // Obtenemos la URL del siguiente servicio desde variables de entorno
    private String getNextServiceUrl() {
        return System.getenv().getOrDefault("NEXT_SERVICE_URL", "");
    }

    @GetMapping("/trigger")
    @Timed(value = "app.trigger.time", description = "Time to process /trigger", histogram = true)
    public String trigger() {
        String myId = getServiceId();
        log.info("Servicio {} recibió petición /trigger", myId);

        String nextUrl = getNextServiceUrl();
        
        if (nextUrl != null && !nextUrl.isEmpty()) {
            // Si hay un siguiente servicio, lo llamamos (Esto genera la Traza)
            log.info("Servicio {} llamando a {}", myId, nextUrl);
            try {
                String response = restTemplate.getForObject(nextUrl + "/trigger", String.class);
                return "Servicio " + myId + " dice: " + response;
            } catch (Exception e) {
                return "Servicio " + myId + " falló al llamar a siguiente: " + e.getMessage();
            }
        } else {
            // Si no hay siguiente (Servicio C), terminamos
            log.info("Servicio {} es el final del camino.", myId);
            return "Servicio " + myId + " dice: Fin del proceso.";
        }
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    public static void main(String[] args) {
        SpringApplication.run(ObsLabApplication.class, args);
    }
}