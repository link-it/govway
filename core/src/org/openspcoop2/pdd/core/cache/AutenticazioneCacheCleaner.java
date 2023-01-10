/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;

/**
 * AutenticazioneCacheCleaner
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AutenticazioneCacheCleaner implements ICacheCleaner {

	@Override
	public void removePortaDelegata(IDPortaDelegata idPD) throws Exception {
		GestoreAutenticazione.removePortaDelegata(idPD);
	}
	@Override
	public void removePortaApplicativa(IDPortaApplicativa idPA)throws Exception {
		GestoreAutenticazione.removePortaApplicativa(idPA);
	}
	
	@Override
	public void removeSoggetto(IDSoggetto idSoggetto) throws Exception {
		GestoreAutenticazione.removeSoggetto(idSoggetto);
	}
	@Override
	public void removeApplicativo(IDServizioApplicativo idApplicativo) throws Exception {
		GestoreAutenticazione.removeApplicativo(idApplicativo);
	}

}
