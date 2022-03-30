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
package org.openspcoop2.pdd.monitor.driver;

/**
 * FiltroStatoConsegnaAsincrona
 * 
 * @author Andrea Poli (apoli@link.it)
 *
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class FiltroStatoConsegnaAsincrona {

	private boolean orderByInCoda;
	private boolean orderByInRiconsegna;
	private boolean orderByInMessageBox;
	public boolean isOrderByInCoda() {
		return this.orderByInCoda;
	}
	public void setOrderByInCoda(boolean orderByInCoda) {
		this.orderByInCoda = orderByInCoda;
	}
	public boolean isOrderByInRiconsegna() {
		return this.orderByInRiconsegna;
	}
	public void setOrderByInRiconsegna(boolean orderByInRiconsegna) {
		this.orderByInRiconsegna = orderByInRiconsegna;
	}
	public boolean isOrderByInMessageBox() {
		return this.orderByInMessageBox;
	}
	public void setOrderByInMessageBox(boolean orderByInMessageBox) {
		this.orderByInMessageBox = orderByInMessageBox;
	}
	
}
