/**
 * 
 */
package org.openspcoop2.utils.serialization.test;

import org.openspcoop2.utils.serialization.Filter;
import org.openspcoop2.utils.serialization.IDeserializer;
import org.openspcoop2.utils.serialization.ISerializer;
import org.openspcoop2.utils.serialization.JsonJacksonDeserializer;
import org.openspcoop2.utils.serialization.JsonJacksonSerializer;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 08 mag 2018 $
 * 
 */
public class SerializerTest {

	public static void main(String[] args) {
		try {
			ISerializer jsonJacksonSerializer = new JsonJacksonSerializer(null);
			IDeserializer jsonJacksonDeserializer = new JsonJacksonDeserializer();

			System.out.println("Test serializzazione e rilettura...");
			testSerializeDeserialize(jsonJacksonSerializer, jsonJacksonDeserializer);
			System.out.println("Test serializzazione e rilettura OK");
			
			Filter filter = new Filter();
			filter.addFilterByName("calendar");
			filter.addFilterByValue(byte[].class);

			ISerializer jsonJacksonSerializerWithFilter = new JsonJacksonSerializer(filter, null, new String[] {"date"});

			System.out.println("Test serializzazione con filtri...");
			testSerializeConFiltro(jsonJacksonSerializerWithFilter);
			System.out.println("Test serializzazione con filtri OK");

			IDeserializer jsonJacksonDeserializerWithFilter = new JsonJacksonDeserializer(new String[] {"date", "calendar"});
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
