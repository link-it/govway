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
package org.openspcoop2.web.loader.servlet.about;

import java.util.List;
import java.util.ArrayList;

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
	
	private AboutCostanti() {}

	/* OBJECT NAME */

	public static final String OBJECT_NAME_ABOUT = "about";

	public static final ForwardParams TIPO_OPERAZIONE_ABOUT = ForwardParams.OTHER("");

	/* SERVLET NAME */

	public static final String SERVLET_NAME_ABOUT = OBJECT_NAME_ABOUT+".do";
	private static final List<String> SERVLET_ABOUT = new ArrayList<>();
	public static List<String> getServletAbout() {
		return SERVLET_ABOUT;
	}
	static{
		SERVLET_ABOUT.add(SERVLET_NAME_ABOUT);
	}
	
	/* LABEL */
	public static final String LABEL_ABOUT = "Informazioni";
	public static final String LABEL_PRODOTTO = "Prodotto";
	public static final String LABEL_VERSIONE = "Versione";
	public static final String LABEL_SITO = "Sito";
	
	public static final String LABEL_COPYRIGHT = "Copyright";
	public static final String LABEL_COPYRIGHT_VALUE = CostantiPdD.OPENSPCOOP2_COPYRIGHT;
	
	public static final String LABEL_LICENZA = "Licenza";
	public static final String LICENSE = CostantiPdD.OPENSPCOOP2_LICENSE;
}
