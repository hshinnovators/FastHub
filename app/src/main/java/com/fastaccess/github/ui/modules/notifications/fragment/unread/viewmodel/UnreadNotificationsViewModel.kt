package com.fastaccess.github.ui.modules.notifications.fragment.unread.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.data.repository.NotificationRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.platform.paging.LoadMoreBoundary
import com.fastaccess.github.usecase.notification.NotificationUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 31.10.18.
 */
class UnreadNotificationsViewModel @Inject constructor(
        private val provider: NotificationRepositoryProvider,
        private val usecase: NotificationUseCase
) : BaseViewModel() {

    private var currentPage = 0
    private var isLastPage = false

    fun notifications(): LiveData<PagedList<NotificationModel>> {
        val dataSourceFactory = provider.getNotifications(true)
        val config = PagedList.Config.Builder()
                .setPrefetchDistance(25)
                .setPageSize(30)
                .build()
        return LivePagedListBuilder(dataSourceFactory, config)
                .setBoundaryCallback(LoadMoreBoundary(loadMoreLiveData))
                .build()
    }

    fun loadNotifications(reload: Boolean = false) {
        if (reload) {
            currentPage = 0
            isLastPage = false
        }
        currentPage++
        if (!reload && isLastPage) return
        usecase.page = currentPage
        add(callApi(usecase.buildObservable())
                .subscribe({
                    isLastPage = it.last == currentPage
                }, ::println))
    }

    fun hasNext() = isLastPage
}