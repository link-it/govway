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
import org.openspcoop2.utils.date.DateUtils;

/**     
 * TimestampDatoRicostruzione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimestampDatoRicostruzione extends AbstractDateDatoRicostruzione {

	public TimestampDatoRicostruzione(InfoDato info, Date dato) throws CoreException {
		super(info, dato);
	}

	public TimestampDatoRicostruzione(String dato, InfoDato info) throws CoreException {
		super(dato, info);
	}

	private static final String DATE_FORMAT_PATTERN = "yyyyMMddHHmmssSSS";

	@Override
	SimpleDateFormat getDateFormat() {
		return DateUtils.getDefaultDateTimeFormatter(DATE_FORMAT_PATTERN);
	}
	
}
