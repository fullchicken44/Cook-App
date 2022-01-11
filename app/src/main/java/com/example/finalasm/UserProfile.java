package com.example.finalasm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserProfile extends AppCompatActivity {
    DatabaseReference mealDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("meal");
    DatabaseReference userDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user").child("users");
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private RecyclerView rcvCategory;
    private CategoryAdapter categoryAdapter;
    private TextView name_user;
    ImageButton nav_menu, nav_search, nav_user, post;
    ImageView image_user;
    List<Meal> mainMealList = new ArrayList<>();
    List<User> mainUserList = new ArrayList<>();
    FirebaseDB provider = new FirebaseDB();
    User user;
    User currentUser;
    Meal meal;
    List<Meal> savedMeal = new ArrayList<>();
    List<Meal> createMeal = new ArrayList<>();
    private static final int IMAGE_REQUEST = 2;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        name_user = findViewById(R.id.name_user);
        nav_menu = findViewById(R.id.nav_menu);
        nav_search = findViewById(R.id.nav_search);
        nav_user = findViewById(R.id.nav_user);
        image_user = findViewById(R.id.image_user);
        post = findViewById(R.id.post_user);
        rcvCategory = findViewById(R.id.recycler_user);
        categoryAdapter = new CategoryAdapter(MealAdapter.VERTICAL_REMOVE, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvCategory.setLayoutManager(linearLayoutManager);


        rcvCategory.setAdapter(categoryAdapter);

        provider.fetchAllUser(userDb, userList -> {
            for (int i = 0; i < userList.size(); i++) {
                user = (User) userList.get(i);
                mainUserList.add(user);
            }
            for (User user : mainUserList) {
                if (firebaseUser.getEmail().equals(user.getUserEmail())) {
                    currentUser = user;
                    String nameBreakDown = user.getUserName();
                    String[] nameParts = nameBreakDown.split(" ");
                    name_user.setText(nameParts[0].trim());
                }
            }
            provider.fetchAllMeal(mealDb, mealList -> {
                for (int i = 0; i < mealList.size(); i++) {
                    meal = (Meal) mealList.get(i);
                    mainMealList.add(meal);
                    for (int j = 0; j < currentUser.getCollection().size(); j++) {
                        if (Long.parseLong(currentUser.getCollection().get(j)) == i) {
                            savedMeal.add(mainMealList.get(i));
                        }
                    }
                    for (int z = 0; z < currentUser.getMealCreate().size(); z++) {
                        if (Long.parseLong(currentUser.getMealCreate().get(z)) == i) {
                            createMeal.add(mainMealList.get(i));
                        }
                    }
                }
                categoryAdapter.setData(getListCategory());
            });
        });

        post.setOnClickListener(v->{
            Intent i = new Intent(UserProfile.this,PostMealPage.class);
            startActivityForResult(i,170);
            System.out.println("start post activity");
        });

        image_user.setOnClickListener(v -> {
            //openImage();
            AlertDialog alert = new AlertDialog.Builder(UserProfile.this).create();
            // alert.setView(mealObjImage);, causing crash, fix later
            alert.setTitle("Change profile picture");
            alert.setMessage("Click upload to upload a new image!");
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Upload",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            openImage();
                        }
                    });

            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alert.dismiss();
                        }
                    });

            alert.show();
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent , IMAGE_REQUEST);
    }

    private List<Category> getListCategory(){
        List<Category> listCategory = new ArrayList<>();
        listCategory.add(new Category("Saved Collection", savedMeal));
        listCategory.add(new Category("Created Collection", createMeal));
        return listCategory;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();

                            Log.d("DownloadUrl", url);

                            pd.dismiss();
                            Toast.makeText(UserProfile.this, "Image upload successful1", Toast.LENGTH_SHORT).show();

                            // After upload image then fetch into the profile picture
                            Picasso.get()
                                    .load(url)
                                    .centerCrop()
                                    .fit()
                                    .into(image_user);
                        }
                    });
                }
            });
        }
    }

    private String getFileExtension (Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}