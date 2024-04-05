/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.example.pdd.client.benchmark;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.hc.core5.benchmark.BenchmarkConfig;
import org.apache.hc.core5.benchmark.HttpBenchmark;
import org.apache.hc.core5.benchmark.ResultFormatter;
import org.apache.hc.core5.benchmark.Results;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

import jakarta.activation.DataHandler;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.util.ByteArrayDataSource;


/**
 * Client di test per provare la porta di dominio 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Client {
	
	private static Logger log = null;
	private static void info(String msg){
		log.info(msg);
	}
	private static void error(String msg){
		log.error(msg);
	}
	
	public static void main(String[] args) throws Exception {

		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.DEBUG);
		log = LoggerWrapperFactory.getLogger(Client.class);
		
		if (args.length  < 1) {
			error("ERROR, Usage:  java Client " +
			"soapEnvelopefile.xml");
			error("PortaDelegata,User,Password impostabili da file 'properties'.");
			System.exit(1);
		}

		java.util.Properties reader = new java.util.Properties();
		try (InputStreamReader isr = new InputStreamReader(
			    new FileInputStream("Client.properties"), Charset.UTF_8.getValue());){ 
			reader.load(isr);
		}catch(java.io.IOException e) {
			error("ERROR : "+e.toString());
			return;
		}

		String interfaceName = Client.getProperty(reader, "openspcoop2.PD", true);
		String httpMethod = Client.getProperty(reader, "openspcoop2.httpMethod", false);
		if(httpMethod==null) {
			httpMethod = "POST";
		}
		String soapActon = Client.getProperty(reader, "openspcoop2.soapAction", false);
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
		


		if(!urlPD.endsWith("/")) {
			if(!interfaceName.startsWith("/")) {
				urlPD = urlPD + "/"; 
			}		
		}
		else {
			if(interfaceName.startsWith("/") && interfaceName.length()>1) {
				interfaceName = interfaceName.substring(1); 
			}
		}
		String soapUrl = urlPD + interfaceName; 

		String xmlFile2Send = args[0];
		if(reader.getProperty("file")!=null){
			xmlFile2Send = reader.getProperty("file").trim();
		}

		String printFileSent = reader.getProperty("openspcoop2.printFileSent","true");
		boolean isPrintFileSent = Boolean.parseBoolean(printFileSent);
		String printFileReceived = reader.getProperty("openspcoop2.printFileReceived","true");
		boolean isPrintFileReceived = Boolean.parseBoolean(printFileReceived);
		
		info("Dati utilizzati URL["+soapUrl+"] Method["+httpMethod+"] SOAPAction["+soapActon+"] ContentType["+contentType+"] File["+
				xmlFile2Send+"] printFileSent["+isPrintFileSent+"] printFileReceived["+isPrintFileReceived+"]");
		
		String user = Client.getProperty(reader, "openspcoop2.username", false);
		String passw = Client.getProperty(reader, "openspcoop2.password", false);
		if((user == null) || (passw == null))
			info("WARNING : Authentication not used.\n\n");
		else{
			info("Autenticazione BASIC Username["+user+"] Password["+passw+"]");
		}


		BenchmarkConfig.Builder builder = BenchmarkConfig.custom();
		
        // Set verbosity level - 4 and above prints response
        // content, 3 and above prints information on headers,
        // 2 and above prints response codes (404, 200, etc.),
        //1 and above prints warnings and info.
		builder.setVerbosity(Integer.parseInt(Client.getProperty(reader, "openspcoop2.verbosity", true)));
        
        // Enable the HTTP KeepAlive feature, i.e., perform multiple requests within one HTTP session. 
        // Default is no KeepAlive
		builder.setKeepAlive(Boolean.parseBoolean(Client.getProperty(reader, "openspcoop2.keepAlive", true)));
        
        // Concurrency while performing the benchmarking session. 
        // The default is to just use a single thread/client.
		builder.setConcurrencyLevel(Integer.parseInt(Client.getProperty(reader, "openspcoop2.threads", true)));
        
        String numReq = Client.getProperty(reader, "openspcoop2.requests", false);
        String timeSec = Client.getProperty(reader, "openspcoop2.durationInSeconds", false);
        if(numReq!=null && timeSec!=null){
        	builder.setRequests(Integer.parseInt(numReq));
        	builder.setTimeLimit(TimeValue.of(Integer.parseInt(timeSec), TimeUnit.SECONDS));
        }
        else if(numReq!=null){
	        //  Number of requests to perform for the benchmarking session. 
	        // The default is to just perform a single request which usually leads to non-representative benchmarking results.
	        builder.setRequests(Integer.parseInt(numReq));
        }else{
        	// Durata del test in secondi
        	builder.setTimeLimit(TimeValue.of(Integer.parseInt(timeSec), TimeUnit.SECONDS));
        	builder.setRequests(Integer.MAX_VALUE); // infinito
        }
                
        // Method
        builder.setMethod(httpMethod);
                        
        // SOAPAction
        if(soapActon!=null && !"".equals(soapActon)) {
        	builder.setSoapAction(soapActon);
        }
        
        // Socket Timeout
        builder.setSocketTimeout(Timeout.of(Integer.parseInt(Client.getProperty(reader, "openspcoop2.socketTimeout", true)),TimeUnit.MILLISECONDS));
        
        // Url
        builder.setUri(new java.net.URI(soapUrl));
        
        // Https
        boolean disableSSLVerification = Boolean.parseBoolean(Client.getProperty(reader, "openspcoop2.disableSSLVerification", true));
        builder.setDisableSSLVerification(disableSSLVerification);
        if(!disableSSLVerification && soapUrl.startsWith("https:")) {
        	builder.setTrustStorePath(Client.getProperty(reader, "openspcoop2.trustStorePath", true));
        	builder.setTrustStorePassword(Client.getProperty(reader, "openspcoop2.trustStorePassword", true));
        }
        
        // HttpsClient
        String keystore = Client.getProperty(reader, "openspcoop2.keyStorePath", false);
        if(keystore!=null && !"".equals(keystore)) {
        	String keystorePassword = Client.getProperty(reader, "openspcoop2.keyStorePassword", true);
        	builder.setIdentityStorePath(keystore);
        	builder.setIdentityStorePassword(keystorePassword);
        }
        
        // Chunking
        builder.setUseChunking(Boolean.parseBoolean(Client.getProperty(reader, "openspcoop2.useChunking", true)));
        
        // Http2
        builder.setForceHttp2(Boolean.parseBoolean(Client.getProperty(reader, "openspcoop2.http2", true)));
        
        // File
        File file = new File(xmlFile2Send);
        // Attachment directory
        String attachDir = Client.getProperty(reader, "openspcoop2.attachmentDirectory", false);
        if(attachDir!=null){
        	String subType = Client.getProperty(reader, "openspcoop2.attachment.subType", false);
        	if(subType==null || "".equals(subType)) {
        		subType = HttpConstants.CONTENT_TYPE_MULTIPART_RELATED_SUBTYPE;
        	}
        	
        	StringBuilder ctBuilder = new StringBuilder();
        	file = buildMime(subType, file, attachDir, ctBuilder);
        	contentType = ctBuilder.toString();
        }
        builder.setPayloadFile(file);
        
        // ContentType
        if(contentType!=null && !"".equals(contentType)) {
        	jakarta.mail.internet.ContentType ct = new jakarta.mail.internet.ContentType(contentType);
        	if(ct.getParameterList()!=null && ct.getParameterList().size()>0) {
        		List<NameValuePair> l = new ArrayList<>();
        		Enumeration<String> names = ct.getParameterList().getNames();
        		while (names.hasMoreElements()) {
					String name = names.nextElement();
					String value = ct.getParameter(name);
					NameValuePair nv = new BasicNameValuePair(name, value);
					l.add(nv);
				}
        		builder.setContentType(ContentType.create(ct.getBaseType(),l.toArray(new NameValuePair[1])));
        	}
        	else {
        		builder.setContentType(ContentType.create(ct.getBaseType()));
        	}
        }
        
        List<String> headers = new ArrayList<>();
        
        // Autenticazione Basic
        String authentication = "";
		if(user!=null && passw!=null){
			authentication = user + ":" + passw;
			authentication = "Basic " + Base64Utilities.encodeAsString(authentication.getBytes());
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
		
		if(!headers.isEmpty()){
			builder.setHeaders(headers.toArray(new String[1]));
		}
		
		BenchmarkConfig config = builder.build();
		
        // Esecuzione
        HttpBenchmark httpBenchmark = new HttpBenchmark(config);
        Results risultato = httpBenchmark.execute();
		
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try(PrintStream pin = new PrintStream(buf, true, StandardCharsets.US_ASCII.name())){
        	ResultFormatter.print(pin, risultato);
        	pin.flush();
        	buf.flush();
        }
        buf.close();
        
        try(FileOutputStream fout = new FileOutputStream("Result.txt");){
	        fout.write(buf.toByteArray());
	        fout.flush();
        }
        
        info(buf.toString());
		
	}

	
	private static String getProperty(Properties reader,String key, boolean required) throws UtilsException{
	
		String tmp = reader.getProperty(key);
		if(tmp == null){
			if(required){
				throw new UtilsException("ERROR : Proprieta' ["+key+"] non definita all'interno del file 'Client.properties'");
			}
			else{
				return null;
			}
		}else{
			tmp = tmp.trim();
			return tmp;
		}
		
	}
	
	private static java.util.Properties readProperties (String prefix,java.util.Properties sorgente)throws UtilsException{
		java.util.Properties prop = new java.util.Properties();
		try{ 
			java.util.Enumeration<?> en = sorgente.propertyNames();
			while (en.hasMoreElements()) {
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
			throw new UtilsException("Utilities.readProperties Riscontrato errore durante la lettura delle propriete con prefisso ["+prefix+"]: "+e.getMessage(),e);
		}  
	}
	
	private static File buildMime(String subType, File payloadFile, String attachDir, StringBuilder ctBuilder) throws UtilsException, MessagingException, IOException {
		
    	MimeMultipart mm = new MimeMultipart(subType);
    	
    	addPart(mm, payloadFile);
    	
    	File f = new File(attachDir);
    	if(f.exists() && f.isDirectory()) {
    		File [] c = f.listFiles();
    		if(c.length>0) {
    			for (File file : c) {
    				if(file.exists() && file.isFile()) {
    					addPart(mm, file);
    				}
				}
    		}
    	}
    	
    	File fTmp = File.createTempFile("mime", ".bin");
    	try(FileOutputStream fout = new FileOutputStream(fTmp)){
    		mm.writeTo(fout);
    		fout.flush();
    	}
    	
    	ctBuilder.append(mm.getContentType());
    	
    	return fTmp;
	}
	private static void addPart(MimeMultipart mm, File file) throws UtilsException, MessagingException, FileNotFoundException {
		MimeTypes mimeTypes = MimeTypes.getInstance();
    	String type = mimeTypes.getMimeType(file);
		byte[] content = FileSystemUtilities.readBytesFromFile(file);
		BodyPart body = new MimeBodyPart();
		body.setDataHandler(new DataHandler(new ByteArrayDataSource(content, type)));
		body.addHeader(HttpConstants.CONTENT_TYPE, type);
		if(file.getName()!=null) {
			String hV = HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA+"; "+HttpConstants.CONTENT_DISPOSITION_NAME_PREFIX+""+file.getName();
			String fileName = file.getName();
			if(fileName!=null) {
				hV = hV + "; "+HttpConstants.CONTENT_DISPOSITION_FILENAME_PREFIX+fileName;
			}
			body.addHeader(HttpConstants.CONTENT_DISPOSITION,hV);
		}
		mm.addBodyPart(body);
	}
}
