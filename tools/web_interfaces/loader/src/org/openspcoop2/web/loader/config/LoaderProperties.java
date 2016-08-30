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



package org.openspcoop2.web.loader.config;


import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;


/**
* ConsoleProperties
*
* @author Andrea Poli <apoli@link.it>
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
	
	public String getConsoleNomeEstesoSuffix() throws UtilsException{
		if(_consoleReadSuffix==null){
			LoaderProperties.initConsoleNomeEstesoSuffix(this);
		}
		return _consoleSuffix;
	}
	
	private static String _consoleSuffix = null;
	private static Boolean _consoleReadSuffix = null;
	private static synchronized void initConsoleNomeEstesoSuffix(LoaderProperties p) throws UtilsException{
		if(_consoleReadSuffix==null){
			
			_consoleReadSuffix = true;
			
			String classLicenseValidator = p.readProperty(false, "loader.licenseValidator");
			String classLicenseValidatorFactory = p.readProperty(false, "loader.licenseValidator.factory");
			if(classLicenseValidator == null || classLicenseValidatorFactory==null){
				return;
			}
			else{
				Class<?> cFactory = null;
				try{
					cFactory = Class.forName(classLicenseValidatorFactory);
				}catch(Exception e){
					throw new UtilsException("[ClassForNameFactory] "+e.getMessage(),e);
				}
				Method mFactory = null;
				try{
					Method [] ms =cFactory.getMethods();
					if(ms==null || ms.length<=0){
						throw new Exception("Non esistono metodi [Factory]");
					}
					for (int i = 0; i < ms.length; i++) {
						
						if("getInstance".equals(ms[i].getName())==false){
							continue;
						}
						
						Type[]types = ms[i].getGenericParameterTypes();
	
						if(types!=null && types.length==1){
							if( (types[0].toString().equals("class org.slf4j.Logger")) ){
								mFactory = ms[i];
								break;
							}
						}
	
					}
	
					if(mFactory==null){
						throw new Exception("Metodo Factory non trovato");
					}
	
				}catch(Exception e){
					throw new UtilsException("[getMethodFactory] "+e.getMessage(),e);
				}
				Object oFactory = null;
				try{
					oFactory = mFactory.invoke(null, p.log);
				}catch(Exception e){
					throw new UtilsException("[invokeFactory] "+e.getMessage(),e);
				}
				if(oFactory==null){
					throw new UtilsException("Factory not found");
				}
				Class<?> c = null;
				try{
					c = Class.forName(classLicenseValidator);
				}catch(Exception e){
					throw new UtilsException("[ClassForName] "+e.getMessage(),e);
				}
				Method m = null;
				try{
					Method [] ms =c.getMethods();
					if(ms==null || ms.length<=0){
						throw new Exception("Non esistono metodi");
					}
					for (int i = 0; i < ms.length; i++) {
						Type[]types = ms[i].getGenericParameterTypes();
	
						if(types!=null && types.length==3){
							if( (types[0].toString().equals("class org.slf4j.Logger")) && 
									(types[1].toString().equals("class "+classLicenseValidatorFactory)) && 
									(types[2].toString().equals("boolean")) ){
								m = ms[i];
								break;
							}
						}
	
					}
	
					if(m==null){
						throw new Exception("Metodo non trovato");
					}
	
				}catch(Exception e){
					throw new UtilsException("[getMethod] "+e.getMessage(),e);
				}
	
				Object o = null;
				try{
					o = m.invoke(null, p.log, oFactory,true);
				}catch(Exception e){
					throw new UtilsException("[invoke] "+e.getMessage(),e);
				}
				if(o==null){
					throw new UtilsException("License not found");
				}
				// Recupero titolo
				Method mTitolo = null;
				try{
					mTitolo = o.getClass().getMethod("getTitlePddMonitor");
				}catch(Exception e){
					throw new UtilsException("[getMethod_TitoloPddMonitor] "+e.getMessage(),e);
				}
				Object oTitoloPddMonitor = null;
				try{
					oTitoloPddMonitor = mTitolo.invoke(o);
				}catch(Exception e){
					throw new UtilsException("[invoke_TitoloPddMonitor] "+e.getMessage(),e);
				}
				if(oTitoloPddMonitor==null){
					throw new UtilsException("TitoloPddMonitor not found");
				}
				if( !(oTitoloPddMonitor instanceof String) ){
					throw new UtilsException("TitoloPddMonitor with wrong type ["+oTitoloPddMonitor.getClass().getName()+"], excepected: "+String.class.getName());
				}
				_consoleSuffix = (String) oTitoloPddMonitor;
				return;
			}
		}
	}
	
	public String getConsoleCSS() throws UtilsException{
		return this.readProperty(true, "loader.css");
	}
	
	public String getConsoleImmagineNomeApplicazione() throws UtilsException{
		return this.readProperty(true, "loader.img.nomeApplicazione");
	}
	
	public String getConsoleLanguage() throws UtilsException{
		return this.readProperty(true, "loader.language");
	}
	
	public Boolean isUsaConsoleImmagineNomeApplicazione() throws UtilsException{
		return this.readBooleanProperty(true, "loader.usaImgNomeApplicazione");
	}
	
}
