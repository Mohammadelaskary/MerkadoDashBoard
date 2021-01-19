package com.example.elmohammadymarket.Views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.example.elmohammadymarket.Adapters.FullOrderAdapter;
import com.example.elmohammadymarket.Model.FullOrder;
import com.example.elmohammadymarket.Model.OrderProduct;
import com.example.elmohammadymarket.OnCallClickListener;
import com.example.elmohammadymarket.OnPrintClickListener;
import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.databinding.ActivityPreviousOrdersBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class PreviousOrders extends AppCompatActivity implements OnCallClickListener, OnPrintClickListener {

    private static final int REQUEST_CODE_CALL = 100;
    private static final int REQUEST_CODE_SHARE = 200 ;
    private static final int REQUEST_CODE_LOCATION = 300 ;
    private static final int PRINT_REQUEST_CODE = 400 ;
    ActivityPreviousOrdersBinding binding;
    List<FullOrder> list = new ArrayList<>();
    FullOrderAdapter adapter;
    String mobileNumber;
    String date;
    FullOrder order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviousOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setTitle("الطلبـــــات السابقة");
        binding.fullOrderRecycler.setVisibility(View.GONE);
        binding.ordersTitle.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        if (isConnected()) {
            getData(date);
            adapter = new FullOrderAdapter(this,list, false, this,this);
            binding.fullOrderRecycler.setAdapter(adapter);
            binding.fullOrderRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            binding.fullOrderRecycler.setHasFixedSize(true);

        } else {
            binding.progressBar.hide();

        }
        binding.share.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    createExcelFileAndShare(date);
                } else {
                    ActivityCompat.requestPermissions(PreviousOrders.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_SHARE);
                }

            }
        });


    }

    private void createExcelFileAndShare(String date) {
        String dateEdited = date.replace('/','-');
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet firstSheet = workbook.createSheet(dateEdited);
        HSSFRow rowA = firstSheet.createRow(0);
        HSSFCell cellA = rowA.createCell(0);
        cellA.setCellValue(new HSSFRichTextString("اسم المستخدم"));
        HSSFCell cellB = rowA.createCell(1);
        cellB.setCellValue(new HSSFRichTextString("رقم التليفون المحمول"));
        HSSFCell cellC = rowA.createCell(2);
        cellC.setCellValue(new HSSFRichTextString("رقم التليفون الأرضي"));
        HSSFCell cellD = rowA.createCell(3);
        cellD.setCellValue(new HSSFRichTextString("العنوان"));
        HSSFCell cellE = rowA.createCell(4);
        cellE.setCellValue(new HSSFRichTextString("المجموع"));
        HSSFCell cellF = rowA.createCell(5);
        cellF.setCellValue(new HSSFRichTextString("الشحن"));
        HSSFCell cellI = rowA.createCell(6);
        cellI.setCellValue(new HSSFRichTextString("الخصم"));
        HSSFCell cellG = rowA.createCell(7);
        cellG.setCellValue(new HSSFRichTextString("الخصم علي المجموع"));
        HSSFCell cellH = rowA.createCell(8);
        cellH.setCellValue(new HSSFRichTextString("الحساب الكلي"));
        for (int i = 0; i < list.size(); i++) {
            HSSFRow rowB = firstSheet.createRow(i+1);
            FullOrder order = list.get(i);
            HSSFCell cellY = rowB.createCell(0);
            cellY.setCellValue(new HSSFRichTextString(order.getUsername()));
            HSSFCell cellZ = rowB.createCell(1);
            cellZ.setCellValue(new HSSFRichTextString(order.getMobilePhone()));
            HSSFCell cellX = rowB.createCell(2);
            cellX.setCellValue(new HSSFRichTextString(order.getPhoneNumber()));
            HSSFCell cellL = rowB.createCell(3);
            cellL.setCellValue(new HSSFRichTextString(order.getAddress()));
            HSSFCell cellM = rowB.createCell(4);
            cellM.setCellValue(new HSSFRichTextString(String.valueOf(order.getSum())));
            HSSFCell cellN = rowB.createCell(6);
            cellN.setCellValue(new HSSFRichTextString(String.valueOf(order.getDiscount())));
            HSSFCell cellQ = rowB.createCell(5);
            cellQ.setCellValue(new HSSFRichTextString(String.valueOf(order.getShipping())));
            HSSFCell cellO = rowB.createCell(7);
            cellO.setCellValue(new HSSFRichTextString(String.valueOf(order.getOverAllDiscount())));
            HSSFCell cellP = rowB.createCell(8);
            cellP.setCellValue(new HSSFRichTextString(String.valueOf(order.getTotalCost())));
        }
        FileOutputStream fos = null;
        try {
            String str_path = Environment.getExternalStorageDirectory().toString();
            File file;
            file = new File(str_path, getString(R.string.app_name) +".xls");
            Uri uri = FileProvider.getUriForFile(this,getApplicationContext().getPackageName() + ".provider",file);
            Log.d("fileURi",String.valueOf(uri));
            fos = new FileOutputStream(file);
            workbook.write(fos);
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.setType("application/xls");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setPackage("com.whatsapp");
            Log.d("uriXls",uri+"");
            startActivity(share);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private float getTotal() {
        float total = 0;
        for (FullOrder fullOrder : list) {
            total += fullOrder.getTotalCost();
        }
        return total;
    }


    void getData(final String date) {
        DatabaseReference getRef = FirebaseDatabase.getInstance().getReference("Done orders");
        getRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                binding.progressBar.hide();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FullOrder fullOrder = snapshot.getValue(FullOrder.class);
                        assert fullOrder != null;
                        if (fullOrder.getDate().equals(date)) {
                            list.add(fullOrder);
                        }
                    }
                    binding.fullOrderRecycler.setVisibility(View.VISIBLE);
                    binding.ordersTitle.setVisibility(View.VISIBLE);
                    binding.total.setText(String.valueOf(getTotal()));
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean isConnected() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        return connected;
    }


    @Override
    public void onCallClickListener(String mobileNumber) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                this.mobileNumber = mobileNumber;
                makeCall(mobileNumber);
            } else {
                ActivityCompat.requestPermissions(PreviousOrders.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            }

        }
    }

    @Override
    public void onSendClickListener(LinearLayout layout, String mobileNumber) {

    }

    private void makeCall(String mobileNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if (requestCode == REQUEST_CODE_CALL) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall(mobileNumber);
            }
        } else if (requestCode == REQUEST_CODE_SHARE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createExcelFileAndShare(date);
            }
        }
        if (requestCode == PRINT_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    printOrder(order);
                } catch (EscPosConnectionException | EscPosParserException | EscPosEncodingException | EscPosBarcodeException e) {
                    e.printStackTrace();
                    Log.d("print error",e.getMessage());
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onPrintClickListener(FullOrder order) {
        this.order = order;
        Log.d("blue","print interface");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PreviousOrders.this, new String[]{Manifest.permission.BLUETOOTH},PRINT_REQUEST_CODE);
        } else {
            try {
                printOrder(order);
            } catch (EscPosConnectionException | EscPosParserException | EscPosEncodingException | EscPosBarcodeException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("printerError",e.getMessage());
            }
        }
    }
    private void printOrder(FullOrder fullOrder) throws EscPosConnectionException, EscPosParserException, EscPosEncodingException, EscPosBarcodeException {
        final String customerName = fullOrder.getUsername();
        final String address = fullOrder.getAddress();
        final String mobileNumber = fullOrder.getMobilePhone();
        final float sum = fullOrder.getSum();
        final float discount = fullOrder.getDiscount();
        final float overAllDiscount = fullOrder.getOverAllDiscount();
        final String phoneNumber = fullOrder.getPhoneNumber();
        final float netCost = fullOrder.getTotalCost();
        final List<OrderProduct> list = fullOrder.getOrders();
        final float shipping = fullOrder.getShipping();
        String ordersPrint = getOrdersPrintText(list);
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss").format(Calendar.getInstance().getTime());
        Log.d("blue","print method");
        EscPosPrinter printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 80f, 32);
        String resetText =
                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.ic_mercado_logo_small, DisplayMetrics.DENSITY_MEDIUM))+"</img>\n"
                        +"[C]\n"
                        +"[C]<u><font size ='big'>ميركادو</font></u>"
                        +"[C]\n"
                        +"[C]_________________________"
                        +"[C]\n"
                        +"[R]"+timeStamp+"[R]<font size = 'big'>الوقت والتاريخ:</font>"
                        +"[C]\n"
                        +"[R]"+customerName+"[R]<font size = 'big'>اسم العميل:</font>"
                        +"[C]\n"
                        +"[R]"+mobileNumber+"[R]<font size = 'big'>رقم التليفون المحمول:</font>"
                        +"[C]\n"
                        +"[R]"+phoneNumber+"[R]<font size = 'big'>رقم التليفون الأرضي:</font>"
                        +"[C]\n"
                        +"[R]"+address+"[R]<font size = 'big'>العنوان:</font>"
                        +"[C]\n"
                        +"[C]_________________________"
                        +"[C]\n"
                        +"[L]الاجمالي[L]الكمية   [L]بعد الخصم   [L]قبل الخصم   [R]اسم الصنف"
                        +"[C]\n"
                        +"[C]_________________________"
                        +"[C]\n"
                        +ordersPrint
                        +"[C]\n"
                        +"[C]_________________________"
                        +"[C]\n"
                        +"[L]"+sum+"[R]<font size = 'big'>الإجمالي:</font>"
                        +"[C]\n"
                        +"[L]"+discount+"[R]<font size = 'big'>إجمالي الخصم:</font>"
                        +"[C]\n"
                        +"[L]"+overAllDiscount+"[R]<font size = 'big'>الخصم علي المجموع:</font>"
                        +"[C]\n"
                        +"[L]"+shipping+"[R]<font size = 'big'>مصاريف الشحن:</font>"
                        +"[C]\n"
                        +"[C]_________________________"
                        +"[C]\n"
                        +"[L]"+netCost+"[R]<font size = 'big'>المبلغ المطلوب:</font>"
                        +"[C]\n"
                        +"[C]========================="
                        +"[C]\n"
                        +"[C]01101515954[C]<font size = 'big'>رقم الشكاوي:</font>"
                        +"[C]\n";
        printer.printFormattedText(resetText);
    }

    private String getOrdersPrintText(List<OrderProduct> list) {
        String ordersPrint = "";
        for (OrderProduct order : list ){
            String productName = order.getProductName();
            String originalPrice = order.getOriginalPrice();
            String finalPrice  = order.getFinalPrice();
            float orderedAmount = order.getOrdered();
            String total = String.valueOf(orderedAmount * Float.parseFloat(finalPrice));
            ordersPrint +=
                    "[L]"+ total+"   [L]"+orderedAmount +"   [L]"+finalPrice+"   [L]"+originalPrice+"   [R]"+productName;
        }
        return ordersPrint;
    }
}