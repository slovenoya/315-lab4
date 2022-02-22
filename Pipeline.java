import java.util.ArrayList;
import java.util.HashMap;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Pipeline {
    private final List<String> instructions = new ArrayList<>();
    private List<String> pipeLine = new LinkedList<>();
    private final Map<String, Integer> labels = new HashMap<>();
    private final MipsExecutor executor;
    private static final String EMPTY = "empty";
    private static final String STALL = "stall";
    private static final String SQUASH = "squash";

    public Pipeline(InputStream inputs) {
        MipsParser parser = new MipsParser(inputs);
        this.instructions.addAll(parser.getInstructions());
        this.labels.putAll(parser.getLabels());
        executor = new MipsExecutor(instructions, labels);
        pipeLine.add(EMPTY);
        pipeLine.add(EMPTY);
        pipeLine.add(EMPTY);
        pipeLine.add(EMPTY);
        pipeLine.add(EMPTY);
    }

    public List<String> getPipeList() {
        return this.pipeLine;
    }

    //return if the pipeline is empty
    private boolean isEmpty() {
        for (String instruction: pipeLine) {
            if (!instruction.equals(EMPTY)) {
                return false;
            }
        }
        return true;
    }

    private void proceed() {

    }

    private void executeOnePipeLine(String instruction) {


    }

}
