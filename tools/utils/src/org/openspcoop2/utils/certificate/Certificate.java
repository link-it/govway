/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.certificate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Certificate
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Certificate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<CertificateInfo> certificateChain = new ArrayList<>();
	private CertificateInfo certificate = null;

	public Certificate(String name, java.security.cert.X509Certificate cParam) {
		init(name, cParam, null);
	}
	public Certificate(String name, java.security.cert.X509Certificate cParam, List<java.security.cert.X509Certificate> chainParam) {
		java.security.cert.X509Certificate [] chainParamArray = null;
		if(chainParam!=null && chainParam.size()>0) {
			chainParamArray = chainParam.toArray(new java.security.cert.X509Certificate [1] );
		}
		init(name, cParam, chainParamArray);
	}
	public Certificate(String name, java.security.cert.X509Certificate cParam, java.security.cert.X509Certificate [] chainParam) {
		init(name, cParam, chainParam);
	}
	private void init(String name, java.security.cert.X509Certificate cParam, java.security.cert.X509Certificate [] chainParam) {
		this.certificate = new CertificateInfo(cParam, name);
		if(chainParam!=null && chainParam.length>0) {
			for (int i = 0; i < chainParam.length; i++) {
				this.certificateChain.add(new CertificateInfo(chainParam[i], name+"-chain-"+i));
			}
		}
	}
	
	public List<CertificateInfo> getCertificateChain() {
		return this.certificateChain;
	}
	public void setCertificateChain(List<CertificateInfo> certificateChain) {
		this.certificateChain = certificateChain;
	}
	public CertificateInfo getCertificate() {
		return this.certificate;
	}
	public void setCertificate(CertificateInfo certificate) {
		this.certificate = certificate;
	}	
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append(this.certificate.toString());
		if(this.certificateChain!=null) {
			bf.append("\n\n certificate chains:\n");
			for (CertificateInfo certificateInfo : this.certificateChain) {
				bf.append("\n");
				bf.append(certificateInfo.toString());
			}
		}
		return bf.toString();
	}
}
