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

import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model NotificaEsitoCommittenteType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotificaEsitoCommittenteTypeModel extends AbstractModel<NotificaEsitoCommittenteType> {

	public NotificaEsitoCommittenteTypeModel(){
	
		super();
	
		this.IDENTIFICATIVO_SD_I = new Field("IdentificativoSdI",java.lang.Integer.class,"NotificaEsitoCommittente_Type",NotificaEsitoCommittenteType.class);
		this.RIFERIMENTO_FATTURA = new it.gov.fatturapa.sdi.messaggi.v1_0.model.RiferimentoFatturaTypeModel(new Field("RiferimentoFattura",it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType.class,"NotificaEsitoCommittente_Type",NotificaEsitoCommittenteType.class));
		this.ESITO = new Field("Esito",java.lang.String.class,"NotificaEsitoCommittente_Type",NotificaEsitoCommittenteType.class);
		this.DESCRIZIONE = new Field("Descrizione",java.lang.String.class,"NotificaEsitoCommittente_Type",NotificaEsitoCommittenteType.class);
		this.MESSAGE_ID_COMMITTENTE = new Field("MessageIdCommittente",java.lang.String.class,"NotificaEsitoCommittente_Type",NotificaEsitoCommittenteType.class);
		this.VERSIONE = new Field("versione",java.lang.String.class,"NotificaEsitoCommittente_Type",NotificaEsitoCommittenteType.class);
	
	}
	
	public NotificaEsitoCommittenteTypeModel(IField father){
	
		super(father);
	
		this.IDENTIFICATIVO_SD_I = new ComplexField(father,"IdentificativoSdI",java.lang.Integer.class,"NotificaEsitoCommittente_Type",NotificaEsitoCommittenteType.class);
		this.RIFERIMENTO_FATTURA = new it.gov.fatturapa.sdi.messaggi.v1_0.model.RiferimentoFatturaTypeModel(new ComplexField(father,"RiferimentoFattura",it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType.class,"NotificaEsitoCommittente_Type",NotificaEsitoCommittenteType.class));
		this.ESITO = new ComplexField(father,"Esito",java.lang.String.class,"NotificaEsitoCommittente_Type",NotificaEsitoCommittenteType.class);
		this.DESCRIZIONE = new ComplexField(father,"Descrizione",java.lang.String.class,"NotificaEsitoCommittente_Type",NotificaEsitoCommittenteType.class);
		this.MESSAGE_ID_COMMITTENTE = new ComplexField(father,"MessageIdCommittente",java.lang.String.class,"NotificaEsitoCommittente_Type",NotificaEsitoCommittenteType.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.String.class,"NotificaEsitoCommittente_Type",NotificaEsitoCommittenteType.class);
	
	}
	
	

	public IField IDENTIFICATIVO_SD_I = null;
	 
	public it.gov.fatturapa.sdi.messaggi.v1_0.model.RiferimentoFatturaTypeModel RIFERIMENTO_FATTURA = null;
	 
	public IField ESITO = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField MESSAGE_ID_COMMITTENTE = null;
	 
	public IField VERSIONE = null;
	 

	@Override
	public Class<NotificaEsitoCommittenteType> getModeledClass(){
		return NotificaEsitoCommittenteType.class;
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