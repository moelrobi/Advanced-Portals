package com.sekwah.advancedportals.core.repository;

import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Singleton;
import com.sekwah.advancedportals.core.AdvancedPortalsCore;
import com.sekwah.advancedportals.core.api.destination.Destination;
import com.sekwah.advancedportals.core.entities.DataTag;
import com.sekwah.advancedportals.core.api.portal.PortalException;
import com.sekwah.advancedportals.core.api.warphandler.TagHandler;
import com.sekwah.advancedportals.core.entities.PlayerLocation;
import com.sekwah.advancedportals.coreconnector.container.PlayerContainer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class DestinationRepositoryImpl implements DestinationRepository {
    private Map<String, Destination> destiHashMap = new HashMap<>();

    @Override
    public void create(String name, Destination destination) {
        destiHashMap.put(name, destination);
    }

    @Override
    public void delete(String name) {
        destiHashMap.remove(name);
    }

    @Override
    public ImmutableMap<String, Destination> getDestinations() {
        return ImmutableMap.copyOf(destiHashMap);
    }

    @Override
    public void loadDestinations() {

    }


    public Destination createDesti(String name, PlayerContainer player, PlayerLocation playerLocation, ArrayList<DataTag> tags) throws PortalException {
        if(name == null || name.equals("")) {
            throw new PortalException("desti.error.noname");
        }
        else if(this.destiHashMap.containsKey(name)) {
            throw new PortalException("desti.error.takenname");
        }

        Destination desti = new Destination(playerLocation);
        for(DataTag portalTag : tags) {
            desti.setArg(portalTag);
        }
        for(DataTag destiTag : tags) {
            TagHandler.Creation<Destination> creation = AdvancedPortalsCore.getDestinationTagRegistry().getCreationHandler(destiTag.NAME);
            if(creation != null) {
                creation.created(desti, player, destiTag.VALUE);
            }
        }
        this.destiHashMap.put(name, desti);
        this.saveDestinations(AdvancedPortalsCore.getInstance());
        return desti;
    }

    /**
     * TODO change these, may be good if the data storage was an inject as well as it would save time and clean up layout
     * @param portalsCore
     */
    public void loadDestinations(AdvancedPortalsCore portalsCore) {
        Type type = new TypeToken<HashMap<String, Destination>>() {
        }.getType();
        this.destiHashMap = portalsCore.getDataStorage().loadJson(type, "destinations.json");
        this.saveDestinations(portalsCore);
    }

    public void saveDestinations(AdvancedPortalsCore portalsCore) {
        if (this.destiHashMap == null) {
            this.destiHashMap = new HashMap<>();
        }
        portalsCore.getDataStorage().storeJson(this.destiHashMap, "destinations.json");
    }
}
