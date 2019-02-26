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
package org.openspcoop2.web.monitor.transazioni.mbean;

import org.openspcoop2.web.monitor.core.core.Utils;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.BlackListElement;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.config.ITraduttore;

/****
 * 
 * Bean per che incapsula un oggetto di tipo Riscontro, per la visualizzazione nella pagina web.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class RiscontroBean extends Riscontro {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient IProtocolFactory<?> protocolFactory;
	private transient ITraduttore traduttore;

	public RiscontroBean(Riscontro riscontro, IProtocolFactory<?> protocolFactory) {

		this.protocolFactory = protocolFactory;

		try {
			this.traduttore = this.protocolFactory.createTraduttore();
		} catch (ProtocolException e) {
		}

		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		metodiEsclusi.add(new BlackListElement("setProtocolFactory",
				IProtocolFactory.class));
		metodiEsclusi.add(new BlackListElement("setTipoOraRegistrazioneValue",
				String.class));

		BeanUtils.copy(this, riscontro, metodiEsclusi);
		try {
			this.setTipoOraRegistrazioneValue(riscontro.getTipoOraRegistrazioneValue(protocolFactory));
		} catch (ProtocolException e) {
		}

	}

	public String getTipoOraRegistrazioneValue() {
		try{
			return super.getTipoOraRegistrazioneValue(this.protocolFactory) == null ? this.traduttore
					.toString(this.getTipoOraRegistrazione())
					: super.getTipoOraRegistrazioneValue(this.protocolFactory);
		}catch(Exception e){
			if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null){
				this.protocolFactory.getLogger().error(e.getMessage(),e);
			}else{
				e.printStackTrace(System.err);
			}
			return null;
		}
	}
	
	public String getTipoOraRegistrazioneRicavato() {
		if(this.getTipoOraRegistrazioneValue()!=null){
			return this.getTipoOraRegistrazioneValue();
		} else if(this.getTipoOraRegistrazione()!=null){
			return this.getTipoOraRegistrazione().getEngineValue();
		} else 
			return null;
	}
	
	public String getPrettyRicevuta() {
		String toRet = null;
		
		if(this.getRicevuta()!=null)
			toRet = Utils.prettifyXml(this.getRicevuta());
		
		if(toRet == null)
			toRet = this.getRicevuta() != null ? this.getRicevuta() : "";
			
		return toRet;
	}



}
