/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

package org.openspcoop2.web.monitor.transazioni.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.transazioni.utils.TempiElaborazione;
import org.openspcoop2.core.transazioni.utils.TempiElaborazioneFunzionalita;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;

/**     
 * TempiElaborazioneBean
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TempiElaborazioneBean extends TempiElaborazione {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String format = "yyyy-MM-dd HH:mm:ss.SSS";
	
	public TempiElaborazioneBean() {
		super();
	}

	public TempiElaborazioneBean(TempiElaborazione tempiElaborazione){
		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(0);
		BeanUtils.copy(this, tempiElaborazione, metodiEsclusi);
	}
	
	private String _getInfoTempiElaborazione(TempiElaborazioneFunzionalita funzionalita) {
		
		if(funzionalita!=null) {
			StringBuilder bf = new StringBuilder();
			SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter(format);
			
			if(funzionalita.getLatenza()>=0) {
				bf.append("Latenza: ").append(funzionalita.getLatenza()).append("ms");
			}
			if(funzionalita.getDataIngresso()!=null) {
				if(bf.length()>0) {
					bf.append(" , ");
				}
				bf.append("Inizio: ").append(dateformat.format(funzionalita.getDataIngresso()));
			}
			if(funzionalita.getDataUscita()!=null) {
				if(bf.length()>0) {
					bf.append(" , ");
				}
				bf.append("Fine: ").append(dateformat.format(funzionalita.getDataUscita()));
			}
			
			if(bf.length()>0) {
				return bf.toString();
			}
			return null;
		}
		return null;
	}
	
	public String getInfoTempiElaborazioneToken() {
		return this._getInfoTempiElaborazione(this.token);
	}
	
	public String getInfoTempiElaborazioneAutenticazione() {
		return this._getInfoTempiElaborazione(this.autenticazione);
	}
	
	public String getInfoTempiElaborazioneAutenticazioneToken() {
		return this._getInfoTempiElaborazione(this.autenticazioneToken);
	}
	
	public String getInfoTempiElaborazioneAutorizzazione() {
		return this._getInfoTempiElaborazione(this.autorizzazione);
	}
	
	public String getInfoTempiElaborazioneAutorizzazioneContenuti() {
		return this._getInfoTempiElaborazione(this.autorizzazioneContenuti);
	}
	
	public String getInfoTempiElaborazioneValidazioneRichiesta() {
		return this._getInfoTempiElaborazione(this.validazioneRichiesta);
	}
	
	public String getInfoTempiElaborazioneValidazioneRisposta() {
		return this._getInfoTempiElaborazione(this.validazioneRisposta);
	}
	
	public String getInfoTempiElaborazioneControlloTraffico_maxRequests() {
		return this._getInfoTempiElaborazione(this.controlloTraffico_maxRequests);
	}
	
	public String getInfoTempiElaborazioneControlloTraffico_rateLimiting() {
		return this._getInfoTempiElaborazione(this.controlloTraffico_rateLimiting);
	}
	
	public String getInfoTempiElaborazioneSicurezzaMessaggioRichiesta() {
		return this._getInfoTempiElaborazione(this.sicurezzaMessaggioRichiesta);
	}
	
	public String getInfoTempiElaborazioneSicurezzaMessaggioRisposta() {
		return this._getInfoTempiElaborazione(this.sicurezzaMessaggioRisposta);
	}
	
	public String getInfoTempiElaborazioneGestioneAttachmentsRichiesta() {
		return this._getInfoTempiElaborazione(this.gestioneAttachmentsRichiesta);
	}
	
	public String getInfoTempiElaborazioneGestioneAttachmentsRisposta() {
		return this._getInfoTempiElaborazione(this.gestioneAttachmentsRisposta);
	}
	
	public String getInfoTempiElaborazioneCorrelazioneApplicativaRichiesta() {
		return this._getInfoTempiElaborazione(this.correlazioneApplicativaRichiesta);
	}
	
	public String getInfoTempiElaborazioneCorrelazioneApplicativaRisposta() {
		return this._getInfoTempiElaborazione(this.correlazioneApplicativaRisposta);
	}
	
	public String getInfoTempiElaborazioneTracciamentoRichiesta() {
		return this._getInfoTempiElaborazione(this.tracciamentoRichiesta);
	}
	
	public String getInfoTempiElaborazioneTracciamentoRisposta() {
		return this._getInfoTempiElaborazione(this.tracciamentoRisposta);
	}
	
	public String getInfoTempiElaborazioneDumpRichiestaIngresso() {
		return this._getInfoTempiElaborazione(this.dumpRichiestaIngresso);
	}
	
	public String getInfoTempiElaborazioneDumpRichiestaUscita() {
		return this._getInfoTempiElaborazione(this.dumpRichiestaUscita);
	}
	
	public String getInfoTempiElaborazioneDumpRispostaIngresso() {
		return this._getInfoTempiElaborazione(this.dumpRispostaIngresso);
	}
	
	public String getInfoTempiElaborazioneDumpRispostaUscita() {
		return this._getInfoTempiElaborazione(this.dumpRispostaUscita);
	}
	
	public String getInfoTempiElaborazioneDumpBinarioRichiestaIngresso() {
		return this._getInfoTempiElaborazione(this.dumpBinarioRichiestaIngresso);
	}
	
	public String getInfoTempiElaborazioneDumpBinarioRichiestaUscita() {
		return this._getInfoTempiElaborazione(this.dumpBinarioRichiestaUscita);
	}
	
	public String getInfoTempiElaborazioneDumpBinarioRispostaIngresso() {
		return this._getInfoTempiElaborazione(this.dumpBinarioRispostaIngresso);
	}
	
	public String getInfoTempiElaborazioneDumpBinarioRispostaUscita() {
		return this._getInfoTempiElaborazione(this.dumpBinarioRispostaUscita);
	}
	
	public String getInfoTempiElaborazioneDumpIntegrationManager() {
		return this._getInfoTempiElaborazione(this.dumpIntegrationManager);
	}
	
	public String getInfoTempiElaborazioneResponseCachingCalcoloDigest() {
		return this._getInfoTempiElaborazione(this.responseCachingCalcoloDigest);
	}
	
	public String getInfoTempiElaborazioneResponseCachingReadFromCache() {
		return this._getInfoTempiElaborazione(this.responseCachingReadFromCache);
	}
	
	public String getInfoTempiElaborazioneResponseCachingSaveInCache() {
		return this._getInfoTempiElaborazione(this.responseCachingSaveInCache);
	}
	
	public String getInfoTempiElaborazioneTrasformazioneRichiesta() {
		return this._getInfoTempiElaborazione(this.trasformazioneRichiesta);
	}
	
	public String getInfoTempiElaborazioneTrasformazioneRisposta() {
		return this._getInfoTempiElaborazione(this.trasformazioneRisposta);
	}
	
	public String getInfoTempiElaborazioneAttributeAuthority() {
		return this._getInfoTempiElaborazione(this.attributeAuthority);
	}
	
}
