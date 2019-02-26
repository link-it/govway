/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
public class JavaDocCheck {
    
    public static final String AUTORE_CHECK = "@author";
    public static final String AUTORE_CHECK2 = "$Author";
    //public static final String AUTORE_VALORE = "$Author$";
    public static final String AUTORE_VALORE2 = "Author: $";
    public static final String AUTORE_VALORE3 = "Author:$";

    public static final String VERSIONE_CHECK = "@version ";
    public static final String VERSIONE_CHECK2 = "$Rev";
    public static final String VERSIONE_CHECK_HTML = "$Rev";
    public static final String VERSIONE_VALORE = "$Rev$";
    public static final String VERSIONE_VALORE2 = "Rev: $";
    public static final String VERSIONE_VALORE3 = "Rev:$";

    public static final String DATA_CHECK = "$Date";
    public static final String DATA_VALORE = "$Date$";
    public static final String DATA_VALORE2 = "Date: $";
    public static final String DATA_VALORE3 = "Date:$";

    public static final String VALORE_CORROTTO = ".java $";

    public static java.util.Vector<String> fileNonValidi = new java.util.Vector<String>();
    public static java.util.Vector<String> dichiarazioneAssente = new java.util.Vector<String>();
   
   public static String versione_rilascio = null;
 
    // codice di uscita:
    // -1 invocazione non valida
    // 1 Errore generale
    // 2 file non validi per dichiarazione GPL
    public static void main(String[] args) {
	try {
	    
	    if(args.length < 2){
		System.out.println("Error usage: java JavaDocCheck directory printQuieteMode(true/false)");
		System.exit(-1);
	    }
	    
	    String dir = args[0];
	    String sintesi = args[1];
	    boolean isSintesi = false;
	    isSintesi = Boolean.parseBoolean(sintesi);
            	    
	    check(new File(dir));

	    java.util.Vector<String> filePrinted = new java.util.Vector<String>();	    
	    if(fileNonValidi.size()>0){
		for(int i=0; i<fileNonValidi.size(); i++){
		    if(isSintesi){
			    String file = fileNonValidi.get(i);
			    if(filePrinted.contains(file)==false){
				    System.out.println(file);
				    filePrinted.add(file);
			    }
		    }
		    else{
			    System.out.println("\nIl file "+fileNonValidi.get(i)+" non ha superato il controllo: \n"+dichiarazioneAssente.get(i)+"\n");
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
    
    private static boolean printTODO = false;
    
    public static void check(File f) {
	try {
	    if(f.isFile()){
		//System.out.println("FILE");


                boolean jumpCheck = false;
		// gestione eccezioni:
                // package dao.
		/* NELLA 2.0 ho fatto generare il java doc author etc... anche in questi package
                if( f.getAbsolutePath().indexOf("src/org/openspcoop/dao/busta")!= -1 && !f.getAbsolutePath().contains("driver"))
			jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("src/org/openspcoop/dao/tracciamento")!= -1 && !f.getAbsolutePath().contains("driver"))
                        jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("src/org/openspcoop/dao/msgdiagnostico")!= -1 && !f.getAbsolutePath().contains("driver"))
                        jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("src/org/openspcoop/dao/config")!= -1 && !f.getAbsolutePath().contains("driver"))
                        jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("src/org/openspcoop/dao/sysconfig")!= -1 && !f.getAbsolutePath().contains("driver"))
                        jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("src/org/openspcoop/dao/registry")!= -1 && !f.getAbsolutePath().contains("driver"))
                        jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("src/it/cnipa/collprofiles")!= -1 && !f.getAbsolutePath().contains("driver"))
                        jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("src/it/gov/spcoop/sica/manifest")!= -1 && !f.getAbsolutePath().contains("driver"))
                        jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("src/it/gov/spcoop/sica/wscp")!= -1 && !f.getAbsolutePath().contains("driver"))
                        jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("src/it/gov/spcoop/sica/wsbl")!= -1 && !f.getAbsolutePath().contains("driver"))
                        jumpCheck = true;
                else if( f.getAbsolutePath().indexOf("src/org/openspcoop/dao/eccezione")!= -1 && !f.getAbsolutePath().contains("driver"))
                        jumpCheck = true;
		*/
		if( f.getAbsolutePath().endsWith("/package-info.java") ){
			jumpCheck = true;
		}
		// ws client senza java doc (sono generati)
		if( f.getAbsolutePath().indexOf("tools/ws/registry/client/src/org/openspcoop2/core/registry/ws/client/")!= -1 && !f.getAbsolutePath().contains("/client/utils/"))
                        jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("tools/ws/config/client/src/org/openspcoop2/core/config/ws/client/")!= -1 && !f.getAbsolutePath().contains("/client/utils/"))
                        jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("tools/ws/monitor/client/src/org/openspcoop2/pdd/monitor/ws/client/")!= -1 && !f.getAbsolutePath().contains("/client/utils/"))
                        jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("tools/ws/diagnostica/client/src/org/openspcoop2/core/diagnostica/ws/client/")!= -1 && !f.getAbsolutePath().contains("/client/utils/"))
                        jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("tools/ws/tracciamento/client/src/org/openspcoop2/core/tracciamento/ws/client/")!= -1 && !f.getAbsolutePath().contains("/client/utils/"))
                        jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("tools/ws/transazioni/client/src/org/openspcoop2/core/transazioni/ws/client/")!= -1 && !f.getAbsolutePath().contains("/client/utils/"))
			jumpCheck = true;
		else if( f.getAbsolutePath().indexOf("tools/web_interfaces/control_station/deploy/index.html")!=-1 )
			jumpCheck = true;
		else if( f.getAbsolutePath().contains("distrib/check/") )
			jumpCheck = true;

		String TODO = "tools/rs/config/server/src/org/openspcoop2/core/config/rs/server";
		String TODO2 = "tools/rs/monitor/server/src/org/openspcoop2/core/monitor/rs/server";
		if(!printTODO){		
			System.out.println("TODO: Eliminare controllo per RS Server");
			printTODO = true;		
		}
		if(!jumpCheck && f.getAbsolutePath().indexOf(TODO)!=-1){
			jumpCheck = true;
		}
		if(!jumpCheck && f.getAbsolutePath().indexOf(TODO2)!=-1){
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



		    // ****** autore ****
		    // check autore
		    if(byteInputBuffer.toString().indexOf(JavaDocCheck.AUTORE_CHECK)==-1){
			fileNonValidi.add(f.getAbsolutePath());
			dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.AUTORE_CHECK+" non presente");		
		    }
                    if(byteInputBuffer.toString().indexOf(JavaDocCheck.AUTORE_CHECK2)==-1){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.AUTORE_CHECK2+" non presente (manca l'informazione sull'ultimo autore che ha modificato il file)");
                    }
		    // check valore autore
		   // if(byteInputBuffer.toString().indexOf(JavaDocCheck.AUTORE_VALORE)>=0){
		//	fileNonValidi.add(f.getAbsolutePath());
                 //       dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.AUTORE_CHECK+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.AUTORE_VALORE+"]");
		   // }
		    if(byteInputBuffer.toString().indexOf(JavaDocCheck.AUTORE_VALORE2)>=0){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.AUTORE_CHECK+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.AUTORE_VALORE2+"]");
                    }
                    if(byteInputBuffer.toString().indexOf(JavaDocCheck.AUTORE_VALORE3)>=0){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.AUTORE_CHECK+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.AUTORE_VALORE3+"]");
                    }


		    // ***** versione ****
		    // check versione
		    if(byteInputBuffer.toString().indexOf(JavaDocCheck.VERSIONE_CHECK)==-1){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.VERSIONE_CHECK+" non presente");
                    }
                    if(byteInputBuffer.toString().indexOf(JavaDocCheck.VERSIONE_CHECK2)==-1){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.VERSIONE_CHECK2+" non presente (manca l'informazione sulla versione svn)");
                    }
                    // check valore versione
		    if(byteInputBuffer.toString().indexOf(JavaDocCheck.VERSIONE_VALORE)>=0){
			fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.VERSIONE_CHECK+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.VERSIONE_VALORE+"]");
		    }
		    if(byteInputBuffer.toString().indexOf(JavaDocCheck.VERSIONE_VALORE2)>=0){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.VERSIONE_CHECK+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.VERSIONE_VALORE2+"]");
                    }
                    if(byteInputBuffer.toString().indexOf(JavaDocCheck.VERSIONE_VALORE3)>=0){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.VERSIONE_CHECK+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.VERSIONE_VALORE3+"]");
                    }



		    // ***** data *****
		    if(byteInputBuffer.toString().indexOf(JavaDocCheck.DATA_CHECK)==-1){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.DATA_CHECK+" non presente");
                    }
                    // check valore data
                    if(byteInputBuffer.toString().indexOf(JavaDocCheck.DATA_VALORE)>=0){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.DATA_CHECK+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.DATA_VALORE+"]");
                    }
                    if(byteInputBuffer.toString().indexOf(JavaDocCheck.DATA_VALORE2)>=0){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.DATA_CHECK+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.DATA_VALORE2+"]");
                    }
                    if(byteInputBuffer.toString().indexOf(JavaDocCheck.DATA_VALORE3)>=0){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.DATA_CHECK+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.DATA_VALORE3+"]");
                    }


		    // ***** valore corrotto *****
		    if(byteInputBuffer.toString().indexOf(JavaDocCheck.VALORE_CORROTTO)>=0){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("valore corrotto "+JavaDocCheck.VALORE_CORROTTO+" presente");
                    }

		}

		if(f.getName().endsWith(".html") && (jumpCheck==false) ){

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

	             //String packageFile = byteInputBuffer.toString().substring(byteInputBuffer.toString().indexOf("package.html"),
			//(byteInputBuffer.toString().indexOf("package.html")+21));
			String packageFile = byteInputBuffer.toString();
		    //System.out.println("TROVATO ["+packageFile+"]");


		    // ********* versione ********
		    // check versione
                    if(packageFile.indexOf(JavaDocCheck.VERSIONE_CHECK_HTML)==-1){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.VERSIONE_CHECK_HTML+" non presente");
                    }
                    // check valore versione
                    if(!(packageFile.indexOf(JavaDocCheck.VERSIONE_VALORE)==-1)){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.VERSIONE_CHECK_HTML+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.VERSIONE_VALORE+"]");
                    }
                    if(!(packageFile.indexOf(JavaDocCheck.VERSIONE_VALORE2)==-1)){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.VERSIONE_CHECK_HTML+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.VERSIONE_VALORE2+"]");
                    }
                    if(!(packageFile.indexOf(JavaDocCheck.VERSIONE_VALORE3)==-1)){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.VERSIONE_CHECK_HTML+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.VERSIONE_VALORE3+"]");
                    }


                    // ***** data *****
                    if(packageFile.indexOf(JavaDocCheck.DATA_CHECK)==-1){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.DATA_CHECK+" non presente");
                    }
                    // check valore data
                    if(packageFile.indexOf(JavaDocCheck.DATA_VALORE)>=0){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.DATA_CHECK+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.DATA_VALORE+"]");
                    }
                    if(packageFile.indexOf(JavaDocCheck.DATA_VALORE2)>=0){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.DATA_CHECK+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.DATA_VALORE2+"]");
                    }
                    if(packageFile.indexOf(JavaDocCheck.DATA_VALORE3)>=0){
                        fileNonValidi.add(f.getAbsolutePath());
                        dichiarazioneAssente.add("dichiarazione "+JavaDocCheck.DATA_CHECK+" presente con un valore non risolto dal commit svn: ["+JavaDocCheck.DATA_VALORE3+"]");
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
