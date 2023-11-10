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
package org.openspcoop2.web.ctrlstat.servlet.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.InUsoType;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
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
public class ProprietaOggettoRegistro extends HttpServlet{

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
		try {
			IOUtils.copy(req.getInputStream(), baos);
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la ricerca delle informazioni oggetto: "+e.getMessage(), e);
			return;
		}			

		this.processRequest(req, resp);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) {
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
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(archiviCore);
			SoggettiCore soggettiCore = new SoggettiCore(archiviCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(archiviCore);
			RuoliCore ruoliCore = new RuoliCore(archiviCore);
			ScopeCore scopeCore = new ScopeCore(archiviCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(archiviCore);
			GruppiCore gruppiCore = new GruppiCore(archiviCore);

			String identificativoOggetto = registroHelper.getParameter(UtilsCostanti.PARAMETRO_PROPRIETA_OGGETTO_ID_OGGETTO); 
			String tipoOggetto = registroHelper.getParameter(UtilsCostanti.PARAMETRO_PROPRIETA_OGGETTO_TIPO_OGGETTO); 
			String tipoRisposta = registroHelper.getParameter(UtilsCostanti.PARAMETRO_PROPRIETA_OGGETTO_TIPO_RISPOSTA); 

			InUsoType inUsoType = InUsoType.valueOf(tipoOggetto);
			
			ExporterUtils exporterUtils = new ExporterUtils(archiviCore);
			List<?> identificativi = null; 
			List<String> risultatiRicerca = new ArrayList<>();
			switch (inUsoType) {
			case ACCORDO_SERVIZIO_PARTE_COMUNE:
				identificativi = exporterUtils.getIdsAccordiServizioParteComune(identificativoOggetto);
				for (Object object : identificativi) {
					IDAccordo idAccordo = (IDAccordo)object;
					AccordoServizioParteComuneSintetico as = apcCore.getAccordoServizioSintetico(idAccordo);
					risultatiRicerca.add(this.getProprieta(as.getProprietaOggetto()));
				}
				break;
			case SERVIZIO_APPLICATIVO:
				identificativi = exporterUtils.getIdsServiziApplicativi(identificativoOggetto);
				for (Object object : identificativi) {
					IDServizioApplicativo idServizioApplicativo = (IDServizioApplicativo)object;
					ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);
					risultatiRicerca.add(this.getProprieta(sa.getProprietaOggetto()));
				}
				break;
			case SOGGETTO:
				identificativi = exporterUtils.getIdsSoggetti(identificativoOggetto);
				for (Object object : identificativi) {
					IDSoggetto idSoggetto = (IDSoggetto)object;
					Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggetto);
					risultatiRicerca.add(this.getProprieta(soggetto.getProprietaOggetto()));
				}
				break;
			case RUOLO:
				identificativi = exporterUtils.getIdsRuoli(identificativoOggetto);
				for (Object object : identificativi) {
					IDRuolo idRuolo = (IDRuolo)object;
					Ruolo ruolo = ruoliCore.getRuolo(idRuolo.getNome());
					risultatiRicerca.add(this.getProprieta(ruolo.getProprietaOggetto()));
				}
				break;
			case SCOPE:
				identificativi = exporterUtils.getIdsScope(identificativoOggetto);
				for (Object object : identificativi) {
					IDScope idScope = (IDScope)object;
					Scope scope = scopeCore.getScope(idScope.getNome());
					risultatiRicerca.add(this.getProprieta(scope.getProprietaOggetto()));
				}
				break;
			case GRUPPO:
				identificativi = exporterUtils.getIdsGruppi(identificativoOggetto);
				for (Object object : identificativi) {
					IDGruppo idGruppo = (IDGruppo)object;
					Gruppo gruppo = gruppiCore.getGruppo(idGruppo.getNome());
					risultatiRicerca.add(this.getProprieta(gruppo.getProprietaOggetto()));
				}
				break;
			case TOKEN_POLICY:
				identificativi = exporterUtils.getIdsTokenPolicy(identificativoOggetto);
				for (Object object : identificativi) {
					IDGenericProperties idGP = (IDGenericProperties)object;
					risultatiRicerca.add("TODO");
				}
				break;
			case ATTRIBUTE_AUTHORITY:
				identificativi = exporterUtils.getIdsAttributeAuthority(identificativoOggetto);
				for (Object object : identificativi) {
					IDGenericProperties idGP = (IDGenericProperties)object;
					risultatiRicerca.add("TODO");
				}
				break;

			case EROGAZIONE:{
				String uriAPSerogata = identificativoOggetto;
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromUri(uriAPSerogata);
				AccordoServizioParteSpecifica as = apsCore.getAccordoServizioParteSpecifica(idServizio);
				risultatiRicerca.add(this.getProprieta(as.getProprietaOggetto()));
				break;
			}
			case FRUIZIONE:{
				String uriAPSfruita = identificativoOggetto;
				if(uriAPSfruita.contains("@")) {
					String tipoNomeFruitore = uriAPSfruita.split("@")[1];
					uriAPSfruita = uriAPSfruita.split("@")[0];
					IDSoggetto idSoggettoFruitore = new IDSoggetto(tipoNomeFruitore.split("/")[0], tipoNomeFruitore.split("/")[1]);
					IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromUri(uriAPSfruita);
					AccordoServizioParteSpecifica as = apsCore.getAccordoServizioParteSpecifica(idServizio);
					for (Fruitore fruitore : as.getFruitoreList()) {
						if(idSoggettoFruitore.getTipo().equals(fruitore.getTipo()) && idSoggettoFruitore.getNome().equals(fruitore.getNome())) {
							risultatiRicerca.add(this.getProprieta(fruitore.getProprietaOggetto()));
							break;
						}
					}
				}
				else {
					risultatiRicerca.add("Internal Error: informazione fruitore non presente");
				}
				break;
			}
			case RISORSA:
			case PORT_TYPE:
			case OPERAZIONE:
			case CANALE:
			case ACCORDO_COOPERAZIONE:
			case ACCORDO_SERVIZIO_COMPOSTO:
			case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
			case EROGAZIONE_INFO:
			case FRUITORE:
			case FRUIZIONE_INFO:
			case PDD:
			case PORTA_APPLICATIVA:
			case PORTA_DELEGATA:
				throw new CoreException("TipoOggetto non gestito.");
			}

			ServletOutputStream outputStream = response.getOutputStream();
			
			if(tipoRisposta.equalsIgnoreCase(UtilsCostanti.VALUE_PARAMETRO_PROPRIETA_OGGETTO_TIPO_RISPOSTA_JSON)) {
				response.setContentType(HttpConstants.CONTENT_TYPE_JSON);
				JSONUtils jsonUtils = JSONUtils.getInstance(true);
				Map<String, Object> mapResult = new HashMap<>();
				mapResult.put(UtilsCostanti.KEY_JSON_RISPOSTA_USO, risultatiRicerca);
				jsonUtils.writeTo(mapResult, outputStream);
			} else if(tipoRisposta.equalsIgnoreCase(UtilsCostanti.VALUE_PARAMETRO_PROPRIETA_OGGETTO_TIPO_RISPOSTA_TEXT)) {
				response.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
				outputStream.write(StringUtils.join(risultatiRicerca.toArray(new String[1])).getBytes());
			} else {
				throw new CoreException("TipoRiposta non gestito.");
			}
			

		}catch(Exception e){
			ControlStationCore.logError("Errore durante la ricerca delle informazioni oggetto: "+e.getMessage(), e);
		}
	}
	
	private String getProprieta(org.openspcoop2.core.registry.beans.ProprietaOggettoSintetico p) {
		return getProprieta(p.getUtenteRichiedente(), p.getDataCreazione(),
				p.getUtenteUltimaModifica(), p.getDataUltimaModifica());
	}
	private String getProprieta(org.openspcoop2.core.registry.ProprietaOggetto p) {
		return getProprieta(p.getUtenteRichiedente(), p.getDataCreazione(),
				p.getUtenteUltimaModifica(), p.getDataUltimaModifica());
	}
	private String getProprieta(org.openspcoop2.core.config.ProprietaOggetto p) {
		return getProprieta(p.getUtenteRichiedente(), p.getDataCreazione(),
				p.getUtenteUltimaModifica(), p.getDataUltimaModifica());
	}
	private String getProprieta(String utenteRichiedente, Date dataCreazione,
			String utenteUtimaModifica, Date dataUtimaModifica) {
		StringBuilder sb = new StringBuilder();
		if(dataCreazione!=null) {
			String dataMs = CostantiControlStation.formatDateMs(dataCreazione);
			sb.append(CostantiControlStation.LABEL_DATA_CREAZIONE).append(": ").append(dataMs);
		}
		if(utenteRichiedente!=null) {
			sb.append(CostantiControlStation.LABEL_UTENTE_RICHIEDENTE).append(": ").append(utenteRichiedente);
		}
		if(dataUtimaModifica!=null) {
			String dataMs = CostantiControlStation.formatDateMs(dataUtimaModifica);
			sb.append(CostantiControlStation.LABEL_DATA_ULTIMA_MODIFICA).append(": ").append(dataMs);
		}
		if(utenteUtimaModifica!=null) {
			sb.append(CostantiControlStation.LABEL_UTENTE_ULTIMA_MODIFICA).append(": ").append(utenteUtimaModifica);
		}
		if(sb.length()<=0) {
			sb.append("Nessuna proprietà disponibile");
		}
		return sb.toString();
	}
}
