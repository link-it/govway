/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.connettori.httpcore5;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hc.core5.http.io.entity.AbstractHttpEntity;

/**
 * FileStreamingEntity
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileStreamingEntity extends AbstractHttpEntity {

	private File f;
	private boolean chunked;
	
	public FileStreamingEntity(File f, org.apache.hc.core5.http.ContentType ct, String contentEncoding) {
		super(ct, contentEncoding, 
				true); // chunked
		this.f = f;
		this.chunked = true;
	}

	@Override
    public final boolean isRepeatable() {
        return true;
    }
	
	@Override
	public InputStream getContent() throws IOException, UnsupportedOperationException {
		return new FileInputStream(this.f);
	}

	@Override
	public boolean isStreaming() {
		return this.chunked;
	}

    @Override
    public final long getContentLength() {
        return this.f.length();
    }

	@Override
	public void close() throws IOException {
		// nop
	}
	
}
