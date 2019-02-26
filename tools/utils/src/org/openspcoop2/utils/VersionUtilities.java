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

package org.openspcoop2.utils;

import java.io.InputStream;
import java.util.Properties;

/**	
 * VersionUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VersionUtilities {

	private static String PROPERTY_FILE_RESOURCE_NAME = "/op2Version.properties";
	public static void setPROPERTY_FILE_RESOURCE_NAME(String PROPERTY_FILE_RESOURCE_NAME) {
		VersionUtilities.PROPERTY_FILE_RESOURCE_NAME = PROPERTY_FILE_RESOURCE_NAME;
	}
	
	private static String BUILD_VERSION_PROPERTY = "buildVersion";
	public static void setBUILD_VERSION_PROPERTY(String bUILD_VERSION_PROPERTY) {
		VersionUtilities.BUILD_VERSION_PROPERTY = bUILD_VERSION_PROPERTY;
	}
	
	public static String readBuildVersion() throws UtilsException {
		InputStream is = VersionUtilities.class.getResourceAsStream(VersionUtilities.PROPERTY_FILE_RESOURCE_NAME);
		return readBuildVersion(is);
	}
	
	public static String readBuildVersion(InputStream is) throws UtilsException {
		try {
			if(is!=null) {
				Properties p = new Properties();
				p.load(is);
				return readBuildVersion(p);
			}
			return null;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public static String readBuildVersion(Properties p) {
		String s = p.getProperty(VersionUtilities.BUILD_VERSION_PROPERTY);
		if(s!=null) {
			s = s.trim();
		}
		return s;
	}
	
}
