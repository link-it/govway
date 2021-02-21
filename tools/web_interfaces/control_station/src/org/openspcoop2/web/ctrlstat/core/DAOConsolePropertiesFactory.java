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
package org.openspcoop2.web.ctrlstat.core;

import java.util.Properties;

import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.web.ctrlstat.config.DatasourceProperties;
import org.slf4j.Logger;

/**
 * DAOConsolePropertiesFactory
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DAOConsolePropertiesFactory extends DAOFactoryProperties {

	public DAOConsolePropertiesFactory(Logger log) throws Exception {
		super(log);
	}

	private boolean isDiag(IProjectInfo tipoDAO) {
		return (org.openspcoop2.core.tracciamento.utils.ProjectInfo.getInstance().getProjectName().equals(tipoDAO.getProjectName())
				||
		    org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance().getProjectName().equals(tipoDAO.getProjectName())
		    	||
		    org.openspcoop2.core.tracciamento.utils.ProjectInfo.getInstance().getProjectName().equals(tipoDAO.getProjectName())
		    ||
		    org.openspcoop2.core.diagnostica.utils.ProjectInfo.getInstance().getProjectName().equals(tipoDAO.getProjectName()));
	}
	
	@Override
	public String getTipoDatabase(IProjectInfo tipoDAO) throws Exception {
		if(isDiag(tipoDAO)) {
			return super.getTipoDatabase(tipoDAO);
		}
		else {
			return DatasourceProperties.getInstance().getTipoDatabase();
		}
	}
	
	@Override
	public String getTipoAccessoDatabase(IProjectInfo tipoDAO) throws Exception {
		if(isDiag(tipoDAO)) {
			return super.getTipoAccessoDatabase(tipoDAO);
		}
		else {
			return DAOFactoryProperties.PROP_TIPO_VALUE_DATASOURCE;
		}
	}
	
	@Override
	public String getDatasourceJNDIName(IProjectInfo tipoDAO) throws Exception {
		if(isDiag(tipoDAO)) {
			return super.getDatasourceJNDIName(tipoDAO);
		}
		else {
			return DatasourceProperties.getInstance().getDataSource();
		}
	}
	
	@Override
	public Properties getDatasourceJNDIContext(IProjectInfo tipoDAO) throws Exception {
		if(isDiag(tipoDAO)) {
			return super.getDatasourceJNDIContext(tipoDAO);
		}
		else {
			return DatasourceProperties.getInstance().getDataSourceContext();
		}
	}
}
