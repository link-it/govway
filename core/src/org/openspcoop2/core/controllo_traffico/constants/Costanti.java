/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.controllo_traffico.constants;

/**
 * Costanti 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {

	public final static String SEPARATORE_IDPOLICY_RAGGRUPPAMENTO = " - ";
	
	private static String controlloTrafficoImagePrefix = "image";
	private static String controlloTrafficoEventiImagePrefix = "imageEventi";
	private static String controlloTrafficoImageExt = ".bin";
	
	public static String getControlloTrafficoImagePrefix(String CT_policyType) {
		return _getControlloTrafficoImagePrefix(CT_policyType, controlloTrafficoImagePrefix);
	}
	public static String getControlloTrafficoEventiImagePrefix(String CT_policyType) {
		return _getControlloTrafficoImagePrefix(CT_policyType, controlloTrafficoEventiImagePrefix);
	}
	private static String _getControlloTrafficoImagePrefix(String CT_policyType, String prefix) {
		StringBuilder sb = new StringBuilder(prefix);
		if(CT_policyType!=null && !"LOCAL".equals(CT_policyType)) {
			sb.append("-").append(CT_policyType);
		}
		sb.append(controlloTrafficoImageExt);
		return sb.toString();
	}
	
	public final static String POLICY_GLOBALE = "Globale";
	public final static String POLICY_API = "API";
	
}
