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
package it.gov.fatturapa.sdi.fatturapa.v1_0.model;

import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiAnagraficiRappresentanteType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiAnagraficiRappresentanteTypeModel extends AbstractModel<DatiAnagraficiRappresentanteType> {

	public DatiAnagraficiRappresentanteTypeModel(){
	
		super();
	
		this.ID_FISCALE_IVA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.IdFiscaleTypeModel(new Field("IdFiscaleIVA",it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType.class,"DatiAnagraficiRappresentanteType",DatiAnagraficiRappresentanteType.class));
		this.CODICE_FISCALE = new Field("CodiceFiscale",java.lang.String.class,"DatiAnagraficiRappresentanteType",DatiAnagraficiRappresentanteType.class);
		this.ANAGRAFICA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.AnagraficaTypeModel(new Field("Anagrafica",it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType.class,"DatiAnagraficiRappresentanteType",DatiAnagraficiRappresentanteType.class));
	
	}
	
	public DatiAnagraficiRappresentanteTypeModel(IField father){
	
		super(father);
	
		this.ID_FISCALE_IVA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.IdFiscaleTypeModel(new ComplexField(father,"IdFiscaleIVA",it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType.class,"DatiAnagraficiRappresentanteType",DatiAnagraficiRappresentanteType.class));
		this.CODICE_FISCALE = new ComplexField(father,"CodiceFiscale",java.lang.String.class,"DatiAnagraficiRappresentanteType",DatiAnagraficiRappresentanteType.class);
		this.ANAGRAFICA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.AnagraficaTypeModel(new ComplexField(father,"Anagrafica",it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType.class,"DatiAnagraficiRappresentanteType",DatiAnagraficiRappresentanteType.class));
	
	}
	
	

	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.IdFiscaleTypeModel ID_FISCALE_IVA = null;
	 
	public IField CODICE_FISCALE = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.AnagraficaTypeModel ANAGRAFICA = null;
	 

	@Override
	public Class<DatiAnagraficiRappresentanteType> getModeledClass(){
		return DatiAnagraficiRappresentanteType.class;
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