/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.handlers.notifier.INotifierCallback;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierException;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierResult;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierType;
import org.openspcoop2.pdd.logger.LoggerUtility;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;

/**     
 * NotifierCallback
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotifierCallback implements INotifierCallback {
	
	private LoggerUtility logUtility = null;
	private synchronized void initializeLogUtility() throws Exception{
		if(this.logUtility==null){
			boolean forceGetLogTransazioni = true;
			this.logUtility = new LoggerUtility(OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioni(forceGetLogTransazioni), // use forceGetLogTransazioni poiche' il debug e' gestito da LoggerUtility
					OpenSPCoop2Properties.getInstance().isTransazioniDebug());
		}
	}
	private LoggerUtility getLoggerUtility() {
		try{
			if(this.logUtility==null){
				this.initializeLogUtility();
			}
			return this.logUtility;
		}catch(Exception e){
			System.err.println("[NotifierCallback] Errore durante l'inizializzazione del logger: "+e.getMessage());
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public NotifierCallback(){
//		try{
//			this.logUtility = new LoggerUtility(LoggerManager.getInstance().getLogTransazioni(),PddInterceptorConfig.isDebugEnabled_generazioneTransazioni());			
//		}catch(Exception e){
//			System.err.println("[NotifierCallback] Errore durante l'inizializzazione del logger: "+e.getMessage());
//		}
	}
	
	public void debug(String msg){
		this.getLoggerUtility().debug("[NotifierCallback] "+msg);
	}
	
	public void error(NotifierType notifierType,Object context,String methodName,String message,Exception e){
		Logger log = NotifierUtilities.getLogger(notifierType, context);
		String idTransazione = NotifierUtilities.getIdTransazione(notifierType, context);
		TipoPdD tipoPorta = NotifierUtilities.getTipoPorta(notifierType, context);
		StringBuilder bf = new StringBuilder();
		bf.append("[NotifierCallback] [").append(idTransazione).append("]-[")
			.append(tipoPorta.name()).append("] (")
			.append(notifierType.name()).append(") @")
			.append(methodName).append("@ : ")
			.append(message);
		log.error(message,e); // log openspcoop_core
		this.getLoggerUtility().error(message, e);
	}
	
	public void error(String msg,Throwable t){
		this.getLoggerUtility().error("[NotifierCallback] "+msg, t);
	}
	
	private void emitLog(NotifierType notifierType,Object context,String methodName){
		StringBuilder bf = new StringBuilder();
		String idTransazione = NotifierUtilities.getIdTransazione(notifierType, context);
		bf.append("[NotifierCallback] \n");
		bf.append("[NotifierCallback] [").append(idTransazione).append("] --------------------------- @"+methodName+"@ (start) ------------------------------\n");			
		OpenSPCoop2Message msg = NotifierUtilities.getOpenSPCoopMessage(notifierType, context);
		String notifierBufferState = "UNDEFINED";
		if(msg!=null && msg.getNotifierInputStream()!=null){
			if(msg.getNotifierInputStream().isBufferEnabled()){
				notifierBufferState = "ON";
			}
			else{
				notifierBufferState = "OFF";
			}
		}
		
		bf.append("[NotifierCallback] [").append(idTransazione).append("[").append(NotifierUtilities.getTipoPorta(notifierType, context)).append("]").
			append(" STATE:").append(notifierType.name()).append(" (StatoBuffer:").
			append(notifierBufferState).append(")\n");
		this.getLoggerUtility().debug(bf.toString());
	}
	private void emitLogEnd(NotifierType notifierType,Object context,String methodName){
		StringBuilder bf = new StringBuilder();
		String idTransazione = NotifierUtilities.getIdTransazione(notifierType, context);
		bf.append("[NotifierCallback] [").append(idTransazione).append("] --------------------------- @"+methodName+"@ (end) ------------------------------\n");		
		this.getLoggerUtility().debug(bf.toString());
	}
	

	
	
	
	// **** interface ***
	
	// ** Metodi utilizzati nelle fasi iniziali di creazione del NotifierInputStream **
	
	@Override
	public boolean enableNotifierInputStream(NotifierType notifierType,
			Object context) throws NotifierException {
		this.emitLog(notifierType, context, "enableNotifierInputStream");
		try{
			return NotifierCallbackEnableUtils.enableNotifierInputStream(this, notifierType, context);
		}catch(Exception e){
			error(notifierType, context, "enableNotifierInputStream", e.getMessage(), e);
			throw new NotifierException(e.getMessage(),e);
		}
		finally{
			this.emitLogEnd(notifierType, context, "enableNotifierInputStream");
		}

	}

	@Override
	public boolean throwStreamingHandlerException(NotifierType notifierType,
			Object context) throws NotifierException {
		this.emitLog(notifierType, context, "throwStreamingHandlerException");
		try{
			return OpenSPCoop2Properties.getInstance().isDumpNonRealtimeThrowStreamingHandlerException();
		}catch(Exception e){
			error(notifierType, context, "throwStreamingHandlerException", e.getMessage(), e);
			throw new NotifierException(e.getMessage(),e);
		}
		finally{
			this.emitLogEnd(notifierType, context, "throwStreamingHandlerException");
		}
	}

	
	
	// ** Metodi utilizzati in tutte le fasi **
	
	@Override
	public NotifierResult notify(NotifierType notifierType, Object context)
			throws NotifierException {
		this.emitLog(notifierType, context, "notify");
		try{
			return NotifierCallbackEnableUtils.notify(this, notifierType, context);
		}catch(Exception e){
			error(notifierType, context, "notify", e.getMessage(), e);
			throw new NotifierException(e.getMessage(),e);
		}
		finally{
			this.emitLogEnd(notifierType, context, "notify");
		}
	}

	
	
	
	public String getClassNamePropertiesName(){
		return "org.openspcoop2.notifierCallback.pddOE";
	}
	
	public String [] getOpenSPCoopPropertiesNames(){
		return new String [] {"org.openspcoop2.pdd.services.notifierInputStreamCallback"};
	}
	public String [] getOpenSPCoopPropertiesValues(){
		return new String [] {"pddOE"};
	}
}
