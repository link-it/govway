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

package org.openspcoop2.pdd.logger.traccia;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeTokenClient;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.service.beans.Diagnostico;
import org.openspcoop2.utils.service.beans.DiagnosticoSeveritaEnum;
import org.openspcoop2.utils.service.beans.HttpMethodEnum;
import org.openspcoop2.utils.service.beans.ProfiloCollaborazioneEnum;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.Transazione;
import org.openspcoop2.utils.service.beans.TransazioneBase;
import org.openspcoop2.utils.service.beans.TransazioneContenutoMessaggio;
import org.openspcoop2.utils.service.beans.TransazioneContenutoMessaggioHeader;
import org.openspcoop2.utils.service.beans.TransazioneContestoEnum;
import org.openspcoop2.utils.service.beans.TransazioneDettaglioMessaggio;
import org.openspcoop2.utils.service.beans.TransazioneDettaglioRichiesta;
import org.openspcoop2.utils.service.beans.TransazioneDettaglioRisposta;
import org.openspcoop2.utils.service.beans.TransazioneEsito;
import org.openspcoop2.utils.service.beans.TransazioneExt;
import org.openspcoop2.utils.service.beans.TransazioneExtContenutoMessaggio;
import org.openspcoop2.utils.service.beans.TransazioneExtContenutoMessaggioAllegato;
import org.openspcoop2.utils.service.beans.TransazioneExtContenutoMessaggioBody;
import org.openspcoop2.utils.service.beans.TransazioneExtContenutoMessaggioBodyMultipart;
import org.openspcoop2.utils.service.beans.TransazioneExtContenutoMessaggioPorzioneBody;
import org.openspcoop2.utils.service.beans.TransazioneExtDettaglioRichiesta;
import org.openspcoop2.utils.service.beans.TransazioneExtDettaglioRisposta;
import org.openspcoop2.utils.service.beans.TransazioneExtInformazioniApi;
import org.openspcoop2.utils.service.beans.TransazioneExtInformazioniMittente;
import org.openspcoop2.utils.service.beans.TransazioneExtInformazioniMittenteApplicativoToken;
import org.openspcoop2.utils.service.beans.TransazioneExtInformazioniSoggetto;
import org.openspcoop2.utils.service.beans.TransazioneExtInformazioniToken;
import org.openspcoop2.utils.service.beans.TransazioneInformazioniApi;
import org.openspcoop2.utils.service.beans.TransazioneInformazioniMittente;
import org.openspcoop2.utils.service.beans.TransazioneMessaggioFormatoEnum;
import org.openspcoop2.utils.service.beans.TransazioneRuoloEnum;
import org.slf4j.Logger;

/**	
 * ConverterUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Converter {

	private Logger log;
	private boolean throwInitProtocol= true;
	
	// configurazione
	private boolean emittente = true;
	private boolean dataEmissione = true;
	private boolean id = true;
	private boolean idConversazione = true;
	private boolean ruolo = true;
	private boolean esito = true;
	private boolean messaggi_id = true;
	private boolean messaggi_idApplicativo = true;
	private boolean messaggi_data = true;
	private boolean messaggi_contenuti = true;
	private boolean richiesta_tipo = true;
	private boolean richiesta_urlInvocazione = true;
	private boolean risposta_esito = true;
	private boolean risposta_fault = true;
	private boolean risposta_errori = true;
	private boolean api_erogatore = true;
	private boolean api_nome = true;
	private boolean api_versione = true;
	private boolean api_operazione = true;
	private boolean mittente_fruitore = true;
	private boolean mittente_principal = true;
	private boolean mittente_utente = true;
	private boolean mittente_client = true;
	private boolean mittente_indirizzoClient = true;
	private boolean mittente_indirizzoClientInoltrato = true;
	
	// configurazione ext
	private boolean profilo = true;
	private boolean contesto = true;
	private boolean idCluster = true;
	private boolean emittenteExtraInfo = true;
	private boolean stato = true;
	private boolean messaggi_dimensione = true;
	private boolean messaggi_dataAccettazione = true;
	private boolean messaggi_duplicati = true;
	private boolean messaggi_tracciaProtocollo = true;
	private boolean richiesta_connettore = true;
	private boolean diagnostici = true;
	private boolean api_erogatoreExtraInfo = true;
	private boolean api_tags = true;
	private boolean api_tipo = true;
	private boolean api_idAsincrono = true;
	private boolean api_profiloCollaborazione = true;
	private boolean mittente_fruitoreExtraInfo = true;
	private boolean mittente_applicativo = true;
	private boolean mittente_credenziali = true;
	private boolean mittente_token = true;
	private boolean mittente_tokenClaims = true;
	private boolean mittente_applicativoToken = true;
	
	
	
	public Converter(Logger log) {
		this.log = log;
	} 
	public Converter(Logger log, Properties pConf) throws TracciaException {
		this.log = log;
		
		try {
			Field [] fields = Converter.class.getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				fieldName = fieldName.replace("_", ".");
				if(pConf.containsKey(fieldName)) {
					String value = pConf.getProperty(fieldName);
					String bCN = boolean.class.getName()+"";
					if(bCN.equals(field.getType().getName())) {
						field.set(this, "true".equalsIgnoreCase(value));
					}else {
						field.set(this, value);
					}
				}
			}
		}catch(Exception e) {
			throw new TracciaException(e.getMessage(),e);
		}
	} 
	
	
	
	
	public Transazione toTransazione(org.openspcoop2.core.transazioni.Transazione transazioneDB, 
			CredenzialiMittente credenzialiMittente, List<MsgDiagnostico> messaggiDiagnostici) throws Exception {
		return  (Transazione) this._toTransazione(transazioneDB, credenzialiMittente, null, null, messaggiDiagnostici, false);
	}
	
	public TransazioneExt toTransazioneExt(org.openspcoop2.core.transazioni.Transazione transazioneDB, 
			CredenzialiMittente credenzialiMittente, Traccia tracciaRichiesta, Traccia tracciaRisposta, List<MsgDiagnostico> messaggiDiagnostici) throws Exception {
		return  (TransazioneExt) this._toTransazione(transazioneDB, credenzialiMittente, tracciaRichiesta, tracciaRisposta, messaggiDiagnostici, true);
	}
	
	private TransazioneBase _toTransazione(org.openspcoop2.core.transazioni.Transazione transazioneDB, 
			CredenzialiMittente credenzialiMittente, Traccia tracciaRichiesta, Traccia tracciaRisposta, List<MsgDiagnostico> messaggiDiagnostici,
			boolean extended) throws Exception {

		List<String> errori = new ArrayList<>();
		if((!extended || !this.diagnostici) && messaggiDiagnostici!=null && messaggiDiagnostici.size()>0) {
			for (MsgDiagnostico diag : messaggiDiagnostici) {
				if(diag.getSeverita() <= LogLevels.SEVERITA_ERROR_INTEGRATION) {
					errori.add(diag.getMessaggio());
				}
			}
		}
		
		TransazioneBase transazione = null;
		if(extended) {
			transazione = new TransazioneExt();
		}
		else {
			transazione = new Transazione();
		}
		
		if(this.id) {
			transazione.setIdTraccia(java.util.UUID.fromString(transazioneDB.getIdTransazione()));
		}
		if(this.dataEmissione) {
			transazione.setDataEmissione(new DateTime(DateManager.getTimeMillis()));
		}
		if(this.idConversazione) {
			transazione.setIdConversazione(transazioneDB.getIdCollaborazione());
		}
		
		if(this.esito) {
			TransazioneEsito esito = new TransazioneEsito();
			esito.setCodice(transazioneDB.getEsito()+"");
			try {
				EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(this.log, transazioneDB.getProtocollo());
				esito.setDescrizione(esitiProperties.getEsitoLabel(transazioneDB.getEsito()));
			}catch(Throwable e) {
				if(this.throwInitProtocol) {
					throw e;
				}
				else {
					esito.setDescrizione("ok");
				}
			}
			transazione.setEsito(esito);
		}
		
		TransazioneRuoloEnum ruoloTraccia = null;
		if(PddRuolo.DELEGATA.equals(transazioneDB.getPddRuolo())){
			ruoloTraccia = TransazioneRuoloEnum.FRUIZIONE;
		}
		else {
			ruoloTraccia = TransazioneRuoloEnum.EROGAZIONE;
		}
		
		IProtocolFactory<?> protocolFactory = null;
		ITracciaSerializer tracciaSerializer = null;
		if(extended) {
			try {
				protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(transazioneDB.getProtocollo());
				if(protocolFactory.createProtocolConfiguration().isAbilitataGenerazioneTracce()) {
					tracciaSerializer = protocolFactory.createTracciaSerializer();
				}
			}catch(Throwable e) {
				if(this.throwInitProtocol) {
					throw e;
				}
			}
			if(this.ruolo) {
				((TransazioneExt)transazione).setRuolo(ruoloTraccia);
			}
			if(this.emittente) {
				if(transazioneDB.getPddNomeSoggetto()!=null) { // liste con dati dell'indice e basta, non c'è questo campo
					((TransazioneExt)transazione).setEmittente(transazioneDB.getPddNomeSoggetto());
				}
				else if(PddRuolo.APPLICATIVA.equals(transazioneDB.getPddRuolo()) && transazioneDB.getNomeSoggettoErogatore()!=null) {
					((TransazioneExt)transazione).setEmittente(transazioneDB.getNomeSoggettoErogatore());
				}
				else if(PddRuolo.DELEGATA.equals(transazioneDB.getPddRuolo()) && transazioneDB.getNomeSoggettoFruitore()!=null) {
					((TransazioneExt)transazione).setEmittente(transazioneDB.getNomeSoggettoFruitore());
				}
				else {
					((TransazioneExt)transazione).setEmittente(transazioneDB.getPddCodice());
				}
			}
			if(this.profilo) {
				ProfiloEnum profilo = ProfiloEnum.APIGATEWAY;
				if(transazioneDB.getProtocollo().equals(CostantiLabel.MODIPA_PROTOCOL_NAME)) {
					profilo = ProfiloEnum.MODIPA;
				}
				else if(transazioneDB.getProtocollo().equals(CostantiLabel.SPCOOP_PROTOCOL_NAME)) {
					profilo = ProfiloEnum.SPCOOP;
				}
				else if(transazioneDB.getProtocollo().equals(CostantiLabel.SDI_PROTOCOL_NAME)) {
					profilo = ProfiloEnum.FATTURAPA;
				}
				else if(transazioneDB.getProtocollo().equals(CostantiLabel.AS4_PROTOCOL_NAME)) {
					profilo = ProfiloEnum.EDELIVERY;
				}
				((TransazioneExt)transazione).setProfilo(profilo);
			}
			if(this.contesto) {
				TransazioneContestoEnum contesto = TransazioneContestoEnum.STANDARD;
				if(transazioneDB.getEsitoContesto()!=null && !TransazioneContestoEnum.STANDARD.name().equalsIgnoreCase(transazioneDB.getEsitoContesto())) {
					contesto = TransazioneContestoEnum.SONDA;
				}
				((TransazioneExt)transazione).setContesto(contesto);
			}
			if(this.idCluster) {
				((TransazioneExt)transazione).setIdCluster(transazioneDB.getClusterId());
			}
			if(this.emittenteExtraInfo) {
				String tipoEmittente = transazioneDB.getPddTipoSoggetto(); // liste con dati dell'indice e basta, non c'è questo campo
				if(tipoEmittente==null) {
					if(PddRuolo.APPLICATIVA.equals(transazioneDB.getPddRuolo()) && transazioneDB.getTipoSoggettoErogatore()!=null) {
						tipoEmittente = transazioneDB.getTipoSoggettoErogatore();
					}
					else if(PddRuolo.DELEGATA.equals(transazioneDB.getPddRuolo()) && transazioneDB.getTipoSoggettoFruitore()!=null) {
						tipoEmittente = transazioneDB.getTipoSoggettoFruitore();
					}
					else {
						tipoEmittente = protocolFactory.createProtocolConfiguration().getTipoSoggettoDefault();
					}
				}
				((TransazioneExt)transazione).setInformazioniEmittente(_newTransazioneSoggetto(tipoEmittente, transazioneDB.getPddCodice(), null));
			}
			if(this.stato) {
				((TransazioneExt)transazione).setStato(transazioneDB.getStato());
			}
		}
		else {
			if(this.ruolo) {
				((Transazione)transazione).setRuolo(ruoloTraccia);
			}
			if(this.emittente) {
				((Transazione)transazione).setEmittente(transazioneDB.getPddNomeSoggetto());
			}
		}

		TransazioneDettaglioMessaggio richiesta = null;
		if(extended) {
			richiesta = new TransazioneExtDettaglioRichiesta();
		}
		else {
			richiesta = new TransazioneDettaglioRichiesta();
		}
		if(extended) {
			if(this.messaggi_dimensione) {
				((TransazioneExtDettaglioRichiesta)richiesta).setBytesIngresso(transazioneDB.getRichiestaIngressoBytes());
				((TransazioneExtDettaglioRichiesta)richiesta).setBytesUscita(transazioneDB.getRichiestaUscitaBytes());
			}
			if(this.messaggi_dataAccettazione && transazioneDB.getDataAccettazioneRichiesta()!=null) {
				((TransazioneExtDettaglioRichiesta)richiesta).setDataAccettazione(new DateTime(transazioneDB.getDataAccettazioneRichiesta().getTime()));
			}
		}
		if(this.messaggi_id) {
			richiesta.setId(transazioneDB.getIdMessaggioRichiesta());
		}
		if(this.messaggi_idApplicativo) {
			richiesta.setIdApplicativo(transazioneDB.getIdCorrelazioneApplicativa());
		}
		if(extended) {
			if(this.messaggi_data) {
				if(transazioneDB.getDataIngressoRichiesta()!=null) {
					((TransazioneExtDettaglioRichiesta)richiesta).setDataRicezione(new DateTime(transazioneDB.getDataIngressoRichiesta().getTime()));
				}
				if(transazioneDB.getDataIngressoRichiestaStream()!=null) {
					((TransazioneExtDettaglioRichiesta)richiesta).setDataRicezioneAcquisita(new DateTime(transazioneDB.getDataIngressoRichiestaStream().getTime()));
				}
				if(transazioneDB.getDataUscitaRichiesta()!=null) {
					((TransazioneExtDettaglioRichiesta)richiesta).setDataConsegna(new DateTime(transazioneDB.getDataUscitaRichiesta().getTime()));
				}
				if(transazioneDB.getDataUscitaRichiestaStream()!=null) {
					((TransazioneExtDettaglioRichiesta)richiesta).setDataConsegnaEffettuata(new DateTime(transazioneDB.getDataUscitaRichiestaStream().getTime()));
				}
			}
			if(this.richiesta_tipo && transazioneDB.getTipoRichiesta()!=null) {
				HttpMethodEnum methodEnum = HttpMethodEnum.fromValue(transazioneDB.getTipoRichiesta().toUpperCase());
				((TransazioneExtDettaglioRichiesta)richiesta).setTipo(methodEnum);
			}
			if(this.richiesta_urlInvocazione) {
				((TransazioneExtDettaglioRichiesta)richiesta).setUrlInvocazione(normalizeUrl(transazioneDB.getUrlInvocazione()));
			}
			if(this.richiesta_connettore) {
				((TransazioneExtDettaglioRichiesta)richiesta).setConnettore(normalizeUrl(transazioneDB.getLocationConnettore()));
			}
			if(this.messaggi_duplicati) {
				((TransazioneExtDettaglioRichiesta)richiesta).setDuplicatiMessaggio(transazioneDB.getDuplicatiRichiesta());
			}
			if(this.messaggi_tracciaProtocollo && tracciaSerializer!=null && tracciaRichiesta!=null) {
				((TransazioneExtDettaglioRichiesta)richiesta).setTraccia(tracciaSerializer.toString(tracciaRichiesta, TipoSerializzazione.DEFAULT));
			}
			if(this.messaggi_contenuti) {
				((TransazioneExtDettaglioRichiesta)richiesta).setContenutiIngresso(
						(TransazioneExtContenutoMessaggio) newTransazioneContenutoMessaggio(transazioneDB, 
								TipoMessaggio.RICHIESTA_INGRESSO, TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO ,true));
				((TransazioneExtDettaglioRichiesta)richiesta).setContenutiUscita(
						(TransazioneExtContenutoMessaggio) newTransazioneContenutoMessaggio(transazioneDB, 
								TipoMessaggio.RICHIESTA_USCITA, TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO ,true));
			}
			((TransazioneExt)transazione).setRichiesta((TransazioneExtDettaglioRichiesta) richiesta);
		}
		else {
			if(this.messaggi_data) {
				if(transazioneDB.getDataIngressoRichiesta()!=null) {
					((TransazioneDettaglioRichiesta)richiesta).setDataRicezione(new DateTime(transazioneDB.getDataIngressoRichiesta().getTime()));
				}
				if(transazioneDB.getDataUscitaRichiesta()!=null) {
					((TransazioneDettaglioRichiesta)richiesta).setDataConsegna(new DateTime(transazioneDB.getDataUscitaRichiesta().getTime()));
				}
			}
			if(this.richiesta_tipo && transazioneDB.getTipoRichiesta()!=null) {
				HttpMethodEnum methodEnum = HttpMethodEnum.fromValue(transazioneDB.getTipoRichiesta().toUpperCase());
				((TransazioneDettaglioRichiesta)richiesta).setTipo(methodEnum);
			}
			if(this.richiesta_urlInvocazione) {
				if(TransazioneRuoloEnum.FRUIZIONE.equals(ruoloTraccia)){
					((TransazioneDettaglioRichiesta)richiesta).setUrlInvocazione(normalizeUrl(transazioneDB.getLocationConnettore()));
				}
				else {
					((TransazioneDettaglioRichiesta)richiesta).setUrlInvocazione(normalizeUrl(transazioneDB.getUrlInvocazione()));
				}
			}
			if(this.messaggi_contenuti) {
				if(TransazioneRuoloEnum.FRUIZIONE.equals(ruoloTraccia)){
					((TransazioneDettaglioRichiesta)richiesta).setContenuti(
							(TransazioneContenutoMessaggio) newTransazioneContenutoMessaggio(transazioneDB, 
									TipoMessaggio.RICHIESTA_USCITA, TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO ,false));
				}
				else {
					((TransazioneDettaglioRichiesta)richiesta).setContenuti(
							(TransazioneContenutoMessaggio) newTransazioneContenutoMessaggio(transazioneDB, 
									TipoMessaggio.RICHIESTA_INGRESSO, TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO ,false));
				}
			}
			((Transazione)transazione).setRichiesta((TransazioneDettaglioRichiesta) richiesta);
		}
		
		TransazioneDettaglioMessaggio risposta = null;
		if(extended) {
			risposta = new TransazioneExtDettaglioRisposta();
		}
		else {
			risposta = new TransazioneDettaglioRisposta();
		}
		if(extended) {
			if(this.messaggi_dimensione) {
				((TransazioneExtDettaglioRisposta)risposta).setBytesIngresso(transazioneDB.getRispostaIngressoBytes());
				((TransazioneExtDettaglioRisposta)risposta).setBytesUscita(transazioneDB.getRispostaUscitaBytes());
			}
			if(this.messaggi_dataAccettazione && transazioneDB.getDataAccettazioneRisposta()!=null) {
				((TransazioneExtDettaglioRisposta)risposta).setDataAccettazione(new DateTime(transazioneDB.getDataAccettazioneRisposta().getTime()));
			}
		}
		if(this.messaggi_id) {
			risposta.setId(transazioneDB.getIdMessaggioRisposta());
		}
		if(this.messaggi_idApplicativo) {
			risposta.setIdApplicativo(transazioneDB.getIdCorrelazioneApplicativaRisposta());
		}
		if(extended) {
			if(this.messaggi_data) {
				if(transazioneDB.getDataIngressoRisposta()!=null) {
					((TransazioneExtDettaglioRisposta)risposta).setDataRicezione(new DateTime(transazioneDB.getDataIngressoRisposta().getTime()));
				}
				if(transazioneDB.getDataIngressoRispostaStream()!=null) {
					((TransazioneExtDettaglioRisposta)risposta).setDataRicezioneAcquisita(new DateTime(transazioneDB.getDataIngressoRispostaStream().getTime()));
				}
				// inverto
				if(transazioneDB.getDataUscitaRisposta()!=null) {
					((TransazioneExtDettaglioRisposta)risposta).setDataConsegnaEffettuata(new DateTime(transazioneDB.getDataUscitaRisposta().getTime()));
				}
				if(transazioneDB.getDataUscitaRispostaStream()!=null) {
					((TransazioneExtDettaglioRisposta)risposta).setDataConsegna(new DateTime(transazioneDB.getDataUscitaRispostaStream().getTime()));
				}
			}
			if(this.risposta_esito) {
				((TransazioneExtDettaglioRisposta)risposta).setEsitoConsegna(transazioneDB.getCodiceRispostaUscita());
				((TransazioneExtDettaglioRisposta)risposta).setEsitoRicezione(transazioneDB.getCodiceRispostaIngresso());
			}
			if(this.messaggi_duplicati) {
				((TransazioneExtDettaglioRisposta)risposta).setDuplicatiMessaggio(transazioneDB.getDuplicatiRisposta());
			}
			if(this.messaggi_tracciaProtocollo && tracciaSerializer!=null && tracciaRisposta!=null) {
				((TransazioneExtDettaglioRisposta)risposta).setTraccia(tracciaSerializer.toString(tracciaRisposta, TipoSerializzazione.DEFAULT));
			}
			if(this.messaggi_contenuti) {
				((TransazioneExtDettaglioRisposta)risposta).setContenutiIngresso(
						(TransazioneExtContenutoMessaggio) newTransazioneContenutoMessaggio(transazioneDB, 
								TipoMessaggio.RISPOSTA_INGRESSO, TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO ,true));
				((TransazioneExtDettaglioRisposta)risposta).setContenutiUscita(
						(TransazioneExtContenutoMessaggio) newTransazioneContenutoMessaggio(transazioneDB, 
								TipoMessaggio.RISPOSTA_USCITA, TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO ,true));
			}
			if(this.risposta_fault) {
				if(TransazioneRuoloEnum.FRUIZIONE.equals(ruoloTraccia)){
					if(transazioneDB.getFaultCooperazione()!=null) {
						((TransazioneExtDettaglioRisposta)risposta).setFaultRicezione(transazioneDB.getFaultCooperazione().getBytes());
						((TransazioneExtDettaglioRisposta)risposta).setFaultRicezioneFormato(_convert(transazioneDB.getFormatoFaultCooperazione()));
					}
					if(transazioneDB.getFaultIntegrazione()!=null) {
						((TransazioneExtDettaglioRisposta)risposta).setFaultConsegna(transazioneDB.getFaultIntegrazione().getBytes());
						((TransazioneExtDettaglioRisposta)risposta).setFaultConsegnaFormato(_convert(transazioneDB.getFormatoFaultIntegrazione()));
					}
				}
				else {
					if(transazioneDB.getFaultCooperazione()!=null) {
						((TransazioneExtDettaglioRisposta)risposta).setFaultConsegna(transazioneDB.getFaultCooperazione().getBytes());
						((TransazioneExtDettaglioRisposta)risposta).setFaultConsegnaFormato(_convert(transazioneDB.getFormatoFaultCooperazione()));
					}
					if(transazioneDB.getFaultIntegrazione()!=null) {
						((TransazioneExtDettaglioRisposta)risposta).setFaultRicezione(transazioneDB.getFaultIntegrazione().getBytes());
						((TransazioneExtDettaglioRisposta)risposta).setFaultRicezioneFormato(_convert(transazioneDB.getFormatoFaultIntegrazione()));
					}
				}
			}
			if(this.risposta_errori && !errori.isEmpty()) {
				((TransazioneExtDettaglioRisposta)risposta).setDettagliErrore(new ArrayList<>());
				for (String errore : errori) {
					((TransazioneExtDettaglioRisposta)risposta).getDettagliErrore().add(errore);
				}
			}
			((TransazioneExt)transazione).setRisposta(((TransazioneExtDettaglioRisposta)risposta));
		}
		else {
			if(this.messaggi_data) {
				if(transazioneDB.getDataIngressoRisposta()!=null) {
					((TransazioneDettaglioRisposta)risposta).setDataRicezione(new DateTime(transazioneDB.getDataIngressoRisposta().getTime()));
				}
				if(transazioneDB.getDataUscitaRisposta()!=null) {
					((TransazioneDettaglioRisposta)risposta).setDataConsegna(new DateTime(transazioneDB.getDataUscitaRisposta().getTime()));
				}
			}
			if(this.risposta_esito) {
				((TransazioneDettaglioRisposta)risposta).setEsitoConsegna(transazioneDB.getCodiceRispostaUscita());
				((TransazioneDettaglioRisposta)risposta).setEsitoRicezione(transazioneDB.getCodiceRispostaIngresso());
			}
			if(this.messaggi_contenuti) {
				if(TransazioneRuoloEnum.FRUIZIONE.equals(ruoloTraccia)){
					((TransazioneDettaglioRisposta)risposta).setContenuti(
							(TransazioneContenutoMessaggio) newTransazioneContenutoMessaggio(transazioneDB, 
									TipoMessaggio.RISPOSTA_INGRESSO, TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO ,false));
				}
				else {
					((TransazioneDettaglioRisposta)risposta).setContenuti(
							(TransazioneContenutoMessaggio) newTransazioneContenutoMessaggio(transazioneDB, 
									TipoMessaggio.RISPOSTA_USCITA, TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO ,false));
				}
			}
			if(this.risposta_fault) {
				if(TransazioneRuoloEnum.FRUIZIONE.equals(ruoloTraccia)){
					if(transazioneDB.getFaultCooperazione()!=null) {
						((TransazioneDettaglioRisposta)risposta).setFaultRicezione(transazioneDB.getFaultCooperazione().getBytes());
					}
					if(transazioneDB.getFaultIntegrazione()!=null) {
						((TransazioneDettaglioRisposta)risposta).setFaultConsegna(transazioneDB.getFaultIntegrazione().getBytes());
					}
				}
				else {
					if(transazioneDB.getFaultCooperazione()!=null) {
						((TransazioneDettaglioRisposta)risposta).setFaultConsegna(transazioneDB.getFaultCooperazione().getBytes());
					}
					if(transazioneDB.getFaultIntegrazione()!=null) {
						((TransazioneDettaglioRisposta)risposta).setFaultRicezione(transazioneDB.getFaultIntegrazione().getBytes());
					}
				}
			}
			if(this.risposta_errori && !errori.isEmpty()) {
				((TransazioneDettaglioRisposta)risposta).setDettagliErrore(new ArrayList<>());
				for (String errore : errori) {
					((TransazioneDettaglioRisposta)risposta).getDettagliErrore().add(errore);
				}
			}
			((Transazione)transazione).setRisposta(((TransazioneDettaglioRisposta)risposta));
		}
		
		
		
		TransazioneInformazioniApi api = null;
		if(extended) {
			api = new TransazioneExtInformazioniApi();
		}
		else {
			api = new TransazioneInformazioniApi();
		}
		if(extended) {
			if(this.api_erogatoreExtraInfo) {
				if(transazioneDB.getNomeSoggettoErogatore()!=null && transazioneDB.getTipoSoggettoErogatore()!=null) {
					String idPorta = transazioneDB.getIdportaSoggettoErogatore();
					if(idPorta==null || "".equals(idPorta)) {
						idPorta = protocolFactory.createTraduttore().getIdentificativoPortaDefault(new IDSoggetto(transazioneDB.getTipoSoggettoErogatore(), transazioneDB.getNomeSoggettoErogatore()));
					}
					((TransazioneExtInformazioniApi)api).setInformazioniErogatore(_newTransazioneSoggetto(transazioneDB.getTipoSoggettoErogatore(), 
							idPorta, transazioneDB.getIndirizzoSoggettoErogatore()));
				}
			}
			if(this.api_tipo) {
				((TransazioneExtInformazioniApi)api).setTipo(transazioneDB.getTipoServizio());
			}
		}
		if(this.api_erogatore) {
			api.setErogatore(transazioneDB.getNomeSoggettoErogatore());
		}
		if(this.api_tags) {
			if(transazioneDB.getGruppi()!=null && !"".equals(transazioneDB.getGruppi())) {
				String [] tmp = transazioneDB.getGruppi().split(",");
				if(tmp!=null && tmp.length>0) {
					((TransazioneExtInformazioniApi)api).setTags(new ArrayList<>());
					for (String tag : tmp) {
						if(tag!=null && !"".equals(tag.trim())) {
							((TransazioneExtInformazioniApi)api).addTagsItem(tag.trim());
						}
					}
				}
			}
		}
		if(this.api_nome) {
			api.setNome(transazioneDB.getNomeServizio());
		}
		if(this.api_versione) {
			api.setVersione(transazioneDB.getVersioneServizio());
		}
		if(this.api_operazione) {
			api.setOperazione(transazioneDB.getAzione());
		}
		if(extended) {
			if(this.api_idAsincrono) {
				((TransazioneExtInformazioniApi)api).setIdAsincrono(transazioneDB.getIdAsincrono());
			}
			if(this.api_profiloCollaborazione && transazioneDB.getProfiloCollaborazioneOp2()!=null) {
				ProfiloDiCollaborazione prof = ProfiloDiCollaborazione.toProfiloDiCollaborazione(transazioneDB.getProfiloCollaborazioneOp2());
				if(prof!=null) {
					switch (prof) {
					case ONEWAY:
						((TransazioneExtInformazioniApi)api).setProfiloCollaborazione(ProfiloCollaborazioneEnum.ONEWAY);		
						break;
					case SINCRONO:
						((TransazioneExtInformazioniApi)api).setProfiloCollaborazione(ProfiloCollaborazioneEnum.SINCRONO);		
						break;
					case ASINCRONO_SIMMETRICO:
						((TransazioneExtInformazioniApi)api).setProfiloCollaborazione(ProfiloCollaborazioneEnum.ASINCRONOSIMMETRICO);		
						break;
					case ASINCRONO_ASIMMETRICO:
						((TransazioneExtInformazioniApi)api).setProfiloCollaborazione(ProfiloCollaborazioneEnum.ASINCRONOASIMMETRICO);		
						break;
					case UNKNOWN:
						break;
					}
				}
			}
			((TransazioneExt)transazione).setApi(((TransazioneExtInformazioniApi)api));
		}
		else {
			((Transazione)transazione).setApi(api);
		}
		
		TransazioneInformazioniMittente mittente = null;
		if(extended) {
			mittente = new TransazioneExtInformazioniMittente();
		}
		else {
			mittente = new TransazioneInformazioniMittente();
		}
		if(extended) {
			if(this.mittente_fruitoreExtraInfo) {
				if(transazioneDB.getNomeSoggettoFruitore()!=null && transazioneDB.getTipoSoggettoFruitore()!=null) {
					String idPorta = transazioneDB.getIdportaSoggettoFruitore();
					if(idPorta==null || "".equals(idPorta)) {
						idPorta = protocolFactory.createTraduttore().getIdentificativoPortaDefault(new IDSoggetto(transazioneDB.getTipoSoggettoFruitore(), transazioneDB.getNomeSoggettoFruitore()));
					}
					((TransazioneExtInformazioniMittente)mittente).setInformazioniFruitore(_newTransazioneSoggetto(transazioneDB.getTipoSoggettoFruitore(), 
							idPorta, transazioneDB.getIndirizzoSoggettoFruitore()));
				}
			}
			if(this.mittente_applicativo) {
				((TransazioneExtInformazioniMittente)mittente).setApplicativo(transazioneDB.getServizioApplicativoFruitore());
			}
			if(this.mittente_credenziali) {
				((TransazioneExtInformazioniMittente)mittente).setCredenziali(transazioneDB.getCredenziali());
			}
			if(this.mittente_applicativoToken && credenzialiMittente!=null && credenzialiMittente.getTokenClientId()!=null) {
				IDServizioApplicativo idSA = CredenzialeTokenClient.convertApplicationDBValueToOriginal(credenzialiMittente.getTokenClientId().getCredenziale());
				if(idSA!=null) {
					((TransazioneExtInformazioniMittente)mittente).setApplicativoToken(new TransazioneExtInformazioniMittenteApplicativoToken());
					((TransazioneExtInformazioniMittente)mittente).getApplicativoToken().setNome(idSA.getNome());
					((TransazioneExtInformazioniMittente)mittente).getApplicativoToken().setSoggetto(idSA.getIdSoggettoProprietario().getNome());
					String idPorta = protocolFactory.createTraduttore().getIdentificativoPortaDefault(new IDSoggetto(idSA.getIdSoggettoProprietario().getTipo(), idSA.getIdSoggettoProprietario().getNome()));
					((TransazioneExtInformazioniMittente)mittente).getApplicativoToken().setInformazioniSoggetto(_newTransazioneSoggetto(idSA.getIdSoggettoProprietario().getTipo(), 
							idPorta, null));
				}
			}
		}
		if(this.mittente_principal && credenzialiMittente!=null && credenzialiMittente.getTrasporto()!=null) {
			mittente.setPrincipal(credenzialiMittente.getTrasporto().getCredenziale());
		}
		if(this.mittente_fruitore) {
			mittente.setFruitore(transazioneDB.getNomeSoggettoFruitore());
		}
		if(this.mittente_utente && credenzialiMittente!=null && credenzialiMittente.getTokenUsername()!=null) {
			mittente.setUtente(credenzialiMittente.getTokenUsername().getCredenziale());
		}
		if(this.mittente_client && credenzialiMittente!=null && credenzialiMittente.getTokenClientId()!=null) {
			mittente.setClient(CredenzialeTokenClient.convertClientIdDBValueToOriginal(credenzialiMittente.getTokenClientId().getCredenziale()));
		}
		if(this.mittente_indirizzoClient) {
			mittente.setIndirizzoClient(transazioneDB.getSocketClientAddress());
		}
		if(this.mittente_indirizzoClientInoltrato) {
			mittente.setIndirizzoClientInoltrato(transazioneDB.getTransportClientAddress());
		}
		
		if(extended) {
			if((this.mittente_tokenClaims && credenzialiMittente!=null) || (this.mittente_token && transazioneDB.getTokenInfo()!=null)) {
				TransazioneExtInformazioniToken informazioniToken = new TransazioneExtInformazioniToken();
				boolean add = false;
				if(this.mittente_tokenClaims && credenzialiMittente!=null) {
					if(credenzialiMittente.getTokenClientId()!=null) {
						informazioniToken.setClientId(CredenzialeTokenClient.convertClientIdDBValueToOriginal(credenzialiMittente.getTokenClientId().getCredenziale()));
						add=true;
					}
					if(credenzialiMittente.getTokenIssuer()!=null) {
						informazioniToken.setIssuer(credenzialiMittente.getTokenIssuer().getCredenziale());
						add=true;
					}
					if(credenzialiMittente.getTokenSubject()!=null) {
						informazioniToken.setSubject(credenzialiMittente.getTokenSubject().getCredenziale());
						add=true;
					}
					if(credenzialiMittente.getTokenUsername()!=null) {
						informazioniToken.setUsername(credenzialiMittente.getTokenUsername().getCredenziale());
						add=true;
					}
					if(credenzialiMittente.getTokenEMail()!=null) {
						informazioniToken.setMail(credenzialiMittente.getTokenEMail().getCredenziale());
						add=true;
					}
				}
				if(add) {
					((TransazioneExtInformazioniMittente)mittente).setInformazioniToken(informazioniToken);
				}
				if(this.mittente_token && transazioneDB.getTokenInfo()!=null) {
					((TransazioneExtInformazioniMittente)mittente).setToken(transazioneDB.getTokenInfo().getBytes());
				}
			}
			((TransazioneExt)transazione).setMittente((TransazioneExtInformazioniMittente)mittente);
		}
		else {
			((Transazione)transazione).setMittente(mittente);
		}
		
		if(extended) {
		
			if(messaggiDiagnostici!=null && messaggiDiagnostici.size()>0) {
			
				((TransazioneExt)transazione).setDiagnostici(new ArrayList<>());
				
				for (MsgDiagnostico msgDiag : messaggiDiagnostici) {
					Diagnostico diag = new Diagnostico();
					diag.setCodice(msgDiag.getCodice());
					diag.setData(new DateTime(msgDiag.getGdo().getTime()));
					diag.setFunzione(msgDiag.getIdFunzione());
					diag.setMessaggio(msgDiag.getMessaggio());
					DiagnosticoSeveritaEnum severita = null;
					if(LogLevels.SEVERITA_FATAL == msgDiag.getSeverita()) {
						severita = DiagnosticoSeveritaEnum.FATAL;
					}
					else if(LogLevels.SEVERITA_ERROR_INTEGRATION == msgDiag.getSeverita() || LogLevels.SEVERITA_ERROR_PROTOCOL == msgDiag.getSeverita()) {
						severita = DiagnosticoSeveritaEnum.ERROR;
					}
					else if(LogLevels.SEVERITA_INFO_INTEGRATION == msgDiag.getSeverita() || LogLevels.SEVERITA_INFO_PROTOCOL == msgDiag.getSeverita()) {
						severita = DiagnosticoSeveritaEnum.INFO;
					}
					else if(LogLevels.SEVERITA_DEBUG_LOW == msgDiag.getSeverita() || LogLevels.SEVERITA_DEBUG_MEDIUM == msgDiag.getSeverita()) {
						severita = DiagnosticoSeveritaEnum.DEBUG;
					}
					else {
						severita = DiagnosticoSeveritaEnum.TRACE;
					}
					diag.setSeverita(severita);
					diag.setSeveritaCodice(msgDiag.getCodice());
					((TransazioneExt)transazione).getDiagnostici().add(diag);
				}
			
			}
			
		}
		
		return transazione;

	}
	
	private String normalizeUrl(String url) {
		if(url!=null) {
			if(url.startsWith("[") && url.contains("] ") && !url.endsWith("] ")) {
				return url.substring(url.indexOf("] ")+2);
			}
		}
		return url;
	}
	
	private TransazioneExtInformazioniSoggetto _newTransazioneSoggetto(String tipo, String codice, String indirizzo) {
		TransazioneExtInformazioniSoggetto soggetto = new TransazioneExtInformazioniSoggetto();
		soggetto.setTipo(tipo);
		soggetto.setCodice(codice);
		soggetto.setIndirizzo(indirizzo);
		return soggetto;
	}
	
	private TransazioneContenutoMessaggio newTransazioneContenutoMessaggio(org.openspcoop2.core.transazioni.Transazione transazioneDB, 
			TipoMessaggio tipoMessaggioNormale, TipoMessaggio tipoMessaggioBinario, boolean extended) {
		
		DumpMessaggio dumpMessaggio = null;
		if(transazioneDB.sizeDumpMessaggioList()>0) {
			for (DumpMessaggio check : transazioneDB.getDumpMessaggioList()) {
				if(tipoMessaggioBinario.equals(check.getTipoMessaggio())) {
					dumpMessaggio = check;
					break;
				}
			}
			if(dumpMessaggio==null) {
				for (DumpMessaggio check : transazioneDB.getDumpMessaggioList()) {
					if(tipoMessaggioNormale.equals(check.getTipoMessaggio())) {
						dumpMessaggio = check;
						break;
					}
				}
			}
		}
		if(dumpMessaggio==null) {
			return null;
		}		
		
		TransazioneContenutoMessaggio msg = null;
		if(extended) {
			msg = new TransazioneExtContenutoMessaggio();
		}
		else {
			msg = new TransazioneContenutoMessaggio();
		}
		
		msg.setBody(dumpMessaggio.getBody());
		
		if(dumpMessaggio.sizeHeaderTrasportoList()>0) {
			msg.setHeaders(new ArrayList<>());
			for (DumpHeaderTrasporto dumpHeaderTrasporto : dumpMessaggio.getHeaderTrasportoList()) {
				TransazioneContenutoMessaggioHeader hdr = new TransazioneContenutoMessaggioHeader();
				hdr.setNome(dumpHeaderTrasporto.getNome());
				hdr.setValore(dumpHeaderTrasporto.getValore());
				msg.getHeaders().add(hdr);
			}
		}
		
		if(extended) {
			TransazioneExtContenutoMessaggioBody body = new TransazioneExtContenutoMessaggioBody();
			body.setFormato(_convert(dumpMessaggio.getFormatoMessaggio()));
			body.setContentType(dumpMessaggio.getContentType());
			body.setContentLength(dumpMessaggio.getContentLength());
			if(dumpMessaggio.getMultipartContentId()!=null || 
					dumpMessaggio.getMultipartContentLocation()!=null ||
					dumpMessaggio.getMultipartContentType()!=null ||
					dumpMessaggio.sizeMultipartHeaderList()>0) {
				TransazioneExtContenutoMessaggioBodyMultipart multipart = new TransazioneExtContenutoMessaggioBodyMultipart();
				multipart.setContentId(dumpMessaggio.getMultipartContentId());
				multipart.setContentType(dumpMessaggio.getMultipartContentType());
				multipart.setContentLocation(dumpMessaggio.getMultipartContentLocation());
				if(dumpMessaggio.sizeMultipartHeaderList()>0) {
					multipart.setHeaders(new ArrayList<>());
					for (DumpMultipartHeader dumpHeaderTrasporto : dumpMessaggio.getMultipartHeaderList()) {
						TransazioneContenutoMessaggioHeader hdr = new TransazioneContenutoMessaggioHeader();
						hdr.setNome(dumpHeaderTrasporto.getNome());
						hdr.setValore(dumpHeaderTrasporto.getValore());
						multipart.getHeaders().add(hdr);
					}
				}
				body.setMultipart(multipart);
			}
			if(dumpMessaggio.sizeContenutoList()>0) {
				body.setContenutiBody(new ArrayList<>());
				for (DumpContenuto contenuto : dumpMessaggio.getContenutoList()) {
					TransazioneExtContenutoMessaggioPorzioneBody r = new TransazioneExtContenutoMessaggioPorzioneBody();
					r.setNome(contenuto.getNome());
					r.setValore(contenuto.getValore());
					body.getContenutiBody().add(r);		
				}
			}
			((TransazioneExtContenutoMessaggio)msg).setInformazioniBody(body);

			if(dumpMessaggio.sizeAllegatoList()>0) {
				((TransazioneExtContenutoMessaggio)msg).setAttachments(new ArrayList<>());
				for (DumpAllegato dumpAllegato : dumpMessaggio.getAllegatoList()) {
					TransazioneExtContenutoMessaggioAllegato attach = new TransazioneExtContenutoMessaggioAllegato();
					attach.setContentId(dumpAllegato.getContentId());
					attach.setContentType(dumpAllegato.getContentType());
					attach.setContentLocation(dumpAllegato.getContentLocation());
					attach.setContenuto(dumpAllegato.getAllegato());
					if(dumpAllegato.sizeHeaderList()>0) {
						dumpAllegato.setHeaderList(new ArrayList<>());
						for (DumpHeaderAllegato dumpHeaderAllegato : dumpAllegato.getHeaderList()) {
							TransazioneContenutoMessaggioHeader hdr = new TransazioneContenutoMessaggioHeader();
							hdr.setNome(dumpHeaderAllegato.getNome());
							hdr.setValore(dumpHeaderAllegato.getValore());
							attach.getHeaders().add(hdr);
						}
					}
					((TransazioneExtContenutoMessaggio)msg).getAttachments().add(attach);
				}
			}
		}
		

		
		return msg;
	}
	
	private TransazioneMessaggioFormatoEnum _convert(String formato) {
		if(formato==null) {
			return null;
		}
		MessageType tipoMessaggio = null;
		try {
			tipoMessaggio = MessageType.valueOf(formato);
		}catch(Throwable t) {}
		if(tipoMessaggio==null) {
			return null;
		}
		switch (tipoMessaggio) {
		case SOAP_11:
			return TransazioneMessaggioFormatoEnum.SOAP11;
		case SOAP_12:
			return TransazioneMessaggioFormatoEnum.SOAP12;
		case JSON:
			return TransazioneMessaggioFormatoEnum.JSON;
		case XML:
			return TransazioneMessaggioFormatoEnum.XML;
		case BINARY:
			return TransazioneMessaggioFormatoEnum.BINARY;
		case MIME_MULTIPART:
			return TransazioneMessaggioFormatoEnum.MULTIPART;
		}
		return null;
	}
	
	
	
	// SETTER
	
	public void setLog(Logger log) {
		this.log = log;
	}


	public void setThrowInitProtocol(boolean throwInitProtocol) {
		this.throwInitProtocol = throwInitProtocol;
	}


	public void setEmittente(boolean emittente) {
		this.emittente = emittente;
	}


	public void setDataEmissione(boolean dataEmissione) {
		this.dataEmissione = dataEmissione;
	}


	public void setId(boolean id) {
		this.id = id;
	}


	public void setIdConversazione(boolean idConversazione) {
		this.idConversazione = idConversazione;
	}


	public void setRuolo(boolean ruolo) {
		this.ruolo = ruolo;
	}


	public void setEsito(boolean esito) {
		this.esito = esito;
	}


	public void setMessaggi_id(boolean messaggi_id) {
		this.messaggi_id = messaggi_id;
	}


	public void setMessaggi_idApplicativo(boolean messaggi_idApplicativo) {
		this.messaggi_idApplicativo = messaggi_idApplicativo;
	}


	public void setMessaggi_data(boolean messaggi_data) {
		this.messaggi_data = messaggi_data;
	}


	public void setMessaggi_contenuti(boolean messaggi_contenuti) {
		this.messaggi_contenuti = messaggi_contenuti;
	}


	public void setRichiesta_tipo(boolean richiesta_tipo) {
		this.richiesta_tipo = richiesta_tipo;
	}


	public void setRichiesta_urlInvocazione(boolean richiesta_urlInvocazione) {
		this.richiesta_urlInvocazione = richiesta_urlInvocazione;
	}


	public void setRisposta_esito(boolean risposta_esito) {
		this.risposta_esito = risposta_esito;
	}


	public void setRisposta_fault(boolean risposta_fault) {
		this.risposta_fault = risposta_fault;
	}


	public void setRisposta_errori(boolean risposta_errori) {
		this.risposta_errori = risposta_errori;
	}


	public void setApi_erogatore(boolean api_erogatore) {
		this.api_erogatore = api_erogatore;
	}


	public void setApi_nome(boolean api_nome) {
		this.api_nome = api_nome;
	}


	public void setApi_versione(boolean api_versione) {
		this.api_versione = api_versione;
	}


	public void setApi_operazione(boolean api_operazione) {
		this.api_operazione = api_operazione;
	}


	public void setMittente_fruitore(boolean mittente_fruitore) {
		this.mittente_fruitore = mittente_fruitore;
	}


	public void setMittente_principal(boolean mittente_principal) {
		this.mittente_principal = mittente_principal;
	}

	
	public void setMittente_utente(boolean mittente_utente) {
		this.mittente_utente = mittente_utente;
	}
	
	
	public void setMittente_client(boolean mittente_client) {
		this.mittente_client = mittente_client;
	}
	

	public void setMittente_indirizzoClient(boolean mittente_indirizzoClient) {
		this.mittente_indirizzoClient = mittente_indirizzoClient;
	}


	public void setMittente_indirizzoClientInoltrato(boolean mittente_indirizzoClientInoltrato) {
		this.mittente_indirizzoClientInoltrato = mittente_indirizzoClientInoltrato;
	}


	public void setProfilo(boolean profilo) {
		this.profilo = profilo;
	}


	public void setContesto(boolean contesto) {
		this.contesto = contesto;
	}


	public void setIdCluster(boolean idCluster) {
		this.idCluster = idCluster;
	}


	public void setEmittenteExtraInfo(boolean emittenteExtraInfo) {
		this.emittenteExtraInfo = emittenteExtraInfo;
	}


	public void setStato(boolean stato) {
		this.stato = stato;
	}


	public void setMessaggi_dimensione(boolean messaggi_dimensione) {
		this.messaggi_dimensione = messaggi_dimensione;
	}


	public void setMessaggi_dataAccettazione(boolean messaggi_dataAccettazione) {
		this.messaggi_dataAccettazione = messaggi_dataAccettazione;
	}


	public void setMessaggi_duplicati(boolean messaggi_duplicati) {
		this.messaggi_duplicati = messaggi_duplicati;
	}


	public void setMessaggi_tracciaProtocollo(boolean messaggi_tracciaProtocollo) {
		this.messaggi_tracciaProtocollo = messaggi_tracciaProtocollo;
	}


	public void setRichiesta_connettore(boolean richiesta_connettore) {
		this.richiesta_connettore = richiesta_connettore;
	}


	public void setDiagnostici(boolean diagnostici) {
		this.diagnostici = diagnostici;
	}


	public void setApi_erogatoreExtraInfo(boolean api_erogatoreExtraInfo) {
		this.api_erogatoreExtraInfo = api_erogatoreExtraInfo;
	}

	public void setApi_tags(boolean api_tags) {
		this.api_tags = api_tags;
	}

	public void setApi_tipo(boolean api_tipo) {
		this.api_tipo = api_tipo;
	}


	public void setApi_idAsincrono(boolean api_idAsincrono) {
		this.api_idAsincrono = api_idAsincrono;
	}


	public void setApi_profiloCollaborazione(boolean api_profiloCollaborazione) {
		this.api_profiloCollaborazione = api_profiloCollaborazione;
	}


	public void setMittente_fruitoreExtraInfo(boolean mittente_fruitoreExtraInfo) {
		this.mittente_fruitoreExtraInfo = mittente_fruitoreExtraInfo;
	}


	public void setMittente_applicativo(boolean mittente_applicativo) {
		this.mittente_applicativo = mittente_applicativo;
	}


	public void setMittente_credenziali(boolean mittente_credenziali) {
		this.mittente_credenziali = mittente_credenziali;
	}


	public void setMittente_token(boolean mittente_token) {
		this.mittente_token = mittente_token;
	}


	public void setMittente_tokenClaims(boolean mittente_tokenClaims) {
		this.mittente_tokenClaims = mittente_tokenClaims;
	}
}
