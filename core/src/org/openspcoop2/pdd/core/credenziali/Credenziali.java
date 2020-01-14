/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.core.credenziali;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.transport.Credential;
import org.openspcoop2.utils.transport.http.HttpServletCredential;
import org.slf4j.Logger;

/**
 * Classe utilizzata per rappresentare le informazioni utilizzata da un generico connettore.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Credenziali  extends HttpServletCredential implements java.io.Serializable  {

	public final static boolean SHOW_BASIC_PASSWORD = true;
	public final static boolean SHOW_ISSUER = true;
	public final static boolean SHOW_DIGEST_CLIENT_CERT = true;
	public final static boolean SHOW_SERIAL_NUMBER_CLIENT_CERT = true;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Credenziali(){
		super();
	}
	public Credenziali(Credential credentials){
		if(credentials!=null){

			this.principal = credentials.getPrincipalObject();
			this.principalName = credentials.getPrincipal();

			this.subject = credentials.getSubject();
			this.issuer = credentials.getIssuer();
			this.certificate = credentials.getCertificate();

			this.username = credentials.getUsername();
			this.password = credentials.getPassword();

			this.bearerToken = credentials.getBearerToken();

		}
	}
	public Credenziali(HttpServletRequest req){
		super(req, null);
	}
	public Credenziali(HttpServletRequest req,Logger log){
		super(req, log);
	}




	@Override
	public boolean equals(Object c){
		return this.toString().equals(((Credenziali)c).toString());
	}

	@Override
	public String toString(){
		return this.toString(false, false, false, false);
	}
	public String toString(boolean showBasicPassword, boolean showIssuer, boolean showDigestClientCert, boolean showSerialNumberClientCert){
		return this.toString(showBasicPassword, showIssuer, showDigestClientCert, showSerialNumberClientCert, 
				"( ", " ) ", ", ");
	}
	public String toString(boolean showBasicPassword, boolean showIssuer, boolean showDigestClientCert, boolean showSerialNumberClientCert, 
			String start, String end, String separator){
		String credenzialiFornite = "";
		if (this.getUsername() != null || this.getSubject() != null || this.getPassword() != null) {
			credenzialiFornite = start;
			boolean printPrincipal = true;
			if (this.getUsername() != null){
				String label = "BasicUsername";
				if (this.getPrincipal() != null && this.getPrincipal().equals(this.getUsername())){
					label = label + "/Principal";
					printPrincipal = false;
				}
				if(this.getPassword()==null)
					credenzialiFornite = credenzialiFornite + label+" '"+ this.getUsername() + "'"+separator+"BasicPassword undefined";
				else if("".equals(this.getPassword()) )
					credenzialiFornite = credenzialiFornite + label+" '"+ this.getUsername() + "'"+separator+"BasicPassword empty";
				else{
					if(showBasicPassword){
						credenzialiFornite = credenzialiFornite + label+" '"+ this.getUsername() + "'"+separator+"BasicPassword '"+this.getPassword()+"'";
					}
					else{
						credenzialiFornite = credenzialiFornite + label+" '"+ this.getUsername() + "'";
					}
				}
			}
			if (this.getSubject() != null){
				String label = "SSL-Subject";
				if (this.getPrincipal() != null && this.getPrincipal().equals(this.getSubject())){
					label = label + "/Principal";
					printPrincipal = false;
				}
				if (this.getUsername() != null){
					label = separator + label;
				}
				credenzialiFornite = credenzialiFornite + label+" '"+ this.getSubject() + "'";

				if(showIssuer) {
					if(this.getIssuer()!=null){
						label = separator+"SSL-Issuer";
						credenzialiFornite = credenzialiFornite + label+" '"+ this.getIssuer() + "'";
					}
				}
				if(showDigestClientCert) {
					if(this.getCertificate()!=null && this.getCertificate().getCertificate()!=null) {
						String digest = null;
						try {
							digest = this.getCertificate().getCertificate().digestBase64Encoded();
							label = separator+"SSL-ClientCert-Digest";
							credenzialiFornite = credenzialiFornite + label+" '"+ digest + "'";
						}catch(Exception e) {
							LoggerWrapperFactory.getLogger(Credenziali.class).error("Errore Digest Certificato: "+e.getMessage(),e);
						}		
					}
				}
				if(showSerialNumberClientCert) {
					if(this.getCertificate()!=null && this.getCertificate().getCertificate()!=null) {				
						label = separator+"SSL-ClientCert-SerialNumber";
						credenzialiFornite = credenzialiFornite + label+" '"+ this.getCertificate().getCertificate().getSerialNumber() + "'";
					}
				}
			}
			if (this.getPrincipal() != null && printPrincipal){
				String label = "Principal";
				if ( (this.getUsername() != null) || (this.getSubject() != null) ){
					label = separator + label;
				}
				credenzialiFornite = credenzialiFornite + label+ " '"+ this.getPrincipal() + "'";
			}
			credenzialiFornite = credenzialiFornite + end;
		}
		return credenzialiFornite;
	}
}
