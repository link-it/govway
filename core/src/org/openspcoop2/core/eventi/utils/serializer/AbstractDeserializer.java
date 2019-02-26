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
package org.openspcoop2.core.eventi.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.DatiEventoGenerico;

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
	 Object: evento
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eventi.Evento}
	 * @return Object type {@link org.openspcoop2.core.eventi.Evento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Evento readEvento(String fileName) throws DeserializerException {
		return (Evento) this.xmlToObj(fileName, Evento.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eventi.Evento}
	 * @return Object type {@link org.openspcoop2.core.eventi.Evento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Evento readEvento(File file) throws DeserializerException {
		return (Evento) this.xmlToObj(file, Evento.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eventi.Evento}
	 * @return Object type {@link org.openspcoop2.core.eventi.Evento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Evento readEvento(InputStream in) throws DeserializerException {
		return (Evento) this.xmlToObj(in, Evento.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eventi.Evento}
	 * @return Object type {@link org.openspcoop2.core.eventi.Evento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Evento readEvento(byte[] in) throws DeserializerException {
		return (Evento) this.xmlToObj(in, Evento.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eventi.Evento}
	 * @return Object type {@link org.openspcoop2.core.eventi.Evento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Evento readEventoFromString(String in) throws DeserializerException {
		return (Evento) this.xmlToObj(in.getBytes(), Evento.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dati-evento-generico
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * @return Object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiEventoGenerico readDatiEventoGenerico(String fileName) throws DeserializerException {
		return (DatiEventoGenerico) this.xmlToObj(fileName, DatiEventoGenerico.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * @return Object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiEventoGenerico readDatiEventoGenerico(File file) throws DeserializerException {
		return (DatiEventoGenerico) this.xmlToObj(file, DatiEventoGenerico.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * @return Object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiEventoGenerico readDatiEventoGenerico(InputStream in) throws DeserializerException {
		return (DatiEventoGenerico) this.xmlToObj(in, DatiEventoGenerico.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * @return Object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiEventoGenerico readDatiEventoGenerico(byte[] in) throws DeserializerException {
		return (DatiEventoGenerico) this.xmlToObj(in, DatiEventoGenerico.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * @return Object type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiEventoGenerico readDatiEventoGenericoFromString(String in) throws DeserializerException {
		return (DatiEventoGenerico) this.xmlToObj(in.getBytes(), DatiEventoGenerico.class);
	}	
	
	
	

}
