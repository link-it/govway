/**
 * 
 */
package org.openspcoop2.utils.serialization;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 11 mag 2018 $
 * 
 */
public class SerializationFactory {

	public enum SERIALIZATION_TYPE {JAVA, JSON_LIB, JSON_JACKSON, XML}
	
	public static ISerializer getSerializer(SERIALIZATION_TYPE type, SerializationConfig config) throws IOException {
		switch(type) {
		case JAVA: return new JavaSerializer();
//		case JAVA_DEFLATER: return new DeflaterSerializer();
		case JSON_JACKSON: return new JsonJacksonSerializer(config);
		case JSON_LIB: return new JSonSerializer(config);
		case XML: return new XMLSerializer(config);
		default: throw new IOException("SERIALIZATION_TYPE ["+type+"] errato"); 
		}
	}
	
	public static IDeserializer getDeserializer(SERIALIZATION_TYPE type, SerializationConfig config) throws IOException {
		switch(type) {
		case JAVA: return new JavaDeserializer();
//		case JAVA_DEFLATER: return new InflaterDeserializer();
		case JSON_JACKSON: return new JsonJacksonDeserializer(config);
		case JSON_LIB: return new JSonDeserializer(config);
		case XML: return new XMLDeserializer(config);
		default: throw new IOException("SERIALIZATION_TYPE ["+type+"] errato"); 
		}
	}
}
