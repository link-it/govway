/*
 * OpenSPCoop - Customizable API Gateway 
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

import org.openspcoop2.utils.UtilsException;

/**	
 * Base64Utilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HexBinaryUtilities {

	
	public static byte[] decode(String data) throws UtilsException{
		return decode(data.toCharArray());
	}
	public static byte[] decode(char [] data) throws UtilsException{
		try{
			return org.apache.commons.codec.binary.Hex.decodeHex(data);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static char[] encode(byte [] data) throws UtilsException{
		try{
			return org.apache.commons.codec.binary.Hex.encodeHex(data);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public static String encodeAsString(byte [] data) throws UtilsException{
		try{
			return org.apache.commons.codec.binary.Hex.encodeHexString(data);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

	
	
}
