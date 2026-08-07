/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.web.lib.mvc;

import java.io.Serializable;

/**
 * Singolo collegamento verso un elemento correlato al campo di una edit page.
 * Viene raccolto in un {@link DataElementLinks} e reso graficamente tramite l'icona
 * posizionata a fianco del campo.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DataElementLink implements Serializable {

	private static final long serialVersionUID = 1L;

	/** etichetta con cui il collegamento viene presentato nella finestra modale di selezione */
	private String label;

	/** tooltip utilizzato quando il collegamento e' l'unico presente, e quindi raggiungibile direttamente dall'icona */
	private String toolTip;

	private String url;

	private String target;

	public DataElementLink() {
		// default constructor
	}
	public DataElementLink(String label) {
		this.label = label;
	}

	public String getLabel() {
		return DataElement.checkNull(this.label);
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public String getToolTip() {
		return DataElement.checkNull(this.toolTip);
	}
	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	public String getUrl() {
		return DataElement.checkNull(this.url);
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setUrl(String servletName, Parameter ... parameter) {
		this.url = DataElement._getUrlValue(servletName, parameter);
	}

	public String getTarget() {
		return DataElement.checkNull(this.target);
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public void setTarget(TargetType s) {
		this.target = s != null ? s.toString() : null;
	}

}
