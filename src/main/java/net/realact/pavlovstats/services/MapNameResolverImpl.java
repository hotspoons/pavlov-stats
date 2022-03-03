package net.realact.pavlovstats.services;

import net.realact.pavlovstats.config.AppConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MapNameResolverImpl implements MapNameResolver {

    private final AppConfig appConfig;
    private static final String STEAM_WORKSHOP_URL = "https://steamcommunity.com/sharedfiles/filedetails/?id=";
    private SSLSocketFactory socketFactory;
    private Map<String, String> workshopIdCache;

    public MapNameResolverImpl(AppConfig appConfig){
        this.appConfig = appConfig;
        this.workshopIdCache = new ConcurrentHashMap<>();
    }

    private SSLSocketFactory socketFactory() {
        if(this.socketFactory == null){
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };

            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                this.socketFactory = sslContext.getSocketFactory();

            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                throw new RuntimeException("Failed to create a SSL socket factory", e);
            }
        }
        return this.socketFactory;
    }

    @Override
    public String getMapNameForWorkshopId(String mapId){
        String mapName = mapId;
        if(mapId.startsWith("UGC") == false){
            return mapId;
        }
        if(workshopIdCache.containsKey(mapId)){
            return workshopIdCache.get(mapId);
        }
        Document document = null;
        try {
            document = Jsoup.connect(STEAM_WORKSHOP_URL + mapId.replace("UGC", ""))
                    .sslSocketFactory(this.socketFactory()).get();
            Elements titleNodes = document.select("div.workshopItemTitle");
            if(titleNodes.size() == 1){
                mapName = titleNodes.get(0).text();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        workshopIdCache.put(mapId, mapName);
        return workshopIdCache.get(mapId);
    }

    @Override
    public void clearMapNameCache(){
        workshopIdCache = new ConcurrentHashMap<>();
    }

}
