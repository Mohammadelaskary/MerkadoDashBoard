package com.example.elmohammadymarket.Views;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dantsu.escposprinter.EscPosCharsetEncoding;
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
import com.example.elmohammadymarket.databinding.ActivityOrdersBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class OrdersActivity extends AppCompatActivity implements OnCallClickListener, OnPrintClickListener {
    private static final int REQUEST_CODE_CALL = 100;
    private static final int REQUEST_CODE_SHARE = 200;
    private static final int PRINT_REQUEST_CODE = 300;
    ActivityOrdersBinding binding;
    List<FullOrder> list = new ArrayList<>();
    FullOrderAdapter adapter;
    String mobileNumber;
    LinearLayout layout;
    String mobileNumberGlobal;
    FullOrder order;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("الطلبـــــات");
        binding.fullOrderRecycler.setVisibility(View.GONE);
        binding.ordersTitle.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        findBT();
        try {
            openBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isConnected()) {
            getData();
            adapter = new FullOrderAdapter(this, list, true, this,this);
            binding.fullOrderRecycler.setAdapter(adapter);
            binding.fullOrderRecycler.setItemAnimator(new DefaultItemAnimator());
            binding.fullOrderRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));
            binding.fullOrderRecycler.setHasFixedSize(true);
        } else {
            binding.progressBar.hide();
            binding.noOrdersText.setVisibility(View.VISIBLE);
            binding.noOrdersText.setText("تأكد من الاتصال بالانترنت...");
        }

    }


    void getData() {
        DatabaseReference getRef = FirebaseDatabase.getInstance().getReference("Orders");
        getRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (!dataSnapshot.exists()) {
                    binding.progressBar.hide();
                    binding.noOrdersText.setVisibility(View.VISIBLE);
                    binding.noOrdersText.setText("لا يوجد طلبات حاليا");
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FullOrder fullOrder = snapshot.getValue(FullOrder.class);
                        list.add(fullOrder);
                        Log.d(TAG, "onDataChange: orders" + fullOrder.getUsername());
                        adapter.notifyDataSetChanged();
                        binding.progressBar.hide();
                        binding.noOrders.setVisibility(View.GONE);
                        binding.fullOrderRecycler.setVisibility(View.VISIBLE);
                        binding.ordersTitle.setVisibility(View.VISIBLE);
                    }
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
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                Toast.makeText(this, "No bluetooth adapter available", Toast.LENGTH_SHORT).show();
            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals("XP-P323B-94EE")) {
                        mmDevice = device;
                        break;
                    }
                }
            }

            Toast.makeText(this, "Bluetooth device found.", Toast.LENGTH_SHORT).show();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            Toast.makeText(this, "Bluetooth Opened", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "ISO/IEC 8859-6");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(OrdersActivity.this, data, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCallClickListener(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {

                makeCall(mobileNumber);


            } else {

                ActivityCompat.requestPermissions(OrdersActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_CALL);
            }

        }
    }

    @Override
    public void onSendClickListener(LinearLayout layout, String mobileNumber) {
        this.layout = layout;
        mobileNumberGlobal = mobileNumber;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                shareData(layout,mobileNumber);

            } else {

                ActivityCompat.requestPermissions(OrdersActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_SHARE);
            }

        }
    }

    private void shareData(LinearLayout layout,  String mobileNumber) {
        File file = saveBitMap(OrdersActivity.this, layout);    //which view you want to pass that view as parameter
        assert file != null;
        Uri imgUri = Uri.parse(file.getAbsolutePath());
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT,    "رقم تليفون العميل " + mobileNumber);
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
        whatsappIntent.setType("image/png");
        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {

        }
    }

    private File saveBitMap(Context context, View drawView) {
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Handcare");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() + File.separator + System.currentTimeMillis() + ".png";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery(context, pictureFile.getAbsolutePath());
        return pictureFile;
    }

    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        }

        if (requestCode == REQUEST_CODE_SHARE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareData(layout,mobileNumber);
            }
        }
        if (requestCode == PRINT_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    printOrder(order);

            }
        }

    }

    @Override
    public void onPrintClickListener(FullOrder order) {
        this.order = order;
        Log.d("blue","print interface");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d("blue","permissiongranted");

                    printOrder(order);

            } else {
                ActivityCompat.requestPermissions(OrdersActivity.this, new String[]{Manifest.permission.BLUETOOTH}, PRINT_REQUEST_CODE);
                Log.d("bluetoothPermissionNot","permission not granted");
            }
        }
    }
    private void printOrder(FullOrder fullOrder) {
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
////        System.setProperty("file.encoding", "UTF-16");
//        EscPosPrinter printer =
//                new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(),
//                        203,
//                        576f,
//                        100);
////        Log.d("blue",printer.getPrinterDpi()+"");
//        String resetText =
//////                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.ic_mercado_logo_small, DisplayMetrics.DENSITY_MEDIUM))+"</img>\n";
//
//                        "ميركادو[C]"
//                       ;
////                        +"[C]_________________________"
////                        +"[C]\n"
////                        +"[R]"+timeStamp+"[R]<font size = 'big'>الوقت والتاريخ:</font>"
////                        +"[C]\n"
////                        +"[R]"+customerName+"[R]<font size = 'big'>اسم العميل:</font>"
////                        +"[C]\n"
////                        +"[R]"+mobileNumber+"[R]<font size = 'big'>رقم التليفون المحمول:</font>"
////                        +"[C]\n"
////                        +"[R]"+phoneNumber+"[R]<font size = 'big'>رقم التليفون الأرضي:</font>"
////                        +"[C]\n"
////                        +"[R]"+address+"[R]<font size = 'big'>العنوان:</font>"
////                        +"[C]\n"
////                        +"[C]_________________________"
////                        +"[C]\n"
////                        +"[L]الاجمالي[L]الكمية   [L]بعد الخصم   [L]قبل الخصم   [R]اسم الصنف"
////                        +"[C]\n"
////                        +"[C]_________________________"
////                        +"[C]\n"
////                        +ordersPrint
////                        +"[C]\n"
////                        +"[C]_________________________"
////                        +"[C]\n"
////                        +"[L]"+sum+"[R]<font size = 'big'>الإجمالي:</font>"
////                        +"[C]\n"
////                        +"[L]"+discount+"[R]<font size = 'big'>إجمالي الخصم:</font>"
////                        +"[C]\n"
////                        +"[L]"+overAllDiscount+"[R]<font size = 'big'>الخصم علي المجموع:</font>"
////                        +"[C]\n"
////                        +"[L]"+shipping+"[R]<font size = 'big'>مصاريف الشحن:</font>"
////                        +"[C]\n"
////                        +"[C]_________________________"
////                        +"[C]\n"
////                        +"[L]"+netCost+"[R]<font size = 'big'>المبلغ المطلوب:</font>"
////                        +"[C]\n"
////                        +"[C]========================="
////                        +"[C]\n"
////                        +"[C]01101515954[C]<font size = 'big'>رقم الشكاوي:</font>"
////                        +"[C]\n";
//
    //    printer.printFormattedTextAndCut(resetText);
        try {

            // the text typed by the user
            String msg = "الله اكبر";
            msg += "\n";
            msg += "\n";
            msg += "\n";
            msg += "\n";

            mmOutputStream.write(msg.getBytes());

            // tell the user data were sent
            Toast.makeText(this, "تم ارسال البيانات", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
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