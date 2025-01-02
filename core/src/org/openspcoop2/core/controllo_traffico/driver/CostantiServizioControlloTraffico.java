/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

    public static final String OPERAZIONE_REGISTER_POLICY = "registerPolicy";
    
    public static final String OPERAZIONE_GET_POLICY = "getPolicy";
    
    public static final String OPERAZIONE_REGISTER_START_REQUEST = "registerStartRequest";
    
    public static final String OPERAZIONE_UPDATE_START_REQUEST = "updateStartRequest";
    
    public static final String OPERAZIONE_REGISTER_STOP_REQUEST = "registerStopRequest";
    
    public static final String OPERAZIONE_SIZE_ACTIVE_THREADS_POLICY = "sizeActiveThreadsPolicy";
    
    public static final String OPERAZIONE_PRINT_KEYS_POLICY = "printKeysPolicy";
    
    public static final String OPERAZIONE_PRINT_INFO_POLICY = "printInfoPolicy";
    
    public static final String OPERAZIONE_REMOVE_ACTIVE_THREADS_POLICY = "removeActiveThreadsPolicy";
    
    public static final String OPERAZIONE_REMOVE_ACTIVE_THREADS_POLICY_UNSAFE = "removeActiveThreadsPolicyUnsafe";
    
    public static final String OPERAZIONE_REMOVE_ALL_ACTIVE_THREADS_POLICY = "removeAllActiveThreadsPolicy";
    
    public static final String OPERAZIONE_RESET_COUNTERS_ACTIVE_THREADS_POLICY = "resetCountersActiveThreadsPolicy";
    
    public static final String OPERAZIONE_RESET_COUNTERS_ALL_ACTIVE_THREADS_POLICY = "resetCountersAllActiveThreadsPolicy";
    
    public static final String PARAMETER_ACTIVE_ID = "activeId";
    
    public static final String PARAMETER_GROUP_BY_ID = "groupById";
    
    public static final String PARAMETER_MISURAZIONI_TRANSAZIONE = "misurazioniTransazione";
    
    public static final String PARAMETER_APPLICABILE = "applicabile";
    
    public static final String PARAMETER_VIOLATA = "violata";
    
    public static final String PARAMETER_SUM = "sum";
    
    public static final String PARAMETER_SEPARATOR = "separator";
}
