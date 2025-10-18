package me.peterferencz.app;

import java.util.ArrayList;
import java.util.HashMap;

public class EventDispacher {
    
    public static enum Events{
        JARFILECHOOSEN,
        JARFILEFINISHEDLOADING,
        CLASSSELECTED,
        NONCLASSFILECHOOSEN,
        MANIFESTFILECHOOSEN
    }

    private static HashMap<Events, ArrayList<Runnable>> callTable = new HashMap<>();

    static {
        for(Events e : Events.values()){
            callTable.put(e, new ArrayList<>());
        }
    }

    public static void subscribe(Events e, Runnable callback){
        callTable.get(e).add(callback);
    }

    public static void dispatch(Events e){
        for(Runnable callback : callTable.get(e)){
            callback.run();
        }
    }

}
