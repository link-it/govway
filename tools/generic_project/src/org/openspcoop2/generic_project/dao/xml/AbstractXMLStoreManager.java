/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.generic_project.dao.xml;


import org.slf4j.Logger;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.xml.JaxbUtils;

/**
 * AbstractXMLStoreManager
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AbstractXMLStoreManager<XML> {

	private String xmlPath;
	
	public AbstractXMLStoreManager(String xmlPath){
		this.xmlPath = xmlPath;
	}
	
	public void store(Logger log,XML xml,Class<XML> c) throws ServiceException{
		try{
			JaxbUtils.objToXml(this.xmlPath, c, xml, false);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw new ServiceException(e);
		}
	}
	
}
