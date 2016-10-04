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

import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasmissioneType;

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
	
		this.ID_TRASMITTENTE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.IdFiscaleTypeModel(new Field("IdTrasmittente",it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType.class,"DatiTrasmissioneType",DatiTrasmissioneType.class));
		this.PROGRESSIVO_INVIO = new Field("ProgressivoInvio",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
		this.FORMATO_TRASMISSIONE = new Field("FormatoTrasmissione",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
		this.CODICE_DESTINATARIO = new Field("CodiceDestinatario",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
		this.CONTATTI_TRASMITTENTE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.ContattiTrasmittenteTypeModel(new Field("ContattiTrasmittente",it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiTrasmittenteType.class,"DatiTrasmissioneType",DatiTrasmissioneType.class));
	
	}
	
	public DatiTrasmissioneTypeModel(IField father){
	
		super(father);
	
		this.ID_TRASMITTENTE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.IdFiscaleTypeModel(new ComplexField(father,"IdTrasmittente",it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType.class,"DatiTrasmissioneType",DatiTrasmissioneType.class));
		this.PROGRESSIVO_INVIO = new ComplexField(father,"ProgressivoInvio",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
		this.FORMATO_TRASMISSIONE = new ComplexField(father,"FormatoTrasmissione",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
		this.CODICE_DESTINATARIO = new ComplexField(father,"CodiceDestinatario",java.lang.String.class,"DatiTrasmissioneType",DatiTrasmissioneType.class);
		this.CONTATTI_TRASMITTENTE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.ContattiTrasmittenteTypeModel(new ComplexField(father,"ContattiTrasmittente",it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiTrasmittenteType.class,"DatiTrasmissioneType",DatiTrasmissioneType.class));
	
	}
	
	

	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.IdFiscaleTypeModel ID_TRASMITTENTE = null;
	 
	public IField PROGRESSIVO_INVIO = null;
	 
	public IField FORMATO_TRASMISSIONE = null;
	 
	public IField CODICE_DESTINATARIO = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.ContattiTrasmittenteTypeModel CONTATTI_TRASMITTENTE = null;
	 

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