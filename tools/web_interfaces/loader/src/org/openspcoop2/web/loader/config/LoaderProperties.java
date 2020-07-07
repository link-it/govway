/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.web.loader.config;


import java.util.Properties;

import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;


/**
* ConsoleProperties
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/


public class LoaderProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'loader.properties' */
	private LoaderInstanceProperties reader;

	/** Copia Statica */
	private static LoaderProperties loaderProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public LoaderProperties(String confDir,Logger log) throws Exception {

		if(log!=null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger(LoaderProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = LoaderProperties.class.getResourceAsStream("/loader.properties");
			if(properties==null){
				throw new Exception("File '/loader.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'loader.properties': \n\n"+e.getMessage());
		    throw new Exception("ConsoleProperties initialize error: "+e.getMessage());
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Exception er){}
		}

		this.reader = new LoaderInstanceProperties(propertiesReader, this.log, confDir);
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String confDir,Logger log){

		try {
		    LoaderProperties.loaderProperties = new LoaderProperties(confDir,log);	
		    return true;
		}
		catch(Exception e) {
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di ClassNameProperties
	 * 
	 */
	public static LoaderProperties getInstance() throws OpenSPCoop2ConfigurationException{
		if(LoaderProperties.loaderProperties==null){
	    	throw new OpenSPCoop2ConfigurationException("LoaderProperties non inizializzato");
	    }
	    return LoaderProperties.loaderProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		LoaderProperties.loaderProperties.reader.setLocalObjectImplementation(prop);
	}








	/* ********  M E T O D I  ******** */

	private String readProperty(boolean required,String property) throws UtilsException{
		String tmp = this.reader.getValue_convertEnvProperties(property);
		if(tmp==null){
			if(required){
				throw new UtilsException("Property ["+property+"] not found");
			}
			else{
				return null;
			}
		}else{
			return tmp.trim();
		}
	}
	private Boolean readBooleanProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		if("true".equalsIgnoreCase(tmp)==false && "false".equalsIgnoreCase(tmp)==false){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (true/value expected)");
		}
		return Boolean.parseBoolean(tmp);
	}
	

	
	/* ----- Funzionalità Generiche -------- */
	
	public String getProtocolloDefault() throws UtilsException{
		return this.readProperty(false, "protocolloDefault");
	}
	
	public Boolean isAutenticazioneUtenti_UtilizzaDabaseRegistro() throws UtilsException{
		return this.readBooleanProperty(false, "user.searchDatabaseRegistro");
	}
	
	public String getConsoleUtenzePassword() throws UtilsException{
		return this.readProperty(true, "user.password");
	}
	
	public String getNomePddOperativaConsoleSinglePdDMode() throws UtilsException{
		return this.readProperty(false, "nomePddOperativa_CtrlstatSinglePdD");
	}
	
	public String getTipoPortaDiDominio() throws UtilsException{
		return this.readProperty(false, "tipoPdD");
	}
	
	public Boolean isGestioneSoggetti() throws UtilsException{
		return this.readBooleanProperty(true, "gestioneSoggetti");
	}
	
	public Boolean isMantieniFruitoriServiziEsistenti() throws UtilsException{
		return this.readBooleanProperty(true, "mantieniFruitoriServiziEsistenti");
	}
	
	public StatiAccordo getStatoAccordi() throws UtilsException{
		String tmp = this.readProperty(true, "statoAccordi");
		if(!StatiAccordo.bozza.toString().equals(tmp) && !StatiAccordo.operativo.toString().equals(tmp) && !StatiAccordo.finale.toString().equals(tmp)){
			String errorMsg = "Opzione 'statoAccordi' non valida ("+tmp+"), valori possibili sono: "+StatiAccordo.bozza.toString()+","+StatiAccordo.operativo.toString()+","+StatiAccordo.finale.toString();
			throw new UtilsException(errorMsg);
		}
		return StatiAccordo.valueOf(tmp);
	}

	
	
	/* ----- Impostazioni grafiche ------- */
	
	public String getConsoleNomeSintesi() throws UtilsException{
		return this.readProperty(true, "loader.nome.sintesi");
	}
	
	public String getConsoleNomeEsteso() throws UtilsException{
		return this.readProperty(true, "loader.nome.esteso");
	}
	
	public String getConsoleCSS() throws UtilsException{
		return this.readProperty(true, "loader.css");
	}
	
	public String getConsoleLanguage() throws UtilsException{
		return this.readProperty(true, "loader.language"); 
	}
	
	public int getConsoleLunghezzaLabel() throws UtilsException{
		String lunghezzaS = this.readProperty(true, "loader.lunghezzaLabel");
		return Integer.parseInt(lunghezzaS); 
	}
	
	public String getLogoHeaderImage() throws Exception{
		return this.readProperty(false,"console.header.logo.image");
	}

	public String getLogoHeaderTitolo() throws Exception{
		return this.readProperty(false,"console.header.logo.titolo");
	}

	public String getLogoHeaderLink() throws Exception{
		return this.readProperty(false,"console.header.logo.link");
	}
}
