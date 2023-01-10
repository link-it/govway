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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.AttributeAuthority;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AttributeAuthority 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttributeAuthorityModel extends AbstractModel<AttributeAuthority> {

	public AttributeAuthorityModel(){
	
		super();
	
		this.ATTRIBUTO = new Field("attributo",java.lang.String.class,"attribute-authority",AttributeAuthority.class);
		this.NOME = new Field("nome",java.lang.String.class,"attribute-authority",AttributeAuthority.class);
	
	}
	
	public AttributeAuthorityModel(IField father){
	
		super(father);
	
		this.ATTRIBUTO = new ComplexField(father,"attributo",java.lang.String.class,"attribute-authority",AttributeAuthority.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"attribute-authority",AttributeAuthority.class);
	
	}
	
	

	public IField ATTRIBUTO = null;
	 
	public IField NOME = null;
	 

	@Override
	public Class<AttributeAuthority> getModeledClass(){
		return AttributeAuthority.class;
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