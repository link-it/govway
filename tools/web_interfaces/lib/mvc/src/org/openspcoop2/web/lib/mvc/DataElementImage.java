/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

/**
 * DataElementImage
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DataElementImage {

	private String image;
	private String toolTip;
	private String url;
	private String target;
	private String onClick;
	private boolean showAjaxStatus = true;
	
	public String getImage() {
		return DataElement.checkNull(this.image);
	}
	public void setImage(String image) {
		this.image = image;
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
	public void setUrl(String servletName,Parameter ... parameter) {
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
	public String getOnClick() {
		return DataElement.checkNull(this.onClick);
	}
	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}
	public void setDisabilitaAjaxStatus() {
		this.showAjaxStatus = false;
	}
	public boolean isShowAjaxStatus() {
		return this.showAjaxStatus;
	}
	public void setShowAjaxStatus(boolean showAjaxStatus) {
		this.showAjaxStatus = showAjaxStatus;
	}
	
}
