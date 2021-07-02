import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

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

            List<String> singleParam = new ArrayList<>();

            List<String> mulParam = new ArrayList<>();

            for (int i = 0; i < parameterAnnotations.length; i++) {
                for (Annotation paramAnnotation : parameterAnnotations[i]) {
                    if (paramAnnotation.annotationType().equals(Param.class)) {
                        joinParam(paramAnnotation, args[i], singleParam, mulParam);
                    } else if (paramAnnotation.annotationType().equals(Params.class)) {
                        Params tempParam = (Params) paramAnnotation;
                        Param[] params = tempParam.value();
                        joinParams(params, args[i], singleParam, mulParam);
                    }
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder resSb = new StringBuilder();
            singleParam.forEach(item -> stringBuilder.append(item));
            List<String> res = new ArrayList<>();
            for (int i = 0; i < mulParam.size(); i++) {
                StringBuilder append = resSb.append(stringBuilder).append(mulParam.get(i));
                res.add(append.toString());
                resSb.delete(0, resSb.length());
            }
            res.forEach(System.out::println);
        }


        return method.invoke(target, args);
    }

    private void joinParam(Annotation annotation, Object object, List<String> singleParam, List<String> mulParam) throws Throwable {
        Param tempParam = (Param) annotation;
        String objectMethod = tempParam.value();
        ParamTypeEnum paramType = tempParam.type();
        if (object == null) {
            return;
        }
        switch (paramType) {
            case MAP:
                break;
            case OBJECT:
                Object o = doSome(object, objectMethod);
                singleParam.add(o.toString());
                break;
            case COLLECTION:
                Collection<Object> objects = listDoSome(object, objectMethod);
                List<String> tempList = objects.stream().map(Object::toString).collect(Collectors.toList());
                mulParam.addAll(tempList);
                break;
            default:
                break;
        }
    }

    private void joinParams(Annotation[] annotations, Object object, List<String> singleParam, List<String> mulParam) throws Throwable {
        //判断参数是否是Collection的
        if (object instanceof Collection) {
            Collection<Object> collections = (Collection<Object>) object;
            for (Object tempObj : collections) {
                StringBuilder tempBuilder = new StringBuilder();
                for (Annotation annotation : annotations) {
                    Param param = (Param) annotation;
                    Object tempResult = doSome(tempObj, param.value());
                    tempBuilder.append(tempResult.toString());
                }
                mulParam.add(tempBuilder.toString());
            }
        } else {
            for (Annotation annotation : annotations) {
                joinParam(annotation, object, singleParam, mulParam);
            }
        }

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
