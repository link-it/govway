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
import java.io.IOException;
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
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.BodyPart;
import javax.mail.internet.InternetHeaders;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.core.commons.search.PortaDominio;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.statistiche.StatistichePdndTracing;
import org.openspcoop2.core.statistiche.constants.PdndMethods;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiPdnd;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiRichieste;
import org.openspcoop2.core.statistiche.dao.IStatistichePdndTracingService;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
	
	private IStatistichePdndTracingService pdndStatisticheSM;
	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM;
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
		this.utilsSM = utilsSM;
		this.config = config;
		this.updateTracingIdStats = new HashMap<>();
		
		try {
			this.pdndStatisticheSM = statisticheSM.getStatistichePdndTracingService();
			this.internalPddCodeName = getCodesSoggettiInterni();
		} catch (ExpressionNotImplementedException 
				| ExpressionException 
				| ServiceException 
				| NotImplementedException 
				| NotFoundException 
				| MultipleResultException e) {
			this.logger.error("Impossibile inizializzare la classe PdndGenerazioneTracciamento", e);
		}
	}
	
	private Map<String, String> getCodesSoggettiInterni() throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException, NotFoundException, MultipleResultException {
		String internalPdd = getPddOperativa();
		
		IPaginatedExpression expr = this.utilsSM.getSoggettoServiceSearch().newPaginatedExpression();
		expr.equals(Soggetto.model().SERVER, internalPdd);
		expr.equals(Soggetto.model().TIPO_SOGGETTO, CostantiLabel.MODIPA_PROTOCOL_NAME);
		
		Set<String> enabledName = this.config.getPdndTracingSoggettiEnabled();
		return this.utilsSM.getSoggettoServiceSearch().findAll(expr)
				.stream()
				.filter(s -> enabledName.isEmpty() || enabledName.contains(s.getNomeSoggetto()))
				.collect(Collectors.toMap(Soggetto::getIdentificativoPorta, Soggetto::getNomeSoggetto));
	}
	
	private String getPddOperativa() throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException, NotFoundException, MultipleResultException {
		IExpression expr = this.utilsSM.getPortaDominioServiceSearch().newExpression();
		expr.equals(PortaDominio.model().TIPO, PddTipologia.OPERATIVO.toString());
		return this.utilsSM.getPortaDominioServiceSearch().find(expr).getNome();
	}
	
	private HttpRequest getBaseRequest(String pddCode) {
		return this.config.getPdndTracingRequestConfig().getBaseRequest(this.internalPddCodeName.get(pddCode));
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
		headers.addHeader(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
		BodyPart bp = multipart.createBodyPart(headers, stat.getCsv());
	
		multipart.addBodyPart(bp);
		
		if (stat.getMethod().equals(PdndMethods.SUBMIT)) {
			final SimpleDateFormat pdndDateFormat = new SimpleDateFormat(PDND_DATE_FORMAT);
			headers = new InternetHeaders();
			headers.addHeader(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_PLAIN);
			bp = multipart.createBodyPart(headers, pdndDateFormat.format(stat.getDataTracciamento()).getBytes());
			multipart.addBodyPart(bp);
		}
		
		return multipart;
	}
	
	private void writePdndError(StatistichePdndTracing stat, JsonNode response) {
		ArrayNode errors = (ArrayNode) response.get("errors");
		
		try {
			stat.setErrorDetails(JSONUtils.getInstance().toString(errors));
		} catch (UtilsException e) {
			stat.setErrorDetails(e.getMessage());
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
	
	/**
	 * Invio un tracciamento alla PDND
	 * @param pddCode: codice pdd del soggetto multitenante a cui si riferiscono i tracciati
	 * @param stat: tracciamento da inviare
	 */
	private void sendTrace(String pddCode, StatistichePdndTracing stat) {
		
		// controllo che non sia stato superato il massimo numero di tentativi
		if (this.config.getMaxAttempt() != null && this.config.getMaxAttempt() <= stat.getTentativiPubblicazione())
			return;
		
		// nel caso faccia una submit in ritardo dovro aggiornare il tracingId e fare una recover (in quanto sara diventato un MISSING)
		if ((stat.getMethod().equals(PdndMethods.SUBMIT) && Duration.between(stat.getDataTracciamento().toInstant(), new Date().toInstant()).compareTo(Duration.ofDays(2)) >= 0) 
				|| (!stat.getMethod().equals(PdndMethods.SUBMIT) && stat.getTracingId() == null)) {
			if (stat.getMethod().equals(PdndMethods.SUBMIT))
				stat.setMethod(PdndMethods.RECOVER);
			this.updateTracingIdStats.put(stat.getDataTracciamento(), stat);
			return;
		}
		
		// aggiorno i tentativi
		stat.setTentativiPubblicazione(stat.getTentativiPubblicazione() + 1);
		
		// provo ad inviare il tracciato alla PDND
		HttpRequest req = getBaseRequest(pddCode);
		req.setMethod(HttpRequestMethod.POST);
		req.setUrl(req.getBaseUrl() + getUploadPath(stat));
		
		HttpResponse res =  null;
		String errMsg = null;
		JsonNode node = null;
		Integer code = null;
		
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
			MimeMultipart multipart = getUploadBody(stat);
			multipart.writeTo(os);
			req.setContentType(multipart.getContentType());
			req.setContent(os.toByteArray());
			res = HttpUtilities.httpInvoke(req);	
			code = res.getResultHTTPOperation();
			
			JSONUtils jsonUtils = JSONUtils.getInstance();
			node = jsonUtils.getAsNode(res.getContent());
		} catch (UtilsException | IOException e) {
			errMsg = e.getMessage();
		}
		
		
		// errore nella comunicazione con la pdnd
		if (code == null || node == null) {
			stat.setErrorDetails(errMsg);
			stat.setStato(PossibiliStatiRichieste.FAILED);
			return;
		}
		stat.setStato(PossibiliStatiRichieste.PUBLISHED);
		
		if (code != HttpServletResponse.SC_OK) {
			writePdndError(stat, node);
		} else {
			String tracingId = node.get(TRACING_ID_FIELD).asText();
			
			stat.setTracingId(tracingId);
			stat.setStatoPdnd(PossibiliStatiPdnd.PENDING);
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
	 */
	private void publishRecords(String pddCode) throws ServiceException, NotFoundException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		
		// ottengo la lista dei record in stato WAITING con csv valorizzzato
		IPaginatedExpression expr = this.pdndStatisticheSM.newPaginatedExpression();
		expr.isNotNull(StatistichePdndTracing.model().CSV);
		expr.equals(StatistichePdndTracing.model().STATO_PDND, PossibiliStatiPdnd.WAITING);
		expr.equals(StatistichePdndTracing.model().PDD_CODICE, pddCode);
		
		List<StatistichePdndTracing> stats = null; 
		
		try {
			stats = this.pdndStatisticheSM.findAll(expr);
		} catch (ServiceException e) {
			if (e.getCause() instanceof NotFoundException)
				return;
			throw e;
		}
		
		// per ogni record invio la traccia alla pdnd e poi aggiorno il db
		for (StatistichePdndTracing stat : stats) {
			sendTrace(pddCode, stat);
			this.pdndStatisticheSM.update(stat);
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
	private void fixPublishRecords(String pddCode) throws ServiceException, NotFoundException, NotImplementedException, StatisticsEngineException {
		
		// itero su la lista di tutti gli stati fin quando ho record che vanno aggiornati (in teoria non ne dovrei quasi mai avere)
		HttpRequest req = getBaseRequest(pddCode);
		req.setMethod(HttpRequestMethod.GET);
		req.setUrl(req.getBaseUrl() + "/tracings");
		
		Iterator<JsonNode> itr = iteratorHttpList(req);
		
		while (!this.updateTracingIdStats.isEmpty() && itr.hasNext()) {
			JsonNode node = itr.next();
			String tracingId = node.get(TRACING_ID_FIELD).asText();
			String tracingDate = node.get("date").asText();
			
			try {
				final SimpleDateFormat pdndDateFormat = new SimpleDateFormat(PDND_DATE_FORMAT);
				Date date = pdndDateFormat.parse(tracingDate);
				
				StatistichePdndTracing stat = this.updateTracingIdStats.remove(date);
				if (stat != null) {
					stat.setTracingId(tracingId);
					if (!PossibiliStatiRichieste.FAILED.equals(stat.getStato())
							&& !PossibiliStatiPdnd.ERROR.equals(stat.getStatoPdnd())) {
						sendTrace(pddCode, stat);
					}
					
					this.pdndStatisticheSM.update(stat);
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
			stat.setErrorDetails(JSONUtils.getInstance().toString(arr));
		} catch (UtilsException e) {
			stat.setStatoPdnd(PossibiliStatiPdnd.ERROR);
			throw new StatisticsEngineException(e, "Errore fatale, la classe JSONUtils non risulta configurata correttamente");
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
	 */
	private void checkPending(String pddCode) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException, NotFoundException, StatisticsEngineException {
		
		// ottengo la lista di tutti i tracciati nello stato PENDING secondo la PDND
		Map<String, Date> pendingIds = getTracingIdStatus(pddCode, "PENDING");
		
		IPaginatedExpression expr = this.pdndStatisticheSM.newPaginatedExpression();
		expr.equals(StatistichePdndTracing.model().STATO_PDND, PossibiliStatiPdnd.PENDING);
		expr.equals(StatistichePdndTracing.model().PDD_CODICE, pddCode);

		// ottengo tutte le tabelle che sono in stato pending
		List<StatistichePdndTracing> stats = null;
		try {
			stats = this.pdndStatisticheSM.findAll(expr);
		} catch (ServiceException e) {
			return;
		}

		for (StatistichePdndTracing stat : stats) {
			// aggiorno lo stato solo dei tracciati che non risultano PENDING alla PDND
			if (!pendingIds.containsKey(stat.getTracingId())) {
				updateTraceStatus(pddCode, stat);
				this.pdndStatisticheSM.update(stat);
			}
		}
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
	private void updateMissing(String pddCode) throws StatisticsEngineException, ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException, MultipleResultException {
		Map<String, Date> missingIds = getTracingIdStatus(pddCode, "MISSING");
		
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
	
	
	public void generate(String pddCode) throws StatisticsEngineException {
		
		// controllo che la pdnd sia funzionante
		if (!checkPdnd(pddCode)) {
			return;
		}
		
		this.updateTracingIdStats.clear();
		
		try {
			
			// pubblico i record che sono nello stato WAITING e con un csv valorizzato
			publishRecords(pddCode);
			
			// alcuni record potrebbero essere stati rifiutati e devono avere il tracingId valorizzato
			fixPublishRecords(pddCode);
			
			// controllo lo stato delle varie richieste pending
			checkPending(pddCode);
			
			// aggiorno i record che la pdnd mi sta informando mancanti
			updateMissing(pddCode);
			
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
		for (String pddCode : this.internalPddCodeName.keySet())
			this.generate(pddCode);
	}
	
	@Override
	public boolean isEnabled(StatisticsConfig config) {
		return config.isPdndPubblicazioneTracciamento();
	}
}
