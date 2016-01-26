/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.web.ctrlstat.servlet.pdd;

import java.util.Vector;

/**
 * PddCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PddCostanti {

	/* OBJECT NAME */
	
	public final static String OBJECT_NAME_PDD = "pdd";
	
	public final static String OBJECT_NAME_PDD_SINGLEPDD = "pddSinglePdD";
	
	public final static String OBJECT_NAME_PDD_SOGGETTI = "pddSoggetti";
	
	
	/* SERVLET NAME */
	
	public final static String SERVLET_NAME_PDD_ADD = OBJECT_NAME_PDD+"Add.do";
	public final static String SERVLET_NAME_PDD_CHANGE = OBJECT_NAME_PDD+"Change.do";
	public final static String SERVLET_NAME_PDD_DELETE = OBJECT_NAME_PDD+"Del.do";
	public final static String SERVLET_NAME_PDD_LIST = OBJECT_NAME_PDD+"List.do";
	public final static Vector<String> SERVLET_PDD = new Vector<String>();
	static{
		SERVLET_PDD.add(SERVLET_NAME_PDD_ADD);
		SERVLET_PDD.add(SERVLET_NAME_PDD_CHANGE);
		SERVLET_PDD.add(SERVLET_NAME_PDD_DELETE);
		SERVLET_PDD.add(SERVLET_NAME_PDD_LIST);
	}
	
	public final static String SERVLET_NAME_PDD_SINGLEPDD_ADD = OBJECT_NAME_PDD_SINGLEPDD+"Add.do";
	public final static String SERVLET_NAME_PDD_SINGLEPDD_CHANGE = OBJECT_NAME_PDD_SINGLEPDD+"Change.do";
	public final static String SERVLET_NAME_PDD_SINGLEPDD_DELETE = OBJECT_NAME_PDD_SINGLEPDD+"Del.do";
	public final static String SERVLET_NAME_PDD_SINGLEPDD_LIST = OBJECT_NAME_PDD_SINGLEPDD+"List.do";
	public final static Vector<String> SERVLET_PDD_SINGLEPDD = new Vector<String>();
	static{
		SERVLET_PDD_SINGLEPDD.add(SERVLET_NAME_PDD_SINGLEPDD_ADD);
		SERVLET_PDD_SINGLEPDD.add(SERVLET_NAME_PDD_SINGLEPDD_CHANGE);
		SERVLET_PDD_SINGLEPDD.add(SERVLET_NAME_PDD_SINGLEPDD_DELETE);
		SERVLET_PDD_SINGLEPDD.add(SERVLET_NAME_PDD_SINGLEPDD_LIST);
	}
	
	public final static String SERVLET_NAME_PDD_SOGGETTI_LIST = OBJECT_NAME_PDD_SOGGETTI+"List.do";
	public final static Vector<String> SERVLET_PDD_SOGGETTI = new Vector<String>();
	static{
		SERVLET_PDD_SOGGETTI.add(SERVLET_NAME_PDD_SOGGETTI_LIST);
	}
	

	
	
	/* LABEL GENERALI */
	
	public final static String LABEL_PORTE_DI_DOMINIO = "Porte di Dominio";
	public final static String LABEL_PORTA_DI_DOMINIO = "Porta di Dominio";
	public final static String LABEL_SOGGETTI = "Soggetti";
	
	public final static String LABEL_PDD_MENU_VISUALE_AGGREGATA = "Porte Dominio";
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_PDD_NOME = "nome";
	public final static String PARAMETRO_PDD_ID = "id";
	public final static String PARAMETRO_PDD_TIPOLOGIA = "tipo";
	public final static String PARAMETRO_PDD_DESCRIZIONE = "descrizione";
	public final static String PARAMETRO_PDD_PROTOCOLLO = "protocollo";
	public final static String PARAMETRO_PDD_PROTOCOLLO_GESTIONE = "protocollo_gestione";
	public final static String PARAMETRO_PDD_INDIRIZZO_PUBBLICO = "ip";
	public final static String PARAMETRO_PDD_PORTA_PUBBLICA = "porta";
	public final static String PARAMETRO_PDD_INDIRIZZO_GESTIONE = "ip_gestione";
	public final static String PARAMETRO_PDD_PORTA_GESTIONE = "porta_gestione";
	public final static String PARAMETRO_PDD_IMPLEMENTAZIONE = "implementazione";
	public final static String PARAMETRO_PDD_SUBJECT = "subject";
	public final static String PARAMETRO_PDD_CLIENT_AUTH = "client_auth";
	
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PDD_NOME = "Nome";
	public final static String LABEL_PDD_ID = "Id";
	public final static String LABEL_PDD_TIPOLOGIA ="Tipo";
	public final static String LABEL_PDD_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PDD_PROTOCOLLO = "Protocollo";
	public final static String LABEL_PDD_PROTOCOLLO_GESTIONE = "Protocollo gestione";
	public final static String LABEL_PDD_INDIRIZZO_PUBBLICO = "Indirizzo pubblico (IP/Hostname)";
	public final static String LABEL_PDD_PORTA_PUBBLICA = "Porta pubblica";
	public final static String LABEL_PDD_INDIRIZZO_GESTIONE = "Indirizzo gestione (IP/Hostname)";
	public final static String LABEL_PDD_PORTA_GESTIONE = "Porta gestione";
	public final static String LABEL_PDD_INDIRIZZO = "IP/Hostname";
	public final static String LABEL_PDD_IMPLEMENTAZIONE = "Implementazione";
	public final static String LABEL_PDD_SUBJECT = "Subject";
	public final static String LABEL_PDD_CLIENT_AUTH = "Client Auth";
	public final static String LABEL_PDD_SOGGETTI = "Soggetti";
	public final static String LABEL_PDD_CONFIGURAZIONE_SISTEMA = "Configurazione";
	
	public final static String[] LABEL_TIPI = { "operativo", "non-operativo", "esterno" };
	public final static String[] LABEL_TIPI_SOLO_OPERATIVI = { "operativo", "non-operativo" };
	public final static String[] LABEL_TIPO_SOLO_ESTERNO = { "esterno" };
	
	
	
	/* DEFAULT VALUE PARAMETRI */
	
	public final static String DEFAULT_PDD_PORTA = "80";
	public final static String DEFAULT_PDD_IMPLEMENTAZIONE = "standard";
	public final static String DEFAULT_PDD_PROTOCOLLO = "http";
	public final static String[] DEFAULT_PDD_PROTOCOLLI = { "http", "https" };
}
