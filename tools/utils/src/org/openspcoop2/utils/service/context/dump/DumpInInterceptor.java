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

package org.openspcoop2.utils.service.context.dump;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.ext.logging.AbstractLoggingInterceptor;
import org.apache.cxf.ext.logging.event.DefaultLogEventMapper;
import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedWriter;
import org.apache.cxf.message.Message;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.service.context.server.ServerConfig;


/**
 * DumpInInterceptor
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@NoJSR250Annotations
public class DumpInInterceptor extends org.apache.cxf.ext.logging.LoggingInInterceptor {

	private DumpConfig dumpConfig;
	private ServerConfig serverConfig;
	
	public DumpInInterceptor() {
		super();
	}

	public DumpConfig getDumpConfig() {
		return this.dumpConfig;
	}
	public void setDumpConfig(DumpConfig dumpConfig) {
		this.dumpConfig = dumpConfig;
		if(dumpConfig.getLimit()!=null) {
			super.setLimit(dumpConfig.getLimit());
		}
		else {
			super.setLimit(-1);
		}
	}
	
	public ServerConfig getServerConfig() {
		return this.serverConfig;
	}
	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	@Override
	public void handleMessage(Message message) throws Fault {

		try {
		
			Set<String> sensitiveProtocolHeaders = new HashSet<String>();
			final LogEvent event = new DefaultLogEventMapper().map(message, sensitiveProtocolHeaders);
			if (shouldLogContent(event)) {
				addContent(message, event);
			} else {
				event.setPayload(AbstractLoggingInterceptor.CONTENT_SUPPRESSED);
			}
			
			DumpUtilities utilities = null;
			if(this.serverConfig!=null) {
				this.serverConfig.setDumpConfig(this.dumpConfig); // update
				utilities = new DumpUtilities(this.serverConfig);
			}
			else {
				utilities = new DumpUtilities(this.dumpConfig);
			}
			
			DumpRequest request = new DumpRequest();
			
			if(event.getPayload()!=null) {
				request.setPayload(event.getPayload().getBytes());
			}
			request.setContentType(event.getContentType());
			request.setHeaders(event.getHeaders());
			
			utilities.processBeforeSend(request);
			
		} catch (Throwable e) {
			LoggerWrapperFactory.getLogger(DumpInInterceptor.class).error(e.getMessage(),e);
			throw new Fault(e); 
		}

	}

	private void addContent(Message message, final LogEvent event) {
		try {
			CachedOutputStream cos = message.getContent(CachedOutputStream.class);
			if (cos != null) {
				handleOutputStream(event, message, cos);
			} else {
				CachedWriter writer = message.getContent(CachedWriter.class);
				if (writer != null) {
					handleWriter(event, writer);
				}
			}
		} catch (IOException e) {
			throw new Fault(e);
		}
	}

	private void handleOutputStream(final LogEvent event, Message message, CachedOutputStream cos) throws IOException {
		String encoding = (String) message.get(Message.ENCODING);
		if (StringUtils.isEmpty(encoding)) {
			encoding = StandardCharsets.UTF_8.name();
		}
		StringBuilder payload = new StringBuilder();
		cos.writeCacheTo(payload, encoding, this.limit);
		cos.close();
		event.setPayload(payload.toString());
		boolean isTruncated = cos.size() > this.limit && this.limit != -1;
		event.setTruncated(isTruncated);
		event.setFullContentFile(cos.getTempFile());
	}

	private void handleWriter(final LogEvent event, CachedWriter writer) throws IOException {
		boolean isTruncated = writer.size() > this.limit && this.limit != -1;
		StringBuilder payload = new StringBuilder();
		writer.writeCacheTo(payload, this.limit);
		event.setPayload(payload.toString());
		event.setTruncated(isTruncated);
		event.setFullContentFile(writer.getTempFile());
	}

}

