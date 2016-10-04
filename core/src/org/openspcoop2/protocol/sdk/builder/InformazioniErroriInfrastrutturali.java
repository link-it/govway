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

package org.openspcoop2.protocol.sdk.builder;

/**
 * InformazioniErroriInfrastrutturali.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniErroriInfrastrutturali {

	boolean erroreUtilizzoConnettore = false;
	boolean ricevutoSoapFaultServerPortaDelegata = false;
	boolean contenutoRichiestaNonRiconosciuto = false;
	boolean contenutoRispostaNonRiconosciuto = false;
	
	public boolean isErroreUtilizzoConnettore() {
		return this.erroreUtilizzoConnettore;
	}
	public void setErroreUtilizzoConnettore(boolean erroreUtilizzoConnettore) {
		this.erroreUtilizzoConnettore = erroreUtilizzoConnettore;
	}
	public boolean isRicevutoSoapFaultServerPortaDelegata() {
		return this.ricevutoSoapFaultServerPortaDelegata;
	}
	public void setRicevutoSoapFaultServerPortaDelegata(boolean ricevutoSoapFaultServerPortaDelegata) {
		this.ricevutoSoapFaultServerPortaDelegata = ricevutoSoapFaultServerPortaDelegata;
	}
	public boolean isContenutoRichiestaNonRiconosciuto() {
		return this.contenutoRichiestaNonRiconosciuto;
	}
	public void setContenutoRichiestaNonRiconosciuto(boolean contenutoRichiestaNonRiconosciuto) {
		this.contenutoRichiestaNonRiconosciuto = contenutoRichiestaNonRiconosciuto;
	}
	public boolean isContenutoRispostaNonRiconosciuto() {
		return this.contenutoRispostaNonRiconosciuto;
	}
	public void setContenutoRispostaNonRiconosciuto(boolean contenutoRispostaNonRiconosciuto) {
		this.contenutoRispostaNonRiconosciuto = contenutoRispostaNonRiconosciuto;
	}
}
