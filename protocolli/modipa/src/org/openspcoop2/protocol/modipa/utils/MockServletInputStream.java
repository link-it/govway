package org.openspcoop2.protocol.modipa.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class MockServletInputStream extends ServletInputStream {

	
	private ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
	private ReadListener cb = null;
	
	public MockServletInputStream() {
		this(new byte[0]);
	}
	
	public MockServletInputStream(byte[] buf) {
		this.is = new ByteArrayInputStream(buf);
	}
	
	@Override
	public boolean isFinished() {
		return this.is.available() > 0;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setReadListener(ReadListener arg0) {
		try {
			arg0.onDataAvailable();
		} catch (IOException e) {
			arg0.onError(e);
		}
		
		this.cb = arg0;
	}

	@Override
	public int read() throws IOException {
		int v = this.is.read();
		if (this.isFinished() && this.cb != null)
			this.cb.onAllDataRead();
		return v;
	}

}
