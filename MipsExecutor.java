import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MipsExecutor {
    private final Emulator emulator;
    private final List<String> instructions = new ArrayList<String>();
    private final Map<String, Integer> labels = new HashMap<>();

    public MipsExecutor (List<String> instructions, Map<String, Integer> labels) {
        emulator = new Emulator();
        this.instructions.addAll(instructions);
        this.labels.putAll(labels);
    }

    public void execute() {
        while (emulator.getPC() < instructions.size()) {
            executeOneLine(instructions.get(emulator.getPC()));
        }
    }

    public void execute(int linesToBeExecuted) {
        int executedLines = 0;
        while (executedLines < linesToBeExecuted) {
            executeOneLine(instructions.get(emulator.getPC()));
            executedLines++;
            if (emulator.getPC() == instructions.size()) {
                break;
            }
        }
    }

    public void executeOneLine(String instruction) {
        if (instruction.startsWith("jal")) {
            emulator.writeReg("$ra", emulator.getPC() + 1);
            String label = instruction.substring(3);
            emulator.setPC(labels.get(label));
        } else if (instruction.startsWith("jr")) {
            String register = instruction.substring(2);
            emulator.setPC(emulator.readReg(register));
        } else if (instruction.startsWith("j")) {
            String label = instruction.substring(1);
            emulator.setPC(labels.get(label));
        } else if (instruction.startsWith("lw")) {
            String[] tokens = getLoadStoreTokens(instruction);
            String rs = tokens[0];
            String rt = tokens[1];
            int imm = Integer.parseInt(tokens[2]);
            int location = imm + emulator.readReg(rs);
            emulator.writeReg(rt, emulator.getMemory(location, location)[0]);
            emulator.incrementPC();
        } else if (instruction.startsWith("sw")) {
            String[] tokens = getLoadStoreTokens(instruction);
            String rs = tokens[0];
            String rt = tokens[1];
            int imm = Integer.parseInt(tokens[2]);
            int location = imm + emulator.readReg(rs);
            int storeValue = emulator.readReg(rt);
            emulator.storeMemory(location, storeValue);
            emulator.incrementPC();
        } else if (instruction.startsWith("bne")) {
            String[] tokens = getTokens(instruction);
            int rs = emulator.readReg(tokens[0]);
            int rt = emulator.readReg(tokens[1]);
            int imm = labels.get(tokens[2]);
            if (rs != rt) {
                emulator.setPC(imm);
            } else {
                emulator.incrementPC();
            }
        } else if (instruction.startsWith("beq")) {
            String[] tokens = getTokens(instruction);
            int rs = emulator.readReg(tokens[0]);
            int rt = emulator.readReg(tokens[1]);
            int imm = labels.get(tokens[2]);
            if (rs == rt) {
                emulator.setPC(imm);
            } else {
                emulator.incrementPC();
            }
        } else if (instruction.startsWith("slt")) {
            String[] tokens = getTokens(instruction);
            String rd = tokens[0];
            int rs = emulator.readReg(tokens[1]);
            int rt = emulator.readReg(tokens[2]);
            int value = rs < rt ? 1 : 0;
            emulator.writeReg(rd, value);
            emulator.incrementPC();
        } else if (instruction.startsWith("sub")) {
            String [] tokens = getTokens(instruction);
            String rd = tokens[0];
            int rs = emulator.readReg(tokens[1]);
            int rt = emulator.readReg(tokens[2]);
            emulator.writeReg(rd, rs - rt);
            emulator.incrementPC();
        } else if (instruction.startsWith("sll")) {
            String[] tokens = getTokens(instruction);
            String rd = tokens[0];
            int rs = emulator.readReg(tokens[1]);
            int imm = Integer.parseInt(tokens[2]);
            emulator.writeReg(rd, rs << imm);
            emulator.incrementPC();
        } else if (instruction.startsWith("addi")) {
            String[] tokens = getTokens(instruction);
            String rd = tokens[0];
            int rs = emulator.readReg(tokens[1]);
            int imm = Integer.parseInt(tokens[2]);
            emulator.writeReg(rd, rs + imm);
            emulator.incrementPC();
        } else if (instruction.startsWith("add")) {
            String[] tokens = getTokens(instruction);
            String rd = tokens[0];
            int rs = emulator.readReg(tokens[1]);
            int rt = emulator.readReg(tokens[2]);
            emulator.writeReg(rd, rs + rt);
            emulator.incrementPC();
        } else if (instruction.startsWith("or")) {
            String[] tokens = getTokens(instruction);
            String rd = tokens[0];
            int rs = emulator.readReg(tokens[1]);
            int rt = emulator.readReg(tokens[2]);
            emulator.writeReg(rd, rs | rt);
            emulator.incrementPC();
        } else if (instruction.startsWith("and")) {
            String[] tokens = getTokens(instruction);
            String rd = tokens[0];
            int rs = emulator.readReg(tokens[1]);
            int rt = emulator.readReg(tokens[2]);
            emulator.writeReg(rd, rs & rt);
            emulator.incrementPC();
        }
    }

    /**
     * returns String[] where String[0] is rs, String[1] is rt, String[2] is immediate
     * @param instruction instruction sw$t0,4($t1), sw rt, rs
     * @return
     */
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

    private String[] getTokens(String instruction) {
        String [] tokens = instruction.split(",");
        String [] result = new String[3];
        String firstToken = "$" + tokens[0].split("[$]")[1];
        result[0] = firstToken;
        result[1] = tokens[1];
        result[2] = tokens[2];
        return result;
    }

    public void printEmulator() {
        this.emulator.printEmulator();
    }

    public void initializeEmulator() {
        this.emulator.initializeEmulator();
    }

    public void printMemory(int low, int high) {
        int[] chunck = this.emulator.getMemory(low, high);
        int position = low;
        for (int i : chunck) {
            System.out.println("[" + position + "] = " + i);
            position ++;
        }
    }

    public int getPC() {
        return this.emulator.getPC();
    }

    public boolean branchEquality(String rs, String rt) {
        return emulator.readReg(rs) == emulator.readReg(rt);
    }
}