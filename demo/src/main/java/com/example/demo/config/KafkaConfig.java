package com.example.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {


    @Bean
    NewTopic userEventsTopic()  {
        return TopicBuilder.name("user-events")
                .partitions(3)
                .replicas(1)
              //  .configs(Map.of("min.ins", "1"))
                .build();
    }

    
}
 