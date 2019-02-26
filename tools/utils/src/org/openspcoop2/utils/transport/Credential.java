/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import javax.security.auth.x500.X500Principal;

import org.apache.commons.lang.NotImplementedException;

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
	protected java.security.cert.X509Certificate[] certs;
	
	// Basic (HTTP-Based)
	protected String username;
	protected String password;
	
	// Bearer (token)
	protected String bearerToken;
		
	
	public Credential(){
		
	}
	
	
	public static void printCertificate(StringBuffer bf,java.security.cert.X509Certificate[] certs){
		bf.append("X509Certificates: "+certs.length+"\n");
		for (int i = 0; i < certs.length; i++) {
			java.security.cert.X509Certificate cert = certs[i];
			bf.append("#### X509Certificate["+i+"]\n");
			bf.append("\tCert["+i+"].toString()="+cert.toString()+"\n");
			bf.append("\tCert["+i+"].getType()="+cert.getType()+"\n");
			bf.append("\tCert["+i+"].getVersion()="+cert.getVersion()+"\n");
			
			if(cert.getIssuerDN()!=null){
				bf.append("\tCert["+i+"].cert.getIssuerDN().toString()="+cert.getIssuerDN().toString()+"\n");
				bf.append("\tCert["+i+"].cert.getIssuerDN().getName()="+cert.getIssuerDN().getName()+"\n");
			}
			else{
				bf.append("\tCert["+i+"].cert.getIssuerDN() is null"+"\n");
			}
			
			if(cert.getIssuerX500Principal()!=null){
				bf.append("\tCert["+i+"].getIssuerX500Principal().toString()="+cert.getIssuerX500Principal().toString()+"\n");
				bf.append("\tCert["+i+"].getIssuerX500Principal().getName()="+cert.getIssuerX500Principal().getName()+"\n");
				bf.append("\tCert["+i+"].getIssuerX500Principal().getName(X500Principal.CANONICAL)="+cert.getIssuerX500Principal().getName(X500Principal.CANONICAL)+"\n");
				bf.append("\tCert["+i+"].getIssuerX500Principal().getName(X500Principal.RFC1779)="+cert.getIssuerX500Principal().getName(X500Principal.RFC1779)+"\n");
				bf.append("\tCert["+i+"].getIssuerX500Principal().getName(X500Principal.RFC2253)="+cert.getIssuerX500Principal().getName(X500Principal.RFC2253)+"\n");
//					Map<String,String> oidMapCanonical = new Hashtable<String, String>();
//					bf.append("\tCert["+i+"].getIssuerX500Principal().getName(X500Principal.CANONICAL,oidMapCanonical)="+
//							cert.getIssuerX500Principal().getName(X500Principal.CANONICAL,oidMapCanonical));
//					if(oidMapCanonical!=null && oidMapCanonical.size()>0){
//						Iterator<String> it = oidMapCanonical.keySet().iterator();
//						while (it.hasNext()) {
//							String key = (String) it.next();
//							String value = oidMapCanonical.get(key);
//							bf.append("\tCert["+i+"].getIssuerX500Principal() ["+key+"]=["+value+"]"+"\n");
//						}
//					}
			}
			else{
				bf.append("\tCert["+i+"].cert.getIssuerX500Principal() is null"+"\n");
			}
			
			if(cert.getSubjectDN()!=null){
				bf.append("\tCert["+i+"].getSubjectDN().toString()="+cert.getSubjectDN().toString()+"\n");
				bf.append("\tCert["+i+"].getSubjectDN().getName()="+cert.getSubjectDN().getName()+"\n");
			}
			else{
				bf.append("\tCert["+i+"].cert.getSubjectDN() is null"+"\n");
			}
			
			bf.append("\tCert["+i+"].getSerialNumber()="+cert.getSerialNumber()+"\n");
			bf.append("\tCert["+i+"].getNotAfter()="+cert.getNotAfter()+"\n");
			bf.append("\tCert["+i+"].getNotBefore()="+cert.getNotBefore()+"\n");
			
			if(cert.getSubjectX500Principal()!=null){
				bf.append("\tCert["+i+"].getSubjectX500Principal().toString()="+cert.getSubjectX500Principal().toString()+"\n");
				bf.append("\tCert["+i+"].getSubjectX500Principal().getName()="+cert.getSubjectX500Principal().getName()+"\n");
				bf.append("\tCert["+i+"].getSubjectX500Principal().getName(X500Principal.CANONICAL)="+cert.getSubjectX500Principal().getName(X500Principal.CANONICAL)+"\n");
				bf.append("\tCert["+i+"].getSubjectX500Principal().getName(X500Principal.RFC1779)="+cert.getSubjectX500Principal().getName(X500Principal.RFC1779)+"\n");
				bf.append("\tCert["+i+"].getSubjectX500Principal().getName(X500Principal.RFC2253)="+cert.getSubjectX500Principal().getName(X500Principal.RFC2253)+"\n");
//					Map<String,String> oidMapCanonical = new Hashtable<String, String>();
//					bf.append("\tCert["+i+"].getSubjectX500Principal().getName(X500Principal.CANONICAL,oidMapCanonical)="+
//							cert.getSubjectX500Principal().getName(X500Principal.CANONICAL,oidMapCanonical));
//					if(oidMapCanonical!=null && oidMapCanonical.size()>0){
//						Iterator<String> it = oidMapCanonical.keySet().iterator();
//						while (it.hasNext()) {
//							String key = (String) it.next();
//							String value = oidMapCanonical.get(key);
//							bf.append("\tCert["+i+"].getSubjectX500Principal() ["+key+"]=["+value+"]"+"\n");
//						}
//					}
			}
			else{
				bf.append("\tCert["+i+"].cert.getSubjectX500Principal() is null"+"\n");
			}
		}
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
	public java.security.cert.X509Certificate[] getCerts() {
		return this.certs;
	}
	public void setCerts(java.security.cert.X509Certificate[] certs) {
		this.certs = certs;
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
		StringBuffer bf = new StringBuffer();

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
