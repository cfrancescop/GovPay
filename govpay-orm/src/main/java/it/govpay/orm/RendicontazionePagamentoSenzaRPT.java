/*
 * GovPay - Porta di Accesso al Nodo dei Pagamenti SPC 
 * http://www.gov4j.it/govpay
 * 
 * Copyright (c) 2014-2016 Link.it srl (http://www.link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package it.govpay.orm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for RendicontazionePagamentoSenzaRPT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RendicontazionePagamentoSenzaRPT">
 * 		&lt;sequence>
 * 			&lt;element name="FR" type="{http://www.govpay.it/orm}FR" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="FrApplicazione" type="{http://www.govpay.it/orm}FrApplicazione" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="RendicontazioneSenzaRPT" type="{http://www.govpay.it/orm}RendicontazioneSenzaRPT" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="SingoloVersamento" type="{http://www.govpay.it/orm}SingoloVersamento" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Versamento" type="{http://www.govpay.it/orm}Versamento" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Giovanni Bussu (bussu@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RendicontazionePagamentoSenzaRPT", 
  propOrder = {
  	"fr",
  	"frApplicazione",
  	"rendicontazioneSenzaRPT",
  	"singoloVersamento",
  	"versamento"
  }
)

@XmlRootElement(name = "RendicontazionePagamentoSenzaRPT")

public class RendicontazionePagamentoSenzaRPT extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RendicontazionePagamentoSenzaRPT() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public FR getFr() {
    return this.fr;
  }

  public void setFr(FR fr) {
    this.fr = fr;
  }

  public FrApplicazione getFrApplicazione() {
    return this.frApplicazione;
  }

  public void setFrApplicazione(FrApplicazione frApplicazione) {
    this.frApplicazione = frApplicazione;
  }

  public RendicontazioneSenzaRPT getRendicontazioneSenzaRPT() {
    return this.rendicontazioneSenzaRPT;
  }

  public void setRendicontazioneSenzaRPT(RendicontazioneSenzaRPT rendicontazioneSenzaRPT) {
    this.rendicontazioneSenzaRPT = rendicontazioneSenzaRPT;
  }

  public SingoloVersamento getSingoloVersamento() {
    return this.singoloVersamento;
  }

  public void setSingoloVersamento(SingoloVersamento singoloVersamento) {
    this.singoloVersamento = singoloVersamento;
  }

  public Versamento getVersamento() {
    return this.versamento;
  }

  public void setVersamento(Versamento versamento) {
    this.versamento = versamento;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static it.govpay.orm.model.RendicontazionePagamentoSenzaRPTModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.govpay.orm.RendicontazionePagamentoSenzaRPT.modelStaticInstance==null){
  			it.govpay.orm.RendicontazionePagamentoSenzaRPT.modelStaticInstance = new it.govpay.orm.model.RendicontazionePagamentoSenzaRPTModel();
	  }
  }
  public static it.govpay.orm.model.RendicontazionePagamentoSenzaRPTModel model(){
	  if(it.govpay.orm.RendicontazionePagamentoSenzaRPT.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.govpay.orm.RendicontazionePagamentoSenzaRPT.modelStaticInstance;
  }


  @XmlElement(name="FR",required=true,nillable=false)
  protected FR fr;

  @XmlElement(name="FrApplicazione",required=true,nillable=false)
  protected FrApplicazione frApplicazione;

  @XmlElement(name="RendicontazioneSenzaRPT",required=true,nillable=false)
  protected RendicontazioneSenzaRPT rendicontazioneSenzaRPT;

  @XmlElement(name="SingoloVersamento",required=true,nillable=false)
  protected SingoloVersamento singoloVersamento;

  @XmlElement(name="Versamento",required=true,nillable=false)
  protected Versamento versamento;

}