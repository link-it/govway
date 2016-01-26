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
package it.gov.fatturapa.sdi.messaggi.v1_0.model;

import it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RicevutaConsegnaType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicevutaConsegnaTypeModel extends AbstractModel<RicevutaConsegnaType> {

	public RicevutaConsegnaTypeModel(){
	
		super();
	
		this.IDENTIFICATIVO_SD_I = new Field("IdentificativoSdI",java.lang.Integer.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.NOME_FILE = new Field("NomeFile",java.lang.String.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.DATA_ORA_RICEZIONE = new Field("DataOraRicezione",java.util.Date.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.DATA_ORA_CONSEGNA = new Field("DataOraConsegna",java.util.Date.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.DESTINATARIO = new it.gov.fatturapa.sdi.messaggi.v1_0.model.DestinatarioTypeModel(new Field("Destinatario",it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class));
		this.RIFERIMENTO_ARCHIVIO = new it.gov.fatturapa.sdi.messaggi.v1_0.model.RiferimentoArchivioTypeModel(new Field("RiferimentoArchivio",it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class));
		this.MESSAGE_ID = new Field("MessageId",java.lang.String.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.PEC_MESSAGE_ID = new Field("PecMessageId",java.lang.String.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.NOTE = new Field("Note",java.lang.String.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.VERSIONE = new Field("versione",java.lang.String.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.INTERMEDIARIO_CON_DUPLICE_RUOLO = new Field("IntermediarioConDupliceRuolo",java.lang.String.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
	
	}
	
	public RicevutaConsegnaTypeModel(IField father){
	
		super(father);
	
		this.IDENTIFICATIVO_SD_I = new ComplexField(father,"IdentificativoSdI",java.lang.Integer.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.NOME_FILE = new ComplexField(father,"NomeFile",java.lang.String.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.DATA_ORA_RICEZIONE = new ComplexField(father,"DataOraRicezione",java.util.Date.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.DATA_ORA_CONSEGNA = new ComplexField(father,"DataOraConsegna",java.util.Date.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.DESTINATARIO = new it.gov.fatturapa.sdi.messaggi.v1_0.model.DestinatarioTypeModel(new ComplexField(father,"Destinatario",it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class));
		this.RIFERIMENTO_ARCHIVIO = new it.gov.fatturapa.sdi.messaggi.v1_0.model.RiferimentoArchivioTypeModel(new ComplexField(father,"RiferimentoArchivio",it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class));
		this.MESSAGE_ID = new ComplexField(father,"MessageId",java.lang.String.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.PEC_MESSAGE_ID = new ComplexField(father,"PecMessageId",java.lang.String.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.NOTE = new ComplexField(father,"Note",java.lang.String.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.String.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
		this.INTERMEDIARIO_CON_DUPLICE_RUOLO = new ComplexField(father,"IntermediarioConDupliceRuolo",java.lang.String.class,"RicevutaConsegna_Type",RicevutaConsegnaType.class);
	
	}
	
	

	public IField IDENTIFICATIVO_SD_I = null;
	 
	public IField NOME_FILE = null;
	 
	public IField DATA_ORA_RICEZIONE = null;
	 
	public IField DATA_ORA_CONSEGNA = null;
	 
	public it.gov.fatturapa.sdi.messaggi.v1_0.model.DestinatarioTypeModel DESTINATARIO = null;
	 
	public it.gov.fatturapa.sdi.messaggi.v1_0.model.RiferimentoArchivioTypeModel RIFERIMENTO_ARCHIVIO = null;
	 
	public IField MESSAGE_ID = null;
	 
	public IField PEC_MESSAGE_ID = null;
	 
	public IField NOTE = null;
	 
	public IField VERSIONE = null;
	 
	public IField INTERMEDIARIO_CON_DUPLICE_RUOLO = null;
	 

	@Override
	public Class<RicevutaConsegnaType> getModeledClass(){
		return RicevutaConsegnaType.class;
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