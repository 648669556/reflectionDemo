
import java.util.Set;

/**
 * @author 陈俊宏
 */
public class BeProxy implements ProxyMethod {

    @Override
    @MyTest(lockTableName = "test1")
    public void giveMoney(@Param String test,
                          @Param Integer number,
                          @Param(type = ParamTypeEnum.COLLECTION,value = "getAge")
                               Set<Student> list) {
        System.out.println("业务内容");
    }

}
