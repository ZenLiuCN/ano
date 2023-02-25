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
import lombok.experimental.Accessors;
import lombok.var;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.stream.Collectors;

/**
 * parsed AnnotationMirror
 *
 * @author Zen.Liu
 * @since 2023-02-25
 */
public class AnnotatedMirror {
    protected final Map<String, AnnotationValue> values = new HashMap<>();
    @Getter
    @Accessors(fluent = true)
    protected final TypeElement annotationType;
    @Getter
    @Accessors(fluent = true)
    protected final String qualifiedName;

    public AnnotatedMirror(AnnotationMirror mirror) {
        final Map<String, AnnotationValue> defaults = new HashMap<>();
        mirror.getElementValues().forEach((k, v) -> {
            var name = k.getSimpleName().toString();
            var def = k.getDefaultValue();
            if (!Objects.equals(def, v)) {
                values.put(name, v);
            }
        });
        annotationType = ((TypeElement) mirror.getAnnotationType().asElement());
        qualifiedName = annotationType.getQualifiedName().toString();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public <T> Optional<T> fetch(String name, Class<T> tClass) {
        return Optional.ofNullable(values.get(name))
                .map(x -> {
                    var v = x.getValue();
                    if (tClass.isInstance(v)) return tClass.cast(v);
                    return null;
                });
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<List<T>> fetchList(String name, Class<T> tClass) {
        return Optional.ofNullable(values.get(name))
                .map(AnnotationValue::getValue)
                .filter(List.class::isInstance)
                .map(List.class::cast)
                .map(x -> (List<T>) x.stream()
                        .map(v -> ((AnnotationValue) v).getValue())
                        .filter(tClass::isInstance)
                        .map(tClass::cast)
                        .collect(Collectors.toList())
                );
    }

    public Optional<Boolean> getBoolean(String name) {
        return fetch(name, Boolean.class);
    }

    public Optional<Byte> getByte(String name) {
        return fetch(name, Byte.class);
    }

    public Optional<Short> getShort(String name) {
        return fetch(name, Short.class);
    }

    public Optional<Integer> getInteger(String name) {
        return fetch(name, Integer.class);
    }

    public Optional<Long> getLong(String name) {
        return fetch(name, Long.class);
    }

    public Optional<Float> getFloat(String name) {
        return fetch(name, Float.class);
    }

    public Optional<Double> getDouble(String name) {
        return fetch(name, Double.class);
    }

    public Optional<Character> getCharacter(String name) {
        return fetch(name, Character.class);
    }

    public Optional<String> getString(String name) {
        return fetch(name, String.class);
    }

    public Optional<TypeMirror> getType(String name) {
        return fetch(name, TypeMirror.class);
    }

    public Optional<VariableElement> getEnum(String name) {
        return fetch(name, VariableElement.class);
    }

    public Optional<AnnotationMirror> getAnnotationMirror(String name) {
        return fetch(name, AnnotationMirror.class);
    }

    public Optional<AnnotatedMirror> getAnnotatedMirror(String name) {
        return fetch(name, AnnotationMirror.class).map(AnnotatedMirror::new);
    }


    public Optional<List<Boolean>> getBooleans(String name) {
        return fetchList(name, Boolean.class);
    }

    public Optional<List<Byte>> getBytes(String name) {
        return fetchList(name, Byte.class);
    }

    public Optional<List<Short>> getShorts(String name) {
        return fetchList(name, Short.class);
    }

    public Optional<List<Integer>> getIntegers(String name) {
        return fetchList(name, Integer.class);
    }

    public Optional<List<Long>> getLongs(String name) {
        return fetchList(name, Long.class);
    }

    public Optional<List<Float>> getFloats(String name) {
        return fetchList(name, Float.class);
    }

    public Optional<List<Double>> getDoubles(String name) {
        return fetchList(name, Double.class);
    }

    public Optional<List<Character>> getCharacters(String name) {
        return fetchList(name, Character.class);
    }

    public Optional<List<String>> getStrings(String name) {
        return fetchList(name, String.class);
    }

    public Optional<List<TypeMirror>> getTypes(String name) {
        return fetchList(name, TypeMirror.class);
    }

    public Optional<List<VariableElement>> getEnums(String name) {
        return fetchList(name, VariableElement.class);
    }

    public Optional<List<AnnotationMirror>> getAnnotationMirrors(String name) {
        return fetchList(name, AnnotationMirror.class);
    }

    public Optional<List<AnnotatedMirror>> getAnnotatedMirrors(String name) {
        return fetchList(name, AnnotationMirror.class).map(x -> x.stream().map(AnnotatedMirror::new).collect(Collectors.toList()));
    }
}
