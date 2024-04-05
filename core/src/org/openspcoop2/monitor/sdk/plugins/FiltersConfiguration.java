/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.sdk.plugins;

/**
 * FiltersConfiguration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FiltersConfiguration {

	private boolean hideGatewayRole = false;
	private boolean forceInGatewayRole = false;
	private boolean forceOutGatewayRole = false;
	
	private boolean hideProtocol = false;
	
	private boolean hideProviderRole = false;
	private boolean hideProvider = false;
	
	private boolean hideSubscriberRole = false;
	private boolean hideSubscriber = false;
	
	private boolean hideTag = false;
	
	private boolean hideService = false;
	private boolean hideAction = false;
	
	private boolean hideApplication = false;

	public boolean isForceInGatewayRole() {
		return  this.forceInGatewayRole;
	}

	public void setForceInGatewayRole(boolean forceInGatewayRole) {
		this.forceInGatewayRole = forceInGatewayRole;
	}

	public boolean isForceOutGatewayRole() {
		return  this.forceOutGatewayRole;
	}

	public void setForceOutGatewayRole(boolean forceOutGatewayRole) {
		this.forceOutGatewayRole = forceOutGatewayRole;
	}
	
	public boolean isHideApplication() {
		return this.hideApplication;
	}
	
	public void setHideApplication(boolean hideApplication) {
		this.hideApplication = hideApplication;
	}

	public boolean isHideService() {
		return this.hideService;
	}

	public void setHideService(boolean hideService) {
		this.hideService = hideService;
	}

	public boolean isHideAction() {
		return this.hideAction;
	}

	public void setHideAction(boolean hideAction) {
		this.hideAction = hideAction;
	}

	public boolean isHideTag() {
		return this.hideTag;
	}

	public void setHideTag(boolean hideTag) {
		this.hideTag = hideTag;
	}

	public boolean isHideProviderRole() {
		return this.hideProviderRole;
	}

	public void setHideProviderRole(boolean hideProviderRole) {
		this.hideProviderRole = hideProviderRole;
	}

	public boolean isHideProvider() {
		return this.hideProvider;
	}

	public void setHideProvider(boolean hideProvider) {
		this.hideProvider = hideProvider;
	}

	public boolean isHideSubscriberRole() {
		return this.hideSubscriberRole;
	}

	public void setHideSubscriberRole(boolean hideSubscriberRole) {
		this.hideSubscriberRole = hideSubscriberRole;
	}

	public boolean isHideSubscriber() {
		return this.hideSubscriber;
	}

	public void setHideSubscriber(boolean hideSubscriber) {
		this.hideSubscriber = hideSubscriber;
	}

	public boolean isHideProtocol() {
		return this.hideProtocol;
	}

	public void setHideProtocol(boolean hideProtocol) {
		this.hideProtocol = hideProtocol;
	}

	public boolean isHideGatewayRole() {
		return this.hideGatewayRole;
	}

	public void setHideGatewayRole(boolean hideGatewayRole) {
		this.hideGatewayRole = hideGatewayRole;
	}
	
}
