/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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


package org.openspcoop2.pdd.core.batch;

import java.io.InputStream;
import java.util.Properties;

/**
* GeneratorProperties
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class GeneratorProperties {
	
	private static GeneratorProperties staticInstance = null;
	private static synchronized void init() throws Exception{
		if(GeneratorProperties.staticInstance == null){
			GeneratorProperties.staticInstance = new GeneratorProperties();
		}
	}
	public static GeneratorProperties getInstance() throws Exception{
		if(GeneratorProperties.staticInstance == null){
			GeneratorProperties.init();
		}
		return GeneratorProperties.staticInstance;
	}
	
	
	
	
	private static String PROPERTIES_FILE = "/batch-runtime-repository.properties";
	
	private String protocolloDefault;
	
	private int refreshConnessione;
	
	private int scadenzaMessaggiMinuti;

	private boolean messaggiDebug;
	private boolean messaggiLogQuery;
	private int messaggiFinestraSecondi;
	
	private String repositoryBuste;
	private boolean useDataRegistrazione;
	

	public GeneratorProperties() throws Exception {

		Properties props = new Properties();
		try {
			InputStream is = GeneratorProperties.class.getResourceAsStream(GeneratorProperties.PROPERTIES_FILE);
			props.load(is);
		} catch(Exception e) {
			throw new Exception("Errore durante l'init delle properties", e);
		}
		
		// PROPERTIES
				
		this.protocolloDefault = this.getProperty(props, "protocolloDefault", true);
	
		this.refreshConnessione = this.getIntProperty(props, "connectionRefresh.secondi", true);
		this.scadenzaMessaggiMinuti = this.getIntProperty(props, "repository.scadenzaMessaggio.minuti", false);
		
		this.messaggiDebug = this.getBooleanProperty(props, "repository.debug", true);
		this.messaggiLogQuery = this.getBooleanProperty(props, "repository.logQuery", true);
		this.messaggiFinestraSecondi = this.getIntProperty(props, "repository.finestra.secondi", true);

		this.repositoryBuste = this.getProperty(props, "repository.gestoreBuste", true);
		this.useDataRegistrazione = this.getBooleanProperty(props, "repository.gestoreBuste.dataRegistrazione", true);
		
	}
	
	private String getProperty(Properties props,String name,boolean required) throws Exception{
		String tmp = props.getProperty(name);
		if(tmp==null){
			if(required){
				throw new Exception("Property '"+name+"' not found");
			}
			else{
				return null;
			}
		}
		else{
			return tmp.trim();
		}
	}
	private boolean getBooleanProperty(Properties props,String name,boolean required) throws Exception{
		String tmp = this.getProperty(props, name, required);
		if(tmp!=null){
			try{
				return Boolean.parseBoolean(tmp);
			}catch(Exception e){
				throw new Exception("Property '"+name+"' wrong int format: "+e.getMessage());
			}
		}
		else{
			return false;
		}
	}
	private int getIntProperty(Properties props,String name,boolean required) throws Exception{
		String tmp = this.getProperty(props, name, required);
		if(tmp!=null){
			try{
				return Integer.valueOf(tmp);
			}catch(Exception e){
				throw new Exception("Property '"+name+"' wrong int format: "+e.getMessage());
			}
		}
		else{
			return -1;
		}
	}
	
	
	public String getProtocolloDefault() {
		return this.protocolloDefault;
	}
	
	public int getRefreshConnessione() {
		return this.refreshConnessione;
	}
	
	public int getScadenzaMessaggiMinuti() {
		return this.scadenzaMessaggiMinuti;
	}
	
	public boolean isMessaggiDebug() {
		return this.messaggiDebug;
	}
	public boolean isMessaggiLogQuery() {
		return this.messaggiLogQuery;
	}
	public int getMessaggiFinestraSecondi() {
		return this.messaggiFinestraSecondi;
	}
	
	public String getRepositoryBuste() {
		return this.repositoryBuste;
	}
	public boolean isUseDataRegistrazione() {
		return this.useDataRegistrazione;
	}
}
