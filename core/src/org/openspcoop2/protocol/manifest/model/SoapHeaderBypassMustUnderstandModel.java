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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SoapHeaderBypassMustUnderstand 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapHeaderBypassMustUnderstandModel extends AbstractModel<SoapHeaderBypassMustUnderstand> {

	public SoapHeaderBypassMustUnderstandModel(){
	
		super();
	
		this.HEADER = new org.openspcoop2.protocol.manifest.model.SoapHeaderBypassMustUnderstandHeaderModel(new Field("header",org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader.class,"SoapHeaderBypassMustUnderstand",SoapHeaderBypassMustUnderstand.class));
	
	}
	
	public SoapHeaderBypassMustUnderstandModel(IField father){
	
		super(father);
	
		this.HEADER = new org.openspcoop2.protocol.manifest.model.SoapHeaderBypassMustUnderstandHeaderModel(new ComplexField(father,"header",org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader.class,"SoapHeaderBypassMustUnderstand",SoapHeaderBypassMustUnderstand.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.SoapHeaderBypassMustUnderstandHeaderModel HEADER = null;
	 

	@Override
	public Class<SoapHeaderBypassMustUnderstand> getModeledClass(){
		return SoapHeaderBypassMustUnderstand.class;
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