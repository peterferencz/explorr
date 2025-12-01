package me.peterferencz.app;

import java.util.ArrayList;
import java.util.EnumMap;

public class EventDispacher {
    
    public enum Events{
        JARFILECHOOSEN,
        JARFILEFINISHEDLOADING,
        CLASSSELECTED,
        NONCLASSFILECHOOSEN,
        MANIFESTFILECHOOSEN,
        SAVEUMLDIAGRAM,
        EXPANDALLTREEVIEW
    }

    private static EnumMap<Events, ArrayList<Runnable>> callTable = new EnumMap<>(Events.class);
    
    // Hide public constructor
    private EventDispacher(){}

    static {
        for(Events e : Events.values()){
            callTable.put(e, new ArrayList<>());
        }
    }

    /** 
     * @param e Event to subscribe to
     * @param callback Function to call on event fire
     */
    public static void subscribe(Events e, Runnable callback){
        callTable.get(e).add(callback);
    }

    /** 
     * @param e Event to fire
     */
    public static void dispatch(Events e){
        for(Runnable callback : callTable.get(e)){
            callback.run();
        }
    }

}
