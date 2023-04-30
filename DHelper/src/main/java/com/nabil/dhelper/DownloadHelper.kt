import com.nabil.dhelper.infofiles.OkHttpInfo
import java.util.regex.Pattern


class DownloadHelper {
    var listener : DownloadFetchListener? = null

    companion object {
        private const val regex1 = "filename.*=\\s*\"(.*)\\s*\""
        private const val regex2 = "filename.*=\\s*'(.*)\\s*'"
        private const val GB_BYTE : Long = 1000000000
        private const val MB_BYTE : Long = 1000000
        private const val KB_BYTE : Long = 1000

        fun convertUnit(sizeInByte : Long) : String{
            if (sizeInByte>= GB_BYTE){
                return "${sizeInByte/ GB_BYTE}GB"
            }

            if (sizeInByte>= MB_BYTE){
                return "${sizeInByte/ MB_BYTE}MB"
            }

            if (sizeInByte>= KB_BYTE){
                return "${sizeInByte/ KB_BYTE}KB"
            }

            return "${sizeInByte}KB"
        }

        class RequestBuilder(){
            val okHttpInfo = OkHttpInfo(null , null, null)

            fun setHttpUrl(url : String) : RequestBuilder{
                okHttpInfo.url = url
                return this
            }

            fun setUserAgent(ua : String): RequestBuilder {
                okHttpInfo.userAgent = ua
                return this
            }

            fun setCookies(cookies : String) : RequestBuilder{
                okHttpInfo.cookies = cookies
                return this
            }

            fun build() : OkHttpInfo {
                return okHttpInfo
            }

        }

        fun retrieveName(contentDisposition: String?, url: String): String? {
            var filename : String? = null

            if (!contentDisposition.isNullOrEmpty()){
                val matcher1 = Pattern.compile(regex1).matcher(contentDisposition)
                val matcher2 = Pattern.compile(regex2).matcher(contentDisposition)
                if (matcher1.find()) {
                    filename = matcher1.group(1)
                    return filename
                }
                if (matcher2.find()) {
                    filename = matcher2.group(1)
                    return filename
                }
            }

            var decodedUrl = url
            val queryIndex = decodedUrl.indexOf('?')
            if (queryIndex > 0) {
                decodedUrl = decodedUrl.substring(0, queryIndex)
            }
            if (!decodedUrl.endsWith("/")) {
                val index = decodedUrl.lastIndexOf('/') + 1
                if (index > 0) {
                    filename = decodedUrl.substring(index)
                }
            }

            return filename
        }
    }

    fun fetchDownload(builder: OkHttpInfo) {
        OkHttpExecutor(builder,listener).executeRequest()
    }

}