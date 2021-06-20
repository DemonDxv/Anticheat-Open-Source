/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package me.rhys.bedrock.tinyprotocol.reflection;

public interface MethodInvoker {
    /**
     * Invoke a method on a specific target object.
     *
     * @param target    - the target object, or NULL for a static method.
     * @param arguments - the arguments to pass to the method.
     * @return The return value, or NULL if is void.
     */
    Object invoke(Object target, Object... arguments);
}