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


package org.openspcoop2.core.registry.constants;


/**
 *
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipiDocumentoSemiformale {

	
	UML ("UML"),
	HTML ("HTML"),
	XML ("XML"),
	LINGUAGGIO_NATURALE ("Linguaggio Naturale");
	
	private final String nome;

	TipiDocumentoSemiformale(String nome)
	{
		this.nome = nome;
	}

	public String getNome()
	{
		return this.nome;
	}
	
	@Override
	public String toString(){
		return this.nome;
	}
	
	public static String[] toStringArray(){
		String[] res = new String[TipiDocumentoSemiformale.values().length];
		int i=0;
		for (TipiDocumentoSemiformale tmp : TipiDocumentoSemiformale.values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	
	public static String[] toEnumNameArray(){
		String[] res = new String[TipiDocumentoSemiformale.values().length];
		int i=0;
		for (TipiDocumentoSemiformale tmp : TipiDocumentoSemiformale.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static TipiDocumentoSemiformale toEnumConstant(String val){
		TipiDocumentoSemiformale res = null;
		if(TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString().equals(val)){
			res = TipiDocumentoSemiformale.LINGUAGGIO_NATURALE;
		}else if(TipiDocumentoSemiformale.HTML.toString().equals(val)){
			res = TipiDocumentoSemiformale.HTML;
		}else if(TipiDocumentoSemiformale.UML.toString().equals(val)){
			res = TipiDocumentoSemiformale.UML;
		}else if(TipiDocumentoSemiformale.XML.toString().equals(val)){
			res = TipiDocumentoSemiformale.XML;
		}
		
		return res;
		
	}
}


