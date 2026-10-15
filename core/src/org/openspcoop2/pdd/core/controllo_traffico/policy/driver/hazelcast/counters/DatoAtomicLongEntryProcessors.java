/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.counters;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.ExtendedMapEntry;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

/**
 * EntryProcessor utilizzati da {@link DatoAtomicLong} per aggiornare in modo atomico i contatori
 * memorizzati nella IMap {@link DatoAtomicLong#MAP_NAME}.
 *
 * Ogni operazione viene eseguita sul member proprietario della partizione della chiave, in modo esclusivo
 * rispetto alle altre operazioni sulla stessa chiave, e replicata sul backup.
 * Una chiave non presente equivale al valore 0, come per un contatore appena creato.
 *
 * La serializzazione avviene tramite {@link IdentifiedDataSerializable}: la factory {@link Factory}
 * viene registrata nella configurazione di ogni istanza Hazelcast da HazelcastManager.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatoAtomicLongEntryProcessors {

	private DatoAtomicLongEntryProcessors() {}

	/** Identificativo della factory: deve essere univoco tra le factory registrate nella configurazione Hazelcast */
	public static final int FACTORY_ID = 1607202601;

	private static final int ADD_AND_GET_ID = 1;
	private static final int SET_ID = 2;
	private static final int COMPARE_AND_SET_ID = 3;

	public static class Factory implements DataSerializableFactory {
		@Override
		public IdentifiedDataSerializable create(int typeId) {
			switch (typeId) {
			case ADD_AND_GET_ID:
				return new AddAndGet();
			case SET_ID:
				return new SetValue();
			case COMPARE_AND_SET_ID:
				return new CompareAndSet();
			default:
				return null;
			}
		}
	}

	private static long getValue(Map.Entry<String, Long> entry) {
		Long v = entry.getValue();
		return v!=null ? v.longValue() : 0l;
	}
	private static void setValue(Map.Entry<String, Long> entry, long value, long ttlMs) {
		if(ttlMs>0 && entry instanceof ExtendedMapEntry<String, Long> extendedEntry) {
			// la scadenza viene ricalcolata ad ogni aggiornamento del contatore
			extendedEntry.setValue(value, ttlMs, TimeUnit.MILLISECONDS);
		}
		else {
			entry.setValue(value);
		}
	}

	private abstract static class AbstractProcessor<R> implements EntryProcessor<String, Long, R>, IdentifiedDataSerializable {

		private static final long serialVersionUID = 1L;

		protected long ttlMs;

		protected AbstractProcessor() {}
		protected AbstractProcessor(long ttlMs) {
			this.ttlMs = ttlMs;
		}

		@Override
		public int getFactoryId() {
			return FACTORY_ID;
		}
		@Override
		public void writeData(ObjectDataOutput out) throws IOException {
			out.writeLong(this.ttlMs);
		}
		@Override
		public void readData(ObjectDataInput in) throws IOException {
			this.ttlMs = in.readLong();
		}
	}

	public static class AddAndGet extends AbstractProcessor<Long> {

		private static final long serialVersionUID = 1L;

		private long delta;

		public AddAndGet() {}
		public AddAndGet(long delta, long ttlMs) {
			super(ttlMs);
			this.delta = delta;
		}

		@Override
		public Long process(Map.Entry<String, Long> entry) {
			long newValue = getValue(entry) + this.delta;
			setValue(entry, newValue, this.ttlMs);
			return newValue;
		}

		@Override
		public int getClassId() {
			return ADD_AND_GET_ID;
		}
		@Override
		public void writeData(ObjectDataOutput out) throws IOException {
			super.writeData(out);
			out.writeLong(this.delta);
		}
		@Override
		public void readData(ObjectDataInput in) throws IOException {
			super.readData(in);
			this.delta = in.readLong();
		}
	}

	public static class SetValue extends AbstractProcessor<Long> {

		private static final long serialVersionUID = 1L;

		private long value;

		public SetValue() {}
		public SetValue(long value, long ttlMs) {
			super(ttlMs);
			this.value = value;
		}

		@Override
		public Long process(Map.Entry<String, Long> entry) {
			setValue(entry, this.value, this.ttlMs);
			return null;
		}

		@Override
		public int getClassId() {
			return SET_ID;
		}
		@Override
		public void writeData(ObjectDataOutput out) throws IOException {
			super.writeData(out);
			out.writeLong(this.value);
		}
		@Override
		public void readData(ObjectDataInput in) throws IOException {
			super.readData(in);
			this.value = in.readLong();
		}
	}

	public static class CompareAndSet extends AbstractProcessor<Boolean> {

		private static final long serialVersionUID = 1L;

		private long expect;
		private long update;

		public CompareAndSet() {}
		public CompareAndSet(long expect, long update, long ttlMs) {
			super(ttlMs);
			this.expect = expect;
			this.update = update;
		}

		@Override
		public Boolean process(Map.Entry<String, Long> entry) {
			if(getValue(entry) != this.expect) {
				return false;
			}
			setValue(entry, this.update, this.ttlMs);
			return true;
		}

		@Override
		public int getClassId() {
			return COMPARE_AND_SET_ID;
		}
		@Override
		public void writeData(ObjectDataOutput out) throws IOException {
			super.writeData(out);
			out.writeLong(this.expect);
			out.writeLong(this.update);
		}
		@Override
		public void readData(ObjectDataInput in) throws IOException {
			super.readData(in);
			this.expect = in.readLong();
			this.update = in.readLong();
		}
	}
}
