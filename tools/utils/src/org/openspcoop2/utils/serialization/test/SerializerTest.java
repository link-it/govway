/**
 * 
 */
package org.openspcoop2.utils.serialization.test;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.openspcoop2.utils.serialization.Filter;
import org.openspcoop2.utils.serialization.IDeserializer;
import org.openspcoop2.utils.serialization.ISerializer;
import org.openspcoop2.utils.serialization.JsonJacksonDeserializer;
import org.openspcoop2.utils.serialization.JsonJacksonSerializer;
import org.openspcoop2.utils.serialization.SerializationConfig;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 08 mag 2018 $
 * 
 */
public class SerializerTest {

	public static void main(String[] args) {
		try {
			ISerializer jsonJacksonSerializer = new JsonJacksonSerializer();
			IDeserializer jsonJacksonDeserializer = new JsonJacksonDeserializer();

			System.out.println("Test serializzazione e rilettura...");
			testSerializeDeserialize(jsonJacksonSerializer, jsonJacksonDeserializer);
			System.out.println("Test serializzazione e rilettura OK");
			
			Filter filter = new Filter();
			filter.addFilterByName("calendar");
			filter.addFilterByValue(byte[].class);

			SerializationConfig config = new SerializationConfig();
			config.setDf(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
			config.setFilter(filter);
			config.setExcludes(Arrays.asList("date"));
			ISerializer jsonJacksonSerializerWithFilter = new JsonJacksonSerializer(config);

			System.out.println("Test serializzazione con filtri...");
			testSerializeConFiltro(jsonJacksonSerializerWithFilter);
			System.out.println("Test serializzazione con filtri OK");

			SerializationConfig config2 = new SerializationConfig();
			config2.setExcludes(Arrays.asList("date", "calendar"));
			IDeserializer jsonJacksonDeserializerWithFilter = new JsonJacksonDeserializer(config2);
			System.out.println("Test deserializzazione con filtri...");
			testDeserializeConFiltro(jsonJacksonSerializer, jsonJacksonDeserializerWithFilter);
			System.out.println("Test deserializzazione con filtri OK");
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private static void testSerializeDeserialize(ISerializer serializer, IDeserializer deserializer) throws Exception {
		ClassToSerialize oggettoIniziale = new ClassToSerialize();
		oggettoIniziale.init();

		String serialized = serializer.getObject(oggettoIniziale);
		ClassToSerialize oggettoDeserializzato = (ClassToSerialize) deserializer.getObject(serialized, ClassToSerialize.class);
		if(!oggettoIniziale.equals(oggettoDeserializzato)) {
			throw new Exception("Oggetto deserializzato non uguale a quello precedentemente serializzato");
		}
	}


	private static void testDeserializeConFiltro(ISerializer serializer, IDeserializer deserializer) throws Exception {
		ClassToSerialize oggettoIniziale = new ClassToSerialize();
		oggettoIniziale.init();

		String serialized = serializer.getObject(oggettoIniziale);
		ClassToSerialize oggettoDeserializzato = (ClassToSerialize) deserializer.getObject(serialized, ClassToSerialize.class);
		if(oggettoDeserializzato.getDate() != null) {
			throw new Exception("Oggetto deserializzato contiene una property [date] che dovrebbe essere stata filtrata");
		}
		if(oggettoDeserializzato.getCalendar() != null) {
			throw new Exception("Oggetto deserializzato contiene una property [calendar] che dovrebbe essere stata filtrata");
		}
	}

	private static void testSerializeConFiltro(ISerializer serializer) throws Exception {
		ClassToSerialize oggettoIniziale = new ClassToSerialize();
		oggettoIniziale.init();

		String serialized = serializer.getObject(oggettoIniziale);
		if(serialized.contains("calendar")) {
			throw new Exception("Oggetto serializzato contiene una property [calendar] che dovrebbe essere filtrata");
		}
		if(serialized.contains("date")) {
			throw new Exception("Oggetto serializzato contiene una property [date] che dovrebbe essere filtrata");
		}
		if(serialized.contains("bytea")) {
			throw new Exception("Oggetto serializzato contiene una property [bytea] che dovrebbe essere filtrata");
		}
	}

}
