package org.openspcoop2.monitor.sdk.plugins;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.condition.IFilter;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.exceptions.ValidationException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;

/**
 * SearchProcessing
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class SearchProcessing implements ISearchProcessing {

	@Override
	public List<Parameter<?>> getParameters(Context context) throws SearchException, ParameterException{
		return new ArrayList<Parameter<?>>();
	}
	
	@Override
	public void updateRendering(Parameter<?> parameter,Context context) throws SearchException, ParameterException{
		
	}
	
	@Override
	public void onChangeValue(Parameter<?> parameter, Context context) throws SearchException, ParameterException{
		
	}

	@Override
	public void validate(Context context) throws ValidationException,
			SearchException, ParameterException {
		
	}

	@Override
	public abstract IFilter createSearchFilter(Context context)
			throws SearchException;
}
