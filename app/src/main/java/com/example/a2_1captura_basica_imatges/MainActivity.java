package com.example.a2_1captura_basica_imatges;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.provider.MediaStore;
import android.view.View;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText edittLoadDialog;
    ImageView imgv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edittLoadDialog = new EditText(this);

        Button btPhoto = findViewById(R.id.btPhoto);
        btPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                } catch (ActivityNotFoundException e) {
                    System.out.println("Error");
                }
            }
        });


        Button btLoad = findViewById(R.id.btLoad);
        btLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap captureImage = (Bitmap)data.getExtras().get("data");

            imgv = findViewById(R.id.imgView);
            imgv.setImageBitmap(captureImage);
            saveImage(captureImage, "foto", 0);
        }
    }
//test for commit

    private AlertDialog makeDialog(String title, String message, EditText et, String cancelMessage) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setView(et);

        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (cancelMessage != null)
                {
                    Toast.makeText(getApplicationContext(),cancelMessage, Toast.LENGTH_LONG).show();
                }

            }
        });

        AlertDialog ad = adb.create();
        ad.setCanceledOnTouchOutside(false);
        return ad;

    }

    private void showLoadDialog()
    {
        edittLoadDialog = new EditText(this);
        AlertDialog alertd = makeDialog("Cargar Foto", "Nombre de la foto: ", edittLoadDialog,null);
        alertd.show();

        alertd.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value =  edittLoadDialog.getText().toString();
                    try {
                        loadImage(value);
                        Toast.makeText(getApplicationContext(), "Foto cargada con el nombre " + value, Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "ERROR: la imagen no existe",Toast.LENGTH_LONG).show();
                    }
                    alertd.dismiss();
            }
        });
    }

    public void saveImage(Bitmap bp, String name, int i)
    {

        File imageFile = new File(getApplicationContext().getFilesDir(), name + i + ".png");
        if (!imageFile.exists())
        {
            try {
                FileOutputStream fos = new FileOutputStream(imageFile);
                bp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                Toast.makeText(getApplicationContext(), "Foto guardada con el nombre " + name + i, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "ERROR: No se ha podido guardar la foto", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            i++;
            saveImage(bp, name, i);
        }

    }

    private void loadImage(String imageName) throws FileNotFoundException {
        imgv = findViewById(R.id.imgView);
        imgv.setImageBitmap(getBitmap(imageName + ".png", getApplicationContext().getFilesDir()));
    }

    private Bitmap getBitmap(String filename, File dirPath) throws FileNotFoundException {
        File bitFile = new File(dirPath.getAbsolutePath(), filename);
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(bitFile));
        return bitmap;
    }

}