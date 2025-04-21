package warren.aandp.project02.login;

public class ProfessorClassDetailsInfo {
    private String studentID;
    private String name;
    private String email;
    private String grade;

    public ProfessorClassDetailsInfo(String studentID, String name, String email, String grade) {
        this.studentID = studentID;
        this.name      = name;
        this.email     = email;
        this.grade     = grade;
    }

    public String getStudentID() {return studentID;}
    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getGrade() {return grade;}
}
