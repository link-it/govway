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
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;

/**
 * Log4jConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Log4jConfig {

	private File log4jConfigFile;
	private String log4jConfigName;
	private URI log4jConfigURI;
	private URL log4jConfigURL;
	private Properties log4jConfigProperties;
	
	public File getLog4jConfigFile() {
		return this.log4jConfigFile;
	}
	public void setLog4jConfigFile(File log4jConfigFile) {
		this.log4jConfigFile = log4jConfigFile;
	}
	public Properties getLog4jConfigProperties() {
		return this.log4jConfigProperties;
	}
	public void setLog4jConfigProperties(Properties log4jConfigProperties) {
		this.log4jConfigProperties = log4jConfigProperties;
	}
	public String getLog4jConfigName() {
		return this.log4jConfigName;
	}
	public void setLog4jConfigName(String log4jConfigName) {
		this.log4jConfigName = log4jConfigName;
	}
	public URI getLog4jConfigURI() {
		return this.log4jConfigURI;
	}
	public void setLog4jConfigURI(URI log4jConfigURI) {
		this.log4jConfigURI = log4jConfigURI;
	}
	public URL getLog4jConfigURL() {
		return this.log4jConfigURL;
	}
	public void setLog4jConfigURL(URL log4jConfigURL) {
		this.log4jConfigURL = log4jConfigURL;
	}

	
	// Istanza statica per motivi di performance
	private static boolean logInitialized = false;
	
	public static void validate(Log4jConfig config) throws UtilsException{
		if(logInitialized==false){
			_validate(config);
		}
	}
	public static void setConfigurationLogger(Log4jConfig config) throws UtilsException{
		if(logInitialized==false){
			_setConfigurationLogger(config);
		}
	}
	
	private static void _validate(Log4jConfig config) throws UtilsException{
		if(logInitialized == false){
			if(config==null){
				throw new UtilsException("Log4j configuration undefined (with enabled mode)");
			}
			int log4jConfigMode = 0;
			if(config.getLog4jConfigFile()!=null){
				log4jConfigMode++;
			}
			if(config.getLog4jConfigName()!=null){
				log4jConfigMode++;
			}
			if(config.getLog4jConfigURI()!=null){
				log4jConfigMode++;
			}
			if(config.getLog4jConfigURL()!=null){
				log4jConfigMode++;
			}
			if(config.getLog4jConfigProperties()!=null){
				log4jConfigMode++;
			}	
			if(log4jConfigMode==0){
				throw new UtilsException("Log4j configuration uncorrect: source log4j configuration file undefined");
			}
			if(log4jConfigMode>1){
				throw new UtilsException("Log4j configuration uncorrect: found multiple source log4j configuration file");
			}
		}
	}
	
	private static synchronized void _setConfigurationLogger(Log4jConfig config) throws UtilsException{
		if(logInitialized == false){
			if(config.getLog4jConfigFile()!=null){
				LoggerWrapperFactory.setLogConfiguration(config.getLog4jConfigFile());
			}
			else if(config.getLog4jConfigName()!=null){
				LoggerWrapperFactory.setLogConfiguration(config.getLog4jConfigName());
			}
			else if(config.getLog4jConfigURI()!=null){
				LoggerWrapperFactory.setLogConfiguration(config.getLog4jConfigURI());
			}
			else if(config.getLog4jConfigURL()!=null){
				LoggerWrapperFactory.setLogConfiguration(config.getLog4jConfigURL());
			}
			else if(config.getLog4jConfigProperties()!=null){
				LoggerWrapperFactory.setLogConfiguration(config.getLog4jConfigProperties());
			}
			logInitialized = true;
		}
	}
}
