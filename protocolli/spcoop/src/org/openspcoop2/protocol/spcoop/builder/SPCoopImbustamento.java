/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.spcoop.builder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.spcoop.SPCoopBustaRawContent;
import org.openspcoop2.protocol.spcoop.config.SPCoopProperties;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.utils.SPCoopUtils;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneSemantica;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneSintattica;
import org.openspcoop2.protocol.utils.IDSerialGenerator;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.dch.InputStreamDataSource;
import org.openspcoop2.utils.id.serial.IDSerialGeneratorParameter;
import org.openspcoop2.utils.id.serial.IDSerialGeneratorType;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.slf4j.Logger;
import org.w3c.dom.Node;


/**
 * Classe utilizzata per costruire una Busta eGov, o parti di essa.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SPCoopImbustamento {
	
	private IProtocolFactory<SOAPHeaderElement> factory;
	private Logger log;
	private SPCoopProperties spcoopProperties = null;
	@SuppressWarnings("unused")
	private List<String> tipiSoggetti = null;
	private List<String> tipiServizi = null;
	private SPCoopValidazioneSemantica validazioneSemantica = null;
	private SPCoopValidazioneSintattica validazioneSintattica = null;
	private AbstractXMLUtils xmlUtils = null;
	private ITraduttore traduttore = null;
	private IProtocolManager protocolManager = null;

	public SPCoopImbustamento(IProtocolFactory<SOAPHeaderElement> factory) throws ProtocolException{
		this.factory = factory;
		this.log = factory.getLogger();
		this.spcoopProperties = SPCoopProperties.getInstance(this.log);
		
		this.tipiSoggetti = this.factory.createProtocolConfiguration().getTipiSoggetti();
		this.tipiServizi = this.factory.createProtocolConfiguration().getTipiServizi(ServiceBinding.SOAP);
		
		this.validazioneSemantica = (SPCoopValidazioneSemantica) this.factory.createValidazioneSemantica();
		this.validazioneSintattica = (SPCoopValidazioneSintattica) this.factory.createValidazioneSintattica();
		
		this.xmlUtils = org.openspcoop2.message.xml.XMLUtils.getInstance();
		
		this.traduttore = this.factory.createTraduttore();
		
		this.protocolManager = this.factory.createProtocolManager();
	}
	
	public IProtocolFactory<SOAPHeaderElement> getProtocolFactory() {
		return this.factory;
	}  
	
	
	/** MAX_SERIALE */
	private static int maxSeriale = 0;
	/** LunghezzaPrefisso */
	private static int prefixLenght = 0;

	// Usato in qualsiasi tipo di identificativo
	private static synchronized void initSerialCounter(int prefixSeriale){
		// Imposto il MAX_SERIAL_VALUE
		if(SPCoopImbustamento.maxSeriale==0){
			if(prefixSeriale==-1){
				SPCoopImbustamento.maxSeriale = SPCoopCostanti.MAX_VALUE_ID_EGOV_COUNTER;
				SPCoopImbustamento.prefixLenght = 0;
			}else if(prefixSeriale<10){
				SPCoopImbustamento.maxSeriale = SPCoopCostanti.MAX_VALUE_ID_EGOV_COUNTER_PREFIX_1;
				SPCoopImbustamento.prefixLenght = 1;
			}else{
				SPCoopImbustamento.maxSeriale = SPCoopCostanti.MAX_VALUE_ID_EGOV_COUNTER_PREFIX_2;
				SPCoopImbustamento.prefixLenght = 2;
			}
		}
	}
	
	// Usato nel tipo di identificativo static
	/** Contatore seriale */
	private static int serialCounter = 0;
	public static synchronized int getNextSerialCounter(){

		if((SPCoopImbustamento.serialCounter+1) > SPCoopImbustamento.maxSeriale){
			SPCoopImbustamento.serialCounter = 0;
		} 
		SPCoopImbustamento.serialCounter++;
		return SPCoopImbustamento.serialCounter;
	}
	
	/* ********  Metodi per la costruzione di parti della busta SPCoop  ******** */ 
	
	/**
	 * Metodo che si occupa di costruire una stringa formata da un identificativo
	 * conforme alla specifica eGov.
	 * L'identificativo e' formato da :
	 * codAmministrazione_codPortaDominio_num.progressivo_data_ora
	 * <p>
	 * Il codice Amministrazione e' preso da <var>destinatario</var>.
	 * Il codice della Porta di Dominio e' preso da <var>idPD</var>.
	 * Le altre informazioni sono costruite dal metodo, che si occupa
	 * di assemblarle in una unica stringa e di ritornarla.
	 *
	 * @param idSoggetto identificativo del soggetto
	 * @param idTransazione identificativo della transazione
	 * @param ruoloMessaggio Indicazione se si tratta della richiesta
	 * @return un oggetto String contenente l'identificativo secondo specifica eGov.
	 * 
	 */
	public String buildID(IState state, IDSoggetto idSoggetto, String idTransazione, RuoloMessaggio ruoloMessaggio) throws ProtocolException {

		
		String idPD = idSoggetto.getCodicePorta();
		String codAmm = idSoggetto.getNome();
		
		if(idPD == null || codAmm == null){
			this.log.error("Creazione ID eGov non riuscita: alcuni parametri di creazione null idPD["+idPD+"] codAmm["+codAmm+"]");
			throw new ProtocolException("Creazione ID eGov non riuscita: alcuni parametri di creazione null idPD["+idPD+"] codAmm["+codAmm+"]");
		}
		
		initSerialCounter(this.spcoopProperties.getPrefissoSeriale_IdentificativoBusta());

		IDSerialGenerator serialGenerator = null;
		IDSerialGeneratorParameter serialGeneratorParameter = null;
		if( SPCoopCostanti.IDENTIFICATIVO_EGOV_SERIALE_STATIC.equals(this.spcoopProperties.getTipoSeriale_IdentificativoBusta())==false ){
			
			ConfigurazionePdD config = this.factory.getConfigurazionePdD();
			
			serialGenerator = new IDSerialGenerator(config.getLog(),state,config.getTipoDatabase());
			
			serialGeneratorParameter = new IDSerialGeneratorParameter(this.factory.getProtocol());
			serialGeneratorParameter.setSerializableTimeWaitMs(config.getAttesaAttivaJDBC());
			serialGeneratorParameter.setSerializableNextIntervalTimeMs(config.getCheckIntervalJDBC());
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.NUMERIC);
			serialGeneratorParameter.setMaxValue(new Long(SPCoopImbustamento.maxSeriale));
			serialGeneratorParameter.setWrap(true);
		}
		
		try{
		
			long counter = -1;
		
			StringBuffer bf = new StringBuffer();
			bf.append(codAmm);
			bf.append('_');
			bf.append(idPD);
			bf.append('_');
	
			if ( SPCoopCostanti.IDENTIFICATIVO_EGOV_SERIALE_MYSQL.equals(this.spcoopProperties.getTipoSeriale_IdentificativoBusta()) ) {
	
				serialGeneratorParameter.setTipo(IDSerialGeneratorType.MYSQL);
				counter = serialGenerator.buildIDAsNumber(serialGeneratorParameter);
	
			} else if(SPCoopCostanti.IDENTIFICATIVO_EGOV_SERIALE_STATIC.equals(this.spcoopProperties.getTipoSeriale_IdentificativoBusta())){
	
				counter = SPCoopImbustamento.getNextSerialCounter();
					
			}else {
	
				counter = serialGenerator.buildIDAsNumber(serialGeneratorParameter);
	
			}
	
			if(this.spcoopProperties.getPrefissoSeriale_IdentificativoBusta()!=-1){
				bf.append(this.spcoopProperties.getPrefissoSeriale_IdentificativoBusta());
			}
			
			String c = Long.toString(counter);
			int padding= SPCoopCostanti.CIFRE_SERIALI_ID_EGOV - SPCoopImbustamento.prefixLenght - c.length();
			int i=0;
			for(;i<padding;i++)
				bf.append('0');
			bf.append(c);
		
			bf.append('_');
	
			// Date nel formato aaaa-mm-gg_hh:mm
			Date now=DateManager.getDate();
			SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm"); // SimpleDateFormat non e' thread-safe
			bf.append(dateformat.format(now));
	
			return bf.toString();
		
		}catch (Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}

	}

















	/* ----------------  Metodi per la costruzione di una busta eGov  -------------------- */

	/**
	 * Metodo che si occupa di costruire un elemento SOAPElement
	 * contenente l'elemento 'Intestazione' definito nella specifica eGov 
	 * 'http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/'.
	 * <p>
	 * Gli elementi eGov da creare, all'interno dell'header 'Intestazione' sono prelevati
	 * dall'oggetto <var>eGov</var> di tipo {@link Busta}.
	 *
	 * @param eGov contiene le funzionalita' eGov da inserire nell'header.
	 * @return il SOAPElement 'eGov_IT:Intestazione'  se la costruzione ha successo, null altrimenti.
	 * 
	 */
	public SOAPHeaderElement build_eGovHeader(OpenSPCoop2Message msg, Busta eGov) throws ProtocolException{ 
		return build_eGovHeader(msg,eGov, true, false);
	}
	public SOAPHeaderElement build_eGovHeader(OpenSPCoop2Message msg,Busta eGov,boolean verificaPresenzaElementiObbligatori) throws ProtocolException{ 
		return build_eGovHeader(msg,eGov, verificaPresenzaElementiObbligatori, false);
	}
	public SOAPHeaderElement build_eGovHeader(OpenSPCoop2Message msg,Busta eGov,boolean verificaPresenzaElementiObbligatori,boolean forzaValidazioneXSDElementiDisabilitata) throws ProtocolException{ 
		try{

			OpenSPCoop2SoapMessage soapMsg = null;
			if(msg==null){
				OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getMessageFactory();
				soapMsg = mf.createEmptyMessage(MessageType.SOAP_11,MessageRole.NONE).castAsSoap();
			}
			else{
				soapMsg = msg.castAsSoap();
			}
				
				
			SOAPHeader hdr = soapMsg.getSOAPHeader();
			if(hdr==null){
				hdr = soapMsg.getSOAPPart().getEnvelope().addHeader(); 
			}
			QName name = new QName(SPCoopCostanti.NAMESPACE_EGOV, "Intestazione",SPCoopCostanti.PREFIX_EGOV);
			SOAPHeaderElement eGovHeader = soapMsg.newSOAPHeaderElement(hdr, name);
						
			
			
			eGovHeader.setActor(SPCoopCostanti.ACTOR_EGOV);
			eGovHeader.setMustUnderstand(true);
			eGovHeader.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");

			// Costruzione IntestazioneMessaggio
			SOAPElement eGovIntestazioneMsg = eGovHeader.addChildElement("IntestazioneMessaggio",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			// Mittente
			if(verificaPresenzaElementiObbligatori){
				if(eGov.getMittente()==null){
					throw new ProtocolException("Mittente e' un elemento obbligatorio, e non e' stato definito");
				}
				if(eGov.getTipoMittente()==null){
					throw new ProtocolException("TipoMittente e' un attributo obbligatorio, e non e' stato definito");
				}
			}
			SOAPElement eGovMitt = eGovIntestazioneMsg.addChildElement("Mittente",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			SOAPElement eGovIDParteMitt = eGovMitt.addChildElement("IdentificativoParte",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			if(eGov.getMittente()!=null){
				eGovIDParteMitt.setValue(eGov.getMittente());
			}
			if(eGov.getTipoMittente()!=null){
				eGovIDParteMitt.setAttribute("tipo",eGov.getTipoMittente());
			}
			if(eGov.getIndirizzoMittente() != null){
				eGovIDParteMitt.setAttribute("indirizzoTelematico",eGov.getIndirizzoMittente());
			}


			// Destinatario
			if(verificaPresenzaElementiObbligatori){
				if(eGov.getDestinatario()==null){
					throw new ProtocolException("Destinatario e' un elemento obbligatorio, e non e' stato definito");
				}
				if(eGov.getTipoDestinatario()==null){
					throw new ProtocolException("TipoDestinatario e' un attributo obbligatorio, e non e' stato definito");
				}
			}
			SOAPElement eGovDest = eGovIntestazioneMsg.addChildElement("Destinatario",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			SOAPElement eGovIDParteDest = eGovDest.addChildElement("IdentificativoParte",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			if(eGov.getDestinatario()!=null){
				eGovIDParteDest.setValue(eGov.getDestinatario());
			}
			if(eGov.getTipoDestinatario()!=null){
				eGovIDParteDest.setAttribute("tipo",eGov.getTipoDestinatario());
			}
			if(eGov.getIndirizzoDestinatario() != null){
				eGovIDParteDest.setAttribute("indirizzoTelematico",eGov.getIndirizzoDestinatario());
			}

			// Profilo di Collaborazione
			if(eGov.getProfiloDiCollaborazione() != null){
				
				boolean generazioneElemento = true;
				if(!forzaValidazioneXSDElementiDisabilitata){
					if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
						if( !(
								ProfiloDiCollaborazione.ONEWAY.equals(eGov.getProfiloDiCollaborazione()) || 
								ProfiloDiCollaborazione.SINCRONO.equals(eGov.getProfiloDiCollaborazione()) || 
								ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(eGov.getProfiloDiCollaborazione()) || 
								ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(eGov.getProfiloDiCollaborazione())
							) 
						){
							generazioneElemento = false;
						}
					}			
				}			
				
				if(generazioneElemento){
					
					SOAPElement eGovProfCollaborazione = eGovIntestazioneMsg.addChildElement("ProfiloCollaborazione",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
					switch (eGov.getProfiloDiCollaborazione()) {
					case ONEWAY:
						eGovProfCollaborazione.setValue(SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY);
						break;
					case SINCRONO:
						eGovProfCollaborazione.setValue(SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO);
						break;
					case ASINCRONO_SIMMETRICO:
						eGovProfCollaborazione.setValue(SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO);
						break;
					case ASINCRONO_ASIMMETRICO:
						eGovProfCollaborazione.setValue(SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO);
						break;
					default:
						eGovProfCollaborazione.setValue(eGov.getProfiloDiCollaborazioneValue());
						break;
					}
					
					if(eGov.getServizioCorrelato() != null){
						eGovProfCollaborazione.setAttribute("servizioCorrelato",eGov.getServizioCorrelato());
					}
					
					if(eGov.getTipoServizioCorrelato() != null){
						
						boolean generazioneAttributo = true;
						if(!forzaValidazioneXSDElementiDisabilitata){
							if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
								generazioneAttributo = this.tipiServizi.contains(eGov.getTipoServizioCorrelato());
							}
						}
						if(generazioneAttributo){
							eGovProfCollaborazione.setAttribute("tipo",eGov.getTipoServizioCorrelato());
						}
					}
				}
			}	    

			// Collaborazione
			if(eGov.getCollaborazione() != null){
				boolean generazioneElemento = true;
				if(!forzaValidazioneXSDElementiDisabilitata){
					if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
						ProprietaValidazione proprietaValidazione = new ProprietaValidazione();
						proprietaValidazione.setValidazioneIDCompleta(false);
						generazioneElemento = this.validazioneSemantica.validazioneID(eGov.getCollaborazione(), null,proprietaValidazione);
					}
				}
				if(generazioneElemento){
					SOAPElement eGovCollaborazione = eGovIntestazioneMsg.addChildElement("Collaborazione",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
					eGovCollaborazione.setValue(eGov.getCollaborazione());
				}
			}

			// Servizio
			if(eGov.getServizio() != null){
				SOAPElement eGovServizio = eGovIntestazioneMsg.addChildElement("Servizio",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				eGovServizio.setValue(eGov.getServizio());
				if(eGov.getTipoServizio() != null){
					eGovServizio.setAttribute("tipo",eGov.getTipoServizio());
				}
			}

			// Azione
			if(eGov.getAzione() != null){
				SOAPElement eGovAzione = eGovIntestazioneMsg.addChildElement("Azione",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				eGovAzione.setValue(eGov.getAzione()); 
			}

			// Messaggio
			SOAPElement eGovMessaggio = eGovIntestazioneMsg.addChildElement("Messaggio",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			
			// Messaggio.Identificatore
			if(eGov.getID() != null){
				boolean generazioneElemento = true;
				if(!forzaValidazioneXSDElementiDisabilitata){
					if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
						generazioneElemento = this.validazioneSemantica.validazioneID_engine(eGov.getID());
					}
				}
				if(generazioneElemento){
					SOAPElement eGovIDMsg = eGovMessaggio.addChildElement("Identificatore",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
					if(eGov.getID() != null)
						eGovIDMsg.setValue(eGov.getID());
				}else{
					throw new ProtocolException("Identificatore e' un elemento obbligatorio, e il valore non e' utilizzabile rispetto all'xsd ("+eGov.getID()+")");
				}
			}else{
				if(verificaPresenzaElementiObbligatori){
					throw new ProtocolException("Identificatore e' un elemento obbligatorio, e non e' stato definito");
				}
			}	
			
			// Messaggio.OraRegistrazione
			SOAPElement eGovOraRegistrazione = eGovMessaggio.addChildElement("OraRegistrazione",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			if(eGov.getOraRegistrazione() == null){
				if(verificaPresenzaElementiObbligatori){
					throw new ProtocolException("OraRegistrazione e' un elemento obbligatorio, e non e' stato definito");
				}
			}
			if(eGov.getOraRegistrazione() != null){
				eGovOraRegistrazione.setValue(SPCoopUtils.getDate_eGovFormat(eGov.getOraRegistrazione()));
			}
			if(eGov.getTipoOraRegistrazione()!=null){
				boolean generazioneAttributo = true;
				if(!forzaValidazioneXSDElementiDisabilitata){
					if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
						generazioneAttributo = ( TipoOraRegistrazione.LOCALE.equals(eGov.getTipoOraRegistrazione()) || TipoOraRegistrazione.SINCRONIZZATO.equals(eGov.getTipoOraRegistrazione()) );
					}
				}
				if(generazioneAttributo){
					if(TipoOraRegistrazione.LOCALE.equals(eGov.getTipoOraRegistrazione()))
						eGovOraRegistrazione.setAttribute("tempo", SPCoopCostanti.TIPO_TEMPO_LOCALE);
					else if(TipoOraRegistrazione.SINCRONIZZATO.equals(eGov.getTipoOraRegistrazione()))
						eGovOraRegistrazione.setAttribute("tempo", SPCoopCostanti.TIPO_TEMPO_SPC);
					else 
						eGovOraRegistrazione.setAttribute("tempo", eGov.getTipoOraRegistrazioneValue());
				}
				else{
					throw new ProtocolException("TipoOraRegistrazione e' un elemento obbligatorio, e il valore non e' utilizzabile rispetto all'xsd ("+eGov.getTipoOraRegistrazioneValue()+")");
				}
			}
			else{
				eGovOraRegistrazione.setAttribute("tempo", SPCoopCostanti.TIPO_TEMPO_SPC); // default
			}
			
			// Messaggio.RiferimentoMessaggio
			if(eGov.getRiferimentoMessaggio() != null){
				boolean generazioneElemento = true;
				if(!forzaValidazioneXSDElementiDisabilitata){
					if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
						generazioneElemento = this.validazioneSemantica.validazioneID_engine(eGov.getRiferimentoMessaggio());
					}
				}
				if(generazioneElemento){
					SOAPElement eGovRifMsg = eGovMessaggio.addChildElement("RiferimentoMessaggio",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
					eGovRifMsg.setValue(eGov.getRiferimentoMessaggio());
				}
			}
			
			// Messaggio.Scadenza
			if(eGov.getScadenza() != null){
				SOAPElement eGovScadenza = eGovMessaggio.addChildElement("Scadenza",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				eGovScadenza.setValue(SPCoopUtils.getDate_eGovFormat(eGov.getScadenza()));
			}

			// Profilo di Trasmissione
			SOAPElement eGovProfTrasmissione = eGovIntestazioneMsg.addChildElement("ProfiloTrasmissione",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			if(eGov.getInoltro() != null) {
				boolean generazioneElemento = true;
				if(!forzaValidazioneXSDElementiDisabilitata){
					if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
						generazioneElemento = ( Inoltro.CON_DUPLICATI.equals(eGov.getInoltro()) || Inoltro.SENZA_DUPLICATI.equals(eGov.getInoltro()) );
					}
				}
				if(generazioneElemento){
					if(Inoltro.CON_DUPLICATI.equals(eGov.getInoltro()))
						eGovProfTrasmissione.setAttribute("inoltro", SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI);
					else if(Inoltro.SENZA_DUPLICATI.equals(eGov.getInoltro()))
						eGovProfTrasmissione.setAttribute("inoltro", SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI);
					else 
						eGovProfTrasmissione.setAttribute("inoltro", eGov.getInoltroValue());
				}
			}
			String crValue = "false";
			if(eGov.isConfermaRicezione())
				crValue="true";
			eGovProfTrasmissione.setAttribute("confermaRicezione",crValue);



			// Sequenza
			if(eGov.getSequenza() != -1){
				SOAPElement eGovSequenza = eGovIntestazioneMsg.addChildElement("Sequenza",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				StringBuffer bf = new StringBuffer();
				String sequenza = Long.toString(eGov.getSequenza());
				int padding= 7 - sequenza.length();
				int i=0;
				for(;i<padding;i++)
					bf.append('0');
				bf.append(sequenza);
				eGovSequenza.setAttribute("numeroProgressivo",bf.toString());
			}





			// Costruzione Lista Riscontri
			if(eGov.sizeListaRiscontri() > 0){
				SOAPElement eGovListaRiscontri = eGovHeader.addChildElement("ListaRiscontri",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				for(int i=0; i<eGov.sizeListaRiscontri();i++){
					Riscontro ris = eGov.getRiscontro(i);

					SOAPElement Riscontro = eGovListaRiscontri.addChildElement("Riscontro",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
					if(ris.getID() != null) {
						boolean generazioneElemento = true;
						if(!forzaValidazioneXSDElementiDisabilitata){
							if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
								generazioneElemento = this.validazioneSemantica.validazioneID_engine(ris.getID());
							}
						}
						if(generazioneElemento){
							SOAPElement id = Riscontro.addChildElement("Identificatore",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
							id.setValue(ris.getID());
						}else{
							throw new ProtocolException("Identificatore e' un elemento obbligatorio in un Riscontro, e il valore non e' utilizzabile rispetto all'xsd ("+ris.getID()+")");
						}
					}
					else{
						if(verificaPresenzaElementiObbligatori){
							throw new ProtocolException("Identificatore e' un elemento obbligatorio in un Riscontro, e non e' stato definito");
						}
					}

					if(ris.getOraRegistrazione() != null) {
						SOAPElement ora = Riscontro.addChildElement("OraRegistrazione",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
						ora.setValue(SPCoopUtils.getDate_eGovFormat(ris.getOraRegistrazione()));
						if(ris.getTipoOraRegistrazione()!=null){
							boolean generazioneAttributo = true;
							if(!forzaValidazioneXSDElementiDisabilitata){
								if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
									generazioneAttributo = ( TipoOraRegistrazione.LOCALE.equals(ris.getTipoOraRegistrazione()) || TipoOraRegistrazione.SINCRONIZZATO.equals(ris.getTipoOraRegistrazione()) );
								}
							}
							if(generazioneAttributo){
								if(TipoOraRegistrazione.LOCALE.equals(ris.getTipoOraRegistrazione()))
									ora.setAttribute("tempo",SPCoopCostanti.TIPO_TEMPO_LOCALE);
								else
									ora.setAttribute("tempo",SPCoopCostanti.TIPO_TEMPO_SPC);
							}
							else{
								throw new ProtocolException("TipoOraRegistrazione e' un attributo obbligatorio in un Riscontro, e il valore non e' utilizzabile rispetto all'xsd ("+ris.getTipoOraRegistrazioneValue(this.factory)+")");
							}
						}
						else{
							ora.setAttribute("tempo", SPCoopCostanti.TIPO_TEMPO_SPC);
						}
					}
					else{
						if(verificaPresenzaElementiObbligatori){
							throw new ProtocolException("OraRegistrazione e' un elemento obbligatorio in un Riscontro, e non e' stato definito");
						}
					}

				}
			}


			// Costruzione Lista Trasmissioni
			if(eGov.sizeListaTrasmissioni() > 0){

				boolean addFirstElement = false;
				SOAPElement eGovListaTrasmissioni = null;

				for(int i=0; i<eGov.sizeListaTrasmissioni();i++){

					Trasmissione tr = eGov.getTrasmissione(i);

					if(tr.getOrigine() != null && tr.getTipoOrigine() != null &&
							tr.getDestinazione() != null && tr.getTipoDestinazione() != null &&
							tr.getOraRegistrazione() != null && tr.getTempo()!= null){

						if(addFirstElement==false){
							eGovListaTrasmissioni = eGovHeader.addChildElement("ListaTrasmissioni",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
							addFirstElement = true;
						}

						SOAPElement Trasmissione = eGovListaTrasmissioni.addChildElement("Trasmissione",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);

						SOAPElement orig = Trasmissione.addChildElement("Origine",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
						SOAPElement IDParteOrigine = orig.addChildElement("IdentificativoParte",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
						IDParteOrigine.setValue(tr.getOrigine());
						IDParteOrigine.setAttribute("tipo",tr.getTipoOrigine());
						if(tr.getIndirizzoOrigine() != null){
							IDParteOrigine.setAttribute("indirizzoTelematico",tr.getIndirizzoOrigine());
						}

						SOAPElement dest = Trasmissione.addChildElement("Destinazione",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
						SOAPElement IDParteDest = dest.addChildElement("IdentificativoParte",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
						IDParteDest.setValue(tr.getDestinazione());
						IDParteDest.setAttribute("tipo",tr.getTipoDestinazione());
						if(tr.getIndirizzoDestinazione() != null){
							IDParteDest.setAttribute("indirizzoTelematico",tr.getIndirizzoDestinazione());
						}

						SOAPElement ora = Trasmissione.addChildElement("OraRegistrazione",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
						ora.setValue(SPCoopUtils.getDate_eGovFormat(tr.getOraRegistrazione()));
						boolean generazioneAttributo = true;
						if(!forzaValidazioneXSDElementiDisabilitata){
							if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
								generazioneAttributo = ( TipoOraRegistrazione.LOCALE.equals(tr.getTempo()) || TipoOraRegistrazione.SINCRONIZZATO.equals(tr.getTempo()) );
							}
						}
						if(generazioneAttributo){
							if(TipoOraRegistrazione.LOCALE.equals(tr.getTempo()))
								ora.setAttribute("tempo",SPCoopCostanti.TIPO_TEMPO_LOCALE);
							else
								ora.setAttribute("tempo",SPCoopCostanti.TIPO_TEMPO_SPC);
						}
						else{
							throw new ProtocolException("Tempo e' un attributo obbligatorio in una trasmissione, e il valore non e' utilizzabile rispetto all'xsd ("+tr.getTempoValue(this.factory)+")");
						}
					}
				}
			}


			// Costruzione Lista Eccezioni
			if(eGov.sizeListaEccezioni() > 0){
				SOAPElement eGovListaEccezioni = eGovHeader.addChildElement("ListaEccezioni",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				for(int i=0; i<eGov.sizeListaEccezioni();i++){
					Eccezione ecc = eGov.getEccezione(i);

					SOAPElement eccezione = eGovListaEccezioni.addChildElement("Eccezione",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
					if(ecc.getContestoCodifica() != null) {
						eccezione.setAttribute("contestoCodifica",ecc.getContestoCodificaValue(this.factory));
					}
					else{
						if(verificaPresenzaElementiObbligatori){
							throw new ProtocolException("ContestoCodifica e' un attributo obbligatorio in una eccezione, e non e' stato definito");
						}
					}

					if(ecc.getCodiceEccezione() != null) {
						boolean generazioneAttributo = true;
						if(!forzaValidazioneXSDElementiDisabilitata){
							if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
								generazioneAttributo = this.validazioneSintattica.validazioneCodiceEccezione(ecc.getCodiceEccezione());
							}
						}
						if(generazioneAttributo){
							eccezione.setAttribute("codiceEccezione",ecc.getCodiceEccezioneValue(this.factory));
						}
						else{
							throw new ProtocolException("CodiceEccezione e' un attributo obbligatorio in una eccezione, e il valore non e' utilizzabile rispetto all'xsd ("+ecc.getCodiceEccezione()+")");
						}
					}
					else{
						if(verificaPresenzaElementiObbligatori){
							throw new ProtocolException("CodiceEccezione e' un attributo obbligatorio in una eccezione, e non e' stato definito");
						}
					}


					if(ecc.getRilevanza() != null) {
						boolean generazioneAttributo = true;
						if(!forzaValidazioneXSDElementiDisabilitata){
							if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
								generazioneAttributo = ( LivelloRilevanza.DEBUG.equals(ecc.getRilevanza()) || 
										LivelloRilevanza.ERROR.equals(ecc.getRilevanza()) || 
										LivelloRilevanza.FATAL.equals(ecc.getRilevanza()) || 
										LivelloRilevanza.INFO.equals(ecc.getRilevanza()) || 
										LivelloRilevanza.WARN.equals(ecc.getRilevanza()) );
							}
						}
						if(generazioneAttributo){
							eccezione.setAttribute("rilevanza",this.traduttore.toString(ecc.getRilevanza()));
						}
						else{
							throw new ProtocolException("Rilevanza e' un attributo obbligatorio in una eccezione, e il valore non e' utilizzabile rispetto all'xsd ("+ecc.getRilevanza()+")");
						}
					}
					else{
						if(verificaPresenzaElementiObbligatori){
							throw new ProtocolException("Rilevanza e' un attributo obbligatorio in una eccezione, e non e' stato definito");
						}
					}

					if(ecc.getDescrizione(this.factory) != null) {
						eccezione.setAttribute("posizione",ecc.getDescrizione(this.factory));
					}
					else{
						if(verificaPresenzaElementiObbligatori){
							throw new ProtocolException("Posizione e' un attributo obbligatorio in una eccezione, e non e' stato definito");
						}
					}
					
				}
			}

			return eGovHeader;	    
		} catch(Exception e) {
			this.log.error("Creazione busta eGov non riuscita: "+e.getMessage(),e);
			throw new ProtocolException("Creazione busta eGov non riuscita: "+e.getMessage(),e);
		}   
	}



	public OpenSPCoop2Message build_eGovManifest(OpenSPCoop2Message msg,RuoloMessaggio ruoloMessaggio,
			ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{ 
		try{

			// Descrizione
			SOAPElement descrizione = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.SOAP_11,MessageRole.NONE).castAsSoap().
					getSOAPBody().addChildElement(new QName(SPCoopCostanti.NAMESPACE_EGOV, "Descrizione",SPCoopCostanti.PREFIX_EGOV));
			
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap(); 

			// Attachments
			java.util.Iterator<?> iter = soapMsg.getAttachments();
			int attach = 1;
			while( iter.hasNext() ){
				Utilities.printFreeMemory("Imbustamento - Costruzione manifest att " + attach);
				AttachmentPart p = (AttachmentPart) iter.next();
				String contentID = p.getContentId();
				String contentLocation = p.getContentLocation();
				//String fileName = p.getAttachmentFile();
				String contentType = p.getContentType();
				// Costruzione DescrizioneMessaggio
				SOAPElement descrizioneMessaggio =  descrizione.addChildElement(SPCoopCostanti.LOCAL_NAME_MANIFEST_EGOV_DESCRIZIONE_MESSAGGIO,
						SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				// Costruzione Riferimento
				SOAPElement riferimento =  descrizioneMessaggio.addChildElement("Riferimento",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				
				//Name href = sf.createName("href",Costanti.PREFIX_EGOV,Costanti.NAMESPACE_EGOV); 
				
				if(contentID!=null){
					riferimento.setAttribute("href","cid:"+contentID); // xsd:anyURI
				}
				else{
					riferimento.setAttribute("href",contentLocation); // xsd:anyURI
				}

				//Name role = sf.createName("role",Costanti.PREFIX_EGOV,Costanti.NAMESPACE_EGOV); 
				
				if(attach==1 && proprietaManifestAttachments.isScartaBody()){
					
					if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
						riferimento.setAttribute("role",this.spcoopProperties.getRoleRichiestaManifest()); // xsd:string
					}
					else{
						riferimento.setAttribute("role",this.spcoopProperties.getRoleRispostaManifest()); // xsd:string
					}
				}
				else{
					riferimento.setAttribute("role",this.spcoopProperties.getRoleAllegatoManifest()); // xsd:string
				}
				// Obbligatorio il namespace
				
				// ??? riferimento.setAttribute(Costanti.NAMESPACE_EGOV,"id","attachment"+attach); // xsd:ID
				QName id = new QName(SPCoopCostanti.NAMESPACE_EGOV,"id",SPCoopCostanti.PREFIX_EGOV); 
				riferimento.addAttribute(id,"attachment"+attach); // xsd:ID
				// Costruzione Schema
				SOAPElement schema = riferimento.addChildElement("Schema",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				schema.setAttribute("posizione",contentType); //xsd:anyURI
				// Costruzione Titolo
				SOAPElement titolo = riferimento.addChildElement("Titolo",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				titolo.setAttribute("Lingua","it"); //xsd:language
				//if(fileName!=null)
				//	titolo.setValue(fileName); // xsd:string
				//else
				if(attach==1 && proprietaManifestAttachments.isScartaBody()){
					if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio))
						titolo.setValue(this.spcoopProperties.getRoleRichiestaManifest()); // xsd:string
					else
						titolo.setValue(this.spcoopProperties.getRoleRispostaManifest()); // xsd:string
				}else{
					titolo.setValue("attachment"+attach); // xsd:string
				}
				attach++;
			}
			

			// Body into Attachments
			if(proprietaManifestAttachments.isScartaBody() == false){
				Utilities.printFreeMemory("Imbustamento - Scarta Body");
				byte [] body = TunnelSoapUtils.sbustamentoSOAPEnvelope(soapMsg.getSOAPPart().getEnvelope());
				AttachmentPart ap = null;
				//ByteArrayInputStream isContent = new ByteArrayInputStream(body);
				//ap.setContent(isContent,"text/xml; charset=UTF-8");
//					String xmlBody = new String(body);
				//xmlBody = StringEscapeUtils.unescapeXml(xmlBody);
//					Reader stringReader = new StringReader(xmlBody);
				//Source streamSource = new javax.xml.transform.stream.StreamSource(new java.io.ByteArrayInputStream(body)); FIX: StreamSource instances may only be used once.
				//System.out.println("BODY IS :["+new String(body)+"] ["+body.length+"]");
				boolean bodyWithMultiRootElement = false;
				if(body==null || body.length==0){
					body = SPCoopCostanti.XML_MANIFEST_EGOV_EMPTY_BODY.getBytes();
					//System.out.println("PATCH ["+new String(body)+"]");
				}
				else{
					List<Node> listNode = SoapUtils.getNotEmptyChildNodes(soapMsg.getSOAPPart().getEnvelope().getBody(), false);
					if(listNode!=null && listNode.size()>1){
						//System.out.println("MULTI ELEMENT: "+listNode.size());
						bodyWithMultiRootElement = true;
					}
				}
				if(bodyWithMultiRootElement){
					//System.out.println("OCTECT");
					InputStreamDataSource isSource = new InputStreamDataSource("ManifestEGov", HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM, body);
					ap = soapMsg.createAttachmentPart(new DataHandler(isSource));
				}else{
					//System.out.println("XML");
					Source streamSource = new DOMSource(this.xmlUtils.newElement(body));
					ap = soapMsg.createAttachmentPart();
					ap.setContent(streamSource, "text/xml");
				}
				ap.setContentId(soapMsg.createContentID(SPCoopCostanti.NAMESPACE_EGOV));
				//ap.setContentLocation("provaOpenSPCoop"); // Test Sbustamento con ContentLocation
				soapMsg.addAttachmentPart(ap);
				//isContent.close();
				// add OriginalMsg in Manifest
				// Costruzione DescrizioneMessaggio
				SOAPElement descrizioneMessaggio = descrizione.addChildElement(SPCoopCostanti.LOCAL_NAME_MANIFEST_EGOV_DESCRIZIONE_MESSAGGIO,
						SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				// Costruzione Riferimento
				SOAPElement riferimento = descrizioneMessaggio.addChildElement("Riferimento",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				riferimento.setAttribute("href","cid:" + ap.getContentId()); // xsd:anyURI
				//riferimento.setAttribute("href",ap.getContentLocation()); // xsd:anyURI // Test Sbustamento con ContentLocation
				if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
					riferimento.setAttribute("role",this.spcoopProperties.getRoleRichiestaManifest()); // xsd:string
				}
				else{
					riferimento.setAttribute("role",this.spcoopProperties.getRoleRispostaManifest()); // xsd:string
				}
				// Obbligatorio il namespace
				//riferimento.setAttribute(Costanti.NAMESPACE_EGOV,"id","attachment"+attach); // xsd:ID
				QName id = new QName(SPCoopCostanti.NAMESPACE_EGOV,"id",SPCoopCostanti.PREFIX_EGOV); 
				riferimento.addAttribute(id,"attachment"+attach); // xsd:ID
				// Costruzione Schema
				SOAPElement schema = riferimento.addChildElement("Schema",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				schema.setAttribute("posizione",ap.getContentType()); //xsd:anyURI
				// Costruzione Titolo
				SOAPElement titolo = riferimento.addChildElement("Titolo",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				titolo.setAttribute("Lingua","it"); //xsd:language
				if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio))
					titolo.setValue(this.spcoopProperties.getRoleRichiestaManifest()); // xsd:string
				else
					titolo.setValue(this.spcoopProperties.getRoleRispostaManifest()); // xsd:string

			}
			Utilities.printFreeMemory("Imbustamento - Add Manifest in Body");
			// Add Manifest in Body
			soapMsg.getSOAPBody().removeContents();
			soapMsg.getSOAPBody().addChildElement(descrizione);


			return msg;

		} catch(Exception e) {
			this.log.error("Creazione Manifest degli attachments non riuscita: "+e.getMessage(),e);
			throw new ProtocolException("Creazione Manifest degli attachments non riuscita: "+e.getMessage(),e);
		}   
	}


	public void imbustamentoEGov(OpenSPCoop2Message msg,Busta busta,ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{	
		this.imbustamentoEGov(msg, busta, RuoloMessaggio.RISPOSTA, proprietaManifestAttachments);
	}


	public SOAPHeaderElement imbustamentoEGov(OpenSPCoop2Message msg,Busta busta,
			RuoloMessaggio ruoloMessaggio,
			ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{	

		try{
			// add header eGov
			Utilities.printFreeMemory("Imbustamento - Creazione header eGov");
			SOAPHeaderElement headerEGovElement = (SOAPHeaderElement) this.build_eGovHeader(msg, busta);
			Utilities.printFreeMemory("Imbustamento - imbustamento");
			this.imbustamentoEGov(msg, headerEGovElement, ruoloMessaggio, proprietaManifestAttachments);
			return headerEGovElement;
		}catch(Exception e){
			this.log.error("ImbustamentoEGov non riuscito: "+e.getMessage(),e);
			throw new ProtocolException("ImbustamentoEGov non riuscito: "+e.getMessage(),e);
		}
	}

	public SOAPHeaderElement imbustamentoEGov(OpenSPCoop2Message msg, SOAPHeaderElement headerEGovElement,
			RuoloMessaggio ruoloMessaggio,
			ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{	

		try{
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			SOAPHeader hdr = soapMsg.getSOAPHeader();
			if(hdr==null){
				hdr = soapMsg.getSOAPPart().getEnvelope().addHeader(); 
			}

			soapMsg.addHeaderElement(hdr, headerEGovElement);
			Utilities.printFreeMemory("Imbustamento -Header Aggiunto");
			// Gestione Manifest
			
			if (proprietaManifestAttachments!=null && proprietaManifestAttachments.isGestioneManifest() && soapMsg.countAttachments()>0) {
				Utilities.printFreeMemory("Imbustamento - Gestione Manifest");
				this.build_eGovManifest(msg,ruoloMessaggio,proprietaManifestAttachments);
			}
			
			//System.out.println("MSG:");
			//msg.writeTo(System.out);
			
			return headerEGovElement;
			
		}catch(Exception e){
			this.log.error("ImbustamentoEGov non riuscito: "+e.getMessage(),e);
			throw new ProtocolException("ImbustamentoEGov non riuscito: "+e.getMessage(),e);
		}
	}


	/**
	 * Metodo che si occupa di costruire un elemento Trasmissione
	 *
	 * @param trasmissione Trasmissione
	 * 
	 */
	public void build_trasmissione(Trasmissione trasmissione,SOAPElement listaTrasmissioni) throws ProtocolException{ 
		try{

			SOAPElement trasmissioneNode = listaTrasmissioni.addChildElement("Trasmissione",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);

			SOAPElement orig = trasmissioneNode.addChildElement("Origine",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			SOAPElement IDParteOrigine = orig.addChildElement("IdentificativoParte",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			IDParteOrigine.setValue(trasmissione.getOrigine());
			IDParteOrigine.setAttribute("tipo",trasmissione.getTipoOrigine());
			if(trasmissione.getIndirizzoOrigine() != null){
				IDParteOrigine.setAttribute("indirizzoTelematico",trasmissione.getIndirizzoOrigine());
			}

			SOAPElement dest = trasmissioneNode.addChildElement("Destinazione",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			SOAPElement IDParteDest = dest.addChildElement("IdentificativoParte",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			IDParteDest.setValue(trasmissione.getDestinazione());
			IDParteDest.setAttribute("tipo",trasmissione.getTipoDestinazione());
			if(trasmissione.getIndirizzoDestinazione() != null){
				IDParteDest.setAttribute("indirizzoTelematico",trasmissione.getIndirizzoDestinazione());
			}

			SOAPElement ora = trasmissioneNode.addChildElement("OraRegistrazione",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			ora.setValue(SPCoopUtils.getDate_eGovFormat(trasmissione.getOraRegistrazione()));
			if(TipoOraRegistrazione.LOCALE.equals(trasmissione.getTempo()))
				ora.setAttribute("tempo",SPCoopCostanti.TIPO_TEMPO_LOCALE);
			else if(TipoOraRegistrazione.SINCRONIZZATO.equals(trasmissione.getTempo()))
				ora.setAttribute("tempo",SPCoopCostanti.TIPO_TEMPO_SPC);
			else
				ora.setAttribute("tempo",trasmissione.getTempoValue(this.factory));

		} catch(Exception e) {
			this.log.error("Creazione trasmissione non riuscita: "+e.getMessage(),e);
			throw new ProtocolException("Creazione trasmissione non riuscita: "+e.getMessage(),e);
		}   
	}


	public SOAPHeaderElement addTrasmissione(OpenSPCoop2Message message,Trasmissione trasmissione,boolean readQualifiedAttribute)throws ProtocolException{
		SOAPHeaderElement eGovHeaderOLD = null;
		SOAPHeaderElement eGovHeaderNEW = null;
		try{
			
			SPCoopBustaRawContent bustaElement = this.validazioneSintattica.getHeaderEGov(message.castAsSoap(),readQualifiedAttribute);
			if(bustaElement==null){
				throw new ProtocolException("Header eGov non esistente");
			}
			eGovHeaderOLD = bustaElement.getElement();
			if(eGovHeaderOLD==null){
				throw new ProtocolException("Header eGov non esistente");
			}

			OpenSPCoop2SoapMessage soapMsg = message.castAsSoap();
			
			// Creo nuovo header egov
			eGovHeaderNEW = 
				OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.SOAP_11,MessageRole.NONE).castAsSoap().
					getSOAPHeader().addHeaderElement(
						new QName(eGovHeaderOLD.getNamespaceURI(),eGovHeaderOLD.getLocalName(),eGovHeaderOLD.getPrefix()));
			
			eGovHeaderNEW.setActor(eGovHeaderOLD.getActor());
			eGovHeaderNEW.setMustUnderstand(eGovHeaderOLD.getMustUnderstand());
			// Aggiungo dichiarazioni di namespaces
			java.util.Iterator<?> namespaces = eGovHeaderOLD.getNamespacePrefixes();
			while(namespaces.hasNext()){
				String mNamespace = (String) namespaces.next();
				eGovHeaderNEW.addNamespaceDeclaration(mNamespace, eGovHeaderOLD.getNamespaceURI(mNamespace));
			}
			// Aggiungo attributi presenti nel vecchio nel nuovo
			java.util.Iterator<?> attributes = eGovHeaderOLD.getAllAttributes();
			while(attributes.hasNext()){
				javax.xml.soap.Name mAttribute = (javax.xml.soap.Name) attributes.next();
				eGovHeaderNEW.addAttribute(mAttribute, eGovHeaderOLD.getAttributeValue(mAttribute));
			}
			// Aggiungo figli presenti nel vecchio, nel nuovo
			java.util.Iterator<?> child = eGovHeaderOLD.getChildElements();
			while(child.hasNext()){
				Object oChild = child.next();
				if(oChild instanceof SOAPElement){
					SOAPElement mChild = (SOAPElement) oChild;
					eGovHeaderNEW.addChildElement(mChild);
				}
			}
			// Cerco eventuale lista trasmissioni
			child = eGovHeaderNEW.getChildElements();
			SOAPElement listaTrasmissioni = null;
			while(child.hasNext()){
				SOAPElement mChild = (SOAPElement) child.next();
				if("ListaTrasmissioni".equals(mChild.getLocalName())){
					listaTrasmissioni = mChild;
					break;
				}
			}
			if(listaTrasmissioni==null){
				listaTrasmissioni = eGovHeaderNEW.addChildElement("ListaTrasmissioni",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
			}
			// Aggiungo nuova trasmissione
			this.build_trasmissione(trasmissione, listaTrasmissioni);


			// NOTA: ho dovuto effettuare una eliminazione, e una ri-creazione, perche' non e' possibile modificare un HeaderElement
			// Rimozione header egov
			soapMsg.getSOAPHeader().removeChild(eGovHeaderOLD);

			// Imbustamento nuovo header
			return this.imbustamentoEGov(message, eGovHeaderNEW, RuoloMessaggio.RISPOSTA, null);

		} catch(Exception e) {
			this.log.error("addTrasmissione non riuscita: "+e.getMessage(),e);
			throw new ProtocolException("addTrasmissione non riuscita: "+e.getMessage(),e);
		}   
		finally{
			// *** GB ***
			if(this.validazioneSintattica!=null){
				this.validazioneSintattica.setHeaderSOAP(null);
			}
			eGovHeaderOLD = null;
			eGovHeaderNEW = null;
			// *** GB ***
		}
	}




	

	public SOAPHeaderElement imbustamento(OpenSPCoop2Message msg, Busta busta,
			RuoloMessaggio ruoloMessaggio,
			ProprietaManifestAttachments proprietaManifestAttachments)
			throws ProtocolException {
		if(busta.getProfiloDiCollaborazione() != null) {
			switch (busta.getProfiloDiCollaborazione()) {
			case ONEWAY:
				busta.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ONEWAY, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY);
				break;
			case SINCRONO:
				busta.setProfiloDiCollaborazione(ProfiloDiCollaborazione.SINCRONO, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO);
				break;
			case ASINCRONO_ASIMMETRICO:
				busta.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO);
				break;
			case ASINCRONO_SIMMETRICO:
				busta.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO);
				break;
			case UNKNOWN:
				break;
			}
		}
		
		if(busta.getTipoOraRegistrazione() != null) {
			switch (busta.getTipoOraRegistrazione()) {
			case LOCALE:
				busta.setTipoOraRegistrazione(TipoOraRegistrazione.LOCALE,SPCoopCostanti.TIPO_TEMPO_LOCALE);
				break;
			case SINCRONIZZATO:
				busta.setTipoOraRegistrazione(TipoOraRegistrazione.SINCRONIZZATO,SPCoopCostanti.TIPO_TEMPO_SPC);
				break;
			case UNKNOWN:
				break;
			}
		}
		
		if(busta.getInoltro() != null) {
			switch (busta.getInoltro()) {
			case CON_DUPLICATI:
				busta.setInoltro(Inoltro.CON_DUPLICATI, SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI);
				break;
			case SENZA_DUPLICATI:
				busta.setInoltro(Inoltro.SENZA_DUPLICATI, SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI);
				break;
			case UNKNOWN:
				break;
			}
		}
		
		Iterator<Riscontro> riscontri = busta.getListaRiscontri().iterator();
		while(riscontri.hasNext()){
			Riscontro riscontro = riscontri.next();
			if(riscontro.getTipoOraRegistrazione() == null) continue;
			if(riscontro.getTipoOraRegistrazione().equals(TipoOraRegistrazione.LOCALE)) 
				riscontro.setTipoOraRegistrazioneValue(SPCoopCostanti.TIPO_TEMPO_LOCALE);
			if(riscontro.getTipoOraRegistrazione().equals(TipoOraRegistrazione.SINCRONIZZATO)) 
				riscontro.setTipoOraRegistrazioneValue(SPCoopCostanti.TIPO_TEMPO_SPC);
		}
		
		Iterator<Trasmissione> trasmissioni = busta.getListaTrasmissioni().iterator();
		while(trasmissioni.hasNext()){
			Trasmissione trasmissione = trasmissioni.next();
			if(trasmissione.getTempo() == null) continue;
			
			if(trasmissione.getTempo().equals(TipoOraRegistrazione.LOCALE)) 
				trasmissione.setTempoValue(SPCoopCostanti.TIPO_TEMPO_LOCALE);
			else  
				trasmissione.setTempoValue(SPCoopCostanti.TIPO_TEMPO_SPC);
			 
		}
			
		return this.imbustamentoEGov(msg, busta, ruoloMessaggio, proprietaManifestAttachments);
	}

	public SOAPHeaderElement addTrasmissione(OpenSPCoop2Message message,
			Trasmissione trasmissione) throws ProtocolException {
		return this.addTrasmissione(message, trasmissione, false);
	}

}





