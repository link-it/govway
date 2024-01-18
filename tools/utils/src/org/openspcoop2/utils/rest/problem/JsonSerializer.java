/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
 * JsonSerializer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonSerializer {

	private boolean generateTypeBlank = false;
	private JSONUtils jsonUtils;
	
	public JsonSerializer() {
		this(false, false);
	}
	public JsonSerializer(boolean prettyPrint) {
		this(prettyPrint, false);
	}
	public JsonSerializer(boolean prettyPrint, boolean generateTypeBlank) {
		this.jsonUtils = JSONUtils.getInstance(prettyPrint);
		this.generateTypeBlank = generateTypeBlank;
	}
	
	public boolean isGenerateTypeBlank() {
		return this.generateTypeBlank;
	}
	public void setGenerateTypeBlank(boolean generateTypeBlank) {
		this.generateTypeBlank = generateTypeBlank;
	}
	
	public String toString(ProblemRFC7807 problem) throws UtilsException {
		ObjectNode jsonProblem = this.toNode(problem);
		return this.jsonUtils.toString(jsonProblem);
	}
	public byte[] toByteArray(ProblemRFC7807 problem) throws UtilsException {
		ObjectNode jsonProblem = toNode(problem);
		return this.jsonUtils.toByteArray(jsonProblem);
	}
	public ObjectNode toNode(ProblemRFC7807 problem) throws UtilsException {
		ObjectNode jsonProblem = this.jsonUtils.newObjectNode();
		
		if(problem.getType()!=null) {
			jsonProblem.put(ProblemConstants.CLAIM_TYPE, problem.getType());
		}
		else if(this.generateTypeBlank) {
			jsonProblem.put(ProblemConstants.CLAIM_TYPE, ProblemConstants.CLAIM_TYPE_BLANK_VALUE);
		}
		
		if(problem.getTitle()!=null) {
			jsonProblem.put(ProblemConstants.CLAIM_TITLE, problem.getTitle());
		}	
		
		if(problem.getStatus()!=null) {
			jsonProblem.put(ProblemConstants.CLAIM_STATUS, problem.getStatus());
		}	
		
		if(problem.getDetail()!=null) {
			jsonProblem.put(ProblemConstants.CLAIM_DETAIL, problem.getDetail());
		}	
		
		if(problem.getInstance()!=null) {
			jsonProblem.put(ProblemConstants.CLAIM_INSTANCE, problem.getInstance());
		}	
		
		if(problem.getCustom()!=null && !problem.getCustom().isEmpty()) {
			Iterator<String> it = problem.getCustom().keySet().iterator();
			while (it.hasNext()) {
				String claimName = (String) it.next();
				Object o = problem.getCustom().get(claimName);
				if(o!=null) {
					if(o instanceof String) {
						jsonProblem.put(claimName, (String) o);
					}
					else if(o instanceof Integer) {
						jsonProblem.put(claimName, (Integer) o);
					}
					else if(o instanceof Long) {
						jsonProblem.put(claimName, (Long) o);
					}
					else if(o instanceof Boolean) {
						jsonProblem.put(claimName, (Boolean) o);
					}
					else {
						throw new UtilsException("Custom claim with type ["+o.getClass().getName()+"] unsupported");
					}
				}
			}
		}
		
		return jsonProblem;
	}
}
