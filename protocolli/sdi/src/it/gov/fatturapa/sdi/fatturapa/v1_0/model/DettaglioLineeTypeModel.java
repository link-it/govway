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

import it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DettaglioLineeType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DettaglioLineeTypeModel extends AbstractModel<DettaglioLineeType> {

	public DettaglioLineeTypeModel(){
	
		super();
	
		this.NUMERO_LINEA = new Field("NumeroLinea",java.lang.Integer.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.TIPO_CESSIONE_PRESTAZIONE = new Field("TipoCessionePrestazione",java.lang.String.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.CODICE_ARTICOLO = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.CodiceArticoloTypeModel(new Field("CodiceArticolo",it.gov.fatturapa.sdi.fatturapa.v1_0.CodiceArticoloType.class,"DettaglioLineeType",DettaglioLineeType.class));
		this.DESCRIZIONE = new Field("Descrizione",java.lang.String.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.QUANTITA = new Field("Quantita",java.lang.Double.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.UNITA_MISURA = new Field("UnitaMisura",java.lang.String.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.DATA_INIZIO_PERIODO = new Field("DataInizioPeriodo",java.util.Date.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.DATA_FINE_PERIODO = new Field("DataFinePeriodo",java.util.Date.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.PREZZO_UNITARIO = new Field("PrezzoUnitario",java.lang.Double.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.SCONTO_MAGGIORAZIONE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.ScontoMaggiorazioneTypeModel(new Field("ScontoMaggiorazione",it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType.class,"DettaglioLineeType",DettaglioLineeType.class));
		this.PREZZO_TOTALE = new Field("PrezzoTotale",java.lang.Double.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.ALIQUOTA_IVA = new Field("AliquotaIVA",java.lang.Double.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.RITENUTA = new Field("Ritenuta",java.lang.String.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.NATURA = new Field("Natura",java.lang.String.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.RIFERIMENTO_AMMINISTRAZIONE = new Field("RiferimentoAmministrazione",java.lang.String.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.ALTRI_DATI_GESTIONALI = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.AltriDatiGestionaliTypeModel(new Field("AltriDatiGestionali",it.gov.fatturapa.sdi.fatturapa.v1_0.AltriDatiGestionaliType.class,"DettaglioLineeType",DettaglioLineeType.class));
	
	}
	
	public DettaglioLineeTypeModel(IField father){
	
		super(father);
	
		this.NUMERO_LINEA = new ComplexField(father,"NumeroLinea",java.lang.Integer.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.TIPO_CESSIONE_PRESTAZIONE = new ComplexField(father,"TipoCessionePrestazione",java.lang.String.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.CODICE_ARTICOLO = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.CodiceArticoloTypeModel(new ComplexField(father,"CodiceArticolo",it.gov.fatturapa.sdi.fatturapa.v1_0.CodiceArticoloType.class,"DettaglioLineeType",DettaglioLineeType.class));
		this.DESCRIZIONE = new ComplexField(father,"Descrizione",java.lang.String.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.QUANTITA = new ComplexField(father,"Quantita",java.lang.Double.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.UNITA_MISURA = new ComplexField(father,"UnitaMisura",java.lang.String.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.DATA_INIZIO_PERIODO = new ComplexField(father,"DataInizioPeriodo",java.util.Date.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.DATA_FINE_PERIODO = new ComplexField(father,"DataFinePeriodo",java.util.Date.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.PREZZO_UNITARIO = new ComplexField(father,"PrezzoUnitario",java.lang.Double.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.SCONTO_MAGGIORAZIONE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.ScontoMaggiorazioneTypeModel(new ComplexField(father,"ScontoMaggiorazione",it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType.class,"DettaglioLineeType",DettaglioLineeType.class));
		this.PREZZO_TOTALE = new ComplexField(father,"PrezzoTotale",java.lang.Double.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.ALIQUOTA_IVA = new ComplexField(father,"AliquotaIVA",java.lang.Double.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.RITENUTA = new ComplexField(father,"Ritenuta",java.lang.String.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.NATURA = new ComplexField(father,"Natura",java.lang.String.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.RIFERIMENTO_AMMINISTRAZIONE = new ComplexField(father,"RiferimentoAmministrazione",java.lang.String.class,"DettaglioLineeType",DettaglioLineeType.class);
		this.ALTRI_DATI_GESTIONALI = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.AltriDatiGestionaliTypeModel(new ComplexField(father,"AltriDatiGestionali",it.gov.fatturapa.sdi.fatturapa.v1_0.AltriDatiGestionaliType.class,"DettaglioLineeType",DettaglioLineeType.class));
	
	}
	
	

	public IField NUMERO_LINEA = null;
	 
	public IField TIPO_CESSIONE_PRESTAZIONE = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.CodiceArticoloTypeModel CODICE_ARTICOLO = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField QUANTITA = null;
	 
	public IField UNITA_MISURA = null;
	 
	public IField DATA_INIZIO_PERIODO = null;
	 
	public IField DATA_FINE_PERIODO = null;
	 
	public IField PREZZO_UNITARIO = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.ScontoMaggiorazioneTypeModel SCONTO_MAGGIORAZIONE = null;
	 
	public IField PREZZO_TOTALE = null;
	 
	public IField ALIQUOTA_IVA = null;
	 
	public IField RITENUTA = null;
	 
	public IField NATURA = null;
	 
	public IField RIFERIMENTO_AMMINISTRAZIONE = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.AltriDatiGestionaliTypeModel ALTRI_DATI_GESTIONALI = null;
	 

	@Override
	public Class<DettaglioLineeType> getModeledClass(){
		return DettaglioLineeType.class;
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