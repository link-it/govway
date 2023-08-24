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



package org.openspcoop2.testsuite.server;


import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



/**
 * Server Generale utilizzato per profili di collaborazione OneWay, Sincrono e AsincronoAsimmetrico
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@SuppressWarnings("serial")
public class ServerReadTimeout extends ServerCore{

	/**
	 * 
	 */
	public ServerReadTimeout() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public void init() {}


	@Override
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{

		try{
			
			this.log.info("Simulo tempo di processamento lungo (3 minuti e 10 secondi)...");
			// 3 minuti
			for (int i = 0; i < 18; i++) {
				try{
					Thread.sleep(10000);
				}catch(Exception e){}
				this.log.info("Sleep da "+((i+1)*10)+" secondi ...");
			}
			// i 10 secondi
			try{
				Thread.sleep(10000);
			}catch(Exception e){}
			

		}catch(Exception e){
			this.log.error("Errore durante la gestione di una richiesta: "+e.getMessage(),e);
			throw new ServletException("Errore durante la gestione di una richiesta: "+e.getMessage(),e);
		}
	}

	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{
		doGet(request,response);
	}
}
