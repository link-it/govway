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

package org.openspcoop2.core.commons;

import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;

/**
 * IExtendedInfo
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IExtendedInfo {

	// read
	public Object getExtendedInfo(Connection connection, Logger log, Object originalObject, Object id) throws UtilsException;
	public List<Object> getAllIdsExtendedInfo(Connection connection,  Logger log, Object originalObject) throws UtilsException; 
	public List<Object> getAllExtendedInfo(Connection connection,  Logger log, Object originalObject) throws UtilsException;
	
	// store
	public void createExtendedInfo(Connection connection,  Logger log, Object originalObject, Object object) throws UtilsException;
	public void deleteExtendedInfo(Connection connection,  Logger log, Object originalObject, Object object) throws UtilsException;
	public void deleteAllExtendedInfo(Connection connection,  Logger log, Object originalObject) throws UtilsException;
	
	// serialize
	public byte[] serialize( Logger log, Object originalObject, Object object) throws UtilsException;
	public Object deserialize( Logger log, Object originalObject, byte[] object) throws UtilsException;
	
}
