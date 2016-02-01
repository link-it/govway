/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.logger.DriverTracciamento;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.builder.TracciaBuilder;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracceConPaginazione;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.resources.HttpUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * Questa servlet si occupa di esportare le tracce in formato xml zippandole
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class TracceExporter extends HttpServlet {

	private static final long serialVersionUID = -7341279067126334095L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	/**
	 * Processa la richiesta pervenuta e si occupa di fornire i dati richiesti
	 * in formato zip
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String filename = "Tracce.zip";

		ControlStationCore.logDebug("Ricevuta Richiesta di esportazione...");
		Enumeration<?> en = request.getParameterNames();
		ControlStationCore.logDebug("Parametri (nome = valore):\n-----------------");
		while (en.hasMoreElements()) {
			String param = (String) en.nextElement();
			String value = request.getParameter(param);
			ControlStationCore.logDebug(param + " = " + value);
		}
		ControlStationCore.logDebug("-----------------");

		// Setto Proprietà Export File
		try{
			HttpUtilities.setOutputFile(response, true, filename);
		}catch(Exception e){
			throw new ServletException(e.getMessage(),e);
		}

		OutputStream out = response.getOutputStream();
		// String res="";
		try {

			HashMap<String, ArrayList<Traccia>> tracce = null;
			int offset = 0;
			int limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			int i = 0;// progressivo per evitare entry duplicate nel file zip
			// Create a buffer for reading the files
			byte[] buf = new byte[1024];
			ZipOutputStream zip = new ZipOutputStream(out);
			InputStream in = null;
			while ((tracce = getTracce(request, offset, limit)).size() > 0) {

				for (Iterator<String> keys = tracce.keySet().iterator(); keys.hasNext();) {
					String key = keys.next();

					ArrayList<Traccia> lista = tracce.get(key);
					// Add ZIP entry to output stream.
					zip.putNextEntry(new ZipEntry(++i + "_" + key + " (" + lista.size() + " entries)" + ".xml"));
					for (int j = 0; j < lista.size(); j++) {
						Traccia traccia = lista.get(j);
						String newLine = j > 0 ? "\n\n" : "";
						IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(traccia.getProtocollo());
						TracciaBuilder tracciaBuilder = new TracciaBuilder(protocolFactory);
						in = new ByteArrayInputStream((newLine + tracciaBuilder.toString(traccia)).getBytes());

						// Transfer bytes from the input stream to the ZIP file
						int len;
						while ((len = in.read(buf)) > 0) {
							zip.write(buf, 0, len);
						}

					}
					// Complete the entry
					zip.closeEntry();
					in.close();
				}
				// aggiorno offset
				offset += limit;
			}
			// close zip file
			zip.flush();
			zip.close();

		} catch (Exception e) {
			ControlStationCore.logError("Errore durante l'esportazione delle traccie "+e.getMessage(), e);
			throw new ServletException(e);
		}
	}

	private HashMap<String, ArrayList<Traccia>> getTracce(HttpServletRequest request, int offset, int limit) throws Exception {

		ControlStationCore core = new ControlStationCore();
		SoggettiCore soggettiCore = new SoggettiCore(core);
		String nomeDs = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE);
		if(nomeDs!=null && nomeDs.equals("")){
			nomeDs = null;
		}
		if(nomeDs!=null && nomeDs.equals("-")){
			nomeDs = null;
		}
		DriverTracciamento driver = core.getDriverTracciamento(nomeDs);

		String datainizio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO);
		String datafine = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE);
		String profcoll = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE);
		String tipo_servizio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO);
		String servizio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO);
		String tipo_mittente = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE);
		String nome_mittente = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE);
		String tipo_destinatario = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO);
		String nome_destinatario = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO);
		String nome_azione = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE);
		String correlazioneApplicativa = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA);
		String protocollo = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
		String idMessaggio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH);

		if (tipo_destinatario == null || "".equals(tipo_destinatario) || "-".equals(tipo_destinatario))
			tipo_destinatario = null;
		if (nome_destinatario == null || "".equals(nome_destinatario))
			nome_destinatario = null;
		if (tipo_servizio == null || "".equals(tipo_servizio) || "-".equals(tipo_servizio))
			tipo_servizio = null;
		if (servizio == null || "".equals(servizio))
			servizio = null;
		if (tipo_mittente == null || "".equals(tipo_mittente) || "-".equals(tipo_mittente))
			tipo_mittente = null;
		if (nome_mittente == null || "".equals(nome_mittente))
			nome_mittente = null;
		if (nome_azione == null || "".equals(nome_azione))
			nome_azione = null;
		if (correlazioneApplicativa == null || "".equals(correlazioneApplicativa))
			correlazioneApplicativa = null;
		if (protocollo == null || "".equals(protocollo) || "-".equals(protocollo))
			protocollo = null;
		if (idMessaggio == null || "".equals(idMessaggio.trim()))
			idMessaggio = null;

		// Conversione Data
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); // SimpleDateFormat non e' thread-safe
		Date dataInizioData = !"".equals(datainizio) ? simpleDateFormat.parse(datainizio) : null;
		Date dataFineData = !"".equals(datafine) ? simpleDateFormat.parse(datafine) : null;

		// bug fix
		// data fine inidica l intera giornata quinid aggiungo 1 giorno alla
		// data
		// inserita
		if (dataFineData != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataFineData);
			cal.add(Calendar.DAY_OF_WEEK, 1);
			dataFineData = cal.getTime();
		}

		ProfiloDiCollaborazione myProfColl = null;
		if (profcoll.equals(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO)) {
			myProfColl = ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO;
		}
		if (profcoll.equals(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO)) {
			myProfColl = ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO;
		}
		if (profcoll.equals(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_SINCRONO)) {
			myProfColl = ProfiloDiCollaborazione.SINCRONO;
		}
		if (profcoll.equals(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ONEWAY)) {
			myProfColl = ProfiloDiCollaborazione.ONEWAY;
		}


		HttpSession session = request.getSession(true);
		String userLogin = (String) ServletUtils.getUserLoginFromSession(session);
		List<IDSoggetto> filtroSoggetti = null;
		if(core.isVisioneOggettiGlobale(userLogin)==false){
			filtroSoggetti = soggettiCore.getSoggettiWithSuperuser(userLogin);
		}
		
		
		FiltroRicercaTracceConPaginazione filtro = new FiltroRicercaTracceConPaginazione();
		if (dataInizioData != null)
			filtro.setMinDate(dataInizioData);
		if (dataFineData != null)
			filtro.setMaxDate(dataFineData);
		
		org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo informazioniProtocollo = null;
		
		IDSoggetto idMittente = null;
		if (tipo_mittente != null){
			if(idMittente==null){
				idMittente = new IDSoggetto();
			}
			idMittente.setTipo(tipo_mittente);
		}
		if (nome_mittente != null){
			if(idMittente==null){
				idMittente = new IDSoggetto();
			}
			idMittente.setNome(nome_mittente);
		}
		if(idMittente!=null){
			if(informazioniProtocollo==null){
				informazioniProtocollo = new org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo();
			}
			informazioniProtocollo.setMittente(idMittente);
		}
		
		
		IDSoggetto idDestinatario = null;
		if (tipo_destinatario != null){
			if(idDestinatario==null){
				idDestinatario = new IDSoggetto();
			}
			idDestinatario.setTipo(tipo_destinatario);
		}
		if (nome_destinatario != null){
			if(idDestinatario==null){
				idDestinatario = new IDSoggetto();
			}
			idDestinatario.setNome(nome_destinatario);
		}
		if(idDestinatario!=null){
			if(informazioniProtocollo==null){
				informazioniProtocollo = new org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo();
			}
			informazioniProtocollo.setDestinatario(idDestinatario);
		}
		
		
		if (tipo_servizio != null){
			if(informazioniProtocollo==null){
				informazioniProtocollo = new org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo();
			}
			informazioniProtocollo.setTipoServizio(tipo_servizio);
		}
		if (servizio != null){
			if(informazioniProtocollo==null){
				informazioniProtocollo = new org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo();
			}
			informazioniProtocollo.setServizio(servizio);
		}
		if (nome_azione != null){
			if(informazioniProtocollo==null){
				informazioniProtocollo = new org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo();
			}
			informazioniProtocollo.setAzione(nome_azione);
		}
		
		
		if (!ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ANY.equals(profcoll)){
			if(informazioniProtocollo==null){
				informazioniProtocollo = new org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo();
			}
			informazioniProtocollo.setProfiloCollaborazioneEngine(myProfColl);
		}
		
		filtro.setInformazioniProtocollo(informazioniProtocollo);
		
		
		if(filtroSoggetti!=null && filtroSoggetti.size()>0){
			filtro.setFiltroSoggetti(filtroSoggetti);
		}
		
		if (correlazioneApplicativa != null){
			filtro.setIdCorrelazioneApplicativa(correlazioneApplicativa);
			filtro.setIdCorrelazioneApplicativaRisposta(correlazioneApplicativa);
			filtro.setIdCorrelazioneApplicativaOrMatch(true);
		}
		
		// Id Messaggio
		if(idMessaggio!=null && !"".equals(idMessaggio))
			filtro.setIdBusta(idMessaggio);
		
		if (protocollo != null){
			filtro.setProtocollo(protocollo);
		}
		filtro.setLimit(limit);
		filtro.setOffset(offset);
		filtro.setAsc(false);
		

		HashMap<String, ArrayList<Traccia>> tracce = null;
		try {
			
			// organizzo le tracce per id_codiceporta
			tracce = new HashMap<String, ArrayList<Traccia>>();
			List<Traccia> listTracce = null;
			try{
				listTracce = driver.getTracce(filtro);
			}catch(DriverTracciamentoNotFoundException dNot){
				listTracce = new ArrayList<Traccia>();
			}
			
			for (Traccia traccia : listTracce) {
				
				String idBusta = traccia.getBusta().getID();
				String idPorta = traccia.getIdSoggetto().getCodicePorta();
				String key = idBusta + "_" + idPorta;
				ArrayList<Traccia> lista;
				if (tracce.containsKey(key)) {
					lista = tracce.remove(key);
				} else {
					lista = new ArrayList<Traccia>();
				}
				lista.add(traccia);
				tracce.put(key, lista);
			}
			
		} finally {
			
		}

		return tracce;

	}

}
