package model;

public class User {
    int id;
    String login;
    String password;
    Role role;


    public User(int idUser,String loginUser, String passwordUser,Role role){
        this.id = idUser;
        this.login = loginUser;
        this.password = passwordUser;
        this.role = role;
    }
    public int getId(){
        return id;
    }
    public String getLogin(){
        return login;
    }
    public String getPassword(){
        return password;
    }
    public Role getRole(){
        return role;
    }

    public String get_info(){
        return String.format("Your id : %d\nYour login : %s\nYour password : %s\n",id,login,password);
    }

    public void setLogin(String login) {
        this.login = login;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
