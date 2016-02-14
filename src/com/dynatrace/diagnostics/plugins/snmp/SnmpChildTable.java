/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: SnmpTable.java
 * @date: 12.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp;




/**
 *
 * @author stefan.moschinski
 */
public interface SnmpChildTable<T> extends SnmpTable<T> {


	/**
	 * 
	 * @return indexes of the parent entries
	 */
	int[] getParentIdx();


}
