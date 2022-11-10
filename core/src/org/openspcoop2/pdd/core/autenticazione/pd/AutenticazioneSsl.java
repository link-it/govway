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



package org.openspcoop2.pdd.core.autenticazione.pd;

import java.security.cert.CertStore;
import java.util.List;

import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.autenticazione.WWWAuthenticateConfig;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.keystore.GestoreKeystoreCaching;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.KeyStore;

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
    public EsitoAutenticazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione) throws AutenticazioneException{

    	EsitoAutenticazionePortaDelegata esito = new EsitoAutenticazionePortaDelegata();
    	
    	OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
    	WWWAuthenticateConfig wwwAuthenticateConfig = op2Properties.getRealmAutenticazioneHttpsWWWAuthenticateConfig();
    	
    	if(datiInvocazione==null) {
    		throw new AutenticazioneException("Param datiInvocazione is null");
    	}
    	
    	IDSoggetto soggettoFruitore = null;
    	if(datiInvocazione.getPd()!=null) {
    		soggettoFruitore = new IDSoggetto(datiInvocazione.getPd().getTipoSoggettoProprietario(), datiInvocazione.getPd().getNomeSoggettoProprietario());
    	}
    	    	
    	Credenziali credenziali = datiInvocazione.getInfoConnettoreIngresso().getCredenziali();
		
    	String subject = credenziali.getSubject();
    	String issuer = credenziali.getIssuer();
    	CertificateInfo certificate = null;
    	if(credenziali.getCertificate()!=null) {
    		certificate = credenziali.getCertificate().getCertificate();
    	}
    	
    	RequestInfo requestInfo = datiInvocazione.getRequestInfo();
    	
		// Controllo credenziali fornite
    	if( subject==null || "".equals(subject) ){
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl(CostantiProtocollo.CREDENZIALI_NON_FORNITE,subject));
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
    		if(datiInvocazione.getPd()!=null) {
    			proprieta = datiInvocazione.getPd().getProprietaList();
    		}

    		boolean checkValid = false;
    		boolean trustStore = false;
    		KeyStore trustStoreCertificatiX509 = null;
    		CertStore trustStoreCertificatiX509_crls = null;
    		try {
    			checkValid = CostantiProprieta.isAutenticazioneHttpsValidityCheck(proprieta, op2Properties.isAutenticazioneHttpsPortaDelegataValidityCheck());
    			trustStore = CostantiProprieta.isAutenticazioneHttpsTrustStore(proprieta, op2Properties.getAutenticazioneHttpsPortaDelegataTruststorePath());
    			if(trustStore) {
    				String path = CostantiProprieta.getAutenticazioneHttpsTrustStorePath(proprieta, op2Properties.getAutenticazioneHttpsPortaDelegataTruststorePath());
    				if(path!=null) {
    					try {
    						String password = CostantiProprieta.getAutenticazioneHttpsTrustStorePassword(proprieta, op2Properties.getAutenticazioneHttpsPortaDelegataTruststorePassword());
    						String type = CostantiProprieta.getAutenticazioneHttpsTrustStoreType(proprieta, op2Properties.getAutenticazioneHttpsPortaDelegataTruststoreType());
    	    				trustStoreCertificatiX509 = GestoreKeystoreCaching.getMerlinTruststore(requestInfo, path, 
    	    						type, 
    								password).getTrustStore();
    					}catch(Exception e){
    						throw new Exception("Errore durante la lettura del truststore indicato ("+path+"): "+e.getMessage());
    					}
    				}
    				
    				if(trustStoreCertificatiX509!=null) {
    					String crl = CostantiProprieta.getAutenticazioneHttpsTrustStoreCRLs(proprieta, op2Properties.getAutenticazioneHttpsPortaDelegataTruststoreCRLs());
        				if(crl!=null) {
    						try {
    							trustStoreCertificatiX509_crls = GestoreKeystoreCaching.getCRLCertstore(requestInfo, crl).getCertStore();
    						}catch(Exception e){
    							throw new Exception("Richiesta autenticazione ssl del gateway gestore delle credenziali; errore durante la lettura delle CRLs ("+crl+"): "+e.getMessage());
    						}
    					}
    				}
    			}
    		}catch(Exception e) {
    			if(this.logError) {
        			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneSsl non riuscita",e);
    			}
    			esito.setErroreIntegrazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
    			esito.setClientIdentified(false);
    			esito.setEccezioneProcessamento(e);
    			return esito;
    		}
    		
    		if(checkValid 
    				&&
    				trustStoreCertificatiX509_crls==null) { // altrimenti la validita' viene verificata insieme alle CRL
	    		try {
	    			certificate.checkValid();
	    		}catch(Exception e) {
	    			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, 
	    					ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl(CostantiProtocollo.CREDENZIALI_FORNITE_NON_CORRETTE+": "+e.getMessage(),subject));
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
						throw new Exception("Certificato non verificabile rispetto alle CA conosciute");
					}
					if(trustStoreCertificatiX509_crls!=null) {
						try {
							certificate.checkValid(trustStoreCertificatiX509_crls, trustStoreCertificatiX509);
						}catch(Throwable t) {
							throw new Exception("Certificato non valido: "+t.getMessage());
						}
					}
	    		}catch(Exception e) {
	    			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, 
	    					ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl(CostantiProtocollo.CREDENZIALI_FORNITE_NON_CORRETTE+": "+e.getMessage(),subject));
	    			esito.setClientAuthenticated(false);
	    			esito.setClientIdentified(false);
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
		
    	// Provo a identificare il chiamante rispetto ad una entita' del registro
		IDServizioApplicativo idServizioApplicativo = null;
		try{
			ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(datiInvocazione.getState());
			
			// NOTA: il fatto di essersi registrati come strict o come non strict è insito nella registrazione dell'applicativo
			
			// 1. Prima si cerca per certificato strict
			if(certificate!=null) {
				idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(certificate, true);
			}
			if(idServizioApplicativo==null) {
				// 2. Poi per certificato no strict
				if(certificate!=null) {
					idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(certificate, false);
				}	
			}
			if(idServizioApplicativo==null) {
				// 3. per subject/issuer
				idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(subject, issuer);	
			}
			if(idServizioApplicativo==null) {
				// 4. solo per subject
				idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(subject, null);	
			}
			
			if(idServizioApplicativo!=null && soggettoFruitore==null) {
				soggettoFruitore = idServizioApplicativo.getIdSoggettoProprietario();
			}
		}catch(Exception e){
			if(this.logError) {
    			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneSsl non riuscita",e);
			}
			esito.setErroreIntegrazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		if(idServizioApplicativo == null){
			// L'identificazione in ssl non e' obbligatoria
			//esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl(CostantiProtocollo.CREDENZIALI_FORNITE_NON_CORRETTE,subject));
			esito.setClientIdentified(false);
		}
		else {
			if(OpenSPCoop2Properties.getInstance().isAutenticazioneHttpsPortaDelegataCheckSoggettiProprietari() && idServizioApplicativo.getIdSoggettoProprietario().equals(soggettoFruitore)==false) {
				esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, 
						ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl(
								"soggetto proprietario ("+idServizioApplicativo.getIdSoggettoProprietario()+") dell'applicativo identificato ("+idServizioApplicativo.getNome()+") differente dal soggetto proprietario della porta invocata ("+soggettoFruitore+")",subject));
				esito.setClientIdentified(false);
				return esito;
			}
			else {
				esito.setClientIdentified(true);
				esito.setIdServizioApplicativo(idServizioApplicativo);
			}
		}
		
		return esito;
		
    }

}

