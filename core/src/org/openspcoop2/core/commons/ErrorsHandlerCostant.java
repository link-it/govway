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



package org.openspcoop2.core.commons;

/**
 * Enumeration per Controlli
 *
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public enum ErrorsHandlerCostant {

	IN_USO_IN_PORTE_APPLICATIVE, IN_USO_IN_PORTE_DELEGATE, IN_USO_IN_SERVIZI_APPLICATIVI,
	IN_USO_IN_SERVIZI, IS_FRUITORE,
	IN_USO_IN_MAPPING_FRUIZIONE_PD, IN_USO_IN_MAPPING_EROGAZIONE_PA,
	IN_USO_IN_SOGGETTI,
	POSSIEDE_FRUITORI, 
	IS_REFERENTE,IS_REFERENTE_COOPERAZIONE,
	IS_PARTECIPANTE_COOPERAZIONE, 
	IN_USO_IN_ACCORDI,
	IS_SERVIZIO_COMPONENTE_IN_ACCORDI,
	UTENTE,
	CONTROLLO_TRAFFICO,
	AUTORIZZAZIONE,
	AUTORIZZAZIONE_MAPPING,
	AUTORIZZAZIONE_PA,
	AUTORIZZAZIONE_MAPPING_PA,
	TRASFORMAZIONE_PD,
	TRASFORMAZIONE_MAPPING_PD,
	TRASFORMAZIONE_PA,
	TRASFORMAZIONE_MAPPING_PA,
	CONFIGURAZIONE_REGOLE_PROXY_PASS,
	CONNETTORE_PD,
	CONNETTORE_MAPPING_PD,
	CONNETTORE_PA,
	CONNETTORE_MAPPING_PA,
	TOKEN_PD,
	TOKEN_MAPPING_PD,
	TOKEN_PA,
	TOKEN_MAPPING_PA,
	IN_USO_IN_CANALE_NODO;
}
