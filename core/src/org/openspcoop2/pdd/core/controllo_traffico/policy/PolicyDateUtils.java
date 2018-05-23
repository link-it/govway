package org.openspcoop2.pdd.core.controllo_traffico.policy;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.generic_project.exception.NotFoundException;

import org.openspcoop2.core.controllo_congestione.constants.TipoFinestra;

public class PolicyDateUtils {

	private static final String format = "yyyy-MM-dd_HH:mm:ss.SSS";
	
	public static String toStringIntervalloTemporale(TipoFinestra tipoFinestra, Date leftDate,Date rightDate,Date checkDate, boolean statistic) throws NotFoundException{
		StringBuffer bf = new StringBuffer("");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		
		if(statistic){
			bf.append("[campionamento statistico, finestra osservazione ");
		}
		else{
			bf.append("[campionamento realtime, finestra osservazione ");
		}
		bf.append(tipoFinestra.getValue());
		bf.append(" ");
		if(leftDate!=null && rightDate!=null){
			bf.append(dateFormat.format(leftDate));
			bf.append(" - ");
			bf.append(dateFormat.format(rightDate));
		}
		else{
			bf.append("non disponibile");
		}
		if(statistic){
			bf.append(" (ultimo aggiornamento:").
			append(dateFormat.format(checkDate)).
			append(")");
		}
		bf.append("]");

		return bf.toString();
	}	
	
}
