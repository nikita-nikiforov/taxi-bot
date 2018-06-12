package pack.constant;

public enum MessageText {
    INITIAL("Hi! Welcome to Taxi Bot. What do you want to do?"),
    START_INPUT("Currently, we can drive you only from Lviv to Odesa. " +
            "Please, enter the start point."),
    START_INPUT_TRUE("Awesome! Now enter the end point."),
    START_INPUT_FALSE("Your location is not supported. Another one?"),
    END_INPUT_TRUE("Nice! Now wait for a car."),
    END_INPUT_FALSE("Unfortunately, we can't drive you here :( " +
            "\nBut maybe you want to go somewhere else?"),
    WAIT_FOR_CAR("You're waiting for a car."),
    EXIT("Okay! If you want to order a taxi again, " +
            "write to me. Have a nice day :)")
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
