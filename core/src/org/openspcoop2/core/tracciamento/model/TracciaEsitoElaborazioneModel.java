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
package org.openspcoop2.core.tracciamento.model;

import org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TracciaEsitoElaborazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TracciaEsitoElaborazioneModel extends AbstractModel<TracciaEsitoElaborazione> {

	public TracciaEsitoElaborazioneModel(){
	
		super();
	
		this.DETTAGLIO = new Field("dettaglio",java.lang.String.class,"traccia-esito-elaborazione",TracciaEsitoElaborazione.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"traccia-esito-elaborazione",TracciaEsitoElaborazione.class);
	
	}
	
	public TracciaEsitoElaborazioneModel(IField father){
	
		super(father);
	
		this.DETTAGLIO = new ComplexField(father,"dettaglio",java.lang.String.class,"traccia-esito-elaborazione",TracciaEsitoElaborazione.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"traccia-esito-elaborazione",TracciaEsitoElaborazione.class);
	
	}
	
	

	public IField DETTAGLIO = null;
	 
	public IField TIPO = null;
	 

	@Override
	public Class<TracciaEsitoElaborazione> getModeledClass(){
		return TracciaEsitoElaborazione.class;
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