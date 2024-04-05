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
package eu.domibus.configuration.model;

import eu.domibus.configuration.Agreements;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Agreements 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AgreementsModel extends AbstractModel<Agreements> {

	public AgreementsModel(){
	
		super();
	
		this.AGREEMENT = new eu.domibus.configuration.model.AgreementModel(new Field("agreement",eu.domibus.configuration.Agreement.class,"agreements",Agreements.class));
	
	}
	
	public AgreementsModel(IField father){
	
		super(father);
	
		this.AGREEMENT = new eu.domibus.configuration.model.AgreementModel(new ComplexField(father,"agreement",eu.domibus.configuration.Agreement.class,"agreements",Agreements.class));
	
	}
	
	

	public eu.domibus.configuration.model.AgreementModel AGREEMENT = null;
	 

	@Override
	public Class<Agreements> getModeledClass(){
		return Agreements.class;
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