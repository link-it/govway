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
package org.openspcoop2.generic_project.dao;

import org.openspcoop2.generic_project.beans.IDMappingBehaviour;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.exception.ValidationException;
import org.openspcoop2.generic_project.expression.IExpression;

/**
 * IDBServiceCRUDNoSingleObject
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IDBServiceCRUDNoSingleObject<T> extends IDBServiceCRUD  {

	public void update(long tableId, T obj) throws ServiceException,NotFoundException,NotImplementedException;
	
	public void update(long tableId, T obj, IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException,NotFoundException,NotImplementedException;
	
	public void update(long tableId, T obj, boolean validate) throws ServiceException,NotFoundException,NotImplementedException,ValidationException;
	
	public void update(long tableId, T obj, boolean validate, IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException,NotFoundException,NotImplementedException,ValidationException;
	
	public void updateFields(long tableId, UpdateField ... updateFields) throws ServiceException,NotFoundException,NotImplementedException;
	
	public void updateFields(long tableId, IExpression condition, UpdateField ... updateFields) throws ServiceException,NotFoundException,NotImplementedException;
	
	public void updateFields(long tableId, UpdateModel ... updateModels) throws ServiceException,NotFoundException,NotImplementedException;
	
	public void updateOrCreate(long tableId, T obj) throws ServiceException,NotImplementedException;
	
	public void updateOrCreate(long tableId, T obj, IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException,NotImplementedException;
	
	public void updateOrCreate(long tableId, T obj, boolean validate) throws ServiceException,NotImplementedException,ValidationException;
	
	public void updateOrCreate(long tableId, T obj, boolean validate, IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException,NotImplementedException,ValidationException;
	
}
