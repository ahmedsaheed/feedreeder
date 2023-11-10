package com.griffith.feedreeder_3061874.data

import com.griffith.feedreeder_3061874.data.room.FeedsDao
import com.griffith.feedreeder_3061874.data.room.TransactionRunner

class FeedStore (
    private val feedDao: FeedsDao,
    private val feedFollowedEntryDao: FeedFollowedEntryDao,
    private val transactionRunner: TransactionRunner
){

}