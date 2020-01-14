/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.openspcoop2.example.server.mtom.ws;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.io.input.ReaderInputStream;
import org.w3c.dom.Document;

/**
 * MTOMServiceExampleImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MTOMServiceExampleImpl {

	private static final Logger LOG = Logger.getLogger(MTOMServiceExampleImpl.class.getName());
	
    public static void echo(java.lang.String richiesta,
    		javax.xml.transform.Source imageData,
    		java.util.List<javax.activation.DataHandler> other,
    		javax.xml.ws.Holder<java.lang.String> risposta,
    		javax.xml.ws.Holder<javax.xml.transform.Source> imageDataResponse,
    		javax.xml.ws.Holder<java.util.List<javax.activation.DataHandler>> otherResponse) { 
      
    	try{
    		
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			documentFactory.setNamespaceAware(true);
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			
			LOG.fine("*** RICHIESTA **** ");
			LOG.fine("richiesta=" + richiesta);
			LOG.fine("richiesta.imageData=" + imageData.getClass().getName());
			Document d = null;
	        if(imageData instanceof javax.xml.transform.stream.StreamSource){
	        	javax.xml.transform.stream.StreamSource ssi = (javax.xml.transform.stream.StreamSource) imageData;
	        	documentBuilder = documentFactory.newDocumentBuilder();
	             if(ssi.getReader()!=null){
	     	        ReaderInputStream ris = new ReaderInputStream(ssi.getReader(),StandardCharsets.UTF_8);
	     	        d = documentBuilder.parse(ris);
	             }
	             else{
	            	 d = documentBuilder.parse(ssi.getInputStream());
	             }
	        	LOG.fine("XML received");
	        }
	        List<byte[]> dhReceived = new ArrayList<byte[]>();
	        List<String> dhReceivedCT = new ArrayList<String>();
	        if(other!=null){
	        	LOG.fine("richiesta.other.size=" + other.size());
	        	for (int i = 0; i < other.size(); i++) {
	        		javax.activation.DataHandler dh = other.get(i);
	        		LOG.fine("richiesta.other.size[i] received: "+dh.getContent().getClass().getName());
	        		ByteArrayOutputStream bout = new ByteArrayOutputStream();
	        		InputStream is = dh.getInputStream();
	        		int letti = 0;
	        		byte [] buffer = new byte[1024];
	        		while((letti=is.read(buffer))!=-1){
	        			bout.write(buffer, 0, letti);
	        		}
	        		bout.flush();
	        		bout.close();
	        		dhReceived.add(bout.toByteArray());
	        		dhReceivedCT.add(dh.getContentType());
	        	}
	        }
    	

            java.lang.String rispostaValue = richiesta; // echo
            risposta.value = rispostaValue;
            
            javax.xml.transform.Source imageDataResponseValue = new DOMSource(d);
            imageDataResponse.value = imageDataResponseValue;
            
            java.util.List<javax.activation.DataHandler> otherResponseValue = null;
            if(dhReceived!=null && dhReceived.size()>0){
            	otherResponseValue = new ArrayList<javax.activation.DataHandler>();
            	for (int i = 0; i < dhReceived.size(); i++) {
            		javax.mail.util.ByteArrayDataSource fDS = new javax.mail.util.ByteArrayDataSource(dhReceived.get(i),dhReceivedCT.get(i));
            		javax.activation.DataHandler dh = new DataHandler(fDS);
            		otherResponseValue.add(dh);
				}
            }
            otherResponse.value = otherResponseValue;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
