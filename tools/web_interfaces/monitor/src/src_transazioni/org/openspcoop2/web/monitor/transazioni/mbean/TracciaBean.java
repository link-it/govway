/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.slf4j.Logger;


/*****
 * 
 * Bean per che incapsula un oggetto di tipo Traccia, per la visualizzazione nella pagina web.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TracciaBean extends Traccia {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private transient IProtocolFactory<?> protocolFactory;
	
	private BustaBean bustaBean;

	public TracciaBean(Traccia traccia, ServiceBinding tipoApi) {
		// creo la protocol factory
		try {
			this.protocolFactory = ProtocolFactoryManager.getInstance()
					.getProtocolFactoryByName(traccia.getProtocollo());
		} catch (ProtocolException e) {
			TracciaBean.log.error("Errore durante la creazione della Factory", e);
		}

		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		metodiEsclusi.add(new BlackListElement("setBusta", Busta.class));
		metodiEsclusi.add(new BlackListElement("setListaAllegati", List.class));
		metodiEsclusi.add(new BlackListElement("setProtocolFactory",
				IProtocolFactory.class));

		BeanUtils.copy(this, traccia, metodiEsclusi);

		this.setListaAllegati(traccia.getListaAllegati());
		// copia della busta
		this.bustaBean = new BustaBean(traccia.getBusta(), this.protocolFactory, tipoApi);

	}

	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}

	public void setProtocolFactory(IProtocolFactory<?> protocolFactory) {
		this.protocolFactory = protocolFactory;
	}
	
	public BustaBean getBustaBean(){
		return this.bustaBean;
	}
	
	public String getLocationLabel(){
		String in = "IN:";
		String out = "OUT:";
		if(this.getLocation() != null) {
			if(this.getLocation().startsWith(in)) {
				return "Indirizzo di provenienza della busta";
			} else if(this.getLocation().startsWith(out)) {
				return "Indirizzo a cui è stata spedita la busta";
			} else {
				return "Indirizzo provenienza/spedizione della busta";
			}
		}else {
			return "";
		}
	}

	public String getLocationValue(){
		String in = "IN:";
		String out = "OUT:";
		if(this.getLocation() != null) {
			if(this.getLocation().startsWith(in)) {
				return this.getLocation().substring(in.length());
			} else if(this.getLocation().startsWith(out)) {
				return this.getLocation().substring(out.length());
			} else {
				return this.getLocation();
			}
		}else {
			return "";
		}
	}
	
	
	
}
