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
 * StringDatoRicostruzione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StringDatoRicostruzione extends AbstractDatoRicostruzione<String> {

	public StringDatoRicostruzione(InfoDato info, String dato, boolean checkSeparator) throws CoreException {
		super(info, dato);
		if(checkSeparator && dato!=null &&
			dato.contains(CostantiDati.SEPARATOR)){
			throw new CoreException("Il dato ["+dato+"] contiene il carattere separatore ["+CostantiDati.SEPARATOR+"]");
		}
	}

	public StringDatoRicostruzione(String dato, InfoDato info) throws CoreException {
		super(dato, info);
	}

	@Override
	public String convertToString() {
		if(this.dato==null){
			return CostantiDati.NON_PRESENTE;
		}
		return this.dato;
	}
	
	@Override
	protected String convertToObject(String dato) throws CoreException {
		if(dato==null){
			throw new CoreException("Dato non fornito");
		}
		if(CostantiDati.NON_PRESENTE.equals(dato)){
			return null;
		}
		return dato;
	}

}
