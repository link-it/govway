/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.generic_project.web.dao;

import java.util.List;

import org.openspcoop2.generic_project.exception.ServiceException;

/***
 *
 * ICrudDAO Definisce una serie di metodi per l'accesso al livello dati.
 *
 * @param <T> tipo dell'oggetto
 * @param <K> tipo della chiave dell'oggetto.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ICrudDAO<T,K> {

	public void store(T obj) throws ServiceException;
	public void deleteById(K key) throws ServiceException;
	public void delete(T obj) throws ServiceException;
	public T findById(K key) throws ServiceException;
	public List<T> findAll() throws ServiceException;
	public boolean exists(T obj) throws ServiceException;
	
}
