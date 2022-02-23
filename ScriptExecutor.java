import java.io.InputStream;
import java.util.Scanner;

public class ScriptExecutor {
    private final InputStream inputStream;
    private final Pipeline pipe;
    private boolean terminate = false;
    public ScriptExecutor (InputStream inputStream, Pipeline pipe) {
        this.inputStream = inputStream;
        this.pipe = pipe;
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
                pipe.proceed();
                System.out.println("        " + step + " instruction(s) executed");
            }  else {
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
        } else if (instruction.equals("s")) {
            pipe.proceed();
            pipe.printPipe();
        } else if (instruction.equals("r")) {
            pipe.run();
        } else {
            System.out.print("bad instruction!");
        }
    }

}
