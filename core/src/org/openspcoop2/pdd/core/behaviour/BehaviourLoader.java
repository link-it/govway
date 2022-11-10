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
package org.openspcoop2.pdd.core.behaviour;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.PortaApplicativaBehaviour;
import org.openspcoop2.core.config.constants.TipoBehaviour;
import org.openspcoop2.pdd.config.dynamic.PddPluginLoader;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.LoadBalancerBehaviour;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverBehaviour;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * Behaviour
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BehaviourLoader {

	public static IBehaviour newInstance(PortaApplicativaBehaviour behaviour, MsgDiagnostico msgDiag,
			PdDContext pddContext, IProtocolFactory<?> protocolFactory, IState state) throws CoreException{
		
		if(behaviour==null || behaviour.getNome()==null || "".equals(behaviour.getNome())) {
			throw new CoreException("Behaviour undefined");
		}
		
		IBehaviour behaviourImpl = null;
		String tipoDiagBehaviour = null;
		try {
			TipoBehaviour bt = TipoBehaviour.toEnumConstant(behaviour.getNome(), false);
			switch (bt) {
			case CONSEGNA_MULTIPLA:
			case CONSEGNA_CONDIZIONALE:
			case CONSEGNA_CON_NOTIFICHE:
				
				behaviourImpl = new MultiDeliverBehaviour(bt, state);
				
				tipoDiagBehaviour = bt.getLabel();
				
				break;
				
			case CONSEGNA_LOAD_BALANCE:
				
				behaviourImpl = new LoadBalancerBehaviour(state);
				
				tipoDiagBehaviour = bt.getLabel();
				
				break;
				
			case CUSTOM:
				
				behaviourImpl = (IBehaviour) PddPluginLoader.getInstance().newBehaviour(behaviour.getNome());
				
				tipoDiagBehaviour = behaviour.getNome();
				
				break;
	
			}
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
		
		if(behaviourImpl==null) {
			throw new CoreException("Init Behaviour '"+behaviour.getNome()+"' failed");
		}
		
		if(msgDiag!=null) {
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_BEHAVIOUR, tipoDiagBehaviour);
		}
		
		behaviourImpl.init(pddContext, protocolFactory);
		
		if(behaviourImpl instanceof AbstractBehaviour) {
			((AbstractBehaviour)behaviourImpl).initMsgDiagnostico(msgDiag);
		}
		
		return behaviourImpl;
		
	} 
	
}
