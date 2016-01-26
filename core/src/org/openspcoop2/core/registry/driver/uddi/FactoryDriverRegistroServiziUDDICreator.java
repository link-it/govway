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



package org.openspcoop2.core.registry.driver.uddi;

import java.util.Enumeration;
import java.util.Properties;

import org.openspcoop2.core.commons.FactoryDriverCreator;
import org.openspcoop2.core.commons.IDriverWS;

/**
 *
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FactoryDriverRegistroServiziUDDICreator extends
FactoryDriverCreator {

	private String inquiryURL;
	private String publishURL;
	private String user;
	private String password;
	private String urlPrefix;
	private String pathPrefix;



	public FactoryDriverRegistroServiziUDDICreator(String fileProperties) {
		super(fileProperties);
	}

	/**
	 * Ritorna una nuova istanza del {@link DriverRegistroServiziUDDI}
	 */
	@Override
	public IDriverWS getDriver() throws Exception {

		//Leggo infoGeneral Properties
		Properties prop = this.readProperties(this.getFilePropertiesName());
		Enumeration<?> en = prop.propertyNames();
		while (en.hasMoreElements()) {
			String property = (String) en.nextElement();
			if (property.equals("UddiInquiryURL")) {
				String value = prop.getProperty(property);
				if (value!=null) {
					value = value.trim();
					this.inquiryURL = value;
				}
			}
			if (property.equals("UddiPublishURL")) {
				String value = prop.getProperty(property);
				if (value!=null) {
					value = value.trim();
					this.publishURL = value;
				}
			}
			if (property.equals("UddiUser")) {
				String value = prop.getProperty(property);
				if (value!=null) {
					value = value.trim();
					this.user = value;
				}
			}
			if (property.equals("UddiPassword")) {
				String value = prop.getProperty(property);
				if (value!=null) {
					value = value.trim();
					this.password = value;
				}
			}
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

//		if (property.equals("sincronizzazioneRegistro")) {
//		String value = prop.getProperty(property);
//		if (value!=null) {
//		value = value.trim();
//		try{
//		engineRegistro = Boolean.parseBoolean(value);
//		}catch(Exception e){
//		log.error("Errore durante la lettura della proprieta': sincronizzazioneRegistro");
//		}
//		}
//		}

		if( this.inquiryURL==null ){
			throw new Exception("InquiryURL non fornita.");
		}
		if( this.publishURL==null ){
			throw new Exception("PublishURL non fornita.");
		}
		if( this.urlPrefix==null ){
			throw new Exception("URLPrefix non fornito.");
		}
		if( this.pathPrefix==null ){
			throw new Exception("PathPrefix non fornito.");
		}


		//Init Driver
		return new DriverRegistroServiziUDDI(this.inquiryURL,this.publishURL,this.user,this.password,this.urlPrefix,this.pathPrefix,null);

	}

}


