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

package org.openspcoop2.utils.rest.problem;

import org.openspcoop2.utils.UtilsException;

/**
 * XmlDeserializer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractDeserializer {

	protected boolean generateTypeBlank = false;
	
	public AbstractDeserializer() {
		this(false);
	}
	public AbstractDeserializer(boolean generateTypeBlank) {
		this.generateTypeBlank = generateTypeBlank;
	}
	
	public boolean isGenerateTypeBlank() {
		return this.generateTypeBlank;
	}
	public void setGenerateTypeBlank(boolean generateTypeBlank) {
		this.generateTypeBlank = generateTypeBlank;
	}
	
	protected void set(ProblemRFC7807 problem, String name, Object value, boolean throwExceptionIfUnsupportedCustomClaim) throws UtilsException {
		
		if(ProblemConstants.CLAIM_TYPE.equals(name)) {
			String type = this.getAsString(value);
			if("about:blank".equals(type)) {
				if(this.generateTypeBlank) {
					problem.setType(type);
				}
			}
			else {
				problem.setType(type);
			}
		}
		else if(ProblemConstants.CLAIM_TITLE.equals(name)) {
			problem.setTitle(this.getAsString(value));
		}
		else if(ProblemConstants.CLAIM_STATUS.equals(name)) {
			problem.setStatus(this.getAsInt(value));
		}
		else if(ProblemConstants.CLAIM_DETAIL.equals(name)) {
			problem.setDetail(this.getAsString(value));
		}
		else if(ProblemConstants.CLAIM_INSTANCE.equals(name)) {
			problem.setInstance(this.getAsString(value));
		}
		else {
			if(value!=null) {
				if(value instanceof String) {
					problem.getCustom().put(name, (String) value);
				}
				else if(value instanceof com.fasterxml.jackson.databind.node.TextNode) {
					com.fasterxml.jackson.databind.node.TextNode textNode = (com.fasterxml.jackson.databind.node.TextNode) value;
					problem.getCustom().put(name, (textNode.asText()));
				}
				else if(value instanceof Integer) {
					problem.getCustom().put(name, (Integer) value);
				}
				else if(value instanceof com.fasterxml.jackson.databind.node.IntNode) {
					com.fasterxml.jackson.databind.node.IntNode intNode = (com.fasterxml.jackson.databind.node.IntNode) value;
					problem.getCustom().put(name, intNode.asInt());
				}
				else if(value instanceof Long) {
					problem.getCustom().put(name, (Long) value);
				}
				else if(value instanceof com.fasterxml.jackson.databind.node.LongNode) {
					com.fasterxml.jackson.databind.node.LongNode longNode = (com.fasterxml.jackson.databind.node.LongNode) value;
					problem.getCustom().put(name, longNode.asInt());
				}
				else if(value instanceof Boolean) {
					problem.getCustom().put(name, (Boolean) value);
				}
				else if(value instanceof com.fasterxml.jackson.databind.node.BooleanNode) {
					com.fasterxml.jackson.databind.node.BooleanNode booleanNode = (com.fasterxml.jackson.databind.node.BooleanNode) value;
					problem.getCustom().put(name, booleanNode.asBoolean());
				}
				else {
					if(throwExceptionIfUnsupportedCustomClaim) {
						throw new UtilsException("Custom claim with type ["+value.getClass().getName()+"] unsupported");
					}
				}
			}
			else {
				problem.getCustom().put(name, null);
			}
		}

	}
	
	private String getAsString(Object value) {
		if(value!=null) {
			if(value instanceof String) {
				return (String)value;
			}
			else if(value instanceof com.fasterxml.jackson.databind.node.TextNode) {
				com.fasterxml.jackson.databind.node.TextNode text = (com.fasterxml.jackson.databind.node.TextNode) value;
				return text.asText();
			}
			else {
				return value.toString();
			}
		}
		return null;
	}
	private Integer getAsInt(Object value) {
		if(value!=null) {
			if(value instanceof Integer) {
				return (Integer)value;
			}
			else if(value instanceof com.fasterxml.jackson.databind.node.IntNode) {
				com.fasterxml.jackson.databind.node.IntNode intNode = (com.fasterxml.jackson.databind.node.IntNode) value;
				return intNode.asInt();
			}
			else if(value instanceof Long) {
				return ((Long)value).intValue();
			}
			else if(value instanceof com.fasterxml.jackson.databind.node.LongNode) {
				com.fasterxml.jackson.databind.node.LongNode longNode = (com.fasterxml.jackson.databind.node.LongNode) value;
				return (int) longNode.asLong();
			}
			else {
				return Integer.parseInt(value.toString());
			}
		}
		return null;
	}
}
