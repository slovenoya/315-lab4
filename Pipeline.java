import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Pipeline {
    private List<String> instructions = new ArrayList<>();
    private List<String> pipeLine = new LinkedList<>();
    private static final String EMPTY = "empty";
    private static final String STALL = "stall";
    private static final String SQUASH = "squash";
    public Pipeline(List<String> instructions) {
        this.instructions = instructions;
        pipeLine.add(EMPTY);
        pipeLine.add(EMPTY);
        pipeLine.add(EMPTY);
        pipeLine.add(EMPTY);
        pipeLine.add(EMPTY);
    }

}
