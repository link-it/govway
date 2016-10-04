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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.SOAPFaultCode;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.w3c.dom.Element;


/**
 * Classe utilizzata per costruire una Busta, o parti di essa.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Imbustamento  {

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Logger utilizzato per debug. */
	private Logger log = null;
	private org.openspcoop2.protocol.sdk.IProtocolFactory protocolFactory;
	private org.openspcoop2.message.XMLUtils xmlUtils;
	private IProtocolManager protocolManager;
	private DettaglioEccezioneOpenSPCoop2Builder dettaglioEccezioneOpenSPCoop2Builder;
	
	public Imbustamento(org.openspcoop2.protocol.sdk.IProtocolFactory protocolFactory) throws ProtocolException{
		this(Configurazione.getLibraryLog(), protocolFactory);
	}
	
	public Imbustamento(Logger aLog, org.openspcoop2.protocol.sdk.IProtocolFactory protocolFactory) throws ProtocolException{
		if(aLog!=null)
			this.log = aLog;
		else
			this.log = LoggerWrapperFactory.getLogger(Imbustamento.class);
		this.protocolFactory = protocolFactory;
		
		this.xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
		
		this.protocolManager = this.protocolFactory.createProtocolManager();
		
		this.dettaglioEccezioneOpenSPCoop2Builder = new DettaglioEccezioneOpenSPCoop2Builder(aLog, protocolFactory);
	}

	public org.openspcoop2.protocol.sdk.IProtocolFactory getProtocolFactory(){
		return this.protocolFactory;
	}

	/** LunghezzaPrefisso */
	public static int prefixLenght = 0;

	/* ********  Metodi per la costruzione di parti della busta  ******** */ 

	/**
	 * Metodo che si occupa di costruire una stringa formata da un identificativo
	 * conforme alla specifica.
	 * L'identificativo e' formato da :
	 * codAmministrazione_codPortaDominio_num.progressivo_data_ora
	 * <p>
	 * Il codice Amministrazione e' preso da <var>destinatario</var>.
	 * Il codice della Porta di Dominio e' preso da <var>idPD</var>.
	 * Le altre informazioni sono costruite dal metodo, che si occupa
	 * di assemblarle in una unica stringa e di ritornarla.
	 *
	 * @param idSoggetto identificativo del Soggetto
	 * @param idTransazione id transazione
	 * @return un oggetto String contenente l'identificativo secondo specifica del protocollo.
	 * 
	 */
	//public String buildID(IDSoggetto idSoggetto, String idTransazione, Boolean isRichiesta) throws ProtocolException{
	//	return this.buildID(idSoggetto, idTransazione, Configurazione.getAttesaAttiva(), Configurazione.getCheckInterval(), isRichiesta);
	//}

	public String buildID(IState state, IDSoggetto idSoggetto, String idTransazione, long attesaAttiva,
			int checkInterval, Boolean isRichiesta) throws ProtocolException {
		try{
			return this.protocolFactory.createBustaBuilder().newID(state,idSoggetto, idTransazione, isRichiesta);	
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	



	/* ----------------  Metodi per la costruzione di una busta  -------------------- */

	/**
	 * Effettua l'imbustamento
	 *  
	 * @param msg Messaggio in cui deve essere aggiunto un header.
	 * @param busta Busta che contiene i dati necessari per la creazione dell'header
	 * 
	 */
	public void imbustamento(IState state, OpenSPCoop2Message msg,Busta busta,Integrazione integrazione,ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{	
		this.imbustamento(state, msg, busta,integrazione, false, false, false, proprietaManifestAttachments);
	}


	/**
	 * Effettua l'imbustamento
	 *  
	 * @param msg Messaggio in cui deve essere aggiunto un header.
	 * @param busta Busta che contiene i dati necessari per la creazione dell'header
	 * @param gestioneManifest Indicazione se deve essere gestito il manifest degli attachments
	 * @param isRichiesta Tipo di Busta
	 * 
	 */
	public SOAPElement imbustamento(IState state, OpenSPCoop2Message msg,Busta busta,Integrazione integrazione,
			boolean gestioneManifest,boolean isRichiesta,boolean scartaBody,
			ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{	
		if(proprietaManifestAttachments==null){
			proprietaManifestAttachments = new ProprietaManifestAttachments();
		}
		proprietaManifestAttachments.setGestioneManifest(gestioneManifest);
		proprietaManifestAttachments.setScartaBody(scartaBody);
		return this.protocolFactory.createBustaBuilder().imbustamento(state, msg, busta, isRichiesta, proprietaManifestAttachments);
	}


	/**
	 * Metodo che si occupa di costruire un elemento Trasmissione
	 *
	 * @param trasmissione Trasmissione
	 * 
	 */

	public SOAPElement addTrasmissione(OpenSPCoop2Message message,Trasmissione trasmissione,boolean readQualifiedAttribute) throws ProtocolException{
		return this.protocolFactory.createBustaBuilder().addTrasmissione(message, trasmissione);
	}










	/**----------------- Metodi per la costruzione di Buste Errore -------------*/
	/**
	 * Passatogli un messaggio (<var>busta</var>), ne costruisce un relativo 
	 * Messaggio Errore, invertendo mittente con destinatario e impostando altre proprieta' .
	 * Il messaggio errore costruito si riferisce ad un messaggio di errore causato
	 * dalla validazione della busta ricevuta, nella quale sono stati riscontrati errori.
	 *
	 * @param eccezioni Eccezioni
	 * @param busta Busta che ha causato l'errore da far diventare una busta Errore.
	 * @param id_busta identificativo da associare alla busta di ritorno.
	 * @return Una busta pronta ad essere usata per un messaggio Errore
	 * @throws ProtocolException 
	 * 
	 */
	public Busta buildMessaggioErroreProtocollo_Validazione(List<Eccezione> eccezioni,Busta busta,String id_busta,TipoOraRegistrazione tipoTempo) throws ProtocolException{
		return this.buildMessaggioErroreProtocollo(eccezioni,busta,id_busta,tipoTempo);
	}

	/**
	 * Passatogli un messaggio (<var>busta</var>), ne costruisce un relativo 
	 * Messaggio Errore, invertendo mittente con destinatario e impostando altre proprieta'.
	 * Il messaggio Errore costruito si riferisce ad un messaggio di errore causato
	 * dal processamento del messaggio da parte della porta di dominio.
	 *
	 * @param eccezioni Eccezioni sulla quale creare un  messaggio Errore.
	 * @param busta Busta che ha causato l'errore da far diventare una busta Errore.
	 * @param id_busta identificativo da associare alla busta di ritorno.
	 * @return Una busta pronta ad essere usata per un messaggio Errore
	 * @throws ProtocolException 
	 * 
	 */
	public Busta buildMessaggioErroreProtocollo_Processamento(List<Eccezione> eccezioni,Busta busta,String id_busta,TipoOraRegistrazione tipoTempo) throws ProtocolException{
		return this.buildMessaggioErroreProtocollo(eccezioni,busta,id_busta,tipoTempo);
	}

	/**
	 * Passatogli un messaggio (<var>busta</var>), ne costruisce un relativo 
	 * Messaggio Errore, invertendo mittente con destinatario e impostando altre proprieta'.
	 * Il messaggio Errore costruito si riferisce ad un messaggio di errore causato
	 * dal processamento del messaggio da parte della porta di dominio.
	 *
	 * @param ecc Eccezione sulla quale creare un  messaggio Errore.
	 * @param busta Busta che ha causato l'errore da far diventare una busta Errore.
	 * @param id_busta identificativo da associare alla busta di ritorno.
	 * @return Una busta pronta ad essere usata per un messaggio Errore
	 * @throws ProtocolException 
	 * 
	 */
	public Busta buildMessaggioErroreProtocollo_Processamento(Eccezione ecc,Busta busta,String id_busta,TipoOraRegistrazione tipoTempo) throws ProtocolException{

		Vector<Eccezione> eccs = new Vector<Eccezione>();
		eccs.add(ecc);
		return this.buildMessaggioErroreProtocollo(eccs,busta,id_busta,tipoTempo);
	}

	/**
	 * Passatogli un messaggio (<var>busta</var>), ne costruisce un relativo 
	 * Messaggio Errore, invertendo mittente con destinatario e impostando altre proprieta' 
	 *
	 * @param eccezioni Eccezione sulla quale creare un  messaggio Errore.
	 * @param busta Busta che ha causato l'errore da far diventare una busta Errore.
	 * @param id_busta identificativo da associare alla busta di ritorno.
	 * @return array di byte contenente il messaggio Errore
	 * @throws ProtocolException 
	 * 
	 */
	private Busta buildMessaggioErroreProtocollo(List<Eccezione> eccezioni,Busta busta,String id_busta,TipoOraRegistrazione tipoTempo) throws ProtocolException{	

		// Scambio mitt con dest
		String tipoDest = busta.getTipoMittente();
		String dest = busta.getMittente();
		String indTdest = busta.getIndirizzoMittente();
		String codicePorta = busta.getIdentificativoPortaMittente();
		busta.setTipoMittente(busta.getTipoDestinatario());
		busta.setMittente(busta.getDestinatario());
		busta.setIndirizzoMittente(busta.getIndirizzoDestinatario());
		busta.setIdentificativoPortaMittente(busta.getIdentificativoPortaDestinatario());
		busta.setTipoDestinatario(tipoDest);
		busta.setDestinatario(dest);
		busta.setIndirizzoDestinatario(indTdest);
		busta.setIdentificativoPortaDestinatario(codicePorta);

		// Costruzione identificativo e riferimentoMessaggio
		busta.setRiferimentoMessaggio(busta.getID());
		busta.setID(id_busta);

		// Ora registrazione
		busta.setOraRegistrazione(DateManager.getDate());
		busta.setTipoOraRegistrazione(tipoTempo,this.protocolFactory.createTraduttore().toString(tipoTempo));

		// Elimino trasmissione della richiesta
		while(busta.sizeListaTrasmissioni() != 0){
			busta.removeTrasmissione(0);
		}

		// Rimozione Eventuali vecchie eccezioni della richiesta
		while(busta.sizeListaEccezioni() != 0){
			busta.removeEccezione(0);
		}

		// Rimozione Eventuali vecchi riscontri della richiesta
		while(busta.sizeListaRiscontri() != 0){
			busta.removeRiscontro(0);
		}

		// Aggiungo eccezioni
		while(eccezioni.size()>0){
			Eccezione e = eccezioni.remove(0);
			busta.addEccezione(e);
		}

		// Validazione XSD: non devo produrre valori non validabili.
		if(this.protocolFactory.createProtocolManager().isGenerazioneElementiNonValidabiliRispettoXSD()==false){
			
			// Profilo di Collaborazione
			if(busta.getProfiloDiCollaborazione()!=null){
				if( !(
						ProfiloDiCollaborazione.ONEWAY.equals(busta.getProfiloDiCollaborazione()) || 
						ProfiloDiCollaborazione.SINCRONO.equals(busta.getProfiloDiCollaborazione()) || 
						ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(busta.getProfiloDiCollaborazione()) || 
						ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(busta.getProfiloDiCollaborazione())
					)
				){
					busta.setProfiloDiCollaborazione(null);
					busta.setTipoServizioCorrelato(null);
					busta.setServizioCorrelato(null);
				}
				else{
					if(busta.getTipoServizioCorrelato()!=null){
						if(this.protocolFactory.createProtocolConfiguration().getTipiServizi().contains(busta.getTipoServizioCorrelato()) == false){
							busta.setTipoServizioCorrelato(null);
						}
					}
				}
			}
			
			// Messaggio.riferimentoMessaggio
			if(busta.getRiferimentoMessaggio()!=null){
				ProprietaValidazione proprietaValidazione = new ProprietaValidazione();
				proprietaValidazione.setValidazioneIDCompleta(false);
				if(this.protocolFactory.createValidazioneSemantica().validazioneID(busta.getRiferimentoMessaggio(), null, proprietaValidazione)==false){
					busta.setRiferimentoMessaggio(null);
				}
			}
			
			// ProfiloTrasmissione
			if(busta.getInoltro()!=null){
				if( !Inoltro.CON_DUPLICATI.equals(busta.getInoltro()) && !Inoltro.SENZA_DUPLICATI.equals(busta.getInoltro())){
					busta.setInoltro(null,null);
				}
			}

		}
		
		return busta;
	}






	/**----------------- Fix soap prefix per OpenSPCoop v1 backward compatibility -------------*/
	/*
	 *OpenSPCoop2 ritorna il seguente soap-fault:

	<SOAP-ENV:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
	<SOAP-ENV:Body>
	<SOAP-ENV:Fault>
	<faultcode>SOAP-ENV:Client</faultcode>
	<faultstring>EGOV_IT_001 - Formato Busta non corretto</faultstring>
	</SOAP-ENV:Fault>
	</SOAP-ENV:Body>
	</SOAP-ENV:Envelope>

Il campo incriminato e' il fault code.
Nel caso di fault eGov deve appartenere al namespace soap 11. Infatti il fault code "Client" possiede il prefisso SOAP-ENV dichiarato nella Envelope.
La busta ritornata e' corretta.

OpenSPCoop 1.x "rifiuta" questo tipo di fault.
Di seguito il codice del controllo implementato nelle due versioni.
In pratica nella nuova versione viene controllato correttamente che il namespace del fault-code appartenga al namespace soap. 
Nella vecchia versione invece il controllo era cablato sul prefisso "soap" e quindi un diverso prefisso (come in questo caso) non supera la validazione. 
Il controllo implementato sul vecchio codice e' doppiamente errato, visto che il "prefisso giusto" potrebbe essere associato ad un namespace non corretto e 
la validazione sarebbe superata lo stesso.

OpenSPCoop 1.x
if(
                        !(
("Client".equals(codeS.getLocalName()) || "Server".equals(codeS.getLocalName()) ) &&
"soap".equalsIgnoreCase(codeS.getPrefix())
                        )
                )
                {
                    GENERA ERRORE BUSTA
                }


OpenSPCoop 2.x (codice plugin spcoop)
if(
                        !(
("Client".equals(codeS.getLocalName()) || "Server".equals(codeS.getLocalName()) ) &&
Costanti.SOAP_ENVELOPE_NAMESPACE.equalsIgnoreCase(codeS.getURI())
                        )
                )
                {
                    GENERA ERRORE BUSTA
                }


Un prefix 'soap' viene fatto generare ad OpenSPCoop2 comunque, nel caso SPCoop.
In questo modo il fault viene interpretato correttamente da OpenSPCoop-v1.
L'xml possiede una dichiarazione ulteriore del namespace soap.

<SOAP-ENV:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
<SOAP-ENV:Body>
<SOAP-ENV:Fault>
<faultcode xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">soap:Client</faultcode>
<faultstring>EGOV_IT_001 - Formato Busta non corretto</faultstring>
</SOAP-ENV:Fault>
</SOAP-ENV:Body>
</SOAP-ENV:Envelope>  
	 **/
	private String getFaultCodePrefix(SOAPVersion versioneSoap, 
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1){
		if(setSoapPrefixBackwardCompatibilityOpenSPCoop1){
			if(this.protocolFactory!=null &&
					this.protocolFactory.getProtocol()!=null &&
					this.protocolFactory.getProtocol().equals("spcoop")){
				if(SOAPVersion.SOAP11.equals(versioneSoap)){
					return "soap";
				}
			}	
		}
		
		return null; // viene generato nel namespace della busta SOAP
		
	}






	/**----------------- Metodi per la costruzione di MessaggiSoap da utilizzare con Buste Errore -------------*/
	/**
	 * Metodo che si occupa di ritornare un SOAPFault Errore di Processamento 
	 *
	 * @return un Message pronto per essere usato con un MessaggioErrore, 
	 *         in caso di costruzione con successo, null altrimenti.
	 * 
	 */
	public OpenSPCoop2Message buildSoapMsgErroreProtocollo_Validazione(SOAPVersion versioneSoap,boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1)  {
		return this.buildSoapMsgProtocolloErrore(true, null, versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);	
	}

	/**
	 * Metodo che si occupa di ritornare un SOAPFault Errore di Validazione 
	 *
	 * @param dettaglioEccezione Dettaglio dell'eccezione da generare
	 * @return un Message pronto per essere usato con un MessaggioErrore, 
	 *         in caso di costruzione con successo, null altrimenti.
	 * 
	 */
	public OpenSPCoop2Message buildSoapMsgErroreProtocollo_Processamento(DettaglioEccezione dettaglioEccezione, 
			SOAPVersion versioneSoap,boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1){
		return this.buildSoapMsgProtocolloErrore(false, dettaglioEccezione, versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);	
	}

	/**
	 * Metodo che si occupa di ritornare un SOAPFault di errore.
	 *
	 * @param erroreValidazione true se l'errore si e' verificato nella validazione della busta, false se si e' verificato nel processamento
	 * @param dettaglioEccezione Dettaglio eccezione
	 * @param versioneSoap Versione SOAP
	 * @return un Message pronto per essere usato con un MessaggioErrore, 
	 *         in caso di costruzione con successo, null altrimenti.
	 * 
	 */
	public OpenSPCoop2Message buildSoapMsgProtocolloErrore(boolean erroreValidazione,DettaglioEccezione dettaglioEccezione, 
			SOAPVersion versioneSoap,boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1)  {

		try{

			OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getMessageFactory();
			OpenSPCoop2Message msg = mf.createMessage(versioneSoap);
			SOAPEnvelope env = msg.getSOAPPart().getEnvelope();
			env.addNamespaceDeclaration("xsi","http://www.w3.org/2001/XMLSchema-instance");

			// Creo SoapFault
			SOAPBody bdy = env.getBody();
			SOAPFault fault = bdy.addFault();

			String soapFaultCodePrefix = this.getFaultCodePrefix(versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
			if(erroreValidazione){
				fault.setFaultString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE.toString(this.protocolFactory));
				fault.setFaultCode(SOAPFaultCode.Sender.toQName(versioneSoap,soapFaultCodePrefix)); //costanti.get_FAULT_CODE_VALIDAZIONE
			}else{
				fault.setFaultString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO.toString(this.protocolFactory));
				fault.setFaultCode(SOAPFaultCode.Receiver.toQName(versioneSoap,soapFaultCodePrefix)); 
			}
			
			if(!erroreValidazione && dettaglioEccezione!=null){
				Detail d = fault.addDetail();
				Element e = this.xmlUtils.newElement(org.openspcoop2.core.eccezione.details.utils.XMLUtils.generateDettaglioEccezione(dettaglioEccezione));
				d.appendChild(d.getOwnerDocument().importNode(e, true));
			}
			
			return msg;

		} catch(Exception e) {
			this.log.error("Build msgErrore non riuscito: " + e.getMessage());
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap,"ErroreProcessamento");
		}


	}

	
	
	
	
	
	
	
	
	
   
    
	public OpenSPCoop2Message buildSoapFaultProtocollo_processamento(IDSoggetto identitaPdD,TipoPdD tipoPdD,String modulo, 
			ErroreIntegrazione errore,Throwable eProcessamento, SOAPVersion versioneSoap, boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1){
		try{
			DettaglioEccezione dettaglioEccezione = null;
			if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneProcessamento()){
				dettaglioEccezione = this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezione(identitaPdD, tipoPdD, modulo, errore.getCodiceErrore(), 
						this.dettaglioEccezioneOpenSPCoop2Builder.transformFaultMsg(errore));
				this.dettaglioEccezioneOpenSPCoop2Builder.gestioneDettaglioEccezioneProcessamento(eProcessamento, dettaglioEccezione);
			}
			String soapFaultCodePrefix = this.getFaultCodePrefix(versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
			QName faultCode = SOAPFaultCode.Receiver.toQName(versioneSoap, soapFaultCodePrefix); 
			OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(versioneSoap, SoapUtils.build_Soap_Fault(versioneSoap, this.protocolFactory.createTraduttore().toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO),null,
					faultCode, 
					this.xmlUtils.newElement(org.openspcoop2.core.eccezione.details.utils.XMLUtils.generateDettaglioEccezione(dettaglioEccezione)), 
					this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneProcessamento()));
			return pr.getMessage_throwParseException();
		}catch(Exception e){
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap, "Errore buildSoapFaultProtocollo_processamento(exception): "+e.getMessage());
		}
	}
	
	public OpenSPCoop2Message buildSoapFaultProtocollo_processamento(IDSoggetto identitaPdD,TipoPdD tipoPdD,String modulo,
			ErroreIntegrazione errore, SOAPVersion versioneSoap, boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1){
		try{
			DettaglioEccezione dettaglioEccezione = null;
			if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneProcessamento()){
				dettaglioEccezione = this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezione(identitaPdD, tipoPdD, modulo, errore.getCodiceErrore(), 
						this.dettaglioEccezioneOpenSPCoop2Builder.transformFaultMsg(errore));
			}
			String soapFaultCodePrefix = this.getFaultCodePrefix(versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
			QName faultCode = SOAPFaultCode.Receiver.toQName(versioneSoap, soapFaultCodePrefix); 
			OpenSPCoop2MessageParseResult pr =  OpenSPCoop2MessageFactory.getMessageFactory().createMessage(versioneSoap, 
					SoapUtils.build_Soap_Fault(versioneSoap, 
					this.protocolFactory.createTraduttore().toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO),null,
					faultCode, 
					this.xmlUtils.newElement(org.openspcoop2.core.eccezione.details.utils.XMLUtils.generateDettaglioEccezione(dettaglioEccezione)), 
					this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneProcessamento()));
			return pr.getMessage_throwParseException();
		}catch(Exception e){
			if(this.log!=null)
				this.log.error("Errore buildSoapFault_processamento: "+e.getMessage(),e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap, "Errore buildSoapFaultProtocollo_processamento: "+e.getMessage());
		}
	}
	
	public OpenSPCoop2Message buildSoapFaultProtocollo_intestazione(IDSoggetto identitaPdD,TipoPdD tipoPdD,String modulo,
			CodiceErroreCooperazione codiceErrore,String msgErrore, SOAPVersion versioneSoap, boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1) throws ProtocolException{
		DettaglioEccezione dettaglioEccezione = null;
		if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneValidazione()){
			dettaglioEccezione = this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezione(identitaPdD, tipoPdD, modulo, codiceErrore, 
					this.dettaglioEccezioneOpenSPCoop2Builder.transformFaultMsg(codiceErrore,msgErrore));
		}
		return buildSoapFaultProtocollo_intestazione(dettaglioEccezione, versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
	}
	
	public OpenSPCoop2Message buildSoapFaultProtocollo_intestazione(IDSoggetto identitaPdD,TipoPdD tipoPdD,String modulo,
			ErroreIntegrazione errore, SOAPVersion versioneSoap, boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1) throws ProtocolException{
		DettaglioEccezione dettaglioEccezione = null;
		if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneValidazione()){
			dettaglioEccezione = this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezione(identitaPdD, tipoPdD, modulo, errore.getCodiceErrore(), 
					this.dettaglioEccezioneOpenSPCoop2Builder.transformFaultMsg(errore));
		}
		return buildSoapFaultProtocollo_intestazione(dettaglioEccezione, versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
	}
	
	public OpenSPCoop2Message buildSoapFaultProtocollo_intestazione(DettaglioEccezione dettaglioEccezione, 
			SOAPVersion versioneSoap, boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1){
		try{
			String soapFaultCodePrefix = this.getFaultCodePrefix(versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
			QName faultCode = SOAPFaultCode.Sender.toQName(versioneSoap, soapFaultCodePrefix); 
			Element elementDetail = null;
			if(dettaglioEccezione!=null){
				byte [] bytesElement = org.openspcoop2.core.eccezione.details.utils.XMLUtils.generateDettaglioEccezione(dettaglioEccezione);
				elementDetail = this.xmlUtils.newElement(bytesElement);
			}
			
			OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(versioneSoap, 
					SoapUtils.build_Soap_Fault(versioneSoap, 
					this.protocolFactory.createTraduttore().toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE),null,
					faultCode, 
					elementDetail, this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneValidazione()));
			return pr.getMessage_throwParseException();
		}catch(Exception e){
			if(this.log!=null)
				this.log.error("Errore buildSoapFaultProtocollo_intestazione: "+e.getMessage(),e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap, "Errore buildSoapFaultProtocollo_intestazione: "+e.getMessage());
		}
	}
	
	
	
	
	






	
	
	
	
	
	/** ------------------- Metodi che gestiscono il bug 131 ---------------- */
	
	public void gestioneListaEccezioniMessaggioErroreProtocolloProcessamento(Busta busta){
		
		if(!this.protocolManager.isGenerazioneListaEccezioniErroreProcessamento()){
		
			while( busta.sizeListaEccezioni() > 0 ){
				
				// Fix Bug 131: eccezione di processamento
				@SuppressWarnings("unused")
				Eccezione e = busta.removeEccezione(0);
				// Fix Bug 131: eccezione di processamento
				//System.out.println("ELIMINO ECCEZIONE DI PROCESSAMENTO: "+e.getPosizione());
				
			}
		}
		
	}

	
	
	
	
	
	
	
	







	/** ------------------- Metodi che generano un msg Errore completo ---------------- */

	/**
	 * Costruisce un messaggio  contenente una risposta SOAPFault di errore. 
	 *
	 * @param identitaPdD Dominio del soggetto che ha effettuato la richiesta
	 * @param modulo Modulo OpenSPCoop che ha chiamato il metodo.
	 * @param busta Busta che ha causato l'errore da far diventare una busta Errore.
	 * @param errori Errori
	 * @param messageSecurityPropertiesResponse Proprieta Message Security da applicare alla risposta
	 * @param messageSecurityContext messageSecurityContext
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * 
	 */
	public OpenSPCoop2Message msgErroreProtocollo_Processamento(IState state,IDSoggetto identitaPdD, TipoPdD tipoPdD,
			String modulo, Busta busta,Integrazione integrazione, String idTransazione,
			List<Eccezione> errori,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni,Exception eProcessamento, 
			SOAPVersion versioneSoap, boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1){
		return msgErroreProtocollo_Processamento(state,identitaPdD, tipoPdD, modulo, busta, integrazione, idTransazione, null, errori, null,
				messageSecurityPropertiesResponse, messageSecurityContext, attesaAttiva, checkInterval, profiloGestione, tipoTempo, generazioneListaTrasmissioni, 
				eProcessamento, versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
	}

	/**
	 * Costruisce un messaggio  contenente una risposta SOAPFault di errore. 
	 *
	 * @param identitaPdD Dominio del soggetto che ha effettuato la richiesta
	 * @param modulo Modulo OpenSPCoop che ha chiamato il metodo.
	 * @param busta Busta che ha causato l'errore da far diventare una busta Errore.
	 * @param erroreCooperazione Messaggio di errore
	 * @param messageSecurityPropertiesResponse Proprieta Message Security da applicare alla risposta
	 * @param messageSecurityContext messageSecurityContext
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * 
	 */
	public OpenSPCoop2Message msgErroreProtocollo_Processamento(IState state,IDSoggetto identitaPdD, TipoPdD tipoPdD,
			String modulo, Busta busta,Integrazione integrazione, String idTransazione,
			ErroreCooperazione erroreCooperazione,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni,
			Exception eProcessamento, SOAPVersion versioneSoap, boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1){ 
		return msgErroreProtocollo_Processamento(state,identitaPdD, tipoPdD, modulo, busta, integrazione, idTransazione, erroreCooperazione, null, null,
				messageSecurityPropertiesResponse, messageSecurityContext, attesaAttiva, checkInterval, profiloGestione, tipoTempo, generazioneListaTrasmissioni, 
				eProcessamento, versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
	}
	
	public OpenSPCoop2Message msgErroreProtocollo_Processamento(IState state,IDSoggetto identitaPdD, TipoPdD tipoPdD,
			String modulo, Busta busta,Integrazione integrazione, String idTransazione,
			ErroreIntegrazione erroreIntegrazione,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni,
			Exception eProcessamento, SOAPVersion versioneSoap, boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1){ 
		return msgErroreProtocollo_Processamento(state,identitaPdD, tipoPdD, modulo, busta, integrazione, idTransazione, null, null, erroreIntegrazione,
				messageSecurityPropertiesResponse, messageSecurityContext, attesaAttiva, checkInterval, profiloGestione, tipoTempo, generazioneListaTrasmissioni, 
				eProcessamento, versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
	}

	/**
	 * Costruisce un messaggio  contenente una risposta SOAPFault di errore. 
	 *
	 * @param identitaPdD Dominio del soggetto che ha effettuato la richiesta
	 * @param modulo Modulo OpenSPCoop che ha chiamato il metodo.
	 * @param busta Busta che ha causato l'errore da far diventare una busta Errore.
	 * @param erroreCooperazione Messaggio di errore di cooperazione
	 * @param errori Errori
	 * @param erroreIntegrazione Messaggio di errore di integrazione
	 * @param messageSecurityPropertiesResponse Proprieta Message Security da applicare alla risposta
	 * @param messageSecurityContext messageSecurityContext
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * 
	 */
	private OpenSPCoop2Message msgErroreProtocollo_Processamento(IState state, IDSoggetto identitaPdD, TipoPdD tipoPdD,
			String modulo, Busta busta,Integrazione integrazione, String idTransazione,
			ErroreCooperazione erroreCooperazione, List<Eccezione> errori, ErroreIntegrazione erroreIntegrazione,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext, long attesaAttiva, int checkInterval, String profiloGestione,
			TipoOraRegistrazione tipoTempo, boolean generazioneListaTrasmissioni, 
			Exception eProcessamento, SOAPVersion versioneSoap, boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1){



		try{

			// Lista trasmissioni della richiesta
			ArrayList<Trasmissione> listaTrasmissioniBustaRichiesta = 
				new ArrayList<Trasmissione>();
			for(int i=0;i<busta.sizeListaTrasmissioni();i++){
				listaTrasmissioniBustaRichiesta.add(busta.getTrasmissione(i));
			}

			String id_bustaErrore = 
				this.buildID(state,identitaPdD, idTransazione, attesaAttiva, checkInterval, Boolean.FALSE);

			if(errori==null){
				errori = new Vector<Eccezione>();
			}
			if(erroreCooperazione!=null){
				Eccezione ecc = 
						Eccezione.getEccezioneProcessamento(erroreCooperazione, this.protocolFactory);
				ecc.setModulo(modulo);
				errori.add(ecc);
			}
			if(erroreIntegrazione!=null){
				Eccezione ecc = 
						Eccezione.getEccezioneProcessamento(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreProcessamento(erroreIntegrazione.getDescrizione(this.protocolFactory)), this.protocolFactory);
				ecc.setModulo(modulo);
				errori.add(ecc);
			}

			//ErroreProcessamento: Header
			busta = this.buildMessaggioErroreProtocollo_Processamento(errori,busta,id_bustaErrore,tipoTempo);

			DettaglioEccezione dettaglioEccezione = null;
			if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneProcessamento()){
				if(erroreIntegrazione!=null){
					dettaglioEccezione = this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezioneProcessamentoBusta(identitaPdD, tipoPdD, modulo, 
							erroreIntegrazione.getCodiceErrore(), erroreIntegrazione.getDescrizione(this.protocolFactory), eProcessamento);
				}else{
					dettaglioEccezione = this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezioneFromBusta(identitaPdD, tipoPdD, modulo, null, busta, eProcessamento);
				}
			}
			
			// Fix Bug 131: eccezione di processamento
			gestioneListaEccezioniMessaggioErroreProtocolloProcessamento(busta);
			// Fix Bug 131
			
			//ErroreProcessamento:  Msg
			OpenSPCoop2Message responseMessage = 
				this.buildSoapMsgErroreProtocollo_Processamento(dettaglioEccezione, versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);

			// Tracciamento in Busta (se il profilo e' null, non aggiunto la trasmissione, rischierei di aggiungere elementi che non sono validi per le LineeGuida1.1)
			if(generazioneListaTrasmissioni){
				Trasmissione tras = new Trasmissione();
				tras.setOrigine(identitaPdD.getNome());
				tras.setTipoOrigine(identitaPdD.getTipo());
				tras.setIdentificativoPortaOrigine(identitaPdD.getCodicePorta());
				// Cerco destinatario con identita che sto assumendo (l'origine di quella trasmissione e' la destinazione di questa!)
				// che come mittente non possieda il mittente attuale della busta (senno potrebbe essere il potenziale
				// precedente hop che ha aggiunto una trasmissione da lui a questo hop)
				for(int i=0;i<listaTrasmissioniBustaRichiesta.size();i++){
					if( identitaPdD.getTipo().equals(listaTrasmissioniBustaRichiesta.get(i).getTipoDestinazione()) &&
							identitaPdD.getNome().equals(listaTrasmissioniBustaRichiesta.get(i).getDestinazione()) ){
						//if( !(listaTrasmissioniBustaRichiesta.get(i).getTipoOrigine().equals(busta.getTipoMittente()) &&
						//      listaTrasmissioniBustaRichiesta.get(i).getOrigine().equals(busta.getMittente())) ){
						tras.setDestinazione(listaTrasmissioniBustaRichiesta.get(i).getOrigine());
						tras.setTipoDestinazione(listaTrasmissioniBustaRichiesta.get(i).getTipoOrigine());
						tras.setIdentificativoPortaDestinazione(listaTrasmissioniBustaRichiesta.get(i).getIdentificativoPortaOrigine());
						//}
					}
				}
				tras.setOraRegistrazione(busta.getOraRegistrazione());
				tras.setTempo(busta.getTipoOraRegistrazione());
				busta.addTrasmissione(tras);
			}

			
			// imbustamento
			this.imbustamento(state, responseMessage, busta, integrazione, null);


			// Message-Security
			if(messageSecurityPropertiesResponse != null){
				if(messageSecurityPropertiesResponse.size() > 0){
					messageSecurityContext.setOutgoingProperties(messageSecurityPropertiesResponse);
					if(messageSecurityContext.processOutgoing(responseMessage) == false){
						return this.msgErroreProtocollo_Validazione(state,identitaPdD, modulo,
								busta, integrazione, idTransazione,
								ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(messageSecurityContext.getMsgErrore(), messageSecurityContext.getCodiceErrore()),
								null,null,attesaAttiva,checkInterval,profiloGestione,tipoTempo,generazioneListaTrasmissioni, versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
					}
				}
			}

			return responseMessage;

		}catch(Exception e) {
			this.log.error("Build msgErroreProcessamento non riuscito: "+e.getMessage());
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap, "ErroreProcessamento");
		}

	}

	/**
	 * Costruisce un messaggio  contenente una risposta SOAPFault di errore. 
	 *
	 * @param identitaPdD Dominio del soggetto che ha effettuato la richiesta
	 * @param modulo Modulo OpenSPCoop che ha chiamato il metodo.
	 * @param busta Busta che ha causato l'errore da far diventare una busta Errore.
	 * @param errori Errori
	 * @param messageSecurityPropertiesResponse Proprieta Message Security da applicare alla risposta
	 * @param messageSecurityContext messageSecurityContext
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * 
	 */
	public  OpenSPCoop2Message msgErroreProtocollo_Validazione(IState state,IDSoggetto identitaPdD,
			String modulo,Busta busta,Integrazione integrazione, String idTransazione,		
			List<Eccezione> errori,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni, 
			SOAPVersion versioneSoap, boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1){
		return msgErroreProtocollo_Validazione(state,identitaPdD, modulo, busta, integrazione, idTransazione, null, errori,
				messageSecurityPropertiesResponse,messageSecurityContext,attesaAttiva,checkInterval,
				profiloGestione,tipoTempo,generazioneListaTrasmissioni, versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
	}

	/**
	 * Costruisce un messaggio  contenente una risposta SOAPFault di errore. 
	 *
	 * @param identitaPdD Dominio del soggetto che ha effettuato la richiesta
	 * @param modulo Modulo OpenSPCoop che ha chiamato il metodo.
	 * @param busta Busta che ha causato l'errore da far diventare una busta Errore.
	 * @param erroreCooperazione Messaggio di errore
	 * @param messageSecurityPropertiesResponse Proprieta Message Security da applicare alla risposta
	 * @param messageSecurityContext messageSecurityContext
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * 
	 */
	public OpenSPCoop2Message msgErroreProtocollo_Validazione(IState state,IDSoggetto identitaPdD,
			String modulo, 
			Busta busta,Integrazione integrazione, 
			String idTransazione,
			ErroreCooperazione erroreCooperazione,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext, 
			long attesaAttiva, 
			int checkInterval, 
			String profiloGestione,
			TipoOraRegistrazione tipoTempo, boolean generazioneListaTrasmissioni, 
			SOAPVersion versioneSoap, boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1){
		return msgErroreProtocollo_Validazione(state,identitaPdD, modulo, busta, integrazione, idTransazione, erroreCooperazione,null,
				messageSecurityPropertiesResponse, messageSecurityContext, attesaAttiva, checkInterval, 
				profiloGestione, tipoTempo, generazioneListaTrasmissioni, versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
	}

	/**
	 * Costruisce un messaggio  contenente una risposta SOAPFault di errore. 
	 *
	 * @param identitaPdD Dominio del soggetto che ha effettuato la richiesta
	 * @param modulo Modulo OpenSPCoop che ha chiamato il metodo.
	 * @param busta Busta che ha causato l'errore da far diventare una busta Errore.
	 * @param erroreCooperazione Messaggio di errore di cooperazione
	 * @param errori Errori
	 * @param messageSecurityPropertiesResponse Proprieta Message Security da applicare alla risposta
	 * @param messageSecurityContext messageSecurityContext
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * 
	 */
	public OpenSPCoop2Message msgErroreProtocollo_Validazione(IState state,IDSoggetto identitaPdD,
			String modulo, Busta busta,Integrazione integrazione, String idTransazione, 
			ErroreCooperazione erroreCooperazione, List<Eccezione> errori, 
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext, long attesaAttiva, int checkInterval, String profiloGestione,
			TipoOraRegistrazione tipoTempo, boolean generazioneListaTrasmissioni, 
			SOAPVersion versioneSoap, boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1){

		try{

			// Lista trasmissioni della richiesta
			ArrayList<Trasmissione> listaTrasmissioniBustaRichiesta = 
				new ArrayList<Trasmissione>();
			for(int i=0;i<busta.sizeListaTrasmissioni();i++){
				listaTrasmissioniBustaRichiesta.add(busta.getTrasmissione(i));
			}

			String id_bustaErrore = 
				this.buildID(state,identitaPdD, idTransazione, attesaAttiva, checkInterval, Boolean.FALSE);


			if(errori==null){
				errori = new Vector<Eccezione>();
			}
			if(erroreCooperazione!=null){
				Eccezione ecc = Eccezione.getEccezioneValidazione(erroreCooperazione, this.protocolFactory);
				ecc.setModulo(modulo);
				errori.add(ecc);
			}

			//ErroreValidazione: Header
			busta = this.buildMessaggioErroreProtocollo_Validazione(errori,busta,id_bustaErrore,tipoTempo);	

			//ErroreValidazione:  Msg
			OpenSPCoop2Message responseMessage = 
				this.buildSoapMsgErroreProtocollo_Validazione(versioneSoap,setSoapPrefixBackwardCompatibilityOpenSPCoop1);

			// Tracciamento in Busta
			if(generazioneListaTrasmissioni){
				Trasmissione tras = new Trasmissione();
				tras.setOrigine(identitaPdD.getNome());
				tras.setTipoOrigine(identitaPdD.getTipo());
				tras.setIdentificativoPortaOrigine(identitaPdD.getCodicePorta());
				// Cerco destinatario con identita che sto assumendo (l'origine di quella trasmissione e' la destinazione di questa!)
				// che come mittente non possieda il mittente attuale della busta (senno potrebbe essere il potenziale
				// precedente hop che ha aggiunto una trasmissione da lui a questo hop)
				for(int i=0;i<listaTrasmissioniBustaRichiesta.size();i++){
					if( identitaPdD.getTipo().equals(listaTrasmissioniBustaRichiesta.get(i).getTipoDestinazione()) &&
							identitaPdD.getNome().equals(listaTrasmissioniBustaRichiesta.get(i).getDestinazione()) ){
						//if( !(listaTrasmissioniBustaRichiesta.get(i).getTipoOrigine().equals(busta.getTipoMittente()) &&
						//      listaTrasmissioniBustaRichiesta.get(i).getOrigine().equals(busta.getMittente())) ){
						tras.setDestinazione(listaTrasmissioniBustaRichiesta.get(i).getOrigine());
						tras.setTipoDestinazione(listaTrasmissioniBustaRichiesta.get(i).getTipoOrigine());
						tras.setIdentificativoPortaDestinazione(listaTrasmissioniBustaRichiesta.get(i).getIdentificativoPortaOrigine());
						//}
					}
				}
				tras.setOraRegistrazione(busta.getOraRegistrazione());
				tras.setTempo(busta.getTipoOraRegistrazione());
				busta.addTrasmissione(tras);
			}

			// Add header

			this.imbustamento(state, responseMessage, busta,integrazione, null);

			// Message-Security
			if(messageSecurityPropertiesResponse != null){
				if(messageSecurityPropertiesResponse.size() > 0){
					messageSecurityContext.setOutgoingProperties(messageSecurityPropertiesResponse);
					if(messageSecurityContext.processOutgoing(responseMessage) == false){
						return this.msgErroreProtocollo_Validazione(state,identitaPdD,modulo,
								busta, integrazione, idTransazione,
								ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(messageSecurityContext.getMsgErrore(), messageSecurityContext.getCodiceErrore()),
								null,null,attesaAttiva,checkInterval,profiloGestione,tipoTempo,generazioneListaTrasmissioni, versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
					}
				}
			}

			return responseMessage;

		}catch(Exception e) {
			this.log.error("Build msgErroreProtocollo_Validazione non riuscito: "+e.getMessage(), e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap, "ErroreProcessamento");
		}
	}



















	/** ------------------- Utility di eraser type su un messaggio Soap. ---------------- */

	/**
	 * Metodo che si occupa di effettuare l'eliminazione degli xsd:type sull'header.
	 *
	 * @deprecated  utility che elimina gli xsd type
	 * @param patch Xml su cui effettuare la pulizia dell'header.
	 * @return String contenente un xml 'pulito'.
	 * 
	 */
	@Deprecated  public String eraserType(String patch,String firmaRootElementHeader) throws ProtocolException{

		try{

			StringBuffer soapEnvelopePatch = new StringBuffer();
			int start = patch.indexOf("<"+firmaRootElementHeader);
			int end =  patch.indexOf("</"+firmaRootElementHeader+">" + "</"+firmaRootElementHeader+">".length());

			// Parte fino all'header della busta
			soapEnvelopePatch.append(patch.substring(0,start));

			// Header
			String header = patch.substring(start,end);
			header = header.replaceAll("xsi:type","");
			header = header.replaceAll("=\"xsd","");
			header = header.replaceAll(":string\"","");
			header = header.replaceAll(":dateTime\"","");
			//header = header.replaceAll(" >",">");
			soapEnvelopePatch.append(header);

			// Parte dopol'header della busta
			soapEnvelopePatch.append(patch.substring(end));


			return soapEnvelopePatch.toString();

		} catch(Exception e) {
			this.log.error("Imbustamento.eraserType_byte non riuscito: "+e.getMessage(),e);
			throw new ProtocolException("Imbustamento.eraserType_byte non riuscito: "+e.getMessage(),e);
		}
	}



	/**
	 * Metodo che si occupa di effettuare l'eliminazione degli xsd:type sull'header.
	 *
	 * @deprecated  utility che elimina gli xsd type
	 * @param xml Xml su cui effettuare la pulizia dell'header.
	 * @return byte[] contenente un xml 'pulito'.
	 * 
	 */
	@Deprecated  public byte[] eraserType(byte[] xml,String firmaRootElementHeader) throws ProtocolException{

		ByteArrayOutputStream cleanBusta = null;
		try{
			String header = new String(xml);
			int start = header.indexOf("<"+firmaRootElementHeader);
			int end =  header.indexOf("</"+firmaRootElementHeader+">" + "</"+firmaRootElementHeader+">".length());
			if(start == -1)
				return null;
			if(end == -1)
				return null;
			if(end <= start)
				return null;

			String eraserString = " xsi:type=\"xsd:string\"";
			String eraserDate = " xsi:type=\"xsd:dateTime\"";


			cleanBusta = new ByteArrayOutputStream();

			// Parte fino all'header della busta
			for(int i=0; i<start ; i++)
				cleanBusta.write(xml[i]);

			// Busta
			for(int i=start; i<end ; ){

				if(xml[i] == ' '){

					// Date
					if(i+eraserDate.length() < end){ 
						StringBuffer test = new StringBuffer();
						for(int k=0 ; k < eraserDate.length(); k++){
							test.append((char)xml[i+k]);
						}
						if(test.toString().equals(eraserDate)){
							i = i + eraserDate.length();
							continue;
						}
					}

					// String
					if(i+eraserString.length() < end){ 
						StringBuffer test = new StringBuffer();
						for(int k=0 ; k < eraserString.length(); k++){
							test.append((char)xml[i+k]);
						}
						if(test.toString().equals(eraserString)){
							i = i + eraserString.length();
							continue;
						}
					}

					cleanBusta.write(xml[i]);
					i++;

				}else{
					cleanBusta.write(xml[i]);
					i++;
				}

			}


			// Parte dopol'header della busta
			for(int i=end; i<xml.length ; i++)
				cleanBusta.write(xml[i]);


			byte [] cleanBytes = cleanBusta.toByteArray();
			cleanBusta.close();
			return cleanBytes;

		} catch(Exception e) {
			try{
				if(cleanBusta!=null)
					cleanBusta.close();
			}catch(Exception eis){}
			this.log.error("Imbustamento.eraserType_byte non riuscito: "+e.getMessage(),e);
			throw new ProtocolException("Imbustamento.eraserType_byte non riuscito: "+e.getMessage(),e);
		}
	}  




}





