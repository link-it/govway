/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved.
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
package org.openspcoop2.generic_project.dao.jpa;


import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;

/**
 * IJPAServiceSearch_DB
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IJPAServiceSearch_DB<T,SM> extends IJPAExpressionConstructor<SM>  {

	public T get(Logger log,EntityManager em, long tableId) throws ServiceException,NotFoundException,MultipleResultException,NotImplementedException, Exception;
	
	public boolean exists(Logger log,EntityManager em, long tableId) throws ServiceException,MultipleResultException,NotImplementedException, Exception;
	
	public List<Long> findAllTableIds(Logger log,EntityManager em, JPAPaginatedExpression expression) throws ServiceException,NotImplementedException, Exception;
	
	public long findTableId(Logger log,EntityManager em, JPAExpression expression) throws ServiceException,NotFoundException,MultipleResultException,NotImplementedException, Exception;
	
	public InUse inUse(Logger log,EntityManager em, long tableId) throws ServiceException,NotFoundException,NotImplementedException, Exception;
	
}
