/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.core.autenticazione.pa;

import java.security.cert.CertStore;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.autenticazione.WWWAuthenticateConfig;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.keystore.GestoreKeystoreCaching;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.ConfigurazioneFiltroServiziApplicativi;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.keystore.cache.GestoreOCSPResource;
import org.openspcoop2.security.keystore.cache.GestoreOCSPValidator;
import org.openspcoop2.utils.LoggerBuffer;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.transport.http.IOCSPValidator;
import org.openspcoop2.utils.transport.http.OCSPResponseException;

/**
 * Classe che implementa una autenticazione BASIC.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutenticazioneSsl extends AbstractAutenticazioneBase {

	private boolean logError = true;
	@Override
	public void setLogError(boolean logError) {
		this.logError = logError;
	}
	
    @Override
    public EsitoAutenticazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione) throws AutenticazioneException{

    	EsitoAutenticazionePortaApplicativa esito = new EsitoAutenticazionePortaApplicativa();
    	
    	if(datiInvocazione==null) {
    		throw new AutenticazioneException("Param datiInvocazione is null");
    	}
    	
    	OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
    	WWWAuthenticateConfig wwwAuthenticateConfig = op2Properties.getRealmAutenticazioneHttpsWWWAuthenticateConfig();
    	
    	Credenziali credenziali = datiInvocazione.getInfoConnettoreIngresso().getCredenziali();
		
    	RequestInfo requestInfo = datiInvocazione.getRequestInfo();
    	
    	String subject = credenziali.getSubject();
    	String issuer = credenziali.getIssuer();
    	CertificateInfo certificate = null;

    	// Controllo credenziali fornite
    	if( subject==null || "".equals(subject) ){
    		esito.setErroreCooperazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE.getErroreCooperazione());
    		esito.setClientAuthenticated(false);
    		esito.setClientIdentified(false);
    		if(wwwAuthenticateConfig!=null) {
    			esito.setWwwAuthenticateErrorHeader(wwwAuthenticateConfig.buildWWWAuthenticateHeaderValue_notFound());
    		}
			return esito;
		}
    	if(credenziali.getCertificate()!=null) {
    		certificate = credenziali.getCertificate().getCertificate();
    		
    		List<Proprieta> proprieta = null;
    		if(datiInvocazione.getPa()!=null) {
    			proprieta = datiInvocazione.getPa().getProprietaList();
    		}
    		else if(datiInvocazione.getPd()!=null) {
    			proprieta = datiInvocazione.getPd().getProprietaList();
    		}
    		
    		boolean checkValid = false;
    		boolean trustStore = false;
    		KeyStore trustStoreCertificatiX509 = null;
    		CertStore trustStoreCertificatiX509crls = null;
    		IOCSPValidator ocspValidator = null;
    		try {
    			checkValid = CostantiProprieta.isAutenticazioneHttpsValidityCheck(proprieta, op2Properties.isAutenticazioneHttpsPortaApplicativaValidityCheck());
    			trustStore = CostantiProprieta.isAutenticazioneHttpsTrustStore(proprieta, op2Properties.getAutenticazioneHttpsPortaApplicativaTruststorePath());
    			if(trustStore) {
    				String path = CostantiProprieta.getAutenticazioneHttpsTrustStorePath(proprieta, op2Properties.getAutenticazioneHttpsPortaApplicativaTruststorePath());
    				if(path!=null) {
    					try {
    						String password = CostantiProprieta.getAutenticazioneHttpsTrustStorePassword(proprieta, op2Properties.getAutenticazioneHttpsPortaApplicativaTruststorePassword());
    						String type = CostantiProprieta.getAutenticazioneHttpsTrustStoreType(proprieta, op2Properties.getAutenticazioneHttpsPortaApplicativaTruststoreType());
    	    				trustStoreCertificatiX509 = GestoreKeystoreCaching.getMerlinTruststore(requestInfo, path, 
    	    						type, 
    								password).getTrustStore();
    					}catch(Exception e){
    						throw new CoreException("Errore durante la lettura del truststore indicato ("+path+"): "+e.getMessage());
    					}
    				}
    				
    				if(trustStoreCertificatiX509!=null) {
    					
    					String crl = CostantiProprieta.getAutenticazioneHttpsTrustStoreCRLs(proprieta, op2Properties.getAutenticazioneHttpsPortaApplicativaTruststoreCRLs());
        				    					
    					boolean crlByOcsp = false;
    					String ocspPolicy = CostantiProprieta.getAutenticazioneHttpsTrustStoreOCSPPolicy(proprieta, op2Properties.getAutenticazioneHttpsPortaApplicativaTruststoreOCSPPolicy());
        				if(ocspPolicy!=null) {
    						LoggerBuffer lb = new LoggerBuffer();
    						lb.setLogDebug(OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
    						lb.setLogError(OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
    						GestoreOCSPResource ocspResourceReader = new GestoreOCSPResource(requestInfo);
    						try {
    							ocspValidator = new GestoreOCSPValidator(requestInfo, lb, 
    									trustStoreCertificatiX509,
    									crl, 
    									ocspPolicy, 
    									ocspResourceReader);
    						}catch(Exception e){
    							throw new CoreException("Errore durante l'inizializzazione del gestore della policy OCSP ("+ocspPolicy+"): "+e.getMessage());
    						}
    						if(ocspValidator!=null) {
    							GestoreOCSPValidator gOcspValidator = (GestoreOCSPValidator) ocspValidator;
    							if(gOcspValidator.getOcspConfig()!=null) {
    								crlByOcsp = gOcspValidator.getOcspConfig().isCrl();
    							}
    						}
    					}
    					
    					if(crl!=null && !crlByOcsp) {
    						try {
    							trustStoreCertificatiX509crls = GestoreKeystoreCaching.getCRLCertstore(requestInfo, crl).getCertStore();
    						}catch(Exception e){
    							throw new CoreException("Errore durante la lettura delle CRLs ("+crl+"): "+e.getMessage());
    						}
    					}
    				}
    			}
    		}catch(Exception e) {
    			if(this.logError) {
        			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneSsl non riuscita",e);
    			}
    			esito.setErroreCooperazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
    			esito.setClientIdentified(false);
    			esito.setEccezioneProcessamento(e);
    			return esito;
    		}
    		
    		if(checkValid 
    				&&
    				trustStoreCertificatiX509crls==null) { // altrimenti la validita' viene verificata insieme alle CRL
	    		try {
	    			certificate.checkValid();
	    		}catch(Exception e) {
	    			esito.setErroreCooperazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, 
        					ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_FORNITE_NON_CORRETTE.getErroreCredenzialiForniteNonCorrette(e.getMessage()));
        			esito.setClientAuthenticated(false);
        			esito.setClientIdentified(false);
        			if(wwwAuthenticateConfig!=null) {
        				esito.setWwwAuthenticateErrorHeader(wwwAuthenticateConfig.buildWWWAuthenticateHeaderValue_invalid());
        			}
        			return esito;
	    		}
    		}
    		
    		if(trustStoreCertificatiX509!=null) {
	    		try {
		    		if(certificate.isVerified(trustStoreCertificatiX509, true)==false) {
						throw new CoreException("Certificato non verificabile rispetto alle CA conosciute");
					}
					if(trustStoreCertificatiX509crls!=null) {
						try {
							certificate.checkValid(trustStoreCertificatiX509crls, trustStoreCertificatiX509);
						}catch(Throwable t) {
							throw new CoreException("Certificato non valido: "+t.getMessage());
						}
					}
	    		}catch(Exception e) {
	    			esito.setErroreCooperazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, 
        					ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_FORNITE_NON_CORRETTE.getErroreCredenzialiForniteNonCorrette(e.getMessage()));
        			esito.setClientAuthenticated(false);
        			esito.setClientIdentified(false);
        			if(wwwAuthenticateConfig!=null) {
        				esito.setWwwAuthenticateErrorHeader(wwwAuthenticateConfig.buildWWWAuthenticateHeaderValue_invalid());
        			}
        			return esito;
	    		}
    		}
    		
    		if(ocspValidator!=null) {
				try {
					ocspValidator.valid(certificate.getCertificate());
				}catch(Throwable t) {
					esito.setErroreCooperazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, 
        					ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_FORNITE_NON_CORRETTE.getErroreCredenzialiForniteNonCorrette(t.getMessage()));
        			esito.setClientAuthenticated(false);
        			esito.setClientIdentified(false);
	    			if(!(t instanceof OCSPResponseException)) {
	    				esito.setNoCache(true);
	    			}
        			if(wwwAuthenticateConfig!=null) {
        				esito.setWwwAuthenticateErrorHeader(wwwAuthenticateConfig.buildWWWAuthenticateHeaderValue_invalid());
        			}
        			return esito;
				}
			}
    		
    	}
    	
    	// Essendoci l'identita' del chiamante, il client e' stato autenticato o da un frontend o dall'application server stesso
    	esito.setClientAuthenticated(true);
    	esito.setCredential(subject);
		
    	IDSoggetto idSoggetto = null;
		try{
			RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance(datiInvocazione.getState());
			List<Throwable> notFoundExceptions = new ArrayList<>();
			
			// NOTA: il fatto di essersi registrati come strict o come non strict è insito nella registrazione dell'applicativo
			
			// 1. Prima si cerca per certificato strict
			if(certificate!=null) {
				try{
					idSoggetto = registroServiziManager.getIdSoggettoByCredenzialiSsl(certificate, true, null); // all registry
				}catch(DriverRegistroServiziNotFound notFound){
					notFoundExceptions.add(notFound);
				}
			}
			if(idSoggetto==null &&
				// 2. Poi per certificato no strict
				certificate!=null) {
				try{
					idSoggetto = registroServiziManager.getIdSoggettoByCredenzialiSsl(certificate, false, null); // all registry
				}catch(DriverRegistroServiziNotFound notFound){
					notFoundExceptions.add(notFound);
				}	
			}
			if(idSoggetto==null) {
				// 3. per subject/issuer
				try {
					idSoggetto = registroServiziManager.getIdSoggettoByCredenzialiSsl(subject, issuer, null); // all registry
				}catch(DriverRegistroServiziNotFound notFound){
					notFoundExceptions.add(notFound);
				}
			}
			if(idSoggetto==null) {
				// 4. solo per subject
				try {
					idSoggetto = registroServiziManager.getIdSoggettoByCredenzialiSsl(subject, null, null); // all registry
				}catch(DriverRegistroServiziNotFound notFound){
					notFoundExceptions.add(notFound);
				}
			}
			
			if(idSoggetto==null && !notFoundExceptions.isEmpty()) {
				throw new UtilsMultiException(notFoundExceptions.toArray(new Throwable[1]));
			}
		}
		catch(UtilsMultiException notFound){
			if(this.logError) {
    			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug("AutenticazioneSsl non ha trovato risultati",notFound);
			}
		}
		catch(Exception e){
			if(this.logError) {
    			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneSsl non riuscita",e);
			}
			esito.setErroreCooperazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		IDServizioApplicativo idServizioApplicativo = null;
		try {
			if(idSoggetto==null && this.getProtocolFactory().createProtocolConfiguration().isSupportoAutenticazioneApplicativiErogazioni()
					&& 
					!Costanti.MODIPA_PROTOCOL_NAME.equals(this.getProtocolFactory().getProtocol())  // su ModI i certificati vengono associati per le firme
					) {
				
				ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(datiInvocazione.getState());
				
				// NOTA: il fatto di essersi registrati come strict o come non strict è insito nella registrazione dell'applicativo
				
				ConfigurazioneFiltroServiziApplicativi filtroHttps = ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiHttps();
				
				// 1. Prima si cerca per certificato strict
				if(certificate!=null) {
					idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(certificate, true,
							filtroHttps.getTipiSoggetti(), filtroHttps.isIncludiApplicativiNonModI(), filtroHttps.isIncludiApplicativiModIEsterni(), filtroHttps.isIncludiApplicativiModIInterni());
				}
				if(idServizioApplicativo==null &&
					// 2. Poi per certificato no strict
					certificate!=null) {
					idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(certificate, false,
							filtroHttps.getTipiSoggetti(), filtroHttps.isIncludiApplicativiNonModI(), filtroHttps.isIncludiApplicativiModIEsterni(), filtroHttps.isIncludiApplicativiModIInterni());
				}
				if(idServizioApplicativo==null) {
					// 3. per subject/issuer
					idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(subject, issuer,
							filtroHttps.getTipiSoggetti(), filtroHttps.isIncludiApplicativiNonModI(), filtroHttps.isIncludiApplicativiModIEsterni(), filtroHttps.isIncludiApplicativiModIInterni());	
				}
				if(idServizioApplicativo==null) {
					// 4. solo per subject
					idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(subject, null,
							filtroHttps.getTipiSoggetti(), filtroHttps.isIncludiApplicativiNonModI(), filtroHttps.isIncludiApplicativiModIEsterni(), filtroHttps.isIncludiApplicativiModIInterni());	
				}

				if(idServizioApplicativo!=null) {
					if(idSoggetto==null) {
						idSoggetto = idServizioApplicativo.getIdSoggettoProprietario();
					}
					// Non ha senso poter identificare entrambi con le stesse credenziali
//					else if(idServizioApplicativo.getIdSoggettoProprietario().equals(idSoggetto)==false) {
//						throw new Exception("Identificato sia un soggetto che un applicativo. Il soggetto ["+idSoggetto+
//								"] identificato è differente dal proprietario dell'applicativo identificato ["+idServizioApplicativo.getIdSoggettoProprietario()+"]");
//					}
				}
			}
		}
		catch(Exception e){
			if(this.logError) {
    				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneSsl (Applicativi) non riuscita",e);
			}
			esito.setErroreCooperazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		if(idSoggetto == null){
			// L'identificazione in ssl non e' obbligatoria
			/** esito.setErroreCooperazione(ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_FORNITE_NON_CORRETTE.getErroreCooperazione());*/
			esito.setClientIdentified(false);
			return esito;
		}
		else {
			esito.setClientIdentified(true);
			esito.setIdSoggetto(idSoggetto);
			esito.setIdServizioApplicativo(idServizioApplicativo);
		}
		
		return esito;
		
    }

}

