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

/**     
 * Factory
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModelFactory {

	public static RicevutaConsegnaTypeModel RICEVUTA_CONSEGNA_TYPE = new RicevutaConsegnaTypeModel();
	
	public static NotificaMancataConsegnaTypeModel NOTIFICA_MANCATA_CONSEGNA_TYPE = new NotificaMancataConsegnaTypeModel();
	
	public static NotificaScartoTypeModel NOTIFICA_SCARTO_TYPE = new NotificaScartoTypeModel();
	
	public static NotificaEsitoTypeModel NOTIFICA_ESITO_TYPE = new NotificaEsitoTypeModel();
	
	public static AttestazioneTrasmissioneFatturaTypeModel ATTESTAZIONE_TRASMISSIONE_FATTURA_TYPE = new AttestazioneTrasmissioneFatturaTypeModel();
	
	public static MetadatiInvioFileTypeModel METADATI_INVIO_FILE_TYPE = new MetadatiInvioFileTypeModel();
	
	public static NotificaEsitoCommittenteTypeModel NOTIFICA_ESITO_COMMITTENTE_TYPE = new NotificaEsitoCommittenteTypeModel();
	
	public static ScartoEsitoCommittenteTypeModel SCARTO_ESITO_COMMITTENTE_TYPE = new ScartoEsitoCommittenteTypeModel();
	
	public static NotificaDecorrenzaTerminiTypeModel NOTIFICA_DECORRENZA_TERMINI_TYPE = new NotificaDecorrenzaTerminiTypeModel();
	

}