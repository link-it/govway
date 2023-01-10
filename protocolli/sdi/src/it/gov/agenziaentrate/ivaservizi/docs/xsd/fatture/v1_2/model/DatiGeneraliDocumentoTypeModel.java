/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiGeneraliDocumentoType;

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
		this.DATI_RITENUTA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiRitenutaTypeModel(new Field("DatiRitenuta",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiRitenutaType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.DATI_BOLLO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiBolloTypeModel(new Field("DatiBollo",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiBolloType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.DATI_CASSA_PREVIDENZIALE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiCassaPrevidenzialeTypeModel(new Field("DatiCassaPrevidenziale",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiCassaPrevidenzialeType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.SCONTO_MAGGIORAZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.ScontoMaggiorazioneTypeModel(new Field("ScontoMaggiorazione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.ScontoMaggiorazioneType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.IMPORTO_TOTALE_DOCUMENTO = new Field("ImportoTotaleDocumento",java.math.BigDecimal.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.ARROTONDAMENTO = new Field("Arrotondamento",java.math.BigDecimal.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.CAUSALE = new Field("Causale",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.ART_73 = new Field("Art73",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
	
	}
	
	public DatiGeneraliDocumentoTypeModel(IField father){
	
		super(father);
	
		this.TIPO_DOCUMENTO = new ComplexField(father,"TipoDocumento",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.DIVISA = new ComplexField(father,"Divisa",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.DATA = new ComplexField(father,"Data",java.util.Date.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.NUMERO = new ComplexField(father,"Numero",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.DATI_RITENUTA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiRitenutaTypeModel(new ComplexField(father,"DatiRitenuta",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiRitenutaType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.DATI_BOLLO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiBolloTypeModel(new ComplexField(father,"DatiBollo",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiBolloType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.DATI_CASSA_PREVIDENZIALE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiCassaPrevidenzialeTypeModel(new ComplexField(father,"DatiCassaPrevidenziale",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiCassaPrevidenzialeType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.SCONTO_MAGGIORAZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.ScontoMaggiorazioneTypeModel(new ComplexField(father,"ScontoMaggiorazione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.ScontoMaggiorazioneType.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class));
		this.IMPORTO_TOTALE_DOCUMENTO = new ComplexField(father,"ImportoTotaleDocumento",java.math.BigDecimal.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.ARROTONDAMENTO = new ComplexField(father,"Arrotondamento",java.math.BigDecimal.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.CAUSALE = new ComplexField(father,"Causale",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.ART_73 = new ComplexField(father,"Art73",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
	
	}
	
	

	public IField TIPO_DOCUMENTO = null;
	 
	public IField DIVISA = null;
	 
	public IField DATA = null;
	 
	public IField NUMERO = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiRitenutaTypeModel DATI_RITENUTA = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiBolloTypeModel DATI_BOLLO = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiCassaPrevidenzialeTypeModel DATI_CASSA_PREVIDENZIALE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.ScontoMaggiorazioneTypeModel SCONTO_MAGGIORAZIONE = null;
	 
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