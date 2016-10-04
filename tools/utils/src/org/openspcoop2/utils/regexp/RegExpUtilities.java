/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.utils.regexp;

import java.io.File;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.HttpUtilities;

/**	
 * Contiene utilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RegExpUtilities {

	/* VALIDAZIONI */
	
	private static final String oneAlpha = "(.)*((\\p{Alpha})|[-])(.)*";
	private static final String domainIdentifier = "((\\p{Alnum})([-]|(\\p{Alnum}))*(\\p{Alnum}))|(\\p{Alnum})";
	private static final String domainNameRule = "(" + RegExpUtilities.domainIdentifier + ")((\\.)(" + RegExpUtilities.domainIdentifier + "))*";
	
	public static void validateUrl(String url) throws java.net.MalformedURLException{
		java.net.URL testUrl = new java.net.URL(url);
		testUrl.toString();
	}
	
	public static void validateUri(String uri,boolean checkEsistenzaFile) throws UtilsException,java.net.MalformedURLException{
		if (uri.startsWith("http://")
				|| uri.startsWith("file://")) {

			if(checkEsistenzaFile)
				HttpUtilities.requestHTTPFile(uri);
			else
				RegExpUtilities.validateUrl(uri);

		} else {
			File f = new File(uri);
			if(checkEsistenzaFile){
				if(f.exists()==false){
					throw new UtilsException("File non esistente");
				}
				else if(f.isDirectory()){
					throw new UtilsException("File e' una directory");
				}else if(f.canRead()==false){
					throw new UtilsException("File non accessibile");
				}
			}
		}
	}
	
	public static boolean validateDomainName(String domainName) {
		if ((domainName == null) || (domainName.length() > 63)) {
			return false;
		}
		return domainName.matches(RegExpUtilities.domainNameRule) && domainName.matches(RegExpUtilities.oneAlpha);
	}

	public static boolean isIPOrHostname(String stringToCheck) {
		boolean ok = true;

		try {
			if (! RegularExpressionEngine.isMatch(stringToCheck, "^[0-9]+.[0-9]+.[0-9]+.[0-9]+$") &&
					!RegExpUtilities.validateDomainName(stringToCheck))
				ok = false;
		} catch (Exception e) {
			java.util.Date now = new java.util.Date();
			System.err.println(now + " Exception " + e.getMessage());
		}

		return ok;
	}
	
}
