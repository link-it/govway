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

package org.openspcoop2.utils.checksum;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

/**
 * ChecksumAdler
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ChecksumAdler {

	
	public static long checksumAdler32(String fileName) throws ChecksumException{
		try{
			File file = new File(fileName);
			return ChecksumAdler.checksumAdler32(file);
		}catch(Exception e){
			throw new ChecksumException("Calcolo checksum non riuscito: "+e.getMessage(),e);
		}
	}
	
	public static long checksumAdler32(File file) throws ChecksumException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(file);
			return ChecksumAdler.checksumAdler32(fin);
		}catch(Exception e){
			throw new ChecksumException("Calcolo checksum non riuscito: "+e.getMessage(),e);
		}finally{
			try{
				if(fin!=null)
					fin.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public static long checksumAdler32(byte[] bytes) throws ChecksumException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(bytes);
			return ChecksumAdler.checksumAdler32(bin);
		}catch(Exception e){
			throw new ChecksumException("Calcolo checksum non riuscito: "+e.getMessage(),e);
		}finally{
			try{
				if(bin!=null)
					bin.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public static long checksumAdler32(InputStream i) throws ChecksumException{
		try{
			CheckedInputStream cis = new CheckedInputStream(i,new Adler32());
			byte[]tempBuf = new byte[128];
			while(cis.read(tempBuf)>=0){}
			long checksum = cis.getChecksum().getValue();
			return checksum;
		}catch(Exception e){
			throw new ChecksumException("Calcolo checksum non riuscito: "+e.getMessage(),e);
		}
	}
	
}
