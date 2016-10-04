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

/**
 * Package accordo di servizio parte comune
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @version 1.4, 02/08/06
 */

package it.gov.spcoop.sica.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Documento inserito in un accordo
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Documento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2982135475324753831L;
	
	public Documento(){}
	public Documento(String nome,String tipo,byte[] contenuto){
		this.nome = nome;
		this.tipo = tipo;
		this.contenuto = contenuto;
	}
	
	public Documento(String nome,String tipo,InputStream contenuto) throws IOException{
		this.nome = nome;
		this.tipo = tipo;
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[]buffer = new byte[1024];
		int byteLetti = -1;
		while( (byteLetti = contenuto.read(buffer))!=-1){
			bout.write(buffer,0,byteLetti);	
		}
		bout.flush();
		bout.close();
		this.contenuto = bout.toByteArray();
	}
	
	private String nome;
	private byte[] contenuto;
	/** doc, xsd, ....*/
	private String tipo;
	
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public byte[] getContenuto() {
		return this.contenuto;
	}
	public void setContenuto(byte[] contenuto) {
		this.contenuto = contenuto;
	}
	public String getTipo() {
		return this.tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
}
