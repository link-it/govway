/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.plugins.utils;

/**
 * FilterUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FilterUtils {

	private static int counterAlias = 1; // lasciare Static senno diversi oggetti usano lo stesso numero, e poi se messi in AND/OR i filtri danno errore!
	private static int maxValueCounterAlias = 10000;
	private static synchronized int getNextAliasCounter(){
		if(FilterUtils.counterAlias==FilterUtils.maxValueCounterAlias){
			FilterUtils.counterAlias = 1;
		}
		else{
			FilterUtils.counterAlias++;
		}
		return counterAlias;
	}
	
	public static String getNextAliasTransactionTable(){
		return org.openspcoop2.core.transazioni.utils.AliasTableRicerchePersonalizzate.ALIAS_PREFIX+FilterUtils.getNextAliasCounter();
	}
	
	public static String getNextAliasStatisticsTable(){
		return org.openspcoop2.core.statistiche.utils.AliasTableRicerchePersonalizzate.ALIAS_PREFIX+FilterUtils.getNextAliasCounter();
	}
	
	public static String getNextAliasPluginsTable(){
		return org.openspcoop2.core.plugins.utils.AliasTableRicerchePersonalizzate.ALIAS_PREFIX+FilterUtils.getNextAliasCounter();
	}
	
}
