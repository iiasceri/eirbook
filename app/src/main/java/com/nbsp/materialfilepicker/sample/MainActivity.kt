package com.nbsp.materialfilepicker.sample

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import org.jsoup.Jsoup
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private var resultStr = ""
    private var wholeFile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()
        scoateToateDiacriticile()
    }

    fun scoateToateDiacriticile() {
        scoateDiacritice("a")
        scoateDiacritice("b")
        scoateDiacritice("c")
        scoateDiacritice("d")
        scoateDiacritice("e")
        scoateDiacritice("f")
        scoateDiacritice("g")
        scoateDiacritice("h")
        scoateDiacritice("i")
        scoateDiacritice("ii")
        scoateDiacritice("j")
        scoateDiacritice("k")
        scoateDiacritice("l")
        scoateDiacritice("m")
        scoateDiacritice("n")
        scoateDiacritice("o")
        scoateDiacritice("p")
        scoateDiacritice("r")
        scoateDiacritice("s")
        scoateDiacritice("ss")
        scoateDiacritice("t")
        scoateDiacritice("tt")
        scoateDiacritice("u")
        scoateDiacritice("v")
        scoateDiacritice("z")
        val fileResult = File(Environment.getExternalStorageDirectory().toString()
                + "/Download/" + File.separator + "alfabet.txt")
        fileResult.createNewFile()
        val writer = FileWriter(fileResult)
        writer.append(wholeFile)
        writer.flush()
        writer.close()
    }

    private fun scoateDiacritice(letter: String){
        var string: String?
        try {
            val inputStream: InputStream = assets.open("$letter.txt")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            string = String(buffer)
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

            val fileResult = File(Environment.getExternalStorageDirectory().toString()
                    + "/Download/" + File.separator + "$letter.txt")
            fileResult.createNewFile()
            val writer = FileWriter(fileResult)
            wholeFile += "\n" + string
            writer.append(string)
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun checkPermissions() {
        val permissionGranted = checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
        val permissionGranted2 = checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED

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
                openFilePicker()
            } else {
                showError()
            }
        }
    }

    private fun openFilePicker() {
        val externalStorage = Environment.getExternalStorageDirectory()
        val folder = File(externalStorage, "Download")

        MaterialFilePicker()
                // Pass a source of context. Can be:
                //    .withActivity(Activity activity)
                //    .withFragment(Fragment fragment)
                //    .withSupportFragment(androidx.fragment.app.Fragment fragment)
                .withActivity(this)
                // With cross icon on the right side of toolbar for closing picker straight away
                .withCloseMenu(true)
                // Entry point path (user will start from it)
                .withPath(folder.absolutePath)
                // Root path (user won't be able to come higher than it)
                .withRootPath(externalStorage.absolutePath)
                // Showing hidden files
                .withHiddenFiles(true)
                // Want to choose only jpg images
                .withFilter(Pattern.compile(".*\\.(txt)$"))
                // Don't apply filter to directories names
                .withFilterDirectories(false)
                .withTitle("Sample title")
                .withRequestCode(FILE_PICKER_REQUEST_CODE)
                .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data ?: throw IllegalArgumentException("data must not be null")

            val path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)

            if (path != null) {
                val file = File(path)
                val str = file.readText()
//                text.text = str
                val lines = file.readLines()
                for (line in lines) {
                    val name = line.replace("\n", "")
                    scrap("Zain")
                    break
                }
                val fName = file.nameWithoutExtension
                var fileResult = File(Environment.getExternalStorageDirectory().toString()
                        + "/Download/" + File.separator + "$fName-result.txt")
                var i = 0
                while (fileResult.exists()) {
                    i++
                    fileResult = File(Environment.getExternalStorageDirectory().toString()
                            + "/Download/" + File.separator + "$fName-result$i.txt")
                }
                fileResult.createNewFile()
                val writer = FileWriter(fileResult)
                writer.append(resultStr)
                writer.flush()
                writer.close()
            }
        }
    }

    private fun scrap(name: String) {
        try {
            val url = "https://www.eirphonebook.ie/q/name/where/Dublin/who/$name/?customerType=RESIDENTIAL"
            //Connect to website
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val document = Jsoup.connect(url).get()
            val nameFull = document.getElementById("listingbase1").text()
            val addresses = document.getElementsByClass("result-address")
            for (address in addresses) {
                val addressText = address.text()
                if (addressText.toLowerCase().contains("dublin"))
                    resultStr += nameFull + "\n" + addressText + "\n"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 0
        private const val FILE_PICKER_REQUEST_CODE = 1
    }
}