/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.utils.mime;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


/**
 * Libreria contenente metodi utili per la gestione degli Attachments.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MultipartUtils {

	/**
	 * Trova la stringa utilizzata, all'interno del parametro <var>input</var>,
	 * per delimitare i vari attachments presenti.
	 *
	 * @param input byte[] del messaggio da esaminare
	 * @return String del boundary, in caso di ricerca con successo, null altrimenti.
	 * 
	 */
	public static String findBoundary(byte [] input){

		ByteArrayOutputStream boundary = null;
		try{

			//if(!messageWithAttachment(xml))
			//  return null;

			boundary = new ByteArrayOutputStream();
			int i=0;
			while(i<input.length){
				if(input[i]!='\n') {
					boundary.write(input[i]);
				}
				if(input[i]=='\n' || i==input.length){
					if(boundary.toString().startsWith("--"))
						break;
					else
						boundary.reset();
				}
				i++;
			}

			String bS = null;
			if(boundary.size() != 0) {
				//bS = boundary.toString().substring(0,boundary.toString().length()-2);
				// Lo \n non lo includo proprio nel calcolo del boundary
				bS = boundary.toString();
			}

			boundary.close();
			return bS;

		}catch(Exception e){
			try{
				if(boundary!=null)
					boundary.close();
			}catch(Exception eis){}
			return null;
		}
	}


	/**
	 * Trova la stringa utilizzata, all'interno del parametro <var>input</var>,
	 * per delimitare i vari attachments presenti.
	 *
	 * @param input InputStream del messaggio da esaminare
	 * @return String del boundary, in caso di ricerca con successo, null altrimenti.
	 * 
	 */
	public static String findBoundary(InputStream input){

		ByteArrayOutputStream boundary = null;
		try{

			boundary = new ByteArrayOutputStream();
			byte date = 0;
			while((date=(byte)input.read())!=-1){
				boundary.write(date);
				if(((char)date)=='\n'){
					if(boundary.toString().startsWith("--"))
						break;
					else
						boundary.reset();
				}
			}

			String bS = null;
			if(boundary.size() != 0)
				bS = boundary.toString().substring(0,boundary.toString().length()-2);

			boundary.close();
			return bS;

		}catch(Exception e){
			try{
				if(boundary!=null)
					boundary.close();
			}catch(Exception eis){}
			return null;
		}
	}




	/**
	 * Esamina il contenuto del messaggio <var>input</var>, per vedere se sono presenti o meno degli attachments.
	 *
	 * @param input byte[] del messaggio da esaminare
	 * @return true in caso di presenza di attachments, false altrimenti.
	 * 
	 */
	public static boolean messageWithAttachment(byte [] input){
		// read first line
		if(input.length < 10)
			return false;

		// Cerco -- nei primi 10 caratteri, e che non incontro <
		for(int i=0; i<9; i++){
			if( ((char)input[i] == '-') &&  ((char)input[i+1] == '-') ){
				return true;
			}else if( (char)input[i] == '<'  )
				return false;
		}

		return false;
	}




	/**
	 * Trova il Content-ID utilizzato, all'interno del parametro <var>input</var>
	 *
	 * @param input byte[] del messaggio da esaminare
	 * @return String del ContentID, in caso di ricerca con successo, null altrimenti.
	 * 
	 */
	public static String firstContentID(byte [] input){

		ByteArrayOutputStream line = null;
		try{

			//log.info("Check ID First");
			String IDfirst = null;
			int index=0;
			boolean found = false;
			while(!found && index<input.length){
				line  = new ByteArrayOutputStream();
				while(true){
					if(input[index]=='\r'){
						index++;
						if(input[index]=='\n'){
							index++;//elimino anche \n
						}
						break;
					} 
					line.write(input[index]);
					index++;
				}
				if (line.toString().toLowerCase().startsWith("Content-Id:".toLowerCase())){
					found = true;
					String[] rr = line.toString().split(" ");
					//log.info("ID FIRST ["+rr[1]+"]");
					IDfirst = rr[1];
				}
				line.close();
			}  

			return IDfirst;

		}catch(Exception e){
			try{
				if(line!=null)
					line.close();
			}catch(Exception eis){}
			return null;
		}

	}


}

