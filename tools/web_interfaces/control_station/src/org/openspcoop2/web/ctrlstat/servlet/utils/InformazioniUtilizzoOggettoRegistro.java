/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.InUsoType;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCore;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.PageData;

/**
 * InformazioniUtilizzoOggettoRegistro
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class InformazioniUtilizzoOggettoRegistro extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(req.getInputStream(), baos);

		this.processRequest(req, resp);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try(ByteArrayOutputStream baosPayload = new ByteArrayOutputStream();){
			HttpRequestMethod httpRequestMethod = HttpRequestMethod.valueOf(request.getMethod().toUpperCase()); 

			if(httpRequestMethod.equals(HttpRequestMethod.POST)) { // copia del payload
				IOUtils.copy(request.getInputStream(), baosPayload);
			}

			HttpSession session = request.getSession(true);
			PageData pd = new PageData();
			UtilsHelper registroHelper = new UtilsHelper(request, pd, session);
			ArchiviCore archiviCore = new ArchiviCore();
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(archiviCore);
			SoggettiCore soggettiCore = new SoggettiCore(archiviCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(archiviCore);
			RuoliCore ruoliCore = new RuoliCore(archiviCore);
			ScopeCore scopeCore = new ScopeCore(archiviCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(archiviCore);
			GruppiCore gruppiCore = new GruppiCore(archiviCore);

			String identificativoOggetto = registroHelper.getParameter(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_ID_OGGETTO); 
			String tipoOggetto = registroHelper.getParameter(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_OGGETTO); 
			String tipoRisposta = registroHelper.getParameter(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA); 

			InUsoType inUsoType = InUsoType.valueOf(tipoOggetto);
			
			ExporterUtils exporterUtils = new ExporterUtils(archiviCore);
			List<?> identificativi = null; 
			List<String> risultatiRicerca = new ArrayList<String>();
			switch (inUsoType) {
			case ACCORDO_SERVIZIO_PARTE_COMUNE:
				identificativi = exporterUtils.getIdsAccordiServizioParteComune(identificativoOggetto);
				for (Object object : identificativi) {
					IDAccordo idAccordo = (IDAccordo)object;
					risultatiRicerca.add(apcCore.getDettagliAccordoInUso(idAccordo));
				}
				break;
			case RISORSA:
				identificativi = exporterUtils.getIdsAccordiServizioParteComuneRisorsa(identificativoOggetto);
				for (Object object : identificativi) {
					IDResource idRisorsa = (IDResource)object;
					risultatiRicerca.add(apcCore.getDettagliRisorsaInUso(idRisorsa));
				}
				break;
			case PORT_TYPE:
				identificativi = exporterUtils.getIdsAccordiServizioParteComunePortType(identificativoOggetto);
				for (Object object : identificativi) {
					IDPortType idPT = (IDPortType)object;
					risultatiRicerca.add(apcCore.getDettagliPortTypeInUso(idPT));
				}
				break;
			case OPERAZIONE:
				identificativi = exporterUtils.getIdsAccordiServizioParteComuneOperazione(identificativoOggetto);
				for (Object object : identificativi) {
					IDPortTypeAzione idOperazione = (IDPortTypeAzione)object;
					risultatiRicerca.add(apcCore.getDettagliOperazioneInUso(idOperazione));
				}
				break;
			case SERVIZIO_APPLICATIVO:
				identificativi = exporterUtils.getIdsServiziApplicativi(identificativoOggetto);
				for (Object object : identificativi) {
					IDServizioApplicativo idServizioApplicativo = (IDServizioApplicativo)object;
					risultatiRicerca.add(saCore.getDettagliServizioApplicativoInUso(idServizioApplicativo));
				}
				break;
			case SOGGETTO:
				identificativi = exporterUtils.getIdsSoggetti(identificativoOggetto);
				for (Object object : identificativi) {
					IDSoggetto idSoggetto = (IDSoggetto)object;
					Soggetto soggetto = new Soggetto();
					soggetto.setTipo(idSoggetto.getTipo());
					soggetto.setNome(idSoggetto.getNome());
					soggetto.setIdentificativoPorta(idSoggetto.getCodicePorta());
					risultatiRicerca.add(soggettiCore.getDettagliSoggettoInUso(soggetto));
				}
				break;
			case RUOLO:
				identificativi = exporterUtils.getIdsRuoli(identificativoOggetto);
				for (Object object : identificativi) {
					IDRuolo idRuolo = (IDRuolo)object;
					risultatiRicerca.add(ruoliCore.getDettagliRuoloInUso(idRuolo));
				}
				break;
			case SCOPE:
				identificativi = exporterUtils.getIdsScope(identificativoOggetto);
				for (Object object : identificativi) {
					IDScope idScope = (IDScope)object;
					risultatiRicerca.add(scopeCore.getDettagliScopeInUso(idScope));
				}
				break;
			case CANALE:
				identificativi = exporterUtils.getIdsCanali(identificativoOggetto);
				for (Object object : identificativi) {
					CanaleConfigurazione canale = (CanaleConfigurazione)object;
					risultatiRicerca.add(confCore.getDettagliCanaleInUso(canale));
				}
				break;
			case GRUPPO:
				identificativi = exporterUtils.getIdsGruppi(identificativoOggetto);
				for (Object object : identificativi) {
					IDGruppo idGruppo = (IDGruppo)object;
					risultatiRicerca.add(gruppiCore.getDettagliGruppoInUso(idGruppo));
				}
				break;
			case TOKEN_POLICY:
				identificativi = exporterUtils.getIdsTokenPolicy(identificativoOggetto);
				for (Object object : identificativi) {
					IDGenericProperties idGP = (IDGenericProperties)object;
					risultatiRicerca.add(confCore.getDettagliTokenPolicyInUso(idGP));
				}
				break;
			case ACCORDO_COOPERAZIONE:
			case ACCORDO_SERVIZIO_COMPOSTO:
			case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
			case EROGAZIONE:
			case FRUITORE:
			case FRUIZIONE:
			case PDD:
			case PORTA_APPLICATIVA:
			case PORTA_DELEGATA:
				throw new Exception("TipoOggetto non gestito.");
			}

			ServletOutputStream outputStream = response.getOutputStream();
			
			if(tipoRisposta.equalsIgnoreCase(UtilsCostanti.VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_JSON)) {
				response.setContentType(HttpConstants.CONTENT_TYPE_JSON);
				JSONUtils jsonUtils = JSONUtils.getInstance(true);
				Map<String, Object> mapResult = new HashMap<>();
				mapResult.put(UtilsCostanti.KEY_JSON_RISPOSTA_USO, risultatiRicerca);
				jsonUtils.writeTo(mapResult, outputStream);
			} else if(tipoRisposta.equalsIgnoreCase(UtilsCostanti.VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_TEXT)) {
				response.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
				outputStream.write(StringUtils.join(risultatiRicerca.toArray(new String[1])).getBytes());
			} else {
				throw new Exception("TipoRiposta non gestito.");
			}
			

		}catch(Exception e){
			ControlStationCore.logError("Errore durante la ricerca delle informazioni oggetto: "+e.getMessage(), e);
			throw new ServletException(e);
		}
	}
}
