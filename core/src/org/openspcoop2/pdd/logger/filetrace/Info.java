/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.logger.filetrace;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeTokenClient;
import org.openspcoop2.monitor.sdk.transaction.FaseTracciamento;
import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.info.DatiEsitoTransazione;
import org.openspcoop2.pdd.logger.info.DatiMittente;
import org.openspcoop2.pdd.logger.info.InfoEsitoTransazioneFormatUtils;
import org.openspcoop2.pdd.logger.info.InfoMittenteFormatUtils;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.PDNDTokenInfo;
import org.openspcoop2.protocol.sdk.PDNDTokenInfoDetails;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.date.DateEngineType;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.xml.XMLUtils;
import org.slf4j.Logger;

/**     
 * Transaction
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Info {
	
	private IProtocolFactory<?> protocolFactory;
	private org.openspcoop2.core.transazioni.Transazione transazione;
	private EsitiProperties esitiProperties;
	private CredenzialiMittente credenzialiMittente;
	private InformazioniToken informazioniToken;
	private InformazioniAttributi informazioniAttributi;
	private InformazioniNegoziazioneToken informazioniNegoziazioneToken;
	private SecurityToken securityToken;
	
	private FaseTracciamento trackingPhase;
	
	private Traccia tracciaRichiesta;
	private Traccia tracciaRisposta;
	
	private List<MsgDiagnostico> msgDiagnostici;
	
	private Messaggio richiestaIngresso;
	private Messaggio richiestaUscita;
	private Messaggio rispostaIngresso;
	private Messaggio rispostaUscita;
	
	private InfoConfigurazione infoConfigurazione;
	
	private Map<String, String> escape;
	private String headersSeparator;
	private String headerSeparator;
	private String headerPrefix;
	private String headerSuffix;
	private List<String> headerBlackList;
	private List<String> headerWhiteList;
	
	private String headerMultiValueSeparator;

	private String defaultValue="";
	private int defaultIntegerValue=0;
	private long defaultLongValue=0l;
	
	private Logger log;
	
	private boolean base64;
	
	private Map<String, String> properties = new HashMap<>();
	
	public Info(Logger log,
			IProtocolFactory<?> protocolFactory,
			org.openspcoop2.core.transazioni.Transazione transazione, 
			CredenzialiMittente credenzialiMittente,
			InformazioniToken informazioniToken,
			InformazioniAttributi informazioniAttributi,
			InformazioniNegoziazioneToken informazioniNegoziazioneToken,
			SecurityToken securityToken,
			Traccia tracciaRichiesta, Traccia tracciaRisposta,
			List<MsgDiagnostico> msgDiagnostici,
			Messaggio richiestaIngresso, Messaggio richiestaUscita,
			Messaggio rispostaIngresso, Messaggio rispostaUscita,
			InfoConfigurazione infoConfigurazione,
			FileTraceConfig config,
			FaseTracciamento trackingPhase,
			boolean base64) throws ProtocolException {
		this.log = log;
		this.protocolFactory = protocolFactory;
		this.transazione = transazione;
		this.esitiProperties = EsitiProperties.getInstance(log, this.protocolFactory);
		this.credenzialiMittente = credenzialiMittente;
		this.informazioniToken = informazioniToken;
		this.informazioniAttributi = informazioniAttributi;
		this.informazioniNegoziazioneToken = informazioniNegoziazioneToken;
		this.securityToken = securityToken;
		this.tracciaRichiesta = tracciaRichiesta;
		this.tracciaRisposta = tracciaRisposta;
		this.msgDiagnostici = msgDiagnostici;
		this.richiestaIngresso = richiestaIngresso;
		this.richiestaUscita = richiestaUscita;
		this.rispostaIngresso = rispostaIngresso;
		this.rispostaUscita = rispostaUscita;
		this.infoConfigurazione = infoConfigurazione;
		this.escape = config.getEscape();
		this.headersSeparator = config.getHeadersSeparator();
		this.headerSeparator = config.getHeaderSeparator();
		this.headerPrefix = config.getHeaderPrefix();
		this.headerSuffix = config.getHeaderSuffix();
		this.headerMultiValueSeparator = config.getHeaderMultiValueSeparator();
		this.headerWhiteList = config.getHeaderWhiteList();
		this.headerBlackList = config.getHeaderBlackList();
		this.trackingPhase = trackingPhase;
		this.base64 = base64;
	}
	
	private String getErrorSuffix(Exception t) {
		return " failed: "+t.getMessage();
	}
		
	
	// identificativi
	
	public java.lang.String getTransactionId() {
		return getTransactionId(null);
	}
	public java.lang.String getTransactionId(String defaultValue) {
		return correctValue(this.transazione.getIdTransazione(), defaultValue);
	}
	
	public java.lang.String getRequestId() {
		return getRequestId(null);
	}
	public java.lang.String getRequestId(String defaultValue) {
		return correctValue(this.transazione.getIdMessaggioRichiesta(), defaultValue);
	}
	
	public java.lang.String getResponseId() {
		return getResponseId(null);
	}
	public java.lang.String getResponseId(String defaultValue) {
		return correctValue(this.transazione.getIdMessaggioRisposta(), defaultValue);
	}
	
	public java.lang.String getCorrelationId() {
		return getCorrelationId(null);
	}
	public java.lang.String getCorrelationId(String defaultValue) {
		return correctValue(this.transazione.getIdCollaborazione(), defaultValue);
	}
	
	public java.lang.String getAsyncId() {
		return getAsyncId(null);
	}
	public java.lang.String getAsyncId(String defaultValue) {
		return correctValue(this.transazione.getIdAsincrono(), defaultValue);
	}
	
	public java.lang.String getRequestApplicationId() {
		return getRequestApplicationId(null);
	}
	public java.lang.String getRequestApplicationId(String defaultValue) {
		return correctValue(this.transazione.getIdCorrelazioneApplicativa(), defaultValue);
	}
	
	public java.lang.String getResponseApplicationId() {
		return getResponseApplicationId(null);
	}
	public java.lang.String getResponseApplicationId(String defaultValue) {
		return correctValue(this.transazione.getIdCorrelazioneApplicativaRisposta(), defaultValue);
	}
	
	public java.lang.String getApplicationId() {
		return getApplicationId(null);
	}
	public java.lang.String getApplicationId(String defaultValue) {
		String s = null;
		if(this.transazione.getIdCorrelazioneApplicativa()!=null) {
			s = this.transazione.getIdCorrelazioneApplicativa();
		}
		if(this.transazione.getIdCorrelazioneApplicativaRisposta()!=null) {
			if(s==null) {
				s = this.transazione.getIdCorrelazioneApplicativaRisposta();
			}
			else {
				s = s + this.transazione.getIdCorrelazioneApplicativaRisposta();
			}
		}
		return correctValue(s, defaultValue);
	}
	
	public java.lang.String getClusterId() {
		return getClusterId(null);
	}
	public java.lang.String getClusterId(String defaultValue) {
		return correctValue(this.transazione.getClusterId(), defaultValue);
	}
	
	
	// esito
	
	private static final String CONVERSIONE_ESITO_FALLITA = "Conversione esito fallita: ";
	
	public String getResultClass() {
		return getResultClass(null);
	}
	public String getResultClass(String defaultValue) {
		try {
			if(InfoEsitoTransazioneFormatUtils.isEsitoOk(this.log, this.transazione.getEsito(), this.esitiProperties)) {
				return correctValue(ResultClass.OK.name(),defaultValue);	
			}
			else if(InfoEsitoTransazioneFormatUtils.isEsitoFaultApplicativo(this.log, this.transazione.getEsito(), this.esitiProperties)) {
				return correctValue(ResultClass.FAULT.name(),defaultValue);
			}
			else if(InfoEsitoTransazioneFormatUtils.isEsitoKo(this.log, this.transazione.getEsito(), this.esitiProperties)) {
				return correctValue(ResultClass.KO.name(),defaultValue);
			}
			else {
				return correctValue(ResultClass.KO.name(),defaultValue);
			}
		}catch(Exception e) {
			this.log.error(CONVERSIONE_ESITO_FALLITA+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	
	public boolean isResultClassOk() {
		try {
			return InfoEsitoTransazioneFormatUtils.isEsitoOk(this.log, this.transazione.getEsito(), this.esitiProperties);
		}catch(Exception e) {
			this.log.error(CONVERSIONE_ESITO_FALLITA+e.getMessage(),e);
			return false;
		}
	}
	public boolean isResultClassFault() {
		try {
			return InfoEsitoTransazioneFormatUtils.isEsitoFaultApplicativo(this.log, this.transazione.getEsito(), this.esitiProperties);
		}catch(Exception e) {
			this.log.error(CONVERSIONE_ESITO_FALLITA+e.getMessage(),e);
			return false;
		}
	}
	public boolean isResultClassKo() {
		try {
			return InfoEsitoTransazioneFormatUtils.isEsitoKo(this.log, this.transazione.getEsito(), this.esitiProperties);
		}catch(Exception e) {
			this.log.error(CONVERSIONE_ESITO_FALLITA+e.getMessage(),e);
			return false;
		}
	}
	
	public int getResultAsInt() {
		return this.transazione.getEsito();
	}
	public int getResultCode() {
		return getResultAsInt();
	}
	public String getResult() {
		return getResult(null);
	}
	public String getResult(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoName(this.transazione.getEsito()),defaultValue);
		}catch(Exception e) {
			this.log.error(CONVERSIONE_ESITO_FALLITA+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	public String getResultDescription() {
		return getResultDescription(null);
	}
	public String getResultDescription(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoDescription(this.transazione.getEsito()),defaultValue);
		}catch(Exception e) {
			this.log.error(CONVERSIONE_ESITO_FALLITA+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	public String getResultLabel() {
		return getResultLabel(null);
	}
	public String getResultLabel(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoLabel(this.transazione.getEsito()),defaultValue);
		}catch(Exception e) {
			this.log.error(CONVERSIONE_ESITO_FALLITA+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	public String getResultLabelSyntetic() {
		return getResultLabelSyntetic(null);
	}
	public String getResultLabelSyntetic(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoLabelSyntetic(this.transazione.getEsito()),defaultValue);
		}catch(Exception e) {
			this.log.error(CONVERSIONE_ESITO_FALLITA+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	
	public int getSyncResultAsInt() {
		return this.transazione.getEsitoSincrono();
	}
	public String getSyncResult() {
		return getSyncResult(null);
	}
	public String getSyncResult(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoName(this.transazione.getEsitoSincrono()),defaultValue);
		}catch(Exception e) {
			this.log.error(CONVERSIONE_ESITO_FALLITA+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	public String getSyncResultDescription() {
		return getSyncResultDescription(null);
	}
	public String getSyncResultDescription(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoDescription(this.transazione.getEsitoSincrono()),defaultValue);
		}catch(Exception e) {
			this.log.error(CONVERSIONE_ESITO_FALLITA+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	public String getSyncResultLabel() {
		return getSyncResultLabel(null);
	}
	public String getSyncResultLabel(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoLabel(this.transazione.getEsitoSincrono()),defaultValue);
		}catch(Exception e) {
			this.log.error(CONVERSIONE_ESITO_FALLITA+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	public String getSyncResultLabelSyntetic() {
		return getSyncResultLabelSyntetic(null);
	}
	public String getSyncResultLabelSyntetic(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoLabelSyntetic(this.transazione.getEsitoSincrono()),defaultValue);
		}catch(Exception e) {
			this.log.error(CONVERSIONE_ESITO_FALLITA+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	
	public java.lang.String getTransactionType() {
		return getTransactionType(null);
	}
	public java.lang.String getTransactionType(String defaultValue) {
		return correctValue(this.transazione.getEsitoContesto(), defaultValue);
	}
	
	public java.lang.String getInHttpStatus() {
		return getInHttpStatus(null);
	}
	public java.lang.String getInHttpStatus(String defaultValue) {
		return correctValue(this.transazione.getCodiceRispostaIngresso(), defaultValue);
	}
	
	public java.lang.String getInHttpReason() {
		return getInHttpReason(null);
	}
	public java.lang.String getInHttpReason(String defaultValue) {
		String reason = null;
		try {
			String codiceRisposta = this.transazione.getCodiceRispostaIngresso();
			if(codiceRisposta!=null && !"".equals(codiceRisposta)) {
				int status = Integer.parseInt(codiceRisposta);
				reason = HttpUtilities.getHttpReason(status);
			}
		}catch(Exception e) {
			this.log.error("Conversione http reason: "+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
		return correctValue(reason, defaultValue);
	}
	
	public java.lang.String getOutHttpStatus() {
		return getOutHttpStatus(null);
	}
	public java.lang.String getOutHttpStatus(String defaultValue) {
		return correctValue(this.transazione.getCodiceRispostaUscita(), defaultValue);
	}
	
	public java.lang.String getOutHttpReason() {
		return getOutHttpReason(null);
	}
	public java.lang.String getOutHttpReason(String defaultValue) {
		String reason = null;
		try {
			String codiceRisposta = this.transazione.getCodiceRispostaUscita();
			if(codiceRisposta!=null && !"".equals(codiceRisposta)) {
				int status = Integer.parseInt(codiceRisposta);
				reason = HttpUtilities.getHttpReason(status);
			}
		}catch(Exception e) {
			this.log.error("Conversione http reason: "+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
		return correctValue(reason, defaultValue);
	}
	
	private DatiEsitoTransazione convertToDatiEsitoTransazioneEngine() {
		
		boolean oldValueBase64 = this.base64;
		DatiEsitoTransazione datiEsitoTransazione = null;
		try {
			this.base64 = false; // senno i dati salvata nell'oggetto sono serializzati in base64
			
			datiEsitoTransazione = new DatiEsitoTransazione();
			
			datiEsitoTransazione.setEsito(this.getResultAsInt());
			datiEsitoTransazione.setProtocollo(this.protocolFactory);
			
			datiEsitoTransazione.setFaultIntegrazione(this.getFaultIntegrazioneEngine(null));
			datiEsitoTransazione.setFormatoFaultIntegrazione(this.getFormatoFaultIntegrazioneEngine(null));
			
			datiEsitoTransazione.setFaultCooperazione(this.getFaultCooperazioneEngine(null));
			datiEsitoTransazione.setFormatoFaultCooperazione(this.getFormatoFaultCooperazioneEngine(null));
					
			datiEsitoTransazione.setPddRuolo(this.transazione.getPddRuolo());
		}finally {
			this.base64 = oldValueBase64;
		}
		
		return datiEsitoTransazione;
		
	}
	
	public java.lang.String getErrorDetail() {
		return getErrorDetail(null);
	}
	public java.lang.String getErrorDetail(String defaultValue) {
		DatiEsitoTransazione datiEsitoTransazione = convertToDatiEsitoTransazioneEngine();
		String dettaglioErroreResult = InfoEsitoTransazioneFormatUtils.getDettaglioErrore(this.log, datiEsitoTransazione, this.msgDiagnostici);
		return correctValue(dettaglioErroreResult, defaultValue);
	}
	
	public java.lang.String getDiagnostics() {
		return getDiagnosticsEngine(false, "\n", DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ,null);
	}
	public java.lang.String getDiagnostics(String separator) {
		return getDiagnosticsEngine(false, separator, DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ,null);
	}
	public java.lang.String getDiagnostics(String separator, String format) {
		return getDiagnosticsEngine(false, separator, format, null);
	}
	public java.lang.String getDiagnostics(String separator, String format, String timeZone) {
		return getDiagnosticsEngine(false, separator, format, timeZone);
	}
	public java.lang.String getErrorDiagnostics() {
		return getDiagnosticsEngine(true, "\n", DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ,null);
	}
	public java.lang.String getErrorDiagnostics(String separator) {
		return getDiagnosticsEngine(true, separator, DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ,null);
	}
	public java.lang.String getErrorDiagnostics(String separator, String format) {
		return getDiagnosticsEngine(true, separator, format, null);
	}
	public java.lang.String getErrorDiagnostics(String separator, String format, String timeZone) {
		return getDiagnosticsEngine(true, separator, format, timeZone);
	}
	private java.lang.String getDiagnosticsEngine(boolean onlyErrors, String separator, String format, String timeZone) {
		StringBuilder sb = new StringBuilder();
		if(this.msgDiagnostici!=null && !this.msgDiagnostici.isEmpty()) {
			for (MsgDiagnostico msgDiagnostico : this.msgDiagnostici) {
				if(onlyErrors &&
					msgDiagnostico.getSeverita()>LogLevels.SEVERITA_ERROR_INTEGRATION) {
					continue;
				}
				
				if(sb.length()>0) {
					sb.append(separator);
				}
				
				String severitaAsString = LogLevels.toOpenSPCoop2(msgDiagnostico.getSeverita());
				sb.append(severitaAsString);
				sb.append(" ");
				sb.append(correctDateWithoutCorrectValue(msgDiagnostico.getGdo(), format, timeZone, null, null));
				sb.append(" ");
				sb.append(msgDiagnostico.getCodice());
				sb.append(" ");
				sb.append(msgDiagnostico.getMessaggio());
			}
		}
		return correctValue(sb.toString(), null);
	}
	

	// date
	
	public java.lang.String getAcceptedRequestDate() {
		return this.getAcceptedRequestDate(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getAcceptedRequestDate(String format) {
		return getAcceptedRequestDate(format, null, null, null);
	}
	public java.lang.String getAcceptedRequestDate(String format, String defaultValue) {
		return getAcceptedRequestDate(format, defaultValue, null, null);
	}
	public java.lang.String getAcceptedRequestDate(String format, String replace, String with) {
		return getAcceptedRequestDate(format, null, replace, with);
	}
	public java.lang.String getAcceptedRequestDate(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataAccettazioneRichiesta(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getAcceptedRequestDateZ(String format, String timeZone) {
		return getAcceptedRequestDateZ(format, timeZone, null, null, null);
	}
	public java.lang.String getAcceptedRequestDateZ(String format, String timeZone, String defaultValue) {
		return getAcceptedRequestDateZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getAcceptedRequestDateZ(String format, String timeZone, String replace, String with) {
		return getAcceptedRequestDateZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getAcceptedRequestDateZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataAccettazioneRichiesta(), format, timeZone, defaultValue, replace, with);
	}
	
	public java.lang.String getInRequestDate() {
		return this.getInRequestDate(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getInRequestDate(String format) {
		return getInRequestDate(format, null, null, null);
	}
	public java.lang.String getInRequestDate(String format, String defaultValue) {
		return getInRequestDate(format, defaultValue, null, null);
	}
	public java.lang.String getInRequestDate(String format, String replace, String with) {
		return getInRequestDate(format, null, replace, with);
	}
	public java.lang.String getInRequestDate(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataIngressoRichiesta(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getInRequestDateZ(String format, String timeZone) {
		return getInRequestDateZ(format, timeZone, null, null, null);
	}
	public java.lang.String getInRequestDateZ(String format, String timeZone, String defaultValue) {
		return getInRequestDateZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getInRequestDateZ(String format, String timeZone, String replace, String with) {
		return getInRequestDateZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getInRequestDateZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataIngressoRichiesta(), format, timeZone, defaultValue, replace, with);
	}
	
	public java.lang.String getInRequestStartTime() {
		return this.getInRequestStartTime(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getInRequestStartTime(String format) {
		return getInRequestStartTime(format, null, null, null);
	}
	public java.lang.String getInRequestStartTime(String format, String defaultValue) {
		return getInRequestStartTime(format, defaultValue, null, null);
	}
	public java.lang.String getInRequestStartTime(String format, String replace, String with) {
		return getInRequestStartTime(format, null, replace, with);
	}
	public java.lang.String getInRequestStartTime(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataIngressoRichiesta(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getInRequestStartTimeZ(String format, String timeZone) {
		return getInRequestStartTimeZ(format, timeZone, null, null, null);
	}
	public java.lang.String getInRequestStartTimeZ(String format, String timeZone, String defaultValue) {
		return getInRequestStartTimeZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getInRequestStartTimeZ(String format, String timeZone, String replace, String with) {
		return getInRequestStartTimeZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getInRequestStartTimeZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataIngressoRichiesta(), format, timeZone, defaultValue, replace, with);
	}
	
	public java.lang.String getInRequestEndTime() {
		return this.getInRequestEndTime(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getInRequestEndTime(String format) {
		return getInRequestEndTime(format, null, null, null);
	}
	public java.lang.String getInRequestEndTime(String format, String defaultValue) {
		return getInRequestEndTime(format, defaultValue, null, null);
	}
	public java.lang.String getInRequestEndTime(String format, String replace, String with) {
		return getInRequestEndTime(format, null, replace, with);
	}
	public java.lang.String getInRequestEndTime(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataIngressoRichiestaStream(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getInRequestEndTimeZ(String format, String timeZone) {
		return getInRequestEndTimeZ(format, timeZone, null, null, null);
	}
	public java.lang.String getInRequestEndTimeZ(String format, String timeZone, String defaultValue) {
		return getInRequestEndTimeZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getInRequestEndTimeZ(String format, String timeZone, String replace, String with) {
		return getInRequestEndTimeZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getInRequestEndTimeZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataIngressoRichiestaStream(), format, timeZone, defaultValue, replace, with);
	}
	
	public java.lang.String getOutRequestDate() {
		return this.getOutRequestDate(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getOutRequestDate(String format) {
		return getOutRequestDate(format, null, null, null);
	}
	public java.lang.String getOutRequestDate(String format, String defaultValue) {
		return getOutRequestDate(format, defaultValue, null, null);
	}
	public java.lang.String getOutRequestDate(String format, String replace, String with) {
		return getOutRequestDate(format, null, replace, with);
	}
	public java.lang.String getOutRequestDate(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataUscitaRichiesta(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getOutRequestDateZ(String format, String timeZone) {
		return getOutRequestDateZ(format, timeZone, null, null, null);
	}
	public java.lang.String getOutRequestDateZ(String format, String timeZone, String defaultValue) {
		return getOutRequestDateZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getOutRequestDateZ(String format, String timeZone, String replace, String with) {
		return getOutRequestDateZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getOutRequestDateZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataUscitaRichiesta(), format, timeZone, defaultValue, replace, with);
	}
	
	public java.lang.String getOutRequestStartTime() {
		return this.getOutRequestStartTime(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getOutRequestStartTime(String format) {
		return getOutRequestStartTime(format, null, null, null);
	}
	public java.lang.String getOutRequestStartTime(String format, String defaultValue) {
		return getOutRequestStartTime(format, defaultValue, null, null);
	}
	public java.lang.String getOutRequestStartTime(String format, String replace, String with) {
		return getOutRequestStartTime(format, null, replace, with);
	}
	public java.lang.String getOutRequestStartTime(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataUscitaRichiesta(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getOutRequestStartTimeZ(String format, String timeZone) {
		return getOutRequestStartTimeZ(format, timeZone, null, null, null);
	}
	public java.lang.String getOutRequestStartTimeZ(String format, String timeZone, String defaultValue) {
		return getOutRequestStartTimeZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getOutRequestStartTimeZ(String format, String timeZone, String replace, String with) {
		return getOutRequestStartTimeZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getOutRequestStartTimeZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataUscitaRichiesta(), format, timeZone, defaultValue, replace, with);
	}
	
	public java.lang.String getOutRequestEndTime() {
		return this.getOutRequestEndTime(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getOutRequestEndTime(String format) {
		return getOutRequestEndTime(format, null, null, null);
	}
	public java.lang.String getOutRequestEndTime(String format, String defaultValue) {
		return getOutRequestEndTime(format, defaultValue, null, null);
	}
	public java.lang.String getOutRequestEndTime(String format, String replace, String with) {
		return getOutRequestEndTime(format, null, replace, with);
	}
	public java.lang.String getOutRequestEndTime(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataUscitaRichiestaStream(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getOutRequestEndTimeZ(String format, String timeZone) {
		return getOutRequestEndTimeZ(format, timeZone, null, null, null);
	}
	public java.lang.String getOutRequestEndTimeZ(String format, String timeZone, String defaultValue) {
		return getOutRequestEndTimeZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getOutRequestEndTimeZ(String format, String timeZone, String replace, String with) {
		return getOutRequestEndTimeZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getOutRequestEndTimeZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataUscitaRichiestaStream(), format, timeZone, defaultValue, replace, with);
	}
	
	public java.lang.String getAcceptedResponseDate() {
		return this.getAcceptedResponseDate(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getAcceptedResponseDate(String format) {
		return getAcceptedResponseDate(format, null, null, null);
	}
	public java.lang.String getAcceptedResponseDate(String format, String defaultValue) {
		return getAcceptedResponseDate(format, defaultValue, null, null);
	}
	public java.lang.String getAcceptedResponseDate(String format, String replace, String with) {
		return getAcceptedResponseDate(format, null, replace, with);
	}
	public java.lang.String getAcceptedResponseDate(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataAccettazioneRisposta(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getAcceptedResponseDateZ(String format, String timeZone) {
		return getAcceptedResponseDateZ(format, timeZone, null, null, null);
	}
	public java.lang.String getAcceptedResponseDateZ(String format, String timeZone, String defaultValue) {
		return getAcceptedResponseDateZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getAcceptedResponseDateZ(String format, String timeZone, String replace, String with) {
		return getAcceptedResponseDateZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getAcceptedResponseDateZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataAccettazioneRisposta(), format, timeZone, defaultValue, replace, with);
	}
	
	public java.lang.String getInResponseDate() {
		return this.getInResponseDate(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getInResponseDate(String format) {
		return getInResponseDate(format, null, null, null);
	}
	public java.lang.String getInResponseDate(String format, String defaultValue) {
		return getInResponseDate(format, defaultValue, null, null);
	}
	public java.lang.String getInResponseDate(String format, String replace, String with) {
		return getInResponseDate(format, null, replace, with);
	}
	public java.lang.String getInResponseDate(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataIngressoRisposta(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getInResponseDateZ(String format, String timeZone) {
		return getInResponseDateZ(format, timeZone, null, null, null);
	}
	public java.lang.String getInResponseDateZ(String format, String timeZone, String defaultValue) {
		return getInResponseDateZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getInResponseDateZ(String format, String timeZone, String replace, String with) {
		return getInResponseDateZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getInResponseDateZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataIngressoRisposta(), format, timeZone, defaultValue, replace, with);
	}
	
	public java.lang.String getInResponseStartTime() {
		return this.getInResponseStartTime(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getInResponseStartTime(String format) {
		return getInResponseStartTime(format, null, null, null);
	}
	public java.lang.String getInResponseStartTime(String format, String defaultValue) {
		return getInResponseStartTime(format, defaultValue, null, null);
	}
	public java.lang.String getInResponseStartTime(String format, String replace, String with) {
		return getInResponseStartTime(format, null, replace, with);
	}
	public java.lang.String getInResponseStartTime(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataIngressoRisposta(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getInResponseStartTimeZ(String format, String timeZone) {
		return getInResponseStartTimeZ(format, timeZone, null, null, null);
	}
	public java.lang.String getInResponseStartTimeZ(String format, String timeZone, String defaultValue) {
		return getInResponseStartTimeZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getInResponseStartTimeZ(String format, String timeZone, String replace, String with) {
		return getInResponseStartTimeZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getInResponseStartTimeZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataIngressoRisposta(), format, timeZone, defaultValue, replace, with);
	}
	
	public java.lang.String getInResponseEndTime() {
		return this.getInResponseEndTime(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getInResponseEndTime(String format) {
		return getInResponseEndTime(format, null, null, null);
	}
	public java.lang.String getInResponseEndTime(String format, String defaultValue) {
		return getInResponseEndTime(format, defaultValue, null, null);
	}
	public java.lang.String getInResponseEndTime(String format, String replace, String with) {
		return getInResponseEndTime(format, null, replace, with);
	}
	public java.lang.String getInResponseEndTime(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataIngressoRispostaStream(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getInResponseEndTimeZ(String format, String timeZone) {
		return getInResponseEndTimeZ(format, timeZone, null, null, null);
	}
	public java.lang.String getInResponseEndTimeZ(String format, String timeZone, String defaultValue) {
		return getInResponseEndTimeZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getInResponseEndTimeZ(String format, String timeZone, String replace, String with) {
		return getInResponseEndTimeZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getInResponseEndTimeZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataIngressoRispostaStream(), format, timeZone, defaultValue, replace, with);
	}
	
	public java.lang.String getOutResponseDate() {
		return this.getOutResponseDate(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getOutResponseDate(String format) {
		return getOutResponseDate(format, null, null, null);
	}
	public java.lang.String getOutResponseDate(String format, String defaultValue) {
		return getOutResponseDate(format, defaultValue, null, null);
	}
	public java.lang.String getOutResponseDate(String format, String replace, String with) {
		return getOutResponseDate(format, null, replace, with);
	}
	public java.lang.String getOutResponseDate(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataUscitaRisposta(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getOutResponseDateZ(String format, String timeZone) {
		return getOutResponseDateZ(format, timeZone, null, null, null);
	}
	public java.lang.String getOutResponseDateZ(String format, String timeZone, String defaultValue) {
		return getOutResponseDateZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getOutResponseDateZ(String format, String timeZone, String replace, String with) {
		return getOutResponseDateZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getOutResponseDateZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataUscitaRisposta(), format, timeZone, defaultValue, replace, with);
	}
	
	public java.lang.String getOutResponseStartTime() {
		return this.getOutResponseStartTime(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getOutResponseStartTime(String format) {
		return getOutResponseStartTime(format, null, null, null);
	}
	public java.lang.String getOutResponseStartTime(String format, String defaultValue) {
		return getOutResponseStartTime(format, defaultValue, null, null);
	}
	public java.lang.String getOutResponseStartTime(String format, String replace, String with) {
		return getOutResponseStartTime(format, null, replace, with);
	}
	public java.lang.String getOutResponseStartTime(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataUscitaRispostaStream(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getOutResponseStartTimeZ(String format, String timeZone) {
		return getOutResponseStartTimeZ(format, timeZone, null, null, null);
	}
	public java.lang.String getOutResponseStartTimeZ(String format, String timeZone, String defaultValue) {
		return getOutResponseStartTimeZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getOutResponseStartTimeZ(String format, String timeZone, String replace, String with) {
		return getOutResponseStartTimeZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getOutResponseStartTimeZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataUscitaRispostaStream(), format, timeZone, defaultValue, replace, with);
	}
	
	public java.lang.String getOutResponseEndTime() {
		return this.getOutResponseEndTime(DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public java.lang.String getOutResponseEndTime(String format) {
		return getOutResponseEndTime(format, null, null, null);
	}
	public java.lang.String getOutResponseEndTime(String format, String defaultValue) {
		return getOutResponseEndTime(format, defaultValue, null, null);
	}
	public java.lang.String getOutResponseEndTime(String format, String replace, String with) {
		return getOutResponseEndTime(format, null, replace, with);
	}
	public java.lang.String getOutResponseEndTime(String format, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataUscitaRisposta(), format, null, defaultValue, replace, with);
	}
	public java.lang.String getOutResponseEndTimeZ(String format, String timeZone) {
		return getOutResponseEndTimeZ(format, timeZone, null, null, null);
	}
	public java.lang.String getOutResponseEndTimeZ(String format, String timeZone, String defaultValue) {
		return getOutResponseEndTimeZ(format, timeZone, defaultValue, null, null);
	}
	public java.lang.String getOutResponseEndTimeZ(String format, String timeZone, String replace, String with) {
		return getOutResponseEndTimeZ(format, timeZone, null, replace, with);
	}
	public java.lang.String getOutResponseEndTimeZ(String format, String timeZone, String defaultValue, String replace, String with) {
		return this.correctDate(this.transazione.getDataUscitaRisposta(), format, timeZone, defaultValue, replace, with);
	}
	
	
	
	// latenza
	
	public long getElapsedTime() {
		return getElapsedTime(null);
	}
	public long getElapsedTime(String defaultValue) {
		return this.correctLong(getLatenzaTotaleEngine(), defaultValue);
	}
	public long getElapsedTimeS() {
		return getElapsedTimeS(null);
	}
	public long getElapsedTimeS(String defaultValue) {
		return getElapsedTime(defaultValue) / 1000;
	}
	public long getElapsedTimeMs() {
		return getElapsedTimeMs(null);
	}
	public long getElapsedTimeMs(String defaultValue) {
		return getElapsedTime(defaultValue);
	}
	public long getElapsedTimeUs() {
		return getElapsedTimeUs(null);
	}
	public long getElapsedTimeUs(String defaultValue) {
		return getElapsedTime(defaultValue) * 1000;
	}
	public long getElapsedTimeNs() {
		return getElapsedTimeNs(null);
	}
	public long getElapsedTimeNs(String defaultValue) {
		return getElapsedTime(defaultValue) * 1000 * 1000;
	}
	private Long getLatenzaTotaleEngine() {
		Long l = null;
		if(this.transazione.getDataUscitaRisposta() != null && this.transazione.getDataIngressoRichiesta() != null){
			l = this.transazione.getDataUscitaRisposta().getTime() - this.transazione.getDataIngressoRichiesta().getTime();
		}
		return l;
	}

	public long getApiElapsedTime() {
		return getApiElapsedTime(null);
	}
	public long getApiElapsedTime(String defaultValue) {
		return this.correctLong(getLatenzaServizioEngine(), defaultValue);
	}
	public long getApiElapsedTimeS() {
		return getApiElapsedTimeS(null);
	}
	public long getApiElapsedTimeS(String defaultValue) {
		return getApiElapsedTime(defaultValue) / 1000;
	}
	public long getApiElapsedTimeMs() {
		return getApiElapsedTimeMs(null);
	}
	public long getApiElapsedTimeMs(String defaultValue) {
		return getApiElapsedTime(defaultValue);
	}
	public long getApiElapsedTimeUs() {
		return getApiElapsedTimeUs(null);
	}
	public long getApiElapsedTimeUs(String defaultValue) {
		return getApiElapsedTime(defaultValue) * 1000;
	}
	public long getApiElapsedTimeNs() {
		return getApiElapsedTimeNs(null);
	}
	public long getApiElapsedTimeNs(String defaultValue) {
		return getApiElapsedTime(defaultValue) * 1000 * 1000;
	}
	private Long getLatenzaServizioEngine() {
		Long l = null;
		if(this.transazione.getDataUscitaRichiesta() != null && this.transazione.getDataIngressoRisposta() != null){
			l = this.transazione.getDataIngressoRisposta().getTime() - this.transazione.getDataUscitaRichiesta().getTime();
		}
		return l;
	}

	public long getGatewayLatency() {
		return getGatewayLatency(null);
	}
	public long getGatewayLatency(String defaultValue) {
		return this.correctLong(getLatenzaPortaEngine(), defaultValue);
	}
	public long getGatewayLatencyS() {
		return getGatewayLatencyS(null);
	}
	public long getGatewayLatencyS(String defaultValue) {
		return getGatewayLatency(defaultValue) / 1000;
	}
	public long getGatewayLatencyMs() {
		return getGatewayLatencyMs(null);
	}
	public long getGatewayLatencyMs(String defaultValue) {
		return getGatewayLatency(defaultValue);
	}
	public long getGatewayLatencyUs() {
		return getGatewayLatencyUs(null);
	}
	public long getGatewayLatencyUs(String defaultValue) {
		return getGatewayLatency(defaultValue) * 1000;
	}
	public long getGatewayLatencyNs() {
		return getGatewayLatencyNs(null);
	}
	public long getGatewayLatencyNs(String defaultValue) {
		return getGatewayLatency(defaultValue) * 1000 * 1000;
	}
	private Long getLatenzaPortaEngine() {
		Long l = null;
		Long latTotale = this.getLatenzaTotaleEngine();
		if(latTotale != null && latTotale>=0) {
			Long latServizio = this.getLatenzaServizioEngine();
			if(latServizio != null && latServizio>=0) {
				l = latTotale.longValue() - latServizio.longValue();
			}else { 
				l = latTotale;
			}
		}
		return l;
	}

	
	
	// dominio
	
	public java.lang.String getDomain() {
		return getDomain(null);
	}
	public java.lang.String getDomain(String defaultValue) {
		return correctValue(this.transazione.getPddCodice(), defaultValue);
	}
	public java.lang.String getOrganizationType() {
		return getOrganizationType(null);
	}
	public java.lang.String getOrganizationType(String defaultValue) {
		return correctValue(this.transazione.getPddTipoSoggetto(), defaultValue);
	}
	public java.lang.String getOrganization() {
		return getOrganization(null);
	}
	public java.lang.String getOrganization(String defaultValue) {
		return correctValue(this.transazione.getPddNomeSoggetto(), defaultValue);
	}
	public java.lang.String getOrganizationId() {
		return getOrganizationId(null);
	}
	public java.lang.String getOrganizationId(String defaultValue) {
		String nome = null;
		if(StringUtils.isNotEmpty(this.getOrganization())) {
			try {
				nome = NamingUtils.getLabelSoggetto(this.getProfile(), this.getOrganizationType(), this.getOrganization());
			}catch(Exception e) {
				// ignore
			}
		}
		return correctValue(nome, defaultValue);
	}
	public java.lang.String getRole() {
		return getRole(null);
	}
	public java.lang.String getRole(String defaultValue) {
		String s = null;
		if(this.transazione.getPddRuolo()!=null) {
			if(org.openspcoop2.core.transazioni.constants.PddRuolo.DELEGATA.equals(this.transazione.getPddRuolo())) {
				s = "fruizione";
			}
			else {
				s = "erogazione";
			}
		}
		return correctValue(s, defaultValue);
	}

	
	// soggetto mittente
	
	public java.lang.String getSenderType() {
		return getSenderType(null);
	}
	public java.lang.String getSenderType(String defaultValue) {
		return correctValue(this.transazione.getTipoSoggettoFruitore(), defaultValue);
	}
	public java.lang.String getSender() {
		return getSender(null);
	}
	public java.lang.String getSender(String defaultValue) {
		return correctValue(this.transazione.getNomeSoggettoFruitore(), defaultValue);
	}
	public java.lang.String getSenderId() {
		return getSenderId(null);
	}
	public java.lang.String getSenderId(String defaultValue) {
		String nome = null;
		if(StringUtils.isNotEmpty(this.getSender())) {
			try {
				nome = NamingUtils.getLabelSoggetto(this.getProfile(), this.getSenderType(), this.getSender());
			}catch(Exception e) {
				// ignore
			}
		}
		return correctValue(nome, defaultValue);
	}
	public java.lang.String getSenderDomain() {
		return getSenderDomain(null);
	}
	public java.lang.String getSenderDomain(String defaultValue) {
		return correctValue(this.transazione.getIdportaSoggettoFruitore(), defaultValue);
	}
	public java.lang.String getSenderURI() {
		return getSenderURI(null);
	}
	public java.lang.String getSenderURI(String defaultValue) {
		return correctValue(this.transazione.getIndirizzoSoggettoFruitore(), defaultValue);
	}
	
	public java.lang.String getSenderProperty(String name) {
		return getSenderProperty(name, null);
	}
	public java.lang.String getSenderProperty(String name, String defaultValue) {
		return correctValue(this.infoConfigurazione.getSenderProperty(name),defaultValue);
	}
	public java.lang.String getSenderPropertiesKeys(){
		return getSenderPropertiesKeys(null);
	}
	public java.lang.String getSenderPropertiesKeys(String defaultValue){
		return getSenderPropertiesKeys(InfoConfigurazione.KEYS_SEPARATOR,defaultValue);
	}
	public java.lang.String getSenderPropertiesKeys(String separator, String defaultValue){
		return correctValue(this.infoConfigurazione.getSenderPropertiesKeysAsString(separator),defaultValue);
	}
	public java.lang.String getSenderProperties(){
		return getSenderProperties(null);
	}
	public java.lang.String getSenderProperties(String defaultValue){
		return getSenderProperties(InfoConfigurazione.PROPERTY_SEPARATOR,InfoConfigurazione.VALUE_SEPARATOR,defaultValue);
	}
	public java.lang.String getSenderProperties(String propertySeparator, String valueSeparator){
		return getSenderProperties(propertySeparator, valueSeparator, null);
	}
	public java.lang.String getSenderProperties(String propertySeparator, String valueSeparator, String defaultValue){
		return correctValue(this.infoConfigurazione.getSenderPropertiesAsString(propertySeparator,valueSeparator),defaultValue);
	}
	
	// soggetto destinatario
	
	public java.lang.String getProviderType() {
		return getProviderType(null);
	}
	public java.lang.String getProviderType(String defaultValue) {
		return correctValue(this.transazione.getTipoSoggettoErogatore(), defaultValue);
	}
	public java.lang.String getProvider() {
		return getProvider(null);
	}
	public java.lang.String getProvider(String defaultValue) {
		return correctValue(this.transazione.getNomeSoggettoErogatore(), defaultValue);
	}
	public java.lang.String getProviderId() {
		return getProviderId(null);
	}
	public java.lang.String getProviderId(String defaultValue) {
		String nome = null;
		if(StringUtils.isNotEmpty(this.getProvider())) {
			try {
				nome = NamingUtils.getLabelSoggetto(this.getProfile(), this.getProviderType(), this.getProvider());
			}catch(Exception e) {
				// ignore
			}
		}
		return correctValue(nome, defaultValue);
	}
	public java.lang.String getProviderDomain() {
		return getProviderDomain(null);
	}
	public java.lang.String getProviderDomain(String defaultValue) {
		return correctValue(this.transazione.getIdportaSoggettoErogatore(), defaultValue);
	}
	public java.lang.String getProviderURI() {
		return getProviderURI(null);
	}
	public java.lang.String getProviderURI(String defaultValue) {
		return correctValue(this.transazione.getIndirizzoSoggettoErogatore(), defaultValue);
	}
	
	public java.lang.String getProviderProperty(String name) {
		return getProviderProperty(name, null);
	}
	public java.lang.String getProviderProperty(String name, String defaultValue) {
		return correctValue(this.infoConfigurazione.getProviderProperty(name),defaultValue);
	}
	public java.lang.String getProviderPropertiesKeys(){
		return getProviderPropertiesKeys(null);
	}
	public java.lang.String getProviderPropertiesKeys(String defaultValue){
		return getProviderPropertiesKeys(InfoConfigurazione.KEYS_SEPARATOR,defaultValue);
	}
	public java.lang.String getProviderPropertiesKeys(String separator, String defaultValue){
		return correctValue(this.infoConfigurazione.getProviderPropertiesKeysAsString(separator),defaultValue);
	}
	public java.lang.String getProviderProperties(){
		return getProviderProperties(null);
	}
	public java.lang.String getProviderProperties(String defaultValue){
		return getProviderProperties(InfoConfigurazione.PROPERTY_SEPARATOR,InfoConfigurazione.VALUE_SEPARATOR,defaultValue);
	}
	public java.lang.String getProviderProperties(String propertySeparator, String valueSeparator){
		return getProviderProperties(propertySeparator, valueSeparator, null);
	}
	public java.lang.String getProviderProperties(String propertySeparator, String valueSeparator, String defaultValue){
		return correctValue(this.infoConfigurazione.getProviderPropertiesAsString(propertySeparator,valueSeparator),defaultValue);
	}
	
	
	// profilo di collaborazione
	
	public java.lang.String getCollaborationProfileCode() {
		return getCollaborationProfileCode(null);
	}
	public java.lang.String getCollaborationProfileCode(String defaultValue) {
		return correctValue(this.transazione.getProfiloCollaborazioneOp2(), defaultValue);
	}
	public java.lang.String getCollaborationProfile() {
		return getCollaborationProfile(null);
	}
	public java.lang.String getCollaborationProfile(String defaultValue) {
		return correctValue(this.transazione.getProfiloCollaborazioneProt(), defaultValue);
	}
	
	
	// API
	
	public String getApiProtocol() {
		return getApiProtocol(null);
	}
	public String getApiProtocol(String defaultValue) {
		if(TipoAPI.REST.getValoreAsInt() == this.transazione.getTipoApi()) {
			return "rest";
		}
		else if(TipoAPI.SOAP.getValoreAsInt() == this.transazione.getTipoApi()) {
			return "soap";
		}
		else {
			return this.correctValue(null, defaultValue);
		}
	}
	
	public java.lang.String getApiInterface() {
		return getApiInterface(null);
	}
	public java.lang.String getApiInterface(String defaultValue) {
		return correctValue(this.transazione.getUriAccordoServizio(), defaultValue);
	}
	
	public java.lang.String getApiInterfaceId() {
		return getApiInterfaceId(null);
	}
	public java.lang.String getApiInterfaceId(String defaultValue) {
		String p = null;
		try {
			String parteComune = this.transazione.getUriAccordoServizio();
			if(parteComune!=null && !"".equals(parteComune)) {
				p = getApiInterfaceIdEngine(parteComune);
			}
		}catch(Exception e) {
			// ignore
		}
		return correctValue(p, defaultValue);
	}
	private String getApiInterfaceIdEngine(String parteComune) {
		try {
			IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(parteComune);
			return NamingUtils.getLabelAccordoServizioParteComune(idAccordo);
		}catch(Exception t) {
			// ignore
		}
		return null;
	}
	
	public java.lang.String getApiType() {
		return getApiType(null);
	}
	public java.lang.String getApiType(String defaultValue) {
		return correctValue(this.transazione.getTipoServizio(), defaultValue);
	}
	
	public java.lang.String getApi() {
		return getApi(null);
	}
	public java.lang.String getApi(String defaultValue) {
		return correctValue(this.transazione.getNomeServizio(), defaultValue);
	}
	
	public int getApiVersion() {
		return getApiVersion(null);
	}
	public int getApiVersion(String defaultValue) {
		return correctInteger(this.transazione.getVersioneServizio(),defaultValue);
	}
	
	public java.lang.String getApiId() {
		return getApiId(null);
	}
	public java.lang.String getApiId(String defaultValue) {
		String nome = null;
		if(StringUtils.isNotEmpty(this.getApi())) {
			try {
				nome = NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(this.getProfile(), this.getApiType(), this.getApi(), this.getApiVersion());
			}catch(Exception e) {
				// ignore
			}
		}
		return correctValue(nome, defaultValue);
	}
	
	public java.lang.String getApiProperty(String name) {
		return getApiProperty(name, null);
	}
	public java.lang.String getApiProperty(String name, String defaultValue) {
		return correctValue(this.infoConfigurazione.getApiImplProperty(name),defaultValue);
	}
	public java.lang.String getApiPropertiesKeys(){
		return getApiPropertiesKeys(null);
	}
	public java.lang.String getApiPropertiesKeys(String defaultValue){
		return getApiPropertiesKeys(InfoConfigurazione.KEYS_SEPARATOR,defaultValue);
	}
	public java.lang.String getApiPropertiesKeys(String separator, String defaultValue){
		return correctValue(this.infoConfigurazione.getApiImplPropertiesKeysAsString(separator),defaultValue);
	}
	public java.lang.String getApiProperties(){
		return getApiProperties(null);
	}
	public java.lang.String getApiProperties(String defaultValue){
		return getApiProperties(InfoConfigurazione.PROPERTY_SEPARATOR,InfoConfigurazione.VALUE_SEPARATOR,defaultValue);
	}
	public java.lang.String getApiProperties(String propertySeparator, String valueSeparator){
		return getApiProperties(propertySeparator, valueSeparator, null);
	}
	public java.lang.String getApiProperties(String propertySeparator, String valueSeparator, String defaultValue){
		return correctValue(this.infoConfigurazione.getApiImplPropertiesAsString(propertySeparator,valueSeparator),defaultValue);
	}
	
	public java.lang.String getInterface() {
		return getInterface(null);
	}
	public java.lang.String getInterface(String defaultValue) {
		return correctValue(this.transazione.getNomePorta(), defaultValue);
	}
	
	
	// Operazione
	
	public java.lang.String getAction() {
		return getAction(null);
	}
	public java.lang.String getAction(String defaultValue) {
		return correctValue(this.transazione.getAzione(), defaultValue);
	}
	
	public java.lang.String getHttpMethod() {
		return getHttpMethod(null);
	}
	public java.lang.String getHttpMethod(String defaultValue) {
		return correctValue(this.transazione.getTipoRichiesta(), defaultValue);
	}
	
	public java.lang.String getOutURL() {
		return getOutURL(null);
	}
	public java.lang.String getOutURL(String defaultValue) {
		String url = this.transazione.getLocationConnettore();
		if(url!=null && url.startsWith("[") && url.contains("] ")) {
			// [HttpType]  es.: [POST]
			url = url.substring(url.indexOf("] ")+2);
		}
		return correctValue(url, defaultValue);
	}
	
	public java.lang.String getOutConnectorName() {
		return getOutConnectorName(null);
	}
	public java.lang.String getOutConnectorName(String defaultValue) {
		return correctValue(this.infoConfigurazione.getOutConnectorName(), defaultValue);
	}
	
	public java.lang.String getInURL() {
		return getInURL(null);
	}
	public java.lang.String getInURL(String defaultValue) {
		String url = this.transazione.getUrlInvocazione();
		if(url!=null && url.startsWith("[") && url.contains("] ")) {
			// [function]  es.: [in]
			url = url.substring(url.indexOf("] ")+2);
		}
		return correctValue(url, defaultValue);
	}
	
	public java.lang.String getInFunction() {
		return getInFunction(null);
	}
	public java.lang.String getInFunction(String defaultValue) {
		String url = this.transazione.getUrlInvocazione();
		String function = null;
		if(url.startsWith("[") && url.contains("] ")) {
			// [function]  es.: [in]
			function = url.substring(1,url.indexOf("] "));
		}
		return correctValue(function, defaultValue);
	}
	
	
	// Mittente
	
	public java.lang.String getApplication() {
		return getApplication(null);
	}
	public java.lang.String getApplication(String defaultValue) {
		return correctValue(this.transazione.getServizioApplicativoFruitore(), defaultValue);
	}
	
	public java.lang.String getApplicationProperty(String name) {
		return getApplicationProperty(name, null);
	}
	public java.lang.String getApplicationProperty(String name, String defaultValue) {
		return correctValue(this.infoConfigurazione.getApplicationProperty(name),defaultValue);
	}
	public java.lang.String getApplicationPropertiesKeys(){
		return getApplicationPropertiesKeys(null);
	}
	public java.lang.String getApplicationPropertiesKeys(String defaultValue){
		return getApplicationPropertiesKeys(InfoConfigurazione.KEYS_SEPARATOR,defaultValue);
	}
	public java.lang.String getApplicationPropertiesKeys(String separator, String defaultValue){
		return correctValue(this.infoConfigurazione.getApplicationPropertiesKeysAsString(separator),defaultValue);
	}
	public java.lang.String getApplicationProperties(){
		return getApplicationProperties(null);
	}
	public java.lang.String getApplicationProperties(String defaultValue){
		return getApplicationProperties(InfoConfigurazione.PROPERTY_SEPARATOR,InfoConfigurazione.VALUE_SEPARATOR,defaultValue);
	}
	public java.lang.String getApplicationProperties(String propertySeparator, String valueSeparator){
		return getApplicationProperties(propertySeparator, valueSeparator, null);
	}
	public java.lang.String getApplicationProperties(String propertySeparator, String valueSeparator, String defaultValue){
		return correctValue(this.infoConfigurazione.getApplicationPropertiesAsString(propertySeparator,valueSeparator),defaultValue);
	}
	
	public java.lang.String getCredentials() {
		return getCredentials(null);
	}
	public java.lang.String getCredentials(String defaultValue) {
		return correctValue(this.transazione.getCredenziali(), defaultValue);
	}
	
	public java.lang.String getPrincipalAuthType() {
		return getPrincipalAuthType(null);
	}
	public java.lang.String getPrincipalAuthType(String defaultValue) {
		String tipo = this.credenzialiMittente!=null && this.credenzialiMittente.getTrasporto()!=null ? this.credenzialiMittente.getTrasporto().getTipo() : null;
		if(tipo!=null && tipo.contains("_")) {
			try {
				String soloTipoAuth = tipo.substring(tipo.indexOf("_")+1, tipo.length());
				if(soloTipoAuth!=null) {
					tipo = soloTipoAuth;
				}
			}catch(Exception e) {
				// ignore
			}
		}
		return correctValue(tipo, defaultValue);
	}
	
	public java.lang.String getPrincipal() {
		return getPrincipal(null);
	}
	public java.lang.String getPrincipal(String defaultValue) {
		return correctValue(this.credenzialiMittente!=null && this.credenzialiMittente.getTrasporto()!=null ? this.credenzialiMittente.getTrasporto().getCredenziale() : null, defaultValue);
	}
	
	public java.lang.String getClientCertificateSubjectDN() {
		return getClientCertificateSubjectDN(null);
	}
	public java.lang.String getClientCertificateSubjectDN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getChannel()!=null && this.securityToken.getChannel().getCertificate()!=null &&
			this.securityToken.getChannel().getCertificate().getSubject()!=null) {
			v = this.securityToken.getChannel().getCertificate().getSubject().toString();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getClientCertificateSubjectCN() {
		return getClientCertificateSubjectCN(null);
	}
	public java.lang.String getClientCertificateSubjectCN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getChannel()!=null && this.securityToken.getChannel().getCertificate()!=null &&
			this.securityToken.getChannel().getCertificate().getSubject()!=null) {
			v = this.securityToken.getChannel().getCertificate().getSubject().getCN();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getClientCertificateSubjectDNInfo(String oid) {
		return getClientCertificateSubjectDNInfo(oid, null);
	}
	public java.lang.String getClientCertificateSubjectDNInfo(String oid, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getChannel()!=null && this.securityToken.getChannel().getCertificate()!=null &&
			this.securityToken.getChannel().getCertificate().getSubject()!=null) {
			v = this.securityToken.getChannel().getCertificate().getSubject().getInfo(oid);
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getClientCertificateIssuerDN() {
		return getClientCertificateIssuerDN(null);
	}
	public java.lang.String getClientCertificateIssuerDN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getChannel()!=null && this.securityToken.getChannel().getCertificate()!=null &&
			this.securityToken.getChannel().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getChannel().getCertificate().getIssuer().toString();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getClientCertificateIssuerCN() {
		return getClientCertificateIssuerCN(null);
	}
	public java.lang.String getClientCertificateIssuerCN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getChannel()!=null && this.securityToken.getChannel().getCertificate()!=null &&
			this.securityToken.getChannel().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getChannel().getCertificate().getIssuer().getCN();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getClientCertificateIssuerDNInfo(String oid) {
		return getClientCertificateIssuerDNInfo(oid, null);
	}
	public java.lang.String getClientCertificateIssuerDNInfo(String oid, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getChannel()!=null && this.securityToken.getChannel().getCertificate()!=null &&
			this.securityToken.getChannel().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getChannel().getCertificate().getIssuer().getInfo(oid);
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getToken() {
		return getToken(null);
	}
	public java.lang.String getToken(String defaultValue) {
		return correctValue(this.transazione.getTokenInfo(), defaultValue);
	}
	
	public java.lang.String getTokenIssuer() {
		return getTokenIssuer(null);
	}
	public java.lang.String getTokenIssuer(String defaultValue) {
		return correctValue(this.credenzialiMittente!=null && this.credenzialiMittente.getTokenIssuer()!=null ? this.credenzialiMittente.getTokenIssuer().getCredenziale() : null, defaultValue);
	}
	
	public java.lang.String getTokenSubject() {
		return getTokenSubject(null);
	}
	public java.lang.String getTokenSubject(String defaultValue) {
		return correctValue(this.credenzialiMittente!=null && this.credenzialiMittente.getTokenSubject()!=null ? this.credenzialiMittente.getTokenSubject().getCredenziale() : null, defaultValue);
	}
	
	public java.lang.String getTokenClientId() {
		return getTokenClientId(null);
	}
	public java.lang.String getTokenClientId(String defaultValue) {
		return correctValue(this.credenzialiMittente!=null && this.credenzialiMittente.getTokenClientId()!=null ? CredenzialeTokenClient.convertClientIdDBValueToOriginal(this.credenzialiMittente.getTokenClientId().getCredenziale()) : null, defaultValue);
	}
	
	private IDServizioApplicativo tokenClientApplication;
	private Boolean tokenClientApplicationRead;
	private void initTokenClientApplicationEngine() {
		try {
			this.tokenClientApplication = this.credenzialiMittente!=null && this.credenzialiMittente.getTokenClientId()!=null ? CredenzialeTokenClient.convertApplicationDBValueToOriginal(this.credenzialiMittente.getTokenClientId().getCredenziale()) : null;
		}catch(Exception t) {
			this.log.error("_initTokenClientApplication()"+getErrorSuffix(t),t);
		}
		this.tokenClientApplicationRead=true;
	}
	private IDServizioApplicativo getTokenClientApplicationEngine() {
		if(this.tokenClientApplicationRead==null) {
			initTokenClientApplicationEngine();
		}
		return this.tokenClientApplication;
	}
	
	public java.lang.String getTokenClientApplication() {
		return getTokenClientApplication(null);
	}
	public java.lang.String getTokenClientApplication(String defaultValue) {
		IDServizioApplicativo idSA = getTokenClientApplicationEngine();
		return correctValue(idSA!=null ? idSA.getNome() : null, defaultValue);
	}
	
	public java.lang.String getTokenClientApplicationProperty(String name) {
		return getTokenClientApplicationProperty(name, null);
	}
	public java.lang.String getTokenClientApplicationProperty(String name, String defaultValue) {
		return correctValue(this.infoConfigurazione.getTokenClientApplicationProperty(name),defaultValue);
	}
	public java.lang.String getTokenClientApplicationPropertiesKeys(){
		return getTokenClientApplicationPropertiesKeys(null);
	}
	public java.lang.String getTokenClientApplicationPropertiesKeys(String defaultValue){
		return getTokenClientApplicationPropertiesKeys(InfoConfigurazione.KEYS_SEPARATOR,defaultValue);
	}
	public java.lang.String getTokenClientApplicationPropertiesKeys(String separator, String defaultValue){
		return correctValue(this.infoConfigurazione.getTokenClientApplicationPropertiesKeysAsString(separator),defaultValue);
	}
	public java.lang.String getTokenClientApplicationProperties(){
		return getTokenClientApplicationProperties(null);
	}
	public java.lang.String getTokenClientApplicationProperties(String defaultValue){
		return getTokenClientApplicationProperties(InfoConfigurazione.PROPERTY_SEPARATOR,InfoConfigurazione.VALUE_SEPARATOR,defaultValue);
	}
	public java.lang.String getTokenClientApplicationProperties(String propertySeparator, String valueSeparator){
		return getTokenClientApplicationProperties(propertySeparator, valueSeparator, null);
	}
	public java.lang.String getTokenClientApplicationProperties(String propertySeparator, String valueSeparator, String defaultValue){
		return correctValue(this.infoConfigurazione.getTokenClientApplicationPropertiesAsString(propertySeparator,valueSeparator),defaultValue);
	}
	
	public java.lang.String getTokenClientOrganizationType() {
		return getTokenClientOrganizationType(null);
	}
	public java.lang.String getTokenClientOrganizationType(String defaultValue) {
		IDServizioApplicativo idSA = getTokenClientApplicationEngine();
		return correctValue(idSA!=null ? idSA.getIdSoggettoProprietario().getTipo() : null, defaultValue);
	}
	public java.lang.String getTokenClientOrganization() {
		return getTokenClientOrganization(null);
	}
	public java.lang.String getTokenClientOrganization(String defaultValue) {
		IDServizioApplicativo idSA = getTokenClientApplicationEngine();
		return correctValue(idSA!=null ? idSA.getIdSoggettoProprietario().getNome() : null, defaultValue);
	}
	public java.lang.String getTokenClientOrganizationId() {
		return getTokenClientOrganizationId(null);
	}
	public java.lang.String getTokenClientOrganizationId(String defaultValue) {
		String nome = null;
		if(StringUtils.isNotEmpty(this.getTokenClientOrganization())) {
			try {
				nome = NamingUtils.getLabelSoggetto(this.getProfile(), this.getTokenClientOrganizationType(), this.getTokenClientOrganization());
			}catch(Exception e) {
				// ignore
			}
		}
		return correctValue(nome, defaultValue);
	}
	
	public java.lang.String getTokenClientOrganizationProperty(String name) {
		return getTokenClientOrganizationProperty(name, null);
	}
	public java.lang.String getTokenClientOrganizationProperty(String name, String defaultValue) {
		return correctValue(this.infoConfigurazione.getTokenClientOrganizationProperty(name),defaultValue);
	}
	public java.lang.String getTokenClientOrganizationPropertiesKeys(){
		return getTokenClientOrganizationPropertiesKeys(null);
	}
	public java.lang.String getTokenClientOrganizationPropertiesKeys(String defaultValue){
		return getTokenClientOrganizationPropertiesKeys(InfoConfigurazione.KEYS_SEPARATOR,defaultValue);
	}
	public java.lang.String getTokenClientOrganizationPropertiesKeys(String separator, String defaultValue){
		return correctValue(this.infoConfigurazione.getTokenClientOrganizationPropertiesKeysAsString(separator),defaultValue);
	}
	public java.lang.String getTokenClientOrganizationProperties(){
		return getTokenClientOrganizationProperties(null);
	}
	public java.lang.String getTokenClientOrganizationProperties(String defaultValue){
		return getTokenClientOrganizationProperties(InfoConfigurazione.PROPERTY_SEPARATOR,InfoConfigurazione.VALUE_SEPARATOR,defaultValue);
	}
	public java.lang.String getTokenClientOrganizationProperties(String propertySeparator, String valueSeparator){
		return getTokenClientOrganizationProperties(propertySeparator, valueSeparator, null);
	}
	public java.lang.String getTokenClientOrganizationProperties(String propertySeparator, String valueSeparator, String defaultValue){
		return correctValue(this.infoConfigurazione.getTokenClientOrganizationPropertiesAsString(propertySeparator,valueSeparator),defaultValue);
	}
	
	public java.lang.String getTokenUsername() {
		return getTokenUsername(null);
	}
	public java.lang.String getTokenUsername(String defaultValue) {
		return correctValue(this.credenzialiMittente!=null && this.credenzialiMittente.getTokenUsername()!=null ? this.credenzialiMittente.getTokenUsername().getCredenziale() : null, defaultValue);
	}
	
	public java.lang.String getTokenMail() {
		return getTokenMail(null);
	}
	public java.lang.String getTokenMail(String defaultValue) {
		return correctValue(this.credenzialiMittente!=null && this.credenzialiMittente.getTokenEMail()!=null ? this.credenzialiMittente.getTokenEMail().getCredenziale() : null, defaultValue);
	}
	
	public java.lang.String getTokenClaim(String tokenClaim) {
		return getTokenClaim(tokenClaim, null);
	}
	public java.lang.String getTokenClaim(String tokenClaim, String defaultValue) {
		String v = null;
		if(tokenClaim!=null &&
				this.informazioniToken!=null && 
				this.informazioniToken.getClaims()!=null && 
				this.informazioniToken.getClaims().containsKey(tokenClaim)) {
			Object valueInfoTokenObject = this.informazioniToken.getClaims().get(tokenClaim);
			if(valueInfoTokenObject!=null) {
				List<String> lClaimValues = TokenUtilities.getClaimValues(valueInfoTokenObject);
				v = TokenUtilities.getClaimValuesAsString(lClaimValues);
			}
		}	
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenRaw() {
		return getTokenRaw(null);
	}
	public java.lang.String getTokenRaw(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null) {
			v = this.securityToken.getAccessToken().getToken();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenHeaderRaw() {
		return getTokenHeaderRaw(null);
	}
	public java.lang.String getTokenHeaderRaw(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null) {
			v = this.securityToken.getAccessToken().getHeader();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenDecodedHeader() {
		return getTokenDecodedHeader(null);
	}
	public java.lang.String getTokenDecodedHeader(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null) {
			v = this.securityToken.getAccessToken().getDecodedHeader();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenHeaderClaim(String tokenClaim) {
		return getTokenHeaderClaim(tokenClaim, null);
	}
	public java.lang.String getTokenHeaderClaim(String tokenClaim, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null) {
			try {
				v = this.securityToken.getAccessToken().getHeaderClaim(tokenClaim);
			}catch(Exception t) {
				this.log.error("getTokenHeaderClaim("+tokenClaim+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenHeaderClaims() {
		return getTokenHeaderClaims(",", "=", null);
	}
	public java.lang.String getTokenHeaderClaims(String claimSeparator, String nameValueSeparator, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null) {
			try {
				Map<String, String> map = this.securityToken.getAccessToken().getHeaderClaims();
				v = formatTokenClaims(map, claimSeparator, nameValueSeparator);
			}catch(Exception t) {
				this.log.error("getTokenHeaderClaims("+claimSeparator+","+nameValueSeparator+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenPayloadRaw() {
		return getTokenPayloadRaw(null);
	}
	public java.lang.String getTokenPayloadRaw(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null) {
			v = this.securityToken.getAccessToken().getPayload();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenDecodedPayload() {
		return getTokenDecodedPayload(null);
	}
	public java.lang.String getTokenDecodedPayload(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null) {
			v = this.securityToken.getAccessToken().getDecodedPayload();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenPayloadClaim(String tokenClaim) {
		return getTokenPayloadClaim(tokenClaim, null);
	}
	public java.lang.String getTokenPayloadClaim(String tokenClaim, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null) {
			try {
				v = this.securityToken.getAccessToken().getPayloadClaim(tokenClaim);
			}catch(Exception t) {
				this.log.error("getTokenPayloadClaim("+tokenClaim+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenPayloadClaims() {
		return getTokenPayloadClaims(",", "=", null);
	}
	public java.lang.String getTokenPayloadClaims(String claimSeparator, String nameValueSeparator, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null) {
			try {
				Map<String, String> map = this.securityToken.getAccessToken().getPayloadClaims();
				v = formatTokenClaims(map, claimSeparator, nameValueSeparator);
			}catch(Exception t) {
				this.log.error("getTokenPayloadClaims("+claimSeparator+","+nameValueSeparator+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenCertificateSubjectDN() {
		return getTokenCertificateSubjectDN(null);
	}
	public java.lang.String getTokenCertificateSubjectDN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null && this.securityToken.getAccessToken().getCertificate()!=null &&
			this.securityToken.getAccessToken().getCertificate().getSubject()!=null) {
			v = this.securityToken.getAccessToken().getCertificate().getSubject().toString();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenCertificateSubjectCN() {
		return getTokenCertificateSubjectCN(null);
	}
	public java.lang.String getTokenCertificateSubjectCN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null && this.securityToken.getAccessToken().getCertificate()!=null &&
			this.securityToken.getAccessToken().getCertificate().getSubject()!=null) {
			v = this.securityToken.getAccessToken().getCertificate().getSubject().getCN();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenCertificateSubjectDNInfo(String oid) {
		return getTokenCertificateSubjectDNInfo(oid, null);
	}
	public java.lang.String getTokenCertificateSubjectDNInfo(String oid, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null && this.securityToken.getAccessToken().getCertificate()!=null &&
			this.securityToken.getAccessToken().getCertificate().getSubject()!=null) {
			v = this.securityToken.getAccessToken().getCertificate().getSubject().getInfo(oid);
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenCertificateIssuerDN() {
		return getTokenCertificateIssuerDN(null);
	}
	public java.lang.String getTokenCertificateIssuerDN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null && this.securityToken.getAccessToken().getCertificate()!=null &&
			this.securityToken.getAccessToken().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getAccessToken().getCertificate().getIssuer().toString();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenCertificateIssuerCN() {
		return getTokenCertificateIssuerCN(null);
	}
	public java.lang.String getTokenCertificateIssuerCN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null && this.securityToken.getAccessToken().getCertificate()!=null &&
			this.securityToken.getAccessToken().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getAccessToken().getCertificate().getIssuer().getCN();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenCertificateIssuerDNInfo(String oid) {
		return getTokenCertificateIssuerDNInfo(oid, null);
	}
	public java.lang.String getTokenCertificateIssuerDNInfo(String oid, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAccessToken()!=null && this.securityToken.getAccessToken().getCertificate()!=null &&
			this.securityToken.getAccessToken().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getAccessToken().getCertificate().getIssuer().getInfo(oid);
		}
		return correctValue(v, defaultValue);
	}
		
	
	
	
	public java.lang.String getPdndClientJson() {
		return getPdndClientJson(null);
	}
	public java.lang.String getPdndClientJson(String defaultValue) {
		return correctValue(getPdndClientJsonEngine(), defaultValue);
	}
	private String getPdndClientJsonEngine() {
		if(this.credenzialiMittente!=null && this.credenzialiMittente.getTokenPdndClientJson()!=null && this.credenzialiMittente.getTokenPdndClientJson().getCredenziale()!=null) {
			return this.credenzialiMittente.getTokenPdndClientJson().getCredenziale();
		}
		else if(this.securityToken!=null && this.securityToken.getPdnd()!=null && this.securityToken.getPdnd().getClientJson()!=null) {
			return this.securityToken.getPdnd().getClientJson();
		}
		return null;
	}
	
	public java.lang.String getPdndOrganizationJson() {
		return getPdndOrganizationJson(null);
	}
	public java.lang.String getPdndOrganizationJson(String defaultValue) {
		return correctValue(getPdndOrganizationJsonEngine(), defaultValue);
	}
	private String getPdndOrganizationJsonEngine() {
		if(this.credenzialiMittente!=null && this.credenzialiMittente.getTokenPdndOrganizationJson()!=null && this.credenzialiMittente.getTokenPdndOrganizationJson().getCredenziale()!=null) {
			return this.credenzialiMittente.getTokenPdndOrganizationJson().getCredenziale();
		}
		else if(this.securityToken!=null && this.securityToken.getPdnd()!=null && this.securityToken.getPdnd().getOrganizationJson()!=null) {
			return this.securityToken.getPdnd().getOrganizationJson();
		}
		return null;
	}
	
	public java.lang.String getPdndOrganizationName() {
		return getPdndOrganizationName(null);
	}
	public java.lang.String getPdndOrganizationName(String defaultValue) {
		String v = this.credenzialiMittente!=null && this.credenzialiMittente.getTokenPdndOrganizationName()!=null ? this.credenzialiMittente.getTokenPdndOrganizationName().getCredenziale() : null;
		if(v==null) {
			try {
				v = this.securityToken!=null && this.securityToken.getPdnd()!=null ? this.securityToken.getPdnd().getOrganizationName() : null;
			}catch(Exception e) {
				// ignore
			}
		}
		if(v==null) {
			PDNDTokenInfo info = this.getPDNDTokenOrganizationInfo();
			try {
				if(info!=null) {
					v = info.getOrganizationName(this.log);
				}
			}catch(Exception e) {
				// ignore
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getPdndOrganizationId() {
		return getPdndOrganizationId(null);
	}
	public java.lang.String getPdndOrganizationId(String defaultValue) {
		PDNDTokenInfo info = this.getPDNDTokenOrganizationInfo();
		String v = null;
		try {
			if(info!=null) {
				v = info.getOrganizationId(this.log);
			}
		}catch(Exception e) {
			// ignore
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getPdndOrganizationCategory() {
		return getPdndOrganizationCategory(null);
	}
	public java.lang.String getPdndOrganizationCategory(String defaultValue) {
		PDNDTokenInfo info = this.getPDNDTokenOrganizationInfo();
		String v = null;
		try {
			if(info!=null) {
				v = info.getOrganizationCategory(this.log);
			}
		}catch(Exception e) {
			// ignore
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getPdndOrganizationExternalOrigin() {
		return getPdndOrganizationExternalOrigin(null);
	}
	public java.lang.String getPdndOrganizationExternalOrigin(String defaultValue) {
		PDNDTokenInfo info = this.getPDNDTokenOrganizationInfo();
		String v = null;
		try {
			if(info!=null) {
				v = info.getOrganizationExternalOrigin(this.log);
			}
		}catch(Exception e) {
			// ignore
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getPdndOrganizationExternalId() {
		return getPdndOrganizationExternalId(null);
	}
	public java.lang.String getPdndOrganizationExternalId(String defaultValue) {
		PDNDTokenInfo info = this.getPDNDTokenOrganizationInfo();
		String v = null;
		try {
			if(info!=null) {
				v = info.getOrganizationExternalId(this.log);
			}
		}catch(Exception e) {
			// ignore
		}
		return correctValue(v, defaultValue);
	}
		
	private PDNDTokenInfo pdndTokenOrganizationInfo = null;
	private PDNDTokenInfo getPDNDTokenOrganizationInfo() {
		if(this.pdndTokenOrganizationInfo==null) {
			initPDNDTokenOrganizationInfo();
		}
		return this.pdndTokenOrganizationInfo;
	}
	private synchronized void initPDNDTokenOrganizationInfo() {
		if(this.pdndTokenOrganizationInfo==null) {
			this.pdndTokenOrganizationInfo=new PDNDTokenInfo();
			String organizationJson = getPdndOrganizationJsonEngine();
			if(organizationJson!=null) {
				PDNDTokenInfoDetails id = new PDNDTokenInfoDetails();
				id.setDetails(organizationJson);
				this.pdndTokenOrganizationInfo.setOrganization(id);
			}
		}
	}
	
	
	public java.lang.String getPdndClientId() {
		return getPdndClientId(null);
	}
	public java.lang.String getPdndClientId(String defaultValue) {
		PDNDTokenInfo info = this.getPDNDTokenClientInfo();
		String v = null;
		try {
			if(info!=null) {
				v = info.getClientId(this.log);
			}
		}catch(Exception e) {
			// ignore
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getPdndClientConsumerId() {
		return getPdndClientConsumerId(null);
	}
	public java.lang.String getPdndClientConsumerId(String defaultValue) {
		PDNDTokenInfo info = this.getPDNDTokenClientInfo();
		String v = null;
		try {
			if(info!=null) {
				v = info.getClientConsumerId(this.log);
			}
		}catch(Exception e) {
			// ignore
		}
		return correctValue(v, defaultValue);
	}
	
	private PDNDTokenInfo pdndTokenClientInfo = null;
	private PDNDTokenInfo getPDNDTokenClientInfo() {
		if(this.pdndTokenClientInfo==null) {
			initPDNDTokenClientInfo();
		}
		return this.pdndTokenClientInfo;
	}
	private synchronized void initPDNDTokenClientInfo() {
		if(this.pdndTokenClientInfo==null) {
			this.pdndTokenClientInfo=new PDNDTokenInfo();
			String clientJson = getPdndClientJsonEngine();
			if(clientJson!=null) {
				PDNDTokenInfoDetails id = new PDNDTokenInfoDetails();
				id.setDetails(clientJson);
				this.pdndTokenClientInfo.setClient(id);
			}
		}
	}
	
	public java.lang.String getAttribute(String attributeName) {
		return getAttribute(attributeName, null);
	}
	public java.lang.String getAttribute(String attributeName, String defaultValue) {
		String v = null;
		if(attributeName!=null &&
				this.informazioniAttributi!=null && 
				(
						this.informazioniAttributi.isMultipleAttributeAuthorities()==null || 
						this.informazioniAttributi.isMultipleAttributeAuthorities().getValue()==null || 
						!this.informazioniAttributi.isMultipleAttributeAuthorities().getValue()
				) &&
				this.informazioniAttributi.getAttributes()!=null && 
				this.informazioniAttributi.getAttributes().containsKey(attributeName)) {
			Object valueAttributeObject = this.informazioniAttributi.getAttributes().get(attributeName);
			if(valueAttributeObject!=null) {
				List<String> lClaimValues = TokenUtilities.getClaimValues(valueAttributeObject);
				v = TokenUtilities.getClaimValuesAsString(lClaimValues);
			}
		}	
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getAttributeByAA(String attributeAuthorityName, String attributeName) {
		return getAttributeByAA(attributeAuthorityName, attributeName, null);
	}
	public java.lang.String getAttributeByAA(String attributeAuthorityName, String attributeName, String defaultValue) {
		String v = null;
		if(attributeAuthorityName!=null && attributeName!=null) {
			v = getAttributeByAAEngine(attributeAuthorityName, attributeName);
		}	
		return correctValue(v, defaultValue);
	}
	private java.lang.String getAttributeByAAEngine(String attributeAuthorityName, String attributeName) {
		if(this.informazioniAttributi!=null && 
				this.informazioniAttributi.isMultipleAttributeAuthorities()!=null && 
				this.informazioniAttributi.isMultipleAttributeAuthorities().getValue()!=null && 
				this.informazioniAttributi.isMultipleAttributeAuthorities().getValue().booleanValue() &&
				this.informazioniAttributi.getAttributes()!=null && 
				this.informazioniAttributi.getAttributes().containsKey(attributeAuthorityName)) {
			Object o = this.informazioniAttributi.getAttributes().get(attributeAuthorityName);
			if(o instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) o;
				if(map.containsKey(attributeName)) {
					Object valueAttributeObject = map.get(attributeName);
					if(valueAttributeObject!=null) {
						List<String> lClaimValues = TokenUtilities.getClaimValues(valueAttributeObject);
						return TokenUtilities.getClaimValuesAsString(lClaimValues);
					}
				}
			}
		}
		return null;
	}
	
	public java.lang.String getRetrievedAccessToken() {
		return getRetrievedAccessToken(null);
	}
	public java.lang.String getRetrievedAccessToken(String defaultValue) {
		String v = null;
		if(this.informazioniNegoziazioneToken!=null) {
			v = this.informazioniNegoziazioneToken.getAccessToken();
		}	
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getRetrievedTokenClaim(String tokenClaim) {
		return getRetrievedTokenClaim(tokenClaim, null);
	}
	public java.lang.String getRetrievedTokenClaim(String tokenClaim, String defaultValue) {
		String v = null;
		if(tokenClaim!=null &&
				this.informazioniNegoziazioneToken!=null && 
				this.informazioniNegoziazioneToken.getClaims()!=null && 
				this.informazioniNegoziazioneToken.getClaims().containsKey(tokenClaim)) {
			Object valueInfoTokenObject = this.informazioniNegoziazioneToken.getClaims().get(tokenClaim);
			if(valueInfoTokenObject!=null) {
				List<String> lClaimValues = TokenUtilities.getClaimValues(valueInfoTokenObject);
				v = TokenUtilities.getClaimValuesAsString(lClaimValues);
			}
		}	
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getRetrievedTokenRequestTransactionId() {
		return getRetrievedTokenRequestTransactionId(null);
	}
	public java.lang.String getRetrievedTokenRequestTransactionId(String defaultValue) {
		String v = null;
		if(this.informazioniNegoziazioneToken!=null && this.informazioniNegoziazioneToken.getRequest()!=null) {
			v = this.informazioniNegoziazioneToken.getRequest().getTransactionId();
		}	
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getRetrievedTokenRequestGrantType() {
		return getRetrievedTokenRequestGrantType(null);
	}
	public java.lang.String getRetrievedTokenRequestGrantType(String defaultValue) {
		String v = null;
		if(this.informazioniNegoziazioneToken!=null && this.informazioniNegoziazioneToken.getRequest()!=null) {
			v = this.informazioniNegoziazioneToken.getRequest().getGrantType();
		}	
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getRetrievedTokenRequestJwtClientAssertion() {
		return getRetrievedTokenRequestJwtClientAssertion(null);
	}
	public java.lang.String getRetrievedTokenRequestJwtClientAssertion(String defaultValue) {
		String v = null;
		if(this.informazioniNegoziazioneToken!=null && this.informazioniNegoziazioneToken.getRequest()!=null 
				&& this.informazioniNegoziazioneToken.getRequest().getJwtClientAssertion()!=null) {
			v = this.informazioniNegoziazioneToken.getRequest().getJwtClientAssertion().getToken();
		}	
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getRetrievedTokenRequestClientId() {
		return getRetrievedTokenRequestClientId(null);
	}
	public java.lang.String getRetrievedTokenRequestClientId(String defaultValue) {
		String v = null;
		if(this.informazioniNegoziazioneToken!=null && this.informazioniNegoziazioneToken.getRequest()!=null) {
			v = this.informazioniNegoziazioneToken.getRequest().getClientId();
		}	
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getRetrievedTokenRequestClientToken() {
		return getRetrievedTokenRequestClientToken(null);
	}
	public java.lang.String getRetrievedTokenRequestClientToken(String defaultValue) {
		String v = null;
		if(this.informazioniNegoziazioneToken!=null && this.informazioniNegoziazioneToken.getRequest()!=null) {
			v = this.informazioniNegoziazioneToken.getRequest().getClientToken();
		}	
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getRetrievedTokenRequestUsername() {
		return getRetrievedTokenRequestUsername(null);
	}
	public java.lang.String getRetrievedTokenRequestUsername(String defaultValue) {
		String v = null;
		if(this.informazioniNegoziazioneToken!=null && this.informazioniNegoziazioneToken.getRequest()!=null) {
			v = this.informazioniNegoziazioneToken.getRequest().getUsername();
		}	
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getRetrievedTokenRequestUrl() {
		return getRetrievedTokenRequestUrl(null);
	}
	public java.lang.String getRetrievedTokenRequestUrl(String defaultValue) {
		String v = null;
		if(this.informazioniNegoziazioneToken!=null && this.informazioniNegoziazioneToken.getRequest()!=null) {
			v = this.informazioniNegoziazioneToken.getRequest().getEndpoint();
		}	
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getClientIP() {
		return getClientIP(null);
	}
	public java.lang.String getClientIP(String defaultValue) {
		return correctValue(this.transazione.getSocketClientAddress(), defaultValue);
	}
	
	public java.lang.String getForwardedIP() {
		return getForwardedIP(null);
	}
	public java.lang.String getForwardedIP(String defaultValue) {
		return correctValue(this.transazione.getTransportClientAddress(), defaultValue);
	}
	
	public java.lang.String getRequesterIP() {
		return getRequesterIP(null);
	}
	public java.lang.String getRequesterIP(String defaultValue) {
		String forwardedIP = getForwardedIP();
		if(forwardedIP!=null && StringUtils.isNotEmpty(forwardedIP)) {
			return forwardedIP;
		}
		return getClientIP(defaultValue);
	}

	private DatiMittente convertToDatiMittenteEngine() {
		
		DatiMittente datiMittente = new DatiMittente();
		
		datiMittente.setTokenUsername(getTokenUsername());
		datiMittente.setTokenSubject(getTokenSubject());
		datiMittente.setTokenIssuer(getTokenIssuer());
		
		datiMittente.setTokenClientId(getTokenClientId());
		datiMittente.setTokenClient(getTokenClientApplication());
		datiMittente.setTokenClientTipoSoggettoFruitore(getTokenClientOrganizationType());
		datiMittente.setTokenClientNomeSoggettoFruitore(getTokenClientOrganization());
		datiMittente.setTokenClientSoggettoFruitore(getTokenClientOrganizationId());
		
		datiMittente.setPdndOrganizationName(getPdndOrganizationName());
		
		datiMittente.setTipoTrasportoMittente(getPrincipalAuthType());
		datiMittente.setTrasportoMittente(getPrincipal());
		
		datiMittente.setServizioApplicativoFruitore(getApplication());
		
		datiMittente.setSoggettoFruitore(getSenderId());
		datiMittente.setTipoSoggettoFruitore(getSenderType());
		datiMittente.setNomeSoggettoFruitore(getSender());

		datiMittente.setPddRuolo(this.transazione.getPddRuolo());
		datiMittente.setSoggettoOperativo(null);
	
		datiMittente.setTransportClientAddress(this.getForwardedIP());
		datiMittente.setSocketClientAddress(getClientIP());
		
		return datiMittente;
	}
	
	public java.lang.String getRequester() {
		return getRequester(null);
	}
	public java.lang.String getRequester(String defaultValue) {
		DatiMittente datiMittente = this.convertToDatiMittenteEngine();
		return correctValue(InfoMittenteFormatUtils.getRichiedente(datiMittente), defaultValue);
	}
	
	public java.lang.String getIpRequester() {
		return getIpRequester(null);
	}
	public java.lang.String getIpRequester(String defaultValue) {
		DatiMittente datiMittente = this.convertToDatiMittenteEngine();
		return correctValue(InfoMittenteFormatUtils.getIpRichiedente(datiMittente), defaultValue);
	}
	
	
	// altre informazioni
	
	public java.lang.String getState() {
		return getState(null);
	}
	public java.lang.String getState(String defaultValue) {
		return correctValue(this.transazione.getStato(), defaultValue);
	}

	public java.lang.String getProfile() {
		return getProfile(null);
	}
	public java.lang.String getProfile(String defaultValue) {
		return correctValue(this.transazione.getProtocollo(), defaultValue);
	}
	
	public java.lang.String getProfileLabel() {
		return getProfileLabel(null);
	}
	public java.lang.String getProfileLabel(String defaultValue) {
		String p = null;
		try {
			p = NamingUtils.getLabelProtocollo(this.getProfile());
		}catch(Exception e) {
			// possible null value
		}
		return correctValue(p, defaultValue);
	}
	
	public java.lang.String getCorrelationType() {
		return getCorrelationType(null);
	}
	public java.lang.String getCorrelationType(String defaultValue) {
		return correctValue(this.transazione.getTipoServizioCorrelato(), defaultValue);
	}
	public java.lang.String getCorrelationApi() {
		return getCorrelationApi(null);
	}
	public java.lang.String getCorrelationApi(String defaultValue) {
		return correctValue(this.transazione.getNomeServizioCorrelato(), defaultValue);
	}
	
	public int getDuplicateRequest() {
		return getDuplicateRequest(null);
	}
	public int getDuplicateRequest(String defaultValue) {
		return correctInteger(this.transazione.getDuplicatiRichiesta(), defaultValue);
	}
	public int getDuplicateResponse() {
		return getDuplicateResponse(null);
	}
	public int getDuplicateResponse(String defaultValue) {
		return correctInteger(this.transazione.getDuplicatiRisposta(), defaultValue);
	}
	
	
	
	// fault messaggi
	
	public java.lang.String getInFault() {
		return getInFault(null);
	}
	public java.lang.String getInFault(String defaultValue) {
		if(this.transazione.getPddRuolo()!=null) {
			switch (this.transazione.getPddRuolo()) {
			case DELEGATA:
				return getFaultCooperazioneEngine(defaultValue);
			case APPLICATIVA:
				return getFaultIntegrazioneEngine(defaultValue);
			default:
				break;
			}
		}
		return correctValue(null, defaultValue);
	}
	
	public java.lang.String getOutFault() {
		return getOutFault(null);
	}
	public java.lang.String getOutFault(String defaultValue) {
		if(this.transazione.getPddRuolo()!=null) {
			switch (this.transazione.getPddRuolo()) {
			case DELEGATA:
				return getFaultIntegrazioneEngine(defaultValue);
			case APPLICATIVA:
				return getFaultCooperazioneEngine(defaultValue);
			default:
				break;
			}
		}
		return correctValue(null, defaultValue);
	}
	
	private java.lang.String getFaultIntegrazioneEngine(String defaultValue) {
		return correctValue(this.transazione.getFaultIntegrazione(), defaultValue);
	}
	private java.lang.String getFormatoFaultIntegrazioneEngine(String defaultValue) {
		return correctValue(this.transazione.getFormatoFaultIntegrazione(), defaultValue);
	}
	private java.lang.String getFaultCooperazioneEngine(String defaultValue) {
		return correctValue(this.transazione.getFaultCooperazione(), defaultValue);
	}
	private java.lang.String getFormatoFaultCooperazioneEngine(String defaultValue) {
		return correctValue(this.transazione.getFormatoFaultCooperazione(), defaultValue);
	}
	
	
	// richiesta ingresso
	
	public java.lang.String getInRequestContentType() {
		return getInRequestContentType(null);
	}
	public java.lang.String getInRequestContentType(String defaultValue) {
		String s = null;
		if(this.richiestaIngresso!=null) {
			s = this.richiestaIngresso.getContentType();
		}
		return correctValue(s, defaultValue);
	}
	
	public java.lang.String getInRequestContent() {
		return getInRequestContent(null);
	}
	public java.lang.String getInRequestContent(String defaultValue) {
		byte[] c = null;
		if(this.richiestaIngresso!=null &&
			this.richiestaIngresso.getBody()!=null && this.richiestaIngresso.getBody().size()>0) {
			c = this.richiestaIngresso.getBody().toByteArray();
		}
		return correctByteArray(c, defaultValue);
	}
	
	public int getInRequestSize() {
		return getInRequestSize(null);
	}
	public int getInRequestSize(String defaultValue) {
		byte[] c = null;
		if(this.richiestaIngresso!=null &&
			this.richiestaIngresso.getBody()!=null && this.richiestaIngresso.getBody().size()>0) {
			c = this.richiestaIngresso.getBody().toByteArray();
		}
		int size = 0;
		if(c!=null) {
			size = c.length;
		}
		else if(this.transazione.getRichiestaIngressoBytes()!=null) {
			size = this.transazione.getRichiestaIngressoBytes().intValue();
		}
		return this.correctInteger(size, defaultValue);
	}
	
	public java.lang.String getInRequestHeader(String name) {
		return getInRequestHeader(name, this.headerMultiValueSeparator);
	}
	public java.lang.String getInRequestHeader(String name, String multiValueSeparator) {
		String s = null;
		if(this.richiestaIngresso!=null && this.richiestaIngresso.getHeaders()!=null) {
			List<String> values = TransportUtils.getRawObject(this.richiestaIngresso.getHeaders(), name);
			s = this.format(values, multiValueSeparator);
		}
		return correctValue(s, null);
	}
	
	public java.lang.String getInRequestHeaders() {
		return getInRequestHeaders(null, null, null, null, null);
	}
	public java.lang.String getInRequestHeaders(String defaultValue) {
		return getInRequestHeaders(defaultValue, null, null, null, null);
	}
	public java.lang.String getInRequestHeaders(String hdrsSeparatpr, String hdrSeparator, String hdrPrefix, String hdrSuffix) {
		return getInRequestHeaders(null, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
	}
	public java.lang.String getInRequestHeaders(String defaultValue, String hdrsSeparatpr, String hdrSeparator, String hdrPrefix, String hdrSuffix) {
		if(this.richiestaIngresso!=null) {
			return formatHeaders(this.richiestaIngresso.getHeaders(), defaultValue, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
		}
		else {
			String s = null;
			return correctValue(s, defaultValue);
		}
	}
	
	
	
	// richiesta uscita
	
	public java.lang.String getOutRequestContentType() {
		return getOutRequestContentType(null);
	}
	public java.lang.String getOutRequestContentType(String defaultValue) {
		String s = null;
		if(this.richiestaUscita!=null) {
			s = this.richiestaUscita.getContentType();
		}
		return correctValue(s, defaultValue);
	}
	
	public java.lang.String getOutRequestContent() {
		return getOutRequestContent(null);
	}
	public java.lang.String getOutRequestContent(String defaultValue) {
		byte[] c = null;
		if(this.richiestaUscita!=null &&
			this.richiestaUscita.getBody()!=null && this.richiestaUscita.getBody().size()>0) {
			c = this.richiestaUscita.getBody().toByteArray();
		}
		return correctByteArray(c, defaultValue);
	}
	
	public int getOutRequestSize() {
		return getOutRequestSize(null);
	}
	public int getOutRequestSize(String defaultValue) {
		byte[] c = null;
		if(this.richiestaUscita!=null &&
			this.richiestaUscita.getBody()!=null && this.richiestaUscita.getBody().size()>0) {
			c = this.richiestaUscita.getBody().toByteArray();
		}
		int size = 0;
		if(c!=null) {
			size = c.length;
		}
		else if(this.transazione.getRichiestaUscitaBytes()!=null) {
			size = this.transazione.getRichiestaUscitaBytes().intValue();
		}
		return this.correctInteger(size, defaultValue);
	}

	public java.lang.String getOutRequestHeader(String name) {
		return getOutRequestHeader(name, this.headerMultiValueSeparator);
	}
	public java.lang.String getOutRequestHeader(String name, String multiValueSeparator) {
		String s = null;
		if(this.richiestaUscita!=null && this.richiestaUscita.getHeaders()!=null) {
			List<String> values = TransportUtils.getRawObject(this.richiestaUscita.getHeaders(), name);
			s = this.format(values, multiValueSeparator);
		}
		return correctValue(s, null);
	}
	
	public java.lang.String getOutRequestHeaders() {
		return getOutRequestHeaders(null, null, null, null, null);
	}
	public java.lang.String getOutRequestHeaders(String defaultValue) {
		return getOutRequestHeaders(defaultValue, null, null, null, null);
	}
	public java.lang.String getOutRequestHeaders(String hdrsSeparatpr, String hdrSeparator, String hdrPrefix, String hdrSuffix) {
		return getOutRequestHeaders(null, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
	}
	public java.lang.String getOutRequestHeaders(String defaultValue, String hdrsSeparatpr, String hdrSeparator, String hdrPrefix, String hdrSuffix) {
		if(this.richiestaUscita!=null) {
			return formatHeaders(this.richiestaUscita.getHeaders(), defaultValue, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
		}
		else {
			String s = null;
			return correctValue(s, defaultValue);
		}
	}
	
	
	// risposta ingresso
	
	public java.lang.String getInResponseContentType() {
		return getInResponseContentType(null);
	}
	public java.lang.String getInResponseContentType(String defaultValue) {
		String s = null;
		if(this.rispostaIngresso!=null) {
			s = this.rispostaIngresso.getContentType();
		}
		return correctValue(s, defaultValue);
	}
	
	public java.lang.String getInResponseContent() {
		return getInResponseContent(null);
	}
	public java.lang.String getInResponseContent(String defaultValue) {
		byte[] c = null;
		if(this.rispostaIngresso!=null &&
			this.rispostaIngresso.getBody()!=null && this.rispostaIngresso.getBody().size()>0) {
			c = this.rispostaIngresso.getBody().toByteArray();
		}
		return correctByteArray(c, defaultValue);
	}
	
	public int getInResponseSize() {
		return getInResponseSize(null);
	}
	public int getInResponseSize(String defaultValue) {
		byte[] c = null;
		if(this.rispostaIngresso!=null &&
			this.rispostaIngresso.getBody()!=null && this.rispostaIngresso.getBody().size()>0) {
			c = this.rispostaIngresso.getBody().toByteArray();
		}
		int size = 0;
		if(c!=null) {
			size = c.length;
		}
		else if(this.transazione.getRispostaIngressoBytes()!=null) {
			size = this.transazione.getRispostaIngressoBytes().intValue();
		}
		return this.correctInteger(size, defaultValue);
	}
	
	public java.lang.String getInResponseHeader(String name) {
		return getInResponseHeader(name, this.headerMultiValueSeparator);
	}
	public java.lang.String getInResponseHeader(String name, String multiValueSeparator) {
		String s = null;
		if(this.rispostaIngresso!=null && this.rispostaIngresso.getHeaders()!=null) {
			List<String> values = TransportUtils.getRawObject(this.rispostaIngresso.getHeaders(), name);
			s = this.format(values, multiValueSeparator);
		}
		return correctValue(s, null);
	}
	
	public java.lang.String getInResponseHeaders() {
		return getInResponseHeaders(null, null, null, null, null);
	}
	public java.lang.String getInResponseHeaders(String defaultValue) {
		return getInResponseHeaders(defaultValue, null, null, null, null);
	}
	public java.lang.String getInResponseHeaders(String hdrsSeparatpr, String hdrSeparator, String hdrPrefix, String hdrSuffix) {
		return getInResponseHeaders(null, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
	}
	public java.lang.String getInResponseHeaders(String defaultValue, String hdrsSeparatpr, String hdrSeparator, String hdrPrefix, String hdrSuffix) {
		if(this.rispostaIngresso!=null) {
			return formatHeaders(this.rispostaIngresso.getHeaders(), defaultValue, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
		}
		else {
			String s = null;
			return correctValue(s, defaultValue);
		}
	}
	
	
	// risposta uscita
	
	public java.lang.String getOutResponseContentType() {
		return getOutResponseContentType(null);
	}
	public java.lang.String getOutResponseContentType(String defaultValue) {
		String s = null;
		if(this.rispostaUscita!=null) {
			s = this.rispostaUscita.getContentType();
		}
		return correctValue(s, defaultValue);
	}
	
	public java.lang.String getOutResponseContent() {
		return getOutResponseContent(null);
	}
	public java.lang.String getOutResponseContent(String defaultValue) {
		byte[] c = null;
		if(this.rispostaUscita!=null &&
			this.rispostaUscita.getBody()!=null && this.rispostaUscita.getBody().size()>0) {
			c = this.rispostaUscita.getBody().toByteArray();
		}
		return correctByteArray(c, defaultValue);
	}
	
	public int getOutResponseSize() {
		return getOutResponseSize(null);
	}
	public int getOutResponseSize(String defaultValue) {
		byte[] c = null;
		if(this.rispostaUscita!=null &&
			this.rispostaUscita.getBody()!=null && this.rispostaUscita.getBody().size()>0) {
			c = this.rispostaUscita.getBody().toByteArray();
		}
		int size = 0;
		if(c!=null) {
			size = c.length;
		}
		else if(this.transazione.getRispostaUscitaBytes()!=null) {
			size = this.transazione.getRispostaUscitaBytes().intValue();
		}
		return this.correctInteger(size, defaultValue);
	}
	
	public java.lang.String getOutResponseHeader(String name) {
		return getOutResponseHeader(name, this.headerMultiValueSeparator);
	}
	public java.lang.String getOutResponseHeader(String name, String multiValueSeparator) {
		String s = null;
		if(this.rispostaUscita!=null && this.rispostaUscita.getHeaders()!=null) {
			List<String> values = TransportUtils.getRawObject(this.rispostaUscita.getHeaders(), name);
			s = this.format(values, multiValueSeparator);
		}
		return correctValue(s, null);
	}
	
	public java.lang.String getOutResponseHeaders() {
		return getOutResponseHeaders(null, null, null, null, null);
	}
	public java.lang.String getOutResponseHeaders(String defaultValue) {
		return getOutResponseHeaders(defaultValue, null, null, null, null);
	}
	public java.lang.String getOutResponseHeaders(String hdrsSeparatpr, String hdrSeparator, String hdrPrefix, String hdrSuffix) {
		return getOutResponseHeaders(null, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
	}
	public java.lang.String getOutResponseHeaders(String defaultValue, String hdrsSeparatpr, String hdrSeparator, String hdrPrefix, String hdrSuffix) {
		if(this.rispostaUscita!=null) {
			return formatHeaders(this.rispostaUscita.getHeaders(), defaultValue, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
		}
		else {
			String s = null;
			return correctValue(s, defaultValue);
		}
	}
	
	
	// traccia
	
	private java.lang.String getPropertiesKeysEngine(String [] p, String separator, String defaultValue){
		String v = null;
		if(p!=null && p.length>0) {
			StringBuilder sb = new StringBuilder();
			for (String key : p) {
				if(sb.length()>0) {
					sb.append(separator);
				}
				sb.append(key);
			}
			v = sb.toString();
		}
		return correctValue(v,defaultValue);
	}
	private java.lang.String getPropertiesEngine(String [] pNames, String [] pValues, String propertySeparator, String valueSeparator, String defaultValue){
		String v = null;
		if(pNames!=null && pNames.length>0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < pNames.length; i++) {
				String key = pNames[i];
				if(sb.length()>0) {
					sb.append(propertySeparator);
				}
				sb.append(key);
				sb.append(valueSeparator);
				sb.append(pValues[i]);
			}
			return sb.toString();
		}
		return correctValue(v,defaultValue);
	}
	
	public java.lang.String getRequestProperty(String name) {
		return getRequestProperty(name, null);
	}
	public java.lang.String getRequestProperty(String name, String defaultValue) {
		String s = null;
		if(this.tracciaRichiesta!=null && this.tracciaRichiesta.getBusta()!=null) {
			s = this.tracciaRichiesta.getBusta().getProperty(name);
		}
		return correctValue(s, defaultValue);
	}
	public java.lang.String getRequestPropertiesKeys(){
		return getRequestPropertiesKeys(null);
	}
	public java.lang.String getRequestPropertiesKeys(String defaultValue){
		return getRequestPropertiesKeys(InfoConfigurazione.KEYS_SEPARATOR,defaultValue);
	}
	public java.lang.String getRequestPropertiesKeys(String separator, String defaultValue){
		String [] p = this.tracciaRichiesta.getPropertiesNames();
		return getPropertiesKeysEngine(p, separator, defaultValue);
	}
	public java.lang.String getRequestProperties(){
		return getRequestProperties(null);
	}
	public java.lang.String getRequestProperties(String defaultValue){
		return getRequestProperties(InfoConfigurazione.PROPERTY_SEPARATOR,InfoConfigurazione.VALUE_SEPARATOR,defaultValue);
	}
	public java.lang.String getRequestProperties(String propertySeparator, String valueSeparator){
		return getRequestProperties(propertySeparator, valueSeparator, null);
	}
	public java.lang.String getRequestProperties(String propertySeparator, String valueSeparator, String defaultValue){
		String [] pNames = this.tracciaRichiesta.getPropertiesNames();
		String [] pValues = this.tracciaRichiesta.getPropertiesValues();
		return getPropertiesEngine(pNames, pValues, propertySeparator, valueSeparator, defaultValue);
	}
	
	public java.lang.String getResponseProperty(String name) {
		return getResponseProperty(name, null);
	}
	public java.lang.String getResponseProperty(String name, String defaultValue) {
		String s = null;
		if(this.tracciaRisposta!=null && this.tracciaRisposta.getBusta()!=null) {
			s = this.tracciaRisposta.getBusta().getProperty(name);
		}
		return correctValue(s, defaultValue);
	}
	public java.lang.String getResponsePropertiesKeys(){
		return getResponsePropertiesKeys(null);
	}
	public java.lang.String getResponsePropertiesKeys(String defaultValue){
		return getResponsePropertiesKeys(InfoConfigurazione.KEYS_SEPARATOR,defaultValue);
	}
	public java.lang.String getResponsePropertiesKeys(String separator, String defaultValue){
		String [] p = this.tracciaRisposta.getPropertiesNames();
		return getPropertiesKeysEngine(p, separator, defaultValue);
	}
	public java.lang.String getResponseProperties(){
		return getResponseProperties(null);
	}
	public java.lang.String getResponseProperties(String defaultValue){
		return getResponseProperties(InfoConfigurazione.PROPERTY_SEPARATOR,InfoConfigurazione.VALUE_SEPARATOR,defaultValue);
	}
	public java.lang.String getResponseProperties(String propertySeparator, String valueSeparator){
		return getResponseProperties(propertySeparator, valueSeparator, null);
	}
	public java.lang.String getResponseProperties(String propertySeparator, String valueSeparator, String defaultValue){
		String [] pNames = this.tracciaRisposta.getPropertiesNames();
		String [] pValues = this.tracciaRisposta.getPropertiesValues();
		return getPropertiesEngine(pNames, pValues, propertySeparator, valueSeparator, defaultValue);
	}
	
	
	// modi (Authorization)
	
	public java.lang.String getTokenModIAuthorizationRaw() {
		return getTokenModIAuthorizationRaw(null);
	}
	public java.lang.String getTokenModIAuthorizationRaw(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null) {
			v = this.securityToken.getAuthorization().getToken();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationHeaderRaw() {
		return getTokenModIAuthorizationHeaderRaw(null);
	}
	public java.lang.String getTokenModIAuthorizationHeaderRaw(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null) {
			v = this.securityToken.getAuthorization().getHeader();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationDecodedHeader() {
		return getTokenModIAuthorizationDecodedHeader(null);
	}
	public java.lang.String getTokenModIAuthorizationDecodedHeader(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null) {
			v = this.securityToken.getAuthorization().getDecodedHeader();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationHeaderClaim(String tokenClaim) {
		return getTokenModIAuthorizationHeaderClaim(tokenClaim, null);
	}
	public java.lang.String getTokenModIAuthorizationHeaderClaim(String tokenClaim, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null) {
			try {
				v = this.securityToken.getAuthorization().getHeaderClaim(tokenClaim);
			}catch(Exception t) {
				this.log.error("getTokenModIAuthorizationHeaderClaim("+tokenClaim+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationHeaderClaims() {
		return getTokenModIAuthorizationHeaderClaims(",", "=", null);
	}
	public java.lang.String getTokenModIAuthorizationHeaderClaims(String claimSeparator, String nameValueSeparator, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null) {
			try {
				Map<String, String> map = this.securityToken.getAuthorization().getHeaderClaims();
				v = formatTokenClaims(map, claimSeparator, nameValueSeparator);
			}catch(Exception t) {
				this.log.error("getTokenModIAuthorizationHeaderClaims("+claimSeparator+","+nameValueSeparator+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationPayloadRaw() {
		return getTokenModIAuthorizationPayloadRaw(null);
	}
	public java.lang.String getTokenModIAuthorizationPayloadRaw(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null) {
			v = this.securityToken.getAuthorization().getPayload();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationDecodedPayload() {
		return getTokenModIAuthorizationDecodedPayload(null);
	}
	public java.lang.String getTokenModIAuthorizationDecodedPayload(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null) {
			v = this.securityToken.getAuthorization().getDecodedPayload();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationPayloadClaim(String tokenClaim) {
		return getTokenModIAuthorizationPayloadClaim(tokenClaim, null);
	}
	public java.lang.String getTokenModIAuthorizationPayloadClaim(String tokenClaim, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null) {
			try {
				v = this.securityToken.getAuthorization().getPayloadClaim(tokenClaim);
			}catch(Exception t) {
				this.log.error("getTokenModIAuthorizationPayloadClaim("+tokenClaim+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationPayloadClaims() {
		return getTokenModIAuthorizationPayloadClaims(",", "=", null);
	}
	public java.lang.String getTokenModIAuthorizationPayloadClaims(String claimSeparator, String nameValueSeparator, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null) {
			try {
				Map<String, String> map = this.securityToken.getAuthorization().getPayloadClaims();
				v = formatTokenClaims(map, claimSeparator, nameValueSeparator);
			}catch(Exception t) {
				this.log.error("getTokenModIAuthorizationPayloadClaims("+claimSeparator+","+nameValueSeparator+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationCertificateSubjectDN() {
		return getTokenModIAuthorizationCertificateSubjectDN(null);
	}
	public java.lang.String getTokenModIAuthorizationCertificateSubjectDN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null && this.securityToken.getAuthorization().getCertificate()!=null &&
			this.securityToken.getAuthorization().getCertificate().getSubject()!=null) {
			v = this.securityToken.getAuthorization().getCertificate().getSubject().toString();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationCertificateSubjectCN() {
		return getTokenModIAuthorizationCertificateSubjectCN(null);
	}
	public java.lang.String getTokenModIAuthorizationCertificateSubjectCN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null && this.securityToken.getAuthorization().getCertificate()!=null &&
			this.securityToken.getAuthorization().getCertificate().getSubject()!=null) {
			v = this.securityToken.getAuthorization().getCertificate().getSubject().getCN();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationCertificateSubjectDNInfo(String oid) {
		return getTokenModIAuthorizationCertificateSubjectDNInfo(oid, null);
	}
	public java.lang.String getTokenModIAuthorizationCertificateSubjectDNInfo(String oid, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null && this.securityToken.getAuthorization().getCertificate()!=null &&
			this.securityToken.getAuthorization().getCertificate().getSubject()!=null) {
			v = this.securityToken.getAuthorization().getCertificate().getSubject().getInfo(oid);
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationCertificateIssuerDN() {
		return getTokenModIAuthorizationCertificateIssuerDN(null);
	}
	public java.lang.String getTokenModIAuthorizationCertificateIssuerDN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null && this.securityToken.getAuthorization().getCertificate()!=null &&
			this.securityToken.getAuthorization().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getAuthorization().getCertificate().getIssuer().toString();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationCertificateIssuerCN() {
		return getTokenModIAuthorizationCertificateIssuerCN(null);
	}
	public java.lang.String getTokenModIAuthorizationCertificateIssuerCN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null && this.securityToken.getAuthorization().getCertificate()!=null &&
			this.securityToken.getAuthorization().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getAuthorization().getCertificate().getIssuer().getCN();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuthorizationCertificateIssuerDNInfo(String oid) {
		return getTokenModIAuthorizationCertificateIssuerDNInfo(oid, null);
	}
	public java.lang.String getTokenModIAuthorizationCertificateIssuerDNInfo(String oid, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAuthorization()!=null && this.securityToken.getAuthorization().getCertificate()!=null &&
			this.securityToken.getAuthorization().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getAuthorization().getCertificate().getIssuer().getInfo(oid);
		}
		return correctValue(v, defaultValue);
	}
	
	
	
	// modi (Integrity)
	
	public java.lang.String getTokenModIIntegrityRaw() {
		return getTokenModIIntegrityRaw(null);
	}
	public java.lang.String getTokenModIIntegrityRaw(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null) {
			v = this.securityToken.getIntegrity().getToken();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityHeaderRaw() {
		return getTokenModIIntegrityHeaderRaw(null);
	}
	public java.lang.String getTokenModIIntegrityHeaderRaw(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null) {
			v = this.securityToken.getIntegrity().getHeader();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityDecodedHeader() {
		return getTokenModIIntegrityDecodedHeader(null);
	}
	public java.lang.String getTokenModIIntegrityDecodedHeader(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null) {
			v = this.securityToken.getIntegrity().getDecodedHeader();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityHeaderClaim(String tokenClaim) {
		return getTokenModIIntegrityHeaderClaim(tokenClaim, null);
	}
	public java.lang.String getTokenModIIntegrityHeaderClaim(String tokenClaim, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null) {
			try {
				v = this.securityToken.getIntegrity().getHeaderClaim(tokenClaim);
			}catch(Exception t) {
				this.log.error("getTokenModIIntegrityHeaderClaim("+tokenClaim+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityHeaderClaims() {
		return getTokenModIIntegrityHeaderClaims(",", "=", null);
	}
	public java.lang.String getTokenModIIntegrityHeaderClaims(String claimSeparator, String nameValueSeparator, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null) {
			try {
				Map<String, String> map = this.securityToken.getIntegrity().getHeaderClaims();
				v = formatTokenClaims(map, claimSeparator, nameValueSeparator);
			}catch(Exception t) {
				this.log.error("getTokenModIIntegrityHeaderClaims("+claimSeparator+","+nameValueSeparator+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityPayloadRaw() {
		return getTokenModIIntegrityPayloadRaw(null);
	}
	public java.lang.String getTokenModIIntegrityPayloadRaw(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null) {
			v = this.securityToken.getIntegrity().getPayload();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityDecodedPayload() {
		return getTokenModIIntegrityDecodedPayload(null);
	}
	public java.lang.String getTokenModIIntegrityDecodedPayload(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null) {
			v = this.securityToken.getIntegrity().getDecodedPayload();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityPayloadClaim(String tokenClaim) {
		return getTokenModIIntegrityPayloadClaim(tokenClaim, null);
	}
	public java.lang.String getTokenModIIntegrityPayloadClaim(String tokenClaim, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null) {
			try {
				v = this.securityToken.getIntegrity().getPayloadClaim(tokenClaim);
			}catch(Exception t) {
				this.log.error("getTokenModIIntegrityPayloadClaim("+tokenClaim+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityPayloadClaims() {
		return getTokenModIIntegrityPayloadClaims(",", "=", null);
	}
	public java.lang.String getTokenModIIntegrityPayloadClaims(String claimSeparator, String nameValueSeparator, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null) {
			try {
				Map<String, String> map = this.securityToken.getIntegrity().getPayloadClaims();
				v = formatTokenClaims(map, claimSeparator, nameValueSeparator);
			}catch(Exception t) {
				this.log.error("getTokenModIIntegrityPayloadClaims("+claimSeparator+","+nameValueSeparator+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityCertificateSubjectDN() {
		return getTokenModIIntegrityCertificateSubjectDN(null);
	}
	public java.lang.String getTokenModIIntegrityCertificateSubjectDN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null && this.securityToken.getIntegrity().getCertificate()!=null &&
			this.securityToken.getIntegrity().getCertificate().getSubject()!=null) {
			v = this.securityToken.getIntegrity().getCertificate().getSubject().toString();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityCertificateSubjectCN() {
		return getTokenModIIntegrityCertificateSubjectCN(null);
	}
	public java.lang.String getTokenModIIntegrityCertificateSubjectCN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null && this.securityToken.getIntegrity().getCertificate()!=null &&
			this.securityToken.getIntegrity().getCertificate().getSubject()!=null) {
			v = this.securityToken.getIntegrity().getCertificate().getSubject().getCN();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityCertificateSubjectDNInfo(String oid) {
		return getTokenModIIntegrityCertificateSubjectDNInfo(oid, null);
	}
	public java.lang.String getTokenModIIntegrityCertificateSubjectDNInfo(String oid, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null && this.securityToken.getIntegrity().getCertificate()!=null &&
			this.securityToken.getIntegrity().getCertificate().getSubject()!=null) {
			v = this.securityToken.getIntegrity().getCertificate().getSubject().getInfo(oid);
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityCertificateIssuerDN() {
		return getTokenModIIntegrityCertificateIssuerDN(null);
	}
	public java.lang.String getTokenModIIntegrityCertificateIssuerDN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null && this.securityToken.getIntegrity().getCertificate()!=null &&
			this.securityToken.getIntegrity().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getIntegrity().getCertificate().getIssuer().toString();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityCertificateIssuerCN() {
		return getTokenModIIntegrityCertificateIssuerCN(null);
	}
	public java.lang.String getTokenModIIntegrityCertificateIssuerCN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null && this.securityToken.getIntegrity().getCertificate()!=null &&
			this.securityToken.getIntegrity().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getIntegrity().getCertificate().getIssuer().getCN();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIIntegrityCertificateIssuerDNInfo(String oid) {
		return getTokenModIIntegrityCertificateIssuerDNInfo(oid, null);
	}
	public java.lang.String getTokenModIIntegrityCertificateIssuerDNInfo(String oid, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getIntegrity()!=null && this.securityToken.getIntegrity().getCertificate()!=null &&
			this.securityToken.getIntegrity().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getIntegrity().getCertificate().getIssuer().getInfo(oid);
		}
		return correctValue(v, defaultValue);
	}
	
	
	
	// modi (Audit)
	
	public java.lang.String getTokenModIAuditRaw() {
		return getTokenModIAuditRaw(null);
	}
	public java.lang.String getTokenModIAuditRaw(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null) {
			v = this.securityToken.getAudit().getToken();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditHeaderRaw() {
		return getTokenModIAuditHeaderRaw(null);
	}
	public java.lang.String getTokenModIAuditHeaderRaw(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null) {
			v = this.securityToken.getAudit().getHeader();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditDecodedHeader() {
		return getTokenModIAuditDecodedHeader(null);
	}
	public java.lang.String getTokenModIAuditDecodedHeader(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null) {
			v = this.securityToken.getAudit().getDecodedHeader();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditHeaderClaim(String tokenClaim) {
		return getTokenModIAuditHeaderClaim(tokenClaim, null);
	}
	public java.lang.String getTokenModIAuditHeaderClaim(String tokenClaim, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null) {
			try {
				v = this.securityToken.getAudit().getHeaderClaim(tokenClaim);
			}catch(Exception t) {
				this.log.error("getTokenModIAuditHeaderClaim("+tokenClaim+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditHeaderClaims() {
		return getTokenModIAuditHeaderClaims(",", "=", null);
	}
	public java.lang.String getTokenModIAuditHeaderClaims(String claimSeparator, String nameValueSeparator, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null) {
			try {
				Map<String, String> map = this.securityToken.getAudit().getHeaderClaims();
				v = formatTokenClaims(map, claimSeparator, nameValueSeparator);
			}catch(Exception t) {
				this.log.error("getTokenModIAuditHeaderClaims("+claimSeparator+","+nameValueSeparator+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditPayloadRaw() {
		return getTokenModIAuditPayloadRaw(null);
	}
	public java.lang.String getTokenModIAuditPayloadRaw(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null) {
			v = this.securityToken.getAudit().getPayload();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditDecodedPayload() {
		return getTokenModIAuditDecodedPayload(null);
	}
	public java.lang.String getTokenModIAuditDecodedPayload(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null) {
			v = this.securityToken.getAudit().getDecodedPayload();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditPayloadClaim(String tokenClaim) {
		return getTokenModIAuditPayloadClaim(tokenClaim, null);
	}
	public java.lang.String getTokenModIAuditPayloadClaim(String tokenClaim, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null) {
			try {
				v = this.securityToken.getAudit().getPayloadClaim(tokenClaim);
			}catch(Exception t) {
				this.log.error("getTokenModIAuditPayloadClaim("+tokenClaim+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditPayloadClaims() {
		return getTokenModIAuditPayloadClaims(",", "=", null);
	}
	public java.lang.String getTokenModIAuditPayloadClaims(String claimSeparator, String nameValueSeparator, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null) {
			try {
				Map<String, String> map = this.securityToken.getAudit().getPayloadClaims();
				v = formatTokenClaims(map, claimSeparator, nameValueSeparator);
			}catch(Exception t) {
				this.log.error("getTokenModIAuditPayloadClaims("+claimSeparator+","+nameValueSeparator+")"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditCertificateSubjectDN() {
		return getTokenModIAuditCertificateSubjectDN(null);
	}
	public java.lang.String getTokenModIAuditCertificateSubjectDN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null && this.securityToken.getAudit().getCertificate()!=null &&
			this.securityToken.getAudit().getCertificate().getSubject()!=null) {
			v = this.securityToken.getAudit().getCertificate().getSubject().toString();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditCertificateSubjectCN() {
		return getTokenModIAuditCertificateSubjectCN(null);
	}
	public java.lang.String getTokenModIAuditCertificateSubjectCN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null && this.securityToken.getAudit().getCertificate()!=null &&
			this.securityToken.getAudit().getCertificate().getSubject()!=null) {
			v = this.securityToken.getAudit().getCertificate().getSubject().getCN();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditCertificateSubjectDNInfo(String oid) {
		return getTokenModIAuditCertificateSubjectDNInfo(oid, null);
	}
	public java.lang.String getTokenModIAuditCertificateSubjectDNInfo(String oid, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null && this.securityToken.getAudit().getCertificate()!=null &&
			this.securityToken.getAudit().getCertificate().getSubject()!=null) {
			v = this.securityToken.getAudit().getCertificate().getSubject().getInfo(oid);
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditCertificateIssuerDN() {
		return getTokenModIAuditCertificateIssuerDN(null);
	}
	public java.lang.String getTokenModIAuditCertificateIssuerDN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null && this.securityToken.getAudit().getCertificate()!=null &&
			this.securityToken.getAudit().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getAudit().getCertificate().getIssuer().toString();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditCertificateIssuerCN() {
		return getTokenModIAuditCertificateIssuerCN(null);
	}
	public java.lang.String getTokenModIAuditCertificateIssuerCN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null && this.securityToken.getAudit().getCertificate()!=null &&
			this.securityToken.getAudit().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getAudit().getCertificate().getIssuer().getCN();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModIAuditCertificateIssuerDNInfo(String oid) {
		return getTokenModIAuditCertificateIssuerDNInfo(oid, null);
	}
	public java.lang.String getTokenModIAuditCertificateIssuerDNInfo(String oid, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getAudit()!=null && this.securityToken.getAudit().getCertificate()!=null &&
			this.securityToken.getAudit().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getAudit().getCertificate().getIssuer().getInfo(oid);
		}
		return correctValue(v, defaultValue);
	}
	
	
	
	// modi (Soap)
	
	public java.lang.String getTokenModISoapRaw() {
		return getTokenModISoapRaw(null);
	}
	public java.lang.String getTokenModISoapRaw(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getEnvelope()!=null) {
			try {
				XMLUtils xmlUtils = XMLUtils.getInstance();
				v = xmlUtils.toString(this.securityToken.getEnvelope().getToken(),true);
			}catch(Exception t) {
				this.log.error("getTokenModISoapRaw"+getErrorSuffix(t),t);
			}
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModISoapCertificateSubjectDN() {
		return getTokenModISoapCertificateSubjectDN(null);
	}
	public java.lang.String getTokenModISoapCertificateSubjectDN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getEnvelope()!=null && this.securityToken.getEnvelope().getCertificate()!=null &&
			this.securityToken.getEnvelope().getCertificate().getSubject()!=null) {
			v = this.securityToken.getEnvelope().getCertificate().getSubject().toString();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModISoapCertificateSubjectCN() {
		return getTokenModISoapCertificateSubjectCN(null);
	}
	public java.lang.String getTokenModISoapCertificateSubjectCN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getEnvelope()!=null && this.securityToken.getEnvelope().getCertificate()!=null &&
			this.securityToken.getEnvelope().getCertificate().getSubject()!=null) {
			v = this.securityToken.getEnvelope().getCertificate().getSubject().getCN();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModISoapCertificateSubjectDNInfo(String oid) {
		return getTokenModISoapCertificateSubjectDNInfo(oid, null);
	}
	public java.lang.String getTokenModISoapCertificateSubjectDNInfo(String oid, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getEnvelope()!=null && this.securityToken.getEnvelope().getCertificate()!=null &&
			this.securityToken.getEnvelope().getCertificate().getSubject()!=null) {
			v = this.securityToken.getEnvelope().getCertificate().getSubject().getInfo(oid);
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModISoapCertificateIssuerDN() {
		return getTokenModISoapCertificateIssuerDN(null);
	}
	public java.lang.String getTokenModISoapCertificateIssuerDN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getEnvelope()!=null && this.securityToken.getEnvelope().getCertificate()!=null &&
			this.securityToken.getEnvelope().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getEnvelope().getCertificate().getIssuer().toString();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModISoapCertificateIssuerCN() {
		return getTokenModISoapCertificateIssuerCN(null);
	}
	public java.lang.String getTokenModISoapCertificateIssuerCN(String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getEnvelope()!=null && this.securityToken.getEnvelope().getCertificate()!=null &&
			this.securityToken.getEnvelope().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getEnvelope().getCertificate().getIssuer().getCN();
		}
		return correctValue(v, defaultValue);
	}
	
	public java.lang.String getTokenModISoapCertificateIssuerDNInfo(String oid) {
		return getTokenModISoapCertificateIssuerDNInfo(oid, null);
	}
	public java.lang.String getTokenModISoapCertificateIssuerDNInfo(String oid, String defaultValue) {
		String v = null;
		if(this.securityToken!=null && this.securityToken.getEnvelope()!=null && this.securityToken.getEnvelope().getCertificate()!=null &&
			this.securityToken.getEnvelope().getCertificate().getIssuer()!=null) {
			v = this.securityToken.getEnvelope().getCertificate().getIssuer().getInfo(oid);
		}
		return correctValue(v, defaultValue);
	}
	
	
	
	// address
	
	public java.lang.String getHostAddress() {
		return getHostAddress(null);
	}
	public java.lang.String getHostAddress(String defaultValue) {
		try {
	    	return InetAddress.getLocalHost().getHostAddress();
	    }catch(Exception e) {
	    	this.log.error("local ip: "+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	
	public java.lang.String getHostName() {
		return getHostName(null);
	}
	public java.lang.String getHostName(String defaultValue) {
		try {
	    	return InetAddress.getLocalHost().getHostName();
	    }catch(Exception e) {
	    	this.log.error("local hostname: "+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	
	
	// env
	
	public java.lang.String getSystemProperty(String name) {
		return getSystemProperty(name, null);
	}
	public java.lang.String getSystemProperty(String name, String defaultValue) {
		String s = System.getenv(name);
		return correctValue(s, defaultValue);
	}
	
	
	// java
	
	public java.lang.String getJavaProperty(String name) {
		return getJavaProperty(name, null);
	}
	public java.lang.String getJavaProperty(String name, String defaultValue) {
		String s = System.getProperty(name);
		return correctValue(s, defaultValue);
	}
	
	
	// properties
	
	public java.lang.String getProperty(String name) throws CoreException {
		return getPropertyEngine(name, null, true);
	}
	public java.lang.String getProperty(String name, String defaultValue) throws CoreException {
		return getPropertyEngine(name, defaultValue, false);
	}
	private java.lang.String getPropertyEngine(String name, String defaultValue, boolean required) throws CoreException {
		String s = this.properties!=null ? this.properties.get(name) : null;
		if(s==null && required) {
			throw new CoreException("Property '"+name+"' not found");
		}
		boolean escapeV = true;
		return correctValue(s, defaultValue, !escapeV);
	}
	protected void addProperty(String name, String value) {
		this.properties.put(name, value);
	}
	
	public java.lang.String getPropertyUnion(String left, String right, String separator) throws CoreException {
		return getPropertyUnion(left, right, separator, null);
	}
	public java.lang.String getPropertyUnion(String left, String right, String separator, String defaultValue) throws CoreException {
		StringBuilder sb = new StringBuilder();
		
		String pLeft = this.getProperty(left, left); // metto come default se stessa in modo che se non la trovo uso lei.
		if(pLeft!=null && pLeft.length()>0) {
			sb.append(pLeft);
		}
		
		String pRight = this.getProperty(right, right); // metto come default se stessa in modo che se non la trovo uso lei.
		if(pRight!=null && pRight.length()>0) {
			
			if(sb.length()>0) {
				sb.append(separator);
			}
			
			sb.append(pRight);
		}
		
		if(sb.length()>0) {
			return sb.toString();
		}
		else {
			return correctValue(null, defaultValue);
		}
	}
	
	public java.lang.String getContextProperty(String name) {
		return getContextProperty(name, null);
	}
	public java.lang.String getContextProperty(String name, String defaultValue) {
		return correctValue(this.infoConfigurazione.getContextProperty(name),defaultValue);
	}
	public java.lang.String getContextPropertiesKeys(){
		return getContextPropertiesKeys(null);
	}
	public java.lang.String getContextPropertiesKeys(String defaultValue){
		return getContextPropertiesKeys(InfoConfigurazione.KEYS_SEPARATOR,defaultValue);
	}
	public java.lang.String getContextPropertiesKeys(String separator, String defaultValue){
		return correctValue(this.infoConfigurazione.getContextPropertiesKeysAsString(separator),defaultValue);
	}
	public java.lang.String getContextProperties(){
		return getContextProperties(null);
	}
	public java.lang.String getContextProperties(String defaultValue){
		return getContextProperties(InfoConfigurazione.PROPERTY_SEPARATOR,InfoConfigurazione.VALUE_SEPARATOR,defaultValue);
	}
	public java.lang.String getContextProperties(String propertySeparator, String valueSeparator){
		return getContextProperties(propertySeparator, valueSeparator, null);
	}
	public java.lang.String getContextProperties(String propertySeparator, String valueSeparator, String defaultValue){
		return correctValue(this.infoConfigurazione.getContextPropertiesAsString(propertySeparator,valueSeparator),defaultValue);
	}
	
	
	// Tracking Phase
	
	public String getTrackingPhase() {
		return this.trackingPhase!=null ? this.trackingPhase.name() : "-";
	}
	

	// UTILITY
	
	private String escape(String v) {
		if(this.escape!=null && !this.escape.isEmpty()) {
			Set<String> specialCharacters = this.escape.keySet();
			boolean found = false;
			for (String specialChar : specialCharacters) {
				if(v.contains(specialChar)) {
					found = true;
					break;
				}
			} 
			if(!found) {
				return v;
			}
			return escapeEngine(v, specialCharacters);
		}
		else {
			return v;
		}
	}
	private String escapeEngine(String v, Set<String> specialCharacters) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < v.length(); i++) {
			String charAtI = v.charAt(i)+"";
			boolean foundSpecialChar = false;
			for (String specialChar : specialCharacters) {
				if(charAtI.equals(specialChar)) {
					String escapeChar = this.escape.get(specialChar);
					sb.append(escapeChar);
					foundSpecialChar = true;
					break;
				}
			}
			if(!foundSpecialChar) {
				sb.append(charAtI);
			}
		}
		return sb.toString();
	}
	
	private String correctValue(String v, String defaultValueParam) {
		return correctValue(v, defaultValueParam, true);
	}
	private String correctValue(String v, String defaultValueParam, boolean escape) {
		return correctValue(v, defaultValueParam, escape, true);
	}
	private String correctValue(String v, String defaultValueParam, boolean escape, boolean useBase64option) {
		String defaultValueTmp = this.defaultValue;
		if(defaultValueParam!=null) {
			defaultValueTmp = defaultValueParam;
		}
		String res = null;
		if(escape && !this.base64) { // se codifico in base64 l'escape non serve
			res = v!=null ? this.escape(v) : defaultValueTmp;
		}
		else {
			res = v!=null ? v : defaultValueTmp;
		}
		if(useBase64option && this.base64 && res!=null && res.length()>0) {
			return Base64Utilities.encodeAsString(res.getBytes());
		}
		else {
			return res;
		}
	}
	
	private String correctDate(java.util.Date d, String format, String timeZone, String defaultValueParam, String replace, String with) {
		if(d==null) {
			return correctValue(null, defaultValueParam);
		}
		String v = correctDateWithoutCorrectValue(d, format, timeZone, replace, with);
		return correctValue(v, defaultValueParam);
	}
	private String correctDateWithoutCorrectValue(java.util.Date d, String format, String timeZone, String replace, String with) {
		SimpleDateFormat formatter = null;
		// Uso JAVA_UTIL perche' mi funziona meglio per avere l'UTC
		if(timeZone==null) {
			formatter = DateUtils.getDateFormatter_ISO_8601_TZ(DateEngineType.JAVA_UTIL, format);
		}
		else {
			formatter = DateUtils.getDateFormatter(DateEngineType.JAVA_UTIL, format);
			formatter.setTimeZone( TimeZone.getTimeZone( timeZone ) );
		}
		String v = formatter.format(d);
		if(replace!=null && with!=null) {
			v = v.replaceAll(replace, with);
		}
		return v;
	}
	
	private long correctLong(Long v, String defaultValueParam) {
		long defaultValueTmp = this.defaultLongValue;
		if(defaultValueParam!=null) {
			defaultValueTmp = Long.valueOf(defaultValueParam);
		}
		return v!=null && v>=0 ? v : defaultValueTmp;
	}
	
	private int correctInteger(Integer v, String defaultValueParam) {
		int defaultValueTmp = this.defaultIntegerValue;
		if(defaultValueParam!=null) {
			defaultValueTmp = Integer.valueOf(defaultValueParam);
		}
		return v!=null && v>=0 ? v : defaultValueTmp;
	}
	
	private String correctByteArray(byte[] v, String defaultValueParam) {
		String s = null;
		if(v!=null && v.length>0) {
			if(this.base64) {
				s = Base64Utilities.encodeAsString(v);
			}
			else {
				s = new String(v);
			}
		}
		String defaultValueTmp = this.defaultValue;
		if(defaultValueParam!=null) {
			defaultValueTmp = defaultValueParam;
		}
		return s!=null ? this.escape(s) : defaultValueTmp;
	}
	
	private String formatHeaders( Map<String, List<String>>  mapParam, String defaultValueParam, String hdrsSeparatpr, String hdrSeparator, String hdrPrefix, String hdrSuffix) {
		
		Map<String, List<String>>  map = null;
		if(mapParam!=null && !mapParam.isEmpty()) {
			if(this.headerWhiteList!=null && !this.headerWhiteList.isEmpty()) {
				map = filterHeaders(true, this.headerWhiteList, mapParam);
			}
			else if(this.headerBlackList!=null && !this.headerBlackList.isEmpty()) {
				map = filterHeaders(false, this.headerBlackList, mapParam);
			}
			else {
				map = mapParam;
			}
		}
		
		return formatHeadersEngine(map, defaultValueParam, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
	}
	private Map<String, List<String>> filterHeaders(boolean white, List<String> filterList, Map<String, List<String>> sourceHeaders) {
		Map<String, List<String>> map = null;
		if(filterList!=null && !filterList.isEmpty()) {
			map = new HashMap<>();
			filterHeaders(map, white, filterList, sourceHeaders);
		}
		return map;
	}
	private void filterHeaders(Map<String, List<String>> map, boolean white, List<String> filterList, Map<String, List<String>> sourceHeaders){
		for (Map.Entry<String,List<String>> entry : sourceHeaders.entrySet()) {
			
			String hdr = entry.getKey();
			
			boolean find = false;
			for (String filterHdr : filterList) {
				if(filterHdr.equalsIgnoreCase(hdr)) {
					find = true;
					break;
				}
			}
			if(white) {
				if(find) {
					map.put(hdr, sourceHeaders.get(hdr));
				}
			}
			else {
				if(!find) {
					map.put(hdr, sourceHeaders.get(hdr));
				}
			}
		}
	}
	private String formatHeadersEngine(Map<String, List<String>>  map, String defaultValueParam, String hdrsSeparatpr, String hdrSeparator, String hdrPrefix, String hdrSuffix) {
		if(map!=null && !map.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String,List<String>> entry : map.entrySet()) {
				
				String hdr = entry.getKey();
				
				if(HttpConstants.RETURN_CODE.equals(hdr)) {
					continue;
				}
				
				formatHeadersEngine(sb, map, hdr, 
						defaultValueParam, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
				
			}
			String res = sb.toString();
			if(this.base64 && res!=null && res.length()>0) {
				return Base64Utilities.encodeAsString(res.getBytes());
			}
			else {
				return res;
			}
		}
		return correctValue(null, defaultValueParam);
	}
	private void formatHeadersEngine(StringBuilder sb, Map<String, List<String>>  map, String hdr, 
			String defaultValueParam, String hdrsSeparatpr, String hdrSeparator, String hdrPrefix, String hdrSuffix) {
		List<String> hdrValues = map.get(hdr);
		if(hdrValues!=null && !hdrValues.isEmpty()) {
			for (String hdrValue : hdrValues) {
				if(sb.length()>0) {
					sb.append(hdrsSeparatpr==null ? this.headersSeparator : hdrsSeparatpr);
				}
				sb.append(hdrPrefix==null ? this.headerPrefix : hdrPrefix);
				sb.append(this.correctValue(hdr, defaultValueParam, true, false));
				sb.append(hdrSeparator==null ? this.headerSeparator : hdrSeparator);
				sb.append(this.correctValue(hdrValue, defaultValueParam, true, false));
				sb.append(hdrSuffix == null ? this.headerSuffix: hdrSuffix);		
			}
		}
	}

	
	private String format(List<String> values, String separator) {
		String s = null;
		if(values!=null && !values.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String value : values) {
				if(sb.length()>0) {
					sb.append(separator);
				}
				sb.append(value);
			}
			s = sb.toString();
		}
		return s;
	}
	
	private String formatTokenClaims(Map<String, String> map, String claimSeparator, String nameValueSeparator) {
		String s = null;
		if(map!=null && !map.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String,String> entry : map.entrySet()) {
				String key = entry.getKey();
				String value = map.get(key);
				if(sb.length()>0) {
					sb.append(claimSeparator);
				}
				sb.append(key);
				sb.append(nameValueSeparator);
				sb.append(value!=null ? value : "");
			}
			s = sb.toString();
		}
		return s;
	}
}
