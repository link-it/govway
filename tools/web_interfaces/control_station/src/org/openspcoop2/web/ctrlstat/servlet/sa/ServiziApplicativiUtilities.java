/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;

/**
 * ServiziApplicativiUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ServiziApplicativiUtilities {

	public static void deleteServizioApplicativo(ServizioApplicativo sa, String userLogin, ServiziApplicativiCore saCore, ServiziApplicativiHelper saHelper, StringBuffer inUsoMessage, String newLine) throws Exception {
		
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setNome(sa.getNome());
		idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
		
		// Controllo che il sil non sia in uso
		Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new Hashtable<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = !saHelper.isModalitaCompleta();
		boolean saInUso  = saCore.isServizioApplicativoInUso(idServizioApplicativo, whereIsInUso, saCore.isRegistroServiziLocale(), normalizeObjectIds);
		
		if (saInUso) {
			
			inUsoMessage.append(DBOggettiInUsoUtils.toString(idServizioApplicativo, whereIsInUso, true, newLine,normalizeObjectIds));
			inUsoMessage.append(newLine);

		} else {

			// Elimino il sil
			saCore.performDeleteOperation(userLogin, saHelper.smista(), sa);
		}
		
	}
	
}
