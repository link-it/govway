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
package org.openspcoop2.monitor.engine.config.transazioni.model;

import org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IdConfigurazioneServizioAzione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdConfigurazioneServizioAzioneModel extends AbstractModel<IdConfigurazioneServizioAzione> {

	public IdConfigurazioneServizioAzioneModel(){
	
		super();
	
		this.ID_CONFIGURAZIONE_SERVIZIO = new org.openspcoop2.monitor.engine.config.transazioni.model.IdConfigurazioneServizioModel(new Field("id-configurazione-servizio",org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio.class,"id-configurazione-servizio-azione",IdConfigurazioneServizioAzione.class));
		this.AZIONE = new Field("azione",java.lang.String.class,"id-configurazione-servizio-azione",IdConfigurazioneServizioAzione.class);
	
	}
	
	public IdConfigurazioneServizioAzioneModel(IField father){
	
		super(father);
	
		this.ID_CONFIGURAZIONE_SERVIZIO = new org.openspcoop2.monitor.engine.config.transazioni.model.IdConfigurazioneServizioModel(new ComplexField(father,"id-configurazione-servizio",org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio.class,"id-configurazione-servizio-azione",IdConfigurazioneServizioAzione.class));
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"id-configurazione-servizio-azione",IdConfigurazioneServizioAzione.class);
	
	}
	
	

	public org.openspcoop2.monitor.engine.config.transazioni.model.IdConfigurazioneServizioModel ID_CONFIGURAZIONE_SERVIZIO = null;
	 
	public IField AZIONE = null;
	 

	@Override
	public Class<IdConfigurazioneServizioAzione> getModeledClass(){
		return IdConfigurazioneServizioAzione.class;
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