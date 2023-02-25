/*
 * Source of ano
 * Copyright (C) 2023.  Zen.Liu
 *
 * SPDX-License-Identifier: GPL-2.0-only WITH Classpath-exception-2.0"
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; version 2.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Class Path Exception
 * Linking this library statically or dynamically with other modules is making a combined work based on this library. Thus, the terms and conditions of the GNU General Public License cover the whole combination.
 *  As a special exception, the copyright holders of this library give you permission to link this library with independent modules to produce an executable, regardless of the license terms of these independent modules, and to copy and distribute the resulting executable under terms of your choice, provided that you also meet, for each linked independent module, the terms and conditions of the license of that module. An independent module is a module which is not derived from or based on this library. If you modify this library, you may extend this exception to your version of the library, but you are not obligated to do so. If you do not wish to do so, delete this exception statement from your version.
 */

package ano;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.var;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Holder proxy of Annotation, with default value check.
 *
 * @author Zen.Liu
 * @since 2023-02-25
 */
@SuppressWarnings("unchecked")
public class Values<T extends Annotation> {
    public static <T extends Annotation> Values<T> of(T val) {
        return new Values<>(val);
    }

    private final T delegate;
    @Getter
    private final T value;
    private volatile Boolean empty;


    Values(T value) {
        this.value = value;
        var type = value.annotationType();
        this.delegate = (T) Proxy.newProxyInstance(value.getClass().getClassLoader(), value.getClass().getInterfaces(), (p, m, args) -> {
            var ret = m.getReturnType();
            var pri = ret.isPrimitive();
            var b = ret == Boolean.TYPE;
            if (m.getDeclaringClass().equals(type) && m.getParameterCount() == 0) {
                var v = m.invoke(value);
                return Objects.equals(m.getDefaultValue(), m.invoke(value)) ? (pri ? m.getDefaultValue() : null) : v;
            }
            return (pri ? b ? false : 0 : null);
        });
    }

    public <V> Optional<V> fetch(Function<T, V> method) {
        return Optional.ofNullable(method.apply(delegate));
    }

    public <V> V get(Function<T, V> method) {
        return method.apply(value);
    }

    @SuppressWarnings("ConstantConditions")
    @SneakyThrows
    public boolean isEmpty() {
        if (empty == null) {
            synchronized (value) {
                for (var m : value.annotationType().getDeclaredMethods()) {
                    if (m.getReturnType().isArray()) {
                        var com = m.getReturnType().getComponentType();
                        var def = m.getDefaultValue();
                        var val = m.invoke(value);
                        if (
                                (com == Boolean.TYPE && !Arrays.equals(((boolean[]) def), ((boolean[]) val))) ||
                                (com == Short.TYPE && !Arrays.equals(((short[]) def), ((short[]) val))) ||
                                (com == Long.TYPE && !Arrays.equals(((long[]) def), ((long[]) val))) ||
                                (com == Integer.TYPE && !Arrays.equals(((int[]) def), ((int[]) val))) ||
                                (com == Float.TYPE && !Arrays.equals(((float[]) def), ((float[]) val))) ||
                                (com == Double.TYPE && !Arrays.equals(((double[]) def), ((double[]) val))) ||
                                (com == Character.TYPE && !Arrays.equals(((char[]) def), ((char[]) val))) ||
                                (!Arrays.equals(((Object[]) def), ((Object[]) val)))
                        ) {
                            // System.out.println("found none empty on " + m.getName() + "==>" + m.getDefaultValue() + "vs" + m.invoke(value) + "::" + value);
                            empty = false;
                            break;
                        }
                    } else {
                        if (!Objects.equals(m.getDefaultValue(), m.invoke(value))) {
                            // System.out.println("found none empty on " + m.getName() + "==>" + m.getDefaultValue() + "vs" + m.invoke(value) + "::" + value);
                            empty = false;
                            break;
                        }
                    }
                }
                if (empty == null) empty = true;
            }
        }
        return empty;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
