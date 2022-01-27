/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.cache;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.registry.RegistroServiziReader;

/**
 * RegistroServiziCacheCleaner
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RegistroServiziCacheCleaner implements ICacheCleaner {
	
	@Override
	public void removeAccordoCooperazione(IDAccordoCooperazione idAccordo) throws Exception {
		RegistroServiziReader.removeAccordoCooperazione(idAccordo);
	}
	@Override
	public void removeApi(IDAccordo idAccordo) throws Exception {
		RegistroServiziReader.removeApi(idAccordo);
	}
	
	@Override
	public void removeErogazione(IDServizio idServizio) throws Exception {
		RegistroServiziReader.removeErogazione(idServizio);
	}
	@Override
	public void removeFruizione(IDSoggetto fruitore, IDServizio idServizio) throws Exception {
		RegistroServiziReader.removeFruizione(fruitore, idServizio);
	}
	
	@Override
	public void removePdd(String portaDominio) throws Exception {
		RegistroServiziReader.removePdd(portaDominio);
	}
	@Override
	public void removeSoggetto(IDSoggetto idSoggetto) throws Exception {
		RegistroServiziReader.removeSoggetto(idSoggetto);
	}

	@Override
	public void removeRuolo(IDRuolo idRuolo) throws Exception {
		RegistroServiziReader.removeRuolo(idRuolo);
	}
	@Override
	public void removeScope(IDScope idScope) throws Exception {
		RegistroServiziReader.removeScope(idScope);
	}
}
