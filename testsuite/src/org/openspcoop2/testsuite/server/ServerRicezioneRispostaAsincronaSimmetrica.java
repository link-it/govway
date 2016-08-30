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



package org.openspcoop2.testsuite.server;
/*
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;*/
import java.io.IOException;/*
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;*/
import java.net.ServerSocket;
import java.net.Socket;/*
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import org.apache.axis.Message;
import org.apache.axis.message.PrefixedQName;*/

import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.asincrono.RepositoryConsegnaRisposteAsincroneSimmetriche;

/**
 * Gestore dei thread per la ricezione di risposte asincrone simmetriche.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ServerRicezioneRispostaAsincronaSimmetrica extends Thread{
	protected RepositoryConsegnaRisposteAsincroneSimmetriche rep;
	private int numberThreads;
	private int numPorta;
	
	// Socket
	ServerSocket serSock=null;
	Socket[] sock = null;
	
	
	/** Nome della proprieta contenente l'id */
	public ServerRicezioneRispostaAsincronaSimmetrica(int numThread,int numPorta,RepositoryConsegnaRisposteAsincroneSimmetriche rep){//Devo aspettare un numero di messaggi pari al numero di thread che lancio
		this.rep=rep;
		this.numberThreads=numThread;
		this.numPorta=numPorta;
	}
	/////da realizzare singoli thread
	@Override
	public void run() {
		try {
			this.serSock = new ServerSocket(this.numPorta);
			this.sock = new Socket[this.numberThreads];
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			throw new TestSuiteException(e1,"nella creazione del serverSocket");
		}
		for(int i=0;i<this.numberThreads;i++){
			try {
				this.sock[i] = this.serSock.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new TestSuiteException(e,"nella creazione del serverSocket");

			}
			ServerRicezioneRispostaAsincronaSimmetricaThread dep=new ServerRicezioneRispostaAsincronaSimmetricaThread(this.sock[i],this.rep);
			dep.start();
		}

	}
	
	public void closeSocket() throws TestSuiteException{
		for(int i=0;i<this.numberThreads;i++){
			try {
				if(this.sock[i].isClosed() == false)
					this.sock[i].close();
			}catch(Exception e){
				throw new TestSuiteException("Errore durante la chiusura del Socket "+i+": "+e.getMessage());
			}
		}
		try {
			this.serSock.close();
		}catch(Exception e){
			throw new TestSuiteException("Errore durante la chiusura del Server: "+e.getMessage());
		}
	}
	
}
