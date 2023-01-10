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


package org.openspcoop2.core.controllo_traffico.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.utils.date.DateUtils;

/**
 * RisultatoStatistico 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RisultatoStatistico implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long risultato;
	private Date dataInizio;
	private Date dataFine;
	private Date dateCheck;
	
	public Date getDateCheck() {
		return this.dateCheck;
	}
	public void setDateCheck(Date dateCheck) {
		this.dateCheck = dateCheck;
	}
	public long getRisultato() {
		return this.risultato;
	}
	public void setRisultato(long risultato) {
		this.risultato = risultato;
	}
	public Date getDataInizio() {
		return this.dataInizio;
	}
	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}
	public Date getDataFine() {
		return this.dataFine;
	}
	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}
	
	@Override
	public String toString(){
		
		StringBuilder bf = new StringBuilder();
		
		bf.append("Risultato: ");
		bf.append(this.risultato);
		
		bf.append("\n");
		
		SimpleDateFormat dateFormat = DateUtils.getSimpleDateFormatMs();
		
		bf.append("DataInizio: ");
		if(this.dataInizio!=null)
			bf.append(dateFormat.format(this.dataInizio));
		else
			bf.append("-");
		
		bf.append("\n");
		
		bf.append("DataFine: ");
		if(this.dataFine!=null)
			bf.append(dateFormat.format(this.dataFine));
		else
			bf.append("-");
		
		bf.append("\n");
		
		bf.append("UltimoAggiornamento: ");
		if(this.dateCheck!=null)
			bf.append(dateFormat.format(this.dateCheck));
		else
			bf.append("-");
		
		return bf.toString();
	}
	
}
