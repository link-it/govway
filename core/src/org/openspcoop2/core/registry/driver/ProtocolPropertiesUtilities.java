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
import org.openspcoop2.core.registry.Soggetto;

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
			
			if(filtro.getNome()!=null){
				boolean found = false;
				for (ProtocolProperty pp : protocolProperties) {
					if(filtro.getNome().equals(pp.getName())){
						
						// check altri valori
						if(filtro.getValore()!=null){
							if(!filtro.getValore().equals(pp.getValue())){
								continue;
							}
						}
						if(filtro.getValoreNumerico()!=null){
							if(pp.getNumberValue()==null){
								continue;
							}
							if(filtro.getValoreNumerico().longValue() != pp.getNumberValue().longValue()){
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
				
				if(filtro.getValore()!=null){
					boolean found = false;
					for (ProtocolProperty pp : protocolProperties) {
						if(filtro.getValore().equals(pp.getValue())){
							found = true;
							break;
						}
					}
					if(!found){
						return false;
					}
				}
				else if(filtro.getValoreNumerico()!=null){
					boolean found = false;
					for (ProtocolProperty pp : protocolProperties) {
						if(pp.getNumberValue()==null){
							continue;
						}
						if(filtro.getValoreNumerico().longValue() == pp.getNumberValue().longValue()){
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
