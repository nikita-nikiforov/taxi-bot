package pack.state;

public enum State {
    INITIAL("INITIAL"), SECOND("SECOND");

    private final String name;

    State(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
