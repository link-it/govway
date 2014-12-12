/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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

import  org.apache.log4j.Level;

/**
 * Classe utilizzata per rappresentare i livelli di msgDiagnostico di OpenSPCoop.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class LogLevels extends org.apache.log4j.Level{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Definisce la costante per il livello di log gestito in OpenSPCoop */
	public static final int COSTANTE_TRASFORMAZIONE_LOG4J_LEVEL = 10000;
	/** Definisce un Livello di Severita' OFF per un messaggio Diagnostico: valore del field = MinValueInt */
	public static final int SEVERITA_OFF = Integer.MIN_VALUE;
	/** Definisce un Livello di Severita' FATAL per un messaggio Diagnostico: valore del field = 0 */
	public static final int SEVERITA_FATAL = 0;
	/** Definisce un Livello di Severita' ERROR-PROTOCOL per un messaggio Diagnostico: valore del field = 1 */
	public static final int SEVERITA_ERROR_PROTOCOL = 1;
	/** Definisce un Livello di Severita' ERROR-OPENSPCOOP per un messaggio Diagnostico: valore del field = 2 */
	public static final int SEVERITA_ERROR_INTEGRATION = 2;
	/** Definisce un Livello di Severita' INFO-PROTOCOL per un messaggio Diagnostico: valore del field = 3 */
	public static final int SEVERITA_INFO_PROTOCOL = 3;
	/** Definisce un Livello di Severita' INFO-INTEGRATION per un messaggio Diagnostico: valore del field = 4 */
	public static final int SEVERITA_INFO_INTEGRATION = 4;
	/** Definisce un Livello di Severita' DEBUG-LOW per un messaggio Diagnostico: valore del field = 5 */
	public static final int SEVERITA_DEBUG_LOW = 5;
	/** Definisce un Livello di Severita' DEBUG-MEDIUM per un messaggio Diagnostico: valore del field = 6 */
	public static final int SEVERITA_DEBUG_MEDIUM = 6;
	/** Definisce un Livello di Severita' DEBUG-HIGH per un messaggio Diagnostico: valore del field = 7 */
	public static final int SEVERITA_DEBUG_HIGH = 7;
	/** Definisce un Livello di Severita' ALL per un messaggio Diagnostico: valore del field = MaxValueInt */
	public static final int SEVERITA_ALL = Integer.MAX_VALUE;
	/** Definisce una funzione per riportare il valore, in valore OpenSPCoop */
	public static int toIntervalloLog4J(int valore){
		
		if(LogLevels.SEVERITA_OFF == valore)
			return valore;
		else if(LogLevels.SEVERITA_FATAL == valore)
			return valore;
		else if(LogLevels.SEVERITA_ERROR_PROTOCOL == valore)
			return valore*LogLevels.COSTANTE_TRASFORMAZIONE_LOG4J_LEVEL;
		else if(LogLevels.SEVERITA_ERROR_INTEGRATION == valore)
			return valore*LogLevels.COSTANTE_TRASFORMAZIONE_LOG4J_LEVEL;
		else if(LogLevels.SEVERITA_INFO_PROTOCOL == valore)
			return valore*LogLevels.COSTANTE_TRASFORMAZIONE_LOG4J_LEVEL;
		else if(LogLevels.SEVERITA_INFO_INTEGRATION == valore)
			return valore*LogLevels.COSTANTE_TRASFORMAZIONE_LOG4J_LEVEL;
		else if(LogLevels.SEVERITA_DEBUG_LOW == valore)
			return valore*LogLevels.COSTANTE_TRASFORMAZIONE_LOG4J_LEVEL;
		else if(LogLevels.SEVERITA_DEBUG_MEDIUM == valore)
			return valore*LogLevels.COSTANTE_TRASFORMAZIONE_LOG4J_LEVEL;
		else if(LogLevels.SEVERITA_DEBUG_HIGH == valore)
			return valore*LogLevels.COSTANTE_TRASFORMAZIONE_LOG4J_LEVEL;
		else if(LogLevels.SEVERITA_ALL == valore)
			return valore;
		else
			return -1;
	}
	
	public static int toIntervalloOpenSPCoop2(int valore){
		
		int valoreOpenspcoop2_resto = (valore % LogLevels.COSTANTE_TRASFORMAZIONE_LOG4J_LEVEL) ;
		int valoreOpenspcoop2 = valore / LogLevels.COSTANTE_TRASFORMAZIONE_LOG4J_LEVEL;
		if(valoreOpenspcoop2_resto>0){
			valoreOpenspcoop2++;
		}
		if( valoreOpenspcoop2 > LogLevels.SEVERITA_DEBUG_HIGH) {
			return LogLevels.SEVERITA_ALL;
		}else if( valoreOpenspcoop2 < LogLevels.SEVERITA_FATAL) {
			return LogLevels.SEVERITA_OFF;
		}else if((valoreOpenspcoop2>= LogLevels.SEVERITA_FATAL) && (valoreOpenspcoop2 <= LogLevels.SEVERITA_DEBUG_HIGH)){
			return valoreOpenspcoop2;
		}
		else{
			return -1;
		}
	}

	/** Definisce un Livello di Severita' FATAL per un messaggio Diagnostico: valore del field = 0 */
	public static final String LIVELLO_FATAL = "fatal";
	/** Definisce un Livello di Severita' ERROR-PROTOCOL per un messaggio Diagnostico: valore del field = 1 */
	public static final String LIVELLO_ERROR_PROTOCOL = "errorProtocol";
	/** Definisce un Livello di Severita' ERROR-INTEGRATION per un messaggio Diagnostico: valore del field = 2 */
	public static final String LIVELLO_ERROR_INTEGRATION = "errorIntegration";
	/** Definisce un Livello di Severita' INFO-PROTOCOL per un messaggio Diagnostico: valore del field = 3 */
	public static final String LIVELLO_INFO_PROTOCOL = "infoProtocol";
	/** Definisce un Livello di Severita' INFO-INTEGRATION per un messaggio Diagnostico: valore del field = 4 */
	public static final String LIVELLO_INFO_INTEGRATION = "infoIntegration";
	/** Definisce un Livello di Severita' DEBUG-LOW per un messaggio Diagnostico: valore del field = 5 */
	public static final String LIVELLO_DEBUG_LOW = "debugLow";
	/** Definisce un Livello di Severita' DEBUG-MEDIUM per un messaggio Diagnostico: valore del field = 6 */
	public static final String LIVELLO_DEBUG_MEDIUM = "debugMedium";
	/** Definisce un Livello di Severita' DEBUG-HIGH per un messaggio Diagnostico: valore del field = 7 */
	public static final String LIVELLO_DEBUG_HIGH = "debugHigh";
	/** Disattiva i Log */
	public static final String LIVELLO_OFF = "off";
	/** Abilita qualsiasi log */
	public static final String LIVELLO_ALL = "all";


	/** Definisce un Livello di Severita' FATAL per un messaggio Diagnostico: valore Log4J = 50001 */
	public static final Level LOG_LEVEL_FATAL = 
		new LogLevels(LogLevels.LIVELLO_FATAL,50001);

	/** Definisce un Livello di Severita' ERROR per un messaggio Diagnostico: valore Log4J = 40002 */
	public static final Level LOG_LEVEL_ERROR_PROTOCOL = 
		new LogLevels(LogLevels.LIVELLO_ERROR_PROTOCOL,40002);

	/** Definisce un Livello di Severita' ERROR-INTEGRATION per un messaggio Diagnostico: valore Log4J = 40001 */
	public static final Level LOG_LEVEL_ERROR_INTEGRATION = 
		new LogLevels(LogLevels.LIVELLO_ERROR_INTEGRATION,40001);

	/** Definisce un Livello di Severita' INFO-PROTOCOL per un messaggio Diagnostico: valore Log4J = 20002 */
	public static final Level LOG_LEVEL_INFO_PROTOCOL = 
		new LogLevels(LogLevels.LIVELLO_INFO_PROTOCOL,20002);

	/** Definisce un Livello di Severita' INFO-INTEGRATION per un messaggio Diagnostico: valore Log4J = 20001 */
	public static final Level LOG_LEVEL_INFO_INTEGRATION = 
		new LogLevels(LogLevels.LIVELLO_INFO_INTEGRATION,20001);

	/** Definisce un Livello di Severita' DEBUG-LOW per un messaggio Diagnostico: valore Log4J = 10003 */
	public static final Level LOG_LEVEL_DEBUG_LOW = 
		new LogLevels(LogLevels.LIVELLO_DEBUG_LOW,10003);

	/** Definisce un Livello di Severita' DEBUG-MEDIUM per un messaggio Diagnostico: valore Log4J = 10002 */
	public static final Level LOG_LEVEL_DEBUG_MEDIUM = 
		new LogLevels(LogLevels.LIVELLO_DEBUG_MEDIUM,10002);

	/** Definisce un Livello di Severita' DEBUG-HIGH per un messaggio Diagnostico: valore Log4J = 10001 */
	public static final Level LOG_LEVEL_DEBUG_HIGH = 
		new LogLevels(LogLevels.LIVELLO_DEBUG_HIGH,10001);


	/** Costruttore */
	public LogLevels(String name, int value) {
		super(value,name,value);
	}


	/** 
	 * Metodo che effettua la trasformazione da un livello di severita OpenSPCoop2 nell'analogo livello Log4J. 
	 *
	 * @param valueLivello Livello da trasformare
	 * @return Il livello di Log4J, se la trasformazione ha successo, false altrimenti.
	 */
	public static Level toLog4J(int valueLivello){
		if((valueLivello < 0) || (valueLivello > 7)){
			return LogLevels.LOG_LEVEL_INFO_PROTOCOL;
		}  else if( valueLivello == LogLevels.SEVERITA_FATAL ){
			return LogLevels.LOG_LEVEL_FATAL;
		}  else if( valueLivello == LogLevels.SEVERITA_ERROR_PROTOCOL ){
			return LogLevels.LOG_LEVEL_ERROR_PROTOCOL;
		}  else if( valueLivello == LogLevels.SEVERITA_ERROR_INTEGRATION ){
			return LogLevels.LOG_LEVEL_ERROR_INTEGRATION;
		}  else if( valueLivello == LogLevels.SEVERITA_INFO_PROTOCOL ){
			return LogLevels.LOG_LEVEL_INFO_PROTOCOL;
		}  else if( valueLivello == LogLevels.SEVERITA_INFO_INTEGRATION ){
			return LogLevels.LOG_LEVEL_INFO_INTEGRATION;
		} else if( valueLivello == LogLevels.SEVERITA_DEBUG_LOW ){
			return LogLevels.LOG_LEVEL_DEBUG_LOW;
		} else if( valueLivello == LogLevels.SEVERITA_DEBUG_MEDIUM ){
			return LogLevels.LOG_LEVEL_DEBUG_MEDIUM;
		} else if( valueLivello == LogLevels.SEVERITA_DEBUG_HIGH ){
			return LogLevels.LOG_LEVEL_DEBUG_HIGH;
		} else
			return null;
	}

	/** 
	 * Metodo che effettua la trasformazione di una stringa nell'analogo livello Log4J. 
	 *
	 * @param livello Oggetto Level da ottenere
	 * @return L'oggetto Level di OpenSPCoop se la trasformazione ha successo, null altrimenti.
	 */
	public static Level toLog4J(String livello){

		if(livello==null)
			return LogLevels.LOG_LEVEL_INFO_PROTOCOL;

		if(livello.equalsIgnoreCase(LogLevels.LIVELLO_FATAL)){
			return LogLevels.LOG_LEVEL_FATAL;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_ERROR_PROTOCOL)){
			return LogLevels.LOG_LEVEL_ERROR_PROTOCOL;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_ERROR_INTEGRATION)){
			return LogLevels.LOG_LEVEL_ERROR_INTEGRATION;
		}	
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_INFO_PROTOCOL)){
			return LogLevels.LOG_LEVEL_INFO_PROTOCOL;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_INFO_INTEGRATION)){
			return LogLevels.LOG_LEVEL_INFO_INTEGRATION;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_DEBUG_LOW)){
			return LogLevels.LOG_LEVEL_DEBUG_LOW;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_DEBUG_MEDIUM)){
			return LogLevels.LOG_LEVEL_DEBUG_MEDIUM;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_DEBUG_HIGH)){
			return LogLevels.LOG_LEVEL_DEBUG_HIGH;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_OFF)){
			return org.apache.log4j.Level.OFF;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_ALL)){
			return org.apache.log4j.Level.ALL;
		}else
			return null;
	}

	/** 
	 * Metodo che effettua la trasformazione di una stringa nell'analogo livello di OpenSPCoop2. 
	 *
	 * @param livello Oggetto Level da ottenere
	 * @return L'intero di severita di OpenSPCoop se la trasformazione ha successo, null altrimenti.
	 */
	public static int toOpenSPCoop2(String livello){

		if(livello==null)
			return LogLevels.SEVERITA_INFO_PROTOCOL;

		if(livello.equalsIgnoreCase(LogLevels.LIVELLO_FATAL)){
			return LogLevels.SEVERITA_FATAL;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_ERROR_PROTOCOL)){
			return LogLevels.SEVERITA_ERROR_PROTOCOL;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_ERROR_INTEGRATION)){
			return LogLevels.SEVERITA_ERROR_INTEGRATION;
		}	
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_INFO_PROTOCOL)){
			return LogLevels.SEVERITA_INFO_PROTOCOL;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_INFO_INTEGRATION)){
			return LogLevels.SEVERITA_INFO_INTEGRATION;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_DEBUG_LOW)){
			return LogLevels.SEVERITA_DEBUG_LOW;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_DEBUG_MEDIUM)){
			return LogLevels.SEVERITA_DEBUG_MEDIUM;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_DEBUG_HIGH)){
			return LogLevels.SEVERITA_DEBUG_HIGH;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_OFF)){
			return LogLevels.SEVERITA_OFF;
		}
		else if(livello.equalsIgnoreCase(LogLevels.LIVELLO_ALL)){
			return LogLevels.SEVERITA_ALL;
		}else
			return LogLevels.SEVERITA_ALL;
	}


	/** 
	 * Metodo che effettua la trasformazione da un livello di severita nell'analogo livello in stringa. 
	 *
	 * @param valueLivello Livello da trasformare
	 * @return Il livello in stringa se la trasformazione ha successo, false altrimenti.
	 */
	public static String toOpenSPCoop2(int valueLivello){
		if( valueLivello == LogLevels.SEVERITA_FATAL ){
			return LogLevels.LIVELLO_FATAL;
		}  else if( valueLivello == LogLevels.SEVERITA_ERROR_PROTOCOL ){
			return LogLevels.LIVELLO_ERROR_PROTOCOL;
		}  else if( valueLivello == LogLevels.SEVERITA_ERROR_INTEGRATION ){
			return LogLevels.LIVELLO_ERROR_INTEGRATION;
		}  else if( valueLivello == LogLevels.SEVERITA_INFO_PROTOCOL ){
			return LogLevels.LIVELLO_INFO_PROTOCOL;
		}  else if( valueLivello == LogLevels.SEVERITA_INFO_INTEGRATION ){
			return LogLevels.LIVELLO_INFO_INTEGRATION;
		} else if( valueLivello == LogLevels.SEVERITA_DEBUG_LOW ){
			return LogLevels.LIVELLO_DEBUG_LOW;
		} else if( valueLivello == LogLevels.SEVERITA_DEBUG_MEDIUM ){
			return LogLevels.LIVELLO_DEBUG_MEDIUM;
		} else if( valueLivello == LogLevels.SEVERITA_DEBUG_HIGH ){
			return LogLevels.LIVELLO_DEBUG_HIGH;
		} else{
			return null;
		}
	}
}





