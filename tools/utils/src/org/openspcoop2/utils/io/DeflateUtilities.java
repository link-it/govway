/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.openspcoop2.utils.UtilsException;

/**	
 * DeflateUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DeflateUtilities {

	private static final int BUFFER_SIZE = 1024;

	public static byte[] decode(String data) throws UtilsException{
		return decode(data.getBytes());
	}
	public static byte[] decode(byte[] data) throws UtilsException{
		ByteArrayOutputStream baos = null;

		try {
			Deflater d = new Deflater(Deflater.BEST_COMPRESSION);
			d.setInput(data);
			d.finish();
	
			byte[] bytesCompressed = new byte[BUFFER_SIZE];
	
			int deflate = d.deflate(bytesCompressed);
			baos = new ByteArrayOutputStream();
	
			while(deflate > 0) {
				baos.write(bytesCompressed, 0 ,deflate);
				deflate = d.deflate(bytesCompressed);
			}
	
			return baos.toByteArray();
		} finally {
			if(baos != null) {
				try {baos.flush();} catch(Exception e) {}
				try {baos.close();} catch(Exception e) {}
			}
		}

	}

	public static byte[] encode(byte [] data) throws UtilsException{
		ByteArrayOutputStream baos = null;
		try {
			Inflater inflater = new Inflater();
			inflater.setInput(data);
			byte[] result = new byte[BUFFER_SIZE];
			int inflate = inflater.inflate(result);
			baos = new ByteArrayOutputStream();
			while(inflate > 0) {
				baos.write(result, 0, inflate);
				inflate = inflater.inflate(result);
			}
			inflater.end();
			return baos.toByteArray();
		} catch (DataFormatException e) {
			throw new UtilsException(e);
		} finally {
			if(baos != null) {
				try {baos.flush();} catch(Exception e) {}
				try {baos.close();} catch(Exception e) {}
			}
		}
	}
	
	public static String encodeAsString(byte [] data) throws UtilsException{
		return new String(encode(data));
	}

}
