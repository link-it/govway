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


package org.openspcoop2.pdd.core.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.dynamic.PddPluginLoader;
import org.openspcoop2.pdd.core.PdDContext;
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
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;


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
	
	// Handlers BuiltIn
	
	private static InitHandler [] initHandlersBuiltIn = null;
	private static String [] tipiInitHandlersBuiltIn = null;
	
	private static ExitHandler [] exitHandlersBuiltIn = null;
	private static String [] tipiExitHandlersBuiltIn = null;
	
	private static PreInRequestHandler [] preInRequestHandlersBuiltIn = null;
	private static String [] tipiPreInRequestHandlersBuiltIn = null;
	
	private static InRequestHandler [] inRequestHandlersBuiltIn = null;
	private static String [] tipiInRequestHandlersBuiltIn = null;
	
	private static InRequestProtocolHandler [] inRequestProtocolHandlersBuiltIn = null;
	private static String [] tipiInRequestProtocolHandlersBuiltIn = null;
	
	private static OutRequestHandler [] outRequestHandlersBuiltIn = null;
	private static String [] tipiOutRequestHandlersBuiltIn = null;
	
	private static PostOutRequestHandler [] postOutRequestHandlersBuiltIn = null;
	private static String [] tipiPostOutRequestHandlersBuiltIn = null;
	
	private static PreInResponseHandler [] preInResponseHandlersBuiltIn = null;
	private static String [] tipiPreInResponseHandlersBuiltIn = null;
	
	private static InResponseHandler [] inResponseHandlersBuiltIn = null;
	private static String [] tipiInResponseHandlersBuiltIn = null;
	
	private static OutResponseHandler [] outResponseHandlersBuiltIn = null;
	private static String [] tipiOutResponseHandlersBuiltIn = null;
	
	private static PostOutResponseHandler [] postOutResponseHandlersBuiltIn = null;
	private static String [] tipiPostOutResponseHandlersBuiltIn = null;
	
	private static IntegrationManagerRequestHandler [] integrationManagerRequestHandlersBuiltIn = null;
	private static String [] tipiIntegrationManagerRequestHandlersBuiltIn = null;
	
	private static IntegrationManagerResponseHandler [] integrationManagerResponseHandlersBuiltIn = null;
	private static String [] tipiIntegrationManagerResponseHandlersBuiltIn = null;
	
	// Handlers Utente
	
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

	
	private static void initialize(MsgDiagnostico msgDiag,Logger logConsoleInit,IState state){
		initialize(msgDiag,null,logConsoleInit, state);
	}
	private static void initialize(Logger logCore,Logger logConsoleInit){
		initialize(null,logCore,logConsoleInit, null);
	}
	private synchronized static void initialize(MsgDiagnostico msgDiag,Logger logCore, Logger logConsoleInit,IState state){
		if(GestoreHandlers.initialize==false){
			
			GestoreHandlers.properties = OpenSPCoop2Properties.getInstance();
			ClassNameProperties classNameProperties = ClassNameProperties.getInstance();
			Loader loader = Loader.getInstance();
			PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
			if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && !UniqueIdentifierManager.isGenerazioneUIDDisabilitata()){
				GestoreHandlers.generatoreCasualeDati = GeneratoreCasualeDate.getGeneratoreCasualeDate();
			}
		
			
			// Handlers BuiltIn
			
			boolean printInfoBuiltIn = GestoreHandlers.properties.isPrintInfoHandlerBuiltIn();
			
			// InitHandler
			GestoreHandlers.tipiInitHandlersBuiltIn = GestoreHandlers.properties.getInitHandlerBuiltIn();
			if(GestoreHandlers.tipiInitHandlersBuiltIn!=null){
				GestoreHandlers.initHandlersBuiltIn = new InitHandler[GestoreHandlers.tipiInitHandlersBuiltIn.length];
				for(int i=0; i<GestoreHandlers.tipiInitHandlersBuiltIn.length; i++){
					try{
						String tipo=GestoreHandlers.tipiInitHandlersBuiltIn[i];
						String classe = classNameProperties.getInitHandlerBuiltIn(tipo);
						GestoreHandlers.initHandlersBuiltIn[i] = (InitHandler) loader.newInstance(classe);
						if(printInfoBuiltIn)
							logConsoleInit.info("InitHandler BuiltIn di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione InitHandler BuiltIn ["+GestoreHandlers.tipiInitHandlersBuiltIn[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione InitHandler BuiltIn ["+GestoreHandlers.tipiInitHandlersBuiltIn[i]+"]", e);
						}
					}
				}
				GestoreHandlers.initHandlersBuiltIn = reorder(GestoreHandlers.initHandlersBuiltIn, new InitHandler[1]);
			}
			
			// ExitHandler
			GestoreHandlers.tipiExitHandlersBuiltIn = GestoreHandlers.properties.getExitHandlerBuiltIn();
			if(GestoreHandlers.tipiExitHandlersBuiltIn!=null){
				GestoreHandlers.exitHandlersBuiltIn = new ExitHandler[GestoreHandlers.tipiExitHandlersBuiltIn.length];
				for(int i=0; i<GestoreHandlers.tipiExitHandlersBuiltIn.length; i++){
					try{
						String tipo=GestoreHandlers.tipiExitHandlersBuiltIn[i];
						String classe = classNameProperties.getExitHandlerBuiltIn(tipo);
						GestoreHandlers.exitHandlersBuiltIn[i] = (ExitHandler) loader.newInstance(classe);
						if(printInfoBuiltIn)
							logConsoleInit.info("ExitHandler BuiltIn di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione ExitHandler BuiltIn ["+GestoreHandlers.tipiExitHandlersBuiltIn[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione ExitHandler BuiltIn ["+GestoreHandlers.tipiExitHandlersBuiltIn[i]+"]", e);
						}
					}
				}
				GestoreHandlers.exitHandlersBuiltIn = reorder(GestoreHandlers.exitHandlersBuiltIn, new ExitHandler[1]);
			}
			
			// PreInRequestHandler
			GestoreHandlers.tipiPreInRequestHandlersBuiltIn = GestoreHandlers.properties.getPreInRequestHandlerBuiltIn();
			GestoreHandlers.tipiPreInRequestHandlersBuiltIn = updateNotifierCallback(GestoreHandlers.tipiPreInRequestHandlersBuiltIn);
			if(GestoreHandlers.tipiPreInRequestHandlersBuiltIn!=null){
				GestoreHandlers.preInRequestHandlersBuiltIn = new PreInRequestHandler[GestoreHandlers.tipiPreInRequestHandlersBuiltIn.length];
				for(int i=0; i<GestoreHandlers.tipiPreInRequestHandlersBuiltIn.length; i++){
					try{
						String tipo=GestoreHandlers.tipiPreInRequestHandlersBuiltIn[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierPreInRequestHandler.class.getName();
						}else{
							classe = classNameProperties.getPreInRequestHandlerBuiltIn(tipo);
						}
						GestoreHandlers.preInRequestHandlersBuiltIn[i] = (PreInRequestHandler) loader.newInstance(classe);
						if(printInfoBuiltIn)
							logConsoleInit.info("PreInRequestHandler BuiltIn di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione PreInRequestHandler BuiltIn ["+GestoreHandlers.tipiPreInRequestHandlersBuiltIn[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione PreInRequestHandler BuiltIn ["+GestoreHandlers.tipiPreInRequestHandlersBuiltIn[i]+"]", e);
						}
					}
				}
				GestoreHandlers.preInRequestHandlersBuiltIn = reorder(GestoreHandlers.preInRequestHandlersBuiltIn, new PreInRequestHandler[1]);
			}
			
			// InRequestHandler
			GestoreHandlers.tipiInRequestHandlersBuiltIn = GestoreHandlers.properties.getInRequestHandlerBuiltIn();
			GestoreHandlers.tipiInRequestHandlersBuiltIn = updateNotifierCallback(GestoreHandlers.tipiInRequestHandlersBuiltIn);
			if(GestoreHandlers.tipiInRequestHandlersBuiltIn!=null){
				GestoreHandlers.inRequestHandlersBuiltIn = new InRequestHandler[GestoreHandlers.tipiInRequestHandlersBuiltIn.length];
				for(int i=0; i<GestoreHandlers.tipiInRequestHandlersBuiltIn.length; i++){
					try{
						String tipo=GestoreHandlers.tipiInRequestHandlersBuiltIn[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierInRequestHandler.class.getName();
						}else{
							classe = classNameProperties.getInRequestHandlerBuiltIn(tipo);
						}
						GestoreHandlers.inRequestHandlersBuiltIn[i] = (InRequestHandler) loader.newInstance(classe);
						if(printInfoBuiltIn)
							logConsoleInit.info("InRequestHandler BuiltIn di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione InRequestHandler BuiltIn ["+GestoreHandlers.tipiInRequestHandlersBuiltIn[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione InRequestHandler BuiltIn ["+GestoreHandlers.tipiInRequestHandlersBuiltIn[i]+"]", e);
						}
					}
				}
				GestoreHandlers.inRequestHandlersBuiltIn = reorder(GestoreHandlers.inRequestHandlersBuiltIn, new InRequestHandler[1]);
			}
			
			// InRequestProtocolHandler
			GestoreHandlers.tipiInRequestProtocolHandlersBuiltIn = GestoreHandlers.properties.getInRequestProtocolHandlerBuiltIn();
			GestoreHandlers.tipiInRequestProtocolHandlersBuiltIn = updateNotifierCallback(GestoreHandlers.tipiInRequestProtocolHandlersBuiltIn);
			if(GestoreHandlers.tipiInRequestProtocolHandlersBuiltIn!=null){
				GestoreHandlers.inRequestProtocolHandlersBuiltIn = new InRequestProtocolHandler[GestoreHandlers.tipiInRequestProtocolHandlersBuiltIn.length];
				for(int i=0; i<GestoreHandlers.tipiInRequestProtocolHandlersBuiltIn.length; i++){
					try{
						String tipo=GestoreHandlers.tipiInRequestProtocolHandlersBuiltIn[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierInRequestProtocolHandler.class.getName();
						}else{
							classe = classNameProperties.getInRequestProtocolHandlerBuiltIn(tipo);
						}
						GestoreHandlers.inRequestProtocolHandlersBuiltIn[i] = (InRequestProtocolHandler) loader.newInstance(classe);
						if(printInfoBuiltIn)
							logConsoleInit.info("InRequestProtocolHandler BuiltIn di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione InRequestProtocolHandler BuiltIn ["+GestoreHandlers.tipiInRequestProtocolHandlersBuiltIn[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione InRequestProtocolHandler BuiltIn ["+GestoreHandlers.tipiInRequestProtocolHandlersBuiltIn[i]+"]", e);
						}
					}
				}
				GestoreHandlers.inRequestProtocolHandlersBuiltIn = reorder(GestoreHandlers.inRequestProtocolHandlersBuiltIn, new InRequestProtocolHandler[1]);
			}
			
			// OutRequestHandler
			GestoreHandlers.tipiOutRequestHandlersBuiltIn = GestoreHandlers.properties.getOutRequestHandlerBuiltIn();
			GestoreHandlers.tipiOutRequestHandlersBuiltIn = updateNotifierCallback(GestoreHandlers.tipiOutRequestHandlersBuiltIn);
			if(GestoreHandlers.tipiOutRequestHandlersBuiltIn!=null){
				GestoreHandlers.outRequestHandlersBuiltIn = new OutRequestHandler[GestoreHandlers.tipiOutRequestHandlersBuiltIn.length];
				for(int i=0; i<GestoreHandlers.tipiOutRequestHandlersBuiltIn.length; i++){
					try{
						String tipo=GestoreHandlers.tipiOutRequestHandlersBuiltIn[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierOutRequestHandler.class.getName();
						}else{
							classe = classNameProperties.getOutRequestHandlerBuiltIn(tipo);
						}
						GestoreHandlers.outRequestHandlersBuiltIn[i] = (OutRequestHandler) loader.newInstance(classe);
						if(printInfoBuiltIn)
							logConsoleInit.info("OutRequestHandler BuiltIn di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione OutRequestHandler BuiltIn ["+GestoreHandlers.tipiOutRequestHandlersBuiltIn[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione OutRequestHandler BuiltIn ["+GestoreHandlers.tipiOutRequestHandlersBuiltIn[i]+"]", e);
						}
					}
				}
				GestoreHandlers.outRequestHandlersBuiltIn = reorder(GestoreHandlers.outRequestHandlersBuiltIn, new OutRequestHandler[1]);
			}
			
			// PostOutRequestHandler
			GestoreHandlers.tipiPostOutRequestHandlersBuiltIn = properties.getPostOutRequestHandlerBuiltIn();
			GestoreHandlers.tipiPostOutRequestHandlersBuiltIn = updateNotifierCallback(GestoreHandlers.tipiPostOutRequestHandlersBuiltIn);
			if(GestoreHandlers.tipiPostOutRequestHandlersBuiltIn!=null){
				GestoreHandlers.postOutRequestHandlersBuiltIn = new PostOutRequestHandler[GestoreHandlers.tipiPostOutRequestHandlersBuiltIn.length];
				for(int i=0; i<GestoreHandlers.tipiPostOutRequestHandlersBuiltIn.length; i++){
					try{
						String tipo=GestoreHandlers.tipiPostOutRequestHandlersBuiltIn[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierPostOutRequestHandler.class.getName();
						}else{
							classe = classNameProperties.getPostOutRequestHandlerBuiltIn(tipo);
						}
						GestoreHandlers.postOutRequestHandlersBuiltIn[i] = (PostOutRequestHandler) loader.newInstance(classe);
						if(printInfoBuiltIn)
							logConsoleInit.info("PostOutRequestHandler BuiltIn di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione PostOutRequestHandler BuiltIn ["+GestoreHandlers.tipiPostOutRequestHandlersBuiltIn[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione PostOutRequestHandler BuiltIn ["+GestoreHandlers.tipiPostOutRequestHandlersBuiltIn[i]+"]", e);
						}
					}
				}
				GestoreHandlers.postOutRequestHandlersBuiltIn = reorder(GestoreHandlers.postOutRequestHandlersBuiltIn, new PostOutRequestHandler[1]);
			}
			
			// PreInResponseHandler
			GestoreHandlers.tipiPreInResponseHandlersBuiltIn = properties.getPreInResponseHandlerBuiltIn();
			GestoreHandlers.tipiPreInResponseHandlersBuiltIn = updateNotifierCallback(GestoreHandlers.tipiPreInResponseHandlersBuiltIn);
			if(GestoreHandlers.tipiPreInResponseHandlersBuiltIn!=null){
				GestoreHandlers.preInResponseHandlersBuiltIn = new PreInResponseHandler[GestoreHandlers.tipiPreInResponseHandlersBuiltIn.length];
				for(int i=0; i<GestoreHandlers.tipiPreInResponseHandlersBuiltIn.length; i++){
					try{
						String tipo=GestoreHandlers.tipiPreInResponseHandlersBuiltIn[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierPreInResponseHandler.class.getName();
						}else{
							classe = classNameProperties.getPreInResponseHandlerBuiltIn(tipo);
						}
						GestoreHandlers.preInResponseHandlersBuiltIn[i] = (PreInResponseHandler) loader.newInstance(classe);
						if(printInfoBuiltIn)
							logConsoleInit.info("PreInResponseHandler BuiltIn di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione PreInResponseHandler BuiltIn ["+GestoreHandlers.tipiPreInResponseHandlersBuiltIn[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione PreInResponseHandler BuiltIn ["+GestoreHandlers.tipiPreInResponseHandlersBuiltIn[i]+"]", e);
						}
					}
				}
				GestoreHandlers.preInResponseHandlersBuiltIn = reorder(GestoreHandlers.preInResponseHandlersBuiltIn, new PreInResponseHandler[1]);
			}
			
			// InResponseHandler
			GestoreHandlers.tipiInResponseHandlersBuiltIn = GestoreHandlers.properties.getInResponseHandlerBuiltIn();
			GestoreHandlers.tipiInResponseHandlersBuiltIn = updateNotifierCallback(GestoreHandlers.tipiInResponseHandlersBuiltIn);
			if(GestoreHandlers.tipiInResponseHandlersBuiltIn!=null){
				GestoreHandlers.inResponseHandlersBuiltIn = new InResponseHandler[GestoreHandlers.tipiInResponseHandlersBuiltIn.length];
				for(int i=0; i<GestoreHandlers.tipiInResponseHandlersBuiltIn.length; i++){
					try{
						String tipo=GestoreHandlers.tipiInResponseHandlersBuiltIn[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierInResponseHandler.class.getName();
						}else{
							classe = classNameProperties.getInResponseHandlerBuiltIn(tipo);
						}
						GestoreHandlers.inResponseHandlersBuiltIn[i] = (InResponseHandler) loader.newInstance(classe);
						if(printInfoBuiltIn)
							logConsoleInit.info("InResponseHandler BuiltIn di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione InResponseHandler BuiltIn ["+GestoreHandlers.tipiInResponseHandlersBuiltIn[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione InResponseHandler BuiltIn ["+GestoreHandlers.tipiInResponseHandlersBuiltIn[i]+"]", e);
						}
					}
				}
				GestoreHandlers.inResponseHandlersBuiltIn = reorder(GestoreHandlers.inResponseHandlersBuiltIn, new InResponseHandler[1]);
			}
			
			// OutResponseHandler
			GestoreHandlers.tipiOutResponseHandlersBuiltIn = GestoreHandlers.properties.getOutResponseHandlerBuiltIn();
			GestoreHandlers.tipiOutResponseHandlersBuiltIn = updateNotifierCallback(GestoreHandlers.tipiOutResponseHandlersBuiltIn);
			if(GestoreHandlers.tipiOutResponseHandlersBuiltIn!=null){
				GestoreHandlers.outResponseHandlersBuiltIn = new OutResponseHandler[GestoreHandlers.tipiOutResponseHandlersBuiltIn.length];
				for(int i=0; i<GestoreHandlers.tipiOutResponseHandlersBuiltIn.length; i++){
					try{
						String tipo=GestoreHandlers.tipiOutResponseHandlersBuiltIn[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierOutResponseHandler.class.getName();
						}else{
							classe = classNameProperties.getOutResponseHandlerBuiltIn(tipo);
						}
						GestoreHandlers.outResponseHandlersBuiltIn[i] = (OutResponseHandler) loader.newInstance(classe);
						if(printInfoBuiltIn)
							logConsoleInit.info("OutResponseHandler BuiltIn di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione OutResponseHandler BuiltIn ["+GestoreHandlers.tipiOutResponseHandlersBuiltIn[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione OutResponseHandler BuiltIn ["+GestoreHandlers.tipiOutResponseHandlersBuiltIn[i]+"]", e);
						}
					}
				}
				GestoreHandlers.outResponseHandlersBuiltIn = reorder(GestoreHandlers.outResponseHandlersBuiltIn, new OutResponseHandler[1]);
			}
			
			// PostOutResponseHandler
			GestoreHandlers.tipiPostOutResponseHandlersBuiltIn = GestoreHandlers.properties.getPostOutResponseHandlerBuiltIn();
			GestoreHandlers.tipiPostOutResponseHandlersBuiltIn = updateNotifierCallback(GestoreHandlers.tipiPostOutResponseHandlersBuiltIn);
			if(GestoreHandlers.tipiPostOutResponseHandlersBuiltIn!=null){
				GestoreHandlers.postOutResponseHandlersBuiltIn = new PostOutResponseHandler[GestoreHandlers.tipiPostOutResponseHandlersBuiltIn.length];
				for(int i=0; i<GestoreHandlers.tipiPostOutResponseHandlersBuiltIn.length; i++){
					try{
						String tipo=GestoreHandlers.tipiPostOutResponseHandlersBuiltIn[i];
						String classe = null;
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							classe = NotifierPostOutResponseHandler.class.getName();
						}else{
							classe = classNameProperties.getPostOutResponseHandlerBuiltIn(tipo);
						}
						GestoreHandlers.postOutResponseHandlersBuiltIn[i] = (PostOutResponseHandler) loader.newInstance(classe);
						if(printInfoBuiltIn)
							logConsoleInit.info("PostOutResponseHandler BuiltIn di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione PostOutResponseHandler BuiltIn ["+GestoreHandlers.tipiPostOutResponseHandlersBuiltIn[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione PostOutResponseHandler BuiltIn ["+GestoreHandlers.tipiPostOutResponseHandlersBuiltIn[i]+"]", e);
						}
					}
				}
				GestoreHandlers.postOutResponseHandlersBuiltIn = reorder(GestoreHandlers.postOutResponseHandlersBuiltIn, new PostOutResponseHandler[1]);
			}
			
			// IntegrationManagerRequestHandler
			GestoreHandlers.tipiIntegrationManagerRequestHandlersBuiltIn = GestoreHandlers.properties.getIntegrationManagerRequestHandlerBuiltIn();
			if(GestoreHandlers.tipiIntegrationManagerRequestHandlersBuiltIn!=null){
				GestoreHandlers.integrationManagerRequestHandlersBuiltIn = new IntegrationManagerRequestHandler[GestoreHandlers.tipiIntegrationManagerRequestHandlersBuiltIn.length];
				for(int i=0; i<GestoreHandlers.tipiIntegrationManagerRequestHandlersBuiltIn.length; i++){
					try{
						String tipo=GestoreHandlers.tipiIntegrationManagerRequestHandlersBuiltIn[i];
						String classe = classNameProperties.getIntegrationManagerRequestHandlerBuiltIn(tipo);
						GestoreHandlers.integrationManagerRequestHandlersBuiltIn[i] = (IntegrationManagerRequestHandler) loader.newInstance(classe);
						if(printInfoBuiltIn)
							logConsoleInit.info("IntegrationManagerRequestHandler BuiltIn di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione IntegrationManagerRequestHandler BuiltIn ["+GestoreHandlers.tipiIntegrationManagerRequestHandlersBuiltIn[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione IntegrationManagerRequestHandler BuiltIn ["+GestoreHandlers.tipiIntegrationManagerRequestHandlersBuiltIn[i]+"]", e);
						}
					}
				}
				GestoreHandlers.integrationManagerRequestHandlersBuiltIn = reorder(GestoreHandlers.integrationManagerRequestHandlersBuiltIn, new IntegrationManagerRequestHandler[1]);
			}
			
			// IntegrationManagerResponseHandler
			GestoreHandlers.tipiIntegrationManagerResponseHandlersBuiltIn = GestoreHandlers.properties.getIntegrationManagerResponseHandlerBuiltIn();
			if(GestoreHandlers.tipiIntegrationManagerResponseHandlersBuiltIn!=null){
				GestoreHandlers.integrationManagerResponseHandlersBuiltIn = new IntegrationManagerResponseHandler[GestoreHandlers.tipiIntegrationManagerResponseHandlersBuiltIn.length];
				for(int i=0; i<GestoreHandlers.tipiIntegrationManagerResponseHandlersBuiltIn.length; i++){
					try{
						String tipo=GestoreHandlers.tipiIntegrationManagerResponseHandlersBuiltIn[i];
						String classe = classNameProperties.getIntegrationManagerResponseHandlerBuiltIn(tipo);
						GestoreHandlers.integrationManagerResponseHandlersBuiltIn[i] = (IntegrationManagerResponseHandler) loader.newInstance(classe);
						if(printInfoBuiltIn)
							logConsoleInit.info("IntegrationManagerResponseHandler BuiltIn di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione IntegrationManagerResponseHandler BuiltIn ["+GestoreHandlers.tipiIntegrationManagerResponseHandlersBuiltIn[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione IntegrationManagerResponseHandler BuiltIn ["+GestoreHandlers.tipiIntegrationManagerResponseHandlersBuiltIn[i]+"]", e);
						}
					}
				}
				GestoreHandlers.integrationManagerResponseHandlersBuiltIn = reorder(GestoreHandlers.integrationManagerResponseHandlersBuiltIn, new IntegrationManagerResponseHandler[1]);
			}
			
			
			
			// Handlers Utente

			boolean printInfo = GestoreHandlers.properties.isPrintInfoHandler();
			
			// InitHandler
			List<String> tipiInitHandlersConfig = null;
			try {
				tipiInitHandlersConfig=ConfigurazionePdDManager.getInstance(state).getInitHandlers();
			}catch(Throwable e) {
				if(msgDiag!=null) {
					msgDiag.logErroreGenerico(e, "Raccolta InitHandlers");
				}
				else if(logCore!=null) {
					logCore.error("Raccolta InitHandlers", e);
				}
			}
			GestoreHandlers.tipiInitHandlers = _mergeTipi(GestoreHandlers.properties.getInitHandler(),
					tipiInitHandlersConfig);
			if(GestoreHandlers.tipiInitHandlers!=null){
				GestoreHandlers.initHandlers = new InitHandler[GestoreHandlers.tipiInitHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiInitHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiInitHandlers[i];
						GestoreHandlers.initHandlers[i] = (InitHandler) pluginLoader.newInitHandler(tipo);
						if(printInfo)
							logConsoleInit.info("InitHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione InitHandler ["+GestoreHandlers.tipiInitHandlers[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione InitHandler ["+GestoreHandlers.tipiInitHandlers[i]+"]", e);
						}
					}
				}
				GestoreHandlers.initHandlers = reorder(GestoreHandlers.initHandlers, new InitHandler[1]);
			}
			
			// ExitHandler
			List<String> tipiExitHandlersConfig = null;
			try {
				tipiExitHandlersConfig=ConfigurazionePdDManager.getInstance(state).getExitHandlers();
			}catch(Throwable e) {
				if(msgDiag!=null) {
					msgDiag.logErroreGenerico(e, "Raccolta ExitHandlers");
				}
				else if(logCore!=null) {
					logCore.error("Raccolta ExitHandlers", e);
				}
			}
			GestoreHandlers.tipiExitHandlers = _mergeTipi(GestoreHandlers.properties.getExitHandler(),
					tipiExitHandlersConfig);
			if(GestoreHandlers.tipiExitHandlers!=null){
				GestoreHandlers.exitHandlers = new ExitHandler[GestoreHandlers.tipiExitHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiExitHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiExitHandlers[i];
						GestoreHandlers.exitHandlers[i] = (ExitHandler) pluginLoader.newExitHandler(tipo);
						if(printInfo)
							logConsoleInit.info("ExitHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione ExitHandler ["+GestoreHandlers.tipiExitHandlers[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione ExitHandler ["+GestoreHandlers.tipiExitHandlers[i]+"]", e);
						}
					}
				}
				GestoreHandlers.exitHandlers = reorder(GestoreHandlers.exitHandlers, new ExitHandler[1]);
			}
			
			// PreInRequestHandler
			List<String> tipiPreInRequestHandlersConfig = null;
			try {
				tipiPreInRequestHandlersConfig=ConfigurazionePdDManager.getInstance(state).getPreInRequestHandlers();
			}catch(Throwable e) {
				if(msgDiag!=null) {
					msgDiag.logErroreGenerico(e, "Raccolta PreInRequestHandlers");
				}
				else if(logCore!=null) {
					logCore.error("Raccolta PreInRequestHandlers", e);
				}
			}
			GestoreHandlers.tipiPreInRequestHandlers = _mergeTipi(GestoreHandlers.properties.getPreInRequestHandler(),
					tipiPreInRequestHandlersConfig);
			GestoreHandlers.tipiPreInRequestHandlers = updateNotifierCallback(GestoreHandlers.tipiPreInRequestHandlers);
			if(GestoreHandlers.tipiPreInRequestHandlers!=null){
				GestoreHandlers.preInRequestHandlers = new PreInRequestHandler[GestoreHandlers.tipiPreInRequestHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiPreInRequestHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiPreInRequestHandlers[i];
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							String classe = NotifierPreInRequestHandler.class.getName();
							GestoreHandlers.preInRequestHandlers[i] = (PreInRequestHandler) loader.newInstance(classe);
						}else{
							GestoreHandlers.preInRequestHandlers[i] = pluginLoader.newPreInRequestHandler(tipo);
						}
						if(printInfo)
							logConsoleInit.info("PreInRequestHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione PreInRequestHandler ["+GestoreHandlers.tipiPreInRequestHandlers[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione PreInRequestHandler ["+GestoreHandlers.tipiPreInRequestHandlers[i]+"]", e);
						}
					}
				}
				GestoreHandlers.preInRequestHandlers = reorder(GestoreHandlers.preInRequestHandlers, new PreInRequestHandler[1]);
			}
			
			// InRequestHandler
			List<String> tipiInRequestHandlersConfig = null;
			try {
				tipiInRequestHandlersConfig=ConfigurazionePdDManager.getInstance(state).getInRequestHandlers();
			}catch(Throwable e) {
				if(msgDiag!=null) {
					msgDiag.logErroreGenerico(e, "Raccolta InRequestHandlers");
				}
				else if(logCore!=null) {
					logCore.error("Raccolta InRequestHandlers", e);
				}
			}
			GestoreHandlers.tipiInRequestHandlers = _mergeTipi(GestoreHandlers.properties.getInRequestHandler(),
					tipiInRequestHandlersConfig);
			GestoreHandlers.tipiInRequestHandlers = updateNotifierCallback(GestoreHandlers.tipiInRequestHandlers);
			if(GestoreHandlers.tipiInRequestHandlers!=null){
				GestoreHandlers.inRequestHandlers = new InRequestHandler[GestoreHandlers.tipiInRequestHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiInRequestHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiInRequestHandlers[i];
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							String classe = NotifierInRequestHandler.class.getName();
							GestoreHandlers.inRequestHandlers[i] = (InRequestHandler) loader.newInstance(classe);
						}else{
							GestoreHandlers.inRequestHandlers[i] = pluginLoader.newInRequestHandler(tipo);
						}
						if(printInfo)
							logConsoleInit.info("InRequestHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione InRequestHandler ["+GestoreHandlers.tipiInRequestHandlers[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione InRequestHandler ["+GestoreHandlers.tipiInRequestHandlers[i]+"]", e);
						}
					}
				}
				GestoreHandlers.inRequestHandlers = reorder(GestoreHandlers.inRequestHandlers, new InRequestHandler[1]);
			}
			
			// InRequestProtocolHandler
			List<String> tipiInRequestProtocolHandlersConfig = null;
			try {
				tipiInRequestProtocolHandlersConfig=ConfigurazionePdDManager.getInstance(state).getInRequestProtocolHandlers();
			}catch(Throwable e) {
				if(msgDiag!=null) {
					msgDiag.logErroreGenerico(e, "Raccolta InRequestProtocolHandlers");
				}
				else if(logCore!=null) {
					logCore.error("Raccolta InRequestProtocolHandlers", e);
				}
			}
			GestoreHandlers.tipiInRequestProtocolHandlers = _mergeTipi(GestoreHandlers.properties.getInRequestProtocolHandler(),
					tipiInRequestProtocolHandlersConfig);
			GestoreHandlers.tipiInRequestProtocolHandlers = updateNotifierCallback(GestoreHandlers.tipiInRequestProtocolHandlers);
			if(GestoreHandlers.tipiInRequestProtocolHandlers!=null){
				GestoreHandlers.inRequestProtocolHandlers = new InRequestProtocolHandler[GestoreHandlers.tipiInRequestProtocolHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiInRequestProtocolHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiInRequestProtocolHandlers[i];
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							String classe = NotifierInRequestProtocolHandler.class.getName();
							GestoreHandlers.inRequestProtocolHandlers[i] = (InRequestProtocolHandler) loader.newInstance(classe);
						}else{
							GestoreHandlers.inRequestProtocolHandlers[i] = pluginLoader.newInRequestProtocolHandler(tipo);
						}
						if(printInfo)
							logConsoleInit.info("InRequestProtocolHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione InRequestProtocolHandler ["+GestoreHandlers.tipiInRequestProtocolHandlers[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione InRequestProtocolHandler ["+GestoreHandlers.tipiInRequestProtocolHandlers[i]+"]", e);
						}
					}
				}
				GestoreHandlers.inRequestProtocolHandlers = reorder(GestoreHandlers.inRequestProtocolHandlers, new InRequestProtocolHandler[1]);
			}
			
			// OutRequestHandler
			List<String> tipiOutRequestHandlersConfig = null;
			try {
				tipiOutRequestHandlersConfig=ConfigurazionePdDManager.getInstance(state).getOutRequestHandlers();
			}catch(Throwable e) {
				if(msgDiag!=null) {
					msgDiag.logErroreGenerico(e, "Raccolta OutRequestHandlers");
				}
				else if(logCore!=null) {
					logCore.error("Raccolta OutRequestHandlers", e);
				}
			}
			GestoreHandlers.tipiOutRequestHandlers = _mergeTipi(GestoreHandlers.properties.getOutRequestHandler(),
					tipiOutRequestHandlersConfig);
			GestoreHandlers.tipiOutRequestHandlers = updateNotifierCallback(GestoreHandlers.tipiOutRequestHandlers);
			if(GestoreHandlers.tipiOutRequestHandlers!=null){
				GestoreHandlers.outRequestHandlers = new OutRequestHandler[GestoreHandlers.tipiOutRequestHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiOutRequestHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiOutRequestHandlers[i];
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							String classe = NotifierOutRequestHandler.class.getName();
							GestoreHandlers.outRequestHandlers[i] = (OutRequestHandler) loader.newInstance(classe);
						}else{
							GestoreHandlers.outRequestHandlers[i] = pluginLoader.newOutRequestHandler(tipo);
						}
						if(printInfo)
							logConsoleInit.info("OutRequestHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione OutRequestHandler ["+GestoreHandlers.tipiOutRequestHandlers[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione OutRequestHandler ["+GestoreHandlers.tipiOutRequestHandlers[i]+"]", e);
						}
					}
				}
				GestoreHandlers.outRequestHandlers = reorder(GestoreHandlers.outRequestHandlers, new OutRequestHandler[1]);
			}
			
			// PostOutRequestHandler
			List<String> tipiPostOutRequestHandlersConfig = null;
			try {
				tipiPostOutRequestHandlersConfig=ConfigurazionePdDManager.getInstance(state).getPostOutRequestHandlers();
			}catch(Throwable e) {
				if(msgDiag!=null) {
					msgDiag.logErroreGenerico(e, "Raccolta PostOutRequestHandlers");
				}
				else if(logCore!=null) {
					logCore.error("Raccolta PostOutRequestHandlers", e);
				}
			}
			GestoreHandlers.tipiPostOutRequestHandlers = _mergeTipi(GestoreHandlers.properties.getPostOutRequestHandler(),
					tipiPostOutRequestHandlersConfig);
			GestoreHandlers.tipiPostOutRequestHandlers = updateNotifierCallback(GestoreHandlers.tipiPostOutRequestHandlers);
			if(GestoreHandlers.tipiPostOutRequestHandlers!=null){
				GestoreHandlers.postOutRequestHandlers = new PostOutRequestHandler[GestoreHandlers.tipiPostOutRequestHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiPostOutRequestHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiPostOutRequestHandlers[i];
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							String classe = NotifierPostOutRequestHandler.class.getName();
							GestoreHandlers.postOutRequestHandlers[i] = (PostOutRequestHandler) loader.newInstance(classe);
						}else{
							GestoreHandlers.postOutRequestHandlers[i] = pluginLoader.newPostOutRequestHandler(tipo);
						}
						if(printInfo)
							logConsoleInit.info("PostOutRequestHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione PostOutRequestHandler ["+GestoreHandlers.tipiPostOutRequestHandlers[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione PostOutRequestHandler ["+GestoreHandlers.tipiPostOutRequestHandlers[i]+"]", e);
						}
					}
				}
				GestoreHandlers.postOutRequestHandlers = reorder(GestoreHandlers.postOutRequestHandlers, new PostOutRequestHandler[1]);
			}
			
			// PreInResponseHandler
			List<String> tipiPreInResponseHandlersConfig = null;
			try {
				tipiPreInResponseHandlersConfig=ConfigurazionePdDManager.getInstance(state).getPreInResponseHandlers();
			}catch(Throwable e) {
				if(msgDiag!=null) {
					msgDiag.logErroreGenerico(e, "Raccolta PreInResponseHandlers");
				}
				else if(logCore!=null) {
					logCore.error("Raccolta PreInResponseHandlers", e);
				}
			}
			GestoreHandlers.tipiPreInResponseHandlers = _mergeTipi(GestoreHandlers.properties.getPreInResponseHandler(),
					tipiPreInResponseHandlersConfig);
			GestoreHandlers.tipiPreInResponseHandlers = updateNotifierCallback(GestoreHandlers.tipiPreInResponseHandlers);
			if(GestoreHandlers.tipiPreInResponseHandlers!=null){
				GestoreHandlers.preInResponseHandlers = new PreInResponseHandler[GestoreHandlers.tipiPreInResponseHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiPreInResponseHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiPreInResponseHandlers[i];
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							String classe = NotifierPreInResponseHandler.class.getName();
							GestoreHandlers.preInResponseHandlers[i] = (PreInResponseHandler) loader.newInstance(classe);
						}else{
							GestoreHandlers.preInResponseHandlers[i] = pluginLoader.newPreInResponseHandler(tipo);
						}
						if(printInfo)
							logConsoleInit.info("PreInResponseHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione PreInResponseHandler ["+GestoreHandlers.tipiPreInResponseHandlers[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione PreInResponseHandler ["+GestoreHandlers.tipiPreInResponseHandlers[i]+"]", e);
						}
					}
				}
				GestoreHandlers.preInResponseHandlers = reorder(GestoreHandlers.preInResponseHandlers, new PreInResponseHandler[1]);
			}
			
			// InResponseHandler
			List<String> tipiInResponseHandlersConfig = null;
			try {
				tipiInResponseHandlersConfig=ConfigurazionePdDManager.getInstance(state).getInResponseHandlers();
			}catch(Throwable e) {
				if(msgDiag!=null) {
					msgDiag.logErroreGenerico(e, "Raccolta InResponseHandlers");
				}
				else if(logCore!=null) {
					logCore.error("Raccolta InResponseHandlers", e);
				}
			}
			GestoreHandlers.tipiInResponseHandlers = _mergeTipi(GestoreHandlers.properties.getInResponseHandler(),
					tipiInResponseHandlersConfig);
			GestoreHandlers.tipiInResponseHandlers = updateNotifierCallback(GestoreHandlers.tipiInResponseHandlers);
			if(GestoreHandlers.tipiInResponseHandlers!=null){
				GestoreHandlers.inResponseHandlers = new InResponseHandler[GestoreHandlers.tipiInResponseHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiInResponseHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiInResponseHandlers[i];
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							String classe = NotifierInResponseHandler.class.getName();
							GestoreHandlers.inResponseHandlers[i] = (InResponseHandler) loader.newInstance(classe);
						}else{
							GestoreHandlers.inResponseHandlers[i] = pluginLoader.newInResponseHandler(tipo);
						}
						if(printInfo)
							logConsoleInit.info("InResponseHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione InResponseHandler ["+GestoreHandlers.tipiInResponseHandlers[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione InResponseHandler ["+GestoreHandlers.tipiInResponseHandlers[i]+"]", e);
						}
					}
				}
				GestoreHandlers.inResponseHandlers = reorder(GestoreHandlers.inResponseHandlers, new InResponseHandler[1]);
			}
			
			// OutResponseHandler
			List<String> tipiOutResponseHandlersConfig = null;
			try {
				tipiOutResponseHandlersConfig=ConfigurazionePdDManager.getInstance(state).getOutResponseHandlers();
			}catch(Throwable e) {
				if(msgDiag!=null) {
					msgDiag.logErroreGenerico(e, "Raccolta OutResponseHandlers");
				}
				else if(logCore!=null) {
					logCore.error("Raccolta OutResponseHandlers", e);
				}
			}
			GestoreHandlers.tipiOutResponseHandlers = _mergeTipi(GestoreHandlers.properties.getOutResponseHandler(),
					tipiOutResponseHandlersConfig);
			GestoreHandlers.tipiOutResponseHandlers = updateNotifierCallback(GestoreHandlers.tipiOutResponseHandlers);
			if(GestoreHandlers.tipiOutResponseHandlers!=null){
				GestoreHandlers.outResponseHandlers = new OutResponseHandler[GestoreHandlers.tipiOutResponseHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiOutResponseHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiOutResponseHandlers[i];
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							String classe = NotifierOutResponseHandler.class.getName();
							GestoreHandlers.outResponseHandlers[i] = (OutResponseHandler) loader.newInstance(classe);
						}else{
							GestoreHandlers.outResponseHandlers[i] = pluginLoader.newOutResponseHandler(tipo);
						}
						if(printInfo)
							logConsoleInit.info("OutResponseHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione OutResponseHandler ["+GestoreHandlers.tipiOutResponseHandlers[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione OutResponseHandler ["+GestoreHandlers.tipiOutResponseHandlers[i]+"]", e);
						}
					}
				}
				GestoreHandlers.outResponseHandlers = reorder(GestoreHandlers.outResponseHandlers, new OutResponseHandler[1]);
			}
			
			// PostOutResponseHandler
			List<String> tipiPostOutResponseHandlersConfig = null;
			try {
				tipiPostOutResponseHandlersConfig=ConfigurazionePdDManager.getInstance(state).getPostOutResponseHandlers();
			}catch(Throwable e) {
				if(msgDiag!=null) {
					msgDiag.logErroreGenerico(e, "Raccolta PostOutResponseHandlers");
				}
				else if(logCore!=null) {
					logCore.error("Raccolta PostOutResponseHandlers", e);
				}
			}
			GestoreHandlers.tipiPostOutResponseHandlers = _mergeTipi(GestoreHandlers.properties.getPostOutResponseHandler(),
					tipiPostOutResponseHandlersConfig);
			GestoreHandlers.tipiPostOutResponseHandlers = updateNotifierCallback(GestoreHandlers.tipiPostOutResponseHandlers);
			if(GestoreHandlers.tipiPostOutResponseHandlers!=null){
				GestoreHandlers.postOutResponseHandlers = new PostOutResponseHandler[GestoreHandlers.tipiPostOutResponseHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiPostOutResponseHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiPostOutResponseHandlers[i];
						if(NotifierConstants.TIPO_NOTIFIER.equals(tipo)){
							String classe = NotifierPostOutResponseHandler.class.getName();
							GestoreHandlers.postOutResponseHandlers[i] = (PostOutResponseHandler) loader.newInstance(classe);
						}else{
							GestoreHandlers.postOutResponseHandlers[i] = pluginLoader.newPostOutResponseHandler(tipo);
						}
						if(printInfo)
							logConsoleInit.info("PostOutResponseHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione PostOutResponseHandler ["+GestoreHandlers.tipiPostOutResponseHandlers[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione PostOutResponseHandler ["+GestoreHandlers.tipiPostOutResponseHandlers[i]+"]", e);
						}
					}
				}
				GestoreHandlers.postOutResponseHandlers = reorder(GestoreHandlers.postOutResponseHandlers, new PostOutResponseHandler[1]);
			}
			
			// IntegrationManagerRequestHandler
			List<String> tipiIntegrationManagerRequestHandlersConfig = null;
			try {
				tipiIntegrationManagerRequestHandlersConfig=ConfigurazionePdDManager.getInstance(state).getIntegrationManagerRequestHandlers();
			}catch(Throwable e) {
				if(msgDiag!=null) {
					msgDiag.logErroreGenerico(e, "Raccolta IntegrationManagerRequestHandlers");
				}
				else if(logCore!=null) {
					logCore.error("Raccolta IntegrationManagerRequestHandlers", e);
				}
			}
			GestoreHandlers.tipiIntegrationManagerRequestHandlers = _mergeTipi(GestoreHandlers.properties.getIntegrationManagerRequestHandler(),
					tipiIntegrationManagerRequestHandlersConfig);
			if(GestoreHandlers.tipiIntegrationManagerRequestHandlers!=null){
				GestoreHandlers.integrationManagerRequestHandlers = new IntegrationManagerRequestHandler[GestoreHandlers.tipiIntegrationManagerRequestHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiIntegrationManagerRequestHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiIntegrationManagerRequestHandlers[i];
						GestoreHandlers.integrationManagerRequestHandlers[i] = (IntegrationManagerRequestHandler) pluginLoader.newIntegrationManagerRequestHandler(tipo);
						if(printInfo)
							logConsoleInit.info("IntegrationManagerRequestHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione IntegrationManagerRequestHandler ["+GestoreHandlers.tipiIntegrationManagerRequestHandlers[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione IntegrationManagerRequestHandler ["+GestoreHandlers.tipiIntegrationManagerRequestHandlers[i]+"]", e);
						}
					}
				}
				GestoreHandlers.integrationManagerRequestHandlers = reorder(GestoreHandlers.integrationManagerRequestHandlers, new IntegrationManagerRequestHandler[1]);
			}
			
			// IntegrationManagerResponseHandler
			List<String> tipiIntegrationManagerResponseHandlersConfig = null;
			try {
				tipiIntegrationManagerResponseHandlersConfig=ConfigurazionePdDManager.getInstance(state).getIntegrationManagerResponseHandlers();
			}catch(Throwable e) {
				if(msgDiag!=null) {
					msgDiag.logErroreGenerico(e, "Raccolta IntegrationManagerResponseHandlers");
				}
				else if(logCore!=null) {
					logCore.error("Raccolta IntegrationManagerResponseHandlers", e);
				}
			}
			GestoreHandlers.tipiIntegrationManagerResponseHandlers = _mergeTipi(GestoreHandlers.properties.getIntegrationManagerResponseHandler(),
					tipiIntegrationManagerResponseHandlersConfig);
			if(GestoreHandlers.tipiIntegrationManagerResponseHandlers!=null){
				GestoreHandlers.integrationManagerResponseHandlers = new IntegrationManagerResponseHandler[GestoreHandlers.tipiIntegrationManagerResponseHandlers.length];
				for(int i=0; i<GestoreHandlers.tipiIntegrationManagerResponseHandlers.length; i++){
					try{
						String tipo=GestoreHandlers.tipiIntegrationManagerResponseHandlers[i];
						GestoreHandlers.integrationManagerResponseHandlers[i] = (IntegrationManagerResponseHandler) pluginLoader.newIntegrationManagerResponseHandler(tipo);
						if(printInfo)
							logConsoleInit.info("IntegrationManagerResponseHandler di tipo ["+tipo+"] correttamente inizializzato.");
					}catch(Throwable e){
						if(msgDiag!=null) {
							msgDiag.logErroreGenerico(e, "Inizializzazione IntegrationManagerResponseHandler ["+GestoreHandlers.tipiIntegrationManagerResponseHandlers[i]+"]");
						}
						else if(logCore!=null) {
							logCore.error("Inizializzazione IntegrationManagerResponseHandler ["+GestoreHandlers.tipiIntegrationManagerResponseHandlers[i]+"]", e);
						}
					}
				}
				GestoreHandlers.integrationManagerResponseHandlers = reorder(GestoreHandlers.integrationManagerResponseHandlers, new IntegrationManagerResponseHandler[1]);
			}
			
		}
		
		GestoreHandlers.initialize=true;
	}
	
	private static String [] _mergeTipi(String [] fromProperties, List<String> fromConfig) {
		List<String> merge = new ArrayList<String>();
		if(fromProperties!=null && fromProperties.length>0) {
			for (int i = 0; i < fromProperties.length; i++) {
				merge.add(fromProperties[i]);
			}
		}
		if(fromConfig!=null && !fromConfig.isEmpty()) {
			for (int i = 0; i < fromConfig.size(); i++) {
				merge.add(fromConfig.get(i));
			}
		}
		if(!merge.isEmpty()) {
			return merge.toArray(new String[merge.size()]);
		}
		return null;
	}
	
	private final static boolean printOrderInfo = false;
	private static <T> T[] reorder(T [] handlers, T[] bufferReturn){
		if(handlers!=null && handlers.length>0){
			
			List<String> handlerPositionHeadId = new ArrayList<String>();
			Map<String,T> handlerPositionHeadMap = new HashMap<String,T>();
			
			List<String> handlerPositionTailId = new ArrayList<String>();
			Map<String,T> handlerPositionTailMap = new HashMap<String,T>();
			
			List<T> handlerPositionMiddle = new ArrayList<T>();
			
			if(printOrderInfo){
				System.out.println("================== "+handlers.getClass().getName()+" =====================");
				System.out.println("PRE ORDER:");
			}
			for(int i=0; i<handlers.length; i++){
				
				if(handlers[i]==null) {
					continue;
				}
				
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
			
			if(orderedList!=null && !orderedList.isEmpty()) {
				if(printOrderInfo){
					System.out.println("POST ORDER:");
					for (T t : orderedList) {
						System.out.println("\t["+t.getClass().getName()+"]");
					}
				}
				
				return (T[]) orderedList.toArray(bufferReturn);
			}
			else {
				return null;
			}
		}
		else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T> List<Object[]> merge(T [] handlers1, String [] tipiHandlers1, 
			T [] handlers2, String [] tipiHandlers2, T[] bufferReturn){
		List<Object[]> lists = new ArrayList<>();
		if(handlers1==null || handlers1.length<=0) {
			lists.add(handlers2);
			lists.add(tipiHandlers2);
			return lists;
		}
		if(handlers2==null || handlers2.length<=0) {
			lists.add(handlers1);
			lists.add(tipiHandlers1);
			return lists;
		}
		
		List<T> listUnsorted = new ArrayList<>();
		for (int i = 0; i < handlers1.length; i++) {
			listUnsorted.add(handlers1[i]);
		}
		for (int i = 0; i < handlers2.length; i++) {
			listUnsorted.add(handlers2[i]);
		}
		T [] sorted = reorder((T[])listUnsorted.toArray(), bufferReturn);
		if(sorted!=null) {
			lists.add(sorted);
			
			List<String> listSortedType = new ArrayList<>();
			for (int i = 0; i < sorted.length; i++) {
				T sortHandler = sorted[i];
				boolean found = false;
				for (int j = 0; j < handlers1.length; j++) {
					if(sortHandler.getClass().getName().equals(handlers1[j].getClass().getName())) {
						listSortedType.add(tipiHandlers1[j]);
						found = true;
						break;
					}
				}
				if(!found) {
					for (int j = 0; j < handlers2.length; j++) {
						if(sortHandler.getClass().getName().equals(handlers2[j].getClass().getName())) {
							listSortedType.add(tipiHandlers2[j]);
							found = true;
							break;
						}
					}
				}
				if(!found) {
					throw new RuntimeException("Errore inatteso durante la gestione dell'handler: "+sortHandler.getClass().getName());
				}
			}
			lists.add(listSortedType.toArray());
		}
		
		return lists;
		
	}
	
	
	private static String[] updateNotifierCallback(String [] tipi) {
		String notifierInputStreamCallback = null;
		try{
			if(properties.isNotifierInputStreamEnabled()) {
				notifierInputStreamCallback = properties.getNotifierInputStreamCallback();
			}
		}catch(Throwable e){}
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
			GestoreHandlers.initialize(msgDiag,log,null);
		}
		
		if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
			List<Object[]> list =  merge(GestoreHandlers.initHandlersBuiltIn, GestoreHandlers.tipiInitHandlersBuiltIn,
					GestoreHandlers.initHandlers, GestoreHandlers.tipiInitHandlers, new InitHandler [1]);
			InitHandler [] handlers = (InitHandler []) list.get(0);
			String [] tipiHandlers = (String []) list.get(1);
			_initHandler(context, msgDiag, log, handlers, tipiHandlers, "InitHandler");
		}
		else {
			_initHandler(context, msgDiag, log, GestoreHandlers.initHandlersBuiltIn, GestoreHandlers.tipiInitHandlersBuiltIn, "InitHandler");
			_initHandler(context, msgDiag, log, GestoreHandlers.initHandlers, GestoreHandlers.tipiInitHandlers, "InitHandler");
		}
		
	}
	private static void _initHandler(InitContext context,MsgDiagnostico msgDiag,Logger log, InitHandler [] handlers, String [] tipiHandlers, String name) throws HandlerException{
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(handlers[i], msgDiag, log);
					handlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(handlers[i], msgDiag, log);
				}catch(Throwable e){
					
					if(log!=null){
						log.error(name+" ["+tipiHandlers[i]+"] (class:"+handlers[i].getClass().getName()+")",e);
					}
					
					// Sollevo l'eccezione
					HandlerException ex = new HandlerException(e.getMessage(),e);
					ex.setIdentitaHandler(name+" ["+tipiHandlers[i]+"]");
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
			
			if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
				List<Object[]> list =  merge(GestoreHandlers.exitHandlersBuiltIn, GestoreHandlers.tipiExitHandlersBuiltIn,
						GestoreHandlers.exitHandlers, GestoreHandlers.tipiExitHandlers, new ExitHandler [1]);
				ExitHandler [] handlers = (ExitHandler []) list.get(0);
				String [] tipiHandlers = (String []) list.get(1);
				_exitHandler(context, log, handlers, tipiHandlers, "ExitHandler");
			}
			else {
				_exitHandler(context, log, GestoreHandlers.exitHandlersBuiltIn, GestoreHandlers.tipiExitHandlersBuiltIn, "ExitHandler");
				_exitHandler(context, log, GestoreHandlers.exitHandlers, GestoreHandlers.tipiExitHandlers, "ExitHandler");
			}
		}
	}
	private static void _exitHandler(ExitContext context,Logger log, ExitHandler [] handlers, String [] tipiHandlers, String name) {
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					handlers[i].invoke(context);
				}catch(Throwable e){
					// Gli handler di tipo exit non dovrebbero sollevare una eccezione.
					// Eventualmento loggo l'errore
					if(log!=null){
						log.error(name+" ["+tipiHandlers[i]+"] (class:"+handlers[i].getClass().getName()+")",e);
					}
				}
			}
		}
	}
	
	
	public static void preInRequest(PreInAcceptRequestContext context,Logger logCore,Logger log) {
		_engine_preInRequest(null, context, null, logCore, log);
	}
	public static void emitDiagnostic(MsgDiagnostico msgDiag, PreInAcceptRequestContext context, PdDContext pddContext,Logger logCore,Logger log) {
		_engine_preInRequest(msgDiag, context, pddContext, logCore, log);
	}
	public static void _engine_preInRequest(MsgDiagnostico msgDiag, PreInAcceptRequestContext context, PdDContext pddContext,Logger logCore,Logger log) {
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(logCore,log);
		}
		
		// unisco handler generali (props+config) con eventuali handler definiti sulla porta
		List<Object[]> _mergeWithImplementation = _mergeWithImplementation(msgDiag, context, pddContext,GestoreHandlers.preInRequestHandlers, GestoreHandlers.tipiPreInRequestHandlers, log);
		PreInRequestHandler [] preInRequestHandlersUsers = (PreInRequestHandler []) _mergeWithImplementation.get(0);
		String [] tipiPreInRequestHandlersUsers = (String []) _mergeWithImplementation.get(1);
		
		// gestisco handler built-in e user
		if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
			List<Object[]> list =  merge(GestoreHandlers.preInRequestHandlersBuiltIn, GestoreHandlers.tipiPreInRequestHandlersBuiltIn,
					preInRequestHandlersUsers, tipiPreInRequestHandlersUsers, new PreInRequestHandler [1]);
			PreInRequestHandler [] handlers = (PreInRequestHandler []) list.get(0);
			String [] tipiHandlers = (String []) list.get(1);
			_preInRequestHandler(msgDiag, context, pddContext, log, handlers, tipiHandlers, "PreInRequestHandler");
		}
		else {
			_preInRequestHandler(msgDiag, context, pddContext, log, GestoreHandlers.preInRequestHandlersBuiltIn, GestoreHandlers.tipiPreInRequestHandlersBuiltIn, "PreInRequestHandler");
			_preInRequestHandler(msgDiag, context, pddContext, log, preInRequestHandlersUsers, tipiPreInRequestHandlersUsers, "PreInRequestHandler");
		}
		
	}
	private static List<Object[]> _mergeWithImplementation(MsgDiagnostico msgDiag, PreInAcceptRequestContext context, PdDContext pddContext,
			PreInRequestHandler [] preInRequestHandlers,String [] tipiPreInRequestHandlers,
			Logger log) {
		List<Object[]> mergeList = null;
		try{
			List<String> tipiPorta=null;
			if(context!=null && context.getRequestInfo()!=null && context.getRequestInfo().getProtocolContext()!=null &&
					context.getRequestInfo().getProtocolContext().getInterfaceName()!=null) {
				ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance();
				if(TipoPdD.DELEGATA.equals(context.getTipoPorta())) {
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(context.getRequestInfo().getProtocolContext().getInterfaceName());
					PortaDelegata pd=configurazionePdDManager.getPortaDelegata_SafeMethod(idPD);
					tipiPorta=configurazionePdDManager.getPreInRequestHandlers(pd);
					//System.out.println("PreInRequestContext find PD '"+pd.getNome()+"'");
				}
				else if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta())) {
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(context.getRequestInfo().getProtocolContext().getInterfaceName());
					PortaApplicativa pa=configurazionePdDManager.getPortaApplicativa_SafeMethod(idPA);
					tipiPorta=configurazionePdDManager.getPreInRequestHandlers(pa);
					//System.out.println("PreInRequestContext find PA '"+pa.getNome()+"'");
				}
			}
			if(tipiPorta!=null && !tipiPorta.isEmpty()) {
				List<PreInRequestHandler> listHandler = new ArrayList<PreInRequestHandler>();
				List<String> listTipi = new ArrayList<String>();
				if(preInRequestHandlers!=null && preInRequestHandlers.length>0) {
					for (int i = 0; i < preInRequestHandlers.length; i++) {
						listHandler.add(preInRequestHandlers[i]);
						listTipi.add(tipiPreInRequestHandlers[i]);
					}
				}
				PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
				for (String tipoPorta : tipiPorta) {
					if(!listTipi.contains(tipoPorta)) {
						try {
							PreInRequestHandler handler = pluginLoader.newPreInRequestHandler(tipoPorta);
							listHandler.add(handler);
							listTipi.add(tipoPorta);
						}catch(Throwable t) {
							log.error("NewInstance '"+tipoPorta+"' failed: "+t.getMessage(),t);
							// Sollevo l'eccezione
							HandlerException ex = new HandlerException(t.getMessage(),t);
							ex.setIdentitaHandler("PreInRequestHandler"+" ["+tipoPorta+"]");
							throw ex;
						}
					}
				}
				if(!listHandler.isEmpty()) {
					mergeList = new ArrayList<Object[]>();
					mergeList.add(listHandler.toArray(new PreInRequestHandler[listHandler.size()]));
					mergeList.add(listTipi.toArray(new String[listTipi.size()]));
				}
			}
		}catch(Throwable t) {
			log.error(t.getMessage(),t);
		}
		if(mergeList==null) {
			mergeList = new ArrayList<Object[]>();
			mergeList.add(preInRequestHandlers);
			mergeList.add(tipiPreInRequestHandlers);
		}
		return mergeList;
	}
	private static void _preInRequestHandler(MsgDiagnostico msgDiag, PreInAcceptRequestContext context, PdDContext pddContext,Logger log,
			PreInRequestHandler [] handlers, String [] tipiHandlers, String name) {
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					if(msgDiag!=null) {
						handlers[i].emitDiagnostic(msgDiag, context, pddContext);
					}
					else {
						emitDiagnosticInvokeHandlerStart(handlers[i], null, log);
						handlers[i].invoke(context);
						emitDiagnosticInvokeHandlerEnd(handlers[i], null, log);
					}
				}catch(Throwable e){
					// Sollevo l'eccezione
					/*HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler(name+" ["+tipiHandlers[i]+"]");
					throw ex;
					*/
					// L'handler invocato con il contesto 'PreInAcceptRequestContext' non dovrebbe sollevare una eccezione
					// Eventualmento loggo l'errore
					if(log!=null){
						log.error(name+" ["+tipiHandlers[i]+"] 'PreInAcceptRequestContext' (class:"+handlers[i].getClass().getName()+")",e);
					}
				}
			}
		}
	}
	
	public static void preInRequest(PreInRequestContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log,null);
		}
		
		// unisco handler generali (props+config) con eventuali handler definiti sulla porta
		List<Object[]> _mergeWithImplementation = _mergeWithImplementation(context,GestoreHandlers.preInRequestHandlers, GestoreHandlers.tipiPreInRequestHandlers, log);
		PreInRequestHandler [] preInRequestHandlersUsers = (PreInRequestHandler []) _mergeWithImplementation.get(0);
		String [] tipiPreInRequestHandlersUsers = (String []) _mergeWithImplementation.get(1);
		
		// gestisco handler built-in e user
		if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
			List<Object[]> list =  merge(GestoreHandlers.preInRequestHandlersBuiltIn, GestoreHandlers.tipiPreInRequestHandlersBuiltIn,
					preInRequestHandlersUsers, tipiPreInRequestHandlersUsers, new PreInRequestHandler [1]);
			PreInRequestHandler [] handlers = (PreInRequestHandler []) list.get(0);
			String [] tipiHandlers = (String []) list.get(1);
			_preInRequestHandler(context, msgDiag, log, handlers, tipiHandlers, "PreInRequestHandler");
		}
		else {
			_preInRequestHandler(context, msgDiag, log, GestoreHandlers.preInRequestHandlersBuiltIn, GestoreHandlers.tipiPreInRequestHandlersBuiltIn, "PreInRequestHandler");
			_preInRequestHandler(context, msgDiag, log, preInRequestHandlersUsers, tipiPreInRequestHandlersUsers, "PreInRequestHandler");
		}
		
	}
	private static List<Object[]> _mergeWithImplementation(PreInRequestContext context,PreInRequestHandler [] preInRequestHandlers,String [] tipiPreInRequestHandlers,
			Logger log) {
		List<Object[]> mergeList = null;
		try{
			List<String> tipiPorta=null;
			if(context!=null && context.getRequestInfo()!=null && context.getRequestInfo().getProtocolContext()!=null &&
					context.getRequestInfo().getProtocolContext().getInterfaceName()!=null) {
				ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance();
				if(TipoPdD.DELEGATA.equals(context.getTipoPorta())) {
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(context.getRequestInfo().getProtocolContext().getInterfaceName());
					PortaDelegata pd=configurazionePdDManager.getPortaDelegata_SafeMethod(idPD);
					tipiPorta=configurazionePdDManager.getPreInRequestHandlers(pd);
					//System.out.println("PreInRequestContext find PD '"+pd.getNome()+"'");
				}
				else if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta())) {
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(context.getRequestInfo().getProtocolContext().getInterfaceName());
					PortaApplicativa pa=configurazionePdDManager.getPortaApplicativa_SafeMethod(idPA);
					tipiPorta=configurazionePdDManager.getPreInRequestHandlers(pa);
					//System.out.println("PreInRequestContext find PA '"+pa.getNome()+"'");
				}
			}
			if(tipiPorta!=null && !tipiPorta.isEmpty()) {
				List<PreInRequestHandler> listHandler = new ArrayList<PreInRequestHandler>();
				List<String> listTipi = new ArrayList<String>();
				if(preInRequestHandlers!=null && preInRequestHandlers.length>0) {
					for (int i = 0; i < preInRequestHandlers.length; i++) {
						listHandler.add(preInRequestHandlers[i]);
						listTipi.add(tipiPreInRequestHandlers[i]);
					}
				}
				PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
				for (String tipoPorta : tipiPorta) {
					if(!listTipi.contains(tipoPorta)) {
						try {
							PreInRequestHandler handler = pluginLoader.newPreInRequestHandler(tipoPorta);
							listHandler.add(handler);
							listTipi.add(tipoPorta);
						}catch(Throwable t) {
							log.error("NewInstance '"+tipoPorta+"' failed: "+t.getMessage(),t);
							// Sollevo l'eccezione
							HandlerException ex = new HandlerException(t.getMessage(),t);
							ex.setIdentitaHandler("PreInRequestHandler"+" ["+tipoPorta+"]");
							throw ex;
						}
					}
				}
				if(!listHandler.isEmpty()) {
					mergeList = new ArrayList<Object[]>();
					mergeList.add(listHandler.toArray(new PreInRequestHandler[listHandler.size()]));
					mergeList.add(listTipi.toArray(new String[listTipi.size()]));
				}
			}
		}catch(Throwable t) {
			log.error(t.getMessage(),t);
		}
		if(mergeList==null) {
			mergeList = new ArrayList<Object[]>();
			mergeList.add(preInRequestHandlers);
			mergeList.add(tipiPreInRequestHandlers);
		}
		return mergeList;
	}
	private static void _preInRequestHandler(PreInRequestContext context,MsgDiagnostico msgDiag,Logger log, PreInRequestHandler [] handlers, String [] tipiHandlers, String name) throws HandlerException{
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(handlers[i], msgDiag, log);
					handlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(handlers[i], msgDiag, log);
				}catch(Throwable e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler(name+" ["+tipiHandlers[i]+"]");
					throw ex;
				}
			}
		}
	}
	
	public static void inRequest(InRequestContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log,context.getStato());
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)));
		}
		
		// unisco handler generali (props+config) con eventuali handler definiti sulla porta
		List<Object[]> _mergeWithImplementation = _mergeWithImplementation(context,GestoreHandlers.inRequestHandlers, GestoreHandlers.tipiInRequestHandlers, log);
		InRequestHandler [] inRequestHandlersUsers = (InRequestHandler []) _mergeWithImplementation.get(0);
		String [] tipiInRequestHandlersUsers = (String []) _mergeWithImplementation.get(1);
		
		// gestisco handler built-in e user
		if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
			List<Object[]> list =  merge(GestoreHandlers.inRequestHandlersBuiltIn, GestoreHandlers.tipiInRequestHandlersBuiltIn,
					inRequestHandlersUsers, tipiInRequestHandlersUsers, new InRequestHandler [1]);
			InRequestHandler [] handlers = (InRequestHandler []) list.get(0);
			String [] tipiHandlers = (String []) list.get(1);
			_inRequestHandler(context, msgDiag, log, handlers, tipiHandlers, "InRequestHandler");
		}
		else {
			_inRequestHandler(context, msgDiag, log, GestoreHandlers.inRequestHandlersBuiltIn, GestoreHandlers.tipiInRequestHandlersBuiltIn, "InRequestHandler");
			_inRequestHandler(context, msgDiag, log, inRequestHandlersUsers, tipiInRequestHandlersUsers, "InRequestHandler");
		}
		
	}
	private static List<Object[]> _mergeWithImplementation(InRequestContext context,InRequestHandler [] inRequestHandlers,String [] tipiInRequestHandlers,
			Logger log) {
		List<Object[]> mergeList = null;
		try{
			List<String> tipiPorta=null;
			if(context!=null && context.getConnettore()!=null && context.getConnettore().getUrlProtocolContext()!=null &&
					context.getConnettore().getUrlProtocolContext().getInterfaceName()!=null) {
				ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
				if(TipoPdD.DELEGATA.equals(context.getTipoPorta())) {
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(context.getConnettore().getUrlProtocolContext().getInterfaceName());
					PortaDelegata pd=configurazionePdDManager.getPortaDelegata_SafeMethod(idPD);
					tipiPorta=configurazionePdDManager.getInRequestHandlers(pd);
					//System.out.println("InRequestContext find PD '"+pd.getNome()+"'");
				}
				else if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta())) {
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(context.getConnettore().getUrlProtocolContext().getInterfaceName());
					PortaApplicativa pa=configurazionePdDManager.getPortaApplicativa_SafeMethod(idPA);
					tipiPorta=configurazionePdDManager.getInRequestHandlers(pa);
					//System.out.println("InRequestContext find PA '"+pa.getNome()+"'");
				}
			}
			if(tipiPorta!=null && !tipiPorta.isEmpty()) {
				List<InRequestHandler> listHandler = new ArrayList<InRequestHandler>();
				List<String> listTipi = new ArrayList<String>();
				if(inRequestHandlers!=null && inRequestHandlers.length>0) {
					for (int i = 0; i < inRequestHandlers.length; i++) {
						listHandler.add(inRequestHandlers[i]);
						listTipi.add(tipiInRequestHandlers[i]);
					}
				}
				PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
				for (String tipoPorta : tipiPorta) {
					if(!listTipi.contains(tipoPorta)) {
						try {
							InRequestHandler handler = pluginLoader.newInRequestHandler(tipoPorta);
							listHandler.add(handler);
							listTipi.add(tipoPorta);
						}catch(Throwable t) {
							log.error("NewInstance '"+tipoPorta+"' failed: "+t.getMessage(),t);
							// Sollevo l'eccezione
							HandlerException ex = new HandlerException(t.getMessage(),t);
							ex.setIdentitaHandler("InRequestHandler"+" ["+tipoPorta+"]");
							throw ex;
						}
					}
				}
				if(!listHandler.isEmpty()) {
					mergeList = new ArrayList<Object[]>();
					mergeList.add(listHandler.toArray(new InRequestHandler[listHandler.size()]));
					mergeList.add(listTipi.toArray(new String[listTipi.size()]));
				}
			}
		}catch(Throwable t) {
			log.error(t.getMessage(),t);
		}
		if(mergeList==null) {
			mergeList = new ArrayList<Object[]>();
			mergeList.add(inRequestHandlers);
			mergeList.add(tipiInRequestHandlers);
		}
		return mergeList;
	}
	private static void _inRequestHandler(InRequestContext context,MsgDiagnostico msgDiag,Logger log, InRequestHandler [] handlers, String [] tipiHandlers, String name) throws HandlerException{
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(handlers[i], msgDiag, log);
					handlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(handlers[i], msgDiag, log);
				}catch(Throwable e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler(name+" ["+tipiHandlers[i]+"]");
					throw ex;
				}
			}
		}
	}
	
	public static void inRequestProtocol(InRequestProtocolContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log,context.getStato());
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)));
		}
		
		// unisco handler generali (props+config) con eventuali handler definiti sulla porta
		List<Object[]> _mergeWithImplementation = _mergeWithImplementation(context,GestoreHandlers.inRequestProtocolHandlers, GestoreHandlers.tipiInRequestProtocolHandlers, log);
		InRequestProtocolHandler [] inRequestProtocolHandlersUsers = (InRequestProtocolHandler []) _mergeWithImplementation.get(0);
		String [] tipiInRequestProtocolHandlersUsers = (String []) _mergeWithImplementation.get(1);
		
		// gestisco handler built-in e user
		if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
			List<Object[]> list =  merge(GestoreHandlers.inRequestProtocolHandlersBuiltIn, GestoreHandlers.tipiInRequestProtocolHandlersBuiltIn,
					inRequestProtocolHandlersUsers, tipiInRequestProtocolHandlersUsers, new InRequestProtocolHandler [1]);
			InRequestProtocolHandler [] handlers = (InRequestProtocolHandler []) list.get(0);
			String [] tipiHandlers = (String []) list.get(1);
			_inRequestProtocolHandler(context, msgDiag, log, handlers, tipiHandlers, "InRequestProtocolHandler");
		}
		else {
			_inRequestProtocolHandler(context, msgDiag, log, GestoreHandlers.inRequestProtocolHandlersBuiltIn, GestoreHandlers.tipiInRequestProtocolHandlersBuiltIn, "InRequestProtocolHandler");
			_inRequestProtocolHandler(context, msgDiag, log, inRequestProtocolHandlersUsers, tipiInRequestProtocolHandlersUsers, "InRequestProtocolHandler");
		}
		
	}
	private static List<Object[]> _mergeWithImplementation(InRequestProtocolContext context,InRequestProtocolHandler [] intRequestProtocolHandlers,String [] tipiInRequestProtocolHandlers,
			Logger log) {
		List<Object[]> mergeList = null;
		try{
			List<String> tipiPorta=null;
			if(context!=null && context.getIntegrazione()!=null) {
				if(TipoPdD.DELEGATA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPD()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaDelegata pd=configurazionePdDManager.getPortaDelegata_SafeMethod(context.getIntegrazione().getIdPD());
					tipiPorta=configurazionePdDManager.getInRequestProtocolHandlers(pd);
					//System.out.println("InRequestProtocolContext find PD '"+pd.getNome()+"'");
				}
				else if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPA()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaApplicativa pa=configurazionePdDManager.getPortaApplicativa_SafeMethod(context.getIntegrazione().getIdPA());
					tipiPorta=configurazionePdDManager.getInRequestProtocolHandlers(pa);
					//System.out.println("InRequestProtocolContext find PA '"+pa.getNome()+"'");
				}
			}
			if(tipiPorta!=null && !tipiPorta.isEmpty()) {
				List<InRequestProtocolHandler> listHandler = new ArrayList<InRequestProtocolHandler>();
				List<String> listTipi = new ArrayList<String>();
				if(intRequestProtocolHandlers!=null && intRequestProtocolHandlers.length>0) {
					for (int i = 0; i < intRequestProtocolHandlers.length; i++) {
						listHandler.add(intRequestProtocolHandlers[i]);
						listTipi.add(tipiInRequestProtocolHandlers[i]);
					}
				}
				PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
				for (String tipoPorta : tipiPorta) {
					if(!listTipi.contains(tipoPorta)) {
						try {
							InRequestProtocolHandler handler = pluginLoader.newInRequestProtocolHandler(tipoPorta);
							listHandler.add(handler);
							listTipi.add(tipoPorta);
						}catch(Throwable t) {
							log.error("NewInstance '"+tipoPorta+"' failed: "+t.getMessage(),t);
							// Sollevo l'eccezione
							HandlerException ex = new HandlerException(t.getMessage(),t);
							ex.setIdentitaHandler("InRequestProtocolHandler"+" ["+tipoPorta+"]");
							throw ex;
						}
					}
				}
				if(!listHandler.isEmpty()) {
					mergeList = new ArrayList<Object[]>();
					mergeList.add(listHandler.toArray(new InRequestProtocolHandler[listHandler.size()]));
					mergeList.add(listTipi.toArray(new String[listTipi.size()]));
				}
			}
		}catch(Throwable t) {
			log.error(t.getMessage(),t);
		}
		if(mergeList==null) {
			mergeList = new ArrayList<Object[]>();
			mergeList.add(intRequestProtocolHandlers);
			mergeList.add(tipiInRequestProtocolHandlers);
		}
		return mergeList;
	}
	private static void _inRequestProtocolHandler(InRequestProtocolContext context,MsgDiagnostico msgDiag,Logger log, InRequestProtocolHandler [] handlers, String [] tipiHandlers, String name) throws HandlerException{
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(handlers[i], msgDiag, log);
					handlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(handlers[i], msgDiag, log);
				}catch(Throwable e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler(name+" ["+tipiHandlers[i]+"]");
					throw ex;
				}
			}
		}
	}
	
	public static void outRequest(OutRequestContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log,context.getStato());
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)));
		}
		
		// unisco handler generali (props+config) con eventuali handler definiti sulla porta
		List<Object[]> _mergeWithImplementation = _mergeWithImplementation(context,GestoreHandlers.outRequestHandlers, GestoreHandlers.tipiOutRequestHandlers, log);
		OutRequestHandler [] outRequestHandlersUsers = (OutRequestHandler []) _mergeWithImplementation.get(0);
		String [] tipiOutRequestHandlersUsers = (String []) _mergeWithImplementation.get(1);
		
		// gestisco handler built-in e user
		if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
			List<Object[]> list =  merge(GestoreHandlers.outRequestHandlersBuiltIn, GestoreHandlers.tipiOutRequestHandlersBuiltIn,
					outRequestHandlersUsers, tipiOutRequestHandlersUsers, new OutRequestHandler [1]);
			OutRequestHandler [] handlers = (OutRequestHandler []) list.get(0);
			String [] tipiHandlers = (String []) list.get(1);
			_outRequestHandler(context, msgDiag, log, handlers, tipiHandlers, "OutRequestHandler");
		}
		else {
			_outRequestHandler(context, msgDiag, log, GestoreHandlers.outRequestHandlersBuiltIn, GestoreHandlers.tipiOutRequestHandlersBuiltIn, "OutRequestHandler");
			_outRequestHandler(context, msgDiag, log, outRequestHandlersUsers, tipiOutRequestHandlersUsers, "OutRequestHandler");
		}
		
	}
	private static List<Object[]> _mergeWithImplementation(OutRequestContext context,OutRequestHandler [] outRequestHandlers,String [] tipiOutRequestHandlers,
			Logger log) {
		List<Object[]> mergeList = null;
		try{
			List<String> tipiPorta=null;
			if(context!=null && context.getIntegrazione()!=null) {
				if(TipoPdD.DELEGATA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPD()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaDelegata pd=configurazionePdDManager.getPortaDelegata_SafeMethod(context.getIntegrazione().getIdPD());
					tipiPorta=configurazionePdDManager.getOutRequestHandlers(pd);
					//System.out.println("OutRequestContext find PD '"+pd.getNome()+"'");
				}
				else if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPA()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaApplicativa pa=configurazionePdDManager.getPortaApplicativa_SafeMethod(context.getIntegrazione().getIdPA());
					tipiPorta=configurazionePdDManager.getOutRequestHandlers(pa);
					//System.out.println("OutRequestContext find PA '"+pa.getNome()+"'");
				}
			}
			if(tipiPorta!=null && !tipiPorta.isEmpty()) {
				List<OutRequestHandler> listHandler = new ArrayList<OutRequestHandler>();
				List<String> listTipi = new ArrayList<String>();
				if(outRequestHandlers!=null && outRequestHandlers.length>0) {
					for (int i = 0; i < outRequestHandlers.length; i++) {
						listHandler.add(outRequestHandlers[i]);
						listTipi.add(tipiOutRequestHandlers[i]);
					}
				}
				PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
				for (String tipoPorta : tipiPorta) {
					if(!listTipi.contains(tipoPorta)) {
						try {
							OutRequestHandler handler = pluginLoader.newOutRequestHandler(tipoPorta);
							listHandler.add(handler);
							listTipi.add(tipoPorta);
						}catch(Throwable t) {
							log.error("NewInstance '"+tipoPorta+"' failed: "+t.getMessage(),t);
							// Sollevo l'eccezione
							HandlerException ex = new HandlerException(t.getMessage(),t);
							ex.setIdentitaHandler("OutRequestHandler"+" ["+tipoPorta+"]");
							throw ex;
						}
					}
				}
				if(!listHandler.isEmpty()) {
					mergeList = new ArrayList<Object[]>();
					mergeList.add(listHandler.toArray(new OutRequestHandler[listHandler.size()]));
					mergeList.add(listTipi.toArray(new String[listTipi.size()]));
				}
			}
		}catch(Throwable t) {
			log.error(t.getMessage(),t);
		}
		if(mergeList==null) {
			mergeList = new ArrayList<Object[]>();
			mergeList.add(outRequestHandlers);
			mergeList.add(tipiOutRequestHandlers);
		}
		return mergeList;
	}
	private static void _outRequestHandler(OutRequestContext context,MsgDiagnostico msgDiag,Logger log, OutRequestHandler [] handlers, String [] tipiHandlers, String name) throws HandlerException{
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(handlers[i], msgDiag, log);
					handlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(handlers[i], msgDiag, log);
				}catch(Throwable e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler(name+" ["+tipiHandlers[i]+"]");
					throw ex;
				}
			}
		}
	}
	
	public static void postOutRequest(PostOutRequestContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log,context.getStato());
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)));
		}
		
		// unisco handler generali (props+config) con eventuali handler definiti sulla porta
		List<Object[]> _mergeWithImplementation = _mergeWithImplementation(context,GestoreHandlers.postOutRequestHandlers, GestoreHandlers.tipiPostOutRequestHandlers, log);
		PostOutRequestHandler [] postOutRequestHandlersUsers = (PostOutRequestHandler []) _mergeWithImplementation.get(0);
		String [] tipiPostOutRequestHandlersUsers = (String []) _mergeWithImplementation.get(1);
		
		// gestisco handler built-in e user
		if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
			List<Object[]> list =  merge(GestoreHandlers.postOutRequestHandlersBuiltIn, GestoreHandlers.tipiPostOutRequestHandlersBuiltIn,
					postOutRequestHandlersUsers, tipiPostOutRequestHandlersUsers, new PostOutRequestHandler [1]);
			PostOutRequestHandler [] handlers = (PostOutRequestHandler []) list.get(0);
			String [] tipiHandlers = (String []) list.get(1);
			_postOutRequestHandler(context, msgDiag, log, handlers, tipiHandlers, "PostOutRequestHandler");
		}
		else {
			_postOutRequestHandler(context, msgDiag, log, GestoreHandlers.postOutRequestHandlersBuiltIn, GestoreHandlers.tipiPostOutRequestHandlersBuiltIn, "PostOutRequestHandler");
			_postOutRequestHandler(context, msgDiag, log, postOutRequestHandlersUsers, tipiPostOutRequestHandlersUsers, "PostOutRequestHandler");
		}
		
	}
	private static List<Object[]> _mergeWithImplementation(PostOutRequestContext context,PostOutRequestHandler [] postOutRequestHandlers,String [] tipiPostOutRequestHandlers,
			Logger log) {
		List<Object[]> mergeList = null;
		try{
			List<String> tipiPorta=null;
			if(context!=null && context.getIntegrazione()!=null) {
				if(TipoPdD.DELEGATA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPD()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaDelegata pd=configurazionePdDManager.getPortaDelegata_SafeMethod(context.getIntegrazione().getIdPD());
					tipiPorta=configurazionePdDManager.getPostOutRequestHandlers(pd);
					//System.out.println("PostOutRequestContext find PD '"+pd.getNome()+"'");
				}
				else if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPA()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaApplicativa pa=configurazionePdDManager.getPortaApplicativa_SafeMethod(context.getIntegrazione().getIdPA());
					tipiPorta=configurazionePdDManager.getPostOutRequestHandlers(pa);
					//System.out.println("PostOutRequestContext find PA '"+pa.getNome()+"'");
				}
			}
			if(tipiPorta!=null && !tipiPorta.isEmpty()) {
				List<PostOutRequestHandler> listHandler = new ArrayList<PostOutRequestHandler>();
				List<String> listTipi = new ArrayList<String>();
				if(postOutRequestHandlers!=null && postOutRequestHandlers.length>0) {
					for (int i = 0; i < postOutRequestHandlers.length; i++) {
						listHandler.add(postOutRequestHandlers[i]);
						listTipi.add(tipiPostOutRequestHandlers[i]);
					}
				}
				PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
				for (String tipoPorta : tipiPorta) {
					if(!listTipi.contains(tipoPorta)) {
						try {
							PostOutRequestHandler handler = pluginLoader.newPostOutRequestHandler(tipoPorta);
							listHandler.add(handler);
							listTipi.add(tipoPorta);
						}catch(Throwable t) {
							log.error("NewInstance '"+tipoPorta+"' failed: "+t.getMessage(),t);
							// Sollevo l'eccezione
							HandlerException ex = new HandlerException(t.getMessage(),t);
							ex.setIdentitaHandler("PostOutRequestHandler"+" ["+tipoPorta+"]");
							throw ex;
						}
					}
				}
				if(!listHandler.isEmpty()) {
					mergeList = new ArrayList<Object[]>();
					mergeList.add(listHandler.toArray(new PostOutRequestHandler[listHandler.size()]));
					mergeList.add(listTipi.toArray(new String[listTipi.size()]));
				}
			}
		}catch(Throwable t) {
			log.error(t.getMessage(),t);
		}
		if(mergeList==null) {
			mergeList = new ArrayList<Object[]>();
			mergeList.add(postOutRequestHandlers);
			mergeList.add(tipiPostOutRequestHandlers);
		}
		return mergeList;
	}
	private static void _postOutRequestHandler(PostOutRequestContext context,MsgDiagnostico msgDiag,Logger log, PostOutRequestHandler [] handlers, String [] tipiHandlers, String name) throws HandlerException{
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(handlers[i], msgDiag, log);
					handlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(handlers[i], msgDiag, log);
				}catch(Throwable e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler(name+" ["+tipiHandlers[i]+"]");
					throw ex;
				}
			}
		}
	}
	
	public static void preInResponse(PreInResponseContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log,context.getStato());
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)));
		}
		
		// unisco handler generali (props+config) con eventuali handler definiti sulla porta
		List<Object[]> _mergeWithImplementation = _mergeWithImplementation(context,GestoreHandlers.preInResponseHandlers, GestoreHandlers.tipiPreInResponseHandlers, log);
		PreInResponseHandler [] preInResponseHandlersUsers = (PreInResponseHandler []) _mergeWithImplementation.get(0);
		String [] tipiPreInResponseHandlersUsers = (String []) _mergeWithImplementation.get(1);
		
		// gestisco handler built-in e user
		if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
			List<Object[]> list =  merge(GestoreHandlers.preInResponseHandlersBuiltIn, GestoreHandlers.tipiPreInResponseHandlersBuiltIn,
					preInResponseHandlersUsers, tipiPreInResponseHandlersUsers, new PreInResponseHandler [1]);
			PreInResponseHandler [] handlers = (PreInResponseHandler []) list.get(0);
			String [] tipiHandlers = (String []) list.get(1);
			_preInResponseHandler(context, msgDiag, log, handlers, tipiHandlers, "PreInResponseHandler");
		}
		else {
			_preInResponseHandler(context, msgDiag, log, GestoreHandlers.preInResponseHandlersBuiltIn, GestoreHandlers.tipiPreInResponseHandlersBuiltIn, "PreInResponseHandler");
			_preInResponseHandler(context, msgDiag, log, preInResponseHandlersUsers, tipiPreInResponseHandlersUsers, "PreInResponseHandler");
		}
		
	}
	private static List<Object[]> _mergeWithImplementation(PreInResponseContext context,PreInResponseHandler [] preInResponseHandlers,String [] tipiPreInResponseHandlers,
			Logger log) {
		List<Object[]> mergeList = null;
		try{
			List<String> tipiPorta=null;
			if(context!=null && context.getIntegrazione()!=null) {
				if(TipoPdD.DELEGATA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPD()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaDelegata pd=configurazionePdDManager.getPortaDelegata_SafeMethod(context.getIntegrazione().getIdPD());
					tipiPorta=configurazionePdDManager.getPreInResponseHandlers(pd);
					//System.out.println("PreInResponseContext find PD '"+pd.getNome()+"'");
				}
				else if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPA()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaApplicativa pa=configurazionePdDManager.getPortaApplicativa_SafeMethod(context.getIntegrazione().getIdPA());
					tipiPorta=configurazionePdDManager.getPreInResponseHandlers(pa);
					//System.out.println("PreInResponseContext find PA '"+pa.getNome()+"'");
				}
			}
			if(tipiPorta!=null && !tipiPorta.isEmpty()) {
				List<PreInResponseHandler> listHandler = new ArrayList<PreInResponseHandler>();
				List<String> listTipi = new ArrayList<String>();
				if(preInResponseHandlers!=null && preInResponseHandlers.length>0) {
					for (int i = 0; i < preInResponseHandlers.length; i++) {
						listHandler.add(preInResponseHandlers[i]);
						listTipi.add(tipiPreInResponseHandlers[i]);
					}
				}
				PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
				for (String tipoPorta : tipiPorta) {
					if(!listTipi.contains(tipoPorta)) {
						try {
							PreInResponseHandler handler = pluginLoader.newPreInResponseHandler(tipoPorta);
							listHandler.add(handler);
							listTipi.add(tipoPorta);
						}catch(Throwable t) {
							log.error("NewInstance '"+tipoPorta+"' failed: "+t.getMessage(),t);
							// Sollevo l'eccezione
							HandlerException ex = new HandlerException(t.getMessage(),t);
							ex.setIdentitaHandler("PreInResponseHandler"+" ["+tipoPorta+"]");
							throw ex;
						}
					}
				}
				if(!listHandler.isEmpty()) {
					mergeList = new ArrayList<Object[]>();
					mergeList.add(listHandler.toArray(new PreInResponseHandler[listHandler.size()]));
					mergeList.add(listTipi.toArray(new String[listTipi.size()]));
				}
			}
		}catch(Throwable t) {
			log.error(t.getMessage(),t);
		}
		if(mergeList==null) {
			mergeList = new ArrayList<Object[]>();
			mergeList.add(preInResponseHandlers);
			mergeList.add(tipiPreInResponseHandlers);
		}
		return mergeList;
	}
	private static void _preInResponseHandler(PreInResponseContext context,MsgDiagnostico msgDiag,Logger log, PreInResponseHandler [] handlers, String [] tipiHandlers, String name) throws HandlerException{
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(handlers[i], msgDiag, log);
					handlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(handlers[i], msgDiag, log);
				}catch(Throwable e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler(name+" ["+tipiHandlers[i]+"]");
					throw ex;
				}
			}
		}
	}
	
	public static void inResponse(InResponseContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log,context.getStato());
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)));
		}
		
		// unisco handler generali (props+config) con eventuali handler definiti sulla porta
		List<Object[]> _mergeWithImplementation = _mergeWithImplementation(context,GestoreHandlers.inResponseHandlers, GestoreHandlers.tipiInResponseHandlers, log);
		InResponseHandler [] inResponseHandlersUsers = (InResponseHandler []) _mergeWithImplementation.get(0);
		String [] tipiInResponseHandlersUsers = (String []) _mergeWithImplementation.get(1);
		
		// gestisco handler built-in e user
		if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
			List<Object[]> list =  merge(GestoreHandlers.inResponseHandlersBuiltIn, GestoreHandlers.tipiInResponseHandlersBuiltIn,
					inResponseHandlersUsers, tipiInResponseHandlersUsers, new InResponseHandler [1]);
			InResponseHandler [] handlers = (InResponseHandler []) list.get(0);
			String [] tipiHandlers = (String []) list.get(1);
			_inResponseHandler(context, msgDiag, log, handlers, tipiHandlers, "InResponseHandler");
		}
		else {
			_inResponseHandler(context, msgDiag, log, GestoreHandlers.inResponseHandlersBuiltIn, GestoreHandlers.tipiInResponseHandlersBuiltIn, "InResponseHandler");
			_inResponseHandler(context, msgDiag, log, inResponseHandlersUsers, tipiInResponseHandlersUsers, "InResponseHandler");
		}
		
	}
	private static List<Object[]> _mergeWithImplementation(InResponseContext context,InResponseHandler [] inResponseHandlers,String [] tipiInResponseHandlers,
			Logger log) {
		List<Object[]> mergeList = null;
		try{
			List<String> tipiPorta=null;
			if(context!=null && context.getIntegrazione()!=null) {
				if(TipoPdD.DELEGATA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPD()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaDelegata pd=configurazionePdDManager.getPortaDelegata_SafeMethod(context.getIntegrazione().getIdPD());
					tipiPorta=configurazionePdDManager.getInResponseHandlers(pd);
					//System.out.println("InResponseContext find PD '"+pd.getNome()+"'");
				}
				else if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPA()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaApplicativa pa=configurazionePdDManager.getPortaApplicativa_SafeMethod(context.getIntegrazione().getIdPA());
					tipiPorta=configurazionePdDManager.getInResponseHandlers(pa);
					//System.out.println("InResponseContext find PA '"+pa.getNome()+"'");
				}
			}
			if(tipiPorta!=null && !tipiPorta.isEmpty()) {
				List<InResponseHandler> listHandler = new ArrayList<InResponseHandler>();
				List<String> listTipi = new ArrayList<String>();
				if(inResponseHandlers!=null && inResponseHandlers.length>0) {
					for (int i = 0; i < inResponseHandlers.length; i++) {
						listHandler.add(inResponseHandlers[i]);
						listTipi.add(tipiInResponseHandlers[i]);
					}
				}
				PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
				for (String tipoPorta : tipiPorta) {
					if(!listTipi.contains(tipoPorta)) {
						try {
							InResponseHandler handler = pluginLoader.newInResponseHandler(tipoPorta);
							listHandler.add(handler);
							listTipi.add(tipoPorta);
						}catch(Throwable t) {
							log.error("NewInstance '"+tipoPorta+"' failed: "+t.getMessage(),t);
							// Sollevo l'eccezione
							HandlerException ex = new HandlerException(t.getMessage(),t);
							ex.setIdentitaHandler("InResponseHandler"+" ["+tipoPorta+"]");
							throw ex;
						}
					}
				}
				if(!listHandler.isEmpty()) {
					mergeList = new ArrayList<Object[]>();
					mergeList.add(listHandler.toArray(new InResponseHandler[listHandler.size()]));
					mergeList.add(listTipi.toArray(new String[listTipi.size()]));
				}
			}
		}catch(Throwable t) {
			log.error(t.getMessage(),t);
		}
		if(mergeList==null) {
			mergeList = new ArrayList<Object[]>();
			mergeList.add(inResponseHandlers);
			mergeList.add(tipiInResponseHandlers);
		}
		return mergeList;
	}
	private static void _inResponseHandler(InResponseContext context,MsgDiagnostico msgDiag,Logger log, InResponseHandler [] handlers, String [] tipiHandlers, String name) throws HandlerException{
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(handlers[i], msgDiag, log);
					handlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(handlers[i], msgDiag, log);
				}catch(Throwable e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler(name+" ["+tipiHandlers[i]+"]");
					throw ex;
				}
			}
		}
	}
	
	public static void outResponse(OutResponseContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log,context.getStato());
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)));
		}
		
		// unisco handler generali (props+config) con eventuali handler definiti sulla porta
		List<Object[]> _mergeWithImplementation = _mergeWithImplementation(context,GestoreHandlers.outResponseHandlers, GestoreHandlers.tipiOutResponseHandlers, log);
		OutResponseHandler [] outResponseHandlersUsers = (OutResponseHandler []) _mergeWithImplementation.get(0);
		String [] tipiOutResponseHandlersUsers = (String []) _mergeWithImplementation.get(1);
		
		// gestisco handler built-in e user
		if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
			List<Object[]> list =  merge(GestoreHandlers.outResponseHandlersBuiltIn, GestoreHandlers.tipiOutResponseHandlersBuiltIn,
					outResponseHandlersUsers, tipiOutResponseHandlersUsers, new OutResponseHandler [1]);
			OutResponseHandler [] handlers = (OutResponseHandler []) list.get(0);
			String [] tipiHandlers = (String []) list.get(1);
			_outResponseHandler(context, msgDiag, log, handlers, tipiHandlers, "OutResponseHandler");
		}
		else {
			_outResponseHandler(context, msgDiag, log, GestoreHandlers.outResponseHandlersBuiltIn, GestoreHandlers.tipiOutResponseHandlersBuiltIn, "OutResponseHandler");
			_outResponseHandler(context, msgDiag, log, outResponseHandlersUsers, tipiOutResponseHandlersUsers, "OutResponseHandler");
		}
		
	}
	private static List<Object[]> _mergeWithImplementation(OutResponseContext context,OutResponseHandler [] outResponseHandlers,String [] tipiOutResponseHandlers,
			Logger log) {
		List<Object[]> mergeList = null;
		try{
			List<String> tipiPorta=null;
			if(context!=null && context.getIntegrazione()!=null) {
				if(TipoPdD.DELEGATA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPD()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaDelegata pd=configurazionePdDManager.getPortaDelegata_SafeMethod(context.getIntegrazione().getIdPD());
					tipiPorta=configurazionePdDManager.getOutResponseHandlers(pd);
					//System.out.println("OutResponseContext find PD '"+pd.getNome()+"'");
				}
				else if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPA()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaApplicativa pa=configurazionePdDManager.getPortaApplicativa_SafeMethod(context.getIntegrazione().getIdPA());
					tipiPorta=configurazionePdDManager.getOutResponseHandlers(pa);
					//System.out.println("OutResponseContext find PA '"+pa.getNome()+"'");
				}
			}
			if(tipiPorta!=null && !tipiPorta.isEmpty()) {
				List<OutResponseHandler> listHandler = new ArrayList<OutResponseHandler>();
				List<String> listTipi = new ArrayList<String>();
				if(outResponseHandlers!=null && outResponseHandlers.length>0) {
					for (int i = 0; i < outResponseHandlers.length; i++) {
						listHandler.add(outResponseHandlers[i]);
						listTipi.add(tipiOutResponseHandlers[i]);
					}
				}
				PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
				for (String tipoPorta : tipiPorta) {
					if(!listTipi.contains(tipoPorta)) {
						try {
							OutResponseHandler handler = pluginLoader.newOutResponseHandler(tipoPorta);
							listHandler.add(handler);
							listTipi.add(tipoPorta);
						}catch(Throwable t) {
							log.error("NewInstance '"+tipoPorta+"' failed: "+t.getMessage(),t);
							// Sollevo l'eccezione
							HandlerException ex = new HandlerException(t.getMessage(),t);
							ex.setIdentitaHandler("OutResponseHandler"+" ["+tipoPorta+"]");
							throw ex;
						}
					}
				}
				if(!listHandler.isEmpty()) {
					mergeList = new ArrayList<Object[]>();
					mergeList.add(listHandler.toArray(new OutResponseHandler[listHandler.size()]));
					mergeList.add(listTipi.toArray(new String[listTipi.size()]));
				}
			}
		}catch(Throwable t) {
			log.error(t.getMessage(),t);
		}
		if(mergeList==null) {
			mergeList = new ArrayList<Object[]>();
			mergeList.add(outResponseHandlers);
			mergeList.add(tipiOutResponseHandlers);
		}
		return mergeList;
	}
	private static void _outResponseHandler(OutResponseContext context,MsgDiagnostico msgDiag,Logger log, OutResponseHandler [] handlers, String [] tipiHandlers, String name) throws HandlerException{
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(handlers[i], msgDiag, log);
					handlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(handlers[i], msgDiag, log);
				}catch(Throwable e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler(name+" ["+tipiHandlers[i]+"]");
					throw ex;
				}
			}
		}
	}
	
	public static void postOutResponse(PostOutResponseContext context,MsgDiagnostico msgDiag,Logger log) {
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log,context.getStato());
		}
	
		if(context.getDataElaborazioneMessaggio()==null){
			context.setDataElaborazioneMessaggio(DateManager.getDate());
		}
		
		// genero date casuali 
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
			context.setDataElaborazioneMessaggio(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)));
		}
		
		// unisco handler generali (props+config) con eventuali handler definiti sulla porta
		List<Object[]> _mergeWithImplementation = _mergeWithImplementation(context,GestoreHandlers.postOutResponseHandlers, GestoreHandlers.tipiPostOutResponseHandlers, log);
		PostOutResponseHandler [] postOutResponseHandlersUsers = (PostOutResponseHandler []) _mergeWithImplementation.get(0);
		String [] tipiPostOutResponseHandlersUsers = (String []) _mergeWithImplementation.get(1);
		
		// gestisco handler built-in e user
		if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
			List<Object[]> list =  merge(GestoreHandlers.postOutResponseHandlersBuiltIn, GestoreHandlers.tipiPostOutResponseHandlersBuiltIn,
					postOutResponseHandlersUsers, tipiPostOutResponseHandlersUsers, new PostOutResponseHandler [1]);
			PostOutResponseHandler [] handlers = (PostOutResponseHandler []) list.get(0);
			String [] tipiHandlers = (String []) list.get(1);
			_postOutResponseHandler(context, msgDiag, log, handlers, tipiHandlers, "PostOutResponseHandler");
		}
		else {
			_postOutResponseHandler(context, msgDiag, log, GestoreHandlers.postOutResponseHandlersBuiltIn, GestoreHandlers.tipiPostOutResponseHandlersBuiltIn, "PostOutResponseHandler");
			_postOutResponseHandler(context, msgDiag, log, postOutResponseHandlersUsers, tipiPostOutResponseHandlersUsers, "PostOutResponseHandler");
		}

		// Rilascio risorse per la generazione di date casuali
		GestoreHandlers.rilasciaRisorseDemoMode((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
	}
	private static List<Object[]> _mergeWithImplementation(PostOutResponseContext context,PostOutResponseHandler [] postOutResponseHandlers,String [] tipiPostOutResponseHandlers,
			Logger log) {
		List<Object[]> mergeList = null;
		try{
			List<String> tipiPorta=null;
			if(context!=null && context.getIntegrazione()!=null) {
				if(TipoPdD.DELEGATA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPD()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaDelegata pd=configurazionePdDManager.getPortaDelegata_SafeMethod(context.getIntegrazione().getIdPD());
					tipiPorta=configurazionePdDManager.getPostOutResponseHandlers(pd);
					//System.out.println("PostOutResponseContext find PD '"+pd.getNome()+"'");
				}
				else if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta()) && context.getIntegrazione().getIdPA()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
					PortaApplicativa pa=configurazionePdDManager.getPortaApplicativa_SafeMethod(context.getIntegrazione().getIdPA());
					tipiPorta=configurazionePdDManager.getPostOutResponseHandlers(pa);
					//System.out.println("PostOutResponseContext find PA '"+pa.getNome()+"'");
				}
			}
			if(tipiPorta!=null && !tipiPorta.isEmpty()) {
				List<PostOutResponseHandler> listHandler = new ArrayList<PostOutResponseHandler>();
				List<String> listTipi = new ArrayList<String>();
				if(postOutResponseHandlers!=null && postOutResponseHandlers.length>0) {
					for (int i = 0; i < postOutResponseHandlers.length; i++) {
						listHandler.add(postOutResponseHandlers[i]);
						listTipi.add(tipiPostOutResponseHandlers[i]);
					}
				}
				PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
				for (String tipoPorta : tipiPorta) {
					if(!listTipi.contains(tipoPorta)) {
						try {
							PostOutResponseHandler handler = pluginLoader.newPostOutResponseHandler(tipoPorta);
							listHandler.add(handler);
							listTipi.add(tipoPorta);
						}catch(Throwable t) {
							// Non sollevo l'eccezione poiche' dove viene chiamato questo handler e' finita la gestione, quindi l'eccezione non causa altri avvenimenti
							log.error("NewInstance '"+tipoPorta+"' failed: "+t.getMessage(),t);
						}
					}
				}
				if(!listHandler.isEmpty()) {
					mergeList = new ArrayList<Object[]>();
					mergeList.add(listHandler.toArray(new PostOutResponseHandler[listHandler.size()]));
					mergeList.add(listTipi.toArray(new String[listTipi.size()]));
				}
			}
		}catch(Throwable t) {
			log.error(t.getMessage(),t);
		}
		if(mergeList==null) {
			mergeList = new ArrayList<Object[]>();
			mergeList.add(postOutResponseHandlers);
			mergeList.add(tipiPostOutResponseHandlers);
		}
		return mergeList;
	}
	private static void _postOutResponseHandler(PostOutResponseContext context,MsgDiagnostico msgDiag,Logger log, PostOutResponseHandler [] handlers, String [] tipiHandlers, String name) {
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(handlers[i], msgDiag, log);
					handlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(handlers[i], msgDiag, log);
				}catch(Throwable e){
					// Non sollevo l'eccezione poiche' dove viene chiamato questo handler e' finita la gestione, quindi l'eccezione non causa altri avvenimenti
					// Registro solamento l'evento
					if(handlers[i] instanceof org.openspcoop2.pdd.core.handlers.transazioni.PostOutResponseHandler) {
						// altrimenti succede questo errore, non esistendo più la transazione:
						// Errore durante l'emissione del msg diagnostico nel contesto della transazione: Non abilitata la gestione delle transazioni stateful 
						// org.openspcoop2.pdd.core.transazioni.TransactionStatefulNotSupportedException: Non abilitata la gestione delle transazioni stateful
						if(log!=null) {
							log.error(name+" ["+tipiHandlers[i]+"]"+e.getMessage(),e);
						}
					}
					else {
						msgDiag.logErroreGenerico(e,name+" ["+tipiHandlers[i]+"]");
					}
				}
			}
		}
	}
	
	
	
	public static void integrationManagerRequest(IntegrationManagerRequestContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log,null);
		}
		
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
			// genero date casuali
			if(context.getDataRichiestaOperazione()!=null){
				context.setDataRichiestaOperazione(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)));
			}
		}
		
		// gestisco handler built-in e user
		if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
			List<Object[]> list =  merge(GestoreHandlers.integrationManagerRequestHandlersBuiltIn, GestoreHandlers.tipiIntegrationManagerRequestHandlersBuiltIn,
					GestoreHandlers.integrationManagerRequestHandlers, GestoreHandlers.tipiIntegrationManagerRequestHandlers, new IntegrationManagerRequestHandler [1]);
			IntegrationManagerRequestHandler [] handlers = (IntegrationManagerRequestHandler []) list.get(0);
			String [] tipiHandlers = (String []) list.get(1);
			_integrationManagerRequestHandler(context, msgDiag, log, handlers, tipiHandlers, "IntegrationManagerRequestHandler");
		}
		else {
			_integrationManagerRequestHandler(context, msgDiag, log, GestoreHandlers.integrationManagerRequestHandlersBuiltIn, GestoreHandlers.tipiIntegrationManagerRequestHandlersBuiltIn, "IntegrationManagerRequestHandler");
			_integrationManagerRequestHandler(context, msgDiag, log, GestoreHandlers.integrationManagerRequestHandlers, GestoreHandlers.tipiIntegrationManagerRequestHandlers, "IntegrationManagerRequestHandler");
		}
	}
	private static void _integrationManagerRequestHandler(IntegrationManagerRequestContext context,MsgDiagnostico msgDiag,Logger log, IntegrationManagerRequestHandler [] handlers, String [] tipiHandlers, String name) throws HandlerException{
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(handlers[i], msgDiag, log);
					handlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(handlers[i], msgDiag, log);
				}catch(Throwable e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler(name+" ["+tipiHandlers[i]+"]");
					throw ex;
				}
			}
		}
	}
	
	
	public static void integrationManagerResponse(IntegrationManagerResponseContext context,MsgDiagnostico msgDiag,Logger log) throws HandlerException{
		
		if(GestoreHandlers.initialize==false){
			GestoreHandlers.initialize(msgDiag,log,null);
		}
		
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
			// genero date casuali
			if(context.getDataRichiestaOperazione()!=null){
				context.setDataRichiestaOperazione(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)));
			}
			if(context.getDataCompletamentoOperazione()!=null){
				context.setDataCompletamentoOperazione(GestoreHandlers.generatoreCasualeDati.getProssimaData((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)));
			}
		}
		
		// gestisco handler built-in e user
		if(properties.isMergeHandlerBuiltInAndHandlerUser()) {
			List<Object[]> list =  merge(GestoreHandlers.integrationManagerResponseHandlersBuiltIn, GestoreHandlers.tipiIntegrationManagerResponseHandlersBuiltIn,
					GestoreHandlers.integrationManagerResponseHandlers, GestoreHandlers.tipiIntegrationManagerResponseHandlers, new IntegrationManagerResponseHandler [1]);
			IntegrationManagerResponseHandler [] handlers = (IntegrationManagerResponseHandler []) list.get(0);
			String [] tipiHandlers = (String []) list.get(1);
			_integrationManagerResponseHandler(context, msgDiag, log, handlers, tipiHandlers, "IntegrationManagerResponseHandler");
		}
		else {
			_integrationManagerResponseHandler(context, msgDiag, log, GestoreHandlers.integrationManagerResponseHandlersBuiltIn, GestoreHandlers.tipiIntegrationManagerResponseHandlersBuiltIn, "IntegrationManagerResponseHandler");
			_integrationManagerResponseHandler(context, msgDiag, log, GestoreHandlers.integrationManagerResponseHandlers, GestoreHandlers.tipiIntegrationManagerResponseHandlers, "IntegrationManagerResponseHandler");
		}
		
		// Rilascio risorse per la generazione di date casuali
		GestoreHandlers.rilasciaRisorseDemoMode((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
	}
	private static void _integrationManagerResponseHandler(IntegrationManagerResponseContext context,MsgDiagnostico msgDiag,Logger log, IntegrationManagerResponseHandler [] handlers, String [] tipiHandlers, String name) throws HandlerException{
		if(handlers!=null){
			for(int i=0; i<handlers.length; i++){
				try{
					emitDiagnosticInvokeHandlerStart(handlers[i], msgDiag, log);
					handlers[i].invoke(context);
					emitDiagnosticInvokeHandlerEnd(handlers[i], msgDiag, log);
				}catch(Throwable e){
					// Sollevo l'eccezione
					HandlerException ex = null;
					if(e instanceof HandlerException){
						ex = (HandlerException) e;
					}else{
						ex = new HandlerException(e.getMessage(),e);
					}
					ex.setIdentitaHandler(name+" ["+tipiHandlers[i]+"]");
					throw ex;
				}
			}
		}
	}

	
	private static void rilasciaRisorseDemoMode(String idCluster){
		if(GestoreHandlers.properties.generazioneDateCasualiLogAbilitato() && idCluster!=null){
			GeneratoreCasualeDate.getGeneratoreCasualeDate().releaseResource(idCluster);
		}
	}
}
