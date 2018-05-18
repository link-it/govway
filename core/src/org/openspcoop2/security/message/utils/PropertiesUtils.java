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
package org.openspcoop2.security.message.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.constants.SecurityConstants;

/**
 * PropertiesUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 06:24:34 -0500 (Fri, 26 Jan 2018) $
 */
public class PropertiesUtils {

	public static EncryptionBean getSenderEncryptionBean(MessageSecurityContext messageSecurityContext) throws NotFoundException, SecurityException {
		
		Properties p = _readProperties(messageSecurityContext.getOutgoingProperties(), SecurityConstants.ENCRYPTION_PROPERTY_REF_ID);
		if(p==null) {
			p = _readProperties(messageSecurityContext.getOutgoingProperties(), SecurityConstants.ENCRYPTION_PROPERTY_FILE);
		}
		
		if(p==null) {
			throw new NotFoundException(SecurityConstants.ENCRYPTION_PROPERTY_REF_ID+"/"+SecurityConstants.ENCRYPTION_PROPERTY_FILE+" non fornita");
		}

		EncryptionBean bean = new EncryptionBean();
		
		bean.setProperties(p);
		
		return bean;
	}
	public static EncryptionBean getReceiverEncryptionBean(MessageSecurityContext messageSecurityContext) throws NotFoundException, SecurityException {

		Properties p = _readProperties(messageSecurityContext.getIncomingProperties(), SecurityConstants.DECRYPTION_PROPERTY_REF_ID);
		if(p==null) {
			p = _readProperties(messageSecurityContext.getIncomingProperties(), SecurityConstants.DECRYPTION_PROPERTY_FILE);
		}
		
		if(p==null) {
			throw new NotFoundException(SecurityConstants.DECRYPTION_PROPERTY_REF_ID+"/"+SecurityConstants.DECRYPTION_PROPERTY_FILE+" non fornita");
		}

		EncryptionBean bean = new EncryptionBean();
		
		bean.setProperties(p);
		
		return bean;
		
	}

	public static SignatureBean getSenderSignatureBean(MessageSecurityContext messageSecurityContext) throws NotFoundException, SecurityException {

		Properties p = _readProperties(messageSecurityContext.getOutgoingProperties(), SecurityConstants.SIGNATURE_PROPERTY_REF_ID);
		if(p==null) {
			p = _readProperties(messageSecurityContext.getOutgoingProperties(), SecurityConstants.SIGNATURE_PROPERTY_FILE);
		}
		
		if(p==null) {
			throw new NotFoundException(SecurityConstants.SIGNATURE_PROPERTY_REF_ID+"/"+SecurityConstants.SIGNATURE_PROPERTY_FILE+" non fornita");
		}
		SignatureBean bean = new SignatureBean();
		
		bean.setProperties(p);
		
		return bean;
		
	}	
	public static SignatureBean getReceiverSignatureBean(MessageSecurityContext messageSecurityContext) throws NotFoundException, SecurityException {

		Properties p = _readProperties(messageSecurityContext.getIncomingProperties(), SecurityConstants.SIGNATURE_VERIFICATION_PROPERTY_REF_ID);
		if(p==null) {
			p = _readProperties(messageSecurityContext.getIncomingProperties(), SecurityConstants.SIGNATURE_VERIFICATION_PROPERTY_FILE);
		}
		if(p==null) {
			p = _readProperties(messageSecurityContext.getOutgoingProperties(), SecurityConstants.SIGNATURE_PROPERTY_REF_ID);
		}
		if(p==null) {
			p = _readProperties(messageSecurityContext.getOutgoingProperties(), SecurityConstants.SIGNATURE_PROPERTY_FILE);
		}
		
		if(p==null) {
			throw new NotFoundException(SecurityConstants.SIGNATURE_VERIFICATION_PROPERTY_REF_ID+"/"+SecurityConstants.SIGNATURE_VERIFICATION_PROPERTY_FILE+"/"+
					SecurityConstants.SIGNATURE_PROPERTY_REF_ID+"/"+SecurityConstants.SIGNATURE_PROPERTY_FILE+" non fornita");
		}

		SignatureBean bean = new SignatureBean();
		
		bean.setProperties(p);
		
		return bean;
		
	}
	
	private static Properties _readProperties(Hashtable<String,Object> map, String property) throws SecurityException {
	
		String path = null;
		File fPath = null;
		
		if(map.containsKey(property)) {
			Object o = map.get(property);
			if(o instanceof Properties) {
				return (Properties) o;
			}
			else if(o instanceof File) {
				fPath = (File) o;
			}
			else if(o instanceof String) {
				path = (String) o;
			}
			else {
				throw new SecurityException("Found property ["+property+"] with wrong type");
			}
		}
		
		if(fPath!=null) {
			try {
				return _readProperties(fPath);
			}catch(Exception e) {
				throw new SecurityException("Occurs error (property ["+property+"]): "+e.getMessage(),e);
			}
		}
		else if(path!=null) {
			File check = new File(path);
			if(check.exists()) {
				return _readProperties(check);
			}
			else {
				InputStream is = PropertiesUtils.class.getResourceAsStream(path);
				if(is==null && path.startsWith("/")==false) {
					is = PropertiesUtils.class.getResourceAsStream("/"+path);
				}
				if(is==null) {
					throw new SecurityException("Occurs error (property ["+property+"]): resource ["+path+"] not found");
				}
			}
		}
		return null;
	}
	private static Properties _readProperties(File file) throws SecurityException {
		if(file.exists()==false) {
			throw new SecurityException("Cannot exists");
		}
		if(file.canRead()==false) {
			throw new SecurityException("Cannot read");
		}
		if(file.isFile()==false) {
			throw new SecurityException("Isn't file");
		}
		Properties p = new Properties();
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file);
			p.load(fin);
		}
		catch(Exception e) {
			throw new SecurityException(e.getMessage(),e);
		}
		finally {
			try {
				fin.close();
			}catch(Exception eClose) {}
		}
		return p;
	}
}
