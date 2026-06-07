package de.robv.android.xposed;

/**
 * Marker interface that all Xposed module hook interfaces must extend.
 *
 * <p>This interface cannot be implemented directly. Instead, module developers should implement
 * one or more of its sub-interfaces:
 * <ul>
 *   <li>{@link IXposedHookLoadPackage} - to hook methods when an app is loaded</li>
 *   <li>{@link IXposedHookZygoteInit} - to hook methods during Zygote initialization</li>
 *   <li>{@link IXposedHookInitPackageResources} - to replace resources for an app</li>
 * </ul>
 *
 * <p>The Xposed framework uses this interface to identify module entry points listed in
 * {@code assets/xposed_init}.
 */
/* package */ public interface IXposedMod {}
