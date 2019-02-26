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
package org.openspcoop2.pdd.core.eventi;

import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;

import java.text.SimpleDateFormat;

/**     
 * EventiUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EventiUtilities {

	private static final String format = "yyyy-MM-dd_HH:mm:ss.SSS";
	public static String toString(Evento evento){
		StringBuffer bf = new StringBuffer();

		SimpleDateFormat sdf = new SimpleDateFormat(format);
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
