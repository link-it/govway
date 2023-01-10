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
package org.openspcoop2.pdd.core.eventi;

import java.text.SimpleDateFormat;

import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;
import org.openspcoop2.utils.date.DateUtils;

/**     
 * EventiUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EventiUtilities {

	public static String toString(Evento evento){
		StringBuilder bf = new StringBuilder();

		SimpleDateFormat sdf = DateUtils.getSimpleDateFormatMs();
		if(evento.getOraRegistrazione()!=null){
			bf.append("<").append(sdf.format(evento.getOraRegistrazione())).append(">");
		}

		if(evento.getTipo()!=null){
			if(bf.length()>0){
				bf.append(" ");
			}
			bf.append("Tipo:").append(evento.getTipo());
		}

		if(evento.getCodice()!=null){
			if(bf.length()>0){
				bf.append(" ");
			}
			bf.append("Codice:").append(evento.getCodice());
		}

		if(evento.getSeverita()>=0){
			if(bf.length()>0){
				bf.append(" ");
			}
			bf.append("Severita:");
			try{
				bf.append(SeveritaConverter.toSeverita(evento.getSeverita()).name());
			}catch(Exception e){
				bf.append("[ERRORE:"+e.getMessage()+"]");
			}
		}

		if(evento.getDescrizione()!=null){
			if(bf.length()>0){
				bf.append(" ");
			}
			bf.append("Descrizione:").append(evento.getDescrizione());
		}

		if(evento.getIdTransazione()!=null){
			if(bf.length()>0){
				bf.append(" ");
			}
			bf.append("IdTransazione:").append(evento.getIdTransazione());
		}

		if(evento.getClusterId()!=null){
			if(bf.length()>0){
				bf.append(" ");
			}
			bf.append("IdCluster:").append(evento.getClusterId());
		}

		return bf.toString();
	}

}
