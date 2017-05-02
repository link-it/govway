/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core.autorizzazione.pd;

import java.util.List;

import org.herasaf.xacml.core.context.impl.DecisionType;
import org.herasaf.xacml.core.context.impl.ResultType;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;
import org.openspcoop2.pdd.core.autorizzazione.XACMLPolicyUtilities;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
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
	private synchronized XacmlRequest getXacmlRequest(DatiInvocazionePortaDelegata datiInvocazione, Logger log) throws AutorizzazioneException{
		if(this.xacmlRequest==null){
	    	this.policyKey = "http://openspcoop2.org/PD/"+datiInvocazione.getIdPD().getNome();
			this.xacmlRequest = XACMLPolicyUtilities.newXacmlRequest(this.getProtocolFactory(), datiInvocazione, 
	    			this.checkRuoloRegistro, this.checkRuoloEsterno, this.policyKey);
			this.xacmlRequestAsString = new String(MarshallUtilities.marshallRequest(this.xacmlRequest));
			this.log.debug("XACML-Request (idPolicy:"+this.policyKey+"): "+this.xacmlRequestAsString);
		}
		return this.xacmlRequest;
	}
	
	@Override
	public String getSuffixKeyAuthorizationResultInCache(DatiInvocazionePortaDelegata datiInvocazione) {
		if(this.xacmlRequestAsString==null){
			try{
				this.getXacmlRequest(datiInvocazione, this.log);
			}catch(Exception e){
				this.log.error("Autorizzazione "+this.nomeAutorizzazione+" non riuscita (create XACML-Request)",e);
				// Comunque l'errore verrà rilanciato anche durante l'utilizzo della classe vera e propria
			}
		}
		return this.xacmlRequestAsString;
	}

	@Override
	public boolean saveAuthorizationResultInCache() {
		return true;
	}
	
	
	
    @Override
	public EsitoAutorizzazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione) throws AutorizzazioneException{

    	EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
    	
    	String servizioApplicativo = null;
    	if(datiInvocazione.getIdServizioApplicativo()!=null){
    		servizioApplicativo = datiInvocazione.getIdServizioApplicativo().getNome();
    	}

    	
    	
    	// ****** Raccolta Dati e Policy ********
		
    	IDServizio idServizio = datiInvocazione.getIdServizio();
    	
    	try{
    		XACMLPolicyUtilities.loadPolicy(idServizio, this.policyKey, true, this.log);
    	}catch(Exception e){
    		this.log.error("Autorizzazione "+this.nomeAutorizzazione+" ("+this.policyKey+") non riuscita (load XACML-Policy)",e);
    		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    				get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
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
    		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    				get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
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
    		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    				get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
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
    			esito.setAutorizzato(false);
        		if(servizioApplicativo!=null){
        			esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.
        					getErrore404_AutorizzazioneFallitaServizioApplicativo(servizioApplicativo,resultAsString));
    			}
    			else{
    				esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.
    						getErrore404_AutorizzazioneFallitaServizioApplicativoAnonimo(resultAsString));
    			}
    			return esito;
        		
    		}catch(Exception e){
        		this.log.error("Autorizzazione "+this.nomeAutorizzazione+" ("+this.policyKey+") fallita (read XACML-Results)",e);
        		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
        				get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
    			esito.setAutorizzato(false);
    			esito.setEccezioneProcessamento(e);
    			return esito;
        	}
    	}
    	
    }

   
}

