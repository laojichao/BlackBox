# BlackBox 项目指南

## 项目描述

BlackBox 是一款 Android 虚拟引擎，可在 Android 设备上克隆并运行虚拟应用，支持免安装运行。核心能力包括：应用多开、Xposed 模块注入、虚拟定位、系统服务代理。基于 VirtualApp 架构演进而来，使用 Pine 作为 Hook 框架。

- 许可证：Apache License 2.0
- 支持系统：Android 5.0 ~ 12.0（minSdk 21，targetSdk 30）
- 语言：Java（主）、Kotlin（app 层）、C++（Native 层）

## 技术栈

| 组件 | 版本/说明 |
|------|----------|
| Gradle Plugin | 4.0.1 |
| Kotlin | 1.4.31 |
| compileSdkVersion | 30（app）、28（Bcore） |
| NDK | 21.0.6113669 |
| FreeReflection | 3.0.1（绕过 hidden API 限制） |
| BlackReflection | 1.1.2（反射辅助框架） |
| Pine | 内嵌（ART 方法 Hook 框架） |
| ViewBinding | 启用 |
| Coroutines | 1.4.2 |

## 项目结构

```
BlackBox/
├── app/                        # 宿主应用 UI 层（Kotlin）
│   └── src/main/java/top/niunaijun/blackboxa/
│       ├── app/                # Application 初始化
│       ├── view/               # UI 界面（主界面、列表、设置、XP管理、虚拟定位）
│       ├── bean/               # 数据模型
│       ├── biz/                # 业务逻辑
│       ├── data/               # 数据层
│       └── widget/             # 自定义控件
│
├── Bcore/                      # 核心引擎模块（Java + C++）
│   ├── src/main/java/top/niunaijun/blackbox/
│   │   ├── BlackBoxCore.java   # 全局单例入口，引擎生命周期管理
│   │   ├── app/                # 虚拟应用线程管理
│   │   │   ├── BActivityThread.java  # 虚拟应用主线程，Hook 注入入口
│   │   │   └── LauncherActivity.java # 虚拟应用启动 Activity
│   │   ├── core/               # 核心子系统
│   │   │   ├── NativeCore.java       # JNI 桥接层（libblackbox.so）
│   │   │   ├── IOCore.java           # I/O 路径重定向引擎
│   │   │   ├── GmsCore.java          # Google 服务支持
│   │   │   ├── env/                  # 环境配置（BEnvironment、VirtualRuntime）
│   │   │   └── system/               # 虚拟系统服务
│   │   │       ├── am/               # ActivityManager 虚拟化
│   │   │       ├── pm/               # PackageManager 虚拟化
│   │   │       ├── location/         # 虚拟定位服务
│   │   │       ├── notification/     # 通知虚拟化
│   │   │       ├── accounts/         # 账户管理虚拟化
│   │   │       ├── os/               # 存储管理虚拟化
│   │   │       └── user/             # 多用户管理
│   │   ├── fake/               # Hook 代理层
│   │   │   ├── hook/           # Hook 管理器与注入框架
│   │   │   ├── delegate/       # Instrumentation 代理
│   │   │   ├── frameworks/     # 虚拟 Manager 代理（BActivityManager 等）
│   │   │   ├── service/        # 系统服务 Proxy（40+ 个服务代理）
│   │   │   └── provider/       # ContentProvider 代理
│   │   ├── proxy/              # 代理组件声明
│   │   │   ├── ProxyActivity.java        # 代理 Activity（P0-P49）
│   │   │   ├── ProxyService.java         # 代理 Service
│   │   │   ├── ProxyBroadcastReceiver.java
│   │   │   └── ProxyContentProvider.java
│   │   └── utils/              # 工具类
│   ├── src/main/cpp/           # Native 层
│   │   ├── BoxCore.cpp         # JNI 入口
│   │   ├── IO.cpp              # 文件系统重定向
│   │   ├── Hook/               # Native Hook 实现
│   │   │   ├── BinderHook      # Binder 通信 Hook
│   │   │   ├── RuntimeHook     # ART Runtime Hook
│   │   │   ├── VMClassLoaderHook # 类加载 Hook
│   │   │   └── UnixFileSystemHook # 文件系统 Hook
│   │   ├── JniHook/            # JNI 方法 Hook
│   │   └── Utils/              # Native 工具（fake_dlfcn 等）
│   ├── black-fake/             # 框架类伪造模块（ActivityThread 等）
│   ├── black-hook/             # JNI Hook 工具库
│   ├── pine-core/              # Pine ART Hook 框架核心
│   ├── pine-xposed/            # Xposed 兼容层（XposedBridge 等）
│   └── pine-xposed-res/        # Xposed 资源
│
├── android-mirror/             # Android 内部 API 镜像层
│   └── src/main/java/black/
│       ├── android/            # android.* 包镜像（app、os、content、view 等）
│       ├── com/                # com.android.* 包镜像
│       ├── dalvik/             # dalvik.* 包镜像
│       ├── java/               # java.* 包镜像
│       └── libcore/            # libcore.* 包镜像
│
└── assets/                     # 文档资源
```

## 构建说明

### 环境要求

- Android Studio 4.0+
- NDK 21.0.6113669
- JDK 1.8
- Android SDK（API 30）

### 构建命令

```bash
# 构建 32 位版本
./gradlew assembleBlackBox32Debug
./gradlew assembleBlackBox32Release

# 构建 64 位版本
./gradlew assembleBlackBox64Debug
./gradlew assembleBlackBox64Release

# 构建 Beta 版本
./gradlew assembleBlackBox32BetaDebug
./gradlew assembleBlackBox64BetaRelease
```

### 产品风味（Product Flavors）

| Flavor | applicationId | 架构 |
|--------|--------------|------|
| BlackBox32 | top.niunaijun.blackboxa32 | armeabi-v7a |
| BlackBox64 | top.niunaijun.blackboxa64 | arm64-v8a |
| BlackBox32Beta | top.niunaijun.blackboxa32_beta | armeabi-v7a |
| BlackBox64Beta | top.niunaijun.blackboxa64_beta | arm64-v8a |

注意：32 位和 64 位是独立的 app，目标应用的 native 库架构决定了应使用哪个版本。

## 虚拟引擎/沙箱原理

### 整体架构

BlackBox 采用宿主应用（Host）+ 虚拟引擎（Bcore）的架构，在宿主进程内创建一个完整的虚拟 Android 环境。虚拟应用运行在宿主进程中，通过 Hook 和代理机制拦截所有系统调用。

### 核心机制

#### 1. 进程模型

BlackBoxCore 启动时识别当前进程类型：
- 主进程（Main）：宿主 UI 进程
- 服务进程（Server）：虚拟系统服务进程
- 客户端进程（Client）：虚拟应用运行进程

每个虚拟应用运行在独立的客户端进程中，通过 `BActivityThread` 管理生命周期。

#### 2. 系统服务代理（Service Proxy）

`HookManager` 注册 40+ 个系统服务代理，通过 Binder Hook 拦截虚拟应用对系统服务的调用：

- `IActivityManagerProxy` / `IActivityTaskManagerProxy`：Activity 生命周期代理
- `IPackageManagerProxy`：包管理代理
- `ITelephonyManagerProxy`：设备信息伪装
- `ILocationManagerProxy`：虚拟定位
- `IAlarmManagerProxy`：闹钟服务代理
- `IWindowManagerProxy`：窗口管理代理
- 等等...

代理实现位于 `Bcore/src/main/java/top/niunaijun/blackbox/fake/service/`。

#### 3. I/O 路径重定向

`IOCore` 使用 Trie 树实现高效的文件路径前缀匹配和重定向：
- 将 `/data/data/<target_pkg>/` 重定向到 `/data/data/<host_pkg>/cache/blackbox/<userId>/<target_pkg>/`
- Native 层（`IO.cpp`、`UnixFileSystemHook`）拦截 libc 文件操作
- 支持白名单路径（如 Pictures）保持原路径
- 可隐藏 Root 相关路径

虚拟根目录：`/data/data/<hostPkg>/cache/blackbox/`

#### 4. Instrumentation 代理

`AppInstrumentation` 替换宿主的 `Instrumentation`，拦截：
- `newActivity()`：创建虚拟应用的 Activity
- `newApplication()`：创建虚拟应用的 Application
- `callApplicationOnCreate()`：触发虚拟应用的 onCreate

#### 5. 代理组件（Proxy Components）

宿主 AndroidManifest 中声明大量代理组件（ProxyActivity P0-P49、ProxyService 等），作为虚拟应用组件的跳板。系统看到的是宿主的代理组件，实际执行的是虚拟应用的真实组件。

#### 6. Xposed 模块支持

- 使用 Pine 框架（ART 级别的方法 Hook）替代传统 Xposed
- `BXposedManagerService` 管理模块安装和启用状态
- `pine-xposed` 模块提供完整的 XposedBridge API 兼容
- Xposed 框架经过隐藏处理，可绕过 Xposed Checker 和 XposedDetector 检测

#### 7. android-mirror 镜像层

`android-mirror` 模块通过 BlackReflection 框架提供对 Android 内部 API 的反射访问，以 `black.android.*`、`black.dalvik.*` 等包名镜像系统隐藏类，使引擎能访问 `ActivityThread`、`ContextImpl`、`LoadedApk` 等内部类的私有成员。

### Native 层（libblackbox.so）

Native 层提供底层 Hook 和 I/O 拦截：
- `BoxCore.cpp`：JNI 入口，初始化 native 引擎
- `IO.cpp`：文件系统路径重定向
- `Hook/BinderHook.cpp`：Binder 通信拦截
- `Hook/RuntimeHook.cpp`：ART Runtime 拦截
- `Hook/VMClassLoaderHook.cpp`：类加载拦截
- `Hook/UnixFileSystemHook.cpp`：POSIX 文件操作拦截
- `JniHook/`：JNI 方法级别的 Hook

## 关键类说明

| 类 | 路径 | 职责 |
|----|------|------|
| `BlackBoxCore` | Bcore/.../BlackBoxCore.java | 全局单例，引擎生命周期、应用安装/启动/卸载、多用户管理 |
| `BActivityThread` | Bcore/.../app/BActivityThread.java | 虚拟应用主线程，注入所有 Hook，管理虚拟应用生命周期 |
| `NativeCore` | Bcore/.../core/NativeCore.java | JNI 桥接，I/O 重定向、UID 重映射、Xposed 隐藏 |
| `IOCore` | Bcore/.../core/IOCore.java | Java 层 I/O 路径重定向引擎 |
| `HookManager` | Bcore/.../fake/hook/HookManager.java | 注册和管理所有系统服务 Hook |
| `AppInstrumentation` | Bcore/.../fake/delegate/AppInstrumentation.java | 拦截 Activity/Application 创建 |
| `BActivityManagerService` | Bcore/.../core/system/am/BActivityManagerService.java | 虚拟 Activity 管理服务 |
| `BPackageManagerService` | Bcore/.../core/system/pm/BPackageManagerService.java | 虚拟包管理服务 |
| `BXposedManagerService` | Bcore/.../core/system/pm/BXposedManagerService.java | Xposed 模块管理 |
| `BEnvironment` | Bcore/.../core/env/BEnvironment.java | 虚拟环境目录结构定义 |
| `ProxyActivity` | Bcore/.../proxy/ProxyActivity.java | 代理 Activity（P0-P49 用于并发） |
| `FakeCore` | Bcore/black-fake/.../FakeCore.java | 初始化框架类伪造 |

## API 使用

```java
// 初始化（在 Application 中）
BlackBoxCore.get().doAttachBaseContext(base, new ClientConfiguration() {
    @Override
    public String getHostPackageName() {
        return base.getPackageName();
    }
});
BlackBoxCore.get().doCreate();

// 安装应用
BlackBoxCore.get().installPackageAsUser("com.example.app", userId);
BlackBoxCore.get().installPackageAsUser(new File("/sdcard/app.apk"), userId);

// 启动应用
BlackBoxCore.get().launchApk("com.example.app", userId);

// 获取已安装应用
BlackBoxCore.get().getInstalledApplications(flags, userId);
BlackBoxCore.get().getInstalledPackages(flags, userId);

// 多用户
List<BUserInfo> users = BlackBoxCore.get().getUsers();
```

## 逆向分析要点

### Xposed 检测绕过

BlackBox 内置 Xposed 隐藏机制，以下检测手段均无法检出：
- Xposed Checker
- XposedDetector（vvb2060）
- Native 层通过 `NativeCore` 隐藏 Xposed 相关文件和堆栈

### 虚拟环境检测

应用可通过以下方式检测是否运行在 BlackBox 中：
- 检查 `/data/data/` 下的 blackbox 目录结构
- 检查 Binder 代理特征
- 检查 Instrumentation 是否被替换
- 检查 `/proc/self/maps` 中的 libblackbox.so

### 关键 Hook 点

逆向分析时需关注：
1. `HookManager.initHook()` - 所有系统服务 Hook 的注册入口
2. `BActivityThread.handleBindApplication()` - 虚拟应用启动流程
3. `IOCore.addIORule()` - I/O 重定向规则
4. `NativeCore.init()` - Native 层初始化
5. `AppInstrumentation.injectHook()` - Instrumentation 替换

### 常见修改场景

- 添加虚拟定位：修改 `BLocationManagerService` 和 `ILocationManagerProxy`
- 添加设备信息伪装：修改 `ITelephonyManagerProxy`、`IDeviceIdentifiersPolicyProxy`
- 添加 Root 隐藏：修改 `IOCore` 中的路径黑名单
- 添加新的系统服务代理：在 `fake/service/` 下新建 Proxy 类，在 `HookManager` 中注册
