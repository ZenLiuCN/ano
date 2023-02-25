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

import lombok.SneakyThrows;
import lombok.var;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.Diagnostic;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * Util methods for Annotation process
 *
 * @author Zen.Liu
 * @since 2023-02-25
 */
public interface Utils {
    ProcessingEnvironment procEnv();

    //region TypeUtils
    default TypeMirror type(String fqn) {
        return procEnv().getElementUtils().getTypeElement(fqn).asType();
    }

    default TypeMirror type(Class<?> type) {
        return procEnv().getElementUtils().getTypeElement(type.getCanonicalName()).asType();
    }

    default TypeElement typeElement(TypeMirror type) {
        return ((TypeElement) procEnv().getTypeUtils().asElement(type));
    }

    default TypeElement typeElement(String qualifiedName) {
        return typeElement(type(qualifiedName));
    }

    default boolean isAssignable(TypeMirror type1, TypeMirror type2) {
        if (type1.getKind() == TypeKind.ERROR || type2.getKind() == TypeKind.ERROR) {
            procEnv().getMessager().printMessage(Diagnostic.Kind.OTHER, "one of " + type1 + " and " + type2 + " not resolved");
            var t1 = typeElement(type1);
            var t2 = typeElement(type2);
            if (t1 == null || t2 == null) return false;
            return t1.getQualifiedName().equals(t2.getQualifiedName());
        }
        return procEnv().getTypeUtils().isAssignable(type1, type2);
    }

    default boolean isAssignable(TypeMirror type1, Class<?> type2) {
        if (type1.getKind() == TypeKind.ERROR) {
            procEnv().getMessager().printMessage(Diagnostic.Kind.OTHER, "one of " + type1 + " and " + type2 + " not resolved");
            var t1 = typeElement(type1);
            if (t1 == null) return false;
            return t1.getQualifiedName().toString().equals(type2.getCanonicalName());
        }
        return procEnv().getTypeUtils().isAssignable(type1, typeElement(type2.getCanonicalName()).asType());
    }

    default boolean equals(TypeMirror type1, TypeMirror type2) {
        return procEnv().getTypeUtils().isSameType(type1, type2);
    }

    default boolean subtypeOf(TypeMirror type1, TypeMirror type2) {
        return procEnv().getTypeUtils().isSubtype(type1, type2);
    }

    default boolean contains(TypeMirror type1, TypeMirror type2) {
        return procEnv().getTypeUtils().contains(type1, type2);
    }

    @SuppressWarnings("SpellCheckingInspection")
    default boolean subsignatureOf(ExecutableType type1, ExecutableType type2) {
        return procEnv().getTypeUtils().isSubsignature(type1, type2);
    }

    default List<? extends TypeMirror> directSupertypes(TypeMirror type) {
        return procEnv().getTypeUtils().directSupertypes(type);
    }

    default TypeMirror erasure(TypeMirror type) {
        return procEnv().getTypeUtils().erasure(type);
    }

    default TypeElement boxedClass(PrimitiveType type) {
        return procEnv().getTypeUtils().boxedClass(type);
    }

    default PrimitiveType unboxedType(TypeMirror type) {
        return procEnv().getTypeUtils().unboxedType(type);
    }

    default TypeMirror capture(TypeMirror type) {
        return procEnv().getTypeUtils().capture(type);
    }

    default PrimitiveType primitiveType(TypeKind kind) {
        return procEnv().getTypeUtils().getPrimitiveType(kind);
    }

    default NullType nullType() {
        return procEnv().getTypeUtils().getNullType();
    }

    default NoType noType(TypeKind kind) {
        return procEnv().getTypeUtils().getNoType(kind);
    }

    default ArrayType arrayType(TypeMirror component) {
        return procEnv().getTypeUtils().getArrayType(component);
    }

    default WildcardType wildcardType(TypeMirror extendsBound, TypeMirror superBound) {
        return procEnv().getTypeUtils().getWildcardType(extendsBound, superBound);
    }

    default WildcardType wildcardExtendsOf(TypeMirror extendsBound) {
        return procEnv().getTypeUtils().getWildcardType(extendsBound, null);
    }

    default WildcardType wildcardSuperOf(TypeMirror superBound) {
        return procEnv().getTypeUtils().getWildcardType(null, superBound);
    }

    default DeclaredType declaredTypeOf(TypeElement typeElem, TypeMirror... typeArgs) {
        return procEnv().getTypeUtils().getDeclaredType(typeElem, typeArgs);
    }

    default DeclaredType declaredTypeOf(DeclaredType containing, TypeElement typeElem, TypeMirror... typeArgs) {
        return procEnv().getTypeUtils().getDeclaredType(containing, typeElem, typeArgs);
    }

    default TypeMirror asMemberOf(DeclaredType containing, Element element) {
        return procEnv().getTypeUtils().asMemberOf(containing, element);
    }
    //endregion

    //region ElementUtils
    default PackageElement packageOf(Element e) {
        return procEnv().getElementUtils().getPackageOf(e);
    }

    default boolean deprecated(Element e) {
        return procEnv().getElementUtils().isDeprecated(e);
    }

    default Name deprecated(TypeElement e) {
        return procEnv().getElementUtils().getBinaryName(e);
    }

    default List<? extends Element> allMembers(TypeElement e) {
        return procEnv().getElementUtils().getAllMembers(e);
    }

    default List<? extends AnnotationMirror> allAnnotationMirrors(TypeElement e) {
        return procEnv().getElementUtils().getAllAnnotationMirrors(e);
    }

    default boolean hides(Element hider, Element hidden) {
        return procEnv().getElementUtils().hides(hider, hidden);
    }

    default boolean overrides(ExecutableElement overrider, ExecutableElement overridden,
                              TypeElement type) {
        return procEnv().getElementUtils().overrides(overrider, overridden, type);
    }

    default Name name(CharSequence name) {
        return procEnv().getElementUtils().getName(name);
    }

    default boolean functionalInterface(TypeElement type) {
        return procEnv().getElementUtils().isFunctionalInterface(type);
    }

    default void print(Writer w, Element... elements) {
        procEnv().getElementUtils().printElements(w, elements);
    }

    default Map<? extends ExecutableElement, ? extends AnnotationValue> valuesWithDefaults(AnnotationMirror e) {
        return procEnv().getElementUtils().getElementValuesWithDefaults(e);
    }
    //endregion


    @SneakyThrows
    static <T extends Annotation> Values<T> values(T anno) {
        if (anno == null) return null;
        return new Values<>(anno);
    }

    static <T extends Annotation> Values<T> values(Element element, Class<T> type) {
        var anno = element.getAnnotation(type);
        if (anno == null) return null;
        return new Values<>(anno);
    }

    /**
     * @param element target
     * @param type    annotation
     * @return annotation exists on type/parameter/type-use
     */
    @SafeVarargs
    static boolean exists(Element element, Class<? extends Annotation>... type) {
        if (element.getKind() == ElementKind.METHOD || element.getKind() == ElementKind.CONSTRUCTOR) {
            var e = (ExecutableElement) element;
            var ps = e.getParameters();
            var rt = e.getReturnType();
            for (var a : type) {
                if (e.getAnnotationsByType(a).length > 0) return true;
                for (var p : ps) {
                    if (p.getAnnotationsByType(a).length > 0) return true;
                }
                if (rt.getAnnotationsByType(a).length > 0) return true;
            }
            return false;
        }
        for (var a : type) {
            if (element.getAnnotationsByType(a).length > 0) return true;
        }
        return false;
    }

    /**
     * @param element target
     * @param type    annotations
     * @return first founded annotation on target
     */
    @SafeVarargs
    static Annotation anyOf(Element element, Class<? extends Annotation>... type) {
        for (var aClass : type) {
            var v = element.getAnnotation(aClass);
            if (v != null) return v;
        }
        return null;
    }


    /**
     * @param def    default value
     * @param values alias values
     * @return found first value not equals to default,or returns default
     */
    @SafeVarargs
    static <T> T alias(T def, T... values) {
        for (var v : values) {
            if (!Objects.equals(v, def)) {
                return v;
            }
        }
        return def;
    }

    static String join(CharSequence sp, CharSequence prefix, Predicate<CharSequence> ignore, CharSequence... values) {
        var b = new StringJoiner(sp);
        if (prefix != null && prefix.length() != 0) b.add(prefix);
        for (var v : values) {
            if (!ignore.test(v)) {
                b.add(v);
            }
        }
        return b.toString();
    }
}
