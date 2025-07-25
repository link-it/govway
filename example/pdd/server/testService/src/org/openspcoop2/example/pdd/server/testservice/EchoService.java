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



package org.openspcoop2.example.pdd.server.testservice;

import java.io.IOException;
import java.util.Properties;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.openspcoop2.message.utils.ServletTestService;


/**
 * Server echo per test
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EchoService extends ServletTestService {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EchoService(){
		super(Startup.logEcho,
				Startup.thresholdRequestDump, Startup.repositoryRequestDump,
				Startup.repositoryResponseFiles,Startup.whitePropertiesList,Startup.genericError,
				Startup.addTransferEncodingHeader);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		Properties headerTrasporto = new Properties();
		headerTrasporto.put("GovWay", "EchoService");
		this.doEngine(req, res, false, headerTrasporto);
	}
}
