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



package org.openspcoop2.pdd.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.behaviour.Behaviour;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardToFilter;
import org.openspcoop2.pdd.core.behaviour.BehaviourLoader;
import org.openspcoop2.pdd.core.behaviour.IBehaviour;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;



/**
 * Classe utilizzata per raccogliere le informazioni su servizi applicativi e 
 * soggetti reali associati ad un unico soggetto virtuale
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SoggettoVirtuale implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<SoggettoVirtualeServizioApplicativo> soggettoVirtuale_serviziApplicativi = new ArrayList<SoggettoVirtualeServizioApplicativo>();
	private int count = 0;

	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public SoggettoVirtuale(){
	}


	public synchronized void addServizioApplicativo(SoggettoVirtualeServizioApplicativo sa){
		this.count++;
		sa.setId("SoggettoVirtuale-SA-"+this.count);
		this.soggettoVirtuale_serviziApplicativi.add(sa);
	}
	
	
	public List<IDSoggetto> getSoggettiRealiSenzaDuplicati() {
		List<IDSoggetto> list = new ArrayList<IDSoggetto>();
		for (SoggettoVirtualeServizioApplicativo sa : this.soggettoVirtuale_serviziApplicativi) {
			boolean find = false;
			for (IDSoggetto giaTrovato : list) {
				if( (sa.getIdSoggettoReale().getTipo().equals(giaTrovato.getTipo())) &&
						(sa.getIdSoggettoReale().getNome().equals(giaTrovato.getNome())) ){
					find = true;
					break;
				}
			}
			if(find == false){
				list.add(sa.getIdSoggettoReale());
			}
		}
		return list;
	}
	
	public List<String> getIdServiziApplicativi(boolean gestisciBehaviuorPerFiltri,GestoreMessaggi gestoreMessaggi,Busta busta, RequestInfo requestInfo,
			PdDContext pddContext, IProtocolFactory<?> protocolFactory, IState state) throws Exception{
		List<String> list = new ArrayList<>();
		for (SoggettoVirtualeServizioApplicativo sa : this.soggettoVirtuale_serviziApplicativi) {

			boolean filtrato = false;
			
			if(gestisciBehaviuorPerFiltri){
				
				if(sa.getPortaApplicativa().getBehaviour()!=null && sa.getPortaApplicativa().getBehaviour().getNome()!=null){
					
					IBehaviour behaviourImpl = BehaviourLoader.newInstance(sa.getPortaApplicativa().getBehaviour(), null,
							pddContext, protocolFactory, state);
					
					Busta bustaConSoggettiReali = busta.newInstance();
					// Inverto mitt-dest
					// il mittente e' il SoggettoVirtuale
					bustaConSoggettiReali.setMittente(busta.getDestinatario()); 
					bustaConSoggettiReali.setTipoMittente(busta.getTipoDestinatario()); 
					// sogg. destinatario reale	
					bustaConSoggettiReali.setDestinatario(sa.getIdSoggettoReale().getNome());
					bustaConSoggettiReali.setTipoDestinatario(sa.getIdSoggettoReale().getTipo());
					
					Behaviour behaviour = behaviourImpl.behaviour(gestoreMessaggi, bustaConSoggettiReali, 
							sa.getPortaApplicativa(), requestInfo);
					if(behaviour!=null && behaviour.getForwardTo()!=null &&
							behaviour.getForwardTo().size()==1){
						BehaviourForwardToFilter filter = behaviour.getForwardTo().get(0).getFilter();
						
						if(filter!=null){
							// Provo a vedere se il sa è filtrato
							List<IDServizioApplicativo> saAttuale = new ArrayList<IDServizioApplicativo>();
							IDServizioApplicativo idSA = new IDServizioApplicativo();
							idSA.setIdSoggettoProprietario(sa.getIdSoggettoReale());
							idSA.setNome(sa.getNomeServizioApplicativo());
							saAttuale.add(idSA);
							List<IDServizioApplicativo> saFiltrato = filter.aggiornaDestinatariAbilitati(saAttuale);
							if(saFiltrato.size()<=0){
								filtrato=true;
							}
						}
					}
				}
						
			}
			
			if(filtrato==false){
				list.add(sa.getId());
			}
		}
		return list;
	}
	
	public String getNomeServizioApplicativo(String idServizioApplicativo){
		for (SoggettoVirtualeServizioApplicativo sa : this.soggettoVirtuale_serviziApplicativi) {
			if(sa.getId().equals(idServizioApplicativo)){
				return sa.getNomeServizioApplicativo();
			}
		}
		return null;
	}
	
	public IDSoggetto getSoggettoReale(String idServizioApplicativo){
		for (SoggettoVirtualeServizioApplicativo sa : this.soggettoVirtuale_serviziApplicativi) {
			if(sa.getId().equals(idServizioApplicativo)){
				return sa.getIdSoggettoReale();
			}
		}
		return null;
	}
	
	public IDPortaApplicativa getIDPortaApplicativa(String idServizioApplicativo){
		for (SoggettoVirtualeServizioApplicativo sa : this.soggettoVirtuale_serviziApplicativi) {
			if(sa.getId().equals(idServizioApplicativo)){
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(sa.getPortaApplicativa().getNome());
				return idPA;
			}
		}
		return null;
	}
	

}
