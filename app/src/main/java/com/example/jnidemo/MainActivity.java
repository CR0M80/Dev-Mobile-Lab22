package com.example.jnidemo;

import android.os.Bundle;
import android.widget.TextView;
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

        TextView tvHello   = findViewById(R.id.tvHello);
        TextView tvFact    = findViewById(R.id.tvFact);
        TextView tvReverse = findViewById(R.id.tvReverse);
        TextView tvArray   = findViewById(R.id.tvArray);

        tvHello.setText(helloFromJNI());

        int fact = factorial(10);
        tvFact.setText(fact >= 0
                ? "Factoriel de 10 = " + fact
                : "Erreur factoriel, code = " + fact);

        tvReverse.setText("Texte inverse : " + reverseString("JNI is powerful!"));

        int[] nombres = {10, 20, 30, 40, 50};
        tvArray.setText("Somme du tableau = " + sumArray(nombres));
    }
}