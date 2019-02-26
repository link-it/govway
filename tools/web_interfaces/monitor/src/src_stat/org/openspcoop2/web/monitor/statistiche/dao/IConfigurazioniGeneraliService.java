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
package org.openspcoop2.web.monitor.statistiche.dao;

import java.util.List;

import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.generic_project.exception.ServiceException;

import org.openspcoop2.web.monitor.core.dao.ISearchFormService;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGeneralePK;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioniGeneraliSearchForm;

/**
 * IConfigurazioniGeneraliService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public interface IConfigurazioniGeneraliService extends ISearchFormService<ConfigurazioneGenerale, ConfigurazioneGeneralePK,
ConfigurazioniGeneraliSearchForm> {

	public List<ConfigurazioneGenerale> findAllInformazioniGenerali() throws ServiceException;
	public List<ConfigurazioneGenerale> findAllInformazioniServizi() throws ServiceException;
	
	List<ConfigurazioneGenerale> findAllDettagli(int start, int limit);
	
	public List<ConfigurazioneGenerale> findConfigurazioniFiglie(String nomePorta, PddRuolo ruolo) throws ServiceException;
}
