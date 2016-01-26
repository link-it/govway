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
package org.openspcoop2.core.tracciamento.model;

import org.openspcoop2.core.tracciamento.Trasmissione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Trasmissione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasmissioneModel extends AbstractModel<Trasmissione> {

	public TrasmissioneModel(){
	
		super();
	
		this.ORIGINE = new org.openspcoop2.core.tracciamento.model.SoggettoModel(new Field("origine",org.openspcoop2.core.tracciamento.Soggetto.class,"trasmissione",Trasmissione.class));
		this.DESTINAZIONE = new org.openspcoop2.core.tracciamento.model.SoggettoModel(new Field("destinazione",org.openspcoop2.core.tracciamento.Soggetto.class,"trasmissione",Trasmissione.class));
		this.ORA_REGISTRAZIONE = new org.openspcoop2.core.tracciamento.model.DataModel(new Field("ora-registrazione",org.openspcoop2.core.tracciamento.Data.class,"trasmissione",Trasmissione.class));
	
	}
	
	public TrasmissioneModel(IField father){
	
		super(father);
	
		this.ORIGINE = new org.openspcoop2.core.tracciamento.model.SoggettoModel(new ComplexField(father,"origine",org.openspcoop2.core.tracciamento.Soggetto.class,"trasmissione",Trasmissione.class));
		this.DESTINAZIONE = new org.openspcoop2.core.tracciamento.model.SoggettoModel(new ComplexField(father,"destinazione",org.openspcoop2.core.tracciamento.Soggetto.class,"trasmissione",Trasmissione.class));
		this.ORA_REGISTRAZIONE = new org.openspcoop2.core.tracciamento.model.DataModel(new ComplexField(father,"ora-registrazione",org.openspcoop2.core.tracciamento.Data.class,"trasmissione",Trasmissione.class));
	
	}
	
	

	public org.openspcoop2.core.tracciamento.model.SoggettoModel ORIGINE = null;
	 
	public org.openspcoop2.core.tracciamento.model.SoggettoModel DESTINAZIONE = null;
	 
	public org.openspcoop2.core.tracciamento.model.DataModel ORA_REGISTRAZIONE = null;
	 

	@Override
	public Class<Trasmissione> getModeledClass(){
		return Trasmissione.class;
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