/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.web.ctrlstat.costanti;

/**     
 * ConnettoreServletType
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum ConnettoreServletType {

	SOGGETTO, 
	SERVIZIO_APPLICATIVO_ADD,
	SERVIZIO_APPLICATIVO_CHANGE,
	SERVIZIO_APPLICATIVO_INVOCAZIONE_SERVIZIO, 
	SERVIZIO_APPLICATIVO_RISPOSTA_ASINCRONA,
	ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, 
	ACCORDO_SERVIZIO_PARTE_SPECIFICA_CHANGE, 
	ACCORDO_SERVIZIO_PARTE_SPECIFICA_PORTA_APPLICATIVA_ADD, 
	FRUIZIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD,
	FRUIZIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA_CHANGE,
	WIZARD_CONFIG,
	WIZARD_REGISTRY,
	
}
