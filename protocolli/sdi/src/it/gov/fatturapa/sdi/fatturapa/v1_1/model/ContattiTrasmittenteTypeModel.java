/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package it.gov.fatturapa.sdi.fatturapa.v1_1.model;

import it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ContattiTrasmittenteType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContattiTrasmittenteTypeModel extends AbstractModel<ContattiTrasmittenteType> {

	public ContattiTrasmittenteTypeModel(){
	
		super();
	
		this.TELEFONO = new Field("Telefono",java.lang.String.class,"ContattiTrasmittenteType",ContattiTrasmittenteType.class);
		this.EMAIL = new Field("Email",java.lang.String.class,"ContattiTrasmittenteType",ContattiTrasmittenteType.class);
	
	}
	
	public ContattiTrasmittenteTypeModel(IField father){
	
		super(father);
	
		this.TELEFONO = new ComplexField(father,"Telefono",java.lang.String.class,"ContattiTrasmittenteType",ContattiTrasmittenteType.class);
		this.EMAIL = new ComplexField(father,"Email",java.lang.String.class,"ContattiTrasmittenteType",ContattiTrasmittenteType.class);
	
	}
	
	

	public IField TELEFONO = null;
	 
	public IField EMAIL = null;
	 

	@Override
	public Class<ContattiTrasmittenteType> getModeledClass(){
		return ContattiTrasmittenteType.class;
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