package it.govpay.model;

import java.math.BigDecimal;
import java.util.Date;

public class RicevutaPagamento {
	
	private Dominio dominioCreditore;
	private Anagrafica anagraficaCreditore;
	private Anagrafica anagraficaVersante;
	private Anagrafica anagraficaAttestante;
	private Date dataScadenza;
	private Date dataPagamento;
	private String iuv;
	private BigDecimal importoDovuto;
	private BigDecimal importoPagato;
	private BigDecimal commissioni;
	private Anagrafica anagraficaDebitore;
	private String causale;
	private String descrizioneCausale;
	private String codVersamento;
	private String codAvviso;
	private String ccp;
	private String psp;
	private String canale;
	private String idRiscossione;
	private String codDominio;
	private byte[] logoDominioCreditore=null;
	
	public Dominio getDominioCreditore() {
		return dominioCreditore;
	}
	public void setDominioCreditore(Dominio dominioCreditore) {
		this.dominioCreditore = dominioCreditore;
	}
	public Anagrafica getAnagraficaCreditore() {
		return anagraficaCreditore;
	}
	public void setAnagraficaCreditore(Anagrafica anagraficaCreditore) {
		this.anagraficaCreditore = anagraficaCreditore;
	}
	public Date getDataScadenza() {
		return dataScadenza;
	}
	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}
	public Date getDataPagamento() {
		return dataPagamento;
	}
	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	public String getIuv() {
		return iuv;
	}
	public void setIuv(String iuv) {
		this.iuv = iuv;
	}
	public BigDecimal getImportoDovuto() {
		return importoDovuto;
	}
	public void setImportoDovuto(BigDecimal importoDovuto) {
		this.importoDovuto = importoDovuto;
	}
	public BigDecimal getImportoPagato() {
		return importoPagato;
	}
	public void setImportoPagato(BigDecimal importoPagato) {
		this.importoPagato = importoPagato;
	}
	public Anagrafica getAnagraficaDebitore() {
		return anagraficaDebitore;
	}
	public void setAnagraficaDebitore(Anagrafica anagraficaDebitore) {
		this.anagraficaDebitore = anagraficaDebitore;
	}
	public String getCausale() {
		return causale;
	}
	public void setCausale(String causale) {
		this.causale = causale;
	}
	public String getCodVersamento() {
		return codVersamento;
	}
	public void setCodVersamento(String codVersamento) {
		this.codVersamento = codVersamento;
	}
	public String getCodAvviso() {
		return codAvviso;
	}
	public void setCodAvviso(String codAvviso) {
		this.codAvviso = codAvviso;
	}
	public String getCcp() {
		return ccp;
	}
	public void setCcp(String ccp) {
		this.ccp = ccp;
	}
	public String getPsp() {
		return psp;
	}
	public void setPsp(String psp) {
		this.psp = psp;
	}
	public String getCanale() {
		return canale;
	}
	public void setCanale(String canale) {
		this.canale = canale;
	}
	public String getIdRiscossione() {
		return idRiscossione;
	}
	public void setIdRiscossione(String idRiscossione) {
		this.idRiscossione = idRiscossione;
	}
	public String getCodDominio() {
		return codDominio;
	}
	public void setCodDominio(String codDominio) {
		this.codDominio = codDominio;
	}
	public Anagrafica getAnagraficaVersante() {
		return anagraficaVersante;
	}
	public void setAnagraficaVersante(Anagrafica anagraficaVersante) {
		this.anagraficaVersante = anagraficaVersante;
	}
	public Anagrafica getAnagraficaAttestante() {
		return anagraficaAttestante;
	}
	public void setAnagraficaAttestante(Anagrafica anagraficaAttestante) {
		this.anagraficaAttestante = anagraficaAttestante;
	}
	public BigDecimal getCommissioni() {
		return commissioni;
	}
	public void setCommissioni(BigDecimal commissioni) {
		this.commissioni = commissioni;
	}
	public String getDescrizioneCausale() {
		return descrizioneCausale;
	}
	public void setDescrizioneCausale(String descrizioneCausale) {
		this.descrizioneCausale = descrizioneCausale;
	}
	public byte[] getLogoDominioCreditore() {
		return logoDominioCreditore;
	}
	public void setLogoDominioCreditore(byte[] logoDominioCreditore) {
		this.logoDominioCreditore = logoDominioCreditore;
	}
	
}
