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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.PortaDelegataAutorizzazioneServiziApplicativi;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaDelegataAutorizzazioneServiziApplicativi 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegataAutorizzazioneServiziApplicativiModel extends AbstractModel<PortaDelegataAutorizzazioneServiziApplicativi> {

	public PortaDelegataAutorizzazioneServiziApplicativiModel(){
	
		super();
	
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.PortaDelegataServizioApplicativoModel(new Field("servizio-applicativo",org.openspcoop2.core.config.PortaDelegataServizioApplicativo.class,"porta-delegata-autorizzazione-servizi-applicativi",PortaDelegataAutorizzazioneServiziApplicativi.class));
	
	}
	
	public PortaDelegataAutorizzazioneServiziApplicativiModel(IField father){
	
		super(father);
	
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.PortaDelegataServizioApplicativoModel(new ComplexField(father,"servizio-applicativo",org.openspcoop2.core.config.PortaDelegataServizioApplicativo.class,"porta-delegata-autorizzazione-servizi-applicativi",PortaDelegataAutorizzazioneServiziApplicativi.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.PortaDelegataServizioApplicativoModel SERVIZIO_APPLICATIVO = null;
	 

	@Override
	public Class<PortaDelegataAutorizzazioneServiziApplicativi> getModeledClass(){
		return PortaDelegataAutorizzazioneServiziApplicativi.class;
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