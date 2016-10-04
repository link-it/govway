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

import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBeniServiziType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiBeniServiziType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiBeniServiziTypeModel extends AbstractModel<DatiBeniServiziType> {

	public DatiBeniServiziTypeModel(){
	
		super();
	
		this.DETTAGLIO_LINEE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DettaglioLineeTypeModel(new Field("DettaglioLinee",it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType.class,"DatiBeniServiziType",DatiBeniServiziType.class));
		this.DATI_RIEPILOGO = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiRiepilogoTypeModel(new Field("DatiRiepilogo",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType.class,"DatiBeniServiziType",DatiBeniServiziType.class));
	
	}
	
	public DatiBeniServiziTypeModel(IField father){
	
		super(father);
	
		this.DETTAGLIO_LINEE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DettaglioLineeTypeModel(new ComplexField(father,"DettaglioLinee",it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType.class,"DatiBeniServiziType",DatiBeniServiziType.class));
		this.DATI_RIEPILOGO = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiRiepilogoTypeModel(new ComplexField(father,"DatiRiepilogo",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType.class,"DatiBeniServiziType",DatiBeniServiziType.class));
	
	}
	
	

	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.DettaglioLineeTypeModel DETTAGLIO_LINEE = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiRiepilogoTypeModel DATI_RIEPILOGO = null;
	 

	@Override
	public Class<DatiBeniServiziType> getModeledClass(){
		return DatiBeniServiziType.class;
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