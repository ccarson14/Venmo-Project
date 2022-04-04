package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/account")
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private UserDao userDao;
    private AccountDao accountDao;

    public AccountController(UserDao userDao, AccountDao accountDao){
        this.userDao = userDao;
        this.accountDao = accountDao;
    }


    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("")
    public Account getUserAccount(Principal principal) throws AccountNotFoundException {
        long userId = userDao.findIdByUsername(principal.getName());
        return accountDao.findAccountByUserId(userId);
    }

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/{id}")
    public long getAccountIdByUserId(@PathVariable long id, Principal principal) throws AccountNotFoundException {
        long accountId = accountDao.findAccountByUserId(id).getAccountId();
        return accountId;
    }


//    @ResponseStatus(HttpStatus.FOUND)
//    @GetMapping("/{id}")
//    public Account findAccountByAccountId(@PathVariable int id) throws AccountNotFoundException {
//        return accountDao.findAccountByAccountId(id);
//    }
//
//    @ResponseStatus(HttpStatus.FOUND)
//    @GetMapping("/{id}")
//    public Account findAccountByUserId(@PathVariable int id) throws AccountNotFoundException {
//        return accountDao.findAccountByUserId(id);
//    }
//
//    @ResponseStatus(HttpStatus.FOUND)
//    @GetMapping("/{id}")
//    public Account findBalance(@PathVariable int id) throws AccountNotFoundException {
//        return accountDao.findBalance(id);
//    }



}
