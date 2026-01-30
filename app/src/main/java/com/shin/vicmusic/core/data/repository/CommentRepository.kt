package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toCommentDetail
import com.shin.vicmusic.core.data.mapper.toCommentThreads
import com.shin.vicmusic.core.domain.CommentDetail
import com.shin.vicmusic.core.domain.CommentThread
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.model.request.CommentAddReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
) {

    /**
     * 获取评论列表 (已重构为评论线程)
     */
    suspend fun getComments(
        resourceType: String,
        resourceId: String,
        queryType: String,
        page: Int,
        size: Int
    ): Result<NetworkPageData<CommentThread>> {
        val dtoResponse = datasource.getComments(resourceType, resourceId, queryType, page, size)

        return if (dtoResponse.code == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            val domainList = dtoList.toCommentThreads()
            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            Result.Success(domainData)
        } else {
            Result.Error(dtoResponse.message ?: "获取评论列表失败")
        }
    }

    /**
     * 获取评论详情 (带回复的分页)
     */
    suspend fun getCommentDetail(id: String, page: Int = 1, size: Int = 5): Result<CommentDetail> {
        val resp = datasource.getCommentDetail(id, page, size)
        return if (resp.code == 0 && resp.data != null) {
            val commentDetail = resp.data.toCommentDetail()
            Result.Success(commentDetail)
        } else {
            Result.Error(resp.message ?: "获取评论回复失败")
        }
    }

    /**
     * 添加评论
     */
    suspend fun addComment(req: CommentAddReq): Result<Unit> {
        val resp = datasource.addComment(req)
        return if (resp.code == 0) {
            Result.Success(Unit)
        } else {
            Result.Error(resp.message ?: "添加评论失败")
        }
    }

    /**
     * 删除评论
     */
    suspend fun deleteComment(id: String): Result<Unit> {
        val resp = datasource.deleteComment(id)
        return if (resp.code == 0) {
            Result.Success(Unit)
        } else {
            Result.Error(resp.message ?: "删除评论失败")
        }
    }

    /**
     * 点赞/取消点赞评论
     * @return Result<Int> 成功时 data 为 0 表示取消点赞，1 表示点赞成功
     */
    suspend fun likeComment(id: String): Result<Int> {
        val resp = datasource.likeComment(id)
        return if (resp.code == 0 && resp.data != null) {
            Result.Success(resp.data)
        } else {
            Result.Error(resp.message ?: "操作失败")
        }
    }
}
