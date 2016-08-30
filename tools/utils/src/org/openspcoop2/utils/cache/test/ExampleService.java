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



package org.openspcoop2.utils.cache.test;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;


/**
 * Servlet di esempio
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExampleService extends HttpServlet {

	private static int uuid = 1;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		doGet(req,res);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		
		if(ExampleServletInitListener.isInitialized()==false){
			throw new ServletException("Applicazione non inizializzata");
		}
		
		Logger log = LoggerWrapperFactory.getLogger(ExampleService.class);
		
		String prefix = "";
		Connection con = null;
		try{
			
			boolean DEBUG = true;
			
			// Genero identificativo di transazione
			String idTransazione = "" + (uuid++);
			prefix = "["+idTransazione+"] ";
			log.info(prefix+"Ricevuta richiesta ...");
			
			
			// Simulazione getConnection
			con = null; 
			
			// Simulazione metodo getName
			String method = "getName";
			String surname = "Green";
			int age=23;
			String key = surname+"_"+age;
			Object name = ExampleServletInitListener.configManager.getObjectCache(con, DEBUG, key, method, surname, age);
			log.info(prefix+"Name: "+name);
			
			// Duplico in cache
			ExampleServletInitListener.configManager.duplicateObjectCache(key, method, key+"NEW", method+"NEW", DEBUG, false);
			name = ExampleServletInitListener.configManager.getObjectCache(con, DEBUG, key+"NEW", method+"NEW", surname, age);
			log.info(prefix+"NameDuplicate: "+name);
			
			// Simulazione metodo eccezione cachable
			method = "isTest";
			key = "EXCEPTION";
			try{
				ExampleServletInitListener.configManager.getObjectCache(con, DEBUG, key, method, "EXCEPTION");
			}catch(ExampleExceptionNotFound notFound){
				log.info(prefix+"Eccezione attesa: "+notFound.getClass().getName());
			}	
			
			// Simulazione metodo eccezione not cachable
			method = "isTest";
			key = "GENERIC_EXCEPTION";
			try{
				ExampleServletInitListener.configManager.getObjectCache(con, DEBUG, key, method, "GENERIC_EXCEPTION");
			}catch(Exception e){
				log.info(prefix+"altra eccezione: "+e.getClass().getName());
			}	
			
			// Simulazione metodo che ritorna null
			method = "isTest";
			try{
				ExampleServletInitListener.configManager.getObjectCache(con, DEBUG, null, method, "NULL");
			}catch(ExampleExceptionNotFound notFound){
				log.info(prefix+"Eccezione attesa: "+notFound.getClass().getName());
			}	
			
			log.info(prefix+"Terminata attivita");	
			
			
		}catch(Throwable e){
			log.error(prefix+"Errore: "+e.getMessage(),e);
		}
	}
}
