/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.modipa.testsuite;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;

import jakarta.activation.MimeTypeParseException;
import jakarta.mail.MessagingException;
import jakarta.mail.util.ByteArrayDataSource;

/**
* DbUtils
*
* @author Tommaso Burlon (tommaso.burlon@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TestUtils {
	
	private TestUtils() {
		
	}
	
	public static String getPart(String request, String contentType, Integer part) throws MimeTypeParseException, MessagingException {

		String content = null;
		
		try (ByteArrayInputStream is = new ByteArrayInputStream(request.getBytes())) {
			
			jakarta.activation.DataSource ds = new ByteArrayDataSource(is, contentType);
			jakarta.mail.internet.MimeMultipart mimeMultipartObject = new jakarta.mail.internet.MimeMultipart(ds);
			return (String) mimeMultipartObject.getBodyPart(part).getContent();

		} catch (IOException e) {
			return content;
		}
	}
	
	public static Timestamp now() {
		return Timestamp.from(Instant.now());
	}
	
	public static String format(Timestamp t, String format) {
		return new SimpleDateFormat(format).format(t);
	}
	
	public static Timestamp parse(String date, String format) {
		try {
			return new Timestamp(new SimpleDateFormat(format).parse(date).getTime());
		} catch (ParseException e) { 
			return null;
		}
	}
	
	public static Timestamp addDays(Timestamp t, int days) {
		return Timestamp.from(Instant.ofEpochMilli(t.getTime()).plus(Duration.ofDays(days)));
	}
}
