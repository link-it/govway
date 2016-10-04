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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SoapHeaderBypassMustUnderstandHeader 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapHeaderBypassMustUnderstandHeaderModel extends AbstractModel<SoapHeaderBypassMustUnderstandHeader> {

	public SoapHeaderBypassMustUnderstandHeaderModel(){
	
		super();
	
		this.LOCAL_NAME = new Field("localName",java.lang.String.class,"soapHeaderBypassMustUnderstandHeader",SoapHeaderBypassMustUnderstandHeader.class);
		this.NAMESPACE = new Field("namespace",java.lang.String.class,"soapHeaderBypassMustUnderstandHeader",SoapHeaderBypassMustUnderstandHeader.class);
	
	}
	
	public SoapHeaderBypassMustUnderstandHeaderModel(IField father){
	
		super(father);
	
		this.LOCAL_NAME = new ComplexField(father,"localName",java.lang.String.class,"soapHeaderBypassMustUnderstandHeader",SoapHeaderBypassMustUnderstandHeader.class);
		this.NAMESPACE = new ComplexField(father,"namespace",java.lang.String.class,"soapHeaderBypassMustUnderstandHeader",SoapHeaderBypassMustUnderstandHeader.class);
	
	}
	
	

	public IField LOCAL_NAME = null;
	 
	public IField NAMESPACE = null;
	 

	@Override
	public Class<SoapHeaderBypassMustUnderstandHeader> getModeledClass(){
		return SoapHeaderBypassMustUnderstandHeader.class;
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