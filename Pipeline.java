import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.File;

public class Pipeline {
    private static final int IFID = 0;
    private static final int IDEXE = 1;
    private static final int EXEMEM = 2;
    private static final int MEMWR = 3;

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
        String instruction = pipeline.get(MEMWR);
        String ex = pipeline.get(EXEMEM);
        executor.executeOneLine(instruction);
        if (getOpName(ex).equals("beq")) {
            String[] splits = ex.split(",");
            String[] splitTwo = splits[0].split("$");
            String rs = "$" + splitTwo[1];
            String rt = splits[1];
            if (executor.branchEquality(rs, rt)) {
                pipeline.clear();
                pipeline.add(SQUASH);
                pipeline.add(SQUASH);
                pipeline.add(SQUASH);
                pipeline.add(ex);
            }
        } else {
            String newInstruction = instructions.get(executor.getPC());
            shift(newInstruction);
        }
    }

    private void shift(String newInstruction) {
        pipeLine.remove(EXEMEM);
        pipeLine.add(IFID, newInstruction);
    }

    private double getCPI() {
        return (double) cycle / instructions.size();
    }

    public void printPipe() {
        System.out.println("PC" + "\t" + "IF/ID" + "\t" + "ID/EXE" + "\t" + "EXE/MEM" + "\t" + "MEM/WR");
        System.out.println(executor.getPC() + "\t" + pipeLine.get(IFID) + "\t" + pipeLine.get(IDEXE) + "\t" + pipeLine.get(EXEMEM) + "\t" + pipeLine.get(MEMWR));
    }

    public static void main(String[] args) {
        File file = new File("tests/input1");
        try {
            InputStream stream = new FileInputStream(file);
            Pipeline pip = new Pipeline(stream);
            pip.printPipe();   
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
