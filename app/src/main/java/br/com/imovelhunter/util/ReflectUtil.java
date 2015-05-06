package br.com.imovelhunter.util;

import java.lang.reflect.Field;

/**
 * Created by Washington Luiz on 21/04/2015.
 */
public class ReflectUtil {

    public Object get(Object obj,String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Class<?> classe = obj.getClass();
        Object result = null;

        Field field = classe.getDeclaredField(fieldName);

        if(field.isAccessible()){
            result = field.get(obj);
        }else{
            field.setAccessible(true);
            result = field.get(obj);
            field.setAccessible(false);
        }

        return result;
    }

}
