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
 * NonShortCircuitLogicCheck
 *
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class NonShortCircuitLogicCheck {

	public static final String OR_CHECK = " | ";
	public static final String AND_CHECK = " & ";

	public static java.util.List<String> fileNonValidi = new java.util.ArrayList<>();

	public static String versione_rilascio = null;

	// codice di uscita:
	// -1 invocazione non valida
	// 1 Errore generale
	// 2 file non validi per dichiarazione GPL
	public static void main(String[] args) {
		try {

			if(args.length < 1){
				System.out.println("Error usage: java NonShortCircuitLogicCheck directory");
				System.exit(-1);
			}

			String dir = args[0];
			
			check(new File(dir));

			java.util.List<String> filePrinted = new java.util.ArrayList<>();	    
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
				boolean orCheck = true;
				boolean andCheck = true;
				// gestione eccezioni:
				
				
				if( f.getAbsolutePath().contains("distrib/check/NonShortCircuitLogicCheck.java") )
					jumpCheck = true;
				
				else if( f.getAbsolutePath().indexOf("core/src/org/openspcoop2/pdd/core/trasformazioni/test/TrasformazioniTest.java")!= -1 )
					orCheck = false;

				else if( f.getAbsolutePath().indexOf("core/src/org/openspcoop2/protocol/basic/Costanti.java")!= -1 )
					orCheck = false;

				else if( f.getAbsolutePath().indexOf("core/src/org/openspcoop2/protocol/engine/driver/repository/GestoreRepositoryBytewise.java")!= -1 )
					jumpCheck = true;
				
				else if( f.getAbsolutePath().indexOf("core/src/org/openspcoop2/message/OpenSPCoop2Message.java")!= -1 )
					orCheck = false;

				else if( f.getAbsolutePath().indexOf("testsuite/src/org/openspcoop2/testsuite/axis14/Axis14WSDoAllReceiverNoActionOrderCheck.java")!= -1 )
					jumpCheck = true;

				else if( f.getAbsolutePath().indexOf("tools/utils/src/org/openspcoop2/utils/crypt/MD5Crypt.java")!= -1 )
					jumpCheck = true;

				else if( f.getAbsolutePath().indexOf("tools/utils/src/org/openspcoop2/utils/instrument/InstrumentationUtils.java")!= -1 )
					andCheck = false;

				else if( f.getAbsolutePath().indexOf("tools/utils/src/org/openspcoop2/utils/regexp/RegularExpressionEngine.java")!= -1 )
					orCheck = false;

				else if( f.getAbsolutePath().indexOf("tools/utils/src/org/openspcoop2/utils/xml/test/TestXXE.java")!= -1 )
					orCheck = false;

				else if( f.getAbsolutePath().indexOf("tools/utils/src/org/openspcoop2/utils/xml/test/TestBugEntityReferences.java")!= -1 )
					jumpCheck = true;

				else if( f.getAbsolutePath().indexOf("tools/utils/src/org/openspcoop2/utils/sql/test/ClientTest.java")!= -1 )
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
					if(orCheck && byteInputBuffer.toString().indexOf(OR_CHECK)>0){
						fileNonValidi.add(f.getAbsolutePath());
					}
					else if(andCheck && byteInputBuffer.toString().indexOf(AND_CHECK)>0){
						if(!byteInputBuffer.toString().contains(" & 0xFF")){
							fileNonValidi.add(f.getAbsolutePath());
						}
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
