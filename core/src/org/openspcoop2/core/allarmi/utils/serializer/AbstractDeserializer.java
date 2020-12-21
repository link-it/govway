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
package org.openspcoop2.core.allarmi.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.AllarmeMail;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeScript;
import org.openspcoop2.core.allarmi.AllarmeFiltro;
import org.openspcoop2.core.allarmi.AllarmeRaggruppamento;
import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.core.allarmi.ElencoAllarmi;
import org.openspcoop2.core.allarmi.ElencoIdAllarmi;

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
	 Object: id-allarme
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * @return Object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAllarme readIdAllarme(String fileName) throws DeserializerException {
		return (IdAllarme) this.xmlToObj(fileName, IdAllarme.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * @return Object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAllarme readIdAllarme(File file) throws DeserializerException {
		return (IdAllarme) this.xmlToObj(file, IdAllarme.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * @return Object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAllarme readIdAllarme(InputStream in) throws DeserializerException {
		return (IdAllarme) this.xmlToObj(in, IdAllarme.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * @return Object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAllarme readIdAllarme(byte[] in) throws DeserializerException {
		return (IdAllarme) this.xmlToObj(in, IdAllarme.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * @return Object type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAllarme readIdAllarmeFromString(String in) throws DeserializerException {
		return (IdAllarme) this.xmlToObj(in.getBytes(), IdAllarme.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: allarme-history
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeHistory readAllarmeHistory(String fileName) throws DeserializerException {
		return (AllarmeHistory) this.xmlToObj(fileName, AllarmeHistory.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeHistory readAllarmeHistory(File file) throws DeserializerException {
		return (AllarmeHistory) this.xmlToObj(file, AllarmeHistory.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeHistory readAllarmeHistory(InputStream in) throws DeserializerException {
		return (AllarmeHistory) this.xmlToObj(in, AllarmeHistory.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeHistory readAllarmeHistory(byte[] in) throws DeserializerException {
		return (AllarmeHistory) this.xmlToObj(in, AllarmeHistory.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeHistory readAllarmeHistoryFromString(String in) throws DeserializerException {
		return (AllarmeHistory) this.xmlToObj(in.getBytes(), AllarmeHistory.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: allarme-mail
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeMail readAllarmeMail(String fileName) throws DeserializerException {
		return (AllarmeMail) this.xmlToObj(fileName, AllarmeMail.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeMail readAllarmeMail(File file) throws DeserializerException {
		return (AllarmeMail) this.xmlToObj(file, AllarmeMail.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeMail readAllarmeMail(InputStream in) throws DeserializerException {
		return (AllarmeMail) this.xmlToObj(in, AllarmeMail.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeMail readAllarmeMail(byte[] in) throws DeserializerException {
		return (AllarmeMail) this.xmlToObj(in, AllarmeMail.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeMail readAllarmeMailFromString(String in) throws DeserializerException {
		return (AllarmeMail) this.xmlToObj(in.getBytes(), AllarmeMail.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: allarme
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * @return Object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allarme readAllarme(String fileName) throws DeserializerException {
		return (Allarme) this.xmlToObj(fileName, Allarme.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * @return Object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allarme readAllarme(File file) throws DeserializerException {
		return (Allarme) this.xmlToObj(file, Allarme.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * @return Object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allarme readAllarme(InputStream in) throws DeserializerException {
		return (Allarme) this.xmlToObj(in, Allarme.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * @return Object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allarme readAllarme(byte[] in) throws DeserializerException {
		return (Allarme) this.xmlToObj(in, Allarme.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * @return Object type {@link org.openspcoop2.core.allarmi.Allarme}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allarme readAllarmeFromString(String in) throws DeserializerException {
		return (Allarme) this.xmlToObj(in.getBytes(), Allarme.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: allarme-script
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeScript readAllarmeScript(String fileName) throws DeserializerException {
		return (AllarmeScript) this.xmlToObj(fileName, AllarmeScript.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeScript readAllarmeScript(File file) throws DeserializerException {
		return (AllarmeScript) this.xmlToObj(file, AllarmeScript.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeScript readAllarmeScript(InputStream in) throws DeserializerException {
		return (AllarmeScript) this.xmlToObj(in, AllarmeScript.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeScript readAllarmeScript(byte[] in) throws DeserializerException {
		return (AllarmeScript) this.xmlToObj(in, AllarmeScript.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeScript readAllarmeScriptFromString(String in) throws DeserializerException {
		return (AllarmeScript) this.xmlToObj(in.getBytes(), AllarmeScript.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: allarme-filtro
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeFiltro readAllarmeFiltro(String fileName) throws DeserializerException {
		return (AllarmeFiltro) this.xmlToObj(fileName, AllarmeFiltro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeFiltro readAllarmeFiltro(File file) throws DeserializerException {
		return (AllarmeFiltro) this.xmlToObj(file, AllarmeFiltro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeFiltro readAllarmeFiltro(InputStream in) throws DeserializerException {
		return (AllarmeFiltro) this.xmlToObj(in, AllarmeFiltro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeFiltro readAllarmeFiltro(byte[] in) throws DeserializerException {
		return (AllarmeFiltro) this.xmlToObj(in, AllarmeFiltro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeFiltro readAllarmeFiltroFromString(String in) throws DeserializerException {
		return (AllarmeFiltro) this.xmlToObj(in.getBytes(), AllarmeFiltro.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: allarme-raggruppamento
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeRaggruppamento readAllarmeRaggruppamento(String fileName) throws DeserializerException {
		return (AllarmeRaggruppamento) this.xmlToObj(fileName, AllarmeRaggruppamento.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeRaggruppamento readAllarmeRaggruppamento(File file) throws DeserializerException {
		return (AllarmeRaggruppamento) this.xmlToObj(file, AllarmeRaggruppamento.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeRaggruppamento readAllarmeRaggruppamento(InputStream in) throws DeserializerException {
		return (AllarmeRaggruppamento) this.xmlToObj(in, AllarmeRaggruppamento.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeRaggruppamento readAllarmeRaggruppamento(byte[] in) throws DeserializerException {
		return (AllarmeRaggruppamento) this.xmlToObj(in, AllarmeRaggruppamento.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeRaggruppamento readAllarmeRaggruppamentoFromString(String in) throws DeserializerException {
		return (AllarmeRaggruppamento) this.xmlToObj(in.getBytes(), AllarmeRaggruppamento.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: allarme-parametro
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeParametro readAllarmeParametro(String fileName) throws DeserializerException {
		return (AllarmeParametro) this.xmlToObj(fileName, AllarmeParametro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeParametro readAllarmeParametro(File file) throws DeserializerException {
		return (AllarmeParametro) this.xmlToObj(file, AllarmeParametro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeParametro readAllarmeParametro(InputStream in) throws DeserializerException {
		return (AllarmeParametro) this.xmlToObj(in, AllarmeParametro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeParametro readAllarmeParametro(byte[] in) throws DeserializerException {
		return (AllarmeParametro) this.xmlToObj(in, AllarmeParametro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * @return Object type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllarmeParametro readAllarmeParametroFromString(String in) throws DeserializerException {
		return (AllarmeParametro) this.xmlToObj(in.getBytes(), AllarmeParametro.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-allarmi
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * @return Object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoAllarmi readElencoAllarmi(String fileName) throws DeserializerException {
		return (ElencoAllarmi) this.xmlToObj(fileName, ElencoAllarmi.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * @return Object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoAllarmi readElencoAllarmi(File file) throws DeserializerException {
		return (ElencoAllarmi) this.xmlToObj(file, ElencoAllarmi.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * @return Object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoAllarmi readElencoAllarmi(InputStream in) throws DeserializerException {
		return (ElencoAllarmi) this.xmlToObj(in, ElencoAllarmi.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * @return Object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoAllarmi readElencoAllarmi(byte[] in) throws DeserializerException {
		return (ElencoAllarmi) this.xmlToObj(in, ElencoAllarmi.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * @return Object type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoAllarmi readElencoAllarmiFromString(String in) throws DeserializerException {
		return (ElencoAllarmi) this.xmlToObj(in.getBytes(), ElencoAllarmi.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-id-allarmi
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * @return Object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdAllarmi readElencoIdAllarmi(String fileName) throws DeserializerException {
		return (ElencoIdAllarmi) this.xmlToObj(fileName, ElencoIdAllarmi.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * @return Object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdAllarmi readElencoIdAllarmi(File file) throws DeserializerException {
		return (ElencoIdAllarmi) this.xmlToObj(file, ElencoIdAllarmi.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * @return Object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdAllarmi readElencoIdAllarmi(InputStream in) throws DeserializerException {
		return (ElencoIdAllarmi) this.xmlToObj(in, ElencoIdAllarmi.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * @return Object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdAllarmi readElencoIdAllarmi(byte[] in) throws DeserializerException {
		return (ElencoIdAllarmi) this.xmlToObj(in, ElencoIdAllarmi.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * @return Object type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdAllarmi readElencoIdAllarmiFromString(String in) throws DeserializerException {
		return (ElencoIdAllarmi) this.xmlToObj(in.getBytes(), ElencoIdAllarmi.class);
	}	
	
	
	

}
