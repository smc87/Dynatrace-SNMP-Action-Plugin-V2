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

import com.dynatrace.diagnostics.pdk.Measure;
import com.dynatrace.diagnostics.plugins.snmp.AbstractChildTable;


/**
 * 
 * @author stefan.moschinski
 */
class MeasureTable extends AbstractChildTable<Measure> {

	private final OID splittingsOid;
	private final OID agentGroupNameTableOid;

	MeasureTable(OID tableOid, int parentId, OID splittingsTableOid, OID agentGroupNameTableOid) {
		super(tableOid, parentId);
		this.splittingsOid = splittingsTableOid;
		this.agentGroupNameTableOid = agentGroupNameTableOid;
	}


	@Override
	protected Map<OID, Variable> createColumnsForValue(Measure measure) {
		ColumnBuilder columnBuilder =
				childColumnBuilder(this, this.getParentIdx())
						.addColumn(measure.getName())
						.addColumn(measure.getDescription())
						.addColumn(measure.getApplication())
						.addColumn(measure.getConfigurationSummary())
						.addColumn(measure.getUnit())
						// metric:
						.addColumn(measure.getMetric().getName())
						.addColumn(measure.getMetric().getDescription())
						.addColumn(measure.getMetric().getGroup())
						.addColumn(measure.getMetric().getUnit())
						.addThresholdColumn(measure.getUpperSevere())
						.addThresholdColumn(measure.getUpperWarning())
						.addThresholdColumn(measure.getLowerSevere())
						.addThresholdColumn(measure.getLowerWarning());

		SourceMapper sourceMapper = new SourceMapper(measure.getSource());
		sourceMapper.addSourceColumns(columnBuilder);

		Map<OID, Variable> entries = new TreeMap<OID, Variable>();
		entries.putAll(columnBuilder.createColumns());
		entries.putAll(sourceMapper.createAgentGroupTable(agentGroupNameTableOid, createCompoundIdx(getParentIdx(), getIndex())));

		SingleValueTable splittingsTable = new SingleValueTable(splittingsOid, createCompoundIdx(getParentIdx(), getIndex()));
		for (String splitting : measure.getSplittings()) {
			splittingsTable.addRow(splitting);
		}
		
		entries.putAll(splittingsTable.getOidVariableMappings());
		return entries;
	}


}
