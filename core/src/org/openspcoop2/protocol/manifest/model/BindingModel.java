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

import org.openspcoop2.protocol.manifest.Binding;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Binding 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BindingModel extends AbstractModel<Binding> {

	public BindingModel(){
	
		super();
	
		this.SOAP_HEADER_BYPASS_MUST_UNDERSTAND = new org.openspcoop2.protocol.manifest.model.SoapHeaderBypassMustUnderstandModel(new Field("soapHeaderBypassMustUnderstand",org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand.class,"binding",Binding.class));
		this.SOAP_11 = new Field("soap11",boolean.class,"binding",Binding.class);
		this.SOAP_12 = new Field("soap12",boolean.class,"binding",Binding.class);
	
	}
	
	public BindingModel(IField father){
	
		super(father);
	
		this.SOAP_HEADER_BYPASS_MUST_UNDERSTAND = new org.openspcoop2.protocol.manifest.model.SoapHeaderBypassMustUnderstandModel(new ComplexField(father,"soapHeaderBypassMustUnderstand",org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand.class,"binding",Binding.class));
		this.SOAP_11 = new ComplexField(father,"soap11",boolean.class,"binding",Binding.class);
		this.SOAP_12 = new ComplexField(father,"soap12",boolean.class,"binding",Binding.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.SoapHeaderBypassMustUnderstandModel SOAP_HEADER_BYPASS_MUST_UNDERSTAND = null;
	 
	public IField SOAP_11 = null;
	 
	public IField SOAP_12 = null;
	 

	@Override
	public Class<Binding> getModeledClass(){
		return Binding.class;
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