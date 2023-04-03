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
package org.openspcoop2.pdd.services.core;

import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.slf4j.Logger;

/**
 * RicezioneBusteUtils
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneBusteUtils {

	/* Utility per la gestione del Message-Security e MTOM */
	/**
	 * Ritorna le Proprieta' Message-Security relative alla ricezione della busta
	 * 
	 * @return Proprieta' Message-Security relative alla ricezione della busta
	 */
	public FlowProperties getFlowPropertiesRequest(OpenSPCoop2Message requestMessage, Busta bustaRichiesta,
			ConfigurazionePdDManager configurazionePdDReader,StateMessage state,
			MsgDiagnostico msgDiag,Logger logCore,OpenSPCoop2Properties properties,
			RuoloBusta ruoloBustaRicevuta,String implementazionePdDMittente,
			RequestInfo requestInfo, PdDContext pddContext,
			PortaApplicativa paFind)throws DriverConfigurazioneException{

		// Proprieta' Message-Security relative alla ricezione della busta

		// Messaggi AD HOC (riscontro) responseFlow della porta delegata, utilizzo l'id del riscontro

		// RichiestaOneWay requestFlow della PortaApplicativa
		// RispostaOneWay (integrazione) responseFlow della porta delegata

		// RichiestaSincrona requestFlow della porta applicativa
		// RispostaSincrona su new Connection NON SUPPORTATA, cmq: (integrazione) responseFlow della porta delegata

		// RichiestaAsincronaSimmetrica requestFlow della PortaApplicativa
		// RicevutaRichiestaAsincronaSimmetrica (integrazione) responseFlow della porta delegata che ha effettuato la richiesta
		// RispostaAsincronaSimmetrica (integrazione) responseFlow della porta delegata che ha effettuato la richiesta
		// RicevutaRispostaAsincronaSimmetrica (integrazione) responseFlow della porta delegata che ha effettuato la risposta

		// RichiestaAsincronaAsimmetrica requestFlow della PortaApplicativa
		// RicevutaRichiestaAsincronaAsimmetrica (integrazione) responseFlow della porta delegata che ha effettuato la richiesta
		// RispostaAsincronaAsimmetrica (conversioneServizio) requestFlow della porta applicativa
		// RicevutaRispostaAsincronaAsimmetrica (integrazione) responseFlow della porta delegata che ha effettuato la risposta

		FlowProperties flowProperties = new FlowProperties();
		flowProperties.tipoMessaggio = RuoloMessaggio.RICHIESTA;
		
		ProfiloDiCollaborazione profiloCollaborazione = null;
		try{
			
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
						
			// Messaggi AD HOC senza profilo: RISCONTRO
			if(bustaRichiesta.getProfiloDiCollaborazione()==null && bustaRichiesta.sizeListaRiscontri()>0 &&
					properties.isGestioneRiscontri(implementazionePdDMittente)){
				RepositoryBuste repository = new RepositoryBuste(state, true,protocolFactory);
				Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiscontro(0).getID());
				if(integrazione.getNomePorta()!=null){
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(integrazione.getNomePorta());
					PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD, requestInfo);
					flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
					flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
				}

			}

			// Profilo OneWay e Sincrono
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) || 
					org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())
			) {	
				// Richiesta
				if(bustaRichiesta.getRiferimentoMessaggio()==null){
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = buildIdServizio(bustaRichiesta);
						// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						if(idServizioPA!=null) {
							idServizioPA.setAzione(bustaRichiesta.getAzione());
							pa = this.getPortaApplicativaIgnoreNotFound(configurazionePdDReader, idServizioPA);
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForReceiver(pa, logCore, requestMessage, bustaRichiesta, requestInfo, pddContext);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);
					}
				}
				// Risposta
				else{
					PortaDelegata pd = null;
					if(state!=null) {
						RepositoryBuste repository = new RepositoryBuste(state, false,protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(integrazione.getNomePorta());
						pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD, requestInfo);
					}
					flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
					flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
					flowProperties.tipoMessaggio = RuoloMessaggio.RISPOSTA;
				}
			}

			// Profilo Asincrono Simmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				//	Richiesta Asincrona
				if(bustaRichiesta.getRiferimentoMessaggio()==null){

					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = buildIdServizio(bustaRichiesta);
						// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						if(idServizioPA!=null) {
							idServizioPA.setAzione(bustaRichiesta.getAzione());
							pa = this.getPortaApplicativaIgnoreNotFound(configurazionePdDReader, idServizioPA);
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForReceiver(pa, logCore, requestMessage, bustaRichiesta, requestInfo, pddContext);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);
					}

				}else{

					//	Risposta Asincrona
					if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, false,protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(integrazione.getNomePorta());
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD, requestInfo);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForSender(pd, logCore, requestMessage, bustaRichiesta, requestInfo, pddContext);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForSender(pd);

					}
					//	Ricevuta alla richiesta.
					else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, true, protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(integrazione.getNomePorta());
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD, requestInfo);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
						flowProperties.tipoMessaggio = RuoloMessaggio.RISPOSTA;

					}
					//	Ricevuta alla risposta.
					else if(RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, false, protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(integrazione.getNomePorta());
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD, requestInfo);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
						flowProperties.tipoMessaggio = RuoloMessaggio.RISPOSTA;

					}

				}

			}

			// Profilo Asincrono Asimmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				//	Richiesta Asincrona
				if(bustaRichiesta.getRiferimentoMessaggio()==null){

					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = buildIdServizio(bustaRichiesta);
						// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						if(idServizioPA!=null) {
							idServizioPA.setAzione(bustaRichiesta.getAzione());
							pa = this.getPortaApplicativaIgnoreNotFound(configurazionePdDReader, idServizioPA);
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForReceiver(pa, logCore, requestMessage, bustaRichiesta, requestInfo, pddContext);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);
					}

				}else{

					profiloCollaborazione = new ProfiloDiCollaborazione(state,protocolFactory);

					//	Risposta Asincrona
					if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						// ConversioneServizio.
						IDServizio idServizioOriginale = profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta(bustaRichiesta.getRiferimentoMessaggio());
						PortaApplicativa pa = paFind;
						if(pa==null){
							IDServizio idServizioPA = buildIdServizio(bustaRichiesta);
							// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
							if(idServizioPA!=null) {
								idServizioPA.setAzione(idServizioOriginale.getAzione());
								pa = this.getPortaApplicativaIgnoreNotFound(configurazionePdDReader,idServizioPA);
							}
						}
						if(pa!=null) {
							flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForReceiver(pa, logCore, requestMessage, bustaRichiesta, requestInfo, pddContext);
							flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);
						}

					}
					//	Ricevuta alla richiesta.
					else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, true,protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(integrazione.getNomePorta());
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD, requestInfo);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
						flowProperties.tipoMessaggio = RuoloMessaggio.RISPOSTA;

					}
					//	Ricevuta alla risposta.
					else if(RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, false,protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(integrazione.getNomePorta());
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD, requestInfo);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
						flowProperties.tipoMessaggio = RuoloMessaggio.RISPOSTA;

					}

				}

			}

		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "lettura_MessageSecurity_MTOM_RequestProperties");
			logCore.error("Lettura dati Message-Security / MTOM per la ricezione del messaggio non riuscita",e);
		}finally{
			if(profiloCollaborazione!=null)
				state.closePreparedStatement();
		}

		return flowProperties;
	}


	/**
	 * Ritorna le Proprieta' Message-Security relative alla spedizione della busta
	 * 
	 * @return Proprieta' Message-Security relative alla spedizione della busta
	 */
	public FlowProperties getFlowPropertiesResponse(OpenSPCoop2Message requestMessage, Busta bustaRichiesta,
			ConfigurazionePdDManager configurazionePdDReader,StateMessage state,
			MsgDiagnostico msgDiag,Logger logCore,OpenSPCoop2Properties properties,
			RuoloBusta ruoloBustaRicevuta,String implementazionePdDMittente,
			RequestInfo requestInfo, PdDContext pddContext,
			PortaApplicativa paFind)throws DriverConfigurazioneException{

		//	Proprieta' Message-Security relative alla spedizione della busta

		// Messaggi AD HOC senza profilo (riscontro) responseFlow della porta applicativa

		// RispostaOneWay responseFlow della porta applicativa

		// RispostaSincrona responseFlow della porta applicativa

		// RicevutaRichiestaAsincronaSimmetrica responseFlow della porta applicativa
		// RicevutaRispostaAsincronaSimmetrica (integrazione) requestFlow della porta delegata che ha effettuato la richiesta

		// RicevutaRichiestaAsincronaAsimmetrica responseFlow della porta applicativa
		// RicevutaRispostaAsincronaAsimmetrica (conversioneServizio) responseFlow della porta applicativa

		FlowProperties flowProperties = new FlowProperties();
		flowProperties.tipoMessaggio = RuoloMessaggio.RISPOSTA;

		// NOTA: La busta che sto gestendo e' la busta che ho ricevuto, non quella che sto inviando!!

		ProfiloDiCollaborazione profiloCollaborazione = null;
		try{

			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
						
			// Messaggi AD HOC senza profilo: RISCONTRO
			if(bustaRichiesta.getProfiloDiCollaborazione()==null && bustaRichiesta.sizeListaRiscontri()>0 &&
					properties.isGestioneRiscontri(implementazionePdDMittente)){
				if(bustaRichiesta.getTipoServizio()!=null &&
						bustaRichiesta.getServizio()!=null){
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = buildIdServizio(bustaRichiesta);
						// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						if(idServizioPA!=null) {
							idServizioPA.setAzione(bustaRichiesta.getAzione());
							pa = this.getPortaApplicativaIgnoreNotFound(configurazionePdDReader, idServizioPA);
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
					}
				}
			}
			// Messaggi con profilo
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) || 
					org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())
			) {	
				PortaApplicativa pa = paFind;
				if(pa==null){
					IDServizio idServizioPA = buildIdServizio(bustaRichiesta);
					// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
					if(idServizioPA!=null) {
						idServizioPA.setAzione(bustaRichiesta.getAzione());
						pa = this.getPortaApplicativaIgnoreNotFound(configurazionePdDReader, idServizioPA);
					}
				}
				if(pa!=null) {
					flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
					flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
				}
			}

			//	 Profilo Asincrono Simmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				//	Ricevuta alla richiesta.
				if(bustaRichiesta.getRiferimentoMessaggio()==null){

					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = buildIdServizio(bustaRichiesta);
						// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						if(idServizioPA!=null) {
							idServizioPA.setAzione(bustaRichiesta.getAzione());
							pa = this.getPortaApplicativaIgnoreNotFound(configurazionePdDReader, idServizioPA);
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
					}

				}

				//	Ricevuta alla risposta.
				else if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

					RepositoryBuste repository = new RepositoryBuste(state, false, protocolFactory);
					Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(integrazione.getNomePorta());
					PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD, requestInfo);
					flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForSender(pd, logCore, requestMessage, bustaRichiesta, requestInfo, pddContext);
					flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForSender(pd);

				}

			}

			//	Profilo Asincrono Asimmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				profiloCollaborazione = new ProfiloDiCollaborazione(state, protocolFactory);

				//	Ricevuta alla richiesta.
				if(bustaRichiesta.getRiferimentoMessaggio()==null){

					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = buildIdServizio(bustaRichiesta);
						// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						if(idServizioPA!=null) {
							idServizioPA.setAzione(bustaRichiesta.getAzione());
							pa = this.getPortaApplicativaIgnoreNotFound(configurazionePdDReader, idServizioPA);
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
					}

				}

				//	Ricevuta alla risposta.
				else if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

					// ConversioneServizio.
					IDServizio idServizioOriginale = profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta(bustaRichiesta.getRiferimentoMessaggio());
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = buildIdServizio(bustaRichiesta);
						// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						if(idServizioPA!=null) {
							idServizioPA.setAzione(idServizioOriginale.getAzione());
							pa = this.getPortaApplicativaIgnoreNotFound(configurazionePdDReader, idServizioPA);
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
					}

				}

			}



		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "lettura_MessageSecurity_MTOM_ResponseProperties");
			logCore.error("Lettura dati Message-Security / MTOM per la spedizione del messaggio di risposta non riuscita",e);
		}finally{
			if(profiloCollaborazione!=null)
				state.closePreparedStatement();
		}

		return flowProperties;
	}

	public PortaApplicativa getPortaApplicativa(ConfigurazionePdDManager configurazionePdDReader, IDServizio idServizio) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<PortaApplicativa> listPa = configurazionePdDReader.getPorteApplicative(idServizio, false);
		if(listPa.isEmpty()){
			throw new DriverConfigurazioneException("Non esiste alcuna porta applicativa indirizzabile tramite il servizio ["+idServizio+"]");
		}
		else{
			if(listPa.size()>1)
				throw new DriverConfigurazioneException("Esiste più di una porta applicativa indirizzabile tramite il servizio ["+idServizio+"]");
			return listPa.get(0);
		}
	}
	public PortaApplicativa getPortaApplicativaIgnoreNotFound(ConfigurazionePdDManager configurazionePdDReader, IDServizio idServizio) throws Exception{
		try {
			return this.getPortaApplicativa(configurazionePdDReader, idServizio);
		}catch(DriverConfigurazioneNotFound notFound) {
			// ignore
		}
		return null;
	}
	
	private IDServizio buildIdServizio(Busta bustaRichiesta) {
		IDServizio idServizioPA = null;
		try {
			idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(), 
					bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(), 
					bustaRichiesta.getVersioneServizio());
		}catch(Exception e) {
			// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
		}
		return idServizioPA;
	}
}
