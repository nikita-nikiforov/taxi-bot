package pack.constant;

import java.util.HashMap;
import java.util.Map;

public enum RideStatus {
    CREATED("created"),
    PROCESSING("processing"),
    ACCEPTED("accepted"),
    ARRIVING("arriving"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    FINISHED("finished");

    private final String name;

    private static final Map<String, RideStatus> map;

    static {
        map = new HashMap<>();
        for (RideStatus rideStatus : RideStatus.values()) {
            map.put(rideStatus.name, rideStatus);
        }
    }

    RideStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Transform uber trip status to user state. E.g. "arrival" -> "UBER_ARRIVAL"
    public String getUserState() {
        return "UBER_" + name.toUpperCase();
    }

    public static RideStatus findByName(String name) {
        return map.get(name);
    }
}
