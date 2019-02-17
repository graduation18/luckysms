package com.example.gaber.luckysms.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.example.gaber.luckysms.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.poovam.pinedittextfield.LinePinField;
import com.poovam.pinedittextfield.PinField;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Created by gaber on 11/30/2018.
 */

public class confirm_code extends AppCompatActivity {
    FirebaseAuth auth;
    String vervication_id,phone;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    LinePinField linePinField;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.confirm_code);
        auth=FirebaseAuth.getInstance();
        linePinField = findViewById(R.id.lineField);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                String code=credential.getSmsCode();
                linePinField.setText(code);
                signInWithPhoneAuthCredential(credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String mverificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                                vervication_id = mverificationId;

            }
        };
        linePinField.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete (@NotNull String enteredText) {
                sign_in(vervication_id,linePinField.getText().toString());
                return true; // Return true to keep the keyboard open else return false to close the keyboard
            }
        });
        phone=getIntent().getStringExtra("phone_number");
        get_verfiy_code(phone);


    }
    private void get_verfiy_code(String phone_number)
    {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);// OnVerificationStateChangedCallbacks

    }
    private void sign_in(String vervication_id,String code)
    {
        PhoneAuthCredential authCredential= PhoneAuthProvider.getCredential(vervication_id,code);
        signInWithPhoneAuthCredential(authCredential);
    }
    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential)
    {

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            getSharedPreferences("logged_in",MODE_PRIVATE).edit()
                                    .putBoolean("state",true)
                                    .putString("phone",phone)
                                    .commit();

                            Intent intent=new Intent(confirm_code.this,MainActivity.class);
                            startActivity(intent);
                            finish();

                        }else {
                            Log.w("khgj",task.getException());

                            Toast.makeText(confirm_code.this,"Login unsuccessful ", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }




}
