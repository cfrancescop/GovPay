package it.govpay.stampe.pdf.rt;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import it.govpay.model.Anagrafica;
import it.govpay.model.RicevutaPagamento;
import it.govpay.stampe.pdf.rt.utils.RicevutaPagamentoProperties;
import it.govpay.stampe.pdf.rt.utils.RicevutaPagamentoUtils;

public class IRicevutaPagamentoTest {
	IRicevutaPagamento iRicevutaPagamento;
	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger();
	@Before
	public void setup() throws Exception {
		iRicevutaPagamento = new RicevutaPagamentoPdf();
		RicevutaPagamentoProperties.newInstance("");
	}
	@Test
	public void testStampa() throws Exception {
		byte[] logo=new byte[0];
		RicevutaPagamentoProperties ricevutaPagamentoProperties = RicevutaPagamentoProperties.getInstance();
		assertNotNull(ricevutaPagamentoProperties);
		Properties propertiesAvvisoPagamentoDominioTributo = RicevutaPagamentoUtils.getRicevutaPagamentoPropertiesPerDominioTributo(ricevutaPagamentoProperties, "0", log);
		assertNotNull(propertiesAvvisoPagamentoDominioTributo);
		try(OutputStream os = new FileOutputStream("/tmp/test.pdf")){
			
			RicevutaPagamento ricevuta=new RicevutaPagamento();
			ricevuta.setImportoPagato(BigDecimal.TEN);
			Anagrafica anagraficaDebitore=new Anagrafica();
			anagraficaDebitore.setCodUnivoco("PIRRONE FRANCESCO (PRRFNC91L10C349N)");
			Anagrafica anagraficaCreditore=new Anagrafica();
			anagraficaCreditore.setCodUnivoco("Comune di Torino (00514490010)");
			ricevuta.setAnagraficaCreditore(anagraficaCreditore);
			ricevuta.setAnagraficaDebitore(anagraficaDebitore);
			ricevuta.setDataPagamento(new Date());
			iRicevutaPagamento.getPdfRicevutaPagamento(ricevuta, propertiesAvvisoPagamentoDominioTributo, os, log);
		}
	}
}
