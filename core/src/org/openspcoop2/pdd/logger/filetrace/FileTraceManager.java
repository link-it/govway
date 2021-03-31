/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.utils.DynamicStringReplace;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**     
 * TransazioneLogTraceManager
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileTraceManager {
	
	private Logger log;
	private FileTraceConfig config;
	
	private Map<String, Object> dynamicMap = new HashMap<>();
	private Info t;
	private Info tBase64;
	

	public FileTraceManager(Logger log, FileTraceConfig config) throws Exception {
		this.config = config;
		this.log = log;
	}
	
	public void buildTransazioneInfo(Transazione transazioneDTO, Transaction transaction) throws ProtocolException {
		
		Messaggio richiestaIngresso = null;
		Messaggio richiestaUscita = null;
		Messaggio rispostaIngresso = null;
		Messaggio rispostaUscita = null;
		
		for(int i=0; i<transaction.sizeMessaggi(); i++){
			Messaggio messaggio = transaction.getMessaggio(i);
			TipoMessaggio tipoMessaggio = messaggio.getTipoMessaggio();
			switch (tipoMessaggio) {
			case RICHIESTA_INGRESSO_DUMP_BINARIO:
				richiestaIngresso = messaggio;
				break;
			case RICHIESTA_USCITA_DUMP_BINARIO:
				richiestaUscita = messaggio;
				break;
			case RISPOSTA_INGRESSO_DUMP_BINARIO:
				rispostaIngresso = messaggio;
				break;
			case RISPOSTA_USCITA_DUMP_BINARIO:
				rispostaUscita = messaggio;
				break;
			default:
				break;
			}
			
		}
		
		CredenzialiMittente credenzialiMittente = transaction.getCredenzialiMittente();
		
		boolean base64 = true;
		
		this.t = new Info(this.log, transazioneDTO, credenzialiMittente,
				transaction.getTracciaRichiesta(), transaction.getTracciaRisposta(),
				transaction.getMsgDiagnostici(),
				richiestaIngresso, richiestaUscita,
				rispostaIngresso, rispostaUscita,
				this.config, !base64);
		this.tBase64 = new Info(this.log, transazioneDTO, credenzialiMittente,
				transaction.getTracciaRichiesta(), transaction.getTracciaRisposta(),
				transaction.getMsgDiagnostici(),
				richiestaIngresso, richiestaUscita,
				rispostaIngresso, rispostaUscita,
				this.config, base64);
		
		this.dynamicMap.put("log", this.t);
		this.dynamicMap.put("logBase64", this.tBase64);
		
	}
		
	public void cleanResourcesForOnlyFileTrace(Transaction transaction) throws ProtocolException {
			
		List<TipoMessaggio> tipiDaEliminare_headers = transaction.getMessaggi_headers_onlyLogFileTrace();
		List<TipoMessaggio> tipiDaEliminare_body = transaction.getMessaggi_body_onlyLogFileTrace();
			
		List<TipoMessaggio> messaggiDaLiminare = new ArrayList<TipoMessaggio>();
		
		for(int i=0; i<transaction.sizeMessaggi(); i++){
			Messaggio messaggio = transaction.getMessaggio(i);
			TipoMessaggio tipoMessaggio = messaggio.getTipoMessaggio();
			
			boolean onlyLogFileTrace_headers = false;
			if(tipiDaEliminare_headers!=null && !tipiDaEliminare_headers.isEmpty()) {
				for (int j = 0; j < tipiDaEliminare_headers.size(); j++) {
					TipoMessaggio tipoMessaggio_headers = tipiDaEliminare_headers.get(j);
					if(tipoMessaggio_headers.equals(tipoMessaggio)) {
						onlyLogFileTrace_headers = true;
						break;
					}
				}
			}
			if(onlyLogFileTrace_headers) {
				tipiDaEliminare_headers.remove(tipoMessaggio);	
			}
			
			boolean onlyLogFileTrace_body = false;
			if(tipiDaEliminare_body!=null && !tipiDaEliminare_body.isEmpty()) {
				for (int j = 0; j < tipiDaEliminare_body.size(); j++) {
					TipoMessaggio tipoMessaggio_body = tipiDaEliminare_body.get(j);
					if(tipoMessaggio_body.equals(tipoMessaggio)) {
						onlyLogFileTrace_body = true;
						break;
					}
				}
			}
			if(onlyLogFileTrace_body) {
				tipiDaEliminare_body.remove(tipoMessaggio);	
			}
			
			if(onlyLogFileTrace_headers && onlyLogFileTrace_body) {
				messaggiDaLiminare.add(tipoMessaggio);
			}
			else if(onlyLogFileTrace_headers) {
				messaggio.getHeaders().clear();
			}
			else if(onlyLogFileTrace_body) {
				if(messaggio.getBody()!=null) {
					messaggio.getBody().unlock();
					messaggio.getBody().clearResources();
					messaggio.setBody(null);
				}
				messaggio.setContentType(null);
			}
		}
		
		if(messaggiDaLiminare!=null && !messaggiDaLiminare.isEmpty()) {
			while(messaggiDaLiminare.size()>0) {
				TipoMessaggio tipo = messaggiDaLiminare.remove(0);
				if(transaction.sizeMessaggi()>0) {
					for (int i = 0; i < transaction.sizeMessaggi(); i++) {
						if(tipo.equals(transaction.getMessaggio(i).getTipoMessaggio())) {
							if(transaction.getMessaggio(i).getBody()!=null){
								transaction.getMessaggio(i).getBody().unlock();
								transaction.getMessaggio(i).getBody().clearResources();
								transaction.getMessaggio(i).setBody(null);
							}
							transaction.removeMessaggio(i);
							break;
						}
					}
				}
			}
		}

	}
	
	public void invoke(TipoPdD tipoPdD, Context context) throws UtilsException {
		this.invoke(tipoPdD, context, null);
	}
	public void invoke(TipoPdD tipoPdD, Context context, Map<String, String> outputMap) throws UtilsException {
		List<String> topic = null;
		Map<String, Topic> topicMap = null;
		switch (tipoPdD) {
		case DELEGATA:
			topic = this.config.getTopicFruizioni();
			topicMap = this.config.getTopicFruizioneMap();
			break;
		case APPLICATIVA:
			topic = this.config.getTopicErogazioni();
			topicMap = this.config.getTopicErogazioniMap();
			break;
		default:
			break;
		}
		if(topic!=null && !topic.isEmpty()) {
			
			boolean requestSended = false;
			if(context!=null && context.containsKey(Costanti.RICHIESTA_INOLTRATA_BACKEND)) {
				Object o = context.getObject(Costanti.RICHIESTA_INOLTRATA_BACKEND);
				if(o!=null && o instanceof String) {
					String s = (String) o;
					if(Costanti.RICHIESTA_INOLTRATA_BACKEND_VALORE.equals(s)) {
						requestSended = true;
					}
				}
			}
			
			List<Topic> topicInvoke = new ArrayList<Topic>();
			for (String topicName : topic) {
				Topic topicConfig = topicMap.get(topicName);
				
				if(topicConfig.isOnlyRequestSended()) {
					if(!requestSended) {
						continue;
					}
				}
				if(topicConfig.isOnlyInRequestContentDefined()) {
					boolean contentDefined = this.t.getInRequestSize()>0;
					if(!contentDefined) {
						continue;
					}
				}
				if(topicConfig.isOnlyOutRequestContentDefined()) {
					boolean contentDefined = this.t.getOutRequestSize()>0;
					if(!contentDefined) {
						continue;
					}
				}
				if(topicConfig.isOnlyInResponseContentDefined()) {
					boolean contentDefined = this.t.getInResponseSize()>0;
					if(!contentDefined) {
						continue;
					}
				}
				if(topicConfig.isOnlyOutResponseContentDefined()) {
					boolean contentDefined = this.t.getOutResponseSize()>0;
					if(!contentDefined) {
						continue;
					}
				}
				
				topicInvoke.add(topicConfig);
			}
			
			if(topicInvoke!=null && !topicInvoke.isEmpty()) {
				
				this.resolveProperties();
				
				for (Topic topicConfig : topicInvoke) {
					String value = this.resolve(topicConfig.getFormat());
					if(outputMap!=null) {
						outputMap.put(topicConfig.getNome(), value);
					}
					else {
						switch (this.config.getLogSeverity()) {
						case trace:
							topicConfig.getLog().trace(value);		
							break;
						case debug:
							topicConfig.getLog().debug(value);		
							break;
						case info:
							topicConfig.getLog().info(value);		
							break;
						case warn:
							topicConfig.getLog().warn(value);		
							break;
						case error:
							topicConfig.getLog().error(value);		
							break;
						}
					}
				}
			}
		}
	}
		
	private void resolveProperties() throws UtilsException {
		List<String> properties = this.config.getPropertiesSortKeys();
		if(properties!=null && !properties.isEmpty()) {
			for (String sortKey : properties) {
				String pName = this.config.getPropertiesNames().get(sortKey);
				String pValue = this.config.getPropertiesValues().get(sortKey);
				//System.out.println("SORT ["+sortKey+"] ["+pName+"]");
				String resolvedValue = this.resolve(pValue);
				this.t.addProperty(pName, resolvedValue);
				this.tBase64.addProperty(pName, resolvedValue);
			}
		}
	}
	
	private String resolve(String format) throws UtilsException {
		boolean complexField = false;
		return DynamicStringReplace.replace(format, this.dynamicMap, true, complexField);
	}
}
