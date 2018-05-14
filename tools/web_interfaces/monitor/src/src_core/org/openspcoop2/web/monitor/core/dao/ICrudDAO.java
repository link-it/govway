package org.openspcoop2.web.monitor.core.dao;

import java.util.List;

public interface ICrudDAO<T,K> {

	public void store(T obj) throws Exception;
    public void deleteById(K key);
    public void delete(T obj) throws Exception;
    public void deleteAll() throws Exception;
    public T findById(K key);
    public List<T> findAll();
	
}
