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
 * GPLWriter
 *
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GPLWriter {

	public static final String VERSION = "* GovWay - A customizable API Gateway";
	public static final String GPL = "/*\n"+
			" "+VERSION+" \n"+
			" * https://govway.org\n"+
			" * \n"+
			" * Copyright (c) 2005-2021 Link.it srl (https://link.it).\n"+ 
			" * \n"+
			" * This program is free software: you can redistribute it and/or modify\n"+
			" * it under the terms of the GNU General Public License version 3, as published by\n"+
			" * the Free Software Foundation.\n"+
			" *\n"+
			" * This program is distributed in the hope that it will be useful,\n"+
			" * but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
			" * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"+
			" * GNU General Public License for more details.\n"+
			" *\n"+
			" * You should have received a copy of the GNU General Public License\n"+
			" * along with this program.  If not, see <http://www.gnu.org/licenses/>.\n"+
			" *\n"+
			" */\n"; 

	public static int profonditaCheck = 1000;

	public static void main(String[] args) {
		try {

			int profondita=1;

			if(args.length < 1){
				System.out.println("Error usage: java GPLWriter directory [profondita]");
				return;
			}
			if(args.length==2){
				profonditaCheck = Integer.parseInt(args[1]);
			}


			String dir = args[0];

			writeGPLDichiarazione(new File(dir),profondita);


		} catch(Exception ex) {
			System.err.println("Errore generale: " + ex);
		}

	}


	public static void writeGPLDichiarazione(File f,int profondita) {
		try {
			if(f.isFile()){
				if(f.getName().endsWith(".java")){

					// Get Bytes Originali
					FileInputStream fis =new FileInputStream(f);
					ByteArrayOutputStream byteInputBuffer = new ByteArrayOutputStream();
					byte [] readB = new byte[8192];
					int readByte = 0;
					while((readByte = fis.read(readB))!= -1){
						byteInputBuffer.write(readB,0,readByte);
					}
					fis.close();


					boolean found = false;
					int indexFound = byteInputBuffer.toString().indexOf(VERSION);
					if(!(indexFound==-1)){
						found = true;
					}
					if(found){
						return; // non aggiungo
					}	 


					FileOutputStream fos =new FileOutputStream(f);
					fos.write(GPL.getBytes());
					fos.write(byteInputBuffer.toByteArray());
					fos.flush();
					fos.close();
				}
			}else{
				if( (profondita++) <= profonditaCheck){
					File [] fChilds = f.listFiles();
					for (int i = 0; i < fChilds.length; i++) {
						writeGPLDichiarazione(fChilds[i],(profondita++));
					}
				}
			}

		} catch(Exception ex) {
			System.err.println("Errore writeGPLDichiarazione: " + ex);
		}

	}
}
