package org.openspcoop2.web.monitor.core.dynamic.components;

import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.parameters.Parameter;

public class InputSecret extends BaseComponent<String> {

	public InputSecret(Parameter<String> parameter,IDynamicLoader loader) {
		super(parameter,loader);
	}
		
}
