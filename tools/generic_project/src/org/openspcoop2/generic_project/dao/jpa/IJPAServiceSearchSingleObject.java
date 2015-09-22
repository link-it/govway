/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.openspcoop2.generic_project.beans.IDMappingBehaviour;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;

/**
 * IJPAServiceSearchSingleObject
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IJPAServiceSearchSingleObject<T,SM> extends IJPAServiceSearchSingleObject_DB<T,SM>{

	public T get(Logger log,EntityManager em) throws NotFoundException,MultipleResultException,NotImplementedException,ServiceException,Exception;
	
	public T get(Logger log,EntityManager em, IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException,NotFoundException,MultipleResultException,NotImplementedException,Exception;
	
	public boolean exists(Logger log,EntityManager em) throws MultipleResultException,NotImplementedException,ServiceException,Exception;
	
	public InUse inUse(Logger log,EntityManager em) throws NotFoundException,NotImplementedException,ServiceException,Exception;

	public List<Object> select(Logger log,EntityManager em,JPAPaginatedExpression expression, IField field) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public List<Object> select(Logger log,EntityManager em,JPAPaginatedExpression expression, boolean distinct, IField field) throws ServiceException,NotFoundException,NotImplementedException,Exception;
		
	public List<Map<String,Object>> select(Logger log,EntityManager em,JPAPaginatedExpression expression, IField ... field) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public List<Map<String,Object>> select(Logger log,EntityManager em,JPAPaginatedExpression expression, boolean distinct, IField ... field) throws ServiceException,NotFoundException,NotImplementedException,Exception;
		
	public List<List<Object>> nativeQuery(Logger log,EntityManager em, String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException, Exception;

}
