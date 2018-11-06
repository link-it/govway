/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.rest.problem;

import java.util.Iterator;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JsonDeserializer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonDeserializer extends AbstractDeserializer {

	private JSONUtils jsonUtils;
	
	public JsonDeserializer() {
		this(false);
	}
	public JsonDeserializer(boolean generateTypeBlank) {
		super(generateTypeBlank);
		this.jsonUtils = JSONUtils.getInstance();
	}
	
	public ProblemRFC7807 fromString(String problemString) throws UtilsException {
		ObjectNode problemNode = (ObjectNode) this.jsonUtils.getAsNode(problemString);
		return this.fromNode(problemNode);
	}
	public ProblemRFC7807 fromByteArray(byte[] problemByteArray) throws UtilsException {
		ObjectNode problemNode = (ObjectNode) this.jsonUtils.getAsNode(problemByteArray);
		return this.fromNode(problemNode);
	}
	public ProblemRFC7807 fromNode(ObjectNode problemNode) throws UtilsException {
		
		ProblemRFC7807 problem = new ProblemRFC7807();
		
		Iterator<String> it = problemNode.fieldNames();
		while (it.hasNext()) {
			String name = (String) it.next();
			Object value = problemNode.get(name);
			
			super.set(problem, name, value);
		}

		return problem;
	}
	
}
