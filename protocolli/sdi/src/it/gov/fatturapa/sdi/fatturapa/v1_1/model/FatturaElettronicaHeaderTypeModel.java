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
package it.gov.fatturapa.sdi.fatturapa.v1_1.model;

import it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model FatturaElettronicaHeaderType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FatturaElettronicaHeaderTypeModel extends AbstractModel<FatturaElettronicaHeaderType> {

	public FatturaElettronicaHeaderTypeModel(){
	
		super();
	
		this.DATI_TRASMISSIONE = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiTrasmissioneTypeModel(new Field("DatiTrasmissione",it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.CEDENTE_PRESTATORE = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.CedentePrestatoreTypeModel(new Field("CedentePrestatore",it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.RAPPRESENTANTE_FISCALE = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.RappresentanteFiscaleTypeModel(new Field("RappresentanteFiscale",it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.CESSIONARIO_COMMITTENTE = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.CessionarioCommittenteTypeModel(new Field("CessionarioCommittente",it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.TERZO_INTERMEDIARIO_OSOGGETTO_EMITTENTE = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.TerzoIntermediarioSoggettoEmittenteTypeModel(new Field("TerzoIntermediarioOSoggettoEmittente",it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.SOGGETTO_EMITTENTE = new Field("SoggettoEmittente",java.lang.String.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class);
	
	}
	
	public FatturaElettronicaHeaderTypeModel(IField father){
	
		super(father);
	
		this.DATI_TRASMISSIONE = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiTrasmissioneTypeModel(new ComplexField(father,"DatiTrasmissione",it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.CEDENTE_PRESTATORE = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.CedentePrestatoreTypeModel(new ComplexField(father,"CedentePrestatore",it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.RAPPRESENTANTE_FISCALE = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.RappresentanteFiscaleTypeModel(new ComplexField(father,"RappresentanteFiscale",it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.CESSIONARIO_COMMITTENTE = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.CessionarioCommittenteTypeModel(new ComplexField(father,"CessionarioCommittente",it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.TERZO_INTERMEDIARIO_OSOGGETTO_EMITTENTE = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.TerzoIntermediarioSoggettoEmittenteTypeModel(new ComplexField(father,"TerzoIntermediarioOSoggettoEmittente",it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.SOGGETTO_EMITTENTE = new ComplexField(father,"SoggettoEmittente",java.lang.String.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class);
	
	}
	
	

	public it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiTrasmissioneTypeModel DATI_TRASMISSIONE = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_1.model.CedentePrestatoreTypeModel CEDENTE_PRESTATORE = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_1.model.RappresentanteFiscaleTypeModel RAPPRESENTANTE_FISCALE = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_1.model.CessionarioCommittenteTypeModel CESSIONARIO_COMMITTENTE = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_1.model.TerzoIntermediarioSoggettoEmittenteTypeModel TERZO_INTERMEDIARIO_OSOGGETTO_EMITTENTE = null;
	 
	public IField SOGGETTO_EMITTENTE = null;
	 

	@Override
	public Class<FatturaElettronicaHeaderType> getModeledClass(){
		return FatturaElettronicaHeaderType.class;
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