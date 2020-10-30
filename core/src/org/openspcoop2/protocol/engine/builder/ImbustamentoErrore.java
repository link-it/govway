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



package org.openspcoop2.protocol.engine.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.utils.TempiElaborazione;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.config.ConfigurationRFC7807;
import org.openspcoop2.message.config.IntegrationErrorReturnConfiguration;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.SOAPFaultCode;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.basic.builder.CodeDetailsError;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.FaseImbustamento;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.rest.problem.JsonSerializer;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807Builder;
import org.openspcoop2.utils.rest.problem.XmlSerializer;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;
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

public class ImbustamentoErrore  {

	/* ********  F I E L D S  P R I V A T I  ******** */

	protected OpenSPCoop2MessageFactory errorFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
	/** Logger utilizzato per debug. */
	private Logger log = null;
	private org.openspcoop2.protocol.sdk.IProtocolFactory<?> protocolFactory;
	private org.openspcoop2.message.xml.XMLUtils xmlUtils;
	private IProtocolManager protocolManager;
	private DettaglioEccezioneOpenSPCoop2Builder dettaglioEccezioneOpenSPCoop2Builder;
	private ServiceBinding serviceBinding;
	private Imbustamento imbustamento;
	private IState state;
	private IErroreApplicativoBuilder erroreApplicativoBuilder;
	private String actorInternalSoapFault;
	private String idTransazione;
	
	public ImbustamentoErrore(Logger aLog, org.openspcoop2.protocol.sdk.IProtocolFactory<?> protocolFactory, IState state, 
			ServiceBinding serviceBinding, String actorInternalSoapFault,
			String idTransazione) throws ProtocolException{
		if(aLog!=null)
			this.log = aLog;
		else
			this.log = LoggerWrapperFactory.getLogger(ImbustamentoErrore.class);
		this.protocolFactory = protocolFactory;
		
		this.xmlUtils = org.openspcoop2.message.xml.XMLUtils.getInstance(this.errorFactory);
		
		this.protocolManager = this.protocolFactory.createProtocolManager();
		
		this.dettaglioEccezioneOpenSPCoop2Builder = new DettaglioEccezioneOpenSPCoop2Builder(aLog, protocolFactory);
		
		this.serviceBinding = serviceBinding;
		
		this.state = state;
		
		this.imbustamento = new Imbustamento(this.log, protocolFactory, state);
		
		this.erroreApplicativoBuilder = this.protocolFactory.createErroreApplicativoBuilder();
		
		this.actorInternalSoapFault = actorInternalSoapFault;
		
		this.idTransazione = idTransazione;
	}

	public org.openspcoop2.protocol.sdk.IProtocolFactory<?> getProtocolFactory(){
		return this.protocolFactory;
	}

	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
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

		List<Eccezione> eccs = new ArrayList<Eccezione>();
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
		boolean mittentePresente = busta.getMittente()!=null; // in alcuni protocollo puo' non esistere
		String tipoDest = busta.getTipoMittente();
		String dest = busta.getMittente();
		String indTdest = busta.getIndirizzoMittente();
		String codicePorta = busta.getIdentificativoPortaMittente();
		
		busta.setTipoMittente(busta.getTipoDestinatario());
		busta.setMittente(busta.getDestinatario());
		busta.setIndirizzoMittente(busta.getIndirizzoDestinatario());
		busta.setIdentificativoPortaMittente(busta.getIdentificativoPortaDestinatario());
		
		if(mittentePresente) {
			busta.setTipoDestinatario(tipoDest);
			busta.setDestinatario(dest);
			busta.setIndirizzoDestinatario(indTdest);
			busta.setIdentificativoPortaDestinatario(codicePorta);
		}
		else {
			busta.setTipoDestinatario(null);
			busta.setDestinatario(null);
			busta.setIndirizzoDestinatario(null);
			busta.setIdentificativoPortaDestinatario(null);
		}

		// Verifico 'bonta' dei tipi
		try {
			this.protocolFactory.createTraduttore().toProtocolOrganizationType(busta.getTipoMittente());
		}catch(Exception e) {
			if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
				busta.setTipoMittente(this.protocolFactory.createProtocolConfiguration().getTipoSoggettoDefault());
			}
		}
		try {
			if(busta.getTipoDestinatario()!=null) {
				this.protocolFactory.createTraduttore().toProtocolOrganizationType(busta.getTipoDestinatario());
			}
		}catch(Exception e) {
			if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
				busta.setTipoDestinatario(this.protocolFactory.createProtocolConfiguration().getTipoSoggettoDefault());
			}
		}
		if(busta.getTipoServizio()!=null) {
			try {
				this.protocolFactory.createTraduttore().toProtocolServiceType(busta.getTipoServizio());
			}catch(Exception e) {
				if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
					busta.setTipoServizio(this.protocolFactory.createProtocolConfiguration().getTipoServizioDefault(null));
				}
			}
		}
		
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
						if(this.protocolFactory.createProtocolConfiguration().getTipiServizi(this.serviceBinding).contains(busta.getTipoServizioCorrelato()) == false){
							busta.setTipoServizioCorrelato(null);
						}
					}
				}
			}
			
			// Messaggio.riferimentoMessaggio
			if(busta.getRiferimentoMessaggio()!=null){
				ProprietaValidazione proprietaValidazione = new ProprietaValidazione();
				proprietaValidazione.setValidazioneIDCompleta(false);
				if(this.protocolFactory.createValidazioneSemantica(this.state).validazioneID(busta.getRiferimentoMessaggio(), null, proprietaValidazione)==false){
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
	private String getFaultCodePrefix(MessageType messageType, 
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1){
		if(setSoapPrefixBackwardCompatibilityOpenSPCoop1){
			if(this.protocolFactory!=null &&
					this.protocolFactory.getProtocol()!=null &&
					this.protocolFactory.getProtocol().equals("spcoop")){
				if(MessageType.SOAP_11.equals(messageType)){
					return "soap";
				}
			}	
		}
		
		return null; // viene generato nel namespace della busta SOAP
		
	}






	/**----------------- Metodi per la costruzione di MessaggiSoap da utilizzare con Buste Errore -------------*/

	public OpenSPCoop2Message buildFaultProtocollo_processamento(IDSoggetto identitaPdD,TipoPdD tipoPdD,String modulo, 
			ErroreIntegrazione errore,Throwable eProcessamento, 
			MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, String nomePorta, 
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, boolean useInternalFault,
			Context context){
		boolean useProblemRFC7807 = rfc7807!=null;
		try{
			DettaglioEccezione dettaglioEccezione = this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezione(identitaPdD, tipoPdD, modulo, 
						errore, // errore.getCodiceErrore(), 
						this.dettaglioEccezioneOpenSPCoop2Builder.transformFaultMsg(errore),
						returnConfig, functionError);
			if(this.protocolManager.isGenerazioneDetailsFaultProtocollo_EccezioneProcessamento()) {
				if(eProcessamento!=null){
					this.dettaglioEccezioneOpenSPCoop2Builder.gestioneDettaglioEccezioneProcessamento(eProcessamento, dettaglioEccezione);
				}
			}
			return this.buildFaultProtocollo_processamento(dettaglioEccezione, this.protocolManager.isGenerazioneDetailsFaultProtocollo_EccezioneProcessamento(), 
					messageType, rfc7807, returnConfig, functionError, nomePorta, 
					setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
					context);
		}catch(Exception e){
			return this.errorFactory.createFaultMessage(messageType, useProblemRFC7807, "Errore buildSoapFaultProtocollo_processamento: "+e.getMessage());
		}
	}
	
	public OpenSPCoop2Message buildFaultProtocollo_processamento(IDSoggetto identitaPdD,TipoPdD tipoPdD,String modulo,
			ErroreIntegrazione errore, MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, String nomePorta, 
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, boolean useInternalFault,
			Context context){
		return buildFaultProtocollo_processamento(identitaPdD, tipoPdD, modulo, errore, null, 
				messageType, rfc7807, returnConfig, functionError, nomePorta, 
				setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
				context);
	}
	
	public OpenSPCoop2Message buildFaultProtocollo_processamento(DettaglioEccezione dettaglioEccezione, boolean generazioneDettaglioEccezione,
			MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, String nomePorta, 
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, boolean useInternalFault,
			Context context){
		return this.buildFaultProtocollo(false, dettaglioEccezione, generazioneDettaglioEccezione, 
				messageType, rfc7807, returnConfig, functionError, nomePorta,
				setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
				context);	
	}
	
	public OpenSPCoop2Message buildFaultProtocollo_intestazione(IDSoggetto identitaPdD,TipoPdD tipoPdD,String modulo,
			CodiceErroreCooperazione codiceErrore,String msgErrore, 
			MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, String nomePorta,
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, boolean useInternalFault,
			Context context) throws ProtocolException{
		DettaglioEccezione dettaglioEccezione = 
				this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezione(identitaPdD, tipoPdD, modulo, codiceErrore, 
					this.dettaglioEccezioneOpenSPCoop2Builder.transformFaultMsg(codiceErrore,msgErrore),
					returnConfig, functionError);
		return this.buildFaultProtocollo_intestazione(dettaglioEccezione, this.protocolManager.isGenerazioneDetailsFaultProtocollo_EccezioneValidazione(), 
				messageType, rfc7807, returnConfig, functionError, nomePorta, 
				setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
				context);
	}
	
	public OpenSPCoop2Message buildFaultProtocollo_intestazione(IDSoggetto identitaPdD,TipoPdD tipoPdD,String modulo,
			ErroreIntegrazione errore, 
			MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, String nomePorta,
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, boolean useInternalFault,
			Context context) throws ProtocolException{
		DettaglioEccezione dettaglioEccezione = 
				this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezione(identitaPdD, tipoPdD, modulo, 
					errore, // errore.getCodiceErrore(), 
					this.dettaglioEccezioneOpenSPCoop2Builder.transformFaultMsg(errore),
					returnConfig, functionError);
		return this.buildFaultProtocollo_intestazione(dettaglioEccezione, this.protocolManager.isGenerazioneDetailsFaultProtocollo_EccezioneValidazione(), 
				messageType, rfc7807, returnConfig, functionError, nomePorta, 
				setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
				context);
	}
	
	public OpenSPCoop2Message buildFaultProtocollo_intestazione(DettaglioEccezione dettaglioEccezione, boolean generazioneDettaglioEccezione,
			MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, String nomePorta,
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, boolean useInternalFault,
			Context context)  {
		return this.buildFaultProtocollo(true, dettaglioEccezione, generazioneDettaglioEccezione, 
				messageType, rfc7807, returnConfig, functionError, nomePorta, 
				setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault, context);	
	}

	private OpenSPCoop2Message buildFaultProtocollo(boolean erroreValidazione,
			DettaglioEccezione dettaglioEccezione, boolean generazioneDettaglioEccezione,
			MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, String nomePorta,
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, 
			boolean useInternalFault,
			Context context)  {

		boolean useProblemRFC7807 = rfc7807!=null;
		CodeDetailsError codeDetailsErrorWrapper = null;
		OpenSPCoop2Message msg = null;
		boolean addErroreProtocolloInMessaggio = false;
		ErroriProperties erroriProperties = null;
		
		try{
			erroriProperties = ErroriProperties.getInstance(this.log);
			
			if(erroreValidazione) { // non aggiungamo anche gli errori di processamento altrimenti vengono catalogati come errore di protocollo se avvengono prima della generazione della busta errore
				if(context!=null && context.containsKey(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_PROTOCOLLO)) {
					Object o = context.getObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_PROTOCOLLO);
					if(o!=null) {
						if(o instanceof String) {
							addErroreProtocolloInMessaggio = "true".equalsIgnoreCase((String)o);
						}
						else if(o instanceof Boolean) {
							addErroreProtocolloInMessaggio = (Boolean) o;
						}
					}
				}
			}
			
			if(nomePorta==null) {
				if(context!=null && context.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
					Object o = context.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
					if(o!=null && o instanceof RequestInfo) {
						RequestInfo requestInfo = (RequestInfo) o;
						if(requestInfo.getProtocolContext()!=null) {
							nomePorta = requestInfo.getProtocolContext().getInterfaceName();
						}
					}
				}
			}
			
			OpenSPCoop2MessageFactory mf = this.errorFactory;
			switch (messageType) {
				case XML: 					
					if(dettaglioEccezione==null){
						throw new Exception("Dettaglio eccezione non fornita");
					}
					byte [] xml = null;
					String contentTypeXml = null;
					if(rfc7807!=null) {
						codeDetailsErrorWrapper = new CodeDetailsError();
						ProblemRFC7807 problemRFC7807 = this._buildProblemRFC7807(erroriProperties, codeDetailsErrorWrapper, 
								rfc7807, returnConfig, functionError, nomePorta, this.idTransazione, dettaglioEccezione);
						XmlSerializer xmlSerializer = new XmlSerializer();	
						boolean omitXMLDeclaration = true;
						xml = xmlSerializer.toByteArray(problemRFC7807, !omitXMLDeclaration);
						contentTypeXml = HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807;
					}
					else {
						xml = org.openspcoop2.core.eccezione.details.utils.XMLUtils.generateDettaglioEccezione(dettaglioEccezione);
						contentTypeXml = MessageUtilities.getDefaultContentType(messageType);
					}
					OpenSPCoop2MessageParseResult pr = mf.createMessage(messageType, MessageRole.FAULT, contentTypeXml, xml);
					msg = pr.getMessage_throwParseException();
					return msg;
				
				case JSON:
					if(dettaglioEccezione==null){
						throw new Exception("Dettaglio eccezione non fornita");
					}
					byte [] json = null;
					String contentTypeJson = null;
					codeDetailsErrorWrapper = null;
					if(rfc7807!=null) {
						codeDetailsErrorWrapper = new CodeDetailsError();
						ProblemRFC7807 problemRFC7807 = this._buildProblemRFC7807(erroriProperties, codeDetailsErrorWrapper, 
								rfc7807, returnConfig, functionError, nomePorta, this.idTransazione, dettaglioEccezione);
						JsonSerializer jsonSerializer = new JsonSerializer();
						json = jsonSerializer.toByteArray(problemRFC7807);
						contentTypeJson = HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807;
					}else {
						json = org.openspcoop2.core.eccezione.details.utils.XMLUtils.generateDettaglioEccezioneAsJson(dettaglioEccezione).getBytes();
						contentTypeJson =  HttpConstants.CONTENT_TYPE_JSON;
					}
					pr = mf.createMessage(messageType, MessageRole.FAULT,contentTypeJson, json);
					msg = pr.getMessage_throwParseException();
					return msg;
					
				case BINARY:
				case MIME_MULTIPART:
					// Viene usato per l'opzione None dove viene ritornato solamente il return code
					msg =  mf.createEmptyMessage(messageType, MessageRole.FAULT);
					return msg;

				default:
					msg = mf.createEmptyMessage(messageType, MessageRole.FAULT);
					SOAPEnvelope env = msg.castAsSoap().getSOAPPart().getEnvelope();
					//env.addNamespaceDeclaration("xsi","http://www.w3.org/2001/XMLSchema-instance");

					// Creo SoapFault
					SOAPBody bdy = env.getBody();
					SOAPFault fault = bdy.addFault();

					if(useInternalFault) {
						
						// Esamino errore
						codeDetailsErrorWrapper = new CodeDetailsError();
						QName eccezioneName = null;
						SOAPFaultCode code = null;
						if(dettaglioEccezione!=null &&
								dettaglioEccezione.getExceptions()!=null && dettaglioEccezione.getExceptions().sizeExceptionList()>0
								&& dettaglioEccezione.getExceptions().getException(0)!=null) {
						
							org.openspcoop2.core.eccezione.details.Eccezione ecc = dettaglioEccezione.getExceptions().getException(0);
							
							String codiceEccezione = ecc.getCode();
							String posizioneEccezione = ecc.getDescription();
							if(org.openspcoop2.core.eccezione.details.constants.TipoEccezione.INTEGRATION.equals(ecc.getType())){
								eccezioneName = this.erroreApplicativoBuilder.getQNameEccezioneIntegrazione(codiceEccezione);
							}else{
								eccezioneName = this.erroreApplicativoBuilder.getQNameEccezioneProtocollo(codiceEccezione);
							}
							
							int codeInt = -1;
							if(codiceEccezione.startsWith(org.openspcoop2.protocol.basic.Costanti.ERRORE_PROTOCOLLO_PREFIX_CODE)) {
								try {
									String tmpCode = codiceEccezione.substring(org.openspcoop2.protocol.basic.Costanti.ERRORE_PROTOCOLLO_PREFIX_CODE.length());
									codeInt = Integer.valueOf(tmpCode);
								}catch(Exception e) {}
							}
							if(codeInt>=400 && codeInt< 500) {
								code = SOAPFaultCode.Sender;
							} else {
								code = SOAPFaultCode.Receiver;
							}
							
							codeDetailsErrorWrapper.setCode(codiceEccezione);
							codeDetailsErrorWrapper.setPrefixCode(eccezioneName.getPrefix());
							codeDetailsErrorWrapper.setDetails(posizioneEccezione);
							
						}
						else {
							String soapFaultCodePrefix = this.getFaultCodePrefix(messageType, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
							if(erroreValidazione){
								codeDetailsErrorWrapper.setDetails(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE.toString(this.protocolFactory));
								QName qNameCode = SOAPFaultCode.Sender.toQName(messageType,soapFaultCodePrefix);
								codeDetailsErrorWrapper.setCode(qNameCode.getLocalPart());
								codeDetailsErrorWrapper.setPrefixCode(qNameCode.getPrefix());
							}else{
								codeDetailsErrorWrapper.setDetails(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO.toString(this.protocolFactory));
								QName qNameCode = SOAPFaultCode.Receiver.toQName(messageType,soapFaultCodePrefix);
								codeDetailsErrorWrapper.setCode(qNameCode.getLocalPart());
								codeDetailsErrorWrapper.setPrefixCode(qNameCode.getPrefix());
							}
						}
						
						boolean genericDetails = returnConfig.isGenericDetails();
						if(!genericDetails && erroriProperties.isForceGenericDetails(functionError)) {
							genericDetails = true;
						}
						if (Costanti.TRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS) {
							genericDetails = false;
						}
						if(codeDetailsErrorWrapper.getDetails()!=null && !genericDetails) {
							SoapUtils.setFaultString(fault, codeDetailsErrorWrapper.getDetails());
						}
						else {
							String errorMsg = erroriProperties.getGenericDetails(functionError);
							SoapUtils.setFaultString(fault, errorMsg);
						}
						
						if(Costanti.TRANSACTION_ERROR_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE) {
							msg.castAsSoap().setFaultCode(fault, code, eccezioneName);
						}
						else {
							String codiceEccezioneGW = Costanti.getTransactionSoapFaultCode(returnConfig.getGovwayReturnCode(),erroriProperties.getErrorType(functionError));
							// aggiorno code con codici govway
							if(returnConfig.getGovwayReturnCode()<=499) {
								code = SOAPFaultCode.Sender;
							}
							else {
								code = SOAPFaultCode.Receiver;
							}
							if(MessageType.SOAP_11.equals(messageType)) {
								codiceEccezioneGW = (SOAPFaultCode.Sender.equals(code) ? 
										org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT :  org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER) +
										org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+codiceEccezioneGW;
							}
							QName eccezioneNameGovway = null;
							if(MessageType.SOAP_11.equals(messageType)) {
								eccezioneNameGovway = new QName(org.openspcoop2.message.constants.Costanti.SOAP_ENVELOPE_NAMESPACE, codiceEccezioneGW, fault.getPrefix());
							}
							else {
								eccezioneNameGovway = this.erroreApplicativoBuilder.getQNameEccezioneIntegrazione(codiceEccezioneGW);
							}
							msg.castAsSoap().setFaultCode(fault, code, eccezioneNameGovway);
						}
						
						if(Costanti.TRANSACTION_ERROR_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE) {
							msg.forceTransportHeader(Costanti._getHTTP_HEADER_GOVWAY_ERROR_CODE(), returnConfig.getGovwayReturnCode()+"");
						}
						
						fault.setFaultActor(this.actorInternalSoapFault); 
					}	
					else {
						String soapFaultCodePrefix = this.getFaultCodePrefix(messageType, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
						if(erroreValidazione){
							SoapUtils.setFaultString(fault, MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE.toString(this.protocolFactory));
							fault.setFaultCode(SOAPFaultCode.Sender.toQName(messageType,soapFaultCodePrefix)); //costanti.get_FAULT_CODE_VALIDAZIONE
						}else{
							SoapUtils.setFaultString(fault, MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO.toString(this.protocolFactory));
							fault.setFaultCode(SOAPFaultCode.Receiver.toQName(messageType,soapFaultCodePrefix)); 
						}
					}
					
					boolean elementoDettaglio = false;
					if(!erroreValidazione && dettaglioEccezione!=null && generazioneDettaglioEccezione){
						elementoDettaglio = true;
					}
					else if(useProblemRFC7807 && dettaglioEccezione!=null) {
						elementoDettaglio = true;
					}
										
					if(elementoDettaglio){
						Detail d = fault.addDetail();
						Element e = null;
						if(rfc7807!=null) {
							CodeDetailsError codeDetailsErrorWrapperNOP = new CodeDetailsError(); // uso quello del fault
							ProblemRFC7807 problemRFC7807 = this._buildProblemRFC7807(erroriProperties, codeDetailsErrorWrapperNOP, 
									rfc7807, returnConfig, functionError, nomePorta, this.idTransazione, dettaglioEccezione);
							XmlSerializer xmlSerializer = new XmlSerializer();	
							e = xmlSerializer.toNode(problemRFC7807);
						}
						else {
							e = this.xmlUtils.newElement(org.openspcoop2.core.eccezione.details.utils.XMLUtils.generateDettaglioEccezione(dettaglioEccezione));
						}
						d.appendChild(d.getOwnerDocument().importNode(e, true));
					}
					
					return msg;
			}
			
		} catch(Exception e) {
			this.log.error("Build msgErrore non riuscito: " + e.getMessage(),e);
			return this.errorFactory.createFaultMessage(messageType, useProblemRFC7807); // ritorno ErroreProcessamento per non far "uscire fuori" l'errore
		} finally {
			if(msg!=null) {
				msg.addContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY, useProblemRFC7807 ? 
						org.openspcoop2.message.constants.Costanti.TIPO_RFC7807 : org.openspcoop2.message.constants.Costanti.TIPO_GOVWAY );
				if(codeDetailsErrorWrapper!=null) {
					if(codeDetailsErrorWrapper.getPrefixCode()!=null) {
						String prefixInternalErrorCode = codeDetailsErrorWrapper.getPrefixCode();
						if(prefixInternalErrorCode.endsWith(":")) {
							prefixInternalErrorCode = prefixInternalErrorCode.substring(0, prefixInternalErrorCode.length()-1);
						}
						msg.addContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY_PREFIX_CODE, prefixInternalErrorCode );
					}
					if(codeDetailsErrorWrapper.getCode()!=null) {
						msg.addContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY_CODE, codeDetailsErrorWrapper.getCode() );
						if(Costanti.TRANSACTION_ERROR_STATUS_ABILITATO) {
							String code = codeDetailsErrorWrapper.getCode();
							if(codeDetailsErrorWrapper.getPrefixCode()!=null) {
								if(codeDetailsErrorWrapper.getPrefixCode().endsWith(":")) {
									code = codeDetailsErrorWrapper.getPrefixCode() + code;
								}
								else {
									code = codeDetailsErrorWrapper.getPrefixCode() + ":" +code;
								}
							}
							msg.forceTransportHeader(Costanti._getHTTP_HEADER_GOVWAY_ERROR_STATUS(), code);
						}
					}
					if(codeDetailsErrorWrapper.getDetails()!=null) {
						msg.addContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY_DETAILS, codeDetailsErrorWrapper.getDetails() );
					}
				}
				if(addErroreProtocolloInMessaggio) {
					msg.addContextProperty(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_PROTOCOLLO, org.openspcoop2.core.constants.Costanti.ERRORE_TRUE);
				}
				
				try {
					if(erroriProperties!=null) {
						msg.forceTransportHeader(Costanti._getHTTP_HEADER_GOVWAY_ERROR_TYPE(), erroriProperties.getErrorType(functionError));
					}
				}catch(Exception e) {
					this.log.error("Scrittura header http 'GovWayErrorType' non riuscita: "+e.getMessage(),e);
				}
				
				if(returnConfig.isRetry()) {
					int seconds = returnConfig.getRetryAfterSeconds();
					if(seconds<0) {
						seconds=0;
					}
					if(returnConfig.getRetryRandomBackoffSeconds()>0) {
						seconds = seconds + new Random().nextInt(returnConfig.getRetryRandomBackoffSeconds());
					}
					msg.forceTransportHeader(HttpConstants.RETRY_AFTER, seconds+"");
				}
				
				msg.setForcedResponseCode(returnConfig.getHttpReturnCode()+"");	
			}
		}


	}
	
	private ProblemRFC7807 _buildProblemRFC7807(ErroriProperties erroriProperties, CodeDetailsError codeDetailsErrorWrapper, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError,
			String nomePorta, String transactionId, DettaglioEccezione dettaglioEccezione)throws ProtocolException{
		
		try{	
			
			// Problem builder
			ProblemRFC7807Builder rfc7807ProblemBuilder = null;
			String webSite = erroriProperties.getWebSite(functionError);
			if(webSite!=null && !"".equals(webSite)) {
				rfc7807ProblemBuilder = new ProblemRFC7807Builder(webSite);
			}
			else if(rfc7807.isType()) {
				rfc7807ProblemBuilder = new ProblemRFC7807Builder(rfc7807.getTypeFormat());
			}
			else {
				rfc7807ProblemBuilder = new ProblemRFC7807Builder(false);
			}
			
			// Esamino errore
			org.openspcoop2.core.eccezione.details.Eccezione eccezione = null;
			if(dettaglioEccezione.getExceptions()!=null && dettaglioEccezione.getExceptions().sizeExceptionList()>0) {
				eccezione = dettaglioEccezione.getExceptions().getException(0); // prendo la prima
			}
			if(eccezione!=null) {
				codeDetailsErrorWrapper.setDetails(eccezione.getDescription());
				if(eccezione.getCode()!=null) {
					String code = eccezione.getCode();
					String prefixCodeStatus = null;
					if(eccezione.getType()!=null && 
							org.openspcoop2.core.eccezione.details.constants.TipoEccezione.PROTOCOL.equals(eccezione.getType())) {
						prefixCodeStatus = org.openspcoop2.protocol.basic.Costanti.PROBLEM_RFC7807_GOVWAY_CODE_PREFIX_PROTOCOL;
					}
					else {
						prefixCodeStatus = org.openspcoop2.protocol.basic.Costanti.PROBLEM_RFC7807_GOVWAY_CODE_PREFIX_INTEGRATION;
					}
					codeDetailsErrorWrapper.setPrefixCode(prefixCodeStatus);
					codeDetailsErrorWrapper.setCode(code);
				}
			}
			
			// Costruisco problem
			ProblemRFC7807 problemRFC7807 = rfc7807ProblemBuilder.buildProblem(returnConfig.getGovwayReturnCode());
			
			// details
			if(rfc7807.isDetails()) {
				
				boolean genericDetails = returnConfig.isGenericDetails();
				if(!genericDetails && erroriProperties.isForceGenericDetails(functionError)) {
					genericDetails = true;
				}
				if (Costanti.TRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS) {
					genericDetails = false;
				}
				
				if(codeDetailsErrorWrapper.getDetails()!=null && !genericDetails) {
					problemRFC7807.setDetail(codeDetailsErrorWrapper.getDetails());
				}
				else {
					problemRFC7807.setDetail(erroriProperties.getGenericDetails(functionError));
				}
			}
			
			// govway-type
			if(rfc7807.isGovwayType()) {
				String govwayType = erroriProperties.getErrorType(functionError);
				
				// title
				if(Costanti.isPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE()) {
					if(Costanti.isPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE()) {
						problemRFC7807.setTitle(StringUtils.join(
							     StringUtils.splitByCharacterTypeCamelCase(govwayType),
							     ' '));
					}
					else {
						problemRFC7807.setTitle(govwayType);
					}
					
					if(Costanti.isPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM()) {
						problemRFC7807.getCustom().put(org.openspcoop2.protocol.basic.Costanti.getPROBLEM_RFC7807_GOVWAY_TYPE(), govwayType);
					}
				}
				else {
					problemRFC7807.getCustom().put(org.openspcoop2.protocol.basic.Costanti.getPROBLEM_RFC7807_GOVWAY_TYPE(), govwayType);
				}
			}
			
			// govway-status
			if(Costanti.TRANSACTION_ERROR_STATUS_ABILITATO && rfc7807.isGovwayStatus()) {
				if(codeDetailsErrorWrapper.getCode()!=null && codeDetailsErrorWrapper.getPrefixCode()!=null) {
					problemRFC7807.getCustom().put(org.openspcoop2.protocol.basic.Costanti.getPROBLEM_RFC7807_GOVWAY_CODE(), 
							codeDetailsErrorWrapper.getPrefixCode()+codeDetailsErrorWrapper.getCode());
				}
			}
			
			// instance
			if(Costanti.TRANSACTION_ERROR_INSTANCE_ID_ABILITATO && rfc7807.isInstance()) {
				problemRFC7807.setInstance(nomePorta);
			}
			
			// govway-transactionId
			if(rfc7807.isGovwayTransactionId()) {
				problemRFC7807.getCustom().put(org.openspcoop2.protocol.basic.Costanti.getPROBLEM_RFC7807_GOVWAY_TRANSACTION_ID(), transactionId);
			}
			
			return problemRFC7807;
		}catch(Exception e){
			throw new ProtocolException("toProblemRFC7807 failed: "+e.getMessage(),e);
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
	 */
	public OpenSPCoop2Message msgErroreProtocollo_Processamento(IDSoggetto identitaPdD, TipoPdD tipoPdD,Context context,
			String modulo, Busta busta,Integrazione integrazione, String idTransazione,
			List<Eccezione> errori,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni,Exception eProcessamento, 
			MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, 
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, boolean useInternalFault,
			TempiElaborazione tempiElaborazione){
		return msgErroreProtocollo_Processamento(identitaPdD, tipoPdD, context, 
				modulo, busta, integrazione, idTransazione, null, errori, null,
				messageSecurityPropertiesResponse, messageSecurityContext, attesaAttiva, checkInterval, profiloGestione, tipoTempo, generazioneListaTrasmissioni, 
				eProcessamento, messageType, rfc7807, returnConfig, functionError, 
				setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
				tempiElaborazione);
	}

	/**
	 * Costruisce un messaggio  contenente una risposta SOAPFault di errore. 
	 */
	public OpenSPCoop2Message msgErroreProtocollo_Processamento(IDSoggetto identitaPdD, TipoPdD tipoPdD,Context context,
			String modulo, Busta busta,Integrazione integrazione, String idTransazione,
			ErroreCooperazione erroreCooperazione,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni,
			Exception eProcessamento, MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, 
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, boolean useInternalFault,
			TempiElaborazione tempiElaborazione){ 
		return msgErroreProtocollo_Processamento(identitaPdD, tipoPdD, context, 
				modulo, busta, integrazione, idTransazione, erroreCooperazione, null, null,
				messageSecurityPropertiesResponse, messageSecurityContext, attesaAttiva, checkInterval, profiloGestione, tipoTempo, generazioneListaTrasmissioni, 
				eProcessamento, messageType, rfc7807, returnConfig, functionError, 
				setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
				tempiElaborazione);
	}
	
	public OpenSPCoop2Message msgErroreProtocollo_Processamento(IDSoggetto identitaPdD, TipoPdD tipoPdD,Context context,
			String modulo, Busta busta,Integrazione integrazione, String idTransazione,
			ErroreIntegrazione erroreIntegrazione,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni,
			Exception eProcessamento, MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, 
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, boolean useInternalFault,
			TempiElaborazione tempiElaborazione){ 
		return msgErroreProtocollo_Processamento(identitaPdD, tipoPdD, context, 
				modulo, busta, integrazione, idTransazione, null, null, erroreIntegrazione,
				messageSecurityPropertiesResponse, messageSecurityContext, attesaAttiva, checkInterval, profiloGestione, tipoTempo, generazioneListaTrasmissioni, 
				eProcessamento, messageType, rfc7807, returnConfig, functionError, 
				setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
				tempiElaborazione);
	}

	/**
	 * Costruisce un messaggio  contenente una risposta SOAPFault di errore. 
	 *
	 */
	private OpenSPCoop2Message msgErroreProtocollo_Processamento(IDSoggetto identitaPdD, TipoPdD tipoPdD,Context context,
			String modulo, Busta busta,Integrazione integrazione, String idTransazione,
			ErroreCooperazione erroreCooperazione, List<Eccezione> errori, ErroreIntegrazione erroreIntegrazione,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext, long attesaAttiva, int checkInterval, String profiloGestione,
			TipoOraRegistrazione tipoTempo, boolean generazioneListaTrasmissioni, 
			Exception eProcessamento, MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, 
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, boolean useInternalFault,
			TempiElaborazione tempiElaborazione){



		boolean useProblemRFC7807 = rfc7807!=null;
		try{

			Busta bustaRichiesta = null;
			if(busta!=null) {
				bustaRichiesta = busta.clone();
			}
			
			// Lista trasmissioni della richiesta
			ArrayList<Trasmissione> listaTrasmissioniBustaRichiesta = 
				new ArrayList<Trasmissione>();
			for(int i=0;i<busta.sizeListaTrasmissioni();i++){
				listaTrasmissioniBustaRichiesta.add(busta.getTrasmissione(i));
			}

			String id_bustaErrore = 
					this.imbustamento.buildID(identitaPdD, idTransazione, attesaAttiva, checkInterval, RuoloMessaggio.RISPOSTA);

			if(errori==null){
				errori = new ArrayList<Eccezione>();
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
			if(this.protocolManager.isGenerazioneDetailsFaultProtocollo_EccezioneProcessamento() || 
					(useProblemRFC7807 && (rfc7807.isDetails() || rfc7807.isGovwayStatus()))
				){
				if(erroreIntegrazione!=null){
					dettaglioEccezione = this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezioneProcessamentoBusta(identitaPdD, tipoPdD, modulo, 
							erroreIntegrazione, //erroreIntegrazione.getCodiceErrore(), 
							erroreIntegrazione.getDescrizione(this.protocolFactory), eProcessamento,
							returnConfig, functionError);
				}else{
					dettaglioEccezione = this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezioneFromBusta(identitaPdD, tipoPdD, modulo, null, busta, eProcessamento);
				}
			}
			
			// Fix Bug 131: eccezione di processamento
			gestioneListaEccezioniMessaggioErroreProtocolloProcessamento(busta);
			// Fix Bug 131
			
			//ErroreProcessamento:  Msg
			String nomePorta = null;
			if(integrazione!=null) {
				nomePorta = integrazione.getNomePorta();
			}
			OpenSPCoop2Message responseMessage = 
					this.buildFaultProtocollo_processamento(dettaglioEccezione, this.protocolManager.isGenerazioneDetailsFaultProtocollo_EccezioneProcessamento(),
							messageType, rfc7807, returnConfig, functionError, nomePorta, 
							setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
							context);

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
						boolean tipoOrigineValido = true;
						try {
							this.protocolFactory.createTraduttore().toProtocolOrganizationType(listaTrasmissioniBustaRichiesta.get(i).getTipoOrigine());
						}catch(Exception e) {
							tipoOrigineValido = false;
						}
						if(tipoOrigineValido) {
							tras.setDestinazione(listaTrasmissioniBustaRichiesta.get(i).getOrigine());
							tras.setTipoDestinazione(listaTrasmissioniBustaRichiesta.get(i).getTipoOrigine());
							tras.setIdentificativoPortaDestinazione(listaTrasmissioniBustaRichiesta.get(i).getIdentificativoPortaOrigine());
						}
					}
				}
				if(tras.getDestinazione()==null || tras.getTipoDestinazione()==null){
					tras.setDestinazione(busta.getDestinatario());
					tras.setTipoDestinazione(busta.getTipoDestinatario());
					try{
						String dominio = RegistroServiziManager.getInstance(this.state).getDominio(new IDSoggetto(tras.getTipoDestinazione(),tras.getDestinazione()), null, this.protocolFactory);
						tras.setIdentificativoPortaDestinazione(dominio);
					}catch(Exception e){}
				}
				tras.setOraRegistrazione(busta.getOraRegistrazione());
				tras.setTempo(busta.getTipoOraRegistrazione());
				busta.addTrasmissione(tras);
			}

			
			// imbustamento
			ProtocolMessage protocolMessage = this.imbustamento.imbustamentoRisposta(responseMessage, context,
					busta, bustaRichiesta, integrazione, null, 
					FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
			if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
				responseMessage = protocolMessage.getMessage(); // updated
			}


			// Message-Security
			if(messageSecurityPropertiesResponse != null){
				if(messageSecurityPropertiesResponse.size() > 0){
					messageSecurityContext.setOutgoingProperties(messageSecurityPropertiesResponse);
					if(messageSecurityContext.processOutgoing(responseMessage,context.getContext(), tempiElaborazione) == false){
						responseMessage = this.msgErroreProtocollo_Intestazione(identitaPdD, tipoPdD, context, 
								modulo,	busta, integrazione, idTransazione,
								ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(messageSecurityContext.getMsgErrore(), messageSecurityContext.getCodiceErrore()),
								null,null,attesaAttiva,checkInterval,profiloGestione,tipoTempo,generazioneListaTrasmissioni, 
								messageType, rfc7807, returnConfig, functionError, 
								setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
								tempiElaborazione);
					}
				}
			}

			// imbustamento
			protocolMessage = this.imbustamento.imbustamentoRisposta(responseMessage, context,
					busta, bustaRichiesta, integrazione, null, 
					FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
			if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
				responseMessage = protocolMessage.getMessage(); // updated
			}
			
			return responseMessage;

		}catch(Exception e) {
			this.log.error("Build msgErroreProcessamento non riuscito: "+e.getMessage(),e);
			return this.errorFactory.createFaultMessage(messageType, useProblemRFC7807);
		}

	}

	/**
	 * Costruisce un messaggio  contenente una risposta SOAPFault di errore. 
	 *
	 */
	public  OpenSPCoop2Message msgErroreProtocollo_Intestazione(IDSoggetto identitaPdD, TipoPdD tipoPdD,Context context,
			String modulo,Busta busta,Integrazione integrazione, String idTransazione,		
			List<Eccezione> errori,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni, 
			MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, 
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, boolean useInternalFault,
			TempiElaborazione tempiElaborazione){
		return msgErroreProtocollo_Intestazione(identitaPdD, tipoPdD, context,
				modulo, busta, integrazione, idTransazione, null, errori,
				messageSecurityPropertiesResponse,messageSecurityContext,attesaAttiva,checkInterval,
				profiloGestione,tipoTempo,generazioneListaTrasmissioni, messageType, rfc7807, returnConfig, functionError,
				setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
				tempiElaborazione);
	}

	/**
	 * Costruisce un messaggio  contenente una risposta SOAPFault di errore. 
	 *
	 */
	public OpenSPCoop2Message msgErroreProtocollo_Intestazione(IDSoggetto identitaPdD, TipoPdD tipoPdD,Context context,
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
			MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, 
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, boolean useInternalFault,
			TempiElaborazione tempiElaborazione){
		return msgErroreProtocollo_Intestazione(identitaPdD, tipoPdD, context,
				modulo, busta, integrazione, idTransazione, erroreCooperazione,null,
				messageSecurityPropertiesResponse, messageSecurityContext, attesaAttiva, checkInterval, 
				profiloGestione, tipoTempo, generazioneListaTrasmissioni, messageType, rfc7807, returnConfig, functionError,
				setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
				tempiElaborazione);
	}

	/**
	 * Costruisce un messaggio  contenente una risposta SOAPFault di errore. 
	 *
	 */
	private OpenSPCoop2Message msgErroreProtocollo_Intestazione(IDSoggetto identitaPdD, TipoPdD tipoPdD,Context context,
			String modulo, Busta busta,Integrazione integrazione, String idTransazione, 
			ErroreCooperazione erroreCooperazione, List<Eccezione> errori, 
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext, long attesaAttiva, int checkInterval, String profiloGestione,
			TipoOraRegistrazione tipoTempo, boolean generazioneListaTrasmissioni, 
			MessageType messageType, ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError, 
			boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1, boolean useInternalFault,
			TempiElaborazione tempiElaborazione){

		boolean useProblemRFC7807 = rfc7807!=null;
		try{

			Busta bustaRichiesta = null;
			if(busta!=null) {
				bustaRichiesta = busta.clone();
			}
			
			// Lista trasmissioni della richiesta
			ArrayList<Trasmissione> listaTrasmissioniBustaRichiesta = 
				new ArrayList<Trasmissione>();
			for(int i=0;i<busta.sizeListaTrasmissioni();i++){
				listaTrasmissioniBustaRichiesta.add(busta.getTrasmissione(i));
			}

			String id_bustaErrore = 
					this.imbustamento.buildID(identitaPdD, idTransazione, attesaAttiva, checkInterval, RuoloMessaggio.RISPOSTA);


			if(errori==null){
				errori = new ArrayList<Eccezione>();
			}
			if(erroreCooperazione!=null){
				Eccezione ecc = Eccezione.getEccezioneValidazione(erroreCooperazione, this.protocolFactory);
				ecc.setModulo(modulo);
				errori.add(ecc);
			}

			//ErroreValidazione: Header
			busta = this.buildMessaggioErroreProtocollo_Validazione(errori,busta,id_bustaErrore,tipoTempo);	

			DettaglioEccezione dettaglioEccezione = null;
			if(this.protocolManager.isGenerazioneDetailsFaultProtocollo_EccezioneValidazione() || 
					(useProblemRFC7807 && (rfc7807.isDetails() || rfc7807.isGovwayStatus()))
				){
				dettaglioEccezione = this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezioneFromBusta(identitaPdD, tipoPdD, modulo, null, busta, null);
			}
			
			//ErroreValidazione:  Msg
			String nomePorta = null;
			if(integrazione!=null) {
				nomePorta = integrazione.getNomePorta();
			}
			OpenSPCoop2Message responseMessage = 
				this.buildFaultProtocollo_intestazione(dettaglioEccezione,this.protocolManager.isGenerazioneDetailsFaultProtocollo_EccezioneValidazione(),
						messageType, rfc7807, returnConfig, functionError, nomePorta, 
						setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
						context);

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
						boolean tipoOrigineValido = true;
						try {
							this.protocolFactory.createTraduttore().toProtocolOrganizationType(listaTrasmissioniBustaRichiesta.get(i).getTipoOrigine());
						}catch(Exception e) {
							tipoOrigineValido = false;
						}
						if(tipoOrigineValido) {
							tras.setDestinazione(listaTrasmissioniBustaRichiesta.get(i).getOrigine());
							tras.setTipoDestinazione(listaTrasmissioniBustaRichiesta.get(i).getTipoOrigine());
							tras.setIdentificativoPortaDestinazione(listaTrasmissioniBustaRichiesta.get(i).getIdentificativoPortaOrigine());
						}
					}
				}
				if(tras.getDestinazione()==null || tras.getTipoDestinazione()==null){
					tras.setDestinazione(busta.getDestinatario());
					tras.setTipoDestinazione(busta.getTipoDestinatario());
					try{
						String dominio = RegistroServiziManager.getInstance(this.state).getDominio(new IDSoggetto(tras.getTipoDestinazione(),tras.getDestinazione()), null, this.protocolFactory);
						tras.setIdentificativoPortaDestinazione(dominio);
					}catch(Exception e){}
				}
				tras.setOraRegistrazione(busta.getOraRegistrazione());
				tras.setTempo(busta.getTipoOraRegistrazione());
				busta.addTrasmissione(tras);
			}

			// Imbustamento
			ProtocolMessage protocolMessage = this.imbustamento.imbustamentoRisposta(responseMessage, context,
					busta, bustaRichiesta, integrazione, null, 
					FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
			if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
				responseMessage = protocolMessage.getMessage(); // updated
			}

			// Message-Security
			if(messageSecurityPropertiesResponse != null){
				if(messageSecurityPropertiesResponse.size() > 0){
					messageSecurityContext.setOutgoingProperties(messageSecurityPropertiesResponse);
					if(messageSecurityContext.processOutgoing(responseMessage,context.getContext(), tempiElaborazione) == false){
						responseMessage = this.msgErroreProtocollo_Intestazione(identitaPdD,tipoPdD,context,
								modulo,	busta, integrazione, idTransazione,
								ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(messageSecurityContext.getMsgErrore(), messageSecurityContext.getCodiceErrore()),
								null,null,attesaAttiva,checkInterval,profiloGestione,tipoTempo,generazioneListaTrasmissioni, 
								messageType, rfc7807, returnConfig, functionError, 
								setSoapPrefixBackwardCompatibilityOpenSPCoop1, useInternalFault,
								tempiElaborazione);
					}
				}
			}

			// Imbustamento
			protocolMessage = this.imbustamento.imbustamentoRisposta(responseMessage, context,
					busta, bustaRichiesta, integrazione, null, 
					FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
			if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
				responseMessage = protocolMessage.getMessage(); // updated
			}
			
			return responseMessage;

		}catch(Exception e) {
			this.log.error("Build msgErroreProtocollo_Validazione non riuscito: "+e.getMessage(), e);
			return this.errorFactory.createFaultMessage(messageType, useProblemRFC7807);
		}
	}



}





