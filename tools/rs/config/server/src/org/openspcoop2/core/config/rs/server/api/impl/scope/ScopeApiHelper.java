package org.openspcoop2.core.config.rs.server.api.impl.scope;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.model.ContestoEnum;
import org.openspcoop2.core.config.rs.server.model.Scope;
import org.openspcoop2.core.config.rs.server.model.ScopeItem;
import org.openspcoop2.core.registry.constants.ScopeContesto;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeCostanti;

public class ScopeApiHelper {
	
	public static final Map<ContestoEnum,ScopeContesto> apiContestoToRegistroContesto = new HashMap<>();
	static {
		apiContestoToRegistroContesto.put(ContestoEnum.EROGAZIONE,ScopeContesto.PORTA_APPLICATIVA);
		apiContestoToRegistroContesto.put(ContestoEnum.FRUIZIONE,ScopeContesto.PORTA_DELEGATA);
		apiContestoToRegistroContesto.put(ContestoEnum.QUALSIASI,ScopeContesto.QUALSIASI);
	}
	
	public static final Map<ScopeContesto,ContestoEnum> registroContestoToApiContesto = new HashMap<>();
	static {
		apiContestoToRegistroContesto.forEach( (ac,rc) -> registroContestoToApiContesto.put(rc, ac));
	}
	

	public static final org.openspcoop2.core.registry.Scope apiScopeToRegistroScope(Scope s, String userLogin) {
		org.openspcoop2.core.registry.Scope ret = new org.openspcoop2.core.registry.Scope();
		
		ret.setNome(s.getNome());
		if (s.getIdentificativoEsterno() != null)
			ret.setNomeEsterno(s.getIdentificativoEsterno().trim());
		
		ret.setContestoUtilizzo(apiContestoToRegistroContesto.get(s.getContesto()));
		ret.setDescrizione(s.getDescrizione());
		ret.setSuperUser(userLogin);
		
		return ret;
	}
	
	public static final ScopeItem registroScopeToScopeItem(org.openspcoop2.core.registry.Scope scope) {
		ScopeItem ret = new ScopeItem();
		ret.setContesto(registroContestoToApiContesto.get(scope.getContestoUtilizzo()));
		ret.setNome(scope.getNome());
		return ret;
	}
	
	public static final Scope registroScopeToScope(org.openspcoop2.core.registry.Scope scope) {
		Scope ret = new Scope();
		ret.setContesto(registroContestoToApiContesto.get(scope.getContestoUtilizzo()));
		ret.setNome(scope.getNome());
		ret.setDescrizione(scope.getDescrizione());
		ret.setIdentificativoEsterno(scope.getNomeEsterno());
		return ret;
	}
	
	public static final HttpRequestWrapper ovverrideParameters(HttpServletRequest req, Scope scope) {
		HttpRequestWrapper wrap = new HttpRequestWrapper(req);

		wrap.overrideParameter(ScopeCostanti.PARAMETRO_SCOPE_NOME, scope.getNome());
		wrap.overrideParameter(ScopeCostanti.PARAMETRO_SCOPE_DESCRIZIONE, scope.getDescrizione());
		wrap.overrideParameter(ScopeCostanti.PARAMETRO_SCOPE_NOME_ESTERNO, scope.getIdentificativoEsterno());
		
		return wrap;
	}
}
