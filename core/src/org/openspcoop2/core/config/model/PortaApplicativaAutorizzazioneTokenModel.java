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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneToken;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaApplicativaAutorizzazioneToken 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaAutorizzazioneTokenModel extends AbstractModel<PortaApplicativaAutorizzazioneToken> {

	public PortaApplicativaAutorizzazioneTokenModel(){
	
		super();
	
		this.SERVIZI_APPLICATIVI = new org.openspcoop2.core.config.model.PortaApplicativaAutorizzazioneServiziApplicativiModel(new Field("servizi-applicativi",org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi.class,"porta-applicativa-autorizzazione-token",PortaApplicativaAutorizzazioneToken.class));
		this.RUOLI = new org.openspcoop2.core.config.model.AutorizzazioneRuoliModel(new Field("ruoli",org.openspcoop2.core.config.AutorizzazioneRuoli.class,"porta-applicativa-autorizzazione-token",PortaApplicativaAutorizzazioneToken.class));
		this.AUTORIZZAZIONE_APPLICATIVI = new Field("autorizzazione-applicativi",java.lang.String.class,"porta-applicativa-autorizzazione-token",PortaApplicativaAutorizzazioneToken.class);
		this.AUTORIZZAZIONE_RUOLI = new Field("autorizzazione-ruoli",java.lang.String.class,"porta-applicativa-autorizzazione-token",PortaApplicativaAutorizzazioneToken.class);
		this.TIPOLOGIA_RUOLI = new Field("tipologia-ruoli",java.lang.String.class,"porta-applicativa-autorizzazione-token",PortaApplicativaAutorizzazioneToken.class);
	
	}
	
	public PortaApplicativaAutorizzazioneTokenModel(IField father){
	
		super(father);
	
		this.SERVIZI_APPLICATIVI = new org.openspcoop2.core.config.model.PortaApplicativaAutorizzazioneServiziApplicativiModel(new ComplexField(father,"servizi-applicativi",org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi.class,"porta-applicativa-autorizzazione-token",PortaApplicativaAutorizzazioneToken.class));
		this.RUOLI = new org.openspcoop2.core.config.model.AutorizzazioneRuoliModel(new ComplexField(father,"ruoli",org.openspcoop2.core.config.AutorizzazioneRuoli.class,"porta-applicativa-autorizzazione-token",PortaApplicativaAutorizzazioneToken.class));
		this.AUTORIZZAZIONE_APPLICATIVI = new ComplexField(father,"autorizzazione-applicativi",java.lang.String.class,"porta-applicativa-autorizzazione-token",PortaApplicativaAutorizzazioneToken.class);
		this.AUTORIZZAZIONE_RUOLI = new ComplexField(father,"autorizzazione-ruoli",java.lang.String.class,"porta-applicativa-autorizzazione-token",PortaApplicativaAutorizzazioneToken.class);
		this.TIPOLOGIA_RUOLI = new ComplexField(father,"tipologia-ruoli",java.lang.String.class,"porta-applicativa-autorizzazione-token",PortaApplicativaAutorizzazioneToken.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.PortaApplicativaAutorizzazioneServiziApplicativiModel SERVIZI_APPLICATIVI = null;
	 
	public org.openspcoop2.core.config.model.AutorizzazioneRuoliModel RUOLI = null;
	 
	public IField AUTORIZZAZIONE_APPLICATIVI = null;
	 
	public IField AUTORIZZAZIONE_RUOLI = null;
	 
	public IField TIPOLOGIA_RUOLI = null;
	 

	@Override
	public Class<PortaApplicativaAutorizzazioneToken> getModeledClass(){
		return PortaApplicativaAutorizzazioneToken.class;
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