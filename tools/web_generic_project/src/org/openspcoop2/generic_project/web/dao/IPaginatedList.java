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
