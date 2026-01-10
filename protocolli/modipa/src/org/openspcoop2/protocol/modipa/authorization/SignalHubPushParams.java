/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.protocol.modipa.authorization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.constants.ModISignalHubOperation;
import org.openspcoop2.utils.MapKey;

/**
 * SignalHubPushParams
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SignalHubPushParams implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final MapKey<String> MODIPA_KEY_INFO_SIGNAL_HUB_PUSH_PARAMS = ModICostanti.MODIPA_KEY_INFO_SIGNAL_HUB_PUSH_PARAMS;
	
	private String eserviceId;
	private String objectId;
	private String objectType;
	private ModISignalHubOperation signalType;
	private IDServizio idServizio;
	
	private static class RequiredKeys implements Serializable {
		private static final long serialVersionUID = 1L;
		String serviceName;
		String roleName;
	}
	
	private final List<RequiredKeys> requiredAuthorizations = new ArrayList<>();
	
	public void save(PdDContext ctx) {
		ctx.addObject(MODIPA_KEY_INFO_SIGNAL_HUB_PUSH_PARAMS, this);
	}
	
	public static SignalHubPushParams load(org.openspcoop2.utils.Map<Object> ctx) {
		return (SignalHubPushParams) ctx.getObject(MODIPA_KEY_INFO_SIGNAL_HUB_PUSH_PARAMS);
	}

	public void addRequiredAuthorization(String saName, String roleName) {
		RequiredKeys keys = new RequiredKeys();
		keys.roleName = roleName;
		keys.serviceName = saName;
		this.requiredAuthorizations.add(keys);
	}
	
	public int getRequiredAuthorizationsSize() {
		return this.requiredAuthorizations.size();
	}
	
	public String getRequiredAuthorizationRole(int index) {
		RequiredKeys keys = this.requiredAuthorizations.get(index);
		return keys == null ? null : keys.roleName;
	}
	
	public String getRequiredAuthorizationSA(int index) {
		RequiredKeys keys = this.requiredAuthorizations.get(index);
		return keys == null ? null : keys.serviceName;
	}
	
	public String getEServiceId() {
		return this.eserviceId;
	}

	public void setEServiceId(String eserviceId) {
		this.eserviceId = eserviceId;
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectType() {
		return this.objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public ModISignalHubOperation getSignalType() {
		return this.signalType;
	}

	public void setSignalType(ModISignalHubOperation signalType) {
		this.signalType = signalType;
	}

	public IDServizio getIdServizio() {
		return this.idServizio;
	}

	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}

}
