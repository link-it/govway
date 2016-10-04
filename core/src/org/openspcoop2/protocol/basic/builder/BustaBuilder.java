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

package org.openspcoop2.protocol.basic.builder;

//import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.xml.namespace.QName;
//import javax.xml.datatype.DatatypeConfigurationException;
//import javax.xml.datatype.DatatypeFactory;
//import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPFaultCode;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.EccezioneProtocolloBuilderParameters;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.UniversallyUniqueIdentifierGenerator;
import org.openspcoop2.utils.xml.AbstractXMLUtils;

/**	
 * BustaBuilder
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BustaBuilder implements org.openspcoop2.protocol.sdk.builder.IBustaBuilder {

	protected Logger log;
	protected IProtocolFactory protocolFactory;
	protected OpenSPCoop2MessageFactory msgFactory;
	protected AbstractXMLUtils xmlUtils;
	protected ITraduttore traduttore;
	protected IErroreApplicativoBuilder erroreApplicativoBuilder = null;
		
	public BustaBuilder(IProtocolFactory factory) throws ProtocolException{
		this.log = factory.getLogger();
		this.protocolFactory = factory;
		this.msgFactory = OpenSPCoop2MessageFactory.getMessageFactory();
		this.xmlUtils = XMLUtils.getInstance();
		this.traduttore = this.protocolFactory.createTraduttore();
		this.erroreApplicativoBuilder = this.protocolFactory.createErroreApplicativoBuilder();
	}
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}

	private final static String dateformatPattern = "yyyyMMddHHmmssSSS"; // utile per il filtro duplicati
	private final static UniversallyUniqueIdentifierGenerator uuidGenerator = new UniversallyUniqueIdentifierGenerator();
	
	@Override
	public String newID(IState state, IDSoggetto idSoggetto, String idTransazione, Boolean isRichiesta) throws ProtocolException {
		return newID(state, idSoggetto, idTransazione, isRichiesta, true);
	}
	public String newID(IState state, IDSoggetto idSoggetto, String idTransazione, Boolean isRichiesta, boolean generateIDasUUID) throws ProtocolException {
		
		if(generateIDasUUID){
			
			try{
				synchronized (uuidGenerator) {
					return uuidGenerator.newID().getAsString();	
				}
			}catch(Exception e){
				throw new ProtocolException(e.getMessage());
			}
			
		}
		else{
		
			String id = idTransazione;
			
	//		try {
	//			XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
	//			id = id + "_T_" + cal.getSecond() + cal.getMillisecond();
	//		} catch (DatatypeConfigurationException e) {
	//			throw new ProtocolException(e);
	//		}
			
			Date now = DateManager.getDate();
			
			// NOTA: La data nell'identificativo e' utile quando l'id viene usato in un filtro duplicati
			// 		 Per avere un insieme di identificativi in ordine raggruppate per date. (aiuta l'indice sulla base dati)
			//		 Essendo questa una classe di base si preferisce aggiungere la data. 
			//		 Se non la si vuole nel protocollo trasparente reimplementare questo metodo nel protocollo trasparente eliminando la data
			//		 Anche il metodo extractDate dovra' a quel punto essere reimplementato ritornando null
			
			SimpleDateFormat dateFormat = new SimpleDateFormat(dateformatPattern); // SimpleDateFormat non e' thread-safe
			
			if(isRichiesta)
	//			return id+ "-request";
				return dateFormat.format(now)+"-"+id;
			else
				return dateFormat.format(now)+"-"+id + "-response";
			
		}
	}

	@Override
	public Date extractDateFromID(String id) throws ProtocolException {
		return this.extractDateFromID(id, true);
	}
	public Date extractDateFromID(String id, boolean generateIDasUUID) throws ProtocolException {
		if(id==null){
			throw new ProtocolException("ID non fornito");
		}
		if(generateIDasUUID == false){
			if(id.contains("-")==false){
				throw new ProtocolException("ID fornito ["+id+"] non e' corretto (missing '-')");
			}
			String [] split = id.split("-");
			
			SimpleDateFormat dateFormat = new SimpleDateFormat(dateformatPattern); // SimpleDateFormat non e' thread-safe
			
			try{
				return dateFormat.parse(split[0].trim());
			}catch(Exception e){
				throw new ProtocolException("ID fornito ["+id+"] non e' corretto: "+e.getMessage());
			}
		}
		else{
			return null;
		}
	}
	
	@Override
	public SOAPElement toElement(Busta busta, boolean isRichiesta)
			throws ProtocolException {
		return null;
	}

	@Override
	public String toString(Busta busta, boolean isRichiesta)
			throws ProtocolException {
		return null;
	}

	@Override
	public byte[] toByteArray(Busta busta, boolean isRichiesta)
			throws ProtocolException {
		return null;
	}
	
	@Override
	public SOAPElement imbustamento(IState state, OpenSPCoop2Message msg, Busta busta,
			boolean isRichiesta,
			ProprietaManifestAttachments proprietaManifestAttachments)
			throws ProtocolException {
		if(busta.getProfiloDiCollaborazione() != null) {
			switch (busta.getProfiloDiCollaborazione()) {
			case ONEWAY:
				busta.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ONEWAY, ProfiloDiCollaborazione.ONEWAY.getEngineValue());
				break;
			case SINCRONO:
				busta.setProfiloDiCollaborazione(ProfiloDiCollaborazione.SINCRONO, ProfiloDiCollaborazione.SINCRONO.getEngineValue());
				break;
			case ASINCRONO_ASIMMETRICO:
				busta.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.getEngineValue());
				break;
			case ASINCRONO_SIMMETRICO:
				busta.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.getEngineValue());
				break;
			case UNKNOWN:
				break;
			}
		}
		
		if(busta.getTipoOraRegistrazione() != null) {
			switch (busta.getTipoOraRegistrazione()) {
			case LOCALE:
				busta.setTipoOraRegistrazione(TipoOraRegistrazione.LOCALE,Costanti.TIPO_TEMPO_LOCALE);
				break;
			case SINCRONIZZATO:
				busta.setTipoOraRegistrazione(TipoOraRegistrazione.SINCRONIZZATO,Costanti.TIPO_TEMPO_SINCRONIZZATO);
				break;
			case UNKNOWN:
				break;
			}
		}
		
		if(busta.getInoltro() != null) {
			switch (busta.getInoltro()) {
			case CON_DUPLICATI:
				busta.setInoltro(Inoltro.CON_DUPLICATI, Costanti.PROFILO_TRASMISSIONE_CON_DUPLICATI);
				break;
			case SENZA_DUPLICATI:
				busta.setInoltro(Inoltro.SENZA_DUPLICATI, Costanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI);
				break;
			case UNKNOWN:
				break;
			}
		}
		
		Iterator<Riscontro> riscontri = busta.getListaRiscontri().iterator();
		while(riscontri.hasNext()){
			Riscontro riscontro = riscontri.next();
			if(riscontro.getTipoOraRegistrazione() == null) 
				continue;
			if(riscontro.getTipoOraRegistrazione().equals(TipoOraRegistrazione.LOCALE)) 
				riscontro.setTipoOraRegistrazioneValue(Costanti.TIPO_TEMPO_LOCALE);
			if(riscontro.getTipoOraRegistrazione().equals(TipoOraRegistrazione.SINCRONIZZATO)) 
				riscontro.setTipoOraRegistrazioneValue(Costanti.TIPO_TEMPO_SINCRONIZZATO);
		}
		
		Iterator<Trasmissione> trasmissioni = busta.getListaTrasmissioni().iterator();
		while(trasmissioni.hasNext()){
			Trasmissione trasmissione = trasmissioni.next();
			if(trasmissione.getTempo() == null) continue;
			
			if(trasmissione.getTempo().equals(TipoOraRegistrazione.LOCALE)) 
				trasmissione.setTempoValue(Costanti.TIPO_TEMPO_LOCALE);
			else  
				trasmissione.setTempoValue(Costanti.TIPO_TEMPO_SINCRONIZZATO);
			 
		}
		
		return null;
	}

	@Override
	public SOAPElement addTrasmissione(OpenSPCoop2Message message,
			Trasmissione trasmissione) throws ProtocolException {
		return null;
	}

	@Override
	public SOAPElement sbustamento(IState state, OpenSPCoop2Message msg, Busta busta,
			boolean isRichiesta, ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException {
		
		return null;
		
	}
	
	
	protected void addEccezioniInFault(OpenSPCoop2Message msg, Busta busta, boolean ignoraEccezioniNonGravi) throws ProtocolException{
		SOAPFault f = null;
		try{
			if(msg.getSOAPBody()!=null){
				if(msg.getSOAPBody().hasFault()==false){
					return;
				}
				f = msg.getSOAPBody().getFault();
			}
		
		
			EccezioneProtocolloBuilderParameters params = new EccezioneProtocolloBuilderParameters();
			
			// Soggetto Produce Eccezione == Dominio
			IDSoggetto soggettoProduceEccezione = new IDSoggetto(busta.getTipoMittente(),busta.getMittente(),busta.getIdentificativoPortaMittente());
			params.setSoggettoProduceEccezione(soggettoProduceEccezione);
			params.setDominioPorta(soggettoProduceEccezione);
			
			// Mittente
			IDSoggetto idSoggettoMittente = new IDSoggetto(busta.getTipoDestinatario(), busta.getDestinatario());
			String idPortaMittente = busta.getIdentificativoPortaDestinatario();
			if(idPortaMittente==null){
				idPortaMittente = this.traduttore.getIdentificativoPortaDefault(idSoggettoMittente);
			}
			idSoggettoMittente.setCodicePorta(idPortaMittente);
			params.setMittente(idSoggettoMittente);
						
			// Servizio
			IDServizio idServizio = new IDServizio(soggettoProduceEccezione, busta.getTipoServizio(), busta.getServizio(), busta.getAzione());
			params.setServizio(idServizio);
			
			// SOAPVersione
			params.setVersioneSoap(msg.getVersioneSoap());
			
			// IDFunzione
			params.setIdFunzione("PortaApplicativa");
			
			// ServizioApplicativo
			params.setServizioApplicativo(busta.getServizioApplicativoErogatore());
			
			// TipoPdD
			params.setTipoPorta(TipoPdD.APPLICATIVA);
			
			// Eccezione
			// NOTA: il parametro 'ignoraEccezioniNonGravi' serve solo a filtrare.
			//       il controllo che esista almeno una eccezione non grave e' fatto dove viene chiamato il metodo.
			Eccezione ecc = null;
			if(busta.sizeListaEccezioni()==1){
				ecc = busta.getEccezione(0);
			}
			else{
				ecc = Eccezione.getEccezioneProcessamento(ErroriCooperazione.ERRORE_GENERICO_PROTOCOLLO_NON_CORRETTO.getErroreCooperazione(), this.protocolFactory);
				StringBuffer bfDescrizione = new StringBuffer();
				for(int k=0; k<busta.sizeListaEccezioni();k++){
					Eccezione eccLista = busta.getEccezione(k);
					if(eccLista.getRilevanza()==null || LivelloRilevanza.isEccezioneLivelloGrave(eccLista.getRilevanza())){
						if(eccLista.getDescrizione(this.protocolFactory)!=null)
							bfDescrizione.append("["+this.traduttore.toString(eccLista.getCodiceEccezione(),eccLista.getSubCodiceEccezione())+"] "+eccLista.getDescrizione(this.protocolFactory)+"\n");
					}
				}
				if(bfDescrizione.length()>0)
					ecc.setDescrizione(bfDescrizione.toString());
			}
			params.setEccezioneProtocollo(ecc);
							
			// Set Fault Code
			String codiceEccezione = 
					this.traduttore.toString(ecc.getCodiceEccezione(),
							ecc.getSubCodiceEccezione());
			QName eccezioneName = this.erroreApplicativoBuilder.getQNameEccezioneProtocollo(codiceEccezione);
			SOAPFaultCode code = params.getSoapFaultCode();
			msg.setFaultCode(f, code, eccezioneName);
			
			// Set fault String
			f.setFaultString(ecc.getDescrizione(this.protocolFactory));
			
			// Set fault Actor
			f.setFaultActor(org.openspcoop2.utils.Costanti.OPENSPCOOP2);
			
			
			// genero errore applicativo
			this.erroreApplicativoBuilder.insertInSOAPFault(params, msg);
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}

}
