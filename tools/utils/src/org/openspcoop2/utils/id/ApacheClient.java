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
package org.openspcoop2.utils.id;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.id.apache.serial.EnumTypeGenerator;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**
 * ApacheClient
 *
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApacheClient {

	public static void main(String[] args) throws Exception {
		
		ApacheIdentifierGenerator generator = new ApacheIdentifierGenerator();
		ApacheGeneratorConfiguration config = new ApacheGeneratorConfiguration();
		
		config.setType(EnumTypeGenerator.PREFIXED_ALPHANUMERIC);
		config.setPrefix("PREFIX");
		
		config.setSize("PREFIX".length()+2);
		//config.setInitialStringValue("PREFIXbb");
		
		config.setStartDigit('3');
		config.setEndDigit('4');
		
		config.setStartLetter('b');
		config.setEndLetter('d');
		
		config.setEnableLowerCaseLetter(true);
		config.setEnableUpperCaseLetter(true);
		
		config.setWrap(false);
		
		generator.initialize(config);
		
		
		List<String> check = new ArrayList<String>();
		
		String v = null;
		int i = 0;
		try{
			for (; i < 10000; i++) {
				v = generator.newID().toString();
				if(!RegularExpressionEngine.isMatch((v+""),"^[a-zA-Z0-9]*$")){
					throw new UtilsException("Deve essere fornito [a-zA-Z0-9] trovato ["+v+"]");
				}
				if(!check.contains(v)){
					check.add(v);
				}else{
					throw new Exception("Valore ["+v+"] gia generato");
				}
				if(i<20){
					System.out.println("VALORE ["+i+"]: "+v);
				}else if(i%50 == 0 )
					System.out.println("VALORE ["+i+"]: "+v);
			}
		}finally{
			System.out.println("VALORE LAST ["+i+"]: "+v);
		}
		
		
	}
}
