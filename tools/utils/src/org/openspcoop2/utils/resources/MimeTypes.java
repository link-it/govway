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

package org.openspcoop2.utils.resources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.openspcoop2.utils.UtilsException;

/**
 * Identity
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MimeTypes {

	private Hashtable<String, Object> mapMimeToExt = new Hashtable<String, Object>();
	private Hashtable<String, String> mapExtToMime = new Hashtable<String, String>();
	
	MimeTypes() throws UtilsException{
		
		InputStream is =null;
		BufferedReader br = null;
		InputStreamReader ir = null;
		try{
			String file = "/org/openspcoop2/utils/resources/mime.types";
			is = MimeTypes.class.getResourceAsStream(file);
			if(is==null){
				throw new Exception("File ["+file+"] in classpath not found");
			}
			
			ir = new InputStreamReader(is);
			br = new BufferedReader(ir);
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if(!line.startsWith("#")){
					if(line.contains("\t")){
						throw new Exception("Line["+line+"] contains tabs");
					}
					String [] tmp = line.split(" "); 
					if(tmp.length<2){
						//System.out.println("TYPE["+tmp[0]+"] without exts");
						this.mapMimeToExt.put(tmp[0].trim(), org.apache.commons.lang.ObjectUtils.NULL);
					}else{
						StringBuffer bf = new StringBuffer();
						for (int i = 1; i < tmp.length; i++) {
							bf.append(" EXT-"+i+"=["+tmp[i].trim()+"]");
							this.mapExtToMime.put(tmp[i].trim(),tmp[0].trim());
						}
						//System.out.println("TYPE["+tmp[0]+"]"+bf.toString());
						this.mapMimeToExt.put(tmp[0].trim(), tmp[1].trim());
					}
				}
			}
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}finally{
			try{
				if(br!=null){
					br.close();
				}
			}catch(Exception eClose){}
			try{
				if(ir!=null){
					ir.close();
				}
			}catch(Exception eClose){}
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}
		
	} 
	
	
	public String getMimeType(String ext){
		return this.mapExtToMime.get(ext.trim().toLowerCase());
	}
	public String getExtension(String mime){
		Object o = this.mapMimeToExt.get(mime.trim().toLowerCase());
		if(o==null){
			return null;
		}
		if(o instanceof org.apache.commons.lang.ObjectUtils.Null){
			return null;
		}
		return (String) o;
	}
	
	public boolean existsExtension(String ext){
		return this.mapExtToMime.containsKey(ext.trim().toLowerCase());
	}
	public boolean existsMimeType(String mime, boolean checkExistsExtension){
		if(this.mapMimeToExt.containsKey(mime.trim().toLowerCase())){
			if(checkExistsExtension){
				String ext = this.getExtension(mime);
				return ext!=null;
			}
			else{
				return true;
			}
		}else{
			return false;
		}
	}
	
	
	
	// static
	
	private static MimeTypes mimeTypes = null;
	private static synchronized void init() throws UtilsException{
		if(mimeTypes==null){
			mimeTypes = new MimeTypes();
		}
	} 
	public static MimeTypes getInstance() throws UtilsException{
		if(mimeTypes==null){
			init();
		}
		return mimeTypes;
	}
	
}
