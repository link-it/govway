/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.plugins.dao.jdbc;

import java.sql.Connection;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;

import org.openspcoop2.core.plugins.IdPlugin;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.plugins.dao.IDBPluginServiceSearch;

/**     
 * JDBCPluginsBaseLib
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCPluginsBaseLib {

	
	public static Long getIdPlugin(Connection connection, JDBCServiceManagerProperties jdbcProperties, Logger log,
			TipoPlugin tipoPlugin,String className, boolean throwNotFound) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException, NotFoundException, MultipleResultException{
		
		JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(connection, jdbcProperties, log);
		
		IExpression expressionSearch = jdbcServiceManager.getPluginServiceSearch().newExpression();
		expressionSearch.
			and().
			equals(Plugin.model().TIPO,tipoPlugin).
			equals(Plugin.model().CLASS_NAME,className);
		
		Long id_plugin = null;
		try{
			id_plugin = ((IDBPluginServiceSearch)jdbcServiceManager.getPluginServiceSearch()).findTableId(expressionSearch);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		
		return id_plugin;
	}
	
	public static IdPlugin getIdPlugin(Connection connection, JDBCServiceManagerProperties jdbcProperties, Logger log, Long idPlugin) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException{
		
		JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(connection, jdbcProperties, log);
		
		Plugin p = ((IDBPluginServiceSearch)jdbcServiceManager.getPluginServiceSearch()).get(idPlugin);
		
		return jdbcServiceManager.getPluginServiceSearch().convertToId(p);
		
	}
	
	public static Plugin getPlugin(Connection connection, JDBCServiceManagerProperties jdbcProperties, Logger log, Long idPlugin) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException{
		
		JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(connection, jdbcProperties, log);
		
		Plugin p = ((IDBPluginServiceSearch)jdbcServiceManager.getPluginServiceSearch()).get(idPlugin);
		
		return p;
		
	}
	
	
	
	
	
}
