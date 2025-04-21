package warren.aandp.project02.login;

public class StudentCourseInfo {
    private String courseID;
    private String courseName;
    private String professorName;
    private String time;
    private String days;
    private String grade;

    public StudentCourseInfo(String courseID, String courseName, String professorName, String time, String days, String grade) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.professorName = professorName;
        this.time = time;
        this.days = days;
        this.grade = grade;
    }

    public String getCourseID() {return courseID;}

    public String getCourseName() {
        return courseName;
    }

    public String getProfessorName() {
        return professorName;
    }

    public String getTime() {
        return time;
    }

    public String getDays() {
        return days;
    }

    public String getGrade() {
        return grade;
    }
}
