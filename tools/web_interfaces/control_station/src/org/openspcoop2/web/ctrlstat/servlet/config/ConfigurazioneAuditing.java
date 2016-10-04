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


package org.openspcoop2.web.ctrlstat.servlet.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.audit.AuditingCore;
import org.openspcoop2.web.lib.audit.dao.Appender;
import org.openspcoop2.web.lib.audit.dao.AppenderProperty;
import org.openspcoop2.web.lib.audit.dao.Configurazione;
import org.openspcoop2.web.lib.audit.web.AuditCostanti;
import org.openspcoop2.web.lib.audit.web.AuditHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * auditing
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneAuditing extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);

			String statoaudit = request.getParameter( AuditCostanti.PARAMETRO_AUDIT_STATO_AUDIT);
			String stato = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_STATO);
			String dump = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_DUMP);
			String formatodump = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_FORMATO_DUMP);
			String log4j = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_LOG4J);

			ConfigurazioneCore confCore = new ConfigurazioneCore();
			AuditHelper ah = new AuditHelper(request, pd, session);
			AuditingCore auditingCore = new AuditingCore(confCore);

			Configurazione c = auditingCore.getConfigurazioneAudit();
			// Preparo il menu
			confHelper.makeMenu();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, null));
			lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT, null));

			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se idhid != null, modifico i dati dell'audit nel db
			if (!ServletUtils.isEditModeInProgress(request)) {
				// Controlli sui campi immessi
				String msg = ah.auditCheckData(request);
				if (!msg.equals("")) {
					pd.setMessage(msg);

					Configurazione tmpConf = auditingCore.getConfigurazioneAudit();

					// preparo i campi
					Vector<DataElement> dati = ah.addAuditingToDati(
							statoaudit, stato, dump, formatodump, log4j,
							tmpConf.sizeFiltri());

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
							AuditCostanti.OBJECT_NAME_CONFIGURAZIONE_AUDITING, AuditCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_AUDITING
							);
				}

				// Modifico i dati dell'audit nel db
				Configurazione newConfigurazione = auditingCore.getConfigurazioneAudit();

				newConfigurazione.setAuditEngineEnabled(
						statoaudit.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) ? true : false);
				newConfigurazione.setAuditEnabled(
						stato.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) ? true : false);
				newConfigurazione.setDumpEnabled(
						dump.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) ? true : false);
				newConfigurazione.setDumpFormat(formatodump);
				if (log4j.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO)) {
					boolean giaPresente = false;
					for(int i=0; i<newConfigurazione.sizeAppender(); i++){
						if(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_NOME.equals(newConfigurazione.getAppender(i).getNome())){
							giaPresente=true;
							break;
						}
					}
					if (giaPresente==false) {
						Appender appender = new Appender();
						appender.setNome(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_NOME);
						appender.setClassName(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_CLASS_NAME);
						AppenderProperty pr1 = new AppenderProperty();
						pr1.setName(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_FILE_CONFIGURAZIONE_NAME);
						pr1.setValue(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_FILE_CONFIGURAZIONE_VALUE);
						appender.addProperty(pr1);
						AppenderProperty pr2 = new AppenderProperty();
						pr2.setName(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_CATEGORY_NAME);
						pr2.setValue(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_CATEGORY_VALUE);
						appender.addProperty(pr2);
						AppenderProperty pr3 = new AppenderProperty();
						pr3.setName(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_XML_NAME);
						pr3.setValue(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_PROPERTY_XML_VALUE);
						appender.addProperty(pr3);
						newConfigurazione.addAppender(appender);
					}
				} else {
					for(int i=0; i<newConfigurazione.sizeAppender(); i++){
						if(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_NOME.equals(newConfigurazione.getAppender(i).getNome())){
							newConfigurazione.removeAppender(i);
							break;
						}
					}
				}

				confCore.performUpdateOperation(userLogin, confHelper.smista(), newConfigurazione);


				// preparo i campi
				Vector<DataElement> dati = ah.addAuditingToDati(
						statoaudit, stato, dump, formatodump, log4j,
						c.sizeFiltri());

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				pd.setDati(dati);

				pd.setMessage(AuditCostanti.LABEL_AUDIT_CONFIGURAZIONE_MODIFICATA);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeFinished(mapping,
						AuditCostanti.OBJECT_NAME_CONFIGURAZIONE_AUDITING, AuditCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_AUDITING);
			}


			if (statoaudit == null)
				statoaudit = c.isAuditEngineEnabled() ? AuditCostanti.DEFAULT_VALUE_ABILITATO :  AuditCostanti.DEFAULT_VALUE_DISABILITATO;
			stato = c.isAuditEnabled() ? AuditCostanti.DEFAULT_VALUE_ABILITATO : AuditCostanti.DEFAULT_VALUE_DISABILITATO;
			dump = c.isDumpEnabled() ? AuditCostanti.DEFAULT_VALUE_ABILITATO :AuditCostanti.DEFAULT_VALUE_DISABILITATO;
			formatodump = c.getDumpFormat();

			log4j = AuditCostanti.DEFAULT_VALUE_DISABILITATO;
			if(c.sizeAppender()>0){
				for(int i=0; i<c.sizeAppender(); i++){
					if(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_APPENDER_NOME.equals(c.getAppender(i).getNome())){
						log4j = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						break;
					}
				}
			}

			// preparo i campi
			Vector<DataElement> dati = ah.addAuditingToDati(
					statoaudit, stato, dump, formatodump, log4j,
					c.sizeFiltri());

			dati.add(ServletUtils.getDataElementForEditModeFinished());
			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
					AuditCostanti.OBJECT_NAME_CONFIGURAZIONE_AUDITING, AuditCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_AUDITING);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AuditCostanti.OBJECT_NAME_CONFIGURAZIONE_AUDITING, AuditCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_AUDITING);
		}
	}
}
