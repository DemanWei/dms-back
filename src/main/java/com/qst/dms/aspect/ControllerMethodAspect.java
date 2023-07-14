package com.qst.dms.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qst.dms.domain.Api;
import com.qst.dms.domain.OperLog;
import com.qst.dms.service.ApiService;
import com.qst.dms.service.OperLogService;
import com.qst.dms.utils.ReflectUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.*;

@Component
@Aspect
public class ControllerMethodAspect {
    @Autowired
    private OperLogService operLogService;
    @Autowired
    private ApiService apiService;


    @Before("execution(* com.qst.dms.controller.*.*(..))")
    public void before(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();

        // 请求方法
        String requestMethod = "";

        // 访问路径: controller的path + method的path
        String path = "";
        Method method = ((MethodSignature) signature).getMethod();
        Class<?> controllerClass = method.getDeclaringClass();
        RequestMapping controllerRequestMapping = controllerClass.getAnnotation(RequestMapping.class);
        String controllerPath = controllerRequestMapping.value()[0];
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (getMapping != null) {
            // 最终 访问路径
            path = controllerPath + getMapping.value()[0];
            requestMethod = "GET";
        } else if (postMapping != null) {
            path = controllerPath + postMapping.value()[0];
            saveOperLog(joinPoint, method, path);
            requestMethod = "POST";
        } else if (putMapping != null) {
            path = controllerPath + putMapping.value()[0];
            saveOperLog(joinPoint, method, path);
            requestMethod = "PUT";
        } else if (deleteMapping != null) {
            path = controllerPath + deleteMapping.value()[0];
            saveOperLog(joinPoint, method, path);
            requestMethod = "DELETE";
        }

        // 新增接口信息或增加visit访问数
        recordApiOrChangeVisit(path, requestMethod, method);
    }


    /**
     * 新增接口信息或增加visit访问数
     */
    private void recordApiOrChangeVisit(String path, String requestMethod, Method method) {
        // 根据path查询api
        Api api = apiService.getOne(
                new LambdaQueryWrapper<Api>()
                        .eq(path.length() > 0, Api::getPath, path)
        );
        if (api != null) {
            // 增加visit访问数
            api.setVisit(api.getVisit() + 1);
            // 更新
            apiService.updateById(api);
        } else {
            // 插入接口信息
            api = new Api();
            api.setPath(path);
            api.setMethod(requestMethod);
            // 设置请求参数信息
            // 获取方法上的@ApiImplicitParams注解
            ApiImplicitParams apiImplicitParams = method.getAnnotation(ApiImplicitParams.class);
            if (apiImplicitParams != null) {
                StringBuilder paramType = new StringBuilder();
                StringBuilder example = new StringBuilder();
                ApiImplicitParam[] apiImplicitParamsArr = apiImplicitParams.value();
                // 遍历@ApiImplicitParam注解数组
                for (int i = 0; i < apiImplicitParamsArr.length; i++) {
                    ApiImplicitParam apiImplicitParam = apiImplicitParamsArr[i];
                    example.append(apiImplicitParam.example());
                    paramType.append(apiImplicitParam.paramType());
                    if (i < apiImplicitParamsArr.length - 1) {
                        paramType.append(",");
                    }
                }
                api.setParamType(paramType.toString());
                api.setParamExample(example.toString());
            }

            // 获取返回值的类型
            Type type = method.getGenericReturnType();// 返回属性声明的Type类型
            if (type instanceof ParameterizedType) {
                //强转为ParameterizedType对象
                ParameterizedType parameterizedType = (ParameterizedType) type;
                //获取原始类型
//                Type rawType = parameterizedType.getRawType();
//                System.out.println("原始类型为：" + rawType);      // class com.qst.dms.utils.R
                //获取map的类型的所有泛型信息
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
//                for (int i = 0; i < actualTypeArguments.length; i++) {
//                    System.out.println("第" + (i + 1) + "个泛型为：" + actualTypeArguments[i]);   // java.util.List<com.qst.dms.domain.User>
//                }
                String respType = actualTypeArguments[0].getTypeName();
                String response = "code={code}, msg={msg}, data=" + respType;
                api.setResponseBody(response);
            }

            // 获取接口描述
            // ApiOperation注解
            ApiOperation apiOperationAnno = method.getAnnotation(ApiOperation.class);
            if (apiOperationAnno != null) {
                String describe = apiOperationAnno.value();
                api.setDescribe(describe);
            }
            // 初始化访问次数
            api.setVisit(1L);
            // 插入
            apiService.save(api);
        }
    }


    /**
     * 对于非查询操作, 记录操作日志
     */
    private void saveOperLog(JoinPoint joinPoint, Method method, String path) {
        OperLog operLog = new OperLog();
        // TODO 获取操作者id
        operLog.setUserId(1L);
        // 获取访问path
        operLog.setPath(path);

        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();
        StringBuilder param = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            // 参数metadata
            String parameterName = parameters[i].getName();
            // 参数值
            Object parameterValue = args[i];
            // 基本类型 => userId=1,
            if (ReflectUtils.isPrimitive(parameterValue)) {
                param.append(parameterName)
                        .append("=")
                        .append(parameterValue)
                        .append(",");
            } else {
                // [Rule(id=null, metricName=cpu_usage, metricValue=10, metricUnit=percent, userId=1, createTime=null, updateTime=null)]
                // [User(id=null, username=90217, password=123abc, createTime=null)]
                // 引用类型 => username=90217, password=123abc,
                for (Field field : parameterValue.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(parameterValue);
                        if (value != null) {
                            param.append(field.getName())
                                    .append("=")
                                    .append(value)
                                    .append(",");
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // 没有参数,不设置,直接为null
        if (param.length() > 1) {
            // 去掉最后一个逗号","
            param.deleteCharAt(param.length() - 1);
            operLog.setParam(param.toString());
        }

        // 保存至数据库
        operLogService.save(operLog);
    }
}
