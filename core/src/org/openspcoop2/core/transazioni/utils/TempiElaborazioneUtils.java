/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.utils.date.DateUtils;


/**     
 * TempiElaborazioneUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TempiElaborazioneUtils implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String format = "yyyyMMddHHmmssSSS";
	private static final String FUNZIONALITA_SEPARATOR = "-";
	private static final String FUNZIONALITA_DATE_UNDEFINED = "0";
	private static final String TEMPI_SEPARATOR = " ";

	/*
	 * NOTA: Spazio massimo occupato.
	 * Ogni informazione occupa  'yyyyMMddHHmmssSSS' + '-' + 'yyyyMMddHHmmssSSS' + ' ' = 36
	 * Per ora siamo a 33 informazioni raccolte (token, autenticazione, ...), quindi 33*36=1188.
	 * Attenzione a non superare 4000 che è la larghezza massima.
	 * 
	 **/
	
	private static String _convertToDBValue(TempiElaborazioneFunzionalita tempiElaborazioneFunzionalita) {
		SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter(format);
		StringBuilder bf = new StringBuilder("");
		if(tempiElaborazioneFunzionalita!=null && tempiElaborazioneFunzionalita.dataIngresso!=null) {
			bf.append(dateformat.format(tempiElaborazioneFunzionalita.dataIngresso));
		}
		else {
			bf.append(FUNZIONALITA_DATE_UNDEFINED);
		}
		bf.append(FUNZIONALITA_SEPARATOR);
		if(tempiElaborazioneFunzionalita!=null && tempiElaborazioneFunzionalita.dataUscita!=null) {
			bf.append(dateformat.format(tempiElaborazioneFunzionalita.dataUscita));
		}
		else {
			bf.append(FUNZIONALITA_DATE_UNDEFINED);
		}
		return bf.toString();
	}
	
	public static String convertToDBValue(TempiElaborazione tempiElaborazione) {
		
		StringBuilder bf = new StringBuilder();
		
		bf.append(_convertToDBValue(tempiElaborazione.token)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.autenticazione)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.autenticazioneToken)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.autorizzazione)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.autorizzazioneContenuti)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.validazioneRichiesta)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.validazioneRisposta)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.controlloTraffico_maxRequests)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.controlloTraffico_rateLimiting)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.sicurezzaMessaggioRichiesta)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.sicurezzaMessaggioRisposta)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.gestioneAttachmentsRichiesta)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.gestioneAttachmentsRisposta)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.correlazioneApplicativaRichiesta)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.correlazioneApplicativaRisposta)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.tracciamentoRichiesta)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.tracciamentoRisposta)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.dumpRichiestaIngresso)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.dumpRichiestaUscita)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.dumpRispostaIngresso)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.dumpRispostaUscita)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.dumpBinarioRichiestaIngresso)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.dumpBinarioRichiestaUscita)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.dumpBinarioRispostaIngresso)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.dumpBinarioRispostaUscita)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.dumpIntegrationManager)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.responseCachingCalcoloDigest)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.responseCachingReadFromCache)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.responseCachingSaveInCache)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.trasformazioneRichiesta)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.trasformazioneRisposta)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.attributeAuthority)).append(TEMPI_SEPARATOR);
		bf.append(_convertToDBValue(tempiElaborazione.autenticazioneApplicativoToken)).append(TEMPI_SEPARATOR);
		
		return bf.toString();
	}
	
	private static TempiElaborazioneFunzionalita _convertFromDBValue(String dbValue) throws CoreException {
		if(dbValue==null || "".equals(dbValue)) {
			return null;
		}
		SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter(format);
		String [] date = dbValue.split(FUNZIONALITA_SEPARATOR);
		if(date==null || date.length!=2) {
			return null;
		}
		String dataInizio = date[0];
		String dataFine = date[1];
		TempiElaborazioneFunzionalita tempi = null;
		try {
			if(!FUNZIONALITA_DATE_UNDEFINED.equals(dataInizio)) {
				Date d = dateformat.parse(dataInizio);
				if(tempi==null) {
					tempi = new TempiElaborazioneFunzionalita();
				}
				tempi.setDataIngresso(d);
			}
			if(!FUNZIONALITA_DATE_UNDEFINED.equals(dataFine)) {
				Date d = dateformat.parse(dataFine);
				if(tempi==null) {
					tempi = new TempiElaborazioneFunzionalita();
				}
				tempi.setDataUscita(d);
			}
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
		return tempi;
	}
	
	public static TempiElaborazione convertFromDBValue(String dbValue) throws CoreException {
		
		if(dbValue==null || "".equals(dbValue)) {
			return null;
		}
		
		String [] tempi = dbValue.split(TEMPI_SEPARATOR);
		if(tempi==null || tempi.length<=0) {
			return null;
		}
		
		TempiElaborazione tempiElaborazione = null;
		
		// Gestiamo per incremento senza mettere paletti sui valori attesi, in modo da supportare eventuali informazioni future rimanendo compatibili all'indietro
		// NOTA!!: Nuove informazioni aggiungerle cmq in fondo anche nel metodo sopra 'convertToDBValue'
		for (int i = 0; i < tempi.length; i++) {
		
			String tempo = tempi[i];
			TempiElaborazioneFunzionalita funzionalita = _convertFromDBValue(tempo);
			if(funzionalita==null) {
				continue;
			}
			if(funzionalita!=null && tempiElaborazione==null) {
				tempiElaborazione = new TempiElaborazione();
			}
						
			if(i==0) {
				tempiElaborazione.token = funzionalita;
			}
			else if(i==1) {
				tempiElaborazione.autenticazione = funzionalita;
			}
			else if(i==2) {
				tempiElaborazione.autenticazioneToken = funzionalita;
			}
			else if(i==3) {
				tempiElaborazione.autorizzazione = funzionalita;
			}
			else if(i==4) {
				tempiElaborazione.autorizzazioneContenuti = funzionalita;
			}
			else if(i==5) {
				tempiElaborazione.validazioneRichiesta = funzionalita;
			}
			else if(i==6) {
				tempiElaborazione.validazioneRisposta = funzionalita;
			}
			else if(i==7) {
				tempiElaborazione.controlloTraffico_maxRequests = funzionalita;
			}
			else if(i==8) {
				tempiElaborazione.controlloTraffico_rateLimiting = funzionalita;
			}
			else if(i==9) {
				tempiElaborazione.sicurezzaMessaggioRichiesta = funzionalita;
			}
			else if(i==10) {
				tempiElaborazione.sicurezzaMessaggioRisposta = funzionalita;
			}
			else if(i==11) {
				tempiElaborazione.gestioneAttachmentsRichiesta = funzionalita;
			}
			else if(i==12) {
				tempiElaborazione.gestioneAttachmentsRisposta = funzionalita;
			}
			else if(i==13) {
				tempiElaborazione.correlazioneApplicativaRichiesta = funzionalita;
			}
			else if(i==14) {
				tempiElaborazione.correlazioneApplicativaRisposta = funzionalita;
			}
			else if(i==15) {
				tempiElaborazione.tracciamentoRichiesta = funzionalita;
			}
			else if(i==16) {
				tempiElaborazione.tracciamentoRisposta = funzionalita;
			}
			else if(i==17) {
				tempiElaborazione.dumpRichiestaIngresso = funzionalita;
			}
			else if(i==18) {
				tempiElaborazione.dumpRichiestaUscita = funzionalita;
			}
			else if(i==19) {
				tempiElaborazione.dumpRispostaIngresso = funzionalita;
			}
			else if(i==20) {
				tempiElaborazione.dumpRispostaUscita = funzionalita;
			}
			else if(i==21) {
				tempiElaborazione.dumpBinarioRichiestaIngresso = funzionalita;
			}
			else if(i==22) {
				tempiElaborazione.dumpBinarioRichiestaUscita = funzionalita;
			}
			else if(i==23) {
				tempiElaborazione.dumpBinarioRispostaIngresso = funzionalita;
			}
			else if(i==24) {
				tempiElaborazione.dumpBinarioRispostaUscita = funzionalita;
			}
			else if(i==25) {
				tempiElaborazione.dumpIntegrationManager = funzionalita;
			}
			else if(i==26) {
				tempiElaborazione.responseCachingCalcoloDigest = funzionalita;
			}
			else if(i==27) {
				tempiElaborazione.responseCachingReadFromCache = funzionalita;
			}
			else if(i==28) {
				tempiElaborazione.responseCachingSaveInCache = funzionalita;
			}
			else if(i==29) {
				tempiElaborazione.trasformazioneRichiesta = funzionalita;
			}
			else if(i==30) {
				tempiElaborazione.trasformazioneRisposta = funzionalita;
			}
			else if(i==31) {
				tempiElaborazione.attributeAuthority = funzionalita;
			}
			else if(i==32) {
				tempiElaborazione.autenticazioneApplicativoToken = funzionalita;
			}

		}
		
		return tempiElaborazione;
	}
	
}