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
package it.gov.fatturapa.sdi.fatturapa.v1_0.model;

import it.gov.fatturapa.sdi.fatturapa.v1_0.TerzoIntermediarioSoggettoEmittenteType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TerzoIntermediarioSoggettoEmittenteType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TerzoIntermediarioSoggettoEmittenteTypeModel extends AbstractModel<TerzoIntermediarioSoggettoEmittenteType> {

	public TerzoIntermediarioSoggettoEmittenteTypeModel(){
	
		super();
	
		this.DATI_ANAGRAFICI = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiAnagraficiTerzoIntermediarioTypeModel(new Field("DatiAnagrafici",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiTerzoIntermediarioType.class,"TerzoIntermediarioSoggettoEmittenteType",TerzoIntermediarioSoggettoEmittenteType.class));
	
	}
	
	public TerzoIntermediarioSoggettoEmittenteTypeModel(IField father){
	
		super(father);
	
		this.DATI_ANAGRAFICI = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiAnagraficiTerzoIntermediarioTypeModel(new ComplexField(father,"DatiAnagrafici",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiTerzoIntermediarioType.class,"TerzoIntermediarioSoggettoEmittenteType",TerzoIntermediarioSoggettoEmittenteType.class));
	
	}
	
	

	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiAnagraficiTerzoIntermediarioTypeModel DATI_ANAGRAFICI = null;
	 

	@Override
	public Class<TerzoIntermediarioSoggettoEmittenteType> getModeledClass(){
		return TerzoIntermediarioSoggettoEmittenteType.class;
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