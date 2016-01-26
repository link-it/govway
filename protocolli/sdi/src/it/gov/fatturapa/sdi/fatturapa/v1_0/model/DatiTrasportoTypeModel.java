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

import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasportoType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiTrasportoType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiTrasportoTypeModel extends AbstractModel<DatiTrasportoType> {

	public DatiTrasportoTypeModel(){
	
		super();
	
		this.DATI_ANAGRAFICI_VETTORE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiAnagraficiVettoreTypeModel(new Field("DatiAnagraficiVettore",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiVettoreType.class,"DatiTrasportoType",DatiTrasportoType.class));
		this.MEZZO_TRASPORTO = new Field("MezzoTrasporto",java.lang.String.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.CAUSALE_TRASPORTO = new Field("CausaleTrasporto",java.lang.String.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.NUMERO_COLLI = new Field("NumeroColli",java.lang.Integer.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.DESCRIZIONE = new Field("Descrizione",java.lang.String.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.UNITA_MISURA_PESO = new Field("UnitaMisuraPeso",java.lang.String.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.PESO_LORDO = new Field("PesoLordo",java.lang.Double.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.PESO_NETTO = new Field("PesoNetto",java.lang.Double.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.DATA_ORA_RITIRO = new Field("DataOraRitiro",java.util.Date.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.DATA_INIZIO_TRASPORTO = new Field("DataInizioTrasporto",java.util.Date.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.TIPO_RESA = new Field("TipoResa",java.lang.String.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.INDIRIZZO_RESA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.IndirizzoTypeModel(new Field("IndirizzoResa",it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType.class,"DatiTrasportoType",DatiTrasportoType.class));
		this.DATA_ORA_CONSEGNA = new Field("DataOraConsegna",java.util.Date.class,"DatiTrasportoType",DatiTrasportoType.class);
	
	}
	
	public DatiTrasportoTypeModel(IField father){
	
		super(father);
	
		this.DATI_ANAGRAFICI_VETTORE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiAnagraficiVettoreTypeModel(new ComplexField(father,"DatiAnagraficiVettore",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiVettoreType.class,"DatiTrasportoType",DatiTrasportoType.class));
		this.MEZZO_TRASPORTO = new ComplexField(father,"MezzoTrasporto",java.lang.String.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.CAUSALE_TRASPORTO = new ComplexField(father,"CausaleTrasporto",java.lang.String.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.NUMERO_COLLI = new ComplexField(father,"NumeroColli",java.lang.Integer.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.DESCRIZIONE = new ComplexField(father,"Descrizione",java.lang.String.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.UNITA_MISURA_PESO = new ComplexField(father,"UnitaMisuraPeso",java.lang.String.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.PESO_LORDO = new ComplexField(father,"PesoLordo",java.lang.Double.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.PESO_NETTO = new ComplexField(father,"PesoNetto",java.lang.Double.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.DATA_ORA_RITIRO = new ComplexField(father,"DataOraRitiro",java.util.Date.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.DATA_INIZIO_TRASPORTO = new ComplexField(father,"DataInizioTrasporto",java.util.Date.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.TIPO_RESA = new ComplexField(father,"TipoResa",java.lang.String.class,"DatiTrasportoType",DatiTrasportoType.class);
		this.INDIRIZZO_RESA = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.IndirizzoTypeModel(new ComplexField(father,"IndirizzoResa",it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType.class,"DatiTrasportoType",DatiTrasportoType.class));
		this.DATA_ORA_CONSEGNA = new ComplexField(father,"DataOraConsegna",java.util.Date.class,"DatiTrasportoType",DatiTrasportoType.class);
	
	}
	
	

	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiAnagraficiVettoreTypeModel DATI_ANAGRAFICI_VETTORE = null;
	 
	public IField MEZZO_TRASPORTO = null;
	 
	public IField CAUSALE_TRASPORTO = null;
	 
	public IField NUMERO_COLLI = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField UNITA_MISURA_PESO = null;
	 
	public IField PESO_LORDO = null;
	 
	public IField PESO_NETTO = null;
	 
	public IField DATA_ORA_RITIRO = null;
	 
	public IField DATA_INIZIO_TRASPORTO = null;
	 
	public IField TIPO_RESA = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.IndirizzoTypeModel INDIRIZZO_RESA = null;
	 
	public IField DATA_ORA_CONSEGNA = null;
	 

	@Override
	public Class<DatiTrasportoType> getModeledClass(){
		return DatiTrasportoType.class;
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