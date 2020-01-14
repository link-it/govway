/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.utils;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.ContentType;

import org.openspcoop2.utils.mime.MimeTypes;
import org.slf4j.Logger;

import org.openspcoop2.web.monitor.core.logger.LoggerManager;

/**
 * MimeTypeUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MimeTypeUtils {

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();	

	public static String getBaseType(String mime){
		try{
			ContentType ct = new ContentType(mime);
			return ct.getBaseType();
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return null;
		}
	}

	public static boolean isBase64(byte [] content){
		
		try{
			// NOTA: il metodo Base64.isBase64 ritorna true anche quando non è davvero un base64....
			return org.apache.commons.codec.binary.Base64.isBase64(content);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return false;
		}
	}
	
	/**
	 * Cerca un mapping per il mime type passato come parametro
	 * @param mime
	 * @return Il mapping, se trovato, oppure bin in caso di mapping non trovato
	 */
	public static String fileExtensionForMIMEType(String mime){
		try{
			return MimeTypeUtils.fileExtensionForMIMEType(mime, false);
		}catch(Exception e){
			return "bin";
		}
	}


	public static String fileExtensionForMIMEType(String mimeParam,boolean throwExeptionIfMappingNotFound) throws Exception
	{
		try{

			ContentType ct = new ContentType(mimeParam);

			MimeTypes mimeTypes = MimeTypes.getInstance();

			String ext = mimeTypes.getExtension(ct.getBaseType());

			if(ext==null && throwExeptionIfMappingNotFound)
				throw new Exception("Mapping per il mime type (base:"+ct.getBaseType()+" - full:"+mimeParam+") non trovato.");

			if(ext==null && !throwExeptionIfMappingNotFound)
				ext="bin";

			return ext;
		}catch(Exception e){
			MimeTypeUtils.log.error("Errore durante la ricerca di un mapping per il mime type:"+mimeParam,e);
			throw e;
		}
	}

	// Non piu usato, serviva per mettere una immagine nella tabella degli allegati
	public static String getMimeTypeImageClass(String mimeType) {

		if(mimeType!=null){
			mimeType = MimeTypeUtils.getBaseType(mimeType);

			if(mimeType!=null){
				// check se è uno registrato nella classe
				if(classMimesTypes.contains(mimeType)){
					return mimeType;
				}
			}
			
		}
		
		// default
		return "application/octet-stream";
	}
	private static List<String> classMimesTypes = new ArrayList<String>();
	static {
		classMimesTypes.add("application/pdf");
		classMimesTypes.add("application/postscript");
		classMimesTypes.add("text/plain");
		classMimesTypes.add("image/bmp"); 
		classMimesTypes.add("image/cgm");
		classMimesTypes.add("image/dcm");  
		classMimesTypes.add("image/dds");  
		classMimesTypes.add("image/exr");  
		classMimesTypes.add("image/gif");  
		classMimesTypes.add("image/hdr");  
		classMimesTypes.add("image/ico");  
		classMimesTypes.add("image/jng");  
		classMimesTypes.add("image/jp2");  
		classMimesTypes.add("image/jpeg");  
		classMimesTypes.add("image/jpg");  
		classMimesTypes.add("image/pbm");  
		classMimesTypes.add("image/pbmraw");  
		classMimesTypes.add("image/pcd");  
		classMimesTypes.add("image/pcx"); 
		classMimesTypes.add("image/pgm");  
		classMimesTypes.add("image/pgmraw");  
		classMimesTypes.add("image/pic");  
		classMimesTypes.add("image/png");  
		classMimesTypes.add("image/pnm");  
		classMimesTypes.add("image/psd");  
		classMimesTypes.add("image/raw");  
		classMimesTypes.add("image/rgb");  
		classMimesTypes.add("image/rgba");  
		classMimesTypes.add("image/tga");  
		classMimesTypes.add("image/tif");  
		classMimesTypes.add("image/tiff");  
		classMimesTypes.add("image/xbm");  
		classMimesTypes.add("image/xcf");  
		classMimesTypes.add("image/xpm");  
		classMimesTypes.add("text/html");  
		classMimesTypes.add("text/htm");  
		classMimesTypes.add("text/sgml");  
		classMimesTypes.add("text/xhtml");  
		classMimesTypes.add("text/xml");  
		classMimesTypes.add("video/mpeg");
		classMimesTypes.add("video/quicktime");
		classMimesTypes.add("video/x-msvideo");
		classMimesTypes.add("file-ttf");  
		classMimesTypes.add("file-otf"); 
		classMimesTypes.add("application/msword");
		classMimesTypes.add("application/x-excel"); 
		classMimesTypes.add("application/x-javascript");
		classMimesTypes.add("text/css");
		classMimesTypes.add("application/x-7z"); 
		classMimesTypes.add("application/x-a"); 
		classMimesTypes.add("application/x-ace"); 
		classMimesTypes.add("application/x-arj");  
		classMimesTypes.add("application/x-bz"); 
		classMimesTypes.add("application/x-bz2"); 
		classMimesTypes.add("application/x-cpio");  
		classMimesTypes.add("application/x-gz"); 
		classMimesTypes.add("application/x-rar"); 
		classMimesTypes.add("application/x-tnf"); 
		classMimesTypes.add("application/x-compress");  
		classMimesTypes.add("application/x-zip");  
		classMimesTypes.add("application/x-zoo");
		classMimesTypes.add("application/x-tar");
		classMimesTypes.add("application/zip");
		classMimesTypes.add("application/mspowerpoint");  
		classMimesTypes.add("application/octet-stream");
		classMimesTypes.add("application/properties");
	}


}
