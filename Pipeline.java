import java.util.ArrayList;
import java.util.HashMap;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Pipeline {
    private final List<String> instructions = new ArrayList<>();
    private List<String> pipeLine = new ArrayList<>();
    private final Map<String, Integer> labels = new HashMap<>();
    private final MipsExecutor executor;
    private int cycle = 0;
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
    }

    public List<String> getPipeList() {
        List<String> pipeList = this.pipeLine.stream().map(s -> getOpName(s)).collect(Collectors.toList());
        return pipeList;
    }

    private String getOpName(String instruction) {
        if (instruction.equals(EMPTY) || instruction.equals(SQUASH) || instruction.equals(STALL)) {
            return instruction;
        } else if (instruction.startsWith("jal")) {
            return "jal";
        } else if (instruction.startsWith("jr")) {
            return "jr";
        } else if (instruction.startsWith("j")) {
            return "j";
        } else {
            String[] splits = instruction.split("$");
            return splits[0];
        }
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
        cycle++;
        String nextInstruction = instructions.get(executor.getPC());
        executeOnePipeLine(pipeLine);
    }

    private void executeOnePipeLine(List<String> pipeline) {
        String instruction = pipeline.get(4);
        String ex = pipeline.get(3);
        executor.executeOneLine(instruction);
        if (getOpName(ex).equals("beq")) {
            String[] splits = ex.split("$");
            String rs = "$" + splits[1];
            String rt = "$" + splits[2];
            if (executor.branchEquality(rs, rt)) {
                pipeline.clear();
                pipeline.add(SQUASH);
                pipeline.add(SQUASH);
                pipeline.add(SQUASH);
                pipeline.add(ex);
            }
        }
    }

    private double getCPI() {
        return (double) cycle / instructions.size();
    }
}
