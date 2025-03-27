package org.openspcoop2.web.monitor.transazioni.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.openspcoop2.core.diagnostica.utils.XMLUtilsException;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XPathException;
import org.openspcoop2.utils.xml.XPathNotValidException;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.transazioni.bean.ArchivioZipFileUploadBean;
import org.openspcoop2.web.monitor.transazioni.bean.ContenutiTransazioneArchivioBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneApplicativoServerArchivioBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneApplicativoServerBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneArchivioBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.constants.TransazioniCostanti;
import org.openspcoop2.web.monitor.transazioni.core.exception.ArchivioZipException;
import org.openspcoop2.web.monitor.transazioni.mbean.TracciaBean;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBException;

public class ArchivioZipManager {

	private static final String MANIFEST_KEY_CONTENT_TYPE = "ContentType";
	private static final String MANIFEST_KEY_CONTENT_LENGTH = "ContentLength";
	private static final String MANIFEST_KEY_MESSAGE_TYPE = "MessageType";
	private static final String MANIFEST_KEY_TRANSACTION_ID = "TransactionId";
	private static final String MANIFEST_KEY_PROTOCOL = "Protocol";

	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private static final String NOME_FILE_SEARCH_FILTER_XML = "SearchFilter.xml";
	private static final String NOME_FILE_MANIFEST_XML = "manifest.xml";
	private static final String NOME_FILE_MSG_DIAGNOSTICI_XML = "diagnostici.xml";
	private static final String NOME_FILE_TRACCE_XML = "tracce.xml";
	private static final String NOME_FILE_FAULT_COOPERAZIONE = "faultCooperazione";
	private static final String NOME_FILE_FAULT_INTEGRAZIONE = "faultIntegrazione";
	private static final String NOME_FILE_FAULT = "fault";
	private static final String NOME_FILE_FAULT_ULTIMO_ERRORE = "faultUltimoErrore";
	private static final String ROOT_FOLDER_TRANSAZIONI = "Transazioni";
	private static final String FOLDER_CONTENUTI = "contenuti";
	private static final String FOLDER_CONSEGNE = "consegne";
	private static final String NOME_FILE_HEADERS_TXT = "headers";
	private static final String NOME_FILE_MANIFEST_TXT = "manifest";
	private static final String NOME_FILE_MESSAGE = "message";
	private static final String NOME_FILE_CONTENTS = "contents";
	private static final String NOME_FILE_MESSAGE_MULTIPART_HEADERS = "message_multipart_headers";
	private static final String NOME_FILE_ALLEGATO_MULTIPART_HEADERS = "allegato_multipart_headers";
	private static final String NOME_FILE_ALLEGATO = "allegato";
	private static final String NOME_FILE_ALLEGATI = "allegati";

	private Map<String, TransazioneArchivioBean> mapTransazioni = null;
	private boolean headersAsProperties = true;
	private boolean contenutiAsProperties = false;
	private boolean exportTracceUseProtocolSerialization = false; // il formato esportato sarà comune per qualsiasi protocollo (in modo da ricostruirli in fase di import)
	private boolean exportDiagnosticiUseProtocolSerialization = false; // il formato esportato sarà comune per qualsiasi protocollo (in modo da ricostruirli in fase di import)
	
	private boolean inizializzaInformazioniSupplementari = false;

	public ArchivioZipManager() {
		this.mapTransazioni = new HashMap<>();

		try{
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(log);

			this.headersAsProperties = govwayMonitorProperties.isAttivoTransazioniExportHeaderAsProperties();	
			this.contenutiAsProperties = govwayMonitorProperties.isAttivoTransazioniExportContenutiAsProperties();
		}catch(Exception e){
			log.error("Inizializzazione ArchivioZipManager fallita, setto enableHeaderInfo=false",e);
		}
	}

	public void clear() {
		this.mapTransazioni.clear();
		this.setInizializzaInformazioniSupplementari(false);
	}

	public void leggiArchivio(ArchivioZipFileUploadBean archivioZip) throws ArchivioZipException {
		// resetta il flag per la lettura delle informazioni supplementari
		this.setInizializzaInformazioniSupplementari(false);
		
		// verifica se ci sono file caricati e il tipo 
		if(archivioZip.getMapElementiRicevuti().isEmpty()) {
			// lista file vuota
			throw new ArchivioZipException(MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_ESAMINA_ARCHIVIO_ZIP_MESSAGGIO_ERRORE_FILE_NON_INDICATO_KEY));
		}

		for (Entry<String, UploadItem> fileCaricatoEntry : archivioZip.getMapElementiRicevuti().entrySet()) {
			UploadItem fileCaricato = fileCaricatoEntry.getValue();
			String fileName = fileCaricato.getFileName();
			if(fileCaricato.getContentType() != null) {
				boolean checkAcceptedType =  archivioZip.checkAcceptedType(fileCaricato.getContentType());
				if(!checkAcceptedType) {
					// tipo file non valido
					throw new ArchivioZipException(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(TransazioniCostanti.TRANSAZIONI_SEARCH_ESAMINA_ARCHIVIO_ZIP_MESSAGGIO_ERRORE_FILE_NON_VALIDO_KEY, fileName));
				}
			}
			this.leggiArchivio(fileCaricato);

			// elabora diagnostici e tracce
			this.elaboraDiagnosticiETracce();

			//elabora consegne 
			this.elaboraConsegne();
		}
	}

	public void leggiArchivio(UploadItem uploadItem) throws ArchivioZipException {
		String fileName = uploadItem.getFileName(); // nome del file caricato
		try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(uploadItem.getData()))) {
			ZipEntry entry;
			String chiave = null;
			String chiaveConsegna = null;
			// Itera attraverso il contenuto del file ZIP
			while ((entry = zis.getNextEntry()) != null) {
				log.debug("Analisi file: {}", entry.getName());
				byte[] fileContent = readFileContent(zis);

				// filtro ricerca
				if (entry.getName().endsWith(File.separatorChar + NOME_FILE_SEARCH_FILTER_XML)) {
					log.debug("File filtro: {}", entry.getName());
				}

				// Estrai la chiave dal nome del primo file che trovo
				chiave = extractKeyFromPath(entry.getName());
				log.debug("Estratto ID transazione: {}", chiave);

				// Processa i file solo se una chiave è stata trovata
				if (chiave != null) {
					// controlla presenza di una consegna
					chiaveConsegna = estraiChiaveConsegna(entry, chiave);

					// manifest
					estraiFileManifest(entry, chiave, chiaveConsegna, fileContent);

					// diagnostici
					estraiDiagnostici(entry, chiave, chiaveConsegna, fileContent);

					// fault cooperazione
					estraiFaultCooperazione(entry, chiave, chiaveConsegna, fileContent);

					// fault integrazione
					estraiFaultIntegrazione(entry, chiave, chiaveConsegna, fileContent);

					// fault ultimo errore
					estraiFaultUltimoErrore(entry, chiave, chiaveConsegna, fileContent);

					// fault 
					estraiFault(entry, chiave, chiaveConsegna, fileContent);

					// tracce 
					estraiTracce(entry, chiave, chiaveConsegna, fileContent);

					// contenuti
					estraiContenuti(entry, chiave, chiaveConsegna, fileContent);

				}

				zis.closeEntry();
			}
		} catch (IOException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			log.error(e.getMessage(), e);
			throw new ArchivioZipException(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(TransazioniCostanti.TRANSAZIONI_SEARCH_ESAMINA_ARCHIVIO_ZIP_MESSAGGIO_ERRORE_FILE_CONTENUTO_NON_VALIDO_KEY, fileName));
		} finally {
			//donothing
		}
	}

	private void elaboraDiagnosticiETracce() throws ArchivioZipException {
		for (Entry<String, TransazioneArchivioBean> transazioneEntry : this.mapTransazioni.entrySet()) {

			TransazioneArchivioBean transazioneArchivioBean = transazioneEntry.getValue();
			TransazioneBean transazioneBean = transazioneArchivioBean.getTransazioneBean();
			String idTransazione = transazioneEntry.getKey();
			elaboraTracce(transazioneArchivioBean, transazioneBean, idTransazione);
			elaboraDiagnostici(transazioneArchivioBean, transazioneBean, idTransazione);
		}
	}

	private void elaboraDiagnostici(TransazioneArchivioBean transazioneArchivioBean, TransazioneBean transazioneBean,
			String idTransazione) throws ArchivioZipException {
		if(transazioneArchivioBean.getDiagnosticiRaw() != null) {
			try { 
				log.debug("Estraggo informazioni diagnostici per la transazione {}:", idTransazione);

				List<MsgDiagnostico> diagnostici = DeserializerTransazioni.readMsgDiagnostici(transazioneArchivioBean.getDiagnosticiRaw(), transazioneBean, null, this.exportDiagnosticiUseProtocolSerialization); 


				log.debug("Lettura diagnostici completata.");

				if(!diagnostici.isEmpty()) {
					transazioneArchivioBean.getDiagnostici().addAll(diagnostici);
				}

			}catch (JAXBException | IOException | ProtocolException | ParserConfigurationException | SAXException | XMLUtilsException | TransformerException | XMLException | XPathException | XPathNotValidException e) {
				this.logError(e);
				throw new ArchivioZipException(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(TransazioniCostanti.TRANSAZIONI_SEARCH_ESAMINA_ARCHIVIO_ZIP_MESSAGGIO_ERRORE_FILE_ELEMENTO_TRANSAZIONE_DIAGNOSTICI_NON_VALIDO_KEY, idTransazione));
			}
		}
	}

	private void elaboraTracce(TransazioneArchivioBean transazioneArchivioBean, TransazioneBean transazioneBean,
			String idTransazione) throws ArchivioZipException {
		if(transazioneArchivioBean.getTracceRaw() != null) {
			try {
				log.debug("Decodifica tracce per la transazione {}:", idTransazione);

				List<TracciaBean> tracce = DeserializerTransazioni.readTracce(transazioneArchivioBean.getTracceRaw(), transazioneBean, this.exportTracceUseProtocolSerialization);

				log.debug("Lettura tracce completata.");

				if(!tracce.isEmpty()) {
					transazioneArchivioBean.getTracce().addAll(tracce);
				}

			}catch (IOException | SAXException | ParserConfigurationException | XMLException | ProtocolException |
					XPathException | XPathNotValidException | TransformerException | org.openspcoop2.core.tracciamento.utils.XMLUtilsException e) {
				this.logError(e);
				throw new ArchivioZipException(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(TransazioniCostanti.TRANSAZIONI_SEARCH_ESAMINA_ARCHIVIO_ZIP_MESSAGGIO_ERRORE_FILE_ELEMENTO_TRANSAZIONE_TRACCE_NON_VALIDO_KEY, idTransazione));
			}
		}
	}

	private void elaboraConsegne() throws ArchivioZipException {
		for (Entry<String, TransazioneArchivioBean> transazioneEntry : this.mapTransazioni.entrySet()) {
			String idTransazione = transazioneEntry.getKey();
			TransazioneArchivioBean transazioneArchivioBean = transazioneEntry.getValue();
			TransazioneBean transazioneBean = transazioneArchivioBean.getTransazioneBean();
			
			if(!transazioneArchivioBean.getConsegne().isEmpty()) {
				Map<String, TransazioneApplicativoServerArchivioBean> mapConChiaviCorrette = new HashMap<>();
				for (Entry<String, TransazioneApplicativoServerArchivioBean> consegnaEntry : transazioneArchivioBean.getConsegne().entrySet()) {
					// elabora consegne	
					TransazioneApplicativoServerArchivioBean transazioneApplicativoServerArchivioBean = consegnaEntry.getValue();
					TransazioneApplicativoServerBean transazioneApplicativoServerBean = transazioneApplicativoServerArchivioBean.getTransazioneApplicativoServerBean();
					String nomeSA = transazioneApplicativoServerBean.getNomeServizioApplicativoErogatore();
					
					// protocollo
					if(transazioneApplicativoServerBean.getProtocollo() == null) {
						transazioneApplicativoServerBean.setProtocollo(transazioneBean.getProtocollo());
					}

					elaboraDiagnosticiConsegna(idTransazione, transazioneBean, transazioneApplicativoServerArchivioBean, transazioneApplicativoServerBean, nomeSA);
					
					// sistemo le chiavi perche' puo' capitare che il nomeSA non coincida con la directory
					mapConChiaviCorrette.put(transazioneApplicativoServerBean.getNomeServizioApplicativoErogatore(), transazioneApplicativoServerArchivioBean);
				}
				transazioneArchivioBean.getConsegne().clear();
				transazioneArchivioBean.getConsegne().putAll(mapConChiaviCorrette);
			}
		}
	}

	private void elaboraDiagnosticiConsegna(String idTransazione, TransazioneBean transazioneBean,
			TransazioneApplicativoServerArchivioBean transazioneApplicativoServerArchivioBean,
			TransazioneApplicativoServerBean transazioneApplicativoServerBean, String nomeSA)
			throws ArchivioZipException {
		// elabora diagnostici consegna
		if(transazioneApplicativoServerArchivioBean.getDiagnosticiRaw() != null) {
			try { 
				log.debug("Estraggo informazioni diagnostici per la consegna {} della transazione {}:", nomeSA, idTransazione);

				List<MsgDiagnostico> diagnostici = DeserializerTransazioni.readMsgDiagnostici(transazioneApplicativoServerArchivioBean.getDiagnosticiRaw(),
						transazioneBean, transazioneApplicativoServerBean, this.exportDiagnosticiUseProtocolSerialization);

				log.debug("Lettura diagnostici completata.");

				if(!diagnostici.isEmpty()) {
					transazioneApplicativoServerArchivioBean.getDiagnostici().addAll(diagnostici);
				}
			}catch (JAXBException | IOException | ProtocolException | ParserConfigurationException | SAXException | XMLUtilsException | TransformerException | XMLException | XPathException | XPathNotValidException e) {
				this.logError(e);
				throw new ArchivioZipException(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(TransazioniCostanti.TRANSAZIONI_SEARCH_ESAMINA_ARCHIVIO_ZIP_MESSAGGIO_ERRORE_FILE_ELEMENTO_TRANSAZIONE_DIAGNOSTICI_CONSEGNA_NON_VALIDO_KEY, nomeSA, idTransazione));
			}
		}
	}

	private void estraiContenuti(ZipEntry entry, String chiave, String chiaveConsegna, byte[] fileContent) throws ArchivioZipException {
		String chiaveContenuto;
		String nomeContenuto;
		TipoMessaggio tipoMessaggio;
		if (entry.getName().contains(File.separatorChar + FOLDER_CONTENUTI)) {
			TransazioneArchivioBean transazionearchivioBean =  this.mapTransazioni.remove(chiave);

			if(transazionearchivioBean == null) {
				transazionearchivioBean = new TransazioneArchivioBean(chiave);	
			}

			if(chiaveConsegna != null) {
				// Estrai il nome del contenuto dal path
				chiaveContenuto = extractTipoContenutoConnettoreFromPath(entry.getName(), chiave, chiaveConsegna);
				nomeContenuto = extractNomeContenutoConsegnaFromPath(entry.getName(), chiave, chiaveConsegna, chiaveContenuto);

				log.debug("Estraggo contenuto {} per il messaggio {} della consegna {} della transazione {} dal file {}:", nomeContenuto, chiaveContenuto, chiaveConsegna, chiave, entry.getName());
				TransazioneApplicativoServerArchivioBean consegnaArchivioBean = transazionearchivioBean.getConsegne().remove(chiaveConsegna);

				if(consegnaArchivioBean == null) {
					consegnaArchivioBean = new TransazioneApplicativoServerArchivioBean(chiaveConsegna);
				}

				tipoMessaggio = getTipoMessaggioFromDirName(chiaveContenuto);

				Map<TipoMessaggio, ContenutiTransazioneArchivioBean> contenuti = consegnaArchivioBean.getContenuti();

				estraiContenuto(fileContent, chiave, chiaveConsegna, nomeContenuto, tipoMessaggio, contenuti);

				transazionearchivioBean.getConsegne().put(chiaveConsegna, consegnaArchivioBean);
			} else {
				// Estrai il nome del contenuto dal path
				chiaveContenuto = extractTipoContenutoFromPath(entry.getName(), chiave);
				nomeContenuto = extractNomeContenutoFromPath(entry.getName(), chiave, chiaveContenuto);

				log.debug("Estraggo contenuto {} per il messaggio {} per la transazione {} dal file {}:", nomeContenuto, chiaveContenuto, chiave, entry.getName());
				tipoMessaggio = getTipoMessaggioFromDirName(chiaveContenuto);

				Map<TipoMessaggio, ContenutiTransazioneArchivioBean> contenuti = transazionearchivioBean.getContenuti();

				estraiContenuto(fileContent, chiave, chiaveConsegna, nomeContenuto, tipoMessaggio, contenuti);	
			}

			this.mapTransazioni.put(chiave, transazionearchivioBean);
		}
	}

	private void estraiContenuto(byte[] fileContent, String chiave, String chiaveConsegna, String nomeContenuto, TipoMessaggio tipoMessaggio,
			Map<TipoMessaggio, ContenutiTransazioneArchivioBean> contenuti) throws ArchivioZipException {
		ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean = contenuti.remove(tipoMessaggio);

		if (contenutiTransazioneArchivioBean == null) {
			contenutiTransazioneArchivioBean = new ContenutiTransazioneArchivioBean();
		}

		if (nomeContenuto != null) {
			try {
				estraiHeadersContenuto(fileContent, nomeContenuto, contenutiTransazioneArchivioBean);
				estraiHeadersMultipartContenuto(fileContent, nomeContenuto, contenutiTransazioneArchivioBean);
				estraiContenutiContenuto(fileContent, nomeContenuto, contenutiTransazioneArchivioBean);
				estraiManifestContenuto(fileContent, nomeContenuto, contenutiTransazioneArchivioBean);
				if (nomeContenuto.contains(NOME_FILE_MESSAGE)) {
					contenutiTransazioneArchivioBean.setMessage(fileContent);
				}

				if(nomeContenuto.contains(NOME_FILE_ALLEGATI)) {
					estratiAllegato(fileContent, chiave, chiaveConsegna, nomeContenuto, contenutiTransazioneArchivioBean);
				}
			}catch (IOException | JAXBException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				this.logError(e);
				throw new ArchivioZipException(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(TransazioniCostanti.TRANSAZIONI_SEARCH_ESAMINA_ARCHIVIO_ZIP_MESSAGGIO_ERRORE_FILE_ELEMENTO_TRANSAZIONE_CONTENUTO_NON_VALIDO_KEY, nomeContenuto, chiave));
			}
		}

		contenuti.put(tipoMessaggio, contenutiTransazioneArchivioBean);
	}

	private void estraiManifestContenuto(byte[] fileContent, String nomeContenuto,
			ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean) throws IOException {
		if (nomeContenuto.contains(NOME_FILE_MANIFEST_TXT)) {
			Map<String,String> contenutiToMap = DeserializerTransazioni.readManifestContenutiToMap(fileContent);

			contenutiTransazioneArchivioBean.setContentType(readFromMap(contenutiToMap, MANIFEST_KEY_CONTENT_TYPE, String.class));
			contenutiTransazioneArchivioBean.setContentLength(readFromMap(contenutiToMap, MANIFEST_KEY_CONTENT_LENGTH, Long.class));
			contenutiTransazioneArchivioBean.setMessageType(readFromMap(contenutiToMap, MANIFEST_KEY_MESSAGE_TYPE, String.class));
			contenutiTransazioneArchivioBean.setTransactionId(readFromMap(contenutiToMap, MANIFEST_KEY_TRANSACTION_ID, String.class));
			contenutiTransazioneArchivioBean.setProtocol(readFromMap(contenutiToMap, MANIFEST_KEY_PROTOCOL, String.class));
		}
	}

	private void estraiContenutiContenuto(byte[] fileContent, String nomeContenuto,
			ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean) throws IOException, JAXBException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(nomeContenuto.contains(NOME_FILE_CONTENTS)) {
			List<DumpContenuto> contenutiXml = DeserializerTransazioni.readContenutiXml(fileContent, this.contenutiAsProperties);
			if(!contenutiXml.isEmpty()) {
				contenutiTransazioneArchivioBean.getContenuti().addAll(contenutiXml);
			}
		}
	}

	private void estraiHeadersMultipartContenuto(byte[] fileContent, String nomeContenuto,
			ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean) throws IOException, JAXBException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(nomeContenuto.contains(NOME_FILE_MESSAGE_MULTIPART_HEADERS)) {
			List<DumpMultipartHeader> multipartHeaderXml = DeserializerTransazioni.readMultipartHeaderXml(fileContent, this.headersAsProperties);
			if(!multipartHeaderXml.isEmpty()) {
				contenutiTransazioneArchivioBean.getHeadersMultiPart().addAll(multipartHeaderXml);
			}
		}
	}

	private void estraiHeadersContenuto(byte[] fileContent, String nomeContenuto,
			ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean) throws IOException, JAXBException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(nomeContenuto.contains(NOME_FILE_HEADERS_TXT)) {
			List<DumpHeaderTrasporto> headersTrasportoXml = DeserializerTransazioni.readHeadersTrasportoXml(fileContent, this.headersAsProperties);
			if(!headersTrasportoXml.isEmpty()) {
				contenutiTransazioneArchivioBean.getHeaders().addAll(headersTrasportoXml);
			}
		}
	}



	private void estratiAllegato(byte[] fileContent, String chiave, String chiaveConsegna, String nomeContenuto, 
			ContenutiTransazioneArchivioBean contenutiTransazioneArchivioBean) throws ArchivioZipException {

		String idAllegato = null;

		if(chiaveConsegna != null) {
			idAllegato = extractIdAllegatoConsegnaFromPath(nomeContenuto, chiave, chiaveConsegna, nomeContenuto);
		} else {
			idAllegato = extractIdAllegatoFromPath(nomeContenuto, chiave, nomeContenuto);
		}

		try {
			DumpAllegato dumpAllegato = contenutiTransazioneArchivioBean.getAllegati().remove(idAllegato);

			if(dumpAllegato == null) {
				dumpAllegato = new DumpAllegato();
			}

			// posso avere due file per allegato, il dump vero e proprio e il file con gli headers

			if (nomeContenuto.contains(NOME_FILE_ALLEGATO_MULTIPART_HEADERS)) {
				List<DumpHeaderAllegato> allegatoHeaderXml = DeserializerTransazioni.readAllegatoHeaderXml(fileContent, this.contenutiAsProperties);

				if (allegatoHeaderXml != null && !allegatoHeaderXml.isEmpty()) {
					dumpAllegato.getHeader().addAll(allegatoHeaderXml);
				}
			}

			if (nomeContenuto.contains(NOME_FILE_ALLEGATO+".")) {
				dumpAllegato.setAllegato(fileContent);
			}

			// salvo allegato
			contenutiTransazioneArchivioBean.getAllegati().put(idAllegato, dumpAllegato);

		}catch (IOException | JAXBException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			this.logError(e);
			if(chiaveConsegna == null) {
				throw new ArchivioZipException(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(TransazioniCostanti.TRANSAZIONI_SEARCH_ESAMINA_ARCHIVIO_ZIP_MESSAGGIO_ERRORE_FILE_ELEMENTO_TRANSAZIONE_ALLEGATO_NON_VALIDO_KEY, idAllegato, chiave));
			} else {
				throw new ArchivioZipException(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(TransazioniCostanti.TRANSAZIONI_SEARCH_ESAMINA_ARCHIVIO_ZIP_MESSAGGIO_ERRORE_FILE_ELEMENTO_TRANSAZIONE_ALLEGATO_CONSEGNA_NON_VALIDO_KEY, idAllegato, chiaveConsegna, chiave));
			}
		}
	}

	private void estraiTracce(ZipEntry entry, String chiave, String chiaveConsegna, byte[] fileContent) {
		if (entry.getName().endsWith(File.separatorChar + NOME_FILE_TRACCE_XML)) {
			TransazioneArchivioBean transazionearchivioBean =  this.mapTransazioni.remove(chiave);

			if(transazionearchivioBean == null) {
				transazionearchivioBean = new TransazioneArchivioBean(chiave);	
			}

			if(chiaveConsegna != null) {
				log.warn("non dovrebbe esistere un file tracce per la consegna {} della transazione {} dal file {}:", chiaveConsegna, chiave, entry.getName());
			} else {
				log.debug("Estraggo informazioni tracce raw per la transazione {} dal file {}:", chiave, entry.getName());
				transazionearchivioBean.setTracceRaw(fileContent);
				log.debug("Lettura tracce raw completata.");
			}

			this.mapTransazioni.put(chiave, transazionearchivioBean);
		}
	}

	private void estraiFaultIntegrazione(ZipEntry entry, String chiave, String chiaveConsegna, byte[] fileContent) {
		if (entry.getName().contains(File.separatorChar + NOME_FILE_FAULT_INTEGRAZIONE)) {
			String fault = new String(fileContent);
			TransazioneArchivioBean transazionearchivioBean =  this.mapTransazioni.remove(chiave);

			if(transazionearchivioBean == null) {
				transazionearchivioBean = new TransazioneArchivioBean(chiave);	
			}

			// estrai diagnostici consegna
			if(chiaveConsegna != null) {
				log.warn("non dovrebbe esistere un faultIntegrazione per la consegna {} della transazione {} dal file {}:", chiaveConsegna, chiave, entry.getName());
			} else {
				log.debug("Estraggo informazioni faultIntegrazione per la transazione {} dal file {}:", chiave, entry.getName());
				TransazioneBean transazioneBean = transazionearchivioBean.getTransazioneBean();
				if(transazioneBean == null) {
					transazioneBean = new TransazioneBean();
					transazionearchivioBean.setTransazioneBean(transazioneBean);
				}

				transazioneBean.setFaultIntegrazione(fault);
			}

			this.mapTransazioni.put(chiave, transazionearchivioBean);
		}
	}

	private void estraiFaultCooperazione(ZipEntry entry, String chiave, String chiaveConsegna, byte[] fileContent) {
		if (entry.getName().contains(File.separatorChar + NOME_FILE_FAULT_COOPERAZIONE)) {
			String fault = new String(fileContent);
			TransazioneArchivioBean transazionearchivioBean =  this.mapTransazioni.remove(chiave);

			if(transazionearchivioBean == null) {
				transazionearchivioBean = new TransazioneArchivioBean(chiave);	
			}

			// estrai diagnostici consegna
			if(chiaveConsegna != null) {
				log.warn("non dovrebbe esistere un faultCooperazione per la consegna {} della transazione {} dal file {}:", chiaveConsegna, chiave, entry.getName());
			} else {
				log.debug("Estraggo informazioni faultCooperazione per la transazione {} dal file {}:", chiave, entry.getName());
				TransazioneBean transazioneBean = transazionearchivioBean.getTransazioneBean();
				if(transazioneBean == null) {
					transazioneBean = new TransazioneBean();
					transazionearchivioBean.setTransazioneBean(transazioneBean);
				}

				transazioneBean.setFaultCooperazione(fault);
			}

			this.mapTransazioni.put(chiave, transazionearchivioBean);
		}
	}

	private void estraiFaultUltimoErrore(ZipEntry entry, String chiave, String chiaveConsegna, byte[] fileContent) {
		if (entry.getName().contains(File.separatorChar + NOME_FILE_FAULT_ULTIMO_ERRORE)) {
			String fault = new String(fileContent);
			TransazioneArchivioBean transazionearchivioBean =  this.mapTransazioni.remove(chiave);

			if(transazionearchivioBean == null) {
				transazionearchivioBean = new TransazioneArchivioBean(chiave);	
			}

			// estrai diagnostici consegna
			if(chiaveConsegna != null) {
				log.debug("Estraggo informazioni faultUltimoErrore per la consegna {} della transazione {} dal file {}:", chiaveConsegna, chiave, entry.getName());

				TransazioneApplicativoServerArchivioBean consegnaArchivioBean = transazionearchivioBean.getConsegne().remove(chiaveConsegna);

				if(consegnaArchivioBean == null) {
					consegnaArchivioBean = new TransazioneApplicativoServerArchivioBean(chiaveConsegna);
				}

				TransazioneApplicativoServerBean transazioneApplicativoServerBean = consegnaArchivioBean.getTransazioneApplicativoServerBean();
				if(transazioneApplicativoServerBean == null) {
					transazioneApplicativoServerBean = new TransazioneApplicativoServerBean();
					consegnaArchivioBean.setTransazioneApplicativoServerBean(transazioneApplicativoServerBean);
				}

				transazioneApplicativoServerBean.setFaultUltimoErrore(fault);
				transazionearchivioBean.getConsegne().put(chiaveConsegna, consegnaArchivioBean);
			} else {
				log.warn("non dovrebbe esistere un faultUltimoErrore non associato a nessuna consegna per la transazione {} dal file {}:", chiave, entry.getName());
			}

			this.mapTransazioni.put(chiave, transazionearchivioBean);
		}
	}

	private void estraiFault(ZipEntry entry, String chiave, String chiaveConsegna, byte[] fileContent) {
		if (entry.getName().contains(File.separatorChar + NOME_FILE_FAULT +".")) {
			String fault = new String(fileContent);
			TransazioneArchivioBean transazionearchivioBean =  this.mapTransazioni.remove(chiave);

			if(transazionearchivioBean == null) {
				transazionearchivioBean = new TransazioneArchivioBean(chiave);	
			}

			// estrai diagnostici consegna
			if(chiaveConsegna != null) {
				log.debug("Estraggo informazioni fault per la consegna {} della transazione {} dal file {}:", chiaveConsegna, chiave, entry.getName());

				TransazioneApplicativoServerArchivioBean consegnaArchivioBean = transazionearchivioBean.getConsegne().remove(chiaveConsegna);

				if(consegnaArchivioBean == null) {
					consegnaArchivioBean = new TransazioneApplicativoServerArchivioBean(chiaveConsegna);
				}

				TransazioneApplicativoServerBean transazioneApplicativoServerBean = consegnaArchivioBean.getTransazioneApplicativoServerBean();
				if(transazioneApplicativoServerBean == null) {
					transazioneApplicativoServerBean = new TransazioneApplicativoServerBean();
					consegnaArchivioBean.setTransazioneApplicativoServerBean(transazioneApplicativoServerBean);
				}

				transazioneApplicativoServerBean.setFault(fault);
				transazionearchivioBean.getConsegne().put(chiaveConsegna, consegnaArchivioBean);
			} else {
				log.warn("non dovrebbe esistere un fault non associato a nessuna consegna per la transazione {} dal file {}:", chiave, entry.getName());
			}

			this.mapTransazioni.put(chiave, transazionearchivioBean);
		}
	}

	private void estraiDiagnostici(ZipEntry entry, String chiave, String chiaveConsegna, byte[] fileContent) throws ArchivioZipException {
		// diagnostici
		if (entry.getName().endsWith(File.separatorChar + NOME_FILE_MSG_DIAGNOSTICI_XML)) {
			TransazioneArchivioBean transazionearchivioBean =  this.mapTransazioni.remove(chiave);

			if(transazionearchivioBean == null) {
				transazionearchivioBean = new TransazioneArchivioBean(chiave);	
			}

			// estrai diagnostici consegna
			if(chiaveConsegna != null) {
				log.debug("Estraggo informazioni diagnostici raw per la consegna {} della transazione {} dal file {}:", chiaveConsegna, chiave, entry.getName());

				TransazioneApplicativoServerArchivioBean consegnaArchivioBean = transazionearchivioBean.getConsegne().remove(chiaveConsegna);

				if(consegnaArchivioBean == null) {
					consegnaArchivioBean = new TransazioneApplicativoServerArchivioBean(chiaveConsegna);
				}

				consegnaArchivioBean.setDiagnosticiRaw(fileContent);
				log.debug("Lettura diagnostici raw completata.");
				transazionearchivioBean.getConsegne().put(chiaveConsegna, consegnaArchivioBean);
			} else {
				log.debug("Estraggo informazioni diagnostici raw per la transazione {} dal file {}:", chiave, entry.getName());
				transazionearchivioBean.setDiagnosticiRaw(fileContent);
				log.debug("Lettura diagnostici raw completata.");
			}

			this.mapTransazioni.put(chiave, transazionearchivioBean);
		}
	}

	private void estraiFileManifest(ZipEntry entry, String chiave, String chiaveConsegna, byte[] fileContent) throws ArchivioZipException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		// manifest
		if (entry.getName().endsWith(File.separatorChar + NOME_FILE_MANIFEST_XML)) {
			TransazioneArchivioBean transazionearchivioBean =  this.mapTransazioni.remove(chiave);

			if(transazionearchivioBean == null) {
				transazionearchivioBean = new TransazioneArchivioBean(chiave);	
			}

			try { 

				// estrai manifest consegna
				if(chiaveConsegna != null) {
					estraiManifestConsegna(entry, chiave, chiaveConsegna, fileContent, transazionearchivioBean);
				} else {
					estraiManifestTransazione(entry, chiave, fileContent, transazionearchivioBean);
				}

				this.mapTransazioni.put(chiave, transazionearchivioBean);
			}catch (UtilsException | JAXBException | IOException e) {
				this.logError(e);
				if(chiaveConsegna == null) {
					throw new ArchivioZipException(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(TransazioniCostanti.TRANSAZIONI_SEARCH_ESAMINA_ARCHIVIO_ZIP_MESSAGGIO_ERRORE_FILE_ELEMENTO_TRANSAZIONE_MANIFEST_NON_VALIDO_KEY, chiave));
				} else {
					throw new ArchivioZipException(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(TransazioniCostanti.TRANSAZIONI_SEARCH_ESAMINA_ARCHIVIO_ZIP_MESSAGGIO_ERRORE_FILE_ELEMENTO_TRANSAZIONE_MANIFEST_CONSEGNA_NON_VALIDO_KEY, chiaveConsegna, chiave));
				}
			}
		}
	}

	private void estraiManifestTransazione(ZipEntry entry, String chiave, byte[] fileContent,
			TransazioneArchivioBean transazionearchivioBean) throws UtilsException, JAXBException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		log.debug("Estraggo informazioni transazione {} dal file {}:", chiave, entry.getName());
		// Leggi il manifest.xml e crea un oggetto TransazioneBean
		TransazioneBean transazioneBean = DeserializerTransazioni.readManifestTransazione(fileContent);
		log.debug("oggetto transazione bean creato.");

		// controllo che non esista gia' una transazione creata leggendo i fault
		if (transazionearchivioBean.getTransazioneBean() != null) {
			TransazioneBean transazioneBean2 = transazionearchivioBean.getTransazioneBean();

			transazioneBean.setFaultCooperazione(transazioneBean2.getFaultCooperazione());
			transazioneBean.setFaultIntegrazione(transazioneBean2.getFaultIntegrazione());
		}

		transazionearchivioBean.setTransazioneBean(transazioneBean);
	}

	private void estraiManifestConsegna(ZipEntry entry, String chiave, String chiaveConsegna, byte[] fileContent,
			TransazioneArchivioBean transazionearchivioBean) throws JAXBException, UtilsException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		log.debug("Estraggo informazioni consegna {} della transazione {} dal file {}:", chiaveConsegna, chiave, entry.getName());
		// Leggi il manifest.xml e crea un oggetto TransazioneServizioApplicativoBean
		TransazioneApplicativoServerBean transazioneApplicativoServerBean = DeserializerTransazioni.readManifestTransazioneApplicativoServer(fileContent);
		log.debug("oggetto transazione bean creato.");
		TransazioneApplicativoServerArchivioBean consegnaArchivioBean = transazionearchivioBean.getConsegne().remove(chiaveConsegna);

		if(consegnaArchivioBean == null) {
			consegnaArchivioBean = new TransazioneApplicativoServerArchivioBean(chiaveConsegna);
		}

		// controllo che non esista gia' una transazioneSA creata leggendo i fault
		if(consegnaArchivioBean.getTransazioneApplicativoServerBean() != null) {
			TransazioneApplicativoServerBean transazioneApplicativoServerBean2 = consegnaArchivioBean.getTransazioneApplicativoServerBean();

			transazioneApplicativoServerBean.setFault(transazioneApplicativoServerBean2.getFault());
			transazioneApplicativoServerBean.setFaultUltimoErrore(transazioneApplicativoServerBean2.getFaultUltimoErrore());
		}

		transazioneApplicativoServerBean.setId((long) transazionearchivioBean.getConsegne().size() + 1);
		consegnaArchivioBean.setTransazioneApplicativoServerBean(transazioneApplicativoServerBean);
		transazionearchivioBean.getConsegne().put(chiaveConsegna, consegnaArchivioBean);
	}

	private String estraiChiaveConsegna(ZipEntry entry, String chiave) {
		String chiaveConsegna = null;
		// gestione consegne
		if (entry.getName().contains(File.separatorChar + FOLDER_CONSEGNE)) {
			// Estrai la chiave dal nome del file (prima di /manifest.xml)
			chiaveConsegna = extractNomeConnettoreFromPath(entry.getName(), chiave);
			log.debug("Estratto ID Connettore: {}", chiaveConsegna);
		}

		return chiaveConsegna;
	}

	private static byte[] readFileContent(ZipInputStream zis) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = zis.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		return outputStream.toByteArray();
	}

	private static String extractKeyFromPath(String path) {
		// Rimozione di "Transazioni/"
		int transazioniIndex = path.indexOf(ROOT_FOLDER_TRANSAZIONI+File.separatorChar);
		if (transazioniIndex != -1) {
			path = path.substring(transazioniIndex + (ROOT_FOLDER_TRANSAZIONI+File.separatorChar).length());
		}

		// Rimozione di tutto quello che c'e' dopo il primo "/"
		int endIndex = path.indexOf(File.separatorChar);
		if (endIndex != -1) {
			return path.substring(0, endIndex);
		}

		return null; 
	}

	private static String extractTipoContenutoFromPath(String path, String chiave) {
		// Rimozione di "Transazioni/CHIAVE/contenuti/"
		String pathToNomeContenuto = ROOT_FOLDER_TRANSAZIONI +File.separatorChar + chiave + File.separatorChar + FOLDER_CONTENUTI + File.separatorChar;
		int transazioniIndex = path.indexOf(pathToNomeContenuto);
		if (transazioniIndex != -1) {
			path = path.substring(transazioniIndex + pathToNomeContenuto.length());
		}

		// Rimozione di tutto quello che c'e' dopo il primo "/"
		int endIndex = path.indexOf(File.separatorChar);
		if (endIndex != -1) {
			return path.substring(0, endIndex);
		}

		return null; 
	}

	private static String extractNomeConnettoreFromPath(String path, String chiave) {
		// Rimozione di "Transazioni/CHIAVE/consegne/"
		String pathToNomeConnettore = ROOT_FOLDER_TRANSAZIONI +File.separatorChar + chiave + File.separatorChar + FOLDER_CONSEGNE + File.separatorChar;
		int transazioniIndex = path.indexOf(pathToNomeConnettore);
		if (transazioniIndex != -1) {
			path = path.substring(transazioniIndex + pathToNomeConnettore.length());
		}

		// Rimozione di tutto quello che c'e' dopo il primo "/"
		int endIndex = path.indexOf(File.separatorChar);
		if (endIndex != -1) {
			return path.substring(0, endIndex);
		}

		return null; 
	}

	private static String extractTipoContenutoConnettoreFromPath(String path, String chiave, String connettore) {
		// Rimozione di "Transazioni/CHIAVE/consegne/CONSEGNA/contenuti/"
		String pathToNomeContenuto = ROOT_FOLDER_TRANSAZIONI +File.separatorChar + chiave + File.separatorChar  + FOLDER_CONSEGNE + File.separatorChar + connettore + File.separatorChar + FOLDER_CONTENUTI + File.separatorChar;
		int transazioniIndex = path.indexOf(pathToNomeContenuto);
		if (transazioniIndex != -1) {
			path = path.substring(transazioniIndex + pathToNomeContenuto.length());
		}

		// Rimozione di tutto quello che c'e' dopo il primo "/"
		int endIndex = path.indexOf(File.separatorChar);
		if (endIndex != -1) {
			return path.substring(0, endIndex);
		}

		return null; 
	}

	private static String extractNomeContenutoFromPath(String path, String chiave, String contenuto) {
		// Rimozione di "Transazioni/CHIAVE/contenuti/CONTENUTO/"
		String pathToNomeContenuto = ROOT_FOLDER_TRANSAZIONI +File.separatorChar + chiave + File.separatorChar + FOLDER_CONTENUTI + File.separatorChar + contenuto + File.separator;
		int transazioniIndex = path.indexOf(pathToNomeContenuto);
		if (transazioniIndex != -1) {
			path = path.substring(transazioniIndex + pathToNomeContenuto.length());
		}
		return path; 
	}

	private static String extractNomeContenutoConsegnaFromPath(String path, String chiave, String connettore, String contenuto) {
		// Rimozione di "Transazioni/CHIAVE/consegne/CONSEGNA/contenuti/CONTENUTO/"
		String pathToNomeContenuto = ROOT_FOLDER_TRANSAZIONI +File.separatorChar + chiave + File.separatorChar + FOLDER_CONSEGNE + File.separatorChar +  connettore  + File.separatorChar + FOLDER_CONTENUTI + File.separatorChar + contenuto + File.separator;
		int transazioniIndex = path.indexOf(pathToNomeContenuto);
		if (transazioniIndex != -1) {
			path = path.substring(transazioniIndex + pathToNomeContenuto.length());
		}
		return path; 
	}

	private static String extractIdAllegatoFromPath(String path, String chiave, String contenuto) {
		// Rimozione di "Transazioni/CHIAVE/contenuti/CONTENUTO/allegati/allegato_"
		String pathToIdAllegato = ROOT_FOLDER_TRANSAZIONI +File.separatorChar + chiave 
				+ File.separatorChar + FOLDER_CONTENUTI + File.separatorChar + contenuto + File.separator + NOME_FILE_ALLEGATI + File.separator + NOME_FILE_ALLEGATO + "_";
		int transazioniIndex = path.indexOf(pathToIdAllegato);
		if (transazioniIndex != -1) {
			path = path.substring(transazioniIndex + pathToIdAllegato.length());
		}

		// Rimozione di tutto quello che c'e' dopo il primo "/"
		int endIndex = path.indexOf(File.separatorChar);
		if (endIndex != -1) {
			return path.substring(0, endIndex);
		}

		return path; 
	}

	private static String extractIdAllegatoConsegnaFromPath(String path, String chiave, String connettore, String contenuto) {
		// Rimozione di "Transazioni/CHIAVE/consegne/CONSEGNA/contenuti/CONTENUTO/allegati/allegato_"
		String pathToIdAllegato = ROOT_FOLDER_TRANSAZIONI +File.separatorChar + chiave + File.separatorChar + FOLDER_CONSEGNE + File.separatorChar +  connettore
				+ File.separatorChar + FOLDER_CONTENUTI + File.separatorChar + contenuto + File.separator + NOME_FILE_ALLEGATI + File.separator + NOME_FILE_ALLEGATO + "_";
		int transazioniIndex = path.indexOf(pathToIdAllegato);
		if (transazioniIndex != -1) {
			path = path.substring(transazioniIndex + pathToIdAllegato.length());
		}

		// Rimozione di tutto quello che c'e' dopo il primo "/"
		int endIndex = path.indexOf(File.separatorChar);
		if (endIndex != -1) {
			return path.substring(0, endIndex);
		}

		return path; 
	}


	public static TipoMessaggio getTipoMessaggioFromDirName(String contenuto) {
		switch (contenuto) {
		case "dati_richiesta_ingresso": {
			return TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO;
		}
		case "dati_richiesta_uscita": {
			return TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO;
		}
		case "dati_risposta_ingresso": {
			return TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO;
		}
		case "dati_risposta_uscita": {
			return TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO;
		}
		case "richiesta_ingresso": {
			return TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO;
		}
		case "richiesta_uscita": {
			return TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO;
		}
		case "risposta_ingresso": {
			return TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO;
		}
		case "risposta_uscita": {
			return TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO;
		}
		case "integration_manager": {
			return TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + contenuto);
		}
	}

	public static <T> T readFromMap(Map<String, String> map, String chiave, Class<T> clazz) {
		T toReturn = null;

		String paramAsString = map.get(chiave);

		if(paramAsString != null) {
			try {
				Constructor<T> constructor = clazz.getConstructor(String.class);
				if(constructor != null)
					toReturn = constructor.newInstance(paramAsString);
				else
					toReturn = clazz.cast(paramAsString);
			}catch(Exception e) {
				return toReturn;
			}

		}
		return toReturn;
	}

	public Map<String, TransazioneArchivioBean> getMapTransazioni() {
		return this.mapTransazioni;
	}

	public void logError(Exception e) {
		log.error(e.getMessage(), e);
	}

	public boolean isInizializzaInformazioniSupplementari() {
		return this.inizializzaInformazioniSupplementari;
	}

	public void setInizializzaInformazioniSupplementari(boolean inizializzaInformazioniSupplementari) {
		this.inizializzaInformazioniSupplementari = inizializzaInformazioniSupplementari;
	}
}
