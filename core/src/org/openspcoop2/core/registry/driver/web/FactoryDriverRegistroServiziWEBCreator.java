/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.registry.driver.web;

import java.util.Enumeration;
import java.util.Properties;

import org.openspcoop2.core.commons.FactoryDriverCreator;
import org.openspcoop2.core.commons.IDriverWS;

/**
 *
 *
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FactoryDriverRegistroServiziWEBCreator extends
	FactoryDriverCreator {

    private String urlPrefix;
    private String pathPrefix;
    
    public FactoryDriverRegistroServiziWEBCreator(String fileProperties) {
	super(fileProperties);
    }

    /**
     * Ritorna una nuova istanza del {@link DriverRegistroServiziWEB}
     */
    @Override
    public IDriverWS getDriver() throws Exception {
	
	//Leggo infoGeneral Properties
	Properties prop = readProperties(getFilePropertiesName());
	Enumeration<?> en = prop.propertyNames();
	while (en.hasMoreElements()) {
	    String property = (String) en.nextElement();
	    if (property.equals("WebUrlPrefix")) {
		String value = prop.getProperty(property);
		if (value!=null) {
		    value = value.trim();
		    this.urlPrefix = value;
		}
	    }
	    if (property.equals("WebPathPrefix")) {
		String value = prop.getProperty(property);
		if (value!=null) {
		    value = value.trim();
		    this.pathPrefix = value;
		}
	    }
	}//chiudo while
	
	return new DriverRegistroServiziWEB(this.urlPrefix,this.pathPrefix,null);
	
	
    }

    
    
    
}


