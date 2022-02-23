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
            Pipeline pipe = new Pipeline(mipsStream);
            ScriptExecutor scriptExecutor = new ScriptExecutor(scriptStream, pipe);
            scriptExecutor.run();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}