package com.cynerds.cyburger.models.profile;

import com.cynerds.cyburger.models.general.BaseModel;
import com.cynerds.cyburger.models.role.Role;

import java.util.Date;

/**
 * Created by fabri on 07/10/2017.
 */

public class Profile extends BaseModel {


    private Role role;
    private String userId;
    private int bonusPoints;
    private Date lastLogin;

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(int bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

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
