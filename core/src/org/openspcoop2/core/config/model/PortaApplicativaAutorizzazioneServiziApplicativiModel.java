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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaApplicativaAutorizzazioneServiziApplicativi 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaAutorizzazioneServiziApplicativiModel extends AbstractModel<PortaApplicativaAutorizzazioneServiziApplicativi> {

	public PortaApplicativaAutorizzazioneServiziApplicativiModel(){
	
		super();
	
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.PortaApplicativaAutorizzazioneServizioApplicativoModel(new Field("servizio-applicativo",org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo.class,"porta-applicativa-autorizzazione-servizi-applicativi",PortaApplicativaAutorizzazioneServiziApplicativi.class));
	
	}
	
	public PortaApplicativaAutorizzazioneServiziApplicativiModel(IField father){
	
		super(father);
	
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.PortaApplicativaAutorizzazioneServizioApplicativoModel(new ComplexField(father,"servizio-applicativo",org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo.class,"porta-applicativa-autorizzazione-servizi-applicativi",PortaApplicativaAutorizzazioneServiziApplicativi.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.PortaApplicativaAutorizzazioneServizioApplicativoModel SERVIZIO_APPLICATIVO = null;
	 

	@Override
	public Class<PortaApplicativaAutorizzazioneServiziApplicativi> getModeledClass(){
		return PortaApplicativaAutorizzazioneServiziApplicativi.class;
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