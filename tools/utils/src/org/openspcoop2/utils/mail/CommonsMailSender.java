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

package org.openspcoop2.utils.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.slf4j.Logger;

/**
 * CommonsMailSender
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CommonsMailSender extends Sender {

	protected CommonsMailSender(Logger log) {
		super(log);
	}

	@Override
	public void send(Mail mail, boolean debug) throws UtilsException {
		
		List<File> filesAllegati = new ArrayList<File>();
		try{
		
			boolean attach = (mail.getBody().getAttachments()!=null && mail.getBody().getAttachments().size()>0);
			Email email = null;
			
			if(attach){
				email = new MultiPartEmail();
			}else{
				email = new SimpleEmail();
			}
			
			email.setSocketConnectionTimeout(this.getConnectionTimeout());
			email.setSocketTimeout(this.getReadTimeout());
			
			email.setHostName(mail.getServerHost());
			email.setSmtpPort(mail.getServerPort());
			if(mail.getUsername()!=null){
				email.setAuthenticator(new DefaultAuthenticator(mail.getUsername(), mail.getPassword()));
			}
			if(mail.getSslConfig()!=null){
				email.setSSLOnConnect(true);
				StringBuilder bf = new StringBuilder();
				SSLUtilities.setSSLContextIntoJavaProperties(mail.getSslConfig(), bf);
				if(debug)
					this.log.debug(bf.toString());
				email.setSSLCheckServerIdentity(false);
				email.setStartTLSEnabled(mail.isStartTls());
			}
			
			email.setFrom(mail.getFrom());
			email.addTo(mail.getTo());
			List<String> ccList = mail.getCc();
			if(ccList!=null && ccList.size()>0){
				for (String cc : ccList) {
					email.addCc(cc);
				}
			}
			
			email.setSubject(mail.getSubject());
			if(attach){
				
				if(mail.getBody().getMessage()!=null)
					email.setMsg(mail.getBody().getMessage());
				
				for (MailAttach mailAttach : mail.getBody().getAttachments()) {
					EmailAttachment attachment = new EmailAttachment();
					File fTmp = null;
					if(mailAttach instanceof MailTextAttach){
						 MailTextAttach text = (MailTextAttach) mailAttach;
						 fTmp = File.createTempFile("mailTextAttach", ".txt");
						 FileSystemUtilities.writeFile(fTmp, text.getContent().getBytes());
					 }
					 else{
						 MailBinaryAttach bin = (MailBinaryAttach) mailAttach;
						 fTmp = File.createTempFile("mailTextAttach", ".bin");
						 FileSystemUtilities.writeFile(fTmp, bin.getContent());
					 }
					 attachment.setPath(fTmp.getAbsolutePath());
					 filesAllegati.add(fTmp);
					 attachment.setDisposition(EmailAttachment.ATTACHMENT);
					 attachment.setDescription(mailAttach.getName());
					 attachment.setName(mailAttach.getName());
					 ((MultiPartEmail)email).attach(attachment);
				}
			}else{
				email.setMsg(mail.getBody().getMessage());
			}
			
			email.setDebug(debug);
						
			if(mail.getUserAgent()!=null) {
				email.addHeader("User-Agent", mail.getUserAgent());
			}
			
			if(mail.getMessageIdDomain()!=null) {
				email.addHeader("Message-ID", "<"+UUID.randomUUID().toString()+"@"+mail.getMessageIdDomain()+">");
			}
			
			if(mail.getContentLanguage()!=null) {
				email.addHeader("Content-Language", mail.getContentLanguage());
			}
			
			if(attach){
				email.buildMimeMessage();
				email.sendMimeMessage();
			}else{
				email.send();
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		finally{
			for (File file : filesAllegati) {
				try{
					file.delete();
				}catch(Throwable e){
					// ignore
				}
			}
		}
		
	}

}
