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
package org.openspcoop2.pdd.core.credenziali.engine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiException;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;

/**     
 * GestoreCredenzialiConfigurazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreCredenzialiConfigurazione {

	private static Map<String, GestoreCredenzialiConfigurazione> configurazione = new HashMap<String, GestoreCredenzialiConfigurazione>();
	private static org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("GestoreCredenzialiConfigurazione");
	
	private static void initConfigurazione(boolean fruizione, IDSoggetto idSoggetto) throws GestoreCredenzialiException {
		String key = getKey(fruizione, idSoggetto);
		if(!configurazione.containsKey(key)) {
			try {
				semaphore.acquire("initConfig");
			}catch(Exception e) {
				throw new GestoreCredenzialiException(e.getMessage(),e);
			}
			try {
				GestoreCredenzialiConfigurazione instance = new GestoreCredenzialiConfigurazione(fruizione, idSoggetto);
				configurazione.put(key, instance);
				//System.out.println("Registrata configurazione per ["+key+"]");
			}finally {
				semaphore.release("initConfig");
			}
		}
	}
	public static GestoreCredenzialiConfigurazione getConfigurazione(boolean fruizione, IDSoggetto idSoggetto) throws GestoreCredenzialiException {
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		boolean abilitatoGlobalmente = false;
		if(fruizione) {
			abilitatoGlobalmente = op2Properties.isGestoreCredenzialiPortaDelegataEnabled();
		}
		else {
			abilitatoGlobalmente = op2Properties.isGestoreCredenzialiPortaApplicativaEnabled();
		}
		if(!abilitatoGlobalmente) {
			return null;
		}
		
		String key = getKey(fruizione, idSoggetto);
		if(!configurazione.containsKey(key)) {
			initConfigurazione(fruizione, idSoggetto);
		}	
		return configurazione.get(key);
	}
	private static String getKey(boolean fruizione, IDSoggetto idSoggetto) {
		return (fruizione ? "fruizione_" : "erogazione_" ) + (idSoggetto!=null ? idSoggetto.toString() : "-undefined-");
	}
	
	
	private boolean enabled;
	private String nome;
	
	private String realm;
	private String authType;
	
	private TipoAutenticazioneGestoreCredenziali tipoAutenticazioneCanale = null;
	private String autenticazioneCanaleBasicUsername=null;
	private String autenticazioneCanaleBasicPassword=null;
	private String autenticazioneCanaleSslSubject=null;
	private String autenticazioneCanalePrincipal=null;
	
	private ModalitaAutenticazioneGestoreCredenziali modalitaAutenticazioneCanale = null;
	private String modalitaAutenticazioneCanaleAtLeastOne_error_description = null;
	
	private String headerBasicUsername;
	private String headerBasicPassword;
	
	private String headerSslSubject;
	private String headerSslIssuer;
	
	private String headerSslCertificate;
	private boolean headerSslCertificateUrlDecode;
	private boolean headerSslCertificateBase64Decode;
	private boolean headerSslCertificateUrlDecodeOrBase64Decode;
	private boolean headerSslCertificateEnrich_BEGIN_END;
	private boolean headerSslCertificateReplaceCharacters;
	private String headerSslCertificateReplaceCharacters_source;
	private String headerSslCertificateReplaceCharacters_dest;
	private String headerSslCertificateTrustStorePath;
	private String headerSslCertificateTrustStorePassword;
	private String headerSslCertificateTrustStoreType;
	private boolean headerSslCertificateTrustStoreCheckValid = true;
	private String headerSslCertificateCrlX509;
	private String headerSslCertificateOcspPolicy;

	private String headerPrincipal;
	
	

	public GestoreCredenzialiConfigurazione(boolean fruizione, IDSoggetto idSoggetto) throws GestoreCredenzialiException {
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		Properties p = null;
		String prefix = null;
		if(fruizione) {
			p = op2Properties.getGestoreCredenzialiPortaDelegataProperties();
			prefix = OpenSPCoop2Properties.prefixGestoreCredenzialiPortaDelegataProperties;
		}
		else {
			p = op2Properties.getGestoreCredenzialiPortaApplicativaProperties();
			prefix = OpenSPCoop2Properties.prefixGestoreCredenzialiPortaApplicativaProperties;
		}
		
		String protocollo = null;
		try {
			if(idSoggetto!=null) {
				protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSoggetto.getTipo());
			}
		}catch(Exception e) {
			throw new GestoreCredenzialiException(e.getMessage(),e);
		}
		
		
		
		String enabledS = getProperty(p, "enabled", protocollo, idSoggetto);
		if(enabledS!=null){
			enabledS = enabledS.trim();
			this.enabled = Boolean.parseBoolean(enabledS);
		}
		else {
			this.enabled = false;
		}
		if(!this.enabled) {
			return;
		}
		
		this.nome = getProperty(p, "nome", protocollo, idSoggetto);
		if(this.nome==null) {
			throw new GestoreCredenzialiException("Proprietà '"+prefix+"nome non impostata");
		}	
		
		this.realm = getProperty(p, "wwwAuthenticate.realm", protocollo, idSoggetto);
		this.authType = getProperty(p, "wwwAuthenticate.authType", protocollo, idSoggetto);
		if( (this.realm!=null && this.authType==null) || (this.realm==null && this.authType!=null) ) {
			throw new GestoreCredenzialiException("L'abilitazione del wwwAuthenticate sul gestore delle credenziali richiede che sia definito sia un nome da associare al realm (trovato "+prefix+"wwwAuthenticate.realm="+this.realm+") che il tipo di autenticazione (trovato "+prefix+"wwwAuthenticate.authType="+this.authType+")");
		}
		
		String authCanaleS = getProperty(p, "autenticazioneCanale", protocollo, idSoggetto);
		if(authCanaleS==null) {
			throw new GestoreCredenzialiException("Proprietà '"+prefix+"autenticazioneCanale non impostata");
		}	
		this.tipoAutenticazioneCanale = TipoAutenticazioneGestoreCredenziali.toEnumConstant(authCanaleS);
		if(this.tipoAutenticazioneCanale==null) {
			throw new GestoreCredenzialiException("Proprietà '"+prefix+"autenticazioneCanale impostata non correttamente, valore indicato ("+authCanaleS+") non gestito");
		}
		
		switch (this.tipoAutenticazioneCanale) {
		case NONE:
			break;
		case BASIC:
			this.autenticazioneCanaleBasicUsername = getProperty(p, "autenticazioneCanale.basic.username", protocollo, idSoggetto);
			if(this.autenticazioneCanaleBasicUsername==null) {
				throw new GestoreCredenzialiException("Proprietà '"+prefix+"autenticazioneCanale.basic.username non impostata");
			}	
			
			this.autenticazioneCanaleBasicPassword = getProperty(p, "autenticazioneCanale.basic.password", protocollo, idSoggetto);
			if(this.autenticazioneCanaleBasicPassword==null) {
				throw new GestoreCredenzialiException("Proprietà '"+prefix+"autenticazioneCanale.basic.password non impostata");
			}	
			break;
		case SSL:
			this.autenticazioneCanaleSslSubject = getProperty(p, "autenticazioneCanale.ssl.subject", protocollo, idSoggetto);
			if(this.autenticazioneCanaleSslSubject==null) {
				throw new GestoreCredenzialiException("Proprietà '"+prefix+"autenticazioneCanale.ssl.subject non impostata");
			}
			break;
		case PRINCIPAL:
			this.autenticazioneCanalePrincipal = getProperty(p, "autenticazioneCanale.principal", protocollo, idSoggetto);
			if(this.autenticazioneCanalePrincipal==null) {
				throw new GestoreCredenzialiException("Proprietà '"+prefix+"autenticazioneCanale.principal non impostata");
			}
			break;
		}
		
		
		this.headerBasicUsername = getProperty(p, "header.basic.username", protocollo, idSoggetto);
		this.headerBasicPassword = getProperty(p, "header.basic.password", protocollo, idSoggetto);
		
		this.headerSslSubject = getProperty(p, "header.ssl.subject", protocollo, idSoggetto);
		this.headerSslIssuer = getProperty(p, "header.ssl.issuer", protocollo, idSoggetto);
		
		this.headerSslCertificate = getProperty(p, "header.ssl.certificate", protocollo, idSoggetto);
		if(this.headerSslCertificate!=null) {
		
			String pValue = getProperty(p, "header.ssl.certificate.url_decode", protocollo, idSoggetto);
			if(pValue!=null){
				pValue = pValue.trim();
				this.headerSslCertificateUrlDecode = Boolean.parseBoolean(pValue);
			}else{
				this.headerSslCertificateUrlDecode = true;// default a true
			}
			
			pValue = getProperty(p, "header.ssl.certificate.base64_decode", protocollo, idSoggetto);
			if(pValue!=null){
				pValue = pValue.trim();
				this.headerSslCertificateBase64Decode = Boolean.parseBoolean(pValue);
			}else{
				this.headerSslCertificateBase64Decode = true;// default a true
			}
			
			pValue = getProperty(p, "header.ssl.certificate.url_decode_or_base64_decode", protocollo, idSoggetto);
			if(pValue!=null){
				pValue = pValue.trim();
				this.headerSslCertificateUrlDecodeOrBase64Decode = Boolean.parseBoolean(pValue);
			}else{
				this.headerSslCertificateUrlDecodeOrBase64Decode = true;// default a true
			}
			
			pValue = getProperty(p, "header.ssl.certificate.enrich_BEGIN_END", protocollo, idSoggetto);
			if(pValue!=null){
				pValue = pValue.trim();
				this.headerSslCertificateEnrich_BEGIN_END = Boolean.parseBoolean(pValue);
			}else{
				this.headerSslCertificateEnrich_BEGIN_END = false;// default a false
			}
			
			pValue = getProperty(p, "header.ssl.certificate.replaceCharacters", protocollo, idSoggetto);
			if(pValue!=null){
				pValue = pValue.trim();
				this.headerSslCertificateReplaceCharacters = Boolean.parseBoolean(pValue);
			}else{
				this.headerSslCertificateReplaceCharacters = false;// default a false
			}
			
			if(this.headerSslCertificateReplaceCharacters) {
				this.headerSslCertificateReplaceCharacters_source = getProperty(p, "header.ssl.certificate.replaceCharacters.source", protocollo, idSoggetto);
				if(this.headerSslCertificateReplaceCharacters_source!=null){
					if(StringUtils.isNotEmpty(this.headerSslCertificateReplaceCharacters_source)) {
						if("\\t".equals(this.headerSslCertificateReplaceCharacters_source)) {
							this.headerSslCertificateReplaceCharacters_source = "\t";
						}
						else if("\\n".equals(this.headerSslCertificateReplaceCharacters_source)) {
							this.headerSslCertificateReplaceCharacters_source = "\n";
						}
					}
					else {
						this.headerSslCertificateReplaceCharacters_source = null;
					}
				}
				
				this.headerSslCertificateReplaceCharacters_dest = getProperty(p, "header.ssl.certificate.replaceCharacters.dest", protocollo, idSoggetto);
				if(this.headerSslCertificateReplaceCharacters_dest!=null){
					if(StringUtils.isNotEmpty(this.headerSslCertificateReplaceCharacters_dest)) {
						if("\\t".equals(this.headerSslCertificateReplaceCharacters_dest)) {
							this.headerSslCertificateReplaceCharacters_dest = "\t";
						}
						else if("\\n".equals(this.headerSslCertificateReplaceCharacters_dest)) {
							this.headerSslCertificateReplaceCharacters_dest = "\n";
						}
					}
					else {
						this.headerSslCertificateReplaceCharacters_dest = null;
					}
				}
			}
			
			String t = getProperty(p, "header.ssl.certificate.truststore.path", protocollo, idSoggetto);
			if(t!=null && StringUtils.isNotEmpty(t)) {
				File fTrustStore = new File(t);
				if(!fTrustStore.exists()) {
					throw new GestoreCredenzialiException("Il truststore dei certificati ssl indicato ["+fTrustStore.getAbsolutePath()+"] non esiste");
				}
				if(!fTrustStore.canRead()) {
					throw new GestoreCredenzialiException("Il truststore dei certificati ssl indicato ["+fTrustStore.getAbsolutePath()+"] non è accessibile in lettura");
				}
				String password = getProperty(p, "header.ssl.certificate.truststore.password", protocollo, idSoggetto);
				if(password==null) {
					throw new GestoreCredenzialiException("Non è stata indicata una password per il truststore dei certificati ssl indicato ["+fTrustStore.getAbsolutePath()+"]");
				}
				this.headerSslCertificateTrustStorePath = fTrustStore.getAbsolutePath();
				this.headerSslCertificateTrustStorePassword = password;
				String type = getProperty(p, "header.ssl.certificate.truststore.type", protocollo, idSoggetto);
				if(type==null) {
					this.headerSslCertificateTrustStoreType = "jks";
				}
				else {
					this.headerSslCertificateTrustStoreType = type;
				}
			}
			
			if(this.headerSslCertificateTrustStorePath!=null) {
				
				String v = getProperty(p, "header.ssl.certificate.truststore.validityCheck", protocollo, idSoggetto);
				if(v!=null && StringUtils.isNotEmpty(v)) {
					try {
						this.headerSslCertificateTrustStoreCheckValid = Boolean.valueOf(v);
					}catch(Throwable e) {
						throw new GestoreCredenzialiException("Errore durante la lettura della proprietà 'header.ssl.certificate.truststore.validityCheck' (valore: "+v+"): "+e.getMessage(),e);
					}
				}
				
				String crls = getProperty(p, "header.ssl.certificate.truststore.crls", protocollo, idSoggetto);
				if(crls!=null && StringUtils.isNotEmpty(crls)) {
					this.headerSslCertificateCrlX509 = crls;
				}
				
				String ocspPolicy = getProperty(p, "header.ssl.certificate.truststore.ocspPolicy", protocollo, idSoggetto);
				if(ocspPolicy!=null && StringUtils.isNotEmpty(ocspPolicy)) {
					this.headerSslCertificateOcspPolicy = ocspPolicy;
				}
			}
		}
		
		this.headerPrincipal = getProperty(p, "header.principal", protocollo, idSoggetto);
	
		
		if(this.headerBasicUsername==null && this.headerSslSubject==null && this.headerSslCertificate==null && this.headerPrincipal==null) {
			throw new GestoreCredenzialiException("L'abilitazione del gestore delle credenziali ("+prefix+"*) richiede almeno la definizione di un header su cui vengono fornite le credenziali");
		}
		if(this.headerBasicUsername!=null) {
			if(this.headerBasicPassword==null) {
				throw new GestoreCredenzialiException("L'abilitazione del gestore delle credenziali ("+prefix+"*) richiede la definizione di un header su cui viene indicata la password, se viene definito un header per l'username");
			}
		}
		
		
		String modalitaCanaleS = getProperty(p, "modalita", protocollo, idSoggetto);
		if(modalitaCanaleS==null) {
			throw new GestoreCredenzialiException("Proprietà '"+prefix+"modalita non impostata");
		}	
		this.modalitaAutenticazioneCanale = ModalitaAutenticazioneGestoreCredenziali.toEnumConstant(modalitaCanaleS);
		if(this.modalitaAutenticazioneCanale==null) {
			throw new GestoreCredenzialiException("Proprietà '"+prefix+"modalita impostata non correttamente, valore indicato ("+modalitaCanaleS+") non gestito");
		}
		switch (this.modalitaAutenticazioneCanale) {
		case NONE:
			break;
		case AT_LEAST_ONE:
			this.modalitaAutenticazioneCanaleAtLeastOne_error_description = getProperty(p, "modalita.atLeastOne.error_description.notFound", protocollo, idSoggetto);
			// Il controllo è gia stato fatto sopra che sia definito almeno una modalità per passare la credenziale
			break;
		case BASIC:
			if(this.headerBasicUsername==null || this.headerBasicPassword==null) {
				throw new GestoreCredenzialiException("Proprietà '"+prefix+"modalita non configurata completamente; la modalità '"+this.modalitaAutenticazioneCanale+"' richiede la definizione degli header http dove far veicolare le credenziali basic");
			}
			break;
		case SSL:
			if(this.headerSslSubject==null && this.headerSslCertificate==null) {
				throw new GestoreCredenzialiException("Proprietà '"+prefix+"modalita non configurata completamente; la modalità '"+this.modalitaAutenticazioneCanale+"' richiede la definizione di almeno un header http dove far veicolare le credenziali ssl");
			}
			break;
		case PRINCIPAL:
			if(this.headerPrincipal==null) {
				throw new GestoreCredenzialiException("Proprietà '"+prefix+"modalita non configurata completamente; la modalità '"+this.modalitaAutenticazioneCanale+"' richiede la definizione dell'header http dove far veicolare le credenziali principal");
			}
			break;
		default:
			break;
		}
	}
	
	private String getProperty(Properties p, String key, String protocollo, IDSoggetto idSoggetto) {
		
		String key1 = null;
		String key2 = null;
		String key3 = null;
		String key4 = null;
		String key5 = null;
		String keyOriginale = key;
		
		if(protocollo!=null && idSoggetto!=null) {
			key1 = protocollo+"-"+idSoggetto.getTipo()+"-"+idSoggetto.getNome()+"."+key;
			key3 = protocollo+"-"+idSoggetto.getNome()+"."+key;
		}
		if(idSoggetto!=null) {
			key2 = idSoggetto.getTipo()+"-"+idSoggetto.getNome()+"."+key;
			key4 = idSoggetto.getNome()+"."+key;
		}
		if(protocollo!=null) {
			key5 = protocollo+"."+key;
		}
		
		String value = null;
		
		if(key1!=null) {
			value = p.getProperty(key1);
			if(value!=null) {
				if(!StringUtils.isEmpty(value.trim())) {
					return value.trim();
				}
				else {
					return null; // e' stata definita vuota per disattivare una opzione generale
				}
			}
		}
		
		if(key2!=null) {
			value = p.getProperty(key2);
			if(value!=null) {
				if(!StringUtils.isEmpty(value.trim())) {
					return value.trim();
				}
				else {
					return null; // e' stata definita vuota per disattivare una opzione generale
				}
			}
		}
		
		if(key3!=null) {
			value = p.getProperty(key3);
			if(value!=null) {
				if(!StringUtils.isEmpty(value.trim())) {
					return value.trim();
				}
				else {
					return null; // e' stata definita vuota per disattivare una opzione generale
				}
			}
		}
		
		if(key4!=null) {
			value = p.getProperty(key4);
			if(value!=null) {
				if(!StringUtils.isEmpty(value.trim())) {
					return value.trim();
				}
				else {
					return null; // e' stata definita vuota per disattivare una opzione generale
				}
			}
		}
		
		if(key5!=null) {
			value = p.getProperty(key5);
			if(value!=null) {
				if(!StringUtils.isEmpty(value.trim())) {
					return value.trim();
				}
				else {
					return null; // e' stata definita vuota per disattivare una opzione generale
				}
			}
		}
		
		value = p.getProperty(keyOriginale);
		if(value!=null && !StringUtils.isEmpty(value.trim())) {
			return value.trim();
		}
		
		return null;

	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	public String getNome() {
		return this.nome;
	}
	
	public String getRealm() {
		return this.realm;
	}
	public String getAuthType() {
		return this.authType;
	}
	
	public TipoAutenticazioneGestoreCredenziali getTipoAutenticazioneCanale() {
		return this.tipoAutenticazioneCanale;
	}
	public String getAutenticazioneCanaleBasicUsername() {
		return this.autenticazioneCanaleBasicUsername;
	}
	public String getAutenticazioneCanaleBasicPassword() {
		return this.autenticazioneCanaleBasicPassword;
	}
	public String getAutenticazioneCanaleSslSubject() {
		return this.autenticazioneCanaleSslSubject;
	}
	public String getAutenticazioneCanalePrincipal() {
		return this.autenticazioneCanalePrincipal;
	}
	
	public ModalitaAutenticazioneGestoreCredenziali getModalitaAutenticazioneCanale() {
		return this.modalitaAutenticazioneCanale;
	}
	public String getModalitaAutenticazioneCanaleAtLeastOne_error_description() {
		return this.modalitaAutenticazioneCanaleAtLeastOne_error_description;
	}
	
	public String getHeaderBasicUsername() {
		return this.headerBasicUsername;
	}
	public String getHeaderBasicPassword() {
		return this.headerBasicPassword;
	}
	
	public String getHeaderSslSubject() {
		return this.headerSslSubject;
	}
	public String getHeaderSslIssuer() {
		return this.headerSslIssuer;
	}
	
	public String getHeaderSslCertificate() {
		return this.headerSslCertificate;
	}
	public boolean isHeaderSslCertificateUrlDecode() {
		return this.headerSslCertificateUrlDecode;
	}
	public boolean isHeaderSslCertificateBase64Decode() {
		return this.headerSslCertificateBase64Decode;
	}
	public boolean isHeaderSslCertificateUrlDecodeOrBase64Decode() {
		return this.headerSslCertificateUrlDecodeOrBase64Decode;
	}
	public boolean isHeaderSslCertificateEnrich_BEGIN_END() {
		return this.headerSslCertificateEnrich_BEGIN_END;
	}
	public boolean isHeaderSslCertificateReplaceCharacters() {
		return this.headerSslCertificateReplaceCharacters;
	}
	public String getHeaderSslCertificateReplaceCharacters_source() {
		return this.headerSslCertificateReplaceCharacters_source;
	}
	public String getHeaderSslCertificateReplaceCharacters_dest() {
		return this.headerSslCertificateReplaceCharacters_dest;
	}
	public String getHeaderSslCertificateTrustStorePath() {
		return this.headerSslCertificateTrustStorePath;
	}
	public String getHeaderSslCertificateTrustStoreType() {
		return this.headerSslCertificateTrustStoreType;
	}
	public String getHeaderSslCertificateTrustStorePassword() {
		return this.headerSslCertificateTrustStorePassword;
	}
	public boolean isHeaderSslCertificateTrustStoreCheckValid() {
		return this.headerSslCertificateTrustStoreCheckValid;
	}
	public String getHeaderSslCertificateCrlX509() {
		return this.headerSslCertificateCrlX509;
	}
	public String getHeaderSslCertificateOcspPolicy() {
		return this.headerSslCertificateOcspPolicy;
	}
	
	public String getHeaderPrincipal() {
		return this.headerPrincipal;
	}
}
