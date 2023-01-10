/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.openspcoop2.generic_project.exception.DeserializerException;

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

import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer extends org.openspcoop2.generic_project.serializer.AbstractDeserializerBase {



	/*
	 =================================================================================
	 Object: legConfiguration
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.LegConfiguration}
	 * @return Object type {@link eu.domibus.configuration.LegConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LegConfiguration readLegConfiguration(String fileName) throws DeserializerException {
		return (LegConfiguration) this.xmlToObj(fileName, LegConfiguration.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.LegConfiguration}
	 * @return Object type {@link eu.domibus.configuration.LegConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LegConfiguration readLegConfiguration(File file) throws DeserializerException {
		return (LegConfiguration) this.xmlToObj(file, LegConfiguration.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.LegConfiguration}
	 * @return Object type {@link eu.domibus.configuration.LegConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LegConfiguration readLegConfiguration(InputStream in) throws DeserializerException {
		return (LegConfiguration) this.xmlToObj(in, LegConfiguration.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.LegConfiguration}
	 * @return Object type {@link eu.domibus.configuration.LegConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LegConfiguration readLegConfiguration(byte[] in) throws DeserializerException {
		return (LegConfiguration) this.xmlToObj(in, LegConfiguration.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.LegConfiguration}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.LegConfiguration}
	 * @return Object type {@link eu.domibus.configuration.LegConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LegConfiguration readLegConfigurationFromString(String in) throws DeserializerException {
		return (LegConfiguration) this.xmlToObj(in.getBytes(), LegConfiguration.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: legConfigurations
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.LegConfigurations}
	 * @return Object type {@link eu.domibus.configuration.LegConfigurations}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LegConfigurations readLegConfigurations(String fileName) throws DeserializerException {
		return (LegConfigurations) this.xmlToObj(fileName, LegConfigurations.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.LegConfigurations}
	 * @return Object type {@link eu.domibus.configuration.LegConfigurations}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LegConfigurations readLegConfigurations(File file) throws DeserializerException {
		return (LegConfigurations) this.xmlToObj(file, LegConfigurations.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.LegConfigurations}
	 * @return Object type {@link eu.domibus.configuration.LegConfigurations}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LegConfigurations readLegConfigurations(InputStream in) throws DeserializerException {
		return (LegConfigurations) this.xmlToObj(in, LegConfigurations.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.LegConfigurations}
	 * @return Object type {@link eu.domibus.configuration.LegConfigurations}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LegConfigurations readLegConfigurations(byte[] in) throws DeserializerException {
		return (LegConfigurations) this.xmlToObj(in, LegConfigurations.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.LegConfigurations}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.LegConfigurations}
	 * @return Object type {@link eu.domibus.configuration.LegConfigurations}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LegConfigurations readLegConfigurationsFromString(String in) throws DeserializerException {
		return (LegConfigurations) this.xmlToObj(in.getBytes(), LegConfigurations.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: service
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Service}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Service}
	 * @return Object type {@link eu.domibus.configuration.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readService(String fileName) throws DeserializerException {
		return (Service) this.xmlToObj(fileName, Service.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Service}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Service}
	 * @return Object type {@link eu.domibus.configuration.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readService(File file) throws DeserializerException {
		return (Service) this.xmlToObj(file, Service.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Service}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Service}
	 * @return Object type {@link eu.domibus.configuration.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readService(InputStream in) throws DeserializerException {
		return (Service) this.xmlToObj(in, Service.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Service}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Service}
	 * @return Object type {@link eu.domibus.configuration.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readService(byte[] in) throws DeserializerException {
		return (Service) this.xmlToObj(in, Service.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Service}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Service}
	 * @return Object type {@link eu.domibus.configuration.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readServiceFromString(String in) throws DeserializerException {
		return (Service) this.xmlToObj(in.getBytes(), Service.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: mpc
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Mpc}
	 * @return Object type {@link eu.domibus.configuration.Mpc}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mpc readMpc(String fileName) throws DeserializerException {
		return (Mpc) this.xmlToObj(fileName, Mpc.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Mpc}
	 * @return Object type {@link eu.domibus.configuration.Mpc}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mpc readMpc(File file) throws DeserializerException {
		return (Mpc) this.xmlToObj(file, Mpc.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Mpc}
	 * @return Object type {@link eu.domibus.configuration.Mpc}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mpc readMpc(InputStream in) throws DeserializerException {
		return (Mpc) this.xmlToObj(in, Mpc.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Mpc}
	 * @return Object type {@link eu.domibus.configuration.Mpc}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mpc readMpc(byte[] in) throws DeserializerException {
		return (Mpc) this.xmlToObj(in, Mpc.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Mpc}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Mpc}
	 * @return Object type {@link eu.domibus.configuration.Mpc}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mpc readMpcFromString(String in) throws DeserializerException {
		return (Mpc) this.xmlToObj(in.getBytes(), Mpc.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: mpcs
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Mpcs}
	 * @return Object type {@link eu.domibus.configuration.Mpcs}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mpcs readMpcs(String fileName) throws DeserializerException {
		return (Mpcs) this.xmlToObj(fileName, Mpcs.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Mpcs}
	 * @return Object type {@link eu.domibus.configuration.Mpcs}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mpcs readMpcs(File file) throws DeserializerException {
		return (Mpcs) this.xmlToObj(file, Mpcs.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Mpcs}
	 * @return Object type {@link eu.domibus.configuration.Mpcs}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mpcs readMpcs(InputStream in) throws DeserializerException {
		return (Mpcs) this.xmlToObj(in, Mpcs.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Mpcs}
	 * @return Object type {@link eu.domibus.configuration.Mpcs}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mpcs readMpcs(byte[] in) throws DeserializerException {
		return (Mpcs) this.xmlToObj(in, Mpcs.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Mpcs}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Mpcs}
	 * @return Object type {@link eu.domibus.configuration.Mpcs}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mpcs readMpcsFromString(String in) throws DeserializerException {
		return (Mpcs) this.xmlToObj(in.getBytes(), Mpcs.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: reliability
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Reliability}
	 * @return Object type {@link eu.domibus.configuration.Reliability}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Reliability readReliability(String fileName) throws DeserializerException {
		return (Reliability) this.xmlToObj(fileName, Reliability.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Reliability}
	 * @return Object type {@link eu.domibus.configuration.Reliability}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Reliability readReliability(File file) throws DeserializerException {
		return (Reliability) this.xmlToObj(file, Reliability.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Reliability}
	 * @return Object type {@link eu.domibus.configuration.Reliability}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Reliability readReliability(InputStream in) throws DeserializerException {
		return (Reliability) this.xmlToObj(in, Reliability.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Reliability}
	 * @return Object type {@link eu.domibus.configuration.Reliability}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Reliability readReliability(byte[] in) throws DeserializerException {
		return (Reliability) this.xmlToObj(in, Reliability.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Reliability}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Reliability}
	 * @return Object type {@link eu.domibus.configuration.Reliability}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Reliability readReliabilityFromString(String in) throws DeserializerException {
		return (Reliability) this.xmlToObj(in.getBytes(), Reliability.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: leg
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Leg}
	 * @return Object type {@link eu.domibus.configuration.Leg}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Leg readLeg(String fileName) throws DeserializerException {
		return (Leg) this.xmlToObj(fileName, Leg.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Leg}
	 * @return Object type {@link eu.domibus.configuration.Leg}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Leg readLeg(File file) throws DeserializerException {
		return (Leg) this.xmlToObj(file, Leg.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Leg}
	 * @return Object type {@link eu.domibus.configuration.Leg}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Leg readLeg(InputStream in) throws DeserializerException {
		return (Leg) this.xmlToObj(in, Leg.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Leg}
	 * @return Object type {@link eu.domibus.configuration.Leg}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Leg readLeg(byte[] in) throws DeserializerException {
		return (Leg) this.xmlToObj(in, Leg.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Leg}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Leg}
	 * @return Object type {@link eu.domibus.configuration.Leg}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Leg readLegFromString(String in) throws DeserializerException {
		return (Leg) this.xmlToObj(in.getBytes(), Leg.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: propertyRef
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyRef}
	 * @return Object type {@link eu.domibus.configuration.PropertyRef}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyRef readPropertyRef(String fileName) throws DeserializerException {
		return (PropertyRef) this.xmlToObj(fileName, PropertyRef.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyRef}
	 * @return Object type {@link eu.domibus.configuration.PropertyRef}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyRef readPropertyRef(File file) throws DeserializerException {
		return (PropertyRef) this.xmlToObj(file, PropertyRef.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyRef}
	 * @return Object type {@link eu.domibus.configuration.PropertyRef}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyRef readPropertyRef(InputStream in) throws DeserializerException {
		return (PropertyRef) this.xmlToObj(in, PropertyRef.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyRef}
	 * @return Object type {@link eu.domibus.configuration.PropertyRef}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyRef readPropertyRef(byte[] in) throws DeserializerException {
		return (PropertyRef) this.xmlToObj(in, PropertyRef.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.PropertyRef}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyRef}
	 * @return Object type {@link eu.domibus.configuration.PropertyRef}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyRef readPropertyRefFromString(String in) throws DeserializerException {
		return (PropertyRef) this.xmlToObj(in.getBytes(), PropertyRef.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: binding
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Binding}
	 * @return Object type {@link eu.domibus.configuration.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBinding(String fileName) throws DeserializerException {
		return (Binding) this.xmlToObj(fileName, Binding.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Binding}
	 * @return Object type {@link eu.domibus.configuration.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBinding(File file) throws DeserializerException {
		return (Binding) this.xmlToObj(file, Binding.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Binding}
	 * @return Object type {@link eu.domibus.configuration.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBinding(InputStream in) throws DeserializerException {
		return (Binding) this.xmlToObj(in, Binding.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Binding}
	 * @return Object type {@link eu.domibus.configuration.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBinding(byte[] in) throws DeserializerException {
		return (Binding) this.xmlToObj(in, Binding.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Binding}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Binding}
	 * @return Object type {@link eu.domibus.configuration.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBindingFromString(String in) throws DeserializerException {
		return (Binding) this.xmlToObj(in.getBytes(), Binding.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: mep
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Mep}
	 * @return Object type {@link eu.domibus.configuration.Mep}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mep readMep(String fileName) throws DeserializerException {
		return (Mep) this.xmlToObj(fileName, Mep.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Mep}
	 * @return Object type {@link eu.domibus.configuration.Mep}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mep readMep(File file) throws DeserializerException {
		return (Mep) this.xmlToObj(file, Mep.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Mep}
	 * @return Object type {@link eu.domibus.configuration.Mep}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mep readMep(InputStream in) throws DeserializerException {
		return (Mep) this.xmlToObj(in, Mep.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Mep}
	 * @return Object type {@link eu.domibus.configuration.Mep}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mep readMep(byte[] in) throws DeserializerException {
		return (Mep) this.xmlToObj(in, Mep.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Mep}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Mep}
	 * @return Object type {@link eu.domibus.configuration.Mep}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Mep readMepFromString(String in) throws DeserializerException {
		return (Mep) this.xmlToObj(in.getBytes(), Mep.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: meps
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Meps}
	 * @return Object type {@link eu.domibus.configuration.Meps}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Meps readMeps(String fileName) throws DeserializerException {
		return (Meps) this.xmlToObj(fileName, Meps.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Meps}
	 * @return Object type {@link eu.domibus.configuration.Meps}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Meps readMeps(File file) throws DeserializerException {
		return (Meps) this.xmlToObj(file, Meps.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Meps}
	 * @return Object type {@link eu.domibus.configuration.Meps}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Meps readMeps(InputStream in) throws DeserializerException {
		return (Meps) this.xmlToObj(in, Meps.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Meps}
	 * @return Object type {@link eu.domibus.configuration.Meps}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Meps readMeps(byte[] in) throws DeserializerException {
		return (Meps) this.xmlToObj(in, Meps.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Meps}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Meps}
	 * @return Object type {@link eu.domibus.configuration.Meps}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Meps readMepsFromString(String in) throws DeserializerException {
		return (Meps) this.xmlToObj(in.getBytes(), Meps.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: partyIdType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PartyIdType}
	 * @return Object type {@link eu.domibus.configuration.PartyIdType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyIdType readPartyIdType(String fileName) throws DeserializerException {
		return (PartyIdType) this.xmlToObj(fileName, PartyIdType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PartyIdType}
	 * @return Object type {@link eu.domibus.configuration.PartyIdType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyIdType readPartyIdType(File file) throws DeserializerException {
		return (PartyIdType) this.xmlToObj(file, PartyIdType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.PartyIdType}
	 * @return Object type {@link eu.domibus.configuration.PartyIdType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyIdType readPartyIdType(InputStream in) throws DeserializerException {
		return (PartyIdType) this.xmlToObj(in, PartyIdType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.PartyIdType}
	 * @return Object type {@link eu.domibus.configuration.PartyIdType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyIdType readPartyIdType(byte[] in) throws DeserializerException {
		return (PartyIdType) this.xmlToObj(in, PartyIdType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.PartyIdType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.PartyIdType}
	 * @return Object type {@link eu.domibus.configuration.PartyIdType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyIdType readPartyIdTypeFromString(String in) throws DeserializerException {
		return (PartyIdType) this.xmlToObj(in.getBytes(), PartyIdType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: payload
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Payload}
	 * @return Object type {@link eu.domibus.configuration.Payload}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Payload readPayload(String fileName) throws DeserializerException {
		return (Payload) this.xmlToObj(fileName, Payload.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Payload}
	 * @return Object type {@link eu.domibus.configuration.Payload}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Payload readPayload(File file) throws DeserializerException {
		return (Payload) this.xmlToObj(file, Payload.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Payload}
	 * @return Object type {@link eu.domibus.configuration.Payload}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Payload readPayload(InputStream in) throws DeserializerException {
		return (Payload) this.xmlToObj(in, Payload.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Payload}
	 * @return Object type {@link eu.domibus.configuration.Payload}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Payload readPayload(byte[] in) throws DeserializerException {
		return (Payload) this.xmlToObj(in, Payload.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Payload}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Payload}
	 * @return Object type {@link eu.domibus.configuration.Payload}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Payload readPayloadFromString(String in) throws DeserializerException {
		return (Payload) this.xmlToObj(in.getBytes(), Payload.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: payloadProfiles
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PayloadProfiles}
	 * @return Object type {@link eu.domibus.configuration.PayloadProfiles}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadProfiles readPayloadProfiles(String fileName) throws DeserializerException {
		return (PayloadProfiles) this.xmlToObj(fileName, PayloadProfiles.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PayloadProfiles}
	 * @return Object type {@link eu.domibus.configuration.PayloadProfiles}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadProfiles readPayloadProfiles(File file) throws DeserializerException {
		return (PayloadProfiles) this.xmlToObj(file, PayloadProfiles.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.PayloadProfiles}
	 * @return Object type {@link eu.domibus.configuration.PayloadProfiles}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadProfiles readPayloadProfiles(InputStream in) throws DeserializerException {
		return (PayloadProfiles) this.xmlToObj(in, PayloadProfiles.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.PayloadProfiles}
	 * @return Object type {@link eu.domibus.configuration.PayloadProfiles}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadProfiles readPayloadProfiles(byte[] in) throws DeserializerException {
		return (PayloadProfiles) this.xmlToObj(in, PayloadProfiles.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.PayloadProfiles}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.PayloadProfiles}
	 * @return Object type {@link eu.domibus.configuration.PayloadProfiles}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadProfiles readPayloadProfilesFromString(String in) throws DeserializerException {
		return (PayloadProfiles) this.xmlToObj(in.getBytes(), PayloadProfiles.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: payloadProfile
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PayloadProfile}
	 * @return Object type {@link eu.domibus.configuration.PayloadProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadProfile readPayloadProfile(String fileName) throws DeserializerException {
		return (PayloadProfile) this.xmlToObj(fileName, PayloadProfile.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PayloadProfile}
	 * @return Object type {@link eu.domibus.configuration.PayloadProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadProfile readPayloadProfile(File file) throws DeserializerException {
		return (PayloadProfile) this.xmlToObj(file, PayloadProfile.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.PayloadProfile}
	 * @return Object type {@link eu.domibus.configuration.PayloadProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadProfile readPayloadProfile(InputStream in) throws DeserializerException {
		return (PayloadProfile) this.xmlToObj(in, PayloadProfile.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.PayloadProfile}
	 * @return Object type {@link eu.domibus.configuration.PayloadProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadProfile readPayloadProfile(byte[] in) throws DeserializerException {
		return (PayloadProfile) this.xmlToObj(in, PayloadProfile.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.PayloadProfile}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.PayloadProfile}
	 * @return Object type {@link eu.domibus.configuration.PayloadProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadProfile readPayloadProfileFromString(String in) throws DeserializerException {
		return (PayloadProfile) this.xmlToObj(in.getBytes(), PayloadProfile.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: PropertyValue
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValue}
	 * @return Object type {@link eu.domibus.configuration.PropertyValue}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValue readPropertyValue(String fileName) throws DeserializerException {
		return (PropertyValue) this.xmlToObj(fileName, PropertyValue.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValue}
	 * @return Object type {@link eu.domibus.configuration.PropertyValue}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValue readPropertyValue(File file) throws DeserializerException {
		return (PropertyValue) this.xmlToObj(file, PropertyValue.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValue}
	 * @return Object type {@link eu.domibus.configuration.PropertyValue}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValue readPropertyValue(InputStream in) throws DeserializerException {
		return (PropertyValue) this.xmlToObj(in, PropertyValue.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValue}
	 * @return Object type {@link eu.domibus.configuration.PropertyValue}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValue readPropertyValue(byte[] in) throws DeserializerException {
		return (PropertyValue) this.xmlToObj(in, PropertyValue.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.PropertyValue}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValue}
	 * @return Object type {@link eu.domibus.configuration.PropertyValue}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValue readPropertyValueFromString(String in) throws DeserializerException {
		return (PropertyValue) this.xmlToObj(in.getBytes(), PropertyValue.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: property
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Property}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Property}
	 * @return Object type {@link eu.domibus.configuration.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(String fileName) throws DeserializerException {
		return (Property) this.xmlToObj(fileName, Property.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Property}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Property}
	 * @return Object type {@link eu.domibus.configuration.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(File file) throws DeserializerException {
		return (Property) this.xmlToObj(file, Property.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Property}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Property}
	 * @return Object type {@link eu.domibus.configuration.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(InputStream in) throws DeserializerException {
		return (Property) this.xmlToObj(in, Property.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Property}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Property}
	 * @return Object type {@link eu.domibus.configuration.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(byte[] in) throws DeserializerException {
		return (Property) this.xmlToObj(in, Property.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Property}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Property}
	 * @return Object type {@link eu.domibus.configuration.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readPropertyFromString(String in) throws DeserializerException {
		return (Property) this.xmlToObj(in.getBytes(), Property.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: security
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Security}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Security}
	 * @return Object type {@link eu.domibus.configuration.Security}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Security readSecurity(String fileName) throws DeserializerException {
		return (Security) this.xmlToObj(fileName, Security.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Security}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Security}
	 * @return Object type {@link eu.domibus.configuration.Security}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Security readSecurity(File file) throws DeserializerException {
		return (Security) this.xmlToObj(file, Security.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Security}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Security}
	 * @return Object type {@link eu.domibus.configuration.Security}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Security readSecurity(InputStream in) throws DeserializerException {
		return (Security) this.xmlToObj(in, Security.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Security}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Security}
	 * @return Object type {@link eu.domibus.configuration.Security}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Security readSecurity(byte[] in) throws DeserializerException {
		return (Security) this.xmlToObj(in, Security.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Security}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Security}
	 * @return Object type {@link eu.domibus.configuration.Security}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Security readSecurityFromString(String in) throws DeserializerException {
		return (Security) this.xmlToObj(in.getBytes(), Security.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: partyIdTypes
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PartyIdTypes}
	 * @return Object type {@link eu.domibus.configuration.PartyIdTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyIdTypes readPartyIdTypes(String fileName) throws DeserializerException {
		return (PartyIdTypes) this.xmlToObj(fileName, PartyIdTypes.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PartyIdTypes}
	 * @return Object type {@link eu.domibus.configuration.PartyIdTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyIdTypes readPartyIdTypes(File file) throws DeserializerException {
		return (PartyIdTypes) this.xmlToObj(file, PartyIdTypes.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.PartyIdTypes}
	 * @return Object type {@link eu.domibus.configuration.PartyIdTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyIdTypes readPartyIdTypes(InputStream in) throws DeserializerException {
		return (PartyIdTypes) this.xmlToObj(in, PartyIdTypes.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.PartyIdTypes}
	 * @return Object type {@link eu.domibus.configuration.PartyIdTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyIdTypes readPartyIdTypes(byte[] in) throws DeserializerException {
		return (PartyIdTypes) this.xmlToObj(in, PartyIdTypes.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.PartyIdTypes}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.PartyIdTypes}
	 * @return Object type {@link eu.domibus.configuration.PartyIdTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyIdTypes readPartyIdTypesFromString(String in) throws DeserializerException {
		return (PartyIdTypes) this.xmlToObj(in.getBytes(), PartyIdTypes.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: parties
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Parties}
	 * @return Object type {@link eu.domibus.configuration.Parties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Parties readParties(String fileName) throws DeserializerException {
		return (Parties) this.xmlToObj(fileName, Parties.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Parties}
	 * @return Object type {@link eu.domibus.configuration.Parties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Parties readParties(File file) throws DeserializerException {
		return (Parties) this.xmlToObj(file, Parties.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Parties}
	 * @return Object type {@link eu.domibus.configuration.Parties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Parties readParties(InputStream in) throws DeserializerException {
		return (Parties) this.xmlToObj(in, Parties.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Parties}
	 * @return Object type {@link eu.domibus.configuration.Parties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Parties readParties(byte[] in) throws DeserializerException {
		return (Parties) this.xmlToObj(in, Parties.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Parties}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Parties}
	 * @return Object type {@link eu.domibus.configuration.Parties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Parties readPartiesFromString(String in) throws DeserializerException {
		return (Parties) this.xmlToObj(in.getBytes(), Parties.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: party
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Party}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Party}
	 * @return Object type {@link eu.domibus.configuration.Party}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Party readParty(String fileName) throws DeserializerException {
		return (Party) this.xmlToObj(fileName, Party.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Party}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Party}
	 * @return Object type {@link eu.domibus.configuration.Party}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Party readParty(File file) throws DeserializerException {
		return (Party) this.xmlToObj(file, Party.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Party}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Party}
	 * @return Object type {@link eu.domibus.configuration.Party}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Party readParty(InputStream in) throws DeserializerException {
		return (Party) this.xmlToObj(in, Party.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Party}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Party}
	 * @return Object type {@link eu.domibus.configuration.Party}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Party readParty(byte[] in) throws DeserializerException {
		return (Party) this.xmlToObj(in, Party.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Party}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Party}
	 * @return Object type {@link eu.domibus.configuration.Party}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Party readPartyFromString(String in) throws DeserializerException {
		return (Party) this.xmlToObj(in.getBytes(), Party.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: properties
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Properties}
	 * @return Object type {@link eu.domibus.configuration.Properties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Properties readProperties(String fileName) throws DeserializerException {
		return (Properties) this.xmlToObj(fileName, Properties.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Properties}
	 * @return Object type {@link eu.domibus.configuration.Properties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Properties readProperties(File file) throws DeserializerException {
		return (Properties) this.xmlToObj(file, Properties.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Properties}
	 * @return Object type {@link eu.domibus.configuration.Properties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Properties readProperties(InputStream in) throws DeserializerException {
		return (Properties) this.xmlToObj(in, Properties.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Properties}
	 * @return Object type {@link eu.domibus.configuration.Properties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Properties readProperties(byte[] in) throws DeserializerException {
		return (Properties) this.xmlToObj(in, Properties.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Properties}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Properties}
	 * @return Object type {@link eu.domibus.configuration.Properties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Properties readPropertiesFromString(String in) throws DeserializerException {
		return (Properties) this.xmlToObj(in.getBytes(), Properties.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: propertySet
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertySet}
	 * @return Object type {@link eu.domibus.configuration.PropertySet}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertySet readPropertySet(String fileName) throws DeserializerException {
		return (PropertySet) this.xmlToObj(fileName, PropertySet.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertySet}
	 * @return Object type {@link eu.domibus.configuration.PropertySet}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertySet readPropertySet(File file) throws DeserializerException {
		return (PropertySet) this.xmlToObj(file, PropertySet.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertySet}
	 * @return Object type {@link eu.domibus.configuration.PropertySet}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertySet readPropertySet(InputStream in) throws DeserializerException {
		return (PropertySet) this.xmlToObj(in, PropertySet.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertySet}
	 * @return Object type {@link eu.domibus.configuration.PropertySet}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertySet readPropertySet(byte[] in) throws DeserializerException {
		return (PropertySet) this.xmlToObj(in, PropertySet.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.PropertySet}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertySet}
	 * @return Object type {@link eu.domibus.configuration.PropertySet}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertySet readPropertySetFromString(String in) throws DeserializerException {
		return (PropertySet) this.xmlToObj(in.getBytes(), PropertySet.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: PropertyValueHeader
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * @return Object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValueHeader readPropertyValueHeader(String fileName) throws DeserializerException {
		return (PropertyValueHeader) this.xmlToObj(fileName, PropertyValueHeader.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * @return Object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValueHeader readPropertyValueHeader(File file) throws DeserializerException {
		return (PropertyValueHeader) this.xmlToObj(file, PropertyValueHeader.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * @return Object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValueHeader readPropertyValueHeader(InputStream in) throws DeserializerException {
		return (PropertyValueHeader) this.xmlToObj(in, PropertyValueHeader.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * @return Object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValueHeader readPropertyValueHeader(byte[] in) throws DeserializerException {
		return (PropertyValueHeader) this.xmlToObj(in, PropertyValueHeader.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * @return Object type {@link eu.domibus.configuration.PropertyValueHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValueHeader readPropertyValueHeaderFromString(String in) throws DeserializerException {
		return (PropertyValueHeader) this.xmlToObj(in.getBytes(), PropertyValueHeader.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: receptionAwareness
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * @return Object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReceptionAwareness readReceptionAwareness(String fileName) throws DeserializerException {
		return (ReceptionAwareness) this.xmlToObj(fileName, ReceptionAwareness.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * @return Object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReceptionAwareness readReceptionAwareness(File file) throws DeserializerException {
		return (ReceptionAwareness) this.xmlToObj(file, ReceptionAwareness.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * @return Object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReceptionAwareness readReceptionAwareness(InputStream in) throws DeserializerException {
		return (ReceptionAwareness) this.xmlToObj(in, ReceptionAwareness.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * @return Object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReceptionAwareness readReceptionAwareness(byte[] in) throws DeserializerException {
		return (ReceptionAwareness) this.xmlToObj(in, ReceptionAwareness.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * @return Object type {@link eu.domibus.configuration.ReceptionAwareness}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReceptionAwareness readReceptionAwarenessFromString(String in) throws DeserializerException {
		return (ReceptionAwareness) this.xmlToObj(in.getBytes(), ReceptionAwareness.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: as4
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.As4}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.As4}
	 * @return Object type {@link eu.domibus.configuration.As4}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public As4 readAs4(String fileName) throws DeserializerException {
		return (As4) this.xmlToObj(fileName, As4.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.As4}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.As4}
	 * @return Object type {@link eu.domibus.configuration.As4}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public As4 readAs4(File file) throws DeserializerException {
		return (As4) this.xmlToObj(file, As4.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.As4}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.As4}
	 * @return Object type {@link eu.domibus.configuration.As4}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public As4 readAs4(InputStream in) throws DeserializerException {
		return (As4) this.xmlToObj(in, As4.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.As4}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.As4}
	 * @return Object type {@link eu.domibus.configuration.As4}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public As4 readAs4(byte[] in) throws DeserializerException {
		return (As4) this.xmlToObj(in, As4.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.As4}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.As4}
	 * @return Object type {@link eu.domibus.configuration.As4}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public As4 readAs4FromString(String in) throws DeserializerException {
		return (As4) this.xmlToObj(in.getBytes(), As4.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: PropertyValueUrl
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * @return Object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValueUrl readPropertyValueUrl(String fileName) throws DeserializerException {
		return (PropertyValueUrl) this.xmlToObj(fileName, PropertyValueUrl.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * @return Object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValueUrl readPropertyValueUrl(File file) throws DeserializerException {
		return (PropertyValueUrl) this.xmlToObj(file, PropertyValueUrl.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * @return Object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValueUrl readPropertyValueUrl(InputStream in) throws DeserializerException {
		return (PropertyValueUrl) this.xmlToObj(in, PropertyValueUrl.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * @return Object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValueUrl readPropertyValueUrl(byte[] in) throws DeserializerException {
		return (PropertyValueUrl) this.xmlToObj(in, PropertyValueUrl.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * @return Object type {@link eu.domibus.configuration.PropertyValueUrl}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PropertyValueUrl readPropertyValueUrlFromString(String in) throws DeserializerException {
		return (PropertyValueUrl) this.xmlToObj(in.getBytes(), PropertyValueUrl.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: role
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Role}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Role}
	 * @return Object type {@link eu.domibus.configuration.Role}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Role readRole(String fileName) throws DeserializerException {
		return (Role) this.xmlToObj(fileName, Role.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Role}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Role}
	 * @return Object type {@link eu.domibus.configuration.Role}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Role readRole(File file) throws DeserializerException {
		return (Role) this.xmlToObj(file, Role.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Role}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Role}
	 * @return Object type {@link eu.domibus.configuration.Role}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Role readRole(InputStream in) throws DeserializerException {
		return (Role) this.xmlToObj(in, Role.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Role}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Role}
	 * @return Object type {@link eu.domibus.configuration.Role}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Role readRole(byte[] in) throws DeserializerException {
		return (Role) this.xmlToObj(in, Role.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Role}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Role}
	 * @return Object type {@link eu.domibus.configuration.Role}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Role readRoleFromString(String in) throws DeserializerException {
		return (Role) this.xmlToObj(in.getBytes(), Role.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: roles
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Roles}
	 * @return Object type {@link eu.domibus.configuration.Roles}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Roles readRoles(String fileName) throws DeserializerException {
		return (Roles) this.xmlToObj(fileName, Roles.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Roles}
	 * @return Object type {@link eu.domibus.configuration.Roles}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Roles readRoles(File file) throws DeserializerException {
		return (Roles) this.xmlToObj(file, Roles.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Roles}
	 * @return Object type {@link eu.domibus.configuration.Roles}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Roles readRoles(InputStream in) throws DeserializerException {
		return (Roles) this.xmlToObj(in, Roles.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Roles}
	 * @return Object type {@link eu.domibus.configuration.Roles}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Roles readRoles(byte[] in) throws DeserializerException {
		return (Roles) this.xmlToObj(in, Roles.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Roles}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Roles}
	 * @return Object type {@link eu.domibus.configuration.Roles}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Roles readRolesFromString(String in) throws DeserializerException {
		return (Roles) this.xmlToObj(in.getBytes(), Roles.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configuration
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Configuration}
	 * @return Object type {@link eu.domibus.configuration.Configuration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Configuration readConfiguration(String fileName) throws DeserializerException {
		return (Configuration) this.xmlToObj(fileName, Configuration.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Configuration}
	 * @return Object type {@link eu.domibus.configuration.Configuration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Configuration readConfiguration(File file) throws DeserializerException {
		return (Configuration) this.xmlToObj(file, Configuration.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Configuration}
	 * @return Object type {@link eu.domibus.configuration.Configuration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Configuration readConfiguration(InputStream in) throws DeserializerException {
		return (Configuration) this.xmlToObj(in, Configuration.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Configuration}
	 * @return Object type {@link eu.domibus.configuration.Configuration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Configuration readConfiguration(byte[] in) throws DeserializerException {
		return (Configuration) this.xmlToObj(in, Configuration.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Configuration}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Configuration}
	 * @return Object type {@link eu.domibus.configuration.Configuration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Configuration readConfigurationFromString(String in) throws DeserializerException {
		return (Configuration) this.xmlToObj(in.getBytes(), Configuration.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: businessProcesses
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.BusinessProcesses}
	 * @return Object type {@link eu.domibus.configuration.BusinessProcesses}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BusinessProcesses readBusinessProcesses(String fileName) throws DeserializerException {
		return (BusinessProcesses) this.xmlToObj(fileName, BusinessProcesses.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.BusinessProcesses}
	 * @return Object type {@link eu.domibus.configuration.BusinessProcesses}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BusinessProcesses readBusinessProcesses(File file) throws DeserializerException {
		return (BusinessProcesses) this.xmlToObj(file, BusinessProcesses.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.BusinessProcesses}
	 * @return Object type {@link eu.domibus.configuration.BusinessProcesses}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BusinessProcesses readBusinessProcesses(InputStream in) throws DeserializerException {
		return (BusinessProcesses) this.xmlToObj(in, BusinessProcesses.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.BusinessProcesses}
	 * @return Object type {@link eu.domibus.configuration.BusinessProcesses}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BusinessProcesses readBusinessProcesses(byte[] in) throws DeserializerException {
		return (BusinessProcesses) this.xmlToObj(in, BusinessProcesses.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.BusinessProcesses}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.BusinessProcesses}
	 * @return Object type {@link eu.domibus.configuration.BusinessProcesses}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BusinessProcesses readBusinessProcessesFromString(String in) throws DeserializerException {
		return (BusinessProcesses) this.xmlToObj(in.getBytes(), BusinessProcesses.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: securities
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Securities}
	 * @return Object type {@link eu.domibus.configuration.Securities}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Securities readSecurities(String fileName) throws DeserializerException {
		return (Securities) this.xmlToObj(fileName, Securities.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Securities}
	 * @return Object type {@link eu.domibus.configuration.Securities}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Securities readSecurities(File file) throws DeserializerException {
		return (Securities) this.xmlToObj(file, Securities.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Securities}
	 * @return Object type {@link eu.domibus.configuration.Securities}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Securities readSecurities(InputStream in) throws DeserializerException {
		return (Securities) this.xmlToObj(in, Securities.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Securities}
	 * @return Object type {@link eu.domibus.configuration.Securities}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Securities readSecurities(byte[] in) throws DeserializerException {
		return (Securities) this.xmlToObj(in, Securities.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Securities}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Securities}
	 * @return Object type {@link eu.domibus.configuration.Securities}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Securities readSecuritiesFromString(String in) throws DeserializerException {
		return (Securities) this.xmlToObj(in.getBytes(), Securities.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: errorHandlings
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.ErrorHandlings}
	 * @return Object type {@link eu.domibus.configuration.ErrorHandlings}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorHandlings readErrorHandlings(String fileName) throws DeserializerException {
		return (ErrorHandlings) this.xmlToObj(fileName, ErrorHandlings.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.ErrorHandlings}
	 * @return Object type {@link eu.domibus.configuration.ErrorHandlings}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorHandlings readErrorHandlings(File file) throws DeserializerException {
		return (ErrorHandlings) this.xmlToObj(file, ErrorHandlings.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.ErrorHandlings}
	 * @return Object type {@link eu.domibus.configuration.ErrorHandlings}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorHandlings readErrorHandlings(InputStream in) throws DeserializerException {
		return (ErrorHandlings) this.xmlToObj(in, ErrorHandlings.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.ErrorHandlings}
	 * @return Object type {@link eu.domibus.configuration.ErrorHandlings}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorHandlings readErrorHandlings(byte[] in) throws DeserializerException {
		return (ErrorHandlings) this.xmlToObj(in, ErrorHandlings.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.ErrorHandlings}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.ErrorHandlings}
	 * @return Object type {@link eu.domibus.configuration.ErrorHandlings}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorHandlings readErrorHandlingsFromString(String in) throws DeserializerException {
		return (ErrorHandlings) this.xmlToObj(in.getBytes(), ErrorHandlings.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: agreements
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Agreements}
	 * @return Object type {@link eu.domibus.configuration.Agreements}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Agreements readAgreements(String fileName) throws DeserializerException {
		return (Agreements) this.xmlToObj(fileName, Agreements.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Agreements}
	 * @return Object type {@link eu.domibus.configuration.Agreements}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Agreements readAgreements(File file) throws DeserializerException {
		return (Agreements) this.xmlToObj(file, Agreements.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Agreements}
	 * @return Object type {@link eu.domibus.configuration.Agreements}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Agreements readAgreements(InputStream in) throws DeserializerException {
		return (Agreements) this.xmlToObj(in, Agreements.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Agreements}
	 * @return Object type {@link eu.domibus.configuration.Agreements}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Agreements readAgreements(byte[] in) throws DeserializerException {
		return (Agreements) this.xmlToObj(in, Agreements.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Agreements}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Agreements}
	 * @return Object type {@link eu.domibus.configuration.Agreements}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Agreements readAgreementsFromString(String in) throws DeserializerException {
		return (Agreements) this.xmlToObj(in.getBytes(), Agreements.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: services
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Services}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Services}
	 * @return Object type {@link eu.domibus.configuration.Services}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Services readServices(String fileName) throws DeserializerException {
		return (Services) this.xmlToObj(fileName, Services.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Services}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Services}
	 * @return Object type {@link eu.domibus.configuration.Services}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Services readServices(File file) throws DeserializerException {
		return (Services) this.xmlToObj(file, Services.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Services}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Services}
	 * @return Object type {@link eu.domibus.configuration.Services}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Services readServices(InputStream in) throws DeserializerException {
		return (Services) this.xmlToObj(in, Services.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Services}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Services}
	 * @return Object type {@link eu.domibus.configuration.Services}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Services readServices(byte[] in) throws DeserializerException {
		return (Services) this.xmlToObj(in, Services.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Services}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Services}
	 * @return Object type {@link eu.domibus.configuration.Services}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Services readServicesFromString(String in) throws DeserializerException {
		return (Services) this.xmlToObj(in.getBytes(), Services.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: actions
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Actions}
	 * @return Object type {@link eu.domibus.configuration.Actions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Actions readActions(String fileName) throws DeserializerException {
		return (Actions) this.xmlToObj(fileName, Actions.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Actions}
	 * @return Object type {@link eu.domibus.configuration.Actions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Actions readActions(File file) throws DeserializerException {
		return (Actions) this.xmlToObj(file, Actions.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Actions}
	 * @return Object type {@link eu.domibus.configuration.Actions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Actions readActions(InputStream in) throws DeserializerException {
		return (Actions) this.xmlToObj(in, Actions.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Actions}
	 * @return Object type {@link eu.domibus.configuration.Actions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Actions readActions(byte[] in) throws DeserializerException {
		return (Actions) this.xmlToObj(in, Actions.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Actions}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Actions}
	 * @return Object type {@link eu.domibus.configuration.Actions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Actions readActionsFromString(String in) throws DeserializerException {
		return (Actions) this.xmlToObj(in.getBytes(), Actions.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: process
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Process}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Process}
	 * @return Object type {@link eu.domibus.configuration.Process}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Process readProcess(String fileName) throws DeserializerException {
		return (Process) this.xmlToObj(fileName, Process.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Process}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Process}
	 * @return Object type {@link eu.domibus.configuration.Process}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Process readProcess(File file) throws DeserializerException {
		return (Process) this.xmlToObj(file, Process.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Process}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Process}
	 * @return Object type {@link eu.domibus.configuration.Process}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Process readProcess(InputStream in) throws DeserializerException {
		return (Process) this.xmlToObj(in, Process.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Process}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Process}
	 * @return Object type {@link eu.domibus.configuration.Process}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Process readProcess(byte[] in) throws DeserializerException {
		return (Process) this.xmlToObj(in, Process.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Process}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Process}
	 * @return Object type {@link eu.domibus.configuration.Process}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Process readProcessFromString(String in) throws DeserializerException {
		return (Process) this.xmlToObj(in.getBytes(), Process.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: agreement
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Agreement}
	 * @return Object type {@link eu.domibus.configuration.Agreement}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Agreement readAgreement(String fileName) throws DeserializerException {
		return (Agreement) this.xmlToObj(fileName, Agreement.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Agreement}
	 * @return Object type {@link eu.domibus.configuration.Agreement}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Agreement readAgreement(File file) throws DeserializerException {
		return (Agreement) this.xmlToObj(file, Agreement.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Agreement}
	 * @return Object type {@link eu.domibus.configuration.Agreement}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Agreement readAgreement(InputStream in) throws DeserializerException {
		return (Agreement) this.xmlToObj(in, Agreement.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Agreement}
	 * @return Object type {@link eu.domibus.configuration.Agreement}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Agreement readAgreement(byte[] in) throws DeserializerException {
		return (Agreement) this.xmlToObj(in, Agreement.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Agreement}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Agreement}
	 * @return Object type {@link eu.domibus.configuration.Agreement}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Agreement readAgreementFromString(String in) throws DeserializerException {
		return (Agreement) this.xmlToObj(in.getBytes(), Agreement.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: initiatorParty
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.InitiatorParty}
	 * @return Object type {@link eu.domibus.configuration.InitiatorParty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InitiatorParty readInitiatorParty(String fileName) throws DeserializerException {
		return (InitiatorParty) this.xmlToObj(fileName, InitiatorParty.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.InitiatorParty}
	 * @return Object type {@link eu.domibus.configuration.InitiatorParty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InitiatorParty readInitiatorParty(File file) throws DeserializerException {
		return (InitiatorParty) this.xmlToObj(file, InitiatorParty.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.InitiatorParty}
	 * @return Object type {@link eu.domibus.configuration.InitiatorParty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InitiatorParty readInitiatorParty(InputStream in) throws DeserializerException {
		return (InitiatorParty) this.xmlToObj(in, InitiatorParty.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.InitiatorParty}
	 * @return Object type {@link eu.domibus.configuration.InitiatorParty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InitiatorParty readInitiatorParty(byte[] in) throws DeserializerException {
		return (InitiatorParty) this.xmlToObj(in, InitiatorParty.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.InitiatorParty}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.InitiatorParty}
	 * @return Object type {@link eu.domibus.configuration.InitiatorParty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InitiatorParty readInitiatorPartyFromString(String in) throws DeserializerException {
		return (InitiatorParty) this.xmlToObj(in.getBytes(), InitiatorParty.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: action
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Action}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Action}
	 * @return Object type {@link eu.domibus.configuration.Action}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Action readAction(String fileName) throws DeserializerException {
		return (Action) this.xmlToObj(fileName, Action.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Action}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Action}
	 * @return Object type {@link eu.domibus.configuration.Action}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Action readAction(File file) throws DeserializerException {
		return (Action) this.xmlToObj(file, Action.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Action}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Action}
	 * @return Object type {@link eu.domibus.configuration.Action}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Action readAction(InputStream in) throws DeserializerException {
		return (Action) this.xmlToObj(in, Action.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Action}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Action}
	 * @return Object type {@link eu.domibus.configuration.Action}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Action readAction(byte[] in) throws DeserializerException {
		return (Action) this.xmlToObj(in, Action.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Action}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Action}
	 * @return Object type {@link eu.domibus.configuration.Action}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Action readActionFromString(String in) throws DeserializerException {
		return (Action) this.xmlToObj(in.getBytes(), Action.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: identifier
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Identifier}
	 * @return Object type {@link eu.domibus.configuration.Identifier}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Identifier readIdentifier(String fileName) throws DeserializerException {
		return (Identifier) this.xmlToObj(fileName, Identifier.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Identifier}
	 * @return Object type {@link eu.domibus.configuration.Identifier}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Identifier readIdentifier(File file) throws DeserializerException {
		return (Identifier) this.xmlToObj(file, Identifier.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Identifier}
	 * @return Object type {@link eu.domibus.configuration.Identifier}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Identifier readIdentifier(InputStream in) throws DeserializerException {
		return (Identifier) this.xmlToObj(in, Identifier.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Identifier}
	 * @return Object type {@link eu.domibus.configuration.Identifier}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Identifier readIdentifier(byte[] in) throws DeserializerException {
		return (Identifier) this.xmlToObj(in, Identifier.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Identifier}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Identifier}
	 * @return Object type {@link eu.domibus.configuration.Identifier}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Identifier readIdentifierFromString(String in) throws DeserializerException {
		return (Identifier) this.xmlToObj(in.getBytes(), Identifier.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: header
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Header}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Header}
	 * @return Object type {@link eu.domibus.configuration.Header}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Header readHeader(String fileName) throws DeserializerException {
		return (Header) this.xmlToObj(fileName, Header.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Header}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Header}
	 * @return Object type {@link eu.domibus.configuration.Header}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Header readHeader(File file) throws DeserializerException {
		return (Header) this.xmlToObj(file, Header.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Header}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Header}
	 * @return Object type {@link eu.domibus.configuration.Header}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Header readHeader(InputStream in) throws DeserializerException {
		return (Header) this.xmlToObj(in, Header.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Header}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Header}
	 * @return Object type {@link eu.domibus.configuration.Header}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Header readHeader(byte[] in) throws DeserializerException {
		return (Header) this.xmlToObj(in, Header.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Header}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Header}
	 * @return Object type {@link eu.domibus.configuration.Header}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Header readHeaderFromString(String in) throws DeserializerException {
		return (Header) this.xmlToObj(in.getBytes(), Header.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: errorHandling
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.ErrorHandling}
	 * @return Object type {@link eu.domibus.configuration.ErrorHandling}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorHandling readErrorHandling(String fileName) throws DeserializerException {
		return (ErrorHandling) this.xmlToObj(fileName, ErrorHandling.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.ErrorHandling}
	 * @return Object type {@link eu.domibus.configuration.ErrorHandling}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorHandling readErrorHandling(File file) throws DeserializerException {
		return (ErrorHandling) this.xmlToObj(file, ErrorHandling.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.ErrorHandling}
	 * @return Object type {@link eu.domibus.configuration.ErrorHandling}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorHandling readErrorHandling(InputStream in) throws DeserializerException {
		return (ErrorHandling) this.xmlToObj(in, ErrorHandling.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.ErrorHandling}
	 * @return Object type {@link eu.domibus.configuration.ErrorHandling}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorHandling readErrorHandling(byte[] in) throws DeserializerException {
		return (ErrorHandling) this.xmlToObj(in, ErrorHandling.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.ErrorHandling}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.ErrorHandling}
	 * @return Object type {@link eu.domibus.configuration.ErrorHandling}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorHandling readErrorHandlingFromString(String in) throws DeserializerException {
		return (ErrorHandling) this.xmlToObj(in.getBytes(), ErrorHandling.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: legs
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Legs}
	 * @return Object type {@link eu.domibus.configuration.Legs}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Legs readLegs(String fileName) throws DeserializerException {
		return (Legs) this.xmlToObj(fileName, Legs.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Legs}
	 * @return Object type {@link eu.domibus.configuration.Legs}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Legs readLegs(File file) throws DeserializerException {
		return (Legs) this.xmlToObj(file, Legs.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Legs}
	 * @return Object type {@link eu.domibus.configuration.Legs}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Legs readLegs(InputStream in) throws DeserializerException {
		return (Legs) this.xmlToObj(in, Legs.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Legs}
	 * @return Object type {@link eu.domibus.configuration.Legs}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Legs readLegs(byte[] in) throws DeserializerException {
		return (Legs) this.xmlToObj(in, Legs.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Legs}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Legs}
	 * @return Object type {@link eu.domibus.configuration.Legs}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Legs readLegsFromString(String in) throws DeserializerException {
		return (Legs) this.xmlToObj(in.getBytes(), Legs.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: attachment
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Attachment}
	 * @return Object type {@link eu.domibus.configuration.Attachment}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Attachment readAttachment(String fileName) throws DeserializerException {
		return (Attachment) this.xmlToObj(fileName, Attachment.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Attachment}
	 * @return Object type {@link eu.domibus.configuration.Attachment}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Attachment readAttachment(File file) throws DeserializerException {
		return (Attachment) this.xmlToObj(file, Attachment.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Attachment}
	 * @return Object type {@link eu.domibus.configuration.Attachment}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Attachment readAttachment(InputStream in) throws DeserializerException {
		return (Attachment) this.xmlToObj(in, Attachment.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Attachment}
	 * @return Object type {@link eu.domibus.configuration.Attachment}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Attachment readAttachment(byte[] in) throws DeserializerException {
		return (Attachment) this.xmlToObj(in, Attachment.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Attachment}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Attachment}
	 * @return Object type {@link eu.domibus.configuration.Attachment}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Attachment readAttachmentFromString(String in) throws DeserializerException {
		return (Attachment) this.xmlToObj(in.getBytes(), Attachment.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: url
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.Url}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Url}
	 * @return Object type {@link eu.domibus.configuration.Url}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Url readUrl(String fileName) throws DeserializerException {
		return (Url) this.xmlToObj(fileName, Url.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.Url}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.Url}
	 * @return Object type {@link eu.domibus.configuration.Url}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Url readUrl(File file) throws DeserializerException {
		return (Url) this.xmlToObj(file, Url.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.Url}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.Url}
	 * @return Object type {@link eu.domibus.configuration.Url}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Url readUrl(InputStream in) throws DeserializerException {
		return (Url) this.xmlToObj(in, Url.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.Url}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.Url}
	 * @return Object type {@link eu.domibus.configuration.Url}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Url readUrl(byte[] in) throws DeserializerException {
		return (Url) this.xmlToObj(in, Url.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.Url}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.Url}
	 * @return Object type {@link eu.domibus.configuration.Url}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Url readUrlFromString(String in) throws DeserializerException {
		return (Url) this.xmlToObj(in.getBytes(), Url.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: initiatorParties
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.InitiatorParties}
	 * @return Object type {@link eu.domibus.configuration.InitiatorParties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InitiatorParties readInitiatorParties(String fileName) throws DeserializerException {
		return (InitiatorParties) this.xmlToObj(fileName, InitiatorParties.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.InitiatorParties}
	 * @return Object type {@link eu.domibus.configuration.InitiatorParties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InitiatorParties readInitiatorParties(File file) throws DeserializerException {
		return (InitiatorParties) this.xmlToObj(file, InitiatorParties.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.InitiatorParties}
	 * @return Object type {@link eu.domibus.configuration.InitiatorParties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InitiatorParties readInitiatorParties(InputStream in) throws DeserializerException {
		return (InitiatorParties) this.xmlToObj(in, InitiatorParties.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.InitiatorParties}
	 * @return Object type {@link eu.domibus.configuration.InitiatorParties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InitiatorParties readInitiatorParties(byte[] in) throws DeserializerException {
		return (InitiatorParties) this.xmlToObj(in, InitiatorParties.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.InitiatorParties}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.InitiatorParties}
	 * @return Object type {@link eu.domibus.configuration.InitiatorParties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InitiatorParties readInitiatorPartiesFromString(String in) throws DeserializerException {
		return (InitiatorParties) this.xmlToObj(in.getBytes(), InitiatorParties.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: responderParties
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.ResponderParties}
	 * @return Object type {@link eu.domibus.configuration.ResponderParties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResponderParties readResponderParties(String fileName) throws DeserializerException {
		return (ResponderParties) this.xmlToObj(fileName, ResponderParties.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.ResponderParties}
	 * @return Object type {@link eu.domibus.configuration.ResponderParties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResponderParties readResponderParties(File file) throws DeserializerException {
		return (ResponderParties) this.xmlToObj(file, ResponderParties.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.ResponderParties}
	 * @return Object type {@link eu.domibus.configuration.ResponderParties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResponderParties readResponderParties(InputStream in) throws DeserializerException {
		return (ResponderParties) this.xmlToObj(in, ResponderParties.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.ResponderParties}
	 * @return Object type {@link eu.domibus.configuration.ResponderParties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResponderParties readResponderParties(byte[] in) throws DeserializerException {
		return (ResponderParties) this.xmlToObj(in, ResponderParties.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.ResponderParties}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.ResponderParties}
	 * @return Object type {@link eu.domibus.configuration.ResponderParties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResponderParties readResponderPartiesFromString(String in) throws DeserializerException {
		return (ResponderParties) this.xmlToObj(in.getBytes(), ResponderParties.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: responderParty
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.ResponderParty}
	 * @return Object type {@link eu.domibus.configuration.ResponderParty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResponderParty readResponderParty(String fileName) throws DeserializerException {
		return (ResponderParty) this.xmlToObj(fileName, ResponderParty.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link eu.domibus.configuration.ResponderParty}
	 * @return Object type {@link eu.domibus.configuration.ResponderParty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResponderParty readResponderParty(File file) throws DeserializerException {
		return (ResponderParty) this.xmlToObj(file, ResponderParty.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link eu.domibus.configuration.ResponderParty}
	 * @return Object type {@link eu.domibus.configuration.ResponderParty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResponderParty readResponderParty(InputStream in) throws DeserializerException {
		return (ResponderParty) this.xmlToObj(in, ResponderParty.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link eu.domibus.configuration.ResponderParty}
	 * @return Object type {@link eu.domibus.configuration.ResponderParty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResponderParty readResponderParty(byte[] in) throws DeserializerException {
		return (ResponderParty) this.xmlToObj(in, ResponderParty.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link eu.domibus.configuration.ResponderParty}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link eu.domibus.configuration.ResponderParty}
	 * @return Object type {@link eu.domibus.configuration.ResponderParty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResponderParty readResponderPartyFromString(String in) throws DeserializerException {
		return (ResponderParty) this.xmlToObj(in.getBytes(), ResponderParty.class);
	}	
	
	
	

}
