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

import org.openspcoop2.core.id.IDConnettore;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;

/**
 * ConfigurazioneCacheCleaner
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneCacheCleaner implements ICacheCleaner {
	
	@Override
	public void removeErogazione(IDServizio idServizio) throws Exception {
		ConfigurazionePdDReader.removeErogazione(idServizio);
	}
	@Override
	public void removeFruizione(IDSoggetto fruitore, IDServizio idServizio) throws Exception {
		ConfigurazionePdDReader.removeFruizione(fruitore, idServizio);
	}
	
	@Override	
	public void removePortaDelegata(IDPortaDelegata idPD) throws Exception {
		ConfigurazionePdDReader.removePortaDelegata(idPD);
	}
	@Override
	public void removePortaApplicativa(IDPortaApplicativa idPA)throws Exception {
		ConfigurazionePdDReader.removePortaApplicativa(idPA);
	}
	@Override
	public void removeConnettore(IDConnettore idConnettore)throws Exception {
		ConfigurazionePdDReader.removeConnettore(idConnettore);
	}
	
	@Override
	public void removeSoggetto(IDSoggetto idSoggetto) throws Exception {
		ConfigurazionePdDReader.removeSoggetto(idSoggetto);
	}
	@Override
	public void removeApplicativo(IDServizioApplicativo idApplicativo) throws Exception {
		ConfigurazionePdDReader.removeApplicativo(idApplicativo);
	}
	
	@Override
	public void removeGenericProperties(IDGenericProperties idGP) throws Exception {
		ConfigurazionePdDReader.removeGenericProperties(idGP);
	}

}
