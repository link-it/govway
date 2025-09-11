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
package org.openspcoop2.monitor.engine.statistic;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.statistiche.StatistichePdndTracing;
import org.openspcoop2.core.statistiche.constants.PdndMethods;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiPdnd;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCCredenzialeMittenteServiceSearch;
import org.openspcoop2.core.transazioni.utils.credenziali.AbstractCredenzialeList;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.csv.Format;
import org.openspcoop2.utils.csv.Printer;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;

/**
 * PdndGenerazioneTracciamento
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PdndGenerazioneTracciamento implements IStatisticsEngine {
	private static final String PDND_DATE_FORMAT = "yyyy-MM-dd";
	private static final String REQUESTS_COUNT_ID = "request_count";
	
	
	PdndGenerazioneTracciamento(){
			super();
	}
	
	private StatisticsConfig config;
	private org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM;
	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM;
	private Map<String, Integer> eventsToCode;
	private Map<String, String> internalPddCodes;
	private Logger logger;
	
	@Override
	public void init(StatisticsConfig config,
			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM,
			org.openspcoop2.core.plugins.dao.IServiceManager pluginsBaseSM,
			org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM,
			org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM) {
		this.config = config;
		this.statisticheSM = statisticheSM;
		this.transazioniSM = transazioniSM;
		
		this.logger = config.getLogCore();
		
		this.eventsToCode = new HashMap<>();
		this.internalPddCodes = new HashMap<>();
		
		try {
			this.internalPddCodes = PdndTracciamentoUtils.getEnabledPddCodes(utilsSM, config);
		} catch ( ExpressionException 
				| ServiceException 
				| NotImplementedException 
				| NotFoundException 
			    | SQLQueryObjectException 
			    | StatisticsEngineException e) {
			this.logger.error("Impossibile inizializzare la classe PdndGenerazioneTracciamento", e);
		}
		
	}
	
	private Date truncDate(Date date) {
		Calendar cTmp = Calendar.getInstance();
		cTmp.setTime(date);
		cTmp.set(Calendar.HOUR_OF_DAY, 0);
		cTmp.set(Calendar.MINUTE, 0);
		cTmp.set(Calendar.SECOND, 0);
		cTmp.set(Calendar.MILLISECOND, 0);
		return cTmp.getTime();
	}
	
	private Date incrementDate(Date date) {
		Calendar cTmp = Calendar.getInstance();
		cTmp.setTime(date);
		cTmp.set(Calendar.DAY_OF_YEAR, cTmp.get(Calendar.DAY_OF_YEAR) + 1);
		return cTmp.getTime();
	}
	
	private Integer convertEvent(String event) throws UtilsException {
		try {
			Integer cached = this.eventsToCode.get(event);
			if (cached != null)
				return cached;
			CredenzialeMittente credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.transazioniSM.getCredenzialeMittenteService()).get(Long.valueOf(event));
			String cred = AbstractCredenzialeList.normalize(credenzialeMittente.getCredenziale());
			String rawCode = RegularExpressionEngine.getStringFindPattern(cred, "Out=(\\d+)");
			Integer code = Integer.valueOf(rawCode);
			this.eventsToCode.put(event, code);
			return code;
		} catch (Exception e) {
			throw new UtilsException("Errore nella risoluzione del campo eventi_gestione della transazione, id={"+event+"}: "+e.getMessage(),e);
		}
	}
	
	
	private static final String[] CSV_HEADERS = {"date", "purpose_id", "status", "token_id", "requests_count"};
	public byte[] generateCsv(Date tracingDate, List<Map<String, Object>> data) throws UtilsException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
		        .setHeader(CSV_HEADERS)
		        .build();
		
		Format format = new Format();
		format.setSkipEmptyRecord(false);
		format.setCsvFormat(csvFormat);
		
		Printer printer = new Printer(format, os);
		
		for (Map<String, Object> row : data) {
			String purposeId = row.get(Transazione.model().TOKEN_PURPOSE_ID.getFieldName()).toString();
			String tokenId = row.get(Transazione.model().TOKEN_ID.getFieldName()).toString();
			String statusId =  row.get(Transazione.model().EVENTI_GESTIONE.getFieldName()).toString();
			Integer status = convertEvent(statusId);
			String requestsCount = row.get(REQUESTS_COUNT_ID).toString();
			
			printer.printRecord(dataTracciamentoFormat(tracingDate), purposeId, status, tokenId, requestsCount);
			printer.printComment("commento");
		}
		
		printer.close();
		
		return os.toByteArray();
	}
	
	public boolean createRecord(Date tracingDate, String pddCode, List<Map<String, Object>> data) {
		
		byte[] csv = null;
		try {	
			csv = generateCsv(tracingDate, data);
		} catch (UtilsException e) {
			String dataTracciamento = dataTracciamentoFormat(tracingDate);
			this.logger.error("Errore nel generare il csv per il soggetto: {}, data tracciamento: {}", pddCode, dataTracciamento, e);
			return false;
		}
		
		Date endTraceDate = truncDate(incrementDate(tracingDate));
		
		StatistichePdndTracing entry = new StatistichePdndTracing();
		Date now = DateManager.getDate();
		entry.setDataRegistrazione(now);
		entry.setDataTracciamento(tracingDate);
		entry.setCsv(csv);
		entry.setPddCodice(pddCode);
		entry.setHistory(0);
		entry.setMethod(endTraceDate.before(truncDate(now)) ? PdndMethods.RECOVER : PdndMethods.SUBMIT);
		entry.setStatoPdnd(PossibiliStatiPdnd.WAITING);

		
		try {
			this.statisticheSM.getStatistichePdndTracingService().create(entry);
		} catch (ServiceException | NotImplementedException e) {
			String dataTracciamento = dataTracciamentoFormat(tracingDate);
			this.logger.error("Errore nel generare il csv per il soggetto: {}, data tracciamento: {}", pddCode, dataTracciamento, e);
			return false;
		}
		
		return true;

	}
	
	private List<Map<String, Object>> aggregateTransactions(Date start, Date end) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		return aggregateTransactions(start, end, null);
	}	
	
	private List<Map<String, Object>> aggregateTransactions(Date start, Date end, String pddCode) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		IExpression expr = this.transazioniSM.getTransazioneService().newExpression();
		expr.addGroupBy(Transazione.model().PDD_CODICE)
			.addGroupBy(Transazione.model().TOKEN_PURPOSE_ID)
			.addGroupBy(Transazione.model().TOKEN_ID)
			.addGroupBy(Transazione.model().EVENTI_GESTIONE)
			.and()
			.between(Transazione.model().DATA_INGRESSO_RICHIESTA, start, end)
			.isNotNull(Transazione.model().PDD_CODICE)
			.isNotNull(Transazione.model().TOKEN_PURPOSE_ID)
			.isNotNull(Transazione.model().TOKEN_ID)
			.isNotNull(Transazione.model().EVENTI_GESTIONE)
			.equals(Transazione.model().PROTOCOLLO, CostantiLabel.MODIPA_PROTOCOL_NAME);
		
		if (!this.config.isPdndTracciamentoFruizioniEnabled())
			expr.notEquals(Transazione.model().PDD_RUOLO, PddRuolo.DELEGATA);
		if (!this.config.isPdndTracciamentoErogazioniEnabled())
			expr.notEquals(Transazione.model().PDD_RUOLO, PddRuolo.APPLICATIVA);
		
		if (pddCode != null)
			expr.equals(Transazione.model().PDD_CODICE, pddCode);
		
		FunctionField countFunction = new FunctionField(Transazione.model().ID_TRANSAZIONE, Function.COUNT, REQUESTS_COUNT_ID);
		
		try {
			return this.transazioniSM
				.getTransazioneService()
				.groupBy(expr, countFunction);
		} catch (NotFoundException e) {
			return List.of();
		}
	}
	
	private boolean createRecords(Date start, Date end, Set<String> ignorePdd) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		List<Map<String, Object>> groups = aggregateTransactions(start, end);
		String dataTracciamento = dataTracciamentoFormat(start);
		boolean errors = false;
		
		Map<String, List<Map<String, Object>>> pddCodeGroup = groups
				.stream()
				.collect(Collectors.groupingBy(row -> (String)row.get(Transazione.model().PDD_CODICE.getFieldName())));
		
		for (Map.Entry<String, String> soggettoEntry : this.internalPddCodes.entrySet()) {
			String pddCode = soggettoEntry.getKey();
			if (ignorePdd.contains(pddCode)) {
				this.logger.info("Tracciato [{}] già presente per il soggetto: {}, non genero",
						dataTracciamento,
						soggettoEntry.getValue());
				continue;
			}
			
			List<Map<String, Object>> rows = Objects.requireNonNullElse(
					pddCodeGroup.get(pddCode),
					List.of()
			);
			
			if(createRecord(start, pddCode, rows)) {
				this.logger.info("Tracciato [{}] generato correttamente per il soggetto: {}",
						dataTracciamento,
						soggettoEntry.getValue());
			}
			else {
				this.logger.info("Tracciato [{}] non generato per il soggetto: {}",
						dataTracciamento,
						soggettoEntry.getValue());
				errors = true;
			}
		}
		
		
		return !errors;
	}
	
	public void scanNull() throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		
		IPaginatedExpression expr = this.statisticheSM.getStatistichePdndTracingService().newPaginatedExpression();
		expr.isNull(StatistichePdndTracing.model().CSV);
		
		this.logger.info("Cerco tracciati vuoti ...");
		
		List<StatistichePdndTracing> stats = null;
		try {
			stats = this.statisticheSM.getStatistichePdndTracingService().findAll(expr);
		} catch (Exception e) {
			if (e.getCause() instanceof NotFoundException || e instanceof NotFoundException) {
				this.logger.info("Non sono stati trovati tracciati vuoti");
				return;
			}
			else {
				throw new ServiceException("Non sono stati trovati tracciati vuoti per via di un'anomalia: "+e.getMessage(),e);
			}
		}
		
		this.logger.info("Sono stati trovati {} tracciati vuoti, procedo a valorizzarli", stats.size());
		
		for (StatistichePdndTracing stat : stats) {
			String nomeSoggetto = this.internalPddCodes.get(stat.getPddCodice());
			String dataTracciamento = dataTracciamentoFormat(stat.getDataTracciamento());
			Date startTracing = stat.getDataTracciamento();
			Date endTracing = truncDate(incrementDate(startTracing));
			
			this.logger.info("Tracciato [{}] del soggetto: {} vuoto, valorizzazione in corso ...", 
					dataTracciamento, 
					nomeSoggetto);
			
			List<Map<String, Object>> data;
			try {
				data = aggregateTransactions(startTracing, endTracing, stat.getPddCodice());
				stat.setCsv(generateCsv(startTracing, data));
				this.statisticheSM.getStatistichePdndTracingService().update(stat);
				this.logger.info("Tracciato [{}] del soggetto: {} valorizzato correttamente", 
						dataTracciamento, 
						nomeSoggetto);
			} catch (Exception e) {
				this.logger.error("Errore nell'update della statistica con csv null, tracingDate: {}, codice pdd: {}", 
						dataTracciamentoFormat(startTracing), 
						stat.getPddCodice(), e);
			}
		}
		
		
		
	}
	
	
	private Map<Date, Set<String>> getAlreadyExistsPddRecords(Date lastDate) throws NotImplementedException, ServiceException {
		Map<Date, Set<String>> ignorePddCodes = null;
		
		try {
			
			// ottengo tutti i record presenti a partire dalla data della prima generazione
			IPaginatedExpression expr = this.statisticheSM.getStatistichePdndTracingService().newPaginatedExpression();
			expr.greaterEquals(StatistichePdndTracing.model().DATA_TRACCIAMENTO, lastDate);
			expr.equals(StatistichePdndTracing.model().HISTORY, 0);
			
			// creo la mappa per capire quali record non devo generare
			List<StatistichePdndTracing> stats = this.statisticheSM.getStatistichePdndTracingService().findAll(expr);
			ignorePddCodes = stats.stream()
					.collect(Collectors.toMap(
							StatistichePdndTracing::getDataTracciamento, 
							o -> {Set<String> baseSet = new HashSet<>(); baseSet.add(o.getPddCodice()); return baseSet; }, 
							(o1, o2) -> { o1.addAll(o2); return o1; }
					));
		
		} catch (Exception e) {
			// nessun record trovato allora la mappa risulta vuota altrimenti rilancio l'eccezione, errore fatale
			if (e.getCause() instanceof NotFoundException || e instanceof NotFoundException)
				ignorePddCodes = Map.of();
			else
				throw new ServiceException("Impossibile ottenere record tracciati PDND già presenti", e);
		}
		
		if (!ignorePddCodes.isEmpty()) {
			this.logger.warn("Attenzione alcuni tracciati sono gia presenti nel db per questo intervallo temporale, non li rigenero: {}", 
					ignorePddCodes);
		}
		
		return ignorePddCodes;
	}
	
	private Date getLastTracingDate(Date currDate) throws ServiceException {
		Date lastDate = null;
		
		try {
			lastDate = StatisticsInfoUtils.readDataUltimaGenerazioneStatistiche(this.statisticheSM.getStatisticaInfoServiceSearch(), TipoIntervalloStatistico.PDND_GENERAZIONE_TRACCIAMENTO,
					this.config.getLogSql());
		} catch (NotFoundException e) {
			lastDate = new Date(0);
		} catch (NotImplementedException | ServiceException e) {
			throw new ServiceException(e);
		}
		
		// se non e' presente l'ultima statistica iniziero da domani in quanto oggi non avrei i dati completi
		if (lastDate.equals(new Date(0)))
			lastDate = incrementDate(currDate);
		return truncDate(lastDate);
	}
	
	public void scanDates() throws NotImplementedException, ServiceException {
		Date nextDate = null;
		Date lastNoErrorDate = null;
		
		int delayMinutes = this.config.getPdndTracciamentoGenerazioneDelayMinutes();
		Date now = DateManager.getDate();
		if(delayMinutes>0) {
			now = new Date(now.getTime()-(delayMinutes*60*1000));
			if (this.logger.isInfoEnabled())
				this.logger.info("Attivo offset delay '{}', nuova 'now date' {}", 
						delayMinutes, DateUtils.getSimpleDateFormatMs().format(now));
		}
		
		Date currDate = truncDate(now);
		Date lastDate = getLastTracingDate(currDate);

		if (this.logger.isInfoEnabled())
			this.logger.info("Verifico esistenza csv già generati per la data {}", 
					dataTracciamentoFormat(lastDate));
		
		// mappa che contiene per ogni data quali record per uno specifico codice pdd sono presenti per non aggiungerli
		Map<Date, Set<String>> ignorePddCodes = getAlreadyExistsPddRecords(lastDate);
		
		if (this.logger.isInfoEnabled())
			this.logger.info("Intervallo di generazione PDND tracciamento {} -> {}", 
					dataTracciamentoFormat(lastDate),
					dataTracciamentoFormat(currDate));
		
		for (; lastDate.before(currDate); lastDate = nextDate) {
			nextDate = truncDate(incrementDate(lastDate));
						
			try {
				if (!createRecords(lastDate, nextDate, ignorePddCodes.getOrDefault(lastDate, Set.of())) && lastNoErrorDate == null)
					lastNoErrorDate = lastDate;
			} catch (ServiceException 
					| NotImplementedException 
					| ExpressionNotImplementedException 
					| ExpressionException e) {
				this.logger.error("Errore nella generazione dei tracciamenti PDND per il range [{},{}(", 
						dataTracciamentoFormat(lastDate), 
						dataTracciamentoFormat(nextDate), 
						e);
				if (lastNoErrorDate == null)
					lastNoErrorDate = lastDate;
			}
		}
		
		scanDatesUpdateDataUltimaGenerazione(lastNoErrorDate, currDate);
	}
	private void scanDatesUpdateDataUltimaGenerazione(Date lastNoErrorDate,Date currDate) throws NotImplementedException, ServiceException {
		if (lastNoErrorDate == null) {
			if (this.logger.isInfoEnabled())
				this.logger.info("Aggiornamento data ultima generazione alla data corrente: {}", dataTracciamentoFormat(currDate));
			lastNoErrorDate = currDate;
		} else if (this.logger.isInfoEnabled()){
			this.logger.info("A causa di errori per la generazione di data: {}, la data di ultima generazione sarà impostata a tale data", dataTracciamentoFormat(lastNoErrorDate));
		}
		
		try {
			if (this.logger.isInfoEnabled())
				this.logger.info("Aggiorno data ultima generazione statistiche {} ...", 
						dataTracciamentoFormat(lastNoErrorDate));
			StatisticsInfoUtils.updateDataUltimaGenerazioneStatistiche(
					this.statisticheSM.getStatisticaInfoServiceSearch(), 
					this.statisticheSM.getStatisticaInfoService(), 
					TipoIntervalloStatistico.PDND_GENERAZIONE_TRACCIAMENTO,
					this.config.getLogSql(), lastNoErrorDate);
			if (this.logger.isInfoEnabled())
				this.logger.info("Aggiorno daa ultima generazione statistiche {} completato", 
						dataTracciamentoFormat(lastNoErrorDate));
		} catch (Exception e) {
			this.logger.error("Errore nell'aggiornamento della data ultima statistica {}", TipoIntervalloStatistico.PDND_GENERAZIONE_TRACCIAMENTO, e);
		}
	}
	
	@Override
	public void generate() throws StatisticsEngineException {
		
		this.logger.info("********************* INIZIO GENERAZIONE TRACCIATO PDND *********************");
		try {
			scanDates();
		} catch (NotImplementedException | ServiceException e) {
			throw new StatisticsEngineException(e);
		}
		
		try {
			scanNull();
		} catch (NotImplementedException 
				| ServiceException
				| ExpressionNotImplementedException
				| ExpressionException e) {
			throw new StatisticsEngineException(e);
		}
		this.logger.info("********************* FINE GENERAZIONE TRACCIATO PDND *********************");
	}
	
	
	@Override
	public boolean isEnabled(StatisticsConfig config) {
		return config.isPdndTracciamentoGenerazione();
	}
	
	private String dataTracciamentoFormat(Date date) {
		return new SimpleDateFormat(PDND_DATE_FORMAT).format(date);
	}

}
