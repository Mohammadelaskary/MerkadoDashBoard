package com.example.elmohammadymarket.Views;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.printservice.PrintService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.example.elmohammadymarket.Adapters.PrintOrderAdapter;
import com.example.elmohammadymarket.Model.FullOrder;
import com.example.elmohammadymarket.Model.Order;
import com.example.elmohammadymarket.Model.OrderProduct;
import com.example.elmohammadymarket.Model.PharmacyOrder;
import com.example.elmohammadymarket.OnCallClickListener;
import com.example.elmohammadymarket.OnPrintClickListener;
import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.databinding.ActivityOrdersBinding;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.output.PrinterOutputStream;
import com.github.danielfelgar.drawreceiptlib.IDrawItem;
import com.github.danielfelgar.drawreceiptlib.ReceiptBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.BitmapCallback;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.BitmapToByteData;
import net.posprinter.utils.DataForSendToPrinterPos80;
import net.posprinter.utils.PosPrinterDev;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.content.ContentValues.TAG;


public class OrdersActivity extends AppCompatActivity implements OnCallClickListener, OnPrintClickListener {
        private static final int REQUEST_CODE_CALL = 100;
        private static final int REQUEST_CODE_SHARE = 200;
        private static final int PRINT_REQUEST_CODE = 300;
        private static final int WRITE_EXTERNAL = 754;
        private static final int READ_EXTERNAL = 450;
        private static final int PERMISSION_BLUETOOTH = 650;
    ActivityOrdersBinding binding;
        List<FullOrder> list = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        FullOrderAdapter adapter;
        PrintOrderAdapter printAdapter;
        String mobileNumber;
        LinearLayout layout;
        String mobileNumberGlobal;
        FullOrder order;
        String complaintPhoneNumber;
        BluetoothAdapter mBluetoothAdapter;
        String imagePath;
        List<PharmacyOrder> pharmacyOrders = new ArrayList<>();
        private UUID applicationUUID = UUID
                .fromString("00001101-0000-1000-8000-00805F9B34FB");
        private BluetoothSocket mBluetoothSocket;
        BluetoothDevice mBluetoothDevice;
        public static IMyBinder binder;
        OutputStream outputStream;
        InputStream inputStream;

       ServiceConnection conn = new ServiceConnection() {
           @Override
           public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
               //Bind successfully
               binder= (IMyBinder) iBinder;
               Log.i("binderconnected",componentName.toString());
               connectBluetooth();
           }

           @Override
           public void onServiceDisconnected(ComponentName componentName) {

           }
       };


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityOrdersBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            getSupportActionBar().setTitle("الطلبـــــات");
            binding.fullOrderRecycler.setVisibility(View.GONE);
            binding.ordersTitle.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);


            Tiny.getInstance().init(getApplication());

            getComplaintPhoneNumber();

            try {
                openBT();
            } catch (IOException e) {
                e.printStackTrace();
            }
            findBT();

            if (isConnected()) {
                getData();
                adapter = new FullOrderAdapter(this, orders, true, this, this);
                binding.fullOrderRecycler.setAdapter(adapter);
                binding.fullOrderRecycler.setItemAnimator(new DefaultItemAnimator());
                binding.fullOrderRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));
            } else {
                binding.progressBar.hide();
                binding.noOrdersText.setVisibility(View.VISIBLE);
                binding.noOrdersText.setText("تأكد من الاتصال بالانترنت...");
            }

        }



        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            Log.d("xxx", "onActivityResult " + requestCode);


        }

        private void getComplaintPhoneNumber() {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Contact");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        complaintPhoneNumber = dataSnapshot.getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent=new Intent(this, PosprinterService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(conn);
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
                            Log.d(TAG, "onDataChange: orders" + fullOrder.getId());
                            adapter.notifyDataSetChanged();
                            binding.progressBar.hide();
                            binding.noOrders.setVisibility(View.GONE);
                            binding.fullOrderRecycler.setVisibility(View.VISIBLE);
                            binding.ordersTitle.setVisibility(View.VISIBLE);
                        }
                    }
                    getPharmacyOrders();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    private void getPharmacyOrders() {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PharmacyOrders");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    pharmacyOrders.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        PharmacyOrder order = dataSnapshot.getValue(PharmacyOrder.class);
                        pharmacyOrders.add(order);
                    }
                    addOrdersToList();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
    }

    private void addOrdersToList() {
        List<Integer> ordersIds = new ArrayList<>();
        for (FullOrder order:list){
            int id = order.getId();
            if (!ordersIds.contains(id)) {
                ordersIds.add(id);
            }
        }

        if (!pharmacyOrders.isEmpty()) {
            for (PharmacyOrder pharmacyOrder : pharmacyOrders) {
                int id = Integer.parseInt(pharmacyOrder.getOrderId());
                if (!ordersIds.contains(id)) {
                    ordersIds.add(id);
                }
            }
        }
        for (int id:ordersIds){
            Order order = new Order(id);
            for (FullOrder fullOrder : list) {
                int orderId = fullOrder.getId();
                if (orderId == id) {
                    order.setFullOrder(fullOrder);
                    break;
                }
            }
            List<PharmacyOrder> currentPharmacyOrders = new ArrayList<>();
            if (!pharmacyOrders.isEmpty()) {
                for (PharmacyOrder pharmacyOrder : pharmacyOrders) {
                    int orderId = Integer.parseInt(pharmacyOrder.getOrderId());
                    if (orderId == id) {
                        currentPharmacyOrders.add(pharmacyOrder);
                    }
                }
            }
            order.setPharmacyOrders(currentPharmacyOrders);
            orders.add(order);
            adapter.notifyDataSetChanged();
        }

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

                if (mBluetoothAdapter == null) {
                    Toast.makeText(this, "No bluetooth adapter available", Toast.LENGTH_SHORT).show();
                }

                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);
                }

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {

                        // RPP300 is the name of the bluetooth printer device
                        // we got this name from the list of paired devices
                        if (device.getName().equals("XP-P323B-94EE")) {
                            mBluetoothDevice = device;
                            Toast.makeText(this, mBluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
                            Log.d("deviceName",mBluetoothDevice.getName());
                            Log.d("deviceAddress",mBluetoothDevice.getAddress());
                            break;
                        }
                    }
                }

                Toast.makeText(this, "Bluetooth device found.", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void openBT() throws IOException {
            try {

                // Standard SerialPortService ID
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                mBluetoothSocket.connect();
                Toast.makeText(this, "Bluetooth Opened", Toast.LENGTH_SHORT).show();

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
                    shareData(layout, mobileNumber);

                } else {

                    ActivityCompat.requestPermissions(OrdersActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_SHARE);
                }

            }
        }

        private void shareData(LinearLayout layout, String mobileNumber) {
            File file = saveBitMap(OrdersActivity.this, layout);    //which view you want to pass that view as parameter
            assert file != null;
            Uri imgUri = Uri.parse(file.getAbsolutePath());
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "رقم تليفون العميل " + mobileNumber);
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

        private File saveBitMap(Context context, Bitmap bitmap) {
            File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Handcare");
            if (!pictureFileDir.exists()) {
                boolean isDirectoryCreated = pictureFileDir.mkdirs();
                if (!isDirectoryCreated)
                    Log.i("ATG", "Can't create directory to save the image");
                return null;
            }
            String filename = pictureFileDir.getPath() + File.separator + System.currentTimeMillis() + ".png";
            File pictureFile = new File(filename);
            try {
                pictureFile.createNewFile();
                FileOutputStream oStream = new FileOutputStream(pictureFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, oStream);
                oStream.flush();
                oStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("TAG", "There was an issue saving the image.");
            }
            scanGallery(context, pictureFile.getAbsolutePath());
            return pictureFile;
        }

        //    private File saveBitMap(Context context, Bitmap drawView) {
    //        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Handcare");
    //        if (!pictureFileDir.exists()) {
    //            boolean isDirectoryCreated = pictureFileDir.mkdirs();
    //            if (!isDirectoryCreated)
    //                Log.i("ATG", "Can't create directory to save the image");
    //            return null;
    //        }
    //        String filename = pictureFileDir.getPath() + File.separator + System.currentTimeMillis() + ".png";
    //        File pictureFile = new File(filename);
    //
    //        try {
    //            pictureFile.createNewFile();
    //            FileOutputStream oStream = new FileOutputStream(pictureFile);
    //            drawView.compress(Bitmap.CompressFormat.PNG, 100, oStream);
    //            oStream.flush();
    //            oStream.close();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //            Log.i("TAG", "There was an issue saving the image.");
    //        }
    //        scanGallery(context, pictureFile.getAbsolutePath());
    //        return pictureFile;
    //    }
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

            return returnedBitmap;
        }

        //    public static Bitmap scaleBitmap(Bitmap bitmapToScale, float newWidth, float newHeight) {
    //        if(bitmapToScale == null)
    //            return null;
    ////get the original width and height
    //        int width = bitmapToScale.getWidth();
    //        int height = bitmapToScale.getHeight();
    //// create a matrix for the manipulation
    //        Matrix matrix = new Matrix();
    //
    //// resize the bit map
    //        matrix.postScale(newWidth / width, newHeight / height);
    //
    //// recreate the new Bitmap and set it back
    //        return Bitmap.createBitmap(bitmapToScale, 0, 0, bitmapToScale.getWidth(), bitmapToScale.getHeight(), matrix, true);  }
    ////    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
    ////        int width = bm.getWidth();
    ////        int height = bm.getHeight();
    ////        float scaleWidth = ((float) newWidth) / width;
    ////        float scaleHeight = ((float) newHeight) / height;
    ////        // CREATE A MATRIX FOR THE MANIPULATION
    ////        Matrix matrix = new Matrix();
    ////        // RESIZE THE BIT MAP
    ////        matrix.postScale(scaleWidth, scaleHeight);
    ////
    ////        // "RECREATE" THE NEW BITMAP
    ////        Bitmap resizedBitmap = Bitmap.createBitmap(
    ////                bm, 0, 0, width, height, matrix, false);
    ////        bm.recycle();
    ////        return resizedBitmap;
    ////    }
        private void scanGallery(Context cntx, String path) {
            try {
                MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        imagePath = path;
                        Log.d("path", path);
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
                    shareData(layout, mobileNumber);
                }
            }
            if (requestCode == PRINT_REQUEST_CODE) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        printOrder(order);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        }

        @Override
        public void onPrintClickListener(FullOrder order) throws EscPosConnectionException, EscPosEncodingException, EscPosBarcodeException, EscPosParserException {
            this.order = order;
            Log.d("blue", "print interface");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.BLUETOOTH)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.d("blue", "permissiongranted");


                            printOrder(order);





                } else {
                    ActivityCompat.requestPermissions(OrdersActivity.this, new String[]{Manifest.permission.BLUETOOTH}, PRINT_REQUEST_CODE);
                    Log.d("bluetoothPermissionNot", "permission not granted");
                }
            }
        }

        private void printOrder(FullOrder fullOrder) throws EscPosConnectionException, EscPosParserException, EscPosEncodingException, EscPosBarcodeException {


            Bitmap receipt = createClusterBitmap(fullOrder);

//            printImage(receipt);
//            Toast.makeText(this, "printed", Toast.LENGTH_SHORT).show();
//            AlertDialog.Builder alertadd = new AlertDialog.Builder(this);
//            LayoutInflater factory = LayoutInflater.from(this);
//            final View view = factory.inflate(R.layout.dialog_image, null);
//            ImageView receiptShow = view.findViewById(R.id.receipt);
//            receiptShow.setImageBitmap(receipt);
//            alertadd.setView(view);
//            alertadd.setNeutralButton("Here!", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dlg, int sumthin) {
//
//                }
//            });
//
//            alertadd.show();
//
            String[] permissions = {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            if (!hasPermissions(this, permissions)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL);
            } else {
//                   Your code HERE
//                EscPosPrinter printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 78f, 32);
//                printer
//                        .printFormattedText(
//                                "[C] allah akbar\n"
//                                +"\n\n\n"
//
//                        );

                //compress the bitmap
                Bitmap b1 =convertGreyImg(receipt);
                Bitmap b2=resizeImage(b1,576,false);
//                printpicCode(b2);

//                compress the bitmap
                Tiny.BitmapCompressOptions options = new Tiny.BitmapCompressOptions();
                Tiny.getInstance().source(b1).asBitmap().withOptions(options).compress(new BitmapCallback() {
                    @Override
                    public void callback(boolean isSuccess, Bitmap bitmap) {
                        if (isSuccess){
//                            Toast.makeText(PosActivity.this,"bitmap: "+bitmap.getByteCount(),Toast.LENGTH_LONG).show();


//                            Log.d("receipt",b2.toString());
                            printpicCode(bitmap);
                        }


                    }
                });
            }
//            // tell the user data were sent
//            Toast.makeText(this, "تم ارسال البيانات", Toast.LENGTH_SHORT).show();


        }
    private void printpicCode(final Bitmap printBmp){


        binder.writeDataByYouself(new UiExecute() {
            @Override
            public void onsucess() {
                Toast.makeText(OrdersActivity.this, "printed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onfailed() {
                Toast.makeText(OrdersActivity.this, "failed", Toast.LENGTH_SHORT).show();
            }
        }, new ProcessData() {
            @Override
            public List<byte[]> processDataBeforeSend() {
                List<byte[]> list=new ArrayList<>();
                list.add(DataForSendToPrinterPos80.initializePrinter());
                list.add(DataForSendToPrinterPos80.printRasterBmp(
                        0,printBmp, BitmapToByteData.BmpType.Threshold, BitmapToByteData.AlignType.Center,576));
                list.add(DataForSendToPrinterPos80.printAndFeedForward(3));
                list.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66,1));
                return list;
            }
        });




    }
    //    private void shareReceipt(Bitmap receipt) {
    //        File file = saveBitMap(OrdersActivity.this, receipt);    //which view you want to pass that view as parameter
    //        assert file != null;
    //        Uri imgUri = Uri.parse(file.getAbsolutePath());
    //        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
    //        whatsappIntent.setType("text/plain");
    //        whatsappIntent.setPackage("com.whatsapp");
    //        whatsappIntent.putExtra(Intent.EXTRA_TEXT,    "رقم تليفون العميل " + mobileNumber);
    //        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
    //        whatsappIntent.setType("image/png");
    //        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    //
    //        try {
    //            startActivity(whatsappIntent);
    //        } catch (android.content.ActivityNotFoundException ex) {
    //
    //        }
    //    }

    //    Bitmap getViewBitmap(View view)
    //    {
    //        //Get the dimensions of the view so we can re-layout the view at its current size
    //        //and create a bitmap of the same size
    //        int width = view.getWidth();
    //        int height = view.getHeight();
    //
    //        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
    //        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
    //
    //        //Cause the view to re-layout
    //        view.measure(measuredWidth, measuredHeight);
    //        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
    //        float totalDIP_X = layout.getWidth();
    //        float totalDIP_Y = layout.getHeight();
    //        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    //        Log.d("viewWidth",width+"");
    //        Log.d("viewHeight",height+"");
    //        //Create a bitmap backed Canvas to draw the view into
    //        Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
    //
    //        Canvas c = new Canvas(b);
    //        c.drawColor(Color.WHITE);
    //        //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
    //        view.draw(c);
    //
    //        return b;
    //    }


        //    private String getOrdersPrintText(List<OrderProduct> list) {
    //        String ordersPrint = "";
    //        for (OrderProduct order : list ){
    //            String productName = order.getProductName();
    //            String originalPrice = order.getOriginalPrice();
    //            String finalPrice  = order.getFinalPrice();
    //            float orderedAmount = order.getOrdered();
    //            String total = String.valueOf(orderedAmount * Float.parseFloat(finalPrice));
    //            ordersPrint +=
    //                    "[L]"+ total+"   [L]"+orderedAmount +"   [L]"+finalPrice+"   [L]"+originalPrice+"   [R]"+productName;
    //        }
    //        return ordersPrint;
    //    }
        public static boolean hasPermissions(Context context, String... permissions) {
            if (context != null && permissions != null) {
                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
            return true;
        }

        private Bitmap createClusterBitmap(FullOrder fullOrder) {
            View cluster = LayoutInflater.from(this).inflate(R.layout.order_print_layout,
                    null);
            final String customerName = fullOrder.getUsername();
            final String address = fullOrder.getAddress();
            final String mobileNumber = fullOrder.getMobilePhone();
            final String sum = fullOrder.getSum();
            final String discount = fullOrder.getDiscount();
            final String overAllDiscount = fullOrder.getOverAllDiscount();
            final String phoneNumber = fullOrder.getPhoneNumber();
            final String netCost = fullOrder.getTotalCost();
            final List<OrderProduct> list = fullOrder.getOrders();
            final String shipping = fullOrder.getShipping();
            String timeStamp = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss").format(Calendar.getInstance().getTime());


            TextView dateTimePrint = cluster.findViewById(R.id.date_time_print);
            TextView customerNamePrint = cluster.findViewById(R.id.customer_name_print);
            TextView addressPrint = cluster.findViewById(R.id.address_print);
            TextView mobilePrint = cluster.findViewById(R.id.mobile_print);
            TextView phonePrint = cluster.findViewById(R.id.phone_print);
            TextView sumPrint = cluster.findViewById(R.id.sum_print);
            TextView sumDiscountPrint = cluster.findViewById(R.id.sum_discount_print);
            TextView discountPrint = cluster.findViewById(R.id.discount_print);
            TextView shippingFeePrint = cluster.findViewById(R.id.shipping_fee_print);
            TextView totalPrint = cluster.findViewById(R.id.total_print);
            TextView comlaints_phone = cluster.findViewById(R.id.complaints_phone);
            RecyclerView orders = cluster.findViewById(R.id.order_products);
            attachRecyclerViewToAdapter(list,orders);


            dateTimePrint.setText(timeStamp);
            customerNamePrint.setText(customerName);
            addressPrint.setText(address);
            mobilePrint.setText(mobileNumber);
            if (phoneNumber.isEmpty()) phonePrint.setVisibility(View.GONE);
            else phonePrint.setText(phoneNumber);
            sumPrint.setText(String.valueOf(sum));
            sumDiscountPrint.setText(String.valueOf(overAllDiscount));
            discountPrint.setText(String.valueOf(discount));
            shippingFeePrint.setText(String.valueOf(shipping));
            totalPrint.setText(String.valueOf(netCost));
            dateTimePrint.setText(timeStamp);
            comlaints_phone.setText("01101515954");
            cluster.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            cluster.layout(0, 0, cluster.getMeasuredWidth(), cluster.getMeasuredHeight());


            final Bitmap clusterBitmap = Bitmap.createBitmap(cluster.getMeasuredWidth(),
                    cluster.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(clusterBitmap);
            canvas.drawColor(Color.WHITE);
            cluster.draw(canvas);

            return clusterBitmap;
        }


        private void attachRecyclerViewToAdapter(List<OrderProduct> list, RecyclerView orders) {
            printAdapter = new PrintOrderAdapter(list);
            orders.setAdapter(printAdapter);

        }

        void connectBluetooth(){
            binder.connectBtPort("DC:0D:30:87:94:EE", new UiExecute() {
                @Override
                public void onsucess() {
                    Log.d("deviceConnected","connected");
                    Toast.makeText(OrdersActivity.this, "تم الاتصال بالطابعة بنجاح", Toast.LENGTH_SHORT).show();
                    binder.write(DataForSendToPrinterPos80.openOrCloseAutoReturnPrintState(0x1f), new UiExecute() {
                        @Override
                        public void onsucess() {
                            binder.acceptdatafromprinter(new UiExecute() {
                                @Override
                                public void onsucess() {

                                }

                                @Override
                                public void onfailed() {

                                }
                            });
                        }

                        @Override
                        public void onfailed() {
                            Toast.makeText(OrdersActivity.this, "حدث خطأ في الاتصال بالطابعة", Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                @Override
                public void onfailed() {


                }
            });
        }


        public void printImage(Bitmap bitmap) {
            Bitmap b1 = convertGreyImg(bitmap);



            //compress the bitmap
            Tiny.BitmapCompressOptions options = new Tiny.BitmapCompressOptions();
            Tiny.getInstance().source(b1).asBitmap().withOptions(options).compress(new BitmapCallback() {
                @Override
                public void callback(boolean isSuccess, Bitmap bitmap) {
                    if (isSuccess) {
    //                            Toast.makeText(PosActivity.this,"bitmap: "+bitmap.getByteCount(),Toast.LENGTH_LONG).show();

                          Bitmap b2=resizeImage(bitmap,380,false);

                    }


                }
            });
    //                b2=resizeImage(b1,576,386,false);
//        }catch(Exception e){
//            e.printStackTrace();

//        }
    }

        public Bitmap convertGreyImg(Bitmap img) {
            int width = img.getWidth();
            int height = img.getHeight();

            int[] pixels = new int[width * height];

            img.getPixels(pixels, 0, width, 0, 0, width, height);


            //The arithmetic average of a grayscale image; a threshold
            double redSum=0,greenSum=0,blueSun=0;
            double total=width*height;

            for(int i = 0; i < height; i++)  {
                for(int j = 0; j < width; j++) {
                    int grey = pixels[width * i + j];

                    int red = ((grey  & 0x00FF0000 ) >> 16);
                    int green = ((grey & 0x0000FF00) >> 8);
                    int blue = (grey & 0x000000FF);



                    redSum+=red;
                    greenSum+=green;
                    blueSun+=blue;


                }
            }
            int m=(int) (redSum/total);

            //Conversion monochrome diagram
            for(int i = 0; i < height; i++)  {
                for(int j = 0; j < width; j++) {
                    int grey = pixels[width * i + j];

                    int alpha1 = 0xFF << 24;
                    int red = ((grey  & 0x00FF0000 ) >> 16);
                    int green = ((grey & 0x0000FF00) >> 8);
                    int blue = (grey & 0x000000FF);


                    if (red>=m) {
                        red=green=blue=255;
                    }else{
                        red=green=blue=0;
                    }
                    grey = alpha1 | (red << 16) | (green << 8) | blue;
                    pixels[width*i+j]=grey;


                }
            }
            Bitmap mBitmap=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);



            return mBitmap;
        }


        /*
            use the Matrix compress the bitmap
         *   */
        public static Bitmap resizeImage(Bitmap bitmap, int w,boolean ischecked)
        {

            Bitmap BitmapOrg = bitmap;
            Bitmap resizedBitmap = null;
            int width = BitmapOrg.getWidth();
            int height = BitmapOrg.getHeight();
            if (width<=w) {
                return bitmap;
            }
            if (!ischecked) {
                int newWidth = w;
                int newHeight = height*w/width;

                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;

                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                // if you want to rotate the Bitmap
                // matrix.postRotate(45);
                resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                        height, matrix, true);
            }else {
                resizedBitmap=Bitmap.createBitmap(BitmapOrg, 0, 0, w, height);
            }

            return resizedBitmap;
        }

}
