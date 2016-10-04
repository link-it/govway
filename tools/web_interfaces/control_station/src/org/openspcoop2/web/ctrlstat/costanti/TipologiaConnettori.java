/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.web.ctrlstat.costanti;

/**
 * TipologiaConnettori
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public enum TipologiaConnettori {

	// public static final int CREATE = 1;
	// public static final int UPDATE = 2;
	// public static final int DELETE = 3;

	TIPOLOGIA_CONNETTORI, TIPOLOGIA_CONNETTORI_ALL, TIPOLOGIA_CONNETTORI_HTTP;

	@Override
	public String toString() {

		switch (this) {
			case TIPOLOGIA_CONNETTORI:
				return "TIPOLOGIA_CONNETTORI";
			case TIPOLOGIA_CONNETTORI_ALL:
				return "ALL";
			case TIPOLOGIA_CONNETTORI_HTTP:
				return "HTTP";
			default:
				return null;
		}

	}
}
