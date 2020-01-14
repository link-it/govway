/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.core.registry.constants;
/**
 *
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum ProprietariDocumento {

	accordoServizio,accordoCooperazione,servizio,servizioCorrelato;
	
	@Override
	public String toString() {
		switch (this) {
			case accordoCooperazione:
				return this.name();
			case accordoServizio:
				return this.name();
			case servizio:
			case servizioCorrelato:
				return servizio.name();
			default:
				return "";
		}

	}
}


