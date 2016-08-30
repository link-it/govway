/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.net.ssl.SSLContext;

import org.apache.commons.io.output.WriterOutputStream;
import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;
import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.SSLUtilities;

/**
 * CommonsNetSender
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CommonsNetSender extends Sender {

	protected CommonsNetSender(Logger log) {
		super(log);
	}

	@Override
	public void send(Mail mail, boolean debug) throws UtilsException {

		AuthenticatingSMTPClient client = null;
		try {			
			client = new AuthenticatingSMTPClient(mail.getEncoding());

			client.setDefaultTimeout(this.getReadTimeout());
			client.setConnectTimeout(this.getConnectionTimeout());
			
			if(mail.getSslConfig()!=null){
				StringBuffer bf = new StringBuffer();
				SSLContext sslContext = SSLUtilities.generateSSLContext(mail.getSslConfig(), bf);
				if(debug)
					this.log.debug(bf.toString());
				client.setSocketFactory(sslContext.getSocketFactory());
			}

			this.log.debug("Connect to ["+mail.getServerHost()+":"+mail.getServerPort()+"] ...");
			client.connect(mail.getServerHost(), mail.getServerPort());
			this.log.debug("Connected to ["+mail.getServerHost()+":"+mail.getServerPort()+"]");

			boolean esito = client.login();
			checkReply(client, esito, "Login failed");

			if(mail.isStartTls()){
				esito = client.execTLS();
				checkReply(client, esito, "STARTTLS was not accepted");
			}

			String replyString = client.getReplyString();
			if(debug){
				this.log.debug("ReplyString: "+replyString);
			}
			int reply = client.getReplyCode();
			if(!SMTPReply.isPositiveCompletion(reply)) {
				throw new Exception("SMTP server refused connection "+ client.getReply() + client.getReplyString());
			}

			if(mail.getUsername()!=null){
				if(debug){
					this.log.debug("Authenticating ["+mail.getUsername()+"] ...");
				}
				esito =  client.auth(AuthenticatingSMTPClient.AUTH_METHOD.LOGIN, mail.getUsername(), mail.getPassword());
				checkReply(client, esito, "Authentication failed");
				if(debug){
					this.log.debug("Authenticating ["+mail.getUsername()+"] ok");
				}
			}

			if(debug){
				this.log.debug("Set sender ["+mail.getFrom()+"] ...");
			}
			esito = client.setSender(mail.getFrom());
			checkReply(client, esito, "Set sender["+mail.getFrom()+"] failed");
			
			if(debug){
				this.log.debug("Set to ["+mail.getTo()+"] ...");
			}
			esito = client.addRecipient(mail.getTo());
			checkReply(client, esito, "Set to["+mail.getTo()+"] failed");
			
			List<String> ccList = mail.getCc();
			if(ccList!=null && ccList.size()>0){
				for (String cc : ccList) {
					if(debug){
						this.log.debug("Add cc ["+cc+"] ...");
					}
					esito = client.addRecipient(cc);
					checkReply(client, esito, "Set cc["+cc+"] failed");
				}
			}

			boolean attach = (mail.getBody().getAttachments()!=null && mail.getBody().getAttachments().size()>0);
			
			Writer writer = client.sendMessageData();
			if(writer!=null){
				
				if(debug){
					this.log.debug("Send ...");
				}
				
				// HEADER
				if(debug){
					this.log.debug("Subject ["+mail.getSubject()+"] ...");
				}
				SimpleSMTPHeader header = new SimpleSMTPHeader(mail.getFrom(), mail.getTo(), mail.getSubject());
				
				if(debug){
					this.log.debug("Body ("+mail.getBody().getContentType()+") ["+mail.getBody().getMessage()+"] ...");
				}
				
				TimeZone tz = TimeZone.getTimeZone( "GMT" );
				Date now = new Date();
				header.addHeaderField("Date", formatSMTPDate( now, tz ));
				
				ccList = mail.getCc();
				if(ccList!=null && ccList.size()>0){
					for (String cc : ccList) {
						header.addCC(cc);	
					}
				}
				
				Multipart multipart = null;
				if(attach){
					 multipart = new MimeMultipart();
					 
					 if(mail.getBody().getMessage()!=null){
						 MimeBodyPart messagePart = new MimeBodyPart();
						 messagePart.setText(mail.getBody().getMessage());
						 if(mail.getBody().getContentType()!=null)
							 messagePart.setHeader("Content-Type", mail.getBody().getContentType());
						 multipart.addBodyPart(messagePart);
					 }
					 
					 for (MailAttach mailAttach : mail.getBody().getAttachments()) {
						 // NOTA: funziona solo per gli attachments testuali!
						 // motivo: usa un writer che corrompe poichè lavora su stringhe
						 MimeBodyPart attachmentPart = new MimeBodyPart();
						 if(mailAttach instanceof MailTextAttach){
							 MailTextAttach text = (MailTextAttach) mailAttach;
							 attachmentPart.setText(text.getContent());
						 }
						 else{
							 String msg = "La libreria CommonsNet non funziona correttamente con gli attachments di tipo binario. Usare il sender di tipo CommonsMail";
							 this.log.warn(msg);
							 System.out.println(msg);
							 MailBinaryAttach bin = (MailBinaryAttach) mailAttach;
							 attachmentPart.setContent(bin.getContent(), mailAttach.getContentType());
						 }
						 attachmentPart.setFileName(mailAttach.getName());
						 attachmentPart.setHeader("Content-Type", mailAttach.getContentType());
						 multipart.addBodyPart(attachmentPart);
					}
					 
				}
				
				if(!attach){
					if(mail.getBody().getContentType()!=null){
						 header.addHeaderField("Content-Type", mail.getBody().getContentType());
					}
				}
				else{
					header.addHeaderField("Content-Type", multipart.getContentType());
				}
				
				writer.write(header.toString());
				
				if(!attach){
					writer.write(mail.getBody().getMessage());
				}
				else{
					WriterOutputStream ww = new WriterOutputStream(writer,Charset.forName("UTF-8"));
					multipart.writeTo(ww);
					ww.flush();
					ww.close();
				}
		          				
				writer.close();
				if(!client.completePendingCommand()) {// failure
					throw new Exception("Failure to send the email "+ client.getReply() + client.getReplyString());
				}
				
			} else {
				throw new Exception("Failure to send the email "+ client.getReply() + client.getReplyString());
			}
			
			if(debug){
				this.log.debug("Send finished");
			}
			
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		} finally {
			try{
				client.logout();
			}catch(Exception e){}
			try{
				client.disconnect();
			}catch(Exception e){}
		}
	}

	private static String formatSMTPDate( Date date, TimeZone tz )
			throws IllegalArgumentException
		{
		SimpleDateFormat dateFormat;
		Locale loc = Locale.US;

		dateFormat = new SimpleDateFormat( "EEE", loc );
		dateFormat.setTimeZone( tz );
		String day = dateFormat.format( date );
		day = day.substring( 0, 3 );

		dateFormat = new SimpleDateFormat( "MMM", loc );
		dateFormat.setTimeZone( tz );
		String month = dateFormat.format( date );
		month = month.substring( 0, 3 );

		dateFormat = new SimpleDateFormat( "dd", loc );
		dateFormat.setTimeZone( tz );
		String dayNum = dateFormat.format( date );

		dateFormat = new SimpleDateFormat( "yyyy HH:mm:ss", loc );
		dateFormat.setTimeZone( tz );
		String rest = dateFormat.format( date );

		String result = new String
			( day + ", " + dayNum + " " + month + " " + rest + " +0000" );

		return result;
		

	}
	
	private static void checkReply(SMTPClient sc, boolean esito, String object) throws Exception {
		if (SMTPReply.isNegativeTransient(sc.getReplyCode())) {
			throw new Exception(object+", transient SMTP error " + sc.getReply() + sc.getReplyString());
		} else if (SMTPReply.isNegativePermanent(sc.getReplyCode())) {
			throw new Exception(object+", permanent SMTP error " + sc.getReply() + sc.getReplyString());
		}
		else if(!esito){
			throw new Exception(object+", ["+esito+"] " + sc.getReply() + sc.getReplyString());
		}
	}
}
