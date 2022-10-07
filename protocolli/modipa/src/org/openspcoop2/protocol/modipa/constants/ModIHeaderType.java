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
package org.openspcoop2.protocol.modipa.constants;

/**
 * ModIHeaderType
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum ModIHeaderType {

	BOTH_AUTH,
	BOTH_INTEGRITY,
	SINGLE;
	
	public boolean isHeaderDuplicati() {
		return BOTH_AUTH.equals(this) || BOTH_INTEGRITY.equals(this);
	}
	public boolean isUsabledForAuthentication() {
		return BOTH_AUTH.equals(this) || SINGLE.equals(this);
	}
	public boolean isUsabledForIntegrity() {
		return BOTH_INTEGRITY.equals(this) || SINGLE.equals(this);
	}
	
}
