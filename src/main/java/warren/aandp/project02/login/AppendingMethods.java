package warren.aandp.project02.login;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppendingMethods {
    private static final String BASE = "src/main/resources/warren/aandp/project02/login/";

    public void appendToLine(String whereID, String valueID) throws IOException {

        String fileName;
        if (whereID.startsWith("S"))       fileName = "Student.txt";
        else if (whereID.startsWith("P"))  fileName = "professor.txt";
        else                                fileName = "Course.txt";

        Path file = Paths.get(BASE + fileName);

        // Read the current contents from the SAME file we will later write to
        java.util.List<String> lines = Files.readAllLines(file);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.startsWith(whereID + ",")) continue;

            // Skip if the value is already present (avoids duplicates)
            if (line.contains("," + valueID)) return;

            StringBuilder sb = new StringBuilder(line.trim());

            if ("Student.txt".equals(fileName) && valueID.startsWith("C-")) {
                sb.append(",100%");                                    // placeholder grade
            }
            sb.append(",").append(valueID);

            lines.set(i, sb.toString());
            break; // only one line matches
        }

        // Write the whole file back
        Files.write(file, lines);
    }
    /*
    public void deleteFromLine(String whereID, String valueID) throws IOException {
        String studentPath = "/warren/aandp/project02/login/Student.txt";
        String professorPath = "/warren/aandp/project02/login/professor.txt";
        String coursePath = "/warren/aandp/project02/login/Course.txt";
        String filePath = "";
        if (whereID.contains("S")) {
            filePath = studentPath;
        } else if (whereID.contains("P")) {
            filePath = professorPath;
        } else if (whereID.contains("C")){
            filePath = coursePath;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filePath)))) {
            String currentLine;
            String[] line;
            int whereValueIDInLine;

            while ((currentLine = reader.readLine()) != null) {
                if(currentLine.startsWith(whereID + ",")) {
                    //splits line into email,name, salt, password
                    line = currentLine.split(",");
                    int num = 0;
                    for (String l : line) {
                        if (l.equals(valueID)) {
                            whereValueIDInLine = num;
                            break;
                        }
                        num++;
                    }
                }
            }

            String[] tempStringArray;

            for (int j = 0; j < line.length; j++) {
                if (j != whereValueIDInLine) { // skip the element at index i

                    tempStringArray.
                    tempStringArray.append(line[j]).append(" ");
                }
            }

            String Newline = sb.toString().trim();

            updatedContent.append(line).append(System.lineSeparator());
        }
    }

     */
}
