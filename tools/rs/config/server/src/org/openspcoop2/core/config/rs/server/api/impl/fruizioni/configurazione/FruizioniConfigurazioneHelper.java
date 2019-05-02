package org.openspcoop2.core.config.rs.server.api.impl.fruizioni.configurazione;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

public class FruizioniConfigurazioneHelper {

	public static void attivazionePolicyCheckData(
			TipoOperazione tipoOperazione, 
			PortaDelegata pd,
			AttivazionePolicy policy,
			InfoPolicy infoPolicy,  
			FruizioniConfEnv env ) throws Exception  {
		
		org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico = env.confCore.getConfigurazioneControlloTraffico();
		final RuoloPolicy ruoloPorta = RuoloPolicy.DELEGATA;
		final String nomePorta = pd.getNome();
		        
		
		// Controllo che l'azione scelta per il filtro sia supportata.
		boolean hasAzioni = pd.getAzione() != null && pd.getAzione().getAzioneDelegataList().size() > 0;
		List<String> azioniSupportate = hasAzioni 
				    ? pd.getAzione().getAzioneDelegataList()
					: env.confCore.getAzioni(
						env.asps,
						env.apcCore.getAccordoServizioSintetico(env.asps.getIdAccordo()), 
						false, 
						true, 
						ErogazioniApiHelper.getAzioniOccupateFruizione(env.idAsps, env.idSoggetto.toIDSoggetto(), env.apsCore, env.pdCore)
					);
		
		if ( policy.getFiltro().getAzione() != null && !azioniSupportate.contains(policy.getFiltro().getAzione()) ) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'azione " + policy.getFiltro().getAzione() + " non è assegnabile a una policy di rate limiting per il gruppo scelto, le azioni supportate sono: " + azioniSupportate.toString());
		}
		
		// Controllo che l'applicativo fruitore scelto per il filtro sia supportato.
		if ( policy.getFiltro().getServizioApplicativoFruitore() != null &&				
				!env.confCore.getServiziApplicativiFruitore(env.tipo_protocollo, null, env.idSoggetto.getTipo(), env.idSoggetto.getNome())
				.stream()
				.filter( id -> id.getNome().equals(policy.getFiltro().getServizioApplicativoFruitore()))
				.findAny().isPresent()
		) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il servizio applicativo fruitore " + policy.getFiltro().getServizioApplicativoFruitore() + " scelto non è assegnabile alla policy di rate limiting");
		}
		
		if (! env.confHelper.attivazionePolicyCheckData(tipoOperazione, configurazioneControlloTraffico, policy,infoPolicy, ruoloPorta, nomePorta) ) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
		}
	}
	
}
