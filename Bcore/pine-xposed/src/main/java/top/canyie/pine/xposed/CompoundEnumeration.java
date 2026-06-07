/*
 * Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

// Pine changed: Move package to top.canyie.pine.xposed
package top.canyie.pine.xposed;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * A utility class that concatenates multiple {@link Enumeration} instances into a single
 * enumeration. Elements from the first enumeration are returned before those from the second, and
 * so on. Null enumerations in the array are silently skipped.
 *
 * <p>This class is adapted from the OpenJDK source and relocated to the Pine Xposed package
 * for use by {@link ModuleClassLoader#getResources}.
 *
 * @param <T> the type of elements returned by this enumeration
 */
public class CompoundEnumeration<E> implements Enumeration<E> {
    private Enumeration<E>[] enums;
    private int index = 0;

    /**
     * Creates a new compound enumeration over the given array of enumerations.
     *
     * @param enums the array of enumerations to iterate over; may contain {@code null} entries
     */
    public CompoundEnumeration(Enumeration<E>[] enums) {
        this.enums = enums;
    }

    private boolean next() {
        while (index < enums.length) {
            if (enums[index] != null && enums[index].hasMoreElements()) {
                return true;
            }
            index++;
        }
        return false;
    }

    /**
     * Returns {@code true} if any of the contained enumerations have more elements.
     *
     * @return {@code true} if more elements are available
     */
    public boolean hasMoreElements() {
        return next();
    }

    /**
     * Returns the next element from the compound enumeration.
     *
     * @return the next element
     * @throws NoSuchElementException if no more elements are available
     */
    public E nextElement() {
        if (!next()) {
            throw new NoSuchElementException();
        }
        return enums[index].nextElement();
    }
}
