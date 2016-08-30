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



package org.openspcoop2.pdd.logger;


import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.MsgDiagnosticiInstanceProperties;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.PropertiesReader;


/**
 * Questo file permette di configurare i piu' importanti messaggi diagnostici emessi dalla porta di dominio
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class MsgDiagnosticiProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'openspcoop2.msgDiagnostici.properties' */
	private MsgDiagnosticiInstanceProperties reader;

	/** Copia Statica */
	private static MsgDiagnosticiProperties msgDiagnosticiProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public MsgDiagnosticiProperties(String location,String confDir) throws Exception {

		if(OpenSPCoop2Startup.initialize)
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		else
			this.log = LoggerWrapperFactory.getLogger(MsgDiagnosticiProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader  = new Properties();
		java.io.InputStream properties = null;
		try{  
			if(location!=null){
				properties = new FileInputStream(location);
			}else{
				properties = MsgDiagnosticiProperties.class.getResourceAsStream("/openspcoop2.msgDiagnostici.properties");
			}
			if(properties==null){
				throw new Exception("File '/openspcoop2.msgDiagnostici.properties' not found");
			}
			propertiesReader.load(properties);		    
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'openspcoop2.msgDiagnostici.properties': \n\n"+e.getMessage());
			throw new Exception("MsgDiagnosticiProperties initialize error: "+e.getMessage());
		}finally{
			try{
				if(properties!=null)
				    properties.close();
			}catch(Exception er){}
		}

		this.reader = new MsgDiagnosticiInstanceProperties(propertiesReader, this.log, confDir);
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String location,String confDir){

		try {
			if(MsgDiagnosticiProperties.msgDiagnosticiProperties == null){
				_initialize(location,confDir);
			}
		    return true;
		}
		catch(Exception e) {
		    return false;
		}
	}
	private static synchronized void _initialize(String location,String confDir) throws Exception{
		if(MsgDiagnosticiProperties.msgDiagnosticiProperties == null){
			MsgDiagnosticiProperties.msgDiagnosticiProperties = new MsgDiagnosticiProperties(location,confDir);
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di ClassNameProperties
	 * 
	 */
	public static MsgDiagnosticiProperties getInstance() {
	   return MsgDiagnosticiProperties.msgDiagnosticiProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		MsgDiagnosticiProperties.msgDiagnosticiProperties.reader.setLocalObjectImplementation(prop);
	}



	
	



	/*---------- Inizializzazione (Usata da PddInterceptor) -------------*/
//	public static boolean initialize(){
//
//		try {
//			if(MsgDiagnosticiProperties.msgDiagnosticiProperties == null){
//				_initialize(null,null);
//			}
//			return true;
//		}
//		catch(Exception e) {
//			return false;
//		}
//	}

	




	/*---------- Gestione livello di filtro -------------*/
	
	private static Integer filtroMsgDiagnostico_OpenSPCoop2_0 = null;
	public int getFiltroMsgDiagnostico_OpenSPCoop2_0() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_0==null){
			try{ 
				String value = null;
				value = this.reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.0");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_0=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_FATAL);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_0=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_0 < 0 || MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_0 > 9999 ){
						throw new Exception("Il valore della proprieta' deve essere compreso nell'intervallo [0,9999]");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.0': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_0=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_FATAL);
			}  
		}
		
		return MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_0;
	}
	
	private static Integer filtroMsgDiagnostico_OpenSPCoop2_1 = null;
	public int getFiltroMsgDiagnostico_OpenSPCoop2_1() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_1==null){
			try{ 
				String value = null;
				value = this.reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.1");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_1=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_PROTOCOL);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_1=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_1 < 1 || MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_1 > 19999 ){
						throw new Exception("Il valore della proprieta' deve essere compreso nell'intervallo [1,19999]");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.1': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_1=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_PROTOCOL);
			}  
		}
		
		return MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_1;
	}
	
	private static Integer filtroMsgDiagnostico_OpenSPCoop2_2 = null;
	public int getFiltroMsgDiagnostico_OpenSPCoop2_2() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_2==null){
			try{ 
				String value = null;
				value = this.reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.2");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_2=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_INTEGRATION);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_2=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_2 < 10001 || MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_2 > 29999 ){
						throw new Exception("Il valore della proprieta' deve essere compreso nell'intervallo [10001,29999]");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.2': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_2=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_INTEGRATION);
			}  
		}
		
		return MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_2;
	}
	
	
	private static Integer filtroMsgDiagnostico_OpenSPCoop2_3 = null;
	public int getFiltroMsgDiagnostico_OpenSPCoop2_3() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_3==null){
			try{ 
				String value = null;
				value = this.reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.3");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_3=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_PROTOCOL);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_3=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_3 < 20001 || MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_3 > 39999 ){
						throw new Exception("Il valore della proprieta' deve essere compreso nell'intervallo [20001,39999]");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.3': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_3=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_PROTOCOL);
			}  
		}
		
		return MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_3;
	}
	
	
	private static Integer filtroMsgDiagnostico_OpenSPCoop2_4 = null;
	public int getFiltroMsgDiagnostico_OpenSPCoop2_4() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_4==null){
			try{ 
				String value = null;
				value = this.reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.4");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_4=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_INTEGRATION);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_4=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_4 < 30001 || MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_4 > 49999 ){
						throw new Exception("Il valore della proprieta' deve essere compreso nell'intervallo [30001,49999]");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.4': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_4=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_INTEGRATION);
			}  
		}
		
		return MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_4;
	}
	
	private static Integer filtroMsgDiagnostico_OpenSPCoop2_5 = null;
	public int getFiltroMsgDiagnostico_OpenSPCoop2_5() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_5==null){
			try{ 
				String value = null;
				value = this.reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.5");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_5=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_LOW);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_5=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_5 < 40001 || MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_5 > 59999 ){
						throw new Exception("Il valore della proprieta' deve essere compreso nell'intervallo [40001,59999]");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.5': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_5=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_LOW);
			}  
		}
		
		return MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_5;
	}
	
	
	private static Integer filtroMsgDiagnostico_OpenSPCoop2_6 = null;
	public int getFiltroMsgDiagnostico_OpenSPCoop2_6() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_6==null){
			try{ 
				String value = null;
				value = this.reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.6");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_6=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_MEDIUM);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_6=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_6 < 50001 || MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_6 > 69999 ){
						throw new Exception("Il valore della proprieta' deve essere compreso nell'intervallo [50001,69999]");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.6': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_6=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_MEDIUM);
			}  
		}
		
		return MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_6;
	}
	
	
	private static Integer filtroMsgDiagnostico_OpenSPCoop2_7 = null;
	public int getFiltroMsgDiagnostico_OpenSPCoop2_7() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_7==null){
			try{ 
				String value = null;
				value = this.reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.7");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_7=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_HIGH);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_7=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_7 < 60001 || MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_7 > 70000 ){
						throw new Exception("Il valore della proprieta' deve essere compreso nell'intervallo [60001,70000]");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.7': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_7=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_HIGH);
			}  
		}
		
		return MsgDiagnosticiProperties.filtroMsgDiagnostico_OpenSPCoop2_7;
	}
	
	
	public int getValoreFiltroFromValoreOpenSPCoop2(int livello){
		if(livello == LogLevels.SEVERITA_FATAL){
			return this.getFiltroMsgDiagnostico_OpenSPCoop2_0();
		}else if(livello == LogLevels.SEVERITA_ERROR_PROTOCOL){
			return this.getFiltroMsgDiagnostico_OpenSPCoop2_1();
		}else if(livello == LogLevels.SEVERITA_ERROR_INTEGRATION){
			return this.getFiltroMsgDiagnostico_OpenSPCoop2_2();
		}else if(livello == LogLevels.SEVERITA_INFO_PROTOCOL){
			return this.getFiltroMsgDiagnostico_OpenSPCoop2_3();
		}else if(livello == LogLevels.SEVERITA_INFO_INTEGRATION){
			return this.getFiltroMsgDiagnostico_OpenSPCoop2_4();
		}else if(livello == LogLevels.SEVERITA_DEBUG_LOW){
			return this.getFiltroMsgDiagnostico_OpenSPCoop2_5();
		}else if(livello == LogLevels.SEVERITA_DEBUG_MEDIUM){
			return this.getFiltroMsgDiagnostico_OpenSPCoop2_6();
		}else if(livello == LogLevels.SEVERITA_DEBUG_HIGH){
			return this.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		}else{
			return livello;
		}
	}

	
	
	public boolean checkValoriFiltriMsgDiagnostici(Logger log){
		int openspcoop2_0 = this.getFiltroMsgDiagnostico_OpenSPCoop2_0(); 
		if(openspcoop2_0!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_FATAL)){
			log.info("Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 0) al valore openspcoop: "+openspcoop2_0);
		}
		
		int openspcoop2_1 = this.getFiltroMsgDiagnostico_OpenSPCoop2_1(); 
		if(openspcoop2_1<=openspcoop2_0){
			log.error("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 1 (valore: "+openspcoop2_1
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 0 (valore: "+openspcoop2_0+").");
			return false;
		}else{
			if(openspcoop2_1!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_PROTOCOL)){
				log.info("Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 1) al valore openspcoop: "+openspcoop2_1);
			}
		}
		
		int openspcoop2_2 = this.getFiltroMsgDiagnostico_OpenSPCoop2_2(); 
		if(openspcoop2_2<=openspcoop2_1){
			log.error("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 2 (valore: "+openspcoop2_2
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 1 (valore: "+openspcoop2_1+").");
			return false;
		}else{
			if(openspcoop2_2!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_INTEGRATION)){
				log.info("Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 2) al valore openspcoop: "+openspcoop2_2);
			}
		}
		
		int openspcoop2_3 = this.getFiltroMsgDiagnostico_OpenSPCoop2_3(); 
		if(openspcoop2_3<=openspcoop2_2){
			log.error("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 3 (valore: "+openspcoop2_3
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 2 (valore: "+openspcoop2_2+").");
			return false;
		}else{
			if(openspcoop2_3!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_PROTOCOL)){
				log.info("Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 3) al valore openspcoop: "+openspcoop2_3);
			}
		}
		
		int openspcoop2_4 = this.getFiltroMsgDiagnostico_OpenSPCoop2_4(); 
		if(openspcoop2_4<=openspcoop2_3){
			log.error("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 4 (valore: "+openspcoop2_4
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 3 (valore: "+openspcoop2_3+").");
			return false;
		}else{
			if(openspcoop2_4!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_INTEGRATION)){
				log.info("Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 4) al valore openspcoop: "+openspcoop2_4);
			}
		}
	
		int openspcoop2_5 = this.getFiltroMsgDiagnostico_OpenSPCoop2_5(); 
		if(openspcoop2_5<=openspcoop2_4){
			log.error("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 5 (valore: "+openspcoop2_5
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 4 (valore: "+openspcoop2_4+").");
			return false;
		}else{
			if(openspcoop2_5!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_LOW)){
				log.info("Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 5) al valore openspcoop: "+openspcoop2_5);
			}
		}
		
		int openspcoop2_6 = this.getFiltroMsgDiagnostico_OpenSPCoop2_6(); 
		if(openspcoop2_6<=openspcoop2_5){
			log.error("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 6 (valore: "+openspcoop2_6
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 5 (valore: "+openspcoop2_5+").");
			return false;
		}else{
			if(openspcoop2_6!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_MEDIUM)){
				log.info("Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 6) al valore openspcoop: "+openspcoop2_6);
			}
		}
		
		int openspcoop2_7 = this.getFiltroMsgDiagnostico_OpenSPCoop2_7(); 
		if(openspcoop2_7<=openspcoop2_6){
			log.error("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 7 (valore: "+openspcoop2_7
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 6 (valore: "+openspcoop2_6+").");
			return false;
		}else{
			if(openspcoop2_7!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_HIGH)){
				log.info("Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 7) al valore openspcoop: "+openspcoop2_7);
			}
		}
		
		return true;
	}
	
	
	
	
	
	
	/*---------- Inizializzazione messaggi diagnostici -------------*/
	
	private static PropertiesReader livelliMsgDiagnosticiPersonalizzati = null;
	public static PropertiesReader getLivelliMsgDiagnosticiPersonalizzati() {
		return livelliMsgDiagnosticiPersonalizzati;
	}
	private static PropertiesReader codiciMsgDiagnosticiPersonalizzati = null;
	public static PropertiesReader getCodiciMsgDiagnosticiPersonalizzati() {
		return codiciMsgDiagnosticiPersonalizzati;
	}
	private static PropertiesReader messaggiMsgDiagnosticiPersonalizzati = null;	
	public static PropertiesReader getMessaggiMsgDiagnosticiPersonalizzati() {
		return messaggiMsgDiagnosticiPersonalizzati;
	}

	private static PropertiesReader codiciIdentificativiFunzione = null;
	public static PropertiesReader getCodiciIdentificativiFunzione() {
		return codiciIdentificativiFunzione;
	}

	private static PropertiesReader mappingCodiceToKeywordMsgDiagnosticiPersonalizzati = null;
	public static PropertiesReader getMappingCodiceToKeywordMsgDiagnosticiPersonalizzati() {
		return mappingCodiceToKeywordMsgDiagnosticiPersonalizzati;
	}
	
	public synchronized boolean initializeMsgDiagnosticiPersonalizzati(){
		try{
			if(MsgDiagnosticiProperties.livelliMsgDiagnosticiPersonalizzati == null){
				
				Properties tmp_livelliMsgDiagnosticiPersonalizzati = new Properties();
				Properties tmp_codiciMsgDiagnosticiPersonalizzati = new Properties();
				Properties tmp_codiciIdentificativiFunzione = new Properties();
				Properties tmp_messaggiMsgDiagnosticiPersonalizzati = new Properties();
				Properties tmp_mappingCodiceToKeywordMsgDiagnosticiPersonalizzati = new Properties();
				
				
				// Search codiceIdentificativoFunzione
				Properties msgDiagnostici =  this.reader.readProperties(MsgDiagnosticiProperties.PREFIX_MSG_DIAGNOSTICO);
				Enumeration<?> nomi = msgDiagnostici.keys();
				while(nomi.hasMoreElements()){
					String key = (String) nomi.nextElement();	
					if(key.endsWith(MsgDiagnosticiProperties.SUFFIX_CODICE_MODULO_MSG_DIAGNOSTICO)){
						String value = msgDiagnostici.getProperty(key);
						String [] tmpSplit = key.split("\\.");
						String modulo = tmpSplit[0].trim();
						
						// Controllo che il modulo non sia gia' definito
						if(tmp_codiciIdentificativiFunzione.containsKey(modulo)){
							throw new Exception("Per Il modulo funzionale ["+modulo+"] sono stati definiti piu' codici??");
						}
						
						// Controllo che gia non esista il valore associato
						if(tmp_codiciIdentificativiFunzione.containsValue(value)){
							throw new Exception("Il codice fornito ("+modulo+"="+value+") e' gia' utilizzato da un altro modulo funzionale");
						}
						
						// Add
						//System.out.println("AGGIUNTI CODICI ["+modulo+"]=["+value+"]");
						tmp_codiciIdentificativiFunzione.put(modulo, value);
					}
				}

				// Leggo messaggio,livello e codice Diagnostico
				msgDiagnostici = this.reader.readProperties(MsgDiagnosticiProperties.PREFIX_MSG_DIAGNOSTICO);
				nomi = msgDiagnostici.keys();
				while(nomi.hasMoreElements()){
					String key = (String) nomi.nextElement();	
					if(key.endsWith(MsgDiagnosticiProperties.SUFFIX_LIVELLO_MSG_DIAGNOSTICO)){
						String chiave = key.substring(0,(key.length()-(MsgDiagnosticiProperties.SUFFIX_LIVELLO_MSG_DIAGNOSTICO.length()))); 
						String valore = msgDiagnostici.getProperty(key);
						try{
							Integer v = Integer.parseInt(valore);
							v.toString();
							//tmp_livelliMsgDiagnosticiPersonalizzati.put(chiave, v);
							tmp_livelliMsgDiagnosticiPersonalizzati.put(chiave, valore);
							//System.out.println("ADD LIVELLO ["+chiave+"] ["+v+"]");
						}catch(Exception e){
							throw new Exception("Valore della proprieta' ["+key+"] non valido ["+valore+"]: "+e.getMessage());
						}
					}else if(key.endsWith(MsgDiagnosticiProperties.SUFFIX_CODICE_MSG_DIAGNOSTICO)){
						String chiave = key.substring(0,(key.length()-(MsgDiagnosticiProperties.SUFFIX_CODICE_MSG_DIAGNOSTICO.length()))); 
						String valore = msgDiagnostici.getProperty(key);
						String [] tmp = chiave.split("\\.");
						String moduloFunzionale = tmp[0].trim();
						
						// getCodice Modulo Funzionale
						Object codiceModuloFunzionaleObject = tmp_codiciIdentificativiFunzione.get(moduloFunzionale);
						if(codiceModuloFunzionaleObject==null){
							throw new Exception("Per il modulo funzionale ["+moduloFunzionale+"] non e' stato definito il codice");
						}
						String codiceModuloFunzionale = (String) codiceModuloFunzionaleObject;
						
						// Codice definitivo
						String codiceDefinitivo = codiceModuloFunzionale+valore;
						if(tmp_codiciMsgDiagnosticiPersonalizzati.containsValue(codiceDefinitivo)){
							throw new Exception("Proprieta' "+chiave+" contiene un codice ["+valore+"] gia' definito per un altro messaggio diagnostico del modulo funzionale "+moduloFunzionale+" ("+codiceModuloFunzionale+")");
						}
						tmp_codiciMsgDiagnosticiPersonalizzati.put(chiave, codiceDefinitivo);
						tmp_mappingCodiceToKeywordMsgDiagnosticiPersonalizzati.put(codiceDefinitivo,chiave);
						//System.out.println("ADD CODICE ["+chiave+"] ["+codiceDefinitivo+"]");
						
					}else if(key.endsWith(MsgDiagnosticiProperties.SUFFIX_MESSAGGIO_MSG_DIAGNOSTICO)){
						String chiave = key.substring(0,(key.length()-(MsgDiagnosticiProperties.SUFFIX_MESSAGGIO_MSG_DIAGNOSTICO.length()))); 
						String valore = msgDiagnostici.getProperty(key);
						tmp_messaggiMsgDiagnosticiPersonalizzati.put(chiave, valore);
						//System.out.println("ADD MESSAGGIO ["+chiave+"] ["+valore+"]");
					}else{
						if(!key.endsWith(MsgDiagnosticiProperties.SUFFIX_CODICE_MODULO_MSG_DIAGNOSTICO)){
							throw new Exception("Proprieta' malformata ["+key+"]");
						}
					}
				}
				
				// Controllo presenza dei codici diagnostici
				for(int i=0; i<MsgDiagnosticiProperties.MSG_DIAGNOSTICI_PERSONALIZZATI.length; i++){
					String prefix = MsgDiagnosticiProperties.PREFIX_MSG_DIAGNOSTICO+
						MsgDiagnosticiProperties.MSG_DIAGNOSTICI_PERSONALIZZATI[i];
					String livello = prefix + MsgDiagnosticiProperties.SUFFIX_LIVELLO_MSG_DIAGNOSTICO;
					String codice = prefix + MsgDiagnosticiProperties.SUFFIX_CODICE_MSG_DIAGNOSTICO;
					String messaggio = prefix + MsgDiagnosticiProperties.SUFFIX_MESSAGGIO_MSG_DIAGNOSTICO;
					if(tmp_livelliMsgDiagnosticiPersonalizzati.containsKey(MsgDiagnosticiProperties.MSG_DIAGNOSTICI_PERSONALIZZATI[i])==false){
						throw new Exception("Proprieta' "+livello+" non definita");
					}
					if(tmp_codiciMsgDiagnosticiPersonalizzati.containsKey(MsgDiagnosticiProperties.MSG_DIAGNOSTICI_PERSONALIZZATI[i])==false){
						throw new Exception("Proprieta' "+codice+" non definita");
					}
					if(tmp_messaggiMsgDiagnosticiPersonalizzati.containsKey(MsgDiagnosticiProperties.MSG_DIAGNOSTICI_PERSONALIZZATI[i])==false){
						throw new Exception("Proprieta' "+messaggio+" non definita");
					}
				}
				
				
				// inizializzo strutture
				
				MsgDiagnosticiProperties.livelliMsgDiagnosticiPersonalizzati = new PropertiesReader(tmp_livelliMsgDiagnosticiPersonalizzati,true);				
				MsgDiagnosticiProperties.codiciMsgDiagnosticiPersonalizzati = new PropertiesReader(tmp_codiciMsgDiagnosticiPersonalizzati,true);			
				MsgDiagnosticiProperties.messaggiMsgDiagnosticiPersonalizzati = new PropertiesReader(tmp_messaggiMsgDiagnosticiPersonalizzati,true);			
				MsgDiagnosticiProperties.mappingCodiceToKeywordMsgDiagnosticiPersonalizzati = new PropertiesReader(tmp_mappingCodiceToKeywordMsgDiagnosticiPersonalizzati,true);	
				MsgDiagnosticiProperties.codiciIdentificativiFunzione = new PropertiesReader(tmp_codiciIdentificativiFunzione,true);

				
			}
			return true;
		}catch(java.lang.Exception e) {
			this.log.error("Riscontrato errore durante la lettura dei messaggi diagnostici personalizzati: "+e.getMessage());
			if(OpenSPCoop2Logger.getLoggerOpenSPCoopCore()!=null){
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Riscontrato errore durante la lettura dei messaggi diagnostici personalizzati: "+e.getMessage(),e);
			}
			MsgDiagnosticiProperties.livelliMsgDiagnosticiPersonalizzati = null;
			MsgDiagnosticiProperties.messaggiMsgDiagnosticiPersonalizzati = null;
			return false;
		} 	
	}
	
	
	
	
	
	
	
	
	
	
	/*---------- Keyword attese -------------*/
	public final static String MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI="ricezioneContenutiApplicativi.";
	public final static String MSG_DIAG_IMBUSTAMENTO="imbustamentoBusta.";
	public final static String MSG_DIAG_IMBUSTAMENTO_RISPOSTE="imbustamentoRispostaBusta.";
	public final static String MSG_DIAG_INOLTRO_BUSTE="inoltroBuste.";
	public final static String MSG_DIAG_RICEZIONE_BUSTE="ricezioneBuste.";
	public final static String MSG_DIAG_SBUSTAMENTO="sbustamentoBusta.";
	public final static String MSG_DIAG_SBUSTAMENTO_RISPOSTE="sbustamentoRispostaBusta.";
	public final static String MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI="consegnaContenutiApplicativi.";
	public final static String MSG_DIAG_INTEGRATION_MANAGER="integrationManager.";
	public final static String MSG_DIAG_TRACCIAMENTO="tracciamento.";
	public final static String MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE="timerGestoreRiscontriRicevute.";
	public final static String MSG_DIAG_TIMER_GESTORE_MESSAGGI="timerGestoreMessaggi.";
	public final static String MSG_DIAG_TIMER_GESTORE_MESSAGGI_INCONSISTENTI="timerGestoreMessaggiInconsistenti.";
	public final static String MSG_DIAG_TIMER_GESTORE_REPOSITORY_BUSTE="timerGestoreRepositoryBuste.";
	public final static String MSG_DIAG_TIMER_MONITORAGGIO_RISORSE="timerMonitoraggioRisorse.";
	public final static String MSG_DIAG_TIMER_THRESHOLD="timerThreshold.";
	public final static String MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI="timerConsegnaContenutiApplicativi.";
	public final static String MSG_DIAG_OPENSPCOOP_STARTUP="openspcoopStartup.";
	public final static String MSG_DIAG_ALL="all.";
	private final static String[] MSG_DIAGNOSTICI_PERSONALIZZATI = {
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"correlazioneApplicativaEsistente",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"correlazioneApplicativaInstaurata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"ricevutaRichiestaApplicativa",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"consegnaRispostaApplicativa",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"consegnaRispostaApplicativaOkEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"consegnaRispostaApplicativaKoEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"consegnaRispostaApplicativaVuota",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"consegnaRispostaApplicativaFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"timeoutRicezioneRisposta",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"richiestaContenenteBusta",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"portaDelegataNonEsistente",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"servizioApplicativoFruitore.identificazioneTramiteInfoIntegrazioneNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"servizioApplicativoFruitore.identificazioneTramiteCredenziali",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"identificazioneDinamicaServizioNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"servizioApplicativoFruitore.nonAutorizzato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"servizioApplicativoFruitore.contenuto.nonAutorizzato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validazioneContenutiApplicativiRichiestaNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"portaDelegataInvocabilePerRiferimento.riferimentoNonPresente",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"portaDelegataInvocabileNormalmente.riferimentoPresente",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"funzionalitaScartaBodyNonEffettuabile",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"funzionalitaAllegaBodyNonEffettuabile",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"headerIntegrazione.letturaFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"messaggioInGestione",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"messaggioInGestione.marcatoDaEliminare",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"mustUnderstand.unknown",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"contentType.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"mustUnderstand.unknown",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"contentType.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"soapEnvelopeNamespace.versionMismatch",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestoreCredenziali.errore",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestoreCredenziali.nuoveCredenziali",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"contentType.notDefined",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"integrationManager.consegnaRispostaApplicativaEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"integrationManager.consegnaRispostaApplicativaVuota",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"integrationManager.consegnaRispostaApplicativaFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"localForward.logInfo",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"localForward.configError",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"protocolli.tipoSoggetto.fruitore.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"protocolli.tipoSoggetto.erogatore.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"protocolli.tipoServizio.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"ricezioneRichiesta.firstLog",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"ricezioneRichiesta.elaborazioneDati.tipologiaMessaggio",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"ricezioneRichiesta.elaborazioneDati.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"ricezioneRichiesta.elaborazioneDati.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autorizzazioneDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autorizzazioneInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autorizzazioneEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autorizzazioneContenutiApplicativiDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autorizzazioneContenutiApplicativiInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autorizzazioneContenutiApplicativiEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autenticazioneDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autenticazioneInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validazioneContenutiApplicativiRichiestaDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validazioneContenutiApplicativiRichiestaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validazioneContenutiApplicativiRichiestaEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"parsingExceptionRichiesta",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"parsingExceptionRisposta",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"registroServizi.ricercaServizioInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"registroServizi.ricercaServizioEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"registroServizi.ricercaServizioFallita",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"consegnaAffidabile.salvataggioInformazioni",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"consegnaAffidabile.profiloNonOneway",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"consegnaInOrdine.profiloNonOneway",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"consegnaInOrdine.confermaRicezioneNonRichiesta",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"consegnaInOrdine.idCollaborazioneNonRichiesto",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"consegnaInOrdine.funzionalitaMancanti",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"profiloAsincronoSimmetrico.saAnonimo",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"profiloAsincronoSimmetrico.saSenzaRispostaAsincrona",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"profiloAsincronoSimmetrico.servizioCorrelatoNonEsistente",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"profiloAsincronoSimmetrico.rispostaNonCorrelataRichiesta",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"profiloAsincronoAsimmetrico.richiestaStatoNonCorrelataRichiesta",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"profiloAsincronoSimmetrico.risposta.correlazioneRichiesta",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"profiloAsincronoAsimmetrico.richiestaStato.correlazioneRichiesta",
		MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO+"protocolli.funzionalita.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"routingTable.esaminaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"routingTable.esaminaInCorsoFallita",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"routingTable.esaminaEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"routingTable.utilizzoIndirizzoTelematico",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"routingTable.soggettoFruitoreNonGestito",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"inoltroInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"inoltroEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"inoltroConErrore",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"ricezioneMessaggio",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"ricezioneMessaggioErrore",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"ricezioneMessaggioErrore.rollback",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"inoltroBustaScaduta",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"ricezioneSoapFault",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"profiloSincrono.rispostaNonPervenuta",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"validazioneContenutiApplicativiRispostaNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"ricezioneSoapMessage.headerProtocolloNonPresente",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"ricezioneSoapMessage.msgGiaPresente",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"riconsegnaMessaggioPrematura",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"rispostaRicevuta.messaggio",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"rispostaRicevuta.messaggioErrore",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"gestioneConsegnaTerminata",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"headerIntegrazione.letturaFallita",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"profiloAsincrono.rispostaNonPervenuta",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"validazioneContenutiApplicativiRispostaDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"validazioneContenutiApplicativiRispostaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"validazioneContenutiApplicativiRispostaEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"comportamentoAnomalo.erroreConsegna.ricezioneMessaggioDiversoFault",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"mtom.beforeSecurity.processamentoRichiestaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"mtom.afterSecurity.processamentoRichiestaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"mtom.processamentoRichiestaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"mtom.processamentoRichiestaEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"mtom.processamentoRichiestaInErrore",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"mtom.beforeSecurity.processamentoRispostaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"mtom.afterSecurity.processamentoRispostaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"mtom.processamentoRispostaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"mtom.processamentoRispostaEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"mtom.processamentoRispostaInErrore",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"messageSecurity.processamentoRichiestaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"messageSecurity.processamentoRichiestaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"messageSecurity.processamentoRichiestaEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"messageSecurity.processamentoRichiestaInErrore",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"messageSecurity.processamentoRispostaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"messageSecurity.processamentoRispostaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"messageSecurity.processamentoRispostaEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"messageSecurity.processamentoRispostaInErrore",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"validazioneSintattica",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"validazioneSemantica.beforeSecurity",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"validazioneSemantica.afterSecurity",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"ricezioneMessaggio",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"ricezioneMessaggioErrore",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autorizzazioneBusteInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autorizzazioneBusteEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autorizzazioneBusteFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"generazioneMessaggioRisposta",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"generazioneMessaggioErroreRisposta",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"generazioneMessaggioErroreRisposta.destinatarioSconosciuto",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"generazioneRiscontro",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"generazioneRicevutaAsincrona",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"consegnaMessaggioOkEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"consegnaMessaggioKoEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"consegnaMessaggioNonPresente",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"consegnaMessaggioFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"timeoutRicezioneRisposta",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messaggioInGestione.marcatoDaEliminare",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messaggioInGestione.gestioneSincrona",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messaggioInGestione.gestioneAsincrona",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messaggioInGestione.attesaFineProcessamento.filtroDuplicatiAbilitato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messaggioInGestione.attesaFineProcessamento.filtroDuplicatiDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messaggioInGestione.attesaFineProcessamento.filtroDuplicatiDisabilitato.attesaTerminata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messaggioInGestione.attesaFineProcessamento.filtroDuplicatiDisabilitato.forzoEliminazione",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messaggioInGestione.attesaFineProcessamento.timeoutScaduto",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"attesaFineProcessamento.richiestaAsincrona.timeoutScaduto",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"attesaFineProcessamento.ricevutaRichiestaAsincrona.timeoutScaduto",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneContenutiApplicativiRichiestaNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"headerIntegrazione.letturaFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"mustUnderstand.unknown",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"contentType.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"soapEnvelopeNamespace.versionMismatch",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestoreCredenziali.errore",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestoreCredenziali.nuoveCredenziali",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"contentType.notDefined",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"identificazionePAErrore",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"protocolli.tipoSoggetto.fruitore.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"protocolli.tipoSoggetto.erogatore.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"protocolli.tipoServizio.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"ricezioneRichiesta.firstLog",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"ricezioneRichiesta.elaborazioneDati.tipologiaMessaggio",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"ricezioneRichiesta.elaborazioneDati.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"ricezioneRichiesta.elaborazioneDati.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autorizzazioneBusteDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autorizzazioneContenutiBusteDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autorizzazioneContenutiBusteInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autorizzazioneContenutiBusteEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autorizzazioneContenutiBusteFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneContenutiApplicativiRichiestaDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneContenutiApplicativiRichiestaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneContenutiApplicativiRichiestaEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"mtom.beforeSecurity.processamentoRichiestaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"mtom.afterSecurity.processamentoRichiestaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"mtom.processamentoRichiestaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"mtom.processamentoRichiestaEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"mtom.processamentoRichiestaInErrore",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"mtom.beforeSecurity.processamentoRispostaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"mtom.afterSecurity.processamentoRispostaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"mtom.processamentoRispostaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"mtom.processamentoRispostaEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"mtom.processamentoRispostaInErrore",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messageSecurity.processamentoRichiestaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messageSecurity.processamentoRichiestaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messageSecurity.processamentoRichiestaEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messageSecurity.processamentoRichiestaInErrore",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messageSecurity.processamentoRispostaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messageSecurity.processamentoRispostaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messageSecurity.processamentoRispostaEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"messageSecurity.processamentoRispostaInErrore",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneSintattica",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneSemantica.beforeSecurity",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneSemantica.afterSecurity",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"parsingExceptionRichiesta",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"parsingExceptionRisposta",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"ricezioneBustaErrore",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"ricezioneBusta.eccezioniNonGravi",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"validazioneNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"validazioneBustaErrore.listaEccezioniMalformata",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"validazioneBusta.bustaNonCorretta",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"validazioneBusta.eccezioniNonGravi",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"validazioneRicevutaAsincrona",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"ricezioneRiscontro",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"ricezioneBustaServizio",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"ricezioneBustaDuplicata",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"ricezioneBustaDuplicata.count",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"ricezioneBusta.registrazionePerFiltroDuplicati",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"funzionalitaRichiestaAccordo.confermaRicezioneNonPresente",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"funzionalitaRichiestaAccordo.consegnaInOrdineNonPresente",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"consegnaInOrdine.profiloDiversoOneway",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"soggettoDestinatarioNonGestito",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"portaApplicativaNonEsistente",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"ricezioneRispostaSincrona",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"profiloAsincrono.flussoRicevutaRichiestaRispostaNonCorretto",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"profiloAsincronoAsimmetrico.saSenzaRispostaAsincrona",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"profiloAsincronoAsimmetrico.servizioCorrelatoNonEsistente",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"ricezioneBustaErroreDetails",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"protocolli.funzionalita.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"ricezioneBustaErrore",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"ricezioneBusta.eccezioniNonGravi",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"validazioneBustaErrore.listaEccezioniMalformata",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"validazioneBusta.eccezioniNonGravi",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"validazioneBusta.bustaNonCorretta",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"validazioneRicevutaAsincrona",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"ricezioneRiscontro",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"ricezioneBustaServizio",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"ricezioneBustaDuplicata",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"ricezioneBustaDuplicata.count",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"ricezioneBusta.registrazionePerFiltroDuplicati",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"rispostaOneway",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"soggettoDestinatarioNonGestito",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"profiloCollaborazioneRisposta.diversoScenarioGestito",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"riferimentoMessaggioNonValido",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"riferimentoMessaggioNonPresente",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"ricezioneBustaErroreDetails",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE+"protocolli.funzionalita.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"integrationManager.messaggioDisponibile",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"consegnaNonDefinita",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasmissioneSincrona.servizioNonUtilizzabile",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"gestioneProfiloAsincrono.servizioNonUtilizzabile",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"servizioApplicativoNonDefinito",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"gestioneProfiloNonOneway.consegnaVersoNServiziApplicativi",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"gestioneStateless.consegnaVersoNServiziApplicativi",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"gestioneStateless.integrationManager",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"consegnaInOrdine.messaggioFuoriOrdine",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"consegnaInOrdine.messaggioGiaConsegnato",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"consegnaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"consegnaEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"consegnaConErrore",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"ricezioneSoapFault",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"validazioneContenutiApplicativiRispostaNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"funzionalitaScartaBodyNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"funzionalitaAllegaBodyNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"riconsegnaMessaggioPrematura",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"gestioneConsegnaTerminata",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"rispostaContenenteBusta",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"headerIntegrazione.letturaFallita",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"behaviour.servizioApplicativoNonDefinito",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"validazioneContenutiApplicativiRispostaDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"validazioneContenutiApplicativiRispostaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"validazioneContenutiApplicativiRispostaEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"comportamentoAnomalo.erroreConsegna.ricezioneMessaggioDiversoFault",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"logInvocazioneOperazione",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"autenticazioneNonImpostata",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"servizioApplicativo.identificazioneTramiteCredenziali",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"servizioApplicativo.nonAutorizzato",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"mappingRifMsgToIdBusta.nonRiuscito",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"buildMsg.nonRiuscito",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"buildMsg.imbustamentoSOAP.nonRiuscito",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"invocazionePortaDelegata.contenutoApplicativoNonPresente",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"invocazionePortaDelegata.profiloAsincrono.riferimentoMessaggioNonPresente",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"invocazionePortaDelegataPerRiferimento.riferimentoMessaggioNonPresente",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"messaggiNonPresenti",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"messaggioNonTrovato",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"gestoreCredenziali.errore",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"gestoreCredenziali.nuoveCredenziali",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"ricezioneRichiesta.firstLog",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"ricezioneRichiesta.elaborazioneDati.tipologiaMessaggio",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"ricezioneRichiesta.elaborazioneDati.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"ricezioneRichiesta.elaborazioneDati.completata",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"registrazioneNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"registrazioneNonRiuscita.openspcoopAppender",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.registrazioneNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.registrazioneNonRiuscita.openspcoopAppender",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"errore.bloccoServizi",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.errore.bloccoServizi",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE+"avvioInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE+"avvioEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE+"timerGiaAvviato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE+"disabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE+"controlloInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE+"bustaNonRiscontrata",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE+"bustaNonRiscontrataScaduta",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE+"bustaNonRiscontrata.messaggioNonEsistente",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE+"ricevutaAsincronaNonRicevuta",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE+"ricevutaAsincronaNonRicevuta.bustaScaduta",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE+"ricevutaAsincronaNonRicevuta.messaggioNonEsistente",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE+"precedenteEsecuzioneInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE+"precedenteEsecuzioneInCorso.stopTimer",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"avvioInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"avvioEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"timerGiaAvviato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"disabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"controlloInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"ricercaMessaggiDaEliminare",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"eliminazioneMessaggio",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"eliminazioneDestinatarioMessaggio",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"messaggioNonConsumato.codaJMS",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"ricercaCorrelazioniApplicativeScadute",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"ricercaCorrelazioniApplicativeScaduteRispettoOraRegistrazione",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"eliminazioneCorrelazioneApplicativaScaduta",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"precedenteEsecuzioneInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"precedenteEsecuzioneInCorso.stopTimer",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"connessioneScaduta.EliminazioneInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI+"connessioneScaduta.EliminazioneEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI_INCONSISTENTI+"avvioInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI_INCONSISTENTI+"avvioEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI_INCONSISTENTI+"timerGiaAvviato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI_INCONSISTENTI+"disabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI_INCONSISTENTI+"controlloInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI_INCONSISTENTI+"ricercaMessaggiDaEliminare",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI_INCONSISTENTI+"eliminazioneMessaggio",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI_INCONSISTENTI+"precedenteEsecuzioneInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI_INCONSISTENTI+"precedenteEsecuzioneInCorso.stopTimer",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_REPOSITORY_BUSTE+"avvioInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_REPOSITORY_BUSTE+"avvioEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_REPOSITORY_BUSTE+"timerGiaAvviato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_REPOSITORY_BUSTE+"disabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_REPOSITORY_BUSTE+"controlloInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_REPOSITORY_BUSTE+"ricercaMessaggiDaEliminare",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_REPOSITORY_BUSTE+"eliminazioneMessaggio",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_REPOSITORY_BUSTE+"precedenteEsecuzioneInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_REPOSITORY_BUSTE+"precedenteEsecuzioneInCorso.stopTimer",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_MONITORAGGIO_RISORSE+"avvioEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_MONITORAGGIO_RISORSE+"risorsaNonDisponibile",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_MONITORAGGIO_RISORSE+"validazioneSemanticaFallita",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_MONITORAGGIO_RISORSE+"risorsaRitornataDisponibile",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_THRESHOLD+"avvioEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_THRESHOLD+"controlloInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_THRESHOLD+"risorsaNonDisponibile",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_THRESHOLD+"risorsaRitornataDisponibile",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI+"avvioInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI+"avvioEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI+"timerGiaAvviato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI+"disabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI+"controlloInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI+"ricercaMessaggiDaInoltrare",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI+"inoltroMessaggio",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI+"precedenteEsecuzioneInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI+"precedenteEsecuzioneInCorso.stopTimer",
		MsgDiagnosticiProperties.MSG_DIAG_OPENSPCOOP_STARTUP+"pdd",
		MsgDiagnosticiProperties.MSG_DIAG_OPENSPCOOP_STARTUP+"IntegrationManager",
		MsgDiagnosticiProperties.MSG_DIAG_OPENSPCOOP_STARTUP+"erroreGenerico",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"erroreGenerico",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"erroreGenericoMalfunzionamentoPdD",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"transactionManager.validityCheckError",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"connessioneUscita.disconnectError",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"deleteMessage.acquisizioneLock.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"deleteMessage.acquisizioneLock.wait.withoutOwner",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"deleteMessage.acquisizioneLock.wait.existsOldOwner",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"deleteMessage.acquisizioneLock.ok",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"deleteMessage.acquisizioneUnlock.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"deleteMessage.acquisizioneUnlock.ok",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"deleteMessage.acquisizioneUnlock.ko"
	};
	
	private final static String PREFIX_MSG_DIAGNOSTICO = "org.openspcoop2.pdd.msgdiagnostico.";
	private final static String SUFFIX_LIVELLO_MSG_DIAGNOSTICO = ".livello";
	private final static String SUFFIX_CODICE_MSG_DIAGNOSTICO = ".codice";
	private final static String SUFFIX_MESSAGGIO_MSG_DIAGNOSTICO = ".messaggio";
	private final static String SUFFIX_CODICE_MODULO_MSG_DIAGNOSTICO = ".codiceModulo";
	
	
	
	
	
	
	
	
	/*---------- Get -------------*/
	public Integer getLivello(String modulo,String keyLivelloPersonalizzato){
		try{
			String livello = MsgDiagnosticiProperties.livelliMsgDiagnosticiPersonalizzati.getValue(modulo+keyLivelloPersonalizzato);
			if(livello!=null){
				return Integer.parseInt(livello);
			} else{
				return -1;
			}
		}catch(Exception e){
			this.log.error("Errore durante l'esecuzione del metodo getLivello("+modulo+","+keyLivelloPersonalizzato+")",e);
			return -1;
		}
	}
	public String getCodice(String modulo,String keyCodicePersonalizzato){
		try{
			return MsgDiagnosticiProperties.codiciMsgDiagnosticiPersonalizzati.getValue(modulo+keyCodicePersonalizzato);
		}catch(Exception e){
			this.log.error("Errore durante l'esecuzione del metodo getCodice("+modulo+","+keyCodicePersonalizzato+")",e);
			return null;
		}
	}
	public String getMessaggio(String modulo,String keyMsgPersonalizzato){
		try{
			return MsgDiagnosticiProperties.messaggiMsgDiagnosticiPersonalizzati.getValue(modulo+keyMsgPersonalizzato);
		}catch(Exception e){
			this.log.error("Errore durante l'esecuzione del metodo getMessaggio("+modulo+","+keyMsgPersonalizzato+")",e);
			return null;
		}
	}
	public String getKeyMessaggio(String codice) throws Exception{
		if(MsgDiagnosticiProperties.mappingCodiceToKeywordMsgDiagnosticiPersonalizzati.containsKey(codice)){
			Object keyword = MsgDiagnosticiProperties.mappingCodiceToKeywordMsgDiagnosticiPersonalizzati.getValue(codice);
			if(keyword == null){
				throw new Exception("MsgDiagnostico con codice ["+codice+"] non registrato?");
			}
			return (String) keyword;
		}else{
			throw new Exception("MsgDiagnostico con codice ["+codice+"] non registrato");
		}
	}
	public String getModulo(String codice) throws Exception{
		
		Enumeration<?> moduli = MsgDiagnosticiProperties.codiciIdentificativiFunzione.propertyNames();
		while(moduli.hasMoreElements()){
			String modulo = (String) moduli.nextElement();
			Object codiceModulo = MsgDiagnosticiProperties.codiciIdentificativiFunzione.getValue(modulo);
			if(codiceModulo==null){
				throw new Exception("Modulo ["+modulo+"] senza un codice registrato");
			}
			if(((String)codiceModulo).equals(codice)){
				return modulo;
			}
			
		}
		throw new Exception("Modulo con codice ["+codice+"] non registrato");
	}
	
	
	
	
	/*---------- Get Codici per i diagnostici non registrati -------------*/
	private static final String PREFIX_UNKNOW_CODE_PREFIX = "org.openspcoop2.pdd.msgdiagnosticoNonRegistrato.";
	private static final String PREFIX_UNKNOW_CODE_SUFFIX = ".codice";
	
	private static boolean codiceDiagnosticoInitialized = false;
	private static String codiceDiagnosticoFatal = null;
	private static String codiceDiagnosticoErrorProtocol = null;
	private static String codiceDiagnosticoErrorIntegration = null;
	private static String codiceDiagnosticoInfoProtocol = null;
	private static String codiceDiagnosticoInfoIntegration = null;
	private static String codiceDiagnosticoDebugLow = null;
	private static String codiceDiagnosticoDebugMedium = null;
	private static String codiceDiagnosticoDebugHigh = null;
	
	private static synchronized void initCodiceDiagnostico(MsgDiagnosticiInstanceProperties reader) throws UtilsException{
		if(codiceDiagnosticoInitialized==false){
			
			codiceDiagnosticoFatal = reader.getValue(PREFIX_UNKNOW_CODE_PREFIX+"fatal"+PREFIX_UNKNOW_CODE_SUFFIX);
			
			codiceDiagnosticoErrorProtocol = reader.getValue(PREFIX_UNKNOW_CODE_PREFIX+"errorProtocol"+PREFIX_UNKNOW_CODE_SUFFIX);
			codiceDiagnosticoErrorIntegration = reader.getValue(PREFIX_UNKNOW_CODE_PREFIX+"errorIntegration"+PREFIX_UNKNOW_CODE_SUFFIX);
			
			codiceDiagnosticoInfoProtocol = reader.getValue(PREFIX_UNKNOW_CODE_PREFIX+"infoProtocol"+PREFIX_UNKNOW_CODE_SUFFIX);
			codiceDiagnosticoInfoIntegration = reader.getValue(PREFIX_UNKNOW_CODE_PREFIX+"infoIntegration"+PREFIX_UNKNOW_CODE_SUFFIX);
			
			codiceDiagnosticoDebugLow = reader.getValue(PREFIX_UNKNOW_CODE_PREFIX+"debugLow"+PREFIX_UNKNOW_CODE_SUFFIX);
			codiceDiagnosticoDebugMedium = reader.getValue(PREFIX_UNKNOW_CODE_PREFIX+"debugMedium"+PREFIX_UNKNOW_CODE_SUFFIX);	
			codiceDiagnosticoDebugHigh = reader.getValue(PREFIX_UNKNOW_CODE_PREFIX+"debugHigh"+PREFIX_UNKNOW_CODE_SUFFIX);
			
			codiceDiagnosticoInitialized=true;
		}
	}
	
	
	public String getCodiceDiagnosticoFatal() throws UtilsException{
		if(codiceDiagnosticoInitialized==false){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoFatal;
	}
	
	public String getCodiceDiagnosticoErrorProtocol() throws UtilsException{
		if(codiceDiagnosticoInitialized==false){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoErrorProtocol;
	}
	public String getCodiceDiagnosticoErrorIntegration() throws UtilsException{
		if(codiceDiagnosticoInitialized==false){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoErrorIntegration;
	}
	public String getCodiceDiagnosticoInfoProtocol() throws UtilsException{
		if(codiceDiagnosticoInitialized==false){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoInfoProtocol;
	}
	public String getCodiceDiagnosticoInfoIntegration() throws UtilsException{
		if(codiceDiagnosticoInitialized==false){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoInfoIntegration;
	}
	public String getCodiceDiagnosticoDebugLow() throws UtilsException{
		if(codiceDiagnosticoInitialized==false){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoDebugLow;
	}
	public String getCodiceDiagnosticoDebugMedium() throws UtilsException{
		if(codiceDiagnosticoInitialized==false){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoDebugMedium;
	}
	public String getCodiceDiagnosticoDebugHigh() throws UtilsException{
		if(codiceDiagnosticoInitialized==false){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoDebugHigh;
	}

}
