//For auto install app after download new version
package com.official.gold.gaming.tournamentpubg.kotlin

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.official.gold.gaming.tournamentpubg.utils.InstallReceivernew
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val NAME = "mostly-unused"
private const val PI_INSTALL = 3439

class MainMotor(app: Application) : AndroidViewModel(app) {
    private val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        app.packageManager.packageInstaller
    } else {
        TODO("VERSION.SDK_INT < LOLLIPOP")
    }
    private val resolver = app.contentResolver

    fun install(apkUri: Uri) {
        viewModelScope.launch(Dispatchers.Main) {
            installCoroutine(apkUri)
        }
    }

    public suspend fun installCoroutine(apkUri: Uri) =
            withContext(Dispatchers.IO) {

                resolver.openInputStream(apkUri)?.use { apkStream ->
                    val length =
                            DocumentFile.fromSingleUri(getApplication(), apkUri)?.length() ?: -1
                    val params =
                            PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
                    val sessionId = installer.createSession(params)
                    val session = installer.openSession(sessionId)

                    session.openWrite(NAME, 0, length).use { sessionStream ->
                        apkStream.copyTo(sessionStream)
                        session.fsync(sessionStream)
                    }
                    //val intent = Intent(getApplication(), InstallReceiver::class.java)
                    val intent = Intent(getApplication(), InstallReceivernew::class.java)
                    val pi = PendingIntent.getBroadcast(
                            getApplication(),
                            PI_INSTALL,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    session.commit(pi.intentSender)
                    session.close()
                }
            }
}
