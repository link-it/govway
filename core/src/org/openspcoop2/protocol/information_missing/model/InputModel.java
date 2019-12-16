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

import org.openspcoop2.protocol.information_missing.Input;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Input 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InputModel extends AbstractModel<Input> {

	public InputModel(){
	
		super();
	
		this.CONDITIONS = new org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel(new Field("conditions",org.openspcoop2.protocol.information_missing.ConditionsType.class,"input",Input.class));
		this.PROPRIETA = new org.openspcoop2.protocol.information_missing.model.ProprietaModel(new Field("proprieta",org.openspcoop2.protocol.information_missing.Proprieta.class,"input",Input.class));
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"input",Input.class);
	
	}
	
	public InputModel(IField father){
	
		super(father);
	
		this.CONDITIONS = new org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel(new ComplexField(father,"conditions",org.openspcoop2.protocol.information_missing.ConditionsType.class,"input",Input.class));
		this.PROPRIETA = new org.openspcoop2.protocol.information_missing.model.ProprietaModel(new ComplexField(father,"proprieta",org.openspcoop2.protocol.information_missing.Proprieta.class,"input",Input.class));
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"input",Input.class);
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel CONDITIONS = null;
	 
	public org.openspcoop2.protocol.information_missing.model.ProprietaModel PROPRIETA = null;
	 
	public IField DESCRIZIONE = null;
	 

	@Override
	public Class<Input> getModeledClass(){
		return Input.class;
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