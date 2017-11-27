/*
 * GovPay - Porta di Accesso al Nodo dei Pagamenti SPC 
 * http://www.gov4j.it/govpay
 * 
 * Copyright (c) 2014-2017 Link.it srl (http://www.link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package it.govpay.model.avvisi;

public class AvvisoPagamento{
	
	public enum StatoAvviso {
		DA_STAMPARE, STAMPATO
	}
	
	private Long id;
	private java.lang.String codDominio;
	private java.lang.String iuv;
	private java.util.Date dataCreazione;
	private StatoAvviso stato;
	private byte[] pdf;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public java.lang.String getCodDominio() {
		return codDominio;
	}
	public void setCodDominio(java.lang.String codDominio) {
		this.codDominio = codDominio;
	}
	public java.lang.String getIuv() {
		return iuv;
	}
	public void setIuv(java.lang.String iuv) {
		this.iuv = iuv;
	}
	public java.util.Date getDataCreazione() {
		return dataCreazione;
	}
	public void setDataCreazione(java.util.Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}
	public StatoAvviso getStato() {
		return stato;
	}
	public void setStato(StatoAvviso stato) {
		this.stato = stato;
	}
	public byte[] getPdf() {
		return pdf;
	}
	public void setPdf(byte[] pdf) {
		this.pdf = pdf;
	}
	
}
