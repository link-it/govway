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

package org.openspcoop2.pdd.core.state;

import java.io.Serializable;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.DimensioneMessaggiAttraversamentoPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.TempiAttraversamentoPDD;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.state.StateMap;
import org.openspcoop2.protocol.sdk.state.StatelessMessage;
import org.openspcoop2.utils.UtilsException;

/**
 * Oggetto che rappresenta lo stato di una richiesta/risposta all'interno della PdD
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoopStateless extends OpenSPCoopState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* ---------- Messaggi  ----------*/
	private OpenSPCoop2Message richiestaMsg = null;
	private OpenSPCoop2Message rispostaMsg = null;
	
	/* ---------- Contiene i proprietari dei messaggi  ----------*/
	private String destinatarioRequestMsgLib;
	private String destinatarioResponseMsgLib;
	
	/* ---------- Contiene i tempi di attraversamento  ----------*/
	private TempiAttraversamentoPDD tempiAttraversamentoPDD = null;
	
	/* ---------- Contiene la dimensione dei messaggi  ----------*/
	private DimensioneMessaggiAttraversamentoPdD dimensioneMessaggiAttraversamentoPDD = null;

	/* ---------- ID di Correlazione Applicativa ------------ */
	private String idCorrelazioneApplicativa = null;
	private String idCorrelazioneApplicativaRisposta = null;

	/* ---------- PdDContext -------------*/
	private PdDContext pddContext = null;
	
	
	
	
	/* ---------- Costruttori  ----------*/
	public OpenSPCoopStateless(StatelessMessage richiesta,OpenSPCoop2Message richiestaMsg,
			StatelessMessage risposta,OpenSPCoop2Message rispostaMsg){
		this.richiestaStato = richiesta;
		this.richiestaMsg = richiestaMsg;
		this.rispostaStato = risposta;
		this.rispostaMsg = rispostaMsg;
		this.useConnection = false;
		this.tempiAttraversamentoPDD = new TempiAttraversamentoPDD();
		this.dimensioneMessaggiAttraversamentoPDD = new DimensioneMessaggiAttraversamentoPdD();
	}
	
	public OpenSPCoopStateless(StatelessMessage richiesta,OpenSPCoop2Message richiestaMsg){
		this.richiestaStato = richiesta;
		this.richiestaMsg = richiestaMsg;
		this.useConnection = false;
		this.tempiAttraversamentoPDD = new TempiAttraversamentoPDD();
		this.dimensioneMessaggiAttraversamentoPDD = new DimensioneMessaggiAttraversamentoPdD();
	}
	
	public OpenSPCoopStateless(){
		this.useConnection = false;
		this.tempiAttraversamentoPDD = new TempiAttraversamentoPDD();
		this.dimensioneMessaggiAttraversamentoPDD = new DimensioneMessaggiAttraversamentoPdD();
	}

	
	
	
	
	
	/* ----------- Init resource ------------*/
	@Override
	public void updateStatoRichiesta() throws UtilsException{
		
		StateMap pstmt = null;
		Busta bustaTmp = null;
		Busta bustaCorrelataTmp = null;
		if(this.richiestaStato!=null){
			if(this.richiestaStato.getPreparedStatement()!=null)
				pstmt = this.richiestaStato.getPreparedStatement();
			StatelessMessage statelessMessage = (StatelessMessage) this.richiestaStato;
			bustaTmp = statelessMessage.getBusta();
			bustaCorrelataTmp = statelessMessage.getBustaCorrelata();
		}
		
		this.richiestaStato = new StatelessMessage(this.connectionDB,this.logger);
		if(pstmt!=null){
			this.richiestaStato.addPreparedStatement(pstmt.getPreparedStatement());
		}
		if(bustaTmp!=null){
			((StatelessMessage) this.richiestaStato).setBusta(bustaTmp);
		}
		if(bustaCorrelataTmp!=null){
			((StatelessMessage) this.richiestaStato).setBustaCorrelata(bustaCorrelataTmp);
		}
		
	}
	@Override
	public void updateStatoRisposta() throws UtilsException{
		
		StateMap pstmt = null;
		Busta bustaTmp = null;
		Busta bustaCorrelataTmp = null;
		if(this.rispostaStato!=null){
			if(this.rispostaStato.getPreparedStatement()!=null)
				pstmt = this.rispostaStato.getPreparedStatement();
			StatelessMessage statelessMessage = (StatelessMessage) this.rispostaStato;
			bustaTmp = statelessMessage.getBusta();
			bustaCorrelataTmp = statelessMessage.getBustaCorrelata();
		}
		
		this.rispostaStato = new StatelessMessage(this.connectionDB,this.logger);
		if(pstmt!=null){
			this.rispostaStato.addPreparedStatement(pstmt.getPreparedStatement());
		}
		if(bustaTmp!=null){
			((StatelessMessage) this.rispostaStato).setBusta(bustaTmp);
		}
		if(bustaCorrelataTmp!=null){
			((StatelessMessage) this.rispostaStato).setBustaCorrelata(bustaCorrelataTmp);
		}

	}
	
	
	
	

	
	

	
	
	
	
	/* ----------- Serializzazione ------------*/

	public OpenSPCoopStateless rendiSerializzabile() {
		OpenSPCoopStateless stato = new OpenSPCoopStateless();
		StatelessMessage statoRichiesta = new StatelessMessage(null, null);
		StatelessMessage statoRisposta = new StatelessMessage(null, null);
		statoRichiesta.setBusta(((StatelessMessage)this.richiestaStato).getBusta());
		statoRisposta.setBusta(((StatelessMessage)this.rispostaStato).getBusta());
		stato.setStatoRichiesta(statoRichiesta);
		stato.setStatoRisposta(statoRisposta);
		stato.setTempiAttraversamentoPDD(this.getTempiAttraversamentoPDD());
		stato.setDimensioneMessaggiAttraversamentoPDD(this.getDimensioneMessaggiAttraversamentoPDD());
		return stato;
	}
	
	
	
	
	
	
	
	
	/* ----------- GET / SET ------------*/
	
	public void setDestinatarioRequestMsgLib(String nextLib) {
		this.destinatarioRequestMsgLib = nextLib;
	}

	public String getDestinatarioRequestMsgLib(){
		return this.destinatarioRequestMsgLib;
	}

	public String getDestinatarioResponseMsgLib() {
		return this.destinatarioResponseMsgLib;
	}

	public void setDestinatarioResponseMsgLib(String destinatarioResponseMsgLib) {
		this.destinatarioResponseMsgLib = destinatarioResponseMsgLib;
	}
	
	public void setRichiestaMsg(OpenSPCoop2Message richiestaMsg) {
		this.richiestaMsg = richiestaMsg;
	}
	
	public OpenSPCoop2Message getRichiestaMsg() {
		return this.richiestaMsg;
	}

	public void setRispostaMsg(OpenSPCoop2Message rispostaMsg) {
		this.rispostaMsg = rispostaMsg;
	}
	
	public OpenSPCoop2Message getRispostaMsg() {
		return this.rispostaMsg;
	}
	
	public TempiAttraversamentoPDD getTempiAttraversamentoPDD() {
		return this.tempiAttraversamentoPDD;
	}
	
	public void setTempiAttraversamentoPDD(TempiAttraversamentoPDD tempiAttraversamentoPDD) {
		this.tempiAttraversamentoPDD = tempiAttraversamentoPDD;
	}
	
	public DimensioneMessaggiAttraversamentoPdD getDimensioneMessaggiAttraversamentoPDD() {
		return this.dimensioneMessaggiAttraversamentoPDD;
	}

	public void setDimensioneMessaggiAttraversamentoPDD(
			DimensioneMessaggiAttraversamentoPdD dimensioneMessaggiAttraversamentoPDD) {
		this.dimensioneMessaggiAttraversamentoPDD = dimensioneMessaggiAttraversamentoPDD;
	}
	
	public boolean isUseConnection() {
		return this.useConnection;
	}

	public void setUseConnection(boolean useConnection) {
		this.useConnection = useConnection;
	}

	public String getIDCorrelazioneApplicativa() {
		return this.idCorrelazioneApplicativa;
	}

	public void setIDCorrelazioneApplicativa(String idCorrelazioneApplicativa) {
		this.idCorrelazioneApplicativa = idCorrelazioneApplicativa;
	}

	public PdDContext getPddContext() {
		return this.pddContext;
	}

	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}

	public String getIDCorrelazioneApplicativaRisposta() {
		return this.idCorrelazioneApplicativaRisposta;
	}

	public void setIDCorrelazioneApplicativaRisposta(
			String idCorrelazioneApplicativaRisposta) {
		this.idCorrelazioneApplicativaRisposta = idCorrelazioneApplicativaRisposta;
	}

}