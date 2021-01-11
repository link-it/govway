/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import org.openspcoop2.protocol.manifest.SoapMediaTypeMapping;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SoapMediaTypeMapping 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapMediaTypeMappingModel extends AbstractModel<SoapMediaTypeMapping> {

	public SoapMediaTypeMappingModel(){
	
		super();
	
		this.BASE = new Field("base",java.lang.String.class,"SoapMediaTypeMapping",SoapMediaTypeMapping.class);
		this.MESSAGE_TYPE = new Field("messageType",java.lang.String.class,"SoapMediaTypeMapping",SoapMediaTypeMapping.class);
		this.REG_EXPR = new Field("regExpr",boolean.class,"SoapMediaTypeMapping",SoapMediaTypeMapping.class);
	
	}
	
	public SoapMediaTypeMappingModel(IField father){
	
		super(father);
	
		this.BASE = new ComplexField(father,"base",java.lang.String.class,"SoapMediaTypeMapping",SoapMediaTypeMapping.class);
		this.MESSAGE_TYPE = new ComplexField(father,"messageType",java.lang.String.class,"SoapMediaTypeMapping",SoapMediaTypeMapping.class);
		this.REG_EXPR = new ComplexField(father,"regExpr",boolean.class,"SoapMediaTypeMapping",SoapMediaTypeMapping.class);
	
	}
	
	

	public IField BASE = null;
	 
	public IField MESSAGE_TYPE = null;
	 
	public IField REG_EXPR = null;
	 

	@Override
	public Class<SoapMediaTypeMapping> getModeledClass(){
		return SoapMediaTypeMapping.class;
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