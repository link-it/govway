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

package org.openspcoop2.utils.rest.problem;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XmlSerializer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XmlSerializer {
	
	private boolean generateTypeBlank = false;
	private XMLUtils xmlUtils;
	private boolean prettyPrint;
	
	public XmlSerializer() {
		this(false, false);
	}
	public XmlSerializer(boolean prettyPrint) {
		this(prettyPrint, false);
	}
	public XmlSerializer(boolean prettyPrint, boolean generateTypeBlank) {
		this.xmlUtils = XMLUtils.getInstance();
		this.generateTypeBlank = generateTypeBlank;
		this.prettyPrint = prettyPrint;
	}
	
	public boolean isGenerateTypeBlank() {
		return this.generateTypeBlank;
	}
	public void setGenerateTypeBlank(boolean generateTypeBlank) {
		this.generateTypeBlank = generateTypeBlank;
	}
	
	public String toString(ProblemRFC7807 problem) throws UtilsException {
		return this.toString(problem, false);
	}
	public String toString(ProblemRFC7807 problem, boolean omitXMLDeclaration) throws UtilsException {
		Element xmlProblem = this.toNode(problem);
		if(this.prettyPrint) {
			try {
				return PrettyPrintXMLUtils.prettyPrintWithTrAX(xmlProblem, omitXMLDeclaration);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		else {
			try {
				return this.xmlUtils.toString(xmlProblem, omitXMLDeclaration);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	
	public byte[] toByteArray(ProblemRFC7807 problem) throws UtilsException {
		return this.toByteArray(problem, false);
	}
	public byte[] toByteArray(ProblemRFC7807 problem, boolean omitXMLDeclaration) throws UtilsException {
		Element xmlProblem = toNode(problem);
		if(this.prettyPrint) {
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrettyPrintXMLUtils.prettyPrintWithTrAX(xmlProblem, bout, omitXMLDeclaration);
				bout.flush();
				bout.close();
				return bout.toByteArray();
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		else {
			try {
				return this.xmlUtils.toByteArray(xmlProblem, omitXMLDeclaration);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public Element toNode(ProblemRFC7807 problem) throws UtilsException {
		Document document = null;
		try {
			document = this.xmlUtils.newDocument();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		Element xmlProblem = document.createElementNS(ProblemConstants.XML_PROBLEM_DETAILS_RFC_7807_NAMESPACE, 
				ProblemConstants.XML_PROBLEM_DETAILS_RFC_7807_LOCAL_NAME);
		
		if(problem.getType()!=null) {
			Element child = document.createElement(ProblemConstants.CLAIM_TYPE);
			child.setTextContent(problem.getType());
			xmlProblem.appendChild(child);
		}
		else if(this.generateTypeBlank) {
			Element child = document.createElement(ProblemConstants.CLAIM_TYPE);
			child.setTextContent(ProblemConstants.CLAIM_TYPE_BLANK_VALUE);
			xmlProblem.appendChild(child);
		}
		
		if(problem.getTitle()!=null) {
			Element child = document.createElement(ProblemConstants.CLAIM_TITLE);
			child.setTextContent(problem.getTitle());
			xmlProblem.appendChild(child);
		}	
		
		if(problem.getStatus()!=null) {
			Element child = document.createElement(ProblemConstants.CLAIM_STATUS);
			child.setTextContent(problem.getStatus()+"");
			xmlProblem.appendChild(child);
		}	
		
		if(problem.getDetail()!=null) {
			Element child = document.createElement(ProblemConstants.CLAIM_DETAIL);
			child.setTextContent(problem.getDetail());
			xmlProblem.appendChild(child);
		}	
		
		if(problem.getInstance()!=null) {
			Element child = document.createElement(ProblemConstants.CLAIM_INSTANCE);
			child.setTextContent(problem.getInstance());
			xmlProblem.appendChild(child);
		}	
		
		if(problem.getCustom()!=null && !problem.getCustom().isEmpty()) {
			Iterator<String> it = problem.getCustom().keySet().iterator();
			while (it.hasNext()) {
				String claimName = (String) it.next();
				Object o = problem.getCustom().get(claimName);
				if(o!=null) {
					Element child = document.createElement(claimName);
					if(o instanceof String) {
						child.setTextContent( (String) o);
					}
					else if(o instanceof Integer) {
						child.setTextContent(((Integer) o).toString());
					}
					else if(o instanceof Long) {
						child.setTextContent(((Long) o).toString());
					}
					else if(o instanceof Boolean) {
						child.setTextContent(((Boolean) o).toString());
					}
					else {
						throw new UtilsException("Custom claim with type ["+o.getClass().getName()+"] unsupported");
					}
					xmlProblem.appendChild(child);
				}
			}
		}
		
		return xmlProblem;
	}
}
