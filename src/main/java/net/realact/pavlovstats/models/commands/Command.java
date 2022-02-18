package net.realact.pavlovstats.models.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public abstract class Command {
    private String command;
    private boolean successful;
    protected List<String> args;

    @JsonProperty("Command")
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @JsonProperty("Successful")
    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String toCommand() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.command);
        if(this.args != null && this.args.size() > 0){
            stringBuilder.append(" ");
            stringBuilder.append(String.join(" ", this.args));

        }
        return stringBuilder.toString();
    }
}
