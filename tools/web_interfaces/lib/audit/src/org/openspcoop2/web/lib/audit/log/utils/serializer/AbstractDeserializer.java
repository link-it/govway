/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.web.lib.audit.log.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.web.lib.audit.log.Operation;
import org.openspcoop2.web.lib.audit.log.Binary;

import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer extends org.openspcoop2.generic_project.serializer.AbstractDeserializer {



	/*
	 =================================================================================
	 Object: operation
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * @return Object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(String fileName) throws DeserializerException {
		return (Operation) this.xmlToObj(fileName, Operation.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * @return Object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(File file) throws DeserializerException {
		return (Operation) this.xmlToObj(file, Operation.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * @return Object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(InputStream in) throws DeserializerException {
		return (Operation) this.xmlToObj(in, Operation.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * @return Object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(byte[] in) throws DeserializerException {
		return (Operation) this.xmlToObj(in, Operation.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * @return Object type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperationFromString(String in) throws DeserializerException {
		return (Operation) this.xmlToObj(in.getBytes(), Operation.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: binary
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * @return Object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binary readBinary(String fileName) throws DeserializerException {
		return (Binary) this.xmlToObj(fileName, Binary.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * @return Object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binary readBinary(File file) throws DeserializerException {
		return (Binary) this.xmlToObj(file, Binary.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * @return Object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binary readBinary(InputStream in) throws DeserializerException {
		return (Binary) this.xmlToObj(in, Binary.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * @return Object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binary readBinary(byte[] in) throws DeserializerException {
		return (Binary) this.xmlToObj(in, Binary.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * @return Object type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binary readBinaryFromString(String in) throws DeserializerException {
		return (Binary) this.xmlToObj(in.getBytes(), Binary.class);
	}	
	
	
	

}
