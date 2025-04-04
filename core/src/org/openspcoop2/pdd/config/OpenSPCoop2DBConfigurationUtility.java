/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.config;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * Inizializza una configurazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class OpenSPCoop2DBConfigurationUtility {

	private static final String TIPOLOGIA_ELIMINAZIONE_FRUIZIONE = "fruizione";
	private static final String TIPOLOGIA_ELIMINAZIONE_EROGAZIONE = "erogazione";

	private static void logError(Logger log,String msg, Exception e) {
		if(log!=null) {
			log.error(msg, e);
		}
	}
	private static void logDebug(Logger log,String msg) {
		if(log!=null) {
			log.debug(msg);
		}
	}
	
	public static void main(String[] args) throws Exception {

		
		                               
		if (args.length  < 4) {
			String errorMsg = "ERROR, Usage:  java OpenSPCoop2DBConfigurationUtility proprietaAccessoDatabase tipologiaEliminazione(fruzione/erogazione) servizio(tipo/nome) soggettoErogatoreServizio(tipo/nome o *) fruitore(SOLO se tipologia=fruizione) [Logger]";
			System.err.println(errorMsg);
			throw new CoreException(errorMsg);
		}

		// proprietaAccessoDatabase
		if(args[0]==null){
			throw new CoreException("Proprieta' per la configurazione dell'accesso al database non fornite");
		}
		String proprietaAccessoDatabase = args[0].trim();
		
		// tipologiaEliminazione
		if(args[1]==null){
			throw new CoreException("Tipo di eliminazione non indicata");
		}
		String tipologiaEliminazione = args[1].trim();
		if( !OpenSPCoop2DBConfigurationUtility.TIPOLOGIA_ELIMINAZIONE_EROGAZIONE.equals(tipologiaEliminazione) && !OpenSPCoop2DBConfigurationUtility.TIPOLOGIA_ELIMINAZIONE_FRUIZIONE.equals(tipologiaEliminazione) ){
			throw new CoreException("Tipo di eliminazione non gestito");
		}
		
		// dati del servizio
		if(args[2]==null){
			throw new Exception("Servizio da eliminare non indicato");
		}
		String tipoNomeServizio = args[2].trim();
		if(tipoNomeServizio.contains("/")==false){
			throw new Exception("Servizio da eliminare non indicato correttamente, atteso: tipo/nome");
		}
		String tipoServizio = null;
		String nomeServizio = null;
		try{
			tipoServizio = tipoNomeServizio.split("/")[0].trim();
			nomeServizio = tipoNomeServizio.split("/")[1].trim();
		}catch(Exception e){
			throw new CoreException("Servizio da eliminare non indicato correttamente, atteso: tipo/nome , errore: "+e.getMessage(),e);
		}
		
		// dati del soggetto erogatore
		if(args[3]==null){
			throw new CoreException("Soggetto da eliminare non indicato");
		}
		String tipoNomeSoggetto = args[3].trim();
		String tipoSoggettoErogatore = null;
		String nomeSoggettoErogatore = null;
		if("*".equals(tipoNomeSoggetto)){
			// voglio qualsiasi servizio
		}
		else if(!tipoNomeSoggetto.contains("/")){
			throw new CoreException("Soggetto erogatore del servizio da eliminare non indicato correttamente, atteso: tipo/nome");
		}
		else{
			try{
				tipoSoggettoErogatore = tipoNomeSoggetto.split("/")[0].trim();
				nomeSoggettoErogatore = tipoNomeSoggetto.split("/")[1].trim();
			}catch(Exception e){
				throw new CoreException("Soggetto erogatore del servizio da eliminare non indicato correttamente, atteso: tipo/nome , errore: "+e.getMessage(),e);
			}
		}
		
		int logIndex = 4;
		
		// dati del fruitore se tipologia = fruizione
		String tipoSoggettoFruitore = null;
		String nomeSoggettoFruitore = null;
		if(OpenSPCoop2DBConfigurationUtility.TIPOLOGIA_ELIMINAZIONE_FRUIZIONE.equals(tipologiaEliminazione)){
			logIndex++;
			String tipoNomeSoggettoFruitore = args[4].trim();
			if(tipoNomeSoggettoFruitore==null){
				throw new CoreException("Soggetto fruitore da eliminare non indicato");
			}
			else if(tipoNomeSoggettoFruitore.contains("/")==false){
				throw new CoreException("Soggetto fruitore da eliminare non indicato correttamente, atteso: tipo/nome");
			}
			try{
				tipoSoggettoFruitore = tipoNomeSoggettoFruitore.split("/")[0].trim();
				nomeSoggettoFruitore = tipoNomeSoggettoFruitore.split("/")[1].trim();
			}catch(Exception e){
				throw new CoreException("Soggetto fruitore da eliminare non indicato correttamente, atteso: tipo/nome , errore: "+e.getMessage(),e);
			}
		}
		
		// Inizializzo logger
		try{
			if(args.length==(logIndex+1)){
				LoggerWrapperFactory.setLogConfiguration(args[logIndex]);
			}else{
				LoggerWrapperFactory.setLogConfiguration(OpenSPCoop2DBConfigurationUtility.class.getResource("/console.log4j2.properties"));
			}
		}catch(Exception e) {
			String errorMsg = "Errore durante il caricamento del file di log args"+logIndex+"["+args[logIndex]+"] : "+e.getMessage();
			System.err.println(errorMsg);
			throw new CoreException(errorMsg);
		}	
		Logger log = LoggerWrapperFactory.getLogger("gestoreEliminazioneServizio");
		
		// Inizializzo file di properties contenente le configurazioni per l'accesso al database
		java.util.Properties reader = new java.util.Properties();
		try{
			try(FileInputStream fin = new FileInputStream(proprietaAccessoDatabase)){
				reader.load(fin);
			}
		}catch(java.io.IOException e) {
			String errorMsg = "Errore durante il caricamento del file di properties ["+proprietaAccessoDatabase+"] : "+e.getMessage();
			logError(log,errorMsg,e);
			throw new CoreException(errorMsg,e);
		}
		
		
		// Indicazione se siamo in un caso di ControlStation
		String govwayConsole = reader.getProperty("openspcoop2.govwayConsole");
		String tipoDatabase = null;
		boolean govwayConsoleMode = false;
		if(govwayConsole!=null){
			govwayConsoleMode = Boolean.parseBoolean(govwayConsole.trim());
		}
		
		
		

		
		// **** Raccolta proprieta' per accesso a configurazione *******
		
		Connection connectionConfigurazione = null;
		DriverConfigurazioneDB driverConfigurazione = null;
		try{
			
			
			String connection = null;
			String username = null;
			String password = null;
			String driver =  null;
			boolean condivisioneDBRegservPddValue = false;
						
			// Database
			tipoDatabase = reader.getProperty("openspcoop2.configurazione.db.tipo");
			if(tipoDatabase==null){
				throw new CoreException("Non e' stato definito il tipo di database per la configurazione");
			}else{
				tipoDatabase = tipoDatabase.trim();
			}
			
			String condivisioneDBRegservPdd = reader.getProperty("openspcoop2.configurazione.db.condivisioneDBRegserv");
			try{
				condivisioneDBRegservPddValue = Boolean.parseBoolean(condivisioneDBRegservPdd.trim());
			}catch(Exception e){
				throw new CoreException("Non e' stato definita o e' definita non correttamente la proprieta' [openspcoop2.configurazione.db.condivisioneDBRegserv]: "+e.getMessage());
			}
			
			String dataSource = reader.getProperty("openspcoop2.configurazione.db.dataSource");
			
			if(dataSource!=null){
				dataSource = dataSource.trim();
				java.util.Properties context = Utilities.readProperties("openspcoop2.configurazione.db.context.",reader);
				
				GestoreJNDI gestoreJNDI = new GestoreJNDI(context);
				javax.sql.DataSource ds = (javax.sql.DataSource) gestoreJNDI.lookup(dataSource);
				connectionConfigurazione = ds.getConnection();
						
			}else{
				connection = reader.getProperty("openspcoop2.configurazione.db.url");
				if(connection==null){
					throw new CoreException("Non e' stata definita una destinazione ne attraverso un datasource, ne attraverso una connessione diretta");
				}
				connection = connection.trim();
				driver = reader.getProperty("openspcoop2.configurazione.db.driver");
				if(driver==null){
					throw new CoreException("Connessione diretta: non e' stato definito il Driver");
				}
				driver = driver.trim();
				username = reader.getProperty("openspcoop2.configurazione.db.user");
				password = reader.getProperty("openspcoop2.configurazione.db.password");
				if(username!=null){
					username = username.trim();
				}
				if(password!=null){
					password = password.trim();
				}
				
				Loader.getInstance().newInstance(driver);
				if(username!=null && password!=null){
					connectionConfigurazione = DriverManager.getConnection(connection,username,password);
				}else{
					connectionConfigurazione = DriverManager.getConnection(connection);
				}
			}

			driverConfigurazione = new DriverConfigurazioneDB(connectionConfigurazione, null, tipoDatabase, condivisioneDBRegservPddValue);
			
		}catch(Exception e) {
			String errorMsg = "Errore durante la lettura del file di properties ["+args[1]+"] : "+e.getMessage();
			logError(log,errorMsg,e);
			throw new CoreException(errorMsg,e);
		}
		
		
		
		// **** Raccolta proprieta' per accesso a registroServizi *******
		
		Connection connectionRegistroServizi = null;
		DriverRegistroServiziDB driverRegistroServizi = null;
		try{
			
			
			String connection = null;
			String username = null;
			String password = null;
			String driver =  null;
						
			// Database
			tipoDatabase = reader.getProperty("openspcoop2.registroServizi.db.tipo");
			if(tipoDatabase==null){
				throw new CoreException("Non e' stato definito il tipo di database per la configurazione");
			}else{
				tipoDatabase = tipoDatabase.trim();
			}
			
			String dataSource = reader.getProperty("openspcoop2.registroServizi.db.dataSource");
			
			if(dataSource!=null){
				dataSource = dataSource.trim();
				java.util.Properties context = Utilities.readProperties("openspcoop2.registroServizi.db.context.",reader);
				
				// NOTA: Se sono in govwayConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
				if(!govwayConsoleMode){
					GestoreJNDI gestoreJNDI = new GestoreJNDI(context);
					javax.sql.DataSource ds = (javax.sql.DataSource) gestoreJNDI.lookup(dataSource);
					connectionRegistroServizi = ds.getConnection();
				}
						
			}else{
				connection = reader.getProperty("openspcoop2.registroServizi.db.url");
				if(connection==null){
					throw new CoreException("Non e' stata definita una destinazione ne attraverso un datasource, ne attraverso una connessione diretta");
				}
				connection = connection.trim();
				driver = reader.getProperty("openspcoop2.registroServizi.db.driver");
				if(driver==null){
					throw new CoreException("Connessione diretta: non e' stato definito il Driver");
				}
				driver = driver.trim();
				username = reader.getProperty("openspcoop2.registroServizi.db.user");
				password = reader.getProperty("openspcoop2.registroServizi.db.password");
				if(username!=null){
					username = username.trim();
				}
				if(password!=null){
					password = password.trim();
				}
				
				// NOTA: Se sono in govwayConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
				if(!govwayConsoleMode){
					Loader.getInstance().newInstance(driver);
					if(username!=null && password!=null){
						connectionRegistroServizi = DriverManager.getConnection(connection,username,password);
					}else{
						connectionRegistroServizi = DriverManager.getConnection(connection);
					}
				}
			}

			if(govwayConsoleMode){
				driverRegistroServizi = new DriverRegistroServiziDB(connectionConfigurazione, null, tipoDatabase);
			}else{
				driverRegistroServizi = new DriverRegistroServiziDB(connectionRegistroServizi, null, tipoDatabase);
			}
			
		}catch(Exception e) {
			String errorMsg = "Errore durante la lettura del file di properties ["+args[1]+"] : "+e.getMessage();
			logError(log,errorMsg,e);
			throw new CoreException(errorMsg,e);
		}
		
		
		
		
		
		
		
		try{
			
			logDebug(log,"Imposto auto-commit a false");
			connectionConfigurazione.setAutoCommit(false);
			// NOTA: Se sono in govwayConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
			if(!govwayConsoleMode){
				connectionRegistroServizi.setAutoCommit(false);
			}else{
				connectionRegistroServizi = connectionConfigurazione;
			}
			
			
			// Ricerca id Servizi da eliminare.
			FiltroRicercaServizi filtroRicercaServizio = new FiltroRicercaServizi();
			filtroRicercaServizio.setTipo(tipoServizio);
			filtroRicercaServizio.setNome(nomeServizio);
			if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null){
				filtroRicercaServizio.setTipoSoggettoErogatore(tipoSoggettoErogatore);
				filtroRicercaServizio.setNomeSoggettoErogatore(nomeSoggettoErogatore);
			}
			List<IDServizio> idServizi = new ArrayList<>();
			try{
				List<IDServizio> idServizio = driverRegistroServizi.getAllIdServizi(filtroRicercaServizio);
				if(idServizio!=null){
					for (int i = 0; i < idServizio.size(); i++) {
						idServizi.add(idServizio.get(i));
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){
				logDebug(log,"Ricerca di servizi con filtro ["+filtroRicercaServizio.toString()+"] non ha prodotto risultati: "+notFound.getMessage());
			}
			
			logDebug(log,"Totale servizi da eliminare con tipologia ["+tipologiaEliminazione+"] : "+idServizi.size());
			
			
			// Scorro tutti i servizi trovati
			while(!idServizi.isEmpty()){
				
				IDServizio idServizio = idServizi.remove(0);
				logDebug(log,"Gestione eliminazione servizio ["+idServizio.toString()+"] ...");
				AccordoServizioParteSpecifica asps = driverRegistroServizi.getAccordoServizioParteSpecifica(idServizio);
				@SuppressWarnings("unused")
				IDSoggetto soggettoErogatore = new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore());
				logDebug(log,"\t dati servizio SoggettoErogatore["+asps.getTipoSoggettoErogatore()+"/"+asps.getNomeSoggettoErogatore()+"] Tipologia["+asps.getTipologiaServizio().toString()+"] ...");
								
				if(OpenSPCoop2DBConfigurationUtility.TIPOLOGIA_ELIMINAZIONE_FRUIZIONE.equals(tipologiaEliminazione)){
					
					logDebug(log,"\t dati Fruitore da eliminare ["+tipoSoggettoFruitore+"/"+nomeSoggettoFruitore+"]");
					IDSoggetto soggettoFruitore = new IDSoggetto(tipoSoggettoFruitore,nomeSoggettoFruitore);
					/**org.openspcoop2.core.config.Soggetto soggettoConfig = driverConfigurazione.getSoggetto(soggettoFruitore);*/
					
					// FRUITORE DAL SERVIZIO (+ mapping fruizioni)
					if(govwayConsoleMode){
						logDebug(log,"\t- eliminazione mapping fruizioni ...");
						DBMappingUtils.deleteMappingFruizione(idServizio, soggettoFruitore, connectionConfigurazione, tipoDatabase);
						logDebug(log,"\t- eliminazione mapping fruizioni effettuata");
					}
					
					logDebug(log,"\t- eliminazione fruizione dal servizio ...");
					for (int i = 0; i < asps.sizeFruitoreList(); i++) {
						Fruitore fr = asps.getFruitore(i);
						if(fr.getTipo().equals(tipoSoggettoFruitore) && fr.getNome().equals(nomeSoggettoFruitore)){
							asps.removeFruitore(i);
							break;
						}
					}
					driverRegistroServizi.updateAccordoServizioParteSpecifica(asps);
					logDebug(log,"\t- eliminazione fruizione dal servizio effettuata");
					
					
					// PORTE DELEGATE CON TALE SERVIZIO e con tale fruitore
					List<String> nomiServiziApplicativi = new ArrayList<>();
					List<PortaDelegata> listaPorteDelegate = 
						driverConfigurazione.getPorteDelegateWithServizio(asps.getId(), asps.getTipo(), asps.getNome(), asps.getVersione(),
								asps.getIdSoggetto(), asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore());
					List<PortaDelegata> listaPorteDelegateFruitoreDaEliminare = new ArrayList<>();
					while(!listaPorteDelegate.isEmpty()){
						PortaDelegata pd = listaPorteDelegate.remove(0);
						if(pd.getTipoSoggettoProprietario().equals(tipoSoggettoFruitore) && pd.getNomeSoggettoProprietario().equals(nomeSoggettoFruitore)){
							listaPorteDelegateFruitoreDaEliminare.add(pd);
						}
					}
					logDebug(log,"\t- eliminazione porte delegate ("+listaPorteDelegateFruitoreDaEliminare.size()+") ...");
					while(!listaPorteDelegateFruitoreDaEliminare.isEmpty()){
						PortaDelegata pd = listaPorteDelegateFruitoreDaEliminare.remove(0);
						logDebug(log,"\t\t. porta delegata ("+pd.getNome()+") ...");
						for (int i = 0; i < pd.sizeServizioApplicativoList(); i++) {
							String sa = pd.getServizioApplicativo(i).getNome();
							if(!nomiServiziApplicativi.contains(sa)){
								nomiServiziApplicativi.add(sa);
							}
						}
						driverConfigurazione.deletePortaDelegata(pd);
						logDebug(log,"\t\t. porta delegata ("+pd.getNome()+") eliminata");
					}
					logDebug(log,"\t- eliminazione porte delegate effettuata");
					
					
					// SERVIZI APPLICATIVI
					logDebug(log,"\t- eliminazione servizi applicativi ("+nomiServiziApplicativi.size()+") ...");
					while(!nomiServiziApplicativi.isEmpty()){
						String nomeSA = nomiServiziApplicativi.remove(0);
						logDebug(log,"\t\t. servizio applicativo ("+nomeSA+") ...");
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setIdSoggettoProprietario(soggettoFruitore);
						idSA.setNome(nomeSA);
						ServizioApplicativo sa = driverConfigurazione.getServizioApplicativo(idSA);
						driverConfigurazione.deleteServizioApplicativo(sa);
						logDebug(log,"\t\t. servizio applicativo ("+nomeSA+") eliminato");
					}
					logDebug(log,"\t- eliminazione servizi applicativi effettuata");
					
				}
				else{
					
					// FRUITORI (+ mapping fruizioni)
					if(govwayConsoleMode){
						for (int i = 0; i < asps.sizeFruitoreList(); i++) {
							Fruitore fr = asps.getFruitore(i);
							IDSoggetto soggettoFruitore = new IDSoggetto(fr.getTipo(),fr.getNome());
							logDebug(log,"\t- eliminazione mapping fruizioni ...");
							DBMappingUtils.deleteMappingFruizione(idServizio, soggettoFruitore, connectionConfigurazione, tipoDatabase);
							logDebug(log,"\t- eliminazione mapping fruizioni effettuata");
						}
					}
						
													
					// PORTE DELEGATE
					List<IDServizioApplicativo> nomiServiziApplicativi = new ArrayList<>();
					List<PortaDelegata> listaPorteDelegate = 
						driverConfigurazione.getPorteDelegateWithServizio(asps.getId(), asps.getTipo(), asps.getNome(), asps.getVersione(),
								asps.getIdSoggetto(), asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore());
					logDebug(log,"\t- eliminazione porte delegate ("+listaPorteDelegate.size()+") ...");
					while(!listaPorteDelegate.isEmpty()){
						PortaDelegata pd = listaPorteDelegate.remove(0);
						logDebug(log,"\t\t. porta delegata ("+pd.getNome()+") ...");
						for (int i = 0; i < pd.sizeServizioApplicativoList(); i++) {
							String sa = pd.getServizioApplicativo(i).getNome();
							IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
							idServizioApplicativo.setNome(sa);
							idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
							if(!nomiServiziApplicativi.contains(idServizioApplicativo)){
								nomiServiziApplicativi.add(idServizioApplicativo);
							}
						}
						driverConfigurazione.deletePortaDelegata(pd);
						logDebug(log,"\t\t. porta delegata ("+pd.getNome()+") eliminata");
					}
					logDebug(log,"\t- eliminazione porte delegate effettuata");
					
					
					// PORTE APPLICATIVE
					List<PortaApplicativa> listaPorteApplicative = 
						driverConfigurazione.getPorteApplicativeWithServizio(asps.getId(), asps.getTipo(), asps.getNome(), asps.getVersione(),
								asps.getIdSoggetto(), asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore());
					logDebug(log,"\t- eliminazione porte applicative ("+listaPorteApplicative.size()+") ...");
					while(!listaPorteApplicative.isEmpty()){
						PortaApplicativa pa = listaPorteApplicative.remove(0);
						IDPortaApplicativa idpa = new IDPortaApplicativa();
						idpa.setNome(pa.getNome());
						pa = driverConfigurazione.getPortaApplicativa(idpa); // Leggo tutti i valori (Bug del metodo getPorteApplicativeWithServizio)
						logDebug(log,"\t\t. porta applicativa ("+pa.getNome()+") ...");
						for (int i = 0; i < pa.sizeServizioApplicativoList(); i++) {
							String sa = pa.getServizioApplicativo(i).getNome();
							IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
							idServizioApplicativo.setNome(sa);
							idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
							if(!nomiServiziApplicativi.contains(idServizioApplicativo)){
								nomiServiziApplicativi.add(idServizioApplicativo);
							}
						}
						driverConfigurazione.deletePortaApplicativa(pa);
						logDebug(log,"\t\t. porta applicativa ("+pa.getNome()+") eliminata");
					}
					logDebug(log,"\t- eliminazione porte applicative effettuata");
					
					
					// SERVIZI APPLICATIVI
					logDebug(log,"\t- eliminazione servizi applicativi ("+nomiServiziApplicativi.size()+") ...");
					while(!nomiServiziApplicativi.isEmpty()){
						IDServizioApplicativo idSA = nomiServiziApplicativi.remove(0);
						logDebug(log,"\t\t. servizio applicativo ("+idSA.toString()+") ...");
						try{
							ServizioApplicativo sa = driverConfigurazione.getServizioApplicativo(idSA);
							driverConfigurazione.deleteServizioApplicativo(sa);
							logDebug(log,"\t\t. servizio applicativo ("+idSA.toString()+") eliminato");
						}catch(DriverConfigurazioneNotFound notFound){
							logDebug(log,"\t\t. servizio applicativo ("+idSA.toString()+") non esiste (puo' darsi che veniva riferito da piu' oggetti, e sia gia' stato eliminato)");
						}
					}
					logDebug(log,"\t- eliminazione servizi applicativi effettuata");

					
					
					// SERVIZI (mapping erogazioni)
					if(govwayConsoleMode){
						logDebug(log,"\t- eliminazione mapping erogazione ...");
						DBMappingUtils.deleteMappingErogazione(idServizio, connectionConfigurazione, tipoDatabase);
						logDebug(log,"\t- eliminazione mapping erogazione effettuata");
					}
					
					
					// SERVIZI 
					logDebug(log,"\t- eliminazione servizio ...");
					driverRegistroServizi.deleteAccordoServizioParteSpecifica(asps);
					logDebug(log,"\t- eliminazione servizio effettuata");
					
					
					// ACCORDI (SE NON VI SONO ALTRI SERVIZI CHE LO IMPLEMENTANO)
					logDebug(log,"\t- eliminazione accordo parte comune ...");
					FiltroRicercaServizi filtroAltriServizi = new FiltroRicercaServizi();
					IDAccordo idAccordoParteComune = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
					filtroAltriServizi.setIdAccordoServizioParteComune(idAccordoParteComune);
					boolean existsAltriServizi = false;
					StringBuilder bfAltriServizi = new StringBuilder();
					try{
						List<IDServizio> others = driverRegistroServizi.getAllIdServizi(filtroAltriServizi);
						if(others!=null && !others.isEmpty()){
							existsAltriServizi = true;
							for (int i = 0; i < others.size(); i++) {
								if(bfAltriServizi.length()>0){
									bfAltriServizi.append(",");
								}
								bfAltriServizi.append(others.get(i).toString());
							}
						}
					}catch(DriverRegistroServiziNotFound notFound){}
					if(!existsAltriServizi){
						AccordoServizioParteComune as = new AccordoServizioParteComune();
						as.setNome(idAccordoParteComune.getNome());
						as.setVersione(idAccordoParteComune.getVersione());
						if(idAccordoParteComune.getSoggettoReferente()!=null){
							IdSoggetto asr = new IdSoggetto();
							asr.setTipo(idAccordoParteComune.getSoggettoReferente().getTipo());
							asr.setNome(idAccordoParteComune.getSoggettoReferente().getNome());
							as.setSoggettoReferente(asr);
						}
						driverRegistroServizi.deleteAccordoServizioParteComune(as);
						logDebug(log,"\t- eliminazione accordo parte comune effettuata");
					}
					else{
						logDebug(log,"\t- eliminazione accordo parte comune non effettuata poiche' implementato da altri servizi: "+bfAltriServizi.toString());
					}
					
				}
				
				logDebug(log,"Gestione eliminazione servizio ["+idServizio.toString()+"] completata");
			}
			
		
			
			
			logDebug(log,"Commit...");
			connectionConfigurazione.commit();
			// NOTA: Se sono in govwayConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
			if(!govwayConsoleMode){
				connectionRegistroServizi.commit();
			}
			logDebug(log,"Commit effettuato");
			
		}catch(Exception e){
			try{
				connectionConfigurazione.rollback();
			}catch(Exception eRollback){}
			try{
				// NOTA: Se sono in govwayConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
				if(!govwayConsoleMode){
					connectionRegistroServizi.rollback();
				}
			}catch(Exception eRollback){}
			String errorMsg = "Errore avvenuto durante l'eliminazione: "+e.getMessage(); 
			logError(log,errorMsg,e);
			throw new CoreException(errorMsg,e);
		}finally{
			try{
				connectionConfigurazione.setAutoCommit(true);
			}catch(Exception e){
				// ignore
			}
			try{
				// NOTA: Se sono in govwayConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
				if(!govwayConsoleMode){
					connectionRegistroServizi.setAutoCommit(true);
				}
			}catch(Exception e){
				// ignore
			}
			try{
				Logger logR = OpenSPCoop2Logger.getLoggerOpenSPCoopResources()!=null ? OpenSPCoop2Logger.getLoggerOpenSPCoopResources() : LoggerWrapperFactory.getLogger(DBConsegnePreseInCaricoManager.class);
				boolean checkAutocommit = (OpenSPCoop2Properties.getInstance()==null) || OpenSPCoop2Properties.getInstance().isJdbcCloseConnectionCheckAutocommit();
				boolean checkIsClosed = (OpenSPCoop2Properties.getInstance()==null) || OpenSPCoop2Properties.getInstance().isJdbcCloseConnectionCheckIsClosed();
				JDBCUtilities.closeConnection(logR, connectionConfigurazione, checkAutocommit, checkIsClosed);
			}catch(Exception e){
				// ignore
			}
			try{
				// NOTA: Se sono in govwayConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
				if(!govwayConsoleMode){
					Logger logR = OpenSPCoop2Logger.getLoggerOpenSPCoopResources()!=null ? OpenSPCoop2Logger.getLoggerOpenSPCoopResources() : LoggerWrapperFactory.getLogger(DBConsegnePreseInCaricoManager.class);
					boolean checkAutocommit = (OpenSPCoop2Properties.getInstance()==null) || OpenSPCoop2Properties.getInstance().isJdbcCloseConnectionCheckAutocommit();
					boolean checkIsClosed = (OpenSPCoop2Properties.getInstance()==null) || OpenSPCoop2Properties.getInstance().isJdbcCloseConnectionCheckIsClosed();
					JDBCUtilities.closeConnection(logR, connectionRegistroServizi, checkAutocommit, checkIsClosed);
				}
			}catch(Exception e){
				// ignore
			}
		}
		
	}
	

}
