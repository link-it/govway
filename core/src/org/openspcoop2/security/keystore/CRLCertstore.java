/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.security.keystore;

import java.io.Serializable;
import java.security.cert.CertStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.List;
import java.util.Map;

import org.openspcoop2.security.SecurityException;

/**
 * CRLCertstore
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CRLCertstore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private org.openspcoop2.utils.certificate.CRLCertstore crl = null;
	
	public CRLCertstore(String crlPaths) throws SecurityException {
		this(crlPaths, null);
	}
	public CRLCertstore(String crlPaths, Map<String, byte[]> localResources) throws SecurityException {
		List<String> crlPathsList = org.openspcoop2.utils.certificate.CRLCertstore.readCrlPaths(crlPaths);
		try {
			this._init(crlPathsList, localResources);
		}catch(Throwable t) {
			throw new SecurityException(t.getMessage(),t);
		}
	}
	
	public CRLCertstore(List<String> crlPaths) throws SecurityException{
		this(crlPaths, null);
	}
	public CRLCertstore(List<String> crlPaths, Map<String, byte[]> localResources) throws SecurityException{
		try {
			this._init(crlPaths, localResources);
		}catch(Throwable t) {
			throw new SecurityException(t.getMessage(),t);
		}
	}

	private void _init(List<String> crlPaths, Map<String, byte[]> localResources) throws SecurityException{
		try{
			this.crl = new org.openspcoop2.utils.certificate.CRLCertstore(crlPaths, localResources);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
	}
	
	public CertificateFactory getCertFactory() throws SecurityException {
		try {
			return this.crl.getCertFactory();
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}

	public List<X509CRL> getCaCrls() throws SecurityException {
		try {
			return this.crl.getCaCrls();
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}

	public CertStore getCertStore() throws SecurityException {
		try {
			return this.crl.getCertStore();
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	public org.openspcoop2.utils.certificate.CRLCertstore getWrappedCRLCertStore() {
		return this.crl;
	}
	
}
