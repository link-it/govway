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



package org.openspcoop2.protocol.sdk.diagnostica;

import java.io.Serializable;

/**
 * MsgDiagnosticoCorrelazioneServizioApplicativo
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MsgDiagnosticoCorrelazioneServizioApplicativo implements Serializable{

	private static final long serialVersionUID = 7616051015989365464L;

	private boolean delegata;
    private String idBusta;
    private String servizioApplicativo;
    private String protocollo;
	
    public MsgDiagnosticoCorrelazioneServizioApplicativo() {
	}

	public boolean isDelegata() {
		return this.delegata;
	}

	public void setDelegata(boolean delegata) {
		this.delegata = delegata;
	}

	public String getIdBusta() {
		return this.idBusta;
	}

	public void setIdBusta(String idBusta) {
		this.idBusta = idBusta;
	}

	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}

	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}

	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
	
	
}


