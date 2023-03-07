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

package org.openspcoop2.utils.rest.problem.test;

import org.openspcoop2.utils.rest.problem.JsonDeserializer;
import org.openspcoop2.utils.rest.problem.JsonSerializer;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807Builder;
import org.openspcoop2.utils.rest.problem.XmlDeserializer;
import org.openspcoop2.utils.rest.problem.XmlSerializer;

/**
 * Test
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProblemTest {

	public static void main(String[] args) throws Exception {
		test();
	}
	
	public static void test() throws Exception {
		
		boolean setType = true;
		ProblemRFC7807Builder builder1 = new ProblemRFC7807Builder(setType);
		ProblemRFC7807Builder builder2 = new ProblemRFC7807Builder(!setType);
		
		boolean prettyPrint = true;
		boolean generateTypeBlank = true;
		boolean omitXMLDeclaration = true;
		JsonSerializer jsonSerializer = new JsonSerializer(prettyPrint);
		JsonSerializer jsonSerializer_generateTypeBlank = new JsonSerializer(prettyPrint,generateTypeBlank);
		XmlSerializer xmlSerializer = new XmlSerializer(prettyPrint);
		XmlSerializer xmlSerializer_generateTypeBlank = new XmlSerializer(prettyPrint,generateTypeBlank);
		JsonDeserializer jsonDeserializer = new JsonDeserializer();
		XmlDeserializer xmlDeserializer = new XmlDeserializer();
		
		ProblemRFC7807 problem1 = builder1.buildProblem(400);
		problem1.setDetail("Esempio di dettaglio del problema");
		problem1.setInstance("NomeIdentificativo");
		problem1.getCustom().put("esempio1", "valore di esempio");
		problem1.getCustom().put("esempio2", 423);
		problem1.getCustom().put("esempio3", true);
		String jsonProblem1 = jsonSerializer.toString(problem1);
		System.out.println("JSON Problem1 ["+jsonProblem1+"]");
		System.out.println("JSON Problem1 ricostruito ["+jsonDeserializer.fromString(jsonProblem1)+"]");
		String xmlProblem1 = xmlSerializer.toString(problem1);
		System.out.println("XML Problem1 ["+xmlProblem1+"]");
		System.out.println("XML Problem1 ricostruito ["+xmlDeserializer.fromString(xmlProblem1)+"]");
		
		ProblemRFC7807 problem2 = builder2.buildProblem(404);
		
		System.out.println("JSON Problem2 ["+jsonSerializer.toString(problem2)+"]");
		System.out.println("XML Problem2 ["+xmlSerializer.toString(problem2, omitXMLDeclaration)+"]");
		
		System.out.println("JSON Problem2 typeBlank ["+jsonSerializer_generateTypeBlank.toString(problem2)+"]");
		System.out.println("XML Problem2 typeBlank ["+xmlSerializer_generateTypeBlank.toString(problem2)+"]");

		
	}

}
