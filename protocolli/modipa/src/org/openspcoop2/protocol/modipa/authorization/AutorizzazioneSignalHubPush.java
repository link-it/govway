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
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.SignalHubUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
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
	private static final String ROLE_SERVICE_UNRECOGNIZED = "Servizio applicativo o ruolo non autorizzati nella confiugrazione dell'erogazione";
	
	@Override
	public boolean saveAuthorizationResultInCache() {
		return false;
	}
	
    @Override
	public EsitoAutorizzazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione){    	
    	try {
    		return this.processEngine(datiInvocazione);
    	} catch (ProtocolException e) {
    		EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
    		esito.setAutorizzato(false);
    		esito.setDetails(GENERIC_ERROR);
    		esito.setEccezioneProcessamento(e);
    		return esito;
    	}
    }
    
    public EsitoAutorizzazionePortaDelegata processEngine(DatiInvocazionePortaDelegata datiInvocazione) throws ProtocolException {
    	EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
    	
		PdDContext context = datiInvocazione.getPddContext();
    	
		// ottengo le proprieta del protocollo per avere gli applicativi/ruoli autorizzati
		List<ProtocolProperty> eServiceProperties = SignalHubUtils.obtainSignalHubProtocolProperty(context);
    	
		String allowedService = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(eServiceProperties, ModICostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_SA_ID);
		String allowedRole = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(eServiceProperties, ModICostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_ROLE_ID);

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
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_DENY, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErroreIntegrazione());
			esito.setDetails(ROLE_SERVICE_UNRECOGNIZED);
			
		} else if (!allowedService.equals(datiInvocazione.getIdServizioApplicativo().getNome())) {
			esito.setAutorizzato(false);
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_DENY, ErroriIntegrazione.ERRORE_410_AUTENTICAZIONE_RICHIESTA.getErroreIntegrazione());
			esito.setDetails(ROLE_SERVICE_UNRECOGNIZED);
		} else {
			esito.setAutorizzato(true);
		}
		
		return esito;
    }
	
}
