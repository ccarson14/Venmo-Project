package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.UserNotFoundException;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserDao userDao;


    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Return Map of usernames and user ids, without returning all user information.
     */

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("")
    public List<User> listUsers() {

        List<User> userList = userDao.findAll();

        return userList;

    }

    /**
     * Return username, given accountId
     */

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/{id}")
    public String getUsernameById(@PathVariable long id) throws UserNotFoundException {

        String username = userDao.findUsernameByAccountId(id);

        return username;
    }

}
