package warren.aandp.project02.login;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AppendingMethods {
    private static final String BASE = "src/main/resources/warren/aandp/project02/login/";

    //-------------------------------------------------------------------------------------------------------------------------------------
    //                           Add data to a line
    //-------------------------------------------------------------------------------------------------------------------------------------
    public void appendToLine(String whereID, String valueID) throws IOException {

        String fileName;
        if (whereID.startsWith("S")) fileName = "Student.txt";
        else if (whereID.startsWith("P")) fileName = "professor.txt";
        else fileName = "Course.txt";

        Path file = Paths.get(BASE + fileName);

        // Read the current contents from the SAME file we will later write to
        List<String> lines = Files.readAllLines(file);

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

    public void deleteFromLine(String whereID, String valueID) throws IOException {

        String studentPath = "src/main/resources/warren/aandp/project02/login/Student.txt";
        String professorPath = "src/main/resources/warren/aandp/project02/login/professor.txt";
        String coursePath = "src/main/resources/warren/aandp/project02/login/Course.txt";

        //sort whereID to proper file
        String filePath = "";
        if (whereID.contains("S")) {
            filePath = studentPath;
        } else if (whereID.contains("P")) {
            filePath = professorPath;
        } else if (whereID.contains("C")) {
            filePath = coursePath;
        }

        File inputFile = new File(filePath);
        List<String> updatedLines = new ArrayList<>();

        //delete a line
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String currentLine;

            boolean gradeDeleted = false;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.startsWith(whereID + ",")) {
                    String[] parts = currentLine.split(",");
                    StringBuilder newLine = new StringBuilder();
                    newLine.append(parts[0]);
                    for (int i = 1; i < parts.length; i++) {
                        if ( !gradeDeleted && (whereID.contains("S") && (parts[i+1].equals(valueID))) ) {
                            gradeDeleted = true;
                        } else if ( !parts[i].equals(valueID) ) {
                            newLine.append(",").append(parts[i]);
                        }
                    }
                    updatedLines.add(newLine.toString());
                } else {
                    updatedLines.add(currentLine); // Keep untouched lines
                }
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(inputFile))) {
            for (String line : updatedLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public void deleteCourseFromStudents(String courseID) throws IOException {
        Path file = Paths.get(BASE + "Student.txt");
        

        List<String> lines = Files.readAllLines(file);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",");
            List<String> cleaned = new ArrayList<>();

            for (int i = 0; i < parts.length; i++) {
                if (parts[i].trim().equals(courseID)) {
                    // Skip this item and the one before it, if there is one
                    if (!cleaned.isEmpty()) {
                        cleaned.remove(cleaned.size() - 1);
                    }
                    continue;
                }
                cleaned.add(parts[i]);
            }

            updatedLines.add(String.join(",", cleaned));
        }

                    Files.write(file, updatedLines);
                }
            
        
    

    public void deleteCourse(String courseID) throws IOException {
        Path file = Paths.get(BASE + "Course.txt");

        // Read all lines, filter out the ones containing the phrase
        List<String> updatedLines = Files.readAllLines(file).stream()
                .filter(line -> !line.contains(courseID))
                .collect(Collectors.toList());

        // Write the filtered list back to the file
        Files.write(file, updatedLines);
    }

    public void updateGrade(String studentID, String courseID, String newGrade) throws IOException {
        Path file = Paths.get(BASE + "Student.txt");
    
        List<String> lines = Files.readAllLines(file);
        List<String> updatedLines = new ArrayList<>();
    
        for (String line: lines) {
            if (line.startsWith(studentID + ",")) {
                String[] parts = line.split(",");
                StringBuilder updatedLine = new StringBuilder(parts[0]); // Preserve student ID
                
                for (int i = 1; i < parts.length; i++) {
                    if (parts[i].trim().equals(courseID) && i + 1 < parts.length) {
                        updatedLine.append(",").append(courseID); // Keep course ID
                        updatedLine.append(",").append(newGrade); // Replace grade
                        i++; // Skip old grade entry
                    } else {
                        updatedLine.append(",").append(parts[i]); // Append unchanged values
                    }
                }
                updatedLines.add(updatedLine.toString());
            } else {
                updatedLines.add(line); // Keep untouched lines
            }
        }
   
    
        Files.write(file, updatedLines);
    }
    }
         
    
     


