package com.nbsp.materialfilepicker.sample

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.media.MediaPlayer
import android.os.*
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import java.io.File
import java.io.FileWriter
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    private var resultStr = ""
    private var firstStr = ""
    private var lastStr = ""
    private val listNames = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()

        val string: String?
        try {
            val inputStream: InputStream = assets.open("alfabet.txt")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            string = String(buffer)
            val lines: List<String> = string.split(System.getProperty("line.separator"))
            for (line in lines) {
                val l = line.replace("\n", "")
                if (l.isNotEmpty())
                    listNames.add(l.toLowerCase())
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        last.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                this.currentFocus?.let { view ->
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }
                runSearch()
                return@OnKeyListener false
            }
            false
        })

        first.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                this.currentFocus?.let { view ->
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }
                runSearch()
                return@OnKeyListener false
            }
            false
        })
    }

    private fun peep(view: View, color: Int = Color.RED) {
        val background = view.background
        view.setBackgroundColor(color)
        Handler().postDelayed({
            view.background = background
        }, 1000)
    }

    @SuppressLint("SetTextI18n")
    fun search(view: View) {
        // Only runs if there is a view that is currently focused
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        runSearch()
    }

    fun runSearch() {
        var firstCheck = true
        var lastCheck = true
        firstStr = getWithoutDiacritics(first.text.toString()).toLowerCase()
        lastStr = getWithoutDiacritics(last.text.toString()).toLowerCase()

        if (firstStr.isEmpty() || !listNames.contains(firstStr)) {
            peep(first)
            firstCheck = false
        }
        if (lastStr.isEmpty() || !listNames.contains(lastStr)) {
            peep(last)
            lastCheck = false
        }

        if (firstCheck && lastCheck) {
            peep(first, Color.GREEN)
            peep(last, Color.GREEN)

            resultStr = ""

            val firstIndex = listNames.indexOf(firstStr)
            val lastIndex = listNames.indexOf(lastStr)

            val namesRange = listNames.toTypedArray().copyOfRange(firstIndex, lastIndex + 1)


            AsyncTask.execute {
                val total = namesRange.size
                for ((i, name) in namesRange.withIndex()) {
                    scrap(name)
                    runOnUiThread {
                        info.text = "$i/$total\n$resultStr"
                    }
                }

                val fileResult = File(
                    Environment.getExternalStorageDirectory().toString()
                            + "/Download/" + File.separator + "$firstStr-$lastStr.txt"
                )
                fileResult.createNewFile()
                val writer = FileWriter(fileResult)
                writer.append(resultStr)
                writer.flush()
                writer.close()
                runOnUiThread {
                    info.text = fileResult.absolutePath + "\n\n" + resultStr
                    MediaPlayer.create(this, R.raw.happy).start()
                }
            }
        }
    }

    private fun scrap(name: String) {
        try {
            val url = "https://www.eirphonebook.ie/q/name/who/$name/?customerType=RESIDENTIAL"
            //Connect to website
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val document = Jsoup.connect(url).get()


            val addresses = document.getElementsByClass("result-address")
            var i = 1
            for (address in addresses) {
                val nameFull = document.getElementById("listingbase$i").text()
                val addressText = address.text()
                if (addressText.toLowerCase().contains("dublin")) {
                    resultStr += nameFull + "\n" + addressText + "\n\n"
                }
                i++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getWithoutDiacritics(str: String): String {
        var string = str
        string = string.replace("ă", "a")
        string = string.replace("Ă", "A")
        string = string.replace("â", "a")
        string = string.replace("Â", "A")
        string = string.replace("î", "i")
        string = string.replace("Î", "I")
        string = string.replace("ş", "s")
        string = string.replace("ș", "s")
        string = string.replace("Ş", "S")
        string = string.replace("ţ", "t")
        string = string.replace("ț", "t")
        string = string.replace("Ţ", "T")
        return string
    }

    private fun checkPermissions() {
        val permissionGranted =
            checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
        val permissionGranted2 =
            checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED

        if (permissionGranted && permissionGranted2) {
            println("greit")
        } else {
            if (shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                showError()
            } else {
                requestPermissions(
                    this,
                    arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    private fun showError() {
        Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.first() == PERMISSION_GRANTED) {
                println("greit")
            } else {
                showError()
            }
        }
    }


    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 0
        private const val FILE_PICKER_REQUEST_CODE = 1
    }

}