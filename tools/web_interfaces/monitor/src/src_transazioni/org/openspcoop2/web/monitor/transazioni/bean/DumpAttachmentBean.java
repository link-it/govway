/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.transazioni.bean;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.openspcoop2.message.utils.DumpAttachment;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.MimeTypeUtils;
import org.openspcoop2.web.monitor.transazioni.utils.DumpMessaggioUtils;
import org.slf4j.Logger;


/****
 * 
 * Bean per che incapsula un oggetto di tipo DumpAttachment, per la visualizzazione nella pagina web.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DumpAttachmentBean extends DumpAttachment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DumpAttachment origin = null;


	public DumpAttachmentBean(DumpAttachment dumpAllegato) {
		this.origin = dumpAllegato;
	}
	
	public String getMimeTypeImageClass() {
		
		return MimeTypeUtils.getMimeTypeImageClass(this.getContentType());
		
	}
	
	public byte[] decodeBase64(){
		Logger log = LoggerManager.getPddMonitorCoreLogger();
		return DumpMessaggioUtils.decodeAllegatoBase64(this.getContent(), this.getContentType(), this.getContentId(), log);
	}
	
	public boolean isBase64(){
		Logger log = LoggerManager.getPddMonitorCoreLogger();
		boolean b = DumpMessaggioUtils.isAllegatoBase64(this.getContent(), this.getContentType(), this.getContentId(), log); 
		
		log.debug("Allegato: " + this.getContentId()+ "base64: " + b);
		return b; 
	}

	@Override
	public String getContentId() {
		return this.origin.getContentId();
	}

	@Override
	public void setContentId(String contentId) {
		this.origin.setContentId(contentId);
	}

	@Override
	public String getContentLocation() {
		return this.origin.getContentLocation();
	}

	@Override
	public void setContentLocation(String contentLocation) {
		this.origin.setContentLocation(contentLocation);
	}

	@Override
	public String getContentType() {
		return this.origin.getContentType();
	}

	@Override
	public void setContentType(String contentType) {
		this.origin.setContentType(contentType);
	}

	@Override
	public String getErrorContentNotSerializable() {
		return this.origin.getErrorContentNotSerializable();
	}

	@Override
	public void setErrorContentNotSerializable(String errorContentNotSerializable) {
		this.origin.setErrorContentNotSerializable(errorContentNotSerializable);
	}

	@Override
	public long getContentLength() {
		return this.origin.getContentLength();
	}

	@Override
	public byte[] getContent() {
		return this.origin.getContent();
	}

	@Override
	public String getContentAsString(boolean ifPrintableContent) {
		return this.origin.getContentAsString(ifPrintableContent);
	}

	@Override
	public void setContent(ByteArrayOutputStream content) {
		this.origin.setContent(content);
	}
	
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public Map<String, String> getHeaders() {
		return this.origin.getHeaders();
	}

	@Override
	public Map<String, List<String>> getHeadersValues() {
		return this.origin.getHeadersValues();
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public void setHeaders(Map<String, String> headers) {
		this.origin.setHeaders(headers);
	}

	@Override
	public void setHeadersValues(Map<String, List<String>> headers) {
		this.origin.setHeadersValues(headers);
	}
	
	
}
