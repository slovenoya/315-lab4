import java.io.InputStream;
import java.util.*;

public class MipsParser {
  private final InputStream input;
  private final Map<String, Integer> labels;
  private final List<String> instructions;

  public MipsParser(InputStream inputStream) {
    this.input = inputStream;
    this.labels = new HashMap<>();
    this.instructions = new ArrayList<>();
    this.parse();
  }

  public List<String> getInstructions() {
      return this.instructions;
  }

  public Map<String, Integer> getLabels() {
    return this.labels;
  }

  private void parse() {
    List<String> lines = readFile();
    firstPass(lines);
  }

  /**
   * firstPass() will format the code:
   * 1. ignore all empty lines and comments
   * 2. identify the line number for label
   * then:
   * 1. put the instructions into instruction list in order (no label, spaces, or comments)
   * 2. put the label into labels table as label name : line number
   */

  private void firstPass(List<String> lines) {
    int lineCnt = 0;
    for (String line : lines) {
      String trimBlank = line.replaceAll("\\s", "");
      String trimComment = trimBlank.replaceAll("#.*", "");
      String label = trimComment.replaceAll(":.*", "");
      if (label.length() != trimComment.length()) {
        labels.put(label, lineCnt);
      }
      String notLabel = trimComment.replaceAll(".*:", "");
      if (notLabel.length() != 0) {
        lineCnt ++;
        instructions.add(notLabel);
      }
    }
  }

  private List<String> readFile() {
    List<String> lines = new ArrayList<>();
    Scanner scanner = new Scanner(this.input);
    while (scanner.hasNextLine()) {
      lines.add(scanner.nextLine());
    }
    scanner.close();
    return lines;
  }
}