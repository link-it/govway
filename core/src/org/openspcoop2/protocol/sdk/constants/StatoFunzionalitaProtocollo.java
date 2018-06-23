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

package org.openspcoop2.protocol.sdk.constants;
/**
 * Identifica lo stato di configurazione di una funzionalita' della Porta di Dominio 
 * per il protocollo in uso:
 * - Abilitata: la funzione e' sempre attiva nel protocollo in uso
 * - Disabilitata:  la funzione e' sempre spenta nel protocollo in uso
 * - Registro: l'uso della funzione dipende dalla configurazione del registro.
 * 
 * @author nardi
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum StatoFunzionalitaProtocollo {

	ABILITATA,DISABILITATA,REGISTRO;
	
}
