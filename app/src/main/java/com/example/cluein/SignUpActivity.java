package com.example.cluein;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

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

//        /*Creating instances in order to access the attributes of the existing checkboxes*/
//        CheckBox checkLength = findViewById(R.id.chckBoxEightChars);
//        CheckBox checkNumber = findViewById(R.id.chckBoxNumbers);
//        CheckBox checkLowerCase = findViewById(R.id.chckBoxLowerCaseChar);
//        CheckBox checkUpperCase = findViewById(R.id.chckBoxUpperCaseChar);
//        CheckBox checkSpecialChar = findViewById(R.id.chckBoxSpecialChar);

        /*Instance for the textInput(Password)  */
        LinearLayout validationLayout = findViewById(R.id.validationLayout);
        EditText passwordInput = findViewById(R.id.txtInptPassword);
        /*Password layout instance*/
       TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);

        /*Instance for imageview and textview*/
        ImageView iconLength = findViewById(R.id.iconLength);
        TextView textLength = findViewById(R.id.chckBoxEightChars);

        ImageView iconNum = findViewById(R.id.iconAtLeastNum);
        TextView textNum = findViewById(R.id.chckBoxNumbers);

        ImageView iconLowerCase = findViewById(R.id.iconLower);
        TextView textLowerCase = findViewById(R.id.chckBoxLowerCaseChar);

        ImageView iconUpperCase = findViewById(R.id.iconUpper);
        TextView textUpperCase = findViewById(R.id.chckBoxUpperCaseChar);

        ImageView iconSpecialChar = findViewById(R.id.iconSpecialChar);
        TextView textSpecialChar = findViewById(R.id.chckBoxSpecialChar);



        /*Instance for the button*/
        Button btnSignUp = findViewById(R.id.btnSignUp);

        /*Password only appears when users focuses on the inputbox  */
        passwordInput.setOnFocusChangeListener((v, hasFocus) ->{
            if(hasFocus){
                validationLayout.setVisibility((View.VISIBLE));
            }else{
                validationLayout.setVisibility(View.GONE);
            }
        });
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

                    iconLength.setImageResource(R.drawable.check);
                    textLength.setTextColor(Color.GREEN);

                }else{
                    iconLength.setImageResource(R.drawable.multiplication);
                    textLength.setTextColor(Color.RED);
                    isValid = false;
                }

                /*Rule 2: Has atleast a number*/
                if(pass.matches(".*[0-9].*")){

                    iconNum.setImageResource(R.drawable.check);
                    textNum.setTextColor(Color.GREEN);
                }else{
                    iconNum.setImageResource(R.drawable.multiplication);
                    textNum.setTextColor(Color.RED);
                    isValid = false;
                }

                /*Rule 3: Has atleast one lowercase*/
                if(pass.matches(".*[a-z].*")){
                    iconLowerCase.setImageResource(R.drawable.check);
                    textLowerCase.setTextColor(Color.GREEN);
                }else{
                    iconLowerCase.setImageResource(R.drawable.multiplication);
                    textLowerCase.setTextColor(Color.RED);
                    isValid = false;
                }

                /*Rule 4: has atleast one uppercase*/
                if(pass.matches(".*[A-Z].*")){
                   iconUpperCase.setImageResource(R.drawable.check);
                    textUpperCase.setTextColor(Color.GREEN);
                }else{
                    iconUpperCase.setImageResource(R.drawable.multiplication);
                    textUpperCase.setTextColor(Color.RED);
                    isValid = false;
                }

                /*Rule 5: has atleast one special character   */
                if(pass.matches(".*[@#$%^&*+=!].*")){
                   iconSpecialChar.setImageResource(R.drawable.check);
                   textSpecialChar.setTextColor(Color.GREEN);
                }else{
                    iconSpecialChar.setImageResource(R.drawable.multiplication);
                    textSpecialChar.setTextColor(Color.RED);
                    isValid = false;
                }


                if(!isValid){
                    validationLayout.setVisibility(View.VISIBLE);
                    passwordLayout.setError("Password is weak");
                }else{
                    validationLayout.setVisibility(View.GONE);
                }

            }
        });
    }


}