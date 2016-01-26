/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.openspcoop2.core.config.StatoServiziPdd;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model StatoServiziPdd 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatoServiziPddModel extends AbstractModel<StatoServiziPdd> {

	public StatoServiziPddModel(){
	
		super();
	
		this.PORTA_DELEGATA = new org.openspcoop2.core.config.model.StatoServiziPddPortaDelegataModel(new Field("porta-delegata",org.openspcoop2.core.config.StatoServiziPddPortaDelegata.class,"stato-servizi-pdd",StatoServiziPdd.class));
		this.PORTA_APPLICATIVA = new org.openspcoop2.core.config.model.StatoServiziPddPortaApplicativaModel(new Field("porta-applicativa",org.openspcoop2.core.config.StatoServiziPddPortaApplicativa.class,"stato-servizi-pdd",StatoServiziPdd.class));
		this.INTEGRATION_MANAGER = new org.openspcoop2.core.config.model.StatoServiziPddIntegrationManagerModel(new Field("integration-manager",org.openspcoop2.core.config.StatoServiziPddIntegrationManager.class,"stato-servizi-pdd",StatoServiziPdd.class));
	
	}
	
	public StatoServiziPddModel(IField father){
	
		super(father);
	
		this.PORTA_DELEGATA = new org.openspcoop2.core.config.model.StatoServiziPddPortaDelegataModel(new ComplexField(father,"porta-delegata",org.openspcoop2.core.config.StatoServiziPddPortaDelegata.class,"stato-servizi-pdd",StatoServiziPdd.class));
		this.PORTA_APPLICATIVA = new org.openspcoop2.core.config.model.StatoServiziPddPortaApplicativaModel(new ComplexField(father,"porta-applicativa",org.openspcoop2.core.config.StatoServiziPddPortaApplicativa.class,"stato-servizi-pdd",StatoServiziPdd.class));
		this.INTEGRATION_MANAGER = new org.openspcoop2.core.config.model.StatoServiziPddIntegrationManagerModel(new ComplexField(father,"integration-manager",org.openspcoop2.core.config.StatoServiziPddIntegrationManager.class,"stato-servizi-pdd",StatoServiziPdd.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.StatoServiziPddPortaDelegataModel PORTA_DELEGATA = null;
	 
	public org.openspcoop2.core.config.model.StatoServiziPddPortaApplicativaModel PORTA_APPLICATIVA = null;
	 
	public org.openspcoop2.core.config.model.StatoServiziPddIntegrationManagerModel INTEGRATION_MANAGER = null;
	 

	@Override
	public Class<StatoServiziPdd> getModeledClass(){
		return StatoServiziPdd.class;
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