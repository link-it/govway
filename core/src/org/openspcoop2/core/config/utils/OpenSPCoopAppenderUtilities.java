package org.openspcoop2.core.config.utils;

import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.config.Property;

public class OpenSPCoopAppenderUtilities {

	public static void addParameters(Logger log,List<Property> appenderProperties,
			String dsJndiName,
			String connectionUrl, String connectionDriver, String connectionUsername, String connectionPassword,
			String tipoDatabase
			) throws Exception{
		
		
		if(dsJndiName!=null){
			Property prop_ds = new Property();
			prop_ds.setNome("datasource");
			prop_ds.setValore(dsJndiName);
			appenderProperties.add(prop_ds);
		}
		else if(connectionUrl!=null){
			Property prop_connectionUrl = new Property();
			prop_connectionUrl.setNome("connectionUrl");
			prop_connectionUrl.setValore(connectionUrl);
			appenderProperties.add(prop_connectionUrl);
			
			Property prop_driver = new Property();
			prop_driver.setNome("connectionDriver");
			prop_driver.setValore(connectionDriver);
			appenderProperties.add(prop_driver);
			
			Property prop_username = new Property();
			prop_username.setNome("connectionUsername");
			prop_username.setValore(connectionUsername);
			appenderProperties.add(prop_username);
			
			Property prop_password = new Property();
			prop_password.setNome("connectionPassword");
			prop_password.setValore(connectionPassword);
			appenderProperties.add(prop_password);
		}
			
		Property prop_tipoDatabase = new Property();
		prop_tipoDatabase.setNome("tipoDatabase");
		prop_tipoDatabase.setValore(tipoDatabase);
		appenderProperties.add(prop_tipoDatabase);

	}
	
}
