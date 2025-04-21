package warren.aandp.project02.login;

import java.io.*;

import javafx.collections.ObservableList;



public class AddClassMethods {
    StudentEnrollControler sec = new StudentEnrollControler();
    String courseFilePath = "src\\main\\resources\\warren\\aandp\\project02\\login\\Course.txt";
    final int SAVED_ID_LOCATION = 0;

   // public void writeCoursesToFile(ObservableList<StudentEnrollInfo> courses = tblCourses.getItems()) {
       // try (BufferedWriter writer = new BufferedWriter(new FileWriter(courseFilePath, true))) {
         //   for (StudentEnrollInfo course : courses) {
           //     writer.write(course.getCourseName() + "," + course.getProfessorName() + "," + course.getTime() + "," + course.getDays());
             //   writer.newLine();
           // }
       // } catch (IOException e) {
         //   e.printStackTrace();
       // }
  //  }
    
    public String addClass(String proffessorID, String courseName, String time, String AMorPM,
                           boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday) throws IOException {
        AppendingMethods appendingMethods = new AppendingMethods();
        String status = "fine";
        String days = "";

        //Get ID -------------------------------------------------------------------------------------------
        int newID = 0;
        String finalID = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(courseFilePath))) {
            //var for name and line read
            //String decryptName;
            String currentLine;
            String[] line;

            //go through every line
            //Find if email is in use, Find next id
            while ((currentLine = reader.readLine()) != null) {
                line = currentLine.split(",");
                newID = Integer.parseInt(line[SAVED_ID_LOCATION].substring(2)) + 1;
            }
            finalID = "C-" + String.valueOf(newID);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
       
      

        //check time as valid time ---------------------------------------------------------------------------------------------
        String [] timeSplit;
        String [] timeStartSplit;
        String [] timeEndSplit;
        String finalTime = "";
        try {
            timeSplit = time.split("-");
            timeStartSplit = timeSplit[0].split(":");
            timeEndSplit = timeSplit[1].split(":");
            if(!(timeSplit.length == 2) || !(timeStartSplit.length == 2) || !(timeEndSplit.length == 2)) {
                status = "Time error";
            } else if ( !( 1 <= Integer.parseInt(timeStartSplit[0]) &&  Integer.parseInt(timeStartSplit[0])<= 12) ||
                        !( 1 <= Integer.parseInt(timeEndSplit[0]) &&  Integer.parseInt(timeEndSplit[0])<= 12)) {
                status = "Time error in hour";
            } else if ( !( 0 <= Integer.parseInt(timeStartSplit[1]) &&  Integer.parseInt(timeStartSplit[1])<= 60) ||
                    !( 0 <= Integer.parseInt(timeEndSplit[1]) &&  Integer.parseInt(timeEndSplit[1])<= 60)) {
                status = "Time error in minute";
            } else {
                finalTime += time + AMorPM;
            }
        } catch (Exception e) {
            status = "Big time error";
            throw new RuntimeException(e);
        }

        //add days together ------------------------------------------------------------------------------------------------------
        if(monday) { days += "M"; }
        if(tuesday) { days += "T"; }
        if(wednesday) { days += "W"; }
        if(thursday) { days += "H"; }
        if(friday) { days += "F"; }
        if(days.equals("")) { status = "No Days Selected"; }

        //write data in Course.txt file ----------------------------------------------------------------------------------------------
        if (status.equals("fine")) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(courseFilePath, true))) {
                // Write the email, name, salt and password
                writer.write(finalID + "," + courseName + "," + finalTime + "," + days + "," + proffessorID);
                writer.newLine();
                System.out.println("Account saved successfully");
                writer.close();
                appendingMethods.appendToLine(proffessorID,finalID);
                return "Class Added";
            } catch (IOException e) {
                System.out.println("An error occurred while saving the account: " + e.getMessage());
            }
        }

        return status;
    }
}
