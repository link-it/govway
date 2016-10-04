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
package org.openspcoop2.generic_project.web.dao;

import java.util.List;

import org.openspcoop2.generic_project.exception.ServiceException;


/***
 * 
 * IPaginatedList fornisce i metodi per l'accesso in modalita' paginata ai dati
 *
 * @param <T> Tipo dell'oggetto.
 * @param <K> Tipo della chiave dell'oggetto.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IPaginatedList<T,K> extends ICrudDAO<T, K>{
	public List<T> findAll(int start, int limit) throws ServiceException;
	public int totalCount() throws ServiceException;
}
