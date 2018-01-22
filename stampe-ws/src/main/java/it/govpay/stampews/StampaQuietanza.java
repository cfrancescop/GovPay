package it.govpay.stampews;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

public class StampaQuietanza {
	public static class VoceQuietanza {
		public String getCausale() {
			return causale;
		}
		public void setCausale(String causale) {
			this.causale = causale;
		}
		public String getDescrizione() {
			return descrizione;
		}
		public void setDescrizione(String descrizione) {
			this.descrizione = descrizione;
		}
		public BigDecimal getImporto() {
			return importo;
		}
		public void setImporto(BigDecimal importo) {
			this.importo = importo;
		}
		private String causale;
		private String descrizione;
		private BigDecimal importo;
	}
	
	@NotNull
	private BigDecimal totale;
	/**
	 * NOME E COGNOME
	 */
	private String intestatarioDenominazione;
	/**
	 * CODICE FISCALE
	 */
	private String intestatarioIdentificativo;
	/**
	 *  INDIRIZZO CIVICO
	 */
	private String intestatarioIndirizzo;
	/**
	 * CAP CITTA (PROVINCIA)
	 */
	private String intestatarioArea;
	/**
	 * RAGIONE SOCIALE ENTE
	 */
	private String enteDenominazione;
	/**
	 * RAGIONE SOCIALE COMUNE
	 */
	private String enteArea;
	/**
	 * CODICE DOMINIO
	 */
	private String enteIndentificativo;
	
	public BigDecimal getTotale() {
		return totale;
	}
	public void setTotale(BigDecimal importo) {
		this.totale = importo;
	}
	public String getIntestatarioDenominazione() {
		return intestatarioDenominazione;
	}
	public void setIntestatarioDenominazione(String intestatarioDenominazione) {
		this.intestatarioDenominazione = intestatarioDenominazione;
	}
	public String getIntestatarioIdentificativo() {
		return intestatarioIdentificativo;
	}
	public void setIntestatarioIdentificativo(String intestatarioIdentificativo) {
		this.intestatarioIdentificativo = intestatarioIdentificativo;
	}
	public String getIntestatarioIndirizzo() {
		return intestatarioIndirizzo;
	}
	public void setIntestatarioIndirizzo(String intestatarioIndirizzo) {
		this.intestatarioIndirizzo = intestatarioIndirizzo;
	}
	public String getEnteDenominazione() {
		return enteDenominazione;
	}
	public void setEnteDenominazione(String enteDenominazione) {
		this.enteDenominazione = enteDenominazione;
	}
	public String getEnteArea() {
		return enteArea;
	}
	public void setEnteArea(String enteArea) {
		this.enteArea = enteArea;
	}
	public String getEnteIndentificativo() {
		return enteIndentificativo;
	}
	public void setEnteIndentificativo(String enteIndentificativo) {
		this.enteIndentificativo = enteIndentificativo;
	}
	
	public List<VoceQuietanza> getCausali() {
		return causali;
	}
	public void setCausali(List<VoceQuietanza> causali) {
		this.causali = causali;
	}
	public String getIntestatarioArea() {
		return intestatarioArea;
	}
	public void setIntestatarioArea(String intestatarioArea) {
		this.intestatarioArea = intestatarioArea;
	}
	/*<parameter name="intestatario_denominazione" class="java.lang.String"/>
	<parameter name="intestatario_identificativo" class="java.lang.String"/>
	<parameter name="intestatario_indirizzo_1" class="java.lang.String"/>
	<parameter name="intestatario_indirizzo_2" class="java.lang.String"/>
	<parameter name="ente_denominazione" class="java.lang.String"/>
	<parameter name="ente_area" class="java.lang.String"/>
	<parameter name="ente_identificativo" class="java.lang.String"/>*/
	List<VoceQuietanza> causali=new ArrayList<>();
}	
