/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.logger.traccia;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.TestSignature;
import org.openspcoop2.utils.security.XmlSignature;
import org.openspcoop2.utils.service.beans.TransazioneBase;
import org.slf4j.Logger;

/**	
 * Client
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Client {

	public static void main(String[] args) throws Exception {
		
		process("JSON", false, false);
		
		process("JSON", true, false);
		
		process("XML", false, false);
		
		process("XML", true, false);

	}
	
	public static void process(String tipo, boolean extended, boolean init) throws Exception {
		
		Logger log = LoggerWrapperFactory.getLogger(Client.class);
		if(init) {
			ConfigurazionePdD confPdD = new ConfigurazionePdD();
			confPdD.setLoader(new Loader());
			confPdD.setLog(log);
			ProtocolFactoryManager.initialize(log, confPdD, CostantiLabel.TRASPARENTE_PROTOCOL_NAME);
			EsitiProperties.initialize(null, log, new Loader(), ProtocolFactoryManager.getInstance().getProtocolFactories());
		}
		
		
		System.out.println("================ ("+tipo+" extended:"+extended+") =================");
		
		TransazioneBase transazione = newTransazione(extended, log, init);
		
		InputStream isKeystore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jks");
		File fKeystore = File.createTempFile("keystore", "jks");
		FileSystemUtilities.writeFile(fKeystore, Utilities.getAsByteArray(isKeystore));
		
		Properties pSerializerConf = new Properties();
		pSerializerConf.setProperty("prettyPrint", "true");
		pSerializerConf.setProperty("xml.namespace", "http://govway.org/traccia");
		pSerializerConf.setProperty("xml.localName", "traccia");
		Serializer serializer = new Serializer(pSerializerConf);
		
		Properties pSignatureConf = new Properties();
		pSignatureConf.setProperty("keystore.type", "jks");
		pSignatureConf.setProperty("keystore.path", fKeystore.getPath());
		pSignatureConf.setProperty("keystore.password", "123456");
		pSignatureConf.setProperty("key.alias", "openspcoop");
		pSignatureConf.setProperty("key.password", "key123456");
		pSignatureConf.setProperty("json.signatureAlgorithm", "RS256");
		pSignatureConf.setProperty("json.signatureSerialization", JOSESerialization.COMPACT.name());
		pSignatureConf.setProperty("json.signatureDetached", "true");
		pSignatureConf.setProperty("json.signaturePayloadEncoding", "true");
		pSignatureConf.setProperty("xml.signatureAlgorithm", XmlSignature.DEFAULT_SIGNATURE_METHOD);
		pSignatureConf.setProperty("xml.digestAlgorithm", XmlSignature.DEFAULT_DIGEST_METHOD);
		pSignatureConf.setProperty("xml.canonicalizationAlgorithm", XmlSignature.DEFAULT_CANONICALIZATION_METHOD);
		pSignatureConf.setProperty("xml.addBouncyCastleProvider", "true");
		pSignatureConf.setProperty("xml.addX509KeyInfo", "true");
		pSignatureConf.setProperty("xml.xml_addRSAKeyInfo", "false");
		Signature signature = new Signature(pSignatureConf);
		signature.init();
		
		if("JSON".equals(tipo)) {
			
			String json = serializer.toJson(transazione);
			System.out.println(json);
			
			String compactSign = signature.jsonSign(json);
			System.out.println("JsonCompactSignature Signed: \n"+compactSign);
			
		}
		else {
		
			String xml = serializer.toXml(transazione);
			System.out.println(xml);
	
			String xmlSign = signature.xmlSign(xml);
			System.out.println("XmlSignature Signed (X509 KeyInfo): "+xmlSign);
			
		}
		
		System.out.println("================ ("+tipo+" extended:"+extended+") terminato =================");
	}
	
	private static TransazioneBase newTransazione(boolean extended, Logger log, boolean init) throws Exception {
		
		org.openspcoop2.core.transazioni.Transazione transazioneDB = new org.openspcoop2.core.transazioni.Transazione();
		
		transazioneDB.setIdTransazione("a16c7501-0664-48ff-9216-a726f8e7778c");
		transazioneDB.setStato("s1");
		transazioneDB.setRuoloTransazione(1);
		transazioneDB.setEsito(0);
		transazioneDB.setEsitoContesto("standard");
		transazioneDB.setProtocollo(CostantiLabel.TRASPARENTE_PROTOCOL_NAME);
		transazioneDB.setTipoRichiesta("POST");
		transazioneDB.setCodiceRispostaIngresso("200");
		transazioneDB.setCodiceRispostaUscita("200");
		
		transazioneDB.setDataAccettazioneRichiesta(DateManager.getDate());
		Utilities.sleep(new Random().nextInt(300));
		transazioneDB.setDataIngressoRichiesta(DateManager.getDate());
		Utilities.sleep(new Random().nextInt(300));
		transazioneDB.setDataUscitaRichiesta(DateManager.getDate());
		Utilities.sleep(new Random().nextInt(300));
		transazioneDB.setDataAccettazioneRisposta(DateManager.getDate());
		Utilities.sleep(new Random().nextInt(300));
		transazioneDB.setDataIngressoRisposta(DateManager.getDate());
		Utilities.sleep(new Random().nextInt(300));
		transazioneDB.setDataUscitaRisposta(DateManager.getDate());
		
		transazioneDB.setRichiestaIngressoBytes(67893l);
		transazioneDB.setRichiestaUscitaBytes(67893l);
		transazioneDB.setRispostaIngressoBytes(57363l);
		transazioneDB.setRispostaUscitaBytes(57363l);
		
		transazioneDB.setPddCodice("domain/gw/Ente");
		transazioneDB.setPddTipoSoggetto("gw");
		transazioneDB.setPddNomeSoggetto("Ente");
		transazioneDB.setPddRuolo(PddRuolo.APPLICATIVA);
		
		String fault = "<fault><test>ESEMPIO</test></fault>";
		transazioneDB.setFaultIntegrazione(fault);
		transazioneDB.setFormatoFaultIntegrazione(MessageType.XML.name());
		transazioneDB.setFaultCooperazione(fault);
		transazioneDB.setFormatoFaultCooperazione(MessageType.XML.name());
		
		transazioneDB.setNomeSoggettoFruitore("UfficioProtocolloComuneFirenze");
		transazioneDB.setTipoSoggettoFruitore("gw");
		transazioneDB.setIdportaSoggettoFruitore("domain/gw/UfficioProtocolloComuneFirenze");
		transazioneDB.setIndirizzoSoggettoFruitore("http://UfficioProtocolloComuneFirenze");
		
		transazioneDB.setNomeSoggettoErogatore("Ente");
		transazioneDB.setTipoSoggettoErogatore("gw");
		transazioneDB.setIdportaSoggettoErogatore("domain/gw/Ente");
		transazioneDB.setIndirizzoSoggettoErogatore("http://Ente");
		
		transazioneDB.setIdMessaggioRichiesta("c72a4403-2288-09ff-99gbh-a726f8e1238h");
		transazioneDB.setIdMessaggioRisposta("b32a7501-7788-48ff-99999-a726f8e7658e");
		
		transazioneDB.setProfiloCollaborazioneOp2(ProfiloDiCollaborazione.SINCRONO.getEngineValue());
		
		transazioneDB.setIdCollaborazione("c77a7501-2288-48ff-kmdi98-a333f8elm2je");
		
		transazioneDB.setTipoServizio("gw");
		transazioneDB.setNomeServizio("Protocollazione");
		transazioneDB.setVersioneServizio(1);
		transazioneDB.setAzione("protocollazioneDocumento");
		transazioneDB.setIdAsincrono("ID_ASINCRONO");
		
		transazioneDB.setIdCorrelazioneApplicativa("ENTE-X-UFFICIOY-123");
		transazioneDB.setIdCorrelazioneApplicativaRisposta("ENTE-X-UFFICIOY-123-PROTOCOLLO-23");
	
		transazioneDB.setServizioApplicativoFruitore("applicativoProtocollazione");
		transazioneDB.setNomePorta("Ente/Protocollazione/v1");
		transazioneDB.setCredenziali("C=IT,O=PROVA");
		transazioneDB.setLocationConnettore("http://applicativoInterno/protocollo");
		transazioneDB.setUrlInvocazione("http://govway/in/Ente/Protocollazione/v1/documenti?test=true");
		
		CredenzialiMittente credenziali = new CredenzialiMittente();
		
		CredenzialeMittente trasporto = new CredenzialeMittente();
		trasporto.setCredenziale("c=IT, O=Comune Firenze, OU=Protocollo, CN=protocollo.regionetoscana.it");
		credenziali.setTrasporto(trasporto);
		
		CredenzialeMittente token_issuer = new CredenzialeMittente();
		token_issuer.setCredenziale("issuer");
		credenziali.setToken_issuer(token_issuer);
		
		CredenzialeMittente token_subject = new CredenzialeMittente();
		token_subject.setCredenziale("subject");
		credenziali.setToken_subject(token_subject);
		
		CredenzialeMittente token_clientId = new CredenzialeMittente();
		token_clientId.setCredenziale("clientId");
		credenziali.setToken_clientId(token_clientId);
		
		CredenzialeMittente token_username = new CredenzialeMittente();
		token_username.setCredenziale("Andrea Rossi");
		credenziali.setToken_username(token_username);
		
		CredenzialeMittente token_mail = new CredenzialeMittente();
		token_mail.setCredenziale("mail");
		credenziali.setToken_eMail(token_mail);
		
		transazioneDB.setTokenInfo("\n{\n\t\"valid\":\"true\",\n\t\"name2\":\"value2\"\n}");
		
		
		transazioneDB.setDuplicatiRichiesta(1);
		transazioneDB.setDuplicatiRisposta(2);
		
		transazioneDB.setClusterId("Nodo1");
		transazioneDB.setSocketClientAddress("127.0.0.1");
		transazioneDB.setTransportClientAddress("10.114.87.23");
		
		if(extended) {
			transazioneDB.addDumpMessaggio(newDumpMessaggio(TipoMessaggio.RICHIESTA_INGRESSO));
			transazioneDB.addDumpMessaggio(newDumpMessaggio(TipoMessaggio.RICHIESTA_USCITA));
			transazioneDB.addDumpMessaggio(newDumpMessaggio(TipoMessaggio.RISPOSTA_INGRESSO));
			transazioneDB.addDumpMessaggio(newDumpMessaggio(TipoMessaggio.RICHIESTA_USCITA));
		}
		else {
			if(PddRuolo.APPLICATIVA.equals(transazioneDB.getPddRuolo())) {
				transazioneDB.addDumpMessaggio(newDumpMessaggio(TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO));
				transazioneDB.addDumpMessaggio(newDumpMessaggio(TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO));
			}
			else {
				transazioneDB.addDumpMessaggio(newDumpMessaggio(TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO));
				transazioneDB.addDumpMessaggio(newDumpMessaggio(TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO));
			}		
		}
	
		List<MsgDiagnostico> messaggiDiagnostici = new ArrayList<>();
			
		MsgDiagnostico diag2 = new MsgDiagnostico();
		diag2.setCodice("002003");
		diag2.setGdo(DateManager.getDate());
		diag2.setIdFunzione("consegna");
		diag2.setMessaggio("Ricevuta richiesta");
		diag2.setSeverita(3);
		messaggiDiagnostici.add(diag2);

		
		MsgDiagnostico diag1 = new MsgDiagnostico();
		diag1.setCodice("001002");
		diag1.setGdo(DateManager.getDate());
		diag1.setIdFunzione("inoltro");
		diag1.setMessaggio("Credenziali non sufficenti");
		diag1.setSeverita(2);
		messaggiDiagnostici.add(diag1);

		Properties properties = new Properties();
		properties.setProperty("mittente.fruitore", "true");
		Converter converter = new Converter(log, properties);
		if(!init) {
			converter.setThrowInitProtocol(false);
		}
		if(extended) {
			return converter.toTransazioneExt(transazioneDB, credenziali, null, null, messaggiDiagnostici);
		}
		else {
			return converter.toTransazione(transazioneDB, credenziali, messaggiDiagnostici);
		}
		
	}

	
	private static DumpMessaggio newDumpMessaggio(TipoMessaggio tipo) {
		DumpMessaggio msg = new DumpMessaggio();
		msg.setTipoMessaggio(tipo);
		
		boolean isRichiesta = tipo.name().startsWith("Richiesta");
		
		String jsonInput = "\n{\n\t\"name\":\"Messaggio "+tipo+"\",\n\t\"name2\":\"value2\"\n}";
		String jsonInput2 = "\n{\n\t\"name\":\"Allegato "+tipo+"\",\n\t\"name2\":\"value2\"\n}";
		String xmlInput = "<prova><test>VALORE "+tipo+"</test></prova>";
		
		msg.setBody(jsonInput.getBytes());
		msg.setFormatoMessaggio(MessageType.JSON.name());
		msg.setContentType("application/json");
		msg.setMultipartContentId("<IDXXX>");
		msg.setMultipartContentType("application/json");
		msg.setMultipartContentLocation("Location");
		
		DumpMultipartHeader multipartHdr1 = new DumpMultipartHeader();
		multipartHdr1.setNome("MIME_HDR1-"+tipo);
		multipartHdr1.setNome("VALORE1-"+tipo);
		msg.addMultipartHeader(multipartHdr1);
		
		DumpMultipartHeader multipartHdr2 = new DumpMultipartHeader();
		multipartHdr2.setNome("MIME_HDR2-"+tipo);
		multipartHdr2.setNome("VALORE2-"+tipo);
		msg.addMultipartHeader(multipartHdr2);
		
		DumpContenuto contenuto1 = new DumpContenuto();
		contenuto1.setNome("BODY1-"+tipo);
		contenuto1.setNome("VALORE1-"+tipo);
		msg.addContenuto(contenuto1);
		
		DumpContenuto contenuto2 = new DumpContenuto();
		contenuto2.setNome("BODY2-"+tipo);
		contenuto2.setNome("VALORE2-"+tipo);
		msg.addContenuto(contenuto2);


		if(isRichiesta) {
			DumpHeaderTrasporto header1 = new DumpHeaderTrasporto();
			header1.setNome("Accept");
			header1.setNome("application/json");
			msg.addHeaderTrasporto(header1);
		}
		
		DumpHeaderTrasporto header2 = new DumpHeaderTrasporto();
		header2.setNome("Content-Length");
		header2.setValore("56789");
		msg.addHeaderTrasporto(header2);
				
		DumpHeaderTrasporto hdr3 = new DumpHeaderTrasporto();
		hdr3.setNome("Content-Type");
		if(isRichiesta) {
			hdr3.setValore("application/pdf");
		}
		else {
			hdr3.setValore("application/problem+json");
		}
		msg.addHeaderTrasporto(hdr3);
		
		if(isRichiesta) {
			DumpHeaderTrasporto hdr4 = new DumpHeaderTrasporto();
			hdr4.setNome("X-Forwarded-For");
			hdr4.setValore("10.114.87.23");
			msg.addHeaderTrasporto(hdr4);
		}
		
		
		DumpAllegato attach1 = new DumpAllegato();
		attach1.setContentId("<ID-ATTACH-1-"+tipo+">");
		attach1.setContentType("text/"+tipo+"+xml");
		attach1.setContentLocation("LocationAttach1-"+tipo);
		attach1.setAllegato(xmlInput.getBytes());
		
		DumpHeaderAllegato attach1Hdr1 = new DumpHeaderAllegato();
		attach1Hdr1.setNome("MIME_HDR1-"+tipo);
		attach1Hdr1.setNome("VALORE1-"+tipo);
		attach1.addHeader(attach1Hdr1);
		
		DumpHeaderAllegato attach1Hdr2 = new DumpHeaderAllegato();
		attach1Hdr2.setNome("MIME_HDR2-"+tipo);
		attach1Hdr2.setNome("VALORE2-"+tipo);
		attach1.addHeader(attach1Hdr2);
		
		msg.addAllegato(attach1);
		
		DumpAllegato attach2 = new DumpAllegato();
		attach2.setContentId("<ID-ATTACH-2-"+tipo+">");
		attach2.setContentType("text/"+tipo+"+json");
		attach2.setContentLocation("LocationAttach2-"+tipo);
		attach2.setAllegato(jsonInput2.getBytes());
		msg.addAllegato(attach2);
		
		
		return msg;
	}
}
