package net.iwmedia.report.common.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;

public class MongoServiceImpl implements MongoService {
    private final MongoClient client;
    private final Datastore datastore;

    public MongoServiceImpl(String connectionString, String database) {
        this.client = MongoClients.create(connectionString);
        this.datastore = Morphia.createDatastore(client, database);
        this.datastore.getMapper().mapPackage("net.iwmedia.report.common.model");
        this.datastore.ensureIndexes();
    }

    @Override
    public Datastore getDatastore() {
        return datastore;
    }

    @Override
    public void close() {
        client.close();
    }
}
