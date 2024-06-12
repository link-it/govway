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
package org.openspcoop2.protocol.engine;

import java.util.List;

import org.openspcoop2.protocol.engine.constants.Costanti;

/**
 * ConfigurazionePdDFiltroServiziApplicativi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneFiltroServiziApplicativi {

	public static ConfigurazioneFiltroServiziApplicativi getFiltroApplicativiHttps() {
		ConfigurazioneFiltroServiziApplicativi filtro = new ConfigurazioneFiltroServiziApplicativi();
		filtro.includiApplicativiNonModI = true;
		filtro.includiApplicativiModIEsterni = false;
		filtro.includiApplicativiModIInterni = true;
		return filtro;
	}
	public static ConfigurazioneFiltroServiziApplicativi getFiltroApplicativiModIFirma() {
		ConfigurazioneFiltroServiziApplicativi filtro = new ConfigurazioneFiltroServiziApplicativi();
		filtro.includiApplicativiNonModI = false;
		filtro.includiApplicativiModIEsterni = true;
		filtro.includiApplicativiModIInterni = false;
		return filtro;
	}
	
	private boolean includiApplicativiNonModI;
	private boolean includiApplicativiModIEsterni;
	private boolean includiApplicativiModIInterni;
	private List<String> tipiSoggetti = Costanti.getTipiSoggettoModI();
	
	public List<String> getTipiSoggetti() {
		return this.tipiSoggetti;
	}
	public void setTipiSoggetti(List<String> tipiSoggetti) {
		this.tipiSoggetti = tipiSoggetti;
	}
	public boolean isIncludiApplicativiNonModI() {
		return this.includiApplicativiNonModI;
	}
	public void setIncludiApplicativiNonModI(boolean includiApplicativiNonModI) {
		this.includiApplicativiNonModI = includiApplicativiNonModI;
	}
	public boolean isIncludiApplicativiModIEsterni() {
		return this.includiApplicativiModIEsterni;
	}
	public void setIncludiApplicativiModIEsterni(boolean includiApplicativiModIEsterni) {
		this.includiApplicativiModIEsterni = includiApplicativiModIEsterni;
	}
	public boolean isIncludiApplicativiModIInterni() {
		return this.includiApplicativiModIInterni;
	}
	public void setIncludiApplicativiModIInterni(boolean includiApplicativiModIInterni) {
		this.includiApplicativiModIInterni = includiApplicativiModIInterni;
	}
	
}
