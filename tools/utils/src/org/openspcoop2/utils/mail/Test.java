package org.openspcoop2.utils.mail;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.openspcoop2.utils.resources.SSLConfig;

public class Test {


	public static void main(String[] args) throws Exception {
		
		// Server parameter
		String serverHost = "smtp.link.it";
		int serverPort = 465;
		String username = "poli";
		String password = "p0l1.@ndr3@";
		SSLConfig sslConfig = new SSLConfig();
		sslConfig.setSslType("TLS");
		sslConfig.setTrustStoreType("JKS");
		sslConfig.setTrustStoreLocation("/home/poli/prova.jks.new");
		sslConfig.setTrustStorePassword("keyserver");
		sslConfig.setTrustManagementAlgorithm("PKIX");
		
		// Address
		String from = "apoli@link.it";
		String to = "manca@link.it";
		String cc = "poli@link.it";
		
		// Mail
		String subject = "TEST";
		String contenuto = "Questa è una prova di invio mail";
		
		// Sender [Commons-Net]
		Sender senderCommonsNet = SenderFactory.newSender(SenderType.COMMONS_NET, Logger.getLogger(Test.class));
		senderCommonsNet.setConnectionTimeout(100);
		senderCommonsNet.setReadTimeout(5 * 1000);
		// Sender [Commons-Mail]
		Sender senderCommonsMail = SenderFactory.newSender(SenderType.COMMONS_MAIL, Logger.getLogger(Test.class));
		senderCommonsMail.setConnectionTimeout(100);
		senderCommonsMail.setReadTimeout(5 * 1000);

		
		
		// 1. *** Email Semplice ***
		
		Mail mail = new Mail();
		
		mail.setServerHost(serverHost);
		mail.setServerPort(serverPort);
		mail.setUsername(username);
		mail.setPassword(password);
		mail.setSslConfig(sslConfig);
		mail.setStartTls(false);
		
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
		
		File file = new File("/opt/local/SVN_GOV4J/gov4j/openspcoop2/branches/2.2/2.2.dev/protocolli/sdi/example/config/ricezione/spcoop/FatturaPA.zip");
		MailAttach binAttach = new MailBinaryAttach(file.getName(), new FileInputStream(file));
		mail.getBody().getAttachments().add(binAttach);
		
		// Invio con commons net [NOTA: gli attachments binari non funzionano bene poichè la libreria commons net lavora con un Writer]
//		System.out.println("Invio mail con attachments ["+SenderType.COMMONS_NET+"] in corso ...");
//		senderCommonsNet.send(mail, true); 
//		System.out.println("Invio mail con attachments ["+SenderType.COMMONS_NET+"] effettuato");
		
		System.out.println("Invio mail con attachments ["+SenderType.COMMONS_MAIL+"] in corso ...");
		senderCommonsMail.send(mail, true); 
		System.out.println("Invio mail con attachments ["+SenderType.COMMONS_MAIL+"] effettuato");
		
		
	}
	
}
