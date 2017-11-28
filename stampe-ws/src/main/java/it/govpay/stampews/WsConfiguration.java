package it.govpay.stampews;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.govpay.stampe.pdf.avvisoPagamento.AvvisoPagamentoPdf;
import it.govpay.stampe.pdf.avvisoPagamento.utils.AvvisoPagamentoProperties;
import it.govpay.stampe.pdf.rt.IRicevutaPagamento;
import it.govpay.stampe.pdf.rt.RicevutaPagamentoPdf;
import it.govpay.stampe.pdf.rt.utils.RicevutaPagamentoProperties;
@Configuration
public class WsConfiguration {
	private static final String GOVPAY_DIR = "/var/govpay";

	@Bean
	public AvvisoPagamentoPdf avvisoPdf() {
		return AvvisoPagamentoPdf.getInstance();
	}
	@Bean
	public AvvisoPagamentoProperties avvisoPagamentoProperties() throws Exception {
		return AvvisoPagamentoProperties.newInstance(GOVPAY_DIR);
	}
	
	@Bean
	public RicevutaPagamentoProperties ricevutaPagamentoProperties() throws Exception {
		return RicevutaPagamentoProperties.newInstance(GOVPAY_DIR);
	}
	
	@Bean
	public IRicevutaPagamento iRicevutaPagamento() {
		return new RicevutaPagamentoPdf();
	}
}
