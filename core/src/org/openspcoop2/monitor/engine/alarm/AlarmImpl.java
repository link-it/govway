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

package org.openspcoop2.monitor.engine.alarm;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.constants.TipoAllarme;
import org.openspcoop2.core.allarmi.dao.IServiceManager;
import org.openspcoop2.core.allarmi.utils.ProjectInfo;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.sdk.alarm.AlarmStatus;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;
import org.openspcoop2.monitor.sdk.exceptions.AlarmNotifyException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
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

	private Logger _logger = LoggerWrapperFactory.getLogger(AlarmImpl.class);
	private DAOFactory daoFactory = null;
	private String threadName;
	private String username;
	private AlarmLogger alarmLogger;
	private AlarmThreadStatus alarmThreadStatus;
	
	public AlarmImpl(Allarme configAllarme,Logger log, DAOFactory daoFactory) {
		this.id = configAllarme.getNome();
		this.nome = configAllarme.getAlias();
		this.configAllarme = configAllarme;
		this._logger = log;
		this.daoFactory = daoFactory;
		this.threadName = "SDK";
		this.alarmLogger = new AlarmLogger(this.nome, this.id, this.threadName, this._logger,
				daoFactory!=null && daoFactory.getLog()!=null ? daoFactory.getLog() : this._logger);
	}
	
	protected void setThreadName(String threadName) {
		this.threadName = threadName;
		this.alarmLogger.setThreadName(this.threadName);
	}
	
	@Override
	public AlarmThreadStatus getActiveThreadStatus() {
		return this.alarmThreadStatus;
	}
	protected void setAlarmThreadStatus(AlarmThreadStatus alarmThreadStatus) {
		this.alarmThreadStatus = alarmThreadStatus;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public AlarmLogger getLogger(){
		return this.alarmLogger;
	}
	
	@Override
	public DAOFactory getDAOFactory(){
		return this.daoFactory;
	}
	
	// Metodo utilizzato dal Gateway in AlarmNotifier
	public void changeStatus(Logger logSql, Connection connection,  ServiceManagerProperties smp, AlarmStatus nuovoStatoAllarme) throws AlarmException, AlarmNotifyException {
		this._changeStatus(logSql, connection, smp, nuovoStatoAllarme);
	}
	
	@Override
	public void changeStatus(AlarmStatus nuovoStatoAllarme) throws AlarmException, AlarmNotifyException {
		
		if(this.alarmThreadStatus!=null && !this.alarmThreadStatus.isRunning()) {
			this.alarmLogger.info("Cambio di stato non effettuato, poichè è stato rilevato uno shutdown in corso");
			return;
		}
		
		this._changeStatus(null, null, null, nuovoStatoAllarme);
	}
	
	private void _changeStatus(Logger logSql, Connection connection,  ServiceManagerProperties smp, AlarmStatus nuovoStatoAllarme) throws AlarmException, AlarmNotifyException {
		
		IServiceManager allarmiSM = null;
		boolean changeEventFromGateway = false;
		List<AllarmeHistory> repositoryHistory = null;
		try {
			if(connection==null) {
				allarmiSM = (IServiceManager) this.daoFactory.getServiceManager(ProjectInfo.getInstance());
			}
			else {
				allarmiSM = (IServiceManager) this.daoFactory.getServiceManager(ProjectInfo.getInstance(), connection, smp, logSql);
				changeEventFromGateway = true;
				repositoryHistory = new ArrayList<AllarmeHistory>();
			}
		}catch(Exception e) {
			throw new AlarmException(e.getMessage(),e);
		}
		
		// Cambio di stato effettivo ?
		boolean statusChanged = false;
		if(this.status==null || this.status.getStatus()==null){
			statusChanged = true;
		}
		else{
			statusChanged = !this.status.getStatus().equals(nuovoStatoAllarme.getStatus());
		}
		
		// Cambio stato sul database degli allarmi
		AlarmManager.changeStatus(nuovoStatoAllarme, this, allarmiSM, this.username, statusChanged, repositoryHistory);
		
		
		// Switch stato nell'attuale implementazione
		AlarmStatus oldStatus = null;
		if(this.status!=null){
			oldStatus = (AlarmStatus) this.status.clone();
		}
		this.setStatus(nuovoStatoAllarme);
		
		if(changeEventFromGateway) {
			
			this.alarmLogger.debug("Registrazione richiesta notifica su db in corso ...");
			AlarmManager.registraNotifica(this.alarmLogger, this.configAllarme, oldStatus, nuovoStatoAllarme, 
					!repositoryHistory.isEmpty() ? repositoryHistory.get(0) : null,
					allarmiSM);
			this.alarmLogger.debug("Registrazione richiesta notifica su db terminata correttamente");
			
		}
		else {
		
			AlarmEngineConfig alarmEngineConfig = AlarmManager.getAlarmEngineConfig();
			if(alarmEngineConfig==null){
				throw new AlarmException("Configurazione Allarme non fornita, utilizzare il metodo AlarmManager.setAlarmEngineConfig(...)");
			}
			
			this.alarmLogger.debug("Analisi nuovo stato in corso ...");
			AlarmManager.notifyChangeStatus(alarmEngineConfig, this.configAllarme,
					this, this.pluginClassName, this.threadName,
					this.alarmLogger,
					oldStatus, nuovoStatoAllarme,
					(this.configAllarme!=null && TipoAllarme.ATTIVO.equals(this.configAllarme.getTipoAllarme()))
					);
			this.alarmLogger.info("Analisi nuovo stato terminata correttamente");
			
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
	public String getNome() {
		return this.nome;
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
	
	public String getPluginClassName() {
		return this.pluginClassName;
	}
	
	protected void setPluginClassName(String plugin) {
		this.pluginClassName = plugin;
	}

	@Override
	public boolean isManuallyUpdateState() {
		return this.manuallyUpdateState;
	}
	
	public void setManuallyUpdateState(boolean manuallyUpdateState) {
		this.manuallyUpdateState = manuallyUpdateState;
	}
	
	@Override
	public boolean isManuallyAckCriteria() {
		return this.manuallyAckCriteria;
	}
	
	public void setManuallyAckCriteria(boolean manuallyAckCriteria) {
		this.manuallyAckCriteria = manuallyAckCriteria;
	}

	private String id = null;
	private String nome = null;
	private Allarme configAllarme;
	private HashMap<String, Parameter<?>> parameters = null;
	private AlarmStatus status = null;
	private String pluginClassName = null;
	private boolean manuallyUpdateState = false;
	private boolean manuallyAckCriteria = false;
}
