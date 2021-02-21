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
package org.openspcoop2.web.monitor.statistiche.dao;

import org.openspcoop2.core.plugins.ConfigurazioneServizioAzione;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;
import org.openspcoop2.core.commons.search.Operation;
import org.openspcoop2.core.commons.search.PortType;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.parameters.Parameter;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;

/**
 * IStatisticaPersonalizzataService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public interface IStatisticaPersonalizzataService extends
		org.openspcoop2.web.monitor.core.dao.IService<ConfigurazioneStatistica, Long> {

	public void setConfigurazione(ConfigurazioneServizioAzione configurazione);

	public List<Parameter<?>> instanceParameters(ConfigurazioneStatistica configurazioneStatistica, Context context);
	
	public ConfigurazioneStatistica findByStatistica(ConfigurazioneStatistica statisticaToCheck) throws NotFoundException , ServiceException;
	public ConfigurazioneStatistica findByStatisticaPlugin(ConfigurazioneStatistica statisticaToCheck, boolean checkAllActions, boolean checkSpecificActions) throws NotFoundException, ServiceException ;
	
	public List<Plugin> compatiblePlugins(ConfigurazioneServizioAzione configurazione, Long idStatistica);
	
	public List<ConfigurazioneStatistica> getStatisticheByValues(IDAccordo idAccordo,
			String nomeServizio, String nomeAzione);

	public PortType getPortTypeFromAccordoServizio(String nomeAccordo,
			String nomeServizio) ;
	
	public List<Operation> getAzioniFromAccordoServizio(String nomeAccordo,
			String nomeServizio);

	public List<Map<String, Object>> findElencoServizi(
			Soggetto erogatore);
	
}
