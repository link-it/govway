/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.core.transazioni.Transazione;

/**
 * TransazioneDaoExt
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneDaoExt extends Transazione {

	private static final long serialVersionUID = 1L;

	// NOTA: l'extends serve solo per utilizzarlo nei metodi
	
	private Transazione wrapped = null;
	
	private boolean useDayIntervalForUpdate = false;
	
	public TransazioneDaoExt(Transazione tr) {
		this.wrapped = tr;
	}
	
	public boolean isUseDayIntervalForUpdate() {
		return this.useDayIntervalForUpdate;
	}
	public void setUseDayIntervalForUpdate(boolean useDayIntervalForUpdate) {
		this.useDayIntervalForUpdate = useDayIntervalForUpdate;
	}
	
	public Transazione getWrapped() {
		return this.wrapped;
	}
}
