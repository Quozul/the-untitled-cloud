package dev.quozul.database.extensions.subscription

import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.models.Subscription


val Subscription.isPending: Boolean
	get() = subscriptionStatus === SubscriptionStatus.PENDING

val Subscription.isCancelled: Boolean
	get() = subscriptionStatus === SubscriptionStatus.CANCELLED

val Subscription.isActive: Boolean
	get() = subscriptionStatus === SubscriptionStatus.ACTIVE

val Subscription.isSuspended: Boolean
	get() = subscriptionStatus === SubscriptionStatus.SUSPENDED

val Subscription.isRegistered: Boolean
	get() = subscriptionStatus === SubscriptionStatus.REGISTERED