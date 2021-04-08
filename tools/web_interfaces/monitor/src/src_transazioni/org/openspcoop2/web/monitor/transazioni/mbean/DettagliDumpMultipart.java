/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.transazioni.mbean;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.utils.DumpUtils;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.utils.DumpAttachment;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.monitor.core.core.Utils;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.PdDBaseBean;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.core.utils.MimeTypeUtils;
import org.openspcoop2.web.monitor.transazioni.bean.DumpAttachmentBean;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.utils.DumpMessaggioUtils;
import org.slf4j.Logger;

/**
 * DettagliDump
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DettagliDumpMultipart extends PdDBaseBean<Transazione, String, ITransazioniService>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private transient Logger log =  LoggerManager.getPddMonitorCoreLogger();


	private String idTransazione;
	private String servizioApplicativoErogatore;
	private String protocollo = null;
	private TipoMessaggio tipoMessaggio;
	private Date dataConsegnaErogatore = null;
	private Date ultimaConsegna = null;

	private org.openspcoop2.message.utils.DumpMessaggio dumpMessaggio;
	private int dumpMessaggioLength;
	private DumpAttachmentBean selectedAttachment;
	private List<DumpAttachmentBean> attachments;
	
	private Boolean exportContenutiMultipart;

	private boolean base64Decode;
	
	
	public DettagliDumpMultipart() {
	}

	public void setBase64Decode(boolean base64Decode) {
		this.base64Decode = base64Decode;
	}

	public void setSelectedAttachment(DumpAttachmentBean selectedAttachment) {
		this.selectedAttachment = selectedAttachment;
	}

	public String getIdTransazione() {
		return this.idTransazione;
	}

	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}

	public String getServizioApplicativoErogatore() {
		return this.servizioApplicativoErogatore;
	}

	public void setServizioApplicativoErogatore(String servizioApplicativoErogatore) {
		this.servizioApplicativoErogatore = servizioApplicativoErogatore;
	}
	
	public Date getDataConsegnaErogatore() {
		return this.dataConsegnaErogatore;
	}

	public void setDataConsegnaErogatore(Date dataConsegnaErogatore) {
		this.dataConsegnaErogatore = dataConsegnaErogatore;
	}

	@Override
	public String getProtocollo() {
		return this.protocollo;
	}
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}

	public boolean isVisualizzaMessaggio(){
		boolean visualizzaMessaggio = true;
		if(this.dumpMessaggio!=null && this.dumpMessaggio.getBody()!=null) {
			if(this.dumpMessaggio.getBody() == null)
				return false;

			StringBuilder contenutoDocumentoStringBuilder = new StringBuilder();
			String errore = Utils.getTestoVisualizzabile(this.dumpMessaggio.getBody(),contenutoDocumentoStringBuilder, false, DumpUtils.getThreshold_readInMemory());
			if(errore!= null)
				return false;

			//			MessageType messageType= MessageType.XML;
			//			if(StringUtils.isNotEmpty(this.dumpMessaggio.getFormatoMessaggio())) {
			//				messageType = MessageType.valueOf(this.dumpMessaggio.getFormatoMessaggio());
			//			}

			//			switch (messageType) {
			//			case BINARY:
			//			case MIME_MULTIPART:
			//				// questi due casi dovrebbero essere gestiti sopra 
			//				break;	
			//			case JSON:
			//				JSONUtils jsonUtils = JSONUtils.getInstance(true);
			//				try {
			//					toRet = jsonUtils.toString(jsonUtils.getAsNode(this.dumpMessaggio.getBody()));
			//				} catch (UtilsException e) {
			//				}
			//				break;
			//			case SOAP_11:
			//			case SOAP_12:
			//			case XML:
			//			default:
			//				toRet = Utils.prettifyXml(this.dumpMessaggio.getBody());
			//				break;
			//			}
		}
		else if(this.dumpMessaggio!=null && this.dumpMessaggio.getBodyLength()>0) {
			return false;
		}

		return visualizzaMessaggio;
	}

	public String getPrettyEnvelop(){
		String toRet = null;
		if(this.dumpMessaggio!=null && this.dumpMessaggio.getBody()!=null) {
			StringBuilder contenutoDocumentoStringBuilder = new StringBuilder();
			String errore = Utils.getTestoVisualizzabile(this.dumpMessaggio.getBody(),contenutoDocumentoStringBuilder, true, DumpUtils.getThreshold_readInMemory());
			if(errore!= null)
				return "";

			// sicuramente da qui si hanno messaggi con "testo visualizzabile" e non troppo lunghi
			
			MessageType messageType = this.dumpMessaggio.getMessageType();

			if(messageType==null) {
				// puo' succedere nei casi binari
				toRet = this._getPrettyUnknownType();
			}
			else {
				switch (messageType) {
				case JSON:
					JSONUtils jsonUtils = JSONUtils.getInstance(true);
					try {
						toRet = jsonUtils.toString(jsonUtils.getAsNode(this.dumpMessaggio.getBody()));
					} catch (UtilsException e) {
					}
					break;
				case SOAP_11:
				case SOAP_12:
				case XML:
					toRet = Utils.prettifyXml(this.dumpMessaggio.getBody());
					break;
				case BINARY:
				case MIME_MULTIPART:
					toRet = this._getPrettyUnknownType();
					break;
				}
			}
		}

		if(toRet == null || "".equals(toRet)) {
			toRet = this.dumpMessaggio.getBody() != null ? new String(this.dumpMessaggio.getBody()) : "";
		}

		return toRet;
	}
	
	private String _getPrettyUnknownType() {
		String toRet = null;
		try {
			String contentType = this.dumpMessaggio.getContentType();
			if(ContentTypeUtilities.isMultipart(contentType)){
				contentType = ContentTypeUtilities.getInternalMultipartContentType(contentType);
			}
			String ext = MimeTypeUtils.fileExtensionForMIMEType(contentType);
			if("json".equals(ext) || contentType.contains("json")) {
				JSONUtils jsonUtils = JSONUtils.getInstance(true);
				try {
					toRet = jsonUtils.toString(jsonUtils.getAsNode(this.dumpMessaggio.getBody()));
				} catch (UtilsException e) {
				}
			}
			else if("xml".equals(ext) || contentType.contains("xml")) {
				toRet = Utils.prettifyXml(this.dumpMessaggio.getBody());
			}
			// else {
			// 	nop
			//}
		}catch(Exception e) {
			this.log.error(e.getMessage(),e);
		}
		return toRet;
	}

	public String getBrush() {
		
		// il brush verrà utilizzato solamente se il messaggio va visualizzato
		// decisione presa dal metodo: isVisualizzaMessaggio()
		
		String toRet = null;
		if(this.dumpMessaggio!=null && this.dumpMessaggio.getBody()!=null) {
			
			MessageType messageType = this.dumpMessaggio.getMessageType();

			if(messageType==null) {
				// puo' succedere nei casi binari
				toRet = this._getBrushUnknownType();
			}
			else {
				switch (messageType) {
				case JSON:
					toRet = "json";
					break;
				case SOAP_11:
				case SOAP_12:
				case XML:
				default:
					toRet = "xml";
					break;
				case MIME_MULTIPART:
				case BINARY:
					toRet = this._getBrushUnknownType();
					break;
				}
			}
		}

		return toRet!=null ? toRet : "xml";
	}

	private String _getBrushUnknownType() {
		String toRet = null;
		try {
			String contentType = this.dumpMessaggio.getContentType();
			if(ContentTypeUtilities.isMultipart(contentType)){
				contentType = ContentTypeUtilities.getInternalMultipartContentType(contentType);
			}
			String ext = MimeTypeUtils.fileExtensionForMIMEType(contentType);
			if("json".equals(ext) || contentType.contains("json")) {
				toRet = "json";
			}
			else {
				toRet = "xml"; // default
			}
		}catch(Exception e) {
			this.log.error(e.getMessage(),e);
			toRet = "xml"; // default
		}
		return toRet;
	}
	
	public String getErroreVisualizzaMessaggio(){
		if(this.dumpMessaggio!=null && this.dumpMessaggio.getBody()!=null) {
			StringBuilder contenutoDocumentoStringBuilder = new StringBuilder();
			String errore = Utils.getTestoVisualizzabile(this.dumpMessaggio.getBody(),contenutoDocumentoStringBuilder,false, DumpUtils.getThreshold_readInMemory());
			return errore;
		}
		else if(this.dumpMessaggio!=null && this.dumpMessaggio.getBodyLength()>0) {
			String errore = Utilities.getErrorMessagePrintableTextMaxLength((int) this.dumpMessaggio.getBodyLength(), DumpUtils.getThreshold_readInMemory());
			return errore;
		}

		return null;
	}

	public org.openspcoop2.message.utils.DumpMessaggio getDumpMessaggio(){
		if(this.dumpMessaggio!=null)
			return this.dumpMessaggio;

		try{
			DumpMessaggio tmp = null;
			try {
				if(this.ultimaConsegna == null)
					tmp = ((this.service)).getDumpMessaggio(this.idTransazione, this.servizioApplicativoErogatore, this.dataConsegnaErogatore, this.tipoMessaggio);
				else 
					tmp = ((this.service)).getDumpMessaggio(this.idTransazione, this.servizioApplicativoErogatore, this.ultimaConsegna, this.tipoMessaggio);
			} catch (Exception e) {
				this.log.error(e.getMessage(), e);
	
			}
			
			InputStream is = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if(tmp.getBody()!=null) {
				byte[] contenutoBody = tmp.getBody();
				is = new ByteArrayInputStream(contenutoBody);
			}
			else {
				try {
					if(this.ultimaConsegna == null)
						is = ((this.service)).getContentInputStream(this.idTransazione, this.servizioApplicativoErogatore, this.dataConsegnaErogatore, this.tipoMessaggio);
					else 
						is = ((this.service)).getContentInputStream(this.idTransazione, this.servizioApplicativoErogatore, this.ultimaConsegna, this.tipoMessaggio);
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		
			CopyStream.copy(is, baos);
			
			byte [] content = baos.toByteArray();
			String contentType = tmp.getContentType();
			
			this.dumpMessaggioLength = content.length;
			this.dumpMessaggio = DumpMessaggioUtils.getFromBytes(content, contentType);
			
		
		}catch (Exception e) {
			this.log.error(e.getMessage(), e);
			MessageUtils.addErrorMsg("Si e' verificato un errore durante il download del messaggio.");
		}
		

		return this.dumpMessaggio;
	}
	
	public int getDumpMessaggioLength() {
		getDumpMessaggio();
		return this.dumpMessaggioLength;
	}

	/***
	 * //  eliminato dalla tabella styleClass="#{allegato.mimeTypeImageClass}"
	 * 
	 * @return Lista degli Allegati
	 */
	public List<DumpAttachmentBean> getAllegati(){

		if(this.getDumpMessaggio()==null)
			return null;
		
		if(this.attachments == null) {
		List<DumpAttachment> list = this.dumpMessaggio.getAttachments();

		if(list.size()>0){
			this.attachments = new ArrayList<DumpAttachmentBean>();
			for (DumpAttachment dumpAllegato : list) {  
				this.attachments.add(new DumpAttachmentBean(dumpAllegato));
			}
			return this.attachments;
		}
		else{
			return null;
		}
		
		}
		return this.attachments;
	}

	public List<DumpHeaderTrasporto> getHeadersTrasporto(){

		if(this.getDumpMessaggio()==null)
			return null;
		
		
		if(this.getDumpMessaggio().getHeadersValues() != null && this.getDumpMessaggio().getHeadersValues().size() > 0) {
			
			List<DumpHeaderTrasporto> headers = new ArrayList<DumpHeaderTrasporto>();
			Map<String, String> toMapSingleValue = TransportUtils.convertToMapSingleValue(this.getDumpMessaggio().getHeadersValues());
			
			Iterator<String> iterator = toMapSingleValue.keySet().iterator();
			
			while(iterator.hasNext()) {
				String key = iterator.next();
				String value = toMapSingleValue.get(key);
				
				DumpHeaderTrasporto header = new DumpHeaderTrasporto();
				header.setNome(key);
				header.setValore(value);
				headers.add(header);
			}

			return headers;
		}
		
		return null;
	}


	public String downloadMessaggio(){
		this.log.debug("downloading messaggio...");
		try{
			//recupero informazioni sul file


			// We must get first our context
			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

			// Now we create some variables we will use for writting the file to the
			// response
			// String filePath = null;
//			int read = 0;
//			byte[] bytes = new byte[1024];

			// Be sure to retrieve the absolute path to the file with the required
			// method
			// filePath = pathToTheFile;

			// Now set the content type for our response, be sure to use the best
			// suitable content type depending on your file
			// the content type presented here is ok for, lets say, text files and
			// others (like CSVs, PDFs)
			response.setContentType(this.dumpMessaggio.getContentType());

			// This is another important attribute for the header of the response
			// Here fileName, is a String with the name that you will suggest as a
			// name to save as
			// I use the same name as it is stored in the file system of the server.
			//String fileName = "allegato_"+this.selectedAttachment.getId();
			// NOTA: L'id potrebbe essere -1 nel caso di mascheramento logico.
			String fileName = "messaggio";

			String ext = "bin";
			String contentType = this.dumpMessaggio.getContentType();
			
			try {
				if(contentType != null) {
					/*
					 * Fix: se è multipart è giusto vederlo come un binario
					if(ContentTypeUtilities.isMultipart(contentType)){
						contentType = ContentTypeUtilities.getInternalMultipartContentType(contentType);
					}*/
				}
				ext = MimeTypeUtils.fileExtensionForMIMEType(contentType);
			}catch(Exception e) {
				ext = "bin";
			}

			fileName+="."+ext;

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName, contentType);

			// Streams we will use to read, write the file bytes to our response
			InputStream is = null;
			OutputStream os = null;

			// First we load the file in our InputStream
			//			if(this.base64Decode){
			//				contenutoBody = ((DumpAllegatoBean)this.dumpMessaggio).decodeBase64();
			//			}
			byte[] contenutoBody = this.dumpMessaggio.getBody();
			is = new ByteArrayInputStream(contenutoBody);
			
//			if(this.dumpMessaggio.getBody()!=null) {
//				byte[] contenutoBody = this.dumpMessaggio.getBody();
//				is = new ByteArrayInputStream(contenutoBody);
//			}
//			else {
//				try {
//					if(this.ultimaConsegna == null)
//						is = ((this.service)).getContentInputStream(this.idTransazione, this.servizioApplicativoErogatore, this.dataConsegnaErogatore, this.tipoMessaggio);
//					else 
//						is = ((this.service)).getContentInputStream(this.idTransazione, this.servizioApplicativoErogatore, this.ultimaConsegna, this.tipoMessaggio);
//				} catch (Exception e) {
//					this.log.error(e.getMessage(), e);
//				}
//			}
			os = response.getOutputStream();

			// While there are still bytes in the file, read them and write them to
			// our OutputStream
//			while ((read = bis.read(bytes)) != -1) {
//				os.write(bytes, 0, read);
//			}
			CopyStream.copy(is, os);

			// Clean resources
			os.flush();
			os.close();

			FacesContext.getCurrentInstance().responseComplete();

			// End of the method
		}catch (Exception e) {
			this.log.error(e.getMessage(), e);
			MessageUtils.addErrorMsg("Si e' verificato un errore durante il download del messaggio.");
		}
		return null;
	}

	public String download(){
		this.log.debug("downloading allegato: "+this.selectedAttachment.getContentId());
		try{
			//recupero informazioni sul file


			// We must get first our context
			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

			// Now we create some variables we will use for writting the file to the
			// response
			// String filePath = null;
			int read = 0;
			byte[] bytes = new byte[1024];

			// Be sure to retrieve the absolute path to the file with the required
			// method
			// filePath = pathToTheFile;

			// Now set the content type for our response, be sure to use the best
			// suitable content type depending on your file
			// the content type presented here is ok for, lets say, text files and
			// others (like CSVs, PDFs)
			response.setContentType(this.selectedAttachment.getContentType());

			// This is another important attribute for the header of the response
			// Here fileName, is a String with the name that you will suggest as a
			// name to save as
			// I use the same name as it is stored in the file system of the server.
			//String fileName = "allegato_"+this.selectedAttachment.getId();
			// NOTA: L'id potrebbe essere -1 nel caso di mascheramento logico.
			String fileName = "allegato";

			String ext = MimeTypeUtils.fileExtensionForMIMEType(this.selectedAttachment.getContentType());

			fileName+="."+ext;

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName, this.selectedAttachment.getContentType());

			// Streams we will use to read, write the file bytes to our response
			ByteArrayInputStream bis = null;
			OutputStream os = null;

			// First we load the file in our InputStream
			byte[] contenutoAllegato = this.selectedAttachment.getContent();
			if(this.base64Decode){
				try{
					contenutoAllegato = DumpMessaggioUtils.decodeAllegatoBase64(this.selectedAttachment.getContent(), this.selectedAttachment.getContentType(), this.selectedAttachment.getContentId(), this.log);
				}catch(Exception e){
					this.log.error("IsBase64 error: "+e.getMessage(),e);
				}
			}
			bis = new ByteArrayInputStream(contenutoAllegato);
			os = response.getOutputStream();

			// While there are still bytes in the file, read them and write them to
			// our OutputStream
			while ((read = bis.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}

			// Clean resources
			os.flush();
			os.close();

			FacesContext.getCurrentInstance().responseComplete();

			// End of the method
		}catch (Exception e) {
			this.log.error(e.getMessage(), e);
			MessageUtils.addErrorMsg("Si e' verificato un errore durante il download dell'allegato.");
		}
		return null;
	}

	public String downloadAll(){

		this.log.debug("downloading all attachments");
		try{
			//recupero informazioni sul file


			// We must get first our context
			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

			// Now we create some variables we will use for writting the file to the
			// response
			// String filePath = null;
			byte[] bytes = new byte[1024];

			// Be sure to retrieve the absolute path to the file with the required
			// method
			// filePath = pathToTheFile;

			// This is another important attribute for the header of the response
			// Here fileName, is a String with the name that you will suggest as a
			// name to save as
			// I use the same name as it is stored in the file system of the server.

			String fileName = this.idTransazione +"-Attachments.zip";

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName);


			// Streams we will use to read, write the file bytes to our response
			// First we load the file in our InputStream
//			List<DumpAllegato> allegatiCore = ((this.service)).getAllegatiMessaggio(this.dumpMessaggio.getIdTransazione(), this.dumpMessaggio.getServizioApplicativoErogatore(), this.dumpMessaggio.getDataConsegnaErogatore(), this.dumpMessaggio.getTipoMessaggio(), this.dumpMessaggio.getId());

//			List<DumpAllegato> allegati = new ArrayList<DumpAllegato>();
//			for (DumpAllegato dumpAllegato : allegatiCore) {
//				allegati.add(new DumpAllegatoBean(dumpAllegato));
//			}

			ZipOutputStream zip = new ZipOutputStream(response.getOutputStream());
			InputStream in = null;

			int index = 1;
			for (DumpAttachment allegato : this.dumpMessaggio.getAttachments()) {

				String allegatofileName = "allegato_"+index;

				String allegatoExt = MimeTypeUtils.fileExtensionForMIMEType(allegato.getContentType());

				allegatofileName+="."+allegatoExt;
				zip.putNextEntry(new ZipEntry(allegatofileName));

				byte[] contenutoAllegato = allegato.getContent();
				in = new ByteArrayInputStream(contenutoAllegato);
				int len;
				while ((len = in.read(bytes)) > 0) {
					zip.write(bytes, 0, len);
				}
				zip.closeEntry();
				in.close();

				try{
					if(DumpMessaggioUtils.isAllegatoBase64(allegato.getContent(), allegato.getContentType(), allegato.getContentId(), this.log)){
						contenutoAllegato = DumpMessaggioUtils.decodeAllegatoBase64(allegato.getContent(), allegato.getContentType(), allegato.getContentId(), this.log);
						allegatofileName = "allegato_"+index+".decodeBase64";
						allegatofileName+="."+allegatoExt;
						zip.putNextEntry(new ZipEntry(allegatofileName));

						in = new ByteArrayInputStream(contenutoAllegato);
						while ((len = in.read(bytes)) > 0) {
							zip.write(bytes, 0, len);
						}
						zip.closeEntry();
						in.close();
					}
				}catch(Throwable e){
					this.log.error(e.getMessage(), e);
				}

				index++;
			}
			zip.flush();
			zip.close();

			FacesContext.getCurrentInstance().responseComplete();

			// End of the method
		}catch (Exception e) {
			this.log.error(e.getMessage(), e);
			MessageUtils.addErrorMsg("Si e' verificato un errore durante il download dell'allegato.");
		}
		return null;
	}

	public void setTipoMessaggio(String value) {
		if(value != null )
			this.tipoMessaggio = (TipoMessaggio) TipoMessaggio.toEnumConstantFromString(value);
	}

	public String getTipoMessaggio() {
		if(this.tipoMessaggio == null){
			return null;
		}else{
			return this.tipoMessaggio.toString();
		}
	}

	public org.openspcoop2.core.transazioni.constants.TipoMessaggio getTipoMessaggioEnum() {
		return this.tipoMessaggio;
	}

	public void setTipoMessaggioEnum(org.openspcoop2.core.transazioni.constants.TipoMessaggio tipoMessaggio) {
		this.tipoMessaggio = tipoMessaggio;
	}

	public String getTitoloPagina() {
		if(this.tipoMessaggio != null) {
			switch (this.tipoMessaggio) {
			case RICHIESTA_INGRESSO:
				return "Messaggio di Richiesta - Contenuti Ingresso";
			case RICHIESTA_USCITA:
				return "Messaggio di Richiesta - Contenuti Uscita";
			case RISPOSTA_INGRESSO:
				return "Messaggio di Risposta - Contenuti Ingresso";
			case RISPOSTA_USCITA:	
				return "Messaggio di Risposta - Contenuti Uscita";
			case RICHIESTA_INGRESSO_DUMP_BINARIO:
				return "Messaggio di Richiesta - Dati Ingresso";
			case RICHIESTA_USCITA_DUMP_BINARIO:
				return "Messaggio di Richiesta - Dati Uscita";
			case RISPOSTA_INGRESSO_DUMP_BINARIO:
				return "Messaggio di Risposta - Dati Ingresso";
			case RISPOSTA_USCITA_DUMP_BINARIO:
				return "Messaggio di Risposta - Dati Uscita";
			case INTEGRATION_MANAGER:
			default:
				return "Contenuti Messaggio";
			}
		}
		return "Contenuti Messaggio";
	}

	public Date getUltimaConsegna() {
		return this.ultimaConsegna;
	}

	public void setUltimaConsegna(Date ultimaConsegna) {
		this.ultimaConsegna = ultimaConsegna;
	}
	
	public Boolean getExportContenutiMultipart() {
		return this.exportContenutiMultipart;
	}
	public void setExportContenutiMultipart(Boolean exportContenutiMultipart) {
		this.exportContenutiMultipart = exportContenutiMultipart;
	}
}
