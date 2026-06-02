package net.iwmedia.report.common.service;

import dev.morphia.Datastore;

public interface MongoService {
    Datastore getDatastore();
    void close();
}
