/*
 * OpenSPCoop - Customizable API Gateway 
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
import org.openspcoop2.pdd.logger.DriverMsgDiagnostici;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.builder.DiagnosticoBuilder;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.InformazioniProtocollo;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.utils.resources.HttpUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * Questa servlet si occupa di esportare le tracce diagnostiche in formato xml
 * zippandole
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DiagnosticaExporter extends HttpServlet {

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

		String filename = "Diagnostici.zip";

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
			HashMap<String, ArrayList<MsgDiagnostico>> tracce = null;

			int limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			int offset = 0;
			int i = 0;// progressivo per evitare entry duplicate nel file zip
			// Create a buffer for reading the files
			byte[] buf = new byte[1024];
			ZipOutputStream zip = new ZipOutputStream(out);
			InputStream in = null;

			while ((tracce = getMessaggiDiagnostici(request, offset, limit)).size() > 0) {

				for (Iterator<String> keys = tracce.keySet().iterator(); keys.hasNext();) {
					String key = keys.next();

					ArrayList<MsgDiagnostico> lista = tracce.get(key);
					// Add ZIP entry to output stream.
					zip.putNextEntry(new ZipEntry(++i + "_" + key + " (" + lista.size() + " entries)" + ".xml"));
					for (int j = 0; j < lista.size(); j++) {
						MsgDiagnostico msg = lista.get(j);
						String newLine = j > 0 ? "\n\n" : "";
						IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(msg.getProtocollo());
						DiagnosticoBuilder diagnostico = new DiagnosticoBuilder(protocolFactory);
						in = new ByteArrayInputStream((newLine + diagnostico.toString(msg)).getBytes());

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
				// modifico offset map
				offset += limit;

			}
			// close zip file
			zip.flush();
			zip.close();

		} catch (Exception e) {
			ControlStationCore.logError("Errore durante l'esportazione dei diagnostici "+e.getMessage(), e);
			throw new ServletException(e);
		} 
	}

	private HashMap<String, ArrayList<MsgDiagnostico>> getMessaggiDiagnostici(HttpServletRequest request, int offset, int limit) throws Exception {

		
		ArchiviCore archiviCore = new ArchiviCore();
		SoggettiCore soggettiCore = new SoggettiCore(archiviCore);
		
		String nomeDs = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE);
		if(nomeDs!=null && nomeDs.equals("")){
			nomeDs = null;
		}
		if(nomeDs!=null && nomeDs.equals("-")){
			nomeDs = null;
		}
		DriverMsgDiagnostici driver = archiviCore.getDriverMSGDiagnostici(nomeDs);

		String severita = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA);
		String idfunzione = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_FUNZIONE);
		String datainizio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO);
		String datafine = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE);
		String tipo_servizio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO);
		String servizio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO);
		String tipo_mittente = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE);
		String nome_mittente = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE);
		String tipo_destinatario = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO);
		String nome_destinatario = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO);
		String nome_azione = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE);
		String correlazioneApplicativa = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA);
		if (correlazioneApplicativa == null || "".equals(correlazioneApplicativa))
			correlazioneApplicativa = null;
		String protocollo = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
		if (protocollo == null || "".equals(protocollo))
			protocollo = null;
		String search = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_SEARCH);
		String idMessaggio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH);

		/*
		 * String params = "&severita="+severita +"&idfunzione="+idfunzione
		 * +"&datainizio="+(datainizio==null?"":datainizio)
		 * +"&datafine="+(datafine==null?"":datafine)
		 * +"&tipo_mittente="+(tipo_mittente==null?"":tipo_mittente)
		 * +"&nome_mittente="+(nome_mittente==null?"":nome_mittente)
		 * +"&tipo_destinatario="+(tipo_destinatario==null?"":tipo_destinatario)
		 * +"&nome_destinatario="+(nome_destinatario==null?"":nome_destinatario)
		 * +"&tipo_servizio="+(tipo_servizio==null?"":tipo_servizio)
		 * +"&servizio="+(servizio==null?"":servizio)
		 * +"&nome_azione="+(nome_azione==null?"":nome_azione);
		 */

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

		FiltroRicercaDiagnosticiConPaginazione filter = new FiltroRicercaDiagnosticiConPaginazione();

		if (severita == null || "".equals(severita)) {
			severita = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_DIAGNOSTICA_SEVERITA_DEFAULT;
		}
		filter.setSeverita(new Integer(severita));

		if (idfunzione != null && !"-".equals(idfunzione) && !"".equals(idfunzione)) {
			filter.setIdFunzione(idfunzione);
		}

		if (dataInizioData != null) {
			filter.setDataInizio(dataInizioData);
		}
		if (dataFineData != null) {
			filter.setDataFine(dataFineData);
		}

		InformazioniProtocollo informazioniProtocollo = null;

		// setto tipo soggetto mittente se specificato
		if ((tipo_mittente != null && !"-".equals(tipo_mittente)) || (nome_mittente != null && !"".equals(nome_mittente))) {
			if ("-".equals(tipo_mittente))
				tipo_mittente = "";
			if (informazioniProtocollo == null)
				informazioniProtocollo = new InformazioniProtocollo();
			IDSoggetto mittente = new IDSoggetto(tipo_mittente, nome_mittente);
			informazioniProtocollo.setFruitore(mittente);// il mittente e' il fruitore

		}
		// setto tipo soggetto erogatore se specificato
		if ((tipo_destinatario != null && !"-".equals(tipo_destinatario)) || (nome_destinatario != null && !"".equals(nome_destinatario))) {
			if ("-".equals(tipo_destinatario))
				tipo_destinatario = "";
			if (informazioniProtocollo == null)
				informazioniProtocollo = new InformazioniProtocollo();
			IDSoggetto destinatario = new IDSoggetto(tipo_destinatario, nome_destinatario);
			informazioniProtocollo.setErogatore(destinatario);// il destinatario e' l erogatore
		}
		// setto tipo servizio se specificato
		if ((tipo_servizio != null && !"-".equals(tipo_servizio)) || (servizio != null && !"".equals(servizio))) {
			if ("-".equals(tipo_servizio))
				tipo_servizio = "";
			if (informazioniProtocollo == null)
				informazioniProtocollo = new InformazioniProtocollo();

			// se erogatore (destinatario) e' stato specificato allora setto
			if (informazioniProtocollo != null && informazioniProtocollo.getErogatore() != null) {
				informazioniProtocollo.setErogatore(informazioniProtocollo.getErogatore());
			}

			informazioniProtocollo.setTipoServizio(tipo_servizio);
			informazioniProtocollo.setServizio(servizio);

		}

		// azione
		if (nome_azione != null && !"".equals(nome_azione)) {
			if (informazioniProtocollo == null)
				informazioniProtocollo = new InformazioniProtocollo();
			informazioniProtocollo.setAzione(nome_azione);
		}

		// CorrelazioneApplicativa
		filter.setCorrelazioneApplicativa(correlazioneApplicativa);
		
		// Id Messaggio
		if(idMessaggio!=null && !"".equals(idMessaggio))
			filter.setIdBustaRichiesta(idMessaggio);
		
		// protocollo
		if(protocollo!=null && !"".equals(protocollo) && !"-".equals(protocollo)){
			filter.setProtocollo(protocollo);
		}
		
		if (informazioniProtocollo != null) {
			filter.setInformazioniProtocollo(informazioniProtocollo);
			filter.setRicercaSoloMessaggiCorrelatiInformazioniProtocollo(true);
		}

		HttpSession session = request.getSession(true);
		String userLogin = (String) ServletUtils.getUserLoginFromSession(session);
		if(archiviCore.isVisioneOggettiGlobale(userLogin)==false){
			filter.setFiltroSoggetti(soggettiCore.getSoggettiWithSuperuser(userLogin));
		}
		
		if (search!=null && !search.equals("")) {
			filter.setMessaggioCercatoInternamenteTestoDiagnostico(search);
		}
		
		filter.setAsc(true); // almeno dentro il singolo file xml i diagnostici sono in ordine di generazione
		
		filter.setLimit(limit);
		filter.setOffset(offset);

		List<MsgDiagnostico> lista = null;
		try{
			lista = driver.getMessaggiDiagnostici(filter);
		}catch(DriverMsgDiagnosticiNotFoundException dNot){
			lista = new ArrayList<MsgDiagnostico>();
		}

		// struttura dati contenente i messaggi con stessa key
		HashMap<String, ArrayList<MsgDiagnostico>> table = new HashMap<String, ArrayList<MsgDiagnostico>>();
		// riempio struttura dati
		for (int j = 0; j < lista.size(); j++) {
			MsgDiagnostico msg = lista.get(j);
			String idBusta = msg.getIdBusta();
			String data = simpleDateFormat.format(msg.getGdo().getTime());

			String key = idBusta != null ? idBusta : data;

			ArrayList<MsgDiagnostico> tmp = null;
			// recupero lista associata a questa chiava, altrimenti la creo
			// nuova
			if (table.containsKey(key)) {
				tmp = table.get(key);
			} else {
				tmp = new ArrayList<MsgDiagnostico>();
			}

			// aggiungo il msg alla struttura dati
			tmp.add(msg);
			table.put(key, tmp);

		}

		return table;

	}

}
