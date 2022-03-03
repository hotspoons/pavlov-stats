package net.realact.pavlovstats.services;

public interface MapNameResolver {
    String getMapNameForWorkshopId(String mapId);

    void clearMapNameCache();
}
