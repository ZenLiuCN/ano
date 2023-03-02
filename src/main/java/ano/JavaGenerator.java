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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import lombok.SneakyThrows;
import lombok.var;
import ref.Ref;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.List;

/**
 * Processor Generator for build a Java Code Generate Annotation Processor
 *
 * @author Zen.Liu
 * @since 2023-02-25
 */
public abstract class JavaGenerator<T extends Annotation, E extends Element> extends BaseProcessor<T, E> {
    protected final String suffix;

    protected JavaGenerator(Class<T> annotationType, Class<E> elementType, String suffix) {
        super(annotationType, elementType);
        this.suffix = suffix;
    }


    /**
     * implement this method to generate sources for type
     * @param pkg current package
     * @param type element
     * @param anno annotation
     * @return group of JavaFiles
     */
    protected abstract List<JavaFile> build(String pkg, E type, T anno);

    /**
     * @param type Element
     * @param anno Annotation
     * @return skip next processor
     */
    @SneakyThrows
    protected boolean proc(E type, T anno) {
        var pkg = packageOf(type).getQualifiedName().toString();
        if (pkg == null || pkg.trim().length() == 0) throw new IllegalStateException("package required");
        var result = build(pkg, type, anno);
        for (var javaFile : result) {
            javaFile.writeTo(filer());
        }
        return false;
    }

    /**
     * @param spec the type to add Generated annotation
     * @return type spec added generated annotation
     */
    public TypeSpec.Builder generatedBy(TypeSpec.Builder spec) {
        spec.addJavadoc("Generated Source should not modified!!");
        if (Ref.$.version >= 9) {
            spec.addAnnotation(AnnotationSpec.builder(ClassName.get("javax.annotation.processing", "Generated"))
                    .addMember("value", "$S", this.getClass().getCanonicalName())
                    .addMember("date", "$S", Instant.now().toString())
                    .build());

        } else if (Ref.$.version >= 6) {
            spec.addAnnotation(AnnotationSpec.builder(ClassName.get("javax.annotation", "Generated"))
                    .addMember("value", "$S", this.getClass().getCanonicalName())
                    .addMember("date", "$S", Instant.now().toString())
                    .build());
        }
        return spec;
    }


}
