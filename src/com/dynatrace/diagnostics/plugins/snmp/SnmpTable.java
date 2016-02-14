/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: SnmpTable.java
 * @date: 12.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp;

import org.snmp4j.smi.OID;



/**
 *
 * @author stefan.moschinski
 */
public interface SnmpTable<T> {

	/**
	 * table entry has always the ID 1
	 */
	int TABLE_ENTRY_ID = 1;

	int getIndex();

	OID getTableOid();

	/**
	 * Adds a new row according to the given value to the table <b>and</b>
	 * (if present) to sub tables
	 * 
	 * @param value value that should be added to the table
	 */
	void addRow(T value);
}
