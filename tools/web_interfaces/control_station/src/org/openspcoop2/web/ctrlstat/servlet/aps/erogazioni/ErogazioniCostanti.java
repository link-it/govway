package org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni;

import java.util.Vector;

import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;

public class ErogazioniCostanti extends AccordiServizioParteSpecificaCostanti {
	
	public final static String OBJECT_NAME_ASPS_EROGAZIONI = "aspsErogazioni";
	
	public final static String SERVLET_NAME_ASPS_EROGAZIONI_CHANGE = OBJECT_NAME_ASPS_EROGAZIONI+"Change.do";
	public final static String SERVLET_NAME_ASPS_EROGAZIONI_LIST = OBJECT_NAME_ASPS_EROGAZIONI+"List.do";
	
	public final static Vector<String> SERVLET_ASPS_EROGAZIONI = new Vector<String>();
	static{
		SERVLET_ASPS_EROGAZIONI.add(SERVLET_NAME_ASPS_EROGAZIONI_CHANGE);
		SERVLET_ASPS_EROGAZIONI.add(SERVLET_NAME_ASPS_EROGAZIONI_LIST);
	}
	
	public final static String LABEL_ASPS_EROGAZIONI = "Erogazioni";
	public final static String LABEL_ASPS_EROGAZIONE = "Erogazione";
	public final static String LABEL_ASPS_FRUIZIONI = "Fruizioni";
	public final static String LABEL_ASPS_FRUIZIONE = "Fruizione";
	
	public final static String LABEL_ASPS_LISTA_EROGAZIONI_COLONNA_SERVIZIO = "Servizio";
	public final static String LABEL_ASPS_LISTA_EROGAZIONI_COLONNA_CONFIGURAZIONE = "Configurazione";
	
	public final static String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_LISTA_FRUIZIONI = "fruizioni";
	public final static String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_LISTA_EROGAZIONI = "erogazioni";
	
	public final static String MESSAGE_METADATI_SERVIZIO_EROGAZIONI = "API {0}: {1}";
	public final static String MESSAGE_METADATI_SERVIZIO_EROGAZIONI_CON_PROFILO = "API {0}: {1}, Profilo Interoperabilit&agrave;: {2}";
	
}

