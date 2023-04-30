import com.nabil.dhelper.infofiles.DownloadInfo
import com.nabil.dhelper.infofiles.OkHttpInfo
import okhttp3.*
import java.io.IOException


class OkHttpExecutor(val info: OkHttpInfo, val listener : DownloadFetchListener?) {
    private val USER_AGENT = "user-agent"
    private val COOKIES = "cookie"
    private val DISPO = "content-disposition"
    private val ContentSize = "content-length"
    private val MIMETYPE = "content-type"
    private val okHttpClient = OkHttpClient()

    fun buildRequest() : Request {
        val requestBuilder = Request.Builder()
            .url(info.url!!)
            .header("connection","keep-alive")

        info.cookies?.let {
            requestBuilder.header(COOKIES,it)
        }

        info.userAgent?.let {
            requestBuilder.header(USER_AGENT, it)
        }

        return requestBuilder.build()
    }

    fun executeRequest(){
        okHttpClient.newCall(buildRequest())
            .enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    listener?.onFetchListener(false, DownloadInfo("",0,"",""))
                }

                override fun onResponse(call: Call, response: Response) {
                    val downloadInfo = DownloadInfo("",0,"","")

                    if (response.isSuccessful){
                        for (header in response.headers.names()){
                            val headername = header.lowercase()
                            if (headername==DISPO){
                                downloadInfo.contentDisposition = response.headers[DISPO].toString()
                            }

                            if (headername==ContentSize){
                                 response.headers[ContentSize]?.let {
                                    downloadInfo.fileSize = it.toLong()
                                }
                            }
                            if (headername==MIMETYPE){
                                downloadInfo.mimetype = response.headers[MIMETYPE].toString()
                            }
                        }
                        downloadInfo.fileName = DownloadHelper.retrieveName(downloadInfo.contentDisposition,info.url!!)
                        listener?.onFetchListener(true,downloadInfo)

                    }else{
                        listener?.onFetchListener(false, DownloadInfo("",0,"",""))
                    }
                }
            })
    }

}