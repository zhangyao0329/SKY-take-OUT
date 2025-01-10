package com.sky.annotation;

import com.sky.enumeration.OperationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于表示某个方法需要进行公共字段的自动填充。
 */
// 定义一个注解，用于自动填充数据库操作的类型
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //    指定当前数据库操作数据库的类型(update insert)，通过枚举的方式来指定
    OperationType value();
}
