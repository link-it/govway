/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.web.impl.jsf1.datamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import javax.faces.context.FacesContext;
 
import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.ajax4jsf.model.SerializableDataModel;
 

/***
 * 
 * Implementazione base per un datamodel con supporto alla paginazione.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 * @param <T> Tipo del bean.
 * @param <U> Tipo della chiave del bean.
 */
public abstract class PaginatingDataModel<T,U> extends SerializableDataModel {
    /** */
    private static final long serialVersionUID = 2954923950179861809L;
    /** */
    protected U currentPk;
    /** */
    protected boolean descending = true;
    /** */
    protected String sortField = getDefaultSortField();
    /** */
    protected boolean detached = false;
    /** */
    protected List<U> wrappedKeys = new ArrayList<U>();
    /** */
    protected Integer rowCount;
    /** */
    protected Map<U,T> wrappedData = new HashMap<U,T>();
 
    /**
     * @see org.ajax4jsf.model.ExtendedDataModel#getRowKey()
     */
    @Override
    public Object getRowKey()
    {
        return this.currentPk;
    }
 
    /**
     * @see org.ajax4jsf.model.ExtendedDataModel#setRowKey(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setRowKey(final Object key)
    {
        this.currentPk = (U) key;
    }
 
    /**
     * @see org.ajax4jsf.model.SerializableDataModel#update()
     */
    @Override
    public void update()
    {
        if (getSortFieldObject() != null)
        {
            final String newSortField = getSortFieldObject().toString();
            if (newSortField.equals(this.sortField))
            {
                this.descending = !this.descending;
            }
            this.sortField = newSortField;
        }
        this.detached = false;
    }
 
    /**
     * @return Object
     */
    protected Object getSortFieldObject()
    {
        final FacesContext context = FacesContext.getCurrentInstance();
        final Object sortFieldObject = context.getExternalContext().getRequestParameterMap().get("sortField");
        return sortFieldObject;
    }
 
    /**
     * @param sortField
     */
    public void setSortField(final String sortField)
    {
        if (this.sortField.equals(sortField))
        {
            this.descending = !this.descending;
        } else
        {
            this.sortField = sortField;
        }
    }
 
    /**
     * @return String
     */
    public String getSortField()
    {
        return this.sortField;
    }
 
    /**
     * @see org.ajax4jsf.model.ExtendedDataModel#getSerializableModel(org.ajax4jsf.model.Range)
     */
    @Override
    public SerializableDataModel getSerializableModel(final Range range)
    {
        if (this.wrappedKeys != null)
        {
            this.detached = true;
            return this;
        }
        return null;
    }
 
    /**
     * @see javax.faces.model.DataModel#setRowIndex(int)
     */
    @Override
    public void setRowIndex(final int rowIndex)
    {
        throw new UnsupportedOperationException();
 
    }
 
    /**
     * @see javax.faces.model.DataModel#setWrappedData(java.lang.Object)
     */
    @Override
    public void setWrappedData(final Object data)
    {
        throw new UnsupportedOperationException();
 
    }
 
    /**
     * @see javax.faces.model.DataModel#getRowIndex()
     */
    @Override
    public int getRowIndex()
    {
        throw new UnsupportedOperationException();
    }
 
    /**
     * @see javax.faces.model.DataModel#getWrappedData()
     */
    @Override
    public Object getWrappedData()
    {
        throw new UnsupportedOperationException();
    }
 
    /**
     * @see org.ajax4jsf.model.ExtendedDataModel#walk(javax.faces.context.FacesContext,
     *      org.ajax4jsf.model.DataVisitor, org.ajax4jsf.model.Range,
     *      java.lang.Object)
     */
    @Override
    public void walk(final FacesContext context, final DataVisitor visitor, final Range range, final Object argument)
            throws IOException
    {
        final int firstRow = ((SequenceRange) range).getFirstRow();
        final int numberOfRows = ((SequenceRange) range).getRows();
        if (this.detached && getSortFieldObject() != null)
        {
            for (final U key : this.wrappedKeys)
            {
                setRowKey(key);
                visitor.process(context, key, argument);
            }
        } else
        { // if not serialized, than we request data from data
            // provider
            this.wrappedKeys = new ArrayList<U>();
            for (final T object : findObjects(firstRow, numberOfRows, this.sortField, this.descending))
            {
                this.wrappedKeys.add(getId(object));
                this.wrappedData.put(getId(object), object);
                visitor.process(context, getId(object), argument);
            }
        }
    }
 
    /**
     * @see javax.faces.model.DataModel#isRowAvailable()
     */
    @Override
    public boolean isRowAvailable()
    {
        if (this.currentPk == null)
        {
            return false;
        }
        if (this.wrappedKeys.contains(this.currentPk))
        {
            return true;
        }
        if (this.wrappedData.entrySet().contains(this.currentPk))
        {
            return true;
        }
        try
        {
            if (getObjectById(this.currentPk) != null)
            {
                return true;
            }
        } catch (final Exception e)
        {
 
        }
        return false;
    }
 
    /**
     * @see javax.faces.model.DataModel#getRowData()
     */
    @Override
    public Object getRowData()
    {
        if (this.currentPk == null)
        {
            return null;
        }
 
        T object = this.wrappedData.get(this.currentPk);
        if (object == null)
        {
            object = getObjectById(this.currentPk);
            this.wrappedData.put(this.currentPk, object);
        }
        return object;
    }
 
    /**
     * @see javax.faces.model.DataModel#getRowCount()
     */
    @Override
    public int getRowCount()
    {
        if (this.rowCount == null)
        {
            this.rowCount = getNumRecords();
        }
        return this.rowCount;
    }
 
    /**
     * 
     * @param object
     * @return U
     */
    public abstract U getId(T object);
 
    /**
     * @param firstRow
     * @param numberOfRows
     * @param sortField
     * @param descending
     * @return List
     */
    public abstract List<T> findObjects(int firstRow, int numberOfRows, String sortField, boolean descending);
 
    /**
     * @param id
     * @return T
     */
    public abstract T getObjectById(U id);
 
    /**
     * @return String
     */
    public abstract String getDefaultSortField();
 
    /**
     * @return int
     */
    public abstract int getNumRecords();
}