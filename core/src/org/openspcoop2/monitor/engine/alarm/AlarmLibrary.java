/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.constants.TipoAllarme;
import org.openspcoop2.core.allarmi.constants.TipoPeriodo;
import org.openspcoop2.core.allarmi.dao.IAllarmeServiceSearch;
import org.openspcoop2.core.allarmi.dao.IServiceManager;
import org.openspcoop2.core.allarmi.utils.AllarmiConverterUtils;
import org.openspcoop2.core.allarmi.utils.ProjectInfo;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.monitor.sdk.constants.AlarmStateValues;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;
import org.openspcoop2.utils.Utilities;

/**
 * AlarmLibrary
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AlarmLibrary {

	private IAllarmeServiceSearch allarmeSearchDAO;
	private Logger log;
	private AlarmEngineConfig alarmEngineConfig;
	private DAOFactory daoFactory;
	
	private Hashtable<String, AlarmThread> activeThreads = null;
	
	public AlarmLibrary(DAOFactory daoFactory,Logger log,AlarmEngineConfig alarmEngineConfig) throws AlarmException{
		try{
			this.daoFactory = daoFactory;
			IServiceManager pluginSM = (IServiceManager) this.daoFactory.getServiceManager(ProjectInfo.getInstance());
			IAllarmeServiceSearch allarmeSearchDAO = pluginSM
					.getAllarmeServiceSearch();
			this.allarmeSearchDAO = allarmeSearchDAO;
			this.log = log;
			this.alarmEngineConfig = alarmEngineConfig;
			AlarmManager.setAlarmEngineConfig(alarmEngineConfig);
		}catch(Exception e){
			throw new AlarmException(e.getMessage(),e);
		}
	}
	
	/* 
	 * Utiltiies per dormire
	 * NOTA: Devo dormire il tempo minimo configurabile per un allarme (un minuto).
	 **/
	
	public void sleep(){
		for (int i = 0; i < 60; i++) {
			Utilities.sleep(1000); // Devo dormire il tempo minimo configurabile per un allarme (un minuto).
			if(this.stop){
				break;
			}
		}
	}
	
	/* Utiltiies per stop threads */
	
	private boolean stop = false;
	public void stop(){
		if(this.activeThreads.size()>0){
			for (AlarmThread alarmThread : this.activeThreads.values()) {
				this.stopAlarm(alarmThread);
			}
		}
		this.stop = true;
		this.activeThreads = null;
	}
	public void stopAlarm(String name,boolean throwExceptionNotFound) throws AlarmException{
		if(this.activeThreads!=null && this.activeThreads.size()>0 && this.activeThreads.containsKey(name)){
			AlarmThread alarmThread = this.activeThreads.get(name);
			this.stopAlarm(alarmThread);
			this.activeThreads.remove(name);
		}
		else{
			if(throwExceptionNotFound){
				throw new AlarmException("Alarm ["+name+"] not exists");
			}
		}
	}
	private void stopAlarm(AlarmThread alarmThread){
		alarmThread.setStop(true);
		int max = 10 * 60 * 5; // attendo 5 minuti
		int offset = 0;
		int increment = 100;
		while(offset<max && alarmThread.isTerminated()==false){
			Utilities.sleep(increment);
			offset = offset + increment;
		}
	}
	
	/* Utiltiies per start threads */
		
	public void executeAlarms(boolean exitAfterExecution) throws AlarmException{
		try{
			this.activeThreads = this.getActiveAlarmThreads(this.allarmeSearchDAO);
			if(this.activeThreads.size()>0){
				for (AlarmThread alarmThread : this.activeThreads.values()) {
					Thread t = new Thread(alarmThread);
					t.start();	
				}
				
				if(exitAfterExecution==false){
					while (true){
						// non deve morire mai
						this.sleep();
					}
				}
			}
			else{
				this.log.warn("Non sono stati trovati allarmi");
			}
		}catch(Exception e){
			throw new AlarmException(e.getMessage(),e);
		}
	}
	
	public void executeAlarm(String name) throws AlarmException{
		try{
			Allarme conf = null;
			try{
				conf = this.getActiveAlarmThread(this.allarmeSearchDAO, name);
			}catch(Exception e){
				throw new AlarmException(e.getMessage(),e);
			}
			if(conf==null){
				throw new AlarmException("Alarm ["+name+"] not exists");
			}
			if(conf.getEnabled()==1 && TipoAllarme.ATTIVO.equals(conf.getTipoAllarme())){
				AlarmThread alarmThread = this.createAlarmThread(conf);
				if(alarmThread!=null){
					this.activeThreads.put(conf.getNome(),alarmThread);
					Thread t = new Thread(alarmThread);
					t.start();	
				}
			}
		}catch(Exception e){
			throw new AlarmException(e.getMessage(),e);
		}
	}
	
	
	/* Utiltiies per update stato threads */
	
	public void updateStateAlarm(String name,AlarmStateValues alarmStatus) throws AlarmException{
		try{
			Allarme conf = null;
			try{
				conf = this.getActiveAlarmThread(this.allarmeSearchDAO, name);
			}catch(Exception e){
				throw new AlarmException(e.getMessage(),e);
			}
			if(conf==null){
				throw new AlarmException("Alarm ["+name+"] not exists");
			}
			AlarmThread alarmThread = this.activeThreads.get(name);
			alarmThread.updateState(alarmStatus);
		}catch(Exception e){
			throw new AlarmException(e.getMessage(),e);
		}
	}
	
	public void updateAcknoledgement(String name,boolean acknoledgement) throws AlarmException{
		try{
			Allarme conf = null;
			try{
				conf = this.getActiveAlarmThread(this.allarmeSearchDAO, name);
			}catch(Exception e){
				throw new AlarmException(e.getMessage(),e);
			}
			if(conf==null){
				throw new AlarmException("Alarm ["+name+"] not exists");
			}
			AlarmThread alarmThread = this.activeThreads.get(name);
			alarmThread.updateAcknowledged(acknoledgement);
		}catch(Exception e){
			throw new AlarmException(e.getMessage(),e);
		}
	}
	
	
	/* Utiltiies interne */
	
	private Hashtable<String, AlarmThread> getActiveAlarmThreads(IAllarmeServiceSearch allarmeSearchDAO) throws Exception{
		
		IExpression expr = allarmeSearchDAO.newExpression();
		expr.and();
		expr.notEquals(Allarme.model().ENABLED,
				Integer.valueOf(0));
		expr.notEquals(Allarme.model().TIPO_ALLARME,
				TipoAllarme.PASSIVO);
		//expr.isNotNull(Allarme.model().CLASS_NAME);

		IPaginatedExpression pagExpr = allarmeSearchDAO
				.toPaginatedExpression(expr);
		
		List<Allarme> list = allarmeSearchDAO.findAll(pagExpr);
		Hashtable<String, AlarmThread> listAlarmThread = new Hashtable<String, AlarmThread>();
		
		for (Allarme confAllarme : list) {
			AlarmThread alarmThread = createAlarmThread(confAllarme);
			if(alarmThread!=null){
				listAlarmThread.put(confAllarme.getNome(),alarmThread);
			}
		}
		return listAlarmThread;
	}
	
	private Allarme getActiveAlarmThread(IAllarmeServiceSearch allarmeSearchDAO, String name) throws Exception{
		
		IdAllarme id = new IdAllarme();
		id.setNome(name);
		return allarmeSearchDAO.get(id);
		
	}
	
	private AlarmThread createAlarmThread(Allarme confAllarme) throws AlarmException{
		IAlarm alarm = AlarmManager.getAlarm(confAllarme,this.log,this.daoFactory);
		String classname = ((AlarmImpl) alarm).getPluginClassName();
		TipoPeriodo tipoPeriodo = AllarmiConverterUtils.toTipoPeriodo(confAllarme.getTipoPeriodo());
		int periodo = confAllarme.getPeriodo().intValue();

		AlarmThread alarmThread = null;
		if (alarm != null) {
			try {
				alarmThread = new AlarmThread(this.log, confAllarme.getTipo(), classname, alarm, this.alarmEngineConfig);
				if (periodo != 0) {
					switch (tipoPeriodo) {
					case G:
						alarmThread.setPeriodByDays(periodo);
						break;
					case H:
						alarmThread.setPeriodByHours(periodo);
						break;
					case M:
						alarmThread.setPeriodByMinutes(periodo);
						break;
					default:
						alarmThread.setPeriodBySeconds(periodo);
						break;
					}
				}
				
			} catch (AlarmException e) {
				this.log.error(e.getMessage(),e);
			}
		}
		return alarmThread;
	}
		
}
