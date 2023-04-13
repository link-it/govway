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
package org.openspcoop2.web.ctrlstat.servlet.pdd;

import java.util.Vector;

import org.openspcoop2.core.registry.constants.PddTipologia;

/**
 * PddCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PddCostanti {

	/* OBJECT NAME */
	
	public static final String OBJECT_NAME_PDD = "pdd";
	
	public static final String OBJECT_NAME_PDD_SINGLEPDD = "pddSinglePdD";
	
	public static final String OBJECT_NAME_PDD_SOGGETTI = "pddSoggetti";
	
	
	/* SERVLET NAME */
	
	public static final String SERVLET_NAME_PDD_ADD = OBJECT_NAME_PDD+"Add.do";
	public static final String SERVLET_NAME_PDD_CHANGE = OBJECT_NAME_PDD+"Change.do";
	public static final String SERVLET_NAME_PDD_DELETE = OBJECT_NAME_PDD+"Del.do";
	public static final String SERVLET_NAME_PDD_LIST = OBJECT_NAME_PDD+"List.do";
	public static final Vector<String> SERVLET_PDD = new Vector<String>();
	static{
		SERVLET_PDD.add(SERVLET_NAME_PDD_ADD);
		SERVLET_PDD.add(SERVLET_NAME_PDD_CHANGE);
		SERVLET_PDD.add(SERVLET_NAME_PDD_DELETE);
		SERVLET_PDD.add(SERVLET_NAME_PDD_LIST);
	}
	
	public static final String SERVLET_NAME_PDD_SINGLEPDD_ADD = OBJECT_NAME_PDD_SINGLEPDD+"Add.do";
	public static final String SERVLET_NAME_PDD_SINGLEPDD_CHANGE = OBJECT_NAME_PDD_SINGLEPDD+"Change.do";
	public static final String SERVLET_NAME_PDD_SINGLEPDD_DELETE = OBJECT_NAME_PDD_SINGLEPDD+"Del.do";
	public static final String SERVLET_NAME_PDD_SINGLEPDD_LIST = OBJECT_NAME_PDD_SINGLEPDD+"List.do";
	public static final Vector<String> SERVLET_PDD_SINGLEPDD = new Vector<String>();
	static{
		SERVLET_PDD_SINGLEPDD.add(SERVLET_NAME_PDD_SINGLEPDD_ADD);
		SERVLET_PDD_SINGLEPDD.add(SERVLET_NAME_PDD_SINGLEPDD_CHANGE);
		SERVLET_PDD_SINGLEPDD.add(SERVLET_NAME_PDD_SINGLEPDD_DELETE);
		SERVLET_PDD_SINGLEPDD.add(SERVLET_NAME_PDD_SINGLEPDD_LIST);
	}
	
	public static final String SERVLET_NAME_PDD_SOGGETTI_LIST = OBJECT_NAME_PDD_SOGGETTI+"List.do";
	public static final Vector<String> SERVLET_PDD_SOGGETTI = new Vector<String>();
	static{
		SERVLET_PDD_SOGGETTI.add(SERVLET_NAME_PDD_SOGGETTI_LIST);
	}
	

	
	
	/* LABEL GENERALI */
	
	public static final String LABEL_PORTE_DI_DOMINIO = "Porte di Dominio";
	public static final String LABEL_PORTA_DI_DOMINIO = "Porta di Dominio";
	public static final String LABEL_SOGGETTI = "Soggetti";
	
	public static final String LABEL_PDD_MENU_VISUALE_AGGREGATA = "Porte Dominio";
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_PDD_NOME = "nome";
	public static final String PARAMETRO_PDD_ID = "id";
	public static final String PARAMETRO_PDD_TIPOLOGIA = "tipo";
	public static final String PARAMETRO_PDD_DESCRIZIONE = "descrizione";
	public static final String PARAMETRO_PDD_PROTOCOLLO = "protocollo";
	public static final String PARAMETRO_PDD_PROTOCOLLO_GESTIONE = "protocollo_gestione";
	public static final String PARAMETRO_PDD_INDIRIZZO_PUBBLICO = "ip";
	public static final String PARAMETRO_PDD_PORTA_PUBBLICA = "porta";
	public static final String PARAMETRO_PDD_INDIRIZZO_GESTIONE = "ip_gestione";
	public static final String PARAMETRO_PDD_PORTA_GESTIONE = "porta_gestione";
	public static final String PARAMETRO_PDD_IMPLEMENTAZIONE = "implementazione";
	public static final String PARAMETRO_PDD_SUBJECT = "subject";
	public static final String PARAMETRO_PDD_CLIENT_AUTH = "client_auth";
	
	
	/* LABEL PARAMETRI */
	
	public static final String LABEL_PDD_NOME = "Nome";
	public static final String LABEL_PDD_ID = "Id";
	public static final String LABEL_PDD_TIPOLOGIA ="Tipo";
	public static final String LABEL_PDD_DESCRIZIONE = "Descrizione";
	public static final String LABEL_PDD_PROTOCOLLO = "Protocollo";
	public static final String LABEL_PDD_PROTOCOLLO_GESTIONE = "Protocollo gestione";
	public static final String LABEL_PDD_INDIRIZZO_PUBBLICO = "Indirizzo pubblico (IP/Hostname)";
	public static final String LABEL_PDD_PORTA_PUBBLICA = "Porta pubblica";
	public static final String LABEL_PDD_INDIRIZZO_GESTIONE = "Indirizzo gestione (IP/Hostname)";
	public static final String LABEL_PDD_PORTA_GESTIONE = "Porta gestione";
	public static final String LABEL_PDD_INDIRIZZO = "IP/Hostname";
	public static final String LABEL_PDD_IMPLEMENTAZIONE = "Implementazione";
	public static final String LABEL_PDD_SUBJECT = "Subject";
	public static final String LABEL_PDD_CLIENT_AUTH = "Client Auth";
	public static final String LABEL_PDD_SOGGETTI = "Soggetti";
	public static final String LABEL_PDD_CONFIGURAZIONE_SISTEMA = "Configurazione";
	
	public static final String[] LABEL_TIPI = { PddTipologia.OPERATIVO.toString(), PddTipologia.NONOPERATIVO.toString(), PddTipologia.ESTERNO.toString() };
	public static final String[] LABEL_TIPI_SOLO_OPERATIVI = { PddTipologia.OPERATIVO.toString(), PddTipologia.NONOPERATIVO.toString() };
	public static final String[] LABEL_TIPO_SOLO_ESTERNO = {  PddTipologia.ESTERNO.toString() };
	
	
	
	/* DEFAULT VALUE PARAMETRI */
	
	public static final String DEFAULT_PDD_PORTA = "80";
	public static final String DEFAULT_PDD_IMPLEMENTAZIONE = "standard";
	public static final String DEFAULT_PDD_PROTOCOLLO = "http";
	public static final String[] DEFAULT_PDD_PROTOCOLLI = { "http", "https" };
}
