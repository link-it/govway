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

package org.openspcoop2.core.transazioni.utils;

import java.io.Serializable;
import java.util.Date;

/**     
 * TempiElaborazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TempiElaborazioneFunzionalita implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Date dataIngresso;
	protected Date dataUscita;
	
	public Date getDataIngresso() {
		return this.dataIngresso;
	}
	public void setDataIngresso(Date dataIngresso) {
		this.dataIngresso = dataIngresso;
	}
	public Date getDataUscita() {
		return this.dataUscita;
	}
	public void setDataUscita(Date dataUscita) {
		this.dataUscita = dataUscita;
	}
	public long getLatenza() {
		if(this.dataUscita!=null && this.dataIngresso!=null) {
			return this.dataUscita.getTime() - this.dataIngresso.getTime();
		}
		else {
			return -1;
		}
	}
	
}