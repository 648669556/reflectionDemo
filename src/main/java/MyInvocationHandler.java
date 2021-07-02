import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author 陈俊宏
 */
public class MyInvocationHandler<T> implements InvocationHandler {

    T target;

    public MyInvocationHandler() {
    }

    public MyInvocationHandler(T target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //获取方法名称
        String methodName = method.getName();
        //获取方法参数
        Class<?>[] parameterTypes = method.getParameterTypes();
        //获取目标方法
        Method targetMethod = target.getClass().getMethod(methodName, parameterTypes);
        MyTest myAnnotation = targetMethod.getAnnotation(MyTest.class);
        if (myAnnotation != null) {
            //if class equals
            MyTest annotation = targetMethod.getAnnotation(MyTest.class);
            String lockTableName = annotation.lockTableName();
            int sleepTime = annotation.sleepTime();

            Annotation[][] parameterAnnotations = targetMethod.getParameterAnnotations();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                for (Annotation paramAnnotation : parameterAnnotations[i]) {
                    if (paramAnnotation.annotationType().equals(Params.class)) {
                        Params tempParam = (Params) paramAnnotation;
                        Param[] value = tempParam.value();
                        for (Param param : value) {
                            String objectMethod = param.value();
                            ParamTypeEnum paramType = param.type();
                            if (args[i] == null) {
                                continue;
                            }
                            switch (paramType) {
                                case MAP:
                                    break;
                                case OBJECT:
                                    Object o = doSome(args[i], objectMethod);
                                    stringBuilder.append(o);
                                    break;
                                case COLLECTION:
                                    Collection<Object> objects = listDoSome(args[i], objectMethod);
                                    for (Object object : objects) {
                                        stringBuilder.append(object);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }

            System.out.println("锁表名" + lockTableName);
            System.out.println("锁时间" + sleepTime);
            System.out.println("锁粒度" + stringBuilder.toString());
        }
        return method.invoke(target, args);
    }

    private Object doSome(Object object, String method) throws Throwable {
        Method targetMethod = null;
        try {
            targetMethod = object.getClass().getMethod(method);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodException("未找到对应的方法");
        }
        return targetMethod.invoke(object);
    }

    private Collection<Object> listDoSome(Object collection, String method) throws Throwable {
        Collection<Object> result = new ArrayList<>();
        Collection<Object> collections = (Collection<Object>) collection;
        Iterator<Object> iterator = collections.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            Object o = doSome(next, method);
            result.add(o);
        }
        return result;
    }
}
