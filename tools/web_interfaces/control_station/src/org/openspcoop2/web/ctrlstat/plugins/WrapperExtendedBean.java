/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.web.ctrlstat.plugins;

/**
 * WrapperExtendedBean
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WrapperExtendedBean {

	private IExtendedCoreServlet extendedServlet;
	private IExtendedBean extendedBean;
	private Object originalBean;
	private boolean manageOriginalBean = true;
	
	public boolean isManageOriginalBean() {
		return this.manageOriginalBean;
	}
	public void setManageOriginalBean(boolean manageOriginalBean) {
		this.manageOriginalBean = manageOriginalBean;
	}
	public IExtendedCoreServlet getExtendedServlet() {
		return this.extendedServlet;
	}
	public void setExtendedServlet(IExtendedCoreServlet extendedServlet) {
		this.extendedServlet = extendedServlet;
	}
	public IExtendedBean getExtendedBean() {
		return this.extendedBean;
	}
	public void setExtendedBean(IExtendedBean extendedBean) {
		this.extendedBean = extendedBean;
	}
	public Object getOriginalBean() {
		return this.originalBean;
	}
	public void setOriginalBean(Object originalBean) {
		this.originalBean = originalBean;
	}
}
