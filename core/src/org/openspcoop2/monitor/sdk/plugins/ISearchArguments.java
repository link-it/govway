package org.openspcoop2.monitor.sdk.plugins;

import java.util.List;

import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.exceptions.ValidationException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;

/**
 * ISearchArguments
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ISearchArguments {

	public List<Parameter<?>> getParameters(Context context) throws SearchException, ParameterException;
	
	public void updateRendering(Parameter<?> parameter,Context context) throws SearchException, ParameterException;
	
	public void onChangeValue(Parameter<?> parameter, Context context) throws SearchException, ParameterException;
	
	public void validate(Context context) throws ValidationException,SearchException, ParameterException;
	
	
}
