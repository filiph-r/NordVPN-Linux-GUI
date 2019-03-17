package thread;

import execution.CommandExecutioner;

public class Connecter implements Runnable {

	CommandExecutioner exec;
	String Country;
	String City;

	public Connecter(CommandExecutioner exec) {
		this.exec = exec;
	}

	@Override
	public void run() {
		exec.disconnect();
		exec.connect(Country, City);
	}

	public void setValue(String country, String city) {
		Country = country;
		City = city;
	}

}
