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



package org.openspcoop2.pdd.monitor.cli;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.utils.ServerProperties;
import org.openspcoop2.pdd.monitor.Busta;
import org.openspcoop2.pdd.monitor.constants.StatoMessaggio;
import org.openspcoop2.pdd.monitor.driver.FilterSearch;
import org.openspcoop2.pdd.monitor.Messaggio;
import org.openspcoop2.pdd.monitor.BustaServizio;
import org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna;
import org.openspcoop2.pdd.monitor.StatoPdd;
import org.openspcoop2.pdd.monitor.driver.DriverMonitoraggio;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;

/**
* MonitorConsole
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class MonitorConsole {
	
	private static DriverMonitoraggio driverMonitoraggio = null;
	private static BufferedReader lettura = null;
	private static Logger log;
	public static void main(String [] args){
		
		
		// Logger
		//System.out.println("Logger, inizializzazione in corso...");
		InputStream inPropLog4j = null;
		Properties propertiesLog4j = new Properties();
		try {
			inPropLog4j = MonitorConsole.class.getResourceAsStream("/monitor_pdd.cli.log4j2.properties");
			propertiesLog4j.load(inPropLog4j);
			LoggerWrapperFactory.setLogConfiguration(propertiesLog4j);
			
		} catch(java.lang.Exception e) {
			System.out.println("Impossibile leggere i dati dal file 'monitor_pdd.cli.log4j2.properties': "+e.getMessage());
			return;
		} finally {
			try {
				if (inPropLog4j != null)
					inPropLog4j.close();
			} catch (Exception e) {}
		}
		log=LoggerWrapperFactory.getLogger("DRIVER_DB_MONITORAGGIO");	
		//System.out.println("Logger inizializzato.");
		
		//		Connessione al DB
		log.debug("Inizializzazione in corso...");

		boolean dataSourceMode = false;
		String tipoDatabase = null;
		
		String jndiName = "";
		Properties jndiProp = null;
		
		String driverJDBC = null;
		String connectionURL = null;
		String username = null;
		String password = null;
		
		InputStream inProp = null;
		try {
			inProp = MonitorConsole.class.getResourceAsStream("/monitor_pdd.cli.database.properties");
			Properties properties = new Properties();
			properties.load(inProp);
			ServerProperties readerProperties = new ServerProperties(properties);
			
			// informazioni generali
			String tmpDataSourceMode = readerProperties.getProperty("tipo", true);
			dataSourceMode = "ds".equalsIgnoreCase(tmpDataSourceMode.trim());
			log.debug("DataSource Mode...["+dataSourceMode+"]");
			tipoDatabase = readerProperties.getProperty("tipoDatabase", true);
			log.debug("TipoDatabase...["+tipoDatabase+"]");
			
			// datasource
			if(dataSourceMode){
				jndiName = readerProperties.getProperty("dataSource", true);
				log.debug("DataSource...["+jndiName+"]");
				jndiProp = readerProperties.readProperties("dataSource.property.");
				log.debug("DataSourceCtx...size["+jndiProp.size()+"]");
			}
			else{
				driverJDBC = readerProperties.getProperty("driver", true);
				connectionURL = readerProperties.getProperty("connection-url", true);
				username = readerProperties.getProperty("username", false);
				password = readerProperties.getProperty("password", false);
			}
			
		} catch(java.lang.Exception e) {
			log.error("Rilevato errore durante la lettura del file 'monitor_pdd.cli.database.properties': "+e.getMessage(),e);
			return;
		} finally {
			try {
				if (inProp != null)
					inProp.close();
			} catch (Exception e) {}
		}
		log.debug("Lettura datasource.properties terminata, jndiName: ["+jndiName+"]");
	
	

		
		
		// Load DriverMonitoraggio
		try{
			if(dataSourceMode){
				MonitorConsole.driverMonitoraggio = new DriverMonitoraggio(jndiName,tipoDatabase,jndiProp);
			}
			else{
				MonitorConsole.driverMonitoraggio = new DriverMonitoraggio(connectionURL, driverJDBC, username, password, tipoDatabase);
			}
		} catch(java.lang.Exception e) {
			log.error("Impossibile inizializzare il driver di monitoraggio: "+e.getMessage(),e);
			return;
		}
		
		if( args.length ==1 ) {
			String cli_parameter=args[0];
			
			if("--totaleMessaggi".equalsIgnoreCase(cli_parameter))
				countMessages_engine_output_parsabile();
			else if("--status".equalsIgnoreCase(cli_parameter))
				countMessages_engine_output_parsabile(null,null,null,null,-1,false);
		} else
			menu();
		
		
	}

		
		
		     
	private static void menu(){

		String scelta="";
		// Inizializzo BufferedReader
		try{
			lettura = new BufferedReader(new InputStreamReader(System.in));
		}catch(Exception e){
			System.out.println("\nErrore, BufferedReader from console non avviato: "+e.getMessage());
			return;
		}
		
		while( !(scelta.equals("27")) ){

			System.out.println("\n############ Console di OpenSPCoop ##############\n");

			System.out.println("Seleziona l´operazione desiderata:");
			System.out.println("1 - Numero Messaggi");
			System.out.println("2 - Numero Messaggi (filtro per Servizio)");
			System.out.println("3 - Numero Messaggi (filtro per Azione)");
			System.out.println("4 - Numero Messaggi (filtro per Servizio+Azione)");
			System.out.println("5 - Numero Messaggi (filtro per Contenuto Applicativo)");
			System.out.println("6 - Numero Messaggi (filtro per StatoMessaggio [consegna/spedizione/processamento/cancellato])");
			System.out.println("7 - Numero Messaggi (filtro per OraRegistrazione)");
			System.out.println("8 - Numero Messaggi (filtro per Messaggi in attesa di Riscontro)");
			
			System.out.println("9 - Lista Messaggi");
			System.out.println("10 - Lista Messaggi (filtro per Servizio)");
			System.out.println("11 - Lista Messaggi (filtro per Azione)");
			System.out.println("12 - Lista Messaggi (filtro per Servizio+Azione)");
			System.out.println("13 - Lista Messaggi (filtro per Contenuto Applicativo)");
			System.out.println("14 - Lista Messaggi (filtro per StatoMessaggio [consegna/spedizione/processamento/cancellato])");
			System.out.println("15 - Lista Messaggi (filtro per OraRegistrazione)");
			System.out.println("16 - Lista Messaggi (filtro per Offset/Limit)");
			System.out.println("17 - Lista Messaggi (filtro per Messaggi in attesa di Riscontro)");
			
			System.out.println("18 - Eliminazione singolo Messaggio");
			
			System.out.println("19 - Eliminazione Messaggi");
			System.out.println("20 - Eliminazione Messaggi (filtro per Servizio)");
			System.out.println("21 - Eliminazione Messaggi (filtro per Azione)");
			System.out.println("22 - Eliminazione Messaggi (filtro per Servizio+Azione)");
			System.out.println("23 - Eliminazione Messaggi (filtro per Contenuto Applicativo)");
			System.out.println("24 - Eliminazione Messaggi (filtro per StatoMessaggio [consegna/spedizione/processamento/cancellato])");
			System.out.println("25 - Eliminazione Messaggi (filtro per OraRegistrazione)");
			System.out.println("26 - Eliminazione Messaggi (filtro per Messaggi in attesa di Riscontro)");
			
			System.out.println("27 - Exit");	
			System.out.print("> ");
			try{
				scelta=lettura.readLine();
			}catch(java.io.IOException e){
				scelta="";
			}
			System.out.println();
			if (scelta.equals("1")){
				countMessages();
			} 	
			else if (scelta.equals("2")){
				countMessagesByService();
			} 
			else if (scelta.equals("3")){
				countMessagesByAction();
			} 
			else if (scelta.equals("4")){
				countMessagesByServiceAndAction();
			} 
			else if (scelta.equals("5")){
				countMessagesByContenutoApplicativo();
			} 
			else if (scelta.equals("6")){
				countMessagesByStatoMessaggio();
			} 
			else if (scelta.equals("7")){
				countMessagesByOraRegistrazione();
			} 
			else if (scelta.equals("8")){
				countMessagesByAttesaRiscontro();
			}
			
			
			else if (scelta.equals("9")){
				listMessages();
			} 	
			else if (scelta.equals("10")){
				listMessagesByService();
			} 
			else if (scelta.equals("11")){
				listMessagesByAction();
			} 
			else if (scelta.equals("12")){
				listMessagesByServiceAndAction();
			} 
			else if (scelta.equals("13")){
				listMessagesByContenutoApplicativo();
			} 
			else if (scelta.equals("14")){
				listMessagesByStatoMessaggio();
			} 
			else if (scelta.equals("15")){
				listMessagesByOraRegistrazione();
			}
			else if (scelta.equals("16")){
				listMessagesByOffsetLimit();
			}
			else if (scelta.equals("17")){
				listMessagesByAttesaRiscontro();
			}
			
			
			
			else if (scelta.equals("18")){
				deleteMessage();
			} 
			
			
			else if (scelta.equals("19")){
				deleteAllMessages();
			} 
			else if (scelta.equals("20")){
				deleteAllMessagesByService();
			}  
			else if (scelta.equals("21")){
				deleteAllMessagesByAction();
			}  
			else if (scelta.equals("22")){
				deleteAllMessagesByServiceAndAction();
			}  
			else if (scelta.equals("23")){
				deleteAllMessagesByContenutoApplicativo();
			} 
			else if (scelta.equals("24")){
				deleteAllMessagesByStatoMessaggio();
			} 
			else if (scelta.equals("25")){
				deleteAllMessagesByOraRegistrazione();
			} 
			else if (scelta.equals("26")){
				deleteAllMessagesByAttesaRiscontro();
			} 
			
			else if (scelta.equals("27")){
				return;
			}  
			else System.out.println("ERROR, Scelta non corretta\n\n");
		}
	}
	
	private static void countMessages(){
		countMessages_engine(null,null,null,null,-1,false);
	}
	
	
	private static void countMessagesByService(){
		System.out.println("Devi specificare un valore per il filtro per il servizio");
		String servizio = "";
		while(servizio.equals("")){
			try{
				servizio=lettura.readLine();
			}catch(java.io.IOException e){
				servizio="";
			}
			if(servizio=="")
				System.out.println("Devi specificare un valore per il filtro per il servizio");
		}
		countMessages_engine(servizio,null,null,null,-1,false);
	}
	
	private static void countMessagesByAction(){
		System.out.println("Devi specificare un valore per il filtro per l'azione");
		String azione = "";
		while(azione.equals("")){
			try{
				azione=lettura.readLine();
			}catch(java.io.IOException e){
				azione="";
			}
			if(azione=="")
				System.out.println("Devi specificare un valore per il filtro per l'azione");
		}
		countMessages_engine(null,azione,null,null,-1,false);
	}
	
	private static void countMessagesByServiceAndAction(){
		System.out.println("Devi specificare un valore per il filtro per il servizio");
		String servizio = "";
		while(servizio.equals("")){
			try{
				servizio=lettura.readLine();
			}catch(java.io.IOException e){
				servizio="";
			}
			if(servizio=="")
				System.out.println("Devi specificare un valore per il filtro per servizio");
		}
		System.out.println("Devi specificare un valore per il filtro per l'azione");
		String azione = "";
		while(azione.equals("")){
			try{
				azione=lettura.readLine();
			}catch(java.io.IOException e){
				azione="";
			}
			if(azione=="")
				System.out.println("Devi specificare un valore per il filtro per l'azione");
		}
		countMessages_engine(servizio,azione,null,null,-1,false);
	}
	private static void countMessagesByContenutoApplicativo(){
		System.out.println("Devi specificare un valore da cercare tra i contenuti applicativi");
		String filtro = "";
		while(filtro.equals("")){
			try{
				filtro=lettura.readLine();
			}catch(java.io.IOException e){
				filtro="";
			}
			if(filtro=="")
				System.out.println("Devi specificare un valore da cercare tra i contenuti applicativi");
		}
		countMessages_engine(null,null,filtro,null,-1,false);
	}
	
	private static void countMessagesByStatoMessaggio(){
		System.out.println("Devi specificare un valore per lo stato del messaggio tra i seguenti valori: consegna/spedizione/processamento/cancellato");
		String filtro = "";
		while(filtro.equals("")){
			try{
				filtro=lettura.readLine();
			}catch(java.io.IOException e){
				filtro="";
			}
			if(filtro=="")
				System.out.println("Devi specificare un valore per lo stato del messaggio tra i seguenti valori: consegna/spedizione/processamento/cancellato");
		}
		countMessages_engine(null,null,null,filtro,-1,false);
	}
	
	private static void countMessagesByOraRegistrazione(){
		System.out.println("Devi specificare un intero che identifica i messaggi piu' vecchi di x secondi");
		String filtro = "";
		long value = -1;
		while(filtro.equals("")){
			try{
				filtro=lettura.readLine();
			}catch(java.io.IOException e){
				filtro="";
			}
			if(filtro=="")
				System.out.println("Devi specificare un intero che identifica i messaggi piu' vecchi di x secondi");
			try{
				value = Long.parseLong(filtro);
			}catch(Exception e){
				System.out.println("Devi specificare un intero che identifica i messaggi piu' vecchi di x secondi: "+e.getMessage());
				filtro="";
			}
		}
		countMessages_engine(null,null,null,null,value,false);
	}
	
	private static void countMessagesByAttesaRiscontro(){
		countMessages_engine(null,null,null,null,-1,true);
	}
	
	private static void countMessages_engine_output_parsabile(){
		
		
		try{	
			if(MonitorConsole.driverMonitoraggio==null){
				System.out.println("DriverMonitoraggio is null");
				return;
			}
			
			long totaleMessaggi = MonitorConsole.driverMonitoraggio.getTotaleMessaggiInGestione();
			
			System.out.print(totaleMessaggi);
						
		} catch(Exception e) {
			System.out.println("Errore durante la lettura del numero dei messaggi: "+e.getMessage());

		}

	}
	private static void countMessages_engine_output_parsabile(String servizio,String azione,String espressione,
			String filtroStato,long secondi,boolean attesaRiscontri){
		
		
		try{	
			if(MonitorConsole.driverMonitoraggio==null){
				System.out.println("DriverMonitoraggio is null");
				return;
			}
			
			FilterSearch filterSearchMonitor = new FilterSearch();
			if(servizio!=null || azione!=null || attesaRiscontri){
				Busta busta = new Busta();
				if(servizio!=null){
					BustaServizio servizioMonitor = new BustaServizio();
					servizioMonitor.setNome(servizio);
					busta.setServizio(servizioMonitor);
				}
				if(azione!=null)
					busta.setAzione(azione);
				busta.setAttesaRiscontro(attesaRiscontri);
				filterSearchMonitor.setBusta(busta);
			}
			if(espressione!=null){
				filterSearchMonitor.setMessagePattern(espressione);
			}
			if(filtroStato!=null){
				filterSearchMonitor.setStato(StatoMessaggio.toEnumConstant(filtroStato));
			}
			if(secondi!=-1){
				filterSearchMonitor.setSoglia(secondi);
			}
			
		
			StatoPdd stato = MonitorConsole.driverMonitoraggio.getStatoRichiestePendenti(filterSearchMonitor);
			
			//
			log.info("X:Quantita,TempoMedioAttesa,TempoMassimoAttesa");
			System.out.print("MC:");
			System.out.print(""+stato.getNumMsgInConsegna());
			if(stato.getNumMsgInConsegna()>0){
				System.out.print(","+stato.getTempoMedioAttesaInConsegna());
				System.out.println(","+stato.getTempoMaxAttesaInConsegna());
			}
			else {
				System.out.print(",0");
				System.out.println(",0");
			}
			System.out.print("MS:");
			System.out.print(""+stato.getNumMsgInSpedizione());
			if(stato.getNumMsgInSpedizione()>0){
				System.out.print(","+stato.getTempoMedioAttesaInSpedizione());
				System.out.println(","+stato.getTempoMaxAttesaInSpedizione());
			}
			else {
				System.out.print(",0");
				System.out.println(",0");
			}
			
			System.out.print("MP:");
			System.out.print(""+stato.getNumMsgInProcessamento());
			if(stato.getNumMsgInProcessamento()>0){
				System.out.print(","+stato.getTempoMedioAttesaInProcessamento());
				System.out.println(","+stato.getTempoMaxAttesaInProcessamento());
			}
			else {
				System.out.print(",0");
				System.out.println(",0");
			}
			
			System.out.print("TM:");
			System.out.print(""+stato.getTotMessaggi());
			if(stato.getTotMessaggi()>0){
				System.out.print(","+stato.getTempoMedioAttesa());
				System.out.println(","+stato.getTempoMaxAttesa());
			}
			else {
				System.out.print(",0");
				System.out.println(",0");
			}
			
			System.out.print("TMD:");
			System.out.println(""+stato.getTotMessaggiDuplicati());
			
			
			
		} catch(Exception e) {
			System.out.println("Errore durante la lettura del numero dei messaggi: "+e.getMessage());

		}

	}
	private static void countMessages_engine(String servizio,String azione,String espressione,
			String filtroStato,long secondi,boolean attesaRiscontri){
		
		System.out.println("\n\n");
		System.out.println("-----------------------------------------------");
		System.out.println("          Numero Messaggi in coda              ");
		System.out.println("-----------------------------------------------");
		
		try{	
			if(MonitorConsole.driverMonitoraggio==null){
				System.out.println("DriverMonitoraggio is null");
				return;
			}
			
			FilterSearch filterSearchMonitor = new FilterSearch();
			if(servizio!=null || azione!=null || attesaRiscontri){
				Busta busta = new Busta();
				if(servizio!=null){
					BustaServizio servizioMonitor = new BustaServizio();
					servizioMonitor.setNome(servizio);
					busta.setServizio(servizioMonitor);
				}
				if(azione!=null)
					busta.setAzione(azione);
				busta.setAttesaRiscontro(attesaRiscontri);
				filterSearchMonitor.setBusta(busta);
			}
			if(espressione!=null){
				filterSearchMonitor.setMessagePattern(espressione);
			}
			if(filtroStato!=null){
				filterSearchMonitor.setStato(StatoMessaggio.toEnumConstant(filtroStato));
			}
			if(secondi!=-1){
				filterSearchMonitor.setSoglia(secondi);
			}
			
			System.out.println("Effettuo query...");
			StatoPdd stato = MonitorConsole.driverMonitoraggio.getStatoRichiestePendenti(filterSearchMonitor);
			System.out.println("Query effettuata.");	
			
			System.out.println("");
			System.out.println("Messaggi in Consegna:");
			System.out.println("\tNumero: "+stato.getNumMsgInConsegna());
			if(stato.getNumMsgInConsegna()>0){
				System.out.println("\tAttesaMedia: "+Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMedioAttesaInConsegna()*1000,false));
				System.out.println("\tAttesaMassima: "+Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMaxAttesaInConsegna()*1000,false));
			}
			System.out.println("");
			System.out.println("Messaggi in Spedizione:");
			System.out.println("\tNumero: "+stato.getNumMsgInSpedizione());
			if(stato.getNumMsgInSpedizione()>0){
				System.out.println("\tAttesaMedia: "+Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMedioAttesaInSpedizione()*1000,false));
				System.out.println("\tAttesaMassima: "+Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMaxAttesaInSpedizione()*1000,false));
			}
			System.out.println("");
			System.out.println("Messaggi in Processamento:");
			System.out.println("\tNumero: "+stato.getNumMsgInProcessamento());
			if(stato.getNumMsgInProcessamento()>0){
				System.out.println("\tAttesaMedia: "+Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMedioAttesaInProcessamento()*1000,false));
				System.out.println("\tAttesaMassima: "+Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMaxAttesaInProcessamento()*1000,false));
			}
			System.out.println("");
			System.out.println("Totale Messaggi:");
			System.out.println("\tNumero: "+stato.getTotMessaggi());
			if(stato.getTotMessaggi()>0){
				System.out.println("\tAttesaMedia: "+Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMedioAttesa()*1000,false));
				System.out.println("\tAttesaMassima: "+Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMaxAttesa()*1000,false));
			}
			System.out.println("");
			System.out.println("Totale Messaggi Duplicati:");
			System.out.println("\tNumero: "+stato.getTotMessaggiDuplicati());
			
			System.out.println("-----------------------------------------------");
			
		} catch(Exception e) {
			System.out.println("Errore durante la lettura del numero dei messaggi: "+e.getMessage());
			System.out.println("-----------------------------------------------");
		}

	}
	
	
	private static void listMessages(){
		listMessages_engine(null,null,null,null,-1,-1,-1,false);
	}
	
	
	private static void listMessagesByService(){
		System.out.println("Devi specificare un valore per il filtro per il servizio");
		String servizio = "";
		while(servizio.equals("")){
			try{
				servizio=lettura.readLine();
			}catch(java.io.IOException e){
				servizio="";
			}
			if(servizio=="")
				System.out.println("Devi specificare un valore per il filtro per il servizio");
		}
		listMessages_engine(servizio,null,null,null,-1,-1,-1,false);
	}
	
	private static void listMessagesByAction(){
		System.out.println("Devi specificare un valore per il filtro per l'azione");
		String azione = "";
		while(azione.equals("")){
			try{
				azione=lettura.readLine();
			}catch(java.io.IOException e){
				azione="";
			}
			if(azione=="")
				System.out.println("Devi specificare un valore per il filtro per l'azione");
		}
		listMessages_engine(null,azione,null,null,-1,-1,-1,false);
	}
	
	private static void listMessagesByServiceAndAction(){
		System.out.println("Devi specificare un valore per il filtro per il servizio");
		String servizio = "";
		while(servizio.equals("")){
			try{
				servizio=lettura.readLine();
			}catch(java.io.IOException e){
				servizio="";
			}
			if(servizio=="")
				System.out.println("Devi specificare un valore per il filtro per servizio");
		}
		System.out.println("Devi specificare un valore per il filtro per l'azione");
		String azione = "";
		while(azione.equals("")){
			try{
				azione=lettura.readLine();
			}catch(java.io.IOException e){
				azione="";
			}
			if(azione=="")
				System.out.println("Devi specificare un valore per il filtro per l'azione");
		}
		listMessages_engine(servizio,azione,null,null,-1,-1,-1,false);
	}
	private static void listMessagesByContenutoApplicativo(){
		System.out.println("Devi specificare un valore da cercare tra i contenuti applicativi");
		String filtro = "";
		while(filtro.equals("")){
			try{
				filtro=lettura.readLine();
			}catch(java.io.IOException e){
				filtro="";
			}
			if(filtro=="")
				System.out.println("Devi specificare un valore da cercare tra i contenuti applicativi");
		}
		listMessages_engine(null,null,filtro,null,-1,-1,-1,false);
	}
	
	private static void listMessagesByStatoMessaggio(){
		System.out.println("Devi specificare un valore per lo stato del messaggio tra i seguenti valori: consegna/spedizione/processamento/cancellato");
		String filtro = "";
		while(filtro.equals("")){
			try{
				filtro=lettura.readLine();
			}catch(java.io.IOException e){
				filtro="";
			}
			if(filtro=="")
				System.out.println("Devi specificare un valore per lo stato del messaggio tra i seguenti valori: consegna/spedizione/processamento/cancellato");
		}
		listMessages_engine(null,null,null,filtro,-1,-1,-1,false);
	}
	
	private static void listMessagesByOraRegistrazione(){
		System.out.println("Devi specificare un intero che identifica i messaggi piu' vecchi di x secondi");
		String filtro = "";
		long value = -1;
		while(filtro.equals("")){
			try{
				filtro=lettura.readLine();
			}catch(java.io.IOException e){
				filtro="";
			}
			if(filtro=="")
				System.out.println("Devi specificare un intero che identifica i messaggi piu' vecchi di x secondi");
			try{
				value = Long.parseLong(filtro);
			}catch(Exception e){
				System.out.println("Devi specificare un intero che identifica i messaggi piu' vecchi di x secondi: "+e.getMessage());
				filtro="";
			}
		}
		listMessages_engine(null,null,null,null,value,-1,-1,false);
	}
	
	private static void listMessagesByOffsetLimit(){
		System.out.println("Devi specificare un intero che identifica un limit");
		String filtro = "";
		long limit = -1;
		while(filtro.equals("")){
			try{
				filtro=lettura.readLine();
			}catch(java.io.IOException e){
				filtro="";
			}
			if(filtro=="")
				System.out.println("Devi specificare un intero che identifica un limit");
			try{
				limit = Long.parseLong(filtro);
			}catch(Exception e){
				System.out.println("Devi specificare un intero che identifica un limit: "+e.getMessage());
				filtro="";
			}
		}
		System.out.println("Devi specificare un intero che identifica un offset");
		filtro = "";
		long offset = -1;
		while(filtro.equals("")){
			try{
				filtro=lettura.readLine();
			}catch(java.io.IOException e){
				filtro="";
			}
			if(filtro=="")
				System.out.println("Devi specificare un intero che identifica un offset");
			try{
				offset = Long.parseLong(filtro);
			}catch(Exception e){
				System.out.println("Devi specificare un intero che identifica un offset: "+e.getMessage());
				filtro="";
			}
		}
		listMessages_engine(null,null,null,null,-1,limit,offset,false);
	}
	
	private static void listMessagesByAttesaRiscontro(){
		listMessages_engine(null,null,null,null,-1,-1,-1,true);
	}
	
	private static void listMessages_engine(String servizio,String azione,String espressione,
			String filtroStato,long secondi,long limit,long offset,boolean attesaRiscontri){
		
		System.out.println("\n\n");
		System.out.println("-----------------------------------------------");
		System.out.println("               Lista Messaggi                  ");
		System.out.println("-----------------------------------------------");
		
		try{

			if(MonitorConsole.driverMonitoraggio==null){
				System.out.println("DriverMonitoraggio is null");
				return;
			}
			
			FilterSearch filterSearchMonitor = new FilterSearch();
			if(servizio!=null || azione!=null || attesaRiscontri){
				Busta busta = new Busta();
				if(servizio!=null){
					BustaServizio servizioMonitor = new BustaServizio();
					servizioMonitor.setNome(servizio);
					busta.setServizio(servizioMonitor);
				}
				if(azione!=null)
					busta.setAzione(azione);
				busta.setAttesaRiscontro(attesaRiscontri);
				filterSearchMonitor.setBusta(busta);
			}
			if(espressione!=null){
				filterSearchMonitor.setMessagePattern(espressione);
			}
			if(filtroStato!=null){
				filterSearchMonitor.setStato(StatoMessaggio.toEnumConstant(filtroStato));
			}
			if(secondi!=-1){
				filterSearchMonitor.setSoglia(secondi);
			}
			if(offset!=-1 && limit!=-1){
				filterSearchMonitor.setLimit(limit);
				filterSearchMonitor.setOffset(offset);
			}
			
			System.out.println("Effettuo query...");
			@SuppressWarnings("unused")
			long countMessaggio = MonitorConsole.driverMonitoraggio.countListaRichiestePendenti(filterSearchMonitor);
			List<Messaggio> listMessaggi = MonitorConsole.driverMonitoraggio.getListaRichiestePendenti(filterSearchMonitor);
			System.out.println("Query effettuata.");	
			
			if(listMessaggi!=null){
				List<Messaggio> lista = listMessaggi;
				for(int i=0; i<lista.size(); i++){
					System.out.println("id e-Gov: "+lista.get(i).getIdMessaggio());
					if(lista.get(i).getDettaglio()!=null){
						System.out.println("Tipo: "+lista.get(i).getDettaglio().getTipo());
						System.out.println("Modulo: "+lista.get(i).getDettaglio().getIdModulo());
						if(lista.get(i).getDettaglio().getIdCorrelazioneApplicativa()!=null){
							System.out.println("id Correlazione Applicativa: "+lista.get(i).getDettaglio().getIdCorrelazioneApplicativa());
						}
						if(lista.get(i).getDettaglio().getProprietaList()!=null && lista.get(i).getDettaglio().getProprietaList().size()>0){
							for (int j = 0; j < lista.get(i).getDettaglio().getProprietaList().size(); j++) {
								String name = lista.get(i).getDettaglio().getProprieta(j).getNome();
								System.out.println(name+": "+lista.get(i).getDettaglio().getProprieta(j).getValore());
							}
						}
						System.out.println("Stato: "+lista.get(i).getStato());
					}
					System.out.println("Ora registrazione: "+lista.get(i).getOraRegistrazione().toString());
					if(lista.get(i).getBustaInfo()!=null){
						if(lista.get(i).getBustaInfo().getMittente()!=null){
							System.out.println("Soggetto mittente (tipo): "+lista.get(i).getBustaInfo().getMittente().getTipo());
							System.out.println("Soggetto mittente (nome): "+lista.get(i).getBustaInfo().getMittente().getNome());
						}
						if(lista.get(i).getBustaInfo().getDestinatario()!=null){
							System.out.println("Soggetto destinatario (tipo): "+lista.get(i).getBustaInfo().getDestinatario().getTipo());
							System.out.println("Soggetto destinatario (nome): "+lista.get(i).getBustaInfo().getDestinatario().getNome());
						}
						if(lista.get(i).getBustaInfo().getServizio()!=null){
							System.out.println("Servizio (tipo): "+lista.get(i).getBustaInfo().getServizio().getTipo());
							System.out.println("Servizio (nome): "+lista.get(i).getBustaInfo().getServizio().getNome());
						}
						if(lista.get(i).getBustaInfo().getAzione()!=null)
							System.out.println("Azione: "+lista.get(i).getBustaInfo().getAzione());
			            else
			            	System.out.println("Azione: non presente");
						if(lista.get(i).getBustaInfo().getProfiloCollaborazione()!=null)
							System.out.println("Profilo di Collaborazione: "+lista.get(i).getBustaInfo().getProfiloCollaborazione());
						else
							System.out.println("Profilo di Collaborazione: non presente, busta di servizio");
						if(lista.get(i).getBustaInfo().getRiferimentoMessaggio()!=null)
							System.out.println("Rifermento Messaggio: "+lista.get(i).getBustaInfo().getRiferimentoMessaggio());
						if(lista.get(i).getBustaInfo().getCollaborazione()!=null)
							System.out.println("Collaborazione: "+lista.get(i).getBustaInfo().getCollaborazione());
						System.out.println("Attesa Riscontro: "+lista.get(i).getBustaInfo().isAttesaRiscontro());					
					}
					if(lista.get(i).getDettaglio()!=null){
						if(lista.get(i).getDettaglio().getServizioApplicativoConsegnaList()!=null){
							List<ServizioApplicativoConsegna> sc = lista.get(i).getDettaglio().getServizioApplicativoConsegnaList();
							for(int j=0; j<sc.size(); j++){
								System.out.println("\tServizioApplicativo: "+sc.get(j).getNome());
								System.out.println("\t\tsbustamento: "+sc.get(j).isSbustamentoSoap());
								System.out.println("\t\tautorizzazione I.M.: "+sc.get(j).isAutorizzazioneIntegrationManager());
								System.out.println("\t\ttipo consegna: "+sc.get(j).getTipoConsegna());
								if(sc.get(j).getErroreProcessamento()!=null)
									System.out.println("\terrore processamento: "+sc.get(j).getErroreProcessamento());
							}
						}else{
							if(lista.get(i).getDettaglio()!=null && lista.get(i).getDettaglio().getErroreProcessamento()!=null)
								System.out.println("Errore di Processamento:"+lista.get(i).getDettaglio().getErroreProcessamento());
						}
					}
					System.out.println("-----------------------------------------------");
				}
			} else{
				System.out.println("Messaggi non presenti");
                System.out.println("-----------------------------------------------");
			}
			
			
		} catch(Exception e) {
			System.out.println("Errore durante la lettura dei dati dei messaggi: "+e.getMessage());
			System.out.println("-----------------------------------------------");
		}
	}
	
	private static void deleteMessage(){
		
		System.out.println("Devi specificare un identificatore del messaggio da eliminare");
		String id = "";
		while(id.equals("")){
			try{
				id=lettura.readLine();
			}catch(java.io.IOException e){
				id="";
			}
			if(id=="")
				System.out.println("Devi specificare un identificatore del messaggio da eliminare");
		}
		
		
		System.out.println("\n\n");
		System.out.println("-----------------------------------------------");
		System.out.println("       Eliminazione singolo Messaggio          ");
		System.out.println("-----------------------------------------------");
		
		try{	
			if(MonitorConsole.driverMonitoraggio==null){
				System.out.println("DriverMonitoraggio is null");
				return;
			}
			
			FilterSearch filter = new FilterSearch();
			filter.setIdMessaggio(id);
			
			long msgEliminati = MonitorConsole.driverMonitoraggio.deleteRichiestePendenti(filter);
			if(msgEliminati!=0){
				if(msgEliminati==1)
					System.out.println("Il messaggio con id: "+id+" e' stato eliminato");
				else
					System.out.println("I messaggi sia di OUTBOX che di INBOX con id: "+id+" sono stati eliminati");
					
			}else{
				System.out.println("Il messaggio con id: "+id+" non esiste");
			}
			System.out.println("-----------------------------------------------");
			
		} catch(Exception e) {
			System.out.println("Errore durante l'eliminazione di un messaggio: "+e.getMessage());
			System.out.println("-----------------------------------------------");
		}
	}
	
	private static void deleteAllMessages(){
		deleteAllMessages_engine(null,null,null,null,-1,false);
	}
	
	private static void deleteAllMessagesByService(){
		System.out.println("Devi specificare un valore per il  servizio");
		String servizio = "";
		while(servizio.equals("")){
			try{
				servizio=lettura.readLine();
			}catch(java.io.IOException e){
				servizio="";
			}
			if(servizio=="")
				System.out.println("Devi specificare un valore per il servizio");
		}
		deleteAllMessages_engine(servizio,null,null,null,-1,false);
	}
	
	private static void deleteAllMessagesByAction(){
		System.out.println("Devi specificare un valore per l'azione");
		String azione = "";
		while(azione.equals("")){
			try{
				azione=lettura.readLine();
			}catch(java.io.IOException e){
				azione="";
			}
			if(azione=="")
				System.out.println("Devi specificare un valore per l'azione");
		}
		deleteAllMessages_engine(null,azione,null,null,-1,false);
	}
	
	private static void deleteAllMessagesByServiceAndAction(){
		System.out.println("Devi specificare un valore per il servizio");
		String servizio = "";
		while(servizio.equals("")){
			try{
				servizio=lettura.readLine();
			}catch(java.io.IOException e){
				servizio="";
			}
			if(servizio=="")
				System.out.println("Devi specificare un valore per il servizio");
		}
		System.out.println("Devi specificare un valore per l'azione");
		String azione = "";
		while(azione.equals("")){
			try{
				azione=lettura.readLine();
			}catch(java.io.IOException e){
				azione="";
			}
			if(azione=="")
				System.out.println("Devi specificare un valore per l'azione");
		}
		deleteAllMessages_engine(servizio,azione,null,null,-1,false);
	}
	private static void deleteAllMessagesByContenutoApplicativo(){
		System.out.println("Devi specificare un valore da cercare tra i contenuti applicativi");
		String filtro = "";
		while(filtro.equals("")){
			try{
				filtro=lettura.readLine();
			}catch(java.io.IOException e){
				filtro="";
			}
			if(filtro=="")
				System.out.println("Devi specificare un valore da cercare tra i contenuti applicativi");
		}
		deleteAllMessages_engine(null,null,filtro,null,-1,false);
	}
	private static void deleteAllMessagesByStatoMessaggio(){
		System.out.println("Devi specificare un valore per lo stato del messaggio tra i seguenti valori: consegna/spedizione/processamento/cancellato");
		String filtro = "";
		while(filtro.equals("")){
			try{
				filtro=lettura.readLine();
			}catch(java.io.IOException e){
				filtro="";
			}
			if(filtro=="")
				System.out.println("Devi specificare un valore per lo stato del messaggio tra i seguenti valori: consegna/spedizione/processamento/cancellato");
		}
		deleteAllMessages_engine(null,null,null,filtro,-1,false);
	}
	private static void deleteAllMessagesByOraRegistrazione(){
		System.out.println("Devi specificare un intero che identifica i messaggi piu' vecchi di x secondi");
		String filtro = "";
		long value = -1;
		while(filtro.equals("")){
			try{
				filtro=lettura.readLine();
			}catch(java.io.IOException e){
				filtro="";
			}
			if(filtro=="")
				System.out.println("Devi specificare un intero che identifica i messaggi piu' vecchi di x secondi");
			try{
				value = Long.parseLong(filtro);
			}catch(Exception e){
				System.out.println("Devi specificare un intero che identifica i messaggi piu' vecchi di x secondi: "+e.getMessage());
				filtro="";
			}
		}
		deleteAllMessages_engine(null,null,null,null,value,false);
	}
	
	private static void deleteAllMessagesByAttesaRiscontro(){
		deleteAllMessages_engine(null,null,null,null,-1,true);
	}
	
	private static void deleteAllMessages_engine(String servizio,String azione,String espressione,
			String filtroStato,long secondi,boolean attesaRiscontri){
		
		System.out.println("\n\n");
		System.out.println("-----------------------------------------------");
		System.out.println("             Eliminazione Messaggi             ");
		System.out.println("-----------------------------------------------");
		
		try{	
			if(MonitorConsole.driverMonitoraggio==null){
				System.out.println("DriverMonitoraggio is null");
				return;
			}
			
			FilterSearch filterSearchMonitor = new FilterSearch();
			if(servizio!=null || azione!=null || attesaRiscontri){
				Busta busta = new Busta();
				if(servizio!=null){
					BustaServizio servizioMonitor = new BustaServizio();
					servizioMonitor.setNome(servizio);
					busta.setServizio(servizioMonitor);
				}
				if(azione!=null)
					busta.setAzione(azione);
				busta.setAttesaRiscontro(attesaRiscontri);
				filterSearchMonitor.setBusta(busta);
			}
			if(espressione!=null){
				filterSearchMonitor.setMessagePattern(espressione);
			}
			if(filtroStato!=null){
				filterSearchMonitor.setStato(StatoMessaggio.toEnumConstant(filtroStato));
			}
			if(secondi!=-1){
				filterSearchMonitor.setSoglia(secondi);
			}
			
			System.out.println("Effettuo query...");
			long messaggiEliminati = MonitorConsole.driverMonitoraggio.deleteRichiestePendenti(filterSearchMonitor);
			if(messaggiEliminati>0)
				System.out.println("Sono stati eliminati "+messaggiEliminati+" messaggi, per il livello di filtro impostato");
			else                                 
				System.out.println("Non esistevano messaggi da eliminare, per il livello di filtro impostato");                         
			System.out.println("-----------------------------------------------");
		} catch(Exception e) {
			System.out.println("Errore durante la eliminazione dei messaggi: "+e.getMessage());
			System.out.println("-----------------------------------------------");
		}
	}
	
}
