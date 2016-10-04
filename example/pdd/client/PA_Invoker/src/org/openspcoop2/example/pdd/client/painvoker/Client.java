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



package org.openspcoop2.example.pdd.client.painvoker;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
			System.exit(1);
		}

		java.util.Properties reader = new java.util.Properties();
		try{
			reader.load(new FileInputStream("Client.properties"));
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}


		String SOAPUrl = reader.getProperty("openspcoop2.portaDiDominio");
		if(SOAPUrl == null){
			System.err.println("ERROR : Punto di Accesso della porta di dominio non definito all'interno del file 'Client.properties'");
			return;
		}



		String xmlFile2Send = args[0];
		if(reader.getProperty("busta")!=null){
			xmlFile2Send = reader.getProperty("busta").trim();
		}


		String soapActon = Client.getProperty(reader, "openspcoop2.soapAction", true);
		String contentType = Client.getProperty(reader, "openspcoop2.contentType", true);
		String printFileSent = reader.getProperty("openspcoop2.printFileSent","true");
		boolean isPrintFileSent = Boolean.parseBoolean(printFileSent);
		String printFileReceived = reader.getProperty("openspcoop2.printFileReceived","true");
		boolean isPrintFileReceived = Boolean.parseBoolean(printFileReceived);

		String dateFormatter = Client.getProperty(reader, "openspcoop2.format.date", true);
		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat (dateFormatter);
		String timeFormatter = Client.getProperty(reader, "openspcoop2.format.time", true);
		java.text.SimpleDateFormat timeformat = new java.text.SimpleDateFormat (timeFormatter);
		
		String dateIdFormatter = Client.getProperty(reader, "openspcoop2.format.id.date", true);
		java.text.SimpleDateFormat dateidformat = new java.text.SimpleDateFormat (dateIdFormatter);
		String timeIdFormatter = Client.getProperty(reader, "openspcoop2.format.id.time", true);
		java.text.SimpleDateFormat timeidformat = new java.text.SimpleDateFormat (timeIdFormatter);
		
		System.out.println("Dati utilizzati URL["+SOAPUrl+"] SOAPAction["+soapActon+"] ContentType["+contentType+"] File["+
				xmlFile2Send+"] printFileSent["+isPrintFileSent+"] printFileReceived["+isPrintFileReceived+"]");
		
		System.out.println("Format @DATE@["+dateFormatter+"]  @TIME@["+timeFormatter+"]  @ID-DATE@["+dateIdFormatter+"]  @ID-TIME@["+timeIdFormatter+"]");

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

		String data = bout.toString();
		
		data = data.replaceAll("@ID-DATE@", dateidformat.format(new java.util.Date()));
		data = data.replaceAll("@ID-TIME@", timeidformat.format(new java.util.Date()));
		data = data.replaceAll("@DATE@", dateformat.format(new java.util.Date()));
		data = data.replaceAll("@TIME@", timeformat.format(new java.util.Date()));

		byte[] b = data.getBytes();

		if(isPrintFileSent)
			System.out.println(new String(b));
		else{
			System.out.println("File spedito di dimensione "+b.length+" bytes");
		}



		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty( "Content-Length",
				String.valueOf( b.length ) );
		httpConn.setRequestProperty("Content-Type",contentType);
		httpConn.setRequestProperty("SOAPAction",soapActon);
		httpConn.setRequestMethod( "POST" );
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);

		// Everything's set up; send the XML that was read in to b.
		OutputStream out = httpConn.getOutputStream();
		out.write( b );    
		out.close();

		System.out.println("\nMessaggio SOAP inviato!\n");

		// Read the response and write it to standard out.
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
		
		ByteArrayOutputStream boutResponse = new ByteArrayOutputStream();
		if(resultHTTPOperation<300)
			Client.copy(httpConn.getInputStream(),boutResponse);
		else
			Client.copy(httpConn.getErrorStream(),boutResponse);
		boutResponse.flush();
		boutResponse.flush();
		System.out.println("--------------------------------------------");
		if(isPrintFileReceived)
			System.out.println(boutResponse.toString());
		else{
			System.out.println("File ricevuto di dimensione "+boutResponse.size()+" bytes");
		}

	}

	// copy method from From E.R. Harold's book "Java I/O"
	public static void copy(InputStream in, OutputStream out) 
			throws IOException {

		// do not allow other threads to read from the
		// input or write to the output while copying is
		// taking place

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
