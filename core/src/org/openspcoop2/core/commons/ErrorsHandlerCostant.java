/*
 * OpenSPCoop - Customizable API Gateway
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
	POSSIEDE_RUOLI, POSSIEDE_FRUITORI, UTILIZZATO_IN_POLITICHE_SICUREZZA,
	IS_REFERENTE,IS_REFERENTE_COOPERAZIONE,
	IS_PARTECIPANTE_COOPERAZIONE, 
	IN_USO_IN_ACCORDI,
	IS_SERVIZIO_COMPONENTE_IN_ACCORDI;
}
