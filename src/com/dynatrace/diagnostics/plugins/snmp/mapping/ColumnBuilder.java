/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: ColumnBuilder.java
 * @date: 15.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp.mapping;

import static com.dynatrace.diagnostics.plugins.snmp.SnmpUtil.createCompoundIdx;
import static com.dynatrace.diagnostics.plugins.snmp.SnmpUtil.createOid;
import static com.dynatrace.diagnostics.plugins.snmp.ValueMapper.octetString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import com.dynatrace.diagnostics.pdk.Threshold;
import com.dynatrace.diagnostics.plugins.snmp.SnmpChildTable;
import com.dynatrace.diagnostics.plugins.snmp.SnmpTable;


/**
 * A helper class implementing the builder pattern to easily add new columns to an {@link SnmpTable}.
 * 
 * @author stefan.moschinski
 */
class ColumnBuilder {

	// in a SNMP table the first column is required for the index, so the
	// first actual column is 0 + 2 = 2
	private static final int INDEX_OFFSET = 2;

	private final List<Variable> columns = new ArrayList<Variable>();
	private final SnmpTable<?> table;

	ColumnBuilder addColumn(Object column) {
		columns.add(octetString(column));
		return this;
	}

	ColumnBuilder addThresholdColumn(Threshold threshold) {
		addColumn(threshold == null ? null : threshold.getValue().getValue()); // maybe add also the unit of the value
		return this;
	}

	Map<OID, Variable> createColumns() {
		Map<OID, Variable> map = new TreeMap<OID, Variable>();
		for (int i = 0; i < columns.size(); i++) {
			map.put(createOidInternal(i), columns.get(i));
		}

		return map;
	}

	OID createOidInternal(int i) {
		return createOid(table.getTableOid(), i + INDEX_OFFSET, table.getIndex());
	}

	static ColumnBuilder columnBuilder(SnmpTable<?> table) {
		return new ColumnBuilder(table);
	}

	static ColumnBuilder childColumnBuilder(final SnmpChildTable<?> table, final int[] parentIdx) {
		return new ColumnBuilder(table) {

			@Override
			OID createOidInternal(int i) {
				return createOid(table.getTableOid(), i + INDEX_OFFSET, createCompoundIdx(parentIdx, table.getIndex()));
			}
		};
	}

	private ColumnBuilder(SnmpTable<?> table) {
		this.table = table;
	}
}
