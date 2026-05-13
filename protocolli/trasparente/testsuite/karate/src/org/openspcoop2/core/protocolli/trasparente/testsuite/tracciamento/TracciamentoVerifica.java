/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.monitor.sdk.transaction.FaseTracciamento;
import org.openspcoop2.utils.BooleanNullable;

/**
* TracciamentoVerifica
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TracciamentoVerifica {

	public TracciamentoVerifica(boolean verificaDB) {
		this.verificaDB = verificaDB;
	}
	
	public FaseTracciamento faseTracciamentoErrore = null;
	public boolean faseTracciamentoErroreDB = false;
	
	public FaseTracciamento checkDiagnosticFromFase = null;
	public boolean checkDiagnostico(FaseTracciamento fase) {
		return check(fase, this.checkDiagnosticFromFase);
	}
	public FaseTracciamento checkLogDetailFromFase = FaseTracciamento.OUT_RESPONSE;
	public boolean checkLogDetail(FaseTracciamento fase) {
		return check(fase, this.checkLogDetailFromFase);
	}
	
	public Boolean forzaVerificaDBInRequest = null;
	public Boolean forzaVerificaDBOutRequest = null;
	public Boolean forzaVerificaDBOutResponse = null;
	public Boolean forzaVerificaDBPostOutResponse = null;
	
	public boolean verificaDB = true;
	public String getTipoVerifica() {
		return this.verificaDB ? "database" : "filetrace";
	}
	
	public BooleanNullable verificaInRequest = null;
	public long sleepAfterInRequest = -1;
		
	public BooleanNullable verificaOutRequest = null;
	public long sleepAfterOutRequest = -1;
	
	public BooleanNullable verificaOutResponse = null;
	public long sleepAfterOutResponse = -1;
	
	public BooleanNullable verificaPostOutResponse = null;
	
	public Map<String, String> queryParameters = new HashMap<>();
	
	public Map<String, String> headers = new HashMap<>();
	
	public boolean checkInfo = false;
	public List<String> mapExpectedTokenInfo = new ArrayList<>();
	public boolean tempiElaborazioneExpected;
	
	public boolean client = true;
	public boolean server = true;

	// Override granulari header/payload separati. Se null, viene utilizzato il valore di 'client' / 'server'.
	// Servono per esprimere configurazioni asimmetriche di fileTrace (es. solo header su 'dump-in').
	public Boolean clientHeader  = null;
	public Boolean clientPayload = null;
	public Boolean serverHeader  = null;
	public Boolean serverPayload = null;

	public boolean isClientHeaderExpected()  { return this.clientHeader  != null ? this.clientHeader.booleanValue()  : this.client; }
	public boolean isClientPayloadExpected() { return this.clientPayload != null ? this.clientPayload.booleanValue() : this.client; }
	public boolean isServerHeaderExpected()  { return this.serverHeader  != null ? this.serverHeader.booleanValue()  : this.server; }
	public boolean isServerPayloadExpected() { return this.serverPayload != null ? this.serverPayload.booleanValue() : this.server; }

	public Boolean verificaContenuti = null;

	// Se true, oltre alla verifica su DB (innescata da verificaDB/forzaVerificaDB...) viene eseguita
	// ANCHE la verifica del contenuto del file di fileTrace nelle stesse fasi.
	// Serve per i test asimmetrici di fileTrace che vogliono coprire in un'unica esecuzione sia
	// "dump_messaggi vuota" sia "file di fileTrace popolato in modo coerente alla config asimmetrica".
	public boolean verificaFileTraceContent = false;
	
	public boolean check(FaseTracciamento fase, FaseTracciamento from) {
		if(from==null) {
			return true;
		}
		switch (fase) {
			case IN_REQUEST:{
				switch (from) {
					case IN_REQUEST:
						return true;
					default:
						return false;
				}
			}
			case OUT_REQUEST:{
				switch (from) {
				case IN_REQUEST:
				case OUT_REQUEST:
					return true;
				default:
					return false;
				}
			}
			case OUT_RESPONSE:{
				switch (from) {
				case IN_REQUEST:
				case OUT_REQUEST:
				case OUT_RESPONSE:
					return true;
				default:
					return false;
				}
			}
			case POST_OUT_RESPONSE:{
				return true;
			}
		}
		return true;
	}
}
