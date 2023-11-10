package com.griffith.feedreeder_3061874.data

import kotlinx.coroutines.CoroutineDispatcher

class FeedRepository (
    private val feedFetcher: FeedFetcher,
    private val feedStore: FeedStore,
//    private val episodeStore: EpisodeStore,
//    private val categoryStore: CategoryStore,
//    private val transactionRunner: TransactionRunner,
//    mainDispatcher: CoroutineDispatcher
    )