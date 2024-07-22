/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.modipa.testsuite.connettori;

import org.openspcoop2.pdd.core.connettori.ConnettoreHTTP;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.TransportUtils;

/**
 * ConnettoreHTTPMock
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPMock extends ConnettoreHTTP {

	public ConnettoreHTTPMock() {
		super();
	}

	public ConnettoreHTTPMock(boolean https) {
		super(https);
	}

	@Override
	protected void checkResponse() throws Exception{
		if(HttpConstants.CONTENT_TYPE_PLAIN.equals(this.tipoRisposta)) {
			boolean contentEmpty = true;
			if(this.isResponse!=null){
				contentEmpty = false;
				String s = Utilities.getAsString(this.isResponse, Charset.UTF_8.getValue());
				/*System.out.println("DEBUG s["+s+"]");*/
				if(s!=null && s.length()==0){
					contentEmpty=true;
				}
				if(s!=null && 
					(s.contains("match failed") || s.contains("org.graalvm.polyglot.PolyglotException"))
					) {
					throw new Exception("Karate failed: "+s);
				}
				this.isResponse=new java.io.ByteArrayInputStream(s.getBytes());
			}
			/**System.out.println("DEBUG contentEmpty["+contentEmpty+"]");
			System.out.println("DEBUG codice["+codice+"]");*/
			/**if(contentEmpty && this.codice==500){
				String server = TransportUtils.getObjectAsString(propertiesTrasportoRisposta, "server");
				System.out.println("server server["+server+"]");
				if(server!=null && server.toLowerCase().contains("armeria")){
					throw new Exception("Karate failed 500: "+server);
				}
			}*/
			
		}
	}
}
