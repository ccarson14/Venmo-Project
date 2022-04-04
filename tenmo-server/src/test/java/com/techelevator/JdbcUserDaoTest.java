package com.techelevator;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;


public class JdbcUserDaoTest extends BaseDaoTest {

    private JdbcUserDao sut;

    @Before
    public void setup() {
        sut = new JdbcUserDao(new JdbcTemplate(dataSource));
    }

    @Test
    public void findByUsername_returns_user_that_is_not_null() {
        User user = sut.findByUsername("user1");
        Assert.assertNotNull("findByUsername failed to find user in database", user);
    }



}
