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

import org.openspcoop2.protocol.manifest.Web;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Web 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WebModel extends AbstractModel<Web> {

	public WebModel(){
	
		super();
	
		this.CONTEXT = new org.openspcoop2.protocol.manifest.model.ContextModel(new Field("context",org.openspcoop2.protocol.manifest.Context.class,"web",Web.class));
		this.EMPTY_CONTEXT = new org.openspcoop2.protocol.manifest.model.WebEmptyContextModel(new Field("emptyContext",org.openspcoop2.protocol.manifest.WebEmptyContext.class,"web",Web.class));
	
	}
	
	public WebModel(IField father){
	
		super(father);
	
		this.CONTEXT = new org.openspcoop2.protocol.manifest.model.ContextModel(new ComplexField(father,"context",org.openspcoop2.protocol.manifest.Context.class,"web",Web.class));
		this.EMPTY_CONTEXT = new org.openspcoop2.protocol.manifest.model.WebEmptyContextModel(new ComplexField(father,"emptyContext",org.openspcoop2.protocol.manifest.WebEmptyContext.class,"web",Web.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.ContextModel CONTEXT = null;
	 
	public org.openspcoop2.protocol.manifest.model.WebEmptyContextModel EMPTY_CONTEXT = null;
	 

	@Override
	public Class<Web> getModeledClass(){
		return Web.class;
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