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

package org.openspcoop2.web.ctrlstat.servlet;

import java.text.MessageFormat;

import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;

/**
 * ConsoleUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConsoleUtilities {

	public static boolean alreadyExistsCorrelazioneApplicativaRichiesta(PorteDelegateCore porteDelegateCore,
			long idPorta, String elemento, long idCorrelazione,
			StringBuilder existsMessage) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		return _alreadyExistsCorrelazioneApplicativaRichiesta(true, porteDelegateCore, null, idPorta, elemento, idCorrelazione, existsMessage);
	}
	public static boolean alreadyExistsCorrelazioneApplicativaRichiesta(PorteApplicativeCore porteApplicativeCore,
			long idPorta, String elemento, long idCorrelazione,
			StringBuilder existsMessage) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		return _alreadyExistsCorrelazioneApplicativaRichiesta(false, null, porteApplicativeCore, idPorta, elemento, idCorrelazione, existsMessage);
	}
	private static boolean _alreadyExistsCorrelazioneApplicativaRichiesta(boolean portaDelegata,
			PorteDelegateCore porteDelegateCore, PorteApplicativeCore porteApplicativeCore,
			long idPorta, String elemento, long idCorrelazione,
			StringBuilder existsMessage) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		// Controllo che non esistano altre correlazioni applicative con gli
		// stessi dati
		boolean giaRegistrato = false;

		long idCorrApp = 0;
		CorrelazioneApplicativa ca = null;
		String nomePorta = null;
		if(portaDelegata){
			PortaDelegata pde = null;
			pde = porteDelegateCore.getPortaDelegata(idPorta);
			ca = pde.getCorrelazioneApplicativa();
			nomePorta = pde.getNome();
		}else{
			PortaApplicativa pda = null;
			pda = porteApplicativeCore.getPortaApplicativa(idPorta);
			ca = pda.getCorrelazioneApplicativa();
			nomePorta = pda.getNome();
		}
		if (ca != null) {
			for (int i = 0; i < ca.sizeElementoList(); i++) {
				CorrelazioneApplicativaElemento cae = ca.getElemento(i);
				String caeNome = cae.getNome();
				if (caeNome == null)
					caeNome = "";
				if (elemento.equals(caeNome) || ("*".equals(caeNome) && "".equals(elemento))) {
					idCorrApp = cae.getId().longValue();
					break;
				}
			}
		}

		if ((idCorrApp != 0) && (idCorrApp != idCorrelazione)) {
			giaRegistrato = true;
		}

		if (giaRegistrato) {
			String nomeElemento = CostantiControlStation.LABEL_NON_DEFINITO;
			if(elemento!=null && ("".equals(elemento)==false))
				nomeElemento = elemento;
			String labelPorta = null;
			if(portaDelegata)
				labelPorta = MessageFormat.format(CostantiControlStation.LABEL_PORTA_DELEGATA_CON_PARAMETRI, nomePorta);
			else
				labelPorta = MessageFormat.format(CostantiControlStation.LABEL_PORTA_APPLICATIVA_CON_PARAMETRI, nomePorta);
			existsMessage.append(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORRELAZIONE_APPLICATIVA_CON_ELEMENTO_XML_DEFINITA_GIA_ESISTENTE,	nomeElemento, labelPorta));
		}
		
		return giaRegistrato;
	}
	
	
	
	public static boolean alreadyExistsCorrelazioneApplicativaRisposta(PorteDelegateCore porteDelegateCore, 
			long idPorta, String elemento, long idCorrelazione,
			StringBuilder existsMessage) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		return _alreadyExistsCorrelazioneApplicativaRisposta(true, porteDelegateCore, null, idPorta, elemento, idCorrelazione, existsMessage);
	}
	public static boolean alreadyExistsCorrelazioneApplicativaRisposta(PorteApplicativeCore porteApplicativeCore, 
			long idPorta, String elemento, long idCorrelazione,
			StringBuilder existsMessage) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		return _alreadyExistsCorrelazioneApplicativaRisposta(false, null, porteApplicativeCore, idPorta, elemento, idCorrelazione, existsMessage);
	}
	private static boolean _alreadyExistsCorrelazioneApplicativaRisposta(boolean portaDelegata,
			PorteDelegateCore porteDelegateCore, PorteApplicativeCore porteApplicativeCore,
			long idPorta, String elemento, long idCorrelazione,
			StringBuilder existsMessage) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		// Controllo che non esistano altre correlazioni applicative con gli
		// stessi dati
		boolean giaRegistrato = false;

		int idCorrApp = 0;
		CorrelazioneApplicativaRisposta ca = null;
		String nomePorta = null;
		if(portaDelegata){
			PortaDelegata pde = null;
			pde = porteDelegateCore.getPortaDelegata(idPorta);
			ca = pde.getCorrelazioneApplicativaRisposta();
			nomePorta = pde.getNome();
		}else{
			PortaApplicativa pda = null;
			pda = porteApplicativeCore.getPortaApplicativa(idPorta);
			ca = pda.getCorrelazioneApplicativaRisposta();
			nomePorta = pda.getNome();
		}
		if (ca != null) {
			for (int i = 0; i < ca.sizeElementoList(); i++) {
				CorrelazioneApplicativaRispostaElemento cae = ca.getElemento(i);
				String caeNome = cae.getNome();
				if (caeNome == null)
					caeNome = "";
				if (elemento.equals(caeNome) || ("*".equals(caeNome) && "".equals(elemento))) {
					idCorrApp = cae.getId().intValue();
					break;
				}
			}
		}

		if ((idCorrApp != 0) && (idCorrApp != idCorrelazione)) {
			giaRegistrato = true;
		}

		if (giaRegistrato) {
			String nomeElemento = CostantiControlStation.LABEL_NON_DEFINITO;
			if(elemento!=null && ("".equals(elemento)==false))
				nomeElemento = elemento;
			String labelPorta = null;
			if(portaDelegata)
				labelPorta = MessageFormat.format(CostantiControlStation.LABEL_PORTA_DELEGATA_CON_PARAMETRI, nomePorta);
			else
				labelPorta = MessageFormat.format(CostantiControlStation.LABEL_PORTA_APPLICATIVA_CON_PARAMETRI, nomePorta);
			existsMessage.append(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORRELAZIONE_APPLICATIVA_PER_LA_RISPOSTA_CON_ELEMENTO_DEFINITA_GIA_ESISTENTE,	nomeElemento, labelPorta));
		}

		return giaRegistrato;
		
	}
}
