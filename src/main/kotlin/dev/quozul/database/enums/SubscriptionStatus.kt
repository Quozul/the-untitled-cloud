package dev.quozul.database.enums

enum class SubscriptionStatus {
	REGISTERED, // Must not use but present just in case
	PENDING, // Awaiting payment to activate the product
	ACTIVE, // Payment has been made, the service can be used
	SUSPENDED, // Payment failed or subscription is being cancelled, but subscription isn't cancelled
	CANCELLED, // Customer has stopped the subscription
}