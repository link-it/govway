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



package org.openspcoop2.protocol.basic.tracciamento;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.tracciamento.Eccezione;
import org.openspcoop2.core.tracciamento.Proprieta;
import org.openspcoop2.core.tracciamento.Riscontro;
import org.openspcoop2.core.tracciamento.Trasmissione;
import org.openspcoop2.core.tracciamento.constants.TipoCodificaEccezione;
import org.openspcoop2.core.tracciamento.constants.TipoInoltro;
import org.openspcoop2.core.tracciamento.constants.TipoProfiloCollaborazione;
import org.openspcoop2.core.tracciamento.constants.TipoRilevanzaEccezione;
import org.openspcoop2.core.tracciamento.constants.TipoTempo;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.BasicComponentFactory;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.XMLRootElement;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.SubCodiceErrore;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.sdk.tracciamento.TracciaExtInfo;
import org.openspcoop2.protocol.sdk.tracciamento.TracciaExtInfoDefinition;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Element;

/**
 * TracciaSerializer
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TracciaSerializer extends BasicComponentFactory implements org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer {
	
	protected AbstractXMLUtils xmlUtils;
	

	public TracciaSerializer(IProtocolFactory<?> factory) throws ProtocolException {
		super(factory);
		this.xmlUtils = org.openspcoop2.message.xml.XMLUtils.DEFAULT;
	}


	
	private org.openspcoop2.core.tracciamento.Traccia toTraccia(Traccia tracciaObject)
			throws ProtocolException {
		String tmpId = null;
		try{
						
			if(tracciaObject.sizeProperties()>0){
				tmpId = tracciaObject.removeProperty(TracciaDriver.IDTRACCIA); // non deve essere serializzato
			}		
			org.openspcoop2.core.tracciamento.Traccia tracciaBase = tracciaObject.getTraccia();
			
			// xml
			if(tracciaBase.getBustaRaw()==null){
				if(tracciaObject.getBustaAsByteArray()!=null)
					tracciaBase.setBustaRaw(new String(tracciaObject.getBustaAsByteArray()));
				else if(tracciaObject.getBustaAsRawContent()!=null){			
					try{
						tracciaBase.setBustaRaw(tracciaObject.getBustaAsRawContent().toString(TipoSerializzazione.DEFAULT));
					}catch(Exception e){
						throw new Exception("Serializzazione RawContent non riuscita: "+e.getMessage(),e);
					}
				}
			}
			
			// Traduzioni da factory
			ITraduttore protocolTraduttore = this.protocolFactory.createTraduttore();
			if(tracciaBase!=null){
				if(tracciaBase.getBusta()!=null){
					if(tracciaBase.getBusta().getProfiloCollaborazione()!=null){
						if(tracciaBase.getBusta().getProfiloCollaborazione().getBase()==null && 
								tracciaBase.getBusta().getProfiloCollaborazione().getTipo()!=null){
							tracciaBase.getBusta().getProfiloCollaborazione().setBase(this.getBaseValueProfiloCollaborazione(protocolTraduttore,tracciaBase.getBusta().getProfiloCollaborazione().getTipo()));
						}
					}
					if(tracciaBase.getBusta().getProfiloTrasmissione()!=null){
						if(tracciaBase.getBusta().getProfiloTrasmissione().getInoltro()!=null){
							if(tracciaBase.getBusta().getProfiloTrasmissione().getInoltro().getBase()==null && 
									tracciaBase.getBusta().getProfiloTrasmissione().getInoltro().getTipo()!=null){
								tracciaBase.getBusta().getProfiloTrasmissione().getInoltro().setBase(this.getBaseValueInoltro(protocolTraduttore,tracciaBase.getBusta().getProfiloTrasmissione().getInoltro().getTipo()));
							}
						}
					}
					if(tracciaBase.getBusta().getOraRegistrazione()!=null){
						if(tracciaBase.getBusta().getOraRegistrazione().getSorgente()!=null){
							if(tracciaBase.getBusta().getOraRegistrazione().getSorgente().getBase()==null && 
									tracciaBase.getBusta().getOraRegistrazione().getSorgente().getTipo()!=null){
								tracciaBase.getBusta().getOraRegistrazione().getSorgente().setBase(this.getBaseValueTipoTempo(protocolTraduttore,tracciaBase.getBusta().getOraRegistrazione().getSorgente().getTipo()));
							}
						}
					}
					if(tracciaBase.getBusta().getTrasmissioni()!=null && tracciaBase.getBusta().getTrasmissioni().sizeTrasmissioneList()>0){
						for (Trasmissione trasmissione : tracciaBase.getBusta().getTrasmissioni().getTrasmissioneList()) {
							if(trasmissione.getOraRegistrazione()!=null){
								if(trasmissione.getOraRegistrazione().getSorgente()!=null){
									if(trasmissione.getOraRegistrazione().getSorgente().getBase()==null &&
											trasmissione.getOraRegistrazione().getSorgente().getTipo()!=null){
										trasmissione.getOraRegistrazione().getSorgente().setBase(this.getBaseValueTipoTempo(protocolTraduttore,trasmissione.getOraRegistrazione().getSorgente().getTipo()));
									}
								}
							}
						}
					}
					if(tracciaBase.getBusta().getRiscontri()!=null && tracciaBase.getBusta().getRiscontri().sizeRiscontroList()>0){
						for (Riscontro riscontro : tracciaBase.getBusta().getRiscontri().getRiscontroList()) {
							if(riscontro.getOraRegistrazione()!=null){
								if(riscontro.getOraRegistrazione().getSorgente()!=null){
									if(riscontro.getOraRegistrazione().getSorgente().getBase()==null &&
											riscontro.getOraRegistrazione().getSorgente().getTipo()!=null){
										riscontro.getOraRegistrazione().getSorgente().setBase(this.getBaseValueTipoTempo(protocolTraduttore,riscontro.getOraRegistrazione().getSorgente().getTipo()));
									}
								}
							}
						}
					}
					if(tracciaBase.getBusta().getEccezioni()!=null && tracciaBase.getBusta().getEccezioni().sizeEccezioneList()>0){
						for (Eccezione eccezione : tracciaBase.getBusta().getEccezioni().getEccezioneList()) {
							if(eccezione.getCodice()!=null){
								if(eccezione.getCodice().getBase()==null && eccezione.getCodice().getTipo()!=null){
									eccezione.getCodice().setBase(this.getBaseValueCodiceEccezione(protocolTraduttore,eccezione.getCodice().getTipo(), eccezione.getCodice().getSottotipo()));
								}
							}
							if(eccezione.getContestoCodifica()!=null){
								if(eccezione.getContestoCodifica().getBase()==null && 
										eccezione.getContestoCodifica().getTipo()!=null){
									eccezione.getContestoCodifica().setBase(this.getBaseValueContestoCodifica(protocolTraduttore,eccezione.getContestoCodifica().getTipo()));
								}
							}
							if(eccezione.getRilevanza()!=null){
								if(eccezione.getRilevanza().getBase()==null &&
										eccezione.getRilevanza().getTipo()!=null){
									eccezione.getRilevanza().setBase(this.getBaseValueRilevanzaEccezione(protocolTraduttore,eccezione.getRilevanza().getTipo()));
								}
							}
						}
					}
				}
			}
							
			return tracciaBase;

		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Tracciamento error: "+e.getMessage(),e);
			throw new ProtocolException("XMLBuilder.buildElement_Tracciamento error: "+e.getMessage(),e);
		}
		finally{
			if(tmpId!=null && tracciaObject!=null){
				tracciaObject.addProperty(TracciaDriver.IDTRACCIA, tmpId);
			}
		}
	}

	@Override
	public Element toElement(Traccia tracciaObject)
			throws ProtocolException {
		byte[] traccia = this.toByteArray(tracciaObject,TipoSerializzazione.XML);
		try{
			return this.xmlUtils.newElement(traccia);
		} catch(Exception e) {
			this.log.error("TracciaSerializer.toElement error: "+e.getMessage(),e);
			throw new ProtocolException("TracciaSerializer.toElement error: "+e.getMessage(),e);
		}
	}

	@Override
	public String toString(Traccia traccia, TipoSerializzazione tipoSerializzazione) throws ProtocolException {
		return this.toByteArrayOutputStream(traccia, tipoSerializzazione).toString();
	}

	@Override
	public byte[] toByteArray(Traccia traccia, TipoSerializzazione tipoSerializzazione) throws ProtocolException {
		return this.toByteArrayOutputStream(traccia, tipoSerializzazione).toByteArray();
	}
	
	protected ByteArrayOutputStream toByteArrayOutputStream(Traccia traccia, TipoSerializzazione tipoSerializzazione) throws ProtocolException {
		
		try{
		
			org.openspcoop2.core.tracciamento.Traccia tracciaBase = this.toTraccia(traccia);
			
			switch (tipoSerializzazione) {
				case XML:
				case DEFAULT:
					
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					org.openspcoop2.core.tracciamento.utils.XMLUtils.generateTraccia(tracciaBase,bout);
					bout.flush();
					bout.close();
					return bout;
	
				case JSON:
					
					bout = new ByteArrayOutputStream();
					String s = org.openspcoop2.core.tracciamento.utils.XMLUtils.generateTracciaAsJson(tracciaBase);
					bout.write(s.getBytes());
					bout.flush();
					bout.close();
					return bout;
			}
			
			throw new Exception("Tipo ["+tipoSerializzazione+"] Non gestito");
			
		} catch(Exception e) {
			this.log.error("TracciaSerializer.toString error: "+e.getMessage(),e);
			throw new ProtocolException("TracciaSerializer.toString error: "+e.getMessage(),e);
		}
	}

	
	
	// UTILITIES 
	
	private String getBaseValueProfiloCollaborazione(ITraduttore protocolTraduttore,TipoProfiloCollaborazione tipoProfiloCollaborazione){
		switch (tipoProfiloCollaborazione) {
		case ONEWAY:
			return protocolTraduttore.toString(ProfiloDiCollaborazione.ONEWAY);
		case SINCRONO:
			return protocolTraduttore.toString(ProfiloDiCollaborazione.SINCRONO);
		case ASINCRONO_ASIMMETRICO:
			return protocolTraduttore.toString(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO);
		case ASINCRONO_SIMMETRICO:
			return protocolTraduttore.toString(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
		case SCONOSCIUTO:
			return protocolTraduttore.toString(ProfiloDiCollaborazione.UNKNOWN);
		}
		return null;
	}
	
	private String getBaseValueInoltro(ITraduttore protocolTraduttore,TipoInoltro tipoInoltro){
		switch (tipoInoltro) {
		case INOLTRO_CON_DUPLICATI:
			return protocolTraduttore.toString(Inoltro.CON_DUPLICATI);
		case INOLTRO_SENZA_DUPLICATI:
			return protocolTraduttore.toString(Inoltro.SENZA_DUPLICATI);
		case SCONOSCIUTO:
			return protocolTraduttore.toString(Inoltro.UNKNOWN);
		}
		return null;
	}
	
	private String getBaseValueTipoTempo(ITraduttore protocolTraduttore,TipoTempo tipoTempo){
		switch (tipoTempo) {
		case LOCALE:
			return protocolTraduttore.toString(TipoOraRegistrazione.LOCALE);
		case SCONOSCIUTO:
			return protocolTraduttore.toString(TipoOraRegistrazione.UNKNOWN);
		case SINCRONIZZATO:
			return protocolTraduttore.toString(TipoOraRegistrazione.SINCRONIZZATO);
		}
		return null;
	}
	
	private String getBaseValueCodiceEccezione(ITraduttore protocolTraduttore,Integer codice, Integer subCodice){
		CodiceErroreCooperazione errore = CodiceErroreCooperazione.toCodiceErroreCooperazione(codice);
		if(subCodice==null){
			return protocolTraduttore.toString(errore);
		}
		else{
			SubCodiceErrore sub = new SubCodiceErrore();
			sub.setSubCodice(subCodice);
			return protocolTraduttore.toString(errore,sub);
		}
	}
	
	private String getBaseValueContestoCodifica(ITraduttore protocolTraduttore,TipoCodificaEccezione codifica){
		switch (codifica) {
		case ECCEZIONE_PROCESSAMENTO:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione.PROCESSAMENTO);
		case ECCEZIONE_VALIDAZIONE_PROTOCOLLO:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione.INTESTAZIONE);
		case SCONOSCIUTO:
			return null;
		}
		return null;
	}
	
	private String getBaseValueRilevanzaEccezione(ITraduttore protocolTraduttore,TipoRilevanzaEccezione codifica){
		switch (codifica) {
		case DEBUG:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.DEBUG);
		case ERROR:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.ERROR);
		case FATAL:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.FATAL);
		case INFO:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.INFO);
		case WARN:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.WARN);
		case SCONOSCIUTO:
			return protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.UNKNOWN);
		}
		return null;
	}



	@Override
	public XMLRootElement getXMLRootElement() throws ProtocolException {
		return new TracciaXMLRootElement();
	}
	
	
	@Override
	public List<TracciaExtInfoDefinition> getExtInfoDefinition(){
		return null;
	}
	@Override
	public List<TracciaExtInfo> extractExtInfo(Busta busta, ServiceBinding tipoApi){
		List<TracciaExtInfoDefinition> extInfoDefinitionList = this.getExtInfoDefinition();
		List<TracciaExtInfo> list = null;
		if(extInfoDefinitionList!=null && !extInfoDefinitionList.isEmpty()) {
			
			list = new ArrayList<TracciaExtInfo>();
			TracciaExtInfo extInfoNoPrefix = new TracciaExtInfo();
			extInfoNoPrefix.setEmpty(true);
			list.add(extInfoNoPrefix);
			
			List<String> propertyNamesEsistentiNellaBusta = new ArrayList<>();
			for (String pName : busta.getPropertiesNames()) {
				propertyNamesEsistentiNellaBusta.add(pName);
			}
			
			for (TracciaExtInfoDefinition tracciaExtInfoDefinition : extInfoDefinitionList) {
				
				TracciaExtInfo extInfo = new TracciaExtInfo();
				extInfo.setLabel(tracciaExtInfoDefinition.getLabel());
				
				List<String> proprietaDaRimuovere = new ArrayList<>();
				
				if(!propertyNamesEsistentiNellaBusta.isEmpty()) {
					for (String pName : propertyNamesEsistentiNellaBusta) {
						if(pName.startsWith(tracciaExtInfoDefinition.getPrefixId())) {
							String pValue = busta.getProperty(pName);
							String pNameWithoutPrefix = pName.substring(tracciaExtInfoDefinition.getPrefixId().length());
							Proprieta proprieta = new Proprieta();
							proprieta.setNome(pNameWithoutPrefix);
							proprieta.setValore(pValue);
							extInfo.getProprieta().add(proprieta);
							
							proprietaDaRimuovere.add(pName);
						}
					}
					
				}
				
				if(!proprietaDaRimuovere.isEmpty()) {
					for (String pName : proprietaDaRimuovere) {
						propertyNamesEsistentiNellaBusta.remove(pName);
					}
				}
				
				if(!extInfo.getProprieta().isEmpty()) {
					
					if(tracciaExtInfoDefinition.isOrder()) {
						
						java.util.ArrayList<String> listKeys = new ArrayList<>(); 
						for (Proprieta proprieta : extInfo.getProprieta()) {
							listKeys.add(proprieta.getNome());
						}
						java.util.Collections.sort(listKeys);
						List<Proprieta> proprietaOrdinate = new ArrayList<Proprieta>();
						for (String key : listKeys) {	
							Proprieta p = extInfo.getProprieta(key);
							proprietaOrdinate.add(p);
						}
						extInfo.setProprieta(proprietaOrdinate);
						
					}
					
					list.add(extInfo);
				}
				
			}
			
			if(!propertyNamesEsistentiNellaBusta.isEmpty()) {
				for (String pName : propertyNamesEsistentiNellaBusta) {
					Proprieta proprieta = new Proprieta();
					proprieta.setNome(pName);
					proprieta.setValore(busta.getProperty(pName));
					extInfoNoPrefix.getProprieta().add(proprieta);
				}
			}
			
			if(list.get(0).getProprieta().isEmpty()) {
				list.remove(0); // noPrefix
			}
		
		}
		return list;
	}
}