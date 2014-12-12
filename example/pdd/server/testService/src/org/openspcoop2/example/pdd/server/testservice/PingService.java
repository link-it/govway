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



package org.openspcoop2.example.pdd.server.testservice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openspcoop2.message.ServletTestService;


/**
 * Server echo per test
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PingService extends ServletTestService {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger log = null;
	private static synchronized void initLogger(){
		try{
			if(PingService.log==null){
				PropertyConfigurator.configure(PingService.class.getResource("/testService.log4j.properties"));
				PingService.log = Logger.getLogger("openspcoop2.pingService");
			}
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
	private static Logger getLogger(){
		if(PingService.log==null){
			PingService.initLogger();
		}
		return PingService.log;
	}

	
	public PingService(){
		super(getLogger());
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		this.doEngine(req, res, true);
	}
}
