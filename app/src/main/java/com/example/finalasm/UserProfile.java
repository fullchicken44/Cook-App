package com.example.finalasm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    int userKey;
    User currentUser;
    Meal meal;
    Intent intent;
    List<Meal> savedMeal = new ArrayList<>();
    List<Meal> createMeal = new ArrayList<>();
    private static final int IMAGE_REQUEST = 2;
    private Uri imageUri;
    int key = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

//        Intent startIntent = getIntent();
//        startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        name_user = findViewById(R.id.name_user);
        nav_menu = findViewById(R.id.nav_menu);
        nav_search = findViewById(R.id.nav_search);
        nav_user = findViewById(R.id.nav_user);
        image_user = findViewById(R.id.image_user);
        post = findViewById(R.id.post_user);
        rcvCategory = findViewById(R.id.recycler_user);
        categoryAdapter = new CategoryAdapter(this, MealAdapter.VERTICAL);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvCategory.setLayoutManager(linearLayoutManager);

        name_user.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
            builder.setTitle("Do you want to log out?");
            builder.setMessage("").setPositiveButton("Confirm",(dialog, which) -> {
                firebaseAuth.signOut();
                intent = new Intent(UserProfile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }).setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.btn_star_big_on)
                    .show();
        });

        nav_menu.setOnClickListener(v -> {
            intent = new Intent(UserProfile.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        nav_search.setOnClickListener(v -> {
            intent = new Intent(UserProfile.this, SearchActivity.class);
            startActivity(intent);
            finish();
        });

        provider.fetchAllUser(userDb, userList -> {
            userKey = 0;
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
                    userKey ++;
                    break;
                }

                //Chỗ này để get index của key để lát nữa biết chỗ để update link ảnh lên db
                key++;
            }
            provider.fetchAllMeal(mealDb, mealList -> {
                savedMeal = new ArrayList<>();
                for (int i = 0; i < mealList.size(); i++) {
                    meal = (Meal) mealList.get(i);
                    mainMealList.add(meal);
                    for (int j = 0; j < currentUser.getCollection().size(); j++) {
                        if (!currentUser.getCollection().get(j).isEmpty()
                                && Long.parseLong(currentUser.getCollection().get(j)) == i
                                && !savedMeal.contains(meal)){
                            savedMeal.add(mainMealList.get(i));
                        }
                    }
                    for (int z = 0; z < currentUser.getMealCreate().size(); z++) {
                        if (!currentUser.getMealCreate().get(z).isEmpty() && Long.parseLong(currentUser.getMealCreate().get(z)) == i ) {
                            createMeal.add(mainMealList.get(i));
                        }
                    }
                }
//                List<Category> listCategory = new ArrayList<>();
//                listCategory.add(new Category("Saved Collection", savedMeal));
//                categoryAdapter = new CollectionAdapter(this, savedMeal, new CollectionAdapter.IClickListener() {
//                    @Override
//                    public void onClickDeleteItem(Meal meal, int position) {
//                        currentUser.getCollection().remove(position);
//                        userDb.child(String.valueOf(userKey)).setValue(currentUser);
//                    }});
                categoryAdapter.setData(getListCategory());
                rcvCategory.setAdapter(categoryAdapter);
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
            alert.setTitle("Change profile picture");
            alert.setMessage("Click upload to upload a new image!");
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Upload",
                    (dialog, which) -> openImage());

            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    (dialog, which) -> alert.dismiss());

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
            assert data != null;
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

            fileRef.putFile(imageUri).addOnCompleteListener(task -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();

                Log.d("DownloadUrl", url);

                pd.dismiss();
                Toast.makeText(UserProfile.this, "Image upload successful1", Toast.LENGTH_SHORT).show();

                // Url tạo xong r update lên userDB, key là index của user
                currentUser.setUrlProfile(url);
                userDb.child(String.valueOf(key)).child("urlProfile").setValue(url);


                // After upload image then fetch into the profile picture
                Picasso.get()
                        .load(url)
                        .centerCrop()
                        .fit()
                        .into(image_user);
            }));
        }
    }

    private String getFileExtension (Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}