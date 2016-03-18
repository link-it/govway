package org.openspcoop2.web.ctrlstat.plugins;

import java.util.List;

import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;

public interface IExtendedConnettore {

	public List<ExtendedConnettore> getExtendedConnettore(ConnettoreServletType type, boolean interfacciaAvanzata, 
			boolean connettoreDisabilitato, String tipoConnettore) throws ExtendedException;
	
	
}
