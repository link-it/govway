package org.openspcoop2.monitor.engine.dynamic;

import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;

/**
 * IDynamicFilter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IDynamicFilter {
	
	/*
	 * RICERCHE
	 */
	public org.openspcoop2.monitor.sdk.condition.IFilter createConditionFilter(Context context) throws SearchException;
	

}
