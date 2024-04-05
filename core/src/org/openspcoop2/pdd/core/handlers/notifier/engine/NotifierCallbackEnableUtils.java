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
package org.openspcoop2.pdd.core.handlers.notifier.engine;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.pdd.core.handlers.PostOutRequestContext;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.handlers.PreInRequestContext;
import org.openspcoop2.pdd.core.handlers.PreInResponseContext;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierBufferState;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierResult;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierType;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.io.notifier.unblocked.PipedInputOutputStreamHandler;
import org.openspcoop2.utils.transport.TransportUtils;
import org.slf4j.Logger;

/**     
 * NotifierCallbackEnableUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotifierCallbackEnableUtils {

	// *** enableNotifierInputStream ***
	
	public static boolean enableNotifierInputStream(NotifierCallback notifierCallback, NotifierType notifierType,
			Object context) throws ConnectorException{
		
		if(OpenSPCoop2Properties.getInstance().isNotifierInputStreamEnabled()==false){
			return false;
		}
		
		if(NotifierType.PRE_IN_REQUEST.equals(notifierType)){
			PreInRequestContext op2Context = (PreInRequestContext) context;
			
			ConnectorInMessage request = (ConnectorInMessage)op2Context.getTransportContext().get(PreInRequestContext.SERVLET_REQUEST);
			
			int length = request.getContentLength();
			notifierCallback.debug("CONTENT LENGTH ["+length+"]");
			setManagementMode(notifierCallback, length, op2Context.getPddContext());
			
			String contentType = request.getContentType();
			notifierCallback.debug("CONTENT TYPE ["+contentType+"]");
			op2Context.getPddContext().addObject(NotifierConstants.REQUEST_CONTENT_TYPE, contentType);
			
			return true;
		}
				
		else if(NotifierType.PRE_IN_RESPONSE.equals(notifierType)){
			PreInResponseContext op2Context = (PreInResponseContext) context;

			Boolean responseDumpPostProcessEnabled = (Boolean) op2Context.getPddContext().getObject(NotifierConstants.RESPONSE_DUMP_POST_PROCESS_ENABLED);
			
			if(responseDumpPostProcessEnabled!=null && responseDumpPostProcessEnabled){
				notifierCallback.debug("ReturnCode ["+op2Context.getCodiceTrasporto()+"]");
				
				int length = -1;
				if(op2Context.getResponseHeaders()!=null && op2Context.getResponseHeaders().size()>0){
					Iterator<String> keys = op2Context.getResponseHeaders().keySet().iterator();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						//notifierCallback.debug("TRANSPORT ["+key+"]=["+op2Context.getPropertiesTrasportoRisposta().getProperty(key)+"]");
						if("Content-Length".equalsIgnoreCase(key)){
							String lengthParam = TransportUtils.getFirstValue(op2Context.getResponseHeaders(),key);
							length = Integer.parseInt(lengthParam);
							notifierCallback.debug("CONTENT LENGTH RESPONSE ["+length+"]");				
						}
						else if("Content-Type".equalsIgnoreCase(key)){
							String contentType = TransportUtils.getFirstValue(op2Context.getResponseHeaders(),key);
							notifierCallback.debug("CONTENT TYPE RESPONSE ["+contentType+"]");	
							op2Context.getPddContext().addObject(NotifierConstants.RESPONSE_CONTENT_TYPE, contentType);
						}
					}
				}
				setManagementMode(notifierCallback, length, op2Context.getPddContext());
				
				return true;
			}
		}
		
		return false;
	}

	private static void setManagementMode(NotifierCallback notifierCallback,int length,PdDContext pddContext){
		ManagementMode mode = null;
		if(length>0){
			mode = ManagementMode.STREAMING;
			Integer thresholdInMemory = OpenSPCoop2Properties.getInstance().getDumpNonRealtimeInMemoryThreshold();
			if(thresholdInMemory!=null){
				if(length<thresholdInMemory){
					mode = ManagementMode.BUFFER;
				}
			}
		}
		else{
			// Per forza streaming, potrebbe essere gigante
			mode = ManagementMode.STREAMING;
		}
		pddContext.addObject(NotifierConstants.MANAGEMENT_MODE, mode);
		notifierCallback.debug("MANAGEMENT MODE ["+mode.name()+"]");
	}
	
	
	
	// *** notify ***
	
	public static NotifierResult notify(NotifierCallback notifierCallback, NotifierType notifierType, Object context)
			throws Exception {
		
		NotifierResult result = new NotifierResult();
		
		newStreamingHandlers(notifierCallback, result, notifierType, context);
		
		result.setBufferState(getBufferState(notifierCallback, notifierType, context));
		
		return result;
		
	}
	
	
	// *** newStreamingHandlers ***
	
	private static void newStreamingHandlers(NotifierCallback notifierCallback, NotifierResult notifierResult,NotifierType notifierType,
			Object context) throws Exception{
		
		if(NotifierType.IN_REQUEST_PROTOCOL_INFO.equals(notifierType)){
			
			InRequestProtocolContext op2Context = (InRequestProtocolContext) context;
			setConfigurazioneDump(notifierCallback, op2Context);
			
			Boolean requestDumpPostProcessEnabled = (Boolean) op2Context.getPddContext().getObject(NotifierConstants.REQUEST_DUMP_POST_PROCESS_ENABLED);
			if(requestDumpPostProcessEnabled){
				
				Map<String, List<String>> headerTrasporto = null;
				if(TipoPdD.DELEGATA.equals(op2Context.getTipoPorta())){
					if(op2Context.getConnettore()!=null && op2Context.getConnettore().getUrlProtocolContext().getHeaders()!=null){
						headerTrasporto = op2Context.getConnettore().getUrlProtocolContext().getHeaders();
						op2Context.getPddContext().addObject(NotifierConstants.REQUEST_DUMP_POST_PROCESS_HEADER_TRASPORTO, headerTrasporto);
					}
				}
				
				ManagementMode managementMode = (ManagementMode) op2Context.getPddContext().getObject(NotifierConstants.MANAGEMENT_MODE);
				if(ManagementMode.STREAMING.equals(managementMode)){
					notifierCallback.debug("CREO HANDLER DI STREAMING ...");
										
					Logger log = op2Context.getLogCore();
										
//					PipedInputOutputStreamHandler streamingHandler = 
//					new PipedInputOutputStreamHandler(NotifierConstants.ID_HANDLER, notifierStreamingHandler, log);
					IDSoggetto dominio = null;
					if(op2Context.getProtocollo()!=null){
						dominio = op2Context.getProtocollo().getDominio();
					}
					notifierResult.addStreamingHandler(NotifierConstants.ID_HANDLER, 
							newHandler(notifierCallback, log, TipoMessaggio.RICHIESTA_INGRESSO, op2Context.getPddContext(), headerTrasporto, dominio), log);
					
					notifierCallback.debug("CREATO!");
					
				}
			}
					
		}
		
		else if(NotifierType.PRE_IN_RESPONSE.equals(notifierType)){
			
			PreInResponseContext op2Context = (PreInResponseContext) context;
			
			Boolean responseDumpPostProcessEnabled = (Boolean) op2Context.getPddContext().getObject(NotifierConstants.RESPONSE_DUMP_POST_PROCESS_ENABLED);
			if(responseDumpPostProcessEnabled){
				
				Map<String, List<String>> headerTrasporto = null;
				if(TipoPdD.APPLICATIVA.equals(op2Context.getTipoPorta())){
					if(op2Context.getResponseHeaders()!=null){
						headerTrasporto = op2Context.getResponseHeaders();
						op2Context.getPddContext().addObject(NotifierConstants.RESPONSE_DUMP_POST_PROCESS_HEADER_TRASPORTO, headerTrasporto);
					}
				}
				
				ManagementMode managementMode = (ManagementMode) op2Context.getPddContext().getObject(NotifierConstants.MANAGEMENT_MODE);
				if(ManagementMode.STREAMING.equals(managementMode)){
					notifierCallback.debug("CREO HANDLER DI STREAMING DI RISPOSTA ...");
										
					Logger log = op2Context.getLogCore();
										
//					PipedInputOutputStreamHandler streamingHandler = 
//					new PipedInputOutputStreamHandler(NotifierConstants.ID_HANDLER, notifierStreamingHandler, log);
					IDSoggetto dominio = null;
					if(op2Context.getProtocollo()!=null){
						dominio = op2Context.getProtocollo().getDominio();
					}
					notifierResult.addStreamingHandler(NotifierConstants.ID_HANDLER, 
							newHandler(notifierCallback, log, TipoMessaggio.RISPOSTA_INGRESSO, op2Context.getPddContext(), headerTrasporto, dominio), log);
					
					notifierCallback.debug("CREATO!");
					
				}
			}
		}

		
	}
	
	private static NotifierStreamingHandler newHandler(NotifierCallback notifierCallback, Logger log, TipoMessaggio tipoMessaggio,
			PdDContext pddContext,Map<String, List<String>> headerTrasporto, IDSoggetto dominio) throws Exception{
		long dumpPostProcessConfigId = (Long) pddContext.getObject(NotifierConstants.DUMP_POST_PROCESS_ID_CONFIG);
		String idTransazione = (String) pddContext.getObject(Costanti.ID_TRANSAZIONE);
		String contentType = null;
		if(TipoMessaggio.RICHIESTA_INGRESSO.equals(tipoMessaggio)){
			contentType = (String) pddContext.getObject(NotifierConstants.REQUEST_CONTENT_TYPE);
		}else{
			contentType = (String) pddContext.getObject(NotifierConstants.RESPONSE_CONTENT_TYPE);
		}
		
		NotifierStreamingHandler notifierStreamingHandler = 
				new NotifierStreamingHandler(notifierCallback,
						idTransazione, 
						tipoMessaggio, 
						headerTrasporto,
						dumpPostProcessConfigId,
						contentType, log, dominio);
		
		return notifierStreamingHandler;
	}
	

	// *** getBufferState ***
	
	@SuppressWarnings("unchecked")
	private static NotifierBufferState getBufferState(NotifierCallback notifierCallback, NotifierType notifierType,
			Object context) throws Exception  {
		
		NotifierBufferState state = NotifierBufferState.UNMODIFIED;
		
		if(NotifierType.PRE_IN_REQUEST.equals(notifierType)){
			
			state = NotifierBufferState.ENABLE;
			notifierCallback.debug("ABILITO IL BUFFER!!");
			
		}
		
		else if(NotifierType.IN_REQUEST_PROTOCOL_INFO.equals(notifierType)){
			
			InRequestProtocolContext op2Context = (InRequestProtocolContext) context;
			setConfigurazioneDump(notifierCallback, op2Context);
			
			Boolean requestDumpPostProcessEnabled = (Boolean) op2Context.getPddContext().getObject(NotifierConstants.REQUEST_DUMP_POST_PROCESS_ENABLED);
			if(requestDumpPostProcessEnabled){
				
				ManagementMode managementMode = (ManagementMode) op2Context.getPddContext().getObject(NotifierConstants.MANAGEMENT_MODE);
				
				if(ManagementMode.BUFFER.equals(managementMode)){
					notifierCallback.debug("DUMP POST PROCESS ABILITATO COME BUFFER, LASCIO BUFFER ABILITATO");					
				}
				else{
					state = NotifierBufferState.DISABLE_AND_RELEASE_BUFFER_READED;
					notifierCallback.debug("RILASCIO IL BUFFER, DUMP POST PROCESS ABILITATO COME STREAMING");
				}
					
			}
			else{
				state = NotifierBufferState.DISABLE_AND_RELEASE_BUFFER_READED;
				notifierCallback.debug("RILASCIO IL BUFFER, DUMP POST PROCESS NON ABILITATO");
			}
			
		}
		
		else if(NotifierType.POST_OUT_REQUEST.equals(notifierType)){
			
			PostOutRequestContext op2Context = (PostOutRequestContext) context;
			
			IDSoggetto dominio = null;
			if(op2Context.getProtocollo()!=null){
				dominio = op2Context.getProtocollo().getDominio();
			}
			
			Boolean requestDumpPostProcessEnabled = (Boolean) op2Context.getPddContext().getObject(NotifierConstants.REQUEST_DUMP_POST_PROCESS_ENABLED);
			if(requestDumpPostProcessEnabled){
			
				Map<String, List<String>> headerTrasporto = null;
				if(TipoPdD.DELEGATA.equals(op2Context.getTipoPorta())){
					Object o = op2Context.getPddContext().getObject(NotifierConstants.REQUEST_DUMP_POST_PROCESS_HEADER_TRASPORTO);
					if(o!=null){
						headerTrasporto = (Map<String, List<String>>) o;
					}
				}
				else{
					if(op2Context.getConnettore()!=null){
						headerTrasporto = op2Context.getConnettore().getHeaders();
					}
				}
				
				long dumpPostProcessConfigId = (Long) op2Context.getPddContext().getObject(NotifierConstants.DUMP_POST_PROCESS_ID_CONFIG);
				String contentTypeRequest = (String) op2Context.getPddContext().getObject(NotifierConstants.REQUEST_CONTENT_TYPE);
				String idTransazione = (String)op2Context.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
				TipoMessaggio tipoMessaggio = TipoMessaggio.RICHIESTA_INGRESSO;
				
				ManagementMode managementMode = (ManagementMode) op2Context.getPddContext().getObject(NotifierConstants.MANAGEMENT_MODE);
				if(ManagementMode.BUFFER.equals(managementMode)){
					
					notifierCallback.debug("[BUFFER MODE] RILASCIO IL BUFFER, e salvo il contenuto su database");
					
					state = NotifierBufferState.DISABLE_AND_RELEASE_BUFFER_READED;
									
					byte[] buffer = op2Context.getMessaggio().getNotifierInputStream().serializeAndConsume();
					
					NotifierDump.getInstance().saveBuffer(notifierCallback, idTransazione, tipoMessaggio, headerTrasporto,
							dumpPostProcessConfigId, contentTypeRequest, buffer, dominio);
					
				}
				else{
					
					// raccolgo esito handler
					PipedInputOutputStreamHandler streamingHandler = 
							(PipedInputOutputStreamHandler) op2Context.getMessaggio().getNotifierInputStream().getStreamingHandler(NotifierConstants.ID_HANDLER);
					
					if(streamingHandler.getError()!=null || streamingHandler.getException()!=null){
						String msg = null;
						if(streamingHandler.getError()!=null){
							msg = streamingHandler.getError();
						}else{
							msg = streamingHandler.getException().getMessage();
						}
						notifierCallback.debug("EVENTUALE ERRORE ["+msg+"]");
						if(streamingHandler.getException()!=null){
							throw new Exception(msg,streamingHandler.getException());
						}
						else{
							throw new Exception(msg);
						}
					}
					
					
					NotifierResultStreamingHandler result = (NotifierResultStreamingHandler) streamingHandler.getResult();
					if(result==null){
						throw new Exception("Streaming Handler ["+NotifierConstants.ID_HANDLER+"] non ha ritornato un risultato, ma nemmeno una eccezione");
					}
					if(result.isSaveOnFileSystem()){
						
						notifierCallback.debug("[STREAMING MODE su FS] RILASCIO IL BUFFER, e salvo il contenuto su database (file:"+result.getFile().getAbsolutePath()+")");
						
						state = NotifierBufferState.DISABLE_AND_RELEASE_BUFFER_READED;
						
						NotifierDump.getInstance().saveOnFileSystem(notifierCallback, idTransazione, tipoMessaggio, headerTrasporto, 
								dumpPostProcessConfigId, contentTypeRequest, result.getFile(), dominio);
						
					}
					else{
						
						notifierCallback.debug("[STREAMING MODE su Database] INSERT EFFETTUATA (ExecuteUpdate:"+result.getExecuteUpdateRow()+")");
						int rowUpdate = result.getExecuteUpdateRow();
						
						if(TipoPdD.APPLICATIVA.equals(op2Context.getTipoPorta())){ 
							// aggiorno l'header di trasporto
							if(headerTrasporto!=null){
								rowUpdate = NotifierDump.getInstance().update(notifierCallback, idTransazione, tipoMessaggio, headerTrasporto, dominio);
							}
						}
						
						notifierCallback.debug("[STREAMING MODE su Database] RILASCIO IL BUFFER (ExecuteUpdate:"+rowUpdate+")");
						
						state = NotifierBufferState.DISABLE_AND_RELEASE_BUFFER_READED;
						
					}
					
				}
			}
		}
		
		else if(NotifierType.PRE_IN_RESPONSE.equals(notifierType)){
			
			PreInResponseContext op2Context = (PreInResponseContext) context;
			
			Boolean responseDumpPostProcessEnabled = (Boolean) op2Context.getPddContext().getObject(NotifierConstants.RESPONSE_DUMP_POST_PROCESS_ENABLED);	
			if(responseDumpPostProcessEnabled){
				
				ManagementMode managementMode = (ManagementMode) op2Context.getPddContext().getObject(NotifierConstants.MANAGEMENT_MODE);
				
				if(ManagementMode.BUFFER.equals(managementMode)){
					notifierCallback.debug("[BUFFER] ABILITO IL BUFFER ");
					
					state = NotifierBufferState.ENABLE;
				}
				
			}
			
		}
		
		else if(NotifierType.POST_OUT_RESPONSE.equals(notifierType)){
			
			PostOutResponseContext op2Context = (PostOutResponseContext) context;
			
			IDSoggetto dominio = null;
			if(op2Context.getProtocollo()!=null){
				dominio = op2Context.getProtocollo().getDominio();
			}
			
			Boolean responseDumpPostProcessEnabled = (Boolean) op2Context.getPddContext().getObject(NotifierConstants.RESPONSE_DUMP_POST_PROCESS_ENABLED);	
			if(responseDumpPostProcessEnabled){
			
				Map<String, List<String>> headerTrasporto = null;
				if(TipoPdD.APPLICATIVA.equals(op2Context.getTipoPorta())){
					Object o = op2Context.getPddContext().getObject(NotifierConstants.RESPONSE_DUMP_POST_PROCESS_HEADER_TRASPORTO);
					if(o!=null){
						headerTrasporto = (Map<String, List<String>>) o;
					}
				}
				else{
					if(op2Context.getResponseHeaders()!=null){
						headerTrasporto = op2Context.getResponseHeaders();
					}
				}
				
				long dumpPostProcessConfigId = (Long) op2Context.getPddContext().getObject(NotifierConstants.DUMP_POST_PROCESS_ID_CONFIG);
				String contentTypeResponse = (String) op2Context.getPddContext().getObject(NotifierConstants.RESPONSE_CONTENT_TYPE);
				String idTransazione = (String)op2Context.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
				TipoMessaggio tipoMessaggio = TipoMessaggio.RISPOSTA_INGRESSO;
				
				ManagementMode managementMode = (ManagementMode) op2Context.getPddContext().getObject(NotifierConstants.MANAGEMENT_MODE);
				if(ManagementMode.BUFFER.equals(managementMode)){
					
					notifierCallback.debug("[BUFFER MODE] RILASCIO IL BUFFER, e salvo il contenuto su database");
					
					state = NotifierBufferState.DISABLE_AND_RELEASE_BUFFER_READED;
									
					byte[] buffer = op2Context.getMessaggio().getNotifierInputStream().serializeAndConsume();
										
					NotifierDump.getInstance().saveBuffer(notifierCallback, idTransazione, tipoMessaggio, headerTrasporto, 
							dumpPostProcessConfigId, contentTypeResponse, buffer, dominio);
					
				}
				else{
					
					// raccolgo esito handler
					PipedInputOutputStreamHandler streamingHandler = 
							(PipedInputOutputStreamHandler) op2Context.getMessaggio().getNotifierInputStream().getStreamingHandler(NotifierConstants.ID_HANDLER);
					
					if(streamingHandler.getError()!=null || streamingHandler.getException()!=null){
						String msg = null;
						if(streamingHandler.getError()!=null){
							msg = streamingHandler.getError();
						}else{
							msg = streamingHandler.getException().getMessage();
						}
						notifierCallback.debug("EVENTUALE ERRORE ["+msg+"]");
						if(streamingHandler.getException()!=null){
							throw new Exception(msg,streamingHandler.getException());
						}
						else{
							throw new Exception(msg);
						}
					}
					
					
					NotifierResultStreamingHandler result = (NotifierResultStreamingHandler) streamingHandler.getResult();
					if(result==null){
						throw new Exception("Streaming Handler ["+NotifierConstants.ID_HANDLER+"] non ha ritornato un risultato, ma nemmeno una eccezione");
					}
					if(result.isSaveOnFileSystem()){
						
						notifierCallback.debug("[STREAMING MODE su FS] RILASCIO IL BUFFER, e salvo il contenuto della risposta su database (file:"+result.getFile().getAbsolutePath()+")");
						
						state = NotifierBufferState.DISABLE_AND_RELEASE_BUFFER_READED;
						
						NotifierDump.getInstance().saveOnFileSystem(notifierCallback, idTransazione, tipoMessaggio, headerTrasporto, 
								dumpPostProcessConfigId, contentTypeResponse, result.getFile(), dominio);
						
					}
					else{
						
						notifierCallback.debug("[STREAMING MODE su Database] INSERT EFFETTUATA (ExecuteUpdate:"+result.getExecuteUpdateRow()+")");
						int rowUpdate = result.getExecuteUpdateRow();
						
						if(TipoPdD.DELEGATA.equals(op2Context.getTipoPorta())){
							// aggiorno l'header di trasporto
							if(headerTrasporto!=null){
								rowUpdate = NotifierDump.getInstance().update(notifierCallback, idTransazione, tipoMessaggio, headerTrasporto, dominio);
							}
						}
						
						notifierCallback.debug("[STREAMING MODE su Database] RILASCIO IL BUFFER (ExecuteUpdate:"+rowUpdate+")");
						
						state = NotifierBufferState.DISABLE_AND_RELEASE_BUFFER_READED;
						
					}
					
				}
			}
		}
		
		return state;
		
	}
	
	
	
	
	
	
	// Accesso configurazione Dump
	
	private static void setConfigurazioneDump(NotifierCallback notifierCallback, InRequestProtocolContext inRequestProtocolContext) throws Exception{
		
		if(inRequestProtocolContext.getPddContext().getObject(NotifierConstants.REQUEST_DUMP_POST_PROCESS_ENABLED)==null){
		
			DumpConfigurazione regolaDump = readConfigurazioneDump(inRequestProtocolContext);
			
			if(regolaDump!=null){
				
				if(StatoFunzionalita.ABILITATO.equals(regolaDump.getRealtime())){
					
					boolean requestDumpPostProcessEnabled = 
							regolaDump.getRichiestaIngresso()!=null && 
							(
								StatoFunzionalita.ABILITATO.equals(regolaDump.getRichiestaIngresso().getBody()) 
									||
								StatoFunzionalita.ABILITATO.equals(regolaDump.getRichiestaIngresso().getHeaders())
									||
								StatoFunzionalita.ABILITATO.equals(regolaDump.getRichiestaIngresso().getAttachments())
							 );
					
					boolean responseDumpPostProcessEnabled = 
							regolaDump.getRispostaIngresso()!=null && 
							(
								StatoFunzionalita.ABILITATO.equals(regolaDump.getRispostaIngresso().getBody()) 
									||
								StatoFunzionalita.ABILITATO.equals(regolaDump.getRispostaIngresso().getHeaders())
									||
								StatoFunzionalita.ABILITATO.equals(regolaDump.getRispostaIngresso().getAttachments())
							 );
					
					inRequestProtocolContext.getPddContext().addObject(NotifierConstants.DUMP_POST_PROCESS_ID_CONFIG, regolaDump.getId());
					inRequestProtocolContext.getPddContext().addObject(NotifierConstants.REQUEST_DUMP_POST_PROCESS_ENABLED, requestDumpPostProcessEnabled);
					inRequestProtocolContext.getPddContext().addObject(NotifierConstants.RESPONSE_DUMP_POST_PROCESS_ENABLED, responseDumpPostProcessEnabled);
					
					notifierCallback.debug("REGOLA DUMP PRESENTE COME POST PROCESS REQUEST["+requestDumpPostProcessEnabled+"] RESPONSE["+responseDumpPostProcessEnabled+"]");
				}
				else{
					notifierCallback.debug("REGOLA DUMP PRESENTE COME REAL TIME");
					inRequestProtocolContext.getPddContext().addObject(NotifierConstants.REQUEST_DUMP_POST_PROCESS_ENABLED, false);
					inRequestProtocolContext.getPddContext().addObject(NotifierConstants.RESPONSE_DUMP_POST_PROCESS_ENABLED, false);
				}
				
			}
			else{
				notifierCallback.debug("REGOLA DUMP NON PRESENTE");
				inRequestProtocolContext.getPddContext().addObject(NotifierConstants.REQUEST_DUMP_POST_PROCESS_ENABLED, false);
				inRequestProtocolContext.getPddContext().addObject(NotifierConstants.RESPONSE_DUMP_POST_PROCESS_ENABLED, false);
			}
			
		}
	}
	
	private static DumpConfigurazione readConfigurazioneDump(InRequestProtocolContext inRequestProtocolContext) throws Exception{
				
		
		if(inRequestProtocolContext.getIntegrazione()==null) {
			throw new Exception("inRequestProtocolContext.getIntegrazione() is null");
		}
		if(inRequestProtocolContext.getIntegrazione().getIdPD()==null &&
				inRequestProtocolContext.getIntegrazione().getIdPA()==null	) {
			throw new Exception("non è presente ne un identificativo di porta delegata, ne uno di porta applicativa");
		}
		
		RequestInfo requestInfo = null;
		if(inRequestProtocolContext.getPddContext()!=null && inRequestProtocolContext.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)){
			requestInfo = (RequestInfo) inRequestProtocolContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		}
		
		DumpConfigurazione config = null;
		ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(inRequestProtocolContext.getStato());
		if(inRequestProtocolContext.getIntegrazione().getIdPD()!=null) {
			PortaDelegata pd = configPdDManager.getPortaDelegata(inRequestProtocolContext.getIntegrazione().getIdPD(), requestInfo);
			config = configPdDManager.getDumpConfigurazione(pd);
		}
		else {
			PortaApplicativa pa = configPdDManager.getPortaApplicativa(inRequestProtocolContext.getIntegrazione().getIdPA(), requestInfo);
			config = configPdDManager.getDumpConfigurazione(pa);
		}
		
		return config;
	}
	
}
