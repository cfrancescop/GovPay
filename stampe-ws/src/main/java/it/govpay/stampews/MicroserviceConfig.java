package it.govpay.stampews;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name="eureka.client.serviceUrl.defaultZone")
@Configuration
@EnableDiscoveryClient
public class MicroserviceConfig {
	
}
