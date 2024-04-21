package com.example.entrevistasapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.entrevistasapp.config.entrevista;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText fechaEntrevista, descripcionEdtx, periodistaEdtx;
    static final int peticion_camara = 100;
    static final int peticion_foto = 102;
    private ImageView fotoEntrevistado;
    private static final int REQUEST_PERMISSIONS = 2;

    private Button btnTomarPotho;
    private Boolean estado = false;
    private MediaRecorder mediaRecorder;
    private String outputFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fechaEntrevista = findViewById(R.id.textDate);
        fotoEntrevistado = (ImageView) findViewById(R.id.imgEntrevistado);
        btnTomarPotho = findViewById(R.id.btnTomarFoto);
        descripcionEdtx = findViewById(R.id.editTextD);
        periodistaEdtx = findViewById(R.id.editTextP);


        //----- Listener para llamar al método de fecha -----
        fechaEntrevista.setOnClickListener(view -> mostrarCalendario());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");


        btnTomarPotho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Permisos();
            }
        });





        // Configurar ruta de salida para el archivo de audio
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/grabacion.3gp";
    }


    //----Métodos-----
    //----- Método para mostrar el selector de fecha -----
    private void mostrarCalendario() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fechaEntrevista.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setSpinnersShown(true);
        datePicker.show();
    }; //Fin metodo calendario--------




    private void Permisos ()
    {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    peticion_camara);
        }
        else
        {
            tomarfoto();
        }
    }

    private void tomarfoto ()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intent, peticion_foto);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
    int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == peticion_camara)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                tomarfoto();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Permiso denegado", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == peticion_foto && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imagen = (Bitmap) extras.get("data");
            fotoEntrevistado.setImageBitmap(imagen);

        }
    }

    //Metodos para grabar
    public void iniciarGrabacion(View view) {

        String[] permisosA = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Boolean cicloPermisos = true;

        for (String permiso : permisosA){
            if(ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED){
                cicloPermisos = false;
                break;
            }
        }
        if (cicloPermisos) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                if (estado != false) {
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mediaRecorder.setOutputFile(outputFile);

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        Toast.makeText(getApplicationContext(), "Grabando...", Toast.LENGTH_SHORT).show();
                        estado = true;
                    } catch (IOException e) {

                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error al iniciar la grabación" + e, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                ActivityCompat.requestPermissions(this, permisosA, REQUEST_PERMISSIONS);
            }
        }



    }


    public void detenerGrabacion(View view) {
        try {
            if (estado != false) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                Toast.makeText(getApplicationContext(), "Grabación finalizada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "No hay grabación para detener", Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error al detener la grabación", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }



}
