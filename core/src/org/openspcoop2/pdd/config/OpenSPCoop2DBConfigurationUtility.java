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



package org.openspcoop2.pdd.config;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;

/**
 * Inizializza una configurazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class OpenSPCoop2DBConfigurationUtility {

	private final static String TIPOLOGIA_ELIMINAZIONE_FRUIZIONE = "fruizione";
	private final static String TIPOLOGIA_ELIMINAZIONE_EROGAZIONE = "erogazione";

	public static void main(String[] args) throws Exception {

		
		                               
		if (args.length  < 4) {
			String errorMsg = "ERROR, Usage:  java TestXMLDataConverter proprietaAccessoDatabase tipologiaEliminazione(fruzione/erogazione) servizio(tipo/nome) soggettoErogatoreServizio(tipo/nome o *) fruitore(SOLO se tipologia=fruizione) [Logger]";
			System.err.println(errorMsg);
			throw new Exception(errorMsg);
		}

		// proprietaAccessoDatabase
		if(args[0]==null){
			throw new Exception("Proprieta' per la configurazione dell'accesso al database non fornite");
		}
		String proprietaAccessoDatabase = args[0].trim();
		
		// tipologiaEliminazione
		if(args[1]==null){
			throw new Exception("Tipo di eliminazione non indicata");
		}
		String tipologiaEliminazione = args[1].trim();
		if( !OpenSPCoop2DBConfigurationUtility.TIPOLOGIA_ELIMINAZIONE_EROGAZIONE.equals(tipologiaEliminazione) && !OpenSPCoop2DBConfigurationUtility.TIPOLOGIA_ELIMINAZIONE_FRUIZIONE.equals(tipologiaEliminazione) ){
			throw new Exception("Tipo di eliminazione non gestito");
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
			throw new Exception("Servizio da eliminare non indicato correttamente, atteso: tipo/nome , errore: "+e.getMessage(),e);
		}
		
		// dati del soggetto erogatore
		if(args[3]==null){
			throw new Exception("Soggetto da eliminare non indicato");
		}
		String tipoNomeSoggetto = args[3].trim();
		String tipoSoggettoErogatore = null;
		String nomeSoggettoErogatore = null;
		if("*".equals(tipoNomeSoggetto)){
			// voglio qualsiasi servizio
		}
		else if(tipoNomeSoggetto.contains("/")==false){
			throw new Exception("Soggetto erogatore del servizio da eliminare non indicato correttamente, atteso: tipo/nome");
		}
		else{
			try{
				tipoSoggettoErogatore = tipoNomeSoggetto.split("/")[0].trim();
				nomeSoggettoErogatore = tipoNomeSoggetto.split("/")[1].trim();
			}catch(Exception e){
				throw new Exception("Soggetto erogatore del servizio da eliminare non indicato correttamente, atteso: tipo/nome , errore: "+e.getMessage(),e);
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
				throw new Exception("Soggetto fruitore da eliminare non indicato");
			}
			else if(tipoNomeSoggettoFruitore.contains("/")==false){
				throw new Exception("Soggetto fruitore da eliminare non indicato correttamente, atteso: tipo/nome");
			}
			try{
				tipoSoggettoFruitore = tipoNomeSoggettoFruitore.split("/")[0].trim();
				nomeSoggettoFruitore = tipoNomeSoggettoFruitore.split("/")[1].trim();
			}catch(Exception e){
				throw new Exception("Soggetto fruitore da eliminare non indicato correttamente, atteso: tipo/nome , errore: "+e.getMessage(),e);
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
			throw new Exception(errorMsg);
		}	
		Logger log = LoggerWrapperFactory.getLogger("gestoreEliminazioneServizio");
		
		// Inizializzo file di properties contenente le configurazioni per l'accesso al database
		java.util.Properties reader = new java.util.Properties();
		try{
			reader.load(new FileInputStream(proprietaAccessoDatabase));
		}catch(java.io.IOException e) {
			String errorMsg = "Errore durante il caricamento del file di properties ["+proprietaAccessoDatabase+"] : "+e.getMessage();
			log.error(errorMsg,e);
			throw new Exception(errorMsg,e);
		}
		
		
		// Indicazione se siamo in un caso di ControlStation
		String pddConsole = reader.getProperty("openspcoop2.pddConsole");
		String tipoDatabase = null;
		boolean pddConsoleMode = false;
		if(pddConsole!=null){
			pddConsoleMode = Boolean.parseBoolean(pddConsole.trim());
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
				throw new Exception("Non e' stato definito il tipo di database per la configurazione");
			}else{
				tipoDatabase = tipoDatabase.trim();
			}
			
			String condivisioneDBRegservPdd = reader.getProperty("openspcoop2.configurazione.db.condivisioneDBRegserv");
			try{
				condivisioneDBRegservPddValue = Boolean.parseBoolean(condivisioneDBRegservPdd.trim());
			}catch(Exception e){
				throw new Exception("Non e' stato definita o e' definita non correttamente la proprieta' [openspcoop2.configurazione.db.condivisioneDBRegserv]: "+e.getMessage());
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
					throw new Exception("Non e' stata definita una destinazione ne attraverso un datasource, ne attraverso una connessione diretta");
				}
				connection = connection.trim();
				driver = reader.getProperty("openspcoop2.configurazione.db.driver");
				if(driver==null){
					throw new Exception("Connessione diretta: non e' stato definito il Driver");
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
			log.error(errorMsg,e);
			throw new Exception(errorMsg,e);
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
				throw new Exception("Non e' stato definito il tipo di database per la configurazione");
			}else{
				tipoDatabase = tipoDatabase.trim();
			}
			
			String dataSource = reader.getProperty("openspcoop2.registroServizi.db.dataSource");
			
			if(dataSource!=null){
				dataSource = dataSource.trim();
				java.util.Properties context = Utilities.readProperties("openspcoop2.registroServizi.db.context.",reader);
				
				// NOTA: Se sono in pddConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
				if(!pddConsoleMode){
					GestoreJNDI gestoreJNDI = new GestoreJNDI(context);
					javax.sql.DataSource ds = (javax.sql.DataSource) gestoreJNDI.lookup(dataSource);
					connectionRegistroServizi = ds.getConnection();
				}
						
			}else{
				connection = reader.getProperty("openspcoop2.registroServizi.db.url");
				if(connection==null){
					throw new Exception("Non e' stata definita una destinazione ne attraverso un datasource, ne attraverso una connessione diretta");
				}
				connection = connection.trim();
				driver = reader.getProperty("openspcoop2.registroServizi.db.driver");
				if(driver==null){
					throw new Exception("Connessione diretta: non e' stato definito il Driver");
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
				
				// NOTA: Se sono in pddConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
				if(!pddConsoleMode){
					Loader.getInstance().newInstance(driver);
					if(username!=null && password!=null){
						connectionRegistroServizi = DriverManager.getConnection(connection,username,password);
					}else{
						connectionRegistroServizi = DriverManager.getConnection(connection);
					}
				}
			}

			if(pddConsoleMode){
				driverRegistroServizi = new DriverRegistroServiziDB(connectionConfigurazione, null, tipoDatabase);
			}else{
				driverRegistroServizi = new DriverRegistroServiziDB(connectionRegistroServizi, null, tipoDatabase);
			}
			
		}catch(Exception e) {
			String errorMsg = "Errore durante la lettura del file di properties ["+args[1]+"] : "+e.getMessage();
			log.error(errorMsg,e);
			throw new Exception(errorMsg,e);
		}
		
		
		
		
		
		
		
		try{
			
			log.debug("Imposto auto-commit a false");
			connectionConfigurazione.setAutoCommit(false);
			// NOTA: Se sono in pddConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
			if(!pddConsoleMode){
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
			Vector<IDServizio> idServizi = new Vector<IDServizio>();
			try{
				List<IDServizio> idServizio = driverRegistroServizi.getAllIdServizi(filtroRicercaServizio);
				if(idServizio!=null){
					for (int i = 0; i < idServizio.size(); i++) {
						idServizi.add(idServizio.get(i));
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){
				log.debug("Ricerca di servizi con filtro ["+filtroRicercaServizio.toString()+"] non ha prodotto risultati: "+notFound.getMessage());
			}
			
			log.debug("Totale servizi da eliminare con tipologia ["+tipologiaEliminazione+"] : "+idServizi.size());
			
			
			// Scorro tutti i servizi trovati
			while(idServizi.size()!=0){
				
				IDServizio idServizio = idServizi.remove(0);
				log.debug("Gestione eliminazione servizio ["+idServizio.toString()+"] ...");
				AccordoServizioParteSpecifica asps = driverRegistroServizi.getAccordoServizioParteSpecifica(idServizio);
				Servizio servizio = asps.getServizio();
				IDSoggetto soggettoErogatore = new IDSoggetto(servizio.getTipoSoggettoErogatore(), servizio.getNomeSoggettoErogatore());
				log.debug("\t dati servizio SoggettoErogatore["+servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore()+"] Tipologia["+servizio.getTipologiaServizio().toString()+"] ...");
								
				if(OpenSPCoop2DBConfigurationUtility.TIPOLOGIA_ELIMINAZIONE_FRUIZIONE.equals(tipologiaEliminazione)){
					
					log.debug("\t dati Fruitore da eliminare ["+tipoSoggettoFruitore+"/"+nomeSoggettoFruitore+"]");
					IDSoggetto soggettoFruitore = new IDSoggetto(tipoSoggettoFruitore,nomeSoggettoFruitore);
					org.openspcoop2.core.config.Soggetto soggettoConfig = driverConfigurazione.getSoggetto(soggettoFruitore);
					
					// FRUITORE DAL SERVIZIO (+ politiche di sicurezza)
					if(pddConsoleMode){
						log.debug("\t- eliminazione politiche di sicurezza ...");
						OpenSPCoop2DBConfigurationUtility.deletePoliticheSicurezza(connectionConfigurazione, tipoDatabase, asps.getId(), soggettoConfig.getId());
						log.debug("\t- eliminazione politiche di sicurezza effettuata");
					}
					
					log.debug("\t- eliminazione fruizione dal servizio ...");
					for (int i = 0; i < asps.sizeFruitoreList(); i++) {
						Fruitore fr = asps.getFruitore(i);
						if(fr.getTipo().equals(tipoSoggettoFruitore) && fr.getNome().equals(nomeSoggettoFruitore)){
							asps.removeFruitore(i);
							break;
						}
					}
					driverRegistroServizi.updateAccordoServizioParteSpecifica(asps);
					log.debug("\t- eliminazione fruizione dal servizio effettuata");
					
					
					// PORTE DELEGATE CON TALE SERVIZIO e con tale fruitore
					Vector<String> nomiServiziApplicativi = new Vector<String>();
					List<PortaDelegata> listaPorteDelegate = 
						driverConfigurazione.getPorteDelegateWithServizio(asps.getId(), servizio.getTipo(), servizio.getNome(), 
								asps.getIdSoggetto(), servizio.getTipoSoggettoErogatore(), servizio.getNomeSoggettoErogatore());
					List<PortaDelegata> listaPorteDelegateFruitoreDaEliminare = new ArrayList<PortaDelegata>();
					while(listaPorteDelegate.size()>0){
						PortaDelegata pd = listaPorteDelegate.remove(0);
						if(pd.getTipoSoggettoProprietario().equals(tipoSoggettoFruitore) && pd.getNomeSoggettoProprietario().equals(nomeSoggettoFruitore)){
							listaPorteDelegateFruitoreDaEliminare.add(pd);
						}
					}
					log.debug("\t- eliminazione porte delegate ("+listaPorteDelegateFruitoreDaEliminare.size()+") ...");
					while(listaPorteDelegateFruitoreDaEliminare.size()>0){
						PortaDelegata pd = listaPorteDelegateFruitoreDaEliminare.remove(0);
						log.debug("\t\t. porta delegata ("+pd.getNome()+") ...");
						for (int i = 0; i < pd.sizeServizioApplicativoList(); i++) {
							String sa = pd.getServizioApplicativo(i).getNome();
							if(!nomiServiziApplicativi.contains(sa)){
								nomiServiziApplicativi.add(sa);
							}
						}
						driverConfigurazione.deletePortaDelegata(pd);
						log.debug("\t\t. porta delegata ("+pd.getNome()+") eliminata");
					}
					log.debug("\t- eliminazione porte delegate effettuata");
					
					
					// SERVIZI APPLICATIVI
					log.debug("\t- eliminazione servizi applicativi ("+nomiServiziApplicativi.size()+") ...");
					while(nomiServiziApplicativi.size()>0){
						String nomeSA = nomiServiziApplicativi.remove(0);
						log.debug("\t\t. servizio applicativo ("+nomeSA+") ...");
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setIdSoggettoProprietario(soggettoFruitore);
						idSA.setNome(nomeSA);
						ServizioApplicativo sa = driverConfigurazione.getServizioApplicativo(idSA);
						driverConfigurazione.deleteServizioApplicativo(sa);
						log.debug("\t\t. servizio applicativo ("+nomeSA+") eliminato");
					}
					log.debug("\t- eliminazione servizi applicativi effettuata");
					
				}
				else{
					
					// FRUITORI (+ politiche di sicurezza)
					if(pddConsoleMode){
						log.debug("\t- eliminazione politiche di sicurezza ("+asps.sizeFruitoreList()+") ...");
						OpenSPCoop2DBConfigurationUtility.deletePoliticheSicurezza(connectionConfigurazione, tipoDatabase, asps.getId(), null);
						log.debug("\t- eliminazione politiche di sicurezza effettuata");
					}
						
													
					// PORTE DELEGATE
					Vector<IDServizioApplicativo> nomiServiziApplicativi = new Vector<IDServizioApplicativo>();
					List<PortaDelegata> listaPorteDelegate = 
						driverConfigurazione.getPorteDelegateWithServizio(asps.getId(), servizio.getTipo(), servizio.getNome(), 
								asps.getIdSoggetto(), servizio.getTipoSoggettoErogatore(), servizio.getNomeSoggettoErogatore());
					log.debug("\t- eliminazione porte delegate ("+listaPorteDelegate.size()+") ...");
					while(listaPorteDelegate.size()>0){
						PortaDelegata pd = listaPorteDelegate.remove(0);
						log.debug("\t\t. porta delegata ("+pd.getNome()+") ...");
						for (int i = 0; i < pd.sizeServizioApplicativoList(); i++) {
							String sa = pd.getServizioApplicativo(i).getNome();
							if(!nomiServiziApplicativi.contains(sa)){
								IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
								idServizioApplicativo.setNome(sa);
								idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
								nomiServiziApplicativi.add(idServizioApplicativo);
							}
						}
						driverConfigurazione.deletePortaDelegata(pd);
						log.debug("\t\t. porta delegata ("+pd.getNome()+") eliminata");
					}
					log.debug("\t- eliminazione porte delegate effettuata");
					
					
					// PORTE APPLICATIVE
					List<PortaApplicativa> listaPorteApplicative = 
						driverConfigurazione.getPorteApplicativeWithServizio(asps.getId(), servizio.getTipo(), servizio.getNome(), 
								asps.getIdSoggetto(), servizio.getTipoSoggettoErogatore(), servizio.getNomeSoggettoErogatore());
					log.debug("\t- eliminazione porte applicative ("+listaPorteApplicative.size()+") ...");
					while(listaPorteApplicative.size()>0){
						PortaApplicativa pa = listaPorteApplicative.remove(0);
						pa = driverConfigurazione.getPortaApplicativa(pa.getNome(), soggettoErogatore); // Leggo tutti i valori (Bug del metodo getPorteApplicativeWithServizio)
						log.debug("\t\t. porta applicativa ("+pa.getNome()+") ...");
						for (int i = 0; i < pa.sizeServizioApplicativoList(); i++) {
							String sa = pa.getServizioApplicativo(i).getNome();
							if(!nomiServiziApplicativi.contains(sa)){
								IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
								idServizioApplicativo.setNome(sa);
								idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
								nomiServiziApplicativi.add(idServizioApplicativo);
							}
						}
						driverConfigurazione.deletePortaApplicativa(pa);
						log.debug("\t\t. porta applicativa ("+pa.getNome()+") eliminata");
					}
					log.debug("\t- eliminazione porte applicative effettuata");
					
					
					// SERVIZI APPLICATIVI
					log.debug("\t- eliminazione servizi applicativi ("+nomiServiziApplicativi.size()+") ...");
					while(nomiServiziApplicativi.size()>0){
						IDServizioApplicativo idSA = nomiServiziApplicativi.remove(0);
						log.debug("\t\t. servizio applicativo ("+idSA.toString()+") ...");
						try{
							ServizioApplicativo sa = driverConfigurazione.getServizioApplicativo(idSA);
							driverConfigurazione.deleteServizioApplicativo(sa);
							log.debug("\t\t. servizio applicativo ("+idSA.toString()+") eliminato");
						}catch(DriverConfigurazioneNotFound notFound){
							log.debug("\t\t. servizio applicativo ("+idSA.toString()+") non esiste (puo' darsi che veniva riferito da piu' oggetti, e sia gia' stato eliminato)");
						}
					}
					log.debug("\t- eliminazione servizi applicativi effettuata");

					
					// SERVIZI 
					log.debug("\t- eliminazione servizio ...");
					driverRegistroServizi.deleteAccordoServizioParteSpecifica(asps);
					log.debug("\t- eliminazione servizio effettuata");
					
					
					// ACCORDI (SE NON VI SONO ALTRI SERVIZI CHE LO IMPLEMENTANO)
					log.debug("\t- eliminazione accordo parte comune ...");
					FiltroRicercaServizi filtroAltriServizi = new FiltroRicercaServizi();
					IDAccordo idAccordoParteComune = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
					filtroAltriServizi.setIdAccordo(idAccordoParteComune);
					boolean existsAltriServizi = false;
					StringBuffer bfAltriServizi = new StringBuffer();
					try{
						List<IDServizio> others = driverRegistroServizi.getAllIdServizi(filtroAltriServizi);
						if(others!=null && others.size()>0){
							existsAltriServizi = true;
						}
						for (int i = 0; i < others.size(); i++) {
							if(bfAltriServizi.length()>0){
								bfAltriServizi.append(",");
							}
							bfAltriServizi.append(others.get(i).toString());
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
						log.debug("\t- eliminazione accordo parte comune effettuata");
					}
					else{
						log.debug("\t- eliminazione accordo parte comune non effettuata poiche' implementato da altri servizi: "+bfAltriServizi.toString());
					}
					
				}
				
				log.debug("Gestione eliminazione servizio ["+idServizio.toString()+"] completata");
			}
			
		
			
			
			log.debug("Commit...");
			connectionConfigurazione.commit();
			// NOTA: Se sono in pddConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
			if(!pddConsoleMode){
				connectionRegistroServizi.commit();
			}
			log.debug("Commit effettuato");
			
		}catch(Exception e){
			try{
				connectionConfigurazione.rollback();
			}catch(Exception eRollback){}
			try{
				// NOTA: Se sono in pddConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
				if(!pddConsoleMode){
					connectionRegistroServizi.rollback();
				}
			}catch(Exception eRollback){}
			String errorMsg = "Errore avvenuto durante l'eliminazione: "+e.getMessage(); 
			log.error(errorMsg,e);
			throw new Exception(errorMsg,e);
		}finally{
			try{
				connectionConfigurazione.setAutoCommit(true);
			}catch(Exception e){}
			try{
				// NOTA: Se sono in pddConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
				if(!pddConsoleMode){
					connectionRegistroServizi.setAutoCommit(true);
				}
			}catch(Exception e){}
			try{
				connectionConfigurazione.close();
			}catch(Exception e){}
			try{
				// NOTA: Se sono in pddConsoleMode devo usare SOLO 1 connessione (uso quella della configurazione)
				if(!pddConsoleMode){
					connectionRegistroServizi.close();
				}
			}catch(Exception e){}
		}
		
	}
	
	
	private static void deletePoliticheSicurezza(Connection con,String tipoDatabase,Long idServizio,Long idFruitore) throws Exception{
		PreparedStatement pstmt = null;
		try{
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addDeleteTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addWhereCondition("id_servizio=?");
			if(idFruitore!=null){
				sqlQueryObject.addWhereCondition("id_fruitore=?");
			}
			
			pstmt = con.prepareStatement(sqlQueryObject.createSQLDelete());
			pstmt.setLong(1, idServizio);
			if(idFruitore!=null){
				pstmt.setLong(2, idFruitore);
			}
			pstmt.executeUpdate();
			
		}finally{
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}

}
