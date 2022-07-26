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
package org.openspcoop2.pdd.core.credenziali.engine;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.utils.WWWAuthenticateErrorCode;
import org.openspcoop2.message.utils.WWWAuthenticateGenerator;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.autenticazione.WWWAuthenticateConfig;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiConfigurationException;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiException;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.SecurityTokenUtilities;
import org.openspcoop2.protocol.sdk.ChannelSecurityToken;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateDecodeConfig;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.transport.TransportUtils;

/**     
 * GestoreCredenzialiEngine
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreCredenzialiEngine {

	private static String KEYWORD_GATEWAY_CREDENZIALI = "@@GatewayCredenziali@@";
	public static String getDBValuePrefixGatewayCredenziali(String identitaGateway, String credenzialiFornite) {
		return KEYWORD_GATEWAY_CREDENZIALI+identitaGateway+"@@"+credenzialiFornite;
	}
	public static boolean containsPrefixGatewayCredenziali(String value) {
		return value.startsWith(GestoreCredenzialiEngine.KEYWORD_GATEWAY_CREDENZIALI);
	}
	public static String erasePrefixGatewayCredenziali(String value) {
		if(value.startsWith(GestoreCredenzialiEngine.KEYWORD_GATEWAY_CREDENZIALI)) {
			String s = value.substring(GestoreCredenzialiEngine.KEYWORD_GATEWAY_CREDENZIALI.length());
			if(s.contains("@@")) {
				s = s.substring(s.indexOf("@@")+2);
			}
			return s;
		}
		else {
			return value;
		}
	}
	public static String readIdentitaGatewayCredenziali(String value) {
		if(value.startsWith(GestoreCredenzialiEngine.KEYWORD_GATEWAY_CREDENZIALI)) {
			String s = value.substring(GestoreCredenzialiEngine.KEYWORD_GATEWAY_CREDENZIALI.length());
			if(s.contains("@@")) {
				return s.substring(0,s.indexOf("@@"));
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	private String identita = null;
	private boolean portaApplicativa = false;
	private Context context;
	public GestoreCredenzialiEngine(boolean portaApplicativa, Context context){
		this.portaApplicativa = portaApplicativa;
		this.context = context;
	}

	
	public Credenziali elaborazioneCredenziali(
			IDSoggetto idSoggetto,
			InfoConnettoreIngresso infoConnettoreIngresso, OpenSPCoop2Message messaggio)
			throws GestoreCredenzialiException,
			GestoreCredenzialiConfigurationException {
		
		Map<String, List<String>> headerTrasporto = infoConnettoreIngresso.getUrlProtocolContext().getHeaders();
		
		Credenziali credenzialiTrasporto = infoConnettoreIngresso.getCredenziali();
		
		GestoreCredenzialiConfigurazione configurazione = GestoreCredenzialiConfigurazione.getConfigurazione(!this.portaApplicativa, idSoggetto);
		
		// Abilitato ?
		boolean enabled = (configurazione!=null) && configurazione.isEnabled();
		if(!enabled) {
			return credenzialiTrasporto; // credenziali originali
		}
		
		// Identita' Gateway
		this.identita=configurazione.getNome();
		
		// Realm
		
		String realm = configurazione.getRealm();
		String authType = configurazione.getAuthType();
		
		
		// AutenticazioneGateway

		TipoAutenticazioneGestoreCredenziali autenticazioneGateway = configurazione.getTipoAutenticazioneCanale();
		if(autenticazioneGateway==null){
			throw new GestoreCredenzialiException("Tipo di autenticazione per il gestore delle credenziali non definito");
		}
		if(TipoAutenticazioneGestoreCredenziali.BASIC.equals(autenticazioneGateway)){
			String usernameGateway = configurazione.getAutenticazioneCanaleBasicUsername();
			String passwordGateway = configurazione.getAutenticazioneCanaleBasicPassword();
			if(usernameGateway == null){
				throw new GestoreCredenzialiException("Richiesta autenticazione basic del gestore delle credenziali, ma username non definito");
			}
			if(passwordGateway == null){
				throw new GestoreCredenzialiException("Richiesta autenticazione basic del gestore delle credenziali, ma password non definito");
			}
			if(credenzialiTrasporto.getUsername()==null || credenzialiTrasporto.getPassword()==null){
				throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_CREDENTIALS_NOT_FOUND, 
						buildWWWProxyAuthBasic(authType, realm, true),
						"Autenticazione basic del Gestore delle Credenziali '"+this.identita+ "' fallita, nessun tipo di credenziali basic riscontrata nel trasporto");
			}
			if( ! ( usernameGateway.equals(credenzialiTrasporto.getUsername()) && passwordGateway.equals(credenzialiTrasporto.getPassword()) ) ){
				String credenzialiPresenti = credenzialiTrasporto.toString();
				if(credenzialiPresenti==null || credenzialiPresenti.equals("")){
					throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_INVALID_CREDENTIALS, 
							buildWWWProxyAuthBasic(authType, realm, false),
							"Autenticazione basic del Gestore delle Credenziali '"+this.identita+ "' fallita, nessun tipo di credenziali (basic/ssl/principal) riscontrate nel trasporto");
				}else{
					throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_INVALID_CREDENTIALS, 
							buildWWWProxyAuthBasic(authType, realm, false),
							"Autenticazione basic del Gestore delle Credenziali '"+this.identita+ "' fallita, credenziali presenti nel trasporto "+credenzialiPresenti);
				}
			}
		}
		else if(TipoAutenticazioneGestoreCredenziali.SSL.equals(autenticazioneGateway)){
			String subjectGateway = configurazione.getAutenticazioneCanaleSslSubject();
			if(subjectGateway == null){
				throw new GestoreCredenzialiException("Richiesta autenticazione ssl del gestore delle credenziali, ma subject non definito");
			}
			try{
				org.openspcoop2.utils.certificate.CertificateUtils.validaPrincipal(subjectGateway, PrincipalType.subject);
			}catch(Exception e){
				throw new GestoreCredenzialiException("Richiesta autenticazione ssl del gestore delle credenziali, ma subject fornito ["+subjectGateway+"] non valido: "+e.getMessage());
			}
			if(credenzialiTrasporto.getSubject()==null){
				throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_CREDENTIALS_NOT_FOUND, 
						buildWWWProxyAuthSSL(authType, realm, true),
						"Autenticazione ssl del Gestore delle Credenziali '"+this.identita+ "' fallita, nessun tipo di credenziali ssl riscontrata nel trasporto");
			}
			try{
				if( ! org.openspcoop2.utils.certificate.CertificateUtils.sslVerify(subjectGateway, credenzialiTrasporto.getSubject(), PrincipalType.subject, OpenSPCoop2Logger.getLoggerOpenSPCoopCore()) ){
					String credenzialiPresenti = credenzialiTrasporto.toString();
					if(credenzialiPresenti==null || credenzialiPresenti.equals("")){
						throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_INVALID_CREDENTIALS, 
								buildWWWProxyAuthSSL(authType, realm, false),
								"Autenticazione ssl del Gestore delle Credenziali '"+this.identita+ "' fallita, nessun tipo di credenziali (basic/ssl/principal) riscontrate nel trasporto");
					}else{
						throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_INVALID_CREDENTIALS, 
								buildWWWProxyAuthSSL(authType, realm, false),
								"Autenticazione ssl del Gestore delle Credenziali '"+this.identita+ "' fallita, credenziali presenti nel trasporto "+credenzialiPresenti);
					}
				}
			}
			catch(GestoreCredenzialiConfigurationException ge) {
				throw ge;
			}
			catch(Exception e){
				throw new GestoreCredenzialiException("Richiesta autenticazione ssl del gateway gestore delle credenziali; errore durante la verifica: "+e.getMessage());
			}
		}
		else if(TipoAutenticazioneGestoreCredenziali.PRINCIPAL.equals(autenticazioneGateway)){
			String principalGateway = configurazione.getAutenticazioneCanalePrincipal();
			if(principalGateway == null){
				throw new GestoreCredenzialiException("Richiesta autenticazione principal del gestore delle credenziali, ma principal non definito");
			}
			if(credenzialiTrasporto.getPrincipal()==null){
				throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_CREDENTIALS_NOT_FOUND, 
						buildWWWProxyAuthPrincipal(authType, realm, true),
						"Autenticazione principal del Gestore delle Credenziali '"+this.identita+ "' fallita, nessun tipo di credenziale principal riscontrata nel trasporto");
			}
			if( ! ( principalGateway.equals(credenzialiTrasporto.getPrincipal()) ) ){
				String credenzialiPresenti = credenzialiTrasporto.toString();
				if(credenzialiPresenti==null || credenzialiPresenti.equals("")){
					throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_INVALID_CREDENTIALS, 
							buildWWWProxyAuthPrincipal(authType, realm, false),
							"Autenticazione principal del Gestore delle Credenziali '"+this.identita+ "' fallita, nessun tipo di credenziali (basic/ssl/principal) riscontrate nel trasporto");
				}else{
					throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_INVALID_CREDENTIALS, 
							buildWWWProxyAuthPrincipal(authType, realm, false),
							"Autenticazione principal del Gestore delle Credenziali '"+this.identita+ "' fallita, credenziali presenti nel trasporto "+credenzialiPresenti);
				}
			}
		}
		
		
		// Modalità di gestione
		
		ModalitaAutenticazioneGestoreCredenziali modalita = configurazione.getModalitaAutenticazioneCanale();
		String modalitaAtLeastOne_errorDescription = null;
		if(ModalitaAutenticazioneGestoreCredenziali.AT_LEAST_ONE.equals(modalita)) {
			modalitaAtLeastOne_errorDescription = configurazione.getModalitaAutenticazioneCanaleAtLeastOne_error_description();
		}
		
		
		// Avvio processo di lettura credenziali portate nell'header HTTP e generate in seguito all'autenticazione effettuata dal componente Gateway
		
		Credenziali c = new Credenziali();	
		
		String headerNameBasicUsername = configurazione.getHeaderBasicUsername();
		String headerNameBasicPassword = configurazione.getHeaderBasicPassword();
		boolean verificaIdentitaBasic = headerNameBasicUsername!=null && headerNameBasicPassword!=null;
		
		String headerNameSSLSubject = configurazione.getHeaderSslSubject();
		String headerNameSSLIssuer = configurazione.getHeaderSslIssuer();
		String headerNameSSLCertificate = configurazione.getHeaderSslCertificate();
		KeyStore trustStoreCertificatiX509 = configurazione.getHeaderSslCertificateTrustStore();
		boolean sslCertificate_urlDecode = false;
		boolean sslCertificate_base64Decode = false;
		boolean sslCertificate_enrich_BEGIN_END = false;
		boolean sslCertificate_replace = false;
		String sslCertificate_replaceSource = null;
		String sslCertificate_replaceDest = null;
		if(headerNameSSLCertificate!=null) {
			sslCertificate_urlDecode = configurazione.isHeaderSslCertificateUrlDecode();
			sslCertificate_base64Decode = configurazione.isHeaderSslCertificateBase64Decode();
			sslCertificate_enrich_BEGIN_END = configurazione.isHeaderSslCertificateEnrich_BEGIN_END();
			sslCertificate_replace = configurazione.isHeaderSslCertificateReplaceCharacters();
			if(sslCertificate_replace) {
				sslCertificate_replaceSource = configurazione.getHeaderSslCertificateReplaceCharacters_source();
				sslCertificate_replaceDest = configurazione.getHeaderSslCertificateReplaceCharacters_dest();
			}
		}
		boolean verificaIdentitaSSL = headerNameSSLSubject!=null || headerNameSSLCertificate!=null;
		
		String headerNamePrincipal = configurazione.getHeaderPrincipal();
		boolean verificaIdentitaPrincipal = headerNamePrincipal!=null;
		
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
		boolean existsHeader_sslIssuer = false;
		boolean existsHeader_sslCertificate = false;
		if(verificaIdentitaSSL){
			existsHeader_sslSubject = existsHeader(headerTrasporto, headerNameSSLSubject);
			existsHeader_sslIssuer = existsHeader(headerTrasporto, headerNameSSLIssuer);
			existsHeader_sslCertificate = existsHeader(headerTrasporto, headerNameSSLCertificate);
		}
		boolean existsHeader_principal = false;
		if(verificaIdentitaPrincipal){
			existsHeader_principal = existsHeader(headerTrasporto, headerNamePrincipal);
		}
		
		
		// Interpretazione della modalità richiesta di gestione
		// Il Gestore può funzionare in modalità 'none' e quindi le richieste in arrivo possono anche non presentare alcun header che veicola le credenziali. 
		// L'unico vincolo è che se vi sono delle credenziali devono essere valide (non vuote e corrette per ssl).
		// Il Gestore può inoltre essere configurato per richiedere almeno una credenziale o esattamente una credenziale di un certo tipo.
		// Con la modalità 'none' o 'atLeastOne' è possibile usare il gestore davanti a erogazioni/fruizioni con tipi di autenticazione differenti, 
		// delegando poi alla singola erogazione o fruizione il controllo che le credenziali siano effetivamente presenti
		switch (modalita) {
		case NONE:
			if( (!existsHeader_basicUsername || !existsHeader_basicPassword) && !existsHeader_sslSubject && !existsHeader_sslCertificate && !existsHeader_principal ){
				return credenzialiTrasporto; // credenziali originali poiche' non sono presenti header che indicano nuove credenziali.	
			}
			break;
		case AT_LEAST_ONE:
			if( (!existsHeader_basicUsername || !existsHeader_basicPassword) && !existsHeader_sslSubject && !existsHeader_sslCertificate && !existsHeader_principal ){
				StringBuilder sb = new StringBuilder();
				if(headerNameBasicUsername!=null) {
					sb.append(headerNameBasicUsername);
				}
				if(headerNameSSLSubject!=null) {
					if(sb.length()>0) {
						sb.append(",");
					}
					sb.append(headerNameSSLSubject);
				}
				if(headerNameSSLCertificate!=null) {
					if(sb.length()>0) {
						sb.append(",");
					}
					sb.append(headerNameSSLCertificate);
				}
				if(headerNamePrincipal!=null) {
					if(sb.length()>0) {
						sb.append(",");
					}
					sb.append(headerNamePrincipal);
				}
				throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
						buildWWWProxyAuthAtleastOne(authType, realm,
								modalitaAtLeastOne_errorDescription),
						"Non sono presenti Header HTTP che veicolano credenziali (header non rilevati: "+sb.toString()+")");
			}
			break;
		case BASIC:
			if(!verificaIdentitaBasic){
				throw new GestoreCredenzialiException("Configurazione del Gestore delle Credenziali non valida, con la modalità '"+modalita.getValore()+"' devono essere definiti gli header http su cui veicolare le credenziali basic");
			}
			if( !existsHeader_basicUsername ) {
				throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
						buildWWWAuthBasic(),
						"Header HTTP '"+headerNameBasicUsername+"' non presente");
			}
			if( !existsHeader_basicPassword ) {
				throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
						buildWWWAuthBasic(),
						"Header HTTP '"+headerNameBasicPassword+"' non presente");
			}
			// il controllo che il valore presente negli header non sia vuoto viene fatto dopo.
			break;
		case SSL:
			if(!verificaIdentitaSSL){
				throw new GestoreCredenzialiException("Configurazione del Gestore delle Credenziali non valida, con la modalità '"+modalita.getValore()+"' deve essere definito almeno un header http su cui veicolare le credenziali ssl");
			}
			if(headerNameSSLSubject!=null && headerNameSSLCertificate!=null) {
				if(!existsHeader_sslSubject && !existsHeader_sslCertificate) {
					throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
							buildWWWAuthSSL(),
							"Header HTTP '"+headerNameSSLSubject+"' o '"+headerNameSSLCertificate+"' non presente");
				}
			}
			else if( headerNameSSLSubject!=null) {
				if(!existsHeader_sslSubject) {
					throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
							buildWWWAuthSSL(),
							"Header HTTP '"+headerNameSSLSubject+"' non presente");
				}
			}
			else { // per forza questo caso if( headerNameSSLCertificate!=null
				if( !existsHeader_sslCertificate) {
					throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
							buildWWWAuthSSL(),
							"Header HTTP '"+headerNameSSLCertificate+"' non presente");
				}
			}
			// il controllo che il valore presente negli header non sia vuoto e/o corretto viene fatto dopo.
			break;
		case PRINCIPAL:
			if(!verificaIdentitaPrincipal){
				throw new GestoreCredenzialiException("Configurazione del Gestore delle Credenziali non valida, con la modalità '"+modalita.getValore()+"' deve essere definito un header http su cui veicolare le credenziali principal");
			}
			if( !existsHeader_principal) {
				throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
						buildWWWAuthPrincipal(),
						"Header HTTP '"+headerNamePrincipal+"' non presente");
			}
			// il controllo che il valore presente negli header non sia vuoto viene fatto dopo.
			break;
		default:
			break;
		}
		

				
		// Lettura credenziali Basic
		if(verificaIdentitaBasic &&
				existsHeader_basicUsername &&
				existsHeader_basicPassword ){
			
			String username = getProperty(headerTrasporto, 	headerNameBasicUsername );
			String password = getProperty(headerTrasporto, 	headerNameBasicPassword );
			if(username==null || "".equals(username)){
				throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
						buildWWWAuthBasic(),
						"Username value non fornito nell'header del trasporto "+headerNameBasicUsername);
			}
			if(password==null || "".equals(password)){
				throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
						buildWWWAuthBasic(),
						"Password value non fornito nell'header del trasporto "+headerNameBasicPassword);
			}
			c.setUsername(username);
			c.setPassword(password);
		}
		
		// Lettura credenziali ssl
		if(verificaIdentitaSSL) {
			if(existsHeader_sslSubject ){
				
				String subject = getProperty(headerTrasporto, 	headerNameSSLSubject );
				if(subject==null || "".equals(subject)){
					throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
							buildWWWAuthSSL(),
							"Subject value non fornito nell'header del trasporto "+headerNameSSLSubject);
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
			if(existsHeader_sslIssuer ){
				
				String issuer = getProperty(headerTrasporto, 	headerNameSSLIssuer );
				if(issuer==null || "".equals(issuer)){
					throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
							buildWWWAuthSSL(),
							"Issuer value non fornito nell'header del trasporto "+headerNameSSLIssuer);
				}
				try{
					org.openspcoop2.utils.certificate.CertificateUtils.formatPrincipal(issuer, PrincipalType.issuer);
					// Non posso validare, verra' fornito un certificato nel formato RFC 2253 o RFC 1779
					// Sicuramente puo' contenere sia il carattere '/' che ',' ma uno dei due sara' escaped tramite il formato richiesto.
					//org.openspcoop.utils.Utilities.validaSubject(subject);
				}catch(Exception e){
					throw new GestoreCredenzialiException("Issuer value fornito nell'header del trasporto "+headerNameSSLIssuer+" non valido: "+e.getMessage(),e);
				}
				c.setIssuer(issuer);
			}
			if(existsHeader_sslCertificate ){
				
				String certificate = getProperty(headerTrasporto, 	headerNameSSLCertificate );
				if(certificate==null || "".equals(certificate)){
					throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
							buildWWWAuthSSL(),
							"Certificate non fornito nell'header del trasporto "+headerNameSSLCertificate);
				}
				
				CertificateDecodeConfig config = new CertificateDecodeConfig();
				config.setUrlDecode(sslCertificate_urlDecode);
				config.setBase64Decode(sslCertificate_base64Decode);
				config.setEnrich_BEGIN_END(sslCertificate_enrich_BEGIN_END);
				config.setReplace(sslCertificate_replace);
				if(sslCertificate_replace) {
					if(sslCertificate_replaceSource!=null && !StringUtils.isEmpty(sslCertificate_replaceSource)) {
						config.setReplaceSource(sslCertificate_replaceSource);
					}
					if(sslCertificate_replaceDest!=null && !StringUtils.isEmpty(sslCertificate_replaceDest)) {
						config.setReplaceDest(sslCertificate_replaceDest);
					}
				}

				Certificate cer = null;
				try{
					cer = CertificateUtils.readCertificate(config, certificate);					
					c.setCertificate(cer);
					
					String subject = c.getCertificate().getCertificate().getSubject().toString();
					String issuer = c.getCertificate().getCertificate().getIssuer().toString();
					c.setSubject(subject);
					c.setIssuer(issuer);
					
				}catch(Exception e){
					throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
							buildWWWAuthSSL(),
							"Certificate fornito nell'header del trasporto "+headerNameSSLCertificate+" non valido: "+e.getMessage(), e);
				}
				
				if(cer!=null && trustStoreCertificatiX509!=null && cer.getCertificate().isVerified(trustStoreCertificatiX509, true)==false) {
					throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
							buildWWWAuthSSL(),
							"Certificato presente nell'header '"+headerNameSSLCertificate+"' non è verificabile rispetto alle CA conosciute");
				}
				
				/* --------------- SecurityToken --------------- */
				try {
					if(cer!=null && this.context!=null) {
						SecurityToken securityToken = SecurityTokenUtilities.newSecurityToken(this.context);
						ChannelSecurityToken channelSecurityToken = new ChannelSecurityToken();
						channelSecurityToken.setCertificate(cer.getCertificate());
						securityToken.setChannel(channelSecurityToken); // sovrascrivo eventuali esistente
					}
				}catch(Exception e){
					throw new GestoreCredenzialiException("Costruzione SecurityToken non riuscita: "+e.getMessage(),e);
				}
			}
		}
		
		// Lettura credenziali principal
		if(verificaIdentitaPrincipal &&
				existsHeader_principal ){
			
			String principal = getProperty(headerTrasporto, 	headerNamePrincipal );
			if(principal==null || "".equals(principal)){
				throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, 
						buildWWWAuthPrincipal(),
						"Principal value non fornito nell'header del trasporto "+headerNamePrincipal);
			}
			c.setPrincipal(principal);
		}
		
		return c;
		
	}
	
	public String getIdentitaGestoreCredenziali(){
		return this.identita;
	}
	
	private boolean existsHeader(Map<String, List<String>> properties, String name){
		if(properties!=null){
			return TransportUtils.containsKey(properties, name);
		}else{
			return false;
		}
	}
	
	private String getProperty(Map<String, List<String>> properties, String name){
		if(properties!=null){
			return TransportUtils.getFirstValue(properties, name);
		}else{
			return null;
		}
		
	}
	
	public static String buildWWWProxyAuthBasic(String authType, String realm, boolean missing) {
		if(realm==null || "".equals(realm) || authType==null || "".equals(authType)) {
			return null;
		}
		WWWAuthenticateConfig configGW = OpenSPCoop2Properties.getInstance().getRealmAutenticazioneBasicWWWAuthenticateConfig();
		WWWAuthenticateConfig proxy = configGW!=null ? (WWWAuthenticateConfig) configGW.clone() : new WWWAuthenticateConfig();
		proxy.setRealm(realm);
		proxy.setAuthType(authType);
		return missing ? proxy.buildWWWAuthenticateHeaderValue_notFound() : proxy.buildWWWAuthenticateHeaderValue_invalid();
	}
	public static String buildWWWProxyAuthSSL(String authType, String realm, boolean missing) {
		if(realm==null || "".equals(realm) || authType==null || "".equals(authType)) {
			return null;
		}
		WWWAuthenticateConfig configGW = OpenSPCoop2Properties.getInstance().getRealmAutenticazioneHttpsWWWAuthenticateConfig();
		WWWAuthenticateConfig proxy = configGW!=null ? (WWWAuthenticateConfig) configGW.clone() : new WWWAuthenticateConfig();
		proxy.setRealm(realm);
		proxy.setAuthType(authType);
		return missing ? proxy.buildWWWAuthenticateHeaderValue_notFound() : proxy.buildWWWAuthenticateHeaderValue_invalid();
	}
	public static String buildWWWProxyAuthPrincipal(String authType, String realm, boolean missing) {
		if(realm==null || "".equals(realm) || authType==null || "".equals(authType)) {
			return null;
		}
		WWWAuthenticateConfig configGW = OpenSPCoop2Properties.getInstance().getRealmAutenticazionePrincipalWWWAuthenticateConfig(TipoAutenticazionePrincipal.CONTAINER);
		WWWAuthenticateConfig proxy = configGW!=null ? (WWWAuthenticateConfig) configGW.clone() : new WWWAuthenticateConfig();
		proxy.setRealm(realm);
		proxy.setAuthType(authType);
		return missing ? proxy.buildWWWAuthenticateHeaderValue_notFound() : proxy.buildWWWAuthenticateHeaderValue_invalid();
	}
	public static String buildWWWProxyAuthAtleastOne(String authType, String realm, String error_description) {
		if(realm==null || "".equals(realm) || authType==null || "".equals(authType)) {
			return null;
		}
		if(error_description!=null && !"".equals(error_description)) {
			return WWWAuthenticateGenerator.buildCustomHeaderValue(authType, realm, WWWAuthenticateErrorCode.invalid_request, 
					error_description);
		}
		else {
			WWWAuthenticateErrorCode errorCode = null;
			return WWWAuthenticateGenerator.buildCustomHeaderValue(authType, realm, errorCode, null);
		}
	}

	
	public static String buildWWWAuthBasic() {
		WWWAuthenticateConfig config = OpenSPCoop2Properties.getInstance().getRealmAutenticazioneBasicWWWAuthenticateConfig();
		if(config!=null) {
			return config.buildWWWAuthenticateHeaderValue_notFound();
		}
		return null;
	}
	public static String buildWWWAuthSSL() {
		WWWAuthenticateConfig config = OpenSPCoop2Properties.getInstance().getRealmAutenticazioneHttpsWWWAuthenticateConfig();
		if(config!=null) {
			return config.buildWWWAuthenticateHeaderValue_notFound();
		}
		return null;
	}
	public static String buildWWWAuthPrincipal() {
		WWWAuthenticateConfig config = OpenSPCoop2Properties.getInstance().getRealmAutenticazionePrincipalWWWAuthenticateConfig(TipoAutenticazionePrincipal.CONTAINER);
		if(config!=null) {
			return config.buildWWWAuthenticateHeaderValue_notFound();
		}
		return null;
	}
}
