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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.core.diagnostica.utils.XMLUtils;
import org.openspcoop2.message.ValidatoreXSD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.testng.Assert;
import org.testng.Reporter;

/**
 * Utilities per verifica log su file system
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FileSystemUtilities {

	public static void verificaOpenspcoopCore(Date dataAvvioGruppoTest, String logDirectory) throws Exception{
		verificaOpenspcoopCore(dataAvvioGruppoTest,logDirectory,new ErroreAttesoOpenSPCoopLogCore[0]);
	}

	public static void verificaOpenspcoopCore(Date dataAvvioGruppoTest, String logDirectory, ErroreAttesoOpenSPCoopLogCore ... erroriAttesi) throws Exception{
		try{
			Reporter.log("Controllo Errori su openspcoop2_core.log a partire da ("+dataAvvioGruppoTest.toString()+") ...");
			File logDir = new File(logDirectory);
			Assert.assertTrue(logDir.exists());
			Assert.assertTrue(logDir.canRead());
			File [] logs = logDir.listFiles();
			Assert.assertTrue(logs!=null);
			Assert.assertTrue(logs.length>0);
			StringBuffer erroriLogs = new StringBuffer();
			for(int i=0; i<logs.length; i++){
				File log = logs[i];
				File tmp = null;
				if(log.isFile() && log.getName().startsWith("openspcoop2_core.log")){
					Reporter.log("esamina ["+log.getAbsolutePath()+"] ...");
					String contenuto = org.openspcoop2.utils.resources.FileSystemUtilities.readFile(log);
					if(contenuto.contains("ERROR") || contenuto.contains("FATAL")){

						ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(log));
						BufferedReader bf = new BufferedReader(new InputStreamReader(bin));
						String line = "";
						while( (line=bf.readLine()) != null ){
							boolean errore = false;
							boolean erroreFatal = false;
							String livello = null;
							if(line.trim().startsWith("ERROR")){
								errore = true;
								livello = "ERROR";
							}
							else if(line.trim().startsWith("FATAL")){
								erroreFatal = true;
								livello = "FATAL";
							}
							if(errore || erroreFatal){
								line = line.substring(5);
								if(line.startsWith(" ")){
									line = line.substring(1);
									if(line.startsWith("<")){
										String data = line.substring(1,line.indexOf(">"));
										SimpleDateFormat dateformat = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
										Date dataLog = dateformat.parse(data);
										if(dataLog.after(dataAvvioGruppoTest)){

											// check che non sia un errore atteso
											boolean erroreAtteso = false;
											if(erroriAttesi!=null && erroriAttesi.length>0){
												for(int errorIndex=0; errorIndex<erroriAttesi.length; errorIndex++){
													if(line.contains(erroriAttesi[errorIndex].getMsgErrore()) && 
															(dataLog.getTime()>=erroriAttesi[errorIndex].getIntervalloInferiore().getTime()) &&
															(dataLog.getTime()<=erroriAttesi[errorIndex].getIntervalloSuperiore().getTime())
														){
														erroreAtteso = true;
														break;
													}
												}
											}
											if(erroreAtteso)
												continue;

											/*System.out.println("ERRORE ["+line+"] DATALOG["+dataLog.toString()+"]["+dataLog.getTime()+"]");
											for(int errorIndex=0; errorIndex<erroriAttesi.length; errorIndex++){
												System.out.println("("+errorIndex+")["+erroriAttesi[errorIndex].getMsgErrore()+"]["+erroriAttesi[errorIndex].getIntervalloInferiore().toString()+"]("+erroriAttesi[errorIndex].getIntervalloInferiore().getTime()+
														")-["+erroriAttesi[errorIndex].getIntervalloSuperiore().toString()+"]("+erroriAttesi[errorIndex].getIntervalloSuperiore().getTime()+")");
												System.out.println("("+errorIndex+") CONTAINS("+line.contains(erroriAttesi[errorIndex].getMsgErrore())+")");
												System.out.println("("+errorIndex+") INFERIORE a("+(dataLog.getTime()>=erroriAttesi[errorIndex].getIntervalloInferiore().getTime())+")");
												System.out.println("("+errorIndex+") INFERIORE b("+(dataLog.getTime()<=erroriAttesi[errorIndex].getIntervalloSuperiore().getTime())+")");
											}*/
														
											
											// Se non ho gia salvato il log di errore openspcoop2_core.log* lo effettuo
											if(tmp==null){
												if(errore)
													tmp = File.createTempFile("logCore_"+i+"_", ".error");
												else
													tmp = File.createTempFile("logCore_"+i+"_", ".fatal");
												org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(tmp, contenuto.getBytes());
											}

											// emetto segnalazione
											if(erroriLogs.length()>0)
												erroriLogs.append("\n");
											String err = "Livello di log '"+livello+"' trovato in ["+log.getAbsolutePath()+"], file salvato in "+tmp.getAbsolutePath()+" , msg di log: "+line;
											Reporter.log(err);
											erroriLogs.append(err);
										}
									}
									else{
										throw new Exception("LOG ["+log.getAbsolutePath()+"] in un formato diverso da quello atteso: LIVELLO <GDO> .... ");
									}
								}
								else{
									throw new Exception("LOG ["+log.getAbsolutePath()+"] in un formato diverso da quello atteso: LIVELLO <GDO> .... ");
								}
							}
						}


					}
				}
			}
			if(erroriLogs.length()>0){
				System.out.println("!!! ERRORE !!! -> Nonostante i test siano stati passati con successo, sono presenti degli errori non previsti in openspcoop2_core.log* successivi alla data di avvio del test: "+dataAvvioGruppoTest);
				System.out.println(erroriLogs.toString());
				throw new Exception("!!! ERRORE !!! -> Nonostante i test siano stati passati con successo, sono presenti degli errori non previsti in openspcoop2_core.log* successivi alla data di avvio del test: "+dataAvvioGruppoTest);
			}
			else{
				System.out.println("- Verifica openspcoop2_core.log attuata con successo.");
			}
		}
		catch(Exception e){
			if(e.getMessage().startsWith("!!! ERRORE !!!")==false)
				e.printStackTrace();
			throw e;
		}
	}










	public static void verificaMsgDiagnosticiXML(String logDirectory,ValidatoreXSD validatoreXSD) throws Exception{


		Reporter.log("Controllo Errori su openspcoop2_msgDiagnostico.log ...");
		File logDir = new File(logDirectory);
		Assert.assertTrue(logDir.exists());
		Assert.assertTrue(logDir.canRead());
		File [] logs = logDir.listFiles();
		Assert.assertTrue(logs!=null);
		Assert.assertTrue(logs.length>0);

		Reporter.log("LOG DIRECTORY["+logDirectory+"]: "+logs.length);
		ValidatoreXSD validatoreXSDBase = new ValidatoreXSD(LoggerWrapperFactory.getLogger(FileSystemUtilities.class),
				FileSystemUtilities.class.getResourceAsStream("/openspcoopDiagnostica.xsd"));
		
		for(int i=0; i<logs.length; i++){
			File log = logs[i];

			//Reporter.log("Trovato file ["+log.getName()+"] ...");
			
			if(log.isFile() && log.getName().startsWith("openspcoop2_msgDiagnostico.log")){

				Reporter.log("esamina ["+log.getAbsolutePath()+"] ...");
				boolean findLogs = false;

				ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(log));
				BufferedReader bf = new BufferedReader(new InputStreamReader(bin));
				StringBuffer msgDiagnostico = null;
				String line = bf.readLine();
				int count = 0;
				while( line != null ){

					msgDiagnostico = new StringBuffer();

					line = line.trim();

					if(!"".equals(line)){
						msgDiagnostico.append(line);
						findLogs = true;
						line=bf.readLine();
						if(line!=null){
							line = line.trim();
							while(!"".equals(line)){
								msgDiagnostico.append(line);
								line=bf.readLine();
								if(line!=null){
									line = line.trim();
								}else{
									break;
								}
							}
						}

						count++;
						
						//System.out.println("valido xml ["+msgDiagnostico.toString()+"] ...");
						byte[]diagnosticoXML = msgDiagnostico.toString().getBytes();
						ValidatoreXSD validatoreXSDDaUtilizzare = null;	
						boolean base = false;
						
						if(XMLUtils.isMessaggioDiagnostico(diagnosticoXML)){
							
							// diagnostico base della Porta di Dominio
							base = true;
							validatoreXSDDaUtilizzare = validatoreXSDBase;
							
						}
						else{
						
							// diagnostico dipendente dal protocollo
							validatoreXSDDaUtilizzare = validatoreXSD;
						
						}					
						
						try{
							validatoreXSDDaUtilizzare.valida(new ByteArrayInputStream(diagnosticoXML));
						}catch(Exception e){

							// Se non ho gia salvato il log di errore openspcoop2_core.log* lo effettuo
							File tmp = File.createTempFile("msgDiagnostico", ".log");
							org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(tmp, org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(log));

							// Salvo il msg diagnostico errato
							File tmpMsgDiag = File.createTempFile("msgDiagnostico", ".xml");
							org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(tmpMsgDiag, msgDiagnostico.toString().getBytes());

							// Segnalo errore
							Reporter.log("MsgDiagnostico trovato in ["+log.getAbsolutePath()+"] possiede xml malformati (OpenSPCoop2Base"+base+")");
							Reporter.log("file di log salvato in "+tmp.getAbsolutePath());
							Reporter.log("msg diagnostico malformato salvato in "+tmpMsgDiag.getAbsolutePath());
							Reporter.log("Errore: "+e.getMessage());
							throw e;
						}

					}
					else{

						line = bf.readLine();

					}

				}

				if(findLogs==false){
					throw new Exception("Files di log ["+log.getAbsolutePath()+"] non possiede msg diagnostici?");
				}
				else{
					Reporter.log("esamina ["+log.getAbsolutePath()+"] terminato. Analizzati "+count+" messaggi diagnostici");
				}

			}
		}


	}




	public static void verificaTracciaturaXML(String logDirectory,ValidatoreXSD validatoreXSD) throws Exception{


		Reporter.log("Controllo Errori su openspcoop2_tracciamento.log ...");
		File logDir = new File(logDirectory);
		Assert.assertTrue(logDir.exists());
		Assert.assertTrue(logDir.canRead());
		File [] logs = logDir.listFiles();
		Assert.assertTrue(logs!=null);
		Assert.assertTrue(logs.length>0);

		Reporter.log("LOG DIRECTORY["+logDirectory+"]: "+logs.length);
		ValidatoreXSD validatoreXSDBase = new ValidatoreXSD(LoggerWrapperFactory.getLogger(FileSystemUtilities.class),
				FileSystemUtilities.class.getResourceAsStream("/openspcoopTracciamento.xsd"));
		
		
		for(int i=0; i<logs.length; i++){
			File log = logs[i];

			//Reporter.log("Trovato file ["+log.getName()+"] ...");
			
			if(log.isFile() && log.getName().startsWith("openspcoop2_tracciamento.log")){

				Reporter.log("esamina ["+log.getAbsolutePath()+"] ...");
				boolean findLogs = false;

				ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(log));
				BufferedReader bf = new BufferedReader(new InputStreamReader(bin));
				StringBuffer tracciamento = null;
				String line = bf.readLine();
				int count = 0;
				while( line != null ){

					tracciamento = new StringBuffer();

					line = line.trim();

					if(!"".equals(line)){
						tracciamento.append(line);
						findLogs = true;
						line=bf.readLine();
						if(line!=null){
							line = line.trim();
							while(!"".equals(line)){
								tracciamento.append(line);
								line=bf.readLine();
								if(line!=null){
									line = line.trim();
								}else{
									break;
								}
							}
						}

						count++;
						
						//System.out.println("valido xml ["+tracciamento.toString()+"] ...");
						byte[]tracciaXML = tracciamento.toString().getBytes();
						ValidatoreXSD validatoreXSDDaUtilizzare = null;	
						boolean base = false;
						
						if(org.openspcoop2.core.tracciamento.utils.XMLUtils.isTraccia(tracciaXML)){
							
							// traccia base della Porta di Dominio
							base = true;
							validatoreXSDDaUtilizzare = validatoreXSDBase;
							
						}
						else{
						
							// traccia dipendente dal protocollo
							validatoreXSDDaUtilizzare = validatoreXSD;
						
						}	
						
						try{
							validatoreXSDDaUtilizzare.valida(new ByteArrayInputStream(tracciaXML));
						}catch(Exception e){

							// Se non ho gia salvato il log di errore openspcoop2_core.log* lo effettuo
							File tmp = File.createTempFile("tracciamento", ".log");
							org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(tmp, org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(log));

							// Salvo il msg diagnostico errato
							File tmpMsgDiag = File.createTempFile("tracciamento", ".xml");
							org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(tmpMsgDiag, tracciamento.toString().getBytes());

							// Segnalo errore
							Reporter.log("Traccia trovato in ["+log.getAbsolutePath()+"] possiede xml malformati (OpenSPCoop2Base"+base+")");
							Reporter.log("file di log salvato in "+tmp.getAbsolutePath());
							Reporter.log("traccia malformata salvato in "+tmpMsgDiag.getAbsolutePath());
							Reporter.log("Errore: "+e.getMessage());
							throw e;
						}

					}
					else{

						line = bf.readLine();

					}

				}

				if(findLogs==false){
					throw new Exception("Files di log ["+log.getAbsolutePath()+"] non possiede tracce?");
				}
				else{
					Reporter.log("esamina ["+log.getAbsolutePath()+"] terminato. Analizzate "+count+" tracce");
				}
			}
		}




	}
}
