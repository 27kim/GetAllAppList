package io.lab27.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_app_select.*

class AppSelectActivity : AppCompatActivity() {

    lateinit var appList: List<PackageInfo>
    val pm: PackageManager by lazy {
        packageManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.scale_fade_in, R.anim.no_anim)
        setContentView(R.layout.activity_app_select)

        appList = pm.getInstalledPackages(0)
            .filter {
                it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
            .sortedBy {
                it.applicationInfo.loadLabel(pm).toString()
            }

        app_list_recyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = AppListAdapter(appList)
        }
        var dp = resources.displayMetrics.density
        app_list_recyclerview.animate().translationX(-40 * dp).setDuration(1000L)
            .setInterpolator(OvershootInterpolator()).start()

        Handler().postDelayed({app_list_recyclerview.animate().translationX(0f).setDuration(1000L)
            .setInterpolator(OvershootInterpolator()).start()},1000L)
    }

    inner class AppListViewHolder(val item: View) : RecyclerView.ViewHolder(item) {
        lateinit var appInfo: PackageInfo
        private val image = item.findViewById<ImageView>(R.id.app_iv_image)
        private val title = item.findViewById<TextView>(R.id.app_tv_title)

        fun onBind(applicationInfo: PackageInfo) {
            appInfo = applicationInfo
            image.setImageDrawable(appInfo.applicationInfo.loadIcon(pm))
            title.text = applicationInfo.applicationInfo.loadLabel(pm)
            item.setOnClickListener {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    inner class AppListAdapter(val list: List<PackageInfo>) :
        RecyclerView.Adapter<AppListViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppListViewHolder {
            val view = LayoutInflater.from(applicationContext)
                .inflate(R.layout.app_list_item, parent, false)
            return AppListViewHolder(view)
        }

        override fun getItemCount(): Int {
            return appList.size
        }

        override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
            holder.onBind(list[position])
        }
    }

    override fun onDestroy() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        super.onDestroy()
    }

    companion object {
        const val RUN_AFTER_JARVIS = "RUN_AFTER_JARVIS"
        const val APP_SELECT_ACTIVITY: Int = 220
        fun newInstance(context: Context): Intent {
            return Intent(context, AppSelectActivity::class.java)
        }
    }


}
