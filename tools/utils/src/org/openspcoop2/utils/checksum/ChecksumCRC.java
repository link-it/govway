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

package org.openspcoop2.utils.checksum;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * ChecksumCRC
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ChecksumCRC {

	
	public static long checksumCRC32(String fileName) throws ChecksumException{
		try{
			File file = new File(fileName);
			return ChecksumCRC.checksumCRC32(file);
		}catch(Exception e){
			throw new ChecksumException("Calcolo checksum non riuscito: "+e.getMessage(),e);
		}
	}
	
	public static long checksumCRC32(File file) throws ChecksumException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(file);
			return ChecksumCRC.checksumCRC32(fin);
		}catch(Exception e){
			throw new ChecksumException("Calcolo checksum non riuscito: "+e.getMessage(),e);
		}finally{
			try{
				if(fin!=null)
					fin.close();
			}catch(Exception eClose){}
		}
	}
	
	public static long checksumCRC32(byte[] bytes) throws ChecksumException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(bytes);
			return ChecksumCRC.checksumCRC32(bin);
		}catch(Exception e){
			throw new ChecksumException("Calcolo checksum non riuscito: "+e.getMessage(),e);
		}finally{
			try{
				if(bin!=null)
					bin.close();
			}catch(Exception eClose){}
		}
	}
	
	public static long checksumCRC32(InputStream i) throws ChecksumException{
		try{
			CheckedInputStream cis = new CheckedInputStream(i,new CRC32());
			byte[]tempBuf = new byte[128];
			while(cis.read(tempBuf)>=0){}
			long checksum = cis.getChecksum().getValue();
			return checksum;
		}catch(Exception e){
			throw new ChecksumException("Calcolo checksum non riuscito: "+e.getMessage(),e);
		}
	}
	
}
