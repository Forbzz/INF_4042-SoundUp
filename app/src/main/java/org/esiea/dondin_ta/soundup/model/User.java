package org.esiea.dondin_ta.soundup.model;


public class User {
    private int _id;
    private String username;
    private byte[] password;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public byte[] getPassword() {
        return password;
    }
}
