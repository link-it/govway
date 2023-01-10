/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.transazioni;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.utils.date.DateUtils;
import org.slf4j.Logger;

/**     
 * DateUtility
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DateUtility {

	private final static String dateformatPattern = "yyyyMMddHHmm"; // utile per il filtro duplicati

	public static Timestamp getTimestampIntoIdProtocollo(Logger log, IBustaBuilder<?> bustaBuilder, String idMessaggio){
		
		Date dataIdBusta = null;
		try{
			dataIdBusta = bustaBuilder.extractDateFromID(idMessaggio);
		}catch(Exception e){
			log.error("Errore durante l'estrazione della data dall'identificativo di protocollo [id: "+idMessaggio+"]: "+e.getMessage(),e);
			return null;
		}
		
		if(dataIdBusta==null){
			return null;
		}
		
		SimpleDateFormat dateFormat = DateUtils.getDefaultDateTimeFormatter(dateformatPattern);
		
		// Le date presenti in un identificativo di protocollo, possono contenere
		// informazioni fino al millisecondo.
		// Per il filtro duplicati si vuole invece mantenere le informazioni fino al minuto
		// al fine di raggruppare gli identificativi in un insieme minimo che renda efficente l'indice di ricerca
		String data = dateFormat.format(dataIdBusta);
		
		try{
			return new Timestamp(dateFormat.parse(data).getTime());
		}catch(Exception e){
			log.error("Errore durante la conversione della data estratta dall'identificativo di protocollo [data estratta: "+data+"]: "+e.getMessage(),e);
			return null;
		}
	}
	
}
