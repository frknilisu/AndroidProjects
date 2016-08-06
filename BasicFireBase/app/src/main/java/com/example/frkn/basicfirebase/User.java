package com.example.frkn.basicfirebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by frkn on 27.07.2016.
 */
public class User {

    public String name;
    public String email;
    public Long number;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, Long number) {
        this.name = name;
        this.email = email;
        this.number = number;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("number", number);
        return result;
    }

}
