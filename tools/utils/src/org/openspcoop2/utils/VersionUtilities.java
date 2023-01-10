/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import org.apache.commons.lang.StringUtils;

/**	
 * VersionUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VersionUtilities {

	private static String PROPERTY_FILE_RESOURCE_NAME = "/govwayVersion.properties";
	public static void setPROPERTY_FILE_RESOURCE_NAME(String PROPERTY_FILE_RESOURCE_NAME) {
		VersionUtilities.PROPERTY_FILE_RESOURCE_NAME = PROPERTY_FILE_RESOURCE_NAME;
	}
	
	private static String BUILD_VERSION_PROPERTY = "buildVersion";
	public static void setBUILD_VERSION_PROPERTY(String bUILD_VERSION_PROPERTY) {
		VersionUtilities.BUILD_VERSION_PROPERTY = bUILD_VERSION_PROPERTY;
	}
	
	private static String VERSION_PROPERTY = "version";
	public static void setVERSION_PROPERTY(String VERSION_PROPERTY) {
		VersionUtilities.VERSION_PROPERTY = VERSION_PROPERTY;
	}
	
	private static String INFO_VERSION_PROPERTY = "infoVersion";
	public static void setINFO_VERSION_PROPERTY(String INFO_VERSION_PROPERTY) {
		VersionUtilities.INFO_VERSION_PROPERTY = INFO_VERSION_PROPERTY;
	}
	
	public static String readVersion() throws UtilsException {
		return _read(VersionUtilities.VERSION_PROPERTY);
	}
	public static String readVersion(InputStream is) throws UtilsException {
		return _read(is, VersionUtilities.VERSION_PROPERTY);
	}
	public static String readVersion(Properties p) {
		return _read(p, VersionUtilities.VERSION_PROPERTY);
	}
	
	public static String readBuildVersion() throws UtilsException {
		return _read(VersionUtilities.BUILD_VERSION_PROPERTY);
	}
	public static String readBuildVersion(InputStream is) throws UtilsException {
		return _read(is, VersionUtilities.BUILD_VERSION_PROPERTY);
	}
	public static String readBuildVersion(Properties p) {
		return _read(p, VersionUtilities.BUILD_VERSION_PROPERTY);
	}
	
	public static IVersionInfo readInfoVersion() throws UtilsException {
		return _instance(_read(VersionUtilities.INFO_VERSION_PROPERTY));
	}
	public static IVersionInfo readInfoVersion(InputStream is) throws UtilsException {
		return _instance(_read(is, VersionUtilities.INFO_VERSION_PROPERTY));
	}
	public static IVersionInfo readInfoVersion(Properties p) throws UtilsException {
		return _instance(_read(p, VersionUtilities.INFO_VERSION_PROPERTY));
	}
	private static IVersionInfo _instance(String className) throws UtilsException {
		if(className!=null && !StringUtils.isEmpty(className)) {
			try {
				return  (IVersionInfo) Utilities.newInstance(className);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(), e);
			}
		}
		return null;
	}
	
	private static String _read(String propertyName) throws UtilsException {
		InputStream is = VersionUtilities.class.getResourceAsStream(VersionUtilities.PROPERTY_FILE_RESOURCE_NAME);
		return _read(is, propertyName);
	}
	private static String _read(InputStream is, String propertyName) throws UtilsException {
		try {
			if(is!=null) {
				Properties p = new Properties();
				p.load(is);
				return _read(p,propertyName);
			}
			return null;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private static String _read(Properties p, String propertyName) {
		String s = p.getProperty(propertyName);
		if(s!=null) {
			s = s.trim();
		}
		return s;
	}
	
}

