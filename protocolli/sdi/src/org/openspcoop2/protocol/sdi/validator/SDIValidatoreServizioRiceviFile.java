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
package org.openspcoop2.protocol.sdi.validator;

import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.constants.ErroreInvioType;
import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.utils.ProjectInfo;

import java.util.Vector;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.protocol.sdi.constants.SDICostanti;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviFile;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;

/**
 * SDIValidatoreServizioRiceviFile
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIValidatoreServizioRiceviFile {

	private SDIValidazioneSintattica sdiValidazioneSintattica;
	@SuppressWarnings("unused")
	private SDIValidazioneSemantica sdiValidazioneSemantica;
	@SuppressWarnings("unused")
	private OpenSPCoop2Message msg;
	private boolean isRichiesta;
	private SOAPElement sdiMessage;
	private String namespace;
	private Busta busta;
	
	public SDIValidatoreServizioRiceviFile(SDIValidazioneSintattica sdiValidazioneSintattica,
			OpenSPCoop2Message msg,boolean isRichiesta,
			SOAPElement sdiMessage,Busta busta){
		this.sdiValidazioneSintattica = sdiValidazioneSintattica;
		this.msg = msg;
		this.isRichiesta = isRichiesta;
		this.sdiMessage = sdiMessage;
		this.namespace = ProjectInfo.getInstance().getProjectNamespace();
		this.busta = busta;
	}
	public SDIValidatoreServizioRiceviFile(SDIValidazioneSemantica sdiValidazioneSemantica,
			OpenSPCoop2Message msg,boolean isRichiesta,
			SOAPElement sdiMessage,Busta busta){
		this.sdiValidazioneSemantica = sdiValidazioneSemantica;
		this.msg = msg;
		this.isRichiesta = isRichiesta;
		this.sdiMessage = sdiMessage;
		this.namespace = ProjectInfo.getInstance().getProjectNamespace();
		this.busta = busta;
	}
	
	private boolean checkServiceNamespace() throws Exception{
		if(this.namespace.equals(this.sdiMessage.getNamespaceURI())==false){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"Namespace ["+this.sdiMessage.getNamespaceURI()+"] differente da quello atteso ["+this.namespace+"]"));
			return false;	
		}
		return true;
	}
	
	
	
	
	/* ***** validaRiceviFile ***** */
	
	public void validaRiceviFile() throws Exception{
		
		if(checkServiceNamespace()==false){
			return;
		}
		
		if(this.sdiValidazioneSintattica!=null){
			if(this.isRichiesta){
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
			else{
				this._validazioneSintattica_RiceviFile_risposta();
			}
		}
		else{
			if(this.isRichiesta){
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
			else{
				// non vi sono contenuti xml da validare
			}
		}
	}
	
	private void _validazioneSintattica_RiceviFile_risposta() throws Exception{
		
		if(SDICostantiServizioRiceviFile.RICEVI_FILE_RISPOSTA_ROOT_ELEMENT.equals(this.sdiMessage.getLocalName())==false){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"LocalName ["+this.sdiMessage.getLocalName()+"] differente da quello atteso ["+SDICostantiServizioRiceviFile.RICEVI_FILE_RISPOSTA_ROOT_ELEMENT+"]"));
			return;	
		}
		
		Vector<SOAPElement> elementChilds = SoapUtils.getNotEmptyChildSOAPElement(this.sdiMessage);
		if(elementChilds==null || elementChilds.size()<=0){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"RootElement [{"+this.namespace+"}"+this.sdiMessage.getLocalName()+"] non contiene elementi"));
			return;	
		}
		
		String identificativoSdI = null;
		String data = null;
		String errore = null;
		for (int i = 0; i < elementChilds.size(); i++) {
			SOAPElement child = elementChilds.get(i);
	
			if(SDICostantiServizioRiceviFile.RICEVI_FILE_RISPOSTA_ELEMENT_IDENTIFICATIVO_SDI.equals(child.getLocalName())){
				if(identificativoSdI!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_PRESENTE_PIU_VOLTE));
					return;	
				}
				if(child.getTextContent()==null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALORIZZATO));
					return;	
				}
				identificativoSdI = child.getTextContent();
			}
			else if(SDICostantiServizioRiceviFile.RICEVI_FILE_RISPOSTA_ELEMENT_DATA_ORA_RICEZIONE.equals(child.getLocalName())){
				if(data!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviFile.RICEVI_FILE_RISPOSTA_ELEMENT_DATA_ORA_RICEZIONE+"] presente piu' volte"));
					return;	
				}
				if(child.getTextContent()==null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviFile.RICEVI_FILE_RISPOSTA_ELEMENT_DATA_ORA_RICEZIONE+"] non valorizzato"));
					return;	
				}
				data = child.getTextContent();
			}
			else if(SDICostantiServizioRiceviFile.RICEVI_FILE_RISPOSTA_ELEMENT_ERRORE.equals(child.getLocalName())){
				if(errore!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviFile.RICEVI_FILE_RISPOSTA_ELEMENT_ERRORE+"] presente piu' volte"));
					return;	
				}
				if(child.getTextContent()==null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviFile.RICEVI_FILE_RISPOSTA_ELEMENT_ERRORE+"] non valorizzato"));
					return;	
				}
				errore = child.getTextContent();
			}
			else{
				this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
						"Element ["+child.getLocalName()+"] sconosciuto"));
				return;	
			}
			
			if(child.getNamespaceURI()!=null){
				this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
						"Element ["+child.getLocalName()+"] appartiene al namespace ("+child.getNamespaceURI()+"). Era atteso un elemento senza namespace"));
				return;	
			}
		}
		
		// identificativoSDI
		if(identificativoSdI==null){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE));
			return;	
		}
		
		// data
		if(data==null){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviFile.RICEVI_FILE_RISPOSTA_ELEMENT_DATA_ORA_RICEZIONE+"] non presente"));
			return;	
		}
		
		// errore
		if(errore!=null){
			try{
				if(!ErroreInvioType.EI01.name().equals(errore) && 
						!ErroreInvioType.EI02.name().equals(errore) && 
						!ErroreInvioType.EI03.name().equals(errore) ){
					throw new Exception("Valore ["+errore+"] differente dai valori attesi: ["+ErroreInvioType.EI01.name()+","+ErroreInvioType.EI02.name()+","+ErroreInvioType.EI03.name()+"]");
				}
			}catch(Exception e){
				this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioRiceviFile.RICEVI_FILE_RISPOSTA_ELEMENT_ERRORE+"] non valido: "+e.getMessage(),e));
				return;	
			}
			
			String motivo = null;
			if(ErroreInvioType.EI01.name().equals(errore)){
				motivo = "EI01 - FILE VUOTO";
			}
			else if(ErroreInvioType.EI02.name().equals(errore)){
				motivo = "EI02 - SERVIZIO NON DISPONIBILE";
			}
			else if(ErroreInvioType.EI03.name().equals(errore)){
				motivo = "EI03 - UTENTE NON ABILITATO";
			}
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviFile.RICEVI_FILE_RISPOSTA_ELEMENT_ERRORE+"] indica uno stato del SdI in errore: "+motivo));
		}
				
		// setto i valori sdi nella busta
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI, identificativoSdI);
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DATA_ORA_RICEZIONE, data);
		if(errore!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_ERRORE, errore);
		}
		
		
	}

}
