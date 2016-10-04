/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.pdd.core.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierConstants;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierInRequestHandler;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierInRequestProtocolHandler;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierInResponseHandler;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierOutRequestHandler;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierOutResponseHandler;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierPostOutRequestHandler;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierPostOutResponseHandler;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierPreInRequestHandler;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierPreInResponseHandler;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.resources.Loader;


/**
 * Utility per la registrazione delle informazioni di attraversamento della richiesta/risposta nella porta di dominio
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreHandlers  {

	private static boolean initialize = false;
	private static OpenSPCoop2Properties properties = null;
	private static GeneratoreCasualeDate generatoreCasualeDati = null;
	
	private static InitHandler [] initHandlers = null;
	private static String [] tipiInitHandlers = null;
	
	private static ExitHandler [] exitHandlers = null;
	private static String [] tipiExitHandlers = null;
	
	private static PreInRequestHandler [] preInRequestHandlers = null;
	private static String [] tipiPreInRequestHandlers = null;
	
	private static InRequestHandler [] inRequestHandlers = null;
	private static String [] tipiInRequestHandlers = null;
	
	private static InRequestProtocolHandler [] inRequestProtocolHandlers = null;
	private static String [] tipiInRequestProtocolHandlers = null;
	
	private static OutRequestHandler [] outRequestHandlers = null;
	private static String [] tipiOutRequestHandlers = null;
	
	private static PostOutRequestHandler [] postOutRequestHandlers = null;
	private static String [] tipiPostOutRequestHandlers = null;
	
	private static PreInResponseHandler [] preInResponseHandlers = null;
	private static String [] tipiPreInResponseHandlers = null;
	
	private static InResponseHandler [] inResponseHandlers = null;
	private static String [] tipiInResponseHandlers = null;
	
	private static OutResponseHandler [] outResponseHandlers = null;
	private static String [] tipiOutResponseHandlers = null;
	
	private static PostOutResponseHandler [] postOutResponseHandlers = null;
	private static String [] tipiPostOutResponseHandlers = null;
	
	private static IntegrationManagerRequestHandler [] integrationManagerRequestHandlers = null;
	private static String [] tipiIntegrationManagerRequestHandlers = null;
	
	private static IntegrationManagerResponseHandler [] integrationManagerResponseHandlers = null;
	private static String [] tipiIntegrationManagerResponseHandlers = null;

	
	private synchronized static void initialize(MsgDiagnostico msgDiag,Logger logConsoleInit){
		if(GestoreHandlers.initialize==false){
			
			GestoreHandlers.properties = OpenSPCoop2Properties.getInstance();
			ClassNameProperties classNameProperties = ClassNameProperties.getInstance();
			Loader loader = Loader.getInstance();
			if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && !UniqueIdentifierManager.isGenerazioneUIDDisabilitata()){
				GestoreHandlers.generatoreCasualeDati = GeneratoreCasualeDate.getGeneratoreCasualeDate();
			}
			boolean printInfo = GestoreHandlers.properties.isPrintInfoHandler();
			
			// InitHandler
			GestoreHandlers.tipiInitHandlers = GestoreHandlers.properties.getInitHandler();
			if(GestoreHandlers.tipiInitHandlers!=null){
				GestoreHandlers.initHandlers = new InitHandler[GestoreHandlers.tipiInitHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiInitHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiInitHandlers[i];
						String classe = classNameProperties.getInitHandler(tipo);
						GestoreHandlers.initHandlers[i] = (InitHandler) loader.newInstance(classe);
						if(printInfo)
							logConsoleInit.info("InitHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "Inizializzazione InitHandler ["+GestoreHandlers.tipiInitHandlers[i]+"]");
					}
				}
				GestoreHandlers.initHandlers = reorder(GestoreHandlers.initHandlers);
			}
			
			// ExitHandler
			GestoreHandlers.tipiExitHandlers = GestoreHandlers.properties.getExitHandler();
			if(GestoreHandlers.tipiExitHandlers!=null){
				GestoreHandlers.exitHandlers = new ExitHandler[GestoreHandlers.tipiExitHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiExitHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiExitHandlers[i];
						String classe = classNameProperties.getExitHandler(tipo);
						GestoreHandlers.exitHandlers[i] = (ExitHandler) loader.newInstance(classe);
						if(printInfo)
							logConsoleInit.info("ExitHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "Inizializzazione ExitHandler ["+GestoreHandlers.tipiExitHandlers[i]+"]");
					}
				}
				GestoreHandlers.exitHandlers = reorder(GestoreHandlers.exitHandlers);
			}
			
			// PreInRequestHandler
			GestoreHandlers.tipiPreInRequestHandlers = GestoreHandlers.properties.getPreInRequestHandler();
			GestoreHandlers.tipiPreInRequestHandlers = updateNotifierCallback(GestoreHandlers.tipiPreInRequestHandlers);
			if(GestoreHandlers.tipiPreInRequestHandlers!=null){
				GestoreHandlers.preInRequestHandlers = new PreInRequestHandler[GestoreHandlers.tipiPreInRequestHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiPreInRequestHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiPreInRequestHandlers[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierPreInRequestHandler.class.getName();
						}else{
							classe = classNameProperties.getPreInRequestHandler(tipo);
						}
						GestoreHandlers.preInRequestHandlers[i] = (PreInRequestHandler) loader.newInstance(classe);
						if(printInfo)
							logConsoleInit.info("PreInRequestHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "Inizializzazione PreInRequestHandler ["+GestoreHandlers.tipiPreInRequestHandlers[i]+"]");
					}
				}
				GestoreHandlers.preInRequestHandlers = reorder(GestoreHandlers.preInRequestHandlers);
			}
			
			// InRequestHandler
			GestoreHandlers.tipiInRequestHandlers = GestoreHandlers.properties.getInRequestHandler();
			GestoreHandlers.tipiInRequestHandlers = updateNotifierCallback(GestoreHandlers.tipiInRequestHandlers);
			if(GestoreHandlers.tipiInRequestHandlers!=null){
				GestoreHandlers.inRequestHandlers = new InRequestHandler[GestoreHandlers.tipiInRequestHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiInRequestHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiInRequestHandlers[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierInRequestHandler.class.getName();
						}else{
							classe = classNameProperties.getInRequestHandler(tipo);
						}
						GestoreHandlers.inRequestHandlers[i] = (InRequestHandler) loader.newInstance(classe);
						if(printInfo)
							logConsoleInit.info("InRequestHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "Inizializzazione InRequestHandler ["+GestoreHandlers.tipiInRequestHandlers[i]+"]");
					}
				}
				GestoreHandlers.inRequestHandlers = reorder(GestoreHandlers.inRequestHandlers);
			}
			
			// InRequestProtocolHandler
			GestoreHandlers.tipiInRequestProtocolHandlers = GestoreHandlers.properties.getInRequestProtocolHandler();
			GestoreHandlers.tipiInRequestProtocolHandlers = updateNotifierCallback(GestoreHandlers.tipiInRequestProtocolHandlers);
			if(GestoreHandlers.tipiInRequestProtocolHandlers!=null){
				GestoreHandlers.inRequestProtocolHandlers = new InRequestProtocolHandler[GestoreHandlers.tipiInRequestProtocolHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiInRequestProtocolHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiInRequestProtocolHandlers[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierInRequestProtocolHandler.class.getName();
						}else{
							classe = classNameProperties.getInRequestProtocolHandler(tipo);
						}
						GestoreHandlers.inRequestProtocolHandlers[i] = (InRequestProtocolHandler) loader.newInstance(classe);
						if(printInfo)
							logConsoleInit.info("InRequestProtocolHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "Inizializzazione InRequestProtocolHandler ["+GestoreHandlers.tipiInRequestProtocolHandlers[i]+"]");
					}
				}
				GestoreHandlers.inRequestProtocolHandlers = reorder(GestoreHandlers.inRequestProtocolHandlers);
			}
			
			// OutRequestHandler
			GestoreHandlers.tipiOutRequestHandlers = GestoreHandlers.properties.getOutRequestHandler();
			GestoreHandlers.tipiOutRequestHandlers = updateNotifierCallback(GestoreHandlers.tipiOutRequestHandlers);
			if(GestoreHandlers.tipiOutRequestHandlers!=null){
				GestoreHandlers.outRequestHandlers = new OutRequestHandler[GestoreHandlers.tipiOutRequestHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiOutRequestHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiOutRequestHandlers[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierOutRequestHandler.class.getName();
						}else{
							classe = classNameProperties.getOutRequestHandler(tipo);
						}
						GestoreHandlers.outRequestHandlers[i] = (OutRequestHandler) loader.newInstance(classe);
						if(printInfo)
							logConsoleInit.info("OutRequestHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "Inizializzazione OutRequestHandler ["+GestoreHandlers.tipiOutRequestHandlers[i]+"]");
					}
				}
				GestoreHandlers.outRequestHandlers = reorder(GestoreHandlers.outRequestHandlers);
			}
			
			// PostOutRequestHandler
			GestoreHandlers.tipiPostOutRequestHandlers = properties.getPostOutRequestHandler();
			GestoreHandlers.tipiPostOutRequestHandlers = updateNotifierCallback(GestoreHandlers.tipiPostOutRequestHandlers);
			if(GestoreHandlers.tipiPostOutRequestHandlers!=null){
				GestoreHandlers.postOutRequestHandlers = new PostOutRequestHandler[GestoreHandlers.tipiPostOutRequestHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiPostOutRequestHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiPostOutRequestHandlers[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierPostOutRequestHandler.class.getName();
						}else{
							classe = classNameProperties.getPostOutRequestHandler(tipo);
						}
						GestoreHandlers.postOutRequestHandlers[i] = (PostOutRequestHandler) loader.newInstance(classe);
						if(printInfo)
							logConsoleInit.info("PostOutRequestHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "Inizializzazione PostOutRequestHandler ["+GestoreHandlers.tipiPostOutRequestHandlers[i]+"]");
					}
				}
				GestoreHandlers.postOutRequestHandlers = reorder(GestoreHandlers.postOutRequestHandlers);
			}
			
			// PreInResponseHandler
			GestoreHandlers.tipiPreInResponseHandlers = properties.getPreInResponseHandler();
			GestoreHandlers.tipiPreInResponseHandlers = updateNotifierCallback(GestoreHandlers.tipiPreInResponseHandlers);
			if(GestoreHandlers.tipiPreInResponseHandlers!=null){
				GestoreHandlers.preInResponseHandlers = new PreInResponseHandler[GestoreHandlers.tipiPreInResponseHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiPreInResponseHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiPreInResponseHandlers[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierPreInResponseHandler.class.getName();
						}else{
							classe = classNameProperties.getPreInResponseHandler(tipo);
						}
						GestoreHandlers.preInResponseHandlers[i] = (PreInResponseHandler) loader.newInstance(classe);
						if(printInfo)
							logConsoleInit.info("PreInResponseHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "Inizializzazione PreInResponseHandler ["+GestoreHandlers.tipiPreInResponseHandlers[i]+"]");
					}
				}
				GestoreHandlers.preInResponseHandlers = reorder(GestoreHandlers.preInResponseHandlers);
			}
			
			// InResponseHandler
			GestoreHandlers.tipiInResponseHandlers = GestoreHandlers.properties.getInResponseHandler();
			GestoreHandlers.tipiInResponseHandlers = updateNotifierCallback(GestoreHandlers.tipiInResponseHandlers);
			if(GestoreHandlers.tipiInResponseHandlers!=null){
				GestoreHandlers.inResponseHandlers = new InResponseHandler[GestoreHandlers.tipiInResponseHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiInResponseHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiInResponseHandlers[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierInResponseHandler.class.getName();
						}else{
							classe = classNameProperties.getInResponseHandler(tipo);
						}
						GestoreHandlers.inResponseHandlers[i] = (InResponseHandler) loader.newInstance(classe);
						if(printInfo)
							logConsoleInit.info("InResponseHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "Inizializzazione InResponseHandler ["+GestoreHandlers.tipiInResponseHandlers[i]+"]");
					}
				}
				GestoreHandlers.inResponseHandlers = reorder(GestoreHandlers.inResponseHandlers);
			}
			
			// OutResponseHandler
			GestoreHandlers.tipiOutResponseHandlers = GestoreHandlers.properties.getOutResponseHandler();
			GestoreHandlers.tipiOutResponseHandlers = updateNotifierCallback(GestoreHandlers.tipiOutResponseHandlers);
			if(GestoreHandlers.tipiOutResponseHandlers!=null){
				GestoreHandlers.outResponseHandlers = new OutResponseHandler[GestoreHandlers.tipiOutResponseHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiOutResponseHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiOutResponseHandlers[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierOutResponseHandler.class.getName();
						}else{
							classe = classNameProperties.getOutResponseHandler(tipo);
						}
						GestoreHandlers.outResponseHandlers[i] = (OutResponseHandler) loader.newInstance(classe);
						if(printInfo)
							logConsoleInit.info("OutResponseHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "Inizializzazione OutResponseHandler ["+GestoreHandlers.tipiOutResponseHandlers[i]+"]");
					}
				}
				GestoreHandlers.outResponseHandlers = reorder(GestoreHandlers.outResponseHandlers);
			}
			
			// PostOutResponseHandler
			GestoreHandlers.tipiPostOutResponseHandlers = GestoreHandlers.properties.getPostOutResponseHandler();
			GestoreHandlers.tipiPostOutResponseHandlers = updateNotifierCallback(GestoreHandlers.tipiPostOutResponseHandlers);
			if(GestoreHandlers.tipiPostOutResponseHandlers!=null){
				GestoreHandlers.postOutResponseHandlers = new PostOutResponseHandler[GestoreHandlers.tipiPostOutResponseHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiPostOutResponseHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiPostOutResponseHandlers[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierPostOutResponseHandler.class.getName();
						}else{
							classe = classNameProperties.getPostOutResponseHandler(tipo);
						}
						GestoreHandlers.postOutResponseHandlers[i] = (PostOutResponseHandler) loader.newInstance(classe);
						if(printInfo)
							logConsoleInit.info("PostOutResponseHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "Inizializzazione PostOutResponseHandler ["+GestoreHandlers.tipiPostOutResponseHandlers[i]+"]");
					}
				}
				GestoreHandlers.postOutResponseHandlers = reorder(GestoreHandlers.postOutResponseHandlers);
			}
			
			// IntegrationManagerRequestHandler
			GestoreHandlers.tipiIntegrationManagerRequestHandlers = GestoreHandlers.properties.getIntegrationManagerRequestHandler();
			if(GestoreHandlers.tipiIntegrationManagerRequestHandlers!=null){
				GestoreHandlers.integrationManagerRequestHandlers = new IntegrationManagerRequestHandler[GestoreHandlers.tipiIntegrationManagerRequestHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiIntegrationManagerRequestHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiIntegrationManagerRequestHandlers[i];
						String classe = classNameProperties.getIntegrationManagerRequestHandler(tipo);
						GestoreHandlers.integrationManagerRequestHandlers[i] = (IntegrationManagerRequestHandler) loader.newInstance(classe);
						if(printInfo)
							logConsoleInit.info("IntegrationManagerRequestHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "Inizializzazione IntegrationManagerRequestHandler ["+GestoreHandlers.tipiIntegrationManagerRequestHandlers[i]+"]");
					}
				}
				GestoreHandlers.integrationManagerRequestHandlers = reorder(GestoreHandlers.integrationManagerRequestHandlers);
			}
			
			// IntegrationManagerResponseHandler
			GestoreHandlers.tipiIntegrationManagerResponseHandlers = GestoreHandlers.properties.getIntegrationManagerResponseHandler();
			if(GestoreHandlers.tipiIntegrationManagerResponseHandlers!=null){
				GestoreHandlers.integrationManagerResponseHandlers = new IntegrationManagerResponseHandler[GestoreHandlers.tipiIntegrationManagerResponseHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiIntegrationManagerResponseHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiIntegrationManagerResponseHandlers[i];
						String classe = classNameProperties.getIntegrationManagerResponseHandler(tipo);
						GestoreHandlers.integrationManagerResponseHandlers[i] = (IntegrationManagerResponseHandler) loader.newInstance(classe);
						if(printInfo)
							logConsoleInit.info("IntegrationManagerResponseHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "Inizializzazione IntegrationManagerResponseHandler ["+GestoreHandlers.tipiIntegrationManagerResponseHandlers[i]+"]");
					}
				}
				GestoreHandlers.integrationManagerResponseHandlers = reorder(GestoreHandlers.integrationManagerResponseHandlers);
			}
			
		}
		
		GestoreHandlers.initialize=true;
	}
	
	private final static boolean printOrderInfo = false;
	private static <T> T[] reorder(T [] handlers){
		if(handlers!=null && handlers.length>0){
			
			List<String> handlerPositionHeadId = new ArrayList<String>();
			Hashtable<String,T> handlerPositionHeadMap = new Hashtable<String,T>();
			
			List<String> handlerPositionTailId = new ArrayList<String>();
			Hashtable<String,T> handlerPositionTailMap = new Hashtable<String,T>();
			
			List<T> handlerPositionMiddle = new ArrayList<T>();
			
			if(printOrderInfo){
				System.out.println("================== "+handlers.getClass().getName()+" =====================");
				System.out.println("PRE ORDER:");
			}
			for(int i=0; i<handlers.length; i++){
				boolean position = false;
				if(handlers[i] instanceof PositionHandler){
					PositionHandler p = (PositionHandler) handlers[i];
					if(p.getIdPosition()!=null){
						if(p.isHeadHandler()){
							handlerPositionHeadId.add(p.getIdPosition());
							handlerPositionHeadMap.put(p.getIdPosition(), handlers[i]);
						}
						else{
							handlerPositionTailId.add(p.getIdPosition());
							handlerPositionTailMap.put(p.getIdPosition(), handlers[i]);
						}
						position = true;
					}
				}
				if(position==false){
					handlerPositionMiddle.add(handlers[i]);
				}
				
				if(printOrderInfo){
					System.out.println("\t(Pos:"+position+")["+handlers[i].getClass().getName()+"]");
				}
			}
			
			// reorder
			List<T> orderedList = new ArrayList<T>();
			
			if(handlerPositionHeadId.size()>0){
				Collections.sort(handlerPositionHeadId);
				for (String positionId : handlerPositionHeadId) {
					orderedList.add(handlerPositionHeadMap.get(positionId));
				}
			}
			
			if(handlerPositionMiddle.size()>0){
				for (T t : handlerPositionMiddle) {
					orderedList.add(t);
				}
			}
			
			if(handlerPositionTailId.size()>0){
				Collections.sort(handlerPositionTailId);
				for (String positionId : handlerPositionTailId) {
					orderedList.add(handlerPositionTailMap.get(positionId));
				}
			}
			
			if(printOrderInfo){
				System.out.println("POST ORDER:");
				for (T t : orderedList) {
					System.out.println("\t["+t.getClass().getName()+"]");
				}
			}
			
			return (T[]) orderedList.toArray(handlers);
		}
		else{
			return null;
		}
	}
	
	
	private static String[] updateNotifierCallback(String [] tipi) {
		String notifierInputStreamCallback = null;
		try{
			notifierInputStreamCallback = properties.getNotifierInputStreamCallback();
		}catch(Exception e){}
		if(notifierInputStreamCallback!=null){
			List<String> list = new ArrayList<String>();
			list.add(NotifierConstants.TIPO_NOTIFIER);
			if(tipi!=null){
				for (int i = 0; i < tipi.length; i++) {
					list.add(tipi[i]);
				}
			}
			return list.toArray(new String[1]);
		}
		else{
			return tipi;
		}
	}	
	
	private static void emitDiagnosticInvokeHandlerStart(Object object, MsgDiagnostico msgDiag,Logger log){
		try{
			if(object!=null && msgDiag!=null){
				msgDiag.mediumDebug("["+object.getClass().getName()+"] invocazione in corso");
			}
		}catch(Throwable t){
			if(log!=null){
				log.error("Errore durante l'emissione del diagnostico: "+t.getMessage(),t);
			}
		}
	}
	private static void emitDiagnosticInvokeHandlerEnd(Object object, MsgDiagnostico msgDiag,Logger log){
		try{
			if(object!=null && msgDiag!=null){
				msgDiag.mediumDebug("["+object.getClass().getName()+"] invocazione terminata");
			}
		}catch(Throwable t){
			if(log!=null){
				log.error("Errore durante l'emissione del diagnostico: "+t.getMessage(),t);
			}
		}
	}
	
	public static void init(InitContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log);
		}
		
		if(GestoreHandlers.initHandlers!=null){
			for(int i=0; i<GestoreHandlers.initHandlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(GestoreHandlers.initHandlers[i], msgDiag, log);
					GestoreHandlers.initHandlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(GestoreHandlers.initHandlers[i], msgDiag, log);
				}catch(Exception e){
					
					if(log!=null){
						log.error("InitHandler["+GestoreHandlers.tipiInitHandlers[i]+"]",e);
					}
					
					// Sollevo l'eccezione
					HandlerException ex = new HandlerException(e.getMessage(),e);
					ex.setIdentitaHandler("InitHandler["+GestoreHandlers.initHandlers[i]+"]");
					throw ex;
				}
			}
		}
		
	}
	
	public static void exit(ExitContext context){
		
		Logger log = null;
		if(context!=null){
			if(context.getLogCore()!=null){
				log = context.getLogCore();
			}else{
				log = context.getLogConsole();
			}
		}
		
		if(GestoreHandlers.initialize){ 
			
			// inizializzati cmq dall'init
			
			if(GestoreHandlers.exitHandlers!=null){
				for(int i=0; i<GestoreHandlers.exitHandlers.length; i++){
					try{
						GestoreHandlers.exitHandlers[i].invoke(context);
					}catch(Exception e){
						// Gli handler di tipo exit non dovrebbero sollevare una eccezione.
						// Eventualmento loggo l'errore
						if(log!=null){
							log.error("ExitHandler["+GestoreHandlers.tipiExitHandlers[i]+"]",e);
						}
					}
				}
			}
		}
	}
	
	
	public static void preInRequest(PreInRequestContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log);
		}
		
		if(GestoreHandlers.preInRequestHandlers!=null){
			for(int i=0; i<GestoreHandlers.preInRequestHandlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(GestoreHandlers.preInRequestHandlers[i], msgDiag, log);
					GestoreHandlers.preInRequestHandlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(GestoreHandlers.preInRequestHandlers[i], msgDiag, log);
				}catch(Exception e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler("PreInRequestHandler["+GestoreHandlers.tipiPreInRequestHandlers[i]+"]");
					throw ex;
				}
			}
		}
		
	}
	
	public static void inRequest(InRequestContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log);
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)));
		}
		
		if(GestoreHandlers.inRequestHandlers!=null){
			for(int i=0; i<GestoreHandlers.inRequestHandlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(GestoreHandlers.inRequestHandlers[i], msgDiag, log);
					GestoreHandlers.inRequestHandlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(GestoreHandlers.inRequestHandlers[i], msgDiag, log);
				}catch(Exception e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler("InRequestHandler["+GestoreHandlers.tipiInRequestHandlers[i]+"]");
					throw ex;
				}
			}
		}
		
	}
	
	public static void inRequestProtocol(InRequestProtocolContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log);
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)));
		}
		
		if(GestoreHandlers.inRequestProtocolHandlers!=null){
			for(int i=0; i<GestoreHandlers.inRequestProtocolHandlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(GestoreHandlers.inRequestProtocolHandlers[i], msgDiag, log);
					GestoreHandlers.inRequestProtocolHandlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(GestoreHandlers.inRequestProtocolHandlers[i], msgDiag, log);
				}catch(Exception e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler("inRequestProtocolHandlers["+GestoreHandlers.tipiInRequestProtocolHandlers[i]+"]");
					throw ex;
				}
			}
		}
		
	}
	
	public static void outRequest(OutRequestContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log);
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)));
		}
		
		if(GestoreHandlers.outRequestHandlers!=null){
			for(int i=0; i<GestoreHandlers.outRequestHandlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(GestoreHandlers.outRequestHandlers[i], msgDiag, log);
					GestoreHandlers.outRequestHandlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(GestoreHandlers.outRequestHandlers[i], msgDiag, log);
				}catch(Exception e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler("OutRequestHandler["+GestoreHandlers.tipiOutRequestHandlers[i]+"]");
					throw ex;
				}
			}
		}
		
	}
	
	public static void postOutRequest(PostOutRequestContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log);
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)));
		}
		
		if(GestoreHandlers.postOutRequestHandlers!=null){
			for(int i=0; i<GestoreHandlers.postOutRequestHandlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(GestoreHandlers.postOutRequestHandlers[i], msgDiag, log);
					GestoreHandlers.postOutRequestHandlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(GestoreHandlers.postOutRequestHandlers[i], msgDiag, log);
				}catch(Exception e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler("PostOutRequestHandler["+GestoreHandlers.tipiPostOutRequestHandlers[i]+"]");
					throw ex;
				}
			}
		}
		
	}
	
	public static void preInResponse(PreInResponseContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log);
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)));
		}
		
		if(GestoreHandlers.preInResponseHandlers!=null){
			for(int i=0; i<GestoreHandlers.preInResponseHandlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(GestoreHandlers.preInResponseHandlers[i], msgDiag, log);
					GestoreHandlers.preInResponseHandlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(GestoreHandlers.preInResponseHandlers[i], msgDiag, log);
				}catch(Exception e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler("PreInResponseHandler["+GestoreHandlers.tipiPreInResponseHandlers[i]+"]");
					throw ex;
				}
			}
		}
		
	}
	
	public static void inResponse(InResponseContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log);
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)));
		}
		
		if(GestoreHandlers.inResponseHandlers!=null){
			for(int i=0; i<GestoreHandlers.inResponseHandlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(GestoreHandlers.inResponseHandlers[i], msgDiag, log);
					GestoreHandlers.inResponseHandlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(GestoreHandlers.inResponseHandlers[i], msgDiag, log);
				}catch(Exception e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler("InResponseHandler["+GestoreHandlers.tipiInResponseHandlers[i]+"]");
					throw ex;
				}
			}
		}
		
	}
	
	public static void outResponse(OutResponseContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log);
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)));
		}
		
		if(GestoreHandlers.outResponseHandlers!=null){
			for(int i=0; i<GestoreHandlers.outResponseHandlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(GestoreHandlers.outResponseHandlers[i], msgDiag, log);
					GestoreHandlers.outResponseHandlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(GestoreHandlers.outResponseHandlers[i], msgDiag, log);
				}catch(Exception e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler("OutResponseHandler["+GestoreHandlers.tipiOutResponseHandlers[i]+"]");
					throw ex;
				}
			}
		}
		
	}
	
	public static void postOutResponse(PostOutResponseContext context,MsgDiagnostico msgDiag,Logger log) {
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log);
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)));
		}
		
		if(GestoreHandlers.postOutResponseHandlers!=null){
			for(int i=0; i<GestoreHandlers.postOutResponseHandlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(GestoreHandlers.postOutResponseHandlers[i], msgDiag, log);
					GestoreHandlers.postOutResponseHandlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(GestoreHandlers.postOutResponseHandlers[i], msgDiag, log);
				}catch(Exception e){
					// Non sollevo l'eccezione poiche' dove viene chiamato questo handler e' finita la gestione, quindi l'eccezione non causa altri avvenimenti
					// Registro solamento l'evento
					msgDiag.logErroreGenerico(e,"PostOutResponseHandler["+GestoreHandlers.tipiPostOutResponseHandlers[i]+"]");
				}
			}
		}

		// Rilascio risorse per la generazione di date casuali
		GestoreHandlers.rilasciaRisorseDemoMode((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
	}
	
	
	
	public static void integrationManagerRequest(IntegrationManagerRequestContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log);
		}
		
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
			// genero date casuali
			if(context.getDataRichiestaOperazione()!=null){
				context.setDataRichiestaOperazione(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)));
			}
		}
		
		if(GestoreHandlers.integrationManagerRequestHandlers!=null){
			for(int i=0; i<GestoreHandlers.integrationManagerRequestHandlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(GestoreHandlers.integrationManagerRequestHandlers[i], msgDiag, log);
					GestoreHandlers.integrationManagerRequestHandlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(GestoreHandlers.integrationManagerRequestHandlers[i], msgDiag, log);
				}catch(Exception e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler("IntegrationManagerRequestHandler["+GestoreHandlers.tipiIntegrationManagerRequestHandlers[i]+"]");
					throw ex;
				}
			}
		}
	}
	
	
	public static void integrationManagerResponse(IntegrationManagerResponseContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log);
		}
		
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
			// genero date casuali
			if(context.getDataRichiestaOperazione()!=null){
				context.setDataRichiestaOperazione(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)));
			}
			if(context.getDataCompletamentoOperazione()!=null){
				context.setDataCompletamentoOperazione(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)));
			}
		}
		
		if(GestoreHandlers.integrationManagerResponseHandlers!=null){
			for(int i=0; i<GestoreHandlers.integrationManagerResponseHandlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(GestoreHandlers.integrationManagerResponseHandlers[i], msgDiag, log);
					GestoreHandlers.integrationManagerResponseHandlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(GestoreHandlers.integrationManagerResponseHandlers[i], msgDiag, log);
				}catch(Exception e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler("IntegrationManagerResponseHandler["+GestoreHandlers.tipiIntegrationManagerResponseHandlers[i]+"]");
					throw ex;
				}
			}
		}
		
		// Rilascio risorse per la generazione di date casuali
		GestoreHandlers.rilasciaRisorseDemoMode((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
	}

	
	private static void rilasciaRisorseDemoMode(String idCluster){
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && idCluster!=null){
			GeneratoreCasualeDate.getGeneratoreCasualeDate().releaseResource(idCluster);
		}
	}
}
