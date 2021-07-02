import java.lang.annotation.*;

/**
 * @author 陈俊宏
 */
@Repeatable(Params.class)
public @interface Param {
    /**
     * 对象使用的方法
     */
    String value() default "toString";

    /**
     * 对象的类型
     */
    ParamTypeEnum type() default ParamTypeEnum.OBJECT;
}
