/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 * 
 * @file: SnmpViolationMeasureMarshaller.java
 * @date: 03.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp.mapping;

import static com.dynatrace.diagnostics.plugins.snmp.mapping.ColumnBuilder.columnBuilder;

import java.util.Collection;
import java.util.Map;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import com.dynatrace.diagnostics.pdk.Violation;
import com.dynatrace.diagnostics.pdk.Violation.TriggerValue;
import com.dynatrace.diagnostics.plugins.snmp.SnmpAbstractTable;


/**
 * 
 * @author stefan.moschinski
 */
class ViolationTable extends SnmpAbstractTable<Violation> {


	private final ViolationChildTableOids childTableOids;

	ViolationTable(OID tableOid, ViolationChildTableOids violationChildTableOids) {
		super(tableOid);
		this.childTableOids = violationChildTableOids;

	}


	@Override
	protected Map<OID, Variable> createColumnsForValue(Violation violation) {
		ColumnBuilder columnBuilder =
				columnBuilder(this)
						.addThresholdColumn(violation.getViolatedThreshold());
		Map<OID, Variable> violationTableEntries = columnBuilder.createColumns();

		MeasureTable measureTable = new MeasureTable(
				childTableOids.getMeasureTableOid(),
				getIndex(),
				childTableOids.getSplittingsTableOid(),
				childTableOids.getMeasureAgentGroupTableOid());
		measureTable.addRow(violation.getViolatedMeasure());
		violationTableEntries.putAll(measureTable.getOidVariableMappings());

		Map<OID, Variable> oidVariableMappings = getTriggerValues(violation);
		violationTableEntries.putAll(oidVariableMappings);

		return violationTableEntries;
	}

	private Map<OID, Variable> getTriggerValues(Violation violation) {
		TriggerValueTable triggerValueTable = new TriggerValueTable(childTableOids.getTriggerValueTableOid(), getIndex(), childTableOids.getTriggerValueAgentMappingTableOid());
		Collection<TriggerValue> triggerValues = violation.getTriggerValues();
		for (TriggerValue triggerValue : triggerValues) {
			triggerValueTable.addRow(triggerValue);
		}
		return triggerValueTable.getOidVariableMappings();
	}



}
