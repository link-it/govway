/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.controllo_traffico.driver;

/**
 * CostantiControlloTraffico
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiControlloTraffico {

	public final static String LABEL_MODALITA_SINCRONIZZAZIONE = "Sincronizzazione";	
	public final static String LABEL_MODALITA_SINCRONIZZAZIONE_DEFAULT = "Default";
	public final static String LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE = "Locale";
	public final static String LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI = "Locale - Quota divisa sui nodi";
	public final static String LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA = "Distribuita";
		
	public final static String LABEL_MODALITA_IMPLEMENTAZIONE = "Implementazione";
	
	public final static String LABEL_MODALITA_IMPLEMENTAZIONE_DATABASE = "Database";
	public final static String LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST = "Hazelcast";
	public final static String LABEL_MODALITA_IMPLEMENTAZIONE_REDIS = "Redis";
		
	
	public final static String LABEL_MODALITA_CONTATORI = "Misurazione";
	
	public final static String LABEL_MODALITA_CONTATORI_EXACT = "Esatta";
	public final static String LABEL_MODALITA_CONTATORI_APPROXIMATED = "Approssimata";
	
	public final static String LABEL_MODALITA_TIPOLOGIA = "Algoritmo";
	
	public final static String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC = "full-sync";
	public final static String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE = "near-cache";
	public final static String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE = "local-cache";
	public final static String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC = "remote-sync";
	public final static String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC = "remote-async";

}
