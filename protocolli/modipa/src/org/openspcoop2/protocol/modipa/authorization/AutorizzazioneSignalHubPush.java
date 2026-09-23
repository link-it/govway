/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.modipa.authorization;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autorizzazione.pd.AbstractAutorizzazioneBase;
import org.openspcoop2.pdd.core.autorizzazione.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.autorizzazione.pd.EsitoAutorizzazionePortaDelegata;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;

/**
 * Interfaccia che definisce un processo di autorizzazione per la fruizione di signal hub
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutorizzazioneSignalHubPush extends AbstractAutorizzazioneBase {

	private static final String GENERIC_ERROR = "Errore di processamento dell'autorizzazione signal hub";
	private static final String ROLE_SERVICE_UNRECOGNIZED = "L'applicativo autenticato non è configurato come pubblicatore del servizio indicato, né tramite assegnazione diretta né mediante ruolo associato";
	
	@Override
	public boolean saveAuthorizationResultInCache() {
		return false;
	}
	
    @Override
	public EsitoAutorizzazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione){    	
    	try {
    		// controllo che signalhub sia abilitato
    		ModIProperties modiProperties = ModIProperties.getInstance();
    		if(!modiProperties.isSignalHubEnabled()) { 
    			throw new ProtocolException("La funzionalità SignalHub non è attiva");
    		}
    		
    		return this.processEngine(datiInvocazione);
    	} catch (ProtocolException e) {
    		return buildConfigurazioneNonDisponibile(null, e);
    	}
    }
    
    private EsitoAutorizzazionePortaDelegata buildConfigurazioneNonDisponibile(String descrizioneErrore, Exception e) {
    	EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
		esito.setErroreIntegrazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
				descrizioneErrore!=null ? 
						 ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(descrizioneErrore, CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE)
						:
							 ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
		esito.setAutorizzato(false);
		esito.setDetails(GENERIC_ERROR);
		esito.setEccezioneProcessamento(e);
		return esito;
    }
    
    public EsitoAutorizzazionePortaDelegata processEngine(DatiInvocazionePortaDelegata datiInvocazione) throws ProtocolException {
    	
		PdDContext context = datiInvocazione.getPddContext();
    	
		// ottengo le proprieta del protocollo per avere gli applicativi/ruoli autorizzati
		SignalHubPushParams params = SignalHubPushParams.load(context);
		
		// l'applicativo pubblicatore può essere stato identificato sia tramite l'autenticazione di trasporto sia tramite il token
		List<ApplicativoIdentificato> applicativi = getApplicativiIdentificati(datiInvocazione);
		if(applicativi.isEmpty()) {
			EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
			esito.setAutorizzato(false);
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_DENY, ErroriIntegrazione.ERRORE_410_AUTENTICAZIONE_RICHIESTA.getErroreIntegrazione());
			/**esito.setDetails(ROLE_SERVICE_UNRECOGNIZED);*/
			return esito;
		}
		
		// il pubblicatore viene ricercato tra gli applicativi del soggetto che eroga il servizio indicato nel segnale
		IDSoggetto soggettoPubblicatore = params.getIdServizio()!=null ? params.getIdServizio().getSoggettoErogatore() : null;
		
		EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
		for (int i = 0; i < params.getRequiredAuthorizationsSize(); i++) {
			
			esito = authorize(datiInvocazione, applicativi, soggettoPubblicatore,
					params.getRequiredAuthorizationSA(i), params.getRequiredAuthorizationRole(i));
			
			// tutte le autorizzazioni richieste devono essere soddisfatte
			if(!esito.isAutorizzato()) {
				return esito;
			}
		}
		
		return esito;
    }
    
    private EsitoAutorizzazionePortaDelegata authorize(DatiInvocazionePortaDelegata datiInvocazione, 
    		List<ApplicativoIdentificato> applicativi, IDSoggetto soggettoPubblicatore,
    		String allowedService, String allowedRole) throws ProtocolException {
    	
    	EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
    	
    	// la configurazione dell'applicativo viene letta solamente se e' stato definito un ruolo pubblicatore
    	boolean verificaRuolo = isDefined(allowedRole);
    	
    	// e' sufficiente che una delle identita' associate alla richiesta risulti essere il pubblicatore configurato
    	for (ApplicativoIdentificato applicativo : applicativi) {
    		if( matchServizioApplicativo(applicativo.getId(), soggettoPubblicatore, allowedService) ||
    			(verificaRuolo && matchRuolo(applicativo.getServizioApplicativo(datiInvocazione), allowedRole)) ) {
    			esito.setAutorizzato(true);
    			return esito;
    		}
		}
    	
    	// viene segnalata l'identita' principale, cioe' quella di trasporto se presente, altrimenti quella fornita dal token
    	String servizioApplicativoNonAutorizzato = applicativi.get(0).getId().getNome();
    	esito.setAutorizzato(false);
		esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_DENY, ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.getErrore404_AutorizzazioneFallitaServizioApplicativo(servizioApplicativoNonAutorizzato));
		esito.setDetails(ROLE_SERVICE_UNRECOGNIZED);
		return esito;
    }
    
    private boolean matchServizioApplicativo(IDServizioApplicativo idServizioApplicativo, IDSoggetto soggettoPubblicatore, String allowedService) {
    	if(!isDefined(allowedService)) {
    		return false;
    	}
    	if(!allowedService.equals(idServizioApplicativo.getNome())) {
    		return false;
    	}
    	// applicativi omonimi appartenenti a soggetti differenti non sono equivalenti
    	return matchSoggetto(soggettoPubblicatore, idServizioApplicativo.getIdSoggettoProprietario());
    }
    
    private boolean matchSoggetto(IDSoggetto soggettoPubblicatore, IDSoggetto soggettoProprietario) {
    	if(soggettoPubblicatore==null) {
    		return true; // non e' stato possibile individuare il soggetto erogatore del servizio indicato nel segnale
    	}
    	return soggettoProprietario!=null &&
    			soggettoPubblicatore.getTipo()!=null && soggettoPubblicatore.getTipo().equals(soggettoProprietario.getTipo()) &&
    			soggettoPubblicatore.getNome()!=null && soggettoPubblicatore.getNome().equals(soggettoProprietario.getNome());
    }
    
    private boolean matchRuolo(ServizioApplicativo servizioApplicativo, String allowedRole) {
    	if(!isDefined(allowedRole) ||
    		servizioApplicativo==null ||
    		servizioApplicativo.getInvocazionePorta()==null ||
    		servizioApplicativo.getInvocazionePorta().getRuoli()==null) {
    		return false;
    	}
    	for (Ruolo role : servizioApplicativo.getInvocazionePorta().getRuoli().getRuoloList()) {
			if(allowedRole.equals(role.getNome())) {
				return true;
			}
		}
    	return false;
    }
    
    private static boolean isDefined(String value) {
    	return value!=null && !value.isEmpty() && !ModICostanti.MODIPA_VALUE_UNDEFINED.equals(value);
    }
    
    private List<ApplicativoIdentificato> getApplicativiIdentificati(DatiInvocazionePortaDelegata datiInvocazione) {
    	
    	List<ApplicativoIdentificato> applicativi = new ArrayList<>();
    	
    	// identita' fornita dall'autenticazione di trasporto o dall'header di integrazione
    	IDServizioApplicativo idServizioApplicativoTrasporto = datiInvocazione.getIdServizioApplicativo();
    	if(idServizioApplicativoTrasporto!=null && 
    		!CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(idServizioApplicativoTrasporto.getNome())) {
    		applicativi.add(new ApplicativoIdentificato(idServizioApplicativoTrasporto, datiInvocazione.getServizioApplicativo()));
    	}
    	
    	// identita' fornita dal token
    	PdDContext context = datiInvocazione.getPddContext();
    	if(context!=null && context.containsKey(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN)) {
    		IDServizioApplicativo idServizioApplicativoToken = (IDServizioApplicativo) context.getObject(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN);
    		if(idServizioApplicativoToken!=null) {
    			applicativi.add(new ApplicativoIdentificato(idServizioApplicativoToken, null));
    		}
    	}
    	
    	return applicativi;
    }
    
    /** Applicativo identificato per la richiesta in corso; la configurazione viene letta solamente se richiesta dalla verifica per ruolo */
    private static class ApplicativoIdentificato {
    	
    	private final IDServizioApplicativo id;
    	private ServizioApplicativo servizioApplicativo;
    	private boolean letto;
    	
    	private ApplicativoIdentificato(IDServizioApplicativo id, ServizioApplicativo servizioApplicativo) {
    		this.id = id;
    		this.servizioApplicativo = servizioApplicativo;
    		this.letto = servizioApplicativo!=null;
    	}
    	
    	private IDServizioApplicativo getId() {
    		return this.id;
    	}
    	
    	private ServizioApplicativo getServizioApplicativo(DatiInvocazionePortaDelegata datiInvocazione) throws ProtocolException {
    		if(!this.letto) {
    			this.letto = true;
    			try {
    				this.servizioApplicativo = ConfigurazionePdDManager.getInstance(datiInvocazione.getState()).
    						getServizioApplicativo(this.id, datiInvocazione.getRequestInfo());
    			}catch(org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound notFound) {
    				// applicativo non presente in configurazione; non sara' possibile verificarne i ruoli
    			}catch(Exception e) {
    				throw new ProtocolException("Lettura dell'applicativo '"+this.id.getNome()+"' non riuscita: "+e.getMessage(),e);
    			}
    		}
    		return this.servizioApplicativo;
    	}
    }
	
}
