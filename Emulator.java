import java.util.HashMap;
import java.util.Map;

/**
 * Emulator class contains the essential data for MIPS code. 
 * Data including : PC, Register Table, RAM of 8192 ints
 * 
 */

public class Emulator {
    private static final String SUPPORTED_REGS = 
        "$0,$v0,$v1,$a0,$a1,$a2,$a3,$t0,$t1,$t2,$t3,"
        + "$t4,$t5,$t6,$t7,$s0,$s1,$s2,$s3,$s4,$s5,$s6," 
        + "$s7,$t8,$t9,$sp,$ra";
    private static final String[] SUPPORTED_REG_LIST = SUPPORTED_REGS.split(",");
    private static final int SUPPORTED_REGS_NUM = SUPPORTED_REG_LIST.length;
    private static final int MEM_SIZE = 8192;

    private int PC; 
    private Map<Integer, Integer> registerIdToValue = new HashMap<>();      //first int : id of register, second int: value of register
    private Map<String, Integer> registerNameToId = new HashMap<>();    //first int : name of register, second int: id of register
    private int [] memory;

    public Emulator() {
        setUpRegisterNameId();
        initializeEmulator();
    }

    /**
     * initialize the emulator, make PC, register table, and memory become zeros. 
     */
    public void initializeEmulator() {
        this.PC = 0;
        initializeRegTable();
        initializeMemory();
    }

    //set up register name id (String name, int id)
    private void setUpRegisterNameId() {
        for (int i = 0; i < SUPPORTED_REGS_NUM; i++) {
            registerNameToId.put(SUPPORTED_REG_LIST[i], i);
        }
    }

    private void initializeRegTable() {
        for (int i = 0; i < SUPPORTED_REGS_NUM; i++) {
            this.registerIdToValue.put(i, 0);
        }
    }

    private void initializeMemory() {
        this.memory = new int[MEM_SIZE];
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public void incrementPC() {
        this.PC++;
    }

    /**
     * change the value for a given register
     * @param regName register name e.g $t0
     * @param value update value for the register
     */
    public void writeReg(String regName, int value) {
        registerIdToValue.replace(registerNameToId.get(regName), value);
    }

    /**
     * Integer value of a register ("$t0" -> 0)
     * @param regName register name
     * @return register value
     */
    public int readReg(String regName) {
        return registerIdToValue.get(registerNameToId.get(regName));
    }

    /**
     * return the memory data between low and high (included)
     * @param low lower bound of the memory
     * @param high higher bound of the memory
     * @return int array contains data between low and high in the memory
     */
    public int[] getMemory(int low, int high) {
        if (isOutOfBound(low) || isOutOfBound(high) || low > high) {
            return null;
        }
        if (low == high) {
            int [] data = {memory[low]};
            return data;
        }
        int[] memoryChunck = new int[high - low + 1];
        for (int i = low; i <= high; i++) {
            memoryChunck[i - low] = memory[i];
        }
        return memoryChunck;
    }

    /**
     * store the value to the address in memory
     * @param address address of memory
     * @param value value to be put in the address of memory
     */
    public void storeMemory(int address, int value) {
        if (!isOutOfBound(address)) 
            memory[address] = value;
    }

    private boolean isOutOfBound(int address) {
        return (address < 0 || address >= MEM_SIZE);
    }

    public void printEmulator() {
        System.out.println("pc = " + this.PC);
        int i = 1;
        for (String reg : SUPPORTED_REG_LIST) {
            System.out.print(reg + " = " + readReg(reg) + "  ");
            if (i % 4 == 0 || i == SUPPORTED_REGS_NUM) {
                System.out.println();
            }
            i++;
        }
    }

}
