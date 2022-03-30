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

package org.openspcoop2.example.server.mtom.ws;

import java.io.ByteArrayOutputStream;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.BindingProvider;

import org.apache.commons.io.input.ReaderInputStream;
import org.w3c.dom.Document;

/**
 * MTOMServiceExample_MTOMServiceExampleSOAP12InterfaceEndpoint_Client
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class MTOMServiceExample_MTOMServiceExampleSOAP12InterfaceEndpoint_Client {

    private static final QName SERVICE_NAME = new QName("http://www.openspcoop2.org/example/server/mtom/ws", "MTOMServiceExampleSOAP12Service");

    private MTOMServiceExample_MTOMServiceExampleSOAP12InterfaceEndpoint_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
    	
       	/*
    	 * Leggo la configurazione
    	 */
    	
    	java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Client.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}
		
		String invocazioneTramitePdD = reader.getProperty("invocazioneTramitePdD");
		Boolean isInvocazioneTramitePdD = false;
		if(invocazioneTramitePdD == null){
			System.err.println("ERROR : Tipo di invocazione 'invocazioneTramitePdD' (true/false) non definito all'interno del file 'Client.properties'");
			return;
		}else{
			invocazioneTramitePdD = invocazioneTramitePdD.trim();
			isInvocazioneTramitePdD = Boolean.parseBoolean(invocazioneTramitePdD);
		}
		
		
		String file_xml = reader.getProperty("file.xml");
		if(file_xml == null){
			System.err.println("ERROR : File xml da inviare non definito all'interno del file 'Client.properties'");
			return;
		}else{
			file_xml = file_xml.trim();
		}
		File f_xml = new File(file_xml);
		
		String file_other1 = reader.getProperty("file.other1");
		File f_other1 = null;
		if(file_other1 != null){
			file_other1 = file_other1.trim();
			f_other1 = new File(file_other1);
		}
		
		String file_other2 = reader.getProperty("file.other2");
		File f_other2 = null;
		if(file_other2 != null){
			file_other2 = file_other2.trim();
			f_other2 = new File(file_other2);
		}
		
		
		
		String url = null;
		
		if(isInvocazioneTramitePdD){
			
			String urlPD = reader.getProperty("portaDiDominio");
			if(urlPD == null){
				System.err.println("ERROR : Punto di Accesso della porta di dominio non definito all'interno del file 'Client.properties'");
				return;
			}else{
				urlPD = urlPD.trim();
			}
			
			String PD = reader.getProperty("Contesto.soap12");
			if(PD == null){
				System.err.println("ERROR : Contesto non definito all'interno del file 'Client.properties'");
				return;
			}else{
				PD = PD.trim();
			}
			
			if(urlPD.endsWith("/")==false)
				urlPD = urlPD + "/"; 
			url = urlPD + PD; 
			
		}
		else{
			
			String endpoint = reader.getProperty("endpoint.soap12");
			if(endpoint == null){
				System.err.println("ERROR : Punto di Accesso del servizio non definito all'interno del file 'Server.properties'");
				return;
			}else{
				url = endpoint.trim();
			}
			
		}
		
		
		String username = reader.getProperty("username");
		if(username == null){
			System.err.println("ERROR : Username non definita all'interno del file 'Client.properties'");
			return;
		}else{
			username = username.trim();
		}
		
		String password = reader.getProperty("password");
		if(password == null){
			System.err.println("ERROR : Password non definita all'interno del file 'Client.properties'");
			return;
		}else{
			password = password.trim();
		}
    	
        URL wsdlURL = MTOMServiceExampleSOAP12Service.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        MTOMServiceExampleSOAP12Service ss = new MTOMServiceExampleSOAP12Service(wsdlURL, MTOMServiceExample_MTOMServiceExampleSOAP12InterfaceEndpoint_Client.SERVICE_NAME);
        MTOMServiceExample port = ss.getMTOMServiceExampleSOAP12InterfaceEndpoint();  
        
        /*
         * Imposto la url della porta di dominio come destinazione
         * Imposto username e password per l'autenticazione
         */
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  url);
        ((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,  username);
    	((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,  password);
        
        System.out.println("Invoking echo (send file:"+f_xml.getName()+")...");
        java.lang.String _echo_richiesta = f_xml.getName();
        
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		documentFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		Document d = documentBuilder.parse(f_xml);
        
        javax.xml.transform.Source _echo_imageData = new DOMSource(d);
        java.util.List<javax.activation.DataHandler> _echo_other = null;
        if(f_other1!=null || f_other2!=null){
        	_echo_other = new ArrayList<javax.activation.DataHandler>();
        	if(f_other1!=null){
        		FileDataSource fDS = new FileDataSource(f_other1);
        		javax.activation.DataHandler dh = new DataHandler(fDS);
        		_echo_other.add(dh);
        	}
        	if(f_other2!=null){
        		FileDataSource fDS = new FileDataSource(f_other2);
        		javax.activation.DataHandler dh = new DataHandler(fDS);
        		_echo_other.add(dh);
        	}
        }
        javax.xml.ws.Holder<java.lang.String> _echo_risposta = new javax.xml.ws.Holder<java.lang.String>();
        javax.xml.ws.Holder<javax.xml.transform.Source> _echo_imageDataResponse = new javax.xml.ws.Holder<javax.xml.transform.Source>();
        javax.xml.ws.Holder<java.util.List<javax.activation.DataHandler>> _echo_otherResponse = new javax.xml.ws.Holder<java.util.List<javax.activation.DataHandler>>();
        port.echo(_echo_richiesta, _echo_imageData, _echo_other,
        		_echo_risposta, _echo_imageDataResponse,_echo_otherResponse);

        System.out.println("echo._echo_risposta=" + _echo_risposta.value);
             
        System.out.println("echo._echo_imageDataResponse=" + _echo_imageDataResponse.value.getClass().getName());
        if(_echo_imageDataResponse.value instanceof javax.xml.transform.stream.StreamSource){
        	javax.xml.transform.stream.StreamSource ssi = (javax.xml.transform.stream.StreamSource) _echo_imageDataResponse.value;
        	ReaderInputStream ris = new ReaderInputStream(ssi.getReader(),StandardCharsets.UTF_8);
        	documentBuilder = documentFactory.newDocumentBuilder();
        	Document dResponse = documentBuilder.parse(ris);
        	System.out.println("XML received: "+dResponse.toString());
        }
        
        java.util.List<javax.activation.DataHandler> other = null;
        if(_echo_otherResponse!=null){
        	other = _echo_otherResponse.value;
        }
        if(other!=null){
        	System.out.println("risposta.other.size=" + other.size());
        	for (int i = 0; i < other.size(); i++) {
        		javax.activation.DataHandler dh = other.get(i);
        		System.out.println("risposta.other.size[i] received: "+dh.getContent().getClass().getName());
        		ByteArrayOutputStream bout = new ByteArrayOutputStream();
        		InputStream is = dh.getInputStream();
        		int letti = 0;
        		byte [] buffer = new byte[1024];
        		while((letti=is.read(buffer))!=-1){
        			bout.write(buffer, 0, letti);
        		}
        		bout.flush();
        		bout.close();
        		System.out.println("risposta.other.size[i] received bytes: "+bout.size());
        	}
        }
     

        System.exit(0);
    }

    
}
