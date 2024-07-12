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

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.byok.BYOKUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.byok.LockUtilities;

/**
 * SecretDecoder
 *
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SecretDecoder extends HttpServlet {

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
			ControlStationCore core = registroHelper.getCore();
			
			String secretToUnwrap = registroHelper.getParameter(UtilsCostanti.PARAMETRO_SECRET_TO_UNWRAP);
			
			// Viaggia comunque un valore cifrato, quindi il caso della informazione in chiaro non è riconoscibile
			if (core.getDriverBYOKUtilities().isEnabledBYOK()) {
				StringBuilder sb = new StringBuilder();
				ControlStationCore.logInfo("SecretDecoder: secretToUnwrap: " + secretToUnwrap);
				String messaggioInformativoInformazioneNonCifrata = core.getNotaInformativaInformazioneMemorizzataInChiaro();
				if(BYOKUtilities.isWrappedValue(secretToUnwrap)) {
					if(!core.getDriverBYOKUtilities().isWrappedWithActivePolicy(secretToUnwrap)) {
						LockUtilities.appendErrorMessageSecurityPolicyDifferente(core.getNotaInformativaInformazioneCifrataSecurityPolicyDifferente(), sb, secretToUnwrap);
					}
				}
				else if(messaggioInformativoInformazioneNonCifrata!=null && StringUtils.isNotEmpty(messaggioInformativoInformazioneNonCifrata)) {
					sb.append(messaggioInformativoInformazioneNonCifrata);
				}
				
				if(sb.length()>0) {
					sb.append("\n\nValore in chiaro: ");
				}
				messaggioEsito = core.getDriverBYOKUtilities().unwrap(secretToUnwrap);
				messageType = MessageType.INFO.toString();
			}
			else {
				messaggioEsito = "ERROR: BYOK Unitialized";
				messageType = MessageType.ERROR.toString();
			}
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la decodifica: "+e.getMessage(), e);
			messaggioEsito = UtilsCostanti.MESSAGGIO_ERRORE_UNWRAP;
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