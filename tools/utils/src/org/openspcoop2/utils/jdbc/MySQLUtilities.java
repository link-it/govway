/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.jdbc;

/**
 * MySQLUtilities
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MySQLUtilities {
	
	private MySQLUtilities() {}

	public static boolean isInvalid0x00error(Throwable t) {
		return org.openspcoop2.utils.Utilities.isInvalid0x00error(t);
	}
	
	public static boolean containsNullByteSequence(String s) {
		return org.openspcoop2.utils.Utilities.containsNullByteSequence(s);
	}

	public static String normalizeString(String s) {

		// MySQL in strict mode rifiuta il salvataggio di NULL (\0x00) caratteri nei campi text.
		// In non-strict mode, il contenuto viene troncato al primo byte nullo.
		return org.openspcoop2.utils.Utilities.removeNullByteSequence(s);

	}

}
