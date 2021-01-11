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
package org.openspcoop2.generic_project.dao.jdbc;

import java.util.Hashtable;

import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.LoaderProperties;
import org.openspcoop2.generic_project.utils.Utilities;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;

/**
 * JDBCProperties
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCProperties extends LoaderProperties {

	public JDBCProperties(IProjectInfo project) throws ServiceException {
		super(Utilities.normalizedProjectName(project.getProjectName())+".dao.jdbc.properties");
	}
	public JDBCProperties(Package packageName, IProjectInfo project) throws ServiceException {
		super(packageName.getName().replaceAll("\\.", "/")+"/"+Utilities.normalizedProjectName(project.getProjectName())+".dao.jdbc.properties");
	}

	private static Hashtable<String, JDBCProperties> mapJDBCProperties = new Hashtable<String, JDBCProperties>();
	private static synchronized void initJDBCProperties(Package packageName, IProjectInfo project) throws ServiceException{
		String key = project.getProjectName();
		if(mapJDBCProperties.containsKey(key)==false){
			JDBCProperties jdbcProps = null;
			if(packageName!=null) {
				jdbcProps = new JDBCProperties(packageName,project);
			}
			else {
				jdbcProps = new JDBCProperties(project);
			}
			mapJDBCProperties.put(key, jdbcProps);
		}
	}
	
	public static JDBCProperties getInstance(IProjectInfo project) throws ServiceException {
		return getInstance(null, project);
	}
	public static JDBCProperties getInstance(Package packageName, IProjectInfo project) throws ServiceException {
		String key = project.getProjectName();
		if(mapJDBCProperties.containsKey(key)==false){
			initJDBCProperties(packageName, project);
		}
		return mapJDBCProperties.get(key);
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
			return (CRUD) ClassLoaderUtilities.newInstance(getServiceCRUDClass(object));
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
			return (SEARCH) ClassLoaderUtilities.newInstance(getServiceSearchClass(object));
		}catch(Exception e){
			throw new ServiceException("Loading service Search class failed: "+e.getMessage(),e);
		}
	}
} 
