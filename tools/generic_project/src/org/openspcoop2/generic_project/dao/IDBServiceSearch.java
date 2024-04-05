/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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


import java.util.List;

import org.openspcoop2.generic_project.beans.IDMappingBehaviour;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;

/**
 * IDBServiceSearch
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IDBServiceSearch<T> {

	public T get(long tableId) throws ServiceException,NotFoundException,MultipleResultException,NotImplementedException;
	
	public T get(long tableId, IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException,NotFoundException,MultipleResultException,NotImplementedException;
	
	public boolean exists(long tableId) throws ServiceException,MultipleResultException,NotImplementedException;
	
	public List<Long> findAllTableIds(IPaginatedExpression expression) throws ServiceException,NotImplementedException;
	
	public long findTableId(IExpression expression) throws ServiceException,NotFoundException,MultipleResultException,NotImplementedException;
	
	public InUse inUse(long tableId) throws ServiceException,NotFoundException,NotImplementedException;
	
	public void enableSelectForUpdate() throws ServiceException,NotImplementedException;
	
	public void disableSelectForUpdate() throws ServiceException,NotImplementedException;
	
}
