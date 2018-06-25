/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.statistiche.datamodel;

import java.util.List;

import org.openspcoop2.generic_project.expression.SortOrder;

import org.openspcoop2.web.monitor.core.datamodel.BaseDataModelWithSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGeneralePK;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioniGeneraliSearchForm;
import org.openspcoop2.web.monitor.statistiche.dao.IConfigurazioniGeneraliService;

/**
 * ConfigurazioniDM
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ConfigurazioniDM extends BaseDataModelWithSearchForm<ConfigurazioneGeneralePK, ConfigurazioneGenerale, 
	IConfigurazioniGeneraliService,ConfigurazioniGeneraliSearchForm>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected List<ConfigurazioneGenerale> findObjects(int start, int limit, String sortField, SortOrder sortOrder) {
		return this.getDataProvider().findAll(start, limit);
	}
	
	@Override
	public ConfigurazioneGeneralePK getId(ConfigurazioneGenerale object) {
		if(object != null) 
			return object.getId();  
		return null;
	}

	public boolean isExecuteQuery(){
		return ((ConfigurazioniGeneraliSearchForm)this.getDataProvider().getSearch()).isExecuteQuery();
	}	
}
