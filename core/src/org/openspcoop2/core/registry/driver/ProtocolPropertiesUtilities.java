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

package org.openspcoop2.core.registry.driver;

import java.util.List;

import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Soggetto;

/**
 * ProtocolPropertiesUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolPropertiesUtilities {

	public static boolean isMatch(Soggetto soggetto,List<FiltroRicercaProtocolProperty> list){
		return isMatch(soggetto.getProtocolPropertyList(), list);
	}
	public static boolean isMatch(AccordoCooperazione accordo,List<FiltroRicercaProtocolProperty> list){
		return isMatch(accordo.getProtocolPropertyList(), list);
	}
	public static boolean isMatch(AccordoServizioParteComune accordo,List<FiltroRicercaProtocolProperty> list){
		return isMatch(accordo.getProtocolPropertyList(), list);
	}
	public static boolean isMatch(PortType pt,List<FiltroRicercaProtocolProperty> list){
		return isMatch(pt.getProtocolPropertyList(), list);
	}
	public static boolean isMatch(Operation op,List<FiltroRicercaProtocolProperty> list){
		return isMatch(op.getProtocolPropertyList(), list);
	}
	public static boolean isMatch(Azione az,List<FiltroRicercaProtocolProperty> list){
		return isMatch(az.getProtocolPropertyList(), list);
	}
	public static boolean isMatch(Resource resource,List<FiltroRicercaProtocolProperty> list){
		return isMatch(resource.getProtocolPropertyList(), list);
	}
	public static boolean isMatch(AccordoServizioParteSpecifica accordo,List<FiltroRicercaProtocolProperty> list){
		return isMatch(accordo.getProtocolPropertyList(), list);
	}
	public static boolean isMatch(Fruitore fruitore,List<FiltroRicercaProtocolProperty> list){
		return isMatch(fruitore.getProtocolPropertyList(), list);
	}
	public static boolean isMatch(List<ProtocolProperty> protocolProperties ,List<FiltroRicercaProtocolProperty> list){
		if(list==null || list.size()<=0){
			return true;
		}
		for (int i = 0; i < list.size(); i++) {
			FiltroRicercaProtocolProperty filtro = list.get(i);
			
			if(filtro.getName()!=null){
				boolean found = false;
				for (ProtocolProperty pp : protocolProperties) {
					if(filtro.getName().equals(pp.getName())){
						
						// check altri valori
						if(filtro.getValueAsString()!=null){
							if(!filtro.getValueAsString().equals(pp.getValue())){
								continue;
							}
						}
						else if(filtro.getValueAsLong()!=null){
							if(pp.getNumberValue()==null){
								continue;
							}
							if(filtro.getValueAsLong().longValue() != pp.getNumberValue().longValue()){
								continue;
							}
						}
						else if(filtro.getValueAsBoolean()!=null){
							if(pp.getBooleanValue()==null){
								continue;
							}
							if(filtro.getValueAsBoolean().booleanValue() != pp.getBooleanValue().booleanValue()){
								continue;
							}
						}
						
						found = true;
						break;
					}
				}
				if(!found){
					return false;
				}
			}
			else{
				
				if(filtro.getValueAsString()!=null){
					boolean found = false;
					for (ProtocolProperty pp : protocolProperties) {
						if(filtro.getValueAsString().equals(pp.getValue())){
							found = true;
							break;
						}
					}
					if(!found){
						return false;
					}
				}
				else if(filtro.getValueAsLong()!=null){
					boolean found = false;
					for (ProtocolProperty pp : protocolProperties) {
						if(pp.getNumberValue()==null){
							continue;
						}
						if(filtro.getValueAsLong().longValue() == pp.getNumberValue().longValue()){
							found = true;
							break;
						}
					}
					if(!found){
						return false;
					}
				}
				else if(filtro.getValueAsBoolean()!=null){
					boolean found = false;
					for (ProtocolProperty pp : protocolProperties) {
						if(pp.getBooleanValue()==null){
							continue;
						}
						if(filtro.getValueAsBoolean().booleanValue() == pp.getBooleanValue().booleanValue()){
							found = true;
							break;
						}
					}
					if(!found){
						return false;
					}
				}
				
			}
			
		}
		
		return true;
	}

	
}
