/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.SubContextMapping;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SubContextMapping 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SubContextMappingModel extends AbstractModel<SubContextMapping> {

	public SubContextMappingModel(){
	
		super();
	
		this.BASE = new Field("base",java.lang.String.class,"SubContextMapping",SubContextMapping.class);
		this.FUNCTION = new Field("function",java.lang.String.class,"SubContextMapping",SubContextMapping.class);
		this.MESSAGE_TYPE = new Field("messageType",java.lang.String.class,"SubContextMapping",SubContextMapping.class);
	
	}
	
	public SubContextMappingModel(IField father){
	
		super(father);
	
		this.BASE = new ComplexField(father,"base",java.lang.String.class,"SubContextMapping",SubContextMapping.class);
		this.FUNCTION = new ComplexField(father,"function",java.lang.String.class,"SubContextMapping",SubContextMapping.class);
		this.MESSAGE_TYPE = new ComplexField(father,"messageType",java.lang.String.class,"SubContextMapping",SubContextMapping.class);
	
	}
	
	

	public IField BASE = null;
	 
	public IField FUNCTION = null;
	 
	public IField MESSAGE_TYPE = null;
	 

	@Override
	public Class<SubContextMapping> getModeledClass(){
		return SubContextMapping.class;
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