/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: SnmpConstants.java
 * @date: 03.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp;

import org.snmp4j.smi.OID;


/**
 *
 * @author stefan.moschinski
 */
public final class SnmpConstants {

	public final static OID COMPUWARE_OID = new OID(new int[] { 1, 3, 6, 1, 4, 1, 31094 });

	public final static String INCIDENT_TRAP_START = "dynaTraceIncidentStart";
	public final static String INCIDENT_TRAP_END = "dynaTraceIncidentEnd";


	private SnmpConstants() {
	};
}
