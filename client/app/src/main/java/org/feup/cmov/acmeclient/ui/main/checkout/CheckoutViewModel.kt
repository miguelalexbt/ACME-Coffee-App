package org.feup.cmov.acmeclient.ui.main.checkout

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okio.Buffer
import org.feup.cmov.acmeclient.MainApplication
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.utils.Cache.Companion.cachedUser
import org.feup.cmov.acmeclient.utils.Crypto
import java.nio.charset.Charset


class CheckoutViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is checkout Fragment"
//    }
//    val text: LiveData<String> = _text

//    val items: LiveData<List<Item>> = dataRepository.getItems().asLiveData()



}