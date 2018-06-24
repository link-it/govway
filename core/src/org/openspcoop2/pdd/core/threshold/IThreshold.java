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


package org.openspcoop2.pdd.core.threshold;

import java.util.Properties;

/**
 * Interfaccia che definisce un meccanismo di Soglia sullo spazio libero rimasto
 * nelle risorse utilizzate dalla PdD. 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IThreshold {

	
	/**
	 * Controlla lo spazio libero
	 * 
	 * @param parametri Parametri del threshold
	 * @return true se vi e' ancora spazio libero che rispetti la soglia
	 */
	public boolean check(Properties parametri) throws ThresholdException;
	
}
