/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package me.rhys.bedrock.util.box.reflection;

public interface ConstructorInvoker {
    /**
     * Invoke a constructor for a specific class.
     *
     * @param arguments - the arguments to pass to the constructor.
     * @return The constructed object.
     */
    Object invoke(Object... arguments);
}