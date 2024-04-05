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

package org.openspcoop2.utils.mime;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.UtilsException;

/**
 * Identity
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MimeTypes {

	private Map<String, Object> mapMimeToExt = new HashMap<>();
	private Map<String, String> mapExtToMime = new HashMap<>();
	
	MimeTypes() throws UtilsException{
		
		String file = "/org/openspcoop2/utils/mime/mime.types";
		try (InputStream is = MimeTypes.class.getResourceAsStream(file);){
			if(is==null){
				throw new UtilsException("File ["+file+"] in classpath not found");
			}
			init(is, this.mapMimeToExt, this.mapExtToMime);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		
	} 
	private static void init(InputStream is, Map<String, Object> mapMimeToExt, Map<String, String> mapExtToMime) throws UtilsException {
		try (
				InputStreamReader ir = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(ir);
			){
				String line;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if(!line.startsWith("#")){
						if(line.contains("\t")){
							throw new UtilsException("Line["+line+"] contains tabs");
						}
						String [] tmp = line.split(" "); 
						if(tmp.length<2){
							/** System.out.println("TYPE["+tmp[0]+"] without exts"); */
							mapMimeToExt.put(tmp[0].trim(), org.apache.commons.lang.ObjectUtils.NULL);
						}else{
							StringBuilder bf = new StringBuilder();
							for (int i = 1; i < tmp.length; i++) {
								bf.append(" EXT-"+i+"=["+tmp[i].trim()+"]");
								mapExtToMime.put(tmp[i].trim(),tmp[0].trim());
							}
							/** System.out.println("TYPE["+tmp[0]+"]"+bf.toString()); */
							mapMimeToExt.put(tmp[0].trim(), tmp[1].trim());
						}
					}
				}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public String getMimeType(File file){
		String ext = null;
		try{
			String fileName = file.getName();
			ext = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
		}catch(Exception e){
			// close
		}
		if(ext==null){
			ext = "bin";
		}
		return this.getMimeType(ext);
	}
	public String getMimeType(String ext){
		if("xml".equalsIgnoreCase(ext)) {
			return MimeTypeConstants.MEDIA_TYPE_SOAP_1_1;
		}
		else if("json".equalsIgnoreCase(ext)) {
			return MimeTypeConstants.MEDIA_TYPE_JSON;
		}
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
