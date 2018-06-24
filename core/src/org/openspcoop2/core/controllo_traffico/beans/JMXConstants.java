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

package org.openspcoop2.core.controllo_traffico.beans;

/**
 * JMXConstants 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JMXConstants {

	public final static String JMX_TYPE = "type";

	public final static String JMX_NAME = "ControlloTraffico";
	
	public final static String JMX_NAME_SERVICE = "ServizioControlloTraffico";
	
	/** Nomi attributi */
	public final static String CC_ATTRIBUTE_MAX_THREADS_STATO = "statoControlloNumeroRichiesteComplessiveGestibiliPdD";
	public final static String CC_ATTRIBUTE_MAX_THREADS_SOGLIA = "numeroRichiesteComplessiveGestibiliPdD";
	public final static String CC_ATTRIBUTE_ACTIVE_THREADS = "threadsAttivi";
	public final static String CC_ATTRIBUTE_PDD_CONGESTIONATA = "portaDominioCongestionata";
	
	/** Nomi metodi */
	public final static String CC_METHOD_NAME_GET_ALL_ID_POLICY = "getAllIdPolicies"; 
	public final static String CC_METHOD_NAME_GET_STATO_POLICY = "getPolicy"; 
	public final static String CC_METHOD_NAME_REMOVE_ALL_POLICY = "removeAllPolicies"; 
	public final static String CC_METHOD_NAME_RESET_ALL_POLICY_COUNTERS = "resetAllPoliciesCounters"; 
	public final static String CC_METHOD_NAME_REMOVE_POLICY = "removePolicy";
	public final static String CC_METHOD_NAME_RESET_POLICY_COUNTERS = "resetPolicyCounters";
	
}
