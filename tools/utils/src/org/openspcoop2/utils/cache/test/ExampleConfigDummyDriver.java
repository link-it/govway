/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.cache.test;

import java.sql.Connection;


/**
 * ExampleConfigDummyDriver
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExampleConfigDummyDriver {

	@SuppressWarnings("unused")
	private Connection con;
	public ExampleConfigDummyDriver(Connection con){
		this.con = con; // example
	}
	
	// Metodi di esempio
	
	public String getName(String surname, Integer age){
		if("Red".equals(surname))
			return "Mike";
		else if("Green".equals(surname))
			return "Bob";
		else
			return "Antony";
		
	}
	
	public Boolean isTest(String param) throws Exception{
		if(param!=null){
			if(param.equals("EXCEPTION")){
				throw new ExampleExceptionNotFound();
			}
			else if(param.equals("GENERIC_EXCEPTION")){
				throw new Exception("Altra eccezione");
			}
			else if(param.equals("NULL")){
				return null;
			}else{
				return true;
			}
		}
		else{
			return false;
		}
	}
	
}
