package com.zl.cpp

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/12 17:10.<br/>
 */
class Test {

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

}