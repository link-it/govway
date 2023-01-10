/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.commons.search.model;

import org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaDelegataServizioApplicativo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegataServizioApplicativoModel extends AbstractModel<PortaDelegataServizioApplicativo> {

	public PortaDelegataServizioApplicativoModel(){
	
		super();
	
		this.ID_SERVIZIO_APPLICATIVO = new org.openspcoop2.core.commons.search.model.IdServizioApplicativoModel(new Field("id-servizio-applicativo",org.openspcoop2.core.commons.search.IdServizioApplicativo.class,"porta-delegata-servizio-applicativo",PortaDelegataServizioApplicativo.class));
	
	}
	
	public PortaDelegataServizioApplicativoModel(IField father){
	
		super(father);
	
		this.ID_SERVIZIO_APPLICATIVO = new org.openspcoop2.core.commons.search.model.IdServizioApplicativoModel(new ComplexField(father,"id-servizio-applicativo",org.openspcoop2.core.commons.search.IdServizioApplicativo.class,"porta-delegata-servizio-applicativo",PortaDelegataServizioApplicativo.class));
	
	}
	
	

	public org.openspcoop2.core.commons.search.model.IdServizioApplicativoModel ID_SERVIZIO_APPLICATIVO = null;
	 

	@Override
	public Class<PortaDelegataServizioApplicativo> getModeledClass(){
		return PortaDelegataServizioApplicativo.class;
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