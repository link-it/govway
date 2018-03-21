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
package eu.domibus.configuration.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import eu.domibus.configuration.LegConfiguration;
import eu.domibus.configuration.LegConfigurations;
import eu.domibus.configuration.Service;
import eu.domibus.configuration.Mpc;
import eu.domibus.configuration.Mpcs;
import eu.domibus.configuration.Reliability;
import eu.domibus.configuration.Leg;
import eu.domibus.configuration.PropertyRef;
import eu.domibus.configuration.Binding;
import eu.domibus.configuration.Mep;
import eu.domibus.configuration.Meps;
import eu.domibus.configuration.PartyIdType;
import eu.domibus.configuration.Payload;
import eu.domibus.configuration.PayloadProfiles;
import eu.domibus.configuration.PayloadProfile;
import eu.domibus.configuration.PropertyValue;
import eu.domibus.configuration.Property;
import eu.domibus.configuration.Security;
import eu.domibus.configuration.PartyIdTypes;
import eu.domibus.configuration.Parties;
import eu.domibus.configuration.Party;
import eu.domibus.configuration.Properties;
import eu.domibus.configuration.PropertySet;
import eu.domibus.configuration.PropertyValueHeader;
import eu.domibus.configuration.ReceptionAwareness;
import eu.domibus.configuration.As4;
import eu.domibus.configuration.PropertyValueUrl;
import eu.domibus.configuration.Role;
import eu.domibus.configuration.Roles;
import eu.domibus.configuration.Configuration;
import eu.domibus.configuration.BusinessProcesses;
import eu.domibus.configuration.Securities;
import eu.domibus.configuration.ErrorHandlings;
import eu.domibus.configuration.Agreements;
import eu.domibus.configuration.Services;
import eu.domibus.configuration.Actions;
import eu.domibus.configuration.Process;
import eu.domibus.configuration.Agreement;
import eu.domibus.configuration.InitiatorParty;
import eu.domibus.configuration.Action;
import eu.domibus.configuration.Identifier;
import eu.domibus.configuration.Header;
import eu.domibus.configuration.ErrorHandling;
import eu.domibus.configuration.Legs;
import eu.domibus.configuration.Attachment;
import eu.domibus.configuration.Url;
import eu.domibus.configuration.InitiatorParties;
import eu.domibus.configuration.ResponderParties;
import eu.domibus.configuration.ResponderParty;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.lang.reflect.Method;

import javax.xml.bind.JAXBElement;

/**     
 * XML Serializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractSerializer {


	protected abstract WriteToSerializerType getType();
	
	protected void _objToXml(OutputStream out, Class<?> c, Object object,
			boolean prettyPrint) throws Exception {
		if(object instanceof JAXBElement){
			// solo per il tipo WriteToSerializerType.JAXB
			JaxbUtils.objToXml(out, c, object, prettyPrint);
		}else{
			Method m = c.getMethod("writeTo", OutputStream.class, WriteToSerializerType.class);
			m.invoke(object, out, this.getType());
		}
	}
	
	protected void objToXml(OutputStream out,Class<?> c,Object object,boolean prettyPrint) throws SerializerException{
		try{
			this._objToXml(out, c, object, prettyPrint);
		}catch(Exception e){
			throw new SerializerException(e.getMessage(), e);
		}
		finally{
			try{
				out.flush();
			}catch(Exception e){}
		}
	}
	protected void objToXml(String fileName,Class<?> c,Object object,boolean prettyPrint) throws SerializerException{
		try{
			this.objToXml(new File(fileName), c, object, prettyPrint);
		}catch(Exception e){
			throw new SerializerException(e.getMessage(), e);
		}
	}
	protected void objToXml(File file,Class<?> c,Object object,boolean prettyPrint) throws SerializerException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(file);
			this._objToXml(fout, c, object, prettyPrint);
		}catch(Exception e){
			throw new SerializerException(e.getMessage(), e);
		}
		finally{
			try{
				fout.flush();
			}catch(Exception e){}
			try{
				fout.close();
			}catch(Exception e){}
		}
	}
	protected ByteArrayOutputStream objToXml(Class<?> c,Object object,boolean prettyPrint) throws SerializerException{
		ByteArrayOutputStream bout = null;
		try{
			bout = new ByteArrayOutputStream();
			this._objToXml(bout, c, object, prettyPrint);
		}catch(Exception e){
			throw new SerializerException(e.getMessage(), e);
		}
		finally{
			try{
				bout.flush();
			}catch(Exception e){}
			try{
				bout.close();
			}catch(Exception e){}
		}
		return bout;
	}




	/*
	 =================================================================================
	 Object: legConfiguration
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>legConfiguration</var> of type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>legConfiguration</var>
	 * @param legConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,LegConfiguration legConfiguration) throws SerializerException {
		this.objToXml(fileName, LegConfiguration.class, legConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>legConfiguration</var> of type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>legConfiguration</var>
	 * @param legConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,LegConfiguration legConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, LegConfiguration.class, legConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>legConfiguration</var> of type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>legConfiguration</var>
	 * @param legConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,LegConfiguration legConfiguration) throws SerializerException {
		this.objToXml(file, LegConfiguration.class, legConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>legConfiguration</var> of type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>legConfiguration</var>
	 * @param legConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,LegConfiguration legConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, LegConfiguration.class, legConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>legConfiguration</var> of type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>legConfiguration</var>
	 * @param legConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,LegConfiguration legConfiguration) throws SerializerException {
		this.objToXml(out, LegConfiguration.class, legConfiguration, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>legConfiguration</var> of type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>legConfiguration</var>
	 * @param legConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,LegConfiguration legConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, LegConfiguration.class, legConfiguration, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>legConfiguration</var> of type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param legConfiguration Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(LegConfiguration legConfiguration) throws SerializerException {
		return this.objToXml(LegConfiguration.class, legConfiguration, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>legConfiguration</var> of type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param legConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(LegConfiguration legConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(LegConfiguration.class, legConfiguration, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>legConfiguration</var> of type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param legConfiguration Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(LegConfiguration legConfiguration) throws SerializerException {
		return this.objToXml(LegConfiguration.class, legConfiguration, false).toString();
	}
	/**
	 * Serialize to String the object <var>legConfiguration</var> of type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param legConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(LegConfiguration legConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(LegConfiguration.class, legConfiguration, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: legConfigurations
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>legConfigurations</var> of type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param fileName Xml file to serialize the object <var>legConfigurations</var>
	 * @param legConfigurations Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,LegConfigurations legConfigurations) throws SerializerException {
		this.objToXml(fileName, LegConfigurations.class, legConfigurations, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>legConfigurations</var> of type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param fileName Xml file to serialize the object <var>legConfigurations</var>
	 * @param legConfigurations Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,LegConfigurations legConfigurations,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, LegConfigurations.class, legConfigurations, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>legConfigurations</var> of type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param file Xml file to serialize the object <var>legConfigurations</var>
	 * @param legConfigurations Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,LegConfigurations legConfigurations) throws SerializerException {
		this.objToXml(file, LegConfigurations.class, legConfigurations, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>legConfigurations</var> of type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param file Xml file to serialize the object <var>legConfigurations</var>
	 * @param legConfigurations Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,LegConfigurations legConfigurations,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, LegConfigurations.class, legConfigurations, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>legConfigurations</var> of type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param out OutputStream to serialize the object <var>legConfigurations</var>
	 * @param legConfigurations Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,LegConfigurations legConfigurations) throws SerializerException {
		this.objToXml(out, LegConfigurations.class, legConfigurations, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>legConfigurations</var> of type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param out OutputStream to serialize the object <var>legConfigurations</var>
	 * @param legConfigurations Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,LegConfigurations legConfigurations,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, LegConfigurations.class, legConfigurations, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>legConfigurations</var> of type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param legConfigurations Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(LegConfigurations legConfigurations) throws SerializerException {
		return this.objToXml(LegConfigurations.class, legConfigurations, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>legConfigurations</var> of type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param legConfigurations Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(LegConfigurations legConfigurations,boolean prettyPrint) throws SerializerException {
		return this.objToXml(LegConfigurations.class, legConfigurations, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>legConfigurations</var> of type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param legConfigurations Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(LegConfigurations legConfigurations) throws SerializerException {
		return this.objToXml(LegConfigurations.class, legConfigurations, false).toString();
	}
	/**
	 * Serialize to String the object <var>legConfigurations</var> of type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param legConfigurations Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(LegConfigurations legConfigurations,boolean prettyPrint) throws SerializerException {
		return this.objToXml(LegConfigurations.class, legConfigurations, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: service
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>service</var> of type {@link eu.domibus.configuration.Service}
	 * 
	 * @param fileName Xml file to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Service service) throws SerializerException {
		this.objToXml(fileName, Service.class, service, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>service</var> of type {@link eu.domibus.configuration.Service}
	 * 
	 * @param fileName Xml file to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Service service,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Service.class, service, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>service</var> of type {@link eu.domibus.configuration.Service}
	 * 
	 * @param file Xml file to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Service service) throws SerializerException {
		this.objToXml(file, Service.class, service, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>service</var> of type {@link eu.domibus.configuration.Service}
	 * 
	 * @param file Xml file to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Service service,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Service.class, service, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>service</var> of type {@link eu.domibus.configuration.Service}
	 * 
	 * @param out OutputStream to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Service service) throws SerializerException {
		this.objToXml(out, Service.class, service, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>service</var> of type {@link eu.domibus.configuration.Service}
	 * 
	 * @param out OutputStream to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Service service,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Service.class, service, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>service</var> of type {@link eu.domibus.configuration.Service}
	 * 
	 * @param service Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Service service) throws SerializerException {
		return this.objToXml(Service.class, service, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>service</var> of type {@link eu.domibus.configuration.Service}
	 * 
	 * @param service Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Service service,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Service.class, service, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>service</var> of type {@link eu.domibus.configuration.Service}
	 * 
	 * @param service Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Service service) throws SerializerException {
		return this.objToXml(Service.class, service, false).toString();
	}
	/**
	 * Serialize to String the object <var>service</var> of type {@link eu.domibus.configuration.Service}
	 * 
	 * @param service Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Service service,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Service.class, service, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: mpc
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>mpc</var> of type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param fileName Xml file to serialize the object <var>mpc</var>
	 * @param mpc Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Mpc mpc) throws SerializerException {
		this.objToXml(fileName, Mpc.class, mpc, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>mpc</var> of type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param fileName Xml file to serialize the object <var>mpc</var>
	 * @param mpc Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Mpc mpc,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Mpc.class, mpc, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>mpc</var> of type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param file Xml file to serialize the object <var>mpc</var>
	 * @param mpc Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Mpc mpc) throws SerializerException {
		this.objToXml(file, Mpc.class, mpc, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>mpc</var> of type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param file Xml file to serialize the object <var>mpc</var>
	 * @param mpc Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Mpc mpc,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Mpc.class, mpc, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>mpc</var> of type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param out OutputStream to serialize the object <var>mpc</var>
	 * @param mpc Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Mpc mpc) throws SerializerException {
		this.objToXml(out, Mpc.class, mpc, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>mpc</var> of type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param out OutputStream to serialize the object <var>mpc</var>
	 * @param mpc Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Mpc mpc,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Mpc.class, mpc, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>mpc</var> of type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param mpc Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Mpc mpc) throws SerializerException {
		return this.objToXml(Mpc.class, mpc, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>mpc</var> of type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param mpc Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Mpc mpc,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Mpc.class, mpc, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>mpc</var> of type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param mpc Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Mpc mpc) throws SerializerException {
		return this.objToXml(Mpc.class, mpc, false).toString();
	}
	/**
	 * Serialize to String the object <var>mpc</var> of type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param mpc Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Mpc mpc,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Mpc.class, mpc, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: mpcs
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>mpcs</var> of type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param fileName Xml file to serialize the object <var>mpcs</var>
	 * @param mpcs Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Mpcs mpcs) throws SerializerException {
		this.objToXml(fileName, Mpcs.class, mpcs, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>mpcs</var> of type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param fileName Xml file to serialize the object <var>mpcs</var>
	 * @param mpcs Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Mpcs mpcs,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Mpcs.class, mpcs, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>mpcs</var> of type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param file Xml file to serialize the object <var>mpcs</var>
	 * @param mpcs Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Mpcs mpcs) throws SerializerException {
		this.objToXml(file, Mpcs.class, mpcs, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>mpcs</var> of type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param file Xml file to serialize the object <var>mpcs</var>
	 * @param mpcs Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Mpcs mpcs,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Mpcs.class, mpcs, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>mpcs</var> of type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param out OutputStream to serialize the object <var>mpcs</var>
	 * @param mpcs Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Mpcs mpcs) throws SerializerException {
		this.objToXml(out, Mpcs.class, mpcs, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>mpcs</var> of type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param out OutputStream to serialize the object <var>mpcs</var>
	 * @param mpcs Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Mpcs mpcs,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Mpcs.class, mpcs, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>mpcs</var> of type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param mpcs Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Mpcs mpcs) throws SerializerException {
		return this.objToXml(Mpcs.class, mpcs, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>mpcs</var> of type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param mpcs Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Mpcs mpcs,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Mpcs.class, mpcs, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>mpcs</var> of type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param mpcs Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Mpcs mpcs) throws SerializerException {
		return this.objToXml(Mpcs.class, mpcs, false).toString();
	}
	/**
	 * Serialize to String the object <var>mpcs</var> of type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param mpcs Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Mpcs mpcs,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Mpcs.class, mpcs, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: reliability
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>reliability</var> of type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param fileName Xml file to serialize the object <var>reliability</var>
	 * @param reliability Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Reliability reliability) throws SerializerException {
		this.objToXml(fileName, Reliability.class, reliability, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>reliability</var> of type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param fileName Xml file to serialize the object <var>reliability</var>
	 * @param reliability Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Reliability reliability,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Reliability.class, reliability, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>reliability</var> of type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param file Xml file to serialize the object <var>reliability</var>
	 * @param reliability Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Reliability reliability) throws SerializerException {
		this.objToXml(file, Reliability.class, reliability, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>reliability</var> of type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param file Xml file to serialize the object <var>reliability</var>
	 * @param reliability Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Reliability reliability,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Reliability.class, reliability, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>reliability</var> of type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param out OutputStream to serialize the object <var>reliability</var>
	 * @param reliability Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Reliability reliability) throws SerializerException {
		this.objToXml(out, Reliability.class, reliability, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>reliability</var> of type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param out OutputStream to serialize the object <var>reliability</var>
	 * @param reliability Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Reliability reliability,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Reliability.class, reliability, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>reliability</var> of type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param reliability Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Reliability reliability) throws SerializerException {
		return this.objToXml(Reliability.class, reliability, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>reliability</var> of type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param reliability Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Reliability reliability,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Reliability.class, reliability, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>reliability</var> of type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param reliability Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Reliability reliability) throws SerializerException {
		return this.objToXml(Reliability.class, reliability, false).toString();
	}
	/**
	 * Serialize to String the object <var>reliability</var> of type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param reliability Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Reliability reliability,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Reliability.class, reliability, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: leg
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>leg</var> of type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param fileName Xml file to serialize the object <var>leg</var>
	 * @param leg Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Leg leg) throws SerializerException {
		this.objToXml(fileName, Leg.class, leg, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>leg</var> of type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param fileName Xml file to serialize the object <var>leg</var>
	 * @param leg Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Leg leg,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Leg.class, leg, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>leg</var> of type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param file Xml file to serialize the object <var>leg</var>
	 * @param leg Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Leg leg) throws SerializerException {
		this.objToXml(file, Leg.class, leg, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>leg</var> of type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param file Xml file to serialize the object <var>leg</var>
	 * @param leg Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Leg leg,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Leg.class, leg, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>leg</var> of type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param out OutputStream to serialize the object <var>leg</var>
	 * @param leg Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Leg leg) throws SerializerException {
		this.objToXml(out, Leg.class, leg, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>leg</var> of type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param out OutputStream to serialize the object <var>leg</var>
	 * @param leg Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Leg leg,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Leg.class, leg, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>leg</var> of type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param leg Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Leg leg) throws SerializerException {
		return this.objToXml(Leg.class, leg, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>leg</var> of type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param leg Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Leg leg,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Leg.class, leg, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>leg</var> of type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param leg Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Leg leg) throws SerializerException {
		return this.objToXml(Leg.class, leg, false).toString();
	}
	/**
	 * Serialize to String the object <var>leg</var> of type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param leg Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Leg leg,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Leg.class, leg, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: propertyRef
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>propertyRef</var> of type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param fileName Xml file to serialize the object <var>propertyRef</var>
	 * @param propertyRef Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PropertyRef propertyRef) throws SerializerException {
		this.objToXml(fileName, PropertyRef.class, propertyRef, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>propertyRef</var> of type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param fileName Xml file to serialize the object <var>propertyRef</var>
	 * @param propertyRef Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PropertyRef propertyRef,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PropertyRef.class, propertyRef, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>propertyRef</var> of type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param file Xml file to serialize the object <var>propertyRef</var>
	 * @param propertyRef Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PropertyRef propertyRef) throws SerializerException {
		this.objToXml(file, PropertyRef.class, propertyRef, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>propertyRef</var> of type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param file Xml file to serialize the object <var>propertyRef</var>
	 * @param propertyRef Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PropertyRef propertyRef,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PropertyRef.class, propertyRef, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>propertyRef</var> of type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param out OutputStream to serialize the object <var>propertyRef</var>
	 * @param propertyRef Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PropertyRef propertyRef) throws SerializerException {
		this.objToXml(out, PropertyRef.class, propertyRef, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>propertyRef</var> of type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param out OutputStream to serialize the object <var>propertyRef</var>
	 * @param propertyRef Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PropertyRef propertyRef,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PropertyRef.class, propertyRef, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>propertyRef</var> of type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param propertyRef Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PropertyRef propertyRef) throws SerializerException {
		return this.objToXml(PropertyRef.class, propertyRef, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>propertyRef</var> of type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param propertyRef Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PropertyRef propertyRef,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PropertyRef.class, propertyRef, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>propertyRef</var> of type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param propertyRef Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PropertyRef propertyRef) throws SerializerException {
		return this.objToXml(PropertyRef.class, propertyRef, false).toString();
	}
	/**
	 * Serialize to String the object <var>propertyRef</var> of type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param propertyRef Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PropertyRef propertyRef,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PropertyRef.class, propertyRef, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: binding
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>binding</var> of type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param fileName Xml file to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Binding binding) throws SerializerException {
		this.objToXml(fileName, Binding.class, binding, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>binding</var> of type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param fileName Xml file to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Binding binding,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Binding.class, binding, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>binding</var> of type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param file Xml file to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Binding binding) throws SerializerException {
		this.objToXml(file, Binding.class, binding, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>binding</var> of type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param file Xml file to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Binding binding,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Binding.class, binding, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>binding</var> of type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param out OutputStream to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Binding binding) throws SerializerException {
		this.objToXml(out, Binding.class, binding, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>binding</var> of type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param out OutputStream to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Binding binding,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Binding.class, binding, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>binding</var> of type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param binding Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Binding binding) throws SerializerException {
		return this.objToXml(Binding.class, binding, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>binding</var> of type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param binding Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Binding binding,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Binding.class, binding, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>binding</var> of type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param binding Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Binding binding) throws SerializerException {
		return this.objToXml(Binding.class, binding, false).toString();
	}
	/**
	 * Serialize to String the object <var>binding</var> of type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param binding Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Binding binding,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Binding.class, binding, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: mep
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>mep</var> of type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param fileName Xml file to serialize the object <var>mep</var>
	 * @param mep Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Mep mep) throws SerializerException {
		this.objToXml(fileName, Mep.class, mep, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>mep</var> of type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param fileName Xml file to serialize the object <var>mep</var>
	 * @param mep Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Mep mep,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Mep.class, mep, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>mep</var> of type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param file Xml file to serialize the object <var>mep</var>
	 * @param mep Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Mep mep) throws SerializerException {
		this.objToXml(file, Mep.class, mep, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>mep</var> of type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param file Xml file to serialize the object <var>mep</var>
	 * @param mep Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Mep mep,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Mep.class, mep, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>mep</var> of type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param out OutputStream to serialize the object <var>mep</var>
	 * @param mep Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Mep mep) throws SerializerException {
		this.objToXml(out, Mep.class, mep, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>mep</var> of type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param out OutputStream to serialize the object <var>mep</var>
	 * @param mep Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Mep mep,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Mep.class, mep, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>mep</var> of type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param mep Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Mep mep) throws SerializerException {
		return this.objToXml(Mep.class, mep, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>mep</var> of type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param mep Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Mep mep,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Mep.class, mep, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>mep</var> of type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param mep Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Mep mep) throws SerializerException {
		return this.objToXml(Mep.class, mep, false).toString();
	}
	/**
	 * Serialize to String the object <var>mep</var> of type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param mep Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Mep mep,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Mep.class, mep, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: meps
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>meps</var> of type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param fileName Xml file to serialize the object <var>meps</var>
	 * @param meps Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Meps meps) throws SerializerException {
		this.objToXml(fileName, Meps.class, meps, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>meps</var> of type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param fileName Xml file to serialize the object <var>meps</var>
	 * @param meps Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Meps meps,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Meps.class, meps, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>meps</var> of type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param file Xml file to serialize the object <var>meps</var>
	 * @param meps Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Meps meps) throws SerializerException {
		this.objToXml(file, Meps.class, meps, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>meps</var> of type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param file Xml file to serialize the object <var>meps</var>
	 * @param meps Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Meps meps,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Meps.class, meps, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>meps</var> of type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param out OutputStream to serialize the object <var>meps</var>
	 * @param meps Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Meps meps) throws SerializerException {
		this.objToXml(out, Meps.class, meps, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>meps</var> of type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param out OutputStream to serialize the object <var>meps</var>
	 * @param meps Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Meps meps,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Meps.class, meps, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>meps</var> of type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param meps Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Meps meps) throws SerializerException {
		return this.objToXml(Meps.class, meps, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>meps</var> of type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param meps Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Meps meps,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Meps.class, meps, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>meps</var> of type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param meps Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Meps meps) throws SerializerException {
		return this.objToXml(Meps.class, meps, false).toString();
	}
	/**
	 * Serialize to String the object <var>meps</var> of type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param meps Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Meps meps,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Meps.class, meps, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: partyIdType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>partyIdType</var> of type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param fileName Xml file to serialize the object <var>partyIdType</var>
	 * @param partyIdType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PartyIdType partyIdType) throws SerializerException {
		this.objToXml(fileName, PartyIdType.class, partyIdType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>partyIdType</var> of type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param fileName Xml file to serialize the object <var>partyIdType</var>
	 * @param partyIdType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PartyIdType partyIdType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PartyIdType.class, partyIdType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>partyIdType</var> of type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param file Xml file to serialize the object <var>partyIdType</var>
	 * @param partyIdType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PartyIdType partyIdType) throws SerializerException {
		this.objToXml(file, PartyIdType.class, partyIdType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>partyIdType</var> of type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param file Xml file to serialize the object <var>partyIdType</var>
	 * @param partyIdType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PartyIdType partyIdType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PartyIdType.class, partyIdType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>partyIdType</var> of type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param out OutputStream to serialize the object <var>partyIdType</var>
	 * @param partyIdType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PartyIdType partyIdType) throws SerializerException {
		this.objToXml(out, PartyIdType.class, partyIdType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>partyIdType</var> of type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param out OutputStream to serialize the object <var>partyIdType</var>
	 * @param partyIdType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PartyIdType partyIdType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PartyIdType.class, partyIdType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>partyIdType</var> of type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param partyIdType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PartyIdType partyIdType) throws SerializerException {
		return this.objToXml(PartyIdType.class, partyIdType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>partyIdType</var> of type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param partyIdType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PartyIdType partyIdType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PartyIdType.class, partyIdType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>partyIdType</var> of type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param partyIdType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PartyIdType partyIdType) throws SerializerException {
		return this.objToXml(PartyIdType.class, partyIdType, false).toString();
	}
	/**
	 * Serialize to String the object <var>partyIdType</var> of type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param partyIdType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PartyIdType partyIdType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PartyIdType.class, partyIdType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: payload
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>payload</var> of type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param fileName Xml file to serialize the object <var>payload</var>
	 * @param payload Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Payload payload) throws SerializerException {
		this.objToXml(fileName, Payload.class, payload, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>payload</var> of type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param fileName Xml file to serialize the object <var>payload</var>
	 * @param payload Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Payload payload,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Payload.class, payload, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>payload</var> of type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param file Xml file to serialize the object <var>payload</var>
	 * @param payload Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Payload payload) throws SerializerException {
		this.objToXml(file, Payload.class, payload, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>payload</var> of type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param file Xml file to serialize the object <var>payload</var>
	 * @param payload Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Payload payload,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Payload.class, payload, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>payload</var> of type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param out OutputStream to serialize the object <var>payload</var>
	 * @param payload Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Payload payload) throws SerializerException {
		this.objToXml(out, Payload.class, payload, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>payload</var> of type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param out OutputStream to serialize the object <var>payload</var>
	 * @param payload Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Payload payload,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Payload.class, payload, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>payload</var> of type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param payload Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Payload payload) throws SerializerException {
		return this.objToXml(Payload.class, payload, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>payload</var> of type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param payload Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Payload payload,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Payload.class, payload, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>payload</var> of type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param payload Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Payload payload) throws SerializerException {
		return this.objToXml(Payload.class, payload, false).toString();
	}
	/**
	 * Serialize to String the object <var>payload</var> of type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param payload Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Payload payload,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Payload.class, payload, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: payloadProfiles
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>payloadProfiles</var> of type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param fileName Xml file to serialize the object <var>payloadProfiles</var>
	 * @param payloadProfiles Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PayloadProfiles payloadProfiles) throws SerializerException {
		this.objToXml(fileName, PayloadProfiles.class, payloadProfiles, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>payloadProfiles</var> of type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param fileName Xml file to serialize the object <var>payloadProfiles</var>
	 * @param payloadProfiles Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PayloadProfiles payloadProfiles,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PayloadProfiles.class, payloadProfiles, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>payloadProfiles</var> of type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param file Xml file to serialize the object <var>payloadProfiles</var>
	 * @param payloadProfiles Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PayloadProfiles payloadProfiles) throws SerializerException {
		this.objToXml(file, PayloadProfiles.class, payloadProfiles, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>payloadProfiles</var> of type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param file Xml file to serialize the object <var>payloadProfiles</var>
	 * @param payloadProfiles Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PayloadProfiles payloadProfiles,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PayloadProfiles.class, payloadProfiles, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>payloadProfiles</var> of type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param out OutputStream to serialize the object <var>payloadProfiles</var>
	 * @param payloadProfiles Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PayloadProfiles payloadProfiles) throws SerializerException {
		this.objToXml(out, PayloadProfiles.class, payloadProfiles, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>payloadProfiles</var> of type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param out OutputStream to serialize the object <var>payloadProfiles</var>
	 * @param payloadProfiles Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PayloadProfiles payloadProfiles,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PayloadProfiles.class, payloadProfiles, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>payloadProfiles</var> of type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param payloadProfiles Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PayloadProfiles payloadProfiles) throws SerializerException {
		return this.objToXml(PayloadProfiles.class, payloadProfiles, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>payloadProfiles</var> of type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param payloadProfiles Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PayloadProfiles payloadProfiles,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PayloadProfiles.class, payloadProfiles, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>payloadProfiles</var> of type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param payloadProfiles Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PayloadProfiles payloadProfiles) throws SerializerException {
		return this.objToXml(PayloadProfiles.class, payloadProfiles, false).toString();
	}
	/**
	 * Serialize to String the object <var>payloadProfiles</var> of type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param payloadProfiles Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PayloadProfiles payloadProfiles,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PayloadProfiles.class, payloadProfiles, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: payloadProfile
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>payloadProfile</var> of type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param fileName Xml file to serialize the object <var>payloadProfile</var>
	 * @param payloadProfile Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PayloadProfile payloadProfile) throws SerializerException {
		this.objToXml(fileName, PayloadProfile.class, payloadProfile, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>payloadProfile</var> of type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param fileName Xml file to serialize the object <var>payloadProfile</var>
	 * @param payloadProfile Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PayloadProfile payloadProfile,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PayloadProfile.class, payloadProfile, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>payloadProfile</var> of type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param file Xml file to serialize the object <var>payloadProfile</var>
	 * @param payloadProfile Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PayloadProfile payloadProfile) throws SerializerException {
		this.objToXml(file, PayloadProfile.class, payloadProfile, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>payloadProfile</var> of type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param file Xml file to serialize the object <var>payloadProfile</var>
	 * @param payloadProfile Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PayloadProfile payloadProfile,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PayloadProfile.class, payloadProfile, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>payloadProfile</var> of type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param out OutputStream to serialize the object <var>payloadProfile</var>
	 * @param payloadProfile Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PayloadProfile payloadProfile) throws SerializerException {
		this.objToXml(out, PayloadProfile.class, payloadProfile, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>payloadProfile</var> of type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param out OutputStream to serialize the object <var>payloadProfile</var>
	 * @param payloadProfile Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PayloadProfile payloadProfile,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PayloadProfile.class, payloadProfile, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>payloadProfile</var> of type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param payloadProfile Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PayloadProfile payloadProfile) throws SerializerException {
		return this.objToXml(PayloadProfile.class, payloadProfile, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>payloadProfile</var> of type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param payloadProfile Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PayloadProfile payloadProfile,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PayloadProfile.class, payloadProfile, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>payloadProfile</var> of type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param payloadProfile Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PayloadProfile payloadProfile) throws SerializerException {
		return this.objToXml(PayloadProfile.class, payloadProfile, false).toString();
	}
	/**
	 * Serialize to String the object <var>payloadProfile</var> of type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param payloadProfile Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PayloadProfile payloadProfile,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PayloadProfile.class, payloadProfile, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: PropertyValue
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>propertyValue</var> of type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param fileName Xml file to serialize the object <var>propertyValue</var>
	 * @param propertyValue Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PropertyValue propertyValue) throws SerializerException {
		this.objToXml(fileName, PropertyValue.class, propertyValue, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>propertyValue</var> of type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param fileName Xml file to serialize the object <var>propertyValue</var>
	 * @param propertyValue Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PropertyValue propertyValue,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PropertyValue.class, propertyValue, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>propertyValue</var> of type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param file Xml file to serialize the object <var>propertyValue</var>
	 * @param propertyValue Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PropertyValue propertyValue) throws SerializerException {
		this.objToXml(file, PropertyValue.class, propertyValue, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>propertyValue</var> of type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param file Xml file to serialize the object <var>propertyValue</var>
	 * @param propertyValue Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PropertyValue propertyValue,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PropertyValue.class, propertyValue, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>propertyValue</var> of type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param out OutputStream to serialize the object <var>propertyValue</var>
	 * @param propertyValue Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PropertyValue propertyValue) throws SerializerException {
		this.objToXml(out, PropertyValue.class, propertyValue, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>propertyValue</var> of type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param out OutputStream to serialize the object <var>propertyValue</var>
	 * @param propertyValue Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PropertyValue propertyValue,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PropertyValue.class, propertyValue, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>propertyValue</var> of type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param propertyValue Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PropertyValue propertyValue) throws SerializerException {
		return this.objToXml(PropertyValue.class, propertyValue, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>propertyValue</var> of type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param propertyValue Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PropertyValue propertyValue,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PropertyValue.class, propertyValue, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>propertyValue</var> of type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param propertyValue Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PropertyValue propertyValue) throws SerializerException {
		return this.objToXml(PropertyValue.class, propertyValue, false).toString();
	}
	/**
	 * Serialize to String the object <var>propertyValue</var> of type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param propertyValue Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PropertyValue propertyValue,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PropertyValue.class, propertyValue, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: property
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>property</var> of type {@link eu.domibus.configuration.Property}
	 * 
	 * @param fileName Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Property property) throws SerializerException {
		this.objToXml(fileName, Property.class, property, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>property</var> of type {@link eu.domibus.configuration.Property}
	 * 
	 * @param fileName Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Property property,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Property.class, property, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>property</var> of type {@link eu.domibus.configuration.Property}
	 * 
	 * @param file Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Property property) throws SerializerException {
		this.objToXml(file, Property.class, property, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>property</var> of type {@link eu.domibus.configuration.Property}
	 * 
	 * @param file Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Property property,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Property.class, property, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>property</var> of type {@link eu.domibus.configuration.Property}
	 * 
	 * @param out OutputStream to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Property property) throws SerializerException {
		this.objToXml(out, Property.class, property, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>property</var> of type {@link eu.domibus.configuration.Property}
	 * 
	 * @param out OutputStream to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Property property,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Property.class, property, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>property</var> of type {@link eu.domibus.configuration.Property}
	 * 
	 * @param property Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Property property) throws SerializerException {
		return this.objToXml(Property.class, property, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>property</var> of type {@link eu.domibus.configuration.Property}
	 * 
	 * @param property Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Property property,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Property.class, property, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>property</var> of type {@link eu.domibus.configuration.Property}
	 * 
	 * @param property Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Property property) throws SerializerException {
		return this.objToXml(Property.class, property, false).toString();
	}
	/**
	 * Serialize to String the object <var>property</var> of type {@link eu.domibus.configuration.Property}
	 * 
	 * @param property Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Property property,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Property.class, property, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: security
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>security</var> of type {@link eu.domibus.configuration.Security}
	 * 
	 * @param fileName Xml file to serialize the object <var>security</var>
	 * @param security Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Security security) throws SerializerException {
		this.objToXml(fileName, Security.class, security, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>security</var> of type {@link eu.domibus.configuration.Security}
	 * 
	 * @param fileName Xml file to serialize the object <var>security</var>
	 * @param security Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Security security,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Security.class, security, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>security</var> of type {@link eu.domibus.configuration.Security}
	 * 
	 * @param file Xml file to serialize the object <var>security</var>
	 * @param security Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Security security) throws SerializerException {
		this.objToXml(file, Security.class, security, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>security</var> of type {@link eu.domibus.configuration.Security}
	 * 
	 * @param file Xml file to serialize the object <var>security</var>
	 * @param security Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Security security,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Security.class, security, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>security</var> of type {@link eu.domibus.configuration.Security}
	 * 
	 * @param out OutputStream to serialize the object <var>security</var>
	 * @param security Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Security security) throws SerializerException {
		this.objToXml(out, Security.class, security, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>security</var> of type {@link eu.domibus.configuration.Security}
	 * 
	 * @param out OutputStream to serialize the object <var>security</var>
	 * @param security Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Security security,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Security.class, security, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>security</var> of type {@link eu.domibus.configuration.Security}
	 * 
	 * @param security Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Security security) throws SerializerException {
		return this.objToXml(Security.class, security, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>security</var> of type {@link eu.domibus.configuration.Security}
	 * 
	 * @param security Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Security security,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Security.class, security, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>security</var> of type {@link eu.domibus.configuration.Security}
	 * 
	 * @param security Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Security security) throws SerializerException {
		return this.objToXml(Security.class, security, false).toString();
	}
	/**
	 * Serialize to String the object <var>security</var> of type {@link eu.domibus.configuration.Security}
	 * 
	 * @param security Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Security security,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Security.class, security, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: partyIdTypes
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>partyIdTypes</var> of type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param fileName Xml file to serialize the object <var>partyIdTypes</var>
	 * @param partyIdTypes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PartyIdTypes partyIdTypes) throws SerializerException {
		this.objToXml(fileName, PartyIdTypes.class, partyIdTypes, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>partyIdTypes</var> of type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param fileName Xml file to serialize the object <var>partyIdTypes</var>
	 * @param partyIdTypes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PartyIdTypes partyIdTypes,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PartyIdTypes.class, partyIdTypes, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>partyIdTypes</var> of type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param file Xml file to serialize the object <var>partyIdTypes</var>
	 * @param partyIdTypes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PartyIdTypes partyIdTypes) throws SerializerException {
		this.objToXml(file, PartyIdTypes.class, partyIdTypes, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>partyIdTypes</var> of type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param file Xml file to serialize the object <var>partyIdTypes</var>
	 * @param partyIdTypes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PartyIdTypes partyIdTypes,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PartyIdTypes.class, partyIdTypes, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>partyIdTypes</var> of type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param out OutputStream to serialize the object <var>partyIdTypes</var>
	 * @param partyIdTypes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PartyIdTypes partyIdTypes) throws SerializerException {
		this.objToXml(out, PartyIdTypes.class, partyIdTypes, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>partyIdTypes</var> of type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param out OutputStream to serialize the object <var>partyIdTypes</var>
	 * @param partyIdTypes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PartyIdTypes partyIdTypes,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PartyIdTypes.class, partyIdTypes, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>partyIdTypes</var> of type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param partyIdTypes Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PartyIdTypes partyIdTypes) throws SerializerException {
		return this.objToXml(PartyIdTypes.class, partyIdTypes, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>partyIdTypes</var> of type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param partyIdTypes Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PartyIdTypes partyIdTypes,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PartyIdTypes.class, partyIdTypes, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>partyIdTypes</var> of type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param partyIdTypes Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PartyIdTypes partyIdTypes) throws SerializerException {
		return this.objToXml(PartyIdTypes.class, partyIdTypes, false).toString();
	}
	/**
	 * Serialize to String the object <var>partyIdTypes</var> of type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param partyIdTypes Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PartyIdTypes partyIdTypes,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PartyIdTypes.class, partyIdTypes, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: parties
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>parties</var> of type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param fileName Xml file to serialize the object <var>parties</var>
	 * @param parties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Parties parties) throws SerializerException {
		this.objToXml(fileName, Parties.class, parties, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>parties</var> of type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param fileName Xml file to serialize the object <var>parties</var>
	 * @param parties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Parties parties,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Parties.class, parties, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>parties</var> of type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param file Xml file to serialize the object <var>parties</var>
	 * @param parties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Parties parties) throws SerializerException {
		this.objToXml(file, Parties.class, parties, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>parties</var> of type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param file Xml file to serialize the object <var>parties</var>
	 * @param parties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Parties parties,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Parties.class, parties, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>parties</var> of type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param out OutputStream to serialize the object <var>parties</var>
	 * @param parties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Parties parties) throws SerializerException {
		this.objToXml(out, Parties.class, parties, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>parties</var> of type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param out OutputStream to serialize the object <var>parties</var>
	 * @param parties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Parties parties,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Parties.class, parties, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>parties</var> of type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param parties Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Parties parties) throws SerializerException {
		return this.objToXml(Parties.class, parties, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>parties</var> of type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param parties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Parties parties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Parties.class, parties, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>parties</var> of type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param parties Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Parties parties) throws SerializerException {
		return this.objToXml(Parties.class, parties, false).toString();
	}
	/**
	 * Serialize to String the object <var>parties</var> of type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param parties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Parties parties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Parties.class, parties, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: party
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>party</var> of type {@link eu.domibus.configuration.Party}
	 * 
	 * @param fileName Xml file to serialize the object <var>party</var>
	 * @param party Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Party party) throws SerializerException {
		this.objToXml(fileName, Party.class, party, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>party</var> of type {@link eu.domibus.configuration.Party}
	 * 
	 * @param fileName Xml file to serialize the object <var>party</var>
	 * @param party Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Party party,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Party.class, party, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>party</var> of type {@link eu.domibus.configuration.Party}
	 * 
	 * @param file Xml file to serialize the object <var>party</var>
	 * @param party Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Party party) throws SerializerException {
		this.objToXml(file, Party.class, party, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>party</var> of type {@link eu.domibus.configuration.Party}
	 * 
	 * @param file Xml file to serialize the object <var>party</var>
	 * @param party Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Party party,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Party.class, party, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>party</var> of type {@link eu.domibus.configuration.Party}
	 * 
	 * @param out OutputStream to serialize the object <var>party</var>
	 * @param party Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Party party) throws SerializerException {
		this.objToXml(out, Party.class, party, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>party</var> of type {@link eu.domibus.configuration.Party}
	 * 
	 * @param out OutputStream to serialize the object <var>party</var>
	 * @param party Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Party party,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Party.class, party, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>party</var> of type {@link eu.domibus.configuration.Party}
	 * 
	 * @param party Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Party party) throws SerializerException {
		return this.objToXml(Party.class, party, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>party</var> of type {@link eu.domibus.configuration.Party}
	 * 
	 * @param party Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Party party,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Party.class, party, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>party</var> of type {@link eu.domibus.configuration.Party}
	 * 
	 * @param party Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Party party) throws SerializerException {
		return this.objToXml(Party.class, party, false).toString();
	}
	/**
	 * Serialize to String the object <var>party</var> of type {@link eu.domibus.configuration.Party}
	 * 
	 * @param party Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Party party,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Party.class, party, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: properties
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>properties</var> of type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param fileName Xml file to serialize the object <var>properties</var>
	 * @param properties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Properties properties) throws SerializerException {
		this.objToXml(fileName, Properties.class, properties, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>properties</var> of type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param fileName Xml file to serialize the object <var>properties</var>
	 * @param properties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Properties properties,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Properties.class, properties, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>properties</var> of type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param file Xml file to serialize the object <var>properties</var>
	 * @param properties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Properties properties) throws SerializerException {
		this.objToXml(file, Properties.class, properties, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>properties</var> of type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param file Xml file to serialize the object <var>properties</var>
	 * @param properties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Properties properties,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Properties.class, properties, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>properties</var> of type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param out OutputStream to serialize the object <var>properties</var>
	 * @param properties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Properties properties) throws SerializerException {
		this.objToXml(out, Properties.class, properties, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>properties</var> of type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param out OutputStream to serialize the object <var>properties</var>
	 * @param properties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Properties properties,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Properties.class, properties, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>properties</var> of type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param properties Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Properties properties) throws SerializerException {
		return this.objToXml(Properties.class, properties, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>properties</var> of type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param properties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Properties properties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Properties.class, properties, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>properties</var> of type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param properties Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Properties properties) throws SerializerException {
		return this.objToXml(Properties.class, properties, false).toString();
	}
	/**
	 * Serialize to String the object <var>properties</var> of type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param properties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Properties properties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Properties.class, properties, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: propertySet
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>propertySet</var> of type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param fileName Xml file to serialize the object <var>propertySet</var>
	 * @param propertySet Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PropertySet propertySet) throws SerializerException {
		this.objToXml(fileName, PropertySet.class, propertySet, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>propertySet</var> of type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param fileName Xml file to serialize the object <var>propertySet</var>
	 * @param propertySet Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PropertySet propertySet,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PropertySet.class, propertySet, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>propertySet</var> of type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param file Xml file to serialize the object <var>propertySet</var>
	 * @param propertySet Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PropertySet propertySet) throws SerializerException {
		this.objToXml(file, PropertySet.class, propertySet, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>propertySet</var> of type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param file Xml file to serialize the object <var>propertySet</var>
	 * @param propertySet Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PropertySet propertySet,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PropertySet.class, propertySet, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>propertySet</var> of type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param out OutputStream to serialize the object <var>propertySet</var>
	 * @param propertySet Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PropertySet propertySet) throws SerializerException {
		this.objToXml(out, PropertySet.class, propertySet, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>propertySet</var> of type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param out OutputStream to serialize the object <var>propertySet</var>
	 * @param propertySet Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PropertySet propertySet,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PropertySet.class, propertySet, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>propertySet</var> of type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param propertySet Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PropertySet propertySet) throws SerializerException {
		return this.objToXml(PropertySet.class, propertySet, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>propertySet</var> of type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param propertySet Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PropertySet propertySet,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PropertySet.class, propertySet, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>propertySet</var> of type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param propertySet Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PropertySet propertySet) throws SerializerException {
		return this.objToXml(PropertySet.class, propertySet, false).toString();
	}
	/**
	 * Serialize to String the object <var>propertySet</var> of type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param propertySet Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PropertySet propertySet,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PropertySet.class, propertySet, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: PropertyValueHeader
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>propertyValueHeader</var> of type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param fileName Xml file to serialize the object <var>propertyValueHeader</var>
	 * @param propertyValueHeader Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PropertyValueHeader propertyValueHeader) throws SerializerException {
		this.objToXml(fileName, PropertyValueHeader.class, propertyValueHeader, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>propertyValueHeader</var> of type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param fileName Xml file to serialize the object <var>propertyValueHeader</var>
	 * @param propertyValueHeader Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PropertyValueHeader propertyValueHeader,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PropertyValueHeader.class, propertyValueHeader, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>propertyValueHeader</var> of type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param file Xml file to serialize the object <var>propertyValueHeader</var>
	 * @param propertyValueHeader Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PropertyValueHeader propertyValueHeader) throws SerializerException {
		this.objToXml(file, PropertyValueHeader.class, propertyValueHeader, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>propertyValueHeader</var> of type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param file Xml file to serialize the object <var>propertyValueHeader</var>
	 * @param propertyValueHeader Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PropertyValueHeader propertyValueHeader,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PropertyValueHeader.class, propertyValueHeader, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>propertyValueHeader</var> of type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param out OutputStream to serialize the object <var>propertyValueHeader</var>
	 * @param propertyValueHeader Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PropertyValueHeader propertyValueHeader) throws SerializerException {
		this.objToXml(out, PropertyValueHeader.class, propertyValueHeader, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>propertyValueHeader</var> of type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param out OutputStream to serialize the object <var>propertyValueHeader</var>
	 * @param propertyValueHeader Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PropertyValueHeader propertyValueHeader,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PropertyValueHeader.class, propertyValueHeader, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>propertyValueHeader</var> of type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param propertyValueHeader Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PropertyValueHeader propertyValueHeader) throws SerializerException {
		return this.objToXml(PropertyValueHeader.class, propertyValueHeader, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>propertyValueHeader</var> of type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param propertyValueHeader Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PropertyValueHeader propertyValueHeader,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PropertyValueHeader.class, propertyValueHeader, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>propertyValueHeader</var> of type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param propertyValueHeader Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PropertyValueHeader propertyValueHeader) throws SerializerException {
		return this.objToXml(PropertyValueHeader.class, propertyValueHeader, false).toString();
	}
	/**
	 * Serialize to String the object <var>propertyValueHeader</var> of type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param propertyValueHeader Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PropertyValueHeader propertyValueHeader,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PropertyValueHeader.class, propertyValueHeader, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: receptionAwareness
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>receptionAwareness</var> of type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param fileName Xml file to serialize the object <var>receptionAwareness</var>
	 * @param receptionAwareness Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ReceptionAwareness receptionAwareness) throws SerializerException {
		this.objToXml(fileName, ReceptionAwareness.class, receptionAwareness, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>receptionAwareness</var> of type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param fileName Xml file to serialize the object <var>receptionAwareness</var>
	 * @param receptionAwareness Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ReceptionAwareness receptionAwareness,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ReceptionAwareness.class, receptionAwareness, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>receptionAwareness</var> of type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param file Xml file to serialize the object <var>receptionAwareness</var>
	 * @param receptionAwareness Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ReceptionAwareness receptionAwareness) throws SerializerException {
		this.objToXml(file, ReceptionAwareness.class, receptionAwareness, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>receptionAwareness</var> of type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param file Xml file to serialize the object <var>receptionAwareness</var>
	 * @param receptionAwareness Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ReceptionAwareness receptionAwareness,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ReceptionAwareness.class, receptionAwareness, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>receptionAwareness</var> of type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param out OutputStream to serialize the object <var>receptionAwareness</var>
	 * @param receptionAwareness Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ReceptionAwareness receptionAwareness) throws SerializerException {
		this.objToXml(out, ReceptionAwareness.class, receptionAwareness, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>receptionAwareness</var> of type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param out OutputStream to serialize the object <var>receptionAwareness</var>
	 * @param receptionAwareness Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ReceptionAwareness receptionAwareness,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ReceptionAwareness.class, receptionAwareness, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>receptionAwareness</var> of type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param receptionAwareness Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ReceptionAwareness receptionAwareness) throws SerializerException {
		return this.objToXml(ReceptionAwareness.class, receptionAwareness, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>receptionAwareness</var> of type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param receptionAwareness Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ReceptionAwareness receptionAwareness,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ReceptionAwareness.class, receptionAwareness, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>receptionAwareness</var> of type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param receptionAwareness Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ReceptionAwareness receptionAwareness) throws SerializerException {
		return this.objToXml(ReceptionAwareness.class, receptionAwareness, false).toString();
	}
	/**
	 * Serialize to String the object <var>receptionAwareness</var> of type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param receptionAwareness Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ReceptionAwareness receptionAwareness,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ReceptionAwareness.class, receptionAwareness, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: as4
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>as4</var> of type {@link eu.domibus.configuration.As4}
	 * 
	 * @param fileName Xml file to serialize the object <var>as4</var>
	 * @param as4 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,As4 as4) throws SerializerException {
		this.objToXml(fileName, As4.class, as4, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>as4</var> of type {@link eu.domibus.configuration.As4}
	 * 
	 * @param fileName Xml file to serialize the object <var>as4</var>
	 * @param as4 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,As4 as4,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, As4.class, as4, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>as4</var> of type {@link eu.domibus.configuration.As4}
	 * 
	 * @param file Xml file to serialize the object <var>as4</var>
	 * @param as4 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,As4 as4) throws SerializerException {
		this.objToXml(file, As4.class, as4, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>as4</var> of type {@link eu.domibus.configuration.As4}
	 * 
	 * @param file Xml file to serialize the object <var>as4</var>
	 * @param as4 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,As4 as4,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, As4.class, as4, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>as4</var> of type {@link eu.domibus.configuration.As4}
	 * 
	 * @param out OutputStream to serialize the object <var>as4</var>
	 * @param as4 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,As4 as4) throws SerializerException {
		this.objToXml(out, As4.class, as4, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>as4</var> of type {@link eu.domibus.configuration.As4}
	 * 
	 * @param out OutputStream to serialize the object <var>as4</var>
	 * @param as4 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,As4 as4,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, As4.class, as4, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>as4</var> of type {@link eu.domibus.configuration.As4}
	 * 
	 * @param as4 Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(As4 as4) throws SerializerException {
		return this.objToXml(As4.class, as4, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>as4</var> of type {@link eu.domibus.configuration.As4}
	 * 
	 * @param as4 Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(As4 as4,boolean prettyPrint) throws SerializerException {
		return this.objToXml(As4.class, as4, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>as4</var> of type {@link eu.domibus.configuration.As4}
	 * 
	 * @param as4 Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(As4 as4) throws SerializerException {
		return this.objToXml(As4.class, as4, false).toString();
	}
	/**
	 * Serialize to String the object <var>as4</var> of type {@link eu.domibus.configuration.As4}
	 * 
	 * @param as4 Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(As4 as4,boolean prettyPrint) throws SerializerException {
		return this.objToXml(As4.class, as4, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: PropertyValueUrl
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>propertyValueUrl</var> of type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param fileName Xml file to serialize the object <var>propertyValueUrl</var>
	 * @param propertyValueUrl Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PropertyValueUrl propertyValueUrl) throws SerializerException {
		this.objToXml(fileName, PropertyValueUrl.class, propertyValueUrl, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>propertyValueUrl</var> of type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param fileName Xml file to serialize the object <var>propertyValueUrl</var>
	 * @param propertyValueUrl Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PropertyValueUrl propertyValueUrl,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PropertyValueUrl.class, propertyValueUrl, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>propertyValueUrl</var> of type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param file Xml file to serialize the object <var>propertyValueUrl</var>
	 * @param propertyValueUrl Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PropertyValueUrl propertyValueUrl) throws SerializerException {
		this.objToXml(file, PropertyValueUrl.class, propertyValueUrl, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>propertyValueUrl</var> of type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param file Xml file to serialize the object <var>propertyValueUrl</var>
	 * @param propertyValueUrl Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PropertyValueUrl propertyValueUrl,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PropertyValueUrl.class, propertyValueUrl, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>propertyValueUrl</var> of type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param out OutputStream to serialize the object <var>propertyValueUrl</var>
	 * @param propertyValueUrl Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PropertyValueUrl propertyValueUrl) throws SerializerException {
		this.objToXml(out, PropertyValueUrl.class, propertyValueUrl, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>propertyValueUrl</var> of type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param out OutputStream to serialize the object <var>propertyValueUrl</var>
	 * @param propertyValueUrl Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PropertyValueUrl propertyValueUrl,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PropertyValueUrl.class, propertyValueUrl, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>propertyValueUrl</var> of type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param propertyValueUrl Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PropertyValueUrl propertyValueUrl) throws SerializerException {
		return this.objToXml(PropertyValueUrl.class, propertyValueUrl, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>propertyValueUrl</var> of type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param propertyValueUrl Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PropertyValueUrl propertyValueUrl,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PropertyValueUrl.class, propertyValueUrl, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>propertyValueUrl</var> of type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param propertyValueUrl Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PropertyValueUrl propertyValueUrl) throws SerializerException {
		return this.objToXml(PropertyValueUrl.class, propertyValueUrl, false).toString();
	}
	/**
	 * Serialize to String the object <var>propertyValueUrl</var> of type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param propertyValueUrl Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PropertyValueUrl propertyValueUrl,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PropertyValueUrl.class, propertyValueUrl, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: role
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>role</var> of type {@link eu.domibus.configuration.Role}
	 * 
	 * @param fileName Xml file to serialize the object <var>role</var>
	 * @param role Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Role role) throws SerializerException {
		this.objToXml(fileName, Role.class, role, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>role</var> of type {@link eu.domibus.configuration.Role}
	 * 
	 * @param fileName Xml file to serialize the object <var>role</var>
	 * @param role Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Role role,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Role.class, role, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>role</var> of type {@link eu.domibus.configuration.Role}
	 * 
	 * @param file Xml file to serialize the object <var>role</var>
	 * @param role Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Role role) throws SerializerException {
		this.objToXml(file, Role.class, role, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>role</var> of type {@link eu.domibus.configuration.Role}
	 * 
	 * @param file Xml file to serialize the object <var>role</var>
	 * @param role Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Role role,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Role.class, role, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>role</var> of type {@link eu.domibus.configuration.Role}
	 * 
	 * @param out OutputStream to serialize the object <var>role</var>
	 * @param role Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Role role) throws SerializerException {
		this.objToXml(out, Role.class, role, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>role</var> of type {@link eu.domibus.configuration.Role}
	 * 
	 * @param out OutputStream to serialize the object <var>role</var>
	 * @param role Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Role role,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Role.class, role, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>role</var> of type {@link eu.domibus.configuration.Role}
	 * 
	 * @param role Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Role role) throws SerializerException {
		return this.objToXml(Role.class, role, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>role</var> of type {@link eu.domibus.configuration.Role}
	 * 
	 * @param role Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Role role,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Role.class, role, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>role</var> of type {@link eu.domibus.configuration.Role}
	 * 
	 * @param role Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Role role) throws SerializerException {
		return this.objToXml(Role.class, role, false).toString();
	}
	/**
	 * Serialize to String the object <var>role</var> of type {@link eu.domibus.configuration.Role}
	 * 
	 * @param role Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Role role,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Role.class, role, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: roles
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>roles</var> of type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param fileName Xml file to serialize the object <var>roles</var>
	 * @param roles Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Roles roles) throws SerializerException {
		this.objToXml(fileName, Roles.class, roles, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>roles</var> of type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param fileName Xml file to serialize the object <var>roles</var>
	 * @param roles Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Roles roles,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Roles.class, roles, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>roles</var> of type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param file Xml file to serialize the object <var>roles</var>
	 * @param roles Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Roles roles) throws SerializerException {
		this.objToXml(file, Roles.class, roles, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>roles</var> of type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param file Xml file to serialize the object <var>roles</var>
	 * @param roles Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Roles roles,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Roles.class, roles, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>roles</var> of type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param out OutputStream to serialize the object <var>roles</var>
	 * @param roles Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Roles roles) throws SerializerException {
		this.objToXml(out, Roles.class, roles, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>roles</var> of type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param out OutputStream to serialize the object <var>roles</var>
	 * @param roles Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Roles roles,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Roles.class, roles, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>roles</var> of type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param roles Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Roles roles) throws SerializerException {
		return this.objToXml(Roles.class, roles, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>roles</var> of type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param roles Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Roles roles,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Roles.class, roles, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>roles</var> of type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param roles Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Roles roles) throws SerializerException {
		return this.objToXml(Roles.class, roles, false).toString();
	}
	/**
	 * Serialize to String the object <var>roles</var> of type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param roles Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Roles roles,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Roles.class, roles, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configuration
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configuration</var> of type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param fileName Xml file to serialize the object <var>configuration</var>
	 * @param configuration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Configuration configuration) throws SerializerException {
		this.objToXml(fileName, Configuration.class, configuration, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configuration</var> of type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param fileName Xml file to serialize the object <var>configuration</var>
	 * @param configuration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Configuration configuration,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Configuration.class, configuration, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configuration</var> of type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param file Xml file to serialize the object <var>configuration</var>
	 * @param configuration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Configuration configuration) throws SerializerException {
		this.objToXml(file, Configuration.class, configuration, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configuration</var> of type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param file Xml file to serialize the object <var>configuration</var>
	 * @param configuration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Configuration configuration,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Configuration.class, configuration, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configuration</var> of type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param out OutputStream to serialize the object <var>configuration</var>
	 * @param configuration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Configuration configuration) throws SerializerException {
		this.objToXml(out, Configuration.class, configuration, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configuration</var> of type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param out OutputStream to serialize the object <var>configuration</var>
	 * @param configuration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Configuration configuration,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Configuration.class, configuration, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configuration</var> of type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param configuration Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Configuration configuration) throws SerializerException {
		return this.objToXml(Configuration.class, configuration, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configuration</var> of type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param configuration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Configuration configuration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Configuration.class, configuration, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configuration</var> of type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param configuration Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Configuration configuration) throws SerializerException {
		return this.objToXml(Configuration.class, configuration, false).toString();
	}
	/**
	 * Serialize to String the object <var>configuration</var> of type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param configuration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Configuration configuration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Configuration.class, configuration, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: businessProcesses
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>businessProcesses</var> of type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param fileName Xml file to serialize the object <var>businessProcesses</var>
	 * @param businessProcesses Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,BusinessProcesses businessProcesses) throws SerializerException {
		this.objToXml(fileName, BusinessProcesses.class, businessProcesses, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>businessProcesses</var> of type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param fileName Xml file to serialize the object <var>businessProcesses</var>
	 * @param businessProcesses Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,BusinessProcesses businessProcesses,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, BusinessProcesses.class, businessProcesses, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>businessProcesses</var> of type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param file Xml file to serialize the object <var>businessProcesses</var>
	 * @param businessProcesses Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,BusinessProcesses businessProcesses) throws SerializerException {
		this.objToXml(file, BusinessProcesses.class, businessProcesses, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>businessProcesses</var> of type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param file Xml file to serialize the object <var>businessProcesses</var>
	 * @param businessProcesses Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,BusinessProcesses businessProcesses,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, BusinessProcesses.class, businessProcesses, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>businessProcesses</var> of type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param out OutputStream to serialize the object <var>businessProcesses</var>
	 * @param businessProcesses Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,BusinessProcesses businessProcesses) throws SerializerException {
		this.objToXml(out, BusinessProcesses.class, businessProcesses, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>businessProcesses</var> of type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param out OutputStream to serialize the object <var>businessProcesses</var>
	 * @param businessProcesses Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,BusinessProcesses businessProcesses,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, BusinessProcesses.class, businessProcesses, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>businessProcesses</var> of type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param businessProcesses Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(BusinessProcesses businessProcesses) throws SerializerException {
		return this.objToXml(BusinessProcesses.class, businessProcesses, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>businessProcesses</var> of type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param businessProcesses Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(BusinessProcesses businessProcesses,boolean prettyPrint) throws SerializerException {
		return this.objToXml(BusinessProcesses.class, businessProcesses, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>businessProcesses</var> of type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param businessProcesses Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(BusinessProcesses businessProcesses) throws SerializerException {
		return this.objToXml(BusinessProcesses.class, businessProcesses, false).toString();
	}
	/**
	 * Serialize to String the object <var>businessProcesses</var> of type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param businessProcesses Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(BusinessProcesses businessProcesses,boolean prettyPrint) throws SerializerException {
		return this.objToXml(BusinessProcesses.class, businessProcesses, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: securities
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>securities</var> of type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param fileName Xml file to serialize the object <var>securities</var>
	 * @param securities Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Securities securities) throws SerializerException {
		this.objToXml(fileName, Securities.class, securities, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>securities</var> of type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param fileName Xml file to serialize the object <var>securities</var>
	 * @param securities Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Securities securities,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Securities.class, securities, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>securities</var> of type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param file Xml file to serialize the object <var>securities</var>
	 * @param securities Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Securities securities) throws SerializerException {
		this.objToXml(file, Securities.class, securities, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>securities</var> of type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param file Xml file to serialize the object <var>securities</var>
	 * @param securities Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Securities securities,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Securities.class, securities, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>securities</var> of type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param out OutputStream to serialize the object <var>securities</var>
	 * @param securities Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Securities securities) throws SerializerException {
		this.objToXml(out, Securities.class, securities, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>securities</var> of type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param out OutputStream to serialize the object <var>securities</var>
	 * @param securities Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Securities securities,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Securities.class, securities, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>securities</var> of type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param securities Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Securities securities) throws SerializerException {
		return this.objToXml(Securities.class, securities, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>securities</var> of type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param securities Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Securities securities,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Securities.class, securities, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>securities</var> of type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param securities Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Securities securities) throws SerializerException {
		return this.objToXml(Securities.class, securities, false).toString();
	}
	/**
	 * Serialize to String the object <var>securities</var> of type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param securities Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Securities securities,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Securities.class, securities, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: errorHandlings
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>errorHandlings</var> of type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param fileName Xml file to serialize the object <var>errorHandlings</var>
	 * @param errorHandlings Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ErrorHandlings errorHandlings) throws SerializerException {
		this.objToXml(fileName, ErrorHandlings.class, errorHandlings, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>errorHandlings</var> of type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param fileName Xml file to serialize the object <var>errorHandlings</var>
	 * @param errorHandlings Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ErrorHandlings errorHandlings,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ErrorHandlings.class, errorHandlings, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>errorHandlings</var> of type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param file Xml file to serialize the object <var>errorHandlings</var>
	 * @param errorHandlings Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ErrorHandlings errorHandlings) throws SerializerException {
		this.objToXml(file, ErrorHandlings.class, errorHandlings, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>errorHandlings</var> of type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param file Xml file to serialize the object <var>errorHandlings</var>
	 * @param errorHandlings Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ErrorHandlings errorHandlings,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ErrorHandlings.class, errorHandlings, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>errorHandlings</var> of type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param out OutputStream to serialize the object <var>errorHandlings</var>
	 * @param errorHandlings Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ErrorHandlings errorHandlings) throws SerializerException {
		this.objToXml(out, ErrorHandlings.class, errorHandlings, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>errorHandlings</var> of type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param out OutputStream to serialize the object <var>errorHandlings</var>
	 * @param errorHandlings Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ErrorHandlings errorHandlings,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ErrorHandlings.class, errorHandlings, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>errorHandlings</var> of type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param errorHandlings Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ErrorHandlings errorHandlings) throws SerializerException {
		return this.objToXml(ErrorHandlings.class, errorHandlings, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>errorHandlings</var> of type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param errorHandlings Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ErrorHandlings errorHandlings,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ErrorHandlings.class, errorHandlings, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>errorHandlings</var> of type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param errorHandlings Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ErrorHandlings errorHandlings) throws SerializerException {
		return this.objToXml(ErrorHandlings.class, errorHandlings, false).toString();
	}
	/**
	 * Serialize to String the object <var>errorHandlings</var> of type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param errorHandlings Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ErrorHandlings errorHandlings,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ErrorHandlings.class, errorHandlings, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: agreements
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>agreements</var> of type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param fileName Xml file to serialize the object <var>agreements</var>
	 * @param agreements Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Agreements agreements) throws SerializerException {
		this.objToXml(fileName, Agreements.class, agreements, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>agreements</var> of type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param fileName Xml file to serialize the object <var>agreements</var>
	 * @param agreements Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Agreements agreements,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Agreements.class, agreements, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>agreements</var> of type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param file Xml file to serialize the object <var>agreements</var>
	 * @param agreements Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Agreements agreements) throws SerializerException {
		this.objToXml(file, Agreements.class, agreements, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>agreements</var> of type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param file Xml file to serialize the object <var>agreements</var>
	 * @param agreements Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Agreements agreements,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Agreements.class, agreements, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>agreements</var> of type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param out OutputStream to serialize the object <var>agreements</var>
	 * @param agreements Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Agreements agreements) throws SerializerException {
		this.objToXml(out, Agreements.class, agreements, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>agreements</var> of type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param out OutputStream to serialize the object <var>agreements</var>
	 * @param agreements Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Agreements agreements,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Agreements.class, agreements, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>agreements</var> of type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param agreements Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Agreements agreements) throws SerializerException {
		return this.objToXml(Agreements.class, agreements, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>agreements</var> of type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param agreements Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Agreements agreements,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Agreements.class, agreements, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>agreements</var> of type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param agreements Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Agreements agreements) throws SerializerException {
		return this.objToXml(Agreements.class, agreements, false).toString();
	}
	/**
	 * Serialize to String the object <var>agreements</var> of type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param agreements Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Agreements agreements,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Agreements.class, agreements, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: services
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>services</var> of type {@link eu.domibus.configuration.Services}
	 * 
	 * @param fileName Xml file to serialize the object <var>services</var>
	 * @param services Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Services services) throws SerializerException {
		this.objToXml(fileName, Services.class, services, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>services</var> of type {@link eu.domibus.configuration.Services}
	 * 
	 * @param fileName Xml file to serialize the object <var>services</var>
	 * @param services Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Services services,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Services.class, services, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>services</var> of type {@link eu.domibus.configuration.Services}
	 * 
	 * @param file Xml file to serialize the object <var>services</var>
	 * @param services Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Services services) throws SerializerException {
		this.objToXml(file, Services.class, services, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>services</var> of type {@link eu.domibus.configuration.Services}
	 * 
	 * @param file Xml file to serialize the object <var>services</var>
	 * @param services Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Services services,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Services.class, services, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>services</var> of type {@link eu.domibus.configuration.Services}
	 * 
	 * @param out OutputStream to serialize the object <var>services</var>
	 * @param services Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Services services) throws SerializerException {
		this.objToXml(out, Services.class, services, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>services</var> of type {@link eu.domibus.configuration.Services}
	 * 
	 * @param out OutputStream to serialize the object <var>services</var>
	 * @param services Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Services services,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Services.class, services, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>services</var> of type {@link eu.domibus.configuration.Services}
	 * 
	 * @param services Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Services services) throws SerializerException {
		return this.objToXml(Services.class, services, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>services</var> of type {@link eu.domibus.configuration.Services}
	 * 
	 * @param services Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Services services,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Services.class, services, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>services</var> of type {@link eu.domibus.configuration.Services}
	 * 
	 * @param services Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Services services) throws SerializerException {
		return this.objToXml(Services.class, services, false).toString();
	}
	/**
	 * Serialize to String the object <var>services</var> of type {@link eu.domibus.configuration.Services}
	 * 
	 * @param services Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Services services,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Services.class, services, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: actions
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>actions</var> of type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param fileName Xml file to serialize the object <var>actions</var>
	 * @param actions Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Actions actions) throws SerializerException {
		this.objToXml(fileName, Actions.class, actions, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>actions</var> of type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param fileName Xml file to serialize the object <var>actions</var>
	 * @param actions Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Actions actions,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Actions.class, actions, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>actions</var> of type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param file Xml file to serialize the object <var>actions</var>
	 * @param actions Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Actions actions) throws SerializerException {
		this.objToXml(file, Actions.class, actions, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>actions</var> of type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param file Xml file to serialize the object <var>actions</var>
	 * @param actions Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Actions actions,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Actions.class, actions, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>actions</var> of type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param out OutputStream to serialize the object <var>actions</var>
	 * @param actions Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Actions actions) throws SerializerException {
		this.objToXml(out, Actions.class, actions, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>actions</var> of type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param out OutputStream to serialize the object <var>actions</var>
	 * @param actions Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Actions actions,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Actions.class, actions, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>actions</var> of type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param actions Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Actions actions) throws SerializerException {
		return this.objToXml(Actions.class, actions, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>actions</var> of type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param actions Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Actions actions,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Actions.class, actions, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>actions</var> of type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param actions Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Actions actions) throws SerializerException {
		return this.objToXml(Actions.class, actions, false).toString();
	}
	/**
	 * Serialize to String the object <var>actions</var> of type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param actions Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Actions actions,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Actions.class, actions, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: process
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>process</var> of type {@link eu.domibus.configuration.Process}
	 * 
	 * @param fileName Xml file to serialize the object <var>process</var>
	 * @param process Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Process process) throws SerializerException {
		this.objToXml(fileName, Process.class, process, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>process</var> of type {@link eu.domibus.configuration.Process}
	 * 
	 * @param fileName Xml file to serialize the object <var>process</var>
	 * @param process Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Process process,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Process.class, process, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>process</var> of type {@link eu.domibus.configuration.Process}
	 * 
	 * @param file Xml file to serialize the object <var>process</var>
	 * @param process Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Process process) throws SerializerException {
		this.objToXml(file, Process.class, process, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>process</var> of type {@link eu.domibus.configuration.Process}
	 * 
	 * @param file Xml file to serialize the object <var>process</var>
	 * @param process Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Process process,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Process.class, process, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>process</var> of type {@link eu.domibus.configuration.Process}
	 * 
	 * @param out OutputStream to serialize the object <var>process</var>
	 * @param process Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Process process) throws SerializerException {
		this.objToXml(out, Process.class, process, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>process</var> of type {@link eu.domibus.configuration.Process}
	 * 
	 * @param out OutputStream to serialize the object <var>process</var>
	 * @param process Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Process process,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Process.class, process, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>process</var> of type {@link eu.domibus.configuration.Process}
	 * 
	 * @param process Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Process process) throws SerializerException {
		return this.objToXml(Process.class, process, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>process</var> of type {@link eu.domibus.configuration.Process}
	 * 
	 * @param process Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Process process,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Process.class, process, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>process</var> of type {@link eu.domibus.configuration.Process}
	 * 
	 * @param process Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Process process) throws SerializerException {
		return this.objToXml(Process.class, process, false).toString();
	}
	/**
	 * Serialize to String the object <var>process</var> of type {@link eu.domibus.configuration.Process}
	 * 
	 * @param process Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Process process,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Process.class, process, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: agreement
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>agreement</var> of type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param fileName Xml file to serialize the object <var>agreement</var>
	 * @param agreement Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Agreement agreement) throws SerializerException {
		this.objToXml(fileName, Agreement.class, agreement, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>agreement</var> of type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param fileName Xml file to serialize the object <var>agreement</var>
	 * @param agreement Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Agreement agreement,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Agreement.class, agreement, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>agreement</var> of type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param file Xml file to serialize the object <var>agreement</var>
	 * @param agreement Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Agreement agreement) throws SerializerException {
		this.objToXml(file, Agreement.class, agreement, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>agreement</var> of type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param file Xml file to serialize the object <var>agreement</var>
	 * @param agreement Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Agreement agreement,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Agreement.class, agreement, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>agreement</var> of type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param out OutputStream to serialize the object <var>agreement</var>
	 * @param agreement Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Agreement agreement) throws SerializerException {
		this.objToXml(out, Agreement.class, agreement, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>agreement</var> of type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param out OutputStream to serialize the object <var>agreement</var>
	 * @param agreement Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Agreement agreement,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Agreement.class, agreement, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>agreement</var> of type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param agreement Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Agreement agreement) throws SerializerException {
		return this.objToXml(Agreement.class, agreement, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>agreement</var> of type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param agreement Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Agreement agreement,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Agreement.class, agreement, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>agreement</var> of type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param agreement Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Agreement agreement) throws SerializerException {
		return this.objToXml(Agreement.class, agreement, false).toString();
	}
	/**
	 * Serialize to String the object <var>agreement</var> of type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param agreement Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Agreement agreement,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Agreement.class, agreement, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: initiatorParty
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>initiatorParty</var> of type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param fileName Xml file to serialize the object <var>initiatorParty</var>
	 * @param initiatorParty Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InitiatorParty initiatorParty) throws SerializerException {
		this.objToXml(fileName, InitiatorParty.class, initiatorParty, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>initiatorParty</var> of type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param fileName Xml file to serialize the object <var>initiatorParty</var>
	 * @param initiatorParty Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InitiatorParty initiatorParty,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, InitiatorParty.class, initiatorParty, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>initiatorParty</var> of type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param file Xml file to serialize the object <var>initiatorParty</var>
	 * @param initiatorParty Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InitiatorParty initiatorParty) throws SerializerException {
		this.objToXml(file, InitiatorParty.class, initiatorParty, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>initiatorParty</var> of type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param file Xml file to serialize the object <var>initiatorParty</var>
	 * @param initiatorParty Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InitiatorParty initiatorParty,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, InitiatorParty.class, initiatorParty, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>initiatorParty</var> of type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param out OutputStream to serialize the object <var>initiatorParty</var>
	 * @param initiatorParty Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InitiatorParty initiatorParty) throws SerializerException {
		this.objToXml(out, InitiatorParty.class, initiatorParty, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>initiatorParty</var> of type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param out OutputStream to serialize the object <var>initiatorParty</var>
	 * @param initiatorParty Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InitiatorParty initiatorParty,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, InitiatorParty.class, initiatorParty, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>initiatorParty</var> of type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param initiatorParty Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InitiatorParty initiatorParty) throws SerializerException {
		return this.objToXml(InitiatorParty.class, initiatorParty, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>initiatorParty</var> of type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param initiatorParty Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InitiatorParty initiatorParty,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InitiatorParty.class, initiatorParty, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>initiatorParty</var> of type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param initiatorParty Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InitiatorParty initiatorParty) throws SerializerException {
		return this.objToXml(InitiatorParty.class, initiatorParty, false).toString();
	}
	/**
	 * Serialize to String the object <var>initiatorParty</var> of type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param initiatorParty Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InitiatorParty initiatorParty,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InitiatorParty.class, initiatorParty, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: action
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>action</var> of type {@link eu.domibus.configuration.Action}
	 * 
	 * @param fileName Xml file to serialize the object <var>action</var>
	 * @param action Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Action action) throws SerializerException {
		this.objToXml(fileName, Action.class, action, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>action</var> of type {@link eu.domibus.configuration.Action}
	 * 
	 * @param fileName Xml file to serialize the object <var>action</var>
	 * @param action Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Action action,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Action.class, action, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>action</var> of type {@link eu.domibus.configuration.Action}
	 * 
	 * @param file Xml file to serialize the object <var>action</var>
	 * @param action Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Action action) throws SerializerException {
		this.objToXml(file, Action.class, action, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>action</var> of type {@link eu.domibus.configuration.Action}
	 * 
	 * @param file Xml file to serialize the object <var>action</var>
	 * @param action Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Action action,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Action.class, action, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>action</var> of type {@link eu.domibus.configuration.Action}
	 * 
	 * @param out OutputStream to serialize the object <var>action</var>
	 * @param action Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Action action) throws SerializerException {
		this.objToXml(out, Action.class, action, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>action</var> of type {@link eu.domibus.configuration.Action}
	 * 
	 * @param out OutputStream to serialize the object <var>action</var>
	 * @param action Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Action action,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Action.class, action, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>action</var> of type {@link eu.domibus.configuration.Action}
	 * 
	 * @param action Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Action action) throws SerializerException {
		return this.objToXml(Action.class, action, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>action</var> of type {@link eu.domibus.configuration.Action}
	 * 
	 * @param action Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Action action,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Action.class, action, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>action</var> of type {@link eu.domibus.configuration.Action}
	 * 
	 * @param action Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Action action) throws SerializerException {
		return this.objToXml(Action.class, action, false).toString();
	}
	/**
	 * Serialize to String the object <var>action</var> of type {@link eu.domibus.configuration.Action}
	 * 
	 * @param action Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Action action,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Action.class, action, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: identifier
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>identifier</var> of type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param fileName Xml file to serialize the object <var>identifier</var>
	 * @param identifier Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Identifier identifier) throws SerializerException {
		this.objToXml(fileName, Identifier.class, identifier, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>identifier</var> of type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param fileName Xml file to serialize the object <var>identifier</var>
	 * @param identifier Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Identifier identifier,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Identifier.class, identifier, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>identifier</var> of type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param file Xml file to serialize the object <var>identifier</var>
	 * @param identifier Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Identifier identifier) throws SerializerException {
		this.objToXml(file, Identifier.class, identifier, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>identifier</var> of type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param file Xml file to serialize the object <var>identifier</var>
	 * @param identifier Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Identifier identifier,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Identifier.class, identifier, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>identifier</var> of type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param out OutputStream to serialize the object <var>identifier</var>
	 * @param identifier Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Identifier identifier) throws SerializerException {
		this.objToXml(out, Identifier.class, identifier, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>identifier</var> of type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param out OutputStream to serialize the object <var>identifier</var>
	 * @param identifier Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Identifier identifier,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Identifier.class, identifier, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>identifier</var> of type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param identifier Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Identifier identifier) throws SerializerException {
		return this.objToXml(Identifier.class, identifier, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>identifier</var> of type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param identifier Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Identifier identifier,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Identifier.class, identifier, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>identifier</var> of type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param identifier Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Identifier identifier) throws SerializerException {
		return this.objToXml(Identifier.class, identifier, false).toString();
	}
	/**
	 * Serialize to String the object <var>identifier</var> of type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param identifier Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Identifier identifier,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Identifier.class, identifier, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: header
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>header</var> of type {@link eu.domibus.configuration.Header}
	 * 
	 * @param fileName Xml file to serialize the object <var>header</var>
	 * @param header Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Header header) throws SerializerException {
		this.objToXml(fileName, Header.class, header, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>header</var> of type {@link eu.domibus.configuration.Header}
	 * 
	 * @param fileName Xml file to serialize the object <var>header</var>
	 * @param header Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Header header,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Header.class, header, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>header</var> of type {@link eu.domibus.configuration.Header}
	 * 
	 * @param file Xml file to serialize the object <var>header</var>
	 * @param header Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Header header) throws SerializerException {
		this.objToXml(file, Header.class, header, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>header</var> of type {@link eu.domibus.configuration.Header}
	 * 
	 * @param file Xml file to serialize the object <var>header</var>
	 * @param header Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Header header,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Header.class, header, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>header</var> of type {@link eu.domibus.configuration.Header}
	 * 
	 * @param out OutputStream to serialize the object <var>header</var>
	 * @param header Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Header header) throws SerializerException {
		this.objToXml(out, Header.class, header, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>header</var> of type {@link eu.domibus.configuration.Header}
	 * 
	 * @param out OutputStream to serialize the object <var>header</var>
	 * @param header Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Header header,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Header.class, header, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>header</var> of type {@link eu.domibus.configuration.Header}
	 * 
	 * @param header Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Header header) throws SerializerException {
		return this.objToXml(Header.class, header, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>header</var> of type {@link eu.domibus.configuration.Header}
	 * 
	 * @param header Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Header header,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Header.class, header, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>header</var> of type {@link eu.domibus.configuration.Header}
	 * 
	 * @param header Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Header header) throws SerializerException {
		return this.objToXml(Header.class, header, false).toString();
	}
	/**
	 * Serialize to String the object <var>header</var> of type {@link eu.domibus.configuration.Header}
	 * 
	 * @param header Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Header header,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Header.class, header, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: errorHandling
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>errorHandling</var> of type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param fileName Xml file to serialize the object <var>errorHandling</var>
	 * @param errorHandling Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ErrorHandling errorHandling) throws SerializerException {
		this.objToXml(fileName, ErrorHandling.class, errorHandling, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>errorHandling</var> of type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param fileName Xml file to serialize the object <var>errorHandling</var>
	 * @param errorHandling Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ErrorHandling errorHandling,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ErrorHandling.class, errorHandling, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>errorHandling</var> of type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param file Xml file to serialize the object <var>errorHandling</var>
	 * @param errorHandling Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ErrorHandling errorHandling) throws SerializerException {
		this.objToXml(file, ErrorHandling.class, errorHandling, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>errorHandling</var> of type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param file Xml file to serialize the object <var>errorHandling</var>
	 * @param errorHandling Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ErrorHandling errorHandling,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ErrorHandling.class, errorHandling, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>errorHandling</var> of type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param out OutputStream to serialize the object <var>errorHandling</var>
	 * @param errorHandling Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ErrorHandling errorHandling) throws SerializerException {
		this.objToXml(out, ErrorHandling.class, errorHandling, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>errorHandling</var> of type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param out OutputStream to serialize the object <var>errorHandling</var>
	 * @param errorHandling Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ErrorHandling errorHandling,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ErrorHandling.class, errorHandling, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>errorHandling</var> of type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param errorHandling Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ErrorHandling errorHandling) throws SerializerException {
		return this.objToXml(ErrorHandling.class, errorHandling, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>errorHandling</var> of type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param errorHandling Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ErrorHandling errorHandling,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ErrorHandling.class, errorHandling, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>errorHandling</var> of type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param errorHandling Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ErrorHandling errorHandling) throws SerializerException {
		return this.objToXml(ErrorHandling.class, errorHandling, false).toString();
	}
	/**
	 * Serialize to String the object <var>errorHandling</var> of type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param errorHandling Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ErrorHandling errorHandling,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ErrorHandling.class, errorHandling, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: legs
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>legs</var> of type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param fileName Xml file to serialize the object <var>legs</var>
	 * @param legs Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Legs legs) throws SerializerException {
		this.objToXml(fileName, Legs.class, legs, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>legs</var> of type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param fileName Xml file to serialize the object <var>legs</var>
	 * @param legs Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Legs legs,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Legs.class, legs, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>legs</var> of type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param file Xml file to serialize the object <var>legs</var>
	 * @param legs Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Legs legs) throws SerializerException {
		this.objToXml(file, Legs.class, legs, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>legs</var> of type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param file Xml file to serialize the object <var>legs</var>
	 * @param legs Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Legs legs,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Legs.class, legs, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>legs</var> of type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param out OutputStream to serialize the object <var>legs</var>
	 * @param legs Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Legs legs) throws SerializerException {
		this.objToXml(out, Legs.class, legs, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>legs</var> of type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param out OutputStream to serialize the object <var>legs</var>
	 * @param legs Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Legs legs,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Legs.class, legs, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>legs</var> of type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param legs Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Legs legs) throws SerializerException {
		return this.objToXml(Legs.class, legs, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>legs</var> of type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param legs Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Legs legs,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Legs.class, legs, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>legs</var> of type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param legs Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Legs legs) throws SerializerException {
		return this.objToXml(Legs.class, legs, false).toString();
	}
	/**
	 * Serialize to String the object <var>legs</var> of type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param legs Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Legs legs,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Legs.class, legs, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: attachment
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>attachment</var> of type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param fileName Xml file to serialize the object <var>attachment</var>
	 * @param attachment Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Attachment attachment) throws SerializerException {
		this.objToXml(fileName, Attachment.class, attachment, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>attachment</var> of type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param fileName Xml file to serialize the object <var>attachment</var>
	 * @param attachment Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Attachment attachment,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Attachment.class, attachment, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>attachment</var> of type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param file Xml file to serialize the object <var>attachment</var>
	 * @param attachment Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Attachment attachment) throws SerializerException {
		this.objToXml(file, Attachment.class, attachment, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>attachment</var> of type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param file Xml file to serialize the object <var>attachment</var>
	 * @param attachment Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Attachment attachment,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Attachment.class, attachment, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>attachment</var> of type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param out OutputStream to serialize the object <var>attachment</var>
	 * @param attachment Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Attachment attachment) throws SerializerException {
		this.objToXml(out, Attachment.class, attachment, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>attachment</var> of type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param out OutputStream to serialize the object <var>attachment</var>
	 * @param attachment Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Attachment attachment,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Attachment.class, attachment, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>attachment</var> of type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param attachment Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Attachment attachment) throws SerializerException {
		return this.objToXml(Attachment.class, attachment, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>attachment</var> of type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param attachment Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Attachment attachment,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Attachment.class, attachment, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>attachment</var> of type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param attachment Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Attachment attachment) throws SerializerException {
		return this.objToXml(Attachment.class, attachment, false).toString();
	}
	/**
	 * Serialize to String the object <var>attachment</var> of type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param attachment Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Attachment attachment,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Attachment.class, attachment, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: url
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>url</var> of type {@link eu.domibus.configuration.Url}
	 * 
	 * @param fileName Xml file to serialize the object <var>url</var>
	 * @param url Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Url url) throws SerializerException {
		this.objToXml(fileName, Url.class, url, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>url</var> of type {@link eu.domibus.configuration.Url}
	 * 
	 * @param fileName Xml file to serialize the object <var>url</var>
	 * @param url Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Url url,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Url.class, url, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>url</var> of type {@link eu.domibus.configuration.Url}
	 * 
	 * @param file Xml file to serialize the object <var>url</var>
	 * @param url Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Url url) throws SerializerException {
		this.objToXml(file, Url.class, url, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>url</var> of type {@link eu.domibus.configuration.Url}
	 * 
	 * @param file Xml file to serialize the object <var>url</var>
	 * @param url Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Url url,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Url.class, url, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>url</var> of type {@link eu.domibus.configuration.Url}
	 * 
	 * @param out OutputStream to serialize the object <var>url</var>
	 * @param url Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Url url) throws SerializerException {
		this.objToXml(out, Url.class, url, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>url</var> of type {@link eu.domibus.configuration.Url}
	 * 
	 * @param out OutputStream to serialize the object <var>url</var>
	 * @param url Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Url url,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Url.class, url, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>url</var> of type {@link eu.domibus.configuration.Url}
	 * 
	 * @param url Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Url url) throws SerializerException {
		return this.objToXml(Url.class, url, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>url</var> of type {@link eu.domibus.configuration.Url}
	 * 
	 * @param url Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Url url,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Url.class, url, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>url</var> of type {@link eu.domibus.configuration.Url}
	 * 
	 * @param url Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Url url) throws SerializerException {
		return this.objToXml(Url.class, url, false).toString();
	}
	/**
	 * Serialize to String the object <var>url</var> of type {@link eu.domibus.configuration.Url}
	 * 
	 * @param url Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Url url,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Url.class, url, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: initiatorParties
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>initiatorParties</var> of type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param fileName Xml file to serialize the object <var>initiatorParties</var>
	 * @param initiatorParties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InitiatorParties initiatorParties) throws SerializerException {
		this.objToXml(fileName, InitiatorParties.class, initiatorParties, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>initiatorParties</var> of type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param fileName Xml file to serialize the object <var>initiatorParties</var>
	 * @param initiatorParties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InitiatorParties initiatorParties,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, InitiatorParties.class, initiatorParties, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>initiatorParties</var> of type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param file Xml file to serialize the object <var>initiatorParties</var>
	 * @param initiatorParties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InitiatorParties initiatorParties) throws SerializerException {
		this.objToXml(file, InitiatorParties.class, initiatorParties, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>initiatorParties</var> of type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param file Xml file to serialize the object <var>initiatorParties</var>
	 * @param initiatorParties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InitiatorParties initiatorParties,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, InitiatorParties.class, initiatorParties, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>initiatorParties</var> of type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param out OutputStream to serialize the object <var>initiatorParties</var>
	 * @param initiatorParties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InitiatorParties initiatorParties) throws SerializerException {
		this.objToXml(out, InitiatorParties.class, initiatorParties, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>initiatorParties</var> of type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param out OutputStream to serialize the object <var>initiatorParties</var>
	 * @param initiatorParties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InitiatorParties initiatorParties,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, InitiatorParties.class, initiatorParties, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>initiatorParties</var> of type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param initiatorParties Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InitiatorParties initiatorParties) throws SerializerException {
		return this.objToXml(InitiatorParties.class, initiatorParties, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>initiatorParties</var> of type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param initiatorParties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InitiatorParties initiatorParties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InitiatorParties.class, initiatorParties, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>initiatorParties</var> of type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param initiatorParties Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InitiatorParties initiatorParties) throws SerializerException {
		return this.objToXml(InitiatorParties.class, initiatorParties, false).toString();
	}
	/**
	 * Serialize to String the object <var>initiatorParties</var> of type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param initiatorParties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InitiatorParties initiatorParties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InitiatorParties.class, initiatorParties, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: responderParties
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responderParties</var> of type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param fileName Xml file to serialize the object <var>responderParties</var>
	 * @param responderParties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponderParties responderParties) throws SerializerException {
		this.objToXml(fileName, ResponderParties.class, responderParties, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responderParties</var> of type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param fileName Xml file to serialize the object <var>responderParties</var>
	 * @param responderParties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponderParties responderParties,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ResponderParties.class, responderParties, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>responderParties</var> of type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param file Xml file to serialize the object <var>responderParties</var>
	 * @param responderParties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponderParties responderParties) throws SerializerException {
		this.objToXml(file, ResponderParties.class, responderParties, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>responderParties</var> of type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param file Xml file to serialize the object <var>responderParties</var>
	 * @param responderParties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponderParties responderParties,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ResponderParties.class, responderParties, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>responderParties</var> of type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param out OutputStream to serialize the object <var>responderParties</var>
	 * @param responderParties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponderParties responderParties) throws SerializerException {
		this.objToXml(out, ResponderParties.class, responderParties, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>responderParties</var> of type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param out OutputStream to serialize the object <var>responderParties</var>
	 * @param responderParties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponderParties responderParties,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ResponderParties.class, responderParties, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>responderParties</var> of type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param responderParties Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponderParties responderParties) throws SerializerException {
		return this.objToXml(ResponderParties.class, responderParties, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>responderParties</var> of type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param responderParties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponderParties responderParties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponderParties.class, responderParties, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>responderParties</var> of type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param responderParties Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponderParties responderParties) throws SerializerException {
		return this.objToXml(ResponderParties.class, responderParties, false).toString();
	}
	/**
	 * Serialize to String the object <var>responderParties</var> of type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param responderParties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponderParties responderParties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponderParties.class, responderParties, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: responderParty
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responderParty</var> of type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param fileName Xml file to serialize the object <var>responderParty</var>
	 * @param responderParty Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponderParty responderParty) throws SerializerException {
		this.objToXml(fileName, ResponderParty.class, responderParty, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responderParty</var> of type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param fileName Xml file to serialize the object <var>responderParty</var>
	 * @param responderParty Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponderParty responderParty,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ResponderParty.class, responderParty, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>responderParty</var> of type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param file Xml file to serialize the object <var>responderParty</var>
	 * @param responderParty Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponderParty responderParty) throws SerializerException {
		this.objToXml(file, ResponderParty.class, responderParty, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>responderParty</var> of type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param file Xml file to serialize the object <var>responderParty</var>
	 * @param responderParty Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponderParty responderParty,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ResponderParty.class, responderParty, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>responderParty</var> of type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param out OutputStream to serialize the object <var>responderParty</var>
	 * @param responderParty Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponderParty responderParty) throws SerializerException {
		this.objToXml(out, ResponderParty.class, responderParty, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>responderParty</var> of type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param out OutputStream to serialize the object <var>responderParty</var>
	 * @param responderParty Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponderParty responderParty,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ResponderParty.class, responderParty, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>responderParty</var> of type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param responderParty Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponderParty responderParty) throws SerializerException {
		return this.objToXml(ResponderParty.class, responderParty, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>responderParty</var> of type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param responderParty Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponderParty responderParty,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponderParty.class, responderParty, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>responderParty</var> of type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param responderParty Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponderParty responderParty) throws SerializerException {
		return this.objToXml(ResponderParty.class, responderParty, false).toString();
	}
	/**
	 * Serialize to String the object <var>responderParty</var> of type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param responderParty Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponderParty responderParty,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponderParty.class, responderParty, prettyPrint).toString();
	}
	
	
	

}
