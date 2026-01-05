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
package org.openspcoop2.web.monitor.transazioni.dao;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCDumpMessaggioStream;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.datamodel.ResLive;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.transazioni.bean.ContenutiTransazioneArchivioBean;
import org.openspcoop2.web.monitor.transazioni.bean.DumpMessaggioBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneApplicativoServerArchivioBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneArchivioBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;
import org.openspcoop2.web.monitor.transazioni.exporter.SingleFileExporter;
import org.slf4j.Logger;

/**
 * ArchivioZipTransazioniService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ArchivioZipTransazioniService implements ITransazioniService{

	private TransazioniSearchForm searchForm;
	private Logger log = null;
	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;

	public ArchivioZipTransazioniService() {
		this.log = LoggerManager.getPddMonitorSqlLogger();
	}
	
	public void inizializzaInformazioniSupplementari() {
		if(!this.searchForm.getArchivioZipManager().isInizializzaInformazioniSupplementari()) { 
            
			Map<String,TransazioneArchivioBean> mapTransazioni = this.searchForm.getArchivioZipManager().getMapTransazioni();
			
			for (Entry<String, TransazioneArchivioBean> entry : mapTransazioni.entrySet()) {
				TransazioneArchivioBean transazioneArchivioBean = entry.getValue();
				TransazioneBean bean = transazioneArchivioBean.getTransazioneBean();
				
				bean.normalizeTipoApiInfo(this.getUtilsServiceManager(), this.log);
				bean.normalizeOperazioneInfo(this.getUtilsServiceManager(), this.log);
				
			}
			
			this.searchForm.getArchivioZipManager().setInizializzaInformazioniSupplementari(true);
		}
	}

	@Override
	public void setSearch(TransazioniSearchForm search) {
		this.searchForm = search;
	}

	@Override
	public TransazioniSearchForm getSearch() {
		return this.searchForm;
	}

	@Override
	public List<TransazioneBean> findAll(int start, int limit) {
		this.inizializzaInformazioniSupplementari();
		Map<String,TransazioneArchivioBean> mapTransazioni = this.searchForm.getArchivioZipManager().getMapTransazioni();
		try {
			return applicaFiltri(mapTransazioni).stream().skip(start).limit(limit).collect(Collectors.toList());
		} catch (ProtocolException | ServiceException e) {
			this.log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	@Override
	public int totalCount() {
		this.inizializzaInformazioniSupplementari();
		Map<String,TransazioneArchivioBean> mapTransazioni = this.searchForm.getArchivioZipManager().getMapTransazioni();
		try {
			return applicaFiltri(mapTransazioni).size();
		} catch (ProtocolException | ServiceException e) {
			this.log.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	public void store(TransazioneBean obj) throws Exception {
		// donothing
	}

	@Override
	public void deleteById(String key) {
		// donothing
	}

	@Override
	public void delete(TransazioneBean obj) throws Exception {
		// donothing
	}

	@Override
	public void deleteAll() throws Exception {
		// donothing		
	}

	@Override
	public TransazioneBean findById(String key) {
		this.log.debug("Find by id: {}", key);
		try {
			throw new NotImplementedException("Metodo Eliminato");

		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<TransazioneBean> findAll() {
		this.inizializzaInformazioniSupplementari();
		Map<String,TransazioneArchivioBean> mapTransazioni = this.searchForm.getArchivioZipManager().getMapTransazioni();
		try {
			return applicaFiltri(mapTransazioni);
		} catch (ProtocolException | ServiceException e) {
			this.log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	@Override
	public List<TransazioneBean> findAllLive() {
		return new ArrayList<>();
	}

	@Override
	public TransazioneBean findByIdTransazione(String id) throws Exception {
		this.inizializzaInformazioniSupplementari();
		TransazioneArchivioBean transazioneArchivioBean = getTransazioneFromMap(id);

		if(transazioneArchivioBean != null) {
			return transazioneArchivioBean.getTransazioneBean();
		}
		
		return null;
	}

	@Override
	public ResLive getEsiti(PermessiUtenteOperatore permessiUtente, Date min, Date max, String esitoContesto,
			String protocolloSelected, String protocolloDefault, TipologiaRicerca tipologiaRicerca) {
		return null;
	}

	@Override
	public ResLive getEsitiInfoLive(PermessiUtenteOperatore permessiUtente, Date lastDatePick,
			String protocolloSelected, String protocolloDefault) {
		return null;
	}

	@Override
	public boolean hasInfoDumpAvailable(String idTransazione, String saErogatore, Date dataConsegnaErogatore,
			TipoMessaggio tipoMessaggio) {
		TransazioneArchivioBean transazioneArchivioBean = getTransazioneFromMap(idTransazione);

		if(transazioneArchivioBean != null) {
			ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean = getContenuti(saErogatore, tipoMessaggio, transazioneArchivioBean);

			if(contenutiTransazioneArchivioBean != null) {
				return contenutiTransazioneArchivioBean.getMessage() != null 
						|| contenutiTransazioneArchivioBean.getAllegati().size() > 0 
						|| contenutiTransazioneArchivioBean.getContenuti().size() > 0 
						|| (contenutiTransazioneArchivioBean.getContentLength() != null && contenutiTransazioneArchivioBean.getContentLength() > 0);
			}
		}
		return false;
	}

	@Override
	public boolean hasInfoHeaderTrasportoAvailable(String idTransazione, String saErogatore, Date dataConsegnaErogatore,
			TipoMessaggio tipoMessaggio) {
		TransazioneArchivioBean transazioneArchivioBean = getTransazioneFromMap(idTransazione);

		if(transazioneArchivioBean != null) {
			ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean = getContenuti(saErogatore, tipoMessaggio, transazioneArchivioBean);

			if(contenutiTransazioneArchivioBean != null) {
				return contenutiTransazioneArchivioBean.getHeaders().size() > 0; 
			}
		}
		return false;
	}

	private ContenutiTransazioneArchivioBean getContenuti(String saErogatore, TipoMessaggio tipoMessaggio,
			TransazioneArchivioBean transazioneArchivioBean) {
		Map<TipoMessaggio, ContenutiTransazioneArchivioBean> contenuti = null;
		if(saErogatore != null) {
			TransazioneApplicativoServerArchivioBean consegnaArchivioBean = transazioneArchivioBean.getConsegne().get(saErogatore);
			contenuti = consegnaArchivioBean.getContenuti();
		} else {
			contenuti = transazioneArchivioBean.getContenuti();
		}

		return contenuti.get(tipoMessaggio);
	}

	private TransazioneArchivioBean getTransazioneFromMap(String idTransazione) {
		this.inizializzaInformazioniSupplementari();
		Map<String,TransazioneArchivioBean> mapTransazioni = this.searchForm.getArchivioZipManager().getMapTransazioni();
		return mapTransazioni.get(idTransazione);
	}

	@Override
	public String getContentTypeMessaggio(String idTransazione, String saErogatore, Date dataConsegnaErogatore,	TipoMessaggio tipoMessaggio) {
		TransazioneArchivioBean transazioneArchivioBean = getTransazioneFromMap(idTransazione);
		if(transazioneArchivioBean != null) {
			ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean = getContenuti(saErogatore, tipoMessaggio, transazioneArchivioBean);

			if(contenutiTransazioneArchivioBean != null) {
				return contenutiTransazioneArchivioBean.getContentType(); 
			}
		}
		return null;
	}

	@Override
	public Long getContentLengthMessaggio(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio) {
		TransazioneArchivioBean transazioneArchivioBean = getTransazioneFromMap(idTransazione);

		if(transazioneArchivioBean != null) {
			ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean = getContenuti(saErogatore, tipoMessaggio, transazioneArchivioBean);

			if(contenutiTransazioneArchivioBean != null) {
				return contenutiTransazioneArchivioBean.getContentLength(); 
			}
		}
		return null;
	}

	@Override
	public DumpMessaggio getDumpMessaggio(String idTransazione, String saErogatore, Date dataConsegnaErogatore,
			TipoMessaggio tipoMessaggio) throws Exception {
		TransazioneArchivioBean transazioneArchivioBean = getTransazioneFromMap(idTransazione);

		if(transazioneArchivioBean != null) {
			ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean = getContenuti(saErogatore, tipoMessaggio, transazioneArchivioBean);

			if(contenutiTransazioneArchivioBean != null) {
				DumpMessaggio dump = new DumpMessaggio();
				dump.setBody(contenutiTransazioneArchivioBean.getMessage());
				dump.setContentLength(contenutiTransazioneArchivioBean.getContentLength());
				dump.setContentType(contenutiTransazioneArchivioBean.getContentType());
				dump.setFormatoMessaggio(contenutiTransazioneArchivioBean.getMessageType());
				dump.setIdTransazione(contenutiTransazioneArchivioBean.getTransactionId());
				dump.setProtocollo(contenutiTransazioneArchivioBean.getProtocol());
				dump.setTipoMessaggio(tipoMessaggio);
				dump.setServizioApplicativoErogatore(saErogatore);
				
				return dump; 
			}
		}
		return null;
	}

	@Override
	public JDBCDumpMessaggioStream getContentInputStream(String idTransazione, String saErogatore,
			Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio) throws ServiceException {
		
		try {
			DumpMessaggio dumpMessaggio = this.getDumpMessaggio(idTransazione, saErogatore, dataConsegnaErogatore, tipoMessaggio);
			ByteArrayInputStream bais = new ByteArrayInputStream(dumpMessaggio.getBody());
			return new JDBCDumpMessaggioStream(bais, null, null, null, null);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public int countDumpMessaggiGByDataConsegnaErogatore(String idTransazione, String saErogatore) {
		TransazioneArchivioBean transazioneArchivioBean = getTransazioneFromMap(idTransazione);

		if(transazioneArchivioBean != null) {
			TransazioneApplicativoServerArchivioBean consegnaArchivioBean = transazioneArchivioBean.getConsegne().get(saErogatore);

			if(consegnaArchivioBean != null) {
				Map<String,Map<TipoMessaggio,ContenutiTransazioneArchivioBean>> storico = consegnaArchivioBean.getStorico();
				
				if(storico != null) {
					return storico.size();
				}
			}
		}
		
		return 0;
	}

	@Override
	public Date getDataConsegnaErogatore(String idTransazione, String saErogatore, Date dataAccettazione) {
		TransazioneArchivioBean transazioneArchivioBean = getTransazioneFromMap(idTransazione);

		if(transazioneArchivioBean != null) {
			TransazioneApplicativoServerArchivioBean consegnaArchivioBean = transazioneArchivioBean.getConsegne().get(saErogatore);

			if(consegnaArchivioBean != null) {
				Map<String,Map<TipoMessaggio,ContenutiTransazioneArchivioBean>> storico = consegnaArchivioBean.getStorico();
				
				if(storico != null) {
					Date dataConsegnaErogatore = null;
                        // Decodifica data
					for (String key : storico.keySet()) {
						Date dataConsegnaTmp = null;
						try {
							dataConsegnaTmp = DateUtils.getSimpleDateFormat(SingleFileExporter.EXPORT_DATE_FORMAT_MS).parse(key);
						} catch (ParseException e) {
							// la data e' un millis
							dataConsegnaTmp = new Date(Long.parseLong(key));
						}
						
						// cerco la data piu' recente
						if (dataConsegnaErogatore == null || dataConsegnaTmp.after(dataConsegnaErogatore)) {
							dataConsegnaErogatore = dataConsegnaTmp;
						}
					}
					
					return dataConsegnaErogatore;
				}
			}
		}
		
		return null;
	}

	@Override
	public List<DumpMessaggioBean> listDumpMessaggiGByDataConsegnaErogatore(String idTransazione, String saErogatore, int start, int limit) {
		TransazioneArchivioBean transazioneArchivioBean = getTransazioneFromMap(idTransazione);

		if(transazioneArchivioBean != null) {
			TransazioneApplicativoServerArchivioBean consegnaArchivioBean = transazioneArchivioBean.getConsegne().get(saErogatore);

			if(consegnaArchivioBean != null) {
				Map<String,Map<TipoMessaggio,ContenutiTransazioneArchivioBean>> storico = consegnaArchivioBean.getStorico();
				
				if(storico != null) {
					List<DumpMessaggioBean> dumpMessaggi = new ArrayList<>();
					
					for (Map.Entry<String, Map<TipoMessaggio, ContenutiTransazioneArchivioBean>> entry : storico.entrySet()) {
						String key = entry.getKey();
						Map<TipoMessaggio, ContenutiTransazioneArchivioBean> val = entry.getValue();
						
						DumpMessaggioBean dumpMessaggioBean = new DumpMessaggioBean();
						dumpMessaggioBean.setIdTransazione(idTransazione);
						dumpMessaggioBean.setServizioApplicativoErogatore(saErogatore);
						// Decodifica data
						try {
							dumpMessaggioBean.setDataConsegnaErogatore(DateUtils.getSimpleDateFormat(SingleFileExporter.EXPORT_DATE_FORMAT_MS).parse(key));
						} catch (ParseException e) {
							// la data e' un millis
							dumpMessaggioBean.setDataConsegnaErogatore(new Date(Long.parseLong(key)));
						}
						
						for (Map.Entry<TipoMessaggio, ContenutiTransazioneArchivioBean> entry2 : val.entrySet()) {
							TipoMessaggio tipoMessaggio = entry2.getKey();
							dumpMessaggioBean.getTipiMessaggio().add(tipoMessaggio);
						}
						dumpMessaggi.add(dumpMessaggioBean);
					}
					// paginazione e ordinamento
					return dumpMessaggi.stream().sorted((a, b) -> b.getDataConsegnaErogatore().compareTo(a.getDataConsegnaErogatore())).skip(start).limit(limit).collect(Collectors.toList());
				}
			}
		}
		
		return new ArrayList<>();
	}

	@Override
	public List<DumpAllegato> getAllegatiMessaggio(String idTransazione, String saErogatore, Date dataConsegnaErogatore,
			TipoMessaggio tipoMessaggio, Long idDump) {

		TransazioneArchivioBean transazioneArchivioBean = getTransazioneFromMap(idTransazione);

		if(transazioneArchivioBean != null) {
			ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean = getContenuti(saErogatore, tipoMessaggio, transazioneArchivioBean);

			if(contenutiTransazioneArchivioBean != null) {
				return contenutiTransazioneArchivioBean.getAllegati().values().stream().collect(Collectors.toList());
			}
		}
		
		return new ArrayList<>();
	}

	@Override
	public List<DumpContenuto> getContenutiSpecifici(String idTransazione, String saErogatore,
			Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio, Long idDump) {
		TransazioneArchivioBean transazioneArchivioBean = getTransazioneFromMap(idTransazione);

		if(transazioneArchivioBean != null) {
			ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean = getContenuti(saErogatore, tipoMessaggio, transazioneArchivioBean);

			if(contenutiTransazioneArchivioBean != null) {
				return contenutiTransazioneArchivioBean.getContenuti();
			}
		}
		
		return new ArrayList<>();
	}

	@Override
	public List<DumpHeaderTrasporto> getHeaderTrasporto(String idTransazione, String saErogatore,
			Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio, Long idDump) {
		TransazioneArchivioBean transazioneArchivioBean = getTransazioneFromMap(idTransazione);

		if(transazioneArchivioBean != null) {
			ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean = getContenuti(saErogatore, tipoMessaggio, transazioneArchivioBean);

			if(contenutiTransazioneArchivioBean != null) {
				return contenutiTransazioneArchivioBean.getHeaders();
			}
		}
		
		return new ArrayList<>();
	}

	@Override
	public List<TransazioneBean> findAllDuplicati(String idTransazione, String idEgov, boolean isRisposta, int start, int limit) {
		return new ArrayList<>();
	}

	@Override
	public int countAllDuplicati(String idTransazione, String idEgov, boolean isRisposta) {
		return 0;
	}

	@Override
	public TransazioneBean findTransazioneOriginale(String idTransazioneDuplicata, String idEgov, boolean isRisposta) {
		return null;
	}

	@Override
	public List<ConfigurazioneRicerca> getRicercheByValues(IDAccordo idAccordo, String nomeServizio,
			String nomeAzione) {
		return new ArrayList<>();
	}

	@Override
	public List<Parameter<?>> instanceParameters(ConfigurazioneRicerca configurazioneRicerca, Context context)
			throws SearchException {
		return new ArrayList<>();
	}

	@Override
	public List<ConfigurazioneTransazioneStato> getStatiByValues(IDAccordo idAccordo, String nomeServizio, String nomeAzione) {
		return new ArrayList<>();
	}

	@Override
	public List<ConfigurazioneTransazioneRisorsaContenuto> getRisorseContenutoByValues(IDAccordo idAccordo,	String nomeServizio, String nomeAzione, String nomeStato) {
		return new ArrayList<>();
	}

	@Override
	public List<TransazioneBean> findAll(int start, int limit, SortOrder sortOrder) {
		return findAll(start, limit, sortOrder, null);
	}

	@Override
	public int totalCount(SortOrder sortOrder, String sortField) {
		this.inizializzaInformazioniSupplementari();
		Map<String,TransazioneArchivioBean> mapTransazioni = this.searchForm.getArchivioZipManager().getMapTransazioni();
		try {
			return applicaFiltri(mapTransazioni).size();
		} catch (ProtocolException | ServiceException e) {
			this.log.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	public List<TransazioneBean> findAll(int start, int limit, SortOrder sortOrder, String sortField) {
		this.inizializzaInformazioniSupplementari();
		Map<String,TransazioneArchivioBean> mapTransazioni = this.searchForm.getArchivioZipManager().getMapTransazioni();
		try {
			return applicaFiltri(mapTransazioni).stream().skip(start).limit(limit).collect(Collectors.toList());
		} catch (ProtocolException | ServiceException e) {
			this.log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	@Override
	public void setLiveMaxResults(Integer limit) {
		// donothing
	}

	@Override
	public ITransazioniApplicativoServerService getTransazioniApplicativoServerService() {
		return null;
	}

	@Override
	public List<String> getHostnames(String gruppo, int refreshSecondsInterval) {
		return new ArrayList<>();
	}

	@Override
	public List<String> getClusterIdDinamici(String gruppo, int refreshSecondsInterval) {
		return new ArrayList<>();
	}

	@Override
	public boolean isTimeoutEvent() {
		return false;
	}

	@Override
	public boolean isProfiloDifferenteEvent() {
		return false;
	}

	@Override
	public boolean isSoggettoDifferenteEvent() {
		return false;
	}
	
	private List<TransazioneBean> applicaFiltri(Map<String,TransazioneArchivioBean> mapTransazioni ) throws ProtocolException, ServiceException {
		List<TransazioneBean> listaTransazioni = mapTransazioni.values().stream().map(a -> a.getTransazioneBean()).collect(Collectors.toList());
		
		List<TransazioneBean> toReturn = new ArrayList<>(); 
		
		Integer esitoGruppo = this.searchForm.getEsitoGruppo();
		Integer esitoDettaglio = this.searchForm.getEsitoDettaglio();
		Integer[] esitoDettaglioPersonalizzato = this.searchForm.getEsitoDettaglioPersonalizzato();
		boolean escludiRichiesteScartate = this.searchForm.isEscludiRichiesteScartate();
		
		boolean senzaFiltro = 
				(esitoGruppo!=null && EsitoUtils.ALL_VALUE.intValue() == esitoGruppo.intValue()) 
				&& 
				(esitoDettaglio!=null && EsitoUtils.ALL_VALUE.intValue() == esitoDettaglio.intValue());
		boolean soloOk = 
				(esitoGruppo!=null && EsitoUtils.ALL_OK_VALUE.intValue() == esitoGruppo.intValue()) 
				&& 
				(esitoDettaglio!=null && EsitoUtils.ALL_VALUE.intValue() == esitoDettaglio.intValue());
		boolean faultApplicativo = 
				(esitoGruppo!=null && EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE.intValue() == esitoGruppo.intValue());
		boolean soloErrori = 
				(esitoGruppo!=null && EsitoUtils.ALL_ERROR_VALUE.intValue() == esitoGruppo.intValue()) 
				&& 
				(esitoDettaglio!=null && EsitoUtils.ALL_VALUE.intValue() == esitoDettaglio.intValue());
		boolean personalizzato = 
				(esitoGruppo!=null && EsitoUtils.ALL_PERSONALIZZATO_VALUE.intValue() == esitoGruppo.intValue());
		boolean soloErroriPiuFaultApplicativi = 
				(esitoGruppo!=null && EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE.intValue() == esitoGruppo.intValue()) 
				&& 
				(esitoDettaglio!=null && EsitoUtils.ALL_VALUE.intValue() == esitoDettaglio.intValue());
		boolean soloErroriConsegna = 
				(esitoGruppo!=null && EsitoUtils.ALL_ERROR_CONSEGNA_VALUE.intValue() == esitoGruppo.intValue()) 
				&& 
				(esitoDettaglio!=null && EsitoUtils.ALL_VALUE.intValue() == esitoDettaglio.intValue());
		boolean soloRichiesteScartate = 
				(esitoGruppo!=null && EsitoUtils.ALL_ERROR_RICHIESTE_SCARTATE_VALUE.intValue() == esitoGruppo.intValue()) 
				&& 
				(esitoDettaglio!=null && EsitoUtils.ALL_VALUE.intValue() == esitoDettaglio.intValue());
		
		if(senzaFiltro && escludiRichiesteScartate) {
			senzaFiltro = false;
		}
		
		EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(this.log,this.searchForm.getSafeProtocol());
		
		String evento = estraiValoreFiltroEvento();
		List<String> listaCodiciRispostaToCheck = estraiValoreCodicRisposta();
		
		for (TransazioneBean transazioneBean : listaTransazioni) {
			// Esito Contesto
			if(StringUtils.isNotBlank(this.searchForm.getEsitoContesto()) 
					&& !EsitoUtils.ALL_VALUE_AS_STRING.equals(this.searchForm.getEsitoContesto()) 
					&& !transazioneBean.getEsitoContesto().equalsIgnoreCase(this.searchForm.getEsitoContesto())) {
				continue;
			}
			
			// Esito Gruppo
			// Esito Dettaglio
			// Esito Dettaglio Personalizzato
			// Escludi richieste scartate			
			if(!senzaFiltro){
				
				if(personalizzato){
					if(esitoDettaglioPersonalizzato==null || esitoDettaglioPersonalizzato.length<=0){
						throw new ServiceException("Esito Personalizzato richiede la selezione di almeno un dettaglio");
					}
					
					boolean checkPersonalizzato = false;
					
					for (int i = 0; i < esitoDettaglioPersonalizzato.length; i++) {
						if(esitoDettaglioPersonalizzato[i].equals(transazioneBean.getEsito())) {
							checkPersonalizzato = true;
							break;
						}
					}
					
					if(!checkPersonalizzato) {
						continue;
					}
				}
				else if(soloOk){
					List<Integer> esitiOk = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
					
					if(!esitiOk.contains(transazioneBean.getEsito())) {
						continue;
					}
				}
				else if(faultApplicativo){
					int codeFaultApplicativo = esitiProperties.convertNameToCode(EsitoTransazioneName.ERRORE_APPLICATIVO.name());
					
					if(codeFaultApplicativo != transazioneBean.getEsito()) {
						continue;
					}
				}
				else if(soloErrori || soloErroriPiuFaultApplicativi){
					
					List<Integer> esitiOk = null;
					if(soloErrori) {
						esitiOk = esitiProperties.getEsitiCodeOk(); // li prendo tutti anche il fault, poichè faccio il not
					}
					else {
						esitiOk = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
					}
					
					// qui al contrario se e' un esito OK devo scartare la transazione
					if(esitiOk.contains(transazioneBean.getEsito())) {
						continue;
					}
					
					if(escludiRichiesteScartate) {
						List<Integer> esitiRichiesteMalformate = esitiProperties.getEsitiCodeRichiestaScartate();
						
						// qui al contrario se e' un esito di transazione malformata devo scartare la transazione
						if(esitiRichiesteMalformate.contains(transazioneBean.getEsito())) {
							continue;
						}
					}
				}
				else if(soloErroriConsegna) {
					List<Integer> esitiErroriConsegna = esitiProperties.getEsitiCodeErroriConsegna();
					
					if(!esitiErroriConsegna.contains(transazioneBean.getEsito())) {
						continue;
					}
				}
				else if(soloRichiesteScartate) {
					List<Integer> esitiRichiesteMalformate = esitiProperties.getEsitiCodeRichiestaScartate();
					
					if(!esitiRichiesteMalformate.contains(transazioneBean.getEsito())) {
						continue;
					}
				}
				else{
					if(esitoDettaglio!=null && (esitoDettaglio.intValue() == EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE.intValue())){
						// si tratta del fault, devo trasformarlo nel codice ufficiale
						// Questo caso avviene quando si seleziona qualsiasi esito, e poi come dettaglio il fault
						int codeFaultApplicativo = esitiProperties.convertNameToCode(EsitoTransazioneName.ERRORE_APPLICATIVO.name());
						if(codeFaultApplicativo != transazioneBean.getEsito()) {
							continue;
						}
					}
					else if(esitoDettaglio!=null && esitoDettaglio>=0){
						if(esitoDettaglio.intValue() != transazioneBean.getEsito()) {
							continue;
						}
					}
					else if(escludiRichiesteScartate) {
						List<Integer> esitiRichiesteMalformate = esitiProperties.getEsitiCodeRichiestaScartate();
						
						// qui al contrario se e' un esito di transazione malformata devo scartare la transazione
						if(esitiRichiesteMalformate.contains(transazioneBean.getEsito())) {
							continue;
						}
					}
				}
				
			}
			
			// Codice Risposta
			if(!listaCodiciRispostaToCheck.isEmpty()) {
				boolean codiceFound = false;
				for (String codice : listaCodiciRispostaToCheck) {
					for (int i = 0; i < 2; i++) {
					
						String prefix = (i==0) ? CostantiPdD.PREFIX_HTTP_STATUS_CODE_OUT : CostantiPdD.PREFIX_HTTP_STATUS_CODE_IN;
						String searchCodice = prefix+codice;
						
						if(StringUtils.isNotBlank(transazioneBean.getEventiLabel()) && transazioneBean.getEventiLabel().contains(searchCodice)) {
							codiceFound = true;
							break;
						}
					}
				}
				
				if(!codiceFound) {
					continue;
				}
			}
			
			// Evento
			if(evento!=null) {
				// permetto di usare api=rest invece di api=1
				evento = evento.trim();
				List<String> eventiGestioneAsList = transazioneBean.getEventiGestioneAsList();
				
				if(!eventiGestioneAsList.isEmpty()) {
					boolean eventoFound = false;
					
					for (String eventoGestione : eventiGestioneAsList) {
						if(eventoGestione.toLowerCase().contains(evento.toLowerCase())) {
							eventoFound = true;
							break;
						}
					}
					
					if(!eventoFound) {
						continue;
					}
				}
			}

			toReturn.add(transazioneBean);
		}
		
		// Ordinamento per data ingresso richiesta DESC
		return toReturn.stream().sorted((a,b)->b.getDataIngressoRichiesta().compareTo(a.getDataIngressoRichiesta())).collect(Collectors.toUnmodifiableList());
		
	}

	private List<String> estraiValoreCodicRisposta() {
		List<String> listaCodiciRispostaToCheck = new ArrayList<>();
		if (StringUtils.isNotEmpty(this.searchForm.getCodiceRisposta())) {
			if(this.searchForm.getCodiceRisposta().contains(",")) {
				String [] tmp = this.searchForm.getCodiceRisposta().split(",");
				if(tmp!=null && tmp.length>0) {
					for (String v : tmp) {
						listaCodiciRispostaToCheck.add(v.trim());
					}
				}
				else {
					listaCodiciRispostaToCheck.add(this.searchForm.getCodiceRisposta());
				}
			}
			else {
				listaCodiciRispostaToCheck.add(this.searchForm.getCodiceRisposta());
			}
		}
		return listaCodiciRispostaToCheck;
	}

	private String estraiValoreFiltroEvento() {
		String evento = this.searchForm.getEvento();
		if (StringUtils.isNotEmpty(evento)) {
			
			// permetto di usare api=rest invece di api=1
			evento = evento.trim();
			if(evento.toLowerCase().startsWith(CostantiPdD.PREFIX_API.toLowerCase()) && evento.length()>CostantiPdD.PREFIX_API.length()) {
				try {
					String sub = evento.substring(CostantiPdD.PREFIX_API.length());
					if("rest".equalsIgnoreCase(sub)) {
						evento = CostantiPdD.PREFIX_API+TipoAPI.REST.getValoreAsInt();
					}
					else if("soap".equalsIgnoreCase(sub)) {
						evento = CostantiPdD.PREFIX_API+TipoAPI.SOAP.getValoreAsInt();
					}
				}catch(IndexOutOfBoundsException t) {
					//donothing
				}
			}
		}
		return evento;
	}

	public org.openspcoop2.core.commons.search.dao.IServiceManager getUtilsServiceManager() {
		return this.utilsServiceManager;
	}

	public void setUtilsServiceManager(org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager) {
		this.utilsServiceManager = utilsServiceManager;
	}
}
