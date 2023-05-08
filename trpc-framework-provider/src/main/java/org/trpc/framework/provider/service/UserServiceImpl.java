package org.trpc.framework.provider.service;

import org.trpc.framework.interfaces.UserService;
import org.trpc.framework.spring.starter.annotation.TRpcService;

@TRpcService
public class UserServiceImpl implements UserService {
    @Override
    public void saveUser() {
        System.out.println("=== save user ===");
    }

    @Override
    public void modifyUser() {
        System.out.println("=== modify user ===");
    }

    @Override
    public void getUser() {
        System.out.println("=== get user ===");
    }

    @Override
    public void removeUser() {
        System.out.println("=== remove user ===");
    }
}
