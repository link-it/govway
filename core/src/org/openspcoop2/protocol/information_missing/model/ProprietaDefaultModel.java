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
package org.openspcoop2.protocol.information_missing.model;

import org.openspcoop2.protocol.information_missing.ProprietaDefault;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ProprietaDefault 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProprietaDefaultModel extends AbstractModel<ProprietaDefault> {

	public ProprietaDefaultModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"ProprietaDefault",ProprietaDefault.class);
		this.VALORE = new Field("valore",java.lang.String.class,"ProprietaDefault",ProprietaDefault.class);
	
	}
	
	public ProprietaDefaultModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"ProprietaDefault",ProprietaDefault.class);
		this.VALORE = new ComplexField(father,"valore",java.lang.String.class,"ProprietaDefault",ProprietaDefault.class);
	
	}
	
	

	public IField NOME = null;
	 
	public IField VALORE = null;
	 

	@Override
	public Class<ProprietaDefault> getModeledClass(){
		return ProprietaDefault.class;
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