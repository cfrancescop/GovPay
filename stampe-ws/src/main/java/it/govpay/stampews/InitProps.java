package it.govpay.stampews;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.govpay.stampe.pdf.rt.utils.RicevutaPagamentoProperties;

@Configuration
public class InitProps {
	@Bean
	public RicevutaPagamentoProperties ricevutaPagamentoProperties() throws Exception {
		return RicevutaPagamentoProperties.newInstance("");
	}
}
