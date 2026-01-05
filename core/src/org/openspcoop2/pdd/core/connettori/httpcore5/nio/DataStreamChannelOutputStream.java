/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.connettori.httpcore5.nio;

import org.apache.hc.core5.http.nio.DataStreamChannel;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * DataStreamChannelOutputStream
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DataStreamChannelOutputStream extends OutputStream {

    private final DataStreamChannel channel;

    public DataStreamChannelOutputStream(DataStreamChannel channel) {
        this.channel = channel;
    }

    @Override
    public void write(int b) throws IOException {
        // Scrive un singolo byte
        this.channel.write(ByteBuffer.wrap(new byte[]{(byte) b}));
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // Scrive un intervallo dell'array di byte
        ByteBuffer buffer = ByteBuffer.wrap(b, off, len);
        this.channel.write(buffer);
    }

    @Override
    public void write(byte[] b) throws IOException {
        // Scrive l'intero array di byte
    	this.channel.write(ByteBuffer.wrap(b));
    }

    @Override
    public void close() throws IOException {
        // Segnala la fine del flusso al canale
    	this.channel.endStream();
    }

    @Override
    public void flush() throws IOException {
        // Non è necessario implementare il flush con DataStreamChannel
    }
}

