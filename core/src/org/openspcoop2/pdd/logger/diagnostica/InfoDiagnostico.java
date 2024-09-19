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

package org.openspcoop2.pdd.logger.diagnostica;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.utils.date.DateUtils;

/**     
 * InfoDiagnostico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InfoDiagnostico {

	/*
	 * Ogni diagnostico e' rappresentato da 6 caratteri che indicano il codice + 
	 * 9 caratteri che rappresentano l'ora nel formato HHMMSSsss + 
	 * 1 caratteri che rappresenta il fatto se deve essere incrementata e di quanti giorni la data giornaliera 
	 * rispetto alla data del primo diagnostici
	 */
		
	private Date gdoFirstDiagnostic;
	private Date gdo;
	private String code;
	
	public Date getGdo() {
		return this.gdo;
	}
	public String getCode() {
		return this.code;
	}
	
	public InfoDiagnostico(Date gdoFirstDiagnostic, MsgDiagnostico msgDiag){
		this.gdoFirstDiagnostic = gdoFirstDiagnostic;
		this.gdo = msgDiag.getGdo();
		this.code = msgDiag.getCodice();
	}
	private InfoDiagnostico(Date gdoFirstDiagnostic){
		this.gdoFirstDiagnostic = gdoFirstDiagnostic;
	}
	
	public String convertToDBValue(){
		SimpleDateFormat dateformat = DateUtils.getDefaultTimeFormatter("HHmmssSSS");
		StringBuilder bf = new StringBuilder();
		bf.append(this.code);
		bf.append(dateformat.format(this.gdo));
		bf.append(diffDay(this.gdo, this.gdoFirstDiagnostic));
		return bf.toString();
	}
	
	public static InfoDiagnostico convertoFromDBColumnValue(Date gdoFirstDiagnostic, String dbValue) throws CoreException{
		if(gdoFirstDiagnostic==null){
			throw new CoreException("Parameter gdoFirstDiagnostic not defined");
		}
		if(dbValue==null){
			throw new CoreException("Parameter dbValue not defined");
		}
		if(dbValue.length()!=16){
			throw new CoreException("Formato diagnostico["+dbValue+"] possiede una lunghezza ["+dbValue.length()+"] differente da quella attesa di 16 caratteri (CODICEHHmmssSSST)");
		}
		
		InfoDiagnostico info = new InfoDiagnostico(gdoFirstDiagnostic);
		
		// ** codice diagnostico **
		info.code = dbValue.substring(0, 6);
		
		// ** data **
		// prelevo la data dal primo diagnostico
		SimpleDateFormat dateformat = DateUtils.getDefaultDateFormatter("yyyyMMdd");
		String data = dateformat.format(gdoFirstDiagnostic);
		// aggiungo il tempo presente nell'informazione letta dal database
		String dataConTime = data + dbValue.substring(6, (6+"HHmmssSSS".length()));
		dateformat =  DateUtils.getDefaultDateTimeFormatter("yyyyMMddHHmmssSSS");
		Date gdo = null;
		try {
			gdo = dateformat.parse(dataConTime);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
		// aggiungo la differenza temporale dei giorni rispetto alla data del primo diagnostico
		long dayDiff = diffDay(gdoFirstDiagnostic, gdo);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(gdo);
		calendar.add(Calendar.DAY_OF_YEAR,(int) dayDiff);
		info.gdo = calendar.getTime();
		
		return info;
	}

	public static long diffDay(Date gdoFirstDiagnosticParam, Date gdo){
		return (gdo.getTime() - gdoFirstDiagnosticParam.getTime()) / (24 * 3600 * 1000);
	}
}
