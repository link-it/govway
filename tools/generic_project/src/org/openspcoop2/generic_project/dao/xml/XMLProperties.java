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
package org.openspcoop2.generic_project.dao.xml;

import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.LoaderProperties;
import org.openspcoop2.generic_project.utils.Utilities;

/**
 * XMLProperties
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLProperties extends LoaderProperties {

	public XMLProperties(IProjectInfo project) throws ServiceException {
		super(Utilities.normalizedProjectName(project.getProjectName())+".dao.xml.properties");
	}

	public static XMLProperties getInstance(IProjectInfo project) throws ServiceException {
		return new XMLProperties(project);
	}
	
	@SuppressWarnings("unchecked")
	public <CRUD> Class<CRUD> getServiceCRUDClass(String object) throws ServiceException{
		try{
			String p = this.properties.getProperty("service.crud."+object);
			if(p!=null){
				p = p.trim();
			}else{
				throw new ServiceException("Property 'service.crud."+object+"' not found in the file properties "+this.filePropertiesName);
			}
			Class<?> serviceClass = Class.forName(p);
			return (Class<CRUD>) serviceClass;
		}catch(Exception e){
			throw new ServiceException("Loading service CRUD class failed: "+e.getMessage(),e);
		}
	}
	@SuppressWarnings("unchecked")
	public <CRUD> CRUD getServiceCRUD(String object) throws ServiceException{
		try{
			return (CRUD) getServiceCRUDClass(object).newInstance();
		}catch(Exception e){
			throw new ServiceException("Loading service CRUD class failed: "+e.getMessage(),e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <SEARCH> Class<SEARCH> getServiceSearchClass(String object) throws ServiceException{
		try{
			String p = this.properties.getProperty("service.search."+object);
			if(p!=null){
				p = p.trim();
			}else{
				throw new ServiceException("Property 'service.search."+object+"' not found in the file properties "+this.filePropertiesName);
			}
			Class<?> serviceClass = Class.forName(p);
			return (Class<SEARCH>) serviceClass;
		}catch(Exception e){
			throw new ServiceException("Loading service Search class failed: "+e.getMessage(),e);
		}
	}
	@SuppressWarnings("unchecked")
	public <SEARCH> SEARCH getServiceSearch(String object) throws ServiceException{
		try{
			return (SEARCH) getServiceSearchClass(object).newInstance();
		}catch(Exception e){
			throw new ServiceException("Loading service Search class failed: "+e.getMessage(),e);
		}
	}
} 
