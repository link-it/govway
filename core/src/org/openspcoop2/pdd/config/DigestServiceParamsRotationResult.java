/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.config;

/**
 * Esito di una richiesta di rotazione forzata delle informazioni crittografiche (seme) di un servizio
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum DigestServiceParamsRotationResult {

	/** rotazione registrata: avverra' alla pubblicazione del primo segnale successivo */
	ROTAZIONE_RICHIESTA,
	/** la rotazione del seme in uso risultava gia' richiesta */
	ROTAZIONE_GIA_RICHIESTA,
	/** il seme indicato non e' piu' quello in uso poiche' e' stato nel frattempo ruotato */
	SEME_GIA_RUOTATO,
	/** il servizio non possiede un seme */
	SEME_NON_PRESENTE;
	
}
