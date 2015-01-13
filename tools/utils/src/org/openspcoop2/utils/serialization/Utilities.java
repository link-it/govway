/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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


package org.openspcoop2.utils.serialization;

import java.util.Date;

/**	
 * Contiene delle utilities per questo package
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Utilities {

	public static void normalizeDateObjects(Object o) throws IOException{
		try{
			if(o!=null){
				//System.out.println("Inizio analisi per  ["+o.getClass().getName()+"]...");
				java.lang.reflect.Method[] methods = o.getClass().getMethods();
				for(int i=0; i<methods.length;i++){
					String nomeMetodo = methods[i].getName();
					//System.out.println("Esamino ["+nomeMetodo+"]...");
					if(nomeMetodo.length()>3 && 
							nomeMetodo.startsWith("get") && 
							methods[i].getParameterTypes()!=null && methods[i].getParameterTypes().length==0 &&
							methods[i].getReturnType()!=null && !("void".equals(methods[i].getReturnType().getName()))){
						Class<?> tipoReturn = methods[i].getReturnType();
						//System.out.println("ANALIZZO TIPO RETURN ["+tipoReturn.getName()+"]...");
												
						if(tipoReturn.isAssignableFrom(Date.class)){
																	
							//System.out.println("DATA creo oggetto time ["+methods[i].getName()+"]? check tipo per evitare object generici che sono assegnabili a Date");
							Object oDate = methods[i].invoke(o);
							if(oDate!=null){
								if(oDate instanceof Date){
									Date tmp = (Date) oDate;
									String nomeMetodoSet = "s"+nomeMetodo.subSequence(1, nomeMetodo.length());
									//System.out.println("DATA set con metodo ["+nomeMetodoSet+"]");
									java.lang.reflect.Method methodSet =  o.getClass().getMethod(nomeMetodoSet,Date.class);
									methodSet.invoke(o,new Date(tmp.getTime()));
									//System.out.println("DATA set effettuato");
								}
							}
						}
						else if(o.getClass().getPackage()!=null && tipoReturn.getName().startsWith(o.getClass().getPackage().getName())){
							if(tipoReturn.isEnum()==false){
								//System.out.println("Normalize per ["+o.getClass().getPackage().getName()+"]...");
								Utilities.normalizeDateObjects(methods[i].invoke(o));
								//System.out.println("Normalize per ["+o.getClass().getPackage().getName()+"] fatto");
							}/*else{
								System.out.println("ENUM!");
							}*/
						}
					}
					else if(nomeMetodo.length()>3 && 
							nomeMetodo.startsWith("size") &&  nomeMetodo.endsWith("List") &&  
							methods[i].getParameterTypes()!=null && methods[i].getParameterTypes().length==0 &&
							methods[i].getReturnType()!=null && "int".equals(methods[i].getReturnType().getName())){
						Object oLista = methods[i].invoke(o);
						//System.out.println("LISTA["+oLista.getClass().getName()+"]");
						int sizeLista = (Integer) oLista;
						for(int j=0; j<sizeLista; j++){
							String nomeMetodoGet = "get"+nomeMetodo.subSequence("size".length(), (nomeMetodo.length()-"List".length()));
							//System.out.println("NOME METODO["+nomeMetodoGet+"]");
							java.lang.reflect.Method methodGet =  null;
							try{
								methodGet = o.getClass().getMethod(nomeMetodoGet,int.class);
							}catch(Exception e){
								//System.out.println("NON ESISTE  IL METODO: "+e.getMessage());
							}
							if(methodGet!=null){
								//System.out.println("ANALIZZO TIPO RETURN METODO GET ["+methodGet.getReturnType().getName()+"]...");
								Class<?> tipoReturn = methodGet.getReturnType();
								if(tipoReturn.getName().startsWith(o.getClass().getPackage().getName())){
									//System.out.println("RINORMALIZZO IN RICORSIONE..... ");
									try{
										Utilities.normalizeDateObjects(methodGet.invoke(o,j));
									}catch(Exception e){
										//System.out.println("RINORMALIZZAZIONE NON RIUSCITA: "+e.getMessage());
									}	
									//System.out.println("RINORMALIZZO IN RICORSIONE FINE -------------------- ");
								}
							}/*else{
								System.out.println("NON ESISTE  IL METODO");
							}*/
						}
					}
				}
				//System.out.println("Fine analisi per  ["+o.getClass().getName()+"].");
			}
		}catch(Exception e){
			throw new IOException("Normalizzazione date non riuscita: "+e.getMessage(),e);
		}
	}
	
}
