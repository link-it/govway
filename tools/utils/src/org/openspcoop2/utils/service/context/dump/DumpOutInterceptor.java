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

package org.openspcoop2.utils.service.context.dump;

import java.io.OutputStream;

import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.ext.logging.event.DefaultLogEventMapper;
import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.ext.logging.event.LogEventSender;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.service.context.server.ServerConfig;


/**
 * DumpOutInterceptor
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@NoJSR250Annotations
public class DumpOutInterceptor extends org.apache.cxf.ext.logging.LoggingOutInterceptor {

	private DumpConfig dumpConfig;
	private ServerConfig serverConfig;
	
	public DumpOutInterceptor() {
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
		
			final OutputStream os = message.getContent(OutputStream.class);
			if (os != null) {
				LoggingCallback callback = new LoggingCallback(this.sender, message, os, this.limit, this.dumpConfig, this.serverConfig);
				message.setContent(OutputStream.class, createCachingOut(message, os, callback));
			}
			
		} catch (Throwable e) {
			LoggerWrapperFactory.getLogger(DumpInInterceptor.class).error(e.getMessage(),e);
			throw new Fault(e); 
		}
	}

	private OutputStream createCachingOut(Message message, final OutputStream os, CachedOutputStreamCallback callback) {
		final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
		if (this.threshold > 0) {
			newOut.setThreshold(this.threshold);
		}
		if (this.limit > 0) {
			// make the limit for the cache greater than the limit for the truncated payload in the log event, 
			// this is necessary for finding out that the payload was truncated 
			//(see boolean isTruncated = cos.size() > limit && limit != -1;)  in method copyPayload
			newOut.setCacheLimit(getCacheLimit());
		}
		newOut.registerCallback(callback);
		return newOut;
	}

	private int getCacheLimit() {
		if (this.limit == Integer.MAX_VALUE) {
			return this.limit;
		}
		return this.limit + 1;
	}

	public class LoggingCallback implements CachedOutputStreamCallback {

		private final Message message;
		private final OutputStream origStream;
		private final int lim;
		@SuppressWarnings("unused")
		private LogEventSender sender;
		private DumpConfig dumpConfig;
		private ServerConfig serverConfig;

		public LoggingCallback(final LogEventSender sender, final Message msg, final OutputStream os, int limit, 
				DumpConfig dumpConfig, ServerConfig serverConfig) {
			this.sender = sender;
			this.message = msg;
			this.origStream = os;
			this.lim = limit == -1 ? Integer.MAX_VALUE : limit;
			this.dumpConfig = dumpConfig;
			this.serverConfig = serverConfig;
		}

		@Override
		public void onFlush(CachedOutputStream cos) {

		}

		@Override
		public void onClose(CachedOutputStream cos) {
			
			try {
			
				final LogEvent event = new DefaultLogEventMapper().map(this.message);
				if (shouldLogContent(event)) {
					copyPayload(cos, event);
				} else {
					event.setPayload(CONTENT_SUPPRESSED);
				}
	
				DumpUtilities utilities = null;
				if(this.serverConfig!=null) {
					this.serverConfig.setDumpConfig(this.dumpConfig); // update
					utilities = new DumpUtilities(this.serverConfig);
				}
				else {
					utilities = new DumpUtilities(this.dumpConfig);
				}
				
				DumpResponse response = new DumpResponse();
				
				if(event.getPayload()!=null) {
					response.setPayload(event.getPayload().getBytes());
				}
				response.setContentType(event.getContentType());
				try {
					if(event.getResponseCode()!=null) {
						response.setResponseCode(Integer.parseInt(event.getResponseCode()));
					}
				}catch(Throwable t) {
				}
				response.setHeaders(event.getHeaders());
				
				utilities.processAfterSend(response);
				
				try {
					// empty out the cache
					cos.lockOutputStream();
					cos.resetOut(null, false);
				} catch (Exception ex) {
					// ignore
				}
				this.message.setContent(OutputStream.class, this.origStream);
								
			} catch (Throwable e) {
				LoggerWrapperFactory.getLogger(DumpInInterceptor.class).error(e.getMessage(),e);
				throw new Fault(e); 
			}
		}

		private void copyPayload(CachedOutputStream cos, final LogEvent event) {
			try {
				String encoding = (String) this.message.get(Message.ENCODING);
				StringBuilder payload = new StringBuilder();
				writePayload(payload, cos, encoding, event.getContentType());
				event.setPayload(payload.toString());
				boolean isTruncated = cos.size() > this.lim && this.lim != -1;
				event.setTruncated(isTruncated);
			} catch (Exception ex) {
				// ignore
			}
		}

		protected void writePayload(StringBuilder builder, CachedOutputStream cos, String encoding, String contentType)
				throws Exception {
			if (StringUtils.isEmpty(encoding)) {
				cos.writeCacheTo(builder, this.lim);
			} else {
				cos.writeCacheTo(builder, encoding, this.lim);
			}
		}
	}

}

