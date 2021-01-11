/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import eu.domibus.configuration.As4;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model As4 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class As4Model extends AbstractModel<As4> {

	public As4Model(){
	
		super();
	
		this.RECEPTION_AWARENESS = new eu.domibus.configuration.model.ReceptionAwarenessModel(new Field("receptionAwareness",eu.domibus.configuration.ReceptionAwareness.class,"as4",As4.class));
		this.RELIABILITY = new eu.domibus.configuration.model.ReliabilityModel(new Field("reliability",eu.domibus.configuration.Reliability.class,"as4",As4.class));
	
	}
	
	public As4Model(IField father){
	
		super(father);
	
		this.RECEPTION_AWARENESS = new eu.domibus.configuration.model.ReceptionAwarenessModel(new ComplexField(father,"receptionAwareness",eu.domibus.configuration.ReceptionAwareness.class,"as4",As4.class));
		this.RELIABILITY = new eu.domibus.configuration.model.ReliabilityModel(new ComplexField(father,"reliability",eu.domibus.configuration.Reliability.class,"as4",As4.class));
	
	}
	
	

	public eu.domibus.configuration.model.ReceptionAwarenessModel RECEPTION_AWARENESS = null;
	 
	public eu.domibus.configuration.model.ReliabilityModel RELIABILITY = null;
	 

	@Override
	public Class<As4> getModeledClass(){
		return As4.class;
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