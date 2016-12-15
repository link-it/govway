package org.openspcoop2.core.config.driver;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;

public class IDServizioUtils {

	// Per motivi di compilazione non è possibile utilizzare nel package 'org.openspcoop2.core.config' la factory IDServizioFactory
	@SuppressWarnings("deprecation")
	public static IDServizio buildIDServizio(String tipoServizio, String nomeServizio, IDSoggetto idSoggettoErogatore, Integer versioneServizio){
		IDServizio id = new IDServizio();
		id.setTipo(tipoServizio);
		id.setNome(nomeServizio);
		id.setSoggettoErogatore(idSoggettoErogatore);
		id.setVersione(versioneServizio);
		return id;
	}
	
}
