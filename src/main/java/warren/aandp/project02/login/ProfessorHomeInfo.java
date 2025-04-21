package warren.aandp.project02.login;

public class ProfessorHomeInfo {
    private String courseID;
    private String courseName;
    private String time;
    private String days;

    public ProfessorHomeInfo(String courseID, String courseName, String time, String days) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.time = time;
        this.days = days;
    }

    public String getCourseID() {return courseID;}
    public String getCourseName() {return courseName;}
    public String getTime() {return time;}
    public String getDays() {return days;}
}
