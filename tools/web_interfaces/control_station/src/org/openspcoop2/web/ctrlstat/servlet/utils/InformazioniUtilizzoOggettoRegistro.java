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
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.PageData;

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
			SoggettiCore soggettiCore = new SoggettiCore(archiviCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(archiviCore);
			RuoliCore ruoliCore = new RuoliCore(archiviCore);
			ScopeCore scopeCore = new ScopeCore(archiviCore);

			String identificativoOggetto = registroHelper.getParameter(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_ID_OGGETTO); 
			String tipoOggetto = registroHelper.getParameter(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_OGGETTO); 
			String tipoRisposta = registroHelper.getParameter(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA); 

			ArchiveType archiveType = ArchiveType.valueOf(tipoOggetto);
			
			ExporterUtils exporterUtils = new ExporterUtils(archiviCore);
			List<?> identificativi = null; 
			List<String> risultatiRicerca = new ArrayList<String>();
			switch (archiveType) {
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
			case ACCORDO_COOPERAZIONE:
			case ACCORDO_SERVIZIO_COMPOSTO:
			case ACCORDO_SERVIZIO_PARTE_COMUNE:
			case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
			case ALL:
			case ALL_WITHOUT_CONFIGURAZIONE:
			case CONFIGURAZIONE:
			case EROGAZIONE:
			case FRUITORE:
			case FRUIZIONE:
			case GRUPPO:
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
