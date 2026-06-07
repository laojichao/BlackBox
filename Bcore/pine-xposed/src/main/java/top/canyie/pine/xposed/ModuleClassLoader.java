/*
 * Copyright (C) 2017 The Android Open Source Project
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
 * limitations under the License
 */

// Pine changed: Move package to top.canyie.pine.xposed
package top.canyie.pine.xposed;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import dalvik.system.PathClassLoader;

/**
 * Custom {@link ClassLoader} used to load Xposed module APKs.
 *
 * <p>This class loader overrides the default parent-delegation model to prioritize the module's
 * own classes. The lookup order is:
 * <ol>
 *   <li>Already-loaded classes (cache)</li>
 *   <li>Boot classpath (Android framework classes)</li>
 *   <li>The module's dex path (the module APK itself)</li>
 *   <li>The parent class loader</li>
 * </ol>
 *
 * <p>This ensures that a module can bundle its own version of a library without conflicting with
 * other modules or the host app. Non-delegating variants of {@code loadClass}, {@code getResource},
 * and {@code getResources} are also exposed for cases where the normal lookup order is not desired.
 *
 * <p>Adapted from Android's {@code DelegateLastClassLoader}, relocated to the Pine Xposed package.
 */
public class ModuleClassLoader extends PathClassLoader {
    /**
     * Creates a new module class loader for the given dex path.
     *
     * @param dexPath the list of dex/APK files to load classes from
     * @param parent the parent class loader for delegation
     */
    public ModuleClassLoader(String dexPath, ClassLoader parent) {
        super(dexPath, parent);
    }

    /**
     * Creates a new module class loader for the given dex path with a native library search path.
     *
     * @param dexPath the list of dex/APK files to load classes from
     * @param librarySearchPath the list of directories containing native libraries
     * @param parent the parent class loader for delegation
     */
    public ModuleClassLoader(String dexPath, String librarySearchPath, ClassLoader parent) {
        super(dexPath, librarySearchPath, parent);
    }

    /**
     * Loads a class using the module-first delegation order: cache, boot classpath, own dex path,
     * then parent.
     *
     * @param name the fully qualified class name
     * @param resolve whether to resolve the class
     * @return the resolved class
     * @throws ClassNotFoundException if the class was not found in any source
     */
    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // First, check whether the class has already been loaded. Return it if that's the case.
        Class<?> cl = findLoadedClass(name);
        if (cl != null) {
            return cl;
        }

        // Next, check whether the class in question is present in the boot classpath.
        try {
            return Object.class.getClassLoader().loadClass(name);
        } catch (ClassNotFoundException ignored) {
        }

        // Next, check whether the class in question is present in the dexPath that this classloader
        // operates on, or its shared libraries.
        ClassNotFoundException fromSuper;
        try {
            return findClass(name);
        } catch (ClassNotFoundException ex) {
            fromSuper = ex;
        }

        // Finally, check whether the class in question is present in the parent classloader.
        try {
            return getParent().loadClass(name);
        } catch (ClassNotFoundException cnfe) {
            // The exception we're catching here is the CNFE thrown by the parent of this
            // classloader. However, we would like to throw a CNFE that provides details about
            // the class path / list of dex files associated with *this* classloader, so we choose
            // to throw the exception thrown from that lookup.
            throw fromSuper;
        }
    }

    /**
     * Loads a class using the standard parent-first delegation order, bypassing the module-first
     * lookup. This delegates directly to {@code super.loadClass}.
     *
     * @param name the fully qualified class name
     * @param resolve whether to resolve the class
     * @return the resolved class
     * @throws ClassNotFoundException if the class was not found
     */
    // Pine added: loadClassNoDelegate
    public Class<?> loadClassNoDelegate(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    /**
     * Finds a class in this class loader's own dex path without checking the parent.
     *
     * @param name the fully qualified class name
     * @return the found class
     * @throws ClassNotFoundException if the class is not in this loader's dex path
     */
    // Pine added: public findClass
    @Override public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    /**
     * Finds a resource using the module-first delegation order: boot classpath, own dex path,
     * then parent.
     *
     * @param name the resource name
     * @return the resource URL, or {@code null} if not found
     */
    @Override
    public URL getResource(String name) {
        // The lookup order we use here is the same as for classes.

        URL resource = Object.class.getClassLoader().getResource(name);
        if (resource != null) {
            return resource;
        }

        resource = findResource(name);
        if (resource != null) {
            return resource;
        }

        final ClassLoader cl = getParent();
        return (cl == null) ? null : cl.getResource(name);
    }

    /**
     * Finds a resource in this class loader's own dex path without delegating to the parent.
     *
     * @param name the resource name
     * @return the resource URL, or {@code null} if not found
     */
    // Pine added: getResourceNoDelegate
    public URL getResourceNoDelegate(String name) {
        return super.getResource(name);
    }

    /**
     * Finds a resource in this class loader's own dex path.
     *
     * @param name the resource name
     * @return the resource URL, or {@code null} if not found
     */
    // Pine added: public findResource
    @Override public URL findResource(String name) {
        return super.findResource(name);
    }

    /**
     * Returns an enumeration of resources using the module-first delegation order:
     * boot classpath, own dex path, then parent. Uses {@link CompoundEnumeration} to merge results.
     *
     * @param name the resource name
     * @return an enumeration of matching resource URLs
     * @throws IOException if an I/O error occurs
     */
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        @SuppressWarnings("unchecked")
        final Enumeration<URL>[] resources = (Enumeration<URL>[]) new Enumeration<?>[] {
                Object.class.getClassLoader().getResources(name),
                findResources(name),
                (getParent() == null)
                        ? null : getParent().getResources(name) };

        return new CompoundEnumeration<>(resources);
    }

    /**
     * Returns an enumeration of resources from this class loader's own dex path without delegating.
     *
     * @param name the resource name
     * @return an enumeration of matching resource URLs
     * @throws IOException if an I/O error occurs
     */
    // Pine added: getResourcesNoDelegate
    public Enumeration<URL> getResourcesNoDelegate(String name) throws IOException {
        return super.getResources(name);
    }
}
