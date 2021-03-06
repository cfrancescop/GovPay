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
package it.govpay.web.rs.dars.anagrafica.domini.input;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import it.govpay.web.rs.dars.model.RawParamValue;
import it.govpay.web.rs.dars.model.input.dinamic.InputFile;
import it.govpay.web.utils.Utils;

public class Logo extends InputFile {
	
//	private String dominioId = null;
	private String nomeServizio = null;
	private String abilitaModificaLogoId = null;

	public Logo(String nomeServizio,String id, String label, List<String> acceptedMimeTypes, long maxByteSize, int maxFiles, URI refreshUri, List<RawParamValue> values,Object... objects) {
		super(id, label, acceptedMimeTypes, maxByteSize, maxFiles, refreshUri, values);
		Locale locale = objects[1] != null ? (Locale) objects[1] : null;
		this.nomeServizio = nomeServizio;
//		this.dominioId = Utils.getInstance(locale).getMessageFromResourceBundle(this.nomeServizio + ".id.id");
		this.abilitaModificaLogoId = Utils.getInstance(locale).getMessageFromResourceBundle(this.nomeServizio + ".abilitaModificaLogo.id");
	}

	@Override
	protected byte[] getDefaultValue(List<RawParamValue> values, Object... objects) {
		return null;
	}

	@Override
	protected boolean isRequired(List<RawParamValue> values, Object... objects) {
		return false;
	}

	@Override
	protected boolean isHidden(List<RawParamValue> values, Object... objects) {
		String abilitaModificaLogoVAlue = Utils.getValue(values, this.abilitaModificaLogoId);
		if(StringUtils.isNotEmpty(abilitaModificaLogoVAlue) && abilitaModificaLogoVAlue.equalsIgnoreCase("false")){
			return true;
		}
		
		return false;
	}

	@Override
	protected boolean isEditable(List<RawParamValue> values, Object... objects) {
		return true;
	}

}
