/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 * 
 * @file: SnmpViolationMeasureMarshaller.java
 * @date: 03.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp.mapping;

import static com.dynatrace.diagnostics.plugins.snmp.mapping.ColumnBuilder.childColumnBuilder;

import java.util.Map;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import com.dynatrace.diagnostics.plugins.snmp.AbstractChildTable;


/**
 * 
 * @author stefan.moschinski
 */
class SingleValueTable extends AbstractChildTable<String> {

	SingleValueTable(OID tableOid, int... parentIdx) {
		super(tableOid, parentIdx);
	}

	@Override
	protected Map<OID, Variable> createColumnsForValue(String value) {
		return childColumnBuilder(this, this.getParentIdx())
				.addColumn(value)
				.createColumns();
	}

}
