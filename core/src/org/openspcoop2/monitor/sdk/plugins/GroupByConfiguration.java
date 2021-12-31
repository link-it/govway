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
package org.openspcoop2.monitor.sdk.plugins;

/**
 * GroupByConfiguration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GroupByConfiguration {

	private boolean hideGatewayRole = false;
	
	private boolean hideProtocol = false;
	
	private boolean hideProvider = false;
	
	private boolean hideSubscriber = false;
	
	private boolean hideService = false;
	private boolean hideAction = false;
	
	private boolean hideApplication = false;
	
	private boolean hideSenderIdentity = false;
	
	private boolean hideToken = false;
	private boolean hideTokenClaims = false;
	
	public boolean isHideToken() {
		return this.hideToken;
	}

	public void setHideToken(boolean hideToken) {
		this.hideToken = hideToken;
	}

	public boolean isHideTokenClaims() {
		return this.hideTokenClaims;
	}

	public void setHideTokenClaims(boolean hideTokenClaims) {
		this.hideTokenClaims = hideTokenClaims;
	}

	public boolean isHideSenderIdentity() {
		return this.hideSenderIdentity;
	}

	public void setHideSenderIdentity(boolean hideSenderIdentity) {
		this.hideSenderIdentity = hideSenderIdentity;
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

	public boolean isHideProvider() {
		return this.hideProvider;
	}

	public void setHideProvider(boolean hideProvider) {
		this.hideProvider = hideProvider;
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
