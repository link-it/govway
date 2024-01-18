/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
 *   <li><a href="http://www.openspcoop2.org">Sito ufficiale del progetto OpenSPCoop v2</a>
 * </ul>
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


@javax.xml.bind.annotation.XmlSchema(namespace = "http://services.pdd.openspcoop2.org", elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package org.openspcoop2.pdd.services.skeleton;
