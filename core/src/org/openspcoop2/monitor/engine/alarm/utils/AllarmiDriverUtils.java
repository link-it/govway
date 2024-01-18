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

package org.openspcoop2.monitor.engine.alarm.utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeHistoryBean;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.plugins.utils.PluginsDriverUtils;
import org.slf4j.Logger;

/**
 * AllarmiDriverUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmiDriverUtils {

	public static List<ConfigurazioneAllarmeBean> allarmiList(ISearch ricerca, RuoloPorta ruoloPorta, String nomePorta, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "allarmiList";
			
		List<ConfigurazioneAllarmeBean> lista = new ArrayList<ConfigurazioneAllarmeBean>();
		
		try {
			
			List<Allarme> findAll = org.openspcoop2.core.allarmi.utils.AllarmiDriverUtils.allarmiList(ricerca, ruoloPorta, nomePorta, con, log, tipoDB);
			
			if(findAll != null && findAll.size() > 0){
				
				for (Allarme al : findAll) {
					
					Plugin plugin = PluginsDriverUtils.getPlugin(TipoPlugin.ALLARME.getValue(), al.getTipo(), true, con, log, tipoDB);
					
					lista.add(new ConfigurazioneAllarmeBean(al, plugin));
				}
			}

			return lista;

		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} 
	}
	
	public static ConfigurazioneAllarmeBean getAllarme(Long id, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "getAllarme";
		
		try {
			Allarme al = org.openspcoop2.core.allarmi.utils.AllarmiDriverUtils.getAllarme(id, con, log, tipoDB);
			Plugin plugin = PluginsDriverUtils.getPlugin(TipoPlugin.ALLARME.getValue(), al.getTipo(), true, con, log, tipoDB);
			return new ConfigurazioneAllarmeBean(al, plugin);
		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		}
	}
	public static ConfigurazioneAllarmeBean getAllarme(String nome, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "getAllarmeByNome";
		
		try {
			Allarme al = org.openspcoop2.core.allarmi.utils.AllarmiDriverUtils.getAllarme(nome, con, log, tipoDB);
			Plugin plugin = PluginsDriverUtils.getPlugin(TipoPlugin.ALLARME.getValue(), al.getTipo(), true, con, log, tipoDB);
			return new ConfigurazioneAllarmeBean(al, plugin);
		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		}
	}
	public static ConfigurazioneAllarmeBean getAllarme(Allarme allarme, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "convertAllarme";
		
		try {
			Plugin plugin = PluginsDriverUtils.getPlugin(TipoPlugin.ALLARME.getValue(), allarme.getTipo(), true, con, log, tipoDB);
			return new ConfigurazioneAllarmeBean(allarme, plugin);
		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		}
	}

	public static List<ConfigurazioneAllarmeHistoryBean> allarmiHistoryList(ISearch ricerca, Long idAllarme, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "allarmiHistoryList";
		
		List<ConfigurazioneAllarmeHistoryBean> lista = new ArrayList<ConfigurazioneAllarmeHistoryBean>();
		
		try {
			List<AllarmeHistory> findAll = org.openspcoop2.core.allarmi.utils.AllarmiDriverUtils.allarmiHistoryList(ricerca, idAllarme, con, log, tipoDB);
			
			if(findAll != null && findAll.size() > 0){
				for (AllarmeHistory al : findAll) {
					lista.add(new ConfigurazioneAllarmeHistoryBean(al));
				}
			}

			return lista;

		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		}
	}
	
	public static List<ConfigurazioneAllarmeBean> configurazioneAllarmiList(ISearch ricerca, RuoloPorta ruoloPorta, String nomePorta, 
			Connection con, Logger log, String tipoDB,
			String nomeMetodo, 
			IDSoggetto filtroSoggettoFruitore, IDServizioApplicativo filtroApplicativoFruitore,String filtroRuoloFruitore,
			IDSoggetto filtroSoggettoErogatore, String filtroRuoloErogatore,
			IDServizio filtroServizioAzione, String filtroRuolo) throws ServiceException{
		
		List<ConfigurazioneAllarmeBean> lista = new ArrayList<ConfigurazioneAllarmeBean>();
		
		try {
			
			List<Allarme> findAll = org.openspcoop2.core.allarmi.utils.AllarmiDriverUtils.configurazioneAllarmiList(ricerca, ruoloPorta, nomePorta, 
					con, log, tipoDB,
					nomeMetodo, 
					filtroSoggettoFruitore, filtroApplicativoFruitore, filtroRuoloFruitore,
					filtroSoggettoErogatore, filtroRuoloErogatore,
					filtroServizioAzione, filtroRuolo);
			
			if(findAll != null && findAll.size() > 0){
				
				for (Allarme al : findAll) {
					
					Plugin plugin = PluginsDriverUtils.getPlugin(TipoPlugin.ALLARME.getValue(), al.getTipo(), true, con, log, tipoDB);
					
					lista.add(new ConfigurazioneAllarmeBean(al, plugin));
				}
			}

			return lista;

		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} 
	}
}
