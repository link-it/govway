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



package org.openspcoop2.pdd.logger;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.config.MsgDiagnosticiInstanceProperties;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.PropertiesReader;
import org.slf4j.Logger;


/**
 * Questo file permette di configurare i piu' importanti messaggi diagnostici emessi dalla porta di dominio
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class MsgDiagnosticiProperties {	

	/** Costanti */
	
	public static final String DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE = "DYNAMIC_INFO_TYPE";
	
	public static final String DIAGNOSTIC_WITH_DYNAMIC_INFO_VALUE = "DYNAMIC_INFO_VALUE";
	
	public static final String DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR = "#_#";
	
	public static final String DIAGNOSTIC_WITH_DYNAMIC_INFO_DIAG_SEPARATOR = "###";
	
	public static final String DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR = " ";
	
	public static final String DIAGNOSTIC_TYPE_POLICY_CONTROLLO_TRAFFICO = "CT";
	
	public static final String NON_PRESENTE = "-";
	
	public static final String SEPARATOR = " ";
	
	
	
	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;
	private void logError(String msgErrore, Exception e) {
		if(this.log!=null) {
			this.log.error(msgErrore,e);
		}
	}
	private void logError(String msgErrore) {
		if(this.log!=null) {
			this.log.error(msgErrore);
		}
	}

	/** Logger passati come argomento */
	private static void logError(Logger log, String msg) {
		if(log!=null) {
			log.error(msg);
		}
	}
	private static void logInfo(Logger log, String msg) {
		if(log!=null) {
			log.info(msg);
		}
	}


	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'govway.msgDiagnostici.properties' */
	private MsgDiagnosticiInstanceProperties reader;

	/** Copia Statica */
	private static MsgDiagnosticiProperties msgDiagnosticiProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public MsgDiagnosticiProperties(String location,String confDir) throws CoreException {

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
				properties = MsgDiagnosticiProperties.class.getResourceAsStream("/govway.msgDiagnostici.properties");
			}
			if(properties==null){
				throw new CoreException("File '/govway.msgDiagnostici.properties' not found");
			}
			propertiesReader.load(properties);		    
		}catch(Exception e) {
			logError("Riscontrato errore durante la lettura del file 'govway.msgDiagnostici.properties': \n\n"+e.getMessage());
			throw new CoreException("MsgDiagnosticiProperties initialize error: "+e.getMessage());
		}finally{
			try{
				if(properties!=null)
				    properties.close();
			}catch(Exception er){
				// close
			}
		}

		try {
			this.reader = new MsgDiagnosticiInstanceProperties(propertiesReader, this.log, confDir);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String location,String confDir){

		try {
			if(MsgDiagnosticiProperties.msgDiagnosticiProperties == null){
				initializeEngine(location,confDir);
			}
		    return true;
		}
		catch(Exception e) {
		    return false;
		}
	}
	private static synchronized void initializeEngine(String location,String confDir) throws CoreException{
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



		




	/*---------- Gestione livello di filtro -------------*/
		
	private static Integer filtroMsgDiagnosticoOpenSPCoop2level0 = null;
	int getFiltroMsgDiagnosticoOpenSPCoop2level0() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level0==null){
			initFiltroMsgDiagnosticoOpenSPCoop2level0(this.reader, this.log);
		}
		return MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level0;
	}
	private static synchronized void initFiltroMsgDiagnosticoOpenSPCoop2level0(MsgDiagnosticiInstanceProperties reader, Logger log) {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level0==null){
			try{ 
				String value = null;
				value = reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.0");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level0=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_FATAL);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level0=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level0 < 0 || MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level0 > 9999 ){
						throw new CoreException("Il valore della proprieta' deve essere compreso nell'intervallo [0,9999]");
					}
				}
			}catch(java.lang.Exception e) {
				logError(log, "Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.0': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level0=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_FATAL);
			}  
		}
	}
	
	private static Integer filtroMsgDiagnosticoOpenSPCoop2level1 = null;
	int getFiltroMsgDiagnosticoOpenSPCoop2level1() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level1==null){
			initFiltroMsgDiagnosticoOpenSPCoop2level1(this.reader, this.log);
		}
		return MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level1;
	}
	private static synchronized void initFiltroMsgDiagnosticoOpenSPCoop2level1(MsgDiagnosticiInstanceProperties reader, Logger log) {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level1==null){
			try{ 
				String value = null;
				value = reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.1");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level1=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_PROTOCOL);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level1=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level1 < 1 || MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level1 > 19999 ){
						throw new CoreException("Il valore della proprieta' deve essere compreso nell'intervallo [1,19999]");
					}
				}
			}catch(java.lang.Exception e) {
				logError(log, "Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.1': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level1=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_PROTOCOL);
			}  
		}
	}
	
	private static Integer filtroMsgDiagnosticoOpenSPCoop2level2 = null;
	int getFiltroMsgDiagnosticoOpenSPCoop2level2() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level2==null){
			initFiltroMsgDiagnosticoOpenSPCoop2level2(this.reader, this.log);
		}
		return MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level2;
	}
	private static synchronized void initFiltroMsgDiagnosticoOpenSPCoop2level2(MsgDiagnosticiInstanceProperties reader, Logger log) {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level2==null){
			try{ 
				String value = null;
				value = reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.2");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level2=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_INTEGRATION);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level2=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level2 < 10001 || MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level2 > 29999 ){
						throw new CoreException("Il valore della proprieta' deve essere compreso nell'intervallo [10001,29999]");
					}
				}
			}catch(java.lang.Exception e) {
				logError(log, "Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.2': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level2=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_INTEGRATION);
			}  
		}
	}
	
	
	private static Integer filtroMsgDiagnosticoOpenSPCoop2level3 = null;
	int getFiltroMsgDiagnosticoOpenSPCoop2level3() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level3==null){
			initFiltroMsgDiagnosticoOpenSPCoop2level3(this.reader, this.log);
		}
		return MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level3;
	}
	private static synchronized void initFiltroMsgDiagnosticoOpenSPCoop2level3(MsgDiagnosticiInstanceProperties reader, Logger log) {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level3==null){
			try{ 
				String value = null;
				value = reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.3");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level3=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_PROTOCOL);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level3=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level3 < 20001 || MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level3 > 39999 ){
						throw new CoreException("Il valore della proprieta' deve essere compreso nell'intervallo [20001,39999]");
					}
				}
			}catch(java.lang.Exception e) {
				logError(log, "Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.3': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level3=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_PROTOCOL);
			}  
		}
	}
	
	
	private static Integer filtroMsgDiagnosticoOpenSPCoop2level4 = null;
	int getFiltroMsgDiagnosticoOpenSPCoop2level4() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level4==null){
			initFiltroMsgDiagnosticoOpenSPCoop2level4(this.reader, this.log);
		}
		return MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level4;
	}
	private static synchronized void initFiltroMsgDiagnosticoOpenSPCoop2level4(MsgDiagnosticiInstanceProperties reader, Logger log) {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level4==null){
			try{ 
				String value = null;
				value = reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.4");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level4=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_INTEGRATION);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level4=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level4 < 30001 || MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level4 > 49999 ){
						throw new CoreException("Il valore della proprieta' deve essere compreso nell'intervallo [30001,49999]");
					}
				}
			}catch(java.lang.Exception e) {
				logError(log, "Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.4': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level4=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_INTEGRATION);
			}  
		}
	}
	
	private static Integer filtroMsgDiagnosticoOpenSPCoop2level5 = null;
	int getFiltroMsgDiagnosticoOpenSPCoop2level5() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level5==null){
			initFiltroMsgDiagnosticoOpenSPCoop2level5(this.reader, this.log);
		}
		return MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level5;
	}
	private static synchronized void initFiltroMsgDiagnosticoOpenSPCoop2level5(MsgDiagnosticiInstanceProperties reader, Logger log) {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level5==null){
			try{ 
				String value = null;
				value = reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.5");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level5=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_LOW);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level5=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level5 < 40001 || MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level5 > 59999 ){
						throw new CoreException("Il valore della proprieta' deve essere compreso nell'intervallo [40001,59999]");
					}
				}
			}catch(java.lang.Exception e) {
				logError(log, "Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.5': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level5=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_LOW);
			}  
		}
	}
	
	
	private static Integer filtroMsgDiagnosticoOpenSPCoop2level6 = null;
	int getFiltroMsgDiagnosticoOpenSPCoop2level6() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level6==null){
			initFiltroMsgDiagnosticoOpenSPCoop2level6(this.reader, this.log);
		}
		return MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level6;
	}
	private static synchronized void initFiltroMsgDiagnosticoOpenSPCoop2level6(MsgDiagnosticiInstanceProperties reader, Logger log) {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level6==null){
			try{ 
				String value = null;
				value = reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.6");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level6=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_MEDIUM);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level6=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level6 < 50001 || MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level6 > 69999 ){
						throw new CoreException("Il valore della proprieta' deve essere compreso nell'intervallo [50001,69999]");
					}
				}
			}catch(java.lang.Exception e) {
				logError(log, "Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.6': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level6=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_MEDIUM);
			}  
		}
	}
	
	
	private static Integer filtroMsgDiagnosticoOpenSPCoop2level7 = null;
	int getFiltroMsgDiagnosticoOpenSPCoop2level7() {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level7==null){
			initFiltroMsgDiagnosticoOpenSPCoop2level7(this.reader, this.log);
		}
		return MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level7;
	}
	private static synchronized void initFiltroMsgDiagnosticoOpenSPCoop2level7(MsgDiagnosticiInstanceProperties reader, Logger log) {	
		if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level7==null){
			try{ 
				String value = null;
				value = reader.getValue("filtroMsgDiagnostici.livelloOpenSPCoop2.7");
				if(value==null){
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level7=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_HIGH);
				}else{
					MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level7=Integer.parseInt(value.trim());
					if(MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level7 < 60001 || MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level7 > 70000 ){
						throw new CoreException("Il valore della proprieta' deve essere compreso nell'intervallo [60001,70000]");
					}
				}
			}catch(java.lang.Exception e) {
				logError(log, "Riscontrato errore durante la lettura della proprieta' 'filtroMsgDiagnostici.livelloOpenSPCoop2.7': "+e.getMessage());
				MsgDiagnosticiProperties.filtroMsgDiagnosticoOpenSPCoop2level7=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_HIGH);
			}  
		}
	}
	
	
	public int getValoreFiltroFromValoreOpenSPCoop2(int livello){
		if(livello == LogLevels.SEVERITA_FATAL){
			return this.getFiltroMsgDiagnosticoOpenSPCoop2level0();
		}else if(livello == LogLevels.SEVERITA_ERROR_PROTOCOL){
			return this.getFiltroMsgDiagnosticoOpenSPCoop2level1();
		}else if(livello == LogLevels.SEVERITA_ERROR_INTEGRATION){
			return this.getFiltroMsgDiagnosticoOpenSPCoop2level2();
		}else if(livello == LogLevels.SEVERITA_INFO_PROTOCOL){
			return this.getFiltroMsgDiagnosticoOpenSPCoop2level3();
		}else if(livello == LogLevels.SEVERITA_INFO_INTEGRATION){
			return this.getFiltroMsgDiagnosticoOpenSPCoop2level4();
		}else if(livello == LogLevels.SEVERITA_DEBUG_LOW){
			return this.getFiltroMsgDiagnosticoOpenSPCoop2level5();
		}else if(livello == LogLevels.SEVERITA_DEBUG_MEDIUM){
			return this.getFiltroMsgDiagnosticoOpenSPCoop2level6();
		}else if(livello == LogLevels.SEVERITA_DEBUG_HIGH){
			return this.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		}else{
			return livello;
		}
	}

	
	
	public boolean checkValoriFiltriMsgDiagnostici(Logger log){
		int openspcoop2level0 = this.getFiltroMsgDiagnosticoOpenSPCoop2level0(); 
		if(openspcoop2level0!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_FATAL)){
			logInfo(log, "Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 0) al valore openspcoop: "+openspcoop2level0);
		}
		
		int openspcoop2level1 = this.getFiltroMsgDiagnosticoOpenSPCoop2level1(); 
		if(openspcoop2level1<=openspcoop2level0){
			this.logError("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 1 (valore: "+openspcoop2level1
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 0 (valore: "+openspcoop2level0+").");
			return false;
		}else{
			if(openspcoop2level1!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_PROTOCOL)){
				logInfo(log, "Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 1) al valore openspcoop: "+openspcoop2level1);
			}
		}
		
		int openspcoop2level2 = this.getFiltroMsgDiagnosticoOpenSPCoop2level2(); 
		if(openspcoop2level2<=openspcoop2level1){
			this.logError("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 2 (valore: "+openspcoop2level2
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 1 (valore: "+openspcoop2level1+").");
			return false;
		}else{
			if(openspcoop2level2!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_INTEGRATION)){
				logInfo(log, "Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 2) al valore openspcoop: "+openspcoop2level2);
			}
		}
		
		int openspcoop2level3 = this.getFiltroMsgDiagnosticoOpenSPCoop2level3(); 
		if(openspcoop2level3<=openspcoop2level2){
			this.logError("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 3 (valore: "+openspcoop2level3
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 2 (valore: "+openspcoop2level2+").");
			return false;
		}else{
			if(openspcoop2level3!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_PROTOCOL)){
				logInfo(log, "Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 3) al valore openspcoop: "+openspcoop2level3);
			}
		}
		
		int openspcoop2level4 = this.getFiltroMsgDiagnosticoOpenSPCoop2level4(); 
		if(openspcoop2level4<=openspcoop2level3){
			this.logError("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 4 (valore: "+openspcoop2level4
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 3 (valore: "+openspcoop2level3+").");
			return false;
		}else{
			if(openspcoop2level4!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_INTEGRATION)){
				logInfo(log, "Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 4) al valore openspcoop: "+openspcoop2level4);
			}
		}
	
		int openspcoop2level5 = this.getFiltroMsgDiagnosticoOpenSPCoop2level5(); 
		if(openspcoop2level5<=openspcoop2level4){
			this.logError("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 5 (valore: "+openspcoop2level5
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 4 (valore: "+openspcoop2level4+").");
			return false;
		}else{
			if(openspcoop2level5!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_LOW)){
				logInfo(log, "Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 5) al valore openspcoop: "+openspcoop2level5);
			}
		}
		
		int openspcoop2level6 = this.getFiltroMsgDiagnosticoOpenSPCoop2level6(); 
		if(openspcoop2level6<=openspcoop2level5){
			this.logError("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 6 (valore: "+openspcoop2level6
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 5 (valore: "+openspcoop2level5+").");
			return false;
		}else{
			if(openspcoop2level6!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_MEDIUM)){
				logInfo(log, "Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 6) al valore openspcoop: "+openspcoop2level6);
			}
		}
		
		int openspcoop2level7 = this.getFiltroMsgDiagnosticoOpenSPCoop2level7(); 
		if(openspcoop2level7<=openspcoop2level6){
			this.logError("Personalizzazione del livello di filtro dei messaggi diagnostici OpenSPCoop2 7 (valore: "+openspcoop2level7
					+") deve essere maggiore del valore impostato per il livello OpenSPCoop2 6 (valore: "+openspcoop2level6+").");
			return false;
		}else{
			if(openspcoop2level7!=LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_HIGH)){
				logInfo(log, "Personalizzazione del livello di filtro dei messaggi diagnostici (OpenSPCoop2 7) al valore openspcoop: "+openspcoop2level7);
			}
		}
		
		return true;
	}
	
	
	
	
	
	
	/*---------- Inizializzazione messaggi diagnostici -------------*/
	
	private static Boolean initMsgDiagnosticiPersonalizzati = null;
	
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
	
	public boolean initializeMsgDiagnosticiPersonalizzati(){
		if(MsgDiagnosticiProperties.initMsgDiagnosticiPersonalizzati == null){
			return initializeMsgDiagnosticiPersonalizzatiEngine(this.reader, this.log);
		}
		return MsgDiagnosticiProperties.initMsgDiagnosticiPersonalizzati!=null ? MsgDiagnosticiProperties.initMsgDiagnosticiPersonalizzati : null;
	}
	private static synchronized boolean initializeMsgDiagnosticiPersonalizzatiEngine(MsgDiagnosticiInstanceProperties reader, Logger log){
		try{
			if(MsgDiagnosticiProperties.initMsgDiagnosticiPersonalizzati == null){
				
				Properties tmpLivelliMsgDiagnosticiPersonalizzati = new Properties();
				Properties tmpCodiciMsgDiagnosticiPersonalizzati = new Properties();
				Properties tmpCodiciIdentificativiFunzione = new Properties();
				Properties tmpMessaggiMsgDiagnosticiPersonalizzati = new Properties();
				Properties tmpMappingCodiceToKeywordMsgDiagnosticiPersonalizzati = new Properties();
				
				
				// Search codiceIdentificativoFunzione
				Properties msgDiagnostici =  reader.readProperties(MsgDiagnosticiProperties.PREFIX_MSG_DIAGNOSTICO);
				Enumeration<?> nomi = msgDiagnostici.keys();
				while(nomi.hasMoreElements()){
					String key = (String) nomi.nextElement();	
					if(key.endsWith(MsgDiagnosticiProperties.SUFFIX_CODICE_MODULO_MSG_DIAGNOSTICO)){
						String value = msgDiagnostici.getProperty(key);
						String [] tmpSplit = key.split("\\.");
						String modulo = tmpSplit[0].trim();
						
						// Controllo che il modulo non sia gia' definito
						if(tmpCodiciIdentificativiFunzione.containsKey(modulo)){
							throw new CoreException("Per Il modulo funzionale ["+modulo+"] sono stati definiti piu' codici??");
						}
						
						// Controllo che gia non esista il valore associato
						if(tmpCodiciIdentificativiFunzione.containsValue(value)){
							throw new CoreException("Il codice fornito ("+modulo+"="+value+") e' gia' utilizzato da un altro modulo funzionale");
						}
						
						// Add
						/**System.out.println("AGGIUNTI CODICI ["+modulo+"]=["+value+"]");*/
						tmpCodiciIdentificativiFunzione.put(modulo, value);
					}
				}

				// Leggo messaggio,livello e codice Diagnostico
				msgDiagnostici = reader.readProperties(MsgDiagnosticiProperties.PREFIX_MSG_DIAGNOSTICO);
				nomi = msgDiagnostici.keys();
				while(nomi.hasMoreElements()){
					String key = (String) nomi.nextElement();	
					if(key.endsWith(MsgDiagnosticiProperties.SUFFIX_LIVELLO_MSG_DIAGNOSTICO)){
						String chiave = key.substring(0,(key.length()-(MsgDiagnosticiProperties.SUFFIX_LIVELLO_MSG_DIAGNOSTICO.length()))); 
						String valore = msgDiagnostici.getProperty(key);
						try{
							Integer v = Integer.parseInt(valore);
							if(v.toString()!=null) {
								// ignore
							}
							tmpLivelliMsgDiagnosticiPersonalizzati.put(chiave, valore);
							/**System.out.println("ADD LIVELLO ["+chiave+"] ["+v+"]");*/
						}catch(Exception e){
							throw new CoreException("Valore della proprieta' ["+key+"] non valido ["+valore+"]: "+e.getMessage());
						}
					}else if(key.endsWith(MsgDiagnosticiProperties.SUFFIX_CODICE_MSG_DIAGNOSTICO)){
						String chiave = key.substring(0,(key.length()-(MsgDiagnosticiProperties.SUFFIX_CODICE_MSG_DIAGNOSTICO.length()))); 
						String valore = msgDiagnostici.getProperty(key);
						String [] tmp = chiave.split("\\.");
						String moduloFunzionale = tmp[0].trim();
						
						// getCodice Modulo Funzionale
						Object codiceModuloFunzionaleObject = tmpCodiciIdentificativiFunzione.get(moduloFunzionale);
						if(codiceModuloFunzionaleObject==null){
							throw new CoreException("Per il modulo funzionale ["+moduloFunzionale+"] non e' stato definito il codice");
						}
						String codiceModuloFunzionale = (String) codiceModuloFunzionaleObject;
						
						// Codice definitivo
						String codiceDefinitivo = codiceModuloFunzionale+valore;
						if(tmpCodiciMsgDiagnosticiPersonalizzati.containsValue(codiceDefinitivo)){
							throw new CoreException("Proprietà "+chiave+" contiene un codice ["+valore+"] gia' definito per un altro messaggio diagnostico del modulo funzionale "+moduloFunzionale+" ("+codiceModuloFunzionale+")");
						}
						tmpCodiciMsgDiagnosticiPersonalizzati.put(chiave, codiceDefinitivo);
						tmpMappingCodiceToKeywordMsgDiagnosticiPersonalizzati.put(codiceDefinitivo,chiave);
						/**System.out.println("ADD CODICE ["+chiave+"] ["+codiceDefinitivo+"]");*/
						
					}else if(key.endsWith(MsgDiagnosticiProperties.SUFFIX_MESSAGGIO_MSG_DIAGNOSTICO)){
						String chiave = key.substring(0,(key.length()-(MsgDiagnosticiProperties.SUFFIX_MESSAGGIO_MSG_DIAGNOSTICO.length()))); 
						String valore = msgDiagnostici.getProperty(key);
						tmpMessaggiMsgDiagnosticiPersonalizzati.put(chiave, valore);
						/**System.out.println("ADD MESSAGGIO ["+chiave+"] ["+valore+"]");*/
					}else{
						if(!key.endsWith(MsgDiagnosticiProperties.SUFFIX_CODICE_MODULO_MSG_DIAGNOSTICO)){
							throw new CoreException("Proprieta' malformata ["+key+"]");
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
					if(tmpLivelliMsgDiagnosticiPersonalizzati.containsKey(MsgDiagnosticiProperties.MSG_DIAGNOSTICI_PERSONALIZZATI[i])==false){
						throw new CoreException("Proprieta' "+livello+" non definita");
					}
					if(tmpCodiciMsgDiagnosticiPersonalizzati.containsKey(MsgDiagnosticiProperties.MSG_DIAGNOSTICI_PERSONALIZZATI[i])==false){
						throw new CoreException("Proprieta' "+codice+" non definita");
					}
					if(tmpMessaggiMsgDiagnosticiPersonalizzati.containsKey(MsgDiagnosticiProperties.MSG_DIAGNOSTICI_PERSONALIZZATI[i])==false){
						throw new CoreException("Proprieta' "+messaggio+" non definita");
					}
				}
				
				
				// inizializzo strutture
				
				MsgDiagnosticiProperties.livelliMsgDiagnosticiPersonalizzati = new PropertiesReader(tmpLivelliMsgDiagnosticiPersonalizzati,true);				
				MsgDiagnosticiProperties.codiciMsgDiagnosticiPersonalizzati = new PropertiesReader(tmpCodiciMsgDiagnosticiPersonalizzati,true);			
				MsgDiagnosticiProperties.messaggiMsgDiagnosticiPersonalizzati = new PropertiesReader(tmpMessaggiMsgDiagnosticiPersonalizzati,true);			
				MsgDiagnosticiProperties.mappingCodiceToKeywordMsgDiagnosticiPersonalizzati = new PropertiesReader(tmpMappingCodiceToKeywordMsgDiagnosticiPersonalizzati,true);	
				MsgDiagnosticiProperties.codiciIdentificativiFunzione = new PropertiesReader(tmpCodiciIdentificativiFunzione,true);

				MsgDiagnosticiProperties.initMsgDiagnosticiPersonalizzati = true;
				
			}
			return true;
		}catch(java.lang.Exception e) {
			logError(log, "Riscontrato errore durante la lettura dei messaggi diagnostici personalizzati: "+e.getMessage());
			if(OpenSPCoop2Logger.getLoggerOpenSPCoopCore()!=null){
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Riscontrato errore durante la lettura dei messaggi diagnostici personalizzati: "+e.getMessage(),e);
			}
			MsgDiagnosticiProperties.livelliMsgDiagnosticiPersonalizzati = null;
			MsgDiagnosticiProperties.codiciMsgDiagnosticiPersonalizzati = null;
			MsgDiagnosticiProperties.messaggiMsgDiagnosticiPersonalizzati = null;
			MsgDiagnosticiProperties.mappingCodiceToKeywordMsgDiagnosticiPersonalizzati = null;
			MsgDiagnosticiProperties.codiciIdentificativiFunzione = null;
			return false;
		}
	}
	
	
	
	
	
	
	/*---------- Codici Warning -------------*/
	public static List<String> MSG_DIAGNOSTICI_WARNING = new ArrayList<>();
	static {
		MSG_DIAGNOSTICI_WARNING.add("001069"); // ricezioneContenutiApplicativi.controlloTraffico.policy.violataWarningOnly
		MSG_DIAGNOSTICI_WARNING.add("001072"); // ricezioneContenutiApplicativi.controlloTraffico.maxRequestsViolatedWarningOnly
		MSG_DIAGNOSTICI_WARNING.add("001084"); // ricezioneContenutiApplicativi.gestioneTokenInCorso.validazioneToken.warningOnly.fallita
		MSG_DIAGNOSTICI_WARNING.add("001091"); // ricezioneContenutiApplicativi.gestioneTokenInCorso.introspectionToken.warningOnly.fallita
		MSG_DIAGNOSTICI_WARNING.add("001098"); // ricezioneContenutiApplicativi.gestioneTokenInCorso.userInfoToken.warningOnly.fallita
		MSG_DIAGNOSTICI_WARNING.add("001108"); // ricezioneContenutiApplicativi.validazioneContenutiApplicativiRichiestaNonRiuscita.warningOnly
		MSG_DIAGNOSTICI_WARNING.add("001122"); // ricezioneContenutiApplicativi.richiesta.warningCharsetDifferenteDefault
		MSG_DIAGNOSTICI_WARNING.add("001123"); // ricezioneContenutiApplicativi.risposta.warningCharsetDifferenteDefault
		
		MSG_DIAGNOSTICI_WARNING.add("003060"); // inoltroBuste.validazioneContenutiApplicativiRispostaNonRiuscita.warningOnly
		
		MSG_DIAGNOSTICI_WARNING.add("004092"); // ricezioneBuste.controlloTraffico.policy.violataWarningOnly
		MSG_DIAGNOSTICI_WARNING.add("004095"); // ricezioneBuste.controlloTraffico.maxRequestsViolatedWarningOnly
		MSG_DIAGNOSTICI_WARNING.add("004107"); // ricezioneBuste.gestioneTokenInCorso.validazioneToken.warningOnly.fallita
		MSG_DIAGNOSTICI_WARNING.add("004114"); // ricezioneBuste.gestioneTokenInCorso.introspectionToken.warningOnly.fallita
		MSG_DIAGNOSTICI_WARNING.add("004121"); // ricezioneBuste.gestioneTokenInCorso.userInfoToken.warningOnly.fallita
		MSG_DIAGNOSTICI_WARNING.add("004131"); // ricezioneBuste.validazioneContenutiApplicativiRichiestaNonRiuscita.warningOnly
		MSG_DIAGNOSTICI_WARNING.add("004145"); // ricezioneBuste.richiesta.warningCharsetDifferenteDefault
		MSG_DIAGNOSTICI_WARNING.add("004146"); // ricezioneBuste.risposta.warningCharsetDifferenteDefault
		MSG_DIAGNOSTICI_WARNING.add("004176"); // ricezioneBuste.protocolli.tipoSoggetto.fruitore.unsupported.warning
		
		MSG_DIAGNOSTICI_WARNING.add("007059"); // consegnaContenutiApplicativi.validazioneContenutiApplicativiRispostaNonRiuscita.warningOnly
	}
	
	/*---------- Codici Consegna Fallita -------------*/
	public static List<String> MSG_DIAGNOSTICI_ERRORE_CONNETTORE = new ArrayList<>();
	static {
		MSG_DIAGNOSTICI_ERRORE_CONNETTORE.add("003008"); // inoltroBuste.inoltroConErrore
		MSG_DIAGNOSTICI_ERRORE_CONNETTORE.add("003013"); // inoltroBuste.ricezioneSoapFault 
		MSG_DIAGNOSTICI_ERRORE_CONNETTORE.add("003059"); // inoltroBuste.ricezioneRestProblem
		MSG_DIAGNOSTICI_ERRORE_CONNETTORE.add("007013"); // consegnaContenutiApplicativi.consegnaConErrore
		MSG_DIAGNOSTICI_ERRORE_CONNETTORE.add("007014"); // consegnaContenutiApplicativi.ricezioneSoapFault
		MSG_DIAGNOSTICI_ERRORE_CONNETTORE.add("007058"); // consegnaContenutiApplicativi.ricezioneRestProblem
	}
	
	/*---------- Codici Generazione Messaggio Errore -------------*/
	public static List<String> MSG_DIAGNOSTICI_SEGNALA_GENERATA_RISPOSTA_ERRORE = new ArrayList<>();
	static {
		MSG_DIAGNOSTICI_SEGNALA_GENERATA_RISPOSTA_ERRORE.add("001008"); // ricezioneContenutiApplicativi.consegnaRispostaApplicativaFallita
		MSG_DIAGNOSTICI_SEGNALA_GENERATA_RISPOSTA_ERRORE.add("001033"); // ricezioneContenutiApplicativi.integrationManager.consegnaRispostaApplicativaFallita
		MSG_DIAGNOSTICI_SEGNALA_GENERATA_RISPOSTA_ERRORE.add("004007"); // ricezioneBuste.generazioneMessaggioErroreRisposta
		MSG_DIAGNOSTICI_SEGNALA_GENERATA_RISPOSTA_ERRORE.add("004008"); // ricezioneBuste.generazioneMessaggioErroreRisposta.destinatarioSconosciuto 
		MSG_DIAGNOSTICI_SEGNALA_GENERATA_RISPOSTA_ERRORE.add("004080"); // ricezioneBuste.generazioneMessaggioErroreRisposta.mittenteAnonimo
	}
	
	/*---------- Keyword attese -------------*/
	public static final String MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI="ricezioneContenutiApplicativi.";
	public static final String MSG_DIAG_IMBUSTAMENTO="imbustamentoBusta.";
	public static final String MSG_DIAG_IMBUSTAMENTO_RISPOSTE="imbustamentoRispostaBusta.";
	public static final String MSG_DIAG_INOLTRO_BUSTE="inoltroBuste.";
	public static final String MSG_DIAG_RICEZIONE_BUSTE="ricezioneBuste.";
	public static final String MSG_DIAG_SBUSTAMENTO="sbustamentoBusta.";
	public static final String MSG_DIAG_SBUSTAMENTO_RISPOSTE="sbustamentoRispostaBusta.";
	public static final String MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI="consegnaContenutiApplicativi.";
	public static final String MSG_DIAG_INTEGRATION_MANAGER="integrationManager.";
	public static final String MSG_DIAG_TRACCIAMENTO="tracciamento.";
	public static final String MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE="timerGestoreRiscontriRicevute.";
	public static final String MSG_DIAG_TIMER_GESTORE_MESSAGGI="timerGestoreMessaggi.";
	public static final String MSG_DIAG_TIMER_GESTORE_MESSAGGI_INCONSISTENTI="timerGestoreMessaggiInconsistenti.";
	public static final String MSG_DIAG_TIMER_GESTORE_REPOSITORY_BUSTE="timerGestoreRepositoryBuste.";
	public static final String MSG_DIAG_TIMER_MONITORAGGIO_RISORSE="timerMonitoraggioRisorse.";
	public static final String MSG_DIAG_TIMER_THRESHOLD="timerThreshold.";
	public static final String MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI="timerConsegnaContenutiApplicativi.";
	public static final String MSG_DIAG_TIMER_STATISTICHE="timerStatistiche.";
	public static final String MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND="timerGestoreChiaviPDND.";
	public static final String MSG_DIAG_OPENSPCOOP_STARTUP="openspcoopStartup.";
	public static final String MSG_DIAG_ALL="all.";
	private static final String[] MSG_DIAGNOSTICI_PERSONALIZZATI = {
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
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"servizioApplicativoFruitore.identificazioneTramiteCredenzialiFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"identificazioneDinamicaAzioneNonRiuscita",
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
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autenticazioneEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"ricevutaRichiestaApplicativa.mittenteAnonimo",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"servizioApplicativoFruitore.identificazioneTramiteCredenzialiFallita.opzionale",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"riferimentoIdRichiesta.nonFornito",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"controlloTraffico.maxRequestsViolated",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"controlloTraffico.pddCongestionata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"controlloTraffico.policy.controlloInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"controlloTraffico.policy.controlloTerminato.richiestaNonBloccata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"controlloTraffico.policy.controlloTerminato.richiestaBloccata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"controlloTraffico.policy.disabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"controlloTraffico.policy.filtrata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"controlloTraffico.policy.nonApplicabile",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"controlloTraffico.policy.violata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"controlloTraffico.policy.violataWarningOnly",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"controlloTraffico.policy.rispettata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"controlloTraffico.policy.inErrore",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"controlloTraffico.maxRequestsViolatedWarningOnly",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.verificaPresenzaToken",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.verificaPresenzaToken.trovato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.verificaPresenzaToken.completataSuccesso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.verificaPresenzaToken.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.validazioneToken",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.validazioneToken.completataSuccesso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.validazioneToken.validato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.validazioneToken.inCache",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.validazioneToken.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.validazioneToken.warningOnly.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.validazioneToken.disabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.introspectionToken",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.introspectionToken.completataSuccesso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.introspectionToken.validato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.introspectionToken.inCache",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.introspectionToken.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.introspectionToken.warningOnly.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.introspectionToken.disabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.userInfoToken",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.userInfoToken.completataSuccesso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.userInfoToken.validato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.userInfoToken.inCache",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.userInfoToken.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.userInfoToken.warningOnly.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenInCorso.userInfoToken.disabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenCompletataConSuccesso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenFallita.erroreGenerico",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneTokenCompletataSenzaRilevazioneToken",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autenticazioneTokenDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autenticazioneTokenInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autenticazioneTokenEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autenticazioneTokenFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validazioneContenutiApplicativiRichiestaNonRiuscita.warningOnly",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"headerIntegrazione.creazioneFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autorizzazioneCanale.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autorizzazioneCanale.effettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autorizzazioneCanale.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"richiestaNonValida",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneAADisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneAAInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneAACompletata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneAAFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneAAInCorso.retrieve",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneAAInCorso.retrieve.completataSuccesso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneAAInCorso.retrieve.completataSuccesso.inCache",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"gestioneAAInCorso.retrieve.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"richiesta.warningCharsetDifferenteDefault",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"risposta.warningCharsetDifferenteDefault",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"ricezioneRichiesta.firstAccessRequestStream",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autenticazioneApplicativoTokenInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autenticazioneApplicativoTokenEffettuata.identificazioneRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autenticazioneApplicativoTokenEffettuata.identificazioneFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"autenticazioneApplicativoTokenFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"protocolli.tipoSoggetto.applicativoToken.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"letturaPayloadRichiesta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"letturaPayloadRichiesta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"addTokenIdAuth.richiesta.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"addTokenIdAuth.richiesta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"addTokenIdAuth.richiesta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validateTokenIdAuth.risposta.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validateTokenIdAuth.risposta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validateTokenIdAuth.risposta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"addTokenIntegrity.richiesta.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"addTokenIntegrity.richiesta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"addTokenIntegrity.richiesta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validateTokenIntegrity.risposta.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validateTokenIntegrity.risposta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validateTokenIntegrity.risposta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"addTokenAudit.richiesta.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"addTokenAudit.richiesta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"addTokenAudit.richiesta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validazioneSemantica.risposta.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validazioneSemantica.risposta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI+"validazioneSemantica.risposta.fallita",
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
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"trasformazione.processamentoRichiestaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"trasformazione.processamentoRichiestaNessunMatch",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"trasformazione.processamentoRichiestaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"trasformazione.processamentoRichiestaEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"trasformazione.processamentoRichiestaInErrore",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"trasformazione.processamentoRispostaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"trasformazione.processamentoRispostaNessunMatch",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"trasformazione.processamentoRispostaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"trasformazione.processamentoRispostaEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"trasformazione.processamentoRispostaInErrore",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"ricezioneRestProblem",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"validazioneContenutiApplicativiRispostaNonRiuscita.warningOnly",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"headerIntegrazione.creazioneFallita",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"ricezioneRisposta.firstAccessRequestStream",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"letturaPayloadRisposta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"letturaPayloadRisposta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"negoziazioneToken.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"negoziazioneToken.completata",
		MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE+"negoziazioneToken.inCache",
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
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autenticazioneDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autenticazioneInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autenticazioneEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autenticazioneFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autenticazioneFallita.opzionale",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"ricezioneMessaggio.mittenteAnonimo",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"generazioneMessaggioRisposta.mittenteAnonimo",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"generazioneMessaggioErroreRisposta.mittenteAnonimo",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"identificazioneDinamicaAzioneNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"riferimentoIdRichiesta.nonFornito",		
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"controlloTraffico.maxRequestsViolated",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"controlloTraffico.pddCongestionata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"controlloTraffico.policy.controlloInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"controlloTraffico.policy.controlloTerminato.richiestaNonBloccata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"controlloTraffico.policy.controlloTerminato.richiestaBloccata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"controlloTraffico.policy.disabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"controlloTraffico.policy.filtrata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"controlloTraffico.policy.nonApplicabile",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"controlloTraffico.policy.violata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"controlloTraffico.policy.violataWarningOnly",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"controlloTraffico.policy.rispettata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"controlloTraffico.policy.inErrore",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"controlloTraffico.maxRequestsViolatedWarningOnly",	
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.verificaPresenzaToken",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.verificaPresenzaToken.trovato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.verificaPresenzaToken.completataSuccesso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.verificaPresenzaToken.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.validazioneToken",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.validazioneToken.completataSuccesso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.validazioneToken.validato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.validazioneToken.inCache",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.validazioneToken.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.validazioneToken.warningOnly.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.validazioneToken.disabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.introspectionToken",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.introspectionToken.completataSuccesso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.introspectionToken.validato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.introspectionToken.inCache",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.introspectionToken.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.introspectionToken.warningOnly.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.introspectionToken.disabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.userInfoToken",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.userInfoToken.completataSuccesso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.userInfoToken.validato",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.userInfoToken.inCache",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.userInfoToken.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.userInfoToken.warningOnly.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenInCorso.userInfoToken.disabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenCompletataConSuccesso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenFallita.erroreGenerico",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneTokenCompletataSenzaRilevazioneToken",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autenticazioneTokenDisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autenticazioneTokenInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autenticazioneTokenEffettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autenticazioneTokenFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneContenutiApplicativiRichiestaNonRiuscita.warningOnly",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"headerIntegrazione.creazioneFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autorizzazioneCanale.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autorizzazioneCanale.effettuata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autorizzazioneCanale.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"richiestaNonValida",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneAADisabilitata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneAAInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneAACompletata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneAAFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneAAInCorso.retrieve",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneAAInCorso.retrieve.completataSuccesso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneAAInCorso.retrieve.completataSuccesso.inCache",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"gestioneAAInCorso.retrieve.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"richiesta.warningCharsetDifferenteDefault",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"risposta.warningCharsetDifferenteDefault",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"ricezioneRichiesta.firstAccessRequestStream",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autenticazioneApplicativoTokenInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autenticazioneApplicativoTokenEffettuata.identificazioneRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autenticazioneApplicativoTokenEffettuata.identificazioneFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"autenticazioneApplicativoTokenFallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"protocolli.tipoSoggetto.applicativoToken.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"letturaPayloadRichiesta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"letturaPayloadRichiesta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validateTokenIdAuth.richiesta.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validateTokenIdAuth.richiesta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validateTokenIdAuth.richiesta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"addTokenIdAuth.risposta.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"addTokenIdAuth.risposta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"addTokenIdAuth.risposta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validateTokenIntegrity.richiesta.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validateTokenIntegrity.richiesta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validateTokenIntegrity.richiesta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"addTokenIntegrity.risposta.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"addTokenIntegrity.risposta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"addTokenIntegrity.risposta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validateTokenAudit.richiesta.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validateTokenAudit.richiesta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validateTokenAudit.richiesta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneSemantica.richiesta.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneSemantica.richiesta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneSemantica.richiesta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneSemantica.autorizzazione.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneSemantica.autorizzazione.completata",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"validazioneSemantica.autorizzazione.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE+"protocolli.tipoSoggetto.fruitore.unsupported.warning",
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
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"portaApplicativaNonEsistente.identificazionePerServizio",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"ricezioneRispostaSincrona",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"profiloAsincrono.flussoRicevutaRichiestaRispostaNonCorretto",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"profiloAsincronoAsimmetrico.saSenzaRispostaAsincrona",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"profiloAsincronoAsimmetrico.servizioCorrelatoNonEsistente",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"ricezioneBustaErroreDetails",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"protocolli.funzionalita.unsupported",
		MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO+"portaApplicativaNonEsistente",
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
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"consegnaEffettuata.mittenteAnonimo",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"consegnaConErrore.mittenteAnonimo",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoRichiestaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoRichiestaNessunMatch",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoRichiestaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoRichiestaEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoRichiestaInErrore",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoRispostaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoRispostaNessunMatch",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoRispostaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoRispostaEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoRispostaInErrore",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"queue.messaggioSchedulato",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.identificazioneFallita.error",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.identificazioneFallita.info",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.connettoreNonEsistente.filtro.error",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.connettoreNonEsistente.filtro.info",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.connettoreNonEsistente.nomeConnettore.error",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.connettoreNonEsistente.nomeConnettore.info",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.connettoreDefault",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.connettoreNotificaDefault",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.loadBalancer.tuttiConnettori",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.nessunConnettore",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.tuttiConnettori",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.servizioSincrono.consegnaVersoNServiziApplicativi",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.servizioSincrono.consegnaIntegrationManager",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.servizioSincrono.consegnaNonTrasparente",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.loadBalancer.sticky.identificazioneFallita",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.loadBalancer.sticky.identificazioneRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.identificazioneRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"ricezioneRestProblem",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"validazioneContenutiApplicativiRispostaNonRiuscita.warningOnly",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"headerIntegrazione.creazioneFallita",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.nessunConnettoreIdentificato.connettoreDefault",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.nessunConnettoreIdentificato.connettoreNotificaDefault",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.nessunConnettoreIdentificato.tuttiConnettori",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.nessunConnettoreIdentificato.tuttiConnettoriNotifica",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"connettoriMultipli.consegnaCondizionale.nessunConnettoreIdentificato",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoNotificaDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoNotificaNessunMatch",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoNotificaInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoNotificaEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"trasformazione.processamentoNotificaInErrore",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"ricezioneRisposta.firstAccessRequestStream",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"letturaPayloadRisposta.completata",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"letturaPayloadRisposta.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"negoziazioneToken.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"negoziazioneToken.completata",
		MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI+"negoziazioneToken.inCache",
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
		MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER+"ricezioneRichiesta.firstAccessRequestStream",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"registrazioneNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"registrazioneNonRiuscita.openspcoopAppender",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.registrazioneNonRiuscita",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.registrazioneNonRiuscita.openspcoopAppender",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"errore.bloccoServizi",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.errore.bloccoServizi",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.richiestaIngresso.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.richiestaIngresso.completato",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.richiestaUscita.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.richiestaUscita.completato",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.rispostaIngresso.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.rispostaIngresso.completato",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.rispostaUscita.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativi.rispostaUscita.completato",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativiFileTrace.richiestaIngresso.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativiFileTrace.richiestaIngresso.completato",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativiFileTrace.richiestaUscita.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativiFileTrace.richiestaUscita.completato",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativiFileTrace.rispostaIngresso.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativiFileTrace.rispostaIngresso.completato",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativiFileTrace.rispostaUscita.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO+"dumpContenutiApplicativiFileTrace.rispostaUscita.completato",
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
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE+"avvioInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE+"avvioEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE+"disabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE+"generazioneStatistiche",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE+"generazioneStatistiche.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE+"generazioneStatistiche.effettuata",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"avvioInCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"avvioEffettuato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"disabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"letturaEventi",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"letturaEventi.nonNecessaria",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"gestioneEventi.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"gestioneEventi.analisi",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"gestioneEventi.effettuata",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"gestioneEventi.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"gestioneEventi.evento",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"inizializzazione.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"inizializzazione.effettuata",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"inizializzazione.fallita",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"letturaCacheKeys",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"letturaCacheKeys.effettuata",
		MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND+"letturaCacheKeys.nonNecessaria",
		MsgDiagnosticiProperties.MSG_DIAG_OPENSPCOOP_STARTUP+"pdd",
		MsgDiagnosticiProperties.MSG_DIAG_OPENSPCOOP_STARTUP+"IntegrationManager",
		MsgDiagnosticiProperties.MSG_DIAG_OPENSPCOOP_STARTUP+"erroreGenerico",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"erroreGenerico",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"erroreGenericoMalfunzionamentoPdD",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"transactionManager.validityCheckError",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"connessioneUscita.disconnectError",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"acquisizioneLock.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"acquisizioneLock.wait.withoutOwner",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"acquisizioneLock.wait.existsOldOwner",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"acquisizioneLock.ok",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"acquisizioneLock.nonDisponibile",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"acquisizioneUnlock.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"acquisizioneUnlock.ok",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"acquisizioneUnlock.ko",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"updateLock.inCorso",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"updateLock.ok",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"updateLock.ko",
		
		// controlloTraffico (base library)
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.violata.risorsaNumeroRichieste.simultaneo",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.violata.risorsaNumeroRichieste",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.violata.risorsaOccupazioneBanda",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.violata.risorsaTempoComplessivo",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.violata.risorsaTempoMedio",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.applicabilita.nonCongestionato",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.applicabilita.controlloCongestioneDisabilitato",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.applicabilita.degradoPrestazionale.rilevato",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.applicabilita.degradoPrestazionale.nonRilevato",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.applicabilita.statoAllarme.rilevato",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.applicabilita.statoAllarme.nonRilevato",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.pddCongestionata",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.violata.risorsaNumeroRichiesteCompletateConSuccesso",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.violata.risorsaNumeroRichiesteFallite",
		MsgDiagnosticiProperties.MSG_DIAG_ALL+"controlloTraffico.policy.violata.risorsaNumeroFaultApplicativi"
	};
	
	private static final String PREFIX_MSG_DIAGNOSTICO = "org.openspcoop2.pdd.msgdiagnostico.";
	private static final String SUFFIX_LIVELLO_MSG_DIAGNOSTICO = ".livello";
	private static final String SUFFIX_CODICE_MSG_DIAGNOSTICO = ".codice";
	private static final String SUFFIX_MESSAGGIO_MSG_DIAGNOSTICO = ".messaggio";
	private static final String SUFFIX_CODICE_MODULO_MSG_DIAGNOSTICO = ".codiceModulo";
	
	
	
	
	
	
	
	
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
			logError("Errore durante l'esecuzione del metodo getLivello("+modulo+","+keyLivelloPersonalizzato+")",e);
			return -1;
		}
	}
	public String getCodice(String modulo,String keyCodicePersonalizzato){
		try{
			return MsgDiagnosticiProperties.codiciMsgDiagnosticiPersonalizzati.getValue(modulo+keyCodicePersonalizzato);
		}catch(Exception e){
			logError("Errore durante l'esecuzione del metodo getCodice("+modulo+","+keyCodicePersonalizzato+")",e);
			return null;
		}
	}
	public String getMessaggio(String modulo,String keyMsgPersonalizzato){
		try{
			return MsgDiagnosticiProperties.messaggiMsgDiagnosticiPersonalizzati.getValue(modulo+keyMsgPersonalizzato);
		}catch(Exception e){
			logError("Errore durante l'esecuzione del metodo getMessaggio("+modulo+","+keyMsgPersonalizzato+")",e);
			return null;
		}
	}
	public String getKeyMessaggio(String codice) throws CoreException{
		if(MsgDiagnosticiProperties.mappingCodiceToKeywordMsgDiagnosticiPersonalizzati.containsKey(codice)){
			Object keyword = null;
			try {
				keyword = MsgDiagnosticiProperties.mappingCodiceToKeywordMsgDiagnosticiPersonalizzati.getValue(codice);
			}catch(Exception e) {
				throw new CoreException(e.getMessage(),e);
			}
			if(keyword == null){
				throw new CoreException("MsgDiagnostico con codice ["+codice+"] non registrato?");
			}
			return (String) keyword;
		}else{
			throw new CoreException("MsgDiagnostico con codice ["+codice+"] non registrato");
		}
	}
	public String getModulo(String codice) throws CoreException{
		
		Enumeration<?> moduli = MsgDiagnosticiProperties.codiciIdentificativiFunzione.propertyNames();
		while(moduli.hasMoreElements()){
			String modulo = (String) moduli.nextElement();
			Object codiceModulo = null;
			try {
				codiceModulo = MsgDiagnosticiProperties.codiciIdentificativiFunzione.getValue(modulo);
			}catch(Exception e) {
				throw new CoreException(e.getMessage(),e);
			}
			if(codiceModulo==null){
				throw new CoreException("Modulo ["+modulo+"] senza un codice registrato");
			}
			if(((String)codiceModulo).equals(codice)){
				return modulo;
			}
			
		}
		throw new CoreException("Modulo con codice ["+codice+"] non registrato");
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
		if(!codiceDiagnosticoInitialized){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoFatal;
	}
	
	public String getCodiceDiagnosticoErrorProtocol() throws UtilsException{
		if(!codiceDiagnosticoInitialized){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoErrorProtocol;
	}
	public String getCodiceDiagnosticoErrorIntegration() throws UtilsException{
		if(!codiceDiagnosticoInitialized){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoErrorIntegration;
	}
	public String getCodiceDiagnosticoInfoProtocol() throws UtilsException{
		if(!codiceDiagnosticoInitialized){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoInfoProtocol;
	}
	public String getCodiceDiagnosticoInfoIntegration() throws UtilsException{
		if(!codiceDiagnosticoInitialized){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoInfoIntegration;
	}
	public String getCodiceDiagnosticoDebugLow() throws UtilsException{
		if(!codiceDiagnosticoInitialized){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoDebugLow;
	}
	public String getCodiceDiagnosticoDebugMedium() throws UtilsException{
		if(!codiceDiagnosticoInitialized){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoDebugMedium;
	}
	public String getCodiceDiagnosticoDebugHigh() throws UtilsException{
		if(!codiceDiagnosticoInitialized){
			initCodiceDiagnostico(this.reader);
		}
		return codiceDiagnosticoDebugHigh;
	}

}
