package it.govpay.stampews;

import it.gov.digitpa.schemas._2011.pagamenti.CtRicevutaTelematica;

public class PrintRtRequest {
	private CtRicevutaTelematica rt;
	String causale;
	String auxDigit;
	String applicationCode;
	private String esito;
	
	
	public String getCausale() {
		return causale;
	}
	public void setCausale(String causale) {
		this.causale = causale;
	}
	public String getAuxDigit() {
		return auxDigit;
	}
	public void setAuxDigit(String auxDigit) {
		this.auxDigit = auxDigit;
	}
	public String getApplicationCode() {
		return applicationCode;
	}
	public void setApplicationCode(String applicationCode) {
		this.applicationCode = applicationCode;
	}
	public CtRicevutaTelematica getRt() {
		return rt;
	}
	public void setRt(CtRicevutaTelematica rt) {
		this.rt = rt;
	}
	public String getEsito() {
		return esito;
	}
	public void setEsito(String esito) {
		this.esito = esito;
	}
}