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
package org.openspcoop2.core.monitor.rs.server.api.impl.utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.core.monitor.rs.server.config.SoggettiConfig;
import org.openspcoop2.core.monitor.rs.server.model.BaseTracingPDND;
import org.openspcoop2.core.monitor.rs.server.model.DetailTransazione;
import org.openspcoop2.core.monitor.rs.server.model.DetailsTracingPDND;
import org.openspcoop2.core.monitor.rs.server.model.Evento;
import org.openspcoop2.core.monitor.rs.server.model.InfoImplementazioneApi;
import org.openspcoop2.core.monitor.rs.server.model.ItemTracingPDND;
import org.openspcoop2.core.monitor.rs.server.model.ItemTransazione;
import org.openspcoop2.core.monitor.rs.server.model.ListaTracingPDND;
import org.openspcoop2.core.monitor.rs.server.model.MethodTracingPDND;
import org.openspcoop2.core.monitor.rs.server.model.PDNDClientInfo;
import org.openspcoop2.core.monitor.rs.server.model.PDNDOrganizationExternalId;
import org.openspcoop2.core.monitor.rs.server.model.PDNDOrganizationInfo;
import org.openspcoop2.core.monitor.rs.server.model.PDNDOrganizationInfoItemTransazione;
import org.openspcoop2.core.monitor.rs.server.model.Riepilogo;
import org.openspcoop2.core.monitor.rs.server.model.RiepilogoApiItem;
import org.openspcoop2.core.monitor.rs.server.model.StatoTracing;
import org.openspcoop2.core.monitor.rs.server.model.StatoTracingPDND;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.statistiche.constants.PdndMethods;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiPdnd;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiRichieste;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeTokenClient;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.utils.service.beans.DiagnosticoSeveritaEnum;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.TransazioneExt;
import org.openspcoop2.utils.service.beans.TransazioneRuoloEnum;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.beans.utils.ProfiloUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.eventi.bean.EventoBean;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiConfigurazioni;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.dao.TransazioniService;
import org.openspcoop2.web.monitor.transazioni.datamodel.TransazioniDM;
import org.slf4j.Logger;

/**
 * Converter
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Converter {

	public static String toProtocollo(ProfiloEnum profilo) throws UtilsException {
		if(profilo==null) {
			return ServerProperties.getInstance().getProtocolloDefault();
		}
		return ProfiloUtils.toProtocollo(profilo);
	}
	
	public static Integer toOffset(Integer offset) {
		return offset!=null ? offset : 0;
	}
	public static Integer toLimit(Integer limit) {
		return limit!=null ? limit : 25;
	}
	public static SortOrder toSortOrder(String sort) {
		if(StringUtils.isNotEmpty(sort)) {
			if(sort.startsWith("+")) {
				return SortOrder.ASC;
			}
			else if(sort.startsWith("-")) {
				return SortOrder.DESC;
			}
		}
		return SortOrder.DESC;
	}
	public static String toSortField(String sort) {
		if(StringUtils.isNotEmpty(sort)) {
			String sortParam = sort;
			if(sort.startsWith("+") || sort.startsWith("-")) {
				sortParam = sort.substring(1);
			}
			if(TransazioniDM.COL_DATA_LATENZA_TOTALE.equals(sortParam)){
				return sortParam;
			} else if(TransazioniDM.COL_DATA_LATENZA_SERVIZIO.equals(sortParam)){
				return sortParam;
			} else if(TransazioniDM.COL_DATA_INGRESSO_RICHIESTA.equals(sortParam)){
				return sortParam;
			}
			else {
				FaultCode.RICHIESTA_NON_VALIDA.throwException("Sort parameter '"+sort+"' non valido");
			}
		}
		return TransazioniDM.COL_DATA_INGRESSO_RICHIESTA;
	}
	
	public static DetailTransazione toTransazioneExt(TransazioneBean transazioneDB, TransazioniService transazioniService, 
			Connection con, ServiceManagerProperties smp,
			Logger log) throws Exception {
		org.openspcoop2.pdd.logger.traccia.Converter converter = new org.openspcoop2.pdd.logger.traccia.Converter(log);
		
		PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(log);
		
		Traccia tracciaRichiesta = null;
		try {
			tracciaRichiesta = govwayMonitorProperties.getDriverTracciamento(con,smp).getTraccia(transazioneDB.getIdTransazione(), RuoloMessaggio.RICHIESTA);
		}catch(DriverTracciamentoNotFoundException notFound) {}
		
		Traccia tracciaRisposta = null;
		try {
			tracciaRisposta = govwayMonitorProperties.getDriverTracciamento(con,smp).getTraccia(transazioneDB.getIdTransazione(), RuoloMessaggio.RISPOSTA);
		}catch(DriverTracciamentoNotFoundException notFound) {}
		
		FiltroRicercaDiagnosticiConPaginazione filter = new FiltroRicercaDiagnosticiConPaginazione();
		filter.setIdTransazione(transazioneDB.getIdTransazione());
		List<MsgDiagnostico> messaggiDiagnostici = null;
		try {
			messaggiDiagnostici = govwayMonitorProperties.getDriverMsgDiagnostici(con,smp).getMessaggiDiagnostici(filter);
		}catch(DriverMsgDiagnosticiNotFoundException notFound) {}

		CredenzialiMittente credenzialiMittente = Converter.convertToCredenzialiMittente(transazioneDB);		
		
		if(transazioniService.hasInfoDumpAvailable(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO)) {
			DumpMessaggio dumpMessaggio = transazioniService.getDumpMessaggio(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO);
			if(dumpMessaggio!=null) {
				transazioneDB.addDumpMessaggio(dumpMessaggio);
			}
		}
		else if(transazioniService.hasInfoDumpAvailable(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RICHIESTA_INGRESSO)) {
			DumpMessaggio dumpMessaggio = transazioniService.getDumpMessaggio(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RICHIESTA_INGRESSO);
			if(dumpMessaggio!=null) {
				transazioneDB.addDumpMessaggio(dumpMessaggio);
			}
		}
		
		if(transazioniService.hasInfoDumpAvailable(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO)) {
			DumpMessaggio dumpMessaggio = transazioniService.getDumpMessaggio(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO);
			if(dumpMessaggio!=null) {
				transazioneDB.addDumpMessaggio(dumpMessaggio);
			}
		}
		else if(transazioniService.hasInfoDumpAvailable(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RICHIESTA_USCITA)) {
			DumpMessaggio dumpMessaggio = transazioniService.getDumpMessaggio(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RICHIESTA_USCITA);
			if(dumpMessaggio!=null) {
				transazioneDB.addDumpMessaggio(dumpMessaggio);
			}
		}
		
		if(transazioniService.hasInfoDumpAvailable(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO)) {
			DumpMessaggio dumpMessaggio = transazioniService.getDumpMessaggio(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO);
			if(dumpMessaggio!=null) {
				transazioneDB.addDumpMessaggio(dumpMessaggio);
			}
		}
		else if(transazioniService.hasInfoDumpAvailable(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RISPOSTA_INGRESSO)) {
			DumpMessaggio dumpMessaggio = transazioniService.getDumpMessaggio(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RISPOSTA_INGRESSO);
			if(dumpMessaggio!=null) {
				transazioneDB.addDumpMessaggio(dumpMessaggio);
			}
		}
		
		if(transazioniService.hasInfoDumpAvailable(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO)) {
			DumpMessaggio dumpMessaggio = transazioniService.getDumpMessaggio(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO);
			if(dumpMessaggio!=null) {
				transazioneDB.addDumpMessaggio(dumpMessaggio);
			}
		}
		else if(transazioniService.hasInfoDumpAvailable(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RISPOSTA_USCITA)) {
			DumpMessaggio dumpMessaggio = transazioniService.getDumpMessaggio(transazioneDB.getIdTransazione(), null, null, TipoMessaggio.RISPOSTA_USCITA);
			if(dumpMessaggio!=null) {
				transazioneDB.addDumpMessaggio(dumpMessaggio);
			}
		}
		
		TransazioneExt transazioneExt = converter.toTransazioneExt(transazioneDB, credenzialiMittente, tracciaRichiesta, tracciaRisposta, messaggiDiagnostici);
		
		// aggiunto campi supplementari
		
		DetailTransazione detail = new DetailTransazione();
		
		if(transazioneExt.getRichiesta()!=null) {
			detail.setData(transazioneExt.getRichiesta().getDataRicezione());
		}
		if(transazioneExt.getRichiesta()!=null && transazioneExt.getRichiesta().getDataConsegna()!=null &&
				transazioneExt.getRisposta()!=null && transazioneExt.getRisposta().getDataAccettazione()!=null) {
			long dataRisposta = transazioneExt.getRisposta().getDataAccettazione().getMillis();
			long dataRichiesta = transazioneExt.getRichiesta().getDataConsegna().getMillis(); 
			detail.setLatenzaServizio(dataRisposta-dataRichiesta);
		}
		if(transazioneExt.getRichiesta()!=null && transazioneExt.getRichiesta().getDataAccettazione()!=null &&
				transazioneExt.getRisposta()!=null && transazioneExt.getRisposta().getDataConsegna()!=null) {
			long dataRisposta = transazioneExt.getRisposta().getDataConsegna().getMillis();
			long dataRichiesta = transazioneExt.getRichiesta().getDataAccettazione().getMillis(); 
			detail.setLatenzaTotale(dataRisposta-dataRichiesta);
		}
		
		String pdndOrganizationName = transazioneDB.getPdndOrganizationName();
		String pdndOrganizationCategory = transazioneDB.getPdndOrganizationCategory();
		String pdndOrganizationSubUnit = transazioneDB.getPdndOrganizationSubUnit();
		String pdndExternalId = transazioneDB.getPdndOrganizationExternalId();
		String pdndConsumerId = transazioneDB.getPdndOrganizationConsumerId();
		if(StringUtils.isNotEmpty(pdndOrganizationName) || 
				StringUtils.isNotEmpty(pdndOrganizationCategory) ||
				StringUtils.isNotEmpty(pdndOrganizationSubUnit) ||
				StringUtils.isNotEmpty(pdndExternalId)) {
			PDNDOrganizationInfo pdndOrganizationInfo = new PDNDOrganizationInfo();
			pdndOrganizationInfo.setNome(pdndOrganizationName);
			if(StringUtils.isNotEmpty(pdndOrganizationCategory)) {
				pdndOrganizationInfo.setCategoria(pdndOrganizationCategory);
			}
			if(StringUtils.isNotEmpty(pdndOrganizationSubUnit)) {
				pdndOrganizationInfo.setSubUnit(pdndOrganizationSubUnit);
			}
			if(StringUtils.isNotEmpty(pdndExternalId) && pdndExternalId.contains(" ")) {
				try {
					int indexOf = pdndExternalId.indexOf(" ");
					String origin = pdndExternalId.substring(0, indexOf);
					String code = pdndExternalId.replaceFirst(origin+" ","");
					PDNDOrganizationExternalId extId = new PDNDOrganizationExternalId();
					extId.setOrigine(origin);
					extId.setCodice(code);
					pdndOrganizationInfo.setExternalId(extId);
				}
				catch(Exception e) {
					// ignore
				}
			}
			pdndOrganizationInfo.setConsumerId(pdndConsumerId);
			detail.setPdndOrganization(pdndOrganizationInfo);
		}
		
		String pdndClientnName = transazioneDB.getPdndClientName();
		String pdndClientDescription = transazioneDB.getPdndClientDescription();
		if(StringUtils.isNotEmpty(pdndClientnName) || 
				StringUtils.isNotEmpty(pdndClientDescription)) {
			PDNDClientInfo pdndClientInfo = new PDNDClientInfo();
			pdndClientInfo.setNome(pdndClientnName);
			if(StringUtils.isNotEmpty(pdndClientDescription)) {
				pdndClientInfo.setDescrizione(pdndClientDescription);
			}
			if(StringUtils.isNotEmpty(transazioneDB.getTokenClientIdLabel())) {
				pdndClientInfo.setClientId(transazioneDB.getTokenClientIdLabel());
			}
			detail.setPdndClient(pdndClientInfo);
		}
		
		String richiedente = transazioneDB.getLabelRichiedenteConFruitore();
		if(StringUtils.isNotEmpty(richiedente)) {
			detail.setRichiedente(richiedente);
		}
		String dettaglioErrore = transazioneDB.getDettaglioErrore(messaggiDiagnostici);
		if(StringUtils.isNotEmpty(dettaglioErrore)) {
			detail.setDettaglioErrore(dettaglioErrore);
		}
				
		List<BlackListElement> metodiEsclusi = new ArrayList<>();
		metodiEsclusi.add(new BlackListElement("setData", DateTime.class));
		metodiEsclusi.add(new BlackListElement("setLatenzaServizio", Long.class));
		metodiEsclusi.add(new BlackListElement("setLatenzaTotale", Long.class));
		metodiEsclusi.add(new BlackListElement("setPdndOrganization", PDNDOrganizationInfo.class));
		metodiEsclusi.add(new BlackListElement("setPdndClient", PDNDClientInfo.class));
		metodiEsclusi.add(new BlackListElement("setRichiedente", String.class));
		metodiEsclusi.add(new BlackListElement("setDettaglioErrore", String.class));
		org.openspcoop2.utils.beans.BeanUtils.copy(log, detail, transazioneExt, metodiEsclusi, true);
		
		// Se presenti, finiscono come informazione del token in detail.getMittente().getInformazioniToken()
		/*
		if(detail.getMittente()!=null) {
			detail.getMittente().setUtente(null);
			detail.getMittente().setClient(null);
		}
		*/
		
		return detail;
	}
	
	public static ItemTransazione toItemTransazione(TransazioneBean transazioneDB, Logger log)  throws Exception {
	
		CredenzialiMittente credenzialiMittente = Converter.convertToCredenzialiMittente(transazioneDB);		
		
		org.openspcoop2.pdd.logger.traccia.Converter converter = new org.openspcoop2.pdd.logger.traccia.Converter(log);
		if(transazioneDB.getGruppiLabel()!=null && !"".equals(transazioneDB.getGruppiLabel())) {
			// converto voce originale
			transazioneDB.setGruppi(transazioneDB.getGruppiLabel());
		}
		if(transazioneDB.getSocketClientAddress()==null && transazioneDB.getSocketClientAddressLabel()!=null) {
			// converto voce originale
			transazioneDB.setSocketClientAddress(transazioneDB.getSocketClientAddressLabel());
		}
		if(transazioneDB.getTransportClientAddress()==null && transazioneDB.getTransportClientAddressLabel()!=null) {
			// converto voce originale
			transazioneDB.setTransportClientAddress(transazioneDB.getTransportClientAddressLabel());
		}
		TransazioneExt transazione = converter.toTransazioneExt(transazioneDB, credenzialiMittente, null, null, null);
	
		// elimino i campi non previsti in un ItemTransazione
		if(transazione.getRichiesta()!=null) {
			transazione.getRichiesta().setContenutiIngresso(null);
			transazione.getRichiesta().setContenutiUscita(null);
			transazione.getRichiesta().setDuplicatiMessaggio(null);
			transazione.getRichiesta().setTraccia(null);
			transazione.getRichiesta().setDataRicezioneAcquisita(null);
			transazione.getRichiesta().setDataConsegnaEffettuata(null);
		}
		if(transazione.getRisposta()!=null) {
			transazione.getRisposta().setContenutiIngresso(null);
			transazione.getRisposta().setContenutiUscita(null);
			transazione.getRisposta().setDuplicatiMessaggio(null);
			transazione.getRisposta().setTraccia(null);
			transazione.getRisposta().setFaultConsegna(null);
			transazione.getRisposta().setFaultConsegnaFormato(null);
			transazione.getRisposta().setFaultRicezione(null);
			transazione.getRisposta().setFaultRicezioneFormato(null);
			transazione.getRisposta().setDettagliErrore(null);
			transazione.getRisposta().setDataRicezioneAcquisita(null);
			if(transazione.getRisposta().getDataConsegnaEffettuata()!=null) { // inverto
				transazione.getRisposta().setDataConsegna(transazione.getRisposta().getDataConsegnaEffettuata());
			}
			transazione.getRisposta().setDataConsegnaEffettuata(null);
		}
		if(transazione.getApi()!=null) {
			transazione.getApi().setProfiloCollaborazione(null);
			transazione.getApi().setIdAsincrono(null);
		}
		if(transazione.getMittente()!=null) {
			transazione.getMittente().setCredenziali(null);
			transazione.getMittente().setToken(null);
			transazione.getMittente().setInformazioniToken(null);
		}
		
		// aggiunto campi supplementari
		ItemTransazione item = new ItemTransazione();
		if(transazione.getRichiesta()!=null) {
			item.setData(transazione.getRichiesta().getDataRicezione());
		}
		if(transazione.getRichiesta()!=null && transazione.getRichiesta().getDataConsegna()!=null &&
				transazione.getRisposta()!=null && transazione.getRisposta().getDataAccettazione()!=null) {
			long dataRisposta = transazione.getRisposta().getDataAccettazione().getMillis();
			long dataRichiesta = transazione.getRichiesta().getDataConsegna().getMillis(); 
			item.setLatenzaServizio(dataRisposta-dataRichiesta);
		}
		if(transazione.getRichiesta()!=null && transazione.getRichiesta().getDataAccettazione()!=null &&
				transazione.getRisposta()!=null && transazione.getRisposta().getDataConsegna()!=null) {
			long dataRisposta = transazione.getRisposta().getDataConsegna().getMillis();
			long dataRichiesta = transazione.getRichiesta().getDataAccettazione().getMillis(); 
			item.setLatenzaTotale(dataRisposta-dataRichiesta);
		}
		
		String pdndOrganizationName = transazioneDB.getPdndOrganizationName();
		if(StringUtils.isNotEmpty(pdndOrganizationName)) {
			PDNDOrganizationInfoItemTransazione pdndOrganizationInfo = new PDNDOrganizationInfoItemTransazione();
			pdndOrganizationInfo.setNome(pdndOrganizationName);
			item.setPdndOrganization(pdndOrganizationInfo);
		}
		
		String richiedente = transazioneDB.getLabelRichiedenteConFruitore();
		if(StringUtils.isNotEmpty(richiedente)) {
			item.setRichiedente(richiedente);
		}
		
		List<BlackListElement> metodiEsclusi = new ArrayList<>();
		metodiEsclusi.add(new BlackListElement("setData", DateTime.class));
		metodiEsclusi.add(new BlackListElement("setLatenzaServizio", Long.class));
		metodiEsclusi.add(new BlackListElement("setLatenzaTotale", Long.class));
		metodiEsclusi.add(new BlackListElement("setPdndOrganization", PDNDOrganizationInfoItemTransazione.class));
		metodiEsclusi.add(new BlackListElement("setRichiedente", String.class));
		org.openspcoop2.utils.beans.BeanUtils.copy(log, item, transazione, metodiEsclusi, true );
				
		return item;
	}
	
	private static CredenzialiMittente convertToCredenzialiMittente(TransazioneBean transazioneDB) {
		CredenzialiMittente credenzialiMittente = new CredenzialiMittente();
		if(transazioneDB.getTrasportoMittenteLabel()!=null) {
			CredenzialeMittente credenziale = new CredenzialeMittente();
			credenziale.setCredenziale(transazioneDB.getTrasportoMittenteLabel());
			credenzialiMittente.setTrasporto(credenziale);
		}
		if(transazioneDB.getTokenSubjectLabel()!=null) {
			CredenzialeMittente credenziale = new CredenzialeMittente();
			credenziale.setCredenziale(transazioneDB.getTokenSubjectLabel());
			credenzialiMittente.setTokenSubject(credenziale);
		}
		if(transazioneDB.getTokenIssuerLabel()!=null) {
			CredenzialeMittente credenziale = new CredenzialeMittente();
			credenziale.setCredenziale(transazioneDB.getTokenIssuerLabel());
			credenzialiMittente.setTokenIssuer(credenziale);
		}
		if(transazioneDB.getTokenClientIdLabel()!=null) {
			CredenzialeMittente credenziale = new CredenzialeMittente();
			String clientId = transazioneDB.getTokenClientIdLabel();
			IDServizioApplicativo idSA = transazioneDB.getTokenClient();
			if(idSA!=null) {
				CredenzialeTokenClient credToken = new CredenzialeTokenClient(clientId, idSA);
				try {
					credenziale.setCredenziale(credToken.getCredenziale());
				}catch(Throwable t) {
					credenziale.setCredenziale(clientId);
				}
			}
			else {
				credenziale.setCredenziale(clientId);
			}
			credenzialiMittente.setTokenClientId(credenziale);
		}
		if(transazioneDB.getTokenUsernameLabel()!=null) {
			CredenzialeMittente credenziale = new CredenzialeMittente();
			credenziale.setCredenziale(transazioneDB.getTokenUsernameLabel());
			credenzialiMittente.setTokenUsername(credenziale);
		}
		if(transazioneDB.getTokenMailLabel()!=null) {
			CredenzialeMittente credenziale = new CredenzialeMittente();
			credenziale.setCredenziale(transazioneDB.getTokenMailLabel());
			credenzialiMittente.setTokenEMail(credenziale);
		}
		return credenzialiMittente;
	}
	
	public static Evento toEvento(EventoBean eventoDB, Logger log) throws Exception {
		Evento evento = new Evento();
		
		if(eventoDB.getSeverita()>=0) {
			TipoSeverita tipo = SeveritaConverter.toSeverita(eventoDB.getSeverita());
			evento.setSeverita(Enums.toDiagnosticoSeverita.get(tipo));
		}
		evento.setOrigine(eventoDB.getIdConfigurazione());
		if(eventoDB.getOraRegistrazione()!=null) {
			evento.setOraRegistrazione(new DateTime(eventoDB.getOraRegistrazione().getTime()));
		}
		
		List<BlackListElement> metodiEsclusi = new ArrayList<>();
		metodiEsclusi.add(new BlackListElement("setSeverita", DiagnosticoSeveritaEnum.class));
		metodiEsclusi.add(new BlackListElement("setOrigine", String.class));
		metodiEsclusi.add(new BlackListElement("setOraRegistrazione", DateTime.class));
		org.openspcoop2.utils.beans.BeanUtils.copy(log, evento, eventoDB, metodiEsclusi, true);
		return evento;
	}
	
	public static RiepilogoApiItem toRiepilogoApiItem(ConfigurazioneGenerale configurazioneDB, Logger log) throws Exception {
		RiepilogoApiItem riepilogo = new RiepilogoApiItem();
		riepilogo.setErogatore(configurazioneDB.getErogatore());
		riepilogo.setFruitore(configurazioneDB.getFruitore());
		
		String nomeAPI = configurazioneDB.getServizio();
		if(nomeAPI!=null) {
			String [] tmp = nomeAPI.split(" v");
			String tipoNome = tmp[0];
			if(tipoNome.contains("/")) {
				riepilogo.setTipo(tipoNome.split("/")[0]);
				riepilogo.setNome(tipoNome.split("/")[1]);
			}
			else {
				riepilogo.setNome(tipoNome);
			}
			String versione = tmp[1];
			riepilogo.setVersione(Integer.parseInt(versione));
		}
		
		return riepilogo;
	}
	
	public static Riepilogo toRiepilogo(List<ConfigurazioneGenerale> listDB_infoGenerali_right, List<ConfigurazioneGenerale> listDB_infoServizi_left , 
			ConfigurazioneGenerale soggettiOperativi, Logger log) throws Exception {
		Riepilogo riepilogo = new Riepilogo();
		
		if(listDB_infoGenerali_right!=null && !listDB_infoGenerali_right.isEmpty()) {
			for (ConfigurazioneGenerale configurazioneGenerale : listDB_infoGenerali_right) {
				if(CostantiConfigurazioni.CONF_SOGGETTI_ESTERNI_LABEL.equals(configurazioneGenerale.getLabel())) {
					riepilogo.setSoggettiDominioEsterno(Integer.valueOf(configurazioneGenerale.getValue()));
				}
				else if(CostantiConfigurazioni.CONF_SOGGETTI_OPERATIVI_LABEL.equals(configurazioneGenerale.getLabel())) {
					riepilogo.setSoggettiDominioInterno(Integer.valueOf(configurazioneGenerale.getValue()));
				}
				else if(CostantiConfigurazioni.CONF_SERVIZI_APPLICATIVI_LABEL.equals(configurazioneGenerale.getLabel())) {
					riepilogo.setApplicativi(Integer.valueOf(configurazioneGenerale.getValue()));
				}
			}
		}
		
		if(listDB_infoServizi_left!=null && !listDB_infoServizi_left.isEmpty()) {
			for (ConfigurazioneGenerale configurazioneGenerale : listDB_infoServizi_left) {
				if(CostantiConfigurazioni.CONF_ASPC_LABEL.equals(configurazioneGenerale.getLabel())) {
					riepilogo.setApi(Integer.valueOf(configurazioneGenerale.getValue()));
				}
				else if(CostantiConfigurazioni.CONF_ASPS_LABEL.equals(configurazioneGenerale.getLabel())) {
					riepilogo.setErogazioni(Integer.valueOf(configurazioneGenerale.getValue()));
				}
				else if(CostantiConfigurazioni.CONF_FRUIZIONI_SERVIZIO_LABEL.equals(configurazioneGenerale.getLabel())) {
					riepilogo.setFruizioni(Integer.valueOf(configurazioneGenerale.getValue()));
				}
			}
		}
		
		if(riepilogo.getSoggettiDominioInterno()==null) {
			// mutitenant disabilitato
			if(soggettiOperativi!=null) {
				riepilogo.setSoggettiDominioInterno(Integer.valueOf(soggettiOperativi.getValue()));
			}
		}
		
		return riepilogo;
	}
	
	public static InfoImplementazioneApi toInfoImplementazioneApi(List<ConfigurazioneGenerale> listDB_infoServizi_left , Logger log) throws Exception {
		InfoImplementazioneApi info = new InfoImplementazioneApi();
		
		if(listDB_infoServizi_left!=null && !listDB_infoServizi_left.isEmpty()) {
			for (ConfigurazioneGenerale configurazioneGenerale : listDB_infoServizi_left) {
				if(CostantiConfigurazioni.CONF_ASPS_LABEL.equals(configurazioneGenerale.getLabel())) {
					info.setErogazioni(Integer.valueOf(configurazioneGenerale.getValue()));
				}
				else if(CostantiConfigurazioni.CONF_FRUIZIONI_SERVIZIO_LABEL.equals(configurazioneGenerale.getLabel())) {
					info.setFruizioni(Integer.valueOf(configurazioneGenerale.getValue()));
				}
				else if(CostantiConfigurazioni.CONF_AZIONI_LABEL.equals(configurazioneGenerale.getLabel())) {
					info.setAzioni(Integer.valueOf(configurazioneGenerale.getValue()));
				}
			}
		}
		
		return info;
	}
	
	public static IDServizio toIDServizio(TransazioneRuoloEnum tipo, ProfiloEnum profilo, String soggetto, 
			String soggettoRemoto, String nomeServizio, String tipoServizio, Integer versioneServizio) throws Exception {
		String protocollo = Converter.toProtocollo(profilo);
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		String tipoSoggetto = protocolFactoryManager.getDefaultOrganizationTypes().get(protocollo);
		String tipoServizioEffettivo = tipoServizio !=null ? tipoServizio : protocolFactoryManager._getServiceTypes().get(protocollo).get(0);
		String nomeSoggettoLocale = soggetto;
		if(nomeSoggettoLocale==null) {
			ServerProperties serverProperties = ServerProperties.getInstance();
			if(serverProperties.useSoggettoDefault()) {
				nomeSoggettoLocale = serverProperties.getSoggettoDefaultIfEnabled(protocollo);
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Soggetto locale non indicato; parametro obbligatorio");
			}
		}
		if(!SoggettiConfig.existsIdentificativoPorta(tipoSoggetto, nomeSoggettoLocale)) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto locale indicato non esiste");
		}
		String nomeSoggettoErogatore = null;
		switch (tipo) {
		case EROGAZIONE:
			nomeSoggettoErogatore = nomeSoggettoLocale;
			break;
		case FRUIZIONE:
			if(StringUtils.isNotBlank(soggettoRemoto)) {
				nomeSoggettoErogatore = soggettoRemoto;
			}
			else {
				FaultCode.RICHIESTA_NON_VALIDA.throwException("Parametro 'soggettoRemoto' è obbligatorio per il dettaglio di una fruizione");
			}
			break;
		default:
			break;
		}
		IDServizio idServizio = 
				IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizioEffettivo, nomeServizio, tipoSoggetto, nomeSoggettoErogatore, 
						versioneServizio==null ? 1 : versioneServizio);
		return idServizio;
	}
	
	
	private static void fillBaseTracingPDND(BaseTracingPDND out, StatistichePdndTracingBean bean) {
		out.tentativiPubblicazione(bean.getTentativiPubblicazione())
				.statoPdnd(toStatoTracingPDND(bean.getStatoPdnd()))
				.stato(toStatoTracing(bean.getStato()))
				.dataRegistrazione(new DateTime(bean.getDataRegistrazione().getTime()))
				.dataTracciamento(new LocalDate(bean.getDataTracciamento().getTime()))
				.id(bean.getId());
		if (bean.getTracingId() != null)
			out.setTracingId(UUID.fromString(bean.getTracingId()));
	}
	
	private static void fillDetailsTracingPDND(DetailsTracingPDND out, StatistichePdndTracingBean bean) {
		fillBaseTracingPDND(out, bean);
		
		out.dettagliErrore(bean.getErrorDetails())
			.metodo(toMethodTracingPDND(bean.getMethod()))
			.dataPubblicazione(new DateTime(bean.getDataRegistrazione().getTime()));
		
		if (bean.isForcePublish())
			out.setForcePublish(true);
	}
	
	public static ListaTracingPDND toListaTracingPDND(IContext context, List<StatistichePdndTracingBean> listDB, int offset, int limit, int totalCount) throws InstantiationException, IllegalAccessException {
		ListaTracingPDND list = ListaUtils.costruisciListaPaginata(context.getUriInfo(),
				Converter.toOffset(offset), Converter.toLimit(limit), totalCount,
				ListaTracingPDND.class);
		
		List<ItemTracingPDND> items = new ArrayList<>();
		for (StatistichePdndTracingBean bean : listDB) {
			ItemTracingPDND item = new ItemTracingPDND();
			fillBaseTracingPDND(item, bean);
			
			items.add(item);
		}
		list.setItems(items);
		
		return list;
	}
	
	public static DetailsTracingPDND toDetailsTracingPDND(StatistichePdndTracingBean bean){
		DetailsTracingPDND details = new DetailsTracingPDND();
		fillDetailsTracingPDND(details, bean);
		return details;
	}
	
	public static StatoTracingPDND toStatoTracingPDND(PossibiliStatiPdnd state) {
		if (state == null)
			return null;
		switch (state) {
		case ERROR: return StatoTracingPDND.ERRORE;
		case OK: return StatoTracingPDND.OK;
		case PENDING: return StatoTracingPDND.ATTESA;
		case WAITING: return StatoTracingPDND.ATTESA;
		default: return null;
		}
	}
	
	public static StatoTracing toStatoTracing(PossibiliStatiRichieste state) {
		if (state == null)
			return null;
		switch (state) {
		case FAILED: return StatoTracing.FALLITA;
		case PUBLISHED: return StatoTracing.PUBBLICATA;
		default: return null;
		}
	}
	
	public static PossibiliStatiPdnd toStatoTracingPDND(StatoTracingPDND state) {
		if (state == null)
			return null;
		switch (state) {
		case ERRORE: return PossibiliStatiPdnd.ERROR;
		case OK: return PossibiliStatiPdnd.OK;
		case ATTESA: return PossibiliStatiPdnd.PENDING;
		default: return null;
		}
	}
	
	public static PossibiliStatiRichieste toStatoTracing(StatoTracing state) {
		if (state == null)
			return null;
		switch (state) {
		case FALLITA: return PossibiliStatiRichieste.FAILED;
		case PUBBLICATA: return PossibiliStatiRichieste.PUBLISHED;
		default: return null;
		}
		
	}
	
	public static MethodTracingPDND toMethodTracingPDND(PdndMethods method) {
		if (method == null)
			return null;
		switch (method) {
		case RECOVER: return MethodTracingPDND.RECOVER;
		case REPLACE: return MethodTracingPDND.REPLACE;
		case SUBMIT: return MethodTracingPDND.SUBMIT;
		default: return null;
		}
	}
}
