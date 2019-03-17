package execution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import data.Settings;

public class CommandExecutioner {

	public Settings settings;
	public String status = "";

	public static boolean isConnecting = false;

	public static String execute(String command) {
		StringBuilder response = new StringBuilder();

		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line + "\n");
			}

			int exitVal = process.waitFor();
			if (exitVal == 0) {
				return response.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private List<String> getList(String resp) {
		List<String> list = new ArrayList<String>();

		String[] split = resp.split("\\s");

		for (int i = 0; i < split.length; i++) {
			if (split[i].length() > 0) {
				char c = split[i].charAt(0);
				if (c >= 'A' && c <= 'Z') {
					list.add(split[i].replaceAll("_", " "));
				}
			}
		}

		return list;
	}

	public List<String> getCountries() {
		String resp = execute("nordvpn countries");

		return getList(resp);
	}

	public List<String> getCities(String country) {
		String resp = execute("nordvpn cities " + country);

		return getList(resp);
	}

	public void refreshSettigns() {
		String resp = execute("nordvpn settings");
		resp = resp.substring(6);

		String protocol = resp.split("Protocol:")[1];
		protocol = protocol.split("Kill Switch:")[0];

		String killSwitch = resp.split("Kill Switch:")[1];
		killSwitch = killSwitch.split("CyberSec:")[0];

		String cyberSec = resp.split("CyberSec:")[1];
		cyberSec = cyberSec.split("Obfuscate:")[0];

		String obfuscate = resp.split("Obfuscate:")[1];
		obfuscate = obfuscate.split("Auto connect:")[0];

		String autoConnect = resp.split("Auto connect:")[1];
		autoConnect = autoConnect.split("DNS:")[0];

		String dns;

		String whitelistedPorts = "";
		if (resp.contains("Whitelisted ports")) {
			whitelistedPorts = resp.split("Whitelisted ports:")[1];

			dns = resp.split("DNS:")[1];
			dns = dns.split("Whitelisted ports:")[0];
		} else {
			dns = resp.split("DNS:")[1];
		}

		if (settings == null) {
			settings = new Settings(protocol, killSwitch, cyberSec, obfuscate, autoConnect, dns, whitelistedPorts);
		} else {
			settings.Set(protocol, killSwitch, cyberSec, obfuscate, autoConnect, dns, whitelistedPorts);
		}
	}

	public String whitelistAdd(String port) {
		String resp = execute("nordvpn whitelist add port " + port);

		return resp;
	}

	public String whitelistAddProtocol(String port, String protocol) {
		String resp = execute("nordvpn whitelist add port " + port + " protocol " + protocol);

		return resp;
	}

	public String whitelistRemove(String port) {
		String resp = execute("nordvpn whitelist remove port " + port);

		return resp;
	}

	public String whitelistRemoveProtocol(String port, String protocol) {
		String resp = execute("nordvpn whitelist remove port " + port + " protocol " + protocol);

		return resp;
	}

	public String setKillSwitch(boolean active) {

		String resp;
		if (active) {
			resp = execute("nordvpn set killswitch on");
		} else {
			resp = execute("nordvpn set killswitch off");
		}

		resp = "Kill" + resp.split("Kill")[1];
		return resp;
	}

	public String setCybersec(boolean active) {

		String resp;
		if (active) {
			resp = execute("nordvpn set cybersec on");
		} else {
			resp = execute("nordvpn set cybersec off");
		}

		resp = "CyberSec" + resp.split("CyberSec")[1];
		return resp;
	}

	public String setAutoconnect(boolean active) {

		String resp;
		if (active) {
			resp = execute("nordvpn set autoconnect on");
		} else {
			resp = execute("nordvpn set autoconnect off");
		}

		resp = "Auto" + resp.split("Auto")[1];
		return resp;
	}

	public String setObfuscate(boolean active) {

		String resp;
		if (active) {
			resp = execute("nordvpn set obfuscate on");
		} else {
			resp = execute("nordvpn set obfuscate off");
		}

		resp = "Obfuscation" + resp.split("Obfuscation")[1];
		return resp;
	}

	public String setProtocol(String protocol) {

		String resp = execute("nordvpn set protocol " + protocol);

		resp = "Protocol" + resp.split("Protocol")[1];
		resp = resp.replaceAll("\\. ", "\n");
		return resp;
	}

	public String setDNS(String dns) {

		String resp = execute("nordvpn set dns " + dns);

		try {
			resp = "DNS" + resp.split("DNS")[1];
			resp = resp.replaceAll("\\. ", "\n");
		} catch (Exception e) {
			return "The DNS you entered is not valid";
		}
		return resp;
	}

	public void disableDNS() {
		execute("nordvpn set dns disable");
	}

	public String getStatus() {
		String resp = execute("nordvpn status");

		resp = "Status" + resp.split("Status")[1];
		status = resp;
		return resp;
	}

	public void disconnect() {
		execute("nordvpn d");
	}

	public void connect(String Country, String City) {
		isConnecting = true;
		execute("nordvpn connect " + Country + " " + City);
		isConnecting = false;
	}

	public void logout() {
		execute("nordvpn logout");
	}

	public boolean login(String username, String password) {

		try {
			execute("mkdir NordVPN-gui_data");
			execute("touch ./NordVPN-gui_data/script");
			execute("chmod +xwr ./NordVPN-gui_data/script");
			File tempScript = new File("./NordVPN-gui_data/script");

			Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
			PrintWriter printWriter = new PrintWriter(streamWriter);

			printWriter.println("#!/usr/bin/expect");
			printWriter.println("spawn nordvpn login");
			printWriter.println("expect \"Email / Username:\"");
			printWriter.println("send \"" + username + "\\r\"");
			printWriter.println("expect \"Password:\"");
			printWriter.println("send \"" + password + "\\r\"");
			printWriter.println("interact");

			printWriter.close();

			String resp = execute("./NordVPN-gui_data/script");

			if (resp.contains("You are logged in. Welcome to NordVPN")) {
				tempScript.delete();
				return true;
			} else {
				tempScript.delete();
				return false;
			}
		} catch (Exception e) {
			execute("rm -f ./NordVPN-gui_data/script");
			e.printStackTrace();
		}

		return false;
	}

	public static boolean isLogedIn() {
		String resp = execute("nordvpn login");

		if (resp.contains("You are already logged in")) {
			return true;
		}

		return false;
	}

}
