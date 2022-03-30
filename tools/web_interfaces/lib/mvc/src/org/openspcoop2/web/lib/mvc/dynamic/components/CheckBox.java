/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.web.lib.mvc.dynamic.components;

import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * CheckBox
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class CheckBox extends BaseComponent<Boolean> {

	public CheckBox(Parameter<Boolean> parameter, IDynamicLoader loader) {
		super(parameter, loader);
	}

	@Override
	public DataElement toDataElement() throws ParameterException {
		DataElement de = new DataElement();
		de.setName(this.getId());
		de.setLabel(this.getRendering().getLabel()); 
		de.setLabelRight(this.getRendering().getLabelRight());
		de.setPostBack_viaPOST(this.getRefreshParamIds().size() > 0);
		de.setRequired(this.getRendering().isRequired()); 
		de.setNote(this.getRendering().getSuggestion());
		de.setType(DataElementType.CHECKBOX);
		de.setValue(this.getValue() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED );
		de.setSelected(this.getValue() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED );

		return de;
	}
	
	@Override
	public void setValueFromRequest(String parameterValue) throws ParameterException {
		if(parameterValue == null) {
			if(this.getRendering().getDefaultValue()) {
				this.setValue(this.getRendering().getDefaultValue());
			}
			else {
				this.setValue(null);
			}
		}else {
			this.setValue(ServletUtils.isCheckBoxEnabled(parameterValue));
		}
	}
}
