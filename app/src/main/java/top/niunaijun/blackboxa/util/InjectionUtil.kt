package top.niunaijun.blackboxa.util

import top.niunaijun.blackboxa.data.AppsRepository
import top.niunaijun.blackboxa.data.FakeLocationRepository
import top.niunaijun.blackboxa.data.GmsRepository
import top.niunaijun.blackboxa.data.XpRepository
import top.niunaijun.blackboxa.view.apps.AppsFactory
import top.niunaijun.blackboxa.view.fake.FakeLocationFactory
import top.niunaijun.blackboxa.view.gms.GmsFactory
import top.niunaijun.blackboxa.view.list.ListFactory
import top.niunaijun.blackboxa.view.xp.XpFactory

/**
 * Simple service locator that provides pre-configured ViewModelFactory instances.
 *
 * Holds singleton instances of each repository and creates corresponding
 * ViewModel factories on demand. This replaces a full dependency injection
 * framework for this application.
 */
object InjectionUtil {

    private val appsRepository = AppsRepository()

    private val xpRepository = XpRepository()

    private val gmsRepository = GmsRepository()

    private val fakeLocationRepository = FakeLocationRepository()

    /**
     * Creates a new [AppsFactory] backed by the shared [AppsRepository].
     *
     * @return A new [AppsFactory] instance.
     */
    fun getAppsFactory() : AppsFactory {
        return AppsFactory(appsRepository)
    }

    /**
     * Creates a new [ListFactory] backed by the shared [AppsRepository].
     *
     * @return A new [ListFactory] instance.
     */
    fun getListFactory(): ListFactory {
        return ListFactory(appsRepository)
    }

    /**
     * Creates a new [XpFactory] backed by the shared [XpRepository].
     *
     * @return A new [XpFactory] instance.
     */
    fun getXpFactory():XpFactory{
        return XpFactory(xpRepository)
    }

    /**
     * Creates a new [GmsFactory] backed by the shared [GmsRepository].
     *
     * @return A new [GmsFactory] instance.
     */
    fun getGmsFactory():GmsFactory{
        return GmsFactory(gmsRepository)
    }

    /**
     * Creates a new [FakeLocationFactory] backed by the shared [FakeLocationRepository].
     *
     * @return A new [FakeLocationFactory] instance.
     */
    fun getFakeLocationFactory():FakeLocationFactory{
        return FakeLocationFactory(fakeLocationRepository)
    }
}