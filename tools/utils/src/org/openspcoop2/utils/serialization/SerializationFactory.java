/**
 * 
 */
package org.openspcoop2.utils.serialization;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $ Rev: 12563 $, $Date$
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
