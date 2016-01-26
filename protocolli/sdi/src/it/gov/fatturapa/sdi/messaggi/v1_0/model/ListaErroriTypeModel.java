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
package it.gov.fatturapa.sdi.messaggi.v1_0.model;

import it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ListaErroriType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ListaErroriTypeModel extends AbstractModel<ListaErroriType> {

	public ListaErroriTypeModel(){
	
		super();
	
		this.ERRORE = new it.gov.fatturapa.sdi.messaggi.v1_0.model.ErroreTypeModel(new Field("Errore",it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType.class,"ListaErrori_Type",ListaErroriType.class));
	
	}
	
	public ListaErroriTypeModel(IField father){
	
		super(father);
	
		this.ERRORE = new it.gov.fatturapa.sdi.messaggi.v1_0.model.ErroreTypeModel(new ComplexField(father,"Errore",it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType.class,"ListaErrori_Type",ListaErroriType.class));
	
	}
	
	

	public it.gov.fatturapa.sdi.messaggi.v1_0.model.ErroreTypeModel ERRORE = null;
	 

	@Override
	public Class<ListaErroriType> getModeledClass(){
		return ListaErroriType.class;
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