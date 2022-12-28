package com.blate.server.service.impl;

import com.blate.server.YebServerApplication;
import com.blate.server.service.IMenuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = YebServerApplication.class)
class MenuServiceImplTest {

    @Autowired
    private IMenuService menuService;

    @Test
    void updateRedisMenu() {
        System.out.println("ddd");
        menuService.getAllMenus();
    }
}