package org.openspcoop2.core.transazioni.utils;

import java.util.Enumeration;
import java.util.Hashtable;

import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.utils.Utilities;

public class CredenzialiMittenteUtils {

	public static IPaginatedExpression createCredenzialeMittentePaginatedExpression(ICredenzialeMittenteService credenzialeMittentiService,
    		TipoCredenzialeMittente tipoCredenziale, String tipoAutenticazione, String credential,
    		boolean ricercaEsatta, boolean caseSensitive) throws Exception {
    	IPaginatedExpression pagEpression = credenzialeMittentiService.newPaginatedExpression();
		pagEpression.and();
		if(TipoCredenzialeMittente.trasporto.equals(tipoCredenziale)) {
			pagEpression.equals(CredenzialeMittente.model().TIPO, tipoCredenziale.name()+"_"+tipoAutenticazione);
		}
		else {
			pagEpression.equals(CredenzialeMittente.model().TIPO, tipoCredenziale.name());
		}
		if(TipoCredenzialeMittente.trasporto.equals(tipoCredenziale) && TipoAutenticazione.SSL.getValue().equalsIgnoreCase(tipoAutenticazione)) {
			Hashtable<String, String> hashSubject = Utilities.getSubjectIntoHashtable(credential);			
			Enumeration<String> keys = hashSubject.keys();			
			while(keys.hasMoreElements()){				
				String key = keys.nextElement();				
				String value = hashSubject.get(key);
				if(caseSensitive) {
					pagEpression.like(CredenzialeMittente.model().CREDENZIALE, "/"+Utilities.formatKeySubject(key)+"="+Utilities.formatValueSubject(value)+"/", LikeMode.ANYWHERE);
				}
				else {
					pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, "/"+Utilities.formatKeySubject(key)+"="+Utilities.formatValueSubject(value)+"/", LikeMode.ANYWHERE);
				}
			}				
		}else {
			if(ricercaEsatta && caseSensitive) {
				pagEpression.equals(CredenzialeMittente.model().CREDENZIALE, credential);
			}
			else if(ricercaEsatta && !caseSensitive) {
				pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, credential, LikeMode.EXACT);
			}
			else if(!ricercaEsatta && !caseSensitive) {
				pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, credential, LikeMode.ANYWHERE);
			}
			else { // !ricercaEsatta && caseSensitive
				pagEpression.like(CredenzialeMittente.model().CREDENZIALE, credential, LikeMode.ANYWHERE);
			}
		}
		return pagEpression;
    }
    
	
}
