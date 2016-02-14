/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 * 
 * @file: AbstractSnmpTable.java
 * @date: 15.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp;

import java.util.Map;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;


/**
 * 
 * @author stefan.moschinski
 */
public abstract class SnmpAbstractTable<T> extends SnmpManagedObject<T> implements SnmpTable<T> {

	private int index = 1;

	public SnmpAbstractTable(OID tableOid) {
		super(tableOid, TABLE_ENTRY_ID);
	}

	@Override
	public final void addRow(T value) {
		Map<OID, Variable> columns = createColumnsForValue(value);
		getOidVariableMappings().putAll(columns);
		index++;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	protected abstract Map<OID, Variable> createColumnsForValue(T value);

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public OID getTableOid() {
		return oid;
	}


}