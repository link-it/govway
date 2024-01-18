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

package org.openspcoop2.core.controllo_traffico.beans;

/**
 * JMXConstants 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JMXConstants {

	public static final String JMX_TYPE = "type";

	public static final String JMX_NAME = "ControlloTraffico";
	
	public static final String JMX_NAME_SERVICE = "ServizioControlloTraffico";
	
	/** Nomi attributi */
	public static final String CC_ATTRIBUTE_MAX_THREADS_STATO = "statoControlloNumeroRichiesteComplessiveGestibiliPdD";
	public static final String CC_ATTRIBUTE_MAX_THREADS_SOGLIA = "numeroRichiesteComplessiveGestibiliPdD";
	public static final String CC_ATTRIBUTE_ACTIVE_THREADS = "threadsAttivi";
	public static final String CC_ATTRIBUTE_PDD_CONGESTIONATA = "portaDominioCongestionata";
	
	/** Nomi metodi */
	public static final String CC_METHOD_NAME_GET_ALL_ID_POLICY = "getAllIdPolicies"; 
	public static final String CC_METHOD_NAME_GET_STATO_POLICY = "getPolicy"; 
	public static final String CC_METHOD_NAME_REMOVE_ALL_POLICY = "removeAllPolicies"; 
	public static final String CC_METHOD_NAME_RESET_ALL_POLICY_COUNTERS = "resetAllPoliciesCounters"; 
	public static final String CC_METHOD_NAME_REMOVE_POLICY = "removePolicy";
	public static final String CC_METHOD_NAME_RESET_POLICY_COUNTERS = "resetPolicyCounters";
	
}
