package com.jraska.dialog.lambda;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import java.io.*;

@Builder(builderClassName = "Builder")
@EqualsAndHashCode
public final class DialogFields {
  public final CharSequence title;
  public final CharSequence message;
  public final int iconRes;
  public final DialogDelegateProvider positiveProvider;
  public final CharSequence positiveText;
  public final DialogDelegateProvider neutralProvider;
  public final CharSequence neutralText;
  public final DialogDelegateProvider negativeProvider;
  public final CharSequence negativeText;
  public final boolean cancelable;

  void validate() {
    validateSerializable(positiveProvider);
    validateSerializable(neutralProvider);
    validateSerializable(negativeProvider);
  }

  @SneakyThrows
  static void validateSerializable(Serializable serializable) {
    if (serializable == null) {
      return;
    }

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);

    outputStream.writeObject(serializable);

    ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
    Object deserialized = inputStream.readObject();

    if (deserialized == null) {
      throw new IllegalArgumentException(serializable.getClass() + " does not implement Serializable properly");
    }
  }
}
