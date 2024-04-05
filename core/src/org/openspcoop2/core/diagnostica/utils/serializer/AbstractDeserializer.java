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
package org.openspcoop2.core.diagnostica.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.diagnostica.DominioSoggetto;
import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;
import org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici;
import org.openspcoop2.core.diagnostica.DominioDiagnostico;
import org.openspcoop2.core.diagnostica.Proprieta;
import org.openspcoop2.core.diagnostica.Protocollo;

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
	 Object: dominio-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(String fileName) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(fileName, DominioSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(File file) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(file, DominioSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(InputStream in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in, DominioSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(byte[] in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in, DominioSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggettoFromString(String in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in.getBytes(), DominioSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: messaggio-diagnostico
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiagnostico readMessaggioDiagnostico(String fileName) throws DeserializerException {
		return (MessaggioDiagnostico) this.xmlToObj(fileName, MessaggioDiagnostico.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiagnostico readMessaggioDiagnostico(File file) throws DeserializerException {
		return (MessaggioDiagnostico) this.xmlToObj(file, MessaggioDiagnostico.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiagnostico readMessaggioDiagnostico(InputStream in) throws DeserializerException {
		return (MessaggioDiagnostico) this.xmlToObj(in, MessaggioDiagnostico.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiagnostico readMessaggioDiagnostico(byte[] in) throws DeserializerException {
		return (MessaggioDiagnostico) this.xmlToObj(in, MessaggioDiagnostico.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiagnostico readMessaggioDiagnosticoFromString(String in) throws DeserializerException {
		return (MessaggioDiagnostico) this.xmlToObj(in.getBytes(), MessaggioDiagnostico.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-messaggi-diagnostici
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoMessaggiDiagnostici readElencoMessaggiDiagnostici(String fileName) throws DeserializerException {
		return (ElencoMessaggiDiagnostici) this.xmlToObj(fileName, ElencoMessaggiDiagnostici.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoMessaggiDiagnostici readElencoMessaggiDiagnostici(File file) throws DeserializerException {
		return (ElencoMessaggiDiagnostici) this.xmlToObj(file, ElencoMessaggiDiagnostici.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoMessaggiDiagnostici readElencoMessaggiDiagnostici(InputStream in) throws DeserializerException {
		return (ElencoMessaggiDiagnostici) this.xmlToObj(in, ElencoMessaggiDiagnostici.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoMessaggiDiagnostici readElencoMessaggiDiagnostici(byte[] in) throws DeserializerException {
		return (ElencoMessaggiDiagnostici) this.xmlToObj(in, ElencoMessaggiDiagnostici.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoMessaggiDiagnostici readElencoMessaggiDiagnosticiFromString(String in) throws DeserializerException {
		return (ElencoMessaggiDiagnostici) this.xmlToObj(in.getBytes(), ElencoMessaggiDiagnostici.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dominio-diagnostico
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioDiagnostico readDominioDiagnostico(String fileName) throws DeserializerException {
		return (DominioDiagnostico) this.xmlToObj(fileName, DominioDiagnostico.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioDiagnostico readDominioDiagnostico(File file) throws DeserializerException {
		return (DominioDiagnostico) this.xmlToObj(file, DominioDiagnostico.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioDiagnostico readDominioDiagnostico(InputStream in) throws DeserializerException {
		return (DominioDiagnostico) this.xmlToObj(in, DominioDiagnostico.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioDiagnostico readDominioDiagnostico(byte[] in) throws DeserializerException {
		return (DominioDiagnostico) this.xmlToObj(in, DominioDiagnostico.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioDiagnostico readDominioDiagnosticoFromString(String in) throws DeserializerException {
		return (DominioDiagnostico) this.xmlToObj(in.getBytes(), DominioDiagnostico.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: proprieta
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(String fileName) throws DeserializerException {
		return (Proprieta) this.xmlToObj(fileName, Proprieta.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(File file) throws DeserializerException {
		return (Proprieta) this.xmlToObj(file, Proprieta.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(InputStream in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in, Proprieta.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(byte[] in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in, Proprieta.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprietaFromString(String in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in.getBytes(), Proprieta.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: protocollo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocollo(String fileName) throws DeserializerException {
		return (Protocollo) this.xmlToObj(fileName, Protocollo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocollo(File file) throws DeserializerException {
		return (Protocollo) this.xmlToObj(file, Protocollo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocollo(InputStream in) throws DeserializerException {
		return (Protocollo) this.xmlToObj(in, Protocollo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocollo(byte[] in) throws DeserializerException {
		return (Protocollo) this.xmlToObj(in, Protocollo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocolloFromString(String in) throws DeserializerException {
		return (Protocollo) this.xmlToObj(in.getBytes(), Protocollo.class);
	}	
	
	
	

}
