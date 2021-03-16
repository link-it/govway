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
package org.openspcoop2.web.ctrlstat.servlet.about;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.IVersionInfo;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.TargetType;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * AboutHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AboutHelper extends ConsoleHelper {

	public AboutHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}

	public Vector<DataElement> addAboutToDati(Vector<DataElement> dati,TipoOperazione tipoOperazione, String userLogin,
			BinaryParameter infoBP) throws UtilsException {
		 
		IVersionInfo versionInfo = null;
		try {
			versionInfo = this.core.getInfoVersion(this.session);
		}catch(Exception e) {
			ControlStationLogger.getPddConsoleCoreLogger().error("Errore durante la lettura delle informazioni sulla versione: "+e.getMessage(),e);
		}
		if(versionInfo!=null) {
			if(!StringUtils.isEmpty(versionInfo.getErrorMessage())) {
				this.pd.setMessage(versionInfo.getErrorMessage(), MessageType.ERROR);
			}
			else if(!StringUtils.isEmpty(versionInfo.getWarningMessage())) {
				this.pd.setMessage(versionInfo.getWarningMessage(), MessageType.INFO);
			}
		}
		
		DataElement de = new DataElement();

		// titolo sezione
		de = new DataElement();
		de.setLabel( ServletUtils.getLabelFromResourceBundle( session, "About.Prodotto" ) );	//AboutCostanti.LABEL_PRODOTTO
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// versione
		de = new DataElement();
		de.setLabel( ServletUtils.getLabelFromResourceBundle( session, "About.Versione" ) );	//AboutCostanti.LABEL_VERSIONE
		de.setType(DataElementType.TEXT);
		de.setValue(this.core.getProductVersion());
		dati.addElement(de);
		
		// sito
		de = new DataElement();
		de.setLabelLink(AboutCostanti.LABEL_SITO);
		de.setType(DataElementType.LINK);
		de.setTarget(TargetType.BLANK);
		if(versionInfo!=null && !StringUtils.isEmpty(versionInfo.getWebSite())) {
			de.setValue(versionInfo.getWebSite());
			de.setUrl(versionInfo.getWebSite());
		}
		else {
			de.setValue(CostantiControlStation.LABEL_OPENSPCOOP2_WEB);
			de.setUrl(CostantiControlStation.LABEL_OPENSPCOOP2_WEB);
		}
		dati.addElement(de);
		
		// copyright
		de = new DataElement();
		de.setLabel( ServletUtils.getLabelFromResourceBundle( session, "About.Copyright" ) );	//AboutCostanti.LABEL_COPYRIGHT
		de.setType(DataElementType.TEXT);
		if(versionInfo!=null && !StringUtils.isEmpty(versionInfo.getCopyright())) {
			de.setValue(versionInfo.getCopyright());
		}
		else {
			de.setValue(AboutCostanti.LABEL_COPYRIGHT_VALUE);
		}
		dati.addElement(de);
		
		// sito openspcoop
		de = new DataElement();
		de.setLabel( ServletUtils.getLabelFromResourceBundle( session, "About.Licenza" ) );	//AboutCostanti.LABEL_LICENZA
		de.setType(DataElementType.TEXT_AREA_NO_EDIT);
		de.setCols(70);
		if(versionInfo!=null) {
			String info = versionInfo.getInfo();
			String [] split = info.split("\n");
			de.setValue(info);
			if(split==null || split.length>11) {
				de.setRows(11);
			}
			else {
				de.setRows(split.length+1);
			}
		}
		else {
			de.setValue(AboutCostanti.LICENSE);
			de.setRows(11);
		}
		dati.addElement(de);
		
		if(versionInfo!=null) {
			
			de = new DataElement();
			de.setLabel( ServletUtils.getLabelFromResourceBundle( session, "About.ParametroAboutInfo" ) );	//AboutCostanti.LABEL_PARAMETRO_ABOUT_INFO
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			DataElement deFile = infoBP.getFileDataElement(AboutCostanti.LABEL_PARAMETRO_ABOUT_INFO, "", getSize());
			deFile.setPostBack(false);
			dati.add(deFile);
			dati.addAll(infoBP.getFileNameDataElement());
			dati.add(infoBP.getFileIdDataElement());
			
				
			de = new DataElement();
			de.setName(AboutCostanti.PARAMETRO_ABOUT_INFO_FINISH);
			de.setType(DataElementType.HIDDEN);
			de.setValue(Costanti.CHECK_BOX_ENABLED);
			dati.addElement(de);
				
			this.pd.setLabelBottoneInvia(AboutCostanti.BUTTON);


		}
		else {
			this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
		}
		
		return dati;
	}

}
