/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

package org.openspcoop2.core.transazioni.utils;

import java.io.Serializable;

import org.openspcoop2.utils.date.DateManager;

/**     
 * TempiElaborazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TempiElaborazione implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected TempiElaborazioneFunzionalita token;
	protected TempiElaborazioneFunzionalita autenticazione;
	protected TempiElaborazioneFunzionalita autenticazioneToken;
	protected TempiElaborazioneFunzionalita autorizzazione;
	protected TempiElaborazioneFunzionalita autorizzazioneContenuti;
	protected TempiElaborazioneFunzionalita validazioneRichiesta;
	protected TempiElaborazioneFunzionalita validazioneRisposta;
	protected TempiElaborazioneFunzionalita controlloTraffico_maxRequests;
	protected TempiElaborazioneFunzionalita controlloTraffico_rateLimiting;
	protected TempiElaborazioneFunzionalita sicurezzaMessaggioRichiesta;
	protected TempiElaborazioneFunzionalita sicurezzaMessaggioRisposta;
	protected TempiElaborazioneFunzionalita gestioneAttachmentsRichiesta;
	protected TempiElaborazioneFunzionalita gestioneAttachmentsRisposta;
	protected TempiElaborazioneFunzionalita correlazioneApplicativaRichiesta;
	protected TempiElaborazioneFunzionalita correlazioneApplicativaRisposta;
	protected TempiElaborazioneFunzionalita tracciamentoRichiesta;
	protected TempiElaborazioneFunzionalita tracciamentoRisposta;
	protected TempiElaborazioneFunzionalita dumpRichiestaIngresso;
	protected TempiElaborazioneFunzionalita dumpRichiestaUscita;
	protected TempiElaborazioneFunzionalita dumpRispostaIngresso;
	protected TempiElaborazioneFunzionalita dumpRispostaUscita;
	protected TempiElaborazioneFunzionalita dumpBinarioRichiestaIngresso;
	protected TempiElaborazioneFunzionalita dumpBinarioRichiestaUscita;
	protected TempiElaborazioneFunzionalita dumpBinarioRispostaIngresso;
	protected TempiElaborazioneFunzionalita dumpBinarioRispostaUscita;
	protected TempiElaborazioneFunzionalita dumpIntegrationManager;
	protected TempiElaborazioneFunzionalita responseCachingCalcoloDigest;
	protected TempiElaborazioneFunzionalita responseCachingReadFromCache;
	protected TempiElaborazioneFunzionalita responseCachingSaveInCache;
	
	
	public TempiElaborazioneFunzionalita getToken() {
		return this.token;
	}
	public void setToken(TempiElaborazioneFunzionalita token) {
		this.token = token;
	}
	public void startToken() {
		this.token = new TempiElaborazioneFunzionalita();
		this.token.setDataIngresso(DateManager.getDate());
	}
	public void endToken() {
		this.token.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getAutenticazione() {
		return this.autenticazione;
	}
	public void setAutenticazione(TempiElaborazioneFunzionalita autenticazione) {
		this.autenticazione = autenticazione;
	}
	public void startAutenticazione() {
		this.autenticazione = new TempiElaborazioneFunzionalita();
		this.autenticazione.setDataIngresso(DateManager.getDate());
	}
	public void endAutenticazione() {
		this.autenticazione.setDataUscita(DateManager.getDate());
	}
	
	public TempiElaborazioneFunzionalita getAutenticazioneToken() {
		return this.autenticazioneToken;
	}
	public void setAutenticazioneToken(TempiElaborazioneFunzionalita autenticazioneToken) {
		this.autenticazioneToken = autenticazioneToken;
	}
	public void startAutenticazioneToken() {
		this.autenticazioneToken = new TempiElaborazioneFunzionalita();
		this.autenticazioneToken.setDataIngresso(DateManager.getDate());
	}
	public void endAutenticazioneToken() {
		this.autenticazioneToken.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getAutorizzazione() {
		return this.autorizzazione;
	}
	public void setAutorizzazione(TempiElaborazioneFunzionalita autorizzazione) {
		this.autorizzazione = autorizzazione;
	}
	public void startAutorizzazione() {
		this.autorizzazione = new TempiElaborazioneFunzionalita();
		this.autorizzazione.setDataIngresso(DateManager.getDate());
	}
	public void endAutorizzazione() {
		this.autorizzazione.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getAutorizzazioneContenuti() {
		return this.autorizzazioneContenuti;
	}
	public void setAutorizzazioneContenuti(TempiElaborazioneFunzionalita autorizzazioneContenuti) {
		this.autorizzazioneContenuti = autorizzazioneContenuti;
	}
	public void startAutorizzazioneContenuti() {
		this.autorizzazioneContenuti = new TempiElaborazioneFunzionalita();
		this.autorizzazioneContenuti.setDataIngresso(DateManager.getDate());
	}
	public void endAutorizzazioneContenuti() {
		this.autorizzazioneContenuti.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getValidazioneRichiesta() {
		return this.validazioneRichiesta;
	}
	public void setValidazioneRichiesta(TempiElaborazioneFunzionalita validazioneRichiesta) {
		this.validazioneRichiesta = validazioneRichiesta;
	}
	public void startValidazioneRichiesta() {
		this.validazioneRichiesta = new TempiElaborazioneFunzionalita();
		this.validazioneRichiesta.setDataIngresso(DateManager.getDate());
	}
	public void endValidazioneRichiesta() {
		this.validazioneRichiesta.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getValidazioneRisposta() {
		return this.validazioneRisposta;
	}
	public void setValidazioneRisposta(TempiElaborazioneFunzionalita validazioneRisposta) {
		this.validazioneRisposta = validazioneRisposta;
	}
	public void startValidazioneRisposta() {
		this.validazioneRisposta = new TempiElaborazioneFunzionalita();
		this.validazioneRisposta.setDataIngresso(DateManager.getDate());
	}
	public void endValidazioneRisposta() {
		this.validazioneRisposta.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getControlloTraffico_maxRequests() {
		return this.controlloTraffico_maxRequests;
	}
	public void setControlloTraffico_maxRequests(TempiElaborazioneFunzionalita controlloTraffico_maxRequests) {
		this.controlloTraffico_maxRequests = controlloTraffico_maxRequests;
	}
	public void startControlloTraffico_maxRequests() {
		this.controlloTraffico_maxRequests = new TempiElaborazioneFunzionalita();
		this.controlloTraffico_maxRequests.setDataIngresso(DateManager.getDate());
	}
	public void endControlloTraffico_maxRequests() {
		this.controlloTraffico_maxRequests.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getControlloTraffico_rateLimiting() {
		return this.controlloTraffico_rateLimiting;
	}
	public void setControlloTraffico_rateLimiting(TempiElaborazioneFunzionalita controlloTraffico_rateLimiting) {
		this.controlloTraffico_rateLimiting = controlloTraffico_rateLimiting;
	}
	public void startControlloTraffico_rateLimiting() {
		this.controlloTraffico_rateLimiting = new TempiElaborazioneFunzionalita();
		this.controlloTraffico_rateLimiting.setDataIngresso(DateManager.getDate());
	}
	public void endControlloTraffico_rateLimiting() {
		this.controlloTraffico_rateLimiting.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getSicurezzaMessaggioRichiesta() {
		return this.sicurezzaMessaggioRichiesta;
	}
	public void setSicurezzaMessaggioRichiesta(TempiElaborazioneFunzionalita sicurezzaMessaggioRichiesta) {
		this.sicurezzaMessaggioRichiesta = sicurezzaMessaggioRichiesta;
	}
	public void startSicurezzaMessaggioRichiesta() {
		this.sicurezzaMessaggioRichiesta = new TempiElaborazioneFunzionalita();
		this.sicurezzaMessaggioRichiesta.setDataIngresso(DateManager.getDate());
	}
	public void endSicurezzaMessaggioRichiesta() {
		this.sicurezzaMessaggioRichiesta.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getSicurezzaMessaggioRisposta() {
		return this.sicurezzaMessaggioRisposta;
	}
	public void setSicurezzaMessaggioRisposta(TempiElaborazioneFunzionalita sicurezzaMessaggioRisposta) {
		this.sicurezzaMessaggioRisposta = sicurezzaMessaggioRisposta;
	}
	public void startSicurezzaMessaggioRisposta() {
		this.sicurezzaMessaggioRisposta = new TempiElaborazioneFunzionalita();
		this.sicurezzaMessaggioRisposta.setDataIngresso(DateManager.getDate());
	}
	public void endSicurezzaMessaggioRisposta() {
		this.sicurezzaMessaggioRisposta.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getGestioneAttachmentsRichiesta() {
		return this.gestioneAttachmentsRichiesta;
	}
	public void setGestioneAttachmentsRichiesta(TempiElaborazioneFunzionalita gestioneAttachmentsRichiesta) {
		this.gestioneAttachmentsRichiesta = gestioneAttachmentsRichiesta;
	}
	public void startGestioneAttachmentsRichiesta() {
		this.gestioneAttachmentsRichiesta = new TempiElaborazioneFunzionalita();
		this.gestioneAttachmentsRichiesta.setDataIngresso(DateManager.getDate());
	}
	public void endGestioneAttachmentsRichiesta() {
		this.gestioneAttachmentsRichiesta.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getGestioneAttachmentsRisposta() {
		return this.gestioneAttachmentsRisposta;
	}
	public void setGestioneAttachmentsRisposta(TempiElaborazioneFunzionalita gestioneAttachmentsRisposta) {
		this.gestioneAttachmentsRisposta = gestioneAttachmentsRisposta;
	}
	public void startGestioneAttachmentsRisposta() {
		this.gestioneAttachmentsRisposta = new TempiElaborazioneFunzionalita();
		this.gestioneAttachmentsRisposta.setDataIngresso(DateManager.getDate());
	}
	public void endGestioneAttachmentsRisposta() {
		this.gestioneAttachmentsRisposta.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getCorrelazioneApplicativaRichiesta() {
		return this.correlazioneApplicativaRichiesta;
	}
	public void setCorrelazioneApplicativaRichiesta(TempiElaborazioneFunzionalita correlazioneApplicativaRichiesta) {
		this.correlazioneApplicativaRichiesta = correlazioneApplicativaRichiesta;
	}
	public void startCorrelazioneApplicativaRichiesta() {
		this.correlazioneApplicativaRichiesta = new TempiElaborazioneFunzionalita();
		this.correlazioneApplicativaRichiesta.setDataIngresso(DateManager.getDate());
	}
	public void endCorrelazioneApplicativaRichiesta() {
		this.correlazioneApplicativaRichiesta.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getCorrelazioneApplicativaRisposta() {
		return this.correlazioneApplicativaRisposta;
	}
	public void setCorrelazioneApplicativaRisposta(TempiElaborazioneFunzionalita correlazioneApplicativaRisposta) {
		this.correlazioneApplicativaRisposta = correlazioneApplicativaRisposta;
	}
	public void startCorrelazioneApplicativaRisposta() {
		this.correlazioneApplicativaRisposta = new TempiElaborazioneFunzionalita();
		this.correlazioneApplicativaRisposta.setDataIngresso(DateManager.getDate());
	}
	public void endCorrelazioneApplicativaRisposta() {
		this.correlazioneApplicativaRisposta.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getTracciamentoRichiesta() {
		return this.tracciamentoRichiesta;
	}
	public void setTracciamentoRichiesta(TempiElaborazioneFunzionalita tracciamentoRichiesta) {
		this.tracciamentoRichiesta = tracciamentoRichiesta;
	}
	public void startTracciamentoRichiesta() {
		this.tracciamentoRichiesta = new TempiElaborazioneFunzionalita();
		this.tracciamentoRichiesta.setDataIngresso(DateManager.getDate());
	}
	public void endTracciamentoRichiesta() {
		this.tracciamentoRichiesta.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getTracciamentoRisposta() {
		return this.tracciamentoRisposta;
	}
	public void setTracciamentoRisposta(TempiElaborazioneFunzionalita tracciamentoRisposta) {
		this.tracciamentoRisposta = tracciamentoRisposta;
	}
	public void startTracciamentoRisposta() {
		this.tracciamentoRisposta = new TempiElaborazioneFunzionalita();
		this.tracciamentoRisposta.setDataIngresso(DateManager.getDate());
	}
	public void endTracciamentoRisposta() {
		this.tracciamentoRisposta.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getDumpRichiestaIngresso() {
		return this.dumpRichiestaIngresso;
	}
	public void setDumpRichiestaIngresso(TempiElaborazioneFunzionalita dumpRichiestaIngresso) {
		this.dumpRichiestaIngresso = dumpRichiestaIngresso;
	}
	public void startDumpRichiestaIngresso() {
		this.dumpRichiestaIngresso = new TempiElaborazioneFunzionalita();
		this.dumpRichiestaIngresso.setDataIngresso(DateManager.getDate());
	}
	public void endDumpRichiestaIngresso() {
		this.dumpRichiestaIngresso.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getDumpRichiestaUscita() {
		return this.dumpRichiestaUscita;
	}
	public void setDumpRichiestaUscita(TempiElaborazioneFunzionalita dumpRichiestaUscita) {
		this.dumpRichiestaUscita = dumpRichiestaUscita;
	}
	public void startDumpRichiestaUscita() {
		this.dumpRichiestaUscita = new TempiElaborazioneFunzionalita();
		this.dumpRichiestaUscita.setDataIngresso(DateManager.getDate());
	}
	public void endDumpRichiestaUscita() {
		this.dumpRichiestaUscita.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getDumpRispostaIngresso() {
		return this.dumpRispostaIngresso;
	}
	public void setDumpRispostaIngresso(TempiElaborazioneFunzionalita dumpRispostaIngresso) {
		this.dumpRispostaIngresso = dumpRispostaIngresso;
	}
	public void startDumpRispostaIngresso() {
		this.dumpRispostaIngresso = new TempiElaborazioneFunzionalita();
		this.dumpRispostaIngresso.setDataIngresso(DateManager.getDate());
	}
	public void endDumpRispostaIngresso() {
		this.dumpRispostaIngresso.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getDumpRispostaUscita() {
		return this.dumpRispostaUscita;
	}
	public void setDumpRispostaUscita(TempiElaborazioneFunzionalita dumpRispostaUscita) {
		this.dumpRispostaUscita = dumpRispostaUscita;
	}
	public void startDumpRispostaUscita() {
		this.dumpRispostaUscita = new TempiElaborazioneFunzionalita();
		this.dumpRispostaUscita.setDataIngresso(DateManager.getDate());
	}
	public void endDumpRispostaUscita() {
		this.dumpRispostaUscita.setDataUscita(DateManager.getDate());
	}
	
	public TempiElaborazioneFunzionalita getDumpBinarioRichiestaIngresso() {
		return this.dumpBinarioRichiestaIngresso;
	}
	public void setDumpBinarioRichiestaIngresso(TempiElaborazioneFunzionalita dumpBinarioRichiestaIngresso) {
		this.dumpBinarioRichiestaIngresso = dumpBinarioRichiestaIngresso;
	}
	public void startDumpBinarioRichiestaIngresso() {
		this.dumpBinarioRichiestaIngresso = new TempiElaborazioneFunzionalita();
		this.dumpBinarioRichiestaIngresso.setDataIngresso(DateManager.getDate());
	}
	public void endDumpBinarioRichiestaIngresso() {
		this.dumpBinarioRichiestaIngresso.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getDumpBinarioRichiestaUscita() {
		return this.dumpBinarioRichiestaUscita;
	}
	public void setDumpBinarioRichiestaUscita(TempiElaborazioneFunzionalita dumpBinarioRichiestaUscita) {
		this.dumpBinarioRichiestaUscita = dumpBinarioRichiestaUscita;
	}
	public void startDumpBinarioRichiestaUscita() {
		this.dumpBinarioRichiestaUscita = new TempiElaborazioneFunzionalita();
		this.dumpBinarioRichiestaUscita.setDataIngresso(DateManager.getDate());
	}
	public void endDumpBinarioRichiestaUscita() {
		this.dumpBinarioRichiestaUscita.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getDumpBinarioRispostaIngresso() {
		return this.dumpBinarioRispostaIngresso;
	}
	public void setDumpBinarioRispostaIngresso(TempiElaborazioneFunzionalita dumpBinarioRispostaIngresso) {
		this.dumpBinarioRispostaIngresso = dumpBinarioRispostaIngresso;
	}
	public void startDumpBinarioRispostaIngresso() {
		this.dumpBinarioRispostaIngresso = new TempiElaborazioneFunzionalita();
		this.dumpBinarioRispostaIngresso.setDataIngresso(DateManager.getDate());
	}
	public void endDumpBinarioRispostaIngresso() {
		this.dumpBinarioRispostaIngresso.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getDumpBinarioRispostaUscita() {
		return this.dumpBinarioRispostaUscita;
	}
	public void setDumpBinarioRispostaUscita(TempiElaborazioneFunzionalita dumpBinarioRispostaUscita) {
		this.dumpBinarioRispostaUscita = dumpBinarioRispostaUscita;
	}
	public void startDumpBinarioRispostaUscita() {
		this.dumpBinarioRispostaUscita = new TempiElaborazioneFunzionalita();
		this.dumpBinarioRispostaUscita.setDataIngresso(DateManager.getDate());
	}
	public void endDumpBinarioRispostaUscita() {
		this.dumpBinarioRispostaUscita.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getDumpIntegrationManager() {
		return this.dumpIntegrationManager;
	}
	public void setDumpIntegrationManager(TempiElaborazioneFunzionalita dumpIntegrationManager) {
		this.dumpIntegrationManager = dumpIntegrationManager;
	}
	public void startDumpIntegrationManager() {
		this.dumpIntegrationManager = new TempiElaborazioneFunzionalita();
		this.dumpIntegrationManager.setDataIngresso(DateManager.getDate());
	}
	public void endDumpIntegrationManager() {
		this.dumpIntegrationManager.setDataUscita(DateManager.getDate());
	}
	
	
	public TempiElaborazioneFunzionalita getResponseCachingCalcoloDigest() {
		return this.responseCachingCalcoloDigest;
	}
	public void setResponseCachingCalcoloDigest(TempiElaborazioneFunzionalita responseCachingCalcoloDigest) {
		this.responseCachingCalcoloDigest = responseCachingCalcoloDigest;
	}
	public void startResponseCachingCalcoloDigest() {
		this.responseCachingCalcoloDigest = new TempiElaborazioneFunzionalita();
		this.responseCachingCalcoloDigest.setDataIngresso(DateManager.getDate());
	}
	public void endResponseCachingCalcoloDigest() {
		this.responseCachingCalcoloDigest.setDataUscita(DateManager.getDate());
	}
	
	public TempiElaborazioneFunzionalita getResponseCachingReadFromCache() {
		return this.responseCachingReadFromCache;
	}
	public void setResponseCachingReadFromCache(TempiElaborazioneFunzionalita responseCachingReadFromCache) {
		this.responseCachingReadFromCache = responseCachingReadFromCache;
	}
	public void startResponseCachingReadFromCache() {
		this.responseCachingReadFromCache = new TempiElaborazioneFunzionalita();
		this.responseCachingReadFromCache.setDataIngresso(DateManager.getDate());
	}
	public void endResponseCachingReadFromCache() {
		this.responseCachingReadFromCache.setDataUscita(DateManager.getDate());
	}
	
	public TempiElaborazioneFunzionalita getResponseCachingSaveInCache() {
		return this.responseCachingSaveInCache;
	}
	public void setResponseCachingSaveInCache(TempiElaborazioneFunzionalita responseCachingSaveInCache) {
		this.responseCachingSaveInCache = responseCachingSaveInCache;
	}
	public void startResponseCachingSaveInCache() {
		this.responseCachingSaveInCache = new TempiElaborazioneFunzionalita();
		this.responseCachingSaveInCache.setDataIngresso(DateManager.getDate());
	}
	public void endResponseCachingSaveInCache() {
		this.responseCachingSaveInCache.setDataUscita(DateManager.getDate());
	}
	
}