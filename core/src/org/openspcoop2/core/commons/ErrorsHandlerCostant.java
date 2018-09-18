/*
 * GovWay - A customizable API Gateway
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
 * @author Stefano Corallo <corallo@link.it>
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
	AUTORIZZAZIONE_MAPPING;
}
