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

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * BaseGenerator for build Code Generate Annotation Processor
 *
 * @author Zen.Liu
 * @since 2023-02-25
 */
public abstract class BaseGenerator<T extends Annotation, E extends Element> extends AbstractProcessor implements Logger, Utils {
    @SuppressWarnings("unused")


    protected Filer filer;
    protected final Class<T> annotationType;
    protected final Class<E> elementType;
    protected final String suffix;

    public boolean disabled() {
        return disabled;
    }

    private boolean disabled;


    protected BaseGenerator(Class<T> annotationType, Class<E> elementType, String suffix) {
        this.annotationType = annotationType;
        this.elementType = elementType;
        this.suffix = suffix;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        disabled = processingEnv.getOptions().getOrDefault("disabled", "").contains(this.getClass().getSimpleName());
    }

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.singleton("disabled");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(annotationType.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (disabled) return false;
        var targets = roundEnv.getElementsAnnotatedWith(annotationType);
        if (targets.isEmpty()) return false;
        var skipNextProcessor = false;
        for (var ele : targets) {
            if (elementType.isInstance(ele)) {
                var type = elementType.cast(ele);
                var anno = type.getAnnotation(annotationType);
                if (!predicate(type, anno)) continue;
                skipNextProcessor = write(type.getSimpleName() + suffix, type, anno);
            }
        }
        return skipNextProcessor;
    }

    protected boolean predicate(E element, T anno) {
        return true;
    }

    protected abstract List<JavaFile> build(String pkg, E type, T anno);

    /**
     * @param name the Type Simple Name
     * @param type Element
     * @param anno Annotation
     * @return skip next processor
     */
    @SneakyThrows
    protected boolean write(String name, E type, T anno) {
        var pkg = packageOf(type).getQualifiedName().toString();
        if (pkg == null || pkg.trim().length() == 0) throw new IllegalStateException("package required");
        var result = build(pkg, type, anno);
        for (var javaFile : result) {
            javaFile.writeTo(filer);
        }
        return false;
    }


    //region Messager
    @Override
    public void log(Diagnostic.Kind kind, String msg, Element element, AnnotationMirror a, AnnotationValue v) {
        var m = processingEnv.getMessager();
        if (element != null)
            if (a != null)
                if (v != null)
                    m.printMessage(kind, msg, element, a, v);
                else
                    m.printMessage(kind, msg, element, a);
            else
                m.printMessage(kind, msg);
    }
    //endregion

    //region Units

    @Override
    public ProcessingEnvironment procEnv() {
        return processingEnv;
    }

    //endregion

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
