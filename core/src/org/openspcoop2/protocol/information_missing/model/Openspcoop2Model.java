/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.protocol.information_missing.Openspcoop2;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Openspcoop2 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Openspcoop2Model extends AbstractModel<Openspcoop2> {

	public Openspcoop2Model(){
	
		super();
	
		this.WIZARD = new org.openspcoop2.protocol.information_missing.model.WizardModel(new Field("wizard",org.openspcoop2.protocol.information_missing.Wizard.class,"openspcoop2",Openspcoop2.class));
		this.OPERAZIONE = new org.openspcoop2.protocol.information_missing.model.OperazioneModel(new Field("operazione",org.openspcoop2.protocol.information_missing.Operazione.class,"openspcoop2",Openspcoop2.class));
	
	}
	
	public Openspcoop2Model(IField father){
	
		super(father);
	
		this.WIZARD = new org.openspcoop2.protocol.information_missing.model.WizardModel(new ComplexField(father,"wizard",org.openspcoop2.protocol.information_missing.Wizard.class,"openspcoop2",Openspcoop2.class));
		this.OPERAZIONE = new org.openspcoop2.protocol.information_missing.model.OperazioneModel(new ComplexField(father,"operazione",org.openspcoop2.protocol.information_missing.Operazione.class,"openspcoop2",Openspcoop2.class));
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.WizardModel WIZARD = null;
	 
	public org.openspcoop2.protocol.information_missing.model.OperazioneModel OPERAZIONE = null;
	 

	@Override
	public Class<Openspcoop2> getModeledClass(){
		return Openspcoop2.class;
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