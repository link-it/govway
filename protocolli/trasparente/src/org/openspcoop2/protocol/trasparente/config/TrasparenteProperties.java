/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.protocol.trasparente.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.trasparente.config.TrasparenteProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;

/**
 * Classe che gestisce il file di properties 'trasparente.properties' del protocollo Trasparente
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasparenteProperties {

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;


	/** Copia Statica */
	private static TrasparenteProperties trasparenteProperties = null;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'trasparente.properties' */
	private TrasparenteInstanceProperties reader;





	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public TrasparenteProperties(String confDir,Logger log) throws ProtocolException{

		if(log != null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger("TrasparenteProperties");

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = TrasparenteProperties.class.getResourceAsStream("/trasparente.properties");
			if(properties==null){
				throw new Exception("File '/trasparente.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'trasparente.properties': "+e.getMessage());
			throw new ProtocolException("TrasparenteProperties initialize error: "+e.getMessage(),e);
		}finally{
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
		}
		try{
			this.reader = new TrasparenteInstanceProperties(propertiesReader, this.log);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}

	}

	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static synchronized void initialize(String confDir,Logger log) throws ProtocolException{

		if(TrasparenteProperties.trasparenteProperties==null)
			TrasparenteProperties.trasparenteProperties = new TrasparenteProperties(confDir,log);	

	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di OpenTrasparenteProperties
	 * @throws Exception 
	 * 
	 */
	public static TrasparenteProperties getInstance(Logger log) throws ProtocolException{

		if(TrasparenteProperties.trasparenteProperties==null)
			throw new ProtocolException("TrasparenteProperties not initialized (use init method in factory)");

		return TrasparenteProperties.trasparenteProperties;
	}




	public void validaConfigurazione(Loader loader) throws ProtocolException  {	
		try{  

			generateIDasUUID();
			
			this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
			this.isAggiungiDetailErroreApplicativo_SoapFaultPdD();

		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la validazione della proprieta' del protocollo trasparente, "+e.getMessage();
			this.log.error(msg,e);
			throw new ProtocolException(msg,e);
		}
	}


	/**
	 * Esempio di read property
	 *   
	 * @return Valore della property
	 * 
	 */
	private static Boolean generateIDasUUID = null;
	public Boolean generateIDasUUID(){
		if(TrasparenteProperties.generateIDasUUID==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.trasparente.id.uuid";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					TrasparenteProperties.generateIDasUUID = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					TrasparenteProperties.generateIDasUUID = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				TrasparenteProperties.generateIDasUUID = defaultValue;
			}
		}

		return TrasparenteProperties.generateIDasUUID;
	}
	
	
	
	
	
	
	/* **** SOAP FAULT **** */
	
    /**
     * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
     *   
     * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
     * 
     */
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo= null;
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead= null;
    public Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo(){
    	if(TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.erroreApplicativo.faultApplicativo.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = null;
				}
				
				TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = null;
				
				TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
			}
    	}
    	
    	return TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo;
	}
    
    /**
     * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
     *   
     * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
     * 
     */
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdD= null;
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdDRead= null;
    public Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdD(){
    	if(TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.erroreApplicativo.faultPdD.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails)");
					TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = null;
				}
				
				TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails), errore:"+e.getMessage());
				TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = null;
				
				TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
			}
    	}
    	
    	return TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD;
	}

}
