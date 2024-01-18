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

package org.openspcoop2.utils.mail.test;

import java.io.File;
import java.io.FileInputStream;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.mail.Mail;
import org.openspcoop2.utils.mail.MailAttach;
import org.openspcoop2.utils.mail.MailBinaryAttach;
import org.openspcoop2.utils.mail.MailTextAttach;
import org.openspcoop2.utils.mail.Sender;
import org.openspcoop2.utils.mail.SenderFactory;
import org.openspcoop2.utils.mail.SenderType;
import org.openspcoop2.utils.transport.http.SSLConfig;

/**
 * Test
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MailTest {


	public static void main(String[] args) throws Exception {
		
		// Server parameter
		String serverHost = "server.smtp.it";
		int serverPort = 465;
		String username = "username";
		String password = "password";
		SSLConfig sslConfig = new SSLConfig();
		sslConfig.setSslType("TLS");
		sslConfig.setTrustStoreType(KeystoreType.JKS.getNome());
		sslConfig.setTrustStoreLocation("keystore.jks");
		sslConfig.setTrustStorePassword("keyserver");
		sslConfig.setTrustManagementAlgorithm("PKIX");
		
		// Address
		String from = "from@prova.org";
		String to = "to@prova.org";
		String cc = "cc@prova.org";
		
		// Mail
		String subject = "TEST";
		String contenuto = "Questa è una prova di invio mail";
		
		// Sender [Commons-Net]
		Sender senderCommonsNet = SenderFactory.newSender(SenderType.COMMONS_NET, LoggerWrapperFactory.getLogger(MailTest.class));
		senderCommonsNet.setConnectionTimeout(100);
		senderCommonsNet.setReadTimeout(5 * 1000);

		
		
		// 1. *** Email Semplice ***
		
		Mail mail = new Mail();
		
		mail.setServerHost(serverHost);
		mail.setServerPort(serverPort);
		mail.setUsername(username);
		mail.setPassword(password);
		mail.setSslConfig(sslConfig);
		mail.setStartTls(false);
		
		mail.setUserAgent("GovWay");
		//mail.setContentLanguage("it-IT");
		//mail.setMessageIdDomain("link.it");
		
		mail.setFrom(from);
		mail.setTo(to);
		mail.getCc().add(cc);
		
		mail.setSubject(subject);
		mail.getBody().setMessage(contenuto+" [test-invio-semplice]");
			
		// Invio con commons net
//		System.out.println("Invio mail ["+SenderType.COMMONS_NET+"] in corso ...");
//		senderCommonsNet.send(mail, true); 
//		System.out.println("Invio mail ["+SenderType.COMMONS_NET+"] effettuato");
		
		// Invio con commons mail
//		System.out.println("Invio mail ["+SenderType.COMMONS_MAIL+"] in corso ...");
//		senderCommonsMail.send(mail, true); 
//		System.out.println("Invio mail ["+SenderType.COMMONS_MAIL+"] effettuato");
		
		
		
		
		
		// 2. *** Email con Allegati ***
					
		mail.getBody().setMessage(contenuto+" [test-invio-con-attachments]");
		
		MailAttach simpleAttach = new MailTextAttach("Simple.txt", "Hello World");
		mail.getBody().getAttachments().add(simpleAttach);
		
		File file = new File("PROVA.zip");
		MailAttach binAttach = new MailBinaryAttach(file.getName(), new FileInputStream(file));
		mail.getBody().getAttachments().add(binAttach);
		
		// Invio con commons net [NOTA: gli attachments binari non funzionano bene poichè la libreria commons net lavora con un Writer]
//		System.out.println("Invio mail con attachments ["+SenderType.COMMONS_NET+"] in corso ...");
//		senderCommonsNet.send(mail, true); 
//		System.out.println("Invio mail con attachments ["+SenderType.COMMONS_NET+"] effettuato");
		
		
	}
	
}
