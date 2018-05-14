package org.openspcoop2.web.monitor.core.dynamic.components;

import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.parameters.Parameter;

public class CheckBox extends BaseComponent<Boolean> {

	public CheckBox(Parameter<Boolean> parameter, IDynamicLoader loader) {
		super(parameter, loader);
	}

}
