package com.qst.dms.controller;

import com.qst.dms.domain.DbInfo;
import com.qst.dms.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@RestController
@RequestMapping("/db")
@Api(tags = "数据库管理接口")
public class DbController {
    // 自动注入，不要手动
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @GetMapping("/info")
    @ApiOperation("查询数据库元数据信息")
    public R<DbInfo> dbInfo() throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession();
//
        Connection connection = sqlSession.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();

        DbInfo dbInfo = new DbInfo();
        dbInfo.setDriverName(metaData.getDriverName());
        dbInfo.setDatabaseProductName(metaData.getDatabaseProductName());
        dbInfo.setDatabaseProductVersion(metaData.getDatabaseProductVersion());

        // 使用数据库元数据进行后续操作
        return R.success(dbInfo);
    }

    @GetMapping("/check")
    @ApiOperation("数据库连接状态检测")
    public R<Integer> check() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        boolean status = result != null && result == 1;
        return status ? R.success(result) : R.error("数据库连接测试失败");
    }
}
