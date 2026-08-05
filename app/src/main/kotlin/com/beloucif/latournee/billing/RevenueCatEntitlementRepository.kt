package com.beloucif.latournee.billing

import android.app.Activity
import android.content.Context
import com.beloucif.latournee.BuildConfig
import com.beloucif.latournee.core.PremiumPlan
import com.beloucif.latournee.core.isPremiumEntitlementActive
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.models.StoreTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Real Purchases SDK-backed [EntitlementRepository]. Only ever instantiated by
 * LaTourneeApplication when BuildConfig.BILLING_ENABLED is true (a RevenueCat key is
 * configured) - see [StubEntitlementRepository] for the guest-mode default otherwise.
 *
 * Every SDK call is best-effort: a network failure, missing offering or cancelled purchase
 * resolves to `false`/no state change rather than throwing, so a flaky connection never
 * crashes the app - it just leaves the player at "not premium yet".
 */
class RevenueCatEntitlementRepository(
    context: Context,
    apiKey: String,
) : EntitlementRepository {

    private val _isPremium = MutableStateFlow(false)
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    init {
        if (!Purchases.isConfigured) {
            Purchases.logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.WARN
            Purchases.configure(PurchasesConfiguration.Builder(context.applicationContext, apiKey).build())
        }
        Purchases.sharedInstance.updatedCustomerInfoListener = UpdatedCustomerInfoListener { info ->
            _isPremium.value = info.hasPremiumEntitlement()
        }
        refreshEntitlement()
    }

    private fun refreshEntitlement() {
        Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
            override fun onReceived(customerInfo: CustomerInfo) {
                _isPremium.value = customerInfo.hasPremiumEntitlement()
            }

            override fun onError(error: PurchasesError) = Unit // best-effort: stay as-is, never crash
        })
    }

    override suspend fun purchasePremium(activity: Activity, plan: PremiumPlan): Boolean =
        suspendCancellableCoroutine { continuation ->
            Purchases.sharedInstance.getOfferings(object : ReceiveOfferingsCallback {
                override fun onReceived(offerings: Offerings) {
                    val packageToPurchase = offerings.current
                        ?.availablePackages
                        ?.firstOrNull { PremiumPlan.fromProductId(it.product.id) == plan }

                    if (packageToPurchase == null) {
                        if (continuation.isActive) continuation.resume(false)
                        return
                    }

                    Purchases.sharedInstance.purchase(
                        PurchaseParams.Builder(activity, packageToPurchase).build(),
                        object : PurchaseCallback {
                            override fun onCompleted(storeTransaction: StoreTransaction, customerInfo: CustomerInfo) {
                                _isPremium.value = customerInfo.hasPremiumEntitlement()
                                if (continuation.isActive) continuation.resume(true)
                            }

                            override fun onError(error: PurchasesError, userCancelled: Boolean) {
                                if (continuation.isActive) continuation.resume(false)
                            }
                        },
                    )
                }

                override fun onError(error: PurchasesError) {
                    if (continuation.isActive) continuation.resume(false)
                }
            })
        }

    override suspend fun restorePurchases(): Boolean = suspendCancellableCoroutine { continuation ->
        Purchases.sharedInstance.restorePurchases(object : ReceiveCustomerInfoCallback {
            override fun onReceived(customerInfo: CustomerInfo) {
                _isPremium.value = customerInfo.hasPremiumEntitlement()
                if (continuation.isActive) continuation.resume(true)
            }

            override fun onError(error: PurchasesError) {
                if (continuation.isActive) continuation.resume(false)
            }
        })
    }

    private fun CustomerInfo.hasPremiumEntitlement(): Boolean =
        isPremiumEntitlementActive(entitlements.active.keys)
}
