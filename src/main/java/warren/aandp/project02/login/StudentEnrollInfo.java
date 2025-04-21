package warren.aandp.project02.login;

public class StudentEnrollInfo {
    private String courseID;
    private String courseName;
    private String professorName;
    private String time;
    private String days;

    public StudentEnrollInfo(String courseID, String courseName, String professorName, String time, String days) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.professorName = professorName;
        this.time = time;
        this.days = days;
    }

    public String getCourseID() {return courseID;}

    public String getCourseName() {return courseName;}

    public String getProfessorName() {return professorName;}

    public String getTime() {return time;}

    public String getDays() {return days;}
}
