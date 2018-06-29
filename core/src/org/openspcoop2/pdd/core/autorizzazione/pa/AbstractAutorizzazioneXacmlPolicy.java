/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core.autorizzazione.pa;

import java.util.List;

import org.herasaf.xacml.core.context.impl.DecisionType;
import org.herasaf.xacml.core.context.impl.ResultType;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;
import org.openspcoop2.pdd.core.autorizzazione.XACMLPolicyUtilities;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.utils.xacml.MarshallUtilities;
import org.openspcoop2.utils.xacml.ResultCombining;
import org.openspcoop2.utils.xacml.ResultUtilities;
import org.openspcoop2.utils.xacml.XacmlRequest;
import org.slf4j.Logger;

/**
 * Classe che implementa una autorizzazione basata sui ruoli
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

abstract class AbstractAutorizzazioneXacmlPolicy extends AbstractAutorizzazioneBase {


	
	private boolean checkRuoloRegistro;
	private boolean checkRuoloEsterno;
	private String nomeAutorizzazione;
	private Logger log = null;
	
	protected AbstractAutorizzazioneXacmlPolicy(boolean checkRuoloRegistro, boolean checkRuoloEsterno, String nomeAutorizzazione){
		this.checkRuoloRegistro = checkRuoloRegistro;
		this.checkRuoloEsterno = checkRuoloEsterno;
		this.nomeAutorizzazione = nomeAutorizzazione;
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
	}
	
	
	private XacmlRequest xacmlRequest = null;
	private String xacmlRequestAsString = null;
	private String policyKey = null;
	private synchronized XacmlRequest getXacmlRequest(DatiInvocazionePortaApplicativa datiInvocazione, Logger log) throws AutorizzazioneException{
		if(this.xacmlRequest==null){
			if(datiInvocazione.getIdPA()!=null){
				this.policyKey =  "http://govway.org/in/"+datiInvocazione.getIdPA().getNome();
			}
			else if(datiInvocazione.getIdPD()!=null){
				this.policyKey =  "http://govway.org/out/"+datiInvocazione.getIdPD().getNome();
			} 
			else{
				throw new AutorizzazioneException("Identificativo Porta non presente");
			}
			this.xacmlRequest = XACMLPolicyUtilities.newXacmlRequest(this.getProtocolFactory(), datiInvocazione,
	    			this.checkRuoloRegistro, this.checkRuoloEsterno, this.policyKey);
			this.xacmlRequestAsString = new String(MarshallUtilities.marshallRequest(this.xacmlRequest));
			this.log.debug("XACML-Request (idPolicy:"+this.policyKey+"): "+this.xacmlRequestAsString);
		}
		return this.xacmlRequest;
	}
	
	@Override
	public String getSuffixKeyAuthorizationResultInCache(DatiInvocazionePortaApplicativa datiInvocazione) {
		try{
			initDatiPolicy(datiInvocazione, false);
		}catch(Exception e){
			// Comunque l'errore verrà rilanciato anche durante l'utilizzo della classe vera e propria nel metodo 'process'
			// quando viene effettuata la 'XACMLPolicyUtilities.loadPolicy'. 
		}
		return this.xacmlRequestAsString;
	}

	@Override
	public boolean saveAuthorizationResultInCache() {
		return true;
	}
	
	private void initDatiPolicy(DatiInvocazionePortaApplicativa datiInvocazione, boolean throwError) throws Exception {
		if(this.xacmlRequestAsString==null){
			try{
				this.getXacmlRequest(datiInvocazione, this.log);
			}catch(Exception e){
				this.log.error("Autorizzazione "+this.nomeAutorizzazione+" non riuscita (create XACML-Request)",e);
				if(throwError) {
					throw e;
				}
			}
		}
	}
	
    @Override
	public EsitoAutorizzazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione) throws AutorizzazioneException{

    	EsitoAutorizzazionePortaApplicativa esito = new EsitoAutorizzazionePortaApplicativa();
    	
    	IDSoggetto idSoggetto = datiInvocazione.getIdSoggettoFruitore();
		IDServizio idServizio = datiInvocazione.getIdServizio();
		String errore = this.getErrorString(idSoggetto, idServizio);
		
		
		
    	// ****** Inizializzazione Policy Key (Serve per la chiave identificativa della policy e anche per la cache) ********
    	// Inizializzazione 'policyKey'
    	// Questo codice serve per creare la chiave della policy che poi viene utilizzata dal PdD
    	// Il codice viene anche usato nel metodo 'getSuffixKeyAuthorizationResultInCache', 
    	// e quindi in presenza di cache attiva, in questo metodo l'invocazione non effettua nessuna operazione.
    	// Se invece la cache non e' attiva, l'invocazione inizializza i dati della policy tra cui la 'policyKey'
    	try{
    		initDatiPolicy(datiInvocazione,true);
    	}catch(Exception e){
    		this.log.error("Autorizzazione "+this.nomeAutorizzazione+" ("+this.policyKey+") non riuscita (init XACML-Policy)",e);
    		esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA));
    		esito.setAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
			return esito;
    	}
		
		
    	
    	
    	
    	// ****** Raccolta Dati e Policy ********
		    	
    	try{
    		String xacmlPolicyPorta = null;
    		if(datiInvocazione.getPa()!=null) {
    			xacmlPolicyPorta = datiInvocazione.getPa().getXacmlPolicy();
    		}
    		else if(datiInvocazione.getPd()!=null) {
    			xacmlPolicyPorta = datiInvocazione.getPd().getXacmlPolicy();
    		}
    		XACMLPolicyUtilities.loadPolicy(xacmlPolicyPorta, idServizio, this.policyKey, false, null, this.log);
    	}catch(Exception e){
    		this.log.error("Autorizzazione "+this.nomeAutorizzazione+" ("+this.policyKey+") non riuscita (load XACML-Policy)",e);
			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA));
    		esito.setAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
			return esito;
    	}
    	
    	
    	
    	
    	// ****** Produzione XACMLRequest a partire dai dati ********
    	
    	XacmlRequest xacmlRequest = null;
    	try{
	    	xacmlRequest = this.getXacmlRequest(datiInvocazione, this.log);
    	}catch(Exception e){
    		this.log.error("Autorizzazione "+this.nomeAutorizzazione+" ("+this.policyKey+") non riuscita (create XACML-Request)",e);
			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA));
    		esito.setAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
			return esito;
    	}
    
    	
    	
    	
    	// ****** Valutazione XACMLRequest con PdD ********
    	
    	List<ResultType> results = null;
		try {	    	
			results = ResultUtilities.evaluate(xacmlRequest, this.log, this.policyKey, XACMLPolicyUtilities.getPolicyDecisionPoint(this.log));
		}catch(Exception e){
    		this.log.error("Autorizzazione "+this.nomeAutorizzazione+" ("+this.policyKey+") non riuscita (evaluate XACML-Request)",e);
			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA));
    		esito.setAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
			return esito;
    	}
    	
		DecisionType decision = ResultCombining.combineDenyOverrides(results);
		if(DecisionType.PERMIT.equals(decision)) {
			esito.setAutorizzato(true);
	    	return esito;
    	} else {
    		try{
    			String resultAsStringForLog = ResultUtilities.toRawString(results);
    			this.log.error("Autorizzazione con XACMLPolicy fallita ("+this.policyKey+") ;\nrequest: "+this.xacmlRequestAsString+
    					"\nresults (size:"+results.size()+"): \n"+resultAsStringForLog);
    			
    			String resultAsString = ResultUtilities.toString(results, decision);
    			if(errore!=null){
        			errore = errore + " ";
        		}
    			errore = errore + "("+resultAsString+")";
    			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
        		esito.setAutorizzato(false);
    			return esito;
        		
    		}catch(Exception e){
        		this.log.error("Autorizzazione "+this.nomeAutorizzazione+" ("+this.policyKey+") fallita (read XACML-Results)",e);
        		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
        				get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
    			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA));
        		esito.setAutorizzato(false);
        		esito.setEccezioneProcessamento(e);
    			return esito;
        	}
    	}
    	
    }

   
}

