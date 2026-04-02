package com.example.cluein;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* Storing the checkboxes idies for later uses */
        boolean boolValidInpt = true;
        EditText password = findViewById(R.id.txtInptPassword);

        /*Creating instances in order to access the attributes of the existing checkboxes*/
        CheckBox checkLength = findViewById(R.id.chckBoxEightChars);
        CheckBox checkNumber = findViewById(R.id.chckBoxNumbers);
        CheckBox checkLowerCase = findViewById(R.id.chckBoxLowerCaseChar);
        CheckBox checkUpperCase = findViewById(R.id.chckBoxUpperCaseChar);
        CheckBox checkSpecialChar = findViewById(R.id.chckBoxSpecialChar);

        /*Instance for the button*/
        Button btnSignUp = findViewById(R.id.btnSignUp);


        /*Validation of  the password  */

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
//            Runs while the user is typing
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = password.getText().toString();

                /* Rule 1: Length*/
                boolean isValid = true;

                if(pass.length() >= 8){

                    checkLength.setChecked(true);
                    checkLength.setTextColor(Color.GREEN);

                }else{
                    checkLength.setChecked(false);
                    checkLength.setTextColor(Color.RED);
                    isValid = false;
                }

                /*Rule 2: Has atleast a number*/
                if(pass.matches(".*[0-9].*")){

                    checkNumber.setChecked(true);
                    checkNumber.setTextColor(Color.GREEN);
                }else{
                    checkNumber.setChecked(false);
                    checkNumber.setTextColor(Color.RED);
                    isValid = false;
                }

                /*Rule 3: Has atleast one lowercase*/
                if(pass.matches(".*[a-z].*")){
                    checkLowerCase.setChecked(true);
                    checkLowerCase.setTextColor(Color.GREEN);
                }else{
                    checkLowerCase.setChecked(false);
                    checkLowerCase.setTextColor(Color.RED);
                    isValid = false;
                }

                /*Rule 4: has atleast one uppercase*/
                if(pass.matches(".*[A-Z].*")){
                    checkUpperCase.setChecked(true);
                    checkUpperCase.setTextColor(Color.GREEN);
                }else{
                    checkUpperCase.setChecked(false);
                    checkUpperCase.setTextColor(Color.RED);
                    isValid = false;
                }

                /*Rule 5: has atleast one special character   */
                if(pass.matches(".*[@#$%^&*+=!].*")){
                    checkSpecialChar.setChecked(true);
                    checkSpecialChar.setTextColor(Color.GREEN);
                }else{
                    checkSpecialChar.setChecked(false);
                    checkSpecialChar.setTextColor(Color.RED);
                    isValid = false;
                }


            }
        });
    }


}