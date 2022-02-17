package net.realact.pavlovstats.lib;

import java.util.ArrayList;
import java.util.List;

public class NetcatIOStub {
    private List<String> input = new ArrayList<>();
    private List<String> output= new ArrayList<>();

    public synchronized void writeInputLine(String line){
        this.input.add(line);
    }

    public synchronized String readInputline(){
        if(this.input.size() == 0){
            return null;
        }
        return this.input.remove(0);
    }

    public synchronized void writeOutputLine(String line){
        this.output.add(line);
    }

    public synchronized String readOutputline(){
        if(this.output.size() == 0){
            return null;
        }
        return this.output.remove(0);
    }
}
