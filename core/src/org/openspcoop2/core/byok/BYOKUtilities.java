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
package org.openspcoop2.core.byok;

import org.openspcoop2.core.constants.CostantiDB;

/**
 * BYOKUtilities
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKUtilities {
	
	private BYOKUtilities() {}

	public static boolean isWrappedValue(byte[] s) {
		if(s==null) {
			return false;
		}
		return isWrappedValue(new String(s));
	}
	public static boolean isWrappedValue(String s) {
		if(s!=null && s.startsWith(CostantiDB.ENC_PREFIX) && s.contains(".")) {
			int indexOf = s.indexOf(".");
			if(indexOf>0 && indexOf<s.length()) {
				String header = s.substring(0, indexOf);
				return header.endsWith(CostantiDB.ENC_PREFIX);
			}
		}
		return false;
	}
	
	public static String newPrefixWrappedValue(String policy) {
		return CostantiDB.ENC_PREFIX + policy + CostantiDB.ENC_PREFIX + ".";
	}
	
	public static String extractPrefixWrappedValue(byte[] s) {
		if(s==null) {
			return null;
		}
		return extractPrefixWrappedValue(new String(s));
	}
	public static String extractPrefixWrappedValue(String s) {
		if(s!=null && s.startsWith(CostantiDB.ENC_PREFIX) && s.contains(".")) {
			int indexOf = s.indexOf(".");
			if(indexOf>0 && indexOf<s.length()) {
				return s.substring(0, indexOf);
			}
		}
		return null;
	}
	public static String getPolicy(String s) {
		String header = extractPrefixWrappedValue(s);
		if(header!=null && header.endsWith(CostantiDB.ENC_PREFIX)) {
			return header.substring(CostantiDB.ENC_PREFIX.length(), header.length()-CostantiDB.ENC_PREFIX.length());
		}
		return null;
	}
	
	public static String getRawWrappedValue(String s) {
		if(s!=null && s.startsWith(CostantiDB.ENC_PREFIX) && s.contains(".")) {
			int indexOf = s.indexOf(".");
			if(indexOf>0 && indexOf<s.length()) {
				String header = s.substring(0, indexOf);
				if(header.endsWith(CostantiDB.ENC_PREFIX) && s.length()>(header.length()+1)) {
					return s.substring(header.length()+1); // +1 per il punto
				}
			}
		}
		return null;
	}
}
