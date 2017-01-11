/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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

package org.openspcoop2.core.config.driver;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * IDServizioUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
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
