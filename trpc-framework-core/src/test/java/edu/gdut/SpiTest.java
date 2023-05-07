package edu.gdut;

import edu.gdut.spi.UserService;
import org.junit.Test;

import java.util.Iterator;
import java.util.ServiceLoader;

public class SpiTest {

    @Test
    public void testSpi() {
        ServiceLoader<UserService> services = ServiceLoader.load(UserService.class);
        Iterator<UserService> iterator = services.iterator();
        while (iterator.hasNext()) {
            UserService us = iterator.next();
            us.save();
        }
    }

}
