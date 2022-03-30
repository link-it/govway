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
package org.openspcoop2.web.monitor.transazioni.mbean;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;


/****
 * 
 * Bean per che incapsula un oggetto di tipo Trasmissione, per la visualizzazione nella pagina web.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TrasmissioneBean extends Trasmissione {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient IProtocolFactory<?> protocolFactory;
	private transient ITraduttore traduttore;

	public TrasmissioneBean(Trasmissione trasmissione, IProtocolFactory<?> protocolFactory) {

		this.protocolFactory = protocolFactory;

		try {
			this.traduttore = this.protocolFactory.createTraduttore();
		} catch (ProtocolException e) {
		}

		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		metodiEsclusi.add(new BlackListElement("setProtocolFactory",
				IProtocolFactory.class));
		metodiEsclusi.add(new BlackListElement("setTempoValue", String.class));

		BeanUtils.copy(this, trasmissione, metodiEsclusi);
		
		this.setTempo(trasmissione.getTempo());
		try {
			if(this.getTempo() != null)
				this.setTempoValue(trasmissione.getTempoValue(protocolFactory));
		} catch (ProtocolException e) {}
	}
	
	public String getTempoRicavato() {
		if(this.getTempoValue()!=null){
			return this.getTempoValue();
		} else if(this.getTempo()!=null){
			return this.getTempo().getEngineValue();
		} else 
			return null;
	}



	public String getTempoValue() {
		try{
			return super.getTempoValue(this.protocolFactory) == null ? this.traduttore.toString(this
					.getTempo()) : super.getTempoValue(this.protocolFactory);
		}catch(Exception e){
			if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null){
				this.protocolFactory.getLogger().error(e.getMessage(),e);
			}else{
				e.printStackTrace(System.err);
			}
			return null;
		}
	}
}
