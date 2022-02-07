import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class lab4 {
    public static void main(String[] args) {
        File mipsFile = new File(args[0]);
        InputStream scriptStream = null;
        if (args.length == 2) {
            File scriptFile = new File(args[1]);
            try {
                scriptStream = new FileInputStream(scriptFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            scriptStream = System.in;
        }
        try {
            InputStream mipsStream = new FileInputStream(mipsFile);
            MipsParser parser = new MipsParser(mipsStream);
            MipsExecutor executor = new MipsExecutor(parser.getInstructions(), parser.getLabels());
            ScriptExecutor scriptExecutor = new ScriptExecutor(scriptStream, executor);
            scriptExecutor.run();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}