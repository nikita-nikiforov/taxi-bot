package pack.constant;

public enum State {
    INITIAL(Constants.INITIAL_VALUE),
    START_INPUT(Constants.START_INPUT_VALUE),
    START_INPUT_TRUE(Constants.START_INPUT_TRUE_VALUE),
    START_INPUT_FALSE(Constants.START_INPUT_FALSE_VALUE),
    END_INPUT(Constants.END_INPUT_VALUE),
    WAIT_FOR_CAR(Constants.WAIT_FOR_CAR_VALUE),;

    State(String value) {
        // In order to names of all enums were corresponding to their values
        if (!value.equals(this.name()))
            throw new IllegalArgumentException();
        this.value = value;
    }

    private final String value;

    public String toString() {
        return value;
    }

    public static class Constants {
        public static final String INITIAL_VALUE = "INITIAL";
        public static final String START_INPUT_VALUE = "START_INPUT";
        public static final String START_INPUT_TRUE_VALUE = "START_INPUT_TRUE";
        public static final String START_INPUT_FALSE_VALUE = "START_INPUT_FALSE";
        public static final String END_INPUT_VALUE = "END_INPUT";
        public static final String WAIT_FOR_CAR_VALUE = "WAIT_FOR_CAR";
    }
}