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

import java.io.*;

/**
 * JavaDocCheck
 *
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class VectorCheck {

	public static final String VECTOR_CHECK = "Vector";

	public static java.util.List<String> fileNonValidi = new java.util.ArrayList<String>();

	public static String versione_rilascio = null;

	// codice di uscita:
	// -1 invocazione non valida
	// 1 Errore generale
	// 2 file non validi per dichiarazione GPL
	public static void main(String[] args) {
		try {

			if(args.length < 1){
				System.out.println("Error usage: java VectorCheck directory");
				System.exit(-1);
			}

			String dir = args[0];
			
			check(new File(dir));

			java.util.List<String> filePrinted = new java.util.ArrayList<String>();	    
			if(fileNonValidi.size()>0){
				for(int i=0; i<fileNonValidi.size(); i++){
					String file = fileNonValidi.get(i);
					if(filePrinted.contains(file)==false){
						System.out.println(file);
						filePrinted.add(file);
					}
				}
				System.exit(2);
			}

		} catch(Exception ex) {
			System.err.println("Errore generale: " + ex);
			ex.printStackTrace(System.out);
			System.exit(1);
		}

	}

	@SuppressWarnings("unused")
	private static boolean printTODO = false;

	public static void check(File f) {
		try {
			if(f.isFile()){
				//System.out.println("FILE");


				boolean jumpCheck = false;
				// gestione eccezioni:
				
				
				if( f.getAbsolutePath().contains("distrib/check/VectorCheck.java") )
					jumpCheck = true;

				else if( f.getAbsolutePath().contains("core/src/org/openspcoop2/core/registry/driver/uddi/UDDILib.java") )
					jumpCheck = true;

				else if( f.getAbsolutePath().contains("tools/web_interfaces/loader/src/org/openspcoop2/web/loader/servlet") && f.getAbsolutePath().endsWith(".java") )
					jumpCheck = true;

				else if( f.getAbsolutePath().contains("tools/web_interfaces/lib/audit/src/org/openspcoop2/web/lib/audit/web/AuditHelper.java") )
					jumpCheck = true;

				else if( f.getAbsolutePath().contains("tools/web_interfaces/lib/mvc/src/org/openspcoop2/web/lib/mvc") )
					jumpCheck = true;

				else if( f.getAbsolutePath().contains("tools/web_interfaces/control_station/src/org/openspcoop2/web/ctrlstat/plugins") )
					jumpCheck = true;
				else if( f.getAbsolutePath().contains("tools/web_interfaces/control_station/src/org/openspcoop2/web/ctrlstat/servlet") )
					jumpCheck = true;
				else if( f.getAbsolutePath().contains("tools/web_interfaces/control_station/ClassiDaReInserireDopoWS") )
					jumpCheck = true;
				else if( f.getAbsolutePath().contains("tools/web_interfaces/control_station/ServletUtils.java") )
					jumpCheck = true;

				else if( f.getAbsolutePath().contains("tools/utils/src/org/openspcoop2/utils/beans/BaseBean.java") )
					jumpCheck = true;

				else if( f.getAbsolutePath().contains("testsuite/src/org/openspcoop2/testsuite") )
					jumpCheck = true;
				else if( f.getAbsolutePath().contains("protocolli/trasparente/testsuite/src/org/openspcoop2/protocol/trasparente/testsuite") )
					jumpCheck = true;
				else if( f.getAbsolutePath().contains("protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite") )
					jumpCheck = true;
				else if( f.getAbsolutePath().contains("protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/trasparente/testsuite") )
					jumpCheck = true;
				else if( f.getAbsolutePath().contains("protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite") )
					jumpCheck = true;
				else if( f.getAbsolutePath().contains("protocolli/spcoop/testsuite/example/registroServizi/wsdl/src/org/openspcoop2/core/registry/wsdl/testsuite") )
					jumpCheck = true;
				else if( f.getAbsolutePath().contains("protocolli/spcoop/testsuite/example/server/ValidazioneContenutiWS/src/org/openspcoop2/ValidazioneContenutiWS") )
					jumpCheck = true;
				else if( f.getAbsolutePath().contains("protocolli/spcoop/example/registroServizi/wsdl/src/org/openspcoop2/core/registry/wsdl/testsuite") )
					jumpCheck = true;
				else if( f.getAbsolutePath().contains("protocolli/spcoop/example/server/ValidazioneContenutiWS/src/org/openspcoop2/ValidazioneContenutiWS") )
					jumpCheck = true;
				

				String TODO = "METTERE QUA EVENTUALE NUOVO PATH";
				//if(!printTODO){		
				//	System.out.println("TODO: Eliminare controllo per RS Monitor Server");
				//	printTODO = true;		
				//}
				if(!jumpCheck && f.getAbsolutePath().indexOf(TODO)!=-1){
					jumpCheck = true;
				}

				// Check
				if(f.getName().endsWith(".java") && (jumpCheck==false) ){


					//System.out.println("check ["+"]");
					// Get Bytes Originali
					FileInputStream fis =new FileInputStream(f);
					ByteArrayOutputStream byteInputBuffer = new ByteArrayOutputStream();
					byte [] readB = new byte[8192];
					int readByte = 0;
					while((readByte = fis.read(readB))!= -1){
						byteInputBuffer.write(readB,0,readByte);
					}
					fis.close();



					// check 
					if(byteInputBuffer.toString().indexOf(VECTOR_CHECK)>0){
						fileNonValidi.add(f.getAbsolutePath());
					}
					

				}
 
			}else{
				//System.out.println("DIR");
				File [] fChilds = f.listFiles();
				if(fChilds!=null){
					for (int i = 0; i < fChilds.length; i++) {
						check(fChilds[i]);
					}
				}
			}
		}catch(Exception ex) {
			System.out.println("Errore CHECK: " + ex);
			ex.printStackTrace(System.out);
			return;
		}

	}
}
