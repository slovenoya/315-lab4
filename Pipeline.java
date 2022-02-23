import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
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
    private int instrNum = 0;
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
        List<String> pipeList = new ArrayList<>();
        for (String a : pipeLine) {
            pipeList.add(getOpName(a));
        }
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
            String[] splits = instruction.split("[$]");
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

    public void run() {
        do {
            proceed();
        } while (!isEmpty());
        System.out.println("Program complete");
        System.out.println("CPI = " + getCPI() + "     Cycles = " + cycle + 	"      Instructions = " + instructions.size());
    }

    public void proceed() {
        cycle++;
        executeOnePipeLine(pipeLine);
    }

    private void executeOnePipeLine(List<String> pipeline) {
        if (isInstruction(pipeline.get(MEMWR))) {
            instrNum ++;
        }
        if (getOpName(pipeline.get(EXEMEM)).equals("beq")) {
            String[] splits = pipeline.get(EXEMEM).split(",");
            String[] splitTwo = splits[0].split("[$]");
            String rs = "[$]" + splitTwo[1];
            String rt = splits[1];
            if (executor.branchEquality(rs, rt)) {
                pipeline.clear();
                pipeline.add(SQUASH);
                pipeline.add(SQUASH);
                pipeline.add(SQUASH);
                pipeline.add(pipeline.get(EXEMEM));
                executor.executeOneLine(pipeline.get(MEMWR));
                String nextInstruction;
                if (executor.getPC() <= instructions.size()) {
                    nextInstruction = instructions.get(executor.getPC());
                } else {
                    nextInstruction = EMPTY;
                }
                shift(nextInstruction);
            }
        } else if (getOpName(pipeline.get(IDEXE)).equals("lw")) {
            String loadInstruction = pipeline.get(IDEXE);
            String prevInstruction = pipeline.get(IFID);
            String[] load = getLoadStoreTokens(loadInstruction);
            String rt = load[1];
            if (prevInstruction.contains(",")) {
                if (needStall(prevInstruction, rt)) {
                    int pc = executor.getPC();
                    executor.executeOneLine(pipeline.get(MEMWR));
                    executor.setPC(pc);
                    String a = pipeline.get(IFID);
                    String b = pipeline.get(IDEXE);
                    String c = pipeline.get(EXEMEM);
                    pipeline.clear();
                    pipeline.add(a);
                    pipeline.add(STALL);
                    pipeline.add(b);
                    pipeline.add(c);
                }
            }
        } else if (isJump(pipeline.get(IDEXE))) {
            int pc = executor.getPC();
            executor.executeOneLine(pipeline.get(MEMWR));
            executor.setPC(pc);
            shift(SQUASH);
        } 
        executor.executeOneLine(pipeline.get(MEMWR));
        String nextInstruction;
        if (executor.getPC() < instructions.size()) {
            nextInstruction = instructions.get(executor.getPC());
            executor.setPC(executor.getPC() + 1);
        } else {
            nextInstruction = EMPTY;
        }
        shift(nextInstruction);
    }

    private boolean needStall(String instruction, String rt) {
        if (getRsRt(instruction) == null) {
            return false;
        }
        for (String s: getRsRt(instruction)) {
            if (s.equals(rt)) {
                return true;
            }
        }
        return false;
    }

    //0 -> rs, 1-> rt
    private String[] getRsRt(String instruction) {
        
        if (!isInstruction(instruction)) {
            return null;
        }
        if (isLwSw(instruction)) {
            String[] result = new String[1];
            String[] s = getLoadStoreTokens(instruction);
            result[0] = s[0];
            return result;
        } else if (isR(instruction)) {
            String[] result = new String[2];
            String[] s = getTokens(instruction);
            result[0] = s[1];
            result[1] = s[2];
            return result;
        } else if (isI(instruction)) {
            String[] result = new String[1];
            String[] s = getTokens(instruction);
            result[0] = s[1];
            return result;
        } else {
            return null; //jump
        }
    }

    private boolean isR (String instruction) {
        String[] s1 = instruction.split("[$]");
        return s1.length == 4;
    }

    private boolean isI (String instruction) {
        String[] s = instruction.split("[$]");
        return s.length == 3;
    }

    private boolean isJump(String instruction) {
        return instruction.startsWith("j");
    }

    private boolean isLwSw(String instruction) {
        return instruction.startsWith("lw") || instruction.startsWith("sw");
    }

    private String[] getLoadStoreTokens(String instruction) {
        String[] result = new String[3];
        String[] split = instruction.split(",");
        String[] second = split[1].split("[(]");
        String rt = split[0].substring(2);
        String imm = second[0];
        String rs = second[1].substring(0, 3);
        result[0] = rs;
        result[1] = rt;
        result[2] = imm;
        return result;
    }

    private void shift(String newInstruction) {
        pipeLine.remove(MEMWR);
        pipeLine.add(IFID, newInstruction);
    }

    private double getCPI() {
        return (double) cycle / instrNum;
    }

    private String[] getTokens(String instruction) {
        String [] tokens = instruction.split(",");
        String [] result = new String[3];
        String firstToken = "[$]" + tokens[0].split("[$]")[1];
        result[0] = firstToken;
        result[1] = tokens[1];
        result[2] = tokens[2];
        return result;
    }

    private boolean isInstruction(String instruction) {
        if (instruction.equals(EMPTY)) {
            return false;
        } else if (instruction.equals(SQUASH)) {
            return false;
        } else if (instruction.equals(STALL)) {
            return false;
        } return true;
    }

    public void printPipe() {
        List<String> names = getPipeList();
        System.out.println();
        System.out.println("pc" + "\t" + "if/id" + "\t" + "id/exe" + "\t" + "exe/mem" + "\t" + "mem/wb");
        System.out.println(executor.getPC() + "\t" + names.get(IFID) + "\t" + names.get(IDEXE) + "\t" + names.get(EXEMEM) + "\t" + names.get(MEMWR));
        System.out.println();
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
