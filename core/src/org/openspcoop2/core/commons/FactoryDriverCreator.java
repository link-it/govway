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

import java.io.InputStream;
import java.util.Properties;

/**
 * Questa classe imple
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class FactoryDriverCreator {

    private String fileProperties;
    
    public String getFilePropertiesName() {
        return this.fileProperties;
    }

    public void setFilePropertiesName(String fileProperties) {
        this.fileProperties = fileProperties;
    }

    public FactoryDriverCreator(String fileProperties)
    {
	this.fileProperties = fileProperties;
    }
    
    /**
     * Ritorna l'istanza del driver
     * @return Driver
     * @throws Exception
     */
    public abstract IDriverWS getDriver() throws Exception;
    
    /**
     * Legge le proprieta da un file properties
     * @param fileProperties
     * @return Properties
     * @throws Exception
     */
    public Properties readProperties(String fileProperties) throws Exception
    {
	Properties prop = new Properties();
	InputStream inProp = getClass().getClassLoader().getResourceAsStream(fileProperties);
	
	try {
	    prop.load(inProp);
	    
	    return new Properties(prop);
	    
	} catch(Exception e) {
	    
	    throw new Exception(e);
	    
	} finally {
	    try {
		inProp.close();
	    } catch (Exception e) {}
	}
    }
}


