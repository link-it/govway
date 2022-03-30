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


package org.openspcoop2.utils.sonde.impl;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sonde.ParametriSonda;
import org.openspcoop2.utils.sonde.Sonda;
import org.openspcoop2.utils.sonde.SondaException;

/**
 * Classe di implementazione della Sonda per le code
 * 
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SondaInvocazione extends Sonda {

	/**
	 * Costruttore per la classe SondaCoda
	 * @param param parametri costruttivi della sonda
	 * @throws Exception
	 */
	public SondaInvocazione(ParametriSonda param) {
		super(param);
		Set<String> reserved = new HashSet<String>();
		reserved.add(RICHIESTA_OK);
		this.getParam().setReserved(reserved);

	}

	private static final String RICHIESTA_OK = "richiestaOk";
	@Override
	public StatoSonda getStatoSonda(){
		
		boolean richiestaOK = false;
		if(super.getParam().getDatiCheck().containsKey(RICHIESTA_OK)) {
			try {
				richiestaOK = Boolean.parseBoolean(super.getParam().getDatiCheck().getProperty(RICHIESTA_OK));
			} catch(Exception e) {
				super.getParam().getDatiCheck().remove(RICHIESTA_OK);
			}
		}
		
		StatoSonda statoSonda = new StatoSonda();
		SimpleDateFormat format = new SimpleDateFormat(PATTERN);

		//non esiste stato warning
		if(richiestaOK) {
			statoSonda.setStato(0);
			statoSonda.setDescrizione(null);
		} else {
			if(this.getParam().getDataError() == null) {
				this.getParam().setDataError(new Date());
			}
			statoSonda.setStato(2);
			statoSonda.setDescrizione("Invocazione richiesta dal check "+super.getParam().getNome()+" fallita dal "+format.format(this.getParam().getDataError()));
		}
 
		return statoSonda;
	}

	/**
	 * @param dimensioneCoda dimensione attuale della coda
	 * @param connection connessione per il DB
	 * @param tipoDatabase tipo database
	 * @return lo stato attuale della sonda
	 * @throws SondaException
	 */
	public StatoSonda aggiornaStatoSonda(boolean richiestaOk, Properties params, Connection connection, TipiDatabase tipoDatabase) throws SondaException {
		// inserisce i dati nel properties
		super.getParam().putAllCheck(params);
		
		super.getParam().getDatiCheck().put(RICHIESTA_OK, richiestaOk + "");
		return updateSonda(connection, tipoDatabase);
	}


}
