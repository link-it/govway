package org.openspcoop2.pdd.core.transazioni;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;

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
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateformatPattern); // SimpleDateFormat non e' thread-safe
		
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
