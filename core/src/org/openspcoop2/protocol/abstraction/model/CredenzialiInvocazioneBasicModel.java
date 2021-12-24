/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.abstraction.model;

import org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CredenzialiInvocazioneBasic 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialiInvocazioneBasicModel extends AbstractModel<CredenzialiInvocazioneBasic> {

	public CredenzialiInvocazioneBasicModel(){
	
		super();
	
		this.USERNAME = new Field("username",java.lang.String.class,"CredenzialiInvocazioneBasic",CredenzialiInvocazioneBasic.class);
		this.PASSWORD = new Field("password",java.lang.String.class,"CredenzialiInvocazioneBasic",CredenzialiInvocazioneBasic.class);
	
	}
	
	public CredenzialiInvocazioneBasicModel(IField father){
	
		super(father);
	
		this.USERNAME = new ComplexField(father,"username",java.lang.String.class,"CredenzialiInvocazioneBasic",CredenzialiInvocazioneBasic.class);
		this.PASSWORD = new ComplexField(father,"password",java.lang.String.class,"CredenzialiInvocazioneBasic",CredenzialiInvocazioneBasic.class);
	
	}
	
	

	public IField USERNAME = null;
	 
	public IField PASSWORD = null;
	 

	@Override
	public Class<CredenzialiInvocazioneBasic> getModeledClass(){
		return CredenzialiInvocazioneBasic.class;
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