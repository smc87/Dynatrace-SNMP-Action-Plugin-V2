/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: ViolationMapper.java
 * @date: 12.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp.mapping;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import com.dynatrace.diagnostics.pdk.Violation;
import com.dynatrace.diagnostics.plugins.snmp.SnmpScalarMapper;


/**
 *
 * @author stefan.moschinski
 */
class ViolationMapper extends SnmpScalarMapper<Collection<Violation>> {


	private final ViolationTable violationTable;


	/**
	 * 
	 * @param parentOid
	 * @param number
	 * @param violationChildTableOids
	 */
	ViolationMapper(OID parentOid, int number, ViolationChildTableOids violationChildTableOids) {
		super(parentOid, number);
		this.violationTable = new ViolationTable(this.oid, violationChildTableOids);
	}

	@Override
	public void map(Collection<Violation> violations) {
		for (Violation violation : violations) {
			violationTable.addRow(violation);
		}

		Map<OID, Variable> mapping = violationTable.getOidVariableMappings();
		for (Entry<OID, Variable> entry : mapping.entrySet()) {
			addVariable(entry.getKey(), entry.getValue());
		}
	}



}
