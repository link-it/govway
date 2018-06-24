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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.ResourceRepresentationXml;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ResourceRepresentationXml 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResourceRepresentationXmlModel extends AbstractModel<ResourceRepresentationXml> {

	public ResourceRepresentationXmlModel(){
	
		super();
	
		this.XML_TYPE = new Field("xml-type",java.lang.String.class,"resource-representation-xml",ResourceRepresentationXml.class);
		this.NOME = new Field("nome",java.lang.String.class,"resource-representation-xml",ResourceRepresentationXml.class);
		this.NAMESPACE = new Field("namespace",java.lang.String.class,"resource-representation-xml",ResourceRepresentationXml.class);
	
	}
	
	public ResourceRepresentationXmlModel(IField father){
	
		super(father);
	
		this.XML_TYPE = new ComplexField(father,"xml-type",java.lang.String.class,"resource-representation-xml",ResourceRepresentationXml.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"resource-representation-xml",ResourceRepresentationXml.class);
		this.NAMESPACE = new ComplexField(father,"namespace",java.lang.String.class,"resource-representation-xml",ResourceRepresentationXml.class);
	
	}
	
	

	public IField XML_TYPE = null;
	 
	public IField NOME = null;
	 
	public IField NAMESPACE = null;
	 

	@Override
	public Class<ResourceRepresentationXml> getModeledClass(){
		return ResourceRepresentationXml.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}