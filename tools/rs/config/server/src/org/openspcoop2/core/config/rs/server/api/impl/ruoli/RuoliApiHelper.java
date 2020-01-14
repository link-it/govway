/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.ruoli;

import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.model.ContestoEnum;
import org.openspcoop2.core.config.rs.server.model.FonteEnum;
import org.openspcoop2.core.config.rs.server.model.Ruolo;
import org.openspcoop2.core.config.rs.server.model.RuoloItem;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCostanti;

/**
 * RuoliApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class RuoliApiHelper {
	
	public static void overrideRuoloParams(Ruolo body, HttpRequestWrapper wrap) {		
		wrap.overrideParameter(RuoliCostanti.PARAMETRO_RUOLO_DESCRIZIONE, body.getDescrizione());
		wrap.overrideParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME, body.getNome());
		wrap.overrideParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME_ESTERNO, body.getIdentificativoEsterno());
	}
		

	public static org.openspcoop2.core.registry.Ruolo apiRuoloToRuoloRegistro(Ruolo ruolo, String superUser) throws NotFoundException  {
		
		org.openspcoop2.core.registry.Ruolo regRuolo = new org.openspcoop2.core.registry.Ruolo();
		regRuolo.setNome(ruolo.getNome());
		regRuolo.setDescrizione(ruolo.getDescrizione());
		
		FonteEnum tipologiaFonte = ruolo.getFonte();
		if (tipologiaFonte == null)
			tipologiaFonte = FonteEnum.QUALSIASI;
		regRuolo.setTipologia(Enums.apiFonteToRegistroTipologia(tipologiaFonte));
		
		ContestoEnum contesto = ruolo.getContesto();
		if (contesto == null)
			contesto = ContestoEnum.QUALSIASI;
		regRuolo.setContestoUtilizzo(Enums.apiContestoToRegistroContesto(contesto));

		if(regRuolo.getTipologia()!=null && (RuoloTipologia.QUALSIASI.equals(regRuolo.getTipologia()) || RuoloTipologia.ESTERNO.equals(regRuolo.getTipologia()))) {
			String n = ruolo.getIdentificativoEsterno();
			if(n!=null) {
				n = n.trim();
			}
			regRuolo.setNomeEsterno(n);
		}
		
		regRuolo.setSuperUser(superUser);
		
		return regRuolo;
	}
	
	public static Ruolo ruoloRegistroToApiRuolo(org.openspcoop2.core.registry.Ruolo ruolo) {
		Ruolo ret = new Ruolo();
		
		ret.setContesto(Enums.registroContestoToApiContesto(ruolo.getContestoUtilizzo()));
		ret.setDescrizione(ruolo.getDescrizione());
		ret.setFonte(Enums.registroTipologiaToApiFonte(ruolo.getTipologia()));
		ret.setIdentificativoEsterno(ruolo.getNomeEsterno());
		ret.setNome(ruolo.getNome());
		
		return ret;
	}
	
	public static RuoloItem ruoloApiToRuoloItem(Ruolo ruolo) {
		RuoloItem ret = new RuoloItem();
		ret.setContesto(ruolo.getContesto());
		ret.setFonte(ruolo.getFonte());
		ret.setNome(ruolo.getNome());
		
		return ret;
	}
	

}
