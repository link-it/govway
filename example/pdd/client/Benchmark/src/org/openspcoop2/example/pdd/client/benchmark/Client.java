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



package org.openspcoop2.example.pdd.client.benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.http.benchmark.Config;
import org.apache.http.benchmark.HttpBenchmark;
import org.apache.soap.encoding.soapenc.Base64;


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
			reader.load(new FileInputStream("Client.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}

		String PD = Client.getProperty(reader, "openspcoop2.PD", true);
		String soapActon = Client.getProperty(reader, "openspcoop2.soapAction", true);
		String contentType = Client.getProperty(reader, "openspcoop2.contentType", true);
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
		
		System.out.println("Dati utilizzati URL["+SOAPUrl+"] SOAPAction["+soapActon+"] ContentType["+contentType+"] File["+
				xmlFile2Send+"] printFileSent["+isPrintFileSent+"] printFileReceived["+isPrintFileReceived+"]");
		
		String user = Client.getProperty(reader, "openspcoop2.username", false);
		String passw = Client.getProperty(reader, "openspcoop2.password", false);
		if((user == null) || (passw == null))
			System.err.println("WARNING : Authentication not used.\n\n");
		else{
			System.err.println("Autenticazione BASIC Username["+user+"] Password["+passw+"]");
		}


		
        Config config = new Config();
        
        // Set verbosity level - 4 and above prints response
        // content, 3 and above prints information on headers,
        // 2 and above prints response codes (404, 200, etc.),
        //1 and above prints warnings and info.
        config.setVerbosity(Integer.parseInt(Client.getProperty(reader, "openspcoop2.verbosity", true)));
        
        // Enable the HTTP KeepAlive feature, i.e., perform multiple requests within one HTTP session. 
        // Default is no KeepAlive
        config.setKeepAlive(Boolean.parseBoolean(Client.getProperty(reader, "openspcoop2.keepAlive", true)));
        
        // Concurrency while performing the benchmarking session. 
        // The default is to just use a single thread/client.
        config.setThreads(Integer.parseInt(Client.getProperty(reader, "openspcoop2.threads", true)));
        
        String numReq = Client.getProperty(reader, "openspcoop2.requests", false);
        String timeSec = Client.getProperty(reader, "openspcoop2.durationInSeconds", false);
        if(numReq!=null && timeSec!=null){
        	throw new Exception("Indicare solo una tra le due seguenti proprietà 'openspcoop2.requests' e 'openspcoop2.durationInSeconds'");
        }
        else if(numReq!=null){
	        //  Number of requests to perform for the benchmarking session. 
	        // The default is to just perform a single request which usually leads to non-representative benchmarking results.
	        config.setRequests(Integer.parseInt(numReq));
        }
        else{
        	// Durata del test in secondi
        	config.setDurationSec(Integer.parseInt(timeSec));
        }
        
        // ContentType
        config.setContentType(contentType);
        
        // Method
        config.setMethod("POST");
        
        // File
        config.setPayloadFile(new File(xmlFile2Send));
                
        // SOAPAction
        config.setSoapAction(soapActon);
        
        // Socket Timeout
        config.setSocketTimeout(Integer.parseInt(Client.getProperty(reader, "openspcoop2.socketTimeout", true)));
        
        // Url
        config.setUrl(new URL(SOAPUrl));
        
        // Chunking
        config.setUseChunking(Boolean.parseBoolean(Client.getProperty(reader, "openspcoop2.useChunking", true)));
        
        // Http1.0
        config.setUseHttp1_0(Boolean.parseBoolean(Client.getProperty(reader, "openspcoop2.http10", true)));
        
        // Attchment directory
        String attachDir = Client.getProperty(reader, "openspcoop2.attachmentDirectory", false);
        if(attachDir!=null){
        	File f = new File(attachDir);
        	config.setAttachmentsDir(f);
        }
        
        // Gestione
        String busta = Client.getProperty(reader, "openspcoop2.busta", false);
        if(busta!=null){
        	File f = new File(busta);
        	config.setBustaFileHeader(f);
        }
        
        // FileBodyTemplate (Possibilità di generare un messaggio dinamico)
        // Non è utilizzabile in combinazione con l'utilizzo di una busta (in tal caso aggiungere l'id dinamico nell'header).
        String bodyTemplate = Client.getProperty(reader, "file.isTemplate", false);
        if(bodyTemplate!=null){
        	config.setPayloadFileTemplate(Boolean.parseBoolean(bodyTemplate));
        }
        
        // Accepted Return Code
        String acceptedReturnCode = Client.getProperty(reader, "openspcoop2.acceptedReturnCode", false);
        if(acceptedReturnCode!=null){
        	List<Integer> acceptedReturnCodes = new ArrayList<Integer>();
        	String [] tmp = acceptedReturnCode.split(",");
        	for (int i = 0; i < tmp.length; i++) {
        		acceptedReturnCodes.add(Integer.parseInt(tmp[i].trim()));
			}
        	config.setAcceptedReturnCode(acceptedReturnCodes);
        }

        // Random Time
        String randomInterval = Client.getProperty(reader, "openspcoop2.randomTimeIntervalBeforeInvoke", false);
        if(randomInterval!=null){
        	boolean randomIntervalEnabled = Boolean.parseBoolean(randomInterval);
        	if(randomIntervalEnabled){
        		config.setSleepMinBeforeRun(Integer.parseInt(Client.getProperty(reader, "openspcoop2.randomTimeIntervalBeforeInvoke.minIntervalMS", true)));
        		config.setSleepMaxBeforeRun(Integer.parseInt(Client.getProperty(reader, "openspcoop2.randomTimeIntervalBeforeInvoke.maxIntervalMS", true)));
        		String randomIntervalEveryMessage = Client.getProperty(reader, "openspcoop2.randomTimeIntervalBeforeInvoke.sleepEveryMessage", false);
                if(randomIntervalEveryMessage!=null){
                	config.setSleepBeforeEveryMessage(Boolean.parseBoolean(randomIntervalEveryMessage));
                }
        	}
        }

        
        List<String> headers = new ArrayList<String>();
        
        // Autenticazione Basic
        String authentication = "";
		if(user!=null && passw!=null){
			authentication = user + ":" + passw;
			authentication = "Basic " + Base64.encode(authentication.getBytes());
			//System.out.println("CODE["+authentication+"]");
			headers.add("Authorization:"+authentication);
		}
        
		// Header Custom
		Properties p = readProperties("openspcoop2.header.", reader);
		if(p!=null && p.size()>0){
			Enumeration<?> enKeys = p.keys();
			while (enKeys.hasMoreElements()) {
				String key = (String) enKeys.nextElement();
				String value = p.getProperty(key);
				headers.add(key+":"+value);
			}
		}
		
		// Altri header
		if(riferimentoMessaggio!=null){
			headers.add(trasportoKeywordRiferimentoMessaggio+":"+riferimentoMessaggio.trim());
		}
		if(idCollaborazione!=null){
			headers.add(trasportoKeywordIDCollaborazione+":"+idCollaborazione.trim());
		}
		if(servizioApplicativo!=null){
			headers.add(trasportoKeywordServizioApplicativo+":"+servizioApplicativo.trim());
		}
		if(idApplicativo!=null){
			headers.add(trasportoKeywordIDApplicativo+":"+idApplicativo.trim());
		}
		if(tipoDestinatario!=null){
			headers.add(trasportoKeywordTipoDestinatario+":"+tipoDestinatario.trim());
		}
		if(destinatario!=null){
			headers.add(trasportoKeywordDestinatario+":"+destinatario.trim());
		}
		if(tipoServizio!=null){
			headers.add(trasportoKeywordTipoServizio+":"+tipoServizio.trim());
		}
		if(servizio!=null){
			headers.add(trasportoKeywordServizio+":"+servizio.trim());
		}
		if(azione!=null){
			headers.add(trasportoKeywordAzione+":"+azione.trim());
		}
		
		if(headers.size()>0){
			config.setHeaders(headers.toArray(new String[1]));
		}
		
        // Esecuzione
        HttpBenchmark httpBenchmark = new HttpBenchmark(config);
        String risultato = httpBenchmark.execute();
		
        FileOutputStream fout = new FileOutputStream("Result.txt");
        fout.write(risultato.getBytes());
        fout.flush();
        fout.close();
        
        System.out.println(risultato);
		


		
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
	
	private static java.util.Properties readProperties (String prefix,java.util.Properties sorgente)throws Exception{
		java.util.Properties prop = new java.util.Properties();
		try{ 
			java.util.Enumeration<?> en = sorgente.propertyNames();
			for (; en.hasMoreElements() ;) {
				String property = (String) en.nextElement();
				if(property.startsWith(prefix)){
					String key = (property.substring(prefix.length()));
					if(key != null)
						key = key.trim();
					String value = sorgente.getProperty(property);
					if(value!=null)
						value = value.trim();
					if(key!=null && value!=null)
						prop.setProperty(key,value);
				}
			}
			return prop;
		}catch(java.lang.Exception e) {
			throw new Exception("Utilities.readProperties Riscontrato errore durante la lettura delle propriete con prefisso ["+prefix+"]: "+e.getMessage(),e);
		}  
	}
}
