/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.protocol.sdi.utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.message.SoapUtilsBuildParameter;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.behaviour.Behaviour;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardTo;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardToConfiguration;
import org.openspcoop2.pdd.core.behaviour.BehaviourResponseTo;
import org.openspcoop2.pdd.core.behaviour.DefaultBehaviour;
import org.openspcoop2.pdd.core.behaviour.StatoFunzionalita;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdi.SDIFactory;
import org.openspcoop2.protocol.sdi.builder.SDIBustaBuilder;
import org.openspcoop2.protocol.sdi.builder.SDIImbustamento;
import org.openspcoop2.protocol.sdi.config.SDIProperties;
import org.openspcoop2.protocol.sdi.constants.SDICostanti;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRicezioneFatture;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * FatturaPABehaviour
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FatturaPABehaviour extends DefaultBehaviour {

	private static OpenSPCoop2Properties openspcoop2Properties = null;
	private static SDIProperties sdiProperties = null;
		
	private static synchronized void init() throws ProtocolException{
		if(openspcoop2Properties==null){
			try{
				openspcoop2Properties = OpenSPCoop2Properties.getInstance();
				sdiProperties = SDIProperties.getInstance(OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(),e);
			}
		}
	}
	
	@Override
	public Behaviour behaviour(GestoreMessaggi gestoreMessaggioRichiesta, Busta busta) throws CoreException {
		
		if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE.equals(busta.getServizio()) &&
				SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_AZIONE_RICEVI_FATTURE.equals(busta.getAzione())){
			
			try{
				if(openspcoop2Properties==null){
					init();
				}
				
				OpenSPCoop2Message msg = gestoreMessaggioRichiesta.getMessage();
				Object fatturaPAObject = msg.getContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_FATTURA);
				byte [] fatturaBytes = null;
				if(fatturaPAObject==null){
					//throw new CoreException("FatturaPA behaviour è utilizzabile solo se l'opzione 'org.openspcoop2.protocol.sdi.parse.fattura.saveInContext' è abilitata");
					// gestisco con lo splitter
					try{
						SDIFactory sdiFactory = new SDIFactory();
						sdiFactory.createBustaBuilder().sbustamento(null, msg, busta, true, new ProprietaManifestAttachments());
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						SoapUtils.sbustamentoMessaggio(msg,bout);
						bout.flush();
						bout.close();
						fatturaBytes = bout.toByteArray();
						
						Object formato = busta.getProperty(SDICostanti.SDI_BUSTA_EXT_FORMATO_ARCHIVIO_INVIO_FATTURA);
						if(formato!=null && ((String)formato).equals(SDICostanti.SDI_TIPO_FATTURA_P7M) ){
							P7MInfo p7m = new P7MInfo(fatturaBytes, sdiFactory.getLogger());
							fatturaBytes = p7m.getXmlDecoded();
						}
					}catch(Throwable e){
						throw new CoreException("FatturaPA behaviour con l'opzione 'org.openspcoop2.protocol.sdi.parse.fattura.saveInContext' disabilitata. Lo sbustamento del messaggio non e' riuscito: "+e.getMessage(),e);	
					}
				}
				
				byte[]metadati = null;
				if(sdiProperties.isBehaviourCreaProtocolloSDI()){
					Object metadatiBytesObject = msg.getContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_FATTURA_METADATI_BYTES);
					if(metadatiBytesObject==null){
						throw new CoreException("FatturaPA behaviour con opzione 'org.openspcoop2.protocol.sdi.behaviour.creaProtocolloSDI' è utilizzabile solo se l'opzione 'org.openspcoop2.protocol.sdi.parse.fattura.saveInContext' è abilitata");
					}
					metadati = (byte[]) metadatiBytesObject;
				}
				
				Behaviour behaviour = new Behaviour();
				
				
				// responseTo
				
				BehaviourResponseTo responseTo = new BehaviourResponseTo();
				responseTo.setResponseTo(true);
				behaviour.setResponseTo(responseTo);
				OpenSPCoop2Message replyTo = OpenSPCoop2MessageFactory.getMessageFactory().createEmptySOAPMessage(msg.getVersioneSoap());
				
				SDIImbustamento sdiImbustamento = new SDIImbustamento((SDIBustaBuilder) gestoreMessaggioRichiesta.getProtocolFactory().createBustaBuilder());
				IState state = null;
				if(gestoreMessaggioRichiesta.getOpenspcoopstate()!=null){
					state = gestoreMessaggioRichiesta.getOpenspcoopstate().getStatoRichiesta();
				}
				sdiImbustamento.creaRisposta_ServizioRicezioneFatture_AzioneRiceviFatture(gestoreMessaggioRichiesta.getProtocolFactory(), 
						state, busta, replyTo);
				behaviour.getResponseTo().setMessage(replyTo);
				

				
				// forwardTo
				List<byte[]> listForwardToList = new ArrayList<byte[]>();
				List<Object> listForwardToObjectList = new ArrayList<Object>();
				
				if(fatturaPAObject==null){
					
					listForwardToList = SDILottoUtils.splitLotto(fatturaBytes);
					
				}
				else{
					if(fatturaPAObject instanceof it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType){
						it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType fattura = 
								(it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType) fatturaPAObject;
						for (int i = 0; i < fattura.sizeFatturaElettronicaBodyList(); i++) {
							
							it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType fatturaSingola = 
									new it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType();
							fatturaSingola.setVersione(fattura.getVersione());
							fatturaSingola.setFatturaElettronicaHeader(fattura.getFatturaElettronicaHeader());
							fatturaSingola.addFatturaElettronicaBody(fattura.getFatturaElettronicaBody(i));
							
							it.gov.fatturapa.sdi.fatturapa.v1_0.utils.serializer.JaxbSerializer serializer =
									new it.gov.fatturapa.sdi.fatturapa.v1_0.utils.serializer.JaxbSerializer();
							it.gov.fatturapa.sdi.fatturapa.v1_0.ObjectFactory of = new it.gov.fatturapa.sdi.fatturapa.v1_0.ObjectFactory();
							byte[] xml = serializer.toByteArray(of.createFatturaElettronica(fatturaSingola));
							listForwardToList.add(xml);
							listForwardToObjectList.add(fatturaSingola);
						}
					}
					else if(fatturaPAObject instanceof it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType){
						it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType fattura = 
								(it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType) fatturaPAObject;
						for (int i = 0; i < fattura.sizeFatturaElettronicaBodyList(); i++) {
							
							it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType fatturaSingola = 
									new it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType();
							fatturaSingola.setVersione(fattura.getVersione());
							fatturaSingola.setFatturaElettronicaHeader(fattura.getFatturaElettronicaHeader());
							fatturaSingola.addFatturaElettronicaBody(fattura.getFatturaElettronicaBody(i));
							
							it.gov.fatturapa.sdi.fatturapa.v1_1.utils.serializer.JaxbSerializer serializer =
									new it.gov.fatturapa.sdi.fatturapa.v1_1.utils.serializer.JaxbSerializer();
							it.gov.fatturapa.sdi.fatturapa.v1_1.ObjectFactory of = new it.gov.fatturapa.sdi.fatturapa.v1_1.ObjectFactory();
							byte[] xml = serializer.toByteArray(of.createFatturaElettronica(fatturaSingola));
							listForwardToList.add(xml);
							listForwardToObjectList.add(fatturaSingola);
							
						}
					}
					else{
						throw new CoreException("Tipo ["+fatturaPAObject.getClass().getName()+"] non gestito");
					}
				}
				
				for (int j = 0; j < listForwardToList.size(); j++) {
					byte[] xml = listForwardToList.get(j);
					
					if(sdiProperties.isBehaviourCreaProtocolloSDI()){
						xml = imbustamentoSDI(busta, xml, metadati);
					}
					
					SoapUtilsBuildParameter params = new SoapUtilsBuildParameter(xml,true,
							true,false,
							openspcoop2Properties.isFileCacheEnable(), openspcoop2Properties.getAttachmentRepoDir(), openspcoop2Properties.getFileThreshold());
					OpenSPCoop2MessageParseResult pr = SoapUtils.build(params,null);
					OpenSPCoop2Message msgForwardTo = pr.getMessage_throwParseException();
					
					if(listForwardToObjectList!=null && (j<listForwardToObjectList.size())){
						Object fatturaSingola = listForwardToObjectList.get(j);
						msgForwardTo.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_FATTURA, fatturaSingola);
					}
					
					BehaviourForwardTo forwardTo = new BehaviourForwardTo();
					forwardTo.setMessage(msgForwardTo);
					
					if(!sdiProperties.isBehaviourCreaProtocolloSDI()){
						BehaviourForwardToConfiguration config = new BehaviourForwardToConfiguration();
						config.setSbustamentoInformazioniProtocollo(StatoFunzionalita.DISABILITATA);
						config.setSbustamentoSoap(StatoFunzionalita.CONFIGURAZIONE_ORIGINALE);
						forwardTo.setConfig(config);
					}
					
					Busta bustaNewMessaggio = busta.clone();
					int posizione = (j+1);
					bustaNewMessaggio.setID("Fattura"+posizione+"_"+busta.getID());
					bustaNewMessaggio.addProperty(SDICostanti.SDI_BUSTA_EXT_POSIZIONE_FATTURA_PA, posizione+"");
					forwardTo.setDescription("PosizioneFattura:"+posizione);
					forwardTo.setBusta(bustaNewMessaggio);
					
					behaviour.getForwardTo().add(forwardTo);
					
				}
				
				return behaviour;
			}catch(Exception e){
				throw new CoreException(e.getMessage(),e);
			}
			
		}
		
		else{
			return super.behaviour(gestoreMessaggioRichiesta, busta);
		}
		
	}

	
	private byte[] imbustamentoSDI(Busta busta,byte[]xml,byte[]metadati) throws SerializerException{
		it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.ObjectFactory of = new it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.ObjectFactory();
		it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType sdi = new it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType();
		sdi.setIdentificativoSdI(Integer.parseInt(busta.getProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI)));
		sdi.setNomeFile(busta.getProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE));
		sdi.setFile(xml);
		sdi.setNomeFileMetadati(busta.getProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE_METADATI));
		sdi.setMetadati(metadati);
		it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.utils.serializer.JaxbSerializer serializerWSRicezione = new it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.utils.serializer.JaxbSerializer();
		return serializerWSRicezione.toByteArray(of.createFileSdIConMetadati(sdi));
	}	
}
