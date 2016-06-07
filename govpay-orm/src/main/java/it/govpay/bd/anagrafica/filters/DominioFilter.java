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
package it.govpay.bd.anagrafica.filters;

import java.util.List;

import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.dao.IExpressionConstructor;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;

import it.govpay.bd.AbstractFilter;
import it.govpay.bd.ConnectionManager;
import it.govpay.bd.FilterSortWrapper;
import it.govpay.orm.Dominio;
import it.govpay.orm.dao.jdbc.converter.DominioFieldConverter;

public class DominioFilter extends AbstractFilter {

	private String codStazione = null;
	private List<Long> idDomini = null; 
	private CustomField cf;
	
	private String codDominio = null;
	
	public enum SortFields {
	}
	
	public DominioFilter(IExpressionConstructor expressionConstructor) {
		super(expressionConstructor);
		
		try{
			DominioFieldConverter converter = new DominioFieldConverter(ConnectionManager.getJDBCServiceManagerProperties().getDatabase()); 
			this.cf = new CustomField("id", Long.class, "id", converter.toTable(it.govpay.orm.Dominio.model()));
		} catch(Exception e){
			
		}
	}

	@Override
	public IExpression toExpression() throws ServiceException {
		try {
			IExpression newExpression = this.newExpression();
			boolean addAnd = false;
			
			if(this.getCodStazione() != null){
				newExpression.equals(it.govpay.orm.Dominio.model().ID_STAZIONE.COD_STAZIONE, this.getCodStazione());
				addAnd = true;
			}
			
			if(this.idDomini != null && this.idDomini.size() > 0 ){
				if(addAnd)
					newExpression.and();
				
				newExpression.in(cf, idDomini);
				addAnd = true;
			}
			
			if(this.codDominio != null){
				IExpression exp = this.newExpression();
				//1 provo a convertirlo in un long
				long l = -1l;
				try{
					l =Long.parseLong(this.codDominio);
				}catch (NumberFormatException e){
					l = -1l;
				}

				// se e' un numero valido maggiore di zero
				if(l>0){
					exp.equals(this.cf, l);
					exp.or();
				}
				
				// 2. metto in or l'eventuale stringa per il nome dell'dominio
				exp.ilike(Dominio.model().COD_DOMINIO, this.codDominio,LikeMode.ANYWHERE);
				
				newExpression.and(exp);
			}
			
			return newExpression;
		} catch (NotImplementedException e) {
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			throw new ServiceException(e);
		}
	}

	public void addSortField(SortFields field, boolean asc) {
		FilterSortWrapper filterSortWrapper = new FilterSortWrapper();
		filterSortWrapper.setSortOrder((asc ? SortOrder.ASC : SortOrder.DESC));
		this.filterSortList.add(filterSortWrapper);
	}

	public String getCodStazione() {
		return codStazione;
	}

	public void setCodStazione(String codStazione) {
		this.codStazione = codStazione;
	}

	public List<Long> getIdDomini() {
		return idDomini;
	}

	public void setIdDomini(List<Long> idDomini) {
		this.idDomini = idDomini;
	}

	public String getCodDominio() {
		return codDominio;
	}

	public void setCodDominio(String codDominio) {
		this.codDominio = codDominio;
	}
	
	
}