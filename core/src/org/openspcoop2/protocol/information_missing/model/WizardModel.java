/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import org.openspcoop2.protocol.information_missing.Wizard;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Wizard 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WizardModel extends AbstractModel<Wizard> {

	public WizardModel(){
	
		super();
	
		this.REQUISITI = new org.openspcoop2.protocol.information_missing.model.RequisitiModel(new Field("requisiti",org.openspcoop2.protocol.information_missing.Requisiti.class,"wizard",Wizard.class));
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"wizard",Wizard.class);
		this.STEP = new Field("step",int.class,"wizard",Wizard.class);
		this.STEP_IN_DELETE = new Field("step-in-delete",int.class,"wizard",Wizard.class);
		this.INTESTAZIONE_ORIGINALE = new Field("intestazione-originale",boolean.class,"wizard",Wizard.class);
	
	}
	
	public WizardModel(IField father){
	
		super(father);
	
		this.REQUISITI = new org.openspcoop2.protocol.information_missing.model.RequisitiModel(new ComplexField(father,"requisiti",org.openspcoop2.protocol.information_missing.Requisiti.class,"wizard",Wizard.class));
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"wizard",Wizard.class);
		this.STEP = new ComplexField(father,"step",int.class,"wizard",Wizard.class);
		this.STEP_IN_DELETE = new ComplexField(father,"step-in-delete",int.class,"wizard",Wizard.class);
		this.INTESTAZIONE_ORIGINALE = new ComplexField(father,"intestazione-originale",boolean.class,"wizard",Wizard.class);
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.RequisitiModel REQUISITI = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField STEP = null;
	 
	public IField STEP_IN_DELETE = null;
	 
	public IField INTESTAZIONE_ORIGINALE = null;
	 

	@Override
	public Class<Wizard> getModeledClass(){
		return Wizard.class;
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