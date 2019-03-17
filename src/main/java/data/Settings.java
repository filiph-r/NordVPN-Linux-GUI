package data;

public class Settings {
	public String protocol;
	public String killswitch;
	public String cyberSec;
	public String obfuscate;
	public String autoConnect;
	public String dns;
	public String whitelistedPorts;

	public Settings(String protocol, String killswitch, String cybetSec, String obfuscate, String autoConnect,
			String dns, String whitelistedPorts) {
		this.protocol = protocol;
		this.killswitch = killswitch;
		this.cyberSec = cybetSec;
		this.obfuscate = obfuscate;
		this.autoConnect = autoConnect;
		this.dns = dns;
		this.whitelistedPorts = whitelistedPorts;
	}

	public void Set(String protocol, String killswitch, String cybetSec, String obfuscate, String autoConnect,
			String dns, String whitelistedPorts) {
		this.protocol = protocol;
		this.killswitch = killswitch;
		this.cyberSec = cybetSec;
		this.obfuscate = obfuscate;
		this.autoConnect = autoConnect;
		this.dns = dns;
		this.whitelistedPorts = whitelistedPorts;
	}

}
