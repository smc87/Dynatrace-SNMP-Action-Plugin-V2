/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: PduBuilder.java
 * @date: 02.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp;

import static com.dynatrace.diagnostics.plugins.snmp.SnmpActionV2.NO_MESSAGE_OCTET_LIMIT;
import static com.dynatrace.diagnostics.plugins.snmp.SnmpConstants.COMPUWARE_OID;
import static com.dynatrace.diagnostics.plugins.snmp.mapping.MapperFactory.createBasicInfoMapper;
import static java.lang.management.ManagementFactory.getRuntimeMXBean;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.snmp4j.PDU;
import org.snmp4j.Target;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import com.dynatrace.diagnostics.pdk.Incident;


/**
 *
 * @author stefan.moschinski
 */
class PduBuilder {
	private static final Logger log = Logger.getLogger(PduBuilder.class.getName());
	private final TrapPDUFactory pduFactory;
	private final Target target;

	private final Collection<SnmpScalarMapper<? extends Object>> metas;

	/**
	 *
	 * @param pduFactory factory that is used to create a new PDU
	 * @param target {@link Target} of the trap
	 */
	PduBuilder(TrapPDUFactory pduFactory, Target target) {
		this.pduFactory = pduFactory;
		this.target = target;
		this.metas = new ArrayList<SnmpScalarMapper<? extends Object>>();
	}

	PduBuilder addBasicInformation(Incident incident) {
		return map(createBasicInfoMapper(COMPUWARE_OID, 1), incident);
	}

	private <T> PduBuilder map(SnmpScalarMapper<T> mapper, T value) {
		mapper.map(value);
		metas.add(mapper);
		return this;
	}

	PDU create(int openOrEndTrap, int maxMessageOctets) throws UnsupportedEncodingException {
		PDU pdu = pduFactory.createTrapPDU(target, getRuntimeMXBean().getUptime() / 10, COMPUWARE_OID, openOrEndTrap);

		Collection<VariableBinding> variableBindings = createVariableBindings(maxMessageOctets, metas);
		pdu.addAll(variableBindings.toArray(new VariableBinding[variableBindings.size()]));

		return pdu;
	}

	static Collection<VariableBinding> createVariableBindings(int maxMessageOctets, Collection<SnmpScalarMapper<? extends Object>> metas)
			throws UnsupportedEncodingException {

		List<VariableBinding> variableBindings = new ArrayList<VariableBinding>();
		List<OID> skippedOids = new ArrayList<OID>();

		int length = 0;
		for (SnmpScalarMapper<? extends Object> meta : metas) {
			for (Entry<OID, Variable> entry : meta.getOidVariableMappings().entrySet()) {
				OID oid = entry.getKey();
				Variable variable = entry.getValue();

				length += oid.toString().getBytes(SnmpActionV2.UTF_8).length + variable.toString().getBytes(SnmpActionV2.UTF_8).length;
				if (maxMessageOctets == NO_MESSAGE_OCTET_LIMIT || length <= maxMessageOctets) {
					if (log.isLoggable(Level.FINE)) {
						int i = variableBindings.size() + 1;
						log.fine("PduBuilder: create method: line # " + i + ": " + oid + " " + variable);
					}
					variableBindings.add(new VariableBinding(oid, variable));
				} else {
					skippedOids.add(oid);
					if (log.isLoggable(Level.FINE)) {
						log.fine(String.format(
								"Skipping variable binding '%s' - '%s', length of the variableBindings collection was %d octets while max message octets length is set to %d",
								oid, variable, length, maxMessageOctets));
					}
				}
			}
		}

		if (skippedOids.size() > 0) {
			log.warning(String.format(
					"Truncated %d variable binding(s), because the length of variable bindings was %d octets while max message octets length is set to %d (truncated OIDs '%s')",
					skippedOids.size(), length, maxMessageOctets, skippedOids));
		}

		if (log.isLoggable(Level.FINE)) {
			log.fine("PduBuilder: create method: Full set of variable bindings values: " + variableBindings);
		}

		return variableBindings;
	}


}
