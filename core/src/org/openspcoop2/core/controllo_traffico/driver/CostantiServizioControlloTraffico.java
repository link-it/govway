/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
 * CostantiServizioControlloTraffico 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiServizioControlloTraffico {

    public final static String OPERAZIONE_REGISTER_POLICY = "registerPolicy";
    
    public final static String OPERAZIONE_GET_POLICY = "getPolicy";
    
    public final static String OPERAZIONE_REGISTER_START_REQUEST = "registerStartRequest";
    
    public final static String OPERAZIONE_UPDATE_START_REQUEST = "updateStartRequest";
    
    public final static String OPERAZIONE_REGISTER_STOP_REQUEST = "registerStopRequest";
    
    public final static String OPERAZIONE_SIZE_ACTIVE_THREADS_POLICY = "sizeActiveThreadsPolicy";
    
    public final static String OPERAZIONE_PRINT_KEYS_POLICY = "printKeysPolicy";
    
    public final static String OPERAZIONE_PRINT_INFO_POLICY = "printInfoPolicy";
    
    public final static String OPERAZIONE_REMOVE_ACTIVE_THREADS_POLICY = "removeActiveThreadsPolicy";
    
    public final static String OPERAZIONE_REMOVE_ALL_ACTIVE_THREADS_POLICY = "removeAllActiveThreadsPolicy";
    
    public final static String OPERAZIONE_RESET_COUNTERS_ACTIVE_THREADS_POLICY = "resetCountersActiveThreadsPolicy";
    
    public final static String OPERAZIONE_RESET_COUNTERS_ALL_ACTIVE_THREADS_POLICY = "resetCountersAllActiveThreadsPolicy";
    
    public final static String PARAMETER_ACTIVE_ID = "activeId";
    
    public final static String PARAMETER_GROUP_BY_ID = "groupById";
    
    public final static String PARAMETER_MISURAZIONI_TRANSAZIONE = "misurazioniTransazione";
    
    public final static String PARAMETER_APPLICABILE = "applicabile";
    
    public final static String PARAMETER_VIOLATA = "violata";
    
    public final static String PARAMETER_SUM = "sum";
    
    public final static String PARAMETER_SEPARATOR = "separator";
}
