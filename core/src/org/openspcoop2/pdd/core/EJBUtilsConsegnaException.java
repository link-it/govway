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



package org.openspcoop2.pdd.core;

import org.openspcoop2.pdd.logger.MsgDiagnostico;

/**	
 * Contiene la definizione di una eccezione lanciata dalla classe EJBUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class EJBUtilsConsegnaException extends EJBUtilsException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String messaggio;
	private int livello;
	private String codice;

	public EJBUtilsConsegnaException(MsgDiagnostico msgDiag,String idFunzioneDiagnostica,String idDiagnostico){
		this(msgDiag.getMessaggio_replaceKeywords(idFunzioneDiagnostica,idDiagnostico),
				msgDiag.getLivello(idFunzioneDiagnostica,idDiagnostico),
				msgDiag.getCodice(idFunzioneDiagnostica,idDiagnostico));
	}
	
	public EJBUtilsConsegnaException(MsgDiagnostico msgDiag,String idFunzioneDiagnostica,String idDiagnostico, Throwable cause){
		this(msgDiag.getMessaggio_replaceKeywords(idFunzioneDiagnostica,idDiagnostico),
				msgDiag.getLivello(idFunzioneDiagnostica,idDiagnostico),
				msgDiag.getCodice(idFunzioneDiagnostica,idDiagnostico),
				cause);
	}
	
	public EJBUtilsConsegnaException(String message, int livelloErrore, String codice) {
		super(message);
		this.messaggio = message;
		this.livello = livelloErrore;
		this.codice = codice;
	}

	public EJBUtilsConsegnaException(String message, int livelloErrore, String codice, Throwable cause){
		super(message, cause);	
		this.messaggio = message;
		this.livello = livelloErrore;
		this.codice = codice;
	}

	public int getLivello() {
		return this.livello;
	}

	public String getMessaggio() {
		return this.messaggio;
	}
	
	public String getCodice() {
		return this.codice;
	}

}

