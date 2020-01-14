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

package org.openspcoop2.protocol.utils;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.registry.driver.FiltroRicercaProtocolProperty;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.NumberProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.StringProperty;

/**
 * ProtocolPropertiesUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolUtils {

	// UTILITIES

	public static List<String> orderProtocolli(List<String> protocolliDispondibili){
		List<String> l = new ArrayList<>();
		
		List<String> newL = new ArrayList<>();
		for (String protocollo : protocolliDispondibili) {
			newL.add(protocollo);
		}
		
		// trasparente
		for (int i = 0; i < newL.size(); i++) {
			String protocollo = newL.get(i);
			if("trasparente".equals(protocollo)) {
				l.add(newL.remove(i));
				break;
			}
		}
		
		// modipa
		for (int i = 0; i < newL.size(); i++) {
			String protocollo = newL.get(i);
			if("modipa".equals(protocollo)) {
				l.add(newL.remove(i));
				break;
			}
		}
		
		// spcoop
		for (int i = 0; i < newL.size(); i++) {
			String protocollo = newL.get(i);
			if("spcoop".equals(protocollo)) {
				l.add(newL.remove(i));
				break;
			}
		}
		
		// edelivery
		for (int i = 0; i < newL.size(); i++) {
			String protocollo = newL.get(i);
			if("as4".equals(protocollo)) {
				l.add(newL.remove(i));
				break;
			}
		}
		
		// fatturazione
		for (int i = 0; i < newL.size(); i++) {
			String protocollo = newL.get(i);
			if("sdi".equals(protocollo)) {
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
	
	public static List<FiltroRicercaProtocolProperty> convert(ProtocolProperties protocolProperties){
		List<FiltroRicercaProtocolProperty> list = null;
		if(protocolProperties!=null && protocolProperties.sizeProperties()>0){
			list = new ArrayList<FiltroRicercaProtocolProperty>();
			for (int i = 0; i < protocolProperties.sizeProperties(); i++) {
				FiltroRicercaProtocolProperty fpp = new FiltroRicercaProtocolProperty();
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
