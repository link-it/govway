/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.web.ctrlstat.servlet.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.InUsoType;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * VerificaCertificati
 *
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class VerificaCertificati extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			IOUtils.copy(req.getInputStream(), baos);
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la ricerca delle informazioni oggetto: "+e.getMessage(), e);
			return;
		}			

		this.processRequest(req, resp);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) {
		String risposta = "";
		String messaggioEsito = null;
		String messageType = null;
		response.setContentType(HttpConstants.CONTENT_TYPE_JSON);


		try(ByteArrayOutputStream baosPayload = new ByteArrayOutputStream();){
			HttpRequestMethod httpRequestMethod = HttpRequestMethod.valueOf(request.getMethod().toUpperCase()); 

			if(httpRequestMethod.equals(HttpRequestMethod.POST)) { // copia del payload
				IOUtils.copy(request.getInputStream(), baosPayload);
			}

			HttpSession session = request.getSession(true);
			PageData pd = new PageData();
			UtilsHelper registroHelper = new UtilsHelper(request, pd, session);

			String tipoOggetto = registroHelper.getParameter(UtilsCostanti.PARAMETRO_VERIFICA_CERTIFICATI_TIPO_OGGETTO); 

			InUsoType inUsoType = InUsoType.valueOf(tipoOggetto);

			switch (inUsoType) {
			case EROGAZIONE:
			case FRUIZIONE:
				ErogazioniHelper apsHelper = new ErogazioniHelper(request, pd, session);
				
				AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
				SoggettiCore soggettiCore = new SoggettiCore(apsCore);
				
				String verificaCertificatiFromLista = apsHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA);
				boolean arrivoDaLista = "true".equalsIgnoreCase(verificaCertificatiFromLista);
				
				boolean soloModI = false;
				if(!arrivoDaLista) {
					String par = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO);
					soloModI = "true".equalsIgnoreCase(par);
				}
							
				String id = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
				long idInt  = Long.parseLong(id);
				AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idInt);
				
				String tipoSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE);
				String nomeSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE);
				IDSoggetto idSoggettoFruitore = null;
				if(tipoSoggettoFruitore!=null && !"".equals(tipoSoggettoFruitore) &&
						nomeSoggettoFruitore!=null && !"".equals(nomeSoggettoFruitore)) {
					idSoggettoFruitore = new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore);
				}
				
				String alias = apsHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
				
				String tipologia = ServletUtils.getObjectFromSession(request, session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
				boolean gestioneFruitori = false;
				boolean gestioneErogatori = false;
				if(tipologia!=null) {
					if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
						gestioneFruitori = true;
					}
					else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
						gestioneErogatori = true;
					}
				}
				
				Fruitore fruitore = null;
				if(gestioneFruitori) {
					// In questa modalità ci deve essere un fruitore indirizzato
					for (Fruitore check : asps.getFruitoreList()) {
						if(check.getTipo().equals(idSoggettoFruitore.getTipo()) && check.getNome().equals(idSoggettoFruitore.getNome())) {
							fruitore = check;
							break;
						}
					}
				}
				
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
				String tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore());
				List<DataElement> dati = new ArrayList<>();
				
				// Esecuzione delle operazioni di verifica
				boolean verificaCertificatiEffettuata = apsHelper.eseguiVerificaCertificati(soloModI, asps, idSoggettoFruitore, alias, gestioneFruitori,
						gestioneErogatori, fruitore, idServizio, tipoProtocollo, dati);
				
				if(verificaCertificatiEffettuata) {
					ControlStationCore.logInfo("Verifica dei certificati completata.");
					messaggioEsito = pd.getMessage();
					messageType = pd.getMessageType();
				} else {
					ControlStationCore.logInfo("Verifica dei certificati non completata.");
					messaggioEsito = UtilsCostanti.MESSAGGIO_INFORMATIVO_VERIFICA_CERTIFICATI_NON_ESEGUITA;
					messageType = MessageType.INFO.toString();
				}
				
				break;
			default:
				throw new CoreException("TipoOggetto non gestito.");
			}

		}catch(Exception e){
			ControlStationCore.logError("Errore durante la decodifica: "+e.getMessage(), e);
			messaggioEsito = UtilsCostanti.MESSAGGIO_ERRORE_VERIFICA_CERTIFICATI;
			messageType = MessageType.ERROR.toString();
		} finally {
			risposta = ServletUtils.getJson(ServletUtils.getJsonPair(UtilsCostanti.KEY_ESITO, messageType), ServletUtils.getJsonPair(UtilsCostanti.KEY_DETTAGLIO_ESITO, messaggioEsito));
			try {
				ServletOutputStream outputStream = response.getOutputStream();
				outputStream.write(risposta.getBytes());
			}catch(Exception eErr){
				ControlStationCore.logError("Errore durante la serializzazione dell'errore di decodifica: "+eErr.getMessage(), eErr);
			}
		}
	
	}
}