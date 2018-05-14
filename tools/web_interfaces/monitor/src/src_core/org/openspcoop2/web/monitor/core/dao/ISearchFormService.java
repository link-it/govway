package org.openspcoop2.web.monitor.core.dao;

import org.openspcoop2.web.monitor.core.bean.AbstractCoreSearchForm;


/**
 * IBaseService definisce un interfaccia tra il livello di accesso ai dati e i Bean della pagina.
 * form: Bean del form (ricerca/CRUD) condiviso con il livello superiore.
 * 
 * @param <T> tipo dell'oggetto
 * @param <K> tipo della chiave dell'oggetto
 * @param <S> bean della ricerca paginata.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 */
public interface ISearchFormService<T, K, S extends AbstractCoreSearchForm> extends IService<T, K> {

	public void setSearch(S search);

	public S getSearch();
	
}
