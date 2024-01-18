/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.spcoop.diagnostica;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.openspcoop2.protocol.basic.diagnostica.DiagnosticSerializer;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.spcoop.utils.SPCoopUtils;
import org.openspcoop2.utils.date.DateManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticSerializer} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopDiagnosticSerializer extends DiagnosticSerializer implements org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticSerializer {

	public SPCoopDiagnosticSerializer(IProtocolFactory<?> protocolFactory) throws ProtocolException{
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
			this.log.error("DiagnosticSerializer.toElement error: "+e.getMessage(),e);
			throw new ProtocolException("DiagnosticSerializer.toElement error: "+e.getMessage(),e);
		}
	}
	
	@Override
	protected ByteArrayOutputStream toByteArrayOutputStream(MsgDiagnostico msgDiag, TipoSerializzazione tipoSerializzazione) throws ProtocolException {
		
		try{
					
			switch (tipoSerializzazione) {
				case XML:
				case DEFAULT:
					
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					bout.write(org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(this.toElement(msgDiag)));
					bout.flush();
					bout.close();
					return bout;
	
				default:
					
					throw new Exception("Tipo Serializzazione ["+tipoSerializzazione+"] Non gestito");
			}
			
		} catch(Exception e) {
			this.log.error("DiagnosticSerializer.toString error: "+e.getMessage(),e);
			throw new ProtocolException("DiagnosticSerializer.toString error: "+e.getMessage(),e);
		}

	}
	
}