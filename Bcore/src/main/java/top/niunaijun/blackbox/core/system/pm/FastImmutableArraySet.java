/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.niunaijun.blackbox.core.system.pm;

import java.util.AbstractSet;
import java.util.Iterator;

/**
 * A fast immutable set wrapper backed by an array, optimized for non-concurrent iteration.
 * <p>
 * Used within the virtual package management intent resolution pipeline to efficiently
 * iterate over intent categories. Reuses a single iterator instance to minimize garbage
 * collection overhead, achieving approximately 2.5x faster iteration compared to {@link HashSet}.
 *
 * @param <T> the type of elements in this set
 * @hide
 */
public final class FastImmutableArraySet<T> extends AbstractSet<T> {
    FastIterator<T> mIterator;
    T[] mContents;

    /**
     * Creates a new immutable set backed by the given array.
     *
     * @param contents the array of elements to wrap; must not be modified after construction
     */
    public FastImmutableArraySet(T[] contents) {
        mContents = contents;
    }

    /**
     * Returns an iterator over the elements in this set.
     * <p>
     * Reuses the same iterator instance on subsequent calls for performance.
     * Not safe for concurrent iteration.
     *
     * @return a reusable iterator over the set elements
     */
    @Override
    public Iterator<T> iterator() {
        FastIterator<T> it = mIterator;
        if (it == null) {
            it = new FastIterator<T>(mContents);
            mIterator = it;
        } else {
            it.mIndex = 0;
        }
        return it;
    }

    /**
     * Returns the number of elements in this set.
     *
     * @return the size of the backing array
     */
    @Override
    public int size() {
        return mContents.length;
    }

    private static final class FastIterator<T> implements Iterator<T> {
        private final T[] mContents;
        int mIndex;

        public FastIterator(T[] contents) {
            mContents = contents;
        }

        @Override
        public boolean hasNext() {
            return mIndex != mContents.length;
        }

        @Override
        public T next() {
            return mContents[mIndex++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
