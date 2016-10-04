/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.dao.jdbc;

import java.sql.Connection;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.beans.IDMappingBehaviour;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.utils.sql.ISQLQueryObject;

/**
 * IJDBCServiceCRUD_DBNoSingleObject
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IJDBCServiceCRUD_DBNoSingleObject<T,SM> extends IJDBCServiceCRUD_DB<T,SM> {

	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, long tableId, T obj, IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, long tableId, UpdateField ... updateFields) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, long tableId, IExpression condition, UpdateField ... updateFields) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, long tableId, UpdateModel ... updateModels) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, long tableId, T obj, IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException,NotImplementedException,Exception;
	
}
