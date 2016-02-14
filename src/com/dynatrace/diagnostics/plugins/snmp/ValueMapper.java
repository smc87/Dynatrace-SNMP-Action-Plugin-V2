package com.dynatrace.diagnostics.plugins.snmp;

import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;

/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: ValueMapper.java
 * @date: 16.04.2013
 * @author: stefan.moschinski
 */

/**
 * Convenience class that maps the Java values to the respective SNMP counterpart.
 * 
 * @author stefan.moschinski
 */
public final class ValueMapper {

	private static final String DEFAULT_NO_VALUE_STR = "(null)";

	/**
	 * 
	 * @param str String value that should be converted to an {@link OctetString}
	 * @return the passed String value represented as an {@link OctetString}
	 */
	public static OctetString octetString(String str) {
		return new OctetString(defaultIfNull(str));
	}

	/**
	 * 
	 * @param val Object value that should be converted to an {@link OctetString}
	 * @return the passed Object value represented as an {@link OctetString}
	 */
	public static Variable octetString(Object val) {
		return new OctetString(defaultIfNull(val));
	}

	/**
	 * Converts the given long value to {@link TimeTicks}
	 * 
	 * @param val long value that should be converted to time ticks
	 * @return the passed long value represented as {@link TimeTicks}
	 */
	public static TimeTicks timeTicks(long val) {
		return new TimeTicks(val);
	}

	/**
	 * Converts the int value to a {@link UnsignedInteger32}
	 * 
	 * @param val int value
	 * @return {@link UnsignedInteger32}
	 */
	public static Variable unsignedInt(int val) {
		return new UnsignedInteger32(val);
	}

	private static String defaultIfNull(Object val) {
		return val == null ? DEFAULT_NO_VALUE_STR : val.toString();
	}

	private ValueMapper() {
		// not for initialization
	}



}
