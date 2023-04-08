package layout

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adelvanchik.server.IHashSearchService
import java.util.regex.Pattern

class MainViewModel(application: Application): AndroidViewModel(application) {

    private var hashSearchService: IHashSearchService? = null
    private lateinit var app: Application
    private var isServiceWork = false

    private val _inputText = MutableLiveData<String>()
    val inputText: LiveData<String>
    get() = _inputText

    private val _hashAdded = MutableLiveData<Boolean>()
    val hashAdded: LiveData<Boolean>
        get() = _hashAdded

    private val _incorrectHashFormat = MutableLiveData<Unit>()
    val incorrectHashFormat: LiveData<Unit>
        get() = _incorrectHashFormat

    private val _serviceNotWork = MutableLiveData<Unit>()
    val serviceNotWork: LiveData<Unit>
        get() = _serviceNotWork

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isServiceWork = true
            hashSearchService = IHashSearchService.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            hashSearchService = null
            isServiceWork = false
        }
    }

    init {
        app = application
        startService()
    }

    fun searchHash() {
        inputText.value.let {
            if (checkFormatHash()) {
                if (isServiceWork) {
                    val isHashFound = hashSearchService!!.searchHash(inputText.value)
                    _hashAdded.value = isHashFound
                } else _serviceNotWork.value = Unit
            } else {
                _incorrectHashFormat.value = Unit
            }
        }
    }

    private fun startService() {
        app.bindService(
            newIntentWithHashSearchService(),
            serviceConnection,
            AppCompatActivity.BIND_AUTO_CREATE)
    }

    private fun checkFormatHash(): Boolean {
        val searchHash = inputText.value
        val pattern = Pattern.compile("[a-fA-F0-9]{32}")
        val matcher = pattern.matcher(searchHash.toString())
        return matcher.matches()
    }

    override fun onCleared() {
        super.onCleared()
        if (isServiceWork) app.unbindService(serviceConnection)
    }

    companion object {
        private fun newIntentWithHashSearchService()  = Intent("HashSearchService")
                .setPackage("com.adelvanchik.server")
    }
}