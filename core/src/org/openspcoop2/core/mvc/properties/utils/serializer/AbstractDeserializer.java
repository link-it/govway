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
package org.openspcoop2.core.mvc.properties.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.mvc.properties.Defined;
import org.openspcoop2.core.mvc.properties.Conditions;
import org.openspcoop2.core.mvc.properties.Subsection;
import org.openspcoop2.core.mvc.properties.Item;
import org.openspcoop2.core.mvc.properties.Selected;
import org.openspcoop2.core.mvc.properties.ItemValue;
import org.openspcoop2.core.mvc.properties.ItemValues;
import org.openspcoop2.core.mvc.properties.Equals;
import org.openspcoop2.core.mvc.properties.Compatibility;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.Properties;
import org.openspcoop2.core.mvc.properties.Section;
import org.openspcoop2.core.mvc.properties.Property;
import org.openspcoop2.core.mvc.properties.Tags;
import org.openspcoop2.core.mvc.properties.Collection;
import org.openspcoop2.core.mvc.properties.Condition;

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
	 Object: defined
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Defined readDefined(String fileName) throws DeserializerException {
		return (Defined) this.xmlToObj(fileName, Defined.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Defined readDefined(File file) throws DeserializerException {
		return (Defined) this.xmlToObj(file, Defined.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Defined readDefined(InputStream in) throws DeserializerException {
		return (Defined) this.xmlToObj(in, Defined.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Defined readDefined(byte[] in) throws DeserializerException {
		return (Defined) this.xmlToObj(in, Defined.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Defined readDefinedFromString(String in) throws DeserializerException {
		return (Defined) this.xmlToObj(in.getBytes(), Defined.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: conditions
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Conditions readConditions(String fileName) throws DeserializerException {
		return (Conditions) this.xmlToObj(fileName, Conditions.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Conditions readConditions(File file) throws DeserializerException {
		return (Conditions) this.xmlToObj(file, Conditions.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Conditions readConditions(InputStream in) throws DeserializerException {
		return (Conditions) this.xmlToObj(in, Conditions.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Conditions readConditions(byte[] in) throws DeserializerException {
		return (Conditions) this.xmlToObj(in, Conditions.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Conditions readConditionsFromString(String in) throws DeserializerException {
		return (Conditions) this.xmlToObj(in.getBytes(), Conditions.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: subsection
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Subsection readSubsection(String fileName) throws DeserializerException {
		return (Subsection) this.xmlToObj(fileName, Subsection.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Subsection readSubsection(File file) throws DeserializerException {
		return (Subsection) this.xmlToObj(file, Subsection.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Subsection readSubsection(InputStream in) throws DeserializerException {
		return (Subsection) this.xmlToObj(in, Subsection.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Subsection readSubsection(byte[] in) throws DeserializerException {
		return (Subsection) this.xmlToObj(in, Subsection.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Subsection readSubsectionFromString(String in) throws DeserializerException {
		return (Subsection) this.xmlToObj(in.getBytes(), Subsection.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: item
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Item readItem(String fileName) throws DeserializerException {
		return (Item) this.xmlToObj(fileName, Item.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Item readItem(File file) throws DeserializerException {
		return (Item) this.xmlToObj(file, Item.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Item readItem(InputStream in) throws DeserializerException {
		return (Item) this.xmlToObj(in, Item.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Item readItem(byte[] in) throws DeserializerException {
		return (Item) this.xmlToObj(in, Item.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Item}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Item readItemFromString(String in) throws DeserializerException {
		return (Item) this.xmlToObj(in.getBytes(), Item.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: selected
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Selected readSelected(String fileName) throws DeserializerException {
		return (Selected) this.xmlToObj(fileName, Selected.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Selected readSelected(File file) throws DeserializerException {
		return (Selected) this.xmlToObj(file, Selected.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Selected readSelected(InputStream in) throws DeserializerException {
		return (Selected) this.xmlToObj(in, Selected.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Selected readSelected(byte[] in) throws DeserializerException {
		return (Selected) this.xmlToObj(in, Selected.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Selected readSelectedFromString(String in) throws DeserializerException {
		return (Selected) this.xmlToObj(in.getBytes(), Selected.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: itemValue
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ItemValue readItemValue(String fileName) throws DeserializerException {
		return (ItemValue) this.xmlToObj(fileName, ItemValue.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ItemValue readItemValue(File file) throws DeserializerException {
		return (ItemValue) this.xmlToObj(file, ItemValue.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ItemValue readItemValue(InputStream in) throws DeserializerException {
		return (ItemValue) this.xmlToObj(in, ItemValue.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ItemValue readItemValue(byte[] in) throws DeserializerException {
		return (ItemValue) this.xmlToObj(in, ItemValue.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ItemValue readItemValueFromString(String in) throws DeserializerException {
		return (ItemValue) this.xmlToObj(in.getBytes(), ItemValue.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: itemValues
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ItemValues readItemValues(String fileName) throws DeserializerException {
		return (ItemValues) this.xmlToObj(fileName, ItemValues.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ItemValues readItemValues(File file) throws DeserializerException {
		return (ItemValues) this.xmlToObj(file, ItemValues.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ItemValues readItemValues(InputStream in) throws DeserializerException {
		return (ItemValues) this.xmlToObj(in, ItemValues.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ItemValues readItemValues(byte[] in) throws DeserializerException {
		return (ItemValues) this.xmlToObj(in, ItemValues.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ItemValues readItemValuesFromString(String in) throws DeserializerException {
		return (ItemValues) this.xmlToObj(in.getBytes(), ItemValues.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: equals
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Equals readEquals(String fileName) throws DeserializerException {
		return (Equals) this.xmlToObj(fileName, Equals.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Equals readEquals(File file) throws DeserializerException {
		return (Equals) this.xmlToObj(file, Equals.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Equals readEquals(InputStream in) throws DeserializerException {
		return (Equals) this.xmlToObj(in, Equals.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Equals readEquals(byte[] in) throws DeserializerException {
		return (Equals) this.xmlToObj(in, Equals.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Equals readEqualsFromString(String in) throws DeserializerException {
		return (Equals) this.xmlToObj(in.getBytes(), Equals.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: compatibility
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Compatibility readCompatibility(String fileName) throws DeserializerException {
		return (Compatibility) this.xmlToObj(fileName, Compatibility.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Compatibility readCompatibility(File file) throws DeserializerException {
		return (Compatibility) this.xmlToObj(file, Compatibility.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Compatibility readCompatibility(InputStream in) throws DeserializerException {
		return (Compatibility) this.xmlToObj(in, Compatibility.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Compatibility readCompatibility(byte[] in) throws DeserializerException {
		return (Compatibility) this.xmlToObj(in, Compatibility.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Compatibility readCompatibilityFromString(String in) throws DeserializerException {
		return (Compatibility) this.xmlToObj(in.getBytes(), Compatibility.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: config
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Config readConfig(String fileName) throws DeserializerException {
		return (Config) this.xmlToObj(fileName, Config.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Config readConfig(File file) throws DeserializerException {
		return (Config) this.xmlToObj(file, Config.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Config readConfig(InputStream in) throws DeserializerException {
		return (Config) this.xmlToObj(in, Config.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Config readConfig(byte[] in) throws DeserializerException {
		return (Config) this.xmlToObj(in, Config.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Config}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Config readConfigFromString(String in) throws DeserializerException {
		return (Config) this.xmlToObj(in.getBytes(), Config.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: properties
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Properties readProperties(String fileName) throws DeserializerException {
		return (Properties) this.xmlToObj(fileName, Properties.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Properties readProperties(File file) throws DeserializerException {
		return (Properties) this.xmlToObj(file, Properties.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Properties readProperties(InputStream in) throws DeserializerException {
		return (Properties) this.xmlToObj(in, Properties.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Properties readProperties(byte[] in) throws DeserializerException {
		return (Properties) this.xmlToObj(in, Properties.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Properties readPropertiesFromString(String in) throws DeserializerException {
		return (Properties) this.xmlToObj(in.getBytes(), Properties.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: section
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Section readSection(String fileName) throws DeserializerException {
		return (Section) this.xmlToObj(fileName, Section.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Section readSection(File file) throws DeserializerException {
		return (Section) this.xmlToObj(file, Section.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Section readSection(InputStream in) throws DeserializerException {
		return (Section) this.xmlToObj(in, Section.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Section readSection(byte[] in) throws DeserializerException {
		return (Section) this.xmlToObj(in, Section.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Section}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Section readSectionFromString(String in) throws DeserializerException {
		return (Section) this.xmlToObj(in.getBytes(), Section.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: property
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(String fileName) throws DeserializerException {
		return (Property) this.xmlToObj(fileName, Property.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(File file) throws DeserializerException {
		return (Property) this.xmlToObj(file, Property.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(InputStream in) throws DeserializerException {
		return (Property) this.xmlToObj(in, Property.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(byte[] in) throws DeserializerException {
		return (Property) this.xmlToObj(in, Property.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readPropertyFromString(String in) throws DeserializerException {
		return (Property) this.xmlToObj(in.getBytes(), Property.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: tags
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tags readTags(String fileName) throws DeserializerException {
		return (Tags) this.xmlToObj(fileName, Tags.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tags readTags(File file) throws DeserializerException {
		return (Tags) this.xmlToObj(file, Tags.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tags readTags(InputStream in) throws DeserializerException {
		return (Tags) this.xmlToObj(in, Tags.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tags readTags(byte[] in) throws DeserializerException {
		return (Tags) this.xmlToObj(in, Tags.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tags readTagsFromString(String in) throws DeserializerException {
		return (Tags) this.xmlToObj(in.getBytes(), Tags.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: collection
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Collection readCollection(String fileName) throws DeserializerException {
		return (Collection) this.xmlToObj(fileName, Collection.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Collection readCollection(File file) throws DeserializerException {
		return (Collection) this.xmlToObj(file, Collection.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Collection readCollection(InputStream in) throws DeserializerException {
		return (Collection) this.xmlToObj(in, Collection.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Collection readCollection(byte[] in) throws DeserializerException {
		return (Collection) this.xmlToObj(in, Collection.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Collection readCollectionFromString(String in) throws DeserializerException {
		return (Collection) this.xmlToObj(in.getBytes(), Collection.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: condition
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Condition readCondition(String fileName) throws DeserializerException {
		return (Condition) this.xmlToObj(fileName, Condition.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Condition readCondition(File file) throws DeserializerException {
		return (Condition) this.xmlToObj(file, Condition.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Condition readCondition(InputStream in) throws DeserializerException {
		return (Condition) this.xmlToObj(in, Condition.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Condition readCondition(byte[] in) throws DeserializerException {
		return (Condition) this.xmlToObj(in, Condition.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * @return Object type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Condition readConditionFromString(String in) throws DeserializerException {
		return (Condition) this.xmlToObj(in.getBytes(), Condition.class);
	}	
	
	
	

}
