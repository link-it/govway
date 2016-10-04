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

import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCedenteType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiAnagraficiCedenteType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiAnagraficiCedenteTypeModel extends AbstractModel<DatiAnagraficiCedenteType> {

	public DatiAnagraficiCedenteTypeModel(){
	
		super();
	
		this.ID_FISCALE_IVA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.IdFiscaleTypeModel(new Field("IdFiscaleIVA",it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class));
		this.CODICE_FISCALE = new Field("CodiceFiscale",java.lang.String.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class);
		this.ANAGRAFICA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.AnagraficaTypeModel(new Field("Anagrafica",it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class));
		this.ALBO_PROFESSIONALE = new Field("AlboProfessionale",java.lang.String.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class);
		this.PROVINCIA_ALBO = new Field("ProvinciaAlbo",java.lang.String.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class);
		this.NUMERO_ISCRIZIONE_ALBO = new Field("NumeroIscrizioneAlbo",java.lang.String.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class);
		this.DATA_ISCRIZIONE_ALBO = new Field("DataIscrizioneAlbo",java.util.Date.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class);
		this.REGIME_FISCALE = new Field("RegimeFiscale",java.lang.String.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class);
	
	}
	
	public DatiAnagraficiCedenteTypeModel(IField father){
	
		super(father);
	
		this.ID_FISCALE_IVA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.IdFiscaleTypeModel(new ComplexField(father,"IdFiscaleIVA",it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class));
		this.CODICE_FISCALE = new ComplexField(father,"CodiceFiscale",java.lang.String.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class);
		this.ANAGRAFICA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.AnagraficaTypeModel(new ComplexField(father,"Anagrafica",it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class));
		this.ALBO_PROFESSIONALE = new ComplexField(father,"AlboProfessionale",java.lang.String.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class);
		this.PROVINCIA_ALBO = new ComplexField(father,"ProvinciaAlbo",java.lang.String.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class);
		this.NUMERO_ISCRIZIONE_ALBO = new ComplexField(father,"NumeroIscrizioneAlbo",java.lang.String.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class);
		this.DATA_ISCRIZIONE_ALBO = new ComplexField(father,"DataIscrizioneAlbo",java.util.Date.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class);
		this.REGIME_FISCALE = new ComplexField(father,"RegimeFiscale",java.lang.String.class,"DatiAnagraficiCedenteType",DatiAnagraficiCedenteType.class);
	
	}
	
	

	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.IdFiscaleTypeModel ID_FISCALE_IVA = null;
	 
	public IField CODICE_FISCALE = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.AnagraficaTypeModel ANAGRAFICA = null;
	 
	public IField ALBO_PROFESSIONALE = null;
	 
	public IField PROVINCIA_ALBO = null;
	 
	public IField NUMERO_ISCRIZIONE_ALBO = null;
	 
	public IField DATA_ISCRIZIONE_ALBO = null;
	 
	public IField REGIME_FISCALE = null;
	 

	@Override
	public Class<DatiAnagraficiCedenteType> getModeledClass(){
		return DatiAnagraficiCedenteType.class;
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