package com.dynatrace.diagnostics.plugins.snmp;

import static com.dynatrace.diagnostics.pdk.Status.StatusCode.ErrorInfrastructure;
import static com.dynatrace.diagnostics.pdk.Status.StatusCode.Success;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.snmp4j.Snmp;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.dynatrace.diagnostics.pdk.ActionEnvironment;
import com.dynatrace.diagnostics.pdk.ActionV2;
import com.dynatrace.diagnostics.pdk.Status;

public class SnmpActionV2 implements ActionV2 {
	private static final Logger log = Logger.getLogger(SnmpActionV2.class.getName());

	private static final String ENV_CONFIG_STRING_HOST = "host";
	private static final String ENV_CONFIG_STRING_PORT = "port";
	private static final String ENV_CONFIG_STRING_VERSION = "version";
	private static final String ENV_CONFIG_STRING_COMMUNITY = "community";
	private static final String ENV_CONFIG_STRING_SUPPRESSPARENT = "suppressParent";
	private static final String	MAX_MESSAGE_OCTETS = "octets";

	static final int NO_MESSAGE_OCTET_LIMIT = -1;
	static final String UTF_8 = "UTF-8";

	private Snmp snmp;
	private TrapPDUFactory pduFactory;

	@Override
	public Status setup(ActionEnvironment env) throws Exception {
		snmp = new Snmp(new DefaultUdpTransportMapping());
		pduFactory = new TrapPDUFactory();

		return new Status(Success);
	}

	// @TestOnly
	public Status setup(Snmp snmp) throws Exception {
		this.snmp = snmp;
		pduFactory = new TrapPDUFactory();

		return new Status(Success);
	}

	@Override
	public Status execute(ActionEnvironment env) throws Exception {
		int port = env.getConfigLong(ENV_CONFIG_STRING_PORT).intValue();
		String version = env.getConfigString(ENV_CONFIG_STRING_VERSION);
		String host = env.getConfigString(ENV_CONFIG_STRING_HOST);
		String community = env.getConfigString(ENV_CONFIG_STRING_COMMUNITY);
		boolean suppressParent = env.getConfigBoolean(ENV_CONFIG_STRING_SUPPRESSPARENT);
		String systemProfile = env.getSystemProfileName();
		int maxMessageOctets = getMaxMessageOctets(env.getConfigLong(MAX_MESSAGE_OCTETS));

		TrapSender trapSender = new TrapSender(snmp, pduFactory, host, port, getSnmpIntVersion(version), community, systemProfile, maxMessageOctets, suppressParent);
		try {
			trapSender.sendIncidents(env.getIncidents());
		} catch (IOException e) {
			log.log(Level.WARNING, String.format("Could not send trap notification to %s:%s", host, port), e);
			return new Status(ErrorInfrastructure, "unable to send notification", "unable to send notification", e);
		}
		return new Status(Success);
	}

	static int getMaxMessageOctets(Long maxOctets) {
		int maxOctects = maxOctets == null ? NO_MESSAGE_OCTET_LIMIT : maxOctets.intValue();
		return maxOctects > 0 ? maxOctects : NO_MESSAGE_OCTET_LIMIT;
	}

	@Override
	public void teardown(ActionEnvironment env) throws Exception {
		if (snmp != null) {
			snmp.close();
		}
	}

	private static int getSnmpIntVersion(String version) {
		if (version.equals("SNMPv1")) {
			return SnmpConstants.version1;
		} else if (version.equals("SNMPv2c")) {
			return SnmpConstants.version2c;
		} else if (version.equals("SNMPv3")) {
			// is it relevant for SNMP ?
			return SnmpConstants.version3;
		} else {
			log.warning("Unknown SNMP Version: " + version);
		}

		return SnmpConstants.version2c;
	}



}