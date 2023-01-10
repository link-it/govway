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

public class SondaCoda extends Sonda {

	/**
	 * Costruttore per la classe SondaCoda
	 * @param param parametri costruttivi della sonda
	 * @throws Exception
	 */
	public SondaCoda(ParametriSonda param) {
		super(param);
		Set<String> reserved = new HashSet<String>();
		reserved.add("dimensione_coda");
		this.getParam().setReserved(reserved);
	}

	@Override
	public StatoSonda getStatoSonda(){
		
		long dimensione_coda = 0;
		String dimensioneCodaString = super.getParam().getDatiCheck().getProperty("dimensione_coda");
		try {
			if(dimensioneCodaString!=null)
				dimensione_coda = Long.parseLong(dimensioneCodaString);
		} catch(NumberFormatException e) {
			e.printStackTrace(System.err);
			System.err.println("Errore durante il parsing del parametro dimensione_coda: " + dimensioneCodaString + ". Elimino il valore");
			super.getParam().getDatiCheck().remove("dimensione_coda");
		}
		StatoSonda statoSonda = new StatoSonda();
		SimpleDateFormat format = new SimpleDateFormat(PATTERN);

		//Valuto l'eventuale superamento delle soglie e calcolo lo stato
		if(dimensione_coda > super.getParam().getSogliaError()) {
			if(this.getParam().getDataError() == null) {
				this.getParam().setDataError(new Date());
			}
			statoSonda.setStato(2);
			statoSonda.setDescrizione("La coda "+super.getParam().getNome()+" ha superato la soglia di errore ("+
										super.getParam().getSogliaError()+") dal "+
										format.format(this.getParam().getDataError())+
										". Dimensione attuale della coda: "+dimensione_coda+".");
		} else if (dimensione_coda > super.getParam().getSogliaWarn()) {
			if(this.getParam().getDataWarn() == null) {
				this.getParam().setDataWarn(new Date());
			}
			statoSonda.setStato(1);
			statoSonda.setDescrizione("La coda "+super.getParam().getNome()+" ha superato la soglia di warn ("+
										super.getParam().getSogliaWarn()+") dal "+
										format.format(this.getParam().getDataWarn())+
										". Dimensione attuale della coda: "+dimensione_coda+".");
		} else {
			statoSonda.setStato(0);
			statoSonda.setDescrizione("Coda "+super.getParam().getNome()+": numero elementi in coda: " + dimensione_coda);
		}
 
		return statoSonda;
	}

	//retrocompatibilita
	public StatoSonda aggiornaStatoSonda(long dimensioneCoda, Connection connection, TipiDatabase tipoDatabase) throws SondaException {
		return this.aggiornaStatoSonda(dimensioneCoda, null, connection, tipoDatabase);
	}
	
	/**
	 * @param dimensioneCoda dimensione attuale della coda
	 * @param connection connessione per il DB
	 * @param tipoDatabase tipo database
	 * @return lo stato attuale della sonda
	 * @throws SondaException
	 */
	public StatoSonda aggiornaStatoSonda(long dimensioneCoda, Properties params, Connection connection, TipiDatabase tipoDatabase) throws SondaException {
		// inserisce i dati nel properties
		
		super.getParam().putAllCheck(params);
		
		super.getParam().getDatiCheck().put("dimensione_coda", dimensioneCoda + "");
		return updateSonda(connection, tipoDatabase);
	}


}
