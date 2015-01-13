/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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

/**
 * Fornisce classi per la gestione delle buste .
 * <p>  
 * Questo package e' classificabile in :
 * <ul>
 * <li>Definizione di busta 
 * <li>Definizione di costanti 
 * <li>Definizione/Esamina di un servizio  registrato nel registro dei servizi.
 * <li>Imbustamento 
 * <li>Validazione/Sbustamento 
 * <li>Funzionalita' 
 * </ul>
 * <p>
 * 
 * 
 * <h3>Definizione di busta </h3>
 * 
 * 
 * Viene fornita una classe, <code>Busta</code>,  che rappresenta una busta , come definito nella specifica.
 * E' possibile definire :
 * <ul>
 * <li>Mittente (con tipo e indirizzoTelematico)
 * <li>Destinatario (con tipo e indirizzoTelematico)
 * <li>Profilo di Collaborazione (con servizioCorrelato e relativo tipo)
 * <li>Collaborazione
 * <li>Servizio (con tipo)
 * <li>Azione
 * <li>Profilo di Trasmissione
 * <li>Sequenza
 * <li>ListaEccezioni
 * <li>ListaTrasmissioni
 * <li>ListaRiscontri
 * </ul>
 * <p>
 * Nella lista Eccezioni, ogni eccezione viene mappata in una apposita classe <code>Eccezione</code> contenente:
 * <ul>
 * <li>Contesto Codifica
 * <li>Codice Eccezione
 * <li>Rilevanza
 * <li>Posizione
 * </ul>
 * <p>
 * Nella lista Trasmissioni, ogni trasmissione viene mappata in una apposita classe <code>Trasmissione</code> contenente:
 * <ul>
 * <li>Origine (con tipo)
 * <li>Destinazione (con tipo)
 * <li>Ora Registrazione (e tipo)
 * </ul>
 * <p>
 * Nella lista Riscontri, ogni riscontro viene mappato in una apposita classe <code>Riscontro</code> contenente:
 * <ul>
 * <li>Identificatore
 * <li>Ora Registrazione 
 * </ul>
 * <p>
 * 
 * 
 * 
 * <h3>Definizione di costanti </h3>
 * 
 * Il package <code>constants</code>, fornisce varie stringhe costanti, definite dalla specifica , 
 * utilizzate dalle classi del package org.openspcoop2.protocol.sdk . Le costanti sono classificabili in :
 * <ul>
 * <li>valori assumibili dal Profili di Collaborazione
 * <li>valori assumibili dall'attributo 'inoltro' del Profilo di Trasmissione
 * <li>tipi dell'ora di creazione di un tracciamento
 * <li>rilevanze di una eccezione riscontrata durante una validazione
 * <li>codici di una eccezione riscontrata durante una validazione
 * <li>actor utilizzato nelle buste 
 * <li>nomi di DB utilizzati per funzionalita' 
 * <li>valori per i Fault dei messaggi Errore
 * <li>tipi di tracciamento
 * </ul>
 * <p>
 * 
 * 
 * <h3>Definizione/Esamina di un Servizio  nel Registro dei Servizi</h3>
 * 
 * La classe <code>Servizio</code> rappresenta un Servizio  presente all'interno
 * del registro.
 * <p>
 * La classe <code>InfoInoltroBusta</code> contiene tutti i dati necessari per la ricostruzione di una busta salvata nell'history
 * delle buste inviate.
 * <p>
 * la classe<code>IDServizio</code> contiene le informazioni necessarie per identificare univocamente un servizio
 * all'interno del registro dei servizi. Contiene tra le altre informazioni anche un oggetto di tipo <code>IDSoggetto</code>
 * che permette l'identificazione univoca di un soggetto all'interno del registro dei servizi.
 * <p>
 * 
 * 
 * <h3>Imbustamento</h3>
 * 
 * La classe <code>Imbustamento</code>, fornisce dei metodi statici per la creazione di un header ,
 * all'interno di un SOAPEnvelope. 
 * <p>
 * La classe <code>Imbustamento</code>, oltre ai metodi per la costruzione dell'header  all'interno di una SOAPEnvelope,
 * fornisce anche altri metodi :
 * <ul>
 * <li><code>buildID_()</code> permette di costruire un identificativo  unico (nel tempo).
 * <li><code>getDate_Format()</code> permette di ottenere la data e l'ora attuale nel formato . 
 * <li><code>build__Fault()</code> costruisce una busta errore, contenente un SOAPFault nel SOAPBody. 
 * </ul>
 * <p>
 * La classe <code>XMLBuilder</code>, fornisce dei metodi per la costruzione di messaggi definiti da specifica quali:
 * <ul>
 * <li>Messaggi di Errore Applicativo
 * <li>Tracciamento di Buste 
 * <li>Messaggi diagnostici
 * </ul>
 * <p> 
 * 
 * 
 * <h3>Sbustamento/Validazione</h3>
 * 
 * La classe <code>Sbustamento</code>, fornisce dei metodi statici per l'eliminazione dell'header ,
 * all'interno di un SOAPEnvelope. 
 * <p>
 * La classe <code>Validatore</code>, permette di effettuare la validazione, secondo
 * specifica , per ogni elemento della busta.
 * <p>
 * 
 * 
 * <h3>Funzionalita' </h3>
 * 
 * La classe <code>ProfiloDiCollaborazione</code>, fornisce dei metodi per la gestione dei vari profili di collaborazione.<p>
 * La classe <code>History</code>, fornisce dei metodi per la gestione di un history di buste inviate/ricevute.<p>
 * La classe <code>Riscontri</code>, fornisce dei metodi per la gestione dei profili dei riscontri.<p>
 * <p>
 * 
 * <p>
 * <h3>See Also...</h3>
 * <p>
 * <p>
 * Per altra documentazione, tutorials, esempi, guide guarda :
 * <ul>
 * <li><a href="http://www.openspcoop2.org">Sito ufficiale del progetto OpenSPCoop v2</a>
 * </ul>
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlSchema(namespace = "http://www.openspcoop2.org/protocol/sdk", elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package org.openspcoop2.protocol.sdk;
