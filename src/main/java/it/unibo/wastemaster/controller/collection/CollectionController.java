package it.unibo.wastemaster.controller.collection;

import java.util.List;

import it.unibo.wastemaster.core.models.Collection;

public class CollectionController {

    private List<Collection> collections;

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
        loadCollections();
    }

    private void loadCollections() {

    }
}
