package com.example.timemanagement

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.timemanagement.bottomsheet.BottomSheetCallback
import com.example.timemanagement.bottomsheet.EditReminderBottomSheetFragment
import com.example.timemanagement.databinding.ActivityMainBinding
import com.example.timemanagement.datastore.DataStoreInstance
import com.example.timemanagement.retrofit.APP_ID
import com.example.timemanagement.retrofit.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import java.time.OffsetDateTime

class MainActivity : AppCompatActivity(), BottomSheetCallback {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    lateinit var reminderCommunicationInterface: ReminderCommunicationInterface

    interface ReminderCommunicationInterface{
        fun onReminderCreation(message: String, date: OffsetDateTime, type: Int, oneSignalMessageID: String)
        fun onReminderClear()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        //

        // Enable verbose logging for debugging (remove in production)
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        // Initialize with your OneSignal App ID
        OneSignal.initWithContext(this, APP_ID)
        // Use this method to prompt for push notifications.
        // We recommend removing this method after testing and instead use In-App Messages to prompt for notification permission.

        Log.d("OneSignal", "OneSignal User ID: ${OneSignal.User.onesignalId}")
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }
        //
        binding.buttonOptions.setOnClickListener { v ->
            openOptionsPopupMenu()
        }


        binding.fab.setOnClickListener { view ->
            val bottomSheet = EditReminderBottomSheetFragment()
            bottomSheet.show(supportFragmentManager, "EditReminderBottomSheet")
        }
    }

    private fun openOptionsPopupMenu(){
        var popupMenu = PopupMenu(this, binding.buttonOptions)

        popupMenu.menuInflater.inflate(R.menu.popup_menu_main, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            lifecycleScope.launch {
                DataStoreInstance.getInstance().clearList(this@MainActivity)
                reminderCommunicationInterface.onReminderClear()
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show();
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onConfirm(message: String, date: OffsetDateTime, type: Int) {
        ApiClient.sendNotification(message, date, object : ApiClient.OnResponseListener{
            override fun onResponseSuccess(id: String) {
                Log.d("MainActivity", "Message ID: $id")
                reminderCommunicationInterface.onReminderCreation(message, date, type, id)
            }
        })
    }
}