package warren.aandp.project02.login;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ProfessorClassDetailsInfo {

    LoginMethods loginMethods = new LoginMethods();
    static String secret = "four scores and 7 years ago?";

    private String studentID;
    private String name;
    private String email;
    private String grade;

    public ProfessorClassDetailsInfo(String studentID, String name, String email, String grade) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        this.studentID = studentID;
        this.name      = name;
        this.email     = loginMethods.decrypt(email, secret);
        this.grade     = grade;
    }

    public String getStudentID() {return studentID;}
    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getGrade() {return grade;}
}
