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
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.ProprietaOggetto;
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
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
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
			PorteApplicativeCore paCore = new PorteApplicativeCore(archiviCore);
			PorteDelegateCore pdCore = new PorteDelegateCore(archiviCore);

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
					risultatiRicerca.add(this.getProprieta(as.getProprietaOggetto(), as.getDescrizione()));
				}
				break;
			case SERVIZIO_APPLICATIVO:
				identificativi = exporterUtils.getIdsServiziApplicativi(identificativoOggetto);
				for (Object object : identificativi) {
					IDServizioApplicativo idServizioApplicativo = (IDServizioApplicativo)object;
					ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);
					risultatiRicerca.add(this.getProprieta(sa.getProprietaOggetto(), sa.getDescrizione()));
				}
				break;
			case SOGGETTO:
				identificativi = exporterUtils.getIdsSoggetti(identificativoOggetto);
				for (Object object : identificativi) {
					IDSoggetto idSoggetto = (IDSoggetto)object;
					Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggetto);
					risultatiRicerca.add(this.getProprieta(soggetto.getProprietaOggetto(), soggetto.getDescrizione()));
				}
				break;
			case RUOLO:
				identificativi = exporterUtils.getIdsRuoli(identificativoOggetto);
				for (Object object : identificativi) {
					IDRuolo idRuolo = (IDRuolo)object;
					Ruolo ruolo = ruoliCore.getRuolo(idRuolo.getNome());
					risultatiRicerca.add(this.getProprieta(ruolo.getProprietaOggetto(), ruolo.getDescrizione()));
				}
				break;
			case SCOPE:
				identificativi = exporterUtils.getIdsScope(identificativoOggetto);
				for (Object object : identificativi) {
					IDScope idScope = (IDScope)object;
					Scope scope = scopeCore.getScope(idScope.getNome());
					risultatiRicerca.add(this.getProprieta(scope.getProprietaOggetto(), scope.getDescrizione()));
				}
				break;
			case GRUPPO:
				identificativi = exporterUtils.getIdsGruppi(identificativoOggetto);
				for (Object object : identificativi) {
					IDGruppo idGruppo = (IDGruppo)object;
					Gruppo gruppo = gruppiCore.getGruppo(idGruppo.getNome());
					risultatiRicerca.add(this.getProprieta(gruppo.getProprietaOggetto(), gruppo.getDescrizione()));
				}
				break;
			case TOKEN_POLICY:
				identificativi = exporterUtils.getIdsTokenPolicy(identificativoOggetto);
				for (Object object : identificativi) {
					IDGenericProperties idGP = (IDGenericProperties)object;
					GenericProperties gp = confCore.getGenericProperties(idGP.getNome(), idGP.getTipologia(), false);
					risultatiRicerca.add(this.getProprieta(gp.getProprietaOggetto(), gp.getDescrizione()));
				}
				break;
			case ATTRIBUTE_AUTHORITY:
				identificativi = exporterUtils.getIdsAttributeAuthority(identificativoOggetto);
				for (Object object : identificativi) {
					IDGenericProperties idGP = (IDGenericProperties)object;
					GenericProperties gp = confCore.getGenericProperties(idGP.getNome(), idGP.getTipologia(), false);
					risultatiRicerca.add(this.getProprieta(gp.getProprietaOggetto(), gp.getDescrizione()));
				}
				break;

			case EROGAZIONE:{
				String uriAPSerogata = identificativoOggetto;
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromUri(uriAPSerogata);
				AccordoServizioParteSpecifica as = apsCore.getAccordoServizioParteSpecifica(idServizio);
				ProprietaOggetto p = as.getProprietaOggetto();
				p = mergeProprietaOggetto(p, idServizio, paCore, saCore, registroHelper);
				risultatiRicerca.add(this.getProprieta(p, as.getDescrizione()));
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
							ProprietaOggetto p = fruitore.getProprietaOggetto();
							p = mergeProprietaOggetto(p, idServizio, idSoggettoFruitore, pdCore, registroHelper);
							risultatiRicerca.add(this.getProprieta(p,fruitore.getDescrizione()));
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
			case RATE_LIMITING_POLICY:
			case PLUGIN_CLASSE:
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
	
	public static ProprietaOggetto mergeProprietaOggetto(ProprietaOggetto p, IDServizio idServizio, PorteApplicativeCore paCore, ServiziApplicativiCore saCore,
			ConsoleHelper consoleHelper) {
		/** Devo considerare tutte le porte */
		List<IDPortaApplicativa> listPA = getIDPorteApplicativeAssociateSafe(paCore, idServizio);
		if(listPA!=null && !listPA.isEmpty()) {
			for (IDPortaApplicativa idPA : listPA) {
				if(idPA!=null) {
					PortaApplicativa pa = getPASafe(paCore, idPA);
					if(pa!=null) {
						p = mergeProprietaOggetto(p, idServizio, pa, saCore, consoleHelper);
					}
				}		
			}
		}
		return p;
	}
	private static ProprietaOggetto mergeProprietaOggetto(ProprietaOggetto p, IDServizio idServizio, PortaApplicativa pa, ServiziApplicativiCore saCore,
			ConsoleHelper consoleHelper) {
		if(pa!=null) {
			boolean consideraDataCreazioneComeDataModifica  = true;
			p = consoleHelper.mergeProprietaOggetto(p, pa.getProprietaOggetto(), consideraDataCreazioneComeDataModifica);
			if(pa.sizeServizioApplicativoList()>0) {
				for (PortaApplicativaServizioApplicativo pasa : pa.getServizioApplicativoList()) {
					IDServizioApplicativo idSA = new IDServizioApplicativo();
					idSA.setIdSoggettoProprietario(idServizio.getSoggettoErogatore());
					idSA.setNome(pasa.getNome());
					ServizioApplicativo sa = getSASafe(saCore, idSA);
					if(sa!=null && !ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo())) {
						p = consoleHelper.mergeProprietaOggetto(p, sa.getProprietaOggetto(), !consideraDataCreazioneComeDataModifica);
					}
				}
			}
		}
		return p;
	}
	
	public static ProprietaOggetto mergeProprietaOggetto(ProprietaOggetto p, IDServizio idServizio, IDSoggetto idSoggettoFruitore, PorteDelegateCore pdCore, 
			ConsoleHelper consoleHelper) {
		/** Devo considerare tutte le porte */
		List<IDPortaDelegata> listPD = getIDPorteDelegateAssociateSafe(pdCore, idServizio, idSoggettoFruitore);
		if(listPD!=null && !listPD.isEmpty()) {
			for (IDPortaDelegata idPD : listPD) {
				if(idPD!=null) {
					PortaDelegata pd = getPDSafe(pdCore, idPD);
					if(pd!=null) {
						p = mergeProprietaOggetto(p, idServizio, idSoggettoFruitore, pd, consoleHelper);
					}
				}
			}
		}
		return p;
	}
	private static ProprietaOggetto mergeProprietaOggetto(ProprietaOggetto p, IDServizio idServizio, IDSoggetto idSoggettoFruitore, PortaDelegata pd, 
			ConsoleHelper consoleHelper) {
		if(idServizio!=null && idSoggettoFruitore!=null) {
			// lascio i parametri per eventuali altri merge
		}
		if(pd!=null) {
			boolean consideraDataCreazioneComeDataModifica  = true;
			p = consoleHelper.mergeProprietaOggetto(p, pd.getProprietaOggetto(), consideraDataCreazioneComeDataModifica);
		}
		return p;
	}
	
	private static List<IDPortaApplicativa> getIDPorteApplicativeAssociateSafe(PorteApplicativeCore paCore, IDServizio idServizio) {
		List<IDPortaApplicativa> l = null;
		try {
			l = paCore.getIDPorteApplicativeAssociate(idServizio);
		}catch(Exception e){
			ControlStationCore.logError("Errore durante il recupero delle porte applicative associate al servizio '"+idServizio+"': "+e.getMessage(), e);
		}
		return l;
	}
	private static List<IDPortaDelegata> getIDPorteDelegateAssociateSafe(PorteDelegateCore pdCore, IDServizio idServizio, IDSoggetto idFruitore) {
		List<IDPortaDelegata> l = null;
		try {
			l = pdCore.getIDPorteDelegateAssociate(idServizio, idFruitore);
		}catch(Exception e){
			ControlStationCore.logError("Errore durante il recupero delle porte delegate associate al servizio '"+idServizio+"': "+e.getMessage(), e);
		}
		return l;
	}
	private static PortaApplicativa getPASafe(PorteApplicativeCore paCore, IDPortaApplicativa idPA) {
		try {
			return paCore.getPortaApplicativa(idPA);
		}catch(Exception e){
			ControlStationCore.logError("Errore durante il recupero della porta applicativa '"+idPA+"': "+e.getMessage(), e);
		}
		return null;
	}
	private static PortaDelegata getPDSafe(PorteDelegateCore pdCore, IDPortaDelegata idPD) {
		try {
			return pdCore.getPortaDelegata(idPD);
		}catch(Exception e){
			ControlStationCore.logError("Errore durante il recupero della porta delegata '"+idPD+"': "+e.getMessage(), e);
		}
		return null;
	}
	private static ServizioApplicativo getSASafe(ServiziApplicativiCore saCore, IDServizioApplicativo idSA) {
		try {
			return saCore.getServizioApplicativo(idSA);
		}catch(Exception e){
			ControlStationCore.logError("Errore durante il recupero dell'applicativo '"+idSA+"': "+e.getMessage(), e);
		}
		return null;
	}
	public static org.openspcoop2.core.config.ProprietaOggetto getProprietaOggettoSafe(PorteApplicativeCore paCore, IDPortaApplicativa idPA) {
		try {
			return paCore.getProprietaOggetto(idPA);
		}catch(Exception e){
			ControlStationCore.logError("Errore durante il recupero della porta applicativa '"+idPA+"': "+e.getMessage(), e);
		}
		return null;
	}
	public static org.openspcoop2.core.config.ProprietaOggetto getProprietaOggettoSafe(PorteDelegateCore pdCore, IDPortaDelegata idPD) {
		try {
			return pdCore.getProprietaOggetto(idPD);
		}catch(Exception e){
			ControlStationCore.logError("Errore durante il recupero della porta delegata '"+idPD+"': "+e.getMessage(), e);
		}
		return null;
	}
	public static org.openspcoop2.core.config.ProprietaOggetto getProprietaOggettoSafe(ServiziApplicativiCore saCore, IDServizioApplicativo idSA) {
		try {
			return saCore.getProprietaOggetto(idSA);
		}catch(Exception e){
			ControlStationCore.logError("Errore durante il recupero dell'applicativo '"+idSA+"': "+e.getMessage(), e);
		}
		return null;
	}
	
	private String getProprieta(org.openspcoop2.core.registry.beans.ProprietaOggettoSintetico p,
			String descrizione) {
		return getProprieta(p!=null ? p.getUtenteRichiedente() : null, 
				p!=null ? p.getDataCreazione() : null,
				p!=null ? p.getUtenteUltimaModifica() : null, 
				p!=null ? p.getDataUltimaModifica() : null,
				descrizione);
	}
	private String getProprieta(org.openspcoop2.core.registry.ProprietaOggetto p,
			String descrizione) {
		return getProprieta(p!=null ? p.getUtenteRichiedente() : null, 
				p!=null ? p.getDataCreazione() : null,
				p!=null ? p.getUtenteUltimaModifica() : null, 
				p!=null ? p.getDataUltimaModifica() : null,
				descrizione);
	}
	private String getProprieta(org.openspcoop2.core.config.ProprietaOggetto p,
			String descrizione) {
		return getProprieta(p!=null ? p.getUtenteRichiedente() : null, 
				p!=null ? p.getDataCreazione() : null,
				p!=null ? p.getUtenteUltimaModifica() : null, 
				p!=null ? p.getDataUltimaModifica() : null,
				descrizione);
	}
	private String getProprieta(String utenteRichiedente, Date dataCreazione,
			String utenteUtimaModifica, Date dataUtimaModifica,
			String descrizione) {
		StringBuilder sb = new StringBuilder();
		if(dataCreazione!=null) {
			String dataMs = CostantiControlStation.formatDateMs(dataCreazione);
			sb.append(CostantiControlStation.LABEL_DATA_CREAZIONE).append(": ").append(dataMs);
		}
		if(utenteRichiedente!=null) {
			if(sb.length()>0) {
				sb.append("\n");
			}
			sb.append(CostantiControlStation.LABEL_UTENTE_RICHIEDENTE).append(": ").append(utenteRichiedente);
		}
		if(dataUtimaModifica!=null) {
			String dataMs = CostantiControlStation.formatDateMs(dataUtimaModifica);
			if(sb.length()>0) {
				sb.append("\n\n");
			}
			sb.append(CostantiControlStation.LABEL_DATA_ULTIMA_MODIFICA).append(": ").append(dataMs);
		}
		if(utenteUtimaModifica!=null) {
			if(sb.length()>0) {
				sb.append("\n");
			}
			sb.append(CostantiControlStation.LABEL_UTENTE_ULTIMA_MODIFICA).append(": ").append(utenteUtimaModifica);
		}
		
		if(descrizione!=null && StringUtils.isNotEmpty(descrizione)) {
			if(sb.length()>0) {
				sb.append("\n\n");
			}
			sb.append(CostantiControlStation.LABEL_PROPRIETA_DESCRIZIONE).append(": \n");
			sb.append(descrizione);
		}
		
		if(sb.length()<=0) {
			sb.append("Nessuna proprietà disponibile");
		}
		return sb.toString();
	}
}
