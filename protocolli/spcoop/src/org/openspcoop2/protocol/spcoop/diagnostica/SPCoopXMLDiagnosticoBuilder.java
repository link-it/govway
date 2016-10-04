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

package org.openspcoop2.protocol.spcoop.diagnostica;

import java.util.Date;

import org.openspcoop2.protocol.basic.diagnostica.XMLDiagnosticoBuilder;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.spcoop.utils.SPCoopUtils;
import org.openspcoop2.utils.date.DateManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.diagnostica.IXMLDiagnosticoBuilder} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopXMLDiagnosticoBuilder extends XMLDiagnosticoBuilder implements org.openspcoop2.protocol.sdk.diagnostica.IXMLDiagnosticoBuilder {

	public SPCoopXMLDiagnosticoBuilder(IProtocolFactory protocolFactory){
		super(protocolFactory);
	}

	/* --------------------- MESSAGGI DIAGNOSTICI -----------------------*/

	@Override
	public Element toElement(MsgDiagnostico msgDiag) throws ProtocolException{
		try{
			
			Document doc = this.xmlUtils.newDocument();
			Element diagnostico = doc.createElementNS("http://www.ctrupa.it/schemas/2003/eGovIT/Diag1_0/", "eGov_IT_Diag:MessaggioDiagnostico");
						
			Element gdoMsgDiag = doc.createElementNS("http://www.ctrupa.it/schemas/2003/eGovIT/Diag1_0/", "eGov_IT_Diag:OraRegistrazione");
			Date gdoD=null;
			if(msgDiag.getGdo()==null){
				gdoD = new Date(DateManager.getTimeMillis());
			}else{
				gdoD = new Date(msgDiag.getGdo().getTime());
			}
			gdoMsgDiag.setTextContent(SPCoopUtils.getDate_eGovFormat(gdoD));
			diagnostico.appendChild(gdoMsgDiag);

			Element identificativoPorta = doc.createElementNS("http://www.ctrupa.it/schemas/2003/eGovIT/Diag1_0/", "eGov_IT_Diag:IdentificativoPorta");
			identificativoPorta.setTextContent(msgDiag.getIdSoggetto().getCodicePorta());
			diagnostico.appendChild(identificativoPorta);

			Element identificativoFunzione = doc.createElementNS("http://www.ctrupa.it/schemas/2003/eGovIT/Diag1_0/", "eGov_IT_Diag:IdentificativoFunzione");
			identificativoFunzione.setTextContent(msgDiag.getIdFunzione());
			diagnostico.appendChild(identificativoFunzione);

			Element livelloSev = doc.createElementNS("http://www.ctrupa.it/schemas/2003/eGovIT/Diag1_0/", "eGov_IT_Diag:LivelloDiSeverita");
			String liv = "" + msgDiag.getSeverita();
			livelloSev.setTextContent(liv);
			diagnostico.appendChild(livelloSev);

			Element testo = doc.createElementNS("http://www.ctrupa.it/schemas/2003/eGovIT/Diag1_0/", "eGov_IT_Diag:TestoDiagnostico");
			testo.setTextContent(msgDiag.getMessaggio());
			diagnostico.appendChild(testo);

			return  diagnostico;

		} catch(Exception e) {
			this.log.error("XMLDiagnosticoBuilder error: "+e.getMessage(),e);
			throw new ProtocolException("XMLDiagnosticoBuilder error: "+e.getMessage(),e);
		}
	}
	
}