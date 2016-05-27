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

/**
 * DiagnosticConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DiagnosticConfig {

	private Boolean throwExceptionPlaceholderFailedResolution;
	
	private File diagnosticPropertiesResource;
	private Properties diagnosticProperties;
	private String diagnosticPropertiesResourceURI;
	
	public Boolean getThrowExceptionPlaceholderFailedResolution() {
		return this.throwExceptionPlaceholderFailedResolution;
	}
	public void setThrowExceptionPlaceholderFailedResolution(Boolean throwExceptionPlaceholderFailedResolution) {
		this.throwExceptionPlaceholderFailedResolution = throwExceptionPlaceholderFailedResolution;
	}
	public File getDiagnosticPropertiesResource() {
		return this.diagnosticPropertiesResource;
	}
	public void setDiagnosticPropertiesResource(File diagnosticPropertiesResource) {
		this.diagnosticPropertiesResource = diagnosticPropertiesResource;
	}
	public Properties getDiagnosticProperties() {
		return this.diagnosticProperties;
	}
	public void setDiagnosticProperties(Properties diagnosticProperties) {
		this.diagnosticProperties = diagnosticProperties;
	}
	public String getDiagnosticPropertiesResourceURI() {
		return this.diagnosticPropertiesResourceURI;
	}
	public void setDiagnosticPropertiesResourceURI(String diagnosticPropertiesResourceURI) {
		this.diagnosticPropertiesResourceURI = diagnosticPropertiesResourceURI;
	}

	
	public static Properties validateAndGetProperties(DiagnosticConfig config) throws UtilsException{
		if(config==null){
			throw new UtilsException("Diagnostic Configuration undefined");
		}
		if(config.getThrowExceptionPlaceholderFailedResolution()==null){
			throw new UtilsException("Diagnostic configuration (property ThrowExceptionPlaceholderFailedResolution) undefined");
		}
		int diagnosticConfigMode = 0;
		if(config.getDiagnosticProperties()!=null){
			diagnosticConfigMode++;
		}
		if(config.getDiagnosticPropertiesResource()!=null){
			diagnosticConfigMode++;
		}
		if(config.getDiagnosticPropertiesResourceURI()!=null){
			diagnosticConfigMode++;
		}
		if(diagnosticConfigMode==0){
			throw new UtilsException("Diagnostic configuration uncorrect: source diagnostic configuration file undefined");
		}
		if(diagnosticConfigMode>1){
			throw new UtilsException("Diagnostic configuration uncorrect: found multiple source diagnostic configuration file");
		}
		Properties diagProperties = null;
		if(config.getDiagnosticProperties()!=null){
			diagProperties = config.getDiagnosticProperties();
		}
		else if(config.getDiagnosticPropertiesResource()!=null){
			diagProperties = AbstractBaseDiagnosticManagerCore.getProperties(config.getDiagnosticPropertiesResource());
		}
		else if(config.getDiagnosticPropertiesResourceURI()!=null){
			diagProperties = AbstractBaseDiagnosticManagerCore.getProperties(config.getDiagnosticPropertiesResourceURI());
		}
		return diagProperties;
	}
}
