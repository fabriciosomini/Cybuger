package com.cynerds.cyburger.models.profile;

import com.cynerds.cyburger.models.BaseModel;
import com.cynerds.cyburger.models.roles.Role;

/**
 * Created by fabri on 07/10/2017.
 */

public class Profile extends BaseModel {


    private Role role;
    private String userId;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
