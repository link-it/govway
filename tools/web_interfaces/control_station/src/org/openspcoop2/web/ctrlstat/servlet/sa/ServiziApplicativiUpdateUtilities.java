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

package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;

/**
 * ServiziApplicativiUpdateUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiziApplicativiUpdateUtilities {

	private List<Object> oggettiDaAggiornare = null;
	private ServiziApplicativiCore saCore;
	private ConfigurazioneCore confCore;
	private IDServizioApplicativo oldIdServizioApplicativo;
	private ServizioApplicativo sa;
	private boolean check = false;
	
	public ServiziApplicativiUpdateUtilities(ServiziApplicativiCore saCore,
			IDServizioApplicativo oldIdServizioApplicativo,
			ServizioApplicativo sa) throws Exception{
		this.oggettiDaAggiornare = new ArrayList<>();
		this.saCore = saCore;
		this.confCore = new ConfigurazioneCore(this.saCore);
		this.oldIdServizioApplicativo = oldIdServizioApplicativo;
		this.sa = sa;
		this.check = !this.sa.getNome().equals(oldIdServizioApplicativo.getNome());
	}
	
	public List<Object> getOggettiDaAggiornare() {
		return this.oggettiDaAggiornare;
	}
	
	public void addServizioApplicativo(){
		this.oggettiDaAggiornare.add(this.sa);
	}
	
	public void checkRateLimiting() throws DriverControlStationException {
		
		if(!this.check) {
			return;
		}
		
		List<AttivazionePolicy> list = null;
		try{
			list = this.confCore.getPolicyByServizioApplicativo(this.oldIdServizioApplicativo);
		}catch(DriverControlStationNotFound notFound) {
		}
		
		if(list!=null && !list.isEmpty()) {
			for (AttivazionePolicy attivazionePolicy : list) {
				AttivazionePolicyFiltro filtro = attivazionePolicy.getFiltro();
				if(filtro!=null) {
					boolean matchFruitore = this.oldIdServizioApplicativo.getNome().equals(filtro.getServizioApplicativoFruitore()) &&
							this.oldIdServizioApplicativo.getIdSoggettoProprietario().getTipo().equals(filtro.getTipoFruitore()) &&
							this.oldIdServizioApplicativo.getIdSoggettoProprietario().getNome().equals(filtro.getNomeFruitore());
					boolean matchErogatore = this.oldIdServizioApplicativo.getNome().equals(filtro.getServizioApplicativoErogatore()) &&
							this.oldIdServizioApplicativo.getIdSoggettoProprietario().getTipo().equals(filtro.getTipoErogatore()) &&
							this.oldIdServizioApplicativo.getIdSoggettoProprietario().getNome().equals(filtro.getNomeErogatore());
					if(matchFruitore) {
						filtro.setServizioApplicativoFruitore(this.sa.getNome());
					}
					if(matchErogatore) {
						filtro.setServizioApplicativoErogatore(this.sa.getNome());
					}
					if(matchFruitore || matchErogatore) {
						this.oggettiDaAggiornare.add(attivazionePolicy);
					}
				}
			}
		}
		
	}
}
