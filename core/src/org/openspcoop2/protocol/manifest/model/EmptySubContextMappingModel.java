/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.EmptySubContextMapping;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model EmptySubContextMapping 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EmptySubContextMappingModel extends AbstractModel<EmptySubContextMapping> {

	public EmptySubContextMappingModel(){
	
		super();
	
		this.FUNCTION = new Field("function",java.lang.String.class,"EmptySubContextMapping",EmptySubContextMapping.class);
		this.MESSAGE_TYPE = new Field("messageType",java.lang.String.class,"EmptySubContextMapping",EmptySubContextMapping.class);
	
	}
	
	public EmptySubContextMappingModel(IField father){
	
		super(father);
	
		this.FUNCTION = new ComplexField(father,"function",java.lang.String.class,"EmptySubContextMapping",EmptySubContextMapping.class);
		this.MESSAGE_TYPE = new ComplexField(father,"messageType",java.lang.String.class,"EmptySubContextMapping",EmptySubContextMapping.class);
	
	}
	
	

	public IField FUNCTION = null;
	 
	public IField MESSAGE_TYPE = null;
	 

	@Override
	public Class<EmptySubContextMapping> getModeledClass(){
		return EmptySubContextMapping.class;
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