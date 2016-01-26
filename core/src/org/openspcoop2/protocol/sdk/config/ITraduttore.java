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

package org.openspcoop2.protocol.sdk.config;

import java.util.Date;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.SubCodiceErrore;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;

/**
 * Interfaccia di traduzione delle costanti utilizzate nel Protocollo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ITraduttore {
	
	public IProtocolFactory getProtocolFactory();
		
	/**
	 * Traduce il valore del parametro di inoltro in un formato conforme alle specifiche del protocollo
	 * @param inoltro Enumeration di inoltro
	 * @return Stringa Rappresentazione dell'inoltro secondo la specifica.
	 */
	public String toString(Inoltro inoltro);
	
	/**
	 * Traduce il valore del parametro del profilo di collaborazione in un formato conforme alle specifiche del protocollo
	 * @param profilo Enumeration di profilo
	 * @return Stringa Rappresentazione del profilo secondo la specifica.
	 */
	public String toString(ProfiloDiCollaborazione profilo);
	
	/**
	 * Traduce il tipo dell'ora di registrazione in un formato conforme alle specifiche del protocollo
	 * @param tipo Enumeration di tipoOra
	 * @return Stringa che rappresenta il tipo di ora secondo la specifica
	 */
	public String toString(TipoOraRegistrazione tipo);
	
	/**
	 * Traduce la rilevanza in un formato conforme alle specifiche del protocollo
	 * @param rilevanza Livello della rilevanza
	 * @return Stringa che rappresenta il tipo di ora secondo la specifica
	 */
	public String toString(LivelloRilevanza rilevanza);
	
	/**
	 * Traduce il contesto in un formato conforme alle specifiche del protocollo
	 * @param contesto ContestoCodifica
	 * @return Stringa che rappresenta il tipo di ora secondo la specifica
	 */
	public String toString(ContestoCodificaEccezione contesto);
	
	/**
	 * Fornisce la stringa di errore, riguardante un faultString, prevista in caso di errore di cooperazione per il protocollo in uso
	 * @param msg Messaggio fault per quanto concerne un errore cooperazione
	 * @return Messaggio di errore, riguardante un faultString, previsto dalla specifica in uso
	 */
	public String toString(MessaggiFaultErroreCooperazione msg);
	
	/**
	 * Traduce il codice di errore in un formato conforme alle specifiche del protocollo
	 * @param cod Il ciodice di errore di cooperazione
	 * @return Stringa che rappresenta l'errore nel protocollo in uso
	 */
	public String toString(CodiceErroreCooperazione cod);
	
	/**
	 * Traduce il codice di errore in un formato conforme alle specifiche del protocollo
	 * @param cod Il ciodice di errore di cooperazione
	 * @param subCode SubCodice
	 * @return Stringa che rappresenta l'errore nel protocollo in uso
	 */
	public String toString(CodiceErroreCooperazione cod,SubCodiceErrore subCode);
		
	/**
	 * Fornisce la stringa di descrizione errore prevista in caso di errore di cooperazione per il protocollo in uso
	 * @param errore Errore cooperazione
	 * @return Messaggio della descrizione di errore previsto dalla specifica in uso
	 */
	public String toString(ErroreCooperazione errore);
	
	/**
	 * Traduce il codice di errore in un formato conforme alle specifiche del protocollo
	 * @param cod Il codice di errore di cooperazione
	 * @param prefix Il prefisso richiesto dal servizio applicativo
	 * @param isGenericCodeFor5XX indicazione se l'errore 5XX debba essere mascherato con un 500
	 * @return Stringa che rappresenta l'errore nel protocollo in uso
	 */
	public String toString(CodiceErroreIntegrazione cod,String prefix, boolean isGenericCodeFor5XX);
	
	/**
	 * Fornisce la stringa di descrizione errore prevista in caso di errore di integrazione per il protocollo in uso
	 * @param errore Errore integrazione
	 * @return Messaggio della descrizione di errore previsto dalla specifica in uso
	 */
	public String toString(ErroreIntegrazione errore);
	
	
	
	/**
	 * Restituisce il CodiceErroreCooperazione corrispondente alla stringa ritornata
	 * @param codiceCooperazione Stringa che rappresenta l'errore
	 * @return CodiceErroreCooperazione
	 */
	
	public CodiceErroreCooperazione toCodiceErroreCooperazione(String codiceCooperazione);
	
	/**
	 * Restituisce il CodiceErroreIntegrazione corrispondente alla stringa ritornata
	 * 
	 * @param codiceErroreIntegrazione Stringa che rappresenta l'errore
	 * @param prefix Il prefisso richiesto dal servizio applicativo
	 * @return CodiceErroreIntegrazione
	 */
	public CodiceErroreIntegrazione toCodiceErroreIntegrazione(String codiceErroreIntegrazione,String prefix);
	
	/**
	 * Restituisce il livelloRilevanza
	 * 
	 * @param livelloRilevanza livelloRilevanza
	 * @return LivelloRilevanza
	 */
	public LivelloRilevanza toLivelloRilevanza(String livelloRilevanza);
	
	/**
	 * Restituisce il contestoCodificaEccezione
	 * 
	 * @param contestoCodificaEccezione
	 * @return ContestoCodificaEccezione
	 */
	public ContestoCodificaEccezione toContestoCodificaEccezione(String contestoCodificaEccezione);
	
	/**
	 * Restituisce il tipo di inoltro
	 * 
	 * @param inoltro tipo di inoltro
	 * @return Inoltro
	 */
	public Inoltro toInoltro(String inoltro);
	
	/**
	 * Restituisce il tipo di ora registrazione
	 * 
	 * @param tipoOraRegistrazione tipo di ora registrazione
	 * @return TipoOraRegistrazione
	 */
	public TipoOraRegistrazione toTipoOraRegistrazione(String tipoOraRegistrazione);
	
	/**
	 * Restituisce il profilo di collaborazione
	 * 
	 * @param profiloDiCollaborazione profilo di collaborazione
	 * @return ProfiloDiCollaborazione
	 */
	public ProfiloDiCollaborazione toProfiloDiCollaborazione (String profiloDiCollaborazione);
	
	/**
	 * Restituisce la data attuale nel formato previsto dal protocollo in uso.
	 * @return Stringa della data attuale
	 */
	public String getDate_protocolFormat();
	
	/**
	 * Restituisce rappresentazione testuale della data fornita per il protocollo in uso.
	 * @param date Data da serializzare.
	 * @return Rappresentazione testuale della data
	 */
	public String getDate_protocolFormat(Date date);
		
	/**
	 * Restituisce il valore dell'identificativo porta di default per il soggetto.
	 * @param soggetto soggetto
	 * @return IdentificativoPortaDefault
	 */
	public String getIdentificativoPortaDefault(IDSoggetto soggetto);
	
	/**
	 * Restituisce il valore del Codice IPA di Default
	 * 
	 * @param soggetto soggetto
	 * @param createURI indicazione se anteporre il prefisso di una uri
	 * @return CodiceIPA
	 * @throws ProtocolException
	 */
	public String getIdentificativoCodiceIPADefault(IDSoggetto soggetto,boolean createURI) throws ProtocolException;
}
