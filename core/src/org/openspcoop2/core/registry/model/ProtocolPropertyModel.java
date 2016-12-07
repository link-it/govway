/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.openspcoop2.core.registry.ProtocolProperty;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ProtocolProperty 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolPropertyModel extends AbstractModel<ProtocolProperty> {

	public ProtocolPropertyModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"protocol-property",ProtocolProperty.class);
		this.VALUE = new Field("value",java.lang.String.class,"protocol-property",ProtocolProperty.class);
		this.NUMBER_VALUE = new Field("number-value",java.lang.Long.class,"protocol-property",ProtocolProperty.class);
		this.BOOLEAN_VALUE = new Field("boolean-value",Boolean.class,"protocol-property",ProtocolProperty.class);
		this.FILE = new Field("file",java.lang.String.class,"protocol-property",ProtocolProperty.class);
		this.BYTE_FILE = new Field("byte-file",byte[].class,"protocol-property",ProtocolProperty.class);
		this.TIPO_PROPRIETARIO_DOCUMENTO = new Field("tipo-proprietario-documento",java.lang.String.class,"protocol-property",ProtocolProperty.class);
		this.ID_PROPRIETARIO_DOCUMENTO = new Field("id-proprietario-documento",java.lang.Long.class,"protocol-property",ProtocolProperty.class);
	
	}
	
	public ProtocolPropertyModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"protocol-property",ProtocolProperty.class);
		this.VALUE = new ComplexField(father,"value",java.lang.String.class,"protocol-property",ProtocolProperty.class);
		this.NUMBER_VALUE = new ComplexField(father,"number-value",java.lang.Long.class,"protocol-property",ProtocolProperty.class);
		this.BOOLEAN_VALUE = new ComplexField(father,"boolean-value",Boolean.class,"protocol-property",ProtocolProperty.class);
		this.FILE = new ComplexField(father,"file",java.lang.String.class,"protocol-property",ProtocolProperty.class);
		this.BYTE_FILE = new ComplexField(father,"byte-file",byte[].class,"protocol-property",ProtocolProperty.class);
		this.TIPO_PROPRIETARIO_DOCUMENTO = new ComplexField(father,"tipo-proprietario-documento",java.lang.String.class,"protocol-property",ProtocolProperty.class);
		this.ID_PROPRIETARIO_DOCUMENTO = new ComplexField(father,"id-proprietario-documento",java.lang.Long.class,"protocol-property",ProtocolProperty.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField VALUE = null;
	 
	public IField NUMBER_VALUE = null;
	 
	public IField BOOLEAN_VALUE = null;
	 
	public IField FILE = null;
	 
	public IField BYTE_FILE = null;
	 
	public IField TIPO_PROPRIETARIO_DOCUMENTO = null;
	 
	public IField ID_PROPRIETARIO_DOCUMENTO = null;
	 

	@Override
	public Class<ProtocolProperty> getModeledClass(){
		return ProtocolProperty.class;
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