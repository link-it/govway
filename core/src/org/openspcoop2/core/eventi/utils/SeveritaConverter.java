/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.core.eventi.utils;

import org.openspcoop2.core.eventi.constants.TipoSeverita;

/**
 * SeveritaConverter
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SeveritaConverter {

	private static final int FATAL = 0;
	private static final int ERROR = 1;
	private static final int WARN = 2;
	private static final int INFO = 3;
	private static final int DEBUG = 4;
	
	public static TipoSeverita toSeverita(int intValue) throws Exception{
		if(intValue==FATAL){
			return TipoSeverita.FATAL;
		}
		else if(intValue==ERROR){
			return TipoSeverita.ERROR;
		}
		else if(intValue==WARN){
			return TipoSeverita.WARN;
		}
		else if(intValue==INFO){
			return TipoSeverita.INFO;
		}
		else if(intValue==DEBUG){
			return TipoSeverita.DEBUG;
		}
		else{
			throw new Exception("Value ["+intValue+"] unknown");
		}
	}
	
	public static int toIntValue(TipoSeverita severita) throws Exception{
		switch (severita) {
		case FATAL:
			return FATAL;
		case ERROR:
			return ERROR;
		case WARN:
			return WARN;
		case INFO:
			return INFO;
		case DEBUG:
			return DEBUG;
		}
		throw new Exception("Enumeration ["+severita+"] non gestita");
	}
	
}
