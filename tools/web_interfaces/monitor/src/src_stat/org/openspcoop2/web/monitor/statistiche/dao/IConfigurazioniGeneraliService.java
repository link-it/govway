package org.openspcoop2.web.monitor.statistiche.dao;

import java.util.List;

import org.openspcoop2.generic_project.exception.ServiceException;

import org.openspcoop2.web.monitor.core.dao.ISearchFormService;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGeneralePK;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioniGeneraliSearchForm;

public interface IConfigurazioniGeneraliService extends ISearchFormService<ConfigurazioneGenerale, ConfigurazioneGeneralePK,
ConfigurazioniGeneraliSearchForm> {

	public List<ConfigurazioneGenerale> findAllInformazioniGenerali() throws ServiceException;
	public List<ConfigurazioneGenerale> findAllInformazioniServizi() throws ServiceException;
	
	List<ConfigurazioneGenerale> findAllDettagli(int start, int limit);
}
