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
import org.slf4j.helpers.MessageFormatter;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Logger helper for Annotation Processor
 *
 * @author Zen.Liu
 * @since 2023-02-25
 */
public
interface Logger {
    default void log(Diagnostic.Kind kind, String msg) {
        log(kind, msg, null, null, null);
    }

    default void log(Diagnostic.Kind kind, MessageInfo msg) {
        log(kind, msg.message(), msg.element(), msg.annotation(), msg.value());
    }

    void log(Diagnostic.Kind kind, String msg, Element element, AnnotationMirror a, AnnotationValue v);

    default void log(Diagnostic.Kind kind, String pattern, Object... args) {
        log(kind, formatter(pattern, args));
    }

    default void other(String msg) {
        log(Diagnostic.Kind.OTHER, msg);
    }

    default void other(MessageInfo msg) {
        log(Diagnostic.Kind.OTHER, msg);
    }

    default void other(String pattern, Object... args) {
        other(formatter(pattern, args));
    }

    default void note(String msg) {
        log(Diagnostic.Kind.NOTE, msg);
    }

    default void note(MessageInfo msg) {
        log(Diagnostic.Kind.NOTE, msg);
    }

    default void note(String pattern, Object... args) {
        note(formatter(pattern, args));
    }

    default void warn(String msg) {
        log(Diagnostic.Kind.WARNING, msg);
    }

    default void warn(MessageInfo msg) {
        log(Diagnostic.Kind.WARNING, msg);
    }

    default void warn(String pattern, Object... args) {
        warn(formatter(pattern, args));
    }

    default void mandatoryWarn(String msg) {
        log(Diagnostic.Kind.MANDATORY_WARNING, msg);
    }

    default void mandatoryWarn(MessageInfo msg) {
        log(Diagnostic.Kind.MANDATORY_WARNING, msg);
    }

    default void mandatoryWarn(String pattern, Object... args) {
        mandatoryWarn(formatter(pattern, args));
    }

    default void error(String msg) {
        log(Diagnostic.Kind.ERROR, msg);
    }

    default void error(MessageInfo msg) {
        log(Diagnostic.Kind.ERROR, msg);
    }

    default void error(String pattern, Object... args) {
        error(formatter(pattern, args));
    }

    /**
     * @return IllegalStateException
     */
    default RuntimeException fatal(String msg) {
        log(Diagnostic.Kind.ERROR, msg);
        return new IllegalStateException(msg);
    }

    default String fatal(MessageInfo msg) {
        log(Diagnostic.Kind.ERROR, msg);
        return msg.message();
    }

    /**
     * @return IllegalStateException
     */
    default String fatal(String pattern, Object... args) {
        return fatal(formatter(pattern, args));

    }

    /**
     * @param pattern default use SL4J pattern style
     * @param args    args
     * @return formatted message
     */
    default MessageInfo formatter(String pattern, Object... args) {
        return MessageInfo.format(pattern, args);
    }

    @Getter
    @Accessors(fluent = true)
    final class MessageInfo {
        private final String message;
        private final Throwable throwable;
        private final Element element;
        private final AnnotationMirror annotation;
        private final AnnotationValue value;

        public MessageInfo(String pattern, Object... args) {
            var t = MessageFormatter.arrayFormat(pattern, args);
            message = t.getMessage();
            throwable = t.getThrowable();
            Element ele = null;
            AnnotationMirror a = null;
            AnnotationValue av = null;
            var ar = t.getArgArray();
            for (Object arg : ar) {
                if (arg instanceof Element) ele = (Element) arg;
                else if (arg instanceof AnnotationMirror) a = (AnnotationMirror) arg;
                else if (arg instanceof AnnotationValue) av = (AnnotationValue) arg;
            }
            this.element = ele;
            this.annotation = a;
            this.value = av;
        }

        public MessageInfo(String pattern, Object arg) {
            var t = MessageFormatter.format(pattern, arg, arg);
            message = t.getMessage();
            throwable = t.getThrowable();
            Element ele = null;
            AnnotationMirror a = null;
            AnnotationValue av = null;
            if (throwable != null) {
                this.element = null;
                this.annotation = null;
                this.value = null;
                return;
            }
            if (arg instanceof Element) ele = (Element) arg;
            else if (arg instanceof AnnotationMirror) a = (AnnotationMirror) arg;
            else if (arg instanceof AnnotationValue) av = (AnnotationValue) arg;

            this.element = ele;
            this.annotation = a;
            this.value = av;
        }

        public MessageInfo(String pattern, Object arg1, Object arg2) {
            var t = MessageFormatter.format(pattern, arg1, arg2);
            message = t.getMessage();
            throwable = t.getThrowable();
            Element ele = null;
            AnnotationMirror a = null;
            AnnotationValue av = null;
            if (arg1 instanceof Element) ele = (Element) arg1;
            else if (arg1 instanceof AnnotationMirror) a = (AnnotationMirror) arg1;
            else if (arg1 instanceof AnnotationValue) av = (AnnotationValue) arg1;
            if (arg2 instanceof Element) ele = (Element) arg2;
            else if (arg2 instanceof AnnotationMirror) a = (AnnotationMirror) arg2;
            else if (arg2 instanceof AnnotationValue) av = (AnnotationValue) arg2;
            this.element = ele;
            this.annotation = a;
            this.value = av;
        }

        public static MessageInfo format(String pattern, Object arg1) {
            return new MessageInfo(pattern, arg1);
        }

        public static MessageInfo format(String pattern, Object arg1, Object arg2) {
            return new MessageInfo(pattern, arg1, arg2);
        }

        public static MessageInfo format(String pattern, Object... args) {
            return new MessageInfo(pattern, args);
        }
    }
}
