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

package org.openspcoop2.core.controllo_traffico.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.utils.date.DateUtils;

/**
 * RisultatoAllarme 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RisultatoStato implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer stato;
	private Date dateCheck;
	
	public Integer getStato() {
		return this.stato;
	}
	public void setStato(Integer stato) {
		this.stato = stato;
	}
	public Date getDateCheck() {
		return this.dateCheck;
	}
	public void setDateCheck(Date dateCheck) {
		this.dateCheck = dateCheck;
	}
	
	@Override
	public String toString(){
		
		StringBuilder bf = new StringBuilder();
		
		bf.append("Stato: ");
		if(this.stato!=null)
			bf.append(this.stato);
		else
			bf.append("-");
		
		bf.append("\n");
		
		SimpleDateFormat dateFormat = DateUtils.getSimpleDateFormatMs();
		bf.append("UltimoAggiornamento: ");
		if(this.dateCheck!=null)
			bf.append(dateFormat.format(this.dateCheck));
		else
			bf.append("-");
		
		return bf.toString();
	}
	
	
}
