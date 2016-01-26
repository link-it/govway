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


package org.openspcoop2.utils.datasource.test;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.datasource.DataSource;
import org.openspcoop2.utils.datasource.DataSourceFactory;
import org.openspcoop2.utils.datasource.DataSourceParams;

/**
 * Esempio di inizializzazione
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class ExampleServletInitListener implements ServletContextListener {

	private static boolean initialized = false;
	public static boolean isInitialized() {
		return ExampleServletInitListener.initialized;
	}
	
	public final static String ID_APPLICATIVO_RUNTIME = "Esempio1RuntimeProdotto";
	public final static String ID_APPLICATIVO_CONFIGURAZIONE = "Esempio2ConfigurazioneProdotto";
	public static String UUID_RUNTIME = null;
	public static String UUID_CONFIGURAZIONE = null;
	public static String JNDI_NAME_RUNTIME = null;
	public static String JNDI_NAME_CONFIGURAZIONE = null;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		TipiDatabase databaseType = null;
		
		String datasourceJndiNameExample1 = null;
		Properties datasourceJndiContextExample1 = null;
		
		String datasourceJndiNameExample2 = null;
		Properties datasourceJndiContextExample2 = null;
		
		try{
			InputStream is = ExampleServletInitListener.class.getResourceAsStream("/example.datasource.properties");
			try{
				if(is!=null){
					Properties p = new Properties();
					p.load(is);
					
					String tmpDatabaseType = p.getProperty("databaseType");
					if(tmpDatabaseType!=null){
						tmpDatabaseType = tmpDatabaseType.trim();
						databaseType = TipiDatabase.toEnumConstant(tmpDatabaseType);
						if(databaseType==null){
							throw new RuntimeException("Property [databaseType] with wrong value ["+tmpDatabaseType+"]");
						}
					}
					else{
						throw new RuntimeException("Property [databaseType] not found");
					}
					
					datasourceJndiNameExample1 = p.getProperty("example1.datasource.jndiName");
					if(datasourceJndiNameExample1!=null){
						datasourceJndiNameExample1 = datasourceJndiNameExample1.trim();
						JNDI_NAME_RUNTIME = datasourceJndiNameExample1;
					}
					else{
						throw new RuntimeException("Property [example1.datasource.jndiName] not found");
					}
					
					datasourceJndiContextExample1 = Utilities.readProperties("example1.datasource.jndiContext.", p);
					
					datasourceJndiNameExample2 = p.getProperty("example2.datasource.jndiName");
					if(datasourceJndiNameExample2!=null){
						datasourceJndiNameExample2 = datasourceJndiNameExample2.trim();
						JNDI_NAME_CONFIGURAZIONE = datasourceJndiNameExample2;
					}
					else{
						throw new RuntimeException("Property [example2.datasource.jndiName] not found");
					}
					
					datasourceJndiContextExample2 = Utilities.readProperties("example2.datasource.jndiContext.", p);
				}
				else{
					throw new RuntimeException("Property [example.datasource.properties] not found");
				}
			}finally{
				try{
					if(is!=null){
						is.close();
					}
				}catch(Exception eClose){}
			}

		}catch(Exception e){}
				
		try{
			DataSourceParams dsParams = new DataSourceParams();
			dsParams.setBindJmx(true);
			dsParams.setWrapOriginalMethods(true); // per poter usare anche getConnection e getConnection(String,String)
			dsParams.setDatabaseType(databaseType);
			
			System.out.println("Inizializzo datasource example 1 ...");
			dsParams.setApplicativeId(ID_APPLICATIVO_RUNTIME);
			DataSource dsExample1 = DataSourceFactory.newInstance(datasourceJndiNameExample1, datasourceJndiContextExample1, dsParams);
			UUID_RUNTIME = dsExample1.getUuidDatasource();
			System.out.println("Inizializzazione datasource example 1 effettuata con uuid: "+UUID_RUNTIME);
			
			System.out.println("Inizializzo datasource example 2 ...");
			dsParams.setApplicativeId(ID_APPLICATIVO_CONFIGURAZIONE);
			DataSource dsExample2 = DataSourceFactory.newInstance(datasourceJndiNameExample2, datasourceJndiContextExample2, dsParams);
			UUID_CONFIGURAZIONE = dsExample2.getUuidDatasource();
			System.out.println("Inizializzazione datasource example 2 effettuata con uuid: "+UUID_CONFIGURAZIONE);
			
			
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		
		initialized = true;
		
	}

	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("Undeploy in corso...");
		
		ExampleServletInitListener.initialized = false;
	
		UUID_RUNTIME = null;
		UUID_CONFIGURAZIONE = null;
		
		try{
			DataSourceFactory.closeResources();
			System.out.println("Undeploy effettuato.");
		}catch(Exception e){
			System.err.println("Undeploy effettuato con errore: "+e.getMessage());
			e.printStackTrace(System.err);
		}

	}
}
