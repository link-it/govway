/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.utils.service.beans.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openspcoop2.utils.service.beans.ProfiloEnum;

/**
 * ProfiloUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProfiloUtils {

	private static final Map<ProfiloEnum,String> MAP_PROFILO_TO_PROTOCOLLO = new HashMap<ProfiloEnum,String>();
	static {
		MAP_PROFILO_TO_PROTOCOLLO.put(ProfiloEnum.APIGATEWAY, "trasparente");
		MAP_PROFILO_TO_PROTOCOLLO.put(ProfiloEnum.MODIPA, "modipa");
		MAP_PROFILO_TO_PROTOCOLLO.put(ProfiloEnum.MODI, "modipa");
		MAP_PROFILO_TO_PROTOCOLLO.put(ProfiloEnum.SPCOOP, "spcoop");
		MAP_PROFILO_TO_PROTOCOLLO.put(ProfiloEnum.FATTURAPA, "sdi");
		MAP_PROFILO_TO_PROTOCOLLO.put(ProfiloEnum.EDELIVERY, "as4");
	}
	public static Map<ProfiloEnum, String> getMapProfiloToProtocollo() {
		return MAP_PROFILO_TO_PROTOCOLLO;
	}
	
	private static Map<String, ProfiloEnum> MAP_PROTOCOLLO_TO_PROFILO = null;
	public static Map<String, ProfiloEnum> getMapProtocolloToProfilo() {
		if(MAP_PROTOCOLLO_TO_PROFILO==null) {
			initMapProtocolloToProfilo();
		}
		return MAP_PROTOCOLLO_TO_PROFILO;
	}
	private static synchronized void initMapProtocolloToProfilo() {
		if(MAP_PROTOCOLLO_TO_PROFILO==null) {
			MAP_PROTOCOLLO_TO_PROFILO = new HashMap<String,ProfiloEnum>();
			Iterator<ProfiloEnum> it = MAP_PROFILO_TO_PROTOCOLLO.keySet().iterator();
			while (it.hasNext()) {
				ProfiloEnum profiloEnum = (ProfiloEnum) it.next();
				if(ProfiloEnum.MODIPA.equals(profiloEnum)) {
					continue; // utilizzo MODI
				}
				String protocollo = MAP_PROFILO_TO_PROTOCOLLO.get(profiloEnum);
				MAP_PROTOCOLLO_TO_PROFILO.put(protocollo, profiloEnum);
			}
		}
	}
	
	public static String toProtocollo(ProfiloEnum profiloEnum) {
		return getMapProfiloToProtocollo().get(profiloEnum);
	}
	public static ProfiloEnum toProfilo(String protocollo) {
		return getMapProtocolloToProfilo().get(protocollo);
	}
	
}
