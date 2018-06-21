package pack.constant;

import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import pack.entity.User;

import java.util.HashMap;
import java.util.Map;

public enum RideStatusEnum {
    PROCESSING("processing") {
        public Request getRequest(User user) {
            return QuickReplies.builder()
                    .user(user)
                    .text(MessageText.UBER_PROCESSING)
                    .postback("Cancel order", Payload.CANCEL_TRIP)
                    .build();
        }
    },
    ACCEPTED("accepted") {
        @Override
        public Request getRequest(User user) {
            return QuickReplies.builder()
                    .user(user)
                    .text(MessageText.UBER_ACCEPTED)
                    .postback("Driver info", Payload.DRIVER_INFO)
                    .build();
        }
    },
    ARRIVING("arriving") {
        @Override
        public Request getRequest(User user) {
            return QuickReplies.builder()
                    .user(user)
                    .text(MessageText.UBER_ARRIVING)
                    .postback("Driver info", Payload.DRIVER_INFO)
                    .build();
        }
    },
    IN_PROGRESS("in_progress") {
        @Override
        public Request getRequest(User user) {
            return QuickReplies.builder()
                    .user(user)
                    .text(MessageText.UBER_IN_PROGRESS)
                    .postback("Driver info", Payload.DRIVER_INFO)
                    .build();
        }
    },
    COMPLETED("completed") {
        @Override
        public Request getRequest(User user) {
            return QuickReplies.builder()
                    .user(user)
                    .text(MessageText.UBER_COMPLETED)
                    .postback("1", Payload.TRIP_RATE + "?rate=1")
                    .postback("2", Payload.TRIP_RATE + "?rate=2")
                    .postback("3", Payload.TRIP_RATE + "?rate=3")
                    .postback("4", Payload.TRIP_RATE + "?rate=4")
                    .postback("5", Payload.TRIP_RATE + "?rate=5")
                    .build();
        }
    };

    private final String name;

    private static final Map<String, RideStatusEnum> map;

    static {
        map = new HashMap<>();
        for (RideStatusEnum rideStatus : RideStatusEnum.values()) {
            map.put(rideStatus.name, rideStatus);
        }
    }

    RideStatusEnum(String name) {
        this.name = name;
    }

    public abstract Request getRequest(User user);

    public String getName() {
        return name;
    }

    // Transform uber trip status to user state. E.g. "arrival" -> "UBER_ARRIVAL"
    public String getUserState() {
        return "UBER_" + name.toUpperCase();
    }

    public static RideStatusEnum findByName(String name) {
        return map.get(name);
    }
}
