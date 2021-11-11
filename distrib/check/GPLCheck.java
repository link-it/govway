/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.io.*;

/**
 * GPLCheck
 *
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GPLCheck {

	public static final String[] GPL_CHECK = {"GovWay - A customizable API Gateway",
			"https://govway.org",
			"Copyright (c) 2005-2021 Link.it srl (https://link.it).",
			"This program is free software: you can redistribute it and/or modify",
			"it under the terms of the GNU General Public License version 3, as published by",
			"the Free Software Foundation.",
			"This program is distributed in the hope that it will be useful,",
			"but WITHOUT ANY WARRANTY; without even the implied warranty of",
			"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the",
			"GNU General Public License for more details.",
			"You should have received a copy of the GNU General Public License",
	"along with this program.  If not, see <http://www.gnu.org/licenses/>."};


	public static java.util.List<String> fileNonValidi = new java.util.ArrayList<String>();
	public static java.util.List<String> dichiarazioneAssente = new java.util.ArrayList<String>();

	// codice di uscita:
	// -1 invocazione non valida
	// 1 Errore generale
	// 2 file non validi per dichiarazione GPL
	public static void main(String[] args) {
		try {

			if(args.length < 1){
				System.out.println("Error usage: java GPLWriter directory");
				System.exit(-1);
			}

			String dir = args[0];

			checkGPLDichiarazione(new File(dir));

			if(fileNonValidi.size()>0){
				for(int i=0; i<fileNonValidi.size(); i++){
					System.out.println("\nIl file "+fileNonValidi.get(i)+" non possiede una dichirazione GPL con copyright LinkIT: \n"+dichiarazioneAssente.get(i)+"\n");
				}
				System.exit(2);
			}

		} catch(Exception ex) {
			System.err.println("Errore generale: " + ex);
			System.exit(1);
		}

	}

	@SuppressWarnings("unused")
	private static boolean printTODO = false;

	public static void checkGPLDichiarazione(File f) {
		try {
			if(f.isFile()){
				//System.out.println("FILE");
				if(f.getName().endsWith(".java") || 
						f.getName().endsWith(".html") ||
						f.getName().endsWith(".htm") ||
						f.getName().endsWith(".jsp") 
						){

					// Get Bytes Originali
					FileInputStream fis =new FileInputStream(f);
					ByteArrayOutputStream byteInputBuffer = new ByteArrayOutputStream();
					byte [] readB = new byte[8192];
					int readByte = 0;
					while((readByte = fis.read(readB))!= -1){
						byteInputBuffer.write(readB,0,readByte);
					}
					fis.close();

					String TODO = "METTERE QUA EVENTUALE NUOVO PATH";
					//if(!printTODO){		
					//	System.out.println("TODO: Eliminare controllo per NUOVO PATH");
					//	printTODO = true;		
					//}

					// check 
					// gestione eccezioni.
					if( !f.getAbsolutePath().endsWith("tools/web_interfaces/lib/jsplib/closeWin.jsp") &&
							!f.getAbsolutePath().endsWith("core/deploy/deploy_web/web_app/index.jsp") &&
							!f.getAbsolutePath().endsWith("services/gestore_eventi/deploy/ws/index.jsp") &&
							!f.getAbsolutePath().endsWith("tools/ws/management/deploy/axis/index.jsp") &&
							!f.getAbsolutePath().endsWith("tools/ws/monitor/deploy/axis/index.jsp") &&
							!f.getAbsolutePath().endsWith("tools/ws/registry_search/deploy/axis/index.jsp") &&
							!f.getAbsolutePath().endsWith("tools/ws/tracce_search/deploy/axis/index.jsp") &&
							!f.getAbsolutePath().endsWith("tools/web_interfaces/operations/deploy/jsp/search.jsp") &&
							!f.getAbsolutePath().endsWith("tools/web_interfaces/operations/deploy/jsp/confermaRimozione.jsp") &&
							!f.getAbsolutePath().endsWith("tools/web_interfaces/operations/deploy/jsp/op.jsp") &&
							!f.getAbsolutePath().endsWith("tools/web_interfaces/operations/deploy/jsp/showOp.jsp") &&
							!f.getAbsolutePath().endsWith("tools/web_interfaces/control_station/deploy/index.html") &&
							!f.getAbsolutePath().endsWith("tools/web_interfaces/monitor/deploy/web-content/timeout.jsp") &&
							!f.getAbsolutePath().endsWith("tools/web_interfaces/monitor/deploy/web-content/index.jsp") &&
							!f.getAbsolutePath().endsWith("example/pdd/client/PD_Invoker/fatturaPA/esempi_altro/Fatturazione elettronica PA - Documentazione Sistema di Interscambio.htm") &&
							!f.getAbsolutePath().endsWith("tools/utils/src/org/openspcoop2/utils/Costanti.java")  &&
							!f.getAbsolutePath().contains("distrib/check/")  &&
							!f.getAbsolutePath().contains(TODO)
							){

						for(int i=0; i<GPL_CHECK.length; i++){

							int indexFound = byteInputBuffer.toString().indexOf(GPL_CHECK[i]);
							if(indexFound==-1){

								fileNonValidi.add(f.getAbsolutePath());
								dichiarazioneAssente.add(GPL_CHECK[i]);		
								break;
							}
							if("https://govway.org".equals(GPL_CHECK[i])==false){
								if(byteInputBuffer.toString().indexOf(GPL_CHECK[i],(indexFound+GPL_CHECK[i].length())) != -1){
									if(f.getAbsolutePath().endsWith("tools/utils/src/org/openspcoop2/utils/Costanti.java")==false){
										fileNonValidi.add(f.getAbsolutePath());
										dichiarazioneAssente.add("doppia presenza della dichiarazione ["+GPL_CHECK[i]+"]");		
										break;
									}
								}
							}

						}
					}
				}   
			}else{
				//System.out.println("DIR");
				File [] fChilds = f.listFiles();
				if(fChilds!=null){
					for (int i = 0; i < fChilds.length; i++) {
						checkGPLDichiarazione(fChilds[i]);
					}
				}
			}

		}
		catch(Exception ex) {
			System.out.println("Errore writeGPLDichiarazione: " + ex);
			return;
		}

	}
}
