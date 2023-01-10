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

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDConnettore;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * ICacheCleaner
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ICacheCleaner {

	public default void removeAccordoCooperazione(IDAccordoCooperazione idAccordo) throws Exception {
		// nop
	}
	public default void removeApi(IDAccordo idAccordo) throws Exception {
		// nop
	}
	public default void removeErogazione(IDServizio idServizio) throws Exception {
		// nop
	}
	public default void removeFruizione(IDSoggetto fruitore, IDServizio idServizio) throws Exception {
		// nop
	}
	
	public default void removePortaDelegata(IDPortaDelegata idPD) throws Exception {
		// nop
	}
	public default void removePortaApplicativa(IDPortaApplicativa idPA)throws Exception {
		// nop
	}
	public default void removeConnettore(IDConnettore idConnettore)throws Exception {
		// nop
	}
	

	public default void removePdd(String portaDominio) throws Exception {
		// nop
	}
	public default void removeSoggetto(IDSoggetto idSoggetto) throws Exception {
		// nop
	}
	public default void removeApplicativo(IDServizioApplicativo idApplicativo) throws Exception {
		// nop
	}
	
	public default void removeRuolo(IDRuolo idRuolo) throws Exception {
		// nop
	}
	public default void removeScope(IDScope idScope) throws Exception {
		// nop
	}
	
	public default void removeGenericProperties(IDGenericProperties idGP) throws Exception {
		// nop
	}

}
