/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: TrapSender.java
 * @date: 03.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp;

import static com.dynatrace.diagnostics.plugins.snmp.SnmpConstants.INCIDENT_TRAP_END;
import static com.dynatrace.diagnostics.plugins.snmp.SnmpConstants.INCIDENT_TRAP_START;
import static com.dynatrace.diagnostics.plugins.snmp.ValueMapper.octetString;
import static com.dynatrace.diagnostics.sdk.resources.BaseConstants.FSLASH;
import static java.lang.String.format;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.logging.Logger;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.UdpAddress;

import com.dynatrace.diagnostics.pdk.Incident;
import com.dynatrace.diagnostics.pdk.Sensitivity.Type;
import com.dynatrace.diagnostics.pdk.Violation;
import com.dynatrace.diagnostics.pdk.Violation.TriggerValue;


/**
 *
 * @author stefan.moschinski
 */
class TrapSender {

	private final String host;
	private final int port;
	private final int version;
	private final String community;
	private final TrapPDUFactory pduFactory;
	private final Snmp snmp;
	private final int maxMessageOctets;
	private final boolean suppressParent;

	private static final Logger log = Logger.getLogger(TrapSender.class.getName());

	TrapSender(Snmp snmp, TrapPDUFactory pduFactory,
			String host, int port, int version, String community, String systemProfileName, int maxMessageOctets, boolean suppressParent) {
		this.snmp = snmp;
		this.pduFactory = pduFactory;
		this.host = host;
		this.port = port;
		this.version = version;
		this.community = community;
		this.maxMessageOctets = maxMessageOctets;
		this.suppressParent = suppressParent;
	}

	void sendIncidents(Collection<Incident> incidents) throws IOException {
		for (Incident incident : incidents) {
			if (suppressParent) {
				boolean suppressed = false;
				for (Violation violation : incident.getViolations()) {
					for (TriggerValue triggerValue : violation.getTriggerValues()) {
						if (triggerValue.getSource().toString().contains("<all-agents>")) {
						log.fine(triggerValue.getSource().toString());
						log.fine("Supressing <all-agents> incident: " + incident.getMessage());
						suppressed = true;
						}
					}
				}
				if (suppressed)
					continue;
			if (incident == null || incident.getIncidentRule() == null)
				continue;
			if (incident.getIncidentRule().getSensitivity().getType() == Type.PerViolation) {
				sendNotification(incident, INCIDENT_TRAP_START);
				sendNotification(incident, INCIDENT_TRAP_END);
			} else {
				final String openOrEndTrap = incident.isOpen() ? INCIDENT_TRAP_START : INCIDENT_TRAP_END;
				sendNotification(incident, openOrEndTrap);

					}
				}
					
				
			
		}
	}

	private void sendNotification(Incident incident, String openOrEndTrap)
			throws IOException {
		Target target = getTarget();
		PDU pdu = createPdu(incident, incident.getKey().getSystemProfile(), getIntValue(openOrEndTrap), target, maxMessageOctets);

		snmp.listen();

		ResponseEvent event = snmp.send(pdu, target);
		if (event != null) {
			PDU response = event.getResponse();
			if (response != null) {
				if (response.getErrorStatus() != SnmpConstants.SNMP_ERROR_SUCCESS) {
					log.warning("SNMP Error: " + response.getErrorStatusText());
				}
			}
		}
	}

	private int getIntValue(String trapStr) {
		if (INCIDENT_TRAP_START.equals(trapStr)) {
			return 1;
		} else if (INCIDENT_TRAP_END.equals(trapStr)) {
			return 2;
		}
		throw new IllegalArgumentException(format("The trap value '%s' is unknown", trapStr));
	}

	private PDU createPdu(Incident incident, String systemProfileName, int trapType, Target target, int maxMessageOctets) throws UnsupportedEncodingException {
		PduBuilder pduBuilder = new PduBuilder(pduFactory, target)
				.addBasicInformation(incident);

		PDU pdu = pduBuilder.create(trapType, maxMessageOctets);
		return pdu;
	}

	private Target getTarget() {
		CommunityTarget target = new CommunityTarget();
		target.setAddress(new UdpAddress(host + FSLASH + String.valueOf(port)));
		target.setVersion(version);
		target.setCommunity(octetString(community));
		return target;
	}



}
