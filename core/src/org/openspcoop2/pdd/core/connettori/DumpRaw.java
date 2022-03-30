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

package org.openspcoop2.pdd.core.connettori;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;

/**
 * DumpRaw
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpRaw {

	@SuppressWarnings("unused")
	private ConnettoreLogger logger;
	@SuppressWarnings("unused")
	private boolean pd;
	private Dump dump;
	private boolean dumpBinario;
	private boolean dumpBinario_registrazioneDatabase = false;
	private DumpConfigurazione dumpDatabaseConfigurazione;
	private boolean dumpDatabaseRichiestaUscitaHeaders;
	private boolean dumpDatabaseRichiestaUscitaPayload;
	private boolean dumpDatabaseRispostaIngressoHeaders;
	private boolean dumpDatabaseRispostaIngressoPayload;
	private boolean onlyLogFileTraceRichiestaUscitaHeaders = false;
	private boolean onlyLogFileTraceRichiestaUscitaPayload = false;
	private boolean onlyLogFileTraceRispostaIngressoHeaders = false;
	private boolean onlyLogFileTraceRispostaIngressoPayload = false;
	
	private IDSoggetto dominio;
	private String modulo;
	private TipoPdD tipoPdD;
	
	public DumpRaw(ConnettoreLogger log,IDSoggetto dominio,String modulo,TipoPdD tipoPdD, 
			boolean dumpBinario, 
			DumpConfigurazione dumpConfigurazione,
			boolean fileTrace) throws ConnectorException{
		this.logger = log;
		
		switch (tipoPdD) {
		case DELEGATA:
			this.pd = true;
			break;
		default:
			this.pd = false;
			break;
		}
		
		this.dumpBinario = dumpBinario;
		if(this.dumpBinario) {
			this.dumpBinario_registrazioneDatabase = OpenSPCoop2Properties.getInstance().isDumpBinario_registrazioneDatabase();
		}
		
		if(dumpConfigurazione!=null) {
			if(dumpConfigurazione.getRichiestaUscita()!=null) {
				// il parsing deve essere gestito con l'altra modalità, che dovrà gestire anche gli header, senno sul db si troveranno 2 messaggi
				boolean payloadParsing = StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaUscita().getPayload()) &&
						StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaUscita().getPayloadParsing());
				if(!payloadParsing) {
					if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaUscita().getHeaders())){
						this.dumpDatabaseRichiestaUscitaHeaders = true;
					}
					if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaUscita().getPayload())){
						this.dumpDatabaseRichiestaUscitaPayload = true;
					}
				}
			}
			if(dumpConfigurazione.getRispostaIngresso()!=null) {
				// il parsing deve essere gestito con l'altra modalità, che dovrà gestire anche gli header, senno sul db si troveranno 2 messaggi
				boolean payloadParsing = StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaIngresso().getPayload()) &&
						StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaIngresso().getPayloadParsing());
				if(!payloadParsing) {
					if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaIngresso().getHeaders())){
						this.dumpDatabaseRispostaIngressoHeaders = true;
					}
					if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaIngresso().getPayload())){
						this.dumpDatabaseRispostaIngressoPayload = true;
					}
				}
			}
			this.dumpDatabaseConfigurazione = dumpConfigurazione;
		}
		
		if(!this.dumpDatabaseRichiestaUscitaHeaders && (!dumpBinario || !this.dumpBinario_registrazioneDatabase) ) {
			this.onlyLogFileTraceRichiestaUscitaHeaders = fileTrace;
		}
		if(!this.dumpDatabaseRichiestaUscitaPayload && (!dumpBinario || !this.dumpBinario_registrazioneDatabase) ) {
			this.onlyLogFileTraceRichiestaUscitaPayload = fileTrace;
		}
		if(!this.dumpDatabaseRispostaIngressoHeaders && (!dumpBinario || !this.dumpBinario_registrazioneDatabase) ) {
			this.onlyLogFileTraceRispostaIngressoHeaders = fileTrace;
		}
		if(!this.dumpDatabaseRispostaIngressoPayload && (!dumpBinario || !this.dumpBinario_registrazioneDatabase) ) {
			this.onlyLogFileTraceRispostaIngressoPayload = fileTrace;
		}
				
		this.dominio = dominio;
		this.modulo = modulo;
		this.tipoPdD = tipoPdD;
	}
	
	public boolean isActiveDumpDatabase() {
		return this.isDumpBinarioRegistrazioneDatabase() || isRegistrazioneDatabase() || onlyLogFileTrace();
	}
	public boolean isActiveDumpDatabaseRichiesta() {
		return this.dumpBinario || isRegistrazioneDatabaseRichiesta() || onlyLogFileTraceRichiesta();
	}
	public boolean isActiveDumpDatabaseRisposta() {
		return this.dumpBinario || isRegistrazioneDatabaseRisposta() || onlyLogFileTraceRisposta();
	}
	
	private boolean isRegistrazioneDatabase() {
		return this.isRegistrazioneDatabaseRichiesta()
				||
			   this.isRegistrazioneDatabaseRisposta()
			   ;
	}
	private boolean isRegistrazioneDatabaseRichiesta() {
		return this.dumpDatabaseRichiestaUscitaHeaders
				||
			   this.dumpDatabaseRichiestaUscitaPayload
			   ;
	}
	private boolean isRegistrazioneDatabaseRisposta() {
		return this.dumpDatabaseRispostaIngressoHeaders
				||
			   this.dumpDatabaseRispostaIngressoPayload
			   ;
	}
	
	private boolean onlyLogFileTrace() {
		return this.onlyLogFileTraceRichiesta()
				||
			   this.onlyLogFileTraceRisposta()
			   ;
	}
	private boolean onlyLogFileTraceRichiesta() {
		return this.onlyLogFileTraceRichiestaUscitaHeaders
				||
			   this.onlyLogFileTraceRichiestaUscitaPayload
				||
			   this.onlyLogFileTraceRispostaIngressoHeaders
				||
			   this.onlyLogFileTraceRispostaIngressoPayload
			   ;
	}
	private boolean onlyLogFileTraceRisposta() {
		return this.onlyLogFileTraceRichiestaUscitaHeaders
				||
			   this.onlyLogFileTraceRichiestaUscitaPayload
				||
			   this.onlyLogFileTraceRispostaIngressoHeaders
				||
			   this.onlyLogFileTraceRispostaIngressoPayload
			   ;
	}
	
	private boolean isDumpBinarioRegistrazioneDatabase() {
		return this.dumpBinario && this.dumpBinario_registrazioneDatabase;
	}
	
	public void initDump(String interfaceName, PdDContext pddContext) throws DumpException {
		initDump(interfaceName, pddContext, 
				null,
				null, null);
	}
	public void initDump(String interfaceName, PdDContext pddContext, 
			TransazioneApplicativoServer transazioneApplicativoServer,
			IDPortaApplicativa idPA, Date dataConsegnaTransazioneApplicativoServer) throws DumpException {
		
		if(!isActiveDumpDatabase()) {
			return;
		}
		
		if(this.isRegistrazioneDatabase() || this.isDumpBinarioRegistrazioneDatabase() || this.onlyLogFileTrace()) {
			this.dump = new Dump(this.dominio, this.modulo, this.tipoPdD, interfaceName, pddContext, this.dumpDatabaseConfigurazione);
			if(transazioneApplicativoServer!=null) {
				this.dump.setTransazioneApplicativoServer(transazioneApplicativoServer, 
						idPA, dataConsegnaTransazioneApplicativoServer);
			}
		}	
	}
		

	public void dumpRequest(DumpByteArrayOutputStream content, MessageType messageType, InfoConnettoreUscita infoConnettoreUscita) throws DumpException {
		
		if(!isActiveDumpDatabaseRichiesta()) {
			return;
		}
		
		if(this.dump!=null) {
			//if(content!=null){ // devono essere registrati anche solamente gli header
			this.dump.dumpBinarioRichiestaUscita(this.isDumpBinarioRegistrazioneDatabase(), this.onlyLogFileTraceRichiestaUscitaHeaders, this.onlyLogFileTraceRichiestaUscitaPayload, 
					content, messageType,
					infoConnettoreUscita);
			//}
		}
	}
	
	public void dumpResponse(DumpByteArrayOutputStream content, MessageType messageType, InfoConnettoreUscita infoConnettoreUscita, Map<String, List<String>> trasportoRisposta) throws DumpException {
		
		if(!isActiveDumpDatabaseRisposta()) {
			return;
		}
		
		if(this.dump!=null) {
			//if(content!=null){ // devono essere registrati anche solamente gli header
			this.dump.dumpBinarioRispostaIngresso(this.isDumpBinarioRegistrazioneDatabase(), this.onlyLogFileTraceRispostaIngressoHeaders, this.onlyLogFileTraceRispostaIngressoPayload, 
					content, messageType, 
					infoConnettoreUscita, trasportoRisposta);
			//}
		}
	}
	
}
