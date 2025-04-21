package warren.aandp.project02.login;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ManagmentMethods {

    final int SAVED_ID_LOCATION = 0;
    final int SAVED_EMAIL_LOCATION = 1;
    final int SAVED_NAME_LOCATION = 2;
    final int SAVED_SALT_LOCATION = 3;
    final int SAVED_PASSWORD_LOCATION = 4;

    public String findUserName(String id) {
        //defult
        String name = "no id found";

        //check for Student of Proffessor
        String filePath;
        if (id.contains("S")) {
            filePath = "warren/aandp/project02/login/Student.txt";;
        } else {
            filePath = "warren/aandp/project02/login/professor.txt";;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            //var for name and line read
            String currentLine;
            String [] line;

            //go through every line
            while ((currentLine = reader.readLine()) != null) {
                //splits line into email, name, salt, password
                line = currentLine.split(",");
                if(line[SAVED_ID_LOCATION].equals(id)) {
                    name = line[SAVED_NAME_LOCATION];
                    break;
                }
            }
            //errors
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    public void findSelectedTabelID() {

    }

}
