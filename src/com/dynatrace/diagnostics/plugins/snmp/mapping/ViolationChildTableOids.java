/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: ViolationChildTableOids.java
 * @date: 17.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp.mapping;

import static com.dynatrace.diagnostics.plugins.snmp.SnmpUtil.checkNotNull;

import org.snmp4j.smi.OID;


/**
 * We defensively copy all values provided by the getter methods of this class.
 * The {@link OID} class is not immutable.
 * 
 * @author stefan.moschinski
 */
final class ViolationChildTableOids {

	private final OID splittingsTableOid;
	private final OID measureAgentGroupTableOid;
	private final OID measureTableOid;
	private final OID triggerValueAgentMappingTableOid;
	private final OID triggerValuOid;

	/**
	 * 
	 * @param measureTableOid
	 * @param measureAgentGroupTableOid
	 * @param splittingsTableOid
	 * @param triggerValueAgentMappingTableOid
	 */
	ViolationChildTableOids(OID measureTableOid, OID measureAgentGroupTableOid, OID splittingsTableOid, OID triggerValuOid, OID triggerValueAgentMappingTableOid) {
		String template = "The OID for the table '%s' is not allowed to be null";
		this.measureTableOid = checkNotNull(measureTableOid, template, "measureTableOid");
		this.splittingsTableOid = checkNotNull(splittingsTableOid, template, "splittingsTableOid");
		this.measureAgentGroupTableOid = checkNotNull(measureAgentGroupTableOid, template, "measureAgentGroupTableOid");
		this.triggerValuOid = checkNotNull(triggerValuOid, template, "triggerValuOid");
		this.triggerValueAgentMappingTableOid = checkNotNull(triggerValueAgentMappingTableOid, template, "triggerValueAgentMappingTableOid");
	}

	/**
	 * 
	 * @return
	 */
	OID getMeasureTableOid() {
		return new OID(measureTableOid);
	}


	/**
	 * @return the splittingsTableOid
	 */
	OID getSplittingsTableOid() {
		return new OID(splittingsTableOid);
	}


	/**
	 * @return the agentGroupTableOid
	 */
	OID getMeasureAgentGroupTableOid() {
		return new OID(measureAgentGroupTableOid);
	}


	/**
	 * @return the triggerValueAgentMappingTableOid
	 */
	OID getTriggerValueAgentMappingTableOid() {
		return new OID(triggerValueAgentMappingTableOid);
	}

	/**
	 * 
	 * @return
	 */
	OID getTriggerValueTableOid() {
		return new OID(triggerValuOid);
	}



}
