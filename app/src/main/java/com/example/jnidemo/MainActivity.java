package com.example.jnidemo;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public native String helloFromJNI();
    public native int    factorial(int n);
    public native String reverseString(String s);
    public native int    sumArray(int[] values);

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button   btnHello      = findViewById(R.id.btnHello);
        TextView tvHello       = findViewById(R.id.tvHello);

        EditText etFactInput   = findViewById(R.id.etFactInput);
        Button   btnFact       = findViewById(R.id.btnFact);
        TextView tvFact        = findViewById(R.id.tvFact);

        EditText etReverseInput = findViewById(R.id.etReverseInput);
        Button   btnReverse     = findViewById(R.id.btnReverse);
        TextView tvReverse      = findViewById(R.id.tvReverse);

        EditText etArrayInput  = findViewById(R.id.etArrayInput);
        Button   btnArray      = findViewById(R.id.btnArray);
        TextView tvArray       = findViewById(R.id.tvArray);

        btnHello.setOnClickListener(v ->
                tvHello.setText(helloFromJNI())
        );

        btnFact.setOnClickListener(v -> {
            String saisie = etFactInput.getText().toString().trim();
            if (TextUtils.isEmpty(saisie)) {
                Toast.makeText(this, "Entrez un entier", Toast.LENGTH_SHORT).show();
                return;
            }
            int n = Integer.parseInt(saisie);
            int res = factorial(n);
            if (res == -1) {
                tvFact.setText("Erreur : valeur negative non acceptee");
            } else if (res == -2) {
                tvFact.setText("Erreur : depassement pour n = " + n);
            } else {
                tvFact.setText("Factoriel de " + n + " = " + res);
            }
        });

        btnReverse.setOnClickListener(v -> {
            String saisie = etReverseInput.getText().toString();
            if (TextUtils.isEmpty(saisie)) {
                Toast.makeText(this, "Entrez un texte", Toast.LENGTH_SHORT).show();
                return;
            }
            tvReverse.setText("Texte inverse : " + reverseString(saisie));
        });

        btnArray.setOnClickListener(v -> {
            String saisie = etArrayInput.getText().toString().trim();
            if (TextUtils.isEmpty(saisie)) {
                Toast.makeText(this, "Entrez des entiers separes par des virgules", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                String[] parties = saisie.split(",");
                int[] nombres = new int[parties.length];
                for (int i = 0; i < parties.length; i++) {
                    nombres[i] = Integer.parseInt(parties[i].trim());
                }
                int somme = sumArray(nombres);
                if (somme < 0) {
                    tvArray.setText("Erreur : code = " + somme);
                } else {
                    tvArray.setText("Somme de " + nombres.length + " element(s) = " + somme);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Format invalide. Ex: 10, 20, 30", Toast.LENGTH_SHORT).show();
            }
        });
    }
}