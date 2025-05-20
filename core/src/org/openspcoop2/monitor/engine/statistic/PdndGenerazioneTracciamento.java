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
import org.openspcoop2.core.commons.search.PortaDominio;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.statistiche.StatistichePdndTracing;
import org.openspcoop2.core.statistiche.constants.PdndMethods;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiPdnd;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCCredenzialeMittenteServiceSearch;
import org.openspcoop2.core.transazioni.utils.credenziali.AbstractCredenzialeList;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.csv.Format;
import org.openspcoop2.utils.csv.Printer;
import org.slf4j.Logger;

/**
 * PdndGenerazioneTracciamento
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PdndGenerazioneTracciamento implements IStatisticsEngine {
	
	private static final String REQUESTS_COUNT_ID = "request_count";
	
	PdndGenerazioneTracciamento(){
			super();
	}
	
	private org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM;
	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM;
	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM;
	private Map<String, Integer> eventsToCode;
	private Set<String> internalPddCodes;
	private Logger logger;
	
	@Override
	public void init(StatisticsConfig config,
			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM,
			org.openspcoop2.core.plugins.dao.IServiceManager pluginsBaseSM,
			org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM,
			org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM) {
		this.statisticheSM = statisticheSM;
		this.transazioniSM = transazioniSM;
		this.utilsSM = utilsSM;
		
		this.logger = config.getLogCore();
		
		this.eventsToCode = new HashMap<>();
		this.internalPddCodes = new HashSet<>();
		
		try {
			this.internalPddCodes = getCodesSoggettiInterni();
		} catch (ExpressionNotImplementedException 
				| ExpressionException 
				| ServiceException 
				| NotImplementedException 
				| NotFoundException 
				| MultipleResultException e) {
			this.logger.error("Impossibile inizializzare la classe PdndGenerazioneTracciamento", e);
		}
		
	}
	
	private Set<String> getCodesSoggettiInterni() throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException, NotFoundException, MultipleResultException {
		String internalPdd = getPddOperativa();
		
		IPaginatedExpression expr = this.utilsSM.getSoggettoServiceSearch().newPaginatedExpression();
		expr.equals(Soggetto.model().SERVER, internalPdd);
		expr.equals(Soggetto.model().TIPO_SOGGETTO, CostantiLabel.MODIPA_PROTOCOL_NAME);
		
		return this.utilsSM.getSoggettoServiceSearch().findAll(expr)
				.stream()
				.map(s -> s.getIdentificativoPorta())
				.collect(Collectors.toSet());
	}
	
	private String getPddOperativa() throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException, NotFoundException, MultipleResultException {
		IExpression expr = this.utilsSM.getPortaDominioServiceSearch().newExpression();
		expr.equals(PortaDominio.model().TIPO, PddTipologia.OPERATIVO.toString());
		return this.utilsSM.getPortaDominioServiceSearch().find(expr).getNome();
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
	
	private Integer convertEvent(String event) {
		try {
			Integer cached = this.eventsToCode.get(event);
			if (cached != null)
				return cached;
			CredenzialeMittente credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.transazioniSM.getCredenzialeMittenteService()).get(Long.valueOf(event));
			String cred = AbstractCredenzialeList.normalize(credenzialeMittente.getCredenziale());
			String rawCode = cred.substring(CostantiPdD.PREFIX_HTTP_STATUS_CODE_OUT.length());
			Integer code = Integer.valueOf(rawCode);
			this.eventsToCode.put(event, code);
			return code;
		} catch (NumberFormatException
				| ServiceException
				| NotFoundException 
				| MultipleResultException 
				| NotImplementedException e) {
			this.logger.error("Errore nella risoluzione del campo eventi_gestione della transazione, id={}", event, e);
			return null;
		}
	}
	
	
	private static final String[] CSV_HEADERS = {"date", "purpose_id", "stato", "token_id", "requests_count"};
	public byte[] generateCsv(Date tracingDate, List<Map<String, Object>> data) throws UtilsException {
		final SimpleDateFormat csvDateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
			String tokenId = row.get(Transazione.model().ID_MESSAGGIO_RICHIESTA.getFieldName()).toString();
			String statusId =  row.get(Transazione.model().EVENTI_GESTIONE.getFieldName()).toString();
			Integer status = convertEvent(statusId);
			String requestsCount = row.get(REQUESTS_COUNT_ID).toString();
			
			printer.printRecord(csvDateFormat.format(tracingDate), purposeId, status, tokenId, requestsCount);
			printer.printComment("commento");
		}
		
		printer.close();
		
		return os.toByteArray();
	}
	
	public void createRecord(Date tracingDate, String pddCode, List<Map<String, Object>> data) throws ServiceException, NotImplementedException {
		
		byte[] csv = null;
		try {	
			csv = generateCsv(tracingDate, data);
		} catch (UtilsException e) {
			this.logger.error("Errore nel generare il csv per il soggetto: {}, data tracciamento: {}", pddCode, tracingDate, e);
			return;
		}
		
		System.out.println("csv: " + new String(csv));
		
		StatistichePdndTracing entry = new StatistichePdndTracing();
		entry.setDataRegistrazione(new Date());
		entry.setDataTracciamento(tracingDate);
		entry.setCsv(csv);
		entry.setPddCodice(pddCode);
		entry.setHistory(0);
		entry.setMethod(PdndMethods.SUBMIT);
		entry.setStatoPdnd(PossibiliStatiPdnd.WAITING);

		this.statisticheSM.getStatistichePdndTracingService().create(entry);

	}
	
	private List<Map<String, Object>> aggregateTransactions(Date start, Date end) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		return aggregateTransactions(start, end, null);
	}	
	
	private List<Map<String, Object>> aggregateTransactions(Date start, Date end, String pddCode) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		IExpression expr = this.transazioniSM.getTransazioneService().newExpression();
		expr.addGroupBy(Transazione.model().PDD_CODICE)
			.addGroupBy(Transazione.model().TOKEN_PURPOSE_ID)
			.addGroupBy(Transazione.model().ID_MESSAGGIO_RICHIESTA)
			.addGroupBy(Transazione.model().EVENTI_GESTIONE)
			.and()
			.between(Transazione.model().DATA_INGRESSO_RICHIESTA, start, end)
			.isNotNull(Transazione.model().TOKEN_PURPOSE_ID)
			.equals(Transazione.model().PROTOCOLLO, CostantiLabel.MODIPA_PROTOCOL_NAME);
		
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
	
	private void createRecords(Date start, Date end) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		List<Map<String, Object>> groups = aggregateTransactions(start, end);
		
		
		Map<String, List<Map<String, Object>>> pddCodeGroup = groups
				.stream()
				.collect(Collectors.groupingBy(row -> (String)row.get(Transazione.model().PDD_CODICE.getFieldName())));
		
		for (String pddCode : this.internalPddCodes) {
			
			System.out.println(pddCode + ":");
			List<Map<String, Object>> rows = Objects.requireNonNullElse(
					pddCodeGroup.get(pddCode),
					List.of()
			);
			
			createRecord(start, pddCode, rows);
		}
		
	}
	
	public void scanNull() throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		
		IPaginatedExpression expr = this.statisticheSM.getStatistichePdndTracingService().newPaginatedExpression();
		expr.isNull(StatistichePdndTracing.model().CSV);
		
		List<StatistichePdndTracing> stats = this.statisticheSM.getStatistichePdndTracingService().findAll(expr);
		
		
		for (StatistichePdndTracing stat : stats) {
			Date startTracing = stat.getDataTracciamento();
			Date endTracing = truncDate(incrementDate(startTracing));
			
			List<Map<String, Object>> data;
			try {
				data = aggregateTransactions(startTracing, endTracing, stat.getPddCodice());
				stat.setCsv(generateCsv(startTracing, data));
				this.statisticheSM.getStatistichePdndTracingService().update(stat);
			} catch (ExpressionNotImplementedException 
					| ExpressionException
					| ServiceException 
					| NotImplementedException
					| NotFoundException
					| UtilsException e) {
				this.logger.error("Errore nell'update della statistica con csv null, tracingDate: {}, codice pdd: {}", startTracing, stat.getPddCodice(), e);
			}
		}
		
		
		
	}
	
	
	
	public void scanDates() throws NotImplementedException, ServiceException {
		Date lastDate = null;
		Date nextDate = null;
		Date currDate = truncDate(new Date());
		
		try {
			lastDate = StatisticsInfoUtils.readDataUltimaGenerazioneStatistiche(this.statisticheSM.getStatisticaInfoServiceSearch(), TipoIntervalloStatistico.PDND_GENERAZIONE_TRACCIAMENTO, this.logger);
		} catch (NotFoundException e) {
			lastDate = currDate;
		} catch (NotImplementedException | ServiceException e) {
			throw e;
		}
		
		if (lastDate.equals(new Date(0)))
			lastDate = currDate;
		
		lastDate = truncDate(new Date());
		currDate = incrementDate(lastDate);
		
		for (lastDate = truncDate(lastDate); !lastDate.equals(currDate); lastDate = nextDate) {
			nextDate = truncDate(incrementDate(lastDate));
			
			System.out.println(lastDate + "-" + currDate);
			
			try {
				createRecords(lastDate, nextDate);
			} catch (ServiceException 
					| NotImplementedException 
					| ExpressionNotImplementedException 
					| ExpressionException e) {
				this.logger.error("Errore nella generazione dei tracciamenti PDND per il range [{},{}(", lastDate, nextDate, e);
			}
		}
		
		
		try {
			StatisticsInfoUtils.updateDataUltimaGenerazioneStatistiche(
					this.statisticheSM.getStatisticaInfoServiceSearch(), 
					this.statisticheSM.getStatisticaInfoService(), 
					TipoIntervalloStatistico.PDND_GENERAZIONE_TRACCIAMENTO,
					this.logger, currDate);
		} catch (Exception e) {
			this.logger.error("Errore nell'aggiornamento della data ultima statistica {}", TipoIntervalloStatistico.PDND_GENERAZIONE_TRACCIAMENTO, e);
		}
	}
	
	@Override
	public void generate() throws StatisticsEngineException {
		
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
	}
	
	
	@Override
	public boolean isEnabled(StatisticsConfig config) {
		return config.isPdndGenerazioneTracciamento();
	}
}
