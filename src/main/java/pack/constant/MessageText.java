package pack.constant;

public enum MessageText {
    INITIAL("Hi! Welcome to Taxi Bot. What do you want to do?"),
    START_INPUT("Please, attach the start location or just write the address."),
    START_INPUT_TRUE("Awesome! Now enter the end point."),
    START_INPUT_FALSE("Your location is not supported. Another one?"),
    END_INPUT_TRUE("Nice! Now wait for a car."),
    END_INPUT_FALSE("Unfortunately, we can't drive you here :( " +
            "\nBut maybe you want to go somewhere else?"),
    WAIT_FOR_CAR("You're waiting for a car."),
    EXIT("Okay! If you want to order a taxi again, " +
            "write to me. Have a nice day :)"),
    FAV_PLACE_INPUT_MAP("Select the place you want to save."),
    FAV_PLACE_INPUT_NAME("Enter the name of this place."),
    FAV_PLACE_ADDED("The place was saved!")
    ;

    MessageText(String text) {
        this.text = text;
    }

    private String text;

    @Override
    public String toString() {
        return text;
    }
}
