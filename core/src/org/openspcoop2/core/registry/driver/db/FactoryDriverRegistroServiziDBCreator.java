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



package org.openspcoop2.core.registry.driver.db;

import java.util.Enumeration;
import java.util.Properties;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.FactoryDriverCreator;
import org.openspcoop2.core.commons.IDriverWS;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;

/**
 *
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FactoryDriverRegistroServiziDBCreator extends FactoryDriverCreator {

	private String jndiName;
	private String tipoDatabase;
	private Properties jndiProp;


	public FactoryDriverRegistroServiziDBCreator(String fileProperties) {
		super(fileProperties);

	}

	/**
	 * Ritorna una nuova istanza del {@link DriverRegistroServiziDB}
	 */
	@Override
	public IDriverWS getDriver() throws Exception {

		//Leggo le informazioni dal file properties
		Properties prop = readProperties(super.getFilePropertiesName());

		Enumeration<?> en = prop.propertyNames();
		while (en.hasMoreElements()) {
			String property = (String) en.nextElement();
			if (property.equals("dataSource")) {
				String value = prop.getProperty(property);
				if (value!=null) {
					value = value.trim();
					this.jndiName = value;
				}
			}
			if (property.startsWith("dataSource.property.")) {
				String key = (property.substring("dataSource.property.".length()));
				if (key != null)
					key = key.trim();
				String value = prop.getProperty(property);
				if (value!=null)
					value = value.trim();
				if (key!=null && value!=null)
					this.jndiProp.setProperty(key,value);
			}
		}
		
		// leggo tipoDatabase
		if(this.jndiName.indexOf("@")!=-1){
			// estrazione tipo database
			try{
				this.tipoDatabase = DBUtils.estraiTipoDatabaseFromLocation(this.jndiName);
				this.jndiName = this.jndiName.substring(this.jndiName.indexOf("@")+1);
			}catch(Exception e){
				throw new Exception("Analisi del tipo di database (tipoDatabase@datasource) non riuscita: "+e.getMessage());
			}
		}else{
			// Leggo come proprieta'
			this.tipoDatabase = prop.getProperty("tipoDatabase");
			if(this.tipoDatabase==null){
				throw new Exception("Il registro dei servizi di tipo ["+CostantiConfigurazione.REGISTRO_DB+"] richiede la definizione del tipo di database indicato o come prefisso della location (tipoDB@datasource) o attraverso la proprieta' 'tipoDatabase'");
			}else{
				this.tipoDatabase = this.tipoDatabase.trim();
			}
		}

		return new DriverRegistroServiziDB(this.jndiName,this.jndiProp,this.tipoDatabase);

	}

}


