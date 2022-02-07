import java.io.InputStream;
import java.util.Scanner;

public class ScriptExecutor {
    private final InputStream inputStream;
    private final MipsExecutor executor;
    private boolean terminate = false;
    public ScriptExecutor (InputStream inputStream, MipsExecutor executor) {
        this.inputStream = inputStream;
        this.executor = executor;
    }
    
    public void run() {
        Scanner scanner = new Scanner(inputStream);
        System.out.print("mips> ");
        do {
	        String nextLine = scanner.nextLine();
            System.out.println(nextLine);
            executeScript(nextLine);
        } while (!terminate && scanner.hasNext());
        scanner.close();
    }

    private void executeScript(String instruction) {
        if (instruction.length() == 1) {
            executeSingleScript(instruction);
        }
        else {
            String[] tokens = instruction.split(" ");
            if (tokens[0].equals("s") && tokens.length == 2) {
                int step = Integer.parseInt(tokens[1]);
                executor.execute(step);
                System.out.println("        " + step + " instruction(s) executed");
            } else if (tokens[0].equals("m") && tokens.length == 3) {
                int low = Integer.parseInt(tokens[1]);
                int high = Integer.parseInt(tokens[2]);
                executor.printMemory(low, high);
            } else {
                System.out.println("bad instruction!");
            }
        }
        if (!terminate) {
            System.out.print("mips> ");
        }
    }

    private void executeSingleScript(String instruction) {
        if (instruction.equals("q")) {
            terminate = true;
        } else if (instruction.equals("h")) {
            System.out.println("h = show help\n" + 
            "d = dump register state\n" +
            "s = single step through the program (i.e. execute 1 instruction and stop)\n" +
            "s num = step through num instructions of the program\n" +
            "r = run until the program ends\n" +
            "m num1 num2 = display data memory from location num1 to num2\n" +
            "c = clear all registers, memory, and the program counter to 0\n" +
            "q = exit the program\n");
        } else if (instruction.equals("d")) {
            executor.printEmulator();
        } else if (instruction.equals("s")) {
            executor.execute(1);
            System.out.println("        1 instruction(s) executed");
        } else if (instruction.equals("r")) {
            executor.execute();
        } else if (instruction.equals("c")) {
            executor.initializeEmulator();
            System.out.println("        Simulator reset");
        } else {
            System.out.print("bad instruction!");
        }
    }

}
