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

package org.openspcoop2.pdd.logger.record;

import org.openspcoop2.core.commons.CoreException;

/**     
 * AbstractDatoRicostruzione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractDatoRicostruzione<T> {

	protected AbstractDatoRicostruzione(String dato, InfoDato info) throws CoreException{
		this.dato = this.convertToObject(dato);
		this.info = info;
	}
	protected AbstractDatoRicostruzione(InfoDato info, T dato) {
		this.dato = dato;
		this.info = info;
	}
	
	T dato;
	InfoDato info;
		
	public InfoDato getInfo() {
		return this.info;
	}
	public T getDato() {
		return this.dato;
	}

	public String convertToString() {
		if(this.dato==null){
			return CostantiDati.NON_PRESENTE;
		}else{
			return this.dato.toString();
		}
	}
	
	protected abstract T convertToObject(String dato) throws CoreException;
		
}
