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



package org.openspcoop2.example.pdd.client.pdinvoker;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;


/**
 * Client di test per provare la porta di dominio 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Client {
	public static void main(String[] args) throws Exception {

		System.out.println();

		if (args.length  < 1) {
			System.err.println("ERROR, Usage:  java Client " +
			"soapEnvelopefile.xml");
			System.err.println("PortaDelegata,User,Password impostabili da file 'properties'.");
			System.exit(1);
		}

		java.util.Properties reader = new java.util.Properties();
		try{  
			InputStreamReader isr = new InputStreamReader(
				    new FileInputStream("Client.properties"), "UTF-8");
			reader.load(isr); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}

		String PD = Client.getProperty(reader, "openspcoop2.PD", true);
		String httpMethod = Client.getProperty(reader, "openspcoop2.httpMethod", false);
		if(httpMethod==null) {
			httpMethod = "POST";
		}
		String soapActon = Client.getProperty(reader, "openspcoop2.soapAction", true);
		String contentType = Client.getProperty(reader, "openspcoop2.contentType", false);
		String urlPD = Client.getProperty(reader, "openspcoop2.portaDiDominio", true);

		
		String riferimentoMessaggio = Client.getProperty(reader, "openspcoop2.integrazione.riferimentoMessaggio", false);
		String idCollaborazione = Client.getProperty(reader, "openspcoop2.integrazione.collaborazione", false);
		String servizioApplicativo = Client.getProperty(reader, "openspcoop2.integrazione.servizioApplicativo", false);
		String idApplicativo = Client.getProperty(reader, "openspcoop2.integrazione.identificativoCorrelazioneApplicativa", false);
		String tipoDestinatario = Client.getProperty(reader, "openspcoop2.integrazione.tipoDestinatario", false);
		String destinatario = Client.getProperty(reader, "openspcoop2.integrazione.destinatario", false);
		String tipoServizio = Client.getProperty(reader, "openspcoop2.integrazione.tipoServizio", false);
		String servizio = Client.getProperty(reader, "openspcoop2.integrazione.servizio", false);
		String azione = Client.getProperty(reader, "openspcoop2.integrazione.azione", false);
		
		String saveResponseIn = Client.getProperty(reader, "openspcoop2.saveResponseIn", false);
		
		@SuppressWarnings("unused")
		String trasportoKeywordTipoMittente = Client.getProperty(reader, "openspcoop2.trasporto.keyword.tipoMittente", true);
		@SuppressWarnings("unused")
		String trasportoKeywordMittente = Client.getProperty(reader, "openspcoop2.trasporto.keyword.mittente", true);
		String trasportoKeywordTipoDestinatario = Client.getProperty(reader, "openspcoop2.trasporto.keyword.tipoDestinatario", true);
		String trasportoKeywordDestinatario = Client.getProperty(reader, "openspcoop2.trasporto.keyword.destinatario", true);
		String trasportoKeywordTipoServizio = Client.getProperty(reader, "openspcoop2.trasporto.keyword.tipoServizio", true);
		String trasportoKeywordServizio = Client.getProperty(reader, "openspcoop2.trasporto.keyword.servizio", true);
		String trasportoKeywordAzione = Client.getProperty(reader, "openspcoop2.trasporto.keyword.azione", true);
		@SuppressWarnings("unused")
		String trasportoKeywordID = Client.getProperty(reader, "openspcoop2.trasporto.keyword.identificativo", true);
		String trasportoKeywordRiferimentoMessaggio = Client.getProperty(reader, "openspcoop2.trasporto.keyword.riferimentoMessaggio", true);
		String trasportoKeywordIDCollaborazione = Client.getProperty(reader, "openspcoop2.trasporto.keyword.idCollaborazione", true);
		String trasportoKeywordIDApplicativo = Client.getProperty(reader, "openspcoop2.trasporto.keyword.idApplicativo", true);
		String trasportoKeywordServizioApplicativo = Client.getProperty(reader, "openspcoop2.trasporto.keyword.servizioApplicativo", true);
		@SuppressWarnings("unused")
		String trasportoKeywordTransazione = Client.getProperty(reader, "openspcoop2.trasporto.keyword.transazione", true);
		
		// Header Custom
        List<String> headersKeys = new ArrayList<String>();
        List<String> headersValues = new ArrayList<String>();
		Properties p = Utilities.readProperties("openspcoop2.header.", reader);
		if(p!=null && p.size()>0){
			Enumeration<?> enKeys = p.keys();
			while (enKeys.hasMoreElements()) {
				String key = (String) enKeys.nextElement();
				String value = p.getProperty(key);
				headersKeys.add(key);
				headersValues.add(value);
			}
		}


		//if(urlPD.endsWith("/")==false)
		//	urlPD = urlPD + "/"; 
		String SOAPUrl = urlPD + PD; 

		String xmlFile2Send = args[0];
		if(reader.getProperty("file")!=null){
			xmlFile2Send = reader.getProperty("file").trim();
		}

		String printFileSent = reader.getProperty("openspcoop2.printFileSent","true");
		boolean isPrintFileSent = Boolean.parseBoolean(printFileSent);
		String printFileReceived = reader.getProperty("openspcoop2.printFileReceived","true");
		boolean isPrintFileReceived = Boolean.parseBoolean(printFileReceived);
		
		System.out.println("Dati utilizzati URL["+SOAPUrl+"] Method["+httpMethod+"] SOAPAction["+soapActon+"] ContentType["+contentType+"] File["+
				xmlFile2Send+"] printFileSent["+isPrintFileSent+"] printFileReceived["+isPrintFileReceived+"]");
		
		String user = Client.getProperty(reader, "openspcoop2.username", false);
		String passw = Client.getProperty(reader, "openspcoop2.password", false);
		if((user == null) || (passw == null))
			System.err.println("WARNING : Authentication not used.\n\n");
		else{
			System.err.println("Autenticazione BASIC Username["+user+"] Password["+passw+"]");
		}


		// Create the connection where we're going to send the file.
		URL url = new URL(SOAPUrl);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;

		// Open the input file. After we copy it to a byte array, we can see
		// how big it is so that we can set the HTTP Cotent-Length
		// property. (See complete e-mail below for more on this.)

		FileInputStream fin = new FileInputStream(xmlFile2Send);

		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		// Copy the SOAP file to the open connection.
		Client.copy(fin,bout);
		fin.close();

		byte[] b = bout.toByteArray();

		if(isPrintFileSent)
			System.out.println(new String(b));
		else{
			System.out.println("File spedito di dimensione "+b.length+" bytes");
		}

		// Authentication
		String authentication = "";
		if(user!=null && passw!=null){
			authentication = user + ":" + passw;
			authentication = "Basic " + Base64Utilities.encodeAsString(authentication.getBytes("UTF-8"));
			//System.out.println("CODE["+authentication+"]");
		}

		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty( "Content-Length",
				String.valueOf( b.length ) );
		if(contentType!=null && !"".equals(contentType)) {
			httpConn.setRequestProperty("Content-Type",contentType);
		}
		httpConn.setRequestProperty("SOAPAction",soapActon);
		if(riferimentoMessaggio!=null){
			riferimentoMessaggio = riferimentoMessaggio.trim();
			httpConn.setRequestProperty(trasportoKeywordRiferimentoMessaggio,riferimentoMessaggio);
		}
		if(idCollaborazione!=null){
			httpConn.setRequestProperty(trasportoKeywordIDCollaborazione,idCollaborazione);
		}
		if(servizioApplicativo!=null){
			httpConn.setRequestProperty(trasportoKeywordServizioApplicativo,servizioApplicativo);
		}
		if(idApplicativo!=null){
			httpConn.setRequestProperty(trasportoKeywordIDApplicativo,idApplicativo);
		}
		if(tipoDestinatario!=null){
			httpConn.setRequestProperty(trasportoKeywordTipoDestinatario,tipoDestinatario);
		}
		if(destinatario!=null){
			httpConn.setRequestProperty(trasportoKeywordDestinatario,destinatario);
		}
		if(tipoServizio!=null){
			httpConn.setRequestProperty(trasportoKeywordTipoServizio,tipoServizio);
		}
		if(servizio!=null){
			httpConn.setRequestProperty(trasportoKeywordServizio,servizio);
		}
		if(azione!=null){
			httpConn.setRequestProperty(trasportoKeywordAzione,azione);
		}
		if(user != null && passw != null)
			httpConn.setRequestProperty("Authorization",authentication);
		if(headersKeys!=null) {
			for (int i = 0; i < headersKeys.size(); i++) {
				httpConn.setRequestProperty(headersKeys.get(i),headersValues.get(i));
			}
		}
		HttpRequestMethod httpRequestMethod = HttpRequestMethod.valueOf(httpMethod);
		HttpUtilities.setStream(httpConn, httpRequestMethod, contentType);
		HttpBodyParameters httpBody = new  HttpBodyParameters(httpRequestMethod, contentType);

		if(httpBody.isDoOutput()){
			// Everything's set up; send the XML that was read in to b.
			OutputStream out = httpConn.getOutputStream();
			out.write( b );    
			out.close();
		}

		System.out.println("\nMessaggio SOAP inviato!\n");

		// STATO INVOCAZIONE
		int resultHTTPOperation = httpConn.getResponseCode();
		System.out.println("Stato Invocazione ["+resultHTTPOperation+"]");
		Map<String,List<String>> headerResponse =  httpConn.getHeaderFields();
		Set<String> keys = headerResponse.keySet();
		java.util.Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String key = it.next();
			//if(key!=null && (key.startsWith("X-SPCoop")) )
			if(key!=null)
				System.out.println(key+" : "+headerResponse.get(key));
		}
		
		// Read the response and write it to standard out.
		ByteArrayOutputStream boutResponse = new ByteArrayOutputStream();
		if(resultHTTPOperation<300)
			Client.copy(httpConn.getInputStream(),boutResponse);
		else
			Client.copy(httpConn.getErrorStream(),boutResponse);
		boutResponse.flush();
		boutResponse.close();
		System.out.println("--------------------------------------------");
		if(boutResponse.size()>0) {
			if(isPrintFileReceived) {
				System.out.println(boutResponse.toString());
			}else{
				System.out.println("File ricevuto di dimensione "+boutResponse.size()+" bytes");
			}
			if(saveResponseIn!=null) {
				FileSystemUtilities.writeFile(saveResponseIn, boutResponse.toByteArray());
				System.out.println("File ricevuto salvato in ["+saveResponseIn+"]");
			}
		}
		else {
			System.out.println("Risposta non contiene alcun contenuto");
		}

	}

	// copy method from From E.R. Harold's book "Java I/O"
	public static void copy(InputStream in, OutputStream out) 
	throws IOException {

		// do not allow other threads to read from the
		// input or write to the output while copying is
		// taking place

		if(in==null) {
			return;
		}
		
		synchronized (in) {
			synchronized (out) {

				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = in.read(buffer);
					if (bytesRead == -1) break;
					out.write(buffer, 0, bytesRead);
				}
			}
		}
	} 
	
	private static String getProperty(Properties reader,String key, boolean required) throws Exception{
	
		String tmp = reader.getProperty(key);
		if(tmp == null){
			if(required){
				throw new Exception("ERROR : Proprieta' ["+key+"] non definita all'interno del file 'Client.properties'");
			}
			else{
				return null;
			}
		}else{
			tmp = tmp.trim();
			return tmp;
		}
		
	}
}
