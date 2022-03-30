/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.basic.archive;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.Utilities;


/**
 * Classe utilizzata per lavorare sui package
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ZIPUtils  {

	public static final String ID_CORRELAZIONE_DEFAULT = "@PackageOpenSPCoop@";
	
	
	
	
	public static String oldMethod_convertCharNonPermessiQualsiasiSistemaOperativo(String nome){
		return oldMethod_convertCharNonPermessiQualsiasiSistemaOperativo(nome, true, null);
	}
	public static String oldMethod_convertCharNonPermessiQualsiasiSistemaOperativo(String nome,boolean permitUnderscore){
		return oldMethod_convertCharNonPermessiQualsiasiSistemaOperativo(nome, permitUnderscore, null);
	}
	public static String oldMethod_convertCharNonPermessiQualsiasiSistemaOperativo(String nome,boolean permitUnderscore,List<Character> permit){
		StringBuilder bf = new StringBuilder();
		for (int i = 0; i < nome.length(); i++) {
			if(Character.isLetterOrDigit(nome.charAt(i))){
				bf.append(nome.charAt(i));
			}
			else {
				if(permit!=null){
					// check che sia nella lista dei caratteri permessi
					boolean found = false;
					for (char charPermit : permit) {
						if(charPermit == nome.charAt(i)){
							found = true;
							break;
						}
					}
					if(found){
						bf.append(nome.charAt(i));
						continue;
					}
				}
				
				// Se non e' nella lista dei caratteri permessi, se e' abilitato l'underscore mappo qualsiasi carattere in un '_',
				// altrimenti lo "brucio"
				if(permitUnderscore){
					// sostituisco tutto con _
					bf.append("_");
				}
			}
		}
		return bf.toString();
	}
	
	
	public static String convertNameToSistemaOperativoCompatible(String nome){
		List<Character> permit = new ArrayList<Character>();
		// Non permetto nessun carattere poiche' alcune versioni del S.O. ad esempio non accettano un dato carattere se e' all'inizio o alla fine...
		// quindi li maschero con il carattere jolly e sono tranquillo
//		permit.add('.');
//		permit.add('_');
//		permit.add('-');
		
		Character cJolly = 'X'; // uso un carattere come jolly almeno non ho problemi
		
		return Utilities.convertNameToSistemaOperativoCompatible(nome,true,cJolly,permit,true);
	}

	
	
}