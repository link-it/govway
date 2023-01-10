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

package org.openspcoop2.pdd.config;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.dao.IAllarmeServiceSearch;
import org.openspcoop2.core.allarmi.utils.AllarmiConverterUtils;
import org.openspcoop2.core.allarmi.utils.FiltroRicercaAllarmi;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.alarm.AlarmImpl;
import org.openspcoop2.monitor.engine.alarm.AlarmManager;
import org.openspcoop2.monitor.sdk.alarm.AlarmStatus;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;

/**     
 * ConfigurazionePdD_controlloTraffico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePdD_allarmi extends AbstractConfigurazionePdDConnectionResourceManager {

	private ServiceManagerProperties smp;
	
	public ConfigurazionePdD_allarmi(OpenSPCoop2Properties openspcoopProperties, DriverConfigurazioneDB driver, boolean useConnectionPdD) {
		super(openspcoopProperties, driver, useConnectionPdD, OpenSPCoop2Logger.getLoggerOpenSPCoopAllarmiSql(openspcoopProperties.isAllarmiDebug()));
			
		this.smp = new ServiceManagerProperties();
		this.smp.setShowSql(this.openspcoopProperties.isAllarmiDebug());
		this.smp.setDatabaseType(this.driver.getTipoDB());
	}
	
	public Allarme getAllarme(Connection connectionPdD, String nomeAllarme) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		
		Allarme allarme = null;
		
		ConfigurazionePdDConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "Allarmi.searchAllarmi");
			org.openspcoop2.core.allarmi.dao.IServiceManager sm = 
					(org.openspcoop2.core.allarmi.dao.IServiceManager) DAOFactory.getInstance(this.log).
					getServiceManager(org.openspcoop2.core.allarmi.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.smp,this.log);
		
			IdAllarme id = new IdAllarme();
			id.setNome(nomeAllarme);
			allarme = sm.getAllarmeServiceSearch().get(id);
						
		}
		catch(NotFoundException e){
			String errorMsg = "Allarme '"+nomeAllarme+"' non trovato";
			this.log.debug(errorMsg,e);
			throw new DriverConfigurazioneNotFound(errorMsg,e);
		}
		catch(Exception e){
			String errorMsg = "Errore durante la ricerca dell'allarme '"+nomeAllarme+"': "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new DriverConfigurazioneException(errorMsg,e);
		}
		finally {
			this.releaseConnection(cr);
		}

		if(allarme!=null) {
			return allarme;
		}
		throw new DriverConfigurazioneNotFound("Allarme '"+nomeAllarme+"' non trovato");
	}
	
	
	public List<Allarme> searchAllarmi(Connection connectionPdD, FiltroRicercaAllarmi filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		
		List<Allarme> list = new ArrayList<Allarme>();
				
		ConfigurazionePdDConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "Allarmi.searchAllarmi");
			org.openspcoop2.core.allarmi.dao.IServiceManager sm = 
					(org.openspcoop2.core.allarmi.dao.IServiceManager) DAOFactory.getInstance(this.log).
					getServiceManager(org.openspcoop2.core.allarmi.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.smp,this.log);
			
			String tipo = filtroRicerca.getTipo();
			if(tipo==null || "".equals(tipo)) {
				throw new Exception("Tipo allarme non fornito");
			}
			
			String idParametroCluster = filtroRicerca.getIdParametroCluster();
			String idCluster = filtroRicerca.getIdCluster(); 
			boolean idClusterOpzionale = filtroRicerca.isIdClusterOpzionale();
			
			String idParametro = filtroRicerca.getIdParametro();
			String valoreParametro = filtroRicerca.getValoreParametro();
			
			boolean recuperaSoloAllarmiInStatoDiversoDaOk = filtroRicerca.isRecuperaSoloAllarmiInStatoDiversoDaOk();
		
			Boolean globale = filtroRicerca.getGlobale();
			RuoloPorta ruoloPorta = null;
			String nomePorta = null;
			if(globale!=null && !globale) {
				ruoloPorta = filtroRicerca.getRuoloPorta();
				nomePorta = filtroRicerca.getNomePorta();
			}
			
			IAllarmeServiceSearch allarmeServiceSearch = sm.getAllarmeServiceSearch();
		
			IPaginatedExpression expr = allarmeServiceSearch.newPaginatedExpression();
			expr.limit(1000); // numero eccessivo
			expr.addOrder(Allarme.model().NOME, SortOrder.ASC);
			expr.and();
			expr.equals(Allarme.model().TIPO, tipo);
			expr.equals(Allarme.model().ENABLED, 1);
			
			if(globale!=null) {
				if(globale) {
					expr.isNull(Allarme.model().FILTRO.NOME_PORTA);
				}
				else {
					expr.equals(Allarme.model().FILTRO.RUOLO_PORTA, ruoloPorta.getValue()).and().equals(Allarme.model().FILTRO.NOME_PORTA, nomePorta);
				}
			}
			
			if(recuperaSoloAllarmiInStatoDiversoDaOk) {
				expr.notEquals(Allarme.model().STATO, AllarmiConverterUtils.toIntegerValue(StatoAllarme.OK));
			}
			
			// AVENDO DUE PARAMETRI IN GIOCO, LA QUERY VIENE SBAGLIATA. DOVREI GENERARE DUE JOIN CON DIFFERENTE ALIAS
			// IL CONTROLLO DEL CLUSTER LO FACCIO APPLICATIVO (tanto sono un numero limitato)
	//		if(idParametroCluster!=null) {
	//			expr.equals(ConfigurazioneAllarme.model().CONFIGURAZIONE_ALLARME_PARAMETRO.ID_PARAMETRO, idParametroCluster);
	//			if(idCluster!=null) {
	//				expr.like(ConfigurazioneAllarme.model().CONFIGURAZIONE_ALLARME_PARAMETRO.VALORE, idCluster,LikeMode.EXACT);
	//			}
	//			else {
	//				expr.isNull(ConfigurazioneAllarme.model().CONFIGURAZIONE_ALLARME_PARAMETRO.VALORE);
	//			}
	//		}
			if(idParametro!=null) {
				expr.equals(Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO, idParametro);
				if(valoreParametro!=null) {
					expr.like(Allarme.model().ALLARME_PARAMETRO.VALORE, valoreParametro,LikeMode.EXACT);
				}
				else {
					expr.isNull(Allarme.model().ALLARME_PARAMETRO.VALORE);
				}
			}
			
			List<Allarme> l = allarmeServiceSearch.findAll(expr);
			if(l!=null && l.size()>0){
				for (Allarme configurazioneAllarme : l) {
					boolean add = true;
					if(idParametroCluster!=null) {
						for (AllarmeParametro configurazioneAllarmeParametro : configurazioneAllarme.getAllarmeParametroList()) {
							if(idParametroCluster.equals(configurazioneAllarmeParametro.getIdParametro())) {
								if(idCluster!=null) {
									if(idCluster.equals(configurazioneAllarmeParametro.getValore())==false) {
										if(idClusterOpzionale) {
											if(configurazioneAllarmeParametro.getValore()!=null && 
													!"".equals(configurazioneAllarmeParametro.getValore()) && 
													!CostantiConfigurazione.CLUSTER_ID_NON_DEFINITO.equals(configurazioneAllarmeParametro.getValore())) {
												add=false;
											}
										}
										else {
											add=false;
										}
									}
								}
								else {
									if(idClusterOpzionale) {
										if(configurazioneAllarmeParametro.getValore()!=null && 
												!"".equals(configurazioneAllarmeParametro.getValore()) && 
												!CostantiConfigurazione.CLUSTER_ID_NON_DEFINITO.equals(configurazioneAllarmeParametro.getValore())) {
											add=false;
										}
									}
									else {
										add=false;
									}
								}
								break;
							}
						}
					}
					if(add) {
						list.add(configurazioneAllarme);
					}
				}
			}
		}
		catch(Exception e){
			String errorMsg = "Errore durante la ricerca degli allarmi: "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new DriverConfigurazioneException(errorMsg,e);
		}
		finally {
			this.releaseConnection(cr);
		}

		if(list!=null && !list.isEmpty()) {
			return list;
		}
		throw new DriverConfigurazioneNotFound("Allarmi non trovati");
	}

	
	public List<IAlarm> instanceAllarmi(Connection connectionPdD, List<Allarme> listAllarmi) throws DriverConfigurazioneException {
		
		ConfigurazionePdDConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "Allarmi.instanceAllarmi");
			DAOFactory daoFactory = DAOFactory.getInstance(this.log);
			org.openspcoop2.core.plugins.dao.IServiceManager smPlugins = 
					(org.openspcoop2.core.plugins.dao.IServiceManager) daoFactory.
					getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.smp,this.log);
			
			if(listAllarmi==null || listAllarmi.isEmpty()) {
				throw new Exception("Non sono stati forniti allarmi da istanziare");
			}
			
			List<IAlarm> list = new ArrayList<IAlarm>();
			for (Allarme allarme : listAllarmi) {
				list.add(AlarmManager.getAlarm(allarme, this.log, daoFactory, smPlugins));
			}
			
			return list;
		}
		catch(Exception e){
			String errorMsg = "Errore durante l'istanziazione degli allarmi: "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new DriverConfigurazioneException(errorMsg,e);
		}
		finally {
			this.releaseConnection(cr);
		}

	}
	
	public boolean changeStatus(Connection connectionPdD,  AlarmImpl alarm, AlarmStatus nuovoStatoAllarme) throws DriverConfigurazioneException {
		return _changeStatus(connectionPdD, alarm, nuovoStatoAllarme);
	}
	public boolean changeStatus(Connection connectionPdD,  IAlarm alarm, AlarmStatus nuovoStatoAllarme) throws DriverConfigurazioneException {
		return _changeStatus(connectionPdD, alarm, nuovoStatoAllarme);
	}
	private boolean _changeStatus(Connection connectionPdD,  IAlarm alarm, AlarmStatus nuovoStatoAllarme) throws DriverConfigurazioneException {
		ConfigurazionePdDConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "Allarmi.changeStato");
				
			if(alarm instanceof AlarmImpl) {
				((AlarmImpl)alarm).changeStatus(this.log, cr.connectionDB, this.smp,nuovoStatoAllarme);
			}
			else {
				alarm.changeStatus(nuovoStatoAllarme);
			}
			return true; // serve solo per passare dai metodi "cache" della configurazione; il valore true/false non viene usato
		}
		catch(Exception e){
			String errorMsg = "Errore durante update stato degli allarmi: "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new DriverConfigurazioneException(errorMsg,e);
		}
		finally {
			this.releaseConnection(cr);
		}
	}
	
	
}
