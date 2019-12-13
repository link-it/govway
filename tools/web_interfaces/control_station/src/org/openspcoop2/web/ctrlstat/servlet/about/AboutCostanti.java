/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * AboutCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AboutCostanti {

	/* OBJECT NAME */

	public final static String OBJECT_NAME_ABOUT = "about";

	public final static ForwardParams TIPO_OPERAZIONE_ABOUT = ForwardParams.OTHER("");

	/* SERVLET NAME */

	public final static String SERVLET_NAME_ABOUT = OBJECT_NAME_ABOUT+".do";
	public final static Vector<String> SERVLET_ABOUT = new Vector<String>();
	static{
		SERVLET_ABOUT.add(SERVLET_NAME_ABOUT);
	}
	
	/* LABEL */
	public final static String LABEL_ABOUT = "Informazioni";
	public final static String LABEL_PRODOTTO = "Prodotto";
	public final static String LABEL_VERSIONE = "Versione";
	public final static String LABEL_SITO = "Sito";
	
	public final static String LABEL_COPYRIGHT = "Copyright";
	public final static String LABEL_COPYRIGHT_VALUE = CostantiPdD.OPENSPCOOP2_COPYRIGHT;
	
	public final static String LABEL_LICENZA = "Licenza";
	public final static String LICENSE = CostantiPdD.OPENSPCOOP2_LICENSE;
	
	public final static String LABEL_PARAMETRO_ABOUT_INFO = "Licenza";
	public final static String BUTTON = "Aggiorna Licenza";
	public final static String PARAMETRO_ABOUT_INFO = "info";
	public final static String PARAMETRO_ABOUT_INFO_FINISH = "infoFinish";
}
