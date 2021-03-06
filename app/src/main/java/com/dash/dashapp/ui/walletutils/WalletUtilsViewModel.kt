package com.dash.dashapp.ui.walletutils

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import org.bitcoinj.core.Masternode
import org.bitcoinj.governance.GovernanceObject
import org.dash.dashwalletkit.data.*

class WalletUtilsViewModel(application: Application) : AndroidViewModel(application) {

    private val _peerConnectivity = PeerConnectivityLiveData(application)
    val peerConnectivity: PeerConnectivityLiveData
        get() = _peerConnectivity

    private val _walletInfoLiveData = WalletInfoLiveData(application)
    val walletInfoLiveData: WalletInfoLiveData
        get() = _walletInfoLiveData

    private val _blockchainState = BlockchainStateLiveData(application)
    val blockchainState: BlockchainStateLiveData
        get() = _blockchainState

    private val _masternodes = MasternodesLiveData(application)
    val masternodes: LiveData<List<Masternode>>
        get() = _masternodes

    private val _governanceObjects = GovernanceLiveData(application)
    val governanceObjects: LiveData<List<GovernanceObject>>
        get() = _governanceObjects

    private val _masternodeSync = MasternodeSyncLiveData(application)
    val masternodeSync: MasternodeSyncLiveData
        get() = _masternodeSync
}
