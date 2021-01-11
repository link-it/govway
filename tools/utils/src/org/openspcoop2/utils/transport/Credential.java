/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.transport;

import java.io.Serializable;
import java.security.Principal;

import org.apache.commons.lang.NotImplementedException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.certificate.Certificate;

/**
 * Credentials
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Credential implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// getUserPrincipal (SERVLET API)
	protected String principalName;
	protected Principal principal;
	
	// SSL (HTTPS)
	protected String subject;
	protected String issuer;
	protected Certificate certificate;
	
	// Basic (HTTP-Based)
	protected String username;
	protected String password;
	
	// Bearer (token)
	protected String bearerToken;
		
	
	public Credential(){
		
	}
	
	
	public Principal getPrincipalObject() {
		return this.principal;
	}
	public void setPrincipalObject(Principal principalObject) {
		this.principal = principalObject;
	}
	public String getPrincipal() {
		return this.principalName;
	}
	public void setPrincipal(String principal) {
		this.principalName = principal;
	}
	public String getSubject() {
		return this.subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getIssuer() {
		return this.issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getBearerToken() {
		return this.bearerToken;
	}
	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}
	public Certificate getCertificate() {
		return this.certificate;
	}
	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}
	
	public boolean isUserInRole(String role){
		throw new NotImplementedException();
	}
	
	public Object getAttribute(String attributeName){
		throw new NotImplementedException();
	}
	
	public Object getAttribute(String role, String attributeName){
		throw new NotImplementedException();
	}

	
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();

		if(this.principal!=null){
			
			if(bf.length()>0){
				bf.append(" ");
			}
			
			bf.append("principal(");
			bf.append(this.principal);
			bf.append(")");
		}
				
		if(this.subject!=null){
			
			if(bf.length()>0){
				bf.append(" ");
			}
			
			bf.append("subject(");
			bf.append(this.subject);
			bf.append(")");
		}
		
		if(this.issuer!=null){
			
			if(bf.length()>0){
				bf.append(" ");
			}
			
			bf.append("issuer(");
			bf.append(this.subject);
			bf.append(")");
		}
		if(this.certificate!=null) {
			if(this.certificate.getCertificate()!=null) {
				if(bf.length()>0){
					bf.append(" ");
				}
				
				bf.append("certificate(");
				try {
					bf.append(this.certificate.getCertificate().digestBase64Encoded());
				}catch(Exception e) {
					bf.append("Errore Digest Certificato");
					LoggerWrapperFactory.getLogger(Credential.class).error("Errore Digest Certificato: "+e.getMessage(),e);
				}
				bf.append(")");
			}
		}
		
		if(this.username!=null){
			
			if(bf.length()>0){
				bf.append(" ");
			}
			
			bf.append("username(");
			bf.append(this.username);
			bf.append(")");
		}
		
		if(this.password!=null){
			
			if(bf.length()>0){
				bf.append(" ");
			}
			
			bf.append("password(");
			bf.append(this.password);
			bf.append(")");
		}
		
		if(this.bearerToken!=null){
			
			if(bf.length()>0){
				bf.append(" ");
			}
			
			bf.append("bearerToken(");
			bf.append(this.bearerToken);
			bf.append(")");
		}
		
		return bf.toString();
	}
}
