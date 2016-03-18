package org.openspcoop2.web.ctrlstat.plugins;

import java.util.List;

import org.openspcoop2.web.lib.users.dao.PermessiUtente;

public interface IExtendedMenu {

	public List<ExtendedMenuItem> getExtendedItemsMenuRegistro(boolean interfacciaAvanzata,boolean registroLocale,boolean singlePdd,PermessiUtente pu);
	public List<ExtendedMenuItem> getExtendedItemsMenuStrumenti(boolean interfacciaAvanzata,boolean registroLocale,boolean singlePdd,PermessiUtente pu);
	public List<ExtendedMenuItem> getExtendedItemsMenuConfigurazione(boolean interfacciaAvanzata,boolean registroLocale,boolean singlePdd,PermessiUtente pu);
	
}
