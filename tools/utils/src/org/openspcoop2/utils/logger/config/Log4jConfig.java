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

package org.openspcoop2.utils.logger.config;

import java.io.File;
import java.util.Properties;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.AbstractBaseDiagnosticManagerCore;
import org.openspcoop2.utils.logger.log4j.Log4jType;

/**
 * Log4jConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Log4jConfig {

	private Log4jType log4jType = Log4jType.LOG4Jv1;
	
	private File log4jPropertiesResource;
	private Properties log4jProperties;
	private String log4jPropertiesResourceURI;
	
	public Log4jType getLog4jType() {
		return this.log4jType;
	}
	public void setLog4jType(Log4jType log4jType) {
		this.log4jType = log4jType;
	}
	
	
	public File getLog4jPropertiesResource() {
		return this.log4jPropertiesResource;
	}
	public void setLog4jPropertiesResource(File log4jPropertiesResource) {
		this.log4jPropertiesResource = log4jPropertiesResource;
	}
	public Properties getLog4jProperties() {
		return this.log4jProperties;
	}
	public void setLog4jProperties(Properties log4jProperties) {
		this.log4jProperties = log4jProperties;
	}
	public String getLog4jPropertiesResourceURI() {
		return this.log4jPropertiesResourceURI;
	}
	public void setLog4jPropertiesResourceURI(String log4jPropertiesResourceURI) {
		this.log4jPropertiesResourceURI = log4jPropertiesResourceURI;
	}
	
	public static Properties validateAndGetProperties(Log4jConfig config) throws UtilsException{
		if(config==null){
			throw new UtilsException("Log4j configuration undefined (with enabled mode)");
		}
		int log4jConfigMode = 0;
		if(config.getLog4jProperties()!=null){
			log4jConfigMode++;
		}
		if(config.getLog4jPropertiesResource()!=null){
			log4jConfigMode++;
		}
		if(config.getLog4jPropertiesResourceURI()!=null){
			log4jConfigMode++;
		}
		if(log4jConfigMode==0){
			throw new UtilsException("Log4j configuration uncorrect: source log4j configuration file undefined");
		}
		if(log4jConfigMode>1){
			throw new UtilsException("Log4j configuration uncorrect: found multiple source log4j configuration file");
		}
		Properties log4jProperties = null;
		if(config.getLog4jProperties()!=null){
			log4jProperties = config.getLog4jProperties();
		}
		else if(config.getLog4jPropertiesResource()!=null){
			log4jProperties = AbstractBaseDiagnosticManagerCore.getProperties(config.getLog4jPropertiesResource());
		}
		else if(config.getLog4jPropertiesResourceURI()!=null){
			log4jProperties = AbstractBaseDiagnosticManagerCore.getProperties(config.getLog4jPropertiesResourceURI());
		}
		return log4jProperties;
	}
}
