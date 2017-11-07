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

package it.govpay.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.InMemoryDocument;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.dss.x509.CertificateToken;
import eu.europa.esig.dss.x509.CommonTrustedCertificateSource;



public class SignUtils {
	
	private static Transformer tf; 
			
	static {
		try {
			tf = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e) {
		} catch (TransformerFactoryConfigurationError e) {
		}
	}

	/**
	 * extract ds:Object from .xades file
	 *  
	 * @param xadesIn .xades file input stream
	 * @return base64 decoded bytes
	 * @throws Exception
	 */
	public static byte[] cleanXadesSignedFile(byte[] signed) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(signed));
		NodeList nl = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
		for(int i=0; i<nl.getLength(); i++){
			nl.item(i).getParentNode().removeChild(nl.item(i));
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		tf.transform(new DOMSource(doc),  new StreamResult(baos));
		return baos.toByteArray();
	}
	
	public static byte[] cleanCadesSignedFile(byte[] rt) throws KeyStoreException, CMSException, IOException {
		CMSSignedData cms = new CMSSignedData(rt);
		return ((byte[]) cms.getSignedContent().getContent());
	}
	    
}
