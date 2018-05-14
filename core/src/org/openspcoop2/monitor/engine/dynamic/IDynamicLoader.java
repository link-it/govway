package org.openspcoop2.monitor.engine.dynamic;

import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;

import java.util.List;

/**
 * IDynamicLoader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IDynamicLoader{
	
	public String getClassName() throws SearchException;
	
	public String getClassSimpleName() throws SearchException;
	
	public Object newInstance() throws SearchException;

	public List<Parameter<?>> getParameters(Context context) throws SearchException;
	
	public void updateRendering(Parameter<?> parameter, Context context) throws SearchException;

	public void valueSelectedListener(Parameter<?> parameter, Context context);

	public List<StatisticType> getEnabledStatisticType(Context context) throws SearchException;
}
