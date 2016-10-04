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



package org.openspcoop2.protocol.spcoop.builder;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.slf4j.Logger;
import org.w3c.dom.Node;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
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
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.spcoop.config.SPCoopProperties;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.utils.SPCoopUtils;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneSemantica;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneSintattica;
import org.openspcoop2.protocol.utils.IDSerialGenerator;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.serial.IDSerialGeneratorParameter;
import org.openspcoop2.utils.id.serial.IDSerialGeneratorType;
import org.openspcoop2.utils.xml.AbstractXMLUtils;


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
	
	private IProtocolFactory factory;
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

	public SPCoopImbustamento(IProtocolFactory factory) throws ProtocolException{
		this.factory = factory;
		this.log = factory.getLogger();
		this.spcoopProperties = SPCoopProperties.getInstance(this.log);
		
		this.tipiSoggetti = this.factory.createProtocolConfiguration().getTipiSoggetti();
		this.tipiServizi = this.factory.createProtocolConfiguration().getTipiServizi();
		
		this.validazioneSemantica = (SPCoopValidazioneSemantica) this.factory.createValidazioneSemantica();
		this.validazioneSintattica = (SPCoopValidazioneSintattica) this.factory.createValidazioneSintattica();
		
		this.xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
		
		this.traduttore = this.factory.createTraduttore();
		
		this.protocolManager = this.factory.createProtocolManager();
	}
	
	public IProtocolFactory getProtocolFactory() {
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
	 * @param isRichiesta Indicazione se si tratta della richiesta
	 * @return un oggetto String contenente l'identificativo secondo specifica eGov.
	 * 
	 */
	public String buildID(IState state, IDSoggetto idSoggetto, String idTransazione, Boolean isRichiesta) throws ProtocolException {

		
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
	public SOAPElement build_eGovHeader(OpenSPCoop2Message msg, Busta eGov) throws ProtocolException{ 
		return build_eGovHeader(msg,eGov, true, false);
	}
	public SOAPElement build_eGovHeader(OpenSPCoop2Message msg,Busta eGov,boolean verificaPresenzaElementiObbligatori) throws ProtocolException{ 
		return build_eGovHeader(msg,eGov, verificaPresenzaElementiObbligatori, false);
	}
	public SOAPElement build_eGovHeader(OpenSPCoop2Message msg,Busta eGov,boolean verificaPresenzaElementiObbligatori,boolean forzaValidazioneXSDElementiDisabilitata) throws ProtocolException{ 
		try{

			OpenSPCoop2Message messaggio = msg;
			if(messaggio==null){
				OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getMessageFactory();
				messaggio = mf.createMessage(SOAPVersion.SOAP11);
			}
				
				
			SOAPHeader hdr = messaggio.getSOAPHeader();
			if(hdr==null){
				hdr = messaggio.getSOAPPart().getEnvelope().addHeader(); 
			}
			QName name = new QName(SPCoopCostanti.NAMESPACE_EGOV, "Intestazione",SPCoopCostanti.PREFIX_EGOV);
			SOAPHeaderElement eGovHeader = messaggio.newSOAPHeaderElement(hdr, name);
						
			
			
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




	/**
	 * Metodo che si occupa di costruire un Manifest per gli Attachments 
	 * definito nella specifica eGov 
	 * 'http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/'.
	 * <p>
	 * Gli attachments da linkare, all'interno del Manifesto sono prelevati
	 * dall'oggetto <var>msg</var>.
	 *
	 * @param msg Messaggio su cui creare il manifesto
	 * @param isRichiesta Tipo di Busta
	 * 
	 */
	public OpenSPCoop2Message build_eGovManifest(OpenSPCoop2Message msg,boolean isRichiesta,
			ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{ 
		try{

			// Descrizione
			SOAPElement descrizione = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(SOAPVersion.SOAP11).getSOAPBody().addChildElement(new QName(SPCoopCostanti.NAMESPACE_EGOV, "Descrizione",SPCoopCostanti.PREFIX_EGOV));
			

			// Attachments
			java.util.Iterator<?> iter = msg.getAttachments();
			int attach = 1;
			while( iter.hasNext() ){
				Utilities.printFreeMemory("Imbustamento - Costruzione manifest att " + attach);
				AttachmentPart p = (AttachmentPart) iter.next();
				String contentID = msg.getContentID(p);
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
					
					if(isRichiesta){
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
					if(isRichiesta)
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
				byte [] body = SoapUtils.sbustamentoSOAPEnvelope(msg.getSOAPPart().getEnvelope());
				AttachmentPart ap = msg.createAttachmentPart();
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
					List<Node> listNode = SoapUtils.getNotEmptyChildNodes(msg.getSOAPPart().getEnvelope().getBody(), false);
					if(listNode!=null && listNode.size()>1){
						//System.out.println("MULTI ELEMENT: "+listNode.size());
						bodyWithMultiRootElement = true;
					}
				}
				if(bodyWithMultiRootElement){
					//System.out.println("OCTECT");
					ap.setContent(body, Costanti.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
				}else{
					//System.out.println("XML");
					Source streamSource = new DOMSource(this.xmlUtils.newElement(body));
					ap.setContent(streamSource, "text/xml");
				}
				ap.setContentId(msg.createContentID(SPCoopCostanti.NAMESPACE_EGOV));
				//ap.setContentLocation("provaOpenSPCoop"); // Test Sbustamento con ContentLocation
				msg.addAttachmentPart(ap);
				//isContent.close();
				// add OriginalMsg in Manifest
				// Costruzione DescrizioneMessaggio
				SOAPElement descrizioneMessaggio = descrizione.addChildElement(SPCoopCostanti.LOCAL_NAME_MANIFEST_EGOV_DESCRIZIONE_MESSAGGIO,
						SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				// Costruzione Riferimento
				SOAPElement riferimento = descrizioneMessaggio.addChildElement("Riferimento",SPCoopCostanti.PREFIX_EGOV,SPCoopCostanti.NAMESPACE_EGOV);
				riferimento.setAttribute("href","cid:" + msg.getContentID(ap)); // xsd:anyURI
				//riferimento.setAttribute("href",ap.getContentLocation()); // xsd:anyURI // Test Sbustamento con ContentLocation
				if(isRichiesta){
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
				if(isRichiesta)
					titolo.setValue(this.spcoopProperties.getRoleRichiestaManifest()); // xsd:string
				else
					titolo.setValue(this.spcoopProperties.getRoleRispostaManifest()); // xsd:string

			}
			Utilities.printFreeMemory("Imbustamento - Add Manifest in Body");
			// Add Manifest in Body
			msg.getSOAPBody().removeContents();
			msg.getSOAPBody().addChildElement(descrizione);


			return msg;

		} catch(Exception e) {
			this.log.error("Creazione Manifest degli attachments non riuscita: "+e.getMessage(),e);
			throw new ProtocolException("Creazione Manifest degli attachments non riuscita: "+e.getMessage(),e);
		}   
	}

	/**
	 * Effettua l'imbustamento eGov
	 *  
	 * @param msg Messaggio in cui deve essere aggiunto un header eGov.
	 * @param busta BustaEGov che contiene i dati necessari per la creazione dell'header
	 * 
	 */
	public void imbustamentoEGov(OpenSPCoop2Message msg,Busta busta,ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{	
		this.imbustamentoEGov(msg, busta, false, proprietaManifestAttachments);
	}


	/**
	 * Effettua l'imbustamento eGov
	 *  
	 * @param msg Messaggio in cui deve essere aggiunto un header eGov.
	 * @param busta BustaEGov che contiene i dati necessari per la creazione dell'header
	 * @param isRichiesta Tipo di Busta
	 * 
	 */
	public SOAPElement imbustamentoEGov(OpenSPCoop2Message msg,Busta busta,
			boolean isRichiesta,
			ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{	

		try{
			// add header eGov
			Utilities.printFreeMemory("Imbustamento - Creazione header eGov");
			SOAPHeaderElement headerEGovElement = (SOAPHeaderElement) this.build_eGovHeader(msg, busta);
			Utilities.printFreeMemory("Imbustamento - imbustamento");
			this.imbustamentoEGov(msg, headerEGovElement, isRichiesta, proprietaManifestAttachments);
			return headerEGovElement;
		}catch(Exception e){
			this.log.error("ImbustamentoEGov non riuscito: "+e.getMessage(),e);
			throw new ProtocolException("ImbustamentoEGov non riuscito: "+e.getMessage(),e);
		}
	}

	/**
	 * Effettua l'imbustamento eGov
	 *  
	 * @param msg Messaggio in cui deve essere aggiunto un header eGov.
	 * @param headerEGovElement BustaEGov che contiene i dati necessari per la creazione dell'header
	 * @param isRichiesta Tipo di Busta
	 * 
	 */
	public SOAPElement imbustamentoEGov(OpenSPCoop2Message msg, SOAPHeaderElement headerEGovElement,
			boolean isRichiesta,
			ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{	

		try{
			// Eliminazione XSI Types
			headerEGovElement = msg.cleanXSITypes(headerEGovElement);

			SOAPHeader hdr = msg.getSOAPHeader();
			if(hdr==null){
				hdr = msg.getSOAPPart().getEnvelope().addHeader(); 
			}

			msg.addHeaderElement(hdr, headerEGovElement);
			Utilities.printFreeMemory("Imbustamento -Header Aggiunto");
			// Gestione Manifest
			
			if (proprietaManifestAttachments!=null && proprietaManifestAttachments.isGestioneManifest() && msg.countAttachments()>0) {
				Utilities.printFreeMemory("Imbustamento - Gestione Manifest");
				this.build_eGovManifest(msg,isRichiesta,proprietaManifestAttachments);
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


	public SOAPElement addTrasmissione(OpenSPCoop2Message message,Trasmissione trasmissione,boolean readQualifiedAttribute)throws ProtocolException{
		SOAPHeaderElement eGovHeaderOLD = null;
		SOAPHeaderElement eGovHeaderNEW = null;
		try{
			
			eGovHeaderOLD = this.validazioneSintattica.getHeaderEGov((SOAPMessage) message,readQualifiedAttribute);
			if(eGovHeaderOLD==null){
				throw new ProtocolException("Header eGov non esistente");
			}

			// Creo nuovo header egov
			eGovHeaderNEW = 
				OpenSPCoop2MessageFactory.getMessageFactory().createMessage(SOAPVersion.SOAP11).getSOAPHeader().addHeaderElement(
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
			message.getSOAPHeader().removeChild(eGovHeaderOLD);

			// Imbustamento nuovo header
			return this.imbustamentoEGov(message, eGovHeaderNEW, false, null);

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










	/**----------------- Metodi per la costruzione di Buste SPCoop Errore -------------*/
//	/**
//	 * Passatogli un messaggio SPCoop (<var>busta</var>), ne costruisce un relativo 
//	 * Messaggio SPCoop Errore, invertendo mittente con destinatario e impostando altre proprieta' .
//	 * Il messaggio SPCoop errore costruito si riferisce ad un messaggio di errore causato
//	 * dalla validazione della busta ricevuta, nella quale sono stati riscontrati errori.
//	 *
//	 * @param eccezioni Eccezioni
//	 * @param busta Busta eGov che ha causato l'errore da far diventare una busta SPCoop Errore.
//	 * @param id_eGov identificativo eGov da associare alla busta di ritorno.
//	 * @return Una busta pronta ad essere usata per un messaggio SPCoop Errore
//	 * 
//	 */
//	public Busta buildSPCoopErrore_Validazione(List<Eccezione> eccezioni,Busta busta,String id_eGov,String tipoTempo){
//
//		return this.buildSPCoopErrore(eccezioni,busta,id_eGov,tipoTempo);
//	}

//	/**
//	 * Passatogli un messaggio SPCoop (<var>busta</var>), ne costruisce un relativo 
//	 * Messaggio SPCoop Errore, invertendo mittente con destinatario e impostando altre proprieta'.
//	 * Il messaggio SPCoop Errore costruito si riferisce ad un messaggio di errore causato
//	 * dal processamento del messaggio da parte della porta di dominio.
//	 *
//	 * @param eccezioni Eccezioni sulla quale creare un  messaggio SPCoop Errore.
//	 * @param busta Busta eGov che ha causato l'errore da far diventare una busta SPCoop Errore.
//	 * @param id_eGov identificativo eGov da associare alla busta di ritorno.
//	 * @return Una busta pronta ad essere usata per un messaggio SPCoop Errore
//	 * 
//	 */
//	public Busta buildSPCoopErrore_Processamento(List<Eccezione> eccezioni,Busta busta,String id_eGov,String tipoTempo){
//		return this.buildSPCoopErrore(eccezioni,busta,id_eGov,tipoTempo);
//	}

//	/**
//	 * Passatogli un messaggio SPCoop (<var>busta</var>), ne costruisce un relativo 
//	 * Messaggio SPCoop Errore, invertendo mittente con destinatario e impostando altre proprieta'.
//	 * Il messaggio SPCoop Errore costruito si riferisce ad un messaggio di errore causato
//	 * dal processamento del messaggio da parte della porta di dominio.
//	 *
//	 * @param ecc Eccezione sulla quale creare un  messaggio SPCoop Errore.
//	 * @param busta Busta eGov che ha causato l'errore da far diventare una busta SPCoop Errore.
//	 * @param id_eGov identificativo eGov da associare alla busta di ritorno.
//	 * @return Una busta pronta ad essere usata per un messaggio SPCoop Errore
//	 * 
//	 */
//	public Busta buildSPCoopErrore_Processamento(Eccezione ecc,Busta busta,String id_eGov,String tipoTempo){
//
//		Vector<Eccezione> eccs = new Vector<Eccezione>();
//		eccs.add(ecc);
//		return this.buildSPCoopErrore(eccs,busta,id_eGov,tipoTempo);
//	}

//	/**
//	 * Passatogli un messaggio SPCoop (<var>busta</var>), ne costruisce un relativo 
//	 * Messaggio SPCoop Errore, invertendo mittente con destinatario e impostando altre proprieta' 
//	 *
//	 * @param eccezioni Eccezione sulla quale creare un  messaggio SPCoop Errore.
//	 * @param busta Busta eGov che ha causato l'errore da far diventare una busta SPCoop Errore.
//	 * @param id_eGov identificativo eGov da associare alla busta di ritorno.
//	 * @return array di byte contenente il messaggio SPCoop Errore
//	 * 
//	 */
//	private Busta buildSPCoopErrore(List<Eccezione> eccezioni,Busta busta,String id_eGov,String tipoTempo){	
//
//		// Scambio mitt con dest
//		String tipoDest = busta.getTipoMittente();
//		String dest = busta.getMittente();
//		String indTdest = busta.getIndirizzoMittente();
//		busta.setTipoMittente(busta.getTipoDestinatario());
//		busta.setMittente(busta.getDestinatario());
//		busta.setIndirizzoMittente(busta.getIndirizzoDestinatario());
//		busta.setTipoDestinatario(tipoDest);
//		busta.setDestinatario(dest);
//		busta.setIndirizzoDestinatario(indTdest);
//
//		// Costruzione identificativo e riferimentoMessaggio
//		busta.setRiferimentoMessaggio(busta.getID());
//		busta.setID(id_eGov);
//
//		// Ora registrazione
//		busta.setOraRegistrazione(DateManager.getDate());
//		if(SPCoopCostanti.TIPO_TEMPO_LOCALE.equals(tipoTempo))
//			busta.setTipoOraRegistrazione(TipoOraRegistrazione.LOCALE, SPCoopCostanti.TIPO_TEMPO_LOCALE);
//		else
//			busta.setTipoOraRegistrazione(TipoOraRegistrazione.SINCRONIZZATO, SPCoopCostanti.TIPO_TEMPO_SPC);
//
//		// Elimino trasmissione della richiesta
//		while(busta.sizeListaTrasmissioni() != 0){
//			busta.removeTrasmissione(0);
//		}
//
//		// Rimozione Eventuali vecchie eccezioni della richiesta
//		while(busta.sizeListaEccezioni() != 0){
//			busta.removeEccezione(0);
//		}
//
//		// Rimozione Eventuali vecchi riscontri della richiesta
//		while(busta.sizeListaRiscontri() != 0){
//			busta.removeRiscontro(0);
//		}
//
//		// Aggiungo eccezioni
//		while(eccezioni.size()>0){
//			Eccezione e = eccezioni.remove(0);
//			busta.addEccezione(e);
//		}
//
//		// Validazione XSD: non devo produrre valori non validabili.
//		if(this.protocolManager.isGenerazioneElementiNonValidabiliRispettoXSD()==false){
//			
//			// Profilo di Collaborazione
//			if(busta.getProfiloDiCollaborazione() != null) {
//				if(ProfiloDiCollaborazione.UNKNOWN.equals(busta.getProfiloDiCollaborazione())){
//					busta.setProfiloDiCollaborazione(ProfiloDiCollaborazione.UNKNOWN, null);
//					busta.setTipoServizioCorrelato(null);
//					busta.setServizioCorrelato(null);
//				}
//				else{
//					if(busta.getTipoServizioCorrelato()!=null){
//						if(this.tipiServizi.contains(busta.getTipoServizioCorrelato()) == false){
//							busta.setTipoServizioCorrelato(null);
//						}
//					}
//				}
//			} 
//			// Messaggio.riferimentoMessaggio
//			if(busta.getRiferimentoMessaggio()!=null){
//				if(this.validazioneSemantica.validazioneID(busta.getRiferimentoMessaggio())==false){
//					busta.setRiferimentoMessaggio(null);
//				}
//			}
//			
//			// ProfiloTrasmissione
//			if(busta.getInoltro()!=null){
//				if( busta.getInoltro().equals(Inoltro.UNKNOWN)){
//					busta.setInoltro(Inoltro.UNKNOWN, null);
//				}
//			}
//
//		}
//		
//		return busta;
//	}












//
//	/**----------------- Metodi per la costruzione di MessaggiSoap da utilizzare con Buste SPCoop Errore -------------*/
//	/**
//	 * Metodo che si occupa di ritornare un SOAPFault Errore di Processamento SPCoop 
//	 *
//	 * @return un Message pronto per essere usato con un MessaggioSPCoopErrore, 
//	 *         in caso di costruzione con successo, null altrimenti.
//	 * 
//	 */
//	public OpenSPCoop2Message buildSoapMsgSPCoopErrore_Validazione()  {
//		return this.buildSoapMsgSPCoopErrore(true,null);	
//	}

//	/**
//	 * Metodo che si occupa di ritornare un SOAPFault Errore di Validazione SPCoop 
//	 *
//	 * @param dettaglioEccezione Dettaglio dell'eccezione da generare
//	 * @return un Message pronto per essere usato con un MessaggioSPCoopErrore, 
//	 *         in caso di costruzione con successo, null altrimenti.
//	 * 
//	 */
//	public OpenSPCoop2Message buildSoapMsgSPCoopErrore_Processamento(DettaglioEccezione dettaglioEccezione){
//		return this.buildSoapMsgSPCoopErrore(false,dettaglioEccezione);	
//	}

//	/**
//	 * Metodo che si occupa di ritornare un SOAPFault di errore SPCoop.
//	 *
//	 * @param erroreValidazione true se l'errore si e' verificato nella validazione della busta, false se si e' verificato nel processamento
//	 * @param dettaglioEccezione dettaglio eccezione
//	 * @return un Message pronto per essere usato con un MessaggioSPCoopErrore, 
//	 *         in caso di costruzione con successo, null altrimenti.
//	 * 
//	 */
//	public OpenSPCoop2Message buildSoapMsgSPCoopErrore(boolean erroreValidazione,DettaglioEccezione dettaglioEccezione)  {
//
//		try{
//
//			OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getMessageFactory();
//			OpenSPCoop2Message msg = mf.createMessage(SOAPVersion.SOAP11);
//			SOAPEnvelope env = msg.getSOAPPart().getEnvelope();
//			env.addNamespaceDeclaration("xsi","http://www.w3.org/2001/XMLSchema-instance");
//
//			// Creo SoapFault SPCoop
//			SOAPBody bdy = env.getBody();
//			SOAPFault fault = bdy.addFault();
//
//			if(erroreValidazione){
//				fault.setFaultString(SPCoopCostanti.FAULT_STRING_VALIDAZIONE);
//				fault.setFaultCode(env.createName("Client","soap",SOAPConstants.URI_NS_SOAP_ENVELOPE)); //Costanti.FAULT_CODE_VALIDAZIONE_SPCOOP
//			}else{
//				fault.setFaultString(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO);
//				fault.setFaultCode(env.createName("Server","soap",SOAPConstants.URI_NS_SOAP_ENVELOPE)); 
//			}
//			
//			if(!erroreValidazione && dettaglioEccezione!=null){
//				Detail d = fault.addDetail();
//				Element e = this.xmlUtils.newElement(org.openspcoop2.core.eccezione.details.utils.XMLUtils.generateDettaglioEccezione(dettaglioEccezione));
//				d.appendChild(d.getOwnerDocument().importNode(e, true));
//			}
//			
//			return msg;
//
//		} catch(Exception e) {
//			this.log.error("Build msgSPCoopErrore non riuscito: " + e.getMessage());
//			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(SOAPVersion.SOAP11, "ErroreProcessamento");
//		}
//
//
//	}

	
	
	
	
	
	
	
	
	
	/** ------------------- Metodi che generano un SOAPFault egov ---------------- 
	 * @throws ProtocolException */
	
//    public String transformFaultMsg(ErroreIntegrazione errore,String msg) throws ProtocolException{
//    	
//    	if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche()){
//    			// 	Mi appoggio a questa utility
//    			ProprietaErroreApplicativo pErroreApplicativo = new ProprietaErroreApplicativo();
//    			pErroreApplicativo.setFaultAsGenericCode(true);
//    			return pErroreApplicativo.transformFaultMsg(errore, this.factory);
//    	} else {
//    		return msg;
//    	}
//    	    	
//    }
    
//    public String transformFaultMsg(CodiceErroreCooperazione code,String msg){
//    	
//    	if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche()){
//    		if(code.equals(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO)){
//    			// EGOV_IT_300, errore di processamento.
//    			// Lascio intatto solo il msg di ServizioApplicativo non disponibile
//    			if("Servizio Applicativo non disponibile".equals(msg)){
//    				return msg;
//    			}else{
//    				return SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SENZA_CODICE;
//    			}
//    		}
//    	}
//    	return msg; 	
//    }
// 
 
//	public OpenSPCoop2Message buildSPCoopSoapFault_processamento(IDSoggetto identitaPdD,String modulo,CodiceErroreIntegrazione codErrore,Exception eProcessamento){
//		return buildSPCoopSoapFault_processamento(identitaPdD, modulo, codErrore, null, eProcessamento);
//	}
//	public OpenSPCoop2Message buildSPCoopSoapFault_processamento(IDSoggetto identitaPdD,String modulo,CodiceErroreIntegrazione codErrore,String prefixMsgErrore,Exception eProcessamento){
//		try{
//			DettaglioEccezione dettaglioEccezione = null;
//			if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneProcessamento()){
//				String msgErrore = null;
//				if(prefixMsgErrore==null){
//					if(eProcessamento.getMessage()!=null)
//						msgErrore = "ErroreProcessamento: "+eProcessamento.getMessage();
//					else
//						msgErrore ="ErroreProcessamento: "+eProcessamento.toString();
//				}else{
//					if(eProcessamento.getMessage()!=null)
//						msgErrore = prefixMsgErrore+eProcessamento.getMessage();
//					else
//						msgErrore = prefixMsgErrore+eProcessamento.toString();
//				}
//				dettaglioEccezione = buildDettaglioEccezione(identitaPdD, modulo, codErrore, transformFaultMsg(codErrore,msgErrore));
//				gestioneDettaglioEccezioneProcessamento(eProcessamento, dettaglioEccezione);
//			}
//			QName faultCode = new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, CostantiProtocollo.FAULT_CODE_SERVER, "soap"); 
//			return OpenSPCoop2MessageFactory.getMessageFactory().createMessage(SOAPVersion.SOAP11, SoapUtils.build_Soap_Fault(SOAPVersion.SOAP11,SPCoopCostanti.FAULT_STRING_PROCESSAMENTO,null,
//					faultCode, 
//					this.xmlUtils.newElement(org.openspcoop2.core.eccezione.details.utils.XMLUtils.generateDettaglioEccezione(dettaglioEccezione)), 
//					this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneProcessamento()));
//		}catch(Exception e){
//			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(SOAPVersion.SOAP11, "Errore buildSPCoopSoapFault_processamento(exception): "+e.getMessage());
//		}
//	}
	
//	public OpenSPCoop2Message buildSPCoopSoapFault_processamento(IDSoggetto identitaPdD,String modulo,CodiceErroreIntegrazione codErrore,String msgErrore){
//		try{
//			DettaglioEccezione dettaglioEccezione = null;
//			if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneProcessamento()){
//				dettaglioEccezione = buildDettaglioEccezione(identitaPdD, modulo, codErrore, transformFaultMsg(codErrore,msgErrore));
//			}
//			QName faultCode = new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE,CostantiProtocollo.FAULT_CODE_SERVER,"soap"); 
//			return OpenSPCoop2MessageFactory.getMessageFactory().createMessage(SOAPVersion.SOAP11,SoapUtils.build_Soap_Fault(SOAPVersion.SOAP11,SPCoopCostanti.FAULT_STRING_PROCESSAMENTO,null,
//					faultCode, 
//					this.xmlUtils.newElement(org.openspcoop2.core.eccezione.details.utils.XMLUtils.generateDettaglioEccezione(dettaglioEccezione)), 
//					this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneProcessamento()));
//		}catch(Exception e){
//			if(this.log!=null)
//				this.log.error("Errore buildSPCoopSoapFault_processamento: "+e.getMessage(),e);
//			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(SOAPVersion.SOAP11, "Errore buildSPCoopSoapFault_processamento: "+e.getMessage());
//		}
//	}
//	
//	public OpenSPCoop2Message buildSPCoopSoapFault_intestazione(IDSoggetto identitaPdD,String modulo, CodiceErroreCooperazione codErrore,SubCodiceErrore subCodErrore,String msgErrore){
//		try{
//			DettaglioEccezione dettaglioEccezione = null;
//			if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneValidazione()){
//				dettaglioEccezione = buildDettaglioEccezione(identitaPdD, modulo, codErrore,subCodErrore, transformFaultMsg(codErrore,msgErrore));
//			}
//			QName faultCode = new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE,CostantiProtocollo.FAULT_CODE_CLIENT,"soap"); 
//			return OpenSPCoop2MessageFactory.getMessageFactory().createMessage(SOAPVersion.SOAP11,
//					SoapUtils.build_Soap_Fault(SOAPVersion.SOAP11,SPCoopCostanti.FAULT_STRING_VALIDAZIONE,null,
//					faultCode, 
//					this.xmlUtils.newElement(org.openspcoop2.core.eccezione.details.utils.XMLUtils.generateDettaglioEccezione(dettaglioEccezione)), 
//					this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneValidazione()));
//		}catch(Exception e){
//			if(this.log!=null)
//				this.log.error("Errore buildSPCoopSoapFault_intestazione: "+e.getMessage(),e);
//			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(SOAPVersion.SOAP11,"Errore buildSPCoopSoapFault_intestazione: "+e.getMessage());
//		}
//	}
	
	
	
	
	
	
	







	
	
	
	
	/** ------------------- Metodi che generano un DettaglioEccezione ---------------- 
	 * @throws ProtocolException */
//	public DettaglioEccezione buildDettaglioEccezione(IDSoggetto identitaPdD, String modulo, CodiceErroreCooperazione codErrore,SubCodiceErrore subCodErrore, String msgErrore) throws ProtocolException{
//		return buildDettaglioEccezione(identitaPdD, modulo, this.traduttore.toString(codErrore,subCodErrore), msgErrore);
//	}
//	public DettaglioEccezione buildDettaglioEccezione(IDSoggetto identitaPdD, String modulo, CodiceErroreIntegrazione codErrore, String msgErrore){
//		return buildDettaglioEccezione(identitaPdD, modulo, codErrore.toString(null,this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche()), msgErrore);
//	}
//	
//	public DettaglioEccezione buildDettaglioEccezione(IDSoggetto identitaPdD,String modulo,String codErrore,String msgErrore){
//		DettaglioEccezione dettaglioEccezione = new DettaglioEccezione();
//		dettaglioEccezione.setIdentificativoPorta(identitaPdD.getCodicePorta());
//		dettaglioEccezione.setIdentificativoFunzione(modulo);
//		dettaglioEccezione.setOraRegistrazione(DateManager.getDate());
//		org.openspcoop2.core.eccezione.details.Eccezione eccezione = new org.openspcoop2.core.eccezione.details.Eccezione();
//		eccezione.setCodice(codErrore);
//		eccezione.setDescrizione(msgErrore);
//		if(dettaglioEccezione.getEccezioni()==null){
//			dettaglioEccezione.setEccezioni(new Eccezioni());
//		}
//		dettaglioEccezione.getEccezioni().addEccezione(eccezione);
//		return dettaglioEccezione;
//	}
//	
//	// METODO CHIAMATO SOLO DALLA PDD EROGATORE PER GENERARE IL DETTAGLIO DI UNA BUSTA SPCOOP ERRORE
//	public DettaglioEccezione buildDettaglioEccezioneFromBusta(IDSoggetto identitaPdD,String modulo,
//			String servizioApplicativoErogatore,
//			Busta busta,
//			Exception eProcessamento) throws ProtocolException{
//		
//		DettaglioEccezione dettaglioEccezione = new DettaglioEccezione();
//		
//		// info generali
//		dettaglioEccezione.setOraRegistrazione(DateManager.getDate());
//		dettaglioEccezione.setIdentificativoPorta(identitaPdD.getCodicePorta());
//		dettaglioEccezione.setIdentificativoFunzione(modulo);
//		
//		// eccezioni buste
//		for (int i = 0; i < busta.sizeListaEccezioni(); i++) {
//			
//			Eccezione e = busta.getEccezione(i);
//				
//			org.openspcoop2.core.eccezione.details.Eccezione eccezione = new org.openspcoop2.core.eccezione.details.Eccezione();
//			if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche()){
//				eccezione.setCodice(e.getCodiceEccezioneValue(this.factory));
//				eccezione.setDescrizione(this.transformFaultMsg(e.getCodiceEccezione(), e.getPosizione()));
//				eccezione.setRilevanza(this.traduttore.toString(e.getRilevanza()));
//				eccezione.setContestoCodifica(this.traduttore.toString(ContestoCodificaEccezione.PROCESSAMENTO));
//			}
//			else{
//				eccezione.setCodice(e.getCodiceEccezioneValue(this.factory));
//				eccezione.setDescrizione(e.getPosizione());
//				eccezione.setRilevanza(this.traduttore.toString(e.getRilevanza()));
//				eccezione.setContestoCodifica(this.traduttore.toString(e.getContestoCodifica()));
//			}
//			
//			if(dettaglioEccezione.getEccezioni()==null){
//				dettaglioEccezione.setEccezioni(new Eccezioni());
//			}
//			dettaglioEccezione.getEccezioni().addEccezione(eccezione);
//		}
//		
//		// dettagli
//		if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche()==false){
//			if(servizioApplicativoErogatore!=null){
//				Dettaglio detail = new Dettaglio();
//				detail.setTipo("servizioApplicativo");
//				detail.setBase(servizioApplicativoErogatore);
//				if(dettaglioEccezione.getDettagli()==null){
//					dettaglioEccezione.setDettagli(new Dettagli());
//				}
//				dettaglioEccezione.getDettagli().addDettaglio(detail);
//			}	
//		}
//		gestioneDettaglioEccezioneProcessamento(eProcessamento, dettaglioEccezione);
//		
//		return dettaglioEccezione;
//	}
//	public void gestioneDettaglioEccezioneIntegrazione(Exception eProcessamento,DettaglioEccezione dettaglioEccezione,boolean generaInformazioniGeneriche){
//		gestioneDettaglioEccezioneProcessamento_engine(eProcessamento, dettaglioEccezione, 
//				this.protocolManager.isGenerazioneDetailsSOAPFaultIntegrationeConStackTrace(),
//				generaInformazioniGeneriche);
//	}
//	public void gestioneDettaglioEccezioneProcessamento(Exception eProcessamento,DettaglioEccezione dettaglioEccezione){
//		gestioneDettaglioEccezioneProcessamento_engine(eProcessamento, dettaglioEccezione, 
//				this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConStackTrace(),
//				this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche());
//	}
//	private void gestioneDettaglioEccezioneProcessamento_engine(Exception eProcessamento,DettaglioEccezione dettaglioEccezione,
//			boolean generaStackTrace,boolean generaInformazioniGeneriche){
//		if(eProcessamento!=null){
//						
//			if(generaInformazioniGeneriche){
//				
//				Dettaglio detail = new Dettaglio();
//				detail.setTipo("causa");
//				
//				String msg = null;
//				if(eProcessamento.getMessage()!=null){
//					msg = eProcessamento.getMessage();
//				}else{
//					msg = eProcessamento.toString();
//				}
//				//System.out.println("MESSAGE ["+msg+"]");
//				// faccio viaggiare solo le informazioni che ritengo pubbliche
//				if("Connection refused".equals(msg)){
//					msg = "Connection refused";
//					detail.setBase(msg);
//					if(dettaglioEccezione.getDettagli()==null){
//						dettaglioEccezione.setDettagli(new Dettagli());
//					}
//					dettaglioEccezione.getDettagli().addDettaglio(detail);			
//				}else if("Read timed out".equals(msg)){
//					msg = "Read timed out";
//					detail.setBase(msg);
//					if(dettaglioEccezione.getDettagli()==null){
//						dettaglioEccezione.setDettagli(new Dettagli());
//					}
//					dettaglioEccezione.getDettagli().addDettaglio(detail);	
//				}else if("connect timed out".equals(msg)){
//					msg = "Connect timed out";
//					detail.setBase(msg);
//					if(dettaglioEccezione.getDettagli()==null){
//						dettaglioEccezione.setDettagli(new Dettagli());
//					}
//					dettaglioEccezione.getDettagli().addDettaglio(detail);	
//				}
//				
//			}else{
//				
//				Dettaglio detail = new Dettaglio();
//				detail.setTipo("causa");
//				
//				if(eProcessamento.getMessage()!=null)
//					detail.setBase(eProcessamento.getMessage());
//				else
//					detail.setBase(eProcessamento.toString());
//				if(dettaglioEccezione.getDettagli()==null){
//					dettaglioEccezione.setDettagli(new Dettagli());
//				}
//				dettaglioEccezione.getDettagli().addDettaglio(detail);
//				
//				if(eProcessamento.getCause()!=null){
//					gestioneDettaglioEccezioneProcessamento_engine_InnerException(eProcessamento.getCause(), dettaglioEccezione);
//				}
//			
//				if(generaStackTrace){
//					Dettaglio detailStackTrace = new Dettaglio();
//					detailStackTrace.setTipo("stackTrace");
//					ByteArrayOutputStream bout = new ByteArrayOutputStream();
//					PrintWriter pWriter = new PrintWriter(bout);
//					eProcessamento.printStackTrace(pWriter);
//					try{
//						bout.flush();
//						pWriter.flush();
//						pWriter.close();
//						bout.close();
//					}catch(Exception eClose){
//						System.err.println("ERRORE buildEccezioneProcessamentoFromBusta: "+eClose.getMessage());
//					}
//					detailStackTrace.setBase(bout.toString());
//					if(dettaglioEccezione.getDettagli()==null){
//						dettaglioEccezione.setDettagli(new Dettagli());
//					}
//					dettaglioEccezione.getDettagli().addDettaglio(detailStackTrace);
//				}
//			}
//		}
//	}
//	private void gestioneDettaglioEccezioneProcessamento_engine_InnerException(Throwable e,DettaglioEccezione dettaglioEccezione){
//		if(e!=null){
//			Dettaglio detail = new Dettaglio();
//			detail.setTipo("causato da");
//			if(e.getMessage()!=null)
//				detail.setBase(e.getMessage());
//			else
//				detail.setBase(e.toString());
//			if(dettaglioEccezione.getDettagli()==null){
//				dettaglioEccezione.setDettagli(new Dettagli());
//			}
//			dettaglioEccezione.getDettagli().addDettaglio(detail);
//			
//			if(e.getCause()!=null){
//				gestioneDettaglioEccezioneProcessamento_engine_InnerException(e.getCause(), dettaglioEccezione);
//			}
//		}
//	}
//	
	
	
	
	
	
	
	
	
	
	
	/** ------------------- Metodi che gestiscono il bug 131 ---------------- */
//	
//	public void gestioneListaEccezioniMessaggioSPCoopErroreProcessamento(Busta busta){
//		
//		if(!this.protocolManager.isGenerazioneListaEccezioniErroreProcessamento()){
//		
//			while( busta.sizeListaEccezioni() > 0 ){
//				
//				// Fix Bug 131: eccezione di processamento
//				@SuppressWarnings("unused")
//				Eccezione e = busta.removeEccezione(0);
//				// Fix Bug 131: eccezione di processamento
//				//System.out.println("ELIMINO ECCEZIONE DI PROCESSAMENTO: "+e.getPosizione());
//				
//			}
//		}
//		
//	}













	/** ------------------- Utility di eraser type su un messaggio Soap. ---------------- */

	/**
	 * Metodo che si occupa di effettuare l'eliminazione degli xsd:type sull'header eGov.
	 *
	 * @deprecated  utility che elimina gli xsd type
	 * @param patch Xml su cui effettuare la pulizia dell'header eGov.
	 * @return String contenente un xml 'pulito'.
	 * 
	 */
	@Deprecated  public String eraserType(String patch) throws ProtocolException{

		try{

			StringBuffer soapEnvelopePatch = new StringBuffer();
			int start = patch.indexOf("<eGov_IT:Intestazione");
			int end =  patch.indexOf("</eGov_IT:Intestazione>") + "</eGov_IT:Intestazione>".length();

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
			this.log.error("ImbustamentoEGov.eraserType_byte non riuscito: "+e.getMessage(),e);
			throw new ProtocolException("ImbustamentoEGov.eraserType_byte non riuscito: "+e.getMessage(),e);
		}
	}



	/**
	 * Metodo che si occupa di effettuare l'eliminazione degli xsd:type sull'header eGov.
	 *
	 * @deprecated  utility che elimina gli xsd type
	 * @param xml Xml su cui effettuare la pulizia dell'header eGov.
	 * @return byte[] contenente un xml 'pulito'.
	 * 
	 */
	@Deprecated  public byte[] eraserType(byte[] xml) throws ProtocolException{

		ByteArrayOutputStream cleanEGov = null;
		try{
			String header = new String(xml);
			int start = header.indexOf("<eGov_IT:Intestazione");
			int end =  header.indexOf("</eGov_IT:Intestazione>") + ("</eGov_IT:Intestazione>").length();
			if(start == -1)
				return null;
			if(end == -1)
				return null;
			if(end <= start)
				return null;

			String eraserString = " xsi:type=\"xsd:string\"";
			String eraserDate = " xsi:type=\"xsd:dateTime\"";


			cleanEGov = new ByteArrayOutputStream();

			// Parte fino all'header della busta
			for(int i=0; i<start ; i++)
				cleanEGov.write(xml[i]);

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

					cleanEGov.write(xml[i]);
					i++;

				}else{
					cleanEGov.write(xml[i]);
					i++;
				}

			}


			// Parte dopol'header della busta
			for(int i=end; i<xml.length ; i++)
				cleanEGov.write(xml[i]);


			byte [] cleanBytes = cleanEGov.toByteArray();
			cleanEGov.close();
			return cleanBytes;

		} catch(Exception e) {
			try{
				if(cleanEGov!=null)
					cleanEGov.close();
			}catch(Exception eis){}
			this.log.error("ImbustamentoEGov.eraserType_byte non riuscito: "+e.getMessage(),e);
			throw new ProtocolException("ImbustamentoEGov.eraserType_byte non riuscito: "+e.getMessage(),e);
		}
	}

	

	public SOAPElement imbustamento(OpenSPCoop2Message msg, Busta busta,
			boolean isRichiesta,
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
			
		return this.imbustamentoEGov(msg, busta, isRichiesta, proprietaManifestAttachments);
	}

	public SOAPElement addTrasmissione(OpenSPCoop2Message message,
			Trasmissione trasmissione) throws ProtocolException {
		return this.addTrasmissione(message, trasmissione, false);
	}

}





