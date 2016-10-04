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

package org.openspcoop2.utils.logger.config;

import java.io.File;
import java.net.URI;
import java.net.URL;
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
	
	private File diagnosticConfigFile;
	private String diagnosticConfigName;
	private URI diagnosticConfigURI;
	private URL diagnosticConfigURL;
	private Properties diagnosticConfigProperties;
	
	
	public File getDiagnosticConfigFile() {
		return this.diagnosticConfigFile;
	}
	public void setDiagnosticConfigFile(File diagnosticConfigFile) {
		this.diagnosticConfigFile = diagnosticConfigFile;
	}
	public Properties getDiagnosticConfigProperties() {
		return this.diagnosticConfigProperties;
	}
	public void setDiagnosticConfigProperties(Properties diagnosticConfigProperties) {
		this.diagnosticConfigProperties = diagnosticConfigProperties;
	}
	public String getDiagnosticConfigName() {
		return this.diagnosticConfigName;
	}
	public void setDiagnosticConfigName(String diagnosticConfigName) {
		this.diagnosticConfigName = diagnosticConfigName;
	}
	public URI getDiagnosticConfigURI() {
		return this.diagnosticConfigURI;
	}
	public void setDiagnosticConfigURI(URI diagnosticConfigURI) {
		this.diagnosticConfigURI = diagnosticConfigURI;
	}
	public URL getDiagnosticConfigURL() {
		return this.diagnosticConfigURL;
	}
	public void setDiagnosticConfigURL(URL diagnosticConfigURL) {
		this.diagnosticConfigURL = diagnosticConfigURL;
	}
	
	
	public Boolean getThrowExceptionPlaceholderFailedResolution() {
		return this.throwExceptionPlaceholderFailedResolution;
	}
	public void setThrowExceptionPlaceholderFailedResolution(Boolean throwExceptionPlaceholderFailedResolution) {
		this.throwExceptionPlaceholderFailedResolution = throwExceptionPlaceholderFailedResolution;
	}


	// Istanza statica per motivi di performance
	private static Properties staticProperties = null;
	
	public static void validate(DiagnosticConfig config) throws UtilsException{
		if(staticProperties==null){
			_validate(config);
		}
	}
	public static Properties readProperties(DiagnosticConfig config) throws UtilsException{
		if(staticProperties==null){
			return _readProperties(config);
		}
		return staticProperties;
	}
	
	private static void _validate(DiagnosticConfig config) throws UtilsException{
		if(staticProperties==null){
			if(config==null){
				throw new UtilsException("Diagnostic Configuration undefined");
			}
			if(config.getThrowExceptionPlaceholderFailedResolution()==null){
				throw new UtilsException("Diagnostic configuration (property ThrowExceptionPlaceholderFailedResolution) undefined");
			}
			int diagnosticConfigMode = 0;
			if(config.getDiagnosticConfigFile()!=null){
				diagnosticConfigMode++;
			}
			if(config.getDiagnosticConfigName()!=null){
				diagnosticConfigMode++;
			}
			if(config.getDiagnosticConfigURI()!=null){
				diagnosticConfigMode++;
			}
			if(config.getDiagnosticConfigURL()!=null){
				diagnosticConfigMode++;
			}
			if(config.getDiagnosticConfigProperties()!=null){
				diagnosticConfigMode++;
			}
			if(diagnosticConfigMode==0){
				throw new UtilsException("Diagnostic configuration uncorrect: source diagnostic configuration file undefined");
			}
			if(diagnosticConfigMode>1){
				throw new UtilsException("Diagnostic configuration uncorrect: found multiple source diagnostic configuration file");
			}
		}
	}
	
	private static synchronized Properties _readProperties(DiagnosticConfig config) throws UtilsException{
		
		if(staticProperties==null){
		
			if(config==null){
				throw new UtilsException("Diagnostic Configuration undefined");
			}
			Properties diagProperties = null;
			if(config.getDiagnosticConfigFile()!=null){
				diagProperties = AbstractBaseDiagnosticManagerCore.getProperties(config.getDiagnosticConfigFile());
			}
			else if(config.getDiagnosticConfigName()!=null){
				diagProperties = AbstractBaseDiagnosticManagerCore.getProperties(config.getDiagnosticConfigName());
			}
			else if(config.getDiagnosticConfigURI()!=null){
				diagProperties = AbstractBaseDiagnosticManagerCore.getProperties(config.getDiagnosticConfigURI());
			}
			else if(config.getDiagnosticConfigURL()!=null){
				diagProperties = AbstractBaseDiagnosticManagerCore.getProperties(config.getDiagnosticConfigURL());
			}
			else if(config.getDiagnosticConfigProperties()!=null){
				diagProperties = config.getDiagnosticConfigProperties();
			}
			staticProperties = diagProperties;
		}
		return staticProperties;
	}
}
