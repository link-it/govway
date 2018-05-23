package org.openspcoop2.pdd.core.eventi;

import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;

import java.text.SimpleDateFormat;

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
