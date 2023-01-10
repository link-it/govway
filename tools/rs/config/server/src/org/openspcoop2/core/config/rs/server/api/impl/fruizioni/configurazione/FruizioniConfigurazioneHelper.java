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


package org.openspcoop2.core.config.rs.server.api.impl.fruizioni.configurazione;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * FruizioniConfigurazioneHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FruizioniConfigurazioneHelper {

	public static void attivazionePolicyCheckData(
			TipoOperazione tipoOperazione, 
			PortaDelegata pd,
			AttivazionePolicy policy,
			InfoPolicy infoPolicy,  
			FruizioniConfEnv env,
			ServiceBinding serviceBinding,
			String modalita) throws Exception  {
		
		org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico = env.confCore.getConfigurazioneControlloTraffico();
		final RuoloPolicy ruoloPorta = RuoloPolicy.DELEGATA;
		final String nomePorta = pd.getNome();

		// Controllo che l'azione scelta per il filtro sia supportata.
		boolean hasAzioni = pd.getAzione() != null && pd.getAzione().getAzioneDelegataList().size() > 0;
		List<String> azioniSupportate = hasAzioni ? pd.getAzione().getAzioneDelegataList()
				: env.confCore.getAzioni(env.asps, env.apcCore.getAccordoServizioSintetico(env.asps.getIdAccordo()),
						false, true, ErogazioniApiHelper.getAzioniOccupateFruizione(env.idAsps,
								env.idSoggetto.toIDSoggetto(), env.apsCore, env.pdCore));

		if(policy.getFiltro().getAzione() != null && !policy.getFiltro().getAzione().isEmpty()) {
			String [] tmp = policy.getFiltro().getAzione().split(",");
			if(tmp!=null && tmp.length>0) {
				for (String azCheck : tmp) {
					if ( !azioniSupportate.contains(azCheck)) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'azione " + azCheck
								+ " non è assegnabile a una policy di rate limiting per il gruppo scelto, le azioni supportate sono: "
								+ azioniSupportate.toString());
					}
				}
			}
		}
		
		if(policy.getFiltro().getRuoloFruitore()!=null) {
			
			FiltroRicercaRuoli filtroRicercaRuoli = new FiltroRicercaRuoli();
			filtroRicercaRuoli.setTipologia(RuoloTipologia.INTERNO);
			List<IDRuolo> listIdRuoli = env.ruoliCore.getAllIdRuoli(filtroRicercaRuoli);
			List<String> ruoli = new ArrayList<>();
			if(listIdRuoli!=null && !listIdRuoli.isEmpty()) {
				for (IDRuolo idRuolo : listIdRuoli) {
					ruoli.add(idRuolo.getNome());
				}
			}
			
			if ( !ruoli.contains(policy.getFiltro().getRuoloFruitore())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il ruolo " + policy.getFiltro().getRuoloFruitore() + " non esiste.");
			}
		}

		// Controllo che l'applicativo fruitore scelto per il filtro sia supportato.
		if (policy.getFiltro().getServizioApplicativoFruitore() != null && !env.confCore
				.getServiziApplicativiFruitore(env.tipo_protocollo, null, env.idSoggetto.getTipo(),
						env.idSoggetto.getNome())
				.stream().filter(id -> id.getNome().equals(policy.getFiltro().getServizioApplicativoFruitore()))
				.findAny().isPresent()) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(
					"Il servizio applicativo fruitore " + policy.getFiltro().getServizioApplicativoFruitore()
							+ " scelto non è assegnabile alla policy di rate limiting");
		}
		
		if (! env.confHelper.attivazionePolicyCheckData(new StringBuilder(), tipoOperazione, configurazioneControlloTraffico, 
				policy,infoPolicy, ruoloPorta, nomePorta, serviceBinding, modalita) ) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
		}
	}

}
