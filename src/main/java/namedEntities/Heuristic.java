package namedEntities;

import java.util.List;

import java.io.Serializable;

public abstract class Heuristic implements Serializable{
    String label = null;
    public Heuristic(String label){
        this.label = label;
    }

    public abstract List<String> extract(String text);
}
