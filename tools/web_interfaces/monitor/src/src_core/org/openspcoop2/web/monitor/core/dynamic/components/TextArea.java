package org.openspcoop2.web.monitor.core.dynamic.components;

import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.parameters.Parameter;

public class TextArea extends BaseComponent<String> {

	public TextArea(Parameter<String> parameter,IDynamicLoader loader) {
		super(parameter,loader);
	}

}
