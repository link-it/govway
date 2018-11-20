package org.openspcoop2.core.config.rs.server.api.impl;

import org.openspcoop2.core.config.rs.server.model.ContestoEnum;
import org.openspcoop2.core.config.rs.server.model.FonteEnum;
import org.openspcoop2.core.config.rs.server.model.Ruolo;
import org.openspcoop2.core.config.rs.server.model.RuoloItem;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.generic_project.exception.NotFoundException;

public class RuoliApiHelper {
	
	public static RuoloContesto apiContestoToRegistroContesto(ContestoEnum c) {
		switch(c) {
		case EROGAZIONE:	return RuoloContesto.PORTA_APPLICATIVA;
		case FRUIZIONE:  	return RuoloContesto.PORTA_DELEGATA;
		case QUALSIASI: 	return RuoloContesto.QUALSIASI;
		default: throw new IllegalArgumentException("Contesto di configurazione ruoli sconosciuto: " + c.toString());
		}
	}
	
	
	public static ContestoEnum registroContestoToApiContesto(RuoloContesto c) {
		switch(c) {
		case PORTA_APPLICATIVA: return ContestoEnum.EROGAZIONE;
		case PORTA_DELEGATA: return ContestoEnum.FRUIZIONE;
		case QUALSIASI: return ContestoEnum.QUALSIASI;
		default: throw new IllegalArgumentException("Contesto di registro ruoli sconosciuto: " + c.toString());
		}
	}
	
	
	public static FonteEnum registroTipologiaToApiFonte(RuoloTipologia tipo) {
		switch (tipo) {
		case ESTERNO: return FonteEnum.ESTERNA;
		case INTERNO: return FonteEnum.REGISTRO;
		case QUALSIASI: return FonteEnum.QUALSIASI;
		default: throw new IllegalArgumentException("TipologiaRuolo sconociuta: " + tipo.toString());
		}
	}
	
	
	public static RuoloTipologia apiFonteToRegistroTipologia(FonteEnum fonte) {
		switch (fonte) {
		case ESTERNA: return RuoloTipologia.ESTERNO;
		case REGISTRO: return RuoloTipologia.INTERNO;
		case QUALSIASI: return RuoloTipologia.QUALSIASI;
		default: throw new IllegalArgumentException("Fonte del ruolo sconosciuta: " + fonte.toString());
		}
	}
		

	public static org.openspcoop2.core.registry.Ruolo apiRuoloToRuoloRegistro(Ruolo ruolo, String superUser) throws NotFoundException  {
		
		org.openspcoop2.core.registry.Ruolo regRuolo = new org.openspcoop2.core.registry.Ruolo();
		regRuolo.setNome(ruolo.getNome());
		regRuolo.setDescrizione(ruolo.getDescrizione());
		
		FonteEnum tipologiaFonte = ruolo.getFonte();
		if (tipologiaFonte == null)
			tipologiaFonte = FonteEnum.QUALSIASI;
		regRuolo.setTipologia(apiFonteToRegistroTipologia(tipologiaFonte));
		
		ContestoEnum contesto = ruolo.getContesto();
		if (contesto == null)
			contesto = ContestoEnum.QUALSIASI;
		regRuolo.setContestoUtilizzo(apiContestoToRegistroContesto(contesto));

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
		
		ret.setContesto(registroContestoToApiContesto(ruolo.getContestoUtilizzo()));
		ret.setDescrizione(ruolo.getDescrizione());
		ret.setFonte(registroTipologiaToApiFonte(ruolo.getTipologia()));
		ret.setIdentificativoEsterno(ruolo.getNomeEsterno());
		ret.setNome(ruolo.getNome());
		
		return ret;
	}
	
	// TODO: Sarà possibile far unificare a swagger il FonteEnum per il ruoloItem e per il Ruolo?
	public static RuoloItem ruoloApiToRuoloItem(Ruolo ruolo) {
		RuoloItem ret = new RuoloItem();
		ret.setContesto(ruolo.getContesto());
		ret.setFonte(ruolo.getFonte());
		ret.setNome(ruolo.getNome());
		
		return ret;
	}
	

}
