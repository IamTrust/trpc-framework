package edu.gdut.spi.impl;

import edu.gdut.spi.UserService;

public class DefaultUserServiceImpl implements UserService {
    @Override
    public void save() {
        System.out.println("save userInfo");
    }
}
