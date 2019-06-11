package org.dash.dashwalletkit.data

import android.app.Application
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.governance.GovernanceObject
import org.bitcoinj.governance.listeners.GovernanceObjectAddedEventListener

import org.bitcoinj.utils.Threading
import org.dash.dashwalletkit.WalletAppKitService

class GovernanceLiveData(application: Application) :
        WalletAppKitServiceLiveData<List<GovernanceObject>>(application), GovernanceObjectAddedEventListener {

    override fun onActive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.context.governanceManager.addGovernanceObjectAddedListener(this, Threading.SAME_THREAD)
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.context.governanceManager.addGovernanceObjectAddedListener(this)
    }

    override fun onGovernanceObjectAdded(nHash: Sha256Hash, governanceObject: GovernanceObject) {
        val governanceObjects = walletAppKitService!!.wallet.context.governanceManager.getAllNewerThan(0)
        postValue(governanceObjects)
    }
}
