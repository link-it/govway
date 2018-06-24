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

package org.openspcoop2.utils.xacml;

import java.util.ArrayList;
import java.util.List;

import org.herasaf.xacml.core.context.impl.ActionType;
import org.herasaf.xacml.core.context.impl.AttributeType;
import org.herasaf.xacml.core.context.impl.AttributeValueType;
import org.herasaf.xacml.core.context.impl.EnvironmentType;
import org.herasaf.xacml.core.context.impl.ObjectFactory;
import org.herasaf.xacml.core.context.impl.RequestType;
import org.herasaf.xacml.core.context.impl.ResourceType;
import org.herasaf.xacml.core.context.impl.SubjectType;
import org.herasaf.xacml.core.dataTypeAttribute.impl.StringDataTypeAttribute;


/**
 * XacmlRequest
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XacmlRequest {

	private ObjectFactory factory;
	private RequestType xacmlRequest;
	
	public RequestType getXacmlRequest() {
		return this.xacmlRequest;
	}

	public XacmlRequest(){
		this.factory = new ObjectFactory();
		this.xacmlRequest = this.factory.createRequestType();
		MarshallUtilities.runInitializers();
	}
	
	public void createAction(){
		if(this.xacmlRequest.getAction()==null){
			ActionType action =  this.factory.createActionType();	
			this.xacmlRequest.setAction(action);
		}
	}
	public void addAction(String azione){
		this.addActionAttribute("urn:oasis:names:tc:xacml:1.0:action:action-id", azione);
	}
	public void addActionAttribute(String attributeName, String attributeValue){
		this.addActionAttribute(createAttribute(attributeName, attributeValue));
	}
	public void addActionAttribute(String attributeName, List<String> attributeValues){
		this.addActionAttribute(createAttribute(attributeName, attributeValues));
	}
	public void addActionAttribute(AttributeType attribute){
		this.createAction();
		this.xacmlRequest.getAction().getAttributes().add(attribute);
	}
	
	
	public void addSubject(String subject){
		this.addSubjectAttribute("urn:oasis:names:tc:xacml:1.0:subject:subject-id", subject);
	}
	public void createSubject(){
		if(this.xacmlRequest.getSubjects().isEmpty()){
			SubjectType subject = this.factory.createSubjectType();
			this.xacmlRequest.getSubjects().add(subject);
		}
	}
	public void addSubjectAttribute(String attributeName, String attributeValue){
		this.addSubjectAttribute(createAttribute(attributeName, attributeValue));
	}
	public void addSubjectAttribute(String attributeName, List<String> attributeValues){
		this.addSubjectAttribute(createAttribute(attributeName, attributeValues));
	}
	public void addSubjectAttribute(AttributeType attribute){
		createSubject();
		this.xacmlRequest.getSubjects().get(0).getAttributes().add(attribute);
	}
	
	
	public void createEnvironment(){
		if(this.xacmlRequest.getEnvironment()==null){
			EnvironmentType action = this.factory.createEnvironmentType();	
			this.xacmlRequest.setEnvironment(action);
		}
	}
	public void addEnvironmentAttribute(String attributeName, String attributeValue){
		this.addEnvironmentAttribute(createAttribute(attributeName, attributeValue));
	}
	public void addEnvironmentAttribute(String attributeName, List<String> attributeValues){
		this.addEnvironmentAttribute(createAttribute(attributeName, attributeValues));
	}
	public void addEnvironmentAttribute(AttributeType attribute){
		createEnvironment();
		this.xacmlRequest.getEnvironment().getAttributes().add(attribute);
	}
	
	
	
	public void createResource(){
		if(this.xacmlRequest.getResources().isEmpty()){
			ResourceType resource = this.factory.createResourceType();
			this.xacmlRequest.getResources().add(resource);
		}
	}
	public void addResourceAttribute(String attributeName, String attributeValue){
		this.addResourceAttribute(createAttribute(attributeName, attributeValue));
	}
	public void addResourceAttribute(String attributeName, List<String> attributeValues){
		this.addResourceAttribute(createAttribute(attributeName, attributeValues));
	}
	public void addResourceAttribute(AttributeType attribute){
		createResource();
		this.xacmlRequest.getResources().get(0).getAttributes().add(attribute);
	}
	
	
	
	private AttributeType createAttribute(String name, List<String> values) {

		AttributeType attribute = this.factory.createAttributeType();
		
		for(String value: values) {
			AttributeValueType value1 = new AttributeValueType();
			value1.getContent().add(value);
			attribute.getAttributeValues().add(value1);
		}

		attribute.setAttributeId(name);
		attribute.setDataType(new StringDataTypeAttribute());		
		return attribute;
		
	}

	private AttributeType createAttribute(String name, String value) {

		List<String> lst = new ArrayList<String>();
		lst.add(value);
		return createAttribute(name, lst);
		
	}
}
