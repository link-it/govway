/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import java.util.List;

import org.openspcoop2.message.utils.WWWAuthenticateErrorCode;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.testng.Reporter;

/**
 * WWWAuthenticateUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WWWAuthenticateUtils {

	public static WWWAuthenticate parse(String headerValue) throws Exception {
		String exp = "([a-zA-Z]+) realm=\"([a-zA-Z]+)\", error=\"([a-zA-Z_]+)\", error_description=\"([a-zA-Z_ -]+)\"";
		if(!RegularExpressionEngine.isMatch(headerValue, exp)) {
			throw new Exception("Formato header non valido, il valore trovato '"+headerValue+"' deve rispettare l'espressione regolare: "+exp);
		}
		List<String> l = RegularExpressionEngine.getAllStringMatchPattern(headerValue, exp);
		if(l==null || l.isEmpty()) {
			throw new Exception("Formato header non valido (non sono stati trovati match). Il valore trovato '"+headerValue+"' deve rispettare l'espressione regolare: "+exp);
		}
		if(l.size()!=4) {
			throw new Exception("Formato header non valido (non sono stati trovati tutti i match, trovati:"+l.size()+" attesi:4). Il valore trovato '"+headerValue+"' deve rispettare l'espressione regolare: "+exp);
		}
		WWWAuthenticate auth = new WWWAuthenticate();
		auth.setAuthType(l.get(0));
		auth.setRealm(l.get(1));
		auth.setError(l.get(2));
		auth.setError_description(l.get(3));
		return auth;
	}
	
	public static void verify(String headerValue, WWWAuthenticate atteso) throws Exception {
		
		if(headerValue==null) {
			throw new Exception("WWWAuthenticate non presente");
		}
		
		WWWAuthenticate auth = parse(headerValue);
		
		Reporter.log("WWWAuthenticate AuthType atteso '"+atteso.getAuthType()+"' rilevato '"+auth.getAuthType()+"'");
		if(!auth.getAuthType().equals(atteso.getAuthType())) {
			throw new Exception("WWWAuthenticate non conforme, AuthType atteso '"+atteso.getAuthType()+"' differisce da quello rilevato '"+auth.getAuthType()+"'"); 
		}
		
		Reporter.log("WWWAuthenticate Realm atteso '"+atteso.getRealm()+"' rilevato '"+auth.getRealm()+"'");
		if(!auth.getRealm().equals(atteso.getRealm())) {
			throw new Exception("WWWAuthenticate non conforme, Realm atteso '"+atteso.getRealm()+"' differisce da quello rilevato '"+auth.getRealm()+"'"); 
		}
		
		Reporter.log("WWWAuthenticate error atteso '"+atteso.getError()+"' rilevato '"+auth.getError()+"'");
		if(!auth.getError().equals(atteso.getError())) {
			throw new Exception("WWWAuthenticate non conforme, error atteso '"+atteso.getError()+"' differisce da quello rilevato '"+auth.getError()+"'"); 
		}
		
		Reporter.log("WWWAuthenticate error_description atteso '"+atteso.getError_description()+"' rilevato '"+auth.getError_description()+"'");
		if(!auth.getError_description().equals(atteso.getError_description())) {
			throw new Exception("WWWAuthenticate non conforme, error_description atteso '"+atteso.getError_description()+"' differisce da quello rilevato '"+auth.getError_description()+"'"); 
		}
	}
	
	public static void verify(String headerValue, String authType, String realm, String error_description) throws Exception {
		 WWWAuthenticate atteso = new WWWAuthenticate();
		 atteso.setAuthType(authType);
		 atteso.setRealm(realm);
		 atteso.setError(WWWAuthenticateErrorCode.invalid_request.name());
		 atteso.setError_description(error_description);
		 verify(headerValue, atteso);
	}
}
