package com.example.elmohammadymarket;

import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.example.elmohammadymarket.Model.FullOrder;

import java.util.List;

public interface OnPrintClickListener {
    void onPrintClickListener(FullOrder order) throws EscPosConnectionException, EscPosEncodingException, EscPosBarcodeException, EscPosParserException;
}
