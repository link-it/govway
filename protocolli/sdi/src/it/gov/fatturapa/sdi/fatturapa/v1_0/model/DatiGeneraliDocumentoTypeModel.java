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

import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliDocumentoType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiGeneraliDocumentoType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiGeneraliDocumentoTypeModel extends AbstractModel<DatiGeneraliDocumentoType> {

	public DatiGeneraliDocumentoTypeModel(){
	
		super();
	
		this.TIPO_DOCUMENTO = new Field("TipoDocumento",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.DIVISA = new Field("Divisa",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.DATA = new Field("Data",java.util.Date.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.NUMERO = new Field("Numero",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.DATI_RITENUTA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiRitenutaTypeModel(new Field("DatiRitenuta",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRitenutaType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.DATI_BOLLO = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiBolloTypeModel(new Field("DatiBollo",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.DATI_CASSA_PREVIDENZIALE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiCassaPrevidenzialeTypeModel(new Field("DatiCassaPrevidenziale",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiCassaPrevidenzialeType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.SCONTO_MAGGIORAZIONE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.ScontoMaggiorazioneTypeModel(new Field("ScontoMaggiorazione",it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.IMPORTO_TOTALE_DOCUMENTO = new Field("ImportoTotaleDocumento",java.lang.Double.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.ARROTONDAMENTO = new Field("Arrotondamento",java.lang.Double.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.CAUSALE = new Field("Causale",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.ART_73 = new Field("Art73",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
	
	}
	
	public DatiGeneraliDocumentoTypeModel(IField father){
	
		super(father);
	
		this.TIPO_DOCUMENTO = new ComplexField(father,"TipoDocumento",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.DIVISA = new ComplexField(father,"Divisa",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.DATA = new ComplexField(father,"Data",java.util.Date.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.NUMERO = new ComplexField(father,"Numero",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.DATI_RITENUTA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiRitenutaTypeModel(new ComplexField(father,"DatiRitenuta",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRitenutaType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.DATI_BOLLO = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiBolloTypeModel(new ComplexField(father,"DatiBollo",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.DATI_CASSA_PREVIDENZIALE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiCassaPrevidenzialeTypeModel(new ComplexField(father,"DatiCassaPrevidenziale",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiCassaPrevidenzialeType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.SCONTO_MAGGIORAZIONE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.ScontoMaggiorazioneTypeModel(new ComplexField(father,"ScontoMaggiorazione",it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.IMPORTO_TOTALE_DOCUMENTO = new ComplexField(father,"ImportoTotaleDocumento",java.lang.Double.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.ARROTONDAMENTO = new ComplexField(father,"Arrotondamento",java.lang.Double.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.CAUSALE = new ComplexField(father,"Causale",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.ART_73 = new ComplexField(father,"Art73",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
	
	}
	
	

	public IField TIPO_DOCUMENTO = null;
	 
	public IField DIVISA = null;
	 
	public IField DATA = null;
	 
	public IField NUMERO = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiRitenutaTypeModel DATI_RITENUTA = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiBolloTypeModel DATI_BOLLO = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiCassaPrevidenzialeTypeModel DATI_CASSA_PREVIDENZIALE = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.ScontoMaggiorazioneTypeModel SCONTO_MAGGIORAZIONE = null;
	 
	public IField IMPORTO_TOTALE_DOCUMENTO = null;
	 
	public IField ARROTONDAMENTO = null;
	 
	public IField CAUSALE = null;
	 
	public IField ART_73 = null;
	 

	@Override
	public Class<DatiGeneraliDocumentoType> getModeledClass(){
		return DatiGeneraliDocumentoType.class;
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