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

import org.openspcoop2.core.config.StatoServiziPddPortaApplicativa;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model StatoServiziPddPortaApplicativa 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatoServiziPddPortaApplicativaModel extends AbstractModel<StatoServiziPddPortaApplicativa> {

	public StatoServiziPddPortaApplicativaModel(){
	
		super();
	
		this.FILTRO_ABILITAZIONE = new org.openspcoop2.core.config.model.TipoFiltroAbilitazioneServiziModel(new Field("filtro-abilitazione",org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi.class,"stato-servizi-pdd-porta-applicativa",StatoServiziPddPortaApplicativa.class));
		this.FILTRO_DISABILITAZIONE = new org.openspcoop2.core.config.model.TipoFiltroAbilitazioneServiziModel(new Field("filtro-disabilitazione",org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi.class,"stato-servizi-pdd-porta-applicativa",StatoServiziPddPortaApplicativa.class));
		this.STATO = new Field("stato",java.lang.String.class,"stato-servizi-pdd-porta-applicativa",StatoServiziPddPortaApplicativa.class);
	
	}
	
	public StatoServiziPddPortaApplicativaModel(IField father){
	
		super(father);
	
		this.FILTRO_ABILITAZIONE = new org.openspcoop2.core.config.model.TipoFiltroAbilitazioneServiziModel(new ComplexField(father,"filtro-abilitazione",org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi.class,"stato-servizi-pdd-porta-applicativa",StatoServiziPddPortaApplicativa.class));
		this.FILTRO_DISABILITAZIONE = new org.openspcoop2.core.config.model.TipoFiltroAbilitazioneServiziModel(new ComplexField(father,"filtro-disabilitazione",org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi.class,"stato-servizi-pdd-porta-applicativa",StatoServiziPddPortaApplicativa.class));
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"stato-servizi-pdd-porta-applicativa",StatoServiziPddPortaApplicativa.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.TipoFiltroAbilitazioneServiziModel FILTRO_ABILITAZIONE = null;
	 
	public org.openspcoop2.core.config.model.TipoFiltroAbilitazioneServiziModel FILTRO_DISABILITAZIONE = null;
	 
	public IField STATO = null;
	 

	@Override
	public Class<StatoServiziPddPortaApplicativa> getModeledClass(){
		return StatoServiziPddPortaApplicativa.class;
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