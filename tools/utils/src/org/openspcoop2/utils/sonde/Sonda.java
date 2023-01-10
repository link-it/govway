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


package org.openspcoop2.utils.sonde;

import java.sql.Connection;
import java.util.Date;

import org.openspcoop2.utils.TipiDatabase;

/**
 * Classe astratta per le Sonde
 * 
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class Sonda {

	private ParametriSonda param;
	protected static final String PATTERN = "dd/MM/yyyy HH:mm:ss.sss";

	
	/**
	 * @param param
	 */
	public Sonda(ParametriSonda param) {
		this.param = param;
	}
	
	/**
	 * @author Bussu Giovanni (bussu@link.it)
	 * @author  $Author$
	 * @version $Rev$, $Date$
	 * 
	 */
	public class StatoSonda {
		private int stato;
		private String descrizione;
		public int getStato() {
			return this.stato;
		}
		public void setStato(int stato) {
			this.stato = stato;
		}
		public String getDescrizione() {
			return this.descrizione;
		}
		public void setDescrizione(String descrizione) {
			this.descrizione = descrizione;
		}

	}

	/**
	 * Metodo che, utilizzando i dati_check, restituisce lo stato attuale della sonda
	 * @return lo stato attuale della Sonda
	 */
	public abstract StatoSonda getStatoSonda();

	/**
	 * @param connection connessione al database
	 * @param tipoDatabase tipo di database
	 * @return lo stato attuale della sonda
	 * @throws SondaException
	 */
	protected StatoSonda updateSonda(Connection connection, TipiDatabase tipoDatabase) throws SondaException {

		this.param.setDataUltimoCheck(new Date());

		StatoSonda statoSonda = getStatoSonda();

		int statoNew = statoSonda.getStato();

		if(this.param.getStatoUltimoCheck() != statoNew) {
			
			// Aggiorna ad adesso il data_warn se lo statoSonda.stato e' passato ad 1 da 0 
			if(this.param.getStatoUltimoCheck() == 0 && statoNew == 1) {
				this.param.setDataWarn(new Date());
				this.param.setDataError(null);
				this.param.setDataOk(null);
			}
			// Aggiorna ad adesso il data_error se lo statoSonda.stato e' passato a 2
			if(statoNew == 2) {
				this.param.setDataError(new Date());
				this.param.setDataWarn(null);
				this.param.setDataOk(null);
			}
			// Aggiorna ad adesso il data_ok se lo statoSonda.stato e' passato a 0
			if(statoNew == 0) {
				this.param.setDataOk(new Date());
				this.param.setDataError(null);
				this.param.setDataWarn(null);
			}
		}

		this.param.setStatoUltimoCheck(statoNew);

		SondaFactory.updateStatoSonda(this.param.getNome(), this, connection, tipoDatabase);

		return statoSonda;
	}

	public ParametriSonda getParam() {
		return this.param;
	}

}
