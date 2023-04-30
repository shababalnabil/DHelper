import com.nabil.dhelper.infofiles.DownloadInfo

interface DownloadFetchListener {
    fun onFetchListener(isSuccessful : Boolean, downloadInfo: DownloadInfo)
}