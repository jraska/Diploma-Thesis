package com.jraska;

import android.app.Activity;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Base class for tests testing activity
 */
public abstract class ActivityBaseTest<T extends Activity> extends BaseTest {
  //region Fields

  private final Class<T> _activityClass;

  //endregion

  //region Constructors

  public ActivityBaseTest() {
    _activityClass = resolveActivityClass(getClass());
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


  @SuppressWarnings("unchecked")
  private static <T extends Activity> Class<T> resolveActivityClass(Class clazz) {
    Type type = clazz.getGenericSuperclass();

    while (!(type instanceof ParameterizedType)) {
      type = ((Class<?>) type).getGenericSuperclass();
    }

    return (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
  }

  /**
   * Method for reflection search for correct activity type through generics
   *
   * @return Nothing
   */
  private T getType() {
    return null;
  }

  //endregion
}
