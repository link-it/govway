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
package it.gov.fatturapa.sdi.messaggi.v1_0.model;

import it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ScartoEsitoCommittenteType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ScartoEsitoCommittenteTypeModel extends AbstractModel<ScartoEsitoCommittenteType> {

	public ScartoEsitoCommittenteTypeModel(){
	
		super();
	
		this.IDENTIFICATIVO_SD_I = new Field("IdentificativoSdI",java.lang.Integer.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
		this.RIFERIMENTO_FATTURA = new it.gov.fatturapa.sdi.messaggi.v1_0.model.RiferimentoFatturaTypeModel(new Field("RiferimentoFattura",it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class));
		this.SCARTO = new Field("Scarto",java.lang.String.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
		this.MESSAGE_ID = new Field("MessageId",java.lang.String.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
		this.MESSAGE_ID_COMMITTENTE = new Field("MessageIdCommittente",java.lang.String.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
		this.PEC_MESSAGE_ID = new Field("PecMessageId",java.lang.String.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
		this.NOTE = new Field("Note",java.lang.String.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
		this.VERSIONE = new Field("versione",java.lang.String.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
	
	}
	
	public ScartoEsitoCommittenteTypeModel(IField father){
	
		super(father);
	
		this.IDENTIFICATIVO_SD_I = new ComplexField(father,"IdentificativoSdI",java.lang.Integer.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
		this.RIFERIMENTO_FATTURA = new it.gov.fatturapa.sdi.messaggi.v1_0.model.RiferimentoFatturaTypeModel(new ComplexField(father,"RiferimentoFattura",it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class));
		this.SCARTO = new ComplexField(father,"Scarto",java.lang.String.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
		this.MESSAGE_ID = new ComplexField(father,"MessageId",java.lang.String.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
		this.MESSAGE_ID_COMMITTENTE = new ComplexField(father,"MessageIdCommittente",java.lang.String.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
		this.PEC_MESSAGE_ID = new ComplexField(father,"PecMessageId",java.lang.String.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
		this.NOTE = new ComplexField(father,"Note",java.lang.String.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.String.class,"ScartoEsitoCommittente_Type",ScartoEsitoCommittenteType.class);
	
	}
	
	

	public IField IDENTIFICATIVO_SD_I = null;
	 
	public it.gov.fatturapa.sdi.messaggi.v1_0.model.RiferimentoFatturaTypeModel RIFERIMENTO_FATTURA = null;
	 
	public IField SCARTO = null;
	 
	public IField MESSAGE_ID = null;
	 
	public IField MESSAGE_ID_COMMITTENTE = null;
	 
	public IField PEC_MESSAGE_ID = null;
	 
	public IField NOTE = null;
	 
	public IField VERSIONE = null;
	 

	@Override
	public Class<ScartoEsitoCommittenteType> getModeledClass(){
		return ScartoEsitoCommittenteType.class;
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