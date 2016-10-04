/*
 * OpenSPCoop - Customizable API Gateway 
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




package org.openspcoop2.testsuite.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import org.apache.axis.Message;
import org.apache.axis.message.PrefixedQName;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.UtilitiesGestioneMessaggiSoap;
import org.openspcoop2.testsuite.core.asincrono.RepositoryConsegnaRisposteAsincroneSimmetriche;


/**
 * Thread per la ricezione di una risposta asincrona simmetrica.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ServerRicezioneRispostaAsincronaSimmetricaThread extends Thread{

	//private static final String FILE_SOAP= "build/soap.xml";

	/** Nome della proprieta contenente l'id */
	String nameIdMessaggio;

	private Socket sock;
	private RepositoryConsegnaRisposteAsincroneSimmetriche rep;

	public ServerRicezioneRispostaAsincronaSimmetricaThread(Socket sock,RepositoryConsegnaRisposteAsincroneSimmetriche rep){
		this.sock=sock;
		this.rep=rep;
		
	}

	@Override
	public void run(){
		try{
			InputStream in=this.sock.getInputStream();
			UtilitiesGestioneMessaggiSoap utilityGestioneMessaggio=new UtilitiesGestioneMessaggiSoap();
			utilityGestioneMessaggio.gestioneHttpInputStream(in);
			Message msg=utilityGestioneMessaggio.buildMessage();//avro bisogno di restituire l' id ;)
			String str=this.getIDFromHeader(msg);
			
			// Gestione messaggio
			long contentLenght = msg.getContentLength();
			String type = msg.getContentType(new org.apache.axis.soap.SOAP11Constants());
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			msg.writeTo(bout);
			
			// Messaggio da Ritornare
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			Message messaggioRepository = new Message(bin,false,msg.getContentType(new org.apache.axis.soap.SOAP11Constants()),null);
			java.util.Iterator<?> it = msg.getMimeHeaders().getAllHeaders();
			while(it.hasNext()){
				MimeHeader mh = (MimeHeader) it.next();
				messaggioRepository.getMimeHeaders().addHeader(mh.getName(),mh.getValue());
			}

			// save changes.
			// N.B. il countAttachments serve per il msg con attachments come saveMessage!
			if(messaggioRepository.countAttachments()==0){
				messaggioRepository.getSOAPPartAsBytes();
			}
			bin.close();
						
			// Mando in echo quello che ricevo
			OutputStream out=this.sock.getOutputStream();
			PrintWriter print=new PrintWriter(out);
//			print.println("HTTP/1.0 200 OK\r");
//			print.println("Content-Type:"+type+"\r");
//			print.println("Content-Length:"+contentLenght+'\r');
//			print.println('\r');
			print.println("HTTP/1.0 200 OK");
			print.println("Content-Type:"+type+"");
			print.println("Content-Length:"+contentLenght);
			print.println();
			print.write(bout.toString());
			print.close();
			
			this.sock.close();
			
			// Metto il messaggio nel repository
			this.rep.put(str, messaggioRepository);

		}catch(Exception e){
			e.printStackTrace();
			throw new TestSuiteException(e,"Errore durante la ricezione della risposta asincrona" +e);
		}
	}


	private String getIDFromHeader(Message msg) throws SOAPException{
		SOAPHeader header=msg.getSOAPHeader();
		Iterator<?> it=header.getChildElements(new PrefixedQName(new QName(CostantiTestSuite.TAG_NAME)));
		SOAPElement ele=(SOAPElement)it.next();
		String str=ele.getAttribute(CostantiTestSuite.ID_HEADER_ATTRIBUTE);
		return str;
	}
}

