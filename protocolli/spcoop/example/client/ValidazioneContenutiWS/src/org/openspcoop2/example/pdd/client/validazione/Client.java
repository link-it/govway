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


package org.openspcoop2.example.pdd.client.validazione;

import java.util.List;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

import javax.xml.rpc.Stub;


import org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoAsincronoWrappedDocumentLiteral;
import org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoAsincronoWrappedDocumentLiteralServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoUtentiWrappedDocumentLiteral;
import org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoUtentiWrappedDocumentLiteralServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.EsitoAggiornamentoAsincronoWrappedDocumentLiteral;
import org.openspcoop2.ValidazioneContenutiWS.Service.EsitoAggiornamentoAsincronoWrappedDocumentLiteralServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiDocumentLiteral;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiDocumentLiteralServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiOverloadedOperations;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiOverloadedOperationsServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiRPCEncoded;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiRPCEncodedServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiRPCLiteral;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiRPCLiteralServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiStileIbrido;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiStileIbridoServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteral;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteralServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoNominativo;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoUtenteWDLRequest;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoUtenteWDLResponse;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.EliminazioneUtenteWDLRequestType;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.EliminazioneUtenteWDLResponseType;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLRequest;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLRequest;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.NotificaAggiornamentoUtenteWDLRequest;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.RegistrazioneUtenteWDLRequestType;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.RegistrazioneUtenteWDLResponseType;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLRequest;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLResponse;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLRequest;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLResponse;

/**
 * Client
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Client {

	public static void main (String [] argv) {

		try {

			java.util.Properties reader = new java.util.Properties();
			try{  
				reader.load(new java.io.FileInputStream("Client.properties")); 
			}catch(java.io.IOException e) {
				System.err.println("ERROR : "+e.toString());
				return;
			}

			String url = reader.getProperty("urlPortaDiDominio");
			if(url == null){
				System.err.println("ERROR : URL del Servizio non definito all'interno del file 'Client.properties'");
				return;
			}
			url = url.trim();

			String portaDelegata = reader.getProperty("PD");
			if(portaDelegata == null){
				System.out.println("WARN : Porta Delegata non definita all'interno del file 'Client.properties'");
			}else{
				portaDelegata = portaDelegata.trim();
			}
			
			if(portaDelegata!=null){
				String prefisso = reader.getProperty("tipoPD");
				if(prefisso == null){
					System.out.println("WARN : Tipo di Validazione non definito all'interno del file 'Client.properties' (Verra utilizzato il default:wsdl)");
				}else{
					if("wsdl".equalsIgnoreCase(prefisso)){
						prefisso = "PDValWSDL";
						portaDelegata = portaDelegata.replace("@TIPO@","");
					}else if("openspcoop".equalsIgnoreCase(prefisso)){
						prefisso = "PDValOpenSPCoop";
						portaDelegata = portaDelegata.replace("@TIPO@","_ValidazioneOpenSPCoop");
					}else if("PD".equalsIgnoreCase(prefisso))
						prefisso = null;
					else {
						System.err.println("ERROR : Tipo di Validazione non corretto (valore ammessi: wsdl/openspcoop/PD )");
						return;
					}
						
					if(prefisso!=null)
						portaDelegata = prefisso + "/" + portaDelegata;
	
				}
			}

			if(url.endsWith("/")==false && portaDelegata!=null)
				url = url + "/";
			String SOAPUrl = url;
			if(portaDelegata!=null)
				SOAPUrl = url + portaDelegata;

			String comando =reader.getProperty("operazione");
			if(comando == null){
				System.err.println("ERROR : operazione non definito all'interno del file 'Client.properties'");
				return;
			}
			comando = comando.trim();

			if( ("GestioneUtentiWrappedDocumentLiteral_registrazione".equals(comando)==false) &&
					("GestioneUtentiWrappedDocumentLiteral_eliminazione".equals(comando)==false) &&
					("AggiornamentoUtentiWrappedDocumentLiteral_notifica".equals(comando)==false) &&
					("AggiornamentoUtentiWrappedDocumentLiteral_aggiornamento".equals(comando)==false) &&
					("AggiornamentoAsincronoWrappedDocumentLiteral_as".equals(comando)==false) &&
					("EsitoAggiornamentoAsincronoWrappedDocumentLiteral_as".equals(comando)==false) &&
					("AggiornamentoAsincronoWrappedDocumentLiteral_aa".equals(comando)==false) &&
					("EsitoAggiornamentoAsincronoWrappedDocumentLiteral_aa".equals(comando)==false) &&
					("GestioneUtentiDocumentLiteral_registrazione".equals(comando)==false) &&
					("GestioneUtentiDocumentLiteral_eliminazione".equals(comando)==false) &&
					("GestioneUtentiRPCLiteral_registrazione".equals(comando)==false) &&
					("GestioneUtentiRPCLiteral_eliminazione".equals(comando)==false) &&
					("GestioneUtentiRPCEncoded_registrazione".equals(comando)==false) &&
					("GestioneUtentiRPCEncoded_eliminazione".equals(comando)==false) &&
					("GestioneUtentiStileIbrido_WrappedDocumentLiteral".equals(comando)==false) &&
					("GestioneUtentiStileIbrido_RPCLiteral".equals(comando)==false) &&
					("GestioneUtentiStileIbrido_RPCEncoded".equals(comando)==false) &&
					("GestioneUtentiOverloadedOperations_Signature1".equals(comando)==false) &&
					("GestioneUtentiOverloadedOperations_Signature2".equals(comando)==false) &&
					("GestioneUtentiOverloadedOperations_Signature3".equals(comando)==false) &&
					("readFileXML".equals(comando)==false)){
				System.err.println("ERROR : operazione definito all'interno del file 'Client.properties' non conosciuta");
				return;
			}
			
			if( "GestioneUtentiWrappedDocumentLiteral_registrazione".equals(comando) ||
					"GestioneUtentiWrappedDocumentLiteral_eliminazione".equals(comando)){
				GestioneUtentiWrappedDocumentLiteralServiceLocator locator = new GestioneUtentiWrappedDocumentLiteralServiceLocator();
				locator.setGestioneUtentiWrappedDocumentLiteralEndpointAddress(SOAPUrl);
				GestioneUtentiWrappedDocumentLiteral port = locator.getGestioneUtentiWrappedDocumentLiteral();
				
				if("GestioneUtentiWrappedDocumentLiteral_registrazione".equals(comando)){
					RegistrazioneUtenteWDLRequestType wrappedRequest = new RegistrazioneUtenteWDLRequestType();
					wrappedRequest.setNominativo("Mario Rossi");
					wrappedRequest.setIndirizzo("viale Roma 26");
					wrappedRequest.setOraRegistrazione(new java.util.Date());
					
					RegistrazioneUtenteWDLResponseType esito = port.registrazioneUtenteWDL(wrappedRequest);
					System.out.println("Registrazione di GestioneUtentiWrappedDocumentLiteral inviata con esito: "+esito.getEsito());
				}else{
					EliminazioneUtenteWDLRequestType wrappedRequest = new EliminazioneUtenteWDLRequestType();
					wrappedRequest.setNominativo("Mario Rossi");
					
					EliminazioneUtenteWDLResponseType esito = port.eliminazioneUtenteWDL(wrappedRequest);
					System.out.println("Eliminazione di GestioneUtentiWrappedDocumentLiteral effettuata con esito: "+esito.getEsito());
				}
			}
			else if( "AggiornamentoUtentiWrappedDocumentLiteral_notifica".equals(comando) ||
					"AggiornamentoUtentiWrappedDocumentLiteral_aggiornamento".equals(comando)){
				
				AggiornamentoUtentiWrappedDocumentLiteralServiceLocator locator = new AggiornamentoUtentiWrappedDocumentLiteralServiceLocator();
				locator.setAggiornamentoUtentiWrappedDocumentLiteralEndpointAddress(SOAPUrl);
				AggiornamentoUtentiWrappedDocumentLiteral port = locator.getAggiornamentoUtentiWrappedDocumentLiteral();
				
				AggiornamentoNominativo nominativo = new AggiornamentoNominativo();
				nominativo.setNomePrecedente("Mario Rossi");
				nominativo.set_value("Mario Verdi");
				
				if("AggiornamentoUtentiWrappedDocumentLiteral_notifica".equals(comando)){
					NotificaAggiornamentoUtenteWDLRequest wrappedRequest = new NotificaAggiornamentoUtenteWDLRequest();
					wrappedRequest.setAggiornamentoNominativo(nominativo);
					wrappedRequest.setIndirizzo("viale Roma 26");
					
					port.notificaAggiornamentoUtenteWDL(wrappedRequest);
					System.out.println("Notifica di AggiornamentoUtentiWrappedDocumentLiteral inviata");
				}else{
					AggiornamentoUtenteWDLRequest wrappedRequest = new AggiornamentoUtenteWDLRequest();
					wrappedRequest.setAggiornamentoNominativo(nominativo);
					wrappedRequest.setIndirizzo("viale Roma 26");
					
					AggiornamentoUtenteWDLResponse esito = port.aggiornamentoUtenteWDL(wrappedRequest);
					String data = "";
					if(esito.getOraRegistrazione()!=null)
						data=" ["+esito.getOraRegistrazione().toString()+"] ";
					System.out.println("Aggiornamento di AggiornamentoUtentiWrappedDocumentLiteral effettuata ["+data+"] con esito: "+esito.getEsito());
				}
			}
			else if( "AggiornamentoAsincronoWrappedDocumentLiteral_as".equals(comando) || "AggiornamentoAsincronoWrappedDocumentLiteral_aa".equals(comando)){
				
				AggiornamentoAsincronoWrappedDocumentLiteralServiceLocator locator = 
					new AggiornamentoAsincronoWrappedDocumentLiteralServiceLocator();
				locator.setAggiornamentoAsincronoWrappedDocumentLiteralEndpointAddress(SOAPUrl);
				AggiornamentoAsincronoWrappedDocumentLiteral port = locator.getAggiornamentoAsincronoWrappedDocumentLiteral();
				
				
				
				String username = null;
				String password = null;
				if(reader.getProperty("username")!=null){
                	username = reader.getProperty("username").trim();
                }
                if(reader.getProperty("password")!=null){
                	password = reader.getProperty("password").trim();
                }
				 if(username !=null && password!=null){
                     // to use Basic HTTP Authentication: 
					 System.out.println("SET AUTENTICAZIONE ["+username+"] ["+password+"]");
                     ((Stub) port)._setProperty(javax.xml.rpc.Call.USERNAME_PROPERTY, username);
                     ((Stub) port)._setProperty(javax.xml.rpc.Call.PASSWORD_PROPERTY, password);
				 }

				 
					if( "AggiornamentoAsincronoWrappedDocumentLiteral_as".equals(comando) ){
						RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLRequest wrappedRequest = new RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLRequest();
						wrappedRequest.setNominativo("Mario Rossi");
						wrappedRequest.setIndirizzo("viale Roma 26");
						wrappedRequest.setOraRegistrazione(new java.util.Date());
						RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLResponse esito = port.richiestaAggiornamentoUtenteAsincronoSimmetricoWDL(wrappedRequest);
						System.out.println("Registrazione di AggiornamentoAsincronoSimmetricoWrappedDocumentLiteral inviata con esito-ack: "+esito.getAckRichiestaAsincrona());
					}else if( "AggiornamentoAsincronoWrappedDocumentLiteral_aa".equals(comando) ){
						RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLRequest wrappedRequest = new RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLRequest();
						wrappedRequest.setNominativo("Mario Rossi");
						wrappedRequest.setIndirizzo("viale Roma 26");
						wrappedRequest.setOraRegistrazione(new java.util.Date());
						RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLResponse esito = port.richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL(wrappedRequest);
						System.out.println("Registrazione di AggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral inviata con esito-ack: "+esito.getAckRichiestaAsincrona());
					}
				
			}
			else if( "EsitoAggiornamentoAsincronoWrappedDocumentLiteral_as".equals(comando) || "EsitoAggiornamentoAsincronoWrappedDocumentLiteral_aa".equals(comando)){
				
				EsitoAggiornamentoAsincronoWrappedDocumentLiteralServiceLocator locator = 
					new EsitoAggiornamentoAsincronoWrappedDocumentLiteralServiceLocator();
				locator.setEsitoAggiornamentoAsincronoWrappedDocumentLiteralEndpointAddress(SOAPUrl);
				EsitoAggiornamentoAsincronoWrappedDocumentLiteral port = locator.getEsitoAggiornamentoAsincronoWrappedDocumentLiteral();
				
				
								
				if( "EsitoAggiornamentoAsincronoWrappedDocumentLiteral_as".equals(comando) ){
					EsitoAggiornamentoUtenteAsincronoSimmetricoWDLRequest request = new EsitoAggiornamentoUtenteAsincronoSimmetricoWDLRequest();
					request.setEsito("OK");
					EsitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse esito = port.esitoAggiornamentoUtenteAsincronoSimmetricoWDL(request);
					System.out.println("Esito di EsitoAggiornamentoAsincronoSimmetricoWrappedDocumentLiteral inviata con esito-ack: "+esito.getAckRichiestaAsincrona());
				}else if( "EsitoAggiornamentoAsincronoWrappedDocumentLiteral_aa".equals(comando) ){
					EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLRequest request = new EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLRequest();
					request.setEsito("OK");
					EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse esito = port.esitoAggiornamentoUtenteAsincronoAsimmetricoWDL(request);
					System.out.println("Esito di EsitoAggiornamentoAsincronoAsimmetricoWrappedDocumentLiteral inviata con esito-ack: "+esito.getAckRichiestaAsincrona());
				}
				
			}
			else if( "GestioneUtentiDocumentLiteral_registrazione".equals(comando) ||
					"GestioneUtentiDocumentLiteral_eliminazione".equals(comando)){
				
				GestioneUtentiDocumentLiteralServiceLocator locator = new GestioneUtentiDocumentLiteralServiceLocator();
				locator.setGestioneUtentiDocumentLiteralEndpointAddress(SOAPUrl);
				GestioneUtentiDocumentLiteral port = locator.getGestioneUtentiDocumentLiteral();
				
				if("GestioneUtentiDocumentLiteral_registrazione".equals(comando)){
					String esito = port.registrazioneUtenteDL("Mario Rossi", "viale Roma 26", new java.util.Date());
					System.out.println("Registrazione di GestioneUtentiDocumentLiteral inviata con esito: "+esito);
				}else{
					String esito = port.eliminazioneUtenteDL("Mario Rossi");
					System.out.println("Eliminazione di GestioneUtentiDocumentLiteral effettuata con esito: "+esito);
				}
			}
			else if( "GestioneUtentiRPCLiteral_registrazione".equals(comando) ||
					"GestioneUtentiRPCLiteral_eliminazione".equals(comando)){
				
				GestioneUtentiRPCLiteralServiceLocator locator = new GestioneUtentiRPCLiteralServiceLocator();
				locator.setGestioneUtentiRPCLiteralEndpointAddress(SOAPUrl);
				GestioneUtentiRPCLiteral port = locator.getGestioneUtentiRPCLiteral();
				
				if("GestioneUtentiRPCLiteral_registrazione".equals(comando)){
					String esito = port.registrazioneUtenteRPCL("Mario Rossi", "viale Roma 26", new java.util.Date());
					System.out.println("Registrazione di GestioneUtentiRPCLiteral inviata con esito: "+esito);
				}else{
					String esito = port.eliminazioneUtenteRPCL("Mario Rossi");
					System.out.println("Eliminazione di GestioneUtentiRPCLiteral effettuata con esito: "+esito);
				}
			}
			else if( "GestioneUtentiRPCEncoded_registrazione".equals(comando) ||
					"GestioneUtentiRPCEncoded_eliminazione".equals(comando)){
				
				GestioneUtentiRPCEncodedServiceLocator locator = new GestioneUtentiRPCEncodedServiceLocator();
				locator.setGestioneUtentiRPCEncodedEndpointAddress(SOAPUrl);
				GestioneUtentiRPCEncoded port = locator.getGestioneUtentiRPCEncoded();
				
				if("GestioneUtentiRPCEncoded_registrazione".equals(comando)){
					String esito = port.registrazioneUtenteRPCE("Mario Rossi", "viale Roma 26", new java.util.Date());
					System.out.println("Registrazione di GestioneUtentiRPCEncoded inviata con esito: "+esito);
				}else{
					String esito = port.eliminazioneUtenteRPCE("Mario Rossi");
					System.out.println("Eliminazione di GestioneUtentiRPCEncoded effettuata con esito: "+esito);
				}
			}
			else if( "GestioneUtentiStileIbrido_WrappedDocumentLiteral".equals(comando) ||
					"GestioneUtentiStileIbrido_RPCLiteral".equals(comando)||
					"GestioneUtentiStileIbrido_RPCEncoded".equals(comando)){
				
				GestioneUtentiStileIbridoServiceLocator locator = new GestioneUtentiStileIbridoServiceLocator();
				locator.setGestioneUtentiStileIbridoEndpointAddress(SOAPUrl);
				GestioneUtentiStileIbrido port = locator.getGestioneUtentiStileIbrido();
				
				if("GestioneUtentiStileIbrido_WrappedDocumentLiteral".equals(comando)){
					RegistrazioneUtenteWDLRequestType wrappedRequest = new RegistrazioneUtenteWDLRequestType();
					wrappedRequest.setNominativo("Mario Rossi");
					wrappedRequest.setIndirizzo("viale Roma 26");
					wrappedRequest.setOraRegistrazione(new java.util.Date());
					
					RegistrazioneUtenteWDLResponseType esito = port.registrazioneUtenteWDL(wrappedRequest);
					System.out.println("Registrazione di GestioneUtentiStileIbrido (WrappedDocumentLiteral) inviata con esito: "+esito.getEsito());
				}
				else if("GestioneUtentiStileIbrido_RPCLiteral".equals(comando)){
						String esito = port.registrazioneUtenteRPCL("Mario Rossi", "viale Roma 26", new java.util.Date());
						System.out.println("Registrazione di GestioneUtentiStileIbrido (RPCLiteral) inviata con esito: "+esito);
				}
				else if("GestioneUtentiStileIbrido_RPCEncoded".equals(comando)){
					String esito = port.registrazioneUtenteRPCE("Mario Rossi", "viale Roma 26", new java.util.Date());
					System.out.println("Registrazione di GestioneUtentiStileIbrido (RPCEncoded) inviata con esito: "+esito);
				}
			}
			else if( "GestioneUtentiOverloadedOperations_Signature1".equals(comando) ||
					"GestioneUtentiOverloadedOperations_Signature2".equals(comando)||
					"GestioneUtentiOverloadedOperations_Signature3".equals(comando)){
				
				GestioneUtentiOverloadedOperationsServiceLocator locator = new GestioneUtentiOverloadedOperationsServiceLocator();
				locator.setGestioneUtentiOverloadedOperationsEndpointAddress(SOAPUrl);
				GestioneUtentiOverloadedOperations port = locator.getGestioneUtentiOverloadedOperations();
				
				if("GestioneUtentiOverloadedOperations_Signature1".equals(comando)){
					String esito = port.registrazioneUtenteOverloadedOperations("Mario Rossi", "viale Roma 26", new java.util.Date());
					System.out.println("Registrazione di GestioneUtentiOverloadedOperations_Signature1 inviata con esito: "+esito);
				}
				else if("GestioneUtentiOverloadedOperations_Signature2".equals(comando)){
					String esito = port.registrazioneUtenteOverloadedOperations("Mario Rossi", "viale Roma 26");
					System.out.println("Registrazione di GestioneUtentiOverloadedOperations_Signature2 inviata con esito: "+esito);
				}
				else if("GestioneUtentiOverloadedOperations_Signature3".equals(comando)){
					String esito = port.registrazioneUtenteOverloadedOperations("Mario Rossi", new java.util.Date());
					System.out.println("Registrazione di GestioneUtentiOverloadedOperations_Signature3 inviata con esito: "+esito);
				}
			}
			else if("readFileXML".equals(comando)){
				
				String xmlFile2Send = argv[0];
	            if(reader.getProperty("file")!=null){
	                    xmlFile2Send = reader.getProperty("file").trim();
	            }
	            
	            FileInputStream fin = new FileInputStream(xmlFile2Send);
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                copy(fin,bout);
                fin.close();
                byte[] b = bout.toByteArray();
                System.out.println("Richiesta inviata:\n"+new String(b));
                
                // Create the connection where we're going to send the file.
                URL urlC = new URL(SOAPUrl);
                URLConnection connection = urlC.openConnection();
                HttpURLConnection httpConn = (HttpURLConnection) connection;

                // Set the appropriate HTTP parameters.
                httpConn.setRequestProperty( "Content-Length",
                                String.valueOf( b.length ) );
                httpConn.setRequestProperty("Content-Type","text/xml; charset=utf-8");
                
                String soapAction = "test";
                if(reader.getProperty("soapAction")!=null){
                	soapAction = reader.getProperty("soapAction").trim();
                }
                httpConn.setRequestProperty("SOAPAction","\""+soapAction+"\"");
                
                String riferimentoMsg = null;
                if(reader.getProperty("riferimentoMessaggio")!=null){
                	riferimentoMsg = reader.getProperty("riferimentoMessaggio").trim();
                	System.out.println("ADD RIFMSG["+riferimentoMsg+"]");
                	httpConn.setRequestProperty("X-SPCoop-RiferimentoMessaggio",riferimentoMsg);
                }
                
                httpConn.setRequestMethod( "POST" );
                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);

                // Everything's set up; send the XML that was read in to b.
                OutputStream out = httpConn.getOutputStream();
                out.write( b );
                out.close();

                System.out.println("\nMessaggio SOAP inviato!\n");

                // STATO INVOCAZIONE
                int resultHTTPOperation = httpConn.getResponseCode();
                System.out.println("Stato Invocazione ["+resultHTTPOperation+"]");
                Map<String,List<String>> headerResponse =  httpConn.getHeaderFields();
                Set<String> keys = headerResponse.keySet();
                java.util.Iterator<String> it = keys.iterator();
                while(it.hasNext()){
                        String key = it.next();
                        if(key!=null && (key.startsWith("SPCoop") || key.startsWith("ID") || key.equals("ServizioApplicativo")) )
                                System.out.println(key+" : "+headerResponse.get(key));
                }

                // Read the response and write it to standard out.
                InputStreamReader isr = null;
                if(resultHTTPOperation<300)
                        isr = new InputStreamReader(httpConn.getInputStream());
                else
                        isr = new InputStreamReader(httpConn.getErrorStream());
                BufferedReader in = new BufferedReader(isr);
                System.out.println("--------------------------------------------");
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                        System.out.println(inputLine);

                in.close();
                
                httpConn.disconnect();
			}

		}
		catch (Exception e) {
			System.out.println("ClientError: "+e.getMessage());
			//e.printStackTrace();
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
	
}
