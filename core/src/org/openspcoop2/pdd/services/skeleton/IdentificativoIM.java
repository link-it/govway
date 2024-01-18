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

package org.openspcoop2.pdd.services.skeleton;

import java.util.Date;

import org.openspcoop2.utils.date.DateUtils;
import org.slf4j.Logger;

/**
 * IdentificativoIM
 *
 * @author apoli@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdentificativoIM {

	private static final String DATE_PREFIX_SEPARATOR = "@";
	private static final String FORMAT = "yyyyMMddHHmmssSSS";
	
	private String id;
	private Date data;
	
	public IdentificativoIM(String id, Date data) {
		this.id = id;
		this.data = data;
	} 
	
	public String getId() {
		return this.id;
	}
	public String getIdWithDate() {
		if(this.data!=null) {
			return DateUtils.getSimpleDateFormat(FORMAT).format(this.data)+DATE_PREFIX_SEPARATOR+this.id;
		}
		else {
			return this.id;
		}
	}

	public Date getData() {
		return this.data;
	}
	
	public static IdentificativoIM getIdentificativoIM(String identificativo, Logger log) {
		//System.out.println("IM indexOf["+identificativo.indexOf(DATE_PREFIX_SEPARATOR)+"]");
		if(identificativo.length()>(FORMAT.length()+1) && 
				identificativo.contains(DATE_PREFIX_SEPARATOR) && 
				!identificativo.endsWith(DATE_PREFIX_SEPARATOR) && 
				!identificativo.startsWith(DATE_PREFIX_SEPARATOR) && 
				identificativo.indexOf(DATE_PREFIX_SEPARATOR)==FORMAT.length()) {
			String dataString = identificativo.substring(0,identificativo.indexOf(DATE_PREFIX_SEPARATOR));
			String id = identificativo.substring(identificativo.indexOf(DATE_PREFIX_SEPARATOR)+1);
			try {
				Date data = DateUtils.getSimpleDateFormat(FORMAT).parse(dataString);
				IdentificativoIM idIM = new IdentificativoIM(id, data);
				return idIM;
			}
			catch(Exception e) {
				log.error("Conversione data '"+dataString+"' non riuscita: "+e.getMessage(),e);
			}
		}
		return new IdentificativoIM(identificativo, null);
	}
}
