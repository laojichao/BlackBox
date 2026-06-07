package top.niunaijun.blackboxa.view.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cbfg.rvadapter.RVAdapter
import com.ferfalk.simplesearchview.SimpleSearchView
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.InstalledAppBean
import top.niunaijun.blackboxa.databinding.ActivityListBinding
import top.niunaijun.blackboxa.util.InjectionUtil
import top.niunaijun.blackboxa.util.inflate
import top.niunaijun.blackboxa.view.base.BaseActivity


/**
 * Activity that displays a searchable list of installed applications or Xposed modules.
 *
 * Allows users to select an app from the device's installed applications list.
 * Supports filtering by name or package name via a search view, and also allows
 * choosing an APK file from the device storage. Returns the selected package name
 * or APK URI as the activity result.
 */
class ListActivity : BaseActivity() {

    private val viewBinding: ActivityListBinding by inflate()

    private lateinit var mAdapter: RVAdapter<InstalledAppBean>

    private lateinit var viewModel: ListViewModel

    private var appList: List<InstalledAppBean> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        initToolbar(viewBinding.toolbarLayout.toolbar, R.string.installed_app, true)

        mAdapter = RVAdapter<InstalledAppBean>(this,ListAdapter()).bind(viewBinding.recyclerView).setItemClickListener { _, item, _ ->
            finishWithResult(item.packageName)
        }

        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)


        initSearchView()
        initViewModel()
    }

    /**
     * Sets up the search view to filter the app list by name or package name as the user types.
     */
    private fun initSearchView() {
        viewBinding.searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                filterApp(newText)
                return true
            }

            override fun onQueryTextCleared(): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

        })
    }

    /**
     * Initializes the ViewModel and loads either installed modules or installed apps
     * based on the "onlyShowXp" intent extra. Observes LiveData for loading state and app list.
     */
    private fun initViewModel() {
        viewModel = ViewModelProvider(this, InjectionUtil.getListFactory()).get(ListViewModel::class.java)
        val onlyShowXp = intent.getBooleanExtra("onlyShowXp", false)
        val userID = intent.getIntExtra("userID",0)

        if (onlyShowXp) {
            viewModel.getInstalledModules()
            viewBinding.toolbarLayout.toolbar.setTitle(R.string.installed_module)
        } else {
            viewModel.getInstallAppList(userID)
            viewBinding.toolbarLayout.toolbar.setTitle(R.string.installed_app)
        }

        viewModel.loadingLiveData.observe(this) {
            if (it) {
                viewBinding.stateView.showLoading()
            } else {
                viewBinding.stateView.showContent()

            }
        }

        viewModel.appsLiveData.observe(this) {
            if (it != null) {
                this.appList = it
                viewBinding.searchView.setQuery("", false)
                filterApp("")
                if (it.isNotEmpty()) {
                    viewBinding.stateView.showContent()
                    viewModel.previewInstalledList()
                } else {
                    viewBinding.stateView.showEmpty()
                }
            }
        }
    }

    /**
     * Filters the displayed app list by matching the query against app names and package names.
     *
     * @param newText the search query string to filter by.
     */
    private fun filterApp(newText: String) {
        val newList = this.appList.filter {
            it.name.contains(newText, true) or it.packageName.contains(newText, true)
        }
        mAdapter.setItems(newList)
    }

    private val openDocumentedResult = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.run {
            finishWithResult(it.toString())
        }
    }

    /**
     * Sets the activity result with the selected source (package name or URI) and finishes.
     * Hides the soft keyboard before finishing.
     *
     * @param source the selected package name or APK file URI to return to the caller.
     */
    private fun finishWithResult(source: String) {
        intent.putExtra("source", source)
        setResult(Activity.RESULT_OK, intent)
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        window.peekDecorView()?.run {
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
        finish()
    }


    override fun onBackPressed() {
        if (viewBinding.searchView.isSearchOpen) {
            viewBinding.searchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.list_choose) {
            openDocumentedResult.launch("application/vnd.android.package-archive")
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
        val item = menu!!.findItem(R.id.list_search)
        viewBinding.searchView.setMenuItem(item)

        return true
    }

    override fun onStop() {
        super.onStop()
        viewModel.loadingLiveData.postValue(true)
        viewModel.loadingLiveData.removeObservers(this)
        viewModel.appsLiveData.postValue(null)
        viewModel.appsLiveData.removeObservers(this)
    }


    companion object{
        /**
         * Starts [ListActivity] from the given context.
         *
         * @param context the context used to launch the activity.
         * @param onlyShowXp if true, displays only installed Xposed modules; if false, shows all installed apps.
         */
        fun start(context: Context,onlyShowXp:Boolean){
            val intent = Intent(context,ListActivity::class.java)
            intent.putExtra("onlyShowXp",onlyShowXp)
            context.startActivity(intent)
        }
    }
}