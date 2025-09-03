package com.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/whatsapp")
public class WhatsappController {

    @Autowired
    private WhatsappService service;

    @PostMapping("/create-user")
    public String createUser(@RequestParam String name, @RequestParam String mobile) throws Exception {
        return service.createUser(name, mobile);
    }

    @PostMapping("/create-group")
    public Group createGroup(@RequestBody List<User> users) {
        return service.createGroup(users);
    }

    @PostMapping("/create-message")
    public int createMessage(@RequestParam String content) {
        return service.createMessage(content);
    }

    @PostMapping("/send-message")
    public int sendMessage(@RequestBody Message message, @RequestParam User sender, @RequestParam Group group) throws Exception {
        return service.sendMessage(message, sender, group);
    }

    @PutMapping("/change-admin")
    public String changeAdmin(@RequestParam User approver, @RequestParam User user, @RequestParam Group group) throws Exception {
        return service.changeAdmin(approver, user, group);
    }

    @DeleteMapping("/remove-user")
    public int removeUser(@RequestParam User user) throws Exception {
        return service.removeUser(user);
    }

    @GetMapping("/find-message")
    public String findMessage(@RequestParam Date start, @RequestParam Date end, @RequestParam int K) throws Exception {
        return service.findMessage(start, end, K);
    }
}
