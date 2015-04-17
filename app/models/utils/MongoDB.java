package models.utils;

/**
 * Created by ntenisOT on 16/10/14.
 */

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import models.*;

import org.mongodb.morphia.Morphia;

import play.Logger;
import play.Play;

import java.net.UnknownHostException;

public final class MongoDB {

    /**
     * Connects to MongoDB based on the configuration settings.
     * If the database is not reachable, an error message is written and the
     * application exits.
     */
    public static boolean connect() {
        String _mongoURI = "mongodb://127.0.0.1:27017";

        MongoClientURI mongoURI = new MongoClientURI(_mongoURI);
        MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(mongoURI);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        MorphiaObject.mongo = null;

        try {
            MorphiaObject.mongo = new MongoClient(mongoURI);
        }
        catch(UnknownHostException e) {
            Logger.info("Unknown Host");
        }

        if (MorphiaObject.mongo != null) {
            MorphiaObject.morphia = new Morphia();
            DB database = mongoClient.getDB("playcal");
            MorphiaObject.datastore = MorphiaObject.morphia.createDatastore(MorphiaObject.mongo, "playcal");
            

            //Map classes

            MorphiaObject.morphia.map(User.class);
            

            MorphiaObject.datastore.ensureIndexes();
            MorphiaObject.datastore.ensureCaps();
        }

        Logger.debug("** Morphia datastore: " + MorphiaObject.datastore.getDB());

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

