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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.core.commons.CoreException;

/**     
 * AbstractDateDatoRicostruzione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractDateDatoRicostruzione extends AbstractDatoRicostruzione<Date> {

	protected AbstractDateDatoRicostruzione(InfoDato info, Date dato) throws CoreException {
		super(info, dato);
	}

	protected AbstractDateDatoRicostruzione(String dato, InfoDato info) throws CoreException {
		super(dato, info);
	}
	
	abstract SimpleDateFormat getDateFormat();
	
	@Override
	public String convertToString() {
		if(this.dato==null){
			return CostantiDati.NON_PRESENTE;
		}
		return this.getDateFormat().format(this.dato);
	}
	
	@Override
	protected Date convertToObject(String dato) throws CoreException {
		if(dato==null){
			throw new CoreException("Dato non fornito");
		}
		if(CostantiDati.NON_PRESENTE.equals(dato)){
			return null;
		}
		Date d = null;
		try {
			d = this.getDateFormat().parse(dato);
		}catch(Exception e) {
			throw new CoreException("Dato non interpretabile tramite il pattern ["+this.getDateFormat()+"]: "+e.getMessage(),e);
		}
		if(d==null){
			throw new CoreException("Dato non interpretabile tramite il pattern ["+this.getDateFormat()+"]");
		}
		return d;
	}

}
