/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package student;

/**
 *
 * @author Kusanagi
 */
import java.io.Serializable;

public class Student implements Serializable {
    private String id;
    private String fullname;
    private String address;
    private String birthday;
    private String classname;
    private String email;

    public Student()
    {
        id = fullname = address = birthday = classname = email = "";
    }
    
    public Student(String id, String fullname, String address, String birthday, String email, String classname)
    {
        this.id = id;
        this.fullname = fullname;
        this.address = address;
        this.birthday = birthday;
        this.classname = classname;
        this.email = email;
    }

    public void setID(String id){
        this.id = id;
    }
    public String getID(){
        return this.id;
    }

    public void setFullname(String fullname){
        this.fullname = fullname;
    }
    public String getFullname(){
        return this.fullname;
    }

    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return this.email;
    }

    public void setAddress(String address){
        this.address = address;
    }
    public String getAddress(){
        return this.address;
    }

    public void setBirthday(String birthday){
        this.birthday = birthday;
    }
    public String getBirthday(){
        return this.birthday;
    }

    public void setClassname(String classname){
        this.classname = classname;
    }
    public String getClassname(){
        return this.classname;
    }
}
