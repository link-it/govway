package org.openspcoop2.web.monitor.core.dynamic.components;

import java.util.Date;

import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.parameters.Parameter;

public class Calendar extends BaseComponent<Date> {

	public Calendar(Parameter<Date> parameter, IDynamicLoader loader) {
		 super(parameter, loader);
	}
	
}
