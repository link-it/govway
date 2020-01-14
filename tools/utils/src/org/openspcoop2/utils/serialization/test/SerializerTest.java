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
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SerializerTest {

	public static void main(String[] args) {
		try {
			ISerializer jsonJacksonSerializer = new JsonJacksonSerializer();
			IDeserializer jsonJacksonDeserializer = new JsonJacksonDeserializer();

			System.out.println("Test serializzazione e rilettura...");
			testSerializeDeserialize(jsonJacksonSerializer, jsonJacksonDeserializer, true);
			System.out.println("Test serializzazione e rilettura OK");

			SerializationConfig configEnumNoString = new SerializationConfig();
			configEnumNoString.setSerializeEnumAsString(false);
			
			ISerializer jsonJacksonSerializerEnumNoString = new JsonJacksonSerializer(configEnumNoString);
			IDeserializer jsonJacksonDeserializerEnumNoString = new JsonJacksonDeserializer(configEnumNoString);

			System.out.println("Test serializzazione e rilettura con enum serializzate non come stringa...");
			testSerializeDeserialize(jsonJacksonSerializerEnumNoString, jsonJacksonDeserializerEnumNoString, false);
			System.out.println("Test serializzazione e rilettura con enum serializzate non come stringa OK");
			
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

	private static void testSerializeDeserialize(ISerializer serializer, IDeserializer deserializer, boolean serializeEnumAsString) throws Exception {
		ClassToSerialize oggettoIniziale = new ClassToSerialize();
		oggettoIniziale.init();

		String serialized = serializer.getObject(oggettoIniziale);
		
		if(serializeEnumAsString) {
			if(!serialized.contains(oggettoIniziale.getMyEnum().toString())) {
				throw new Exception("Oggetto non correttamente serializzato: " + serialized);
			}
		} else {
			if(!serialized.contains(oggettoIniziale.getMyEnum().name())) {
				throw new Exception("Oggetto non correttamente serializzato: " + serialized);
			}
		}
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
