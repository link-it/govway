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

package org.openspcoop2.testsuite.core;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import org.apache.axis.Message;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.message.MimeHeaders;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**
 * Gestore del server utilizzato per la ricezione di una risposta asincrona simmetrica
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class UtilitiesGestioneMessaggiSoap {

	/** Identificativo della risposta arrivata */
	String idMessaggio;
	/** RiferimentoAsincrono della risposta arrivata */
	String riferimentoAsincrono;
	/** Collaborazione della risposta arrivata */
	String collaborazione;
	/** TestSuiteProperties */
	TestSuiteProperties testsuiteProperties = null;

	/** Cursore dello stream http */
	int cursoreHttpInputStream;
	/** ContentType della risposta arrivata */
	String contentType;
	/** ContentLength della risposta arrivata */
	int contentLength;
	/** ContentEncodingChunked */
	boolean transferEncodingChunked = false;

	/** Indicazione se la risposta contiene attachments */
	boolean withAttachments = false;
	/** Attachments presenti */
	Vector<byte[]> attachments;
	/** Identificativi degli attachments */
	Vector<String> idAttachments;
	/** MimeBoundary in presenza di attachments */
	String mimeBoundary; // rappresentazionezione del boundary senza --





	public UtilitiesGestioneMessaggiSoap() {
		this.contentLength = 0;
		this.withAttachments=false;
		this.idAttachments=new Vector<String>();
		this.attachments=new Vector<byte[]>();
		this.cursoreHttpInputStream=0;
		this.testsuiteProperties = TestSuiteProperties.getInstance();
	}

	/** Indicazione se la gestione deve avvenire con messaggi SoapWithAttachments */
	public void setWithAttachments(boolean value){
		this.withAttachments=value;
	}

	/** Gestione dell'http input stream */
	public void gestioneHttpInputStream(InputStream in) throws Exception {

		InputStream is = new BufferedInputStream(in);
		readHttpHeaders(is);

		if(this.contentType.contains("multipart/related"))
			this.withAttachments=true;

		if( !(this.contentLength>0) ){
			is = gestioneChunked(is);
		}

		if(this.withAttachments){
			getSOAPMessageWithAttachments(is);
		}
		else{
			getSOAPMessage(is);
		}   

	}

	/** 
	 * Interpreta i byte presenti in una richiesta HTTP
	 * 
	 * @param is
	 * @throws IOException
	 */
	public void readHttpHeaders(InputStream is) throws IOException {

		boolean exit = false;
		StringBuffer string = new StringBuffer();
		do {
			/**
			 * **********prelievo del content
			 * length*********************************************
			 */
			byte read = (byte) is.read();
			/** ************leggo un header o la fine degli header */
			if (read == '\r') { // return carrage
				byte readAgain = (byte) is.read();
				if (readAgain == '\n') {// line feed
					String str = string.toString(); // quello che ho letto fino
					//System.out.println("LETTO: "+str);
					// adesso
					readHttpHeader(str);
					if (string.toString().equals(""))
						exit = true; // la fine degli header
					else { // vado in una nuova linea
						string = new StringBuffer();
					}
				} else {
					string.append((char) read);
					string.append((char) readAgain);
				}
			} else {
				// non e' la fine della stringa
				string.append((char) read);
				//System.out.print("APPEND ["+(char) read+"]");
				if (string.toString().equals(""))
					exit = true;
			}
		} while (!exit);

	}

	/** 
	 * Raccoglie le informazioni sul messaggio arrivato
	 * 
	 * @param header
	 */
	private void readHttpHeader(String header) {
		String[] array;
		if(header!=null){
			array = header.split(":", 2);
			if (header.startsWith("Content-Length")) {
				String l = array[1].trim();
				this.contentLength = new Integer(l).intValue();
			}
			else if (header.startsWith("Transfer-Encoding")) {
				String l = array[1].trim();
				this.transferEncodingChunked = "chunked".equalsIgnoreCase(l);
			}
			else if (header.startsWith("Content-Type")) {
				this.contentType=array[1];
				if(this.contentType.contains("multipart/related"))
					this.withAttachments=true;
			}
			else if(this.testsuiteProperties.getIdMessaggioTrasporto()!=null && header.startsWith(this.testsuiteProperties.getIdMessaggioTrasporto())){
				this.idMessaggio = array[1].trim();
			}
			else if(this.testsuiteProperties.getRiferimentoAsincronoTrasporto()!=null && header.startsWith(this.testsuiteProperties.getRiferimentoAsincronoTrasporto())){
				this.riferimentoAsincrono = array[1].trim();
			}
			else if(this.testsuiteProperties.getCollaborazioneTrasporto()!=null && header.startsWith(this.testsuiteProperties.getCollaborazioneTrasporto())){
				this.collaborazione = array[1].trim();
			}
		}
	}


	public InputStream gestioneChunked(InputStream is) throws Exception{

		/*
		 * Chunked Transfer-Encoding

		If a server wants to start sending a response before knowing its total length (like with long script output), it might use the simple chunked transfer-encoding, which breaks the complete response into smaller chunks and sends them in series. You can identify such a response because it contains the "Transfer-Encoding: chunked" header. All HTTP 1.1 clients must be able to receive chunked messages.

		A chunked message body contains a series of chunks, followed by a line with "0" (zero), followed by optional footers (just like headers), and a blank line. Each chunk consists of two parts:

		 * (Rule 1) a line with the size of the chunk data, in hex, possibly followed by a semicolon and extra parameters you can ignore (none are currently standard), and ending with CRLF.
		 * (Rule 2) the data itself, followed by CRLF. 

		So a chunked response might look like the following:

    	HTTP/1.1 200 OK
    	Date: Fri, 31 Dec 1999 23:59:59 GMT
    	Content-Type: text/plain
    	Transfer-Encoding: chunked

    	1a; ignore-stuff-here
    	abcdefghijklmnopqrstuvwxyz
    	10
    	1234567890abcdef
    	0
    	some-footer: some-value
    	another-footer: another-value
    	[blank line here]

		 */

		try{

			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			char ch = (char) is.read();
			while(true){

				// Brucio caratteri iniziali
				while(RegularExpressionEngine.isMatch(ch+"","[0-9A-Za-z]")==false){
					//System.out.println("INIZIO BRUCIO: ["+ch+"]");
					ch = (char) is.read();
				}

				// Leggo lunghezza del chunk
				StringBuffer chunkLengthEsadecimale = new StringBuffer();
				//System.out.println("LENGTH CHUNK: ["+ch+"]");
				while(RegularExpressionEngine.isMatch(ch+"","[0-9A-Za-z]")){
					//System.out.println("LENGTH CHUNK TROVATO: ["+ch+"]");
					chunkLengthEsadecimale.append(ch);
					ch = (char) is.read();
				}
				while ( ch != '\n' ) { // brucio la rimanente informazione che puo' essere ';' o il \r\n o il \n  (Regola1)
					ch = (char) is.read();
				}	
				//System.out.println("PROSSIMO ["+ch+"]");

				int chunkLength = Integer.parseInt(chunkLengthEsadecimale.toString(),16);
				//System.out.println("CHUNK: "+chunkLength);
				if(chunkLength==0){
					//System.out.println("TERMINATO");
					break;
				}

				byte[] temp = new byte[chunkLength];
				//int letti = 
				is.read(temp);
				//System.out.println("LETTO PEZZO CHUNK ("+letti+"): "+new String(temp));
				bout.write(temp);

				ch = (char) is.read();
				//System.out.println("IN FONDO ["+ch+"]");
				while ( ch != '\n' ) { // il dato stesso puo' essere seguito da '\n' (Regola2)
					//System.out.println("IN FONDO WHILE ["+ch+"]");
					ch = (char) is.read();
				}
			}

			//System.out.println("CONTENUTO("+bout.toString()+")FINE");
			return new ByteArrayInputStream(bout.toByteArray());

		}catch(Exception e){
			throw new Exception("Lettura SOAPMessage non riuscita: "+e.getMessage(),e);
		}
	}





	/**
	 * Legge il messaggio
	 * 
	 * @throws IOException 
	 */

	private void getSOAPMessage(InputStream is) throws Exception {
		try{

			if(this.contentLength>0){
				//System.out.println("CONTENT LENTGTH: "+this.contentLength);
				byte[] temp=new byte[this.contentLength];
				is.read(temp);
				this.attachments.add(temp);
			}else{
				// Chunked Mode
				//System.out.println("CHUNKED? ("+this.transferEncodingChunked+")");
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				byte[] temp = new byte[Utilities.DIMENSIONE_BUFFER];
				int letti = 0;
				while( (letti=is.read(temp)) != -1){
					bout.write(temp, 0, letti);
					bout.flush();
				}
				bout.flush();
				bout.close();
				//System.out.println("CONTENUTO: "+bout.toString());

				this.attachments.add(bout.toByteArray());
			}

		}catch(Exception e){
			throw new Exception("Lettura SOAPMessage non riuscita: "+e.getMessage(),e);
		}
	}

	private void getSOAPMessageWithAttachments(InputStream is) throws Exception {
		try{

			getBoundary(is);
			//System.out.println("READ BOUNDARY OK :["+this.mimeBoundary+"]");

			//ystem.out.println("READ APs");
			int length = this.contentLength;
			if( !(this.contentLength>0) ){

				// Chunked
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				byte[] temp = new byte[Utilities.DIMENSIONE_BUFFER];
				int letti = 0;
				while( (letti=is.read(temp)) != -1){
					bout.write(temp, 0, letti);
					bout.flush();
				}
				bout.flush();
				bout.close();

				length = bout.toByteArray().length;
				is = new ByteArrayInputStream(bout.toByteArray());

				//System.out.println("READ CHUNKED LENGTH["+length+"] ["+bout.toString()+"]");
			}
			getAttachmentParts(is,length);
			//System.out.println("READ ");

		}catch(Exception e){
			throw new Exception("Lettura SOAPMessage non riuscita: "+e.getMessage(),e);
		}
	}


	/**
	 * Legge il boundary
	 * 
	 * @param is
	 * @throws IOException
	 */
	private void getBoundary(InputStream is) throws IOException {
		char ch;
		boolean exit = false;
		do {
			ch = (char) is.read();

			this.cursoreHttpInputStream++;
			if (ch == '-') {
				char ch2 = (char) is.read();
				this.cursoreHttpInputStream++;
				if (ch2 == '-'){
					byte[]b=readLine(is);
					String str=new String(b);// /siamo nel boundary;
					this.mimeBoundary=str.substring(0,str.length()-2);//tolgo la \n
					exit=true;
				}
			}
		} while (!exit);
	}


	/**
	 * Legge una linea incrementando il cursore
	 * 
	 */
	private byte[] readLine(InputStream is) throws IOException {
		char toRead;
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		do {
			toRead = (char) is.read();
			this.cursoreHttpInputStream++;
			baos.write((byte)toRead);
		} while (toRead != '\n');
		return baos.toByteArray();
	}

	/**
	 ** Legge una linea incrementando il cursore
	 ** 
	 **/
	private byte[] readLineUntilEnd(InputStream is,int length) throws IOException {
		char toRead;
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		do {
			toRead = (char) is.read();
			this.cursoreHttpInputStream++;
			baos.write((byte)toRead);
		} while ((toRead != '\n') && (this.cursoreHttpInputStream<length));
		return baos.toByteArray();
	}


	/**
	 * Restituisce un attachment
	 * 
	 * @param is
	 * @throws IOException
	 */
	private void getAttachment(InputStream is,int length) throws IOException {
		boolean exit = false;
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		getIdAttachment(is);
		do {
			byte[] vet = readLineUntilEnd(is,length);
			String str = new String(vet);
			if(this.cursoreHttpInputStream==length){
				if (!str.contains(this.mimeBoundary) && !str.endsWith("--")){
					//System.out.println("SCRIVO ultima cosa letta? ["+str+"]");
					baos.write(vet);
				}
				//System.out.println("ESCO PER LENGTH["+length+"]");
				exit=true;
			}
			else{
				if (str.contains(this.mimeBoundary)){
					//System.out.println("ESCO PER BOUNDARY["+this.mimeBoundary+"]");
					exit=true;
				}
				else {
					//System.out.println("SCRIVO per ["+str+"]");
					baos.write(vet);
				}
			}
		} while (!exit);
		//System.out.println("FINALE["+baos.toString()+"]");
		byte [] b=baos.toByteArray();
		byte [] toAttach=new byte[b.length-1];
		for(int i=0;i<toAttach.length;i++){
			toAttach[i]=b[i];
		}
		this.attachments.add(toAttach);
	}

	/**
	 * Ritorna l'id di un attachment
	 * 
	 * @param is
	 * @throws IOException
	 */
	private void getIdAttachment(InputStream is) throws IOException{ //ad ogni attachment assoccio l'id
		boolean exit=false;
		do{
			byte[] by=readLine(is);
			if(by.length==2){
				if((char)(((Byte)by[0]).byteValue())=='\r')exit=true;
			}
			else{
				String str=new String(by);
				if(str!=null && str.length()>="content-id".length()){
					String checkContentId = str.substring(0, "content-id".length());
					if(checkContentId.equalsIgnoreCase("content-id")){
						String[] contentID=str.split(":",2);
						String rightSide=contentID[1];
						String id=rightSide.trim();
						id=id.substring(1, id.length()-1);
						//System.out.println("--------- ID("+id+") ------------");
						this.idAttachments.add(id);
					}
				}
			}
		}while(!exit);
	}

	/**
	 * Ritorna gli attachments part
	 * 
	 * @param is
	 * @throws IOException
	 */
	public void getAttachmentParts(InputStream is,int length) throws IOException{
		while(this.cursoreHttpInputStream!=length){
			getAttachment(is,length);
		}
		for(int i=1;i<this.attachments.size();i++){
			byte b[] =(byte[])this.attachments.get(i);
			byte[] temp=new byte[b.length-1];
			for(int j=0;j<temp.length;j++){
				temp[j]=b[j];
			}
			this.attachments.set(i,temp);
		}
	}

	/**
	 * Trasforma un vector in byte
	 * 
	 * @param vet
	 * @return byte[]
	 */
	protected byte[] toByte(Vector<?> vet){
		byte[] toRet=new byte[vet.size()];
		for(int i=0;i<vet.size();i++){
			toRet[i]=((Byte)vet.get(i)).byteValue();
		}
		return toRet;
	}


	/*************Dal file si prende il messaggio con attachment************/
	public void readFromFile(String fileName,boolean withMime) throws IOException{
		FileInputStream in = new FileInputStream(new File(fileName));
		BufferedInputStream buff=new BufferedInputStream(in);
		if(withMime){
			readHttpHeaders(buff);
		}
		getBoundary(buff);
		getAttachmentFromFile(buff);
		in.close();
	}



	private void getAttachmentFromFile(BufferedInputStream buff) throws IOException{
		while(!this.finishedMessage)getAttachmentForm(buff);
		for(int i=1;i<this.attachments.size();i++){
			byte[]b=(byte[])this.attachments.get(i);
			byte[] temp=new byte[b.length-1];
			for(int j=0;j<temp.length;j++){
				temp[j]=b[j];
			}
			this.attachments.set(i, temp);
		}
	}



	private boolean finishedMessage=false;
	private void getAttachmentForm(BufferedInputStream buff) throws IOException{
		boolean exit = false;
		ByteArrayOutputStream attach=new ByteArrayOutputStream();
		getIdAttachment(buff);
		do {
			byte [] b=readLine(buff);
			String str = new String(b);

			if (str.contains(this.mimeBoundary)){
				exit=true;
				if((str.substring(0, str.length()-2)).equals("--"+this.mimeBoundary+"--")){
					this.finishedMessage=true;
				}
			}
			else attach.write(b);
		} while (!exit);
		byte[] toAttach=attach.toByteArray();
		byte [] att=new byte[toAttach.length-1];
		for(int i=0;i<att.length;i++){
			att[i]=toAttach[i];
		}
		this.attachments.add(att);
	}


	public Message buildMessage(){
		byte[]mess=(byte[])this.attachments.get(0);
		Message msg=new Message(mess);
		for(int i=1;i<this.attachments.size();i++){
			AttachmentPart att=(AttachmentPart) msg.createAttachmentPart();	
			byte[] attach=(byte[])this.attachments.get(i);
			att.setContent(new ByteArrayInputStream(attach), "text/xml");
			String id=(String) this.idAttachments.get(i);
			att.setContentId(id);
			msg.addAttachmentPart(att);
		}
		if(this.idMessaggio!=null || this.riferimentoAsincrono!=null || this.collaborazione!=null){
			MimeHeaders mime=(MimeHeaders)msg.getMimeHeaders();
			if(this.idMessaggio!=null)
				mime.addHeader(this.testsuiteProperties.getIdMessaggioTrasporto(), this.idMessaggio);
			if(this.riferimentoAsincrono!=null)
				mime.addHeader(this.testsuiteProperties.getRiferimentoAsincronoTrasporto(),this.riferimentoAsincrono);
			if(this.collaborazione!=null)
				mime.addHeader(this.testsuiteProperties.getCollaborazioneTrasporto(),this.collaborazione);
		}

		return msg;
	}

	public void setLength(int length){
		this.contentLength=length;
	}



	public String buildContentType(String str){
		String cont="multipart/related; type=\"text/xml\"; start=\"<"+str+">\"; 	boundary=\""+this.mimeBoundary+"\"";
		return cont;
	}

	public String getIdMessaggio(){
		return this.idMessaggio;
	}

	/****************Questa parte e' relativa ai messaggi con attachment*
	 * @throws IOException */


	public Message createMessage(byte[] b){
		getBoundary(b);
		while(this.cursoreHttpInputStream<b.length-1){
			getID(b);
			byte[] line=readLine(b);
			String str=new String(line);
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			while(!str.startsWith("--"+this.mimeBoundary.substring(0,this.mimeBoundary.length()-2))){
				try {
					baos.write(line);
					baos.write('\n');
				} catch (IOException e) {
					throw new TestSuiteException(e,"Errore nel scrivere nella ArrayOutputStream");
				}
				line=readLine(b);
				str=new String(line);
			}
			byte [] toArray=baos.toByteArray();
			byte [] toAttach=new byte[toArray.length-2];
			for(int i=0;i<toAttach.length;i++){
				toAttach[i]=toArray[i];
			}
			this.attachments.add(toAttach);
		}
		Message msg=buildMessage();
		return msg;
	}

	private void getID(byte[]b){
		boolean toExit=false;
		while(!toExit){
			byte[] temp=readLine(b);
			String line=new String(temp);
			if(line.equals("\r"))toExit=true;
			if(line!=null && line.length()>="content-id".length()){
				String str = line.substring(0, "content-id".length());
				if(str.equalsIgnoreCase("content-id")){
					String[] arr=line.split(":",2);
					String id=arr[1].trim();
					id=id.substring(1, id.length()-1);
					this.idAttachments.add(id);
				}
			}
		}
	}

	private void getBoundary(byte[] b){
		boolean found=false;
		while(!found){
			if((char)b[this.cursoreHttpInputStream]=='-'&&b[this.cursoreHttpInputStream++]=='-'){
				this.cursoreHttpInputStream++;
				byte [] bound=readLine(b);
				this.mimeBoundary=new String(bound);
				found=true;
			}
			this.cursoreHttpInputStream++;
		}

	}


	private byte[] readLine(byte [] b){
		ByteArrayOutputStream bais=new ByteArrayOutputStream();
		while(this.cursoreHttpInputStream<b.length-1 && b[this.cursoreHttpInputStream]!='\n'){
			bais.write(b[this.cursoreHttpInputStream]);
			this.cursoreHttpInputStream++;
		}

		this.cursoreHttpInputStream++;
		byte[] ret=bais.toByteArray();
		return ret;
	}
}