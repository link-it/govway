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



package org.openspcoop2.utils.datasource.test;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.datasource.DataSourceFactory;
import org.openspcoop2.utils.id.IDUtilities;
import org.openspcoop2.utils.id.UUIDUtilsGenerator;
import org.slf4j.Logger;


/**
 * Servlet di esempio
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExampleService extends HttpServlet {

	private static Logger log = LoggerWrapperFactory.getLogger(ExampleService.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		try {
			doGet(req,res);
		}catch(Exception e) {
			try {
				res.sendError(500, e.getMessage());
			}catch(Exception eSendError) {
				log.error(eSendError.getMessage(),eSendError);
			}
		}
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		
		if(ExampleServletInitListener.isInitialized()==false){
			try {
				res.sendError(500, "Applicazione non inizializzata");
				return;
			}catch(Exception eSendError) {
				log.error(eSendError.getMessage(),eSendError);
			}
		}
		
		String prefix = "";
		try{
		
			boolean useUUID = false;
			boolean useApplicativeID = false;
			if(req.getParameter("useUUID")!=null){
				useUUID = Boolean.parseBoolean(req.getParameter("useUUID").trim());
			}
			else if(req.getParameter("useApplicativeID")!=null){
				useApplicativeID = Boolean.parseBoolean(req.getParameter("useApplicativeID").trim());
			}
			
			
			// Genero identificativo di transazione
			String idTransazione = UUIDUtilsGenerator.newUUID();
			prefix = "["+idTransazione+"] ";
			System.out.println(prefix+"Ricevuta richiesta ...");
			
			
			// Recupero datasource configurazione
			org.openspcoop2.utils.datasource.DataSource dsConfig = null;
			String idDsConfig = null;
			if(useUUID){
				idDsConfig = ExampleServletInitListener.UUID_CONFIGURAZIONE;
			}else if(useApplicativeID){
				idDsConfig = ExampleServletInitListener.ID_APPLICATIVO_CONFIGURAZIONE;
			}
			else{
				idDsConfig = ExampleServletInitListener.JNDI_NAME_CONFIGURAZIONE;
			}
			System.out.println(prefix+"Recupero datasource configurazione ["+idDsConfig+"] ...");
			dsConfig = DataSourceFactory.getInstance(idDsConfig);
			System.out.println(prefix+"Recuperato datasource configurazione ["+idDsConfig+"]");
			
			
			
			// Recupero datasource runtime
			javax.sql.DataSource dsRuntime = null;
			String idDsRuntime = null;
			if(useUUID){
				idDsRuntime = ExampleServletInitListener.UUID_RUNTIME;
			}else if(useApplicativeID){
				idDsRuntime = ExampleServletInitListener.ID_APPLICATIVO_RUNTIME;
			}
			else{
				idDsRuntime = ExampleServletInitListener.JNDI_NAME_RUNTIME;
			}
			System.out.println(prefix+"Recupero datasource runtime ["+idDsRuntime+"] ...");
			dsRuntime = DataSourceFactory.getInstance(idDsRuntime);
			System.out.println(prefix+"Recuperato datasource runtime ["+idDsRuntime+"]");
			
			
			
			// Simulo utilizzo del ds per leggere la configurazione
			// Simulo che mi servono diverse connessioni per descrivere tutti i possibili metodi
			org.openspcoop2.utils.datasource.Connection conConfigInit = null;
			org.openspcoop2.utils.datasource.Connection conConfig1 = null;
			org.openspcoop2.utils.datasource.Connection conConfig2 = null;
			java.sql.Connection conRuntime1 = null;
			java.sql.Connection conRuntime2 = null;
			try{
				conConfigInit = dsConfig.getWrappedConnection();
				
				conConfig1 = dsConfig.getWrappedConnection(idTransazione);
				
				conConfig2 = dsConfig.getWrappedConnection(idTransazione, IDUtilities.generateAlphaNumericRandomString(10));
				
				// simulo attivita con sleep
				System.out.println(prefix+"Recuperate connessioni dalla configurazione, sleep 10s ...");
				Utilities.sleep(10000);
				
				// metodo equivalente a getWrappedConnection(), 
				// NOTA: utilizzabile solo se il ds è stato creato con setWrapOriginalMethods(true)
				conRuntime1 = dsRuntime.getConnection();
				
				// metodo equivalente a getWrappedConnection(idTransazione, "password"), 
				// NOTA: utilizzabile solo se il ds è stato creato con setWrapOriginalMethods(true)
				conRuntime2 = dsRuntime.getConnection(idTransazione, IDUtilities.generateAlphaNumericRandomString(10));
				
				// simulo altra attivita con sleep
				System.out.println(prefix+"Recuperate connessioni dalla runtime, sleep 20s ...");
				Utilities.sleep(20000);
				
			}
			finally{
				try{
					if(conConfigInit!=null)
						conConfigInit.close();
				}catch(Exception eClose){
					System.err.println(prefix+"Rilascio connessione configInit non riuscito: "+eClose.getMessage());
					eClose.printStackTrace(System.err);
				}
				try{
					if(conConfig1!=null)
						conConfig1.close();
				}catch(Exception eClose){
					System.err.println(prefix+"Rilascio connessione config1 non riuscito: "+eClose.getMessage());
					eClose.printStackTrace(System.err);
				}
				try{
					if(conConfig2!=null)
						conConfig2.close();
				}catch(Exception eClose){
					System.err.println(prefix+"Rilascio connessione config2 non riuscito: "+eClose.getMessage());
					eClose.printStackTrace(System.err);
				}
				try{
					if(conRuntime1!=null)
						conRuntime1.close();
				}catch(Exception eClose){
					System.err.println(prefix+"Rilascio connessione runtime1 non riuscito: "+eClose.getMessage());
					eClose.printStackTrace(System.err);
				}
				try{
					if(conRuntime2!=null)
						conRuntime2.close();
				}catch(Exception eClose){
					System.err.println(prefix+"Rilascio connessione runtime2 non riuscito: "+eClose.getMessage());
					eClose.printStackTrace(System.err);
				}
			}
			
			System.out.println(prefix+"Terminata attivita");	
			
			
		}catch(Exception e){
			System.err.println(prefix+"Errore: "+e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}
