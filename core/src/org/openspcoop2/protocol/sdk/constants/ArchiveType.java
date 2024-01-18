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

package org.openspcoop2.protocol.sdk.constants;


/**
 *  ArchiveType
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum ArchiveType {

	PDD,
	GRUPPO,
	RUOLO,
	SCOPE,
	SOGGETTO, 
	SERVIZIO_APPLICATIVO, PORTA_DELEGATA, PORTA_APPLICATIVA,
	ACCORDO_SERVIZIO_PARTE_COMUNE, ACCORDO_SERVIZIO_PARTE_SPECIFICA,
	ACCORDO_SERVIZIO_COMPOSTO, ACCORDO_COOPERAZIONE,
	FRUITORE,
	EROGAZIONE, FRUIZIONE,
	CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIG_POLICY,
	CONFIGURAZIONE_CONTROLLO_TRAFFICO_ACTIVE_POLICY,
	ALLARME,
	CONFIGURAZIONE_TOKEN_POLICY, 
	CONFIGURAZIONE_ATTRIBUTE_AUTHORITY, 
	CONFIGURAZIONE_PLUGIN_CLASSE, 
	CONFIGURAZIONE_PLUGIN_ARCHVIO,
	CONFIGURAZIONE_URL_INVOCAZIONE,
	CONFIGURAZIONE_URL_INVOCAZIONE_REGOLA,
	CONFIGURAZIONE,
	ALL_WITHOUT_CONFIGURAZIONE,
	ALL;
	
}
