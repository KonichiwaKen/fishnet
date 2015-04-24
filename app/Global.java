import play.Application;
import play.GlobalSettings;
import play.Logger;

import models.utils.MongoDB;

public class Global extends GlobalSettings {
	
	@Override
	public void onStart(Application app) {
		MongoDB.connect();
		Logger.info("Application has started");
	}
	
	@Override
	public void onStop(Application app) {
		MongoDB.disconnect();
	    Logger.info("Application shutdown");
	}
	
}
