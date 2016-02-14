/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 * 
 * @file: SnmpViolationMeasureMarshaller.java
 * @date: 03.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp.mapping;

import static com.dynatrace.diagnostics.plugins.snmp.SnmpUtil.createCompoundIdx;
import static com.dynatrace.diagnostics.plugins.snmp.mapping.ColumnBuilder.childColumnBuilder;

import java.util.Map;
import java.util.TreeMap;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import com.dynatrace.diagnostics.pdk.Violation.TriggerValue;
import com.dynatrace.diagnostics.plugins.snmp.AbstractChildTable;
/**
 * 
 * @author stefan.moschinski
 */
class TriggerValueTable extends AbstractChildTable<TriggerValue> {

	private final OID triggerValueAgentGroupOid;

	TriggerValueTable(OID tableOid, int parentIdx, OID triggerValueAgentGroupOid) {
		super(tableOid, parentIdx);
		this.triggerValueAgentGroupOid = triggerValueAgentGroupOid;
	}


	@Override
	protected Map<OID, Variable> createColumnsForValue(TriggerValue triggerValue) {
		ColumnBuilder columnBuilder =
				childColumnBuilder(this, this.getParentIdx())
						.addColumn(triggerValue.getValue().getValue());

		SourceMapper sourceBlub = new SourceMapper(triggerValue.getSource());
		sourceBlub.addSourceColumns(columnBuilder);

		Map<OID, Variable> entries = new TreeMap<OID, Variable>();
		entries.putAll(columnBuilder.createColumns());
		entries.putAll(sourceBlub.createAgentGroupTable(triggerValueAgentGroupOid, createCompoundIdx(getParentIdx(), getIndex())));

		return entries;
	}


}
