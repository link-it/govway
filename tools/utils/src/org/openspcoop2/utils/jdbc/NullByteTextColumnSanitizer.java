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

import org.openspcoop2.utils.TipiDatabase;

/**
 * Utility per la sanitizzazione di colonne text contenenti byte nulli (0x00)
 * non supportati da alcuni database (PostgreSQL, MySQL) nei campi text/varchar.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NullByteTextColumnSanitizer {

	private NullByteTextColumnSanitizer() {}

	public static boolean needsSanitization(TipiDatabase tipoDatabase) {
		return TipiDatabase.POSTGRESQL.equals(tipoDatabase) || TipiDatabase.MYSQL.equals(tipoDatabase);
	}
	public static boolean needsSanitization(String tipoDatabase) {
		return TipiDatabase.POSTGRESQL.equals(tipoDatabase) || TipiDatabase.MYSQL.equals(tipoDatabase);
	}

	public static String sanitize(TipiDatabase tipoDatabase, String value) {
		if(tipoDatabase==null) {
			return value;
		}
		return sanitize(tipoDatabase.toString(), value);
	}
	public static String sanitize(String tipoDatabase, String value) {
		if(value==null || tipoDatabase==null) {
			return value;
		}
		if(TipiDatabase.POSTGRESQL.equals(tipoDatabase) && PostgreSQLUtilities.containsNullByteSequence(value)) {
			return PostgreSQLUtilities.normalizeString(value);
		}
		if(TipiDatabase.MYSQL.equals(tipoDatabase) && MySQLUtilities.containsNullByteSequence(value)) {
			return MySQLUtilities.normalizeString(value);
		}
		return value;
	}

}
