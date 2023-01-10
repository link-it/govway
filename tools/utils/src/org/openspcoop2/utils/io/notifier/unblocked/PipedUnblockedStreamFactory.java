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
package org.openspcoop2.utils.io.notifier.unblocked;

import java.io.IOException;

import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * IPipedUnblockedStream
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PipedUnblockedStreamFactory {

	private static boolean USE_PIPED_BYTES_STREAM_IMPL = true; 
	private static boolean USE_PIPED_BYTE_ARRAY_OUTPUT_STREAM_IMPL = false; 
	private static String CLASSNAME_IMPL = null;
	
	public static void setImplementation(String className) {
		if(org.openspcoop2.utils.io.notifier.unblocked.PipedBytesStream.class.getName().equals(className)) {
			USE_PIPED_BYTES_STREAM_IMPL = true;
			USE_PIPED_BYTE_ARRAY_OUTPUT_STREAM_IMPL = false;
			CLASSNAME_IMPL = null;
		}
		else if(org.openspcoop2.utils.io.notifier.unblocked.PipedUnblockedStream.class.getName().equals(className)) {
			USE_PIPED_BYTES_STREAM_IMPL = false;
			USE_PIPED_BYTE_ARRAY_OUTPUT_STREAM_IMPL = true;
			CLASSNAME_IMPL = null;
		}
		else {
			USE_PIPED_BYTES_STREAM_IMPL = false;
			USE_PIPED_BYTE_ARRAY_OUTPUT_STREAM_IMPL = false;
			CLASSNAME_IMPL = className;
		}
	}
	
	public static IPipedUnblockedStream newPipedUnblockedStream(Logger log, long sizeBuffer, int timeoutMs, String source) throws IOException {
		try {
			IPipedUnblockedStream pipe = null;
			if(USE_PIPED_BYTES_STREAM_IMPL) {
				pipe = new org.openspcoop2.utils.io.notifier.unblocked.PipedBytesStream();
			}
			else if(USE_PIPED_BYTE_ARRAY_OUTPUT_STREAM_IMPL) {
				pipe = new org.openspcoop2.utils.io.notifier.unblocked.PipedUnblockedStream();
			}
			else {
				pipe = (IPipedUnblockedStream) new Loader().newInstance(CLASSNAME_IMPL);
			}
			pipe.init(log, sizeBuffer, timeoutMs, source);
			return pipe;
		}catch(Throwable t) {
			throw new IOException(t.getMessage(),t);
		}
	}
	
}
