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

import lombok.var;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Eager parsed Annotation Mirrors
 */
public class AnnotatedMirrors implements Collection<AnnotationMirror> {
    final Map<TypeElement, AnnotationMirror> values = new HashMap<>();

    public AnnotatedMirrors(List<? extends AnnotationMirror> mirrors) {
        for (var mirror : mirrors) {
            values.put(((TypeElement) mirror.getAnnotationType().asElement()), mirror);
        }
    }


    //region Impl
    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return values.containsValue(o);
    }


    @Override
    public Iterator<AnnotationMirror> iterator() {
        return values.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return values.values().toArray();
    }


    @Override
    public <T> T[] toArray(T[] a) {
        return values.values().toArray(a);
    }

    @Override
    public boolean add(AnnotationMirror annotationMirror) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return values.values().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends AnnotationMirror> c) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    //endregion

    public Optional<AnnotatedMirror> find(Predicate<TypeElement> predicate) {
        for (TypeElement key : values.keySet()) {
            if (predicate.test(key)) return Optional.of(new AnnotatedMirror(values.get(key)));
        }
        return Optional.empty();
    }

    public Stream<AnnotatedMirror> findStream(Predicate<TypeElement> predicate) {
        return values.keySet().stream().filter(predicate).map(values::get).map(AnnotatedMirror::new);
    }

    public Optional<AnnotatedMirror> find(Class<? extends Annotation> type) {
        for (TypeElement key : values.keySet()) {
            if (key.getQualifiedName().toString().equals(type.getCanonicalName()))
                return Optional.of(new AnnotatedMirror(values.get(key)));
        }
        return Optional.empty();
    }

    @SafeVarargs
    public final Optional<Map.Entry<Class<? extends Annotation>, AnnotatedMirror>> oneOf(Class<? extends Annotation>... types) {
        if (types.length == 0 || values.isEmpty()) return Optional.empty();
        var names = Arrays.stream(types).collect(Collectors.toMap(Class::getCanonicalName, Function.identity()));
        var keys = names.keySet();
        return values.keySet()
                .stream()
                .filter(x -> keys.contains(x.getQualifiedName().toString()))
                .findFirst()
                .map(x -> new AbstractMap.SimpleEntry<>(
                        names.get(x.getQualifiedName().toString()),
                        new AnnotatedMirror(values.get(x))));
    }
}
