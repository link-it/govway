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



package org.openspcoop2.core.config.driver.db;

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
public class FactoryDriverConfigurazioneDBCreator extends FactoryDriverCreator {

	private String jndiName;
	private String tipoDatabase;
	private Properties jndiProp;

	/**
	 * Ritorna una nuova istanza del {@link DriverConfigurazioneDB}
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
				throw new Exception("La configurazione di tipo ["+CostantiConfigurazione.CONFIGURAZIONE_DB+"] richiede la definizione del tipo di database indicato o come prefisso della location (tipoDB@datasource) o attraverso la proprieta' 'tipoDatabase'");
			}else{
				this.tipoDatabase = this.tipoDatabase.trim();
			}
		}
		
		return new DriverConfigurazioneDB(this.jndiName,this.jndiProp,this.tipoDatabase);

	}


	public FactoryDriverConfigurazioneDBCreator(String fileProperties)
	{
		super(fileProperties);
	}

}


