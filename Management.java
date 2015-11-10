package management;

import java.lang.Integer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import app.MyDbApp;
import utils.TextIO;

public class Management {

    private String sql;
    private String editInput;

    public void setEditInput(String editInput) {
        this.editInput = editInput;
    }

    public String getEditInput() {
        return editInput;
    }

    public void menuManagement()
    {
        try {
            int select;
            TextIO.putln("---------------- MANAGEMENT MENU ----------------");
            TextIO.putln("1. Manage Student");
            TextIO.putln("2. Manage Course");
            TextIO.putln("3. Manage Enrolment");
            TextIO.putln("4. Report");
            TextIO.putln("0. Exit");
            TextIO.put("Your choice is: ");
            select = TextIO.getlnInt();
            Course_Management course = new Course_Management();
            Student_Management student = new Student_Management();
            Enrolment_Management enrol = new Enrolment_Management();
            Reports reports = new Reports();
            switch (select){
                case 1:TextIO.putln("---------------------------------------------------");
                    this.displayConsole("student");
                    student.menuStudent();
                    break;
                case 2:TextIO.putln("---------------------------------------------------");
                    this.displayConsole("course");
                    course.menuCourse();
                    break;
                case 3:TextIO.putln("---------------------------------------------------");
                    this.displayConsole("enrolment");
                    enrol.menuEnrolment();
                    break;
                case 4:TextIO.putln("---------------------------------------------------");
                    reports.menuReports();
                    break;
                case 0:System.exit(0);
                    break;
                default:
                    TextIO.putln("Invalid input. Please enter again.\n");
                    this.menuManagement();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        public boolean addRow(String tableName) throws SQLException {

        MyDbApp app = new MyDbApp();
        String[] column = app.getColumnName(tableName);
        String[] table = new String[column.length];
        String input="";
        this.sql = "INSERT INTO " + tableName + "  VALUES (";
        for (int i = 0; i < column.length; i++) {
            if (table[i] == null)
                table[i] = "";
            try{
                do{
                    TextIO.putln("INPUT " + column[i] + ": ");
                    input = TextIO.getlnString();
                } while (!app.validateAdd(input, column[i], tableName));
            } catch (SQLException e)
            {
                e.getMessage();
            }
            table[i] += input;
            if (i == column.length - 1)
                this.sql += "'" + table[i] + "'";
            else
                this.sql += "'" + table[i] + "'" + ",";
        }
        this.sql += ")";
        TextIO.putln("-----------------------------SUCCESSFUL------------------------------");
        return app.insert(this.sql);
    }

    public boolean deleteRow(String tableName) throws SQLException
    {
        MyDbApp app = new MyDbApp();
        String ID;
        do {
            TextIO.putln("INPUT StudentID: ");
            ID = TextIO.getln();
        }while (!app.validateDelete(ID,tableName));
        if (!tableName.equals("enrolment")) {
            this.sql = "DELETE FROM " + tableName + " WHERE " + tableName + "id = " + "'"+ID+"'";
        }
        if (tableName.equals("enrolment")) {
            this.sql = "DELETE FROM " + tableName + " WHERE " + "student = " + ID;
        }
        TextIO.putln("-----------------------------SUCCESSFUL------------------------------");
        return app.delete(this.sql);
    }

    public boolean editRow(String tableName) throws SQLException
    {
        MyDbApp app = new MyDbApp();
        String ID;
        String input="";
        do {
            TextIO.putln("INPUT " + tableName + "ID: ");
            ID = TextIO.getln();
        }while (!app.validateInput(ID,tableName));

        String[] column = app.getColumnName(tableName);
        String[] table = new String[column.length];
        this.sql = "UPDATE "+tableName+" SET ";
        for (int i = 1; i < column.length; i++) {
            TextIO.putln(column[i] + ": ");
            if (table[i] == null)
                table[i] = "";
            try{
                do{
                    TextIO.putln("INPUT " + column[i] + ": ");
                    input = TextIO.getlnString();
                } while (!app.validateEdit(input, column[i], tableName));
            } catch (SQLException e)
            {
                e.getMessage();
            }
            table[i] += input;
            if (i == column.length - 1)
                this.sql +=column[i]+ "=" + "'" + table[i] + "'";
            else
                this.sql +=column[i]+ "=" + "'" + table[i] + "'" + ",";
        }
        this.sql+=" WHERE "+tableName+"id = "+ID;
        TextIO.putln("-----------------------------SUCCESSFUL------------------------------");
        return app.update(this.sql);
    }

    public String displayAll(String tableName)
    {
        this.sql="SELECT * FROM "+tableName+ " ORDER BY "+tableName+"id";
        return this.sql;
    }

    public String OrderbyDESC(String tableName,String order)
    {
        this.sql ="SELECT * FROM "+tableName+" ORDER BY "+ order+" DESC";
        return this.sql;
    }

	public void writeToHTML(String sql,String filename) throws SQLException {
        MyDbApp app = new MyDbApp();
        app.writetoHTML(sql,filename);
        app.close();
    }

    public int getMaxLength(String tableName,String columnName) throws SQLException
    {
        MyDbApp app = new MyDbApp();
        return app.getMaxLength(tableName,columnName);
    }

    public  void writeToTXT(String tableName) throws SQLException {
        String userDir = System.getProperty("user.dir");
        String fileChar = System.getProperty("file.separator");
        String file = userDir+fileChar+ "src"+"\\"+"app"+"\\"+ tableName+".txt";
        TextIO.putln("Written result to file " + file);
        TextIO.writeFile(file);
        displayConsole(tableName);
        TextIO.writeStandardOutput();
    }
    public void displayConsole(String tableName) throws SQLException
    {
        MyDbApp app = new MyDbApp();
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int i=0;i<this.getColumnName(tableName).length;i++) {
            String[] s =getColumnName(tableName);
            int a = this.getMaxLength(tableName,s[i]);
            list.add(a);
        }
        app.displayConsole(tableName, list);
    }

    public String[] getColumnName(String tableName) throws SQLException
    {
        MyDbApp app = new MyDbApp();
        return app.getColumnName(tableName);
    }

    public String enrolStudent() throws SQLException
    {
        MyDbApp app = new MyDbApp();
        String id;
        do {
            TextIO.putln("INPUT StudentID: ");
            id = TextIO.getln();
        }while (!app.validateInput(id,"student"));
        this.sql="SELECT enrolment.student, CONCAT(student.lastname ||' '|| student.firstname) AS fullname, " +
                "enrolment.course,course.name, enrolment.semester, enrolment.finalgrade " +
                "FROM ((enrolment INNER JOIN student ON enrolment.student = student.studentid)" +
                "INNER JOIN course ON enrolment.course = course.courseid) " +
                "WHERE student.studentid="+id;
        return this.sql;
    }

    public String enrolCourse() throws SQLException
    {
        MyDbApp app = new MyDbApp();
        String id;
        do {
            TextIO.putln("INPUT CourseID: ");
            id = TextIO.getln();
        }while (!app.validateInput(id,"course"));
        this.sql="SELECT enrolment.student, CONCAT(student.lastname ||' '|| student.firstname) AS fullname, " +
                "enrolment.course,course.name, enrolment.semester, enrolment.finalgrade " +
                "FROM ((enrolment INNER JOIN student ON enrolment.student = student.studentid)" +
                "INNER JOIN course ON enrolment.course = course.courseid) WHERE course.courseid="+"'"+id+"'";
        return this.sql;
    }

    public String failGrade()
    {
        this.sql="SELECT student.studentid, student.lastname||' '||student.firstname AS \"Student Full Name\", \n" +
                "COUNT(enrolment.course) AS \"Number of Fail Courses\"\n" +
                "FROM enrolment\n" +
                "LEFT JOIN student\n" +
                "ON student.studentid = enrolment.student\n" +
                "WHERE enrolment.finalgrade = 'F'\n" +
                "GROUP BY student.studentid\n" +
                "ORDER BY student.studentid";
        return this.sql;
    }

}
