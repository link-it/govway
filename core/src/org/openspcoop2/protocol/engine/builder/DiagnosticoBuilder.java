/*
 * OpenSPCoop - Customizable API Gateway 
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



package org.openspcoop2.protocol.engine.builder;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.diagnostica.IXMLDiagnosticoBuilder;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.w3c.dom.Element;

/**
 * Classe che contiene utility per generare xml relativo a :
 * <ul>
 * <li> Tracciamento
 * <li> Diagnostica
 * <li> Eccezioni
 * </ul>
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DiagnosticoBuilder  {


	/** Logger utilizzato per debug. */
	protected Logger log = null;
	private IProtocolFactory protocolFactory;
	private IXMLDiagnosticoBuilder diagnosticoProtocolBuilder;
	public DiagnosticoBuilder(IProtocolFactory protocolFactory) {
		this(Configurazione.getLibraryLog(), protocolFactory);
	}
	public DiagnosticoBuilder(Logger aLog,IProtocolFactory protocolFactory) {
		if(aLog!=null)
			this.log = aLog;
		else
			this.log = LoggerWrapperFactory.getLogger(DiagnosticoBuilder.class);
		this.protocolFactory = protocolFactory;
		try{
			this.diagnosticoProtocolBuilder = this.protocolFactory.createXMLDiagnosticoBuilder();
		}catch(Exception e){
			this.log.error("Errore durante la creazione dell'XMLDiagnosticoBuilder: "+e.getMessage(),e);
		}
	}

	public IProtocolFactory getProtocolFactory(){
		return this.protocolFactory;
	}
	

	/* --------------------- MESSAGGI DIAGNOSTICI -----------------------*/

	/**
	 * Costruisce un SOAPElement contenente un Messaggio Diagnostico come definito da specifica  
	 *
	 * @param msgDiag Contiene i dati del diagnostico
	 * @return SOAPElement contenente il messaggio Diagnostico in caso di successo, null altrimenti. 
	 * 
	 */

	public Element toElement(MsgDiagnostico msgDiag) throws ProtocolException{
		return this.diagnosticoProtocolBuilder.toElement(msgDiag);
	}
	


	/**
	 * Costruisce un array di byte contenente un Messaggio Diagnostico
	 *
	 * @param msgDiag Contiene i dati del diagnostico
	 * @return array di byte contenente il messaggio Diagnostico in caso di successo, null altrimenti. 
	 * 
	 */
	public byte[] toByteArray(MsgDiagnostico msgDiag) throws ProtocolException{
		return this.diagnosticoProtocolBuilder.toByteArray(msgDiag);
	}

	/**
	 * Costruisce una Stringa contenente un Messaggio Diagnostico
	 * 
	 * @param msgDiag Contiene i dati del diagnostico
	 * @return String contenente il messaggio Diagnostico in caso di successo, null altrimenti. 
	 * 
	 */
	public String toString(MsgDiagnostico msgDiag) throws ProtocolException{
		return this.diagnosticoProtocolBuilder.toString(msgDiag);
	}
}