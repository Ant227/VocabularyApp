package com.example.vocabularyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerUsername, registerGmail, registerPassword, registerConfirmPassword;
    private FirebaseAuth mAuth;
    private String currentUid;

    private RecyclerView recyclerView;
    private int[] images = {R.drawable.emoji1,R.drawable.emoji2,
                            R.drawable.emoji3,R.drawable.emoji4,
                            R.drawable.emoji5,R.drawable.emoji6,
                            R.drawable.emoji7};

    private CircularImageView circularImageView;

    private LinearLayoutManager linearLayoutManager;

    private RecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        registerUsername = findViewById(R.id.register_username);
        registerGmail = findViewById(R.id.register_gmail);
        registerPassword = findViewById(R.id.register_password);
        registerConfirmPassword = findViewById(R.id.register_confirm_password);

        recyclerView = findViewById(R.id.recyclerViewLogo);
        circularImageView = findViewById(R.id.selectedLogo);

        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter(images,circularImageView);

        recyclerView.setAdapter(adapter);
    }

    public void createAccount(View view){

        Calendar calendar = Calendar.getInstance();

        final  String date = DateFormat.getDateInstance(DateFormat.DEFAULT).format(calendar.getTime());
        final   String username = registerUsername.getText().toString();
        final  String email  = registerGmail.getText().toString();
        final  String password = registerPassword.getText().toString();
        final String confirm_password = registerConfirmPassword.getText().toString();
        final int selectedLogoLocation;

        selectedLogoLocation = adapter.returnSelectedLogo();

        if(       TextUtils.isEmpty(username)
                ||TextUtils.isEmpty(email)
                ||TextUtils.isEmpty(password)
                ||TextUtils.isEmpty(confirm_password)){
            Toast.makeText(this, "Please Enter all fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setTitle("Creating Account");
            progressDialog.setMessage("Please wait while account is being created");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference userRef = rootRef.child("users");


            if(password.equals(confirm_password))
            {
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful())
                                {

                                    HashMap<String, Object> userHashMap = new HashMap();
                                    userHashMap.put("name",username);
                                    userHashMap.put("gmail",email);
                                    userHashMap.put("date",date);
                                    userHashMap.put("profile",selectedLogoLocation);
                                    userHashMap.put("book_status","none");

                                    currentUid = mAuth.getUid();
                                    userRef.child(currentUid).updateChildren(userHashMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        progressDialog.dismiss();
                                                        sendUserToMainActivity();
                                                        Toast.makeText(RegisterActivity.this,
                                                                "Account has been created successfully!",
                                                                Toast.LENGTH_SHORT).show();




                                                    }
                                                    else
                                                    {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(RegisterActivity.this,
                                                                "Error: "+ task.getException().toString(),

                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                                else
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this,
                                            "Error: "+task.getException().toString(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else
            {
                progressDialog.dismiss();
                Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
            }



        }
    }

    private void sendUserToMainActivity() {
        Intent intentMain = new Intent(RegisterActivity.this, MainActivity.class);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentMain);
        finish();
    }

    public void logoChoose(View view) {
    }
}
