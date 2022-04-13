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
package org.openspcoop2.pdd.core.behaviour.built_in.load_balance;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaBehaviour;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.behaviour.BehaviourEmitDiagnosticException;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.behaviour.BehaviourPropertiesUtils;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.slf4j.Logger;

/**
 * ConfigurazioneLoadBalancer
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneLoadBalancer  {

	public static void addLoadBalancerType(PortaApplicativaBehaviour paBehaviour, String type) {
		BehaviourPropertiesUtils.addProprieta(paBehaviour, Costanti.LOAD_BALANCER_TYPE, type);
	}
	public static String readLoadBalancerType(PortaApplicativaBehaviour paBehaviour) {
		if(paBehaviour!=null && paBehaviour.sizeProprietaList()>0) {
			for (Proprieta p : paBehaviour.getProprietaList()) {
				if(Costanti.LOAD_BALANCER_TYPE.equals(p.getNome())) {
					return p.getValore();
				}
			}
		}
		return null;
	}	
	
	
	public static void addLoadBalancerWeight(PortaApplicativaServizioApplicativo paSA, String weight) {
		BehaviourPropertiesUtils.addProprieta(paSA.getDatiConnettore(), Costanti.LOAD_BALANCER_WEIGHT, weight);
	}
	public static String readLoadBalancerWeight(PortaApplicativaServizioApplicativo paSA) {
		if(paSA!=null && paSA.getDatiConnettore()!=null && paSA.getDatiConnettore().sizeProprietaList()>0) {
			for (Proprieta p : paSA.getDatiConnettore().getProprietaList()) {
				if(Costanti.LOAD_BALANCER_WEIGHT.equals(p.getNome())) {
					return p.getValore();
				}
			}
		}
		return LoadBalancerPool.DEFAULT_WEIGHT+"";
	}	
	
	
	
	public static ConfigurazioneLoadBalancer read(PortaApplicativa pa, OpenSPCoop2Message message, Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext, 
			MsgDiagnostico msgDiag, Logger log, IState state) throws BehaviourException, BehaviourEmitDiagnosticException {
		ConfigurazioneLoadBalancer config = new ConfigurazioneLoadBalancer();
		if(pa.getBehaviour()==null || pa.getBehaviour().sizeProprietaList()<=0) {
			throw new BehaviourException("Load Balancer type undefined");
		}
		String type = null;
		for (Proprieta p : pa.getBehaviour().getProprietaList()) {
			if(Costanti.LOAD_BALANCER_TYPE.equals(p.getNome())) {
				type = p.getValore();
			}
		}
		if(type==null) {
			throw new BehaviourException("Load Balancer type undefined");
		}
		LoadBalancerType enumType = LoadBalancerType.toEnumConstant(type);
		if(enumType==null) {
			throw new BehaviourException("Load Balancer type '"+type+"' unknown");
		}
		config.setType(enumType);
		
		LoadBalancerInstance lbInstance = GestoreLoadBalancerCaching.getLoadBalancerInstance(pa, message, busta, 
				requestInfo, pddContext, 
				msgDiag, log,
				enumType, state);
		
		config.setPool(lbInstance.getLoadBalancerPool());

		config.setConnectorSelected(lbInstance.getConnectorSelected());
				
		return config;
	}

	private LoadBalancerType type;
	private LoadBalancerPool pool;
	private String connectorSelected;
	
	public String getConnectorSelected() {
		return this.connectorSelected;
	}
	public void setConnectorSelected(String connectorSelected) {
		this.connectorSelected = connectorSelected;
	}
	public LoadBalancerPool getPool() {
		return this.pool;
	}
	public void setPool(LoadBalancerPool pool) {
		this.pool = pool;
	}
	public LoadBalancerType getType() {
		return this.type;
	}
	public void setType(LoadBalancerType type) {
		this.type = type;
	}

}
