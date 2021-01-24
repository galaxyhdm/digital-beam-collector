package dev.markusk.digitalbeam.collector.misc;

import java.util.HashMap;
import java.util.Map;

public class ClassContainer {

  private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<>();

  public static <T> T newInstance(final String path, final Class<T> classOfT) throws Exception {
    final Class<?> baseClass;
    if (CLASS_CACHE.containsKey(path)) {
      baseClass = CLASS_CACHE.get(path);
    } else {
      baseClass = Class.forName(path);
      CLASS_CACHE.put(path, baseClass);
    }
    final Class<? extends T> subclass = baseClass.asSubclass(classOfT);
    return subclass.getDeclaredConstructor().newInstance();
  }

}
