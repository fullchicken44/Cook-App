//package com.example.finalasm;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatButton;
//
//import android.annotation.SuppressLint;
//import android.app.ProgressDialog;
//import android.content.ContentResolver;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.webkit.MimeTypeMap;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import org.w3c.dom.Text;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class PostMealPage extends AppCompatActivity {
//    FirebaseDB firebaseHandler = new FirebaseDB();
//    Meal meal = new Meal();
//    AppCompatButton finishButton;
//    private int key = 0;
//
//    private final String dbAPI = "https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app";
//    DatabaseReference mealDbs = FirebaseDatabase.getInstance(dbAPI).getReference("meal");
//
//    private Uri imageUri;
//    private static final  int IMAGE_REQUEST = 2;
//
//
//    @SuppressLint("WrongViewCast")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_post_meal_page);
//        ImageButton imageAdd = findViewById(R.id.button_image_post_meal);
//        finishButton = (AppCompatButton) findViewById(R.id.button_finish_post_meal);
//
//        ImageButton btnBack = (ImageButton) findViewById(R.id.back_post_meal);
//        btnBack.setOnClickListener(v->{
//            Intent intent = new Intent(PostMealPage.this,UserProfile.class);
//            startActivity(intent);
//            finish();
//        });
//
//        imageAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openImage();
//            }
//        });
//
//        finishButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TextView mealName = findViewById(R.id.input_name_post_meal);
//                TextView mealCategories = findViewById(R.id.input_category_post_meal);
//                TextView mealTag = findViewById(R.id.input_tag_post_meal);
//                TextView mealArea = findViewById(R.id.input_area_post_meal);
//                TextView strDrinkAlternate = findViewById(R.id.input_drink_post_meal);
//                TextView mealYoutubeURL = findViewById(R.id.input_youtube_post_meal);
//                TextView strIngredient = findViewById(R.id.input_ingredient_post_meal);
//                String stringIngredient = strIngredient.getText().toString();
//                TextView strMeasurePost = findViewById(R.id.input_measure_post_meal);
//                String stringMeasure = strMeasurePost.getText().toString();
//
//                if (mealName.getText().toString().equals("") || mealCategories.getText().toString().equals("") || mealTag.getText().toString().equals("") ||
//                        mealArea.getText().toString().equals("") || strDrinkAlternate.getText().toString().equals("") || mealYoutubeURL.getText().toString().equals("") ||
//                        strIngredient.getText().toString().equals("") || strMeasurePost.getText().toString().equals("") || stringMeasure.equals("")) {
//                    Toast.makeText(PostMealPage.this, "Please fill out all the fields", Toast.LENGTH_LONG).show();
//                } else {
//                    ingredientHandler(stringIngredient);
//                    measureHandler(stringMeasure);
//                    meal.setStrMeal(mealName.getText().toString());
//                    meal.setStrCategory(mealCategories.getText().toString());
//                    meal.setStrTags(mealTag.getText().toString());
//                    meal.setStrArea(mealArea.getText().toString());
//                    meal.setStrDrinkAlternate(strDrinkAlternate.getText().toString());
//                    meal.setStrYoutube(mealYoutubeURL.getText().toString());
//                }
//
//                firebaseHandler.fetchAllMeal(mealDbs, mealList -> {
//                    for (int i =0; i < mealList.size();i++) {
//                        key++;
//                    }
//                });
//
//                mealDbs.child(String.valueOf(key)).setValue(meal);
//                Log.d("TAG", "Size: " + key);
//
//                finish();
//
//
//                Log.d("TAG", "meal user post trong day" + meal.toString());
//            }
//        });
//
//    }
//
//    private void openImage() {
//        Intent intent = new Intent();
//        intent.setType("image/");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent , IMAGE_REQUEST);
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){
//            imageUri = data.getData();
//
//            uploadImage();
//        }
//
////        finishButton.setOnClickListener(v -> onClickPushData());
//    }
//
//    private String getFileExtension (Uri uri){
//        ContentResolver contentResolver = getContentResolver();
//
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//
//        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
//    }
//
//    private void uploadImage() {
//
//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("Uploading");
//        pd.show();
//
//        if (imageUri != null) {
//            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
//
//            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            String url = uri.toString();
//
//
//                            Log.d("DownloadUrl", url);
//
//                            meal.setStrMealThumb(url);
//
//                            pd.dismiss();
//                            Toast.makeText(PostMealPage.this, "Image upload successful1", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
//        }
//    }
//
//    // Post on meal unofficial
//    private void onClickPushData() {
//        TextView mealName = findViewById(R.id.input_name_post_meal);
//        meal.setStrMeal(mealName.getText().toString());
//
//        TextView mealCategories = findViewById(R.id.input_category_post_meal);
//        meal.setStrCategory(mealCategories.getText().toString());
//
//        TextView mealTag = findViewById(R.id.input_tag_post_meal);
//        meal.setStrTags(mealTag.getText().toString());
//
//        TextView mealArea = findViewById(R.id.input_area_post_meal);
//        meal.setStrArea(mealArea.getText().toString());
//
//        TextView strDrinkAlternate = findViewById(R.id.input_drink_post_meal);
//        meal.setStrDrinkAlternate(strDrinkAlternate.getText().toString());
//
//        TextView mealYoutubeURL = findViewById(R.id.input_youtube_post_meal);
//        meal.setStrYoutube(mealYoutubeURL.getText().toString());
//
//        TextView strIngredient = findViewById(R.id.input_ingredient_post_meal);
//        String stringIngredient = strIngredient.getText().toString();
//        ingredientHandler(stringIngredient);
//
//        TextView strMeasurePost = findViewById(R.id.input_measure_post_meal);
//        String stringMeasure = strMeasurePost.getText().toString();
//        measureHandler(stringMeasure);
//
//        firebaseHandler.fetchAllMeal(mealDbs, mealList -> {
//            mealDbs.child(String.valueOf(mealList.size())).setValue(meal);
//        });
//        //Meal meal = new Meal("123456", "Area51", "A", "Name", "B");
//
//    }
//
//    public void ingredientHandler(String str) {
//        String[] arrOfStr = str.split(",");
//        List<String> ingredientList = new ArrayList<String>(Arrays.asList(arrOfStr));
//        for (int i = arrOfStr.length; i < 20; i++) {
//            ingredientList.add("");
//        }
//
//        meal.setStrIngredient1(ingredientList.get(0));
//        meal.setStrIngredient2(ingredientList.get(1));
//        meal.setStrIngredient3(ingredientList.get(2));
//        meal.setStrIngredient4(ingredientList.get(3));
//        meal.setStrIngredient5(ingredientList.get(4));
//        meal.setStrIngredient6(ingredientList.get(5));
//        meal.setStrIngredient7(ingredientList.get(6));
//        meal.setStrIngredient8(ingredientList.get(7));
//        meal.setStrIngredient9(ingredientList.get(8));
//        meal.setStrIngredient10(ingredientList.get(9));
//        meal.setStrIngredient11(ingredientList.get(10));
//        meal.setStrIngredient12(ingredientList.get(11));
//        meal.setStrIngredient13(ingredientList.get(12));
//        meal.setStrIngredient14(ingredientList.get(13));
//        meal.setStrIngredient15(ingredientList.get(14));
//        meal.setStrIngredient16(ingredientList.get(15));
//        meal.setStrIngredient17(ingredientList.get(16));
//        meal.setStrIngredient18(ingredientList.get(17));
//        meal.setStrIngredient19(ingredientList.get(18));
//        meal.setStrIngredient20(ingredientList.get(19));
//
//    }
//
//    public void measureHandler(String str) {
//        List<String> measureList = new ArrayList<String>();
//        String[] arrOfStr = str.split(",");
//        for (int i = 0; i < arrOfStr.length; i++) {
//            measureList.add(arrOfStr[i]);
//        }
//        for (int i = arrOfStr.length; i < 20; i++) {
//            measureList.add("");
//        }
//
//        meal.setStrMeasure1(measureList.get(0));
//        meal.setStrMeasure2(measureList.get(1));
//        meal.setStrMeasure3(measureList.get(2));
//        meal.setStrMeasure4(measureList.get(3));
//        meal.setStrMeasure5(measureList.get(4));
//        meal.setStrMeasure6(measureList.get(5));
//        meal.setStrMeasure7(measureList.get(6));
//        meal.setStrMeasure8(measureList.get(7));
//        meal.setStrMeasure9(measureList.get(8));
//        meal.setStrMeasure10(measureList.get(9));
//        meal.setStrMeasure11(measureList.get(10));
//        meal.setStrMeasure12(measureList.get(11));
//        meal.setStrMeasure13(measureList.get(12));
//        meal.setStrMeasure14(measureList.get(13));
//        meal.setStrMeasure15(measureList.get(14));
//        meal.setStrMeasure16(measureList.get(15));
//        meal.setStrMeasure17(measureList.get(16));
//        meal.setStrMeasure18(measureList.get(17));
//        meal.setStrMeasure19(measureList.get(18));
//        meal.setStrMeasure20(measureList.get(19));
//    }
//
//    public int getRandomNumber(int min) {
//        int max = 10000;
//        return (int) ((Math.random() * (max - min)) + min);
//    }
//
//    public interface firebaseCallback {
//        void call(List list);
//    }
//}
package com.example.finalasm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PostMealPage extends AppCompatActivity {
    FirebaseDB firebaseHandler = new FirebaseDB();
    Meal meal = new Meal();
    Meal tempMeal;
    AppCompatButton finishButton;
    int dbKey;
    List<Meal> tempList = new ArrayList<Meal>();
    //    private int key = 0;
    String key;
    // db của Quân
//    private final String dbAPI = "https://android-2a378-default-rtdb.asia-southeast1.firebasedatabase.app/";
    // db của Phong
    private final String dbAPI = "https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app";
    DatabaseReference mealDbs = FirebaseDatabase.getInstance(dbAPI).getReference("meal");

    private Uri imageUri;
    private static final  int IMAGE_REQUEST = 2;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_meal_page);
        ImageButton imageAdd = findViewById(R.id.button_image_post_meal);
        finishButton = (AppCompatButton) findViewById(R.id.button_finish_post_meal);

        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView mealName = findViewById(R.id.input_name_post_meal);
                TextView mealCategories = findViewById(R.id.input_category_post_meal);
                TextView mealTag = findViewById(R.id.input_tag_post_meal);
                TextView mealArea = findViewById(R.id.input_area_post_meal);
                TextView strDrinkAlternate = findViewById(R.id.input_drink_post_meal);
                TextView mealYoutubeURL = findViewById(R.id.input_youtube_post_meal);
                TextView strIngredient = findViewById(R.id.input_ingredient_post_meal);
                String stringIngredient = strIngredient.getText().toString();
                TextView strMeasurePost = findViewById(R.id.input_measure_post_meal);
                String stringMeasure = strMeasurePost.getText().toString();

                if (mealName.getText().toString().equals("") || mealCategories.getText().toString().equals("") || mealTag.getText().toString().equals("") ||
                        mealArea.getText().toString().equals("") || strDrinkAlternate.getText().toString().equals("") || mealYoutubeURL.getText().toString().equals("") ||
                        strIngredient.getText().toString().equals("") || strMeasurePost.getText().toString().equals("") || stringMeasure.equals("")) {
                    Toast.makeText(PostMealPage.this, "Please fill out all the fields", Toast.LENGTH_LONG).show();
                } else {
                    ingredientHandler(stringIngredient);
                    measureHandler(stringMeasure);
                    meal.setStrMeal(mealName.getText().toString());
                    meal.setStrCategory(mealCategories.getText().toString());
                    meal.setStrTags(mealTag.getText().toString());
                    meal.setStrArea(mealArea.getText().toString());
                    meal.setStrDrinkAlternate(strDrinkAlternate.getText().toString());
                    meal.setStrYoutube(mealYoutubeURL.getText().toString());
                    meal.setIdMeal(String.valueOf(getRandomNumber(50000)));
                }

                readCountDb(mealDbs, dbCount -> {
                    Log.d("TAG", String.valueOf(dbCount));
                    mealDbs.child(String.valueOf(dbCount)).setValue(meal);
                });

                finish();
                Log.d("TAG", "meal user post trong day" + meal.toString());
            }
        });

    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent , IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();

            uploadImage();
        }

//        finishButton.setOnClickListener(v -> onClickPushData());
    }

    private String getFileExtension (Uri uri){
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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

                            meal.setStrMealThumb(url);

                            pd.dismiss();
                            Toast.makeText(PostMealPage.this, "Image upload successful1", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    // Post on meal unofficial
    private void onClickPushData() {
        TextView mealName = findViewById(R.id.input_name_post_meal);
        meal.setStrMeal(mealName.getText().toString());

        TextView mealCategories = findViewById(R.id.input_category_post_meal);
        meal.setStrCategory(mealCategories.getText().toString());

        TextView mealTag = findViewById(R.id.input_tag_post_meal);
        meal.setStrTags(mealTag.getText().toString());

        TextView mealArea = findViewById(R.id.input_area_post_meal);
        meal.setStrArea(mealArea.getText().toString());

        TextView strDrinkAlternate = findViewById(R.id.input_drink_post_meal);
        meal.setStrDrinkAlternate(strDrinkAlternate.getText().toString());

        TextView mealYoutubeURL = findViewById(R.id.input_youtube_post_meal);
        meal.setStrYoutube(mealYoutubeURL.getText().toString());

        TextView strIngredient = findViewById(R.id.input_ingredient_post_meal);
        String stringIngredient = strIngredient.getText().toString();
        ingredientHandler(stringIngredient);

        TextView strMeasurePost = findViewById(R.id.input_measure_post_meal);
        String stringMeasure = strMeasurePost.getText().toString();
        measureHandler(stringMeasure);

        firebaseHandler.fetchAllMeal(mealDbs, mealList -> {
            mealDbs.child(String.valueOf(mealList.size())).setValue(meal);
        });
        //Meal meal = new Meal("123456", "Area51", "A", "Name", "B");

    }

    public void ingredientHandler(String str) {
        String[] arrOfStr = str.split(",");
        List<String> ingredientList = new ArrayList<String>(Arrays.asList(arrOfStr));
        for (int i = arrOfStr.length; i < 20; i++) {
            ingredientList.add("");
        }

        meal.setStrIngredient1(ingredientList.get(0));
        meal.setStrIngredient2(ingredientList.get(1));
        meal.setStrIngredient3(ingredientList.get(2));
        meal.setStrIngredient4(ingredientList.get(3));
        meal.setStrIngredient5(ingredientList.get(4));
        meal.setStrIngredient6(ingredientList.get(5));
        meal.setStrIngredient7(ingredientList.get(6));
        meal.setStrIngredient8(ingredientList.get(7));
        meal.setStrIngredient9(ingredientList.get(8));
        meal.setStrIngredient10(ingredientList.get(9));
        meal.setStrIngredient11(ingredientList.get(10));
        meal.setStrIngredient12(ingredientList.get(11));
        meal.setStrIngredient13(ingredientList.get(12));
        meal.setStrIngredient14(ingredientList.get(13));
        meal.setStrIngredient15(ingredientList.get(14));
        meal.setStrIngredient16(ingredientList.get(15));
        meal.setStrIngredient17(ingredientList.get(16));
        meal.setStrIngredient18(ingredientList.get(17));
        meal.setStrIngredient19(ingredientList.get(18));
        meal.setStrIngredient20(ingredientList.get(19));

    }

    public void measureHandler(String str) {
        List<String> measureList = new ArrayList<String>();
        String[] arrOfStr = str.split(",");
        for (int i = 0; i < arrOfStr.length; i++) {
            measureList.add(arrOfStr[i]);
        }
        for (int i = arrOfStr.length; i < 20; i++) {
            measureList.add("");
        }

        meal.setStrMeasure1(measureList.get(0));
        meal.setStrMeasure2(measureList.get(1));
        meal.setStrMeasure3(measureList.get(2));
        meal.setStrMeasure4(measureList.get(3));
        meal.setStrMeasure5(measureList.get(4));
        meal.setStrMeasure6(measureList.get(5));
        meal.setStrMeasure7(measureList.get(6));
        meal.setStrMeasure8(measureList.get(7));
        meal.setStrMeasure9(measureList.get(8));
        meal.setStrMeasure10(measureList.get(9));
        meal.setStrMeasure11(measureList.get(10));
        meal.setStrMeasure12(measureList.get(11));
        meal.setStrMeasure13(measureList.get(12));
        meal.setStrMeasure14(measureList.get(13));
        meal.setStrMeasure15(measureList.get(14));
        meal.setStrMeasure16(measureList.get(15));
        meal.setStrMeasure17(measureList.get(16));
        meal.setStrMeasure18(measureList.get(17));
        meal.setStrMeasure19(measureList.get(18));
        meal.setStrMeasure20(measureList.get(19));
    }

    public int getRandomNumber(int min) {
        int max = 999999;
        return (int) ((Math.random() * (max - min)) + min);
    }

    public void readCountDb(DatabaseReference mealDb, firebaseCallback callback) {
        mealDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                callback.call(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface firebaseCallback {
        void call(int dbCount);
    }
}