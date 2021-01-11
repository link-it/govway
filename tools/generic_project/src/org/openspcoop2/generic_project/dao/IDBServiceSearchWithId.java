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
package org.openspcoop2.generic_project.dao;

import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;


/**
 * IDBServiceSearchWithId
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IDBServiceSearchWithId<T,K> extends IDBServiceSearch<T>, IServiceSearchWithId<T, K> {

	public void mappingTableIds(K id, T obj) throws ServiceException,NotFoundException,NotImplementedException;
	
	public void mappingTableIds(long tableId, T obj) throws ServiceException,NotFoundException,NotImplementedException;
	
	public Long findTableId(K id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException;

	public K findId(long tableId, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException;

}
