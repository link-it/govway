/*
 * OpenSPCoop - Customizable API Gateway 
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

import it.gov.fatturapa.sdi.fatturapa.v1_0.RappresentanteFiscaleType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RappresentanteFiscaleType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RappresentanteFiscaleTypeModel extends AbstractModel<RappresentanteFiscaleType> {

	public RappresentanteFiscaleTypeModel(){
	
		super();
	
		this.DATI_ANAGRAFICI = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiAnagraficiRappresentanteTypeModel(new Field("DatiAnagrafici",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType.class,"RappresentanteFiscaleType",RappresentanteFiscaleType.class));
	
	}
	
	public RappresentanteFiscaleTypeModel(IField father){
	
		super(father);
	
		this.DATI_ANAGRAFICI = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiAnagraficiRappresentanteTypeModel(new ComplexField(father,"DatiAnagrafici",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType.class,"RappresentanteFiscaleType",RappresentanteFiscaleType.class));
	
	}
	
	

	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiAnagraficiRappresentanteTypeModel DATI_ANAGRAFICI = null;
	 

	@Override
	public Class<RappresentanteFiscaleType> getModeledClass(){
		return RappresentanteFiscaleType.class;
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