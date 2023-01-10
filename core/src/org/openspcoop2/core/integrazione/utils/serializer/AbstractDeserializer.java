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
package org.openspcoop2.core.integrazione.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.integrazione.EsitoRichiesta;

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
	 Object: esito-richiesta
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * @return Object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EsitoRichiesta readEsitoRichiesta(String fileName) throws DeserializerException {
		return (EsitoRichiesta) this.xmlToObj(fileName, EsitoRichiesta.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * @return Object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EsitoRichiesta readEsitoRichiesta(File file) throws DeserializerException {
		return (EsitoRichiesta) this.xmlToObj(file, EsitoRichiesta.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * @return Object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EsitoRichiesta readEsitoRichiesta(InputStream in) throws DeserializerException {
		return (EsitoRichiesta) this.xmlToObj(in, EsitoRichiesta.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * @return Object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EsitoRichiesta readEsitoRichiesta(byte[] in) throws DeserializerException {
		return (EsitoRichiesta) this.xmlToObj(in, EsitoRichiesta.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * @return Object type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EsitoRichiesta readEsitoRichiestaFromString(String in) throws DeserializerException {
		return (EsitoRichiesta) this.xmlToObj(in.getBytes(), EsitoRichiesta.class);
	}	
	
	
	

}
