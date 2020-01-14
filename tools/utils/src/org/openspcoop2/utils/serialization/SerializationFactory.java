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
package org.openspcoop2.utils.serialization;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SerializationFactory {

	public enum SERIALIZATION_TYPE {JAVA, JSON_LIB, JSON_JACKSON, XML_JSON_LIB}
	
	public static ISerializer getSerializer(SERIALIZATION_TYPE type, SerializationConfig config) throws IOException {
		switch(type) {
		case JAVA: return new JavaSerializer();
		case JSON_JACKSON: return new JsonJacksonSerializer(config);
		case JSON_LIB: return new JSonSerializer(config);
		case XML_JSON_LIB: return new XMLSerializer(config);
		default: throw new IOException("SERIALIZATION_TYPE ["+type+"] errato"); 
		}
	}
	
	public static IDeserializer getDeserializer(SERIALIZATION_TYPE type, SerializationConfig config) throws IOException {
		switch(type) {
		case JAVA: return new JavaDeserializer();
		case JSON_JACKSON: return new JsonJacksonDeserializer(config);
		case JSON_LIB: return new JSonDeserializer(config);
		case XML_JSON_LIB: return new XMLDeserializer(config);
		default: throw new IOException("SERIALIZATION_TYPE ["+type+"] errato"); 
		}
	}
}
