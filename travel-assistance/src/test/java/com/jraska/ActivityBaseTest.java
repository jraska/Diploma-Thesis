package com.jraska;

import android.app.Activity;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import java.lang.reflect.Method;

/**
 * Base class for tests testing activity
 */
public abstract class ActivityBaseTest<T extends Activity> extends BaseTest {
  //region Fields

  private final Class<? extends Activity> _activityClass;

  //endregion

  //region Constructors

  public ActivityBaseTest() {
    _activityClass = findActivityClass(getClass());
  }

  //endregion

  //region Test Methods

  @Test
  public void testLifecyclePass() throws Exception {
    ActivityController<? extends Activity> controller = Robolectric.buildActivity(_activityClass);

    controller.create();
    controller.start();
    controller.resume();
    controller.pause();
    controller.stop();
    controller.destroy();
  }

  //endregion

  //region Methods


  private static Class<? extends Activity> findActivityClass(Class clazz) {
    try {
      return finActivtyClassUnchecked(clazz);
    }
    catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends Activity> finActivtyClassUnchecked(Class clazz)
      throws NoSuchMethodException {
    Class currentClazz = clazz;
    for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
      for (Method method : c.getDeclaredMethods()) {
        if (method.getName().equals("getType")) {
          return (Class<? extends Activity>) method.getReturnType();
        }
      }
    }
    throw new Error("Cannot determine correct type for getType() method.");
  }

  /**
   * Method for reflection search for correct activity type through generics
   *
   * @return Type fo tested activity
   */
  private T getType() {
    return null;
  }

  //endregion
}
