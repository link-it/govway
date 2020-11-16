/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.info.DatiEsitoTransazione;
import org.openspcoop2.pdd.logger.info.DatiMittente;
import org.openspcoop2.pdd.logger.info.InfoEsitoTransazioneFormatUtils;
import org.openspcoop2.pdd.logger.info.InfoMittenteFormatUtils;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
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
import org.slf4j.Logger;

/**     
 * Transaction
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Info {
	
	private org.openspcoop2.core.transazioni.Transazione transazione;
	private EsitiProperties esitiProperties;
	private CredenzialiMittente credenzialiMittente;
	
	private Traccia tracciaRichiesta;
	private Traccia tracciaRisposta;
	
	private List<MsgDiagnostico> msgDiagnostici;
	
	private Messaggio richiestaIngresso;
	private Messaggio richiestaUscita;
	private Messaggio rispostaIngresso;
	private Messaggio rispostaUscita;
	
	private Map<String, String> escape;
	private String headersSeparator;
	private String headerSeparator;
	private String headerPrefix;
	private String headerSuffix;

	private String defaultValue="";
	private int defaultIntegerValue=0;
	private long defaultLongValue=0l;
	
	private Logger log;
	
	private boolean base64;
	
	private Map<String, String> properties = new HashMap<String, String>();
	
	public Info(Logger log,
			org.openspcoop2.core.transazioni.Transazione transazione, 
			CredenzialiMittente credenzialiMittente,
			Traccia tracciaRichiesta, Traccia tracciaRisposta,
			List<MsgDiagnostico> msgDiagnostici,
			Messaggio richiestaIngresso, Messaggio richiestaUscita,
			Messaggio rispostaIngresso, Messaggio rispostaUscita,
			FileTraceConfig config,
			boolean base64) throws ProtocolException {
		this.log = log;
		this.transazione = transazione;
		this.esitiProperties = EsitiProperties.getInstance(log, transazione.getProtocollo());
		this.credenzialiMittente = credenzialiMittente;
		this.tracciaRichiesta = tracciaRichiesta;
		this.tracciaRisposta = tracciaRisposta;
		this.msgDiagnostici = msgDiagnostici;
		this.richiestaIngresso = richiestaIngresso;
		this.richiestaUscita = richiestaUscita;
		this.rispostaIngresso = rispostaIngresso;
		this.rispostaUscita = rispostaUscita;
		this.escape = config.getEscape();
		this.headersSeparator = config.getHeadersSeparator();
		this.headerSeparator = config.getHeaderSeparator();
		this.headerPrefix = config.getHeaderPrefix();
		this.headerSuffix = config.getHeaderSuffix();
		this.base64 = base64;
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
		}catch(Throwable e) {
			this.log.error("Conversione esito fallita: "+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	
	public boolean isResultClassOk() {
		try {
			if(InfoEsitoTransazioneFormatUtils.isEsitoOk(this.log, this.transazione.getEsito(), this.esitiProperties)) {
				return true;
			}
			else {
				return false;
			}
		}catch(Throwable e) {
			this.log.error("Conversione esito fallita: "+e.getMessage(),e);
			return false;
		}
	}
	public boolean isResultClassFault() {
		try {
			if(InfoEsitoTransazioneFormatUtils.isEsitoFaultApplicativo(this.log, this.transazione.getEsito(), this.esitiProperties)) {
				return true;
			}
			else {
				return false;
			}
		}catch(Throwable e) {
			this.log.error("Conversione esito fallita: "+e.getMessage(),e);
			return false;
		}
	}
	public boolean isResultClassKo() {
		try {
			if(InfoEsitoTransazioneFormatUtils.isEsitoKo(this.log, this.transazione.getEsito(), this.esitiProperties)) {
				return true;
			}
			else {
				return false;
			}
		}catch(Throwable e) {
			this.log.error("Conversione esito fallita: "+e.getMessage(),e);
			return false;
		}
	}
	
	public int getResultAsInt() {
		return this.transazione.getEsito();
	}
	public String getResult() {
		return getResult(null);
	}
	public String getResult(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoName(this.transazione.getEsito()),defaultValue);
		}catch(Throwable e) {
			this.log.error("Conversione esito fallita: "+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	public String getResultDescription() {
		return getResultDescription(null);
	}
	public String getResultDescription(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoDescription(this.transazione.getEsito()),defaultValue);
		}catch(Throwable e) {
			this.log.error("Conversione esito fallita: "+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	public String getResultLabel() {
		return getResultLabel(null);
	}
	public String getResultLabel(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoLabel(this.transazione.getEsito()),defaultValue);
		}catch(Throwable e) {
			this.log.error("Conversione esito fallita: "+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	public String getResultLabelSyntetic() {
		return getResultLabelSyntetic(null);
	}
	public String getResultLabelSyntetic(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoLabelSyntetic(this.transazione.getEsito()),defaultValue);
		}catch(Throwable e) {
			this.log.error("Conversione esito fallita: "+e.getMessage(),e);
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
		}catch(Throwable e) {
			this.log.error("Conversione esito fallita: "+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	public String getSyncResultDescription() {
		return getSyncResultDescription(null);
	}
	public String getSyncResultDescription(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoDescription(this.transazione.getEsitoSincrono()),defaultValue);
		}catch(Throwable e) {
			this.log.error("Conversione esito fallita: "+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	public String getSyncResultLabel() {
		return getSyncResultLabel(null);
	}
	public String getSyncResultLabel(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoLabel(this.transazione.getEsitoSincrono()),defaultValue);
		}catch(Throwable e) {
			this.log.error("Conversione esito fallita: "+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
	}
	public String getSyncResultLabelSyntetic() {
		return getSyncResultLabelSyntetic(null);
	}
	public String getSyncResultLabelSyntetic(String defaultValue) {
		try {
			return correctValue(this.esitiProperties.getEsitoLabelSyntetic(this.transazione.getEsitoSincrono()),defaultValue);
		}catch(Throwable e) {
			this.log.error("Conversione esito fallita: "+e.getMessage(),e);
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
				int status = Integer.valueOf(codiceRisposta);
				reason = HttpUtilities.getHttpReason(status);
			}
		}catch(Throwable e) {
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
				int status = Integer.valueOf(codiceRisposta);
				reason = HttpUtilities.getHttpReason(status);
			}
		}catch(Throwable e) {
			this.log.error("Conversione http reason: "+e.getMessage(),e);
			return correctValue(null, defaultValue);
		}
		return correctValue(reason, defaultValue);
	}
	
	private DatiEsitoTransazione _convertToDatiEsitoTransazione() {
		
		DatiEsitoTransazione datiEsitoTransazione = new DatiEsitoTransazione();
		
		datiEsitoTransazione.setEsito(this.getResultAsInt());
		datiEsitoTransazione.setProtocollo(this.getProfile());
		
		datiEsitoTransazione.setFaultIntegrazione(this._getFaultIntegrazione(null));
		datiEsitoTransazione.setFormatoFaultIntegrazione(this._getFormatoFaultIntegrazione(null));
		
		datiEsitoTransazione.setFaultCooperazione(this._getFaultCooperazione(null));
		datiEsitoTransazione.setFormatoFaultCooperazione(this._getFormatoFaultCooperazione(null));
				
		datiEsitoTransazione.setPddRuolo(this.transazione.getPddRuolo());
		
		return datiEsitoTransazione;
		
	}
	
	public java.lang.String getErrorDetail() {
		return getErrorDetail(null);
	}
	public java.lang.String getErrorDetail(String defaultValue) {
		DatiEsitoTransazione datiEsitoTransazione = _convertToDatiEsitoTransazione();
		String dettaglioErroreResult = InfoEsitoTransazioneFormatUtils.getDettaglioErrore(this.log, datiEsitoTransazione, this.msgDiagnostici);
		return correctValue(dettaglioErroreResult, defaultValue);
	}
	
	public java.lang.String getDiagnostics() {
		return _getDiagnostics(false, "\n", DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ,null);
	}
	public java.lang.String getDiagnostics(String separator) {
		return _getDiagnostics(false, separator, DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ,null);
	}
	public java.lang.String getDiagnostics(String separator, String format) {
		return _getDiagnostics(false, separator, format, null);
	}
	public java.lang.String getDiagnostics(String separator, String format, String timeZone) {
		return _getDiagnostics(false, separator, format, timeZone);
	}
	public java.lang.String getErrorDiagnostics() {
		return _getDiagnostics(true, "\n", DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ,null);
	}
	public java.lang.String getErrorDiagnostics(String separator) {
		return _getDiagnostics(true, separator, DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ,null);
	}
	public java.lang.String getErrorDiagnostics(String separator, String format) {
		return _getDiagnostics(true, separator, format, null);
	}
	public java.lang.String getErrorDiagnostics(String separator, String format, String timeZone) {
		return _getDiagnostics(true, separator, format, timeZone);
	}
	private java.lang.String _getDiagnostics(boolean onlyErrors, String separator, String format, String timeZone) {
		StringBuilder sb = new StringBuilder();
		if(this.msgDiagnostici!=null && !this.msgDiagnostici.isEmpty()) {
			for (MsgDiagnostico msgDiagnostico : this.msgDiagnostici) {
				if(onlyErrors) {
					if(!(msgDiagnostico.getSeverita()<=LogLevels.SEVERITA_ERROR_INTEGRATION)) {
						continue;
					}
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
	
	
	// latenza
	
	public long getElapsedTime() {
		return getElapsedTime(null);
	}
	public long getElapsedTime(String defaultValue) {
		return this.correctLong(_getLatenzaTotale(), defaultValue);
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
	private Long _getLatenzaTotale() {
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
		return this.correctLong(_getLatenzaServizio(), defaultValue);
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
	private Long _getLatenzaServizio() {
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
		return this.correctLong(_getLatenzaPorta(), defaultValue);
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
	private Long _getLatenzaPorta() {
		Long l = null;
		Long latTotale = this._getLatenzaTotale();
		if(latTotale != null && latTotale>=0) {
			Long latServizio = this._getLatenzaServizio();
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
			}catch(Exception e) {}
		}
		return correctValue(nome, defaultValue);
	}
	public java.lang.String getRole() {
		return getRole(null);
	}
	public java.lang.String getRole(String defaultValue) {
		String s = null;
		if(this.transazione.getPddRuolo()!=null) {
			switch (this.transazione.getPddRuolo()) {
			case DELEGATA:
				s = "fruizione";
				break;
			default:
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
			}catch(Exception e) {}
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
			}catch(Exception e) {}
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
				try {
					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(parteComune);
					p = NamingUtils.getLabelAccordoServizioParteComune(idAccordo);
				}catch(Throwable t) {}
			}
		}catch(Exception e) {}
		return correctValue(p, defaultValue);
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
			}catch(Exception e) {}
		}
		return correctValue(nome, defaultValue);
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
		String url = correctValue(this.transazione.getLocationConnettore(), defaultValue);
		if(url.startsWith("[") && url.contains("] ")) {
			// [HttpType]  es.: [POST]
			return url.substring(url.indexOf("] ")+2);
		}
		return url;
	}
	
	public java.lang.String getInURL() {
		return getInURL(null);
	}
	public java.lang.String getInURL(String defaultValue) {
		String url = correctValue(this.transazione.getUrlInvocazione(), defaultValue);
		if(url.startsWith("[") && url.contains("] ")) {
			// [function]  es.: [in]
			return url.substring(url.indexOf("] ")+2);
		}
		return url;
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
				String solo_tipo_auth = tipo.substring(tipo.indexOf("_")+1, tipo.length());
				if(solo_tipo_auth!=null) {
					tipo = solo_tipo_auth;
				}
			}catch(Exception e) {}
		}
		return correctValue(tipo, defaultValue);
	}
	
	public java.lang.String getPrincipal() {
		return getPrincipal(null);
	}
	public java.lang.String getPrincipal(String defaultValue) {
		return correctValue(this.credenzialiMittente!=null && this.credenzialiMittente.getTrasporto()!=null ? this.credenzialiMittente.getTrasporto().getCredenziale() : null, defaultValue);
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
		return correctValue(this.credenzialiMittente!=null && this.credenzialiMittente.getToken_issuer()!=null ? this.credenzialiMittente.getToken_issuer().getCredenziale() : null, defaultValue);
	}
	
	public java.lang.String getTokenSubject() {
		return getTokenSubject(null);
	}
	public java.lang.String getTokenSubject(String defaultValue) {
		return correctValue(this.credenzialiMittente!=null && this.credenzialiMittente.getToken_subject()!=null ? this.credenzialiMittente.getToken_subject().getCredenziale() : null, defaultValue);
	}
	
	public java.lang.String getTokenClientId() {
		return getTokenClientId(null);
	}
	public java.lang.String getTokenClientId(String defaultValue) {
		return correctValue(this.credenzialiMittente!=null && this.credenzialiMittente.getToken_clientId()!=null ? this.credenzialiMittente.getToken_clientId().getCredenziale() : null, defaultValue);
	}
	
	public java.lang.String getTokenUsername() {
		return getTokenUsername(null);
	}
	public java.lang.String getTokenUsername(String defaultValue) {
		return correctValue(this.credenzialiMittente!=null && this.credenzialiMittente.getToken_username()!=null ? this.credenzialiMittente.getToken_username().getCredenziale() : null, defaultValue);
	}
	
	public java.lang.String getTokenMail() {
		return getTokenMail(null);
	}
	public java.lang.String getTokenMail(String defaultValue) {
		return correctValue(this.credenzialiMittente!=null && this.credenzialiMittente.getToken_eMail()!=null ? this.credenzialiMittente.getToken_eMail().getCredenziale() : null, defaultValue);
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

	private DatiMittente _convertToDatiMittente() {
		
		DatiMittente datiMittente = new DatiMittente();
		
		datiMittente.setTokenUsername(getTokenUsername());
		datiMittente.setTokenSubject(getTokenSubject());
		datiMittente.setTokenIssuer(getTokenIssuer());
		datiMittente.setTokenClientId(getTokenClientId());
		
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
		DatiMittente datiMittente = this._convertToDatiMittente();
		return correctValue(InfoMittenteFormatUtils.getRichiedente(datiMittente), defaultValue);
	}
	
	public java.lang.String getIpRequester() {
		return getIpRequester(null);
	}
	public java.lang.String getIpRequester(String defaultValue) {
		DatiMittente datiMittente = this._convertToDatiMittente();
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
		}catch(Exception e) {}
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
				return _getFaultCooperazione(defaultValue);
			case APPLICATIVA:
				return _getFaultIntegrazione(defaultValue);
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
				return _getFaultIntegrazione(defaultValue);
			case APPLICATIVA:
				return _getFaultCooperazione(defaultValue);
			default:
				break;
			}
		}
		return correctValue(null, defaultValue);
	}
	
	private java.lang.String _getFaultIntegrazione(String defaultValue) {
		return correctValue(this.transazione.getFaultIntegrazione(), defaultValue);
	}
	private java.lang.String _getFormatoFaultIntegrazione(String defaultValue) {
		return correctValue(this.transazione.getFormatoFaultIntegrazione(), defaultValue);
	}
	private java.lang.String _getFaultCooperazione(String defaultValue) {
		return correctValue(this.transazione.getFaultCooperazione(), defaultValue);
	}
	private java.lang.String _getFormatoFaultCooperazione(String defaultValue) {
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
		if(this.richiestaIngresso!=null) {
			c = this.richiestaIngresso.getBody();
		}
		return correctByteArray(c, defaultValue);
	}
	
	public int getInRequestSize() {
		return getInRequestSize(null);
	}
	public int getInRequestSize(String defaultValue) {
		byte[] c = null;
		if(this.richiestaIngresso!=null) {
			c = this.richiestaIngresso.getBody();
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
		String s = null;
		if(this.richiestaIngresso!=null && this.richiestaIngresso.getHeaders()!=null) {
			s = TransportUtils.get(this.richiestaIngresso.getHeaders(), name);
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
			return format(this.richiestaIngresso.getHeaders(), defaultValue, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
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
		if(this.richiestaUscita!=null) {
			c = this.richiestaUscita.getBody();
		}
		return correctByteArray(c, defaultValue);
	}
	
	public int getOutRequestSize() {
		return getOutRequestSize(null);
	}
	public int getOutRequestSize(String defaultValue) {
		byte[] c = null;
		if(this.richiestaUscita!=null) {
			c = this.richiestaUscita.getBody();
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
		String s = null;
		if(this.richiestaUscita!=null && this.richiestaUscita.getHeaders()!=null) {
			s = TransportUtils.get(this.richiestaUscita.getHeaders(), name);
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
			return format(this.richiestaUscita.getHeaders(), defaultValue, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
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
		if(this.rispostaIngresso!=null) {
			c = this.rispostaIngresso.getBody();
		}
		return correctByteArray(c, defaultValue);
	}
	
	public int getInResponseSize() {
		return getInResponseSize(null);
	}
	public int getInResponseSize(String defaultValue) {
		byte[] c = null;
		if(this.rispostaIngresso!=null) {
			c = this.rispostaIngresso.getBody();
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
		String s = null;
		if(this.rispostaIngresso!=null && this.rispostaIngresso.getHeaders()!=null) {
			s = TransportUtils.get(this.rispostaIngresso.getHeaders(), name);
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
			return format(this.rispostaIngresso.getHeaders(), defaultValue, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
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
		if(this.rispostaUscita!=null) {
			c = this.rispostaUscita.getBody();
		}
		return correctByteArray(c, defaultValue);
	}
	
	public int getOutResponseSize() {
		return getOutResponseSize(null);
	}
	public int getOutResponseSize(String defaultValue) {
		byte[] c = null;
		if(this.rispostaUscita!=null) {
			c = this.rispostaUscita.getBody();
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
		String s = null;
		if(this.rispostaUscita!=null && this.rispostaUscita.getHeaders()!=null) {
			s = TransportUtils.get(this.rispostaUscita.getHeaders(), name);
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
			return format(this.rispostaUscita.getHeaders(), defaultValue, hdrsSeparatpr, hdrSeparator, hdrPrefix, hdrSuffix);
		}
		else {
			String s = null;
			return correctValue(s, defaultValue);
		}
	}
	
	
	// traccia
	
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
	
	
	// address
	
	public java.lang.String getHostAddress() {
		return getHostAddress(null);
	}
	public java.lang.String getHostAddress(String defaultValue) {
		try {
	    	return InetAddress.getLocalHost().getHostAddress();
	    }catch(Throwable e) {
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
	    }catch(Throwable e) {
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
		return _getProperty(name, null, true);
	}
	public java.lang.String getProperty(String name, String defaultValue) throws CoreException {
		return _getProperty(name, defaultValue, false);
	}
	private java.lang.String _getProperty(String name, String defaultValue, boolean required) throws CoreException {
		String s = this.properties!=null ? this.properties.get(name) : null;
		if(s==null && required) {
			throw new CoreException("Property '"+name+"' not found");
		}
		boolean escape = true;
		return correctValue(s, defaultValue, !escape);
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
		else {
			return v;
		}
	}
	
	private String correctValue(String v, String defaultValueParam) {
		return correctValue(v, defaultValueParam, true);
	}
	private String correctValue(String v, String defaultValueParam, boolean escape) {
		String defaultValue = this.defaultValue;
		if(defaultValueParam!=null) {
			defaultValue = defaultValueParam;
		}
		String res = null;
		if(escape && !this.base64) { // se codifico in base64 l'escape non serve
			res = v!=null ? this.escape(v) : defaultValue;
		}
		else {
			res = v!=null ? v : defaultValue;
		}
		if(this.base64 && res!=null && res.length()>0) {
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
	
	@SuppressWarnings("unused")
	private long correctLong(Long v, String defaultValueParam) {
		long defaultValue = this.defaultLongValue;
		if(defaultValueParam!=null) {
			defaultValue = Long.valueOf(defaultValueParam);
		}
		return v!=null && v>=0 ? v : defaultValue;
	}
	
	private int correctInteger(Integer v, String defaultValueParam) {
		int defaultValue = this.defaultIntegerValue;
		if(defaultValueParam!=null) {
			defaultValue = Integer.valueOf(defaultValueParam);
		}
		return v!=null && v>=0 ? v : defaultValue;
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
		String defaultValue = this.defaultValue;
		if(defaultValueParam!=null) {
			defaultValue = defaultValueParam;
		}
		return s!=null ? this.escape(s) : defaultValue;
	}
	
	private String format( Map<String, String>  map, String defaultValueParam, String hdrsSeparatpr, String hdrSeparator, String hdrPrefix, String hdrSuffix) {
		if(map!=null && !map.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String hdr : map.keySet()) {
				
				if(HttpConstants.RETURN_CODE.equals(hdr)) {
					continue;
				}
				
				String hdrValue = map.get(hdr);
				if(sb.length()>0) {
					sb.append(hdrsSeparatpr==null ? this.headersSeparator : hdrsSeparatpr);
				}
				sb.append(hdrPrefix==null ? this.headerPrefix : hdrPrefix);
				sb.append(this.correctValue(hdr, defaultValueParam));
				sb.append(hdrSeparator==null ? this.headerSeparator : hdrSeparator);
				sb.append(this.correctValue(hdrValue, defaultValueParam));
				sb.append(hdrSuffix == null ? this.headerSuffix: hdrSuffix);
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
}
