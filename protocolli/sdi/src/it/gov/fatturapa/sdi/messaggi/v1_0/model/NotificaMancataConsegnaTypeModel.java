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

import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model NotificaMancataConsegnaType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotificaMancataConsegnaTypeModel extends AbstractModel<NotificaMancataConsegnaType> {

	public NotificaMancataConsegnaTypeModel(){
	
		super();
	
		this.IDENTIFICATIVO_SD_I = new Field("IdentificativoSdI",java.lang.Integer.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.NOME_FILE = new Field("NomeFile",java.lang.String.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.DATA_ORA_RICEZIONE = new Field("DataOraRicezione",java.util.Date.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.RIFERIMENTO_ARCHIVIO = new it.gov.fatturapa.sdi.messaggi.v1_0.model.RiferimentoArchivioTypeModel(new Field("RiferimentoArchivio",it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class));
		this.DESCRIZIONE = new Field("Descrizione",java.lang.String.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.MESSAGE_ID = new Field("MessageId",java.lang.String.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.PEC_MESSAGE_ID = new Field("PecMessageId",java.lang.String.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.NOTE = new Field("Note",java.lang.String.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.VERSIONE = new Field("versione",java.lang.String.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
	
	}
	
	public NotificaMancataConsegnaTypeModel(IField father){
	
		super(father);
	
		this.IDENTIFICATIVO_SD_I = new ComplexField(father,"IdentificativoSdI",java.lang.Integer.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.NOME_FILE = new ComplexField(father,"NomeFile",java.lang.String.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.DATA_ORA_RICEZIONE = new ComplexField(father,"DataOraRicezione",java.util.Date.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.RIFERIMENTO_ARCHIVIO = new it.gov.fatturapa.sdi.messaggi.v1_0.model.RiferimentoArchivioTypeModel(new ComplexField(father,"RiferimentoArchivio",it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class));
		this.DESCRIZIONE = new ComplexField(father,"Descrizione",java.lang.String.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.MESSAGE_ID = new ComplexField(father,"MessageId",java.lang.String.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.PEC_MESSAGE_ID = new ComplexField(father,"PecMessageId",java.lang.String.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.NOTE = new ComplexField(father,"Note",java.lang.String.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.String.class,"NotificaMancataConsegna_Type",NotificaMancataConsegnaType.class);
	
	}
	
	

	public IField IDENTIFICATIVO_SD_I = null;
	 
	public IField NOME_FILE = null;
	 
	public IField DATA_ORA_RICEZIONE = null;
	 
	public it.gov.fatturapa.sdi.messaggi.v1_0.model.RiferimentoArchivioTypeModel RIFERIMENTO_ARCHIVIO = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField MESSAGE_ID = null;
	 
	public IField PEC_MESSAGE_ID = null;
	 
	public IField NOTE = null;
	 
	public IField VERSIONE = null;
	 

	@Override
	public Class<NotificaMancataConsegnaType> getModeledClass(){
		return NotificaMancataConsegnaType.class;
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