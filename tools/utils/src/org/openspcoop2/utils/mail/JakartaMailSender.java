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
package org.openspcoop2.utils.mail;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.slf4j.Logger;

/**
 * CommonsNetSender
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Pintori Giuliano (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JakartaMailSender extends Sender {

    protected JakartaMailSender(Logger log) {
        super(log);
    }

    @Override
    public void send(Mail mail, boolean debug) throws UtilsException {
        List<File> tempFiles = new ArrayList<>();
        try {
            // Proprietà di connessione
            Properties props = new Properties();
            props.put("mail.smtp.host", mail.getServerHost());
            props.put("mail.smtp.port", String.valueOf(mail.getServerPort()));
            props.put("mail.smtp.connectiontimeout", String.valueOf(this.getConnectionTimeout()));
            props.put("mail.smtp.timeout", String.valueOf(this.getReadTimeout()));

            if (mail.isStartTls()) {
                props.put("mail.smtp.starttls.enable", "true");
            }
            if (mail.getSslConfig() != null) {
                // Forza SSL diretto (SMTPS)
                props.put("mail.smtp.ssl.enable", "true");
                // Configurazioni aggiuntive come hostname verifier
                props.put("mail.smtp.ssl.checkserveridentity", String.valueOf(mail.getSslConfig().isHostnameVerifier()));
                StringBuilder bf = new StringBuilder();
                SSLUtilities.setSSLContextIntoJavaProperties(mail.getSslConfig(), bf);
                if (debug) this.logDebug(bf.toString());
            }

            Session session = initSession(mail, props, debug);

            // Costruzione messaggio
            MimeMessage message = buildBaseMessage(mail, session, debug);
           
            boolean hasAttachments = mail.getBody().getAttachments() != null &&
                    !mail.getBody().getAttachments().isEmpty();

            if (hasAttachments) {
            	addAttachments(mail, tempFiles, message, debug);
            } else {
                // Solo testo
                message.setText(mail.getBody().getMessage(), "UTF-8");
            }

            // Invio
            this.logDebug("Send ["+mail.getServerHost()+":"+mail.getServerPort()+"] ...");
            Transport.send(message);
            this.logDebug("Send ["+mail.getServerHost()+":"+mail.getServerPort()+"] completed");
            
        } catch (Exception e) {
            throw new UtilsException(e.getMessage(), e);
        } finally {
            // Pulizia temporanei
            for (File f : tempFiles) {
            	FileSystemUtilities.deleteFile(f);
            }
        }
    }
    
    private Session initSession(Mail mail, Properties props, boolean debug) {
    	this.logDebug("Init session username["+mail.getUsername()+"] ...");
    	Session session;
        if (mail.getUsername() != null) {
        	props.put("mail.smtp.auth", "true");
        	
            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mail.getUsername(), mail.getPassword());
                }
            });
        } else {
            session = Session.getInstance(props);
        }
        session.setDebug(debug);
        this.logDebug("Init session username["+mail.getUsername()+"] completed");
        return session;
    }
    
    private MimeMessage buildBaseMessage(Mail mail, Session session, boolean debug) throws MessagingException {
    	if(debug){
    		this.logDebug("Build message ...");
    	}
    	MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mail.getFrom()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getTo()));

        if (mail.getCc() != null && !mail.getCc().isEmpty()) {
            message.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(String.join(",", mail.getCc())));
        }

        message.setSubject(mail.getSubject());

        // Headers extra
        if (mail.getUserAgent() != null) {
            message.setHeader("User-Agent", mail.getUserAgent());
        }
        if (mail.getMessageIdDomain() != null) {
            message.setHeader("Message-ID", "<" + UUID.randomUUID() + "@" + mail.getMessageIdDomain() + ">");
        }
        if (mail.getContentLanguage() != null) {
            message.setHeader("Content-Language", mail.getContentLanguage());
        }
        if(debug){
    		this.logDebug("Build message completed");
        }
        return message;
    }
    
    private void addAttachments(Mail mail, List<File> tempFiles, MimeMessage message, boolean debug) throws MessagingException, IOException, UtilsException {
    	if(debug){
    		this.logDebug("Add attachments ...");
    	}
    	 // Corpo multiparte
        MimeMultipart multipart = new MimeMultipart();

        // Parte testo
        if (mail.getBody().getMessage() != null) {
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(mail.getBody().getMessage(), "UTF-8");
            multipart.addBodyPart(textPart);
        }

        // Allegati
        for (MailAttach attach : mail.getBody().getAttachments()) {
            File tmpFile;
            switch (attach) {
              case MailTextAttach mailTextAttach -> {
                  tmpFile = FileSystemUtilities.createTempFile("mailTextAttach", ".txt");
                  FileSystemUtilities.writeFile(tmpFile,
                  		mailTextAttach.getContent().getBytes());
              }
              case MailBinaryAttach mailBinaryAttach -> {
                  tmpFile = FileSystemUtilities.createTempFile("mailBinaryAttach", ".bin");
                  FileSystemUtilities.writeFile(tmpFile,
                  		mailBinaryAttach.getContent());
              }
              default -> 
              	throw new UtilsException("MailAttach class '"+attach.getClass().getName()+"' unsupported");
            }
            tempFiles.add(tmpFile);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.setDataHandler(new DataHandler(new FileDataSource(tmpFile)));
            attachmentPart.setFileName(attach.getName());
            multipart.addBodyPart(attachmentPart);
        }

        message.setContent(multipart);
        if(debug){
    		this.logDebug("Add attachments completed ...");
        }
    }
}
