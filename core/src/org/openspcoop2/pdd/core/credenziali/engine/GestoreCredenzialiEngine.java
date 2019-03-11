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
package org.openspcoop2.pdd.core.credenziali.engine;

import java.util.Properties;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiConfigurationException;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiException;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.certificate.PrincipalType;

/**     
 * GestoreCredenzialiEngine
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreCredenzialiEngine {

	public static String KEYWORD_GATEWAY_CREDENZIALI = "@@GatewayCredenziali@@";
	
	private String identita = null;
	private boolean portaApplicativa = false;
	public GestoreCredenzialiEngine(boolean portaApplicativa){
		this.portaApplicativa = portaApplicativa;
	}

	
	public Credenziali elaborazioneCredenziali(
			InfoConnettoreIngresso infoConnettoreIngresso, OpenSPCoop2Message messaggio)
			throws GestoreCredenzialiException,
			GestoreCredenzialiConfigurationException {
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		Properties headerTrasporto = infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto();
		
		Credenziali credenzialiTrasporto = infoConnettoreIngresso.getCredenziali();
		
		// Abilitato ?
		boolean enabled = false;
		if(this.portaApplicativa) {
			enabled = op2Properties.isGestoreCredenzialiPortaApplicativaEnabled();
		}
		else {
			enabled = op2Properties.isGestoreCredenzialiPortaDelegataEnabled();
		}
		if(!enabled) {
			return credenzialiTrasporto; // credenziali originali
		}
		
		// Identita' Gateway
		try {
			if(this.portaApplicativa)
				this.identita = op2Properties.getGestoreCredenzialiPortaApplicativaNome();
			else
				this.identita = op2Properties.getGestoreCredenzialiPortaDelegataNome();
		}catch(Exception e) {
			// non dovrebbe succedere eccezioni, la validazione dei metodi viene fatta allo startup della PdD
			throw new GestoreCredenzialiException(e.getMessage(),e);
		}
		if(this.identita==null){
			throw new GestoreCredenzialiException("Nome del gestore delle credenziali non definito");
		}
		
		// AutenticazioneGateway

		TipoAutenticazioneGestoreCredenziali autenticazioneGateway = null;
		try {
			if(this.portaApplicativa){
				autenticazioneGateway = op2Properties.getGestoreCredenzialiPortaApplicativaTipoAutenticazioneCanale();
			}else{
				autenticazioneGateway = op2Properties.getGestoreCredenzialiPortaDelegataTipoAutenticazioneCanale();
			}
		}catch(Exception e) {
			// non dovrebbe succedere eccezioni, la validazione dei metodi viene fatta allo startup della PdD
			throw new GestoreCredenzialiException(e.getMessage(),e);
		}
		if(autenticazioneGateway==null){
			throw new GestoreCredenzialiException("Tipo di autenticazione per il gestore delle credenziali non definito");
		}
		if(TipoAutenticazioneGestoreCredenziali.BASIC.equals(autenticazioneGateway)){
			String usernameGateway = null;
			String passwordGateway = null;
			try {
				if(this.portaApplicativa){
					usernameGateway = op2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicUsername();
				}else{
					usernameGateway = op2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicUsername();
				}
				if(this.portaApplicativa){
					passwordGateway = op2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicPassword();
				}else{
					passwordGateway = op2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicPassword();
				}
			}catch(Exception e) {
				// non dovrebbe succedere eccezioni, la validazione dei metodi viene fatta allo startup della PdD
				throw new GestoreCredenzialiException(e.getMessage(),e);
			}
			if(usernameGateway == null){
				throw new GestoreCredenzialiException("Richiesta autenticazione basic del gestore delle credenziali, ma username non definito");
			}
			if(passwordGateway == null){
				throw new GestoreCredenzialiException("Richiesta autenticazione basic del gestore delle credenziali, ma password non definito");
			}
			if( ! ( usernameGateway.equals(credenzialiTrasporto.getUsername()) && passwordGateway.equals(credenzialiTrasporto.getPassword()) ) ){
				String credenzialiPresenti = credenzialiTrasporto.toString();
				if(credenzialiPresenti==null || credenzialiPresenti.equals("")){
					throw new GestoreCredenzialiConfigurationException("Autenticazione basic del Gestore delle Credenziali '"+this.identita+ "' fallita, nessun tipo di credenziali (basic/ssl/principal) riscontrate nel trasporto");
				}else{
					throw new GestoreCredenzialiConfigurationException("Autenticazione basic del Gestore delle Credenziali '"+this.identita+ "' fallita, credenziali presenti nel trasporto "+credenzialiPresenti);
				}
			}
		}
		else if(TipoAutenticazioneGestoreCredenziali.SSL.equals(autenticazioneGateway)){
			String subjectGateway = null;
			try {
				if(this.portaApplicativa){
					subjectGateway = op2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleSslSubject();
				}else{
					subjectGateway = op2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleSslSubject();
				}
				if(subjectGateway == null){
					throw new GestoreCredenzialiException("Richiesta autenticazione ssl del gestore delle credenziali, ma subject non definito");
				}
			}catch(Exception e) {
				// non dovrebbe succedere eccezioni, la validazione dei metodi viene fatta allo startup della PdD
				throw new GestoreCredenzialiException(e.getMessage(),e);
			}
			try{
				org.openspcoop2.utils.certificate.CertificateUtils.validaPrincipal(subjectGateway, PrincipalType.subject);
			}catch(Exception e){
				throw new GestoreCredenzialiException("Richiesta autenticazione ssl del gestore delle credenziali, ma subject fornito non valido: "+e.getMessage());
			}
			if(credenzialiTrasporto.getSubject()==null){
				throw new GestoreCredenzialiConfigurationException("Autenticazione ssl del Gestore delle Credenziali '"+this.identita+ "' fallita, nessun tipo di credenziali ssl riscontrata nel trasporto");
			}
			try{
				if( ! org.openspcoop2.utils.certificate.CertificateUtils.sslVerify(subjectGateway, credenzialiTrasporto.getSubject(), PrincipalType.subject, OpenSPCoop2Logger.getLoggerOpenSPCoopCore()) ){
					String credenzialiPresenti = credenzialiTrasporto.toString();
					if(credenzialiPresenti==null || credenzialiPresenti.equals("")){
						throw new GestoreCredenzialiConfigurationException("Autenticazione ssl del Gestore delle Credenziali '"+this.identita+ "' fallita, nessun tipo di credenziali (basic/ssl/principal) riscontrate nel trasporto");
					}else{
						throw new GestoreCredenzialiConfigurationException("Autenticazione ssl del Gestore delle Credenziali '"+this.identita+ "' fallita, credenziali presenti nel trasporto "+credenzialiPresenti);
					}
				}
			}catch(Exception e){
				throw new GestoreCredenzialiException("Richiesta autenticazione ssl del gateway gestore delle credenziali; errore durante la verifica: "+e.getMessage());
			}
		}
		else if(TipoAutenticazioneGestoreCredenziali.PRINCIPAL.equals(autenticazioneGateway)){
			String principalGateway = null;
			try {
				if(this.portaApplicativa){
					principalGateway = op2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanalePrincipal();
				}else{
					principalGateway = op2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanalePrincipal();
				}
			}catch(Exception e) {
				// non dovrebbe succedere eccezioni, la validazione dei metodi viene fatta allo startup della PdD
				throw new GestoreCredenzialiException(e.getMessage(),e);
			}
			if(principalGateway == null){
				throw new GestoreCredenzialiException("Richiesta autenticazione principal del gestore delle credenziali, ma principal non definito");
			}
			if( ! ( principalGateway.equals(credenzialiTrasporto.getPrincipal()) ) ){
				String credenzialiPresenti = credenzialiTrasporto.toString();
				if(credenzialiPresenti==null || credenzialiPresenti.equals("")){
					throw new GestoreCredenzialiConfigurationException("Autenticazione principal del Gestore delle Credenziali '"+this.identita+ "' fallita, nessun tipo di credenziali (basic/ssl/principal) riscontrate nel trasporto");
				}else{
					throw new GestoreCredenzialiConfigurationException("Autenticazione principal del Gestore delle Credenziali '"+this.identita+ "' fallita, credenziali presenti nel trasporto "+credenzialiPresenti);
				}
			}
		}
		
		
		
		// Avvio processo di lettura credenziali portate nell'header HTTP e generate in seguito all'autenticazione effettuata dal componente Gateway
		
		Credenziali c = new Credenziali();	
		
		String headerNameBasicUsername = null;
		String headerNameBasicPassword = null;
		boolean verificaIdentitaBasic = false;
		try {
			if(this.portaApplicativa){
				headerNameBasicUsername = op2Properties.getGestoreCredenzialiPortaApplicativaHeaderBasicUsername();
			}else{
				headerNameBasicUsername = op2Properties.getGestoreCredenzialiPortaDelegataHeaderBasicUsername();
			}
			if(this.portaApplicativa){
				headerNameBasicPassword = op2Properties.getGestoreCredenzialiPortaApplicativaHeaderBasicPassword();
			}else{
				headerNameBasicPassword = op2Properties.getGestoreCredenzialiPortaDelegataHeaderBasicPassword();
			}
			verificaIdentitaBasic = headerNameBasicUsername!=null && headerNameBasicPassword!=null;
		}catch(Exception e) {
			// non dovrebbe succedere eccezioni, la validazione dei metodi viene fatta allo startup della PdD
			throw new GestoreCredenzialiException(e.getMessage(),e);
		}
		
		String headerNameSSLSubject = null;
		boolean verificaIdentitaSSL = false;
		try {
			if(this.portaApplicativa){
				headerNameSSLSubject = op2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslSubject();
			}else{
				headerNameSSLSubject = op2Properties.getGestoreCredenzialiPortaDelegataHeaderSslSubject();
			}
			verificaIdentitaSSL = headerNameSSLSubject!=null;
		}catch(Exception e) {
			// non dovrebbe succedere eccezioni, la validazione dei metodi viene fatta allo startup della PdD
			throw new GestoreCredenzialiException(e.getMessage(),e);
		}
		
		String headerNamePrincipal = null;
		boolean verificaIdentitaPrincipal = false;
		try {
			if(this.portaApplicativa){
				headerNamePrincipal = op2Properties.getGestoreCredenzialiPortaApplicativaHeaderPrincipal();
			}else{
				headerNamePrincipal = op2Properties.getGestoreCredenzialiPortaDelegataHeaderPrincipal();
			}
			verificaIdentitaPrincipal = headerNamePrincipal!=null;
		}catch(Exception e) {
			// non dovrebbe succedere eccezioni, la validazione dei metodi viene fatta allo startup della PdD
			throw new GestoreCredenzialiException(e.getMessage(),e);
		}
		
		if(!verificaIdentitaBasic && !verificaIdentitaSSL && !verificaIdentitaPrincipal){
			return credenzialiTrasporto; // credenziali originali
		}
		
		boolean existsHeader_basicUsername = false;
		boolean existsHeader_basicPassword = false;
		if(verificaIdentitaBasic){
			existsHeader_basicUsername = existsHeader(headerTrasporto, headerNameBasicUsername);
			existsHeader_basicPassword = existsHeader(headerTrasporto, headerNameBasicPassword);
		}
		boolean existsHeader_sslSubject = false;
		if(verificaIdentitaSSL){
			existsHeader_sslSubject = existsHeader(headerTrasporto, headerNameSSLSubject);
		}
		boolean existsHeader_principal = false;
		if(verificaIdentitaPrincipal){
			existsHeader_principal = existsHeader(headerTrasporto, headerNamePrincipal);
		}
		
		if( (!existsHeader_basicUsername || !existsHeader_basicPassword) && !existsHeader_sslSubject && !existsHeader_principal ){
			return credenzialiTrasporto; // credenziali originali poiche' non sono presenti header che indicano nuove credenziali.	
		}
				
		// Lettura credenziali Basic
		if(verificaIdentitaBasic &&
				existsHeader_basicUsername &&
				existsHeader_basicPassword ){
			
			String username = getProperty(headerTrasporto, 	headerNameBasicUsername );
			String password = getProperty(headerTrasporto, 	headerNameBasicPassword );
			if(username==null || "".equals(username)){
				throw new GestoreCredenzialiConfigurationException("Username value non fornito nell'header del trasporto "+headerNameBasicUsername);
			}
			if(password==null || "".equals(password)){
				throw new GestoreCredenzialiConfigurationException("Password value non fornito nell'header del trasporto "+headerNameBasicPassword);
			}
			c.setUsername(username);
			c.setPassword(password);
		}
		
		// Lettura credenziali ssl
		if(verificaIdentitaSSL &&
				existsHeader_sslSubject ){
			
			String subject = getProperty(headerTrasporto, 	headerNameSSLSubject );
			if(subject==null || "".equals(subject)){
				throw new GestoreCredenzialiConfigurationException("Subject value non fornito nell'header del trasporto "+headerNameSSLSubject);
			}
			try{
				org.openspcoop2.utils.certificate.CertificateUtils.formatPrincipal(subject, PrincipalType.subject);
				// Non posso validare, verra' fornito un certificato nel formato RFC 2253 o RFC 1779
				// Sicuramente puo' contenere sia il carattere '/' che ',' ma uno dei due sara' escaped tramite il formato richiesto.
				//org.openspcoop.utils.Utilities.validaSubject(subject);
			}catch(Exception e){
				throw new GestoreCredenzialiException("Subject value fornito nell'header del trasporto "+headerNameSSLSubject+" non valido: "+e.getMessage(),e);
			}
			c.setSubject(subject);
		}
		
		// Lettura credenziali principal
		if(verificaIdentitaPrincipal &&
				existsHeader_principal ){
			
			String principal = getProperty(headerTrasporto, 	headerNamePrincipal );
			if(principal==null || "".equals(principal)){
				throw new GestoreCredenzialiConfigurationException("Principal value non fornito nell'header del trasporto "+headerNamePrincipal);
			}
			c.setPrincipal(principal);
		}
		
		return c;
		
	}
	
	public String getIdentitaGestoreCredenziali(){
		return this.identita;
	}
	
	private boolean existsHeader(Properties properties, String name){
		if(properties!=null){
			return properties.containsKey(name) ||
				properties.containsKey(name.toLowerCase()) ||
				properties.containsKey(name.toUpperCase());
		}else{
			return false;
		}
	}
	
	private String getProperty(Properties properties, String name){
		if(properties!=null){
			String tmp = properties.getProperty(name);
			if(tmp==null){
				tmp = properties.getProperty(name.toLowerCase());
			}
			if(tmp==null){
				tmp = properties.getProperty(name.toUpperCase());
			}
			return tmp;
		}else{
			return null;
		}
		
	}

}
