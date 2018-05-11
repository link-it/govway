package org.openspcoop2.monitor.engine.condition;

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
	
}
