/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.protocol.sdi.validator;

import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;

/**
 * SDIValidazioneUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIValidazioneUtils {

	private IProtocolFactory protocolFactory;
	
	public SDIValidazioneUtils(IProtocolFactory protocolFactory){
		this.protocolFactory = protocolFactory;
	}
	
	public Eccezione newEccezioneValidazione(CodiceErroreCooperazione codiceErrore) throws ProtocolException{
		return Eccezione.getEccezioneValidazione(codiceErrore,null, this.protocolFactory);
	}
	public Eccezione newEccezioneValidazione(CodiceErroreCooperazione codiceErrore,String descrizioneErrore) throws ProtocolException{
		return Eccezione.getEccezioneValidazione(codiceErrore, descrizioneErrore, this.protocolFactory);
	}
	public Eccezione newEccezioneValidazione(CodiceErroreCooperazione codiceErrore,String descrizioneErrore, Throwable e) throws ProtocolException{
		if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null)
			this.protocolFactory.getLogger().error(descrizioneErrore,e);
		return Eccezione.getEccezioneValidazione(codiceErrore, descrizioneErrore, this.protocolFactory);
	}
	
	public Eccezione newEccezioneValidazione(CodiceErroreCooperazione codiceErrore,boolean info) throws ProtocolException{
		Eccezione ecc = Eccezione.getEccezioneValidazione(codiceErrore,null, this.protocolFactory);
		if(info){
			ecc.setRilevanza(LivelloRilevanza.INFO);
		}
		return ecc;
	}
	public Eccezione newEccezioneValidazione(CodiceErroreCooperazione codiceErrore,String descrizioneErrore,boolean info) throws ProtocolException{
		Eccezione ecc =  Eccezione.getEccezioneValidazione(codiceErrore, descrizioneErrore, this.protocolFactory);
		if(info){
			ecc.setRilevanza(LivelloRilevanza.INFO);
		}
		return ecc;
	}
	public Eccezione newEccezioneValidazione(CodiceErroreCooperazione codiceErrore,String descrizioneErrore, Throwable e,boolean info) throws ProtocolException{
		if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null)
			this.protocolFactory.getLogger().error(descrizioneErrore,e);
		Eccezione ecc =  Eccezione.getEccezioneValidazione(codiceErrore, descrizioneErrore, this.protocolFactory);
		if(info){
			ecc.setRilevanza(LivelloRilevanza.INFO);
		}
		return ecc;
	}
	
	public Eccezione newEccezioneProcessamento(CodiceErroreCooperazione codiceErrore) throws ProtocolException{
		return Eccezione.getEccezioneProcessamento(codiceErrore,null, this.protocolFactory);
	}
	public Eccezione newEccezioneProcessamento(CodiceErroreCooperazione codiceErrore,String descrizioneErrore) throws ProtocolException{
		return Eccezione.getEccezioneProcessamento(codiceErrore, descrizioneErrore, this.protocolFactory);
	}
	public Eccezione newEccezioneProcessamento(CodiceErroreCooperazione codiceErrore,String descrizioneErrore, Throwable e) throws ProtocolException{
		if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null)
			this.protocolFactory.getLogger().error(descrizioneErrore,e);
		return Eccezione.getEccezioneProcessamento(codiceErrore, descrizioneErrore, this.protocolFactory);
	}
	
}
