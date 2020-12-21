
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
package org.openspcoop2.web.monitor.allarmi.datamodel;

import java.util.List;

import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.web.monitor.allarmi.bean.AllarmiSearchForm;
import org.openspcoop2.web.monitor.allarmi.dao.IAllarmiService;
import org.openspcoop2.web.monitor.core.datamodel.BaseDataModelWithSearchForm;

/**
 * AllarmiDM
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class AllarmiDM extends BaseDataModelWithSearchForm<Long, ConfigurazioneAllarmeBean, IAllarmiService,AllarmiSearchForm>{

	private static final long serialVersionUID = 4494962270743603849L;
	
	@Override
	protected List<ConfigurazioneAllarmeBean> findObjects(int start, int limit, String sortField, SortOrder sortOrder) {
		return this.getDataProvider().findAll(start, limit); 
	}

	@Override
	public Long getId(ConfigurazioneAllarmeBean object) {
		if(object != null) 
			return object.getId();
		return null;
	}
}
