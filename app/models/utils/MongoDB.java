package models.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;

import models.User;

import org.mongodb.morphia.Morphia;

import play.Logger;

public final class MongoDB {

    /**
     * Connects to MongoDB based on the configuration settings.
     * If the database is not reachable, an error message is written and the
     * application exits.
     */
    public static boolean connect() {
        String _mongoURI = "mongodb://127.0.0.1:27017";

        MongoClientURI mongoURI = new MongoClientURI(_mongoURI);
		
        MorphiaObject.mongo = null;

        try {
            MorphiaObject.mongo = new MongoClient(mongoURI);
        } catch(UnknownHostException e) {
            Logger.info("Unknown Host");
        }

        if (MorphiaObject.mongo != null) {
            MorphiaObject.morphia = new Morphia();
            MorphiaObject.datastore = MorphiaObject.morphia
            		.createDatastore(MorphiaObject.mongo, "fishnet");
            

            //Map classes
            MorphiaObject.morphia.map(User.class);

            MorphiaObject.datastore.ensureIndexes();
            MorphiaObject.datastore.ensureCaps();
        }

        Logger.debug("Morphia datastore: " + MorphiaObject.datastore.getDB());

        return true;
    }

    /**
     * Disconnect from MongoDB.
     */
    public static boolean disconnect() {
        if (MorphiaObject.mongo == null) {
            return false;
        }

        MorphiaObject.morphia = null;
        MorphiaObject.datastore = null;
        MorphiaObject.mongo.close();
        return true;
    }
    
}
