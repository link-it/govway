package org.openspcoop2.web.monitor.statistiche.dao;

import org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.base.Plugin;
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
