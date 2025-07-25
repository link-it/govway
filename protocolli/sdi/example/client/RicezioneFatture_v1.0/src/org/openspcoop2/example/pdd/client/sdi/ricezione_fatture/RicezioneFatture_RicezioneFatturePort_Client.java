/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

package org.openspcoop2.example.pdd.client.sdi.ricezione_fatture;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;
import jakarta.xml.ws.BindingProvider;

import org.openspcoop2.protocol.sdi.utils.SDICompatibilitaNamespaceErrati;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * This class was generated by Apache CXF 2.7.4
 * 2014-10-07T14:32:46.361+02:00
 * Generated source version: 2.7.4
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class RicezioneFatture_RicezioneFatturePort_Client {

    private static final QName SERVICE_NAME = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0", "RicezioneFatture_service");

    private RicezioneFatture_RicezioneFatturePort_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = RicezioneFattureService.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URI(args[0]).toURL();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
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
		
		
		String tipoOperazione = reader.getProperty("tipoOperazione");
		if(tipoOperazione==null){
			throw new Exception("Property [tipoOperazione] not definded");
		}
		else{
			tipoOperazione = tipoOperazione.trim();
		}
		
		String pdd = reader.getProperty("pdd");
		if(pdd==null){
			throw new Exception("Property [pdd] not definded");
		}
		else{
			pdd = pdd.trim();
			if(pdd.endsWith("/")==false){
				pdd = pdd + "/";
			}
		}
		
		String pa = reader.getProperty("pa");
		if(pa==null){
			throw new Exception("Property [pa] not definded");
		}
		else{
			pa = pa.trim();
		}
		
		String username = reader.getProperty("username");
		if(username!=null){
			username = username.trim();
		}
		String password = reader.getProperty("password");
		if(password!=null){
			password = password.trim();
		}
		
		String mtom = reader.getProperty("mtom");
		boolean isMTOMEnabled = false;
		if(mtom==null){
			throw new Exception("Property [mtom] not definded");
		}
		else{
			mtom = mtom.trim();
			isMTOMEnabled = Boolean.parseBoolean(mtom);
		}
        
		String bustaP = reader.getProperty("busta."+tipoOperazione);
		EGOVHeader egovHeader = null;
		if(bustaP!=null){
			java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat ("yyyy-MM-dd");
			java.text.SimpleDateFormat timeformat = new java.text.SimpleDateFormat ("HH:mm:ss.SSS");
			java.text.SimpleDateFormat dateidformat = new java.text.SimpleDateFormat ("yyyy-MM-dd");
			java.text.SimpleDateFormat timeidformat = new java.text.SimpleDateFormat ("HH:mm");
			String data = FileSystemUtilities.readFile(new File(bustaP));
			data = data.replaceAll("@ID-DATE@", dateidformat.format(new java.util.Date()));
			data = data.replaceAll("@ID-TIME@", timeidformat.format(new java.util.Date()));
			data = data.replaceAll("@DATE@", dateformat.format(new java.util.Date()));
			data = data.replaceAll("@TIME@", timeformat.format(new java.util.Date()));
			byte[]busta = data.getBytes();
			egovHeader = new EGOVHeader(busta);
		}
			
		
        
        RicezioneFattureService ss = new RicezioneFattureService(wsdlURL, RicezioneFatture_RicezioneFatturePort_Client.SERVICE_NAME);
        if(egovHeader!=null){
			ss.setHandlerResolver(new org.openspcoop2.example.pdd.client.sdi.ricezione_fatture.HandlerResolver(egovHeader));
		}
        RicezioneFatture port = ss.getRicezioneFatturePort();  
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pdd+pa);
        ((jakarta.xml.ws.soap.SOAPBinding)((BindingProvider)port).getBinding()).setMTOMEnabled(isMTOMEnabled);
        if(username!=null && password!=null){
            ((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,  username);
        	((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,  password);
        }
        
        if("notificaDecorrenzaTermini".equals(tipoOperazione)){
	       
    		String notificaDecorrenzaTermini = reader.getProperty("notificaDecorrenzaTermini");
    		if(notificaDecorrenzaTermini==null){
    			throw new Exception("Property [notificaDecorrenzaTermini] not definded");
    		}
    		else{
    			notificaDecorrenzaTermini = notificaDecorrenzaTermini.trim();
    		}
        	
    		byte [] xml = FileSystemUtilities.readBytesFromFile(notificaDecorrenzaTermini);
    		xml = SDICompatibilitaNamespaceErrati.convertiXmlNamespaceSenzaGov(LoggerWrapperFactory.getLogger(RicezioneFatture_RicezioneFatturePort_Client.class), xml);
    		it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer = new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer();
    		NotificaDecorrenzaTerminiType ntd = deserializer.readNotificaDecorrenzaTerminiType(xml);
    		
        	System.out.println("Invoking notificaDecorrenzaTermini...");
	        org.openspcoop2.example.pdd.client.sdi.ricezione_fatture.FileSdIType _notificaDecorrenzaTermini_parametersNotifica = new FileSdIType();
	        _notificaDecorrenzaTermini_parametersNotifica.setNomeFile((new File(notificaDecorrenzaTermini).getName()));
	        _notificaDecorrenzaTermini_parametersNotifica.setFile(FileSystemUtilities.readBytesFromFile(notificaDecorrenzaTermini));
	        _notificaDecorrenzaTermini_parametersNotifica.setIdentificativoSdI(new BigInteger(ntd.getIdentificativoSdI()+""));
	        port.notificaDecorrenzaTermini(_notificaDecorrenzaTermini_parametersNotifica);
	 
        }
        
        else if("riceviFatture".equals(tipoOperazione)){
        	
    		
    		String fattura = reader.getProperty("fattura");
    		if(fattura==null){
    			throw new Exception("Property [fattura] not definded");
    		}
    		else{
    			fattura = fattura.trim();
    		}
    		
    		String metadati = reader.getProperty("metadati");
    		if(metadati==null){
    			throw new Exception("Property [metadati] not definded");
    		}
    		else{
    			metadati = metadati.trim();
    		}

    		byte [] metadatiArray = FileSystemUtilities.readBytesFromFile(metadati);
    		
    		boolean fatturaB2B = false;
    		if(it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.XMLUtils.isNotificaB2B(metadatiArray)) {
    			fatturaB2B = true;
    		}
    		else if(it.gov.fatturapa.sdi.messaggi.v1_0.utils.XMLUtils.isNotificaPA(metadatiArray, true)) {
    			fatturaB2B = false;
    		}
    		
    		String identificativoSDI = null;
    		if(fatturaB2B) {
    			it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.serializer.JaxbDeserializer();
    			FileMetadatiType metadatiObject = deserializer.readFileMetadatiType(metadati);
    			identificativoSDI = metadatiObject.getIdentificativoSdI();
    		}
    		else {
    			it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer = new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer();
    			MetadatiInvioFileType metadatiObject = deserializer.readMetadatiInvioFileType(metadati);
    			identificativoSDI = metadatiObject.getIdentificativoSdI()+"";
    		}
        	
	        System.out.println("Invoking riceviFatture...");
	        org.openspcoop2.example.pdd.client.sdi.ricezione_fatture.FileSdIConMetadatiType _riceviFatture_parametersIn = new FileSdIConMetadatiType();
	        _riceviFatture_parametersIn.setIdentificativoSdI(new BigInteger(identificativoSDI));
	        _riceviFatture_parametersIn.setNomeFile((new File(fattura).getName()));
	        _riceviFatture_parametersIn.setFile(FileSystemUtilities.readBytesFromFile(fattura));
	        _riceviFatture_parametersIn.setNomeFileMetadati((new File(metadati).getName()));
	        _riceviFatture_parametersIn.setMetadati(metadatiArray);
	        	        
	        org.openspcoop2.example.pdd.client.sdi.ricezione_fatture.RispostaRiceviFattureType _riceviFatture__return = port.riceviFatture(_riceviFatture_parametersIn);
	        System.out.println("riceviFatture.result=" + _riceviFatture__return.getEsito().name());
	        
        }
        
        else{
        	throw new Exception("Property [tipoOperazione="+tipoOperazione+"] not supported");
        }

        System.exit(0);
    }

}
