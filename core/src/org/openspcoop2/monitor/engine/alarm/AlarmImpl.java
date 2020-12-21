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

package org.openspcoop2.monitor.engine.alarm;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.alarm.AlarmStatus;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.monitor.sdk.constants.AlarmStateValues;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.monitor.sdk.plugins.IAlarmProcessing;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * AlarmImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AlarmImpl implements IAlarm {

	private Logger logger = LoggerWrapperFactory.getLogger(AlarmImpl.class);
	private DAOFactory daoFactory = null;
	private String threadName;
	private String username;
	
	public AlarmImpl(Allarme configAllarme,Logger log, DAOFactory daoFactory) {
		this.id = configAllarme.getNome();
		this.configAllarme = configAllarme;
		this.logger = log;
		this.daoFactory = daoFactory;
		this.threadName = "SDK";
	}
	
	protected void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public Logger getLogger(){
		return this.logger;
	}
	
	@Override
	public DAOFactory getDAOFactory(){
		return this.daoFactory;
	}
	
	@Override
	public void changeStatus(AlarmStatus nuovoStatoAllarme) throws AlarmException {
		
		// Cambio stato sul database degli allarmi
		AlarmManager.changeStatus(nuovoStatoAllarme, this, this.logger, this.daoFactory, this.username);
		
		
		// Switch stato nell'attuale implementazione
		AlarmStatus oldStatus = null;
		if(this.status!=null){
			oldStatus = (AlarmStatus) this.status.clone();
		}
		this.setStatus(nuovoStatoAllarme);
				
		// Cambio di stato effettivo
		boolean statusChanged = false;
		if(oldStatus==null || oldStatus.getStatus()==null){
			statusChanged = true;
		}
		else{
			statusChanged = !oldStatus.getStatus().equals(nuovoStatoAllarme.getStatus());
		}
		
		// Gestione invocazione script e mail
		if(statusChanged){
			
			// Notifico cambio di stato all'interfaccia		
			try {
				IDynamicLoader cPlugin = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME,this.configAllarme.getTipo(),this.pluginClassName, this.logger);
				IAlarmProcessing alarmProc = (IAlarmProcessing) cPlugin.newInstance();
				alarmProc.changeStatusNotify(this, oldStatus, this.status);
			} catch( Exception e) {
				this.logger.error("AlarmImpl.changeStatus() ha rilevato un errore: "+e.getMessage(),e);
				throw new AlarmException(e);
			}
			
			try{
			
				AlarmEngineConfig alarmEngineConfig = AlarmManager.getAlarmEngineConfig();
				if(alarmEngineConfig==null){
					throw new Exception("Configurazione Allarme non fornita, utilizzare il metodo AlarmManager.setAlarmEngineConfig(...)");
				}
				
				boolean mailAckMode = false;
				if(this.getConfigAllarme().getMail()==null || this.getConfigAllarme().getMail().getAckMode()==null){
					mailAckMode = alarmEngineConfig.isMailAckMode();
				}
				else{
					mailAckMode = this.getConfigAllarme().getMail().getAckMode()==1;
				}
				
				boolean mailSendChangeStatusOk = alarmEngineConfig.isMailSendChangeStatusOk();
				
				boolean scriptAckMode = false;
				if(this.getConfigAllarme().getScript()==null || this.getConfigAllarme().getScript().getAckMode()==null){
					scriptAckMode = alarmEngineConfig.isScriptAckMode();
				}
				else{
					scriptAckMode = this.getConfigAllarme().getScript().getAckMode()==1;
				}
				
				boolean scriptSendChangeStatusOk = alarmEngineConfig.isScriptSendChangeStatusOk();
				
				
				boolean sendMail = false;
				boolean invokeScript = false;
				if(AlarmStateValues.OK.equals(nuovoStatoAllarme.getStatus())){
					if(mailSendChangeStatusOk){
						// sia in ack mode che non in ack mode viene spedito da questo metodo.
						// tanto deve essere spedita solo la prima volta e non continuamente
						sendMail = (this.getConfigAllarme().getMail()!=null &&
								this.getConfigAllarme().getMail().getInviaAlert()!=null && 
								this.getConfigAllarme().getMail().getInviaAlert()==1); // note: Alert rappresenta l'invio della mail nel db e non veramente solo il livello error
					}
					if(scriptSendChangeStatusOk){
						// sia in ack mode che non in ack mode viene spedito da questo metodo.
						// tanto deve essere spedita solo la prima volta e non continuamente
						invokeScript = (this.getConfigAllarme().getScript()!=null &&
								this.getConfigAllarme().getScript().getInvocaAlert()!=null && 
								this.getConfigAllarme().getScript().getInvocaAlert()==1); // note: Alert rappresenta l'invio della mail nel db e non veramente solo il livello error
					}
				}
				else if(AlarmStateValues.WARNING.equals(nuovoStatoAllarme.getStatus())){
					if(mailAckMode==false){
						// NOTA: per come è impostata la pddMonitor il warning esiste solo se è attivo anche l'alert
						sendMail = (
									this.getConfigAllarme().getMail()!=null && 
									this.getConfigAllarme().getMail().getInviaAlert()!=null && 
									this.getConfigAllarme().getMail().getInviaAlert()==1
								) 
								&& // note: Alert rappresenta l'invio della mail nel db e non veramente solo il livello error
								(
										this.getConfigAllarme().getMail()!=null &&
										this.getConfigAllarme().getMail().getInviaWarning()!=null && 
										this.getConfigAllarme().getMail().getInviaWarning()==1
								);
					}
					if(scriptAckMode==false){
						// NOTA: per come è impostata la pddMonitor il warning esiste solo se è attivo anche l'alert
						invokeScript = (
									this.getConfigAllarme().getScript()!=null &&
									this.getConfigAllarme().getScript().getInvocaAlert()!=null && 
									this.getConfigAllarme().getScript().getInvocaAlert()==1
								) 
								&& // note: Alert rappresenta l'invio via script nel db e non veramente solo il livello error
								(
										this.getConfigAllarme().getScript()!=null && 
										this.getConfigAllarme().getScript().getInvocaWarning()!=null && 
										this.getConfigAllarme().getScript().getInvocaWarning()==1
								);
					}
				}
				else if(AlarmStateValues.ERROR.equals(nuovoStatoAllarme.getStatus())){
					if(mailAckMode==false){
						sendMail = this.getConfigAllarme().getMail()!=null &&
								this.getConfigAllarme().getMail().getInviaAlert()!=null && 
								this.getConfigAllarme().getMail().getInviaAlert()==1; // note: Alert rappresenta l'invio della mail nel db e non veramente solo il livello error
					}
					if(scriptAckMode==false){
						invokeScript = this.getConfigAllarme().getScript()!=null &&
								this.getConfigAllarme().getScript().getInvocaAlert()!=null && 
								this.getConfigAllarme().getScript().getInvocaAlert()==1; // note: Alert rappresenta l'invio via script nel db e non veramente solo il livello error
					}
				}
				
				if(sendMail){
					AlarmManager.sendMail(this.getConfigAllarme(), this.logger, this.threadName);
				}
				if(invokeScript){
					AlarmManager.invokeScript(this.getConfigAllarme(), this.logger, this.threadName);
				}
				
			}catch(Exception e){
				throw new AlarmException(e.getMessage(),e);
			}
			
		}
	}

	public void setStatus(AlarmStatus statoAllarme) throws AlarmException {
		this.status = statoAllarme;
	}

	@Override
	public AlarmStatus getStatus() {
		return this.status;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public Allarme getConfigAllarme() {
		return this.configAllarme;
	}
	
	@Override
	public Parameter<?> getParameter(String paramId) {
		if (this.parameters != null)
			return this.parameters.get(paramId);
		else 
			return null;
	}

	@Override
	public Map<String, Parameter<?>> getParameters() {
		return this.parameters;
	}
	
	protected void addParameter(Parameter<?> param) {
		if (this.parameters == null)
			this.parameters = new HashMap<String, Parameter<?>>();
		this.parameters.put(param.getId(), param);
	}
	
	protected String getPluginClassName() {
		return this.pluginClassName;
	}
	
	protected void setPluginClassName(String plugin) {
		this.pluginClassName = plugin;
	}

	private String id = null;
	private Allarme configAllarme;
	private HashMap<String, Parameter<?>> parameters = null;
	private AlarmStatus status = null;
	private String pluginClassName = null;
}
