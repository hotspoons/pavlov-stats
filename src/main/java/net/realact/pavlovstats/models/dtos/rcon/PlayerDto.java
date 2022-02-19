package net.realact.pavlovstats.models.dtos.rcon;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerDto {
    private String username;
    private String uniqueId;

    @JsonProperty("Username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    @JsonProperty("UniqueId")
    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
