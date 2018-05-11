package org.openspcoop2.monitor.sdk.plugins;

import org.openspcoop2.monitor.sdk.condition.IFilter;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;

/**
 * ISearchProcessing
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ISearchProcessing extends ISearchArguments {
	
	public IFilter createSearchFilter(Context context) throws SearchException;
	
}
