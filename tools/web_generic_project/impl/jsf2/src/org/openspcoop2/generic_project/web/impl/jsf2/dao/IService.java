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
package org.openspcoop2.generic_project.web.impl.jsf2.dao;

import java.util.List;
import java.util.Map;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.web.dao.IPaginatedList;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

/**
 * IService Interfaccia per l'accesso ai servizi 
 *
 * @param <T> Tipo dell'oggetto
 * @param <K> Tipo della chiave dell'oggetto
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IService<T,K> extends IPaginatedList<T, K>{

    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) throws ServiceException;
    
    public List<T> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String,Object> filters) throws ServiceException; 
}