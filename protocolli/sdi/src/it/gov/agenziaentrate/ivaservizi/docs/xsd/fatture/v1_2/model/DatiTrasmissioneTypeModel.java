/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiTrasmissioneType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiTrasmissioneType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiTrasmissioneTypeModel extends AbstractModel<DatiTrasmissioneType> {

	public DatiTrasmissioneTypeModel(){
	
		super();
	
		this.ID_TRASMITTENTE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IdFiscaleTypeModel(new Field("IdTrasmittente",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IdFiscaleType.class,"DatiTrasmissioneType",DatiTrasmissioneType.class));
		this.PROGRESSIVO_INVIO = new Field("ProgressivoInvio",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
		this.FORMATO_TRASMISSIONE = new Field("FormatoTrasmissione",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
		this.CODICE_DESTINATARIO = new Field("CodiceDestinatario",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
		this.CONTATTI_TRASMITTENTE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.ContattiTrasmittenteTypeModel(new Field("ContattiTrasmittente",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.ContattiTrasmittenteType.class,"DatiTrasmissioneType",DatiTrasmissioneType.class));
		this.PECDESTINATARIO = new Field("PECDestinatario",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
	
	}
	
	public DatiTrasmissioneTypeModel(IField father){
	
		super(father);
	
		this.ID_TRASMITTENTE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IdFiscaleTypeModel(new ComplexField(father,"IdTrasmittente",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IdFiscaleType.class,"DatiTrasmissioneType",DatiTrasmissioneType.class));
		this.PROGRESSIVO_INVIO = new ComplexField(father,"ProgressivoInvio",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
		this.FORMATO_TRASMISSIONE = new ComplexField(father,"FormatoTrasmissione",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
		this.CODICE_DESTINATARIO = new ComplexField(father,"CodiceDestinatario",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
		this.CONTATTI_TRASMITTENTE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.ContattiTrasmittenteTypeModel(new ComplexField(father,"ContattiTrasmittente",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.ContattiTrasmittenteType.class,"DatiTrasmissioneType",DatiTrasmissioneType.class));
		this.PECDESTINATARIO = new ComplexField(father,"PECDestinatario",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
	
	}
	
	

	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IdFiscaleTypeModel ID_TRASMITTENTE = null;
	 
	public IField PROGRESSIVO_INVIO = null;
	 
	public IField FORMATO_TRASMISSIONE = null;
	 
	public IField CODICE_DESTINATARIO = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.ContattiTrasmittenteTypeModel CONTATTI_TRASMITTENTE = null;
	 
	public IField PECDESTINATARIO = null;
	 

	@Override
	public Class<DatiTrasmissioneType> getModeledClass(){
		return DatiTrasmissioneType.class;
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