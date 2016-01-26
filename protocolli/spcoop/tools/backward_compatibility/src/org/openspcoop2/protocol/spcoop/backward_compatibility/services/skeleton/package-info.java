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

/**
 * Contiene la definizione del servizio di IntegrationManager; servizio che si occupa di recuperare all'interno dell'infrastruttura OpenSPCoop, 
 * eventuali risposte applicative destinate
 * ad uno specifico cliente, il quale aveva precedentemente effettuato la richiesta, senza mai ricevere la risposta,
 * volendola ricevere in modalita' asincrona. 
 * Fornisce anche altri metodi di integrazione con OpenSPCoop, quali ad esempio l'invocazione di una porta delegata.
 * 
 * <p>
 * <h3>See Also...</h3>
 * <p>
 * <p>
 * Per altra documentazione, tutorials, esempi, guide guarda :
 * <ul>
 *   <li><a href="http://www.openspcoop.org">Sito ufficiale del progetto OpenSPCoop</a>
 * </ul>
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 */


@javax.xml.bind.annotation.XmlSchema(namespace = "http://services.pdd.openspcoop.org", elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package org.openspcoop2.protocol.spcoop.backward_compatibility.services.skeleton;
