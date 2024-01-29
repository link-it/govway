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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.SecurityToken;
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
	

	public FileTraceManager(Logger log, FileTraceConfig config) {
		this.config = config;
		this.log = log;
	}
	
	public void buildTransazioneInfo(IProtocolFactory<?> protocolFactory, Transazione transazioneDTO, Transaction transaction, 
			InformazioniToken informazioniToken,
			InformazioniAttributi informazioniAttributi,
			InformazioniNegoziazioneToken informazioniNegoziazioneToken,
			SecurityToken securityToken,
			Context context) throws ProtocolException {
		
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
		
		InfoConfigurazione infoConfigurazione = new InfoConfigurazione(transazioneDTO, context, credenzialiMittente);
		
		boolean base64 = true;
		
		this.t = new Info(this.log, protocolFactory, transazioneDTO, credenzialiMittente, 
				informazioniToken,
				informazioniAttributi,
				informazioniNegoziazioneToken,
				securityToken,
				transaction.getTracciaRichiesta(), transaction.getTracciaRisposta(),
				transaction.getMsgDiagnostici(),
				richiestaIngresso, richiestaUscita,
				rispostaIngresso, rispostaUscita,
				infoConfigurazione,
				this.config, !base64);
		this.tBase64 = new Info(this.log, protocolFactory, transazioneDTO, credenzialiMittente, 
				informazioniToken,
				informazioniAttributi,
				informazioniNegoziazioneToken,
				securityToken,
				transaction.getTracciaRichiesta(), transaction.getTracciaRisposta(),
				transaction.getMsgDiagnostici(),
				richiestaIngresso, richiestaUscita,
				rispostaIngresso, rispostaUscita,
				infoConfigurazione,
				this.config, base64);
		
		this.dynamicMap.put("log", this.t);
		this.dynamicMap.put("logBase64", this.tBase64);
		
	}
		
	public void cleanResourcesForOnlyFileTrace(Transaction transaction) throws ProtocolException {
			
		List<TipoMessaggio> tipiDaEliminareHeaders = transaction.getMessaggi_headers_onlyLogFileTrace();
		List<TipoMessaggio> tipiDaEliminareBody = transaction.getMessaggi_body_onlyLogFileTrace();
			
		List<TipoMessaggio> messaggiDaLiminare = new ArrayList<>();
		
		for(int i=0; i<transaction.sizeMessaggi(); i++){
			Messaggio messaggio = transaction.getMessaggio(i);
			TipoMessaggio tipoMessaggio = messaggio.getTipoMessaggio();
			
			boolean onlyLogFileTraceHeaders = false;
			if(tipiDaEliminareHeaders!=null && !tipiDaEliminareHeaders.isEmpty()) {
				for (int j = 0; j < tipiDaEliminareHeaders.size(); j++) {
					TipoMessaggio tipoMessaggioHeaders = tipiDaEliminareHeaders.get(j);
					if(tipoMessaggioHeaders.equals(tipoMessaggio)) {
						onlyLogFileTraceHeaders = true;
						break;
					}
				}
			}
			if(onlyLogFileTraceHeaders) {
				tipiDaEliminareHeaders.remove(tipoMessaggio);	
			}
			
			boolean onlyLogFileTraceBody = false;
			if(tipiDaEliminareBody!=null && !tipiDaEliminareBody.isEmpty()) {
				for (int j = 0; j < tipiDaEliminareBody.size(); j++) {
					TipoMessaggio tipoMessaggioBody = tipiDaEliminareBody.get(j);
					if(tipoMessaggioBody.equals(tipoMessaggio)) {
						onlyLogFileTraceBody = true;
						break;
					}
				}
			}
			if(onlyLogFileTraceBody) {
				tipiDaEliminareBody.remove(tipoMessaggio);	
			}
			
			if(onlyLogFileTraceHeaders && onlyLogFileTraceBody) {
				messaggiDaLiminare.add(tipoMessaggio);
			}
			else if(onlyLogFileTraceHeaders) {
				messaggio.getHeaders().clear();
			}
			else if(onlyLogFileTraceBody) {
				if(messaggio.getBody()!=null) {
					messaggio.getBody().unlock();
					messaggio.getBody().clearResources();
					messaggio.setBody(null);
				}
				messaggio.setContentType(null);
			}
		}
		
		if(messaggiDaLiminare!=null && !messaggiDaLiminare.isEmpty()) {
			while(!messaggiDaLiminare.isEmpty()) {
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
			
			boolean requestSent = false;
			if(context!=null && context.containsKey(Costanti.RICHIESTA_INOLTRATA_BACKEND)) {
				Object o = context.getObject(Costanti.RICHIESTA_INOLTRATA_BACKEND);
				if(o instanceof String) {
					String s = (String) o;
					if(Costanti.RICHIESTA_INOLTRATA_BACKEND_VALORE.equals(s)) {
						requestSent = true;
					}
				}
			}
			
			List<Topic> topicInvoke = new ArrayList<>();
			for (String topicName : topic) {
				Topic topicConfig = topicMap.get(topicName);
				
				if(topicConfig.isOnlyRequestSent()) {
					if(!requestSent) {
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
				/**System.out.println("SORT ["+sortKey+"] ["+pName+"]");*/
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
