package gatech.cs2340.android.drop.model;

public class User {

    //public variable required by FireBase
    public String _name;
    public String _email;
    public String _password;
    public String _userType;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String password, String userType) {
        _name = name;
        _email = email;
        _password = password;
        _userType = userType;
    }

}