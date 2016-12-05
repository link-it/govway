package org.openspcoop2.protocol.utils;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.registry.driver.FiltroRicercaProtocolProperty;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.NumberProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.StringProperty;

public class ProtocolPropertiesUtils {

	// UTILITIES
	
	public static List<FiltroRicercaProtocolProperty> convert(ProtocolProperties protocolProperties){
		List<FiltroRicercaProtocolProperty> list = null;
		if(protocolProperties!=null && protocolProperties.sizeProperties()>0){
			list = new ArrayList<FiltroRicercaProtocolProperty>();
			for (int i = 0; i < protocolProperties.sizeProperties(); i++) {
				FiltroRicercaProtocolProperty fpp = new FiltroRicercaProtocolProperty();
				AbstractProperty<?> p = protocolProperties.getProperty(i);
				fpp.setNome(p.getId());
				if(p instanceof StringProperty){
					StringProperty sp = (StringProperty) p;
					fpp.setValore(sp.getValue());
				}
				else if(p instanceof NumberProperty){
					NumberProperty np = (NumberProperty) p;
					fpp.setValoreNumerico(np.getValue());
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
