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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.mail.BodyPart;
import javax.mail.internet.InternetHeaders;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.core.statistiche.StatistichePdndTracing;
import org.openspcoop2.core.statistiche.constants.PdndMethods;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiPdnd;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiRichieste;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.core.statistiche.dao.IStatistichePdndTracingService;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * PdndPublicazioneTracciamento
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PdndPublicazioneTracciamento implements IStatisticsEngine {
	
	private static final String PDND_DATE_FORMAT = "yyyy-MM-dd";
	private static final String TRACING_ID_FIELD = "tracingId";
	
	
	// Errore nella connessione con la pdnd, in tal caso necessario retry
	private static final String CONNECTION_ERROR = "CONNECTION_ERROR";
	
	// Errore fornito dalla pdnd dopo il parsing del documento
	private static final String PDND_PARSING_ERROR = "PDND_PARSING_ERROR";
	
	// Errore fornito dalla pdnd all'inivio del documento
	private static final String PDND_PUBLISHING_ERROR = "PDND_PUBLISHING_ERROR";

	
	private IStatistichePdndTracingService pdndStatisticheSM;
	private org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM;
	private Logger logger;
	private StatisticsConfig config;
	private Map<String, String> internalPddCodeName;
	private Map<Date, StatistichePdndTracing> updateTracingIdStats;
	
	PdndPublicazioneTracciamento(){
			super();
	}
	
	@Override
	public void init(StatisticsConfig config,
			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM,
			org.openspcoop2.core.plugins.dao.IServiceManager pluginsBaseSM,
			org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM,
			org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM) {
		this.logger = config.getLogCore();
		this.config = config;
		
		this.updateTracingIdStats = new HashMap<>();
		
		try {
			this.statisticheSM = statisticheSM;
			this.pdndStatisticheSM = statisticheSM.getStatistichePdndTracingService();
			this.internalPddCodeName = PdndTracciamentoUtils.getEnabledPddCodes(utilsSM, this.config);
			
			this.logger.debug("Soggetti abilitati al tracing PDND: {}", this.internalPddCodeName.values());
		} catch ( ExpressionException 
				| ServiceException 
				| NotImplementedException 
				| NotFoundException 
				| SQLQueryObjectException 
				| StatisticsEngineException e) {
			this.logger.error("Impossibile inizializzare la classe PdndGenerazioneTracciamento", e);
		}
	}
	
	private HttpRequest getBaseRequest(String pddCode) {
		return this.config.getPdndTracciamentoRequestConfig().getBaseRequest(this.internalPddCodeName.get(pddCode));
	}
	
	private Iterator<JsonNode> iteratorHttpList(HttpRequest req) {
		final int limit = 50;
		
		AtomicInteger offset = new AtomicInteger(-limit);
		Queue<JsonNode> list = new LinkedList<>();
		JSONUtils jsonUtils = JSONUtils.getInstance();
		
		return Stream.generate((Supplier<JsonNode>) () -> {
			if (list.isEmpty()) {
				offset.addAndGet(limit);
				req.addParam("limit", Integer.toString(limit));
				req.addParam("offset", Integer.toString(offset.get()));
				
				try {
					HttpResponse res = HttpUtilities.httpInvoke(req);
					JsonNode node = jsonUtils.getAsNode(res.getContent());
					JsonNode result = node.get("results");
					
					for ( int i = 0; i < result.size(); i++)
						list.add(result.get(i));
					
				} catch (UtilsException e) {
					return null;
				}
			}
			return list.isEmpty() ? null : list.remove();
		}).takeWhile(Objects::nonNull).sequential().iterator();
	}
	
	private String getUploadPath(StatistichePdndTracing stat) {
		switch (stat.getMethod()) {
		case REPLACE:
			return String.format("/tracings/%s/replace", stat.getTracingId());
		case RECOVER:
			return String.format("/tracings/%s/recover", stat.getTracingId());
		case SUBMIT:
			return "/tracings/submit";
		}
		return null;
	}
	
	private MimeMultipart getUploadBody(StatistichePdndTracing stat) throws UtilsException {
		MimeMultipart multipart = new MimeMultipart(HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
		
		InternetHeaders headers = new InternetHeaders();
		headers.addHeader(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_CSV);
		headers.addHeader(HttpConstants.CONTENT_DISPOSITION, HttpConstants.CONTENT_DISPOSITION_FORM_DATA_NAME_PREFIX+"\"file\"; "+HttpConstants.CONTENT_DISPOSITION_FILENAME_PREFIX+"\"record.csv\"");		
		BodyPart bp = multipart.createBodyPart(headers, stat.getCsv());
	
		multipart.addBodyPart(bp);
		
		if (stat.getMethod().equals(PdndMethods.SUBMIT)) {
			final SimpleDateFormat pdndDateFormat = new SimpleDateFormat(PDND_DATE_FORMAT);
			headers = new InternetHeaders();
			headers.addHeader(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_PLAIN);
			headers.addHeader(HttpConstants.CONTENT_DISPOSITION, HttpConstants.CONTENT_DISPOSITION_FORM_DATA_NAME_PREFIX+"\"date\"");		
			bp = multipart.createBodyPart(headers, pdndDateFormat.format(stat.getDataTracciamento()).getBytes());
			multipart.addBodyPart(bp);
		}
		
		return multipart;
	}
	
	private void writePdndError(StatistichePdndTracing stat, JsonNode response) throws StatisticsEngineException {
		ArrayNode errors = (ArrayNode) response.get("errors");
		
		try {
			stat.setErrorDetails(getErrorDetails(PDND_PUBLISHING_ERROR, errors));
		} catch (StatisticsEngineException e) {
			stat.setStatoPdnd(PossibiliStatiPdnd.ERROR);
			stat.setErrorDetails(getErrorDetails(PDND_PUBLISHING_ERROR, "Il messaggio di errore ricevuto dalla PDND non è conforme all'openAPI fornito"));
			return;
		}
		
		stat.setStatoPdnd(PossibiliStatiPdnd.ERROR);
		
		
		for (JsonNode node : errors) {
			String code = node.get("code").asText();
			
			if (code.equals("TRACING_ALREADY_EXISTS")) {
				stat.setStatoPdnd(PossibiliStatiPdnd.PENDING);
				this.updateTracingIdStats.put(stat.getDataTracciamento(), stat);
			}
		}
	}
	
	
	private String getErrorDetails(String type, JsonNode node) throws StatisticsEngineException {
		JSONUtils json = JSONUtils.getInstance();
		
		try {
			ObjectNode errorNode = json.newObjectNode();
			
			errorNode.set("pdnd_details", node);
			errorNode.put("type", type);
			return json.toString(errorNode);
		} catch (UtilsException e) {
			throw new StatisticsEngineException("Errore nella generazione del nodeo di errore", e);
		}
	}
	
	
	private String getErrorDetails(String type, String errMsg) throws StatisticsEngineException {
		JSONUtils json = JSONUtils.getInstance();
		
		try {
			ObjectNode errorNode = json.newObjectNode();
			
			errorNode.put("details", errMsg);
			errorNode.put("type", type);
			return json.toString(errorNode);
		} catch (UtilsException e) {
			throw new StatisticsEngineException("Errore nella generazione del nodeo di errore", e);
		}
	}
	
	private void checkSendTraceResult(StatistichePdndTracing stat, HttpResponse res, String errMsg) throws StatisticsEngineException {
		// errore nella comunicazione con la pdnd
		if (res == null) {
			stat.setErrorDetails(getErrorDetails(CONNECTION_ERROR, errMsg));
			stat.setStato(PossibiliStatiRichieste.FAILED);
			return;
		}
		
		Integer code = res.getResultHTTPOperation();
		byte[] content = res.getContent();
		JsonNode node = null;
		
		try {
			JSONUtils jsonUtils = JSONUtils.getInstance();
			node = jsonUtils.getAsNode(content);
		} catch (UtilsException e) {
			// ignore 
		}

		String baseType = null;
		try {
			baseType = ContentTypeUtilities.readBaseTypeFromContentType(res.getContentType());
		}catch(Exception e) {
			// la PDND non ha ritornato un json
			stat.setErrorDetails(getErrorDetails(PDND_PUBLISHING_ERROR,
					"Content-Type della risposta invalido, content-type: " + res.getContentType()
							+ ", content: " + new String(content)));
			stat.setStato(PossibiliStatiRichieste.FAILED);
			return;
		}
		if (!HttpConstants.CONTENT_TYPE_JSON.equals(baseType)
				&& !HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807.equals(baseType)) {
			// la PDND non ha ritornato un json
			stat.setErrorDetails(getErrorDetails(PDND_PUBLISHING_ERROR,
					"Content-Type della risposta non di tipo json, content-type: " + res.getContentType()
							+ ", content: " + new String(content)));
			stat.setStato(PossibiliStatiRichieste.FAILED);
		} else if (node == null) {
			// il json fornito dalla pdnd non è stato parsato correttamente
			stat.setErrorDetails(getErrorDetails(PDND_PUBLISHING_ERROR,
					"Non è stato possibile interpretare il contenuto: " + new String(content)));
			stat.setStato(PossibiliStatiRichieste.FAILED);
		} else if (code != HttpServletResponse.SC_OK) {
			// la pdnd ha ritornato un codice di errore
			writePdndError(stat, node);
			stat.setStato(PossibiliStatiRichieste.PUBLISHED);
			stat.setStatoPdnd(PossibiliStatiPdnd.ERROR);
		} else if (node.get(TRACING_ID_FIELD).isNull()) {
			// la pdnd ha ritornato un json senza il campo tracing_id
			stat.setErrorDetails(getErrorDetails(PDND_PUBLISHING_ERROR,
					"Dal contenuto non è stato possibile ottenere il campo tracingId: " + new String(content)));
			stat.setStato(PossibiliStatiRichieste.FAILED);
		} else {
			// tutto è andato correttamente salvo il tracing id
			String tracingId = node.get(TRACING_ID_FIELD).asText();

			stat.setTracingId(tracingId);
			stat.setStato(PossibiliStatiRichieste.PUBLISHED);
			stat.setStatoPdnd(PossibiliStatiPdnd.PENDING);
		}
	}
	
	/**
	 * Invio un tracciamento alla PDND
	 * @param pddCode: codice pdd del soggetto multitenante a cui si riferiscono i tracciati
	 * @param stat: tracciamento da inviare
	 * @throws StatisticsEngineException 
	 */
	private void sendTrace(String pddCode, StatistichePdndTracing stat) throws StatisticsEngineException {
		
		// controllo che non sia stato superato il massimo numero di tentativi a meno che la pubblicazione non sia forzata
		if (this.config.getPdndTracciamentoMaxAttempt() != null 
				&& this.config.getPdndTracciamentoMaxAttempt() <= stat.getTentativiPubblicazione()
				&& !stat.isForcePublish())
			return;
		
		// ripulisco eventuali errori precedenti
		stat.setErrorDetails(null);
		stat.setStato(null);
		
		// nel caso faccia una submit in ritardo dovro aggiornare il tracingId e fare una recover (in quanto sara diventato un MISSING)
		if ((stat.getMethod().equals(PdndMethods.SUBMIT) && Duration.between(stat.getDataTracciamento().toInstant(), new Date().toInstant()).compareTo(Duration.ofDays(2)) >= 0) 
				|| (!stat.getMethod().equals(PdndMethods.SUBMIT) && stat.getTracingId() == null)) {
			if (stat.getMethod().equals(PdndMethods.SUBMIT))
				stat.setMethod(PdndMethods.RECOVER);
			this.updateTracingIdStats.put(stat.getDataTracciamento(), stat);
			return;
		}
		
		stat.setForcePublish(false);
		
		HttpResponse res =  null;
		String errMsg = null;
		
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
		
			// aggiorno i tentativi
			stat.setTentativiPubblicazione(stat.getTentativiPubblicazione() + 1);
			stat.setDataPubblicazione(new Date());
			
			// provo ad inviare il tracciato alla PDND
			HttpRequest req = getBaseRequest(pddCode);
			req.setMethod(HttpRequestMethod.POST);
			req.setUrl(req.getBaseUrl() + getUploadPath(stat));
		
			MimeMultipart multipart = getUploadBody(stat);
			multipart.writeTo(os);
			req.setContentType(multipart.getContentType());
			req.setContent(os.toByteArray());
			res = HttpUtilities.httpInvoke(req);	
			
		} catch (Exception e) {
			errMsg = e.getMessage();
		}
		
		checkSendTraceResult(stat, res, errMsg);
		
	}
	
	
	private void logPublishResults(StatistichePdndTracing stat, String nomeSoggetto) {
		String dataTracciamentoFormat = dataTracciamentoFormat(stat.getDataTracciamento());

		if (PossibiliStatiRichieste.FAILED.equals(stat.getStato())
				|| PossibiliStatiPdnd.ERROR.equals(stat.getStatoPdnd())) {
			this.logger.error("Fallita la pubblicazione del tracciato [{}], soggetto: {}, dettagli: {}",
					dataTracciamentoFormat,
					nomeSoggetto,
					stat.getErrorDetails());
		} else if (PossibiliStatiRichieste.PUBLISHED.equals(stat.getStato())
				&& PossibiliStatiPdnd.PENDING.equals(stat.getStatoPdnd())) {
			this.logger.info("Pubblicazione tracciato [{}], soggetto: {}, inviata correttamente alla PDND",
					dataTracciamentoFormat,
					nomeSoggetto);
		} else {
			this.logger.info("tracciato [{}], soggetto: {}, non inviato in quanto il tracingId non risulta presente nel db riproverò nella prossima fase",
					dataTracciamentoFormat,
					nomeSoggetto);
		}
	}
	
	/**
	 * Pubblico i record dal db che risultano nello stato WAITING e con csv valorizzato
	 * @param pddCode: codice pdd del soggetto multitenante a cui si riferiscono i tracciati
	 * @throws ServiceException
	 * @throws NotFoundException
	 * @throws NotImplementedException
	 * @throws ExpressionNotImplementedException
	 * @throws ExpressionException
	 * @throws StatisticsEngineException 
	 */
	private void publishRecords(String nomeSoggetto, String pddCode) throws ServiceException, NotFoundException, NotImplementedException, ExpressionNotImplementedException, ExpressionException, StatisticsEngineException {		
		// ottengo la lista dei record in stato WAITING con csv valorizzzato
		IPaginatedExpression expr = this.pdndStatisticheSM.newPaginatedExpression();
		expr.isNotNull(StatistichePdndTracing.model().CSV);
		expr.equals(StatistichePdndTracing.model().STATO_PDND, PossibiliStatiPdnd.WAITING);
		expr.equals(StatistichePdndTracing.model().PDD_CODICE, pddCode);
		
		// se i tentativi di pubblicazione sono inferiori al massimo o la flag di force pubblicazione risulta abilitata
		if (this.config.getPdndTracciamentoMaxAttempt() != null) {
			IExpression attemptsExpr = this.pdndStatisticheSM.newExpression();
			attemptsExpr.or().lessThan(StatistichePdndTracing.model().TENTATIVI_PUBBLICAZIONE, this.config.getPdndTracciamentoMaxAttempt());
			attemptsExpr.or().equals(StatistichePdndTracing.model().FORCE_PUBLISH, true);
			expr.and(attemptsExpr);
		}
		
		expr.addOrder(StatistichePdndTracing.model().DATA_TRACCIAMENTO, SortOrder.ASC);
		
		List<StatistichePdndTracing> stats = null; 
		
		try {
			stats = this.pdndStatisticheSM.findAll(expr);
		} catch (ServiceException e) {
			if (e.getCause() instanceof NotFoundException) {
				this.logger.debug("Nessun record da pubblicare per il soggetto: {} trovato", nomeSoggetto);
				return;
			}
			throw new StatisticsEngineException("Errore inaspettato nella ricerca delle transazioni", e);
		}
		
		// per ogni record invio la traccia alla pdnd e poi aggiorno il db
		this.logger.debug("Trovati {} record da pubblicare per il soggetto: {}", stats.size(), nomeSoggetto);
		for (StatistichePdndTracing stat : stats) {
			sendTrace(pddCode, stat);
			this.pdndStatisticheSM.update(stat);
					
			this.logPublishResults(stat, nomeSoggetto);
		}
		
	}
	
	/**
	 * Aggiorna il tracing id a tutti i tracciati non submittati che non hanno un tracingId aggiornato
	 * @param pddCode: codice pdd del soggetto multitenante a cui si riferiscono i tracciati
	 * @throws ServiceException
	 * @throws NotFoundException
	 * @throws NotImplementedException
	 * @throws StatisticsEngineException
	 */
	private void fixPublishRecords(String nomeSoggetto, String pddCode) throws ServiceException, NotFoundException, NotImplementedException, StatisticsEngineException {
		// itero su la lista di tutti gli stati fin quando ho record che vanno aggiornati (in teoria non ne dovrei quasi mai avere)
		HttpRequest req = getBaseRequest(pddCode);
		req.setMethod(HttpRequestMethod.GET);
		req.setUrl(req.getBaseUrl() + "/tracings");
		
		Iterator<JsonNode> itr = iteratorHttpList(req);
		
		if (this.updateTracingIdStats.isEmpty()) {
			this.logger.info("Non ci sono tracciati senza un tracingId valido per il soggetto {}", nomeSoggetto);
		} else {
			this.logger.info("Individuati {} tracciati senza un tracingId valido per il soggetto: {}, procedo ad interrogare la PDND per ricavarlo",
					this.updateTracingIdStats.size(), 
					nomeSoggetto);
		}
		
		while (!this.updateTracingIdStats.isEmpty() && itr.hasNext()) {
			JsonNode node = itr.next();
			String tracingId = node.get(TRACING_ID_FIELD).asText();
			String tracingDate = node.get("date").asText();
			
			try {
				final SimpleDateFormat pdndDateFormat = new SimpleDateFormat(PDND_DATE_FORMAT);
				Date date = pdndDateFormat.parse(tracingDate);
				
				StatistichePdndTracing stat = this.updateTracingIdStats.remove(date);
				if (stat != null) {
					String formatTracingDate = dataTracciamentoFormat(stat.getDataTracciamento());
					stat.setTracingId(tracingId);
					if (!PossibiliStatiRichieste.FAILED.equals(stat.getStato())
							&& !PossibiliStatiPdnd.ERROR.equals(stat.getStatoPdnd())) {
						this.logger.info("Individuato tracciato [{}] per il soggetto: {}, senza tracingId valido, tracingId: {} aggiornato, applico l'operazione {}",
								formatTracingDate,
								nomeSoggetto,
								tracingId,
								stat.getMethod());
						
						sendTrace(pddCode, stat);
					}
					
					this.pdndStatisticheSM.update(stat);
					
					this.logPublishResults(stat, nomeSoggetto);
				}
			} catch (ParseException e) {
				throw new StatisticsEngineException(e, "Errore nel parsing della data ritornata dalla PDND, " + tracingDate + " data non riconosciuta");
			}
		}
		
		
	}
	
	
	/**
	 * Aggiorna lo stato di un singolo tracciato che non si trova nello stato PENDING
	 * @param pddCode: codice pdd del soggetto multitenante a cui si riferiscono i tracciati
	 * @param stat: Tracciato da aggiornare
	 * @throws StatisticsEngineException: errore fatale il batch non e' stato configurato correttamente 
	 */
	private void updateTraceStatus(String pddCode, StatistichePdndTracing stat) throws StatisticsEngineException {
		
		// itero su tutti gli errori ritornati
		HttpRequest req = getBaseRequest(pddCode);
		req.setMethod(HttpRequestMethod.GET);
		req.setUrl(req.getBaseUrl() + "/tracings/" + stat.getTracingId() + "/errors");
		
		Iterator<JsonNode> itr = iteratorHttpList(req);
		
		// se non ci sono errori tutto funziona correttamente
		if (!itr.hasNext()) {
			stat.setStatoPdnd(PossibiliStatiPdnd.OK);
			return;
		}
		
		try {
			// se ci sono errori li 
			ArrayNode arr = JSONUtils.getInstance().newArrayNode();
			while(itr.hasNext()) {
				JsonNode node = itr.next();
				arr.add(node);
			}
			
			stat.setStatoPdnd(PossibiliStatiPdnd.ERROR);
			stat.setErrorDetails(getErrorDetails(PDND_PARSING_ERROR, arr));
		} catch (UtilsException e) {
			stat.setStatoPdnd(PossibiliStatiPdnd.ERROR);
			stat.setErrorDetails(getErrorDetails(PDND_PARSING_ERROR, "Errore nel parsing della risposta dalla pdnd: " + e.getMessage()));
		}
	}
	
	/**
	 * Controlla lo stato delle richieste pending
	 * @param pddCode: codice pdd del soggetto multitenante a cui si riferiscono i tracciati
	 * @throws ServiceException
	 * @throws NotImplementedException
	 * @throws ExpressionNotImplementedException
	 * @throws ExpressionException
	 * @throws NotFoundException
	 * @throws StatisticsEngineException: eccezione fatale il batch non e' stato configurato correttamente
	 * @throws UtilsException
	 * @return true se non ci sono piu richieste pending nel db
	 */
	private boolean checkPending(String nomeSoggetto, String pddCode) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException, NotFoundException, StatisticsEngineException {
		
		// ottengo la lista di tutti i tracciati nello stato PENDING secondo la PDND
		Map<String, Date> pendingIds = getTracingIdStatus(pddCode, "PENDING");
		
		IPaginatedExpression expr = this.pdndStatisticheSM.newPaginatedExpression();
		expr.equals(StatistichePdndTracing.model().STATO_PDND, PossibiliStatiPdnd.PENDING);
		expr.equals(StatistichePdndTracing.model().PDD_CODICE, pddCode);
		expr.addOrder(StatistichePdndTracing.model().DATA_TRACCIAMENTO, SortOrder.ASC);

		// ottengo tutte le tabelle che sono in stato pending
		List<StatistichePdndTracing> stats = null;
		try {
			stats = this.pdndStatisticheSM.findAll(expr);
		} catch (ServiceException e) {
			this.logger.info("Nessun tracciato nello stato PENDING");
			return true;
		}

		this.logger.info("Tracciati con stato PENDING trovati: {}, soggetto: {}", stats.size(), nomeSoggetto);
		
		boolean noMorePending = true;
		for (StatistichePdndTracing stat : stats) {
			String dataTracciamentoFormat = dataTracciamentoFormat(stat.getDataTracciamento());

			// aggiorno lo stato solo dei tracciati che non risultano PENDING alla PDND
			if (!pendingIds.containsKey(stat.getTracingId())) {
				updateTraceStatus(pddCode, stat);
				this.pdndStatisticheSM.update(stat);

				// log risultato pubblicazione
				if (PossibiliStatiPdnd.ERROR.equals(stat.getStatoPdnd())) {
					this.logger.error("La PDND ha rilevato un errore nel tracciato [{}], soggetto: {}, dettagli: {}",
							dataTracciamentoFormat,
							nomeSoggetto,
							stat.getErrorDetails());
				} else if (PossibiliStatiPdnd.OK.equals(stat.getStatoPdnd())) {
					this.logger.info("La PDND ha caricato con successo tracciato [{}], soggetto: {}, inviata correttamente all PDND",
							dataTracciamentoFormat,
							nomeSoggetto);
				}
			} else {
				this.logger.info("Tracciato [{}], soggetto: {}, ancora in stato PENDING, non aggiorno", 
						dataTracciamentoFormat, 
						nomeSoggetto);
				noMorePending = false;
			}
		}
		
		return noMorePending;
	}
	
	/**
	 * Aggiorna il db con tutti i tracciati che non risultano MISSING alla PDND
	 * @param pddCode: codice pdd del soggetto multitenante a cui si riferiscono i tracciati
	 * @throws StatisticsEngineException, errore fatale il batch non e' stato configurato correttamente
	 * @throws ServiceException
	 * @throws NotImplementedException
	 * @throws ExpressionException 
	 * @throws ExpressionNotImplementedException 
	 * @throws MultipleResultException 
	 */
	private void updateMissing(String nomeSoggetto, String pddCode) throws StatisticsEngineException, ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException, MultipleResultException {
		Map<String, Date> missingIds = getTracingIdStatus(pddCode, "MISSING");
		
		if (!missingIds.isEmpty()) {
			this.logger.info("Le seguenti date [{}], risultano mancanti lato PDND per il soggetto {}, procedo alla creazione di entry vuote",
					missingIds.values(),
					nomeSoggetto);
		} else {
			this.logger.info("Tutti i tracciati per il soggetto {} sono stati caricati, nessun MISSING", nomeSoggetto);
		}
		
		for (Map.Entry<String, Date> id : missingIds.entrySet()) {
			StatistichePdndTracing stat = new StatistichePdndTracing();
			stat.setTracingId(id.getKey());
			stat.setDataTracciamento(id.getValue());
			stat.setDataRegistrazione(new Date());
			stat.setCsv(null);
			stat.setMethod(PdndMethods.RECOVER);
			stat.setHistory(0);
			stat.setPddCodice(pddCode);
			stat.setStatoPdnd(PossibiliStatiPdnd.WAITING);
			
			
			
			try {
				IExpression expr = this.pdndStatisticheSM.newExpression();
				expr.equals(StatistichePdndTracing.model().PDD_CODICE, pddCode);
				expr.equals(StatistichePdndTracing.model().DATA_TRACCIAMENTO, id.getValue());
				expr.equals(StatistichePdndTracing.model().HISTORY, 0);
				
				this.pdndStatisticheSM.find(expr);
			} catch (ServiceException e) {
				if (e.getCause() instanceof NotFoundException)
					this.pdndStatisticheSM.create(stat);
			} catch (NotFoundException e) {
				this.pdndStatisticheSM.create(stat);
			}
		}
	}
	
	
	private Map<String, Date> getTracingIdStatus(String pddCode, String status) throws StatisticsEngineException {
		
		HttpRequest req = getBaseRequest(pddCode);
		req.setMethod(HttpRequestMethod.GET);
		req.addParam("states", status);
		req.setUrl(req.getBaseUrl() + "/tracings");
		
		Iterator<JsonNode> itr = iteratorHttpList(req);
		Map<String, Date> ids = new HashMap<>();
		
		while(itr.hasNext()) {
			JsonNode node = itr.next();
			String tracingId = node.get(TRACING_ID_FIELD).asText();
			String tracingDate = node.get("date").asText();
			
			try {
				final SimpleDateFormat pdndDateFormat = new SimpleDateFormat(PDND_DATE_FORMAT);
				Date date = pdndDateFormat.parse(tracingDate);
				ids.put(tracingId, date);
			} catch (ParseException e) {
				throw new StatisticsEngineException(e, "Errore nel parsing della data ritornata dalla PDND, " + tracingDate + " data non riconosciuta");
			}
		}
		return ids;
	}
	
	
	/**
	 * Fa una semplice richiesta alla risorsa status delle PDND (dovrebbe semplicemente ritornare 200)
	 * @param pddCode: codice pdd del soggetto multi tenante che effettuera la richiesta
	 * @return true se la PDND ritorna un codice 200
	 */
	private boolean checkPdnd(String pddCode) {
		
		HttpRequest req = getBaseRequest(pddCode);
		req.setUrl(req.getBaseUrl() + "/status");
		req.setMethod(HttpRequestMethod.GET);
		
		HttpResponse res = null;
		try {
			res = HttpUtilities.httpInvoke(req);
		} catch (UtilsException e) {
			this.logger.error("Errore nella comunicazione con la risorsa /status della PDND tracciamento", e);
			return false;
		}
		
		int code = res.getResultHTTPOperation();
		
		if (code != HttpServletResponse.SC_OK) {
			this.logger.error("Errore nella comunicazione con la risorsa /status della PDND tracciamento, return code: {}", code);
			return false;
		}
		
		return true;
	}
	
	
	public void generate(String nomeSoggetto, String pddCode) throws StatisticsEngineException {
		
		// controllo che la pdnd sia funzionante
		if (!checkPdnd(pddCode)) {
			return;
		}
		
		this.updateTracingIdStats.clear();
		
		try {
			
			// pubblico i record che sono nello stato WAITING e con un csv valorizzato
			this.logger.info("------- FASE 1 [soggetto: {}] pubblicazione record -------", nomeSoggetto);
			publishRecords(nomeSoggetto, pddCode);
			
			// alcuni record potrebbero essere stati rifiutati e devono avere il tracingId valorizzato
			this.logger.info("------- FASE 2 [soggetto: {}] patch dei record senza tracingId -------", nomeSoggetto);
			fixPublishRecords(nomeSoggetto, pddCode);
			
			// aggiorno i record che la pdnd mi sta informando mancanti
			this.logger.info("------- FASE 3 [soggetto: {}] creazione dei record MISSING -------", nomeSoggetto);
			updateMissing(nomeSoggetto, pddCode);
			
			// controllo lo stato delle varie richieste pending
			this.logger.info("------- FASE 4 [soggetto: {}] controllo dei record PENDING -------", nomeSoggetto);
			
			Integer delayIndex = 0;
			List<Integer> delays = this.config.getPdndTracciamentoPendingCheck();
			
			
			boolean checkPendingResult = false;
			do {
				Integer delayI = delays.get(delayIndex++);
				int delay = Objects.requireNonNullElse(delayI, 0); // non dovrebbe mai succedere il caso di null
				
				if (delayIndex == 1) {
					this.logger.info("Aspetto {}s prima di aggiornare i record in PENDING, numero massimo di tentativi da effettuare: {}", delay, delays.size());
				} else if (delayIndex < delays.size()) {
					this.logger.info("La PDND non ha ancora valutato tutti i tracciati con lo stato PENDING, tentativo: {}, aspetto {}s e riprovo", delayIndex, delay);
				}
				
				if(delay>0) {
					Utilities.sleep(delay*1000l);
				}
				
				checkPendingResult = checkPending(nomeSoggetto, pddCode);
			} while(!checkPendingResult && delayIndex < delays.size());
			
			if (!checkPendingResult)
				this.logger.info("La PDND non ha ancora valutato tutti i tracciati con lo stato PENDING ma ho superato i tentativi massimi, riproverò alla prossima esecuzione");
			else
				this.logger.info("La PDND ha valutato tutti i tracciati con lo stato PENDING, nessun record in PENDING rimasto");
		
		} catch (ServiceException 
				| NotFoundException 
				| NotImplementedException
				| ExpressionNotImplementedException 
				| ExpressionException 
				| MultipleResultException e) {
			this.logger.error("Errore nella pubblicazione delle tracce, pdd code: {}", pddCode, e);
		}
	}
	
	@Override
	public void generate() throws StatisticsEngineException {
		this.logger.info("********************* INIZIO PUBBLICAZIONE TRACCIATO PDND *********************");
		
		Date currDate = new Date();
		try {
			StatisticsInfoUtils.updateDataUltimaGenerazioneStatistiche(
					this.statisticheSM.getStatisticaInfoServiceSearch(), 
					this.statisticheSM.getStatisticaInfoService(), 
					TipoIntervalloStatistico.PDND_PUBBLICAZIONE_TRACCIAMENTO,
					this.config.getLogSql(), currDate);
		} catch (Exception e) {
			this.logger.error("Errore nell'aggiornamento della data ultima statistica {}", TipoIntervalloStatistico.PDND_PUBBLICAZIONE_TRACCIAMENTO, e);
		}
		
		for (Map.Entry<String, String> soggetto : this.internalPddCodeName.entrySet())
			this.generate(soggetto.getValue(), soggetto.getKey());
		
		
		
		this.logger.info("********************* FINE PUBBLICAZIONE TRACCIATO PDND *********************");
	}
	
	@Override
	public boolean isEnabled(StatisticsConfig config) {
		return config.isPdndTracciamentoPubblicazione();
	}
	
	private String dataTracciamentoFormat(Date date) {
		return new SimpleDateFormat(PDND_DATE_FORMAT).format(date);
	}
}
