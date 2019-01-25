package com.zl.update

import com.zl.core.api.data.BaseResult

data class UpdateResult(var notes: String?,
                        var creater: Int?,
                        var mustUpdate: Int?,
                        var versionName: String?,
                        var type: String?,
                        var url: String?,
                        var versionCode: Int?) : BaseResult()