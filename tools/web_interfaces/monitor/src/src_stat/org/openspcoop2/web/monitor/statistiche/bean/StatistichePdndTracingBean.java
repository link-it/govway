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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.statistiche.StatistichePdndTracing;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiRichieste;
import org.openspcoop2.core.transazioni.utils.DumpUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.web.monitor.core.core.Utils;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.statistiche.constants.StatisticheCostanti;

/**
 * StatistichePdndTracingBean
 * 
 * @author Pintori Giuliano (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatistichePdndTracingBean extends StatistichePdndTracing {

	private static final long serialVersionUID = 1L;
	
	private String soggettoReadable;
	
	public StatistichePdndTracingBean() {
		super();
	}
	
	public StatistichePdndTracingBean(StatistichePdndTracing statistichePdndTracing){
		List<BlackListElement> metodiEsclusi = new ArrayList<>(0);
		metodiEsclusi.add(new BlackListElement("setSoggettoReadable", String.class));
		
		BeanUtils.copy(this, statistichePdndTracing, metodiEsclusi);
	}

	public String getStatoReadable() {
		if(this.getStato() == null)
			return StatisticheCostanti.NON_SELEZIONATO;
		
		if(this.getStato().equals(PossibiliStatiRichieste.FAILED)) { // Failed
			return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_FAILED_LABEL_KEY);
		} else {
			return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PUBLISHED_LABEL_KEY);
		}
		
    }
	
	public String getStatoPdndReadable() {
		if (this.getStatoPdnd() == null)
			return StatisticheCostanti.NON_SELEZIONATO;
		
		switch (this.getStatoPdnd()) {
		case ERROR:
			return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PDND_ERROR_LABEL_KEY);
		case OK:
			return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PDND_OK_LABEL_KEY);
		case PENDING:
			return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PDND_PENDING_LABEL_KEY);
		case WAITING:
			return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PDND_WAITING_LABEL_KEY);
		default:
			break;
		}

		return this.getStatoPdnd().toString();
	}
	
	public boolean isShowStatoPdnd(){
		// visualizzo stato pdnd se e' stato indicato lo stato ed e' published
		return (this.stato != null && this.stato.equals(PossibiliStatiRichieste.PUBLISHED.getValue()));
	}
	
	public void setSoggettoReadable(String soggettoReadable) {
		this.soggettoReadable = soggettoReadable;
	}
	
	public String getSoggettoReadable() {
		if(this.soggettoReadable != null) {
			return this.soggettoReadable;
		}
		
		if (this.getPddCodice() == null)
			return StatisticheCostanti.NON_SELEZIONATO;
		
		return this.getPddCodice();
	}
	
	public String getTentativiPubblicazioneReadable() {
		if (this.getTentativiPubblicazione() == null)
			return StatisticheCostanti.NON_SELEZIONATO;

		return this.getTentativiPubblicazione().toString();
	}
	
	public String getMethodReadable() {
		if (this.getMethod() == null)
			return StatisticheCostanti.NON_SELEZIONATO;

		switch (this.getMethod()) {
		case RECOVER:
			return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_METHOD_RECOVER_LABEL_KEY);
		case REPLACE:
			return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_METHOD_REPLACE_LABEL_KEY);
		case SUBMIT:
			return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_METHOD_SUBMIT_LABEL_KEY);
		default:
			break;
		
		}
		
		return this.getMethod().toString();
	}
	
	public String getErrorDetailsPretty(){
		String toRet = null;
		String errorDetails = this.getErrorDetails();
		if(errorDetails!=null) {
			StringBuilder contenutoDocumentoStringBuilder = new StringBuilder();
			String errore = Utils.getTestoVisualizzabile(errorDetails.getBytes(),contenutoDocumentoStringBuilder, true, DumpUtils.getThreshold_readInMemory());
			if(errore!= null)
				return "";

			// sicuramente da qui si hanno messaggi con "testo visualizzabile" e non troppo lunghi
			JSONUtils jsonUtils = JSONUtils.getInstance(true);
			try {
				toRet = jsonUtils.toString(jsonUtils.getAsNode(errorDetails.getBytes()));
			} catch (UtilsException e) {
				//donothing
			}
		}

		if(toRet == null || "".equals(toRet)) {
			toRet = errorDetails != null ? errorDetails : "";
		}

		return toRet;
	}
	
	public String getBrushErrorDetails() {
		// il brush verrà utilizzato solamente se il messaggio va visualizzato
		// decisione presa dal metodo: isVisualizzaMessaggio()
		
		String toRet = null;
		String errorDetails = this.getErrorDetails();
		if(errorDetails!=null) {
			toRet = "json";
		}

		return toRet!=null ? toRet : "xml";
	}
	
	public String getStatoTooltip() {
		if(this.stato == null) {
			return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PDND_WAITING_LABEL_KEY);
		}
		
		if(this.stato.equals(PossibiliStatiRichieste.FAILED)) { // Failed
			return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_FAILED_LABEL_KEY);
		} else { // published
			switch (this.statoPdnd) {
			case ERROR:
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PDND_ERROR_LABEL_KEY);
			case OK:
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PDND_OK_LABEL_KEY);
			case PENDING:
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PDND_PENDING_LABEL_KEY);
			case WAITING:
			default:
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PDND_WAITING_LABEL_KEY);
			}
		}
	}
	
	public String getColoreIconaStato() {
		if(this.stato == null) {
			return "icona-tracingcsv-grey";
		}
		
		if(this.stato.equals(PossibiliStatiRichieste.FAILED)) { // Failed
			return "icona-tracingcsv-error";
		} else { // published
			switch (this.statoPdnd) {
			case ERROR:
				return "icona-tracingcsv-error";
			case OK:
				return "icona-tracingcsv-ok";
			case PENDING:
				return "icona-tracingcsv-warning";
			case WAITING:
			default:
				return "icona-tracingcsv-grey";
			}
		}
	}
	
	public boolean getFailed() {
		return PossibiliStatiRichieste.FAILED.equals(this.stato);	
	}
}
