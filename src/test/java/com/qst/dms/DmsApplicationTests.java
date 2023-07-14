package com.qst.dms;

import com.qst.dms.component.MailUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DmsApplicationTests {
    @Test
    void test01() throws Exception {
        for (int i = 0; i < 1000000000; i++) {
            System.out.print("");
        }
    }

    @Autowired
    private MailUtils mailUtils;

    @Test
    public void test02() throws Exception {
        mailUtils.sendEmail("570001953@qq.com", "你好，这是一封测试邮件，无需回复。", "测试邮件");
    }
}
