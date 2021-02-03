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

package org.openspcoop2.monitor.engine.alarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.utils.AllarmiConverterUtils;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.monitor.sdk.constants.AlarmStateValues;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;
import org.openspcoop2.monitor.sdk.plugins.IAlarmProcessing;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.IDUtilities;
import org.slf4j.Logger;

/**
 * AlarmLibrary
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AlarmThread implements Runnable {

	private int periodMillis;
	private String tipo;
	private String classname;
	private IAlarm alarm;
	private Logger _log;
	private AlarmEngineConfig alarmEngineConfig;
	private String threadName;
	private boolean stop = false;
	private boolean terminated = false;
	
	private CircularFifoQueue<String> logHistory; 
	private final static int NUMERO_EVENTI = 10;

	public boolean isTerminated() {
		return this.terminated;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public AlarmThread(Logger log,String tipo, String classname, IAlarm alarm, AlarmEngineConfig alarmEngineConfig)
			throws AlarmException {
		if (tipo == null)
			throw new AlarmException(
					"parametro tipo NULL passato al thread");
		if (classname == null)
			throw new AlarmException(
					"parametro classname NULL passato al thread");
		if (alarm == null)
			throw new AlarmException(
					"parametro alarm NULL passato al thread");
		this.tipo = tipo;
		this.classname = classname;
		this.alarm = alarm;
		this._log = log;
		this.alarmEngineConfig = alarmEngineConfig;
		this.threadName = "T_"+IDUtilities.getUniqueSerialNumber()+"_"+DateManager.getTimeMillis();
		((AlarmImpl)this.alarm).setThreadName(this.threadName);
		
		this.logHistory = new CircularFifoQueue<String>(NUMERO_EVENTI); // mantiene gli ultimi dieci log
	}

	public void updateState(AlarmStateValues alarmStatus){
		this.alarm.getStatus().setStatus(alarmStatus);
		switch (alarmStatus) {
		case OK:
			this.alarm.getConfigAllarme().setStato(AllarmiConverterUtils.toIntegerValue(StatoAllarme.OK));
			break;
		case WARNING:
			this.alarm.getConfigAllarme().setStato(AllarmiConverterUtils.toIntegerValue(StatoAllarme.WARNING));
			break;
		case ERROR:
			this.alarm.getConfigAllarme().setStato(AllarmiConverterUtils.toIntegerValue(StatoAllarme.ERROR));
			break;
		}
	}
	
	public void updateAcknowledged(boolean acknoledgement){
		if(acknoledgement){
			this.alarm.getConfigAllarme().setAcknowledged(1);
		}else{
			this.alarm.getConfigAllarme().setAcknowledged(0);
		}
	}
	
	public void setPeriodByDays(int days) {
		this.periodMillis = days * 24 * 60 * 60 * 1000;
	}

	public void setPeriodByHours(int hours) {
		this.periodMillis = hours * 60 * 60 * 1000;
	}

	public void setPeriodByMinutes(int minutes) {
		this.periodMillis = minutes * 60 * 1000;
	}

	public void setPeriodBySeconds(int seconds) {
		this.periodMillis = seconds * 1000;
	}

	@Override
	public void run() {
		
		try{
		
			this.logDebug("Thread avviato ...");
			
			while (this.stop == false) {
				try {
					
					this.logInfo("Verifica stato in corso ...");
					
					TipoPlugin tipoPlugin = TipoPlugin.ALLARME;
					IDynamicLoader cAllarme = DynamicFactory.getInstance().newDynamicLoader(tipoPlugin, this.tipo, this.classname, this._log);
					IAlarmProcessing alarmProc = (IAlarmProcessing) cAllarme.newInstance();
					this.logDebug("Invocazione plugin '"+this.tipo+"' ...");
					alarmProc.check(this.alarm);
					this.logDebug("Invocazione plugin '"+this.tipo+"' terminata; analisi nuovo stato in corso ...");
					
					boolean mailAckMode = false;
					if(this.alarm.getConfigAllarme().getMail()==null || this.alarm.getConfigAllarme().getMail().getAckMode()==null){
						mailAckMode = this.alarmEngineConfig.isMailAckMode();
					}
					else{
						mailAckMode = this.alarm.getConfigAllarme().getMail().getAckMode()==1;
					}
					
					boolean scriptAckMode = false;
					if(this.alarm.getConfigAllarme().getScript()==null || this.alarm.getConfigAllarme().getScript().getAckMode()==null){
						scriptAckMode = this.alarmEngineConfig.isScriptAckMode();
					}
					else{
						scriptAckMode = this.alarm.getConfigAllarme().getScript().getAckMode()==1;
					}
								
					boolean sendMail = false;
					boolean invokeScript = false;
					boolean acknowledged = (this.alarm.getConfigAllarme().getAcknowledged()==null || this.alarm.getConfigAllarme().getAcknowledged()==1);
					if(this.alarm.getStatus()!=null){
						if(AlarmStateValues.WARNING.equals(this.alarm.getStatus().getStatus())){
							if(mailAckMode && !acknowledged){
								// NOTA: per come è impostata la pddMonitor il warning esiste solo se è attivo anche l'alert
								sendMail = 
										(
												this.alarm.getConfigAllarme().getMail()!=null &&
												this.alarm.getConfigAllarme().getMail().getInviaAlert()!=null && 
												this.alarm.getConfigAllarme().getMail().getInviaAlert()==1
										) 
										&& 
										(
												this.alarm.getConfigAllarme().getMail()!=null &&
												this.alarm.getConfigAllarme().getMail().getInviaWarning()!=null && 
												this.alarm.getConfigAllarme().getMail().getInviaWarning()==1
										);
							}
							if(scriptAckMode && !acknowledged){
								// NOTA: per come è impostata la pddMonitor il warning esiste solo se è attivo anche l'alert
								invokeScript = 
										(
												this.alarm.getConfigAllarme().getScript()!=null &&
												this.alarm.getConfigAllarme().getScript().getInvocaAlert()!=null && 
												this.alarm.getConfigAllarme().getScript().getInvocaAlert()==1) 
										&&
										(
												this.alarm.getConfigAllarme().getScript()!=null &&
												this.alarm.getConfigAllarme().getScript().getInvocaWarning()!=null && 
												this.alarm.getConfigAllarme().getScript().getInvocaWarning()==1);
							}
						}
						else if(AlarmStateValues.ERROR.equals(this.alarm.getStatus().getStatus())){
							if(mailAckMode && !acknowledged){
								sendMail = this.alarm.getConfigAllarme().getMail()!=null &&
										this.alarm.getConfigAllarme().getMail().getInviaAlert()!=null && 
										this.alarm.getConfigAllarme().getMail().getInviaAlert()==1;
							}
							if(scriptAckMode && !acknowledged){
								invokeScript = this.alarm.getConfigAllarme().getScript()!=null &&
										this.alarm.getConfigAllarme().getScript().getInvocaAlert()!=null && 
										this.alarm.getConfigAllarme().getScript().getInvocaAlert()==1;
							}
						}
					}
					
					if(sendMail){
						List<String> logEvents = new ArrayList<String>();
						AlarmManager.sendMail(this.alarm.getConfigAllarme(), this._log, logEvents);
						if(logEvents!=null && !logEvents.isEmpty()) {
							for (String logEvent : logEvents) {
								this.logDebug(logEvent);
							}
						}
					}
					if(invokeScript){
						List<String> logEvents = new ArrayList<String>();
						AlarmManager.invokeScript(this.alarm.getConfigAllarme(), this._log, logEvents);
						if(logEvents!=null && !logEvents.isEmpty()) {
							for (String logEvent : logEvents) {
								this.logDebug(logEvent);
							}
						}
					}
					
					this.logInfo("Verifica nuovo stato terminata correttamente");
				} catch (Exception e) {
					this.logError("Errore inatteso emerso durante la verifica dello stato: "
									+ e.getMessage(), e);
				}
				int sleep = 0;
				int timeSleep = 500;
				this.logDebug("Sleep "+this.periodMillis+"ms ...");
				while(sleep<this.periodMillis && this.stop==false){
					Utilities.sleep(timeSleep);
					sleep = sleep+timeSleep;
				}
				this.logDebug("Sleep "+this.periodMillis+"ms terminato");
			}
			
			this.logDebug("Thread terminato");
		}finally{
			this.terminated = true;
		}
	}
		
	
	private void logDebug(String messaggio) {
		_log(messaggio, true, false, false, null);
	}
	private void logInfo(String messaggio) {
		_log(messaggio, false, true, false, null);
	}
	private void logError(String messaggio, Exception e) {
		_log(messaggio, false, false, true, e);
	}
	private void _log(String messaggio, boolean debug, boolean info, boolean error, Exception e) {
		String prefix = buildPrefix(this.threadName,this.alarm.getNome(),this.alarm.getId());
		String logMsg = prefix+messaggio;
		if(debug) {
			this._log.debug(logMsg);
		}
		else if(info) {
			this._log.info(logMsg);
		}
		else if(error) {
			if(e!=null) {
				this._log.error(logMsg,e);
			}
			else {
				this._log.error(logMsg);
			}
		}
		String data = " <"+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+"> ";
		this.logHistory.add("\n\t"+data+ messaggio);
	}

	public static String buildPrefix(String threadName, String aliasAllarme, String idAllarme) {
		String prefix = "["+threadName+"] Allarme '"+aliasAllarme+"' (" + idAllarme+ ") ";
		return prefix;
	}

	public String getStatoAllarme() {
		StringBuilder sb = new StringBuilder();
		sb.append("================================================================\n");
		sb.append("Dati Identificativi\n");
		sb.append("\tNome: ").append(this.alarm.getNome()).append("\n");
		sb.append("\tIdAllarme: ").append(this.alarm.getId()).append("\n");
		sb.append("\tIdThread: ").append(this.threadName).append("\n");
		sb.append("Ultimi "+NUMERO_EVENTI+" eventi\n");
		if(this.logHistory.isFull()) {
			sb.append("\t...");
		}
		sb.append(this.logHistory.toString());
		sb.append("\n");
		sb.append("================================================================\n");
		return sb.toString();
	}
}
