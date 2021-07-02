import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TestProxy {
    public static void main(String[] args) {
        //真实的被代理对象
        BeProxy beProxy = new BeProxy();

        //我们的代理类
        MyInvocationHandler<ProxyMethod> handler = new MyInvocationHandler<>(beProxy);

        //生成代理对象
        ProxyMethod proxy = (ProxyMethod) Proxy.newProxyInstance(ProxyMethod.class.getClassLoader(), new Class<?>[]{ProxyMethod.class}, handler);


        Student student = new Student("a",5);

        Student student1 = new Student("b",10);

        List<Student> list = Arrays.asList(student,student1);

        //用代理对象去执行方法
        proxy.giveMoney("采购单号",8888,new HashSet<>(list));
    }
}
