// java-app/src/main/java/com/lab/MetricsConfig.java
package com.lab;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterFilter enableHistogramsForHttpServerRequests() {
        return new MeterFilter() {
            @Override
            public DistributionStatisticConfig configureMeterFilter(org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsProvider.MeterFilterMetadata metadata, DistributionStatisticConfig config) {
                // No usamos este método, lo sobrescribimos abajo
                return config;
            }
            
            @Override
            public MeterFilter.IdAndTags mapIdAndTags(MeterFilter.IdAndTags idAndTags) {
                return idAndTags;
            }
            
            @Override
            public DistributionStatisticConfig configure(org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsProvider.MeterFilterMetadata metadata, DistributionStatisticConfig config) {
                return config;
            }
        };
    }
    
    // Método SIMPLE y funcional para Spring Boot 3
    @Bean
    public MeterFilter enableHttpServerRequestsHistogram(MeterRegistry registry) {
        MeterFilter filter = new MeterFilter() {
            @Override
            public DistributionStatisticConfig configure(org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsProvider.MeterFilterMetadata id, DistributionStatisticConfig config) {
                if (id.getName().startsWith("http.server.requests")) {
                    return DistributionStatisticConfig.builder()
                        .percentilesHistogram(true)  // ✅ Habilita buckets para Prometheus
                        .serviceLevelObjectives(0.01, 0.05, 0.1, 0.25, 0.5, 1.0, 2.0, 5.0) // Buckets en segundos
                        .build()
                        .merge(config);
                }
                return config;
            }
        };
        registry.config().meterFilter(filter);
        return filter;
    }
}