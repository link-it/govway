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



package org.openspcoop2.protocol.modipa.authorization;

import java.util.List;

import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autorizzazione.pd.AbstractAutorizzazioneBase;
import org.openspcoop2.pdd.core.autorizzazione.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.autorizzazione.pd.EsitoAutorizzazionePortaDelegata;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.SignalHubUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;

/**
 * Interfaccia che definisce un processo di autorizzazione sui token
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
    	EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
    	
		PdDContext context = datiInvocazione.getPddContext();
    	
		// ottengo le proprieta del protocollo per avere gli applicativi/ruoli autorizzati
		List<ProtocolProperty> eServiceProperties = SignalHubUtils.obtainSignalHubProtocolProperty(context);
    	
		String allowedService = null;
		try {
			allowedService = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(eServiceProperties, ModICostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_SA_ID);
		}catch(Exception e) {
			if(getProtocolFactory().getLogger()!=null) {
				getProtocolFactory().getLogger().error("Configurazione SignalHub non fornita per il servizio indicato: "+e.getMessage(),e);
			}
			return buildConfigurazioneNonDisponibile("Configurazione SignalHub non fornita per il servizio indicato", e);
		}
		String allowedRole = null;
		try {
			allowedRole = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(eServiceProperties, ModICostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_ROLE_ID);
		}catch(Exception e) {
			if(getProtocolFactory().getLogger()!=null) {
				getProtocolFactory().getLogger().error("Configurazione SignalHub non fornita per il servizio indicato: "+e.getMessage(),e);
			}
			return buildConfigurazioneNonDisponibile("Configurazione SignalHub non fornita per il servizio indicato", e);
		}
			

		List<Ruolo> roles = List.of();
		
		// controllo se sono presenti ruoli nel servizio applicativo
		if (datiInvocazione.getServizioApplicativo() != null && 
			datiInvocazione.getServizioApplicativo().getInvocazionePorta() != null &&
			datiInvocazione.getServizioApplicativo().getInvocazionePorta().getRuoli() != null)
			roles = datiInvocazione.getServizioApplicativo().getInvocazionePorta().getRuoli().getRuoloList();
		
		// controllo se sono presenti ruoli autorizzati
		for (Ruolo role : roles) {
			if (role.getNome().equals(allowedRole)) {
				esito.setAutorizzato(true);
				return esito;
			}
		}
		
		// controllo se l'applicativo e' autorizzato
		if (datiInvocazione.getIdServizioApplicativo() == null) {
			esito.setAutorizzato(false);
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_DENY, ErroriIntegrazione.ERRORE_410_AUTENTICAZIONE_RICHIESTA.getErroreIntegrazione());
			/**esito.setDetails(ROLE_SERVICE_UNRECOGNIZED);*/
			
		} else if (!allowedService.equals(datiInvocazione.getIdServizioApplicativo().getNome())) {
			esito.setAutorizzato(false);
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_DENY, ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.getErrore404_AutorizzazioneFallitaServizioApplicativo(datiInvocazione.getIdServizioApplicativo().getNome()));
			esito.setDetails(ROLE_SERVICE_UNRECOGNIZED);
		} else {
			esito.setAutorizzato(true);
		}
		
		return esito;
    }
	
}
