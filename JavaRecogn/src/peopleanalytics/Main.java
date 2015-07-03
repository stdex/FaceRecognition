package peopleanalytics;

import java.util.logging.Level;
import java.util.logging.Logger;

import peopleanalytics.ui.Window;

public class Main {
	public static Logger LOGGER;
        public static String result = "";
        public static int numberofpeople = 0;
	
	public static void main(String[] args) {
		initLogger(Level.INFO);  // set to Level.OFF to turn off logging
		initUI();
	}
	
	private static void initLogger(Level level) {
		LOGGER = Logger.getLogger(Main.class.getName());
		LOGGER.setLevel(level);
	}
	
	private static void initUI() {
		Window.initialize();
	}
}
