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
package org.openspcoop2.utils.json;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.openspcoop2.utils.resources.FileSystemUtilities;

import com.fasterxml.jackson.databind.JsonNode;

import net.minidev.json.JSONObject;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class JsonPathTest {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {

		String pattern = "$.store.book[*]";
		JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
		JSONUtils jsonUtils = JSONUtils.getInstance();
		String name = "file.json";
		

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			JSONObject object = JsonPathExpressionEngine.getJSONObject(is);
			
			{
				JsonNode obj = engine.getJsonNodeMatchPattern(object, pattern);
				System.out.println("JsonObject->Nodo:" + obj);
			}

			pattern = "$.store.book[*].available";
			{
				List<Boolean> obj = engine.getBooleanMatchPattern(object, pattern);
				System.out.println("JsonObject->Boolean:" + obj);
			}

			pattern = "$.store.book[*].price";

			{
				List<Number> obj = engine.getNumberMatchPattern(object, pattern);
				System.out.println("JsonObject->Number:" + obj);
			}

			pattern = "$.store.book[*].author";

			{
				List<String> obj = engine.getStringMatchPattern(object, pattern);
				System.out.println("JsonObject->String:" + obj);
			}
			
		}
		
		pattern = "$.store.book[*]";

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			JsonNode matchPattern = engine.getJsonNodeMatchPattern(is, pattern);
			System.out.println("InputStream->Nodo:" + jsonUtils.toString(matchPattern));
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			JsonNode node = jsonUtils.getAsNode(is);
			JsonNode matchPattern = engine.getJsonNodeMatchPattern(node, pattern);
			System.out.println("Nodo->Nodo:" + jsonUtils.toString(matchPattern));
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileSystemUtilities.copy(is, baos);
			JsonNode matchPattern = engine.getJsonNodeMatchPattern(new String(baos.toByteArray()), pattern);
			System.out.println("String->Nodo:" + jsonUtils.toString(matchPattern));
		}
		
		pattern = "$.store.book[*].available";

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			List<Boolean> matchPattern = engine.getBooleanMatchPattern(is, pattern);
			System.out.println("InputStream->Boolean:" + matchPattern);
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			JsonNode node = jsonUtils.getAsNode(is);
			List<Boolean> matchPattern = engine.getBooleanMatchPattern(node, pattern);
			System.out.println("Nodo->Boolean:" + matchPattern);
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileSystemUtilities.copy(is, baos);
			List<Boolean> matchPattern = engine.getBooleanMatchPattern(new String(baos.toByteArray()), pattern);
			System.out.println("String->Boolean:" + matchPattern);
		}
		
		pattern = "$.store.book[*].author";

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			List<String> matchPattern = engine.getStringMatchPattern(is, pattern);
			System.out.println("InputStream->String:" + matchPattern);
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			JsonNode node = jsonUtils.getAsNode(is);
			List<String> matchPattern = engine.getStringMatchPattern(node, pattern);
			System.out.println("Nodo->String:" + matchPattern);
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileSystemUtilities.copy(is, baos);
			List<String> matchPattern = engine.getStringMatchPattern(new String(baos.toByteArray()), pattern);
			System.out.println("String->String:" + matchPattern);
		}

		pattern = "$.store.book[*].price";
		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			List<Number> matchPattern = engine.getNumberMatchPattern(is, pattern);
			System.out.println("InputStream->Number:" + matchPattern);
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			JsonNode node = jsonUtils.getAsNode(is);
			List<Number> matchPattern = engine.getNumberMatchPattern(node, pattern);
			System.out.println("Nodo->Number:" + matchPattern);
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileSystemUtilities.copy(is, baos);
			List<Number> matchPattern = engine.getNumberMatchPattern(new String(baos.toByteArray()), pattern);
			System.out.println("String->Number:" + matchPattern);
		}

		// match pattern
		
		pattern = "$.store.book[*]";
		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			JsonNode matchPattern = (JsonNode) engine.getMatchPattern(is, pattern, JsonPathReturnType.NODE);
			System.out.println("(Match pattern) InputStream->Nodo:" + jsonUtils.toString(matchPattern));
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			JsonNode node = jsonUtils.getAsNode(is);
			JsonNode matchPattern = (JsonNode) engine.getMatchPattern(node, pattern, JsonPathReturnType.NODE);
			System.out.println("(Match pattern) Nodo->Nodo:" + jsonUtils.toString(matchPattern));
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileSystemUtilities.copy(is, baos);
			JsonNode matchPattern = (JsonNode) engine.getMatchPattern(new String(baos.toByteArray()), pattern, JsonPathReturnType.NODE);
			System.out.println("(Match pattern) String->Nodo:" + jsonUtils.toString(matchPattern));
		}
		
		pattern = "$.store.book[*].available";

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			List<Boolean> matchPattern = (List<Boolean>) engine.getMatchPattern(is, pattern, JsonPathReturnType.BOOLEAN);
			System.out.println("(Match pattern) InputStream->Boolean:" + matchPattern);
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			JsonNode node = jsonUtils.getAsNode(is);
			List<Boolean> matchPattern = (List<Boolean>) engine.getMatchPattern(node, pattern, JsonPathReturnType.BOOLEAN);
			System.out.println("(Match pattern) Nodo->Boolean:" + matchPattern);
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileSystemUtilities.copy(is, baos);
			List<Boolean> matchPattern = (List<Boolean>) engine.getMatchPattern(new String(baos.toByteArray()), pattern, JsonPathReturnType.BOOLEAN);
			System.out.println("(Match pattern) String->Boolean:" + matchPattern);
		}
		
		pattern = "$.store.book[*].author";

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			List<String> matchPattern = (List<String>) engine.getMatchPattern(is, pattern, JsonPathReturnType.STRING);
			System.out.println("(Match pattern) InputStream->String:" + matchPattern);
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			JsonNode node = jsonUtils.getAsNode(is);
			List<String> matchPattern = (List<String>) engine.getMatchPattern(node, pattern, JsonPathReturnType.STRING);
			System.out.println("(Match pattern) Nodo->String:" + matchPattern);
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileSystemUtilities.copy(is, baos);
			List<String> matchPattern = (List<String>) engine.getMatchPattern(new String(baos.toByteArray()), pattern, JsonPathReturnType.STRING);
			System.out.println("(Match pattern) String->String:" + matchPattern);
		}

		pattern = "$.store.book[*].price";
		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			List<Number> matchPattern = (List<Number>) engine.getMatchPattern(is, pattern, JsonPathReturnType.NUMBER);
			System.out.println("(Match pattern) InputStream->Number:" + matchPattern);
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			JsonNode node = jsonUtils.getAsNode(is);
			List<Number> matchPattern = (List<Number>) engine.getMatchPattern(node, pattern, JsonPathReturnType.NUMBER);
			System.out.println("(Match pattern) Nodo->Number:" + matchPattern);
		}

		{
			InputStream is = JsonPathTest.class.getResourceAsStream(name);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileSystemUtilities.copy(is, baos);
			List<Number> matchPattern = (List<Number>) engine.getMatchPattern(new String(baos.toByteArray()), pattern, JsonPathReturnType.NUMBER);
			System.out.println("(Match pattern) String->Number:" + matchPattern);
		}


}

}
