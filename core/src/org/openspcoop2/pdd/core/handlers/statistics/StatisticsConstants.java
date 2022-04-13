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

package org.openspcoop2.pdd.core.handlers.statistics;

import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;

/**
 * StatisticsConstants
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsConstants {

	public static final MapKey<String> DATA_INGRESSO_RICHIESTA = Map.newMapKey("STAT_DATA_INGRESSO_RICHIESTA"); 
	public static final MapKey<String> DIMENSIONE_INGRESSO_RICHIESTA = Map.newMapKey("STAT_DIMENSIONE_INGRESSO_RICHIESTA"); 
	
	public static final MapKey<String> DATA_USCITA_RICHIESTA = Map.newMapKey("STAT_DATA_USCITA_RICHIESTA"); 
	public static final MapKey<String> DIMENSIONE_USCITA_RICHIESTA = Map.newMapKey("STAT_DIMENSIONE_USCITA_RICHIESTA");
	
	public static final MapKey<String> DATA_INGRESSO_RISPOSTA = Map.newMapKey("STAT_DATA_INGRESSO_RISPOSTA"); 
	public static final MapKey<String> DIMENSIONE_INGRESSO_RISPOSTA = Map.newMapKey("STAT_DIMENSIONE_INGRESSO_RISPOSTA"); 
	
	public static final MapKey<String> DATA_USCITA_RISPOSTA = Map.newMapKey("STAT_DATA_USCITA_RISPOSTA"); 
	public static final MapKey<String> DIMENSIONE_USCITA_RISPOSTA = Map.newMapKey("STAT_DIMENSIONE_USCITA_RISPOSTA");
	
}
