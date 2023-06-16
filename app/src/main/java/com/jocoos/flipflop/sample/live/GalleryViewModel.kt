package com.jocoos.flipflop.sample.live

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import java.io.IOException

private const val INITIAL_POSITION = 0
private const val PAGE_SIZE = 20

class GalleryViewModel : ViewModel() {
    fun loadPhotos(contentResolver: ContentResolver, uri: Uri): Flow<PagingData<Uri>> = Pager(
        pagingSourceFactory = { PhotoDataSource(contentResolver, uri) },
        config = PagingConfig(
            pageSize = PAGE_SIZE,
        )
    ).flow.cachedIn(viewModelScope)
}

class PhotoDataSource(
    private val contentResolver: ContentResolver,
    private val uri: Uri
) : PagingSource<Int, Uri>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Uri> {
        return try {
            val startPosition = params.key ?: INITIAL_POSITION
            val data = getContacts(uri, params.loadSize, startPosition)
            LoadResult.Page(
                data = data,
                prevKey = if (startPosition == INITIAL_POSITION) null else startPosition - PAGE_SIZE,
                nextKey = if (data.size != params.loadSize) null else startPosition + data.size
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Uri>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    private fun getContacts(uri: Uri, limit: Int, offset: Int): MutableList<Uri> {
        val projection = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATE_ADDED)
        val photos: MutableList<Uri> = mutableListOf()
        var cursor: Cursor? = null
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                val bundle = Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                    putString(
                        ContentResolver.QUERY_ARG_SQL_SORT_ORDER,
                        MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
                    )
                }
                cursor = contentResolver.query(uri, projection, bundle, null)
                if (cursor != null) {
                    cursor.moveToFirst()

                    while (!cursor.isAfterLast) {
                        val columnIndexId = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                        val contentUri = ContentUris.withAppendedId(uri, cursor.getLong(columnIndexId))

                        photos.add(contentUri)
                        cursor.moveToNext()
                    }
                }

            } else {
                cursor = contentResolver.query(uri, projection, null, null,
                    "${MediaStore.Files.FileColumns.DATE_ADDED} DESC LIMIT $limit OFFSET $offset")
                if (cursor != null) {
                    cursor.moveToFirst()

                    while (!cursor.isAfterLast) {
                        val columnIndexId = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                        val contentUri = ContentUris.withAppendedId(uri, cursor.getLong(columnIndexId))

                        photos.add(contentUri)
                        cursor.moveToNext()
                    }
                }
            }
        } finally {
            cursor?.close()
        }
        return photos
    }
}
