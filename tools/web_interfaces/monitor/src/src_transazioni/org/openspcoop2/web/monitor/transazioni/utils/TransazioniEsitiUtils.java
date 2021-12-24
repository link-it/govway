/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.monitor.transazioni.utils;

import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.pdd.logger.info.InfoEsitoTransazioneFormatUtils;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

/**
 * TransazioniEsitiUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TransazioniEsitiUtils {
	
	public static boolean isEsitoOk(Integer esito, String protocollo){	
		return InfoEsitoTransazioneFormatUtils.isEsitoOk(LoggerManager.getPddMonitorCoreLogger(), esito, protocollo);
	}
	public static boolean isEsitoFaultApplicativo(Integer esito, String protocollo){	
		return InfoEsitoTransazioneFormatUtils.isEsitoFaultApplicativo(LoggerManager.getPddMonitorCoreLogger(), esito, protocollo);
	}
	public static boolean isEsitoKo(Integer esito, String protocollo){	
		return InfoEsitoTransazioneFormatUtils.isEsitoKo(LoggerManager.getPddMonitorCoreLogger(), esito, protocollo);
	}
	
	public static java.lang.String getEsitoLabel(Integer esito, String protocollo) {
		try{
			EsitoUtils esitoUtils = new EsitoUtils(LoggerManager.getPddMonitorCoreLogger(), protocollo);
			return esitoUtils.getEsitoLabelFromValue(esito, false);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo della label per l'esito ["+esito+"]: "+e.getMessage(),e);
			return "Conversione non riuscita";
		}
	}
	
	public static java.lang.String getEsitoLabelSyntetic(Integer esito, String protocollo, Integer httpStatusCode, int tipoApi) {
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),protocollo);
			String esitoLabel = esitiProperties.getEsitoLabelSyntetic(esito);
			if(httpStatusCode!=null && httpStatusCode>0) {
				if(esitiProperties.convertoToCode(EsitoTransazioneName.OK) == esito.intValue()) {
					esitoLabel = "HTTP " +httpStatusCode.intValue();
				}
				else if(esitiProperties.convertoToCode(EsitoTransazioneName.OK_PRESENZA_ANOMALIE) == esito.intValue()) {
					esitoLabel = "HTTP " +httpStatusCode.intValue()+" (Anomalia)";
				}
				else if(esitiProperties.convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA_WARNING_ONLY) == esito.intValue()) {
					esitoLabel = "HTTP " +httpStatusCode.intValue()+" (Rate Limiting)";
				}
				else if(esitiProperties.convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_MAX_THREADS_WARNING_ONLY) == esito.intValue()) {
					esitoLabel = "HTTP " +httpStatusCode.intValue()+" (Limite Richieste)";
				}
				else if(esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA) == esito.intValue()) {
					esitoLabel = "HTTP " +httpStatusCode.intValue()+" (Consegna Multipla in Corso)";
				}
				else if(esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_COMPLETATA) == esito.intValue()) {
					esitoLabel = "HTTP " +httpStatusCode.intValue()+" (Consegna Multipla)";
				}
				else if(esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_FALLITA) == esito.intValue()) {
					esitoLabel = "HTTP " +httpStatusCode.intValue()+" (Consegna Multipla Fallita)";
				}
				else if(esitiProperties.convertoToCode(EsitoTransazioneName.HTTP_3xx) == esito.intValue()) {
					esitoLabel = esitoLabel.replace("Risposta ", "").replace("3xx", httpStatusCode.intValue()+"");
				}
				else if(esitiProperties.convertoToCode(EsitoTransazioneName.HTTP_4xx) == esito.intValue()) {
					esitoLabel = esitoLabel.replace("Risposta ", "").replace("4xx", httpStatusCode.intValue()+"");
				}
				else if(esitiProperties.convertoToCode(EsitoTransazioneName.HTTP_5xx) == esito.intValue()) {
					esitoLabel = esitoLabel.replace("Risposta ", "").replace("5xx", httpStatusCode.intValue()+"");
				}
				else if(esitiProperties.convertoToCode(EsitoTransazioneName.MESSAGE_BOX) == esito.intValue()) {
					//esitoLabel = esitoLabel+" "+httpStatusCode.intValue();
				}
				else if(esitiProperties.convertoToCode(EsitoTransazioneName.ERRORE_APPLICATIVO) == esito.intValue()) {
					if(TipoAPI.REST.getValoreAsInt()==tipoApi) {
						esitoLabel = "Problem Details "+httpStatusCode.intValue();
					}
					else if(TipoAPI.SOAP.getValoreAsInt()==tipoApi) {
						esitoLabel = "SOAP Fault "+httpStatusCode.intValue();
					}
					else {
						esitoLabel = esitoLabel+" "+httpStatusCode.intValue();
					}
				}
				else if(esitiProperties.convertoToCode(EsitoTransazioneName.ERRORE_CONNESSIONE_CLIENT_NON_DISPONIBILE) == esito.intValue()) {
					// esitoLabel = esitoLabel+" "+httpStatusCode.intValue();
					// non ha senso aggiungere un codice http, la connessione client è stata interrotta
				}
				else {
					esitoLabel = esitoLabel+" "+httpStatusCode.intValue();
				}
			}
			return esitoLabel;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo della label per l'esito ["+esito+"]: "+e.getMessage(),e);
			return "Conversione non riuscita";
		}
	}
	
	public static java.lang.String getEsitoLabelDescription(Integer esito, String protocollo, Integer httpOutStatusCode, Integer httpInStatusCode, int tipoApi) {
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),protocollo);
			String esitoLabel = getEsitoLabel(esito, protocollo);
			String esitoRispostaIngresso = "";
			String esitoDescription = esitiProperties.getEsitoDescription(esito);
			if(httpOutStatusCode!=null) {
				if(httpOutStatusCode>0 && esitiProperties.convertoToCode(EsitoTransazioneName.HTTP_3xx) == esito.intValue()) {
					esitoLabel = esitoLabel.replace("3xx", httpOutStatusCode.intValue()+"");
				}
				else if(httpOutStatusCode>0 && esitiProperties.convertoToCode(EsitoTransazioneName.HTTP_4xx) == esito.intValue()) {
					esitoLabel = esitoLabel.replace("4xx", httpOutStatusCode.intValue()+"");
				}
				else if(httpOutStatusCode>0 && esitiProperties.convertoToCode(EsitoTransazioneName.HTTP_5xx) == esito.intValue()) {
					esitoLabel = esitoLabel.replace("5xx", httpOutStatusCode.intValue()+"");
				}
				else if(esitiProperties.convertoToCode(EsitoTransazioneName.ERRORE_APPLICATIVO) == esito.intValue()) {
					if(TipoAPI.REST.getValoreAsInt()==tipoApi) {
						esitoLabel = "Problem Details";
					}
					else if(TipoAPI.SOAP.getValoreAsInt()==tipoApi) {
						esitoLabel = "SOAP Fault";
					}
				}
				else {
					//esitoLabel = esitoLabel+" "+httpOutStatusCode.intValue();
				}

				if(httpInStatusCode!=null && httpInStatusCode.intValue()!=httpOutStatusCode.intValue()) {
					esitoRispostaIngresso = "<BR/>Codice Risposta API Invocata: "+httpInStatusCode.intValue();
				}
			}
			return esitoLabel + "<BR/>" + esitoDescription+esitoRispostaIngresso;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo della label per l'esito ["+esito+"]: "+e.getMessage(),e);
			return "Conversione non riuscita";
		}
	}
	
	public static java.lang.String getEsitoContestoLabel(String esitoContesto, String protocollo) {
		try{
			EsitoUtils esitoUtils = new EsitoUtils(LoggerManager.getPddMonitorCoreLogger(), protocollo);
			return esitoUtils.getEsitoContestoLabelFromValue(esitoContesto);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo della label per il contesto esito ["+esitoContesto+"]: "+e.getMessage(),e);
			return "Conversione non riuscita";
		}
	}
	
	
	public static String getEsitoStyleClass(Integer esito, String protocollo){

		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),protocollo);
			String name = esitiProperties.getEsitoName(esito);
			EsitoTransazioneName esitoName = EsitoTransazioneName.convertoTo(name);
			boolean casoSuccesso = esitiProperties.getEsitiCodeOk().contains(esito);
			if(EsitoTransazioneName.ERRORE_APPLICATIVO.equals(esitoName)){
				//return "icon-alert-orange";
				return "icon-alert-yellow";
			}
			else if(casoSuccesso){
				return "icon-verified-green";
			}
			else{
				return "icon-alert-red";
			}
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+esito+"]: "+e.getMessage(),e);
			return "icon-ko";
		}

	}
}
