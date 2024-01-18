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

package org.openspcoop2.protocol.utils;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.registry.driver.FiltroRicercaProtocolPropertyRegistry;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.NumberProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;

/**
 * ProtocolPropertiesUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolUtils {

	// UTILITIES

	public static MapKey<String> protocolToMapKey(String protocollo){
		if(CostantiLabel.TRASPARENTE_PROTOCOL_NAME.equals(protocollo)) {
			return CostantiLabel.TRASPARENTE_PROTOCOL_MAP_KEY;
		}
		else if(CostantiLabel.MODIPA_PROTOCOL_NAME.equals(protocollo)) {
			return CostantiLabel.MODIPA_PROTOCOL_MAP_KEY;
		}
		else if(CostantiLabel.SPCOOP_PROTOCOL_NAME.equals(protocollo)) {
			return CostantiLabel.SPCOOP_PROTOCOL_MAP_KEY;
		}
		else if(CostantiLabel.AS4_PROTOCOL_NAME.equals(protocollo)) {
			return CostantiLabel.AS4_PROTOCOL_MAP_KEY;
		}
		else if(CostantiLabel.SDI_PROTOCOL_NAME.equals(protocollo)) {
			return CostantiLabel.SDI_PROTOCOL_MAP_KEY;
		}
		else if(CostantiLabel.NO_PROTOCOL_NAME.equals(protocollo)) {
			return CostantiLabel.NO_PROTOCOL_MAP_KEY;
		}
		else {
			return Map.newMapKey(protocollo);
		}
	}
	
	public static List<String> orderProtocolli(List<String> protocolliDispondibili){
		List<String> l = new ArrayList<>();
		
		List<String> newL = new ArrayList<>();
		for (String protocollo : protocolliDispondibili) {
			newL.add(protocollo);
		}
		
		// trasparente
		for (int i = 0; i < newL.size(); i++) {
			String protocollo = newL.get(i);
			if(CostantiLabel.TRASPARENTE_PROTOCOL_NAME.equals(protocollo)) {
				l.add(newL.remove(i));
				break;
			}
		}
		
		// modipa
		for (int i = 0; i < newL.size(); i++) {
			String protocollo = newL.get(i);
			if(CostantiLabel.MODIPA_PROTOCOL_NAME.equals(protocollo)) {
				l.add(newL.remove(i));
				break;
			}
		}
		
		// spcoop
		for (int i = 0; i < newL.size(); i++) {
			String protocollo = newL.get(i);
			if(CostantiLabel.SPCOOP_PROTOCOL_NAME.equals(protocollo)) {
				l.add(newL.remove(i));
				break;
			}
		}
		
		// edelivery
		for (int i = 0; i < newL.size(); i++) {
			String protocollo = newL.get(i);
			if(CostantiLabel.AS4_PROTOCOL_NAME.equals(protocollo)) {
				l.add(newL.remove(i));
				break;
			}
		}
		
		// fatturazione
		for (int i = 0; i < newL.size(); i++) {
			String protocollo = newL.get(i);
			if(CostantiLabel.SDI_PROTOCOL_NAME.equals(protocollo)) {
				l.add(newL.remove(i));
				break;
			}
		}
		
		// altri
		for (int i = 0; i < newL.size(); i++) {
			String protocollo = newL.get(i);
			l.add(protocollo);
		}
		
		return l;
	}
	
	public static List<FiltroRicercaProtocolPropertyRegistry> convert(ProtocolProperties protocolProperties){
		List<FiltroRicercaProtocolPropertyRegistry> list = null;
		if(protocolProperties!=null && protocolProperties.sizeProperties()>0){
			list = new ArrayList<FiltroRicercaProtocolPropertyRegistry>();
			for (int i = 0; i < protocolProperties.sizeProperties(); i++) {
				FiltroRicercaProtocolPropertyRegistry fpp = new FiltroRicercaProtocolPropertyRegistry();
				AbstractProperty<?> p = protocolProperties.getProperty(i);
				fpp.setName(p.getId());
				if(p instanceof StringProperty){
					StringProperty sp = (StringProperty) p;
					if(sp.getValue()!=null && !"".equals(sp.getValue())) {
						fpp.setValueAsString(sp.getValue());
					}
				}
				else if(p instanceof NumberProperty){
					NumberProperty np = (NumberProperty) p;
					fpp.setValueAsLong(np.getValue());
				}
				else if(p instanceof BooleanProperty){
					BooleanProperty bp = (BooleanProperty) p;
					fpp.setValueAsBoolean(bp.getValue());
				}
				else{
					throw new RuntimeException("Tipo di Filtro ["+p.getClass().getName()+"] non supportato");
				}
				list.add(fpp);
			}
		}
		return list;
	}
	
}
