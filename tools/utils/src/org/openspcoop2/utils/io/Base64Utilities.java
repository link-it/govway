/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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


package org.openspcoop2.utils.io;

/**	
 * Base64Utilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Base64Utilities {

	
	public static byte[] decode(byte [] data){
		return org.apache.commons.codec.binary.Base64.decodeBase64(data);
	}
	public static byte[] decode(String data){
		return org.apache.commons.codec.binary.Base64.decodeBase64(data);
	}
	
	public static byte[] encode(byte [] data){
		return org.apache.commons.codec.binary.Base64.encodeBase64(data);
	}
	public static String encodeAsString(byte [] data){
		return org.apache.commons.codec.binary.Base64.encodeBase64String(data);
	}
	
	
}
