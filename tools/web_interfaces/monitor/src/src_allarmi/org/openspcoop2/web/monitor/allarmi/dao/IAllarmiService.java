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

package org.openspcoop2.web.monitor.allarmi.dao;

import java.util.List;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.web.monitor.allarmi.bean.AllarmiSearchForm;
import org.openspcoop2.web.monitor.core.dao.ISearchFormService;

/**     
 * IAllarmiService
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IAllarmiService extends
		ISearchFormService<ConfigurazioneAllarmeBean, Long, AllarmiSearchForm> {

	public Long getCountAllarmiByStato(String stato, Integer acknowledged);

	public List<Parameter<?>> instanceParameters(Allarme configurazioneAllarme, Context context) throws Exception;
	
	public boolean isUsableFilter(Allarme configurazioneAllarme) throws Exception;
	
	public boolean isUsableGroupBy(Allarme configurazioneAllarme) throws Exception;
	
	public List<Plugin> plugins() throws Exception;
	
	public ConfigurazioneAllarmeBean getAllarme(String nome) throws NotFoundException, ServiceException;

	public AllarmeParametro getParametroByIdParametro(
			ConfigurazioneAllarmeBean allarme, String idParametro)
					throws NotFoundException, ServiceException;
	
	public List<String> nomeAllarmeAutoComplete(String val);
	
	public List<AllarmeHistory> findAllHistory(long idAllarme,int start,int limit) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException;
	
	public long countAllHistory(long idAllarme) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException;
		
	public void addHistory(AllarmeHistory history) throws ServiceException, NotImplementedException;
	
	public IAlarm getAlarm(String name) throws AlarmException;
}
