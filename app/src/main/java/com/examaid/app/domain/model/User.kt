package com.examaid.app.domain.model

import com.examaid.app.core.constants.Constants

data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val subscriptionType: Constants.SubscriptionType = Constants.SubscriptionType.FREE,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)

