/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.random;

import java.security.SecureRandom;

/**
 * RandomUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RandomUtilities {

	private static SecureRandom _rnd = null;
	private static synchronized void initRandom() {
		if(_rnd==null) {
			_rnd = new SecureRandom();
		}
	}
	public static SecureRandom getSecureRandom() {
		if(_rnd==null) {
			initRandom();
		}
		return _rnd;
	}
	public static java.util.Random getRandom() {
		if(_rnd==null) {
			initRandom();
		}
		return _rnd;
	}
	
}
