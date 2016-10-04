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

import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiAnagraficiCessionarioType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiAnagraficiCessionarioTypeModel extends AbstractModel<DatiAnagraficiCessionarioType> {

	public DatiAnagraficiCessionarioTypeModel(){
	
		super();
	
		this.ID_FISCALE_IVA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.IdFiscaleTypeModel(new Field("IdFiscaleIVA",it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType.class,"DatiAnagraficiCessionarioType",DatiAnagraficiCessionarioType.class));
		this.CODICE_FISCALE = new Field("CodiceFiscale",java.lang.String.class,"DatiAnagraficiCessionarioType",DatiAnagraficiCessionarioType.class);
		this.ANAGRAFICA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.AnagraficaTypeModel(new Field("Anagrafica",it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType.class,"DatiAnagraficiCessionarioType",DatiAnagraficiCessionarioType.class));
	
	}
	
	public DatiAnagraficiCessionarioTypeModel(IField father){
	
		super(father);
	
		this.ID_FISCALE_IVA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.IdFiscaleTypeModel(new ComplexField(father,"IdFiscaleIVA",it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType.class,"DatiAnagraficiCessionarioType",DatiAnagraficiCessionarioType.class));
		this.CODICE_FISCALE = new ComplexField(father,"CodiceFiscale",java.lang.String.class,"DatiAnagraficiCessionarioType",DatiAnagraficiCessionarioType.class);
		this.ANAGRAFICA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.AnagraficaTypeModel(new ComplexField(father,"Anagrafica",it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType.class,"DatiAnagraficiCessionarioType",DatiAnagraficiCessionarioType.class));
	
	}
	
	

	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.IdFiscaleTypeModel ID_FISCALE_IVA = null;
	 
	public IField CODICE_FISCALE = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.AnagraficaTypeModel ANAGRAFICA = null;
	 

	@Override
	public Class<DatiAnagraficiCessionarioType> getModeledClass(){
		return DatiAnagraficiCessionarioType.class;
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