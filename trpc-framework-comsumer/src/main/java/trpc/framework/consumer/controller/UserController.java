package trpc.framework.consumer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trpc.framework.interfaces.UserService;
import org.trpc.framework.spring.starter.annotation.TRpcReference;

@RestController
@RequestMapping("/user")
public class UserController {

    @TRpcReference
    private UserService userService;

    @GetMapping("/saveUser")
    public String saveUser() {
        userService.saveUser();
        return "save user";
    }

}
