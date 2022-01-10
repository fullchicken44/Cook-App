package com.example.finalasm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalasm.FirebaseDB;
import com.example.finalasm.Meal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeActivity extends AppCompatActivity {
    //private final String dbAPI = "https://android-2a378-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private final String dbAPI = "https://s3777242androidfinal-default-rtdb.firebaseio.com/";

    DatabaseReference mealDb = FirebaseDatabase.getInstance(dbAPI).getReference("meal");
    DatabaseReference userDb = FirebaseDatabase.getInstance(dbAPI).getReference("user").child("users");
    List<Meal> mainMealList = new ArrayList<Meal>();
    List<User> mainUserList = new ArrayList<User>();
    Meal meal;
    FirebaseDB firebaseHandler = new FirebaseDB();

    String nameValue = "Apple Frangipan Tart";
    String currentID;
    Double currentRating;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = getIntent();
//        String nameValue = (String) intent.getExtras().get("name");


        // Meals
        firebaseHandler.fetchAllMeal(mealDb, mealList -> {
            for (int i = 0; i < mealList.size();i++) {
                meal = (Meal) mealList.get(i);
                mainMealList.add(meal);
            }

            Meal mealObj = new Meal();
            for(int i=0; i < mainMealList.size(); i++) {
                if (nameValue.equals(mainMealList.get(i).getStrMeal())) {
                    mealObj = mainMealList.get(i);
                }
            }

            Log.d("TAG", "Index cua meal la " + mealObj.toString());

            ImageButton rate = (ImageButton) findViewById(R.id.ratingBarRecipe);
            ImageButton btnSave = (ImageButton) findViewById(R.id.btn_save_recipe);
            ImageButton btnMap = (ImageButton) findViewById(R.id.btn_map_recipe);
            ImageButton btnPlay = (ImageButton) findViewById(R.id.btn_play_recipe);

            // Current meal position
            // Get one meal object
            // Meal mealObj = mainMealList.get(Integer.parseInt(currentID));

            // Meal obj name
            TextView mealObjName = (TextView) findViewById(R.id.name_recipe);
            mealObjName.setText(mealObj.getStrMeal()); //name Dish5
            //mealObjName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13.f); //Lower the size, enable will cause marquee to stop working

            // Need all three function to make the test movable;
            mealObjName.setSelected(true);
            mealObjName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mealObjName.setSingleLine(true);

            // Meal obj instruction
            TextView mealObjInstruction = (TextView) findViewById(R.id.text_instruction);
            mealObjInstruction.setText(mealObj.getStrInstructions());
            //mealObjInstruction.append(System.getProperty("line.separator"));
            mealObjInstruction.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START); //left
            mealObjInstruction.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.f);


            // Meal obj image
            ImageView mealObjImage = (ImageView) findViewById(R.id.image_recipe);
            if (mealObj != null) {
                Picasso.get()
                        .load(mealObj.getStrMealThumb())
                        .centerCrop()
                        .fit()
                        .into(mealObjImage);
            }

            Meal finalMealObj = mealObj;
            mealObjImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Hello","The function OnClick is called");
                    AlertDialog alert = new AlertDialog.Builder(RecipeActivity.this).create();
                    // alert.setView(mealObjImage);, causing crash, fix later
                    alert.setTitle(finalMealObj.getStrMeal());
                    alert.setMessage("AREA: " + finalMealObj.getStrArea()+ "\n\n" +
                            "CATEGORIES: " + finalMealObj.getStrCategory() + "\n\n" +
                            "TAGS: " + finalMealObj.getStrTags());
                    alert.show();
                }
            });


            /*
            Ingredients view
             */

            ListView listIngre = (ListView) findViewById(R.id.list_ingredient_recipe);
            List<String> listIngredient = new ArrayList<>();
            listIngredient.add(mealObj.getStrIngredient1()+"\t"+mealObj.getStrMeasure1());
            listIngredient.add(mealObj.getStrIngredient2()+"\t"+mealObj.getStrMeasure2());
            listIngredient.add(mealObj.getStrIngredient3()+"\t"+mealObj.getStrMeasure3());
            listIngredient.add(mealObj.getStrIngredient4()+"\t"+mealObj.getStrMeasure4());
            listIngredient.add(mealObj.getStrIngredient5()+"\t"+mealObj.getStrMeasure5());
            listIngredient.add(mealObj.getStrIngredient6()+"\t"+mealObj.getStrMeasure6());
            listIngredient.add(mealObj.getStrIngredient7()+"\t"+mealObj.getStrMeasure7());
            listIngredient.add(mealObj.getStrIngredient8()+"\t"+mealObj.getStrMeasure8());
            listIngredient.add(mealObj.getStrIngredient9()+"\t"+mealObj.getStrMeasure9());
            listIngredient.add(mealObj.getStrIngredient10()+"\t"+mealObj.getStrMeasure10());
            listIngredient.add(mealObj.getStrIngredient11()+"\t"+mealObj.getStrMeasure11());
            listIngredient.add(mealObj.getStrIngredient12()+"\t"+mealObj.getStrMeasure12());
            listIngredient.add(mealObj.getStrIngredient13()+"\t"+mealObj.getStrMeasure13());
            listIngredient.add(mealObj.getStrIngredient14()+"\t"+mealObj.getStrMeasure14());
            listIngredient.add(mealObj.getStrIngredient15()+"\t"+mealObj.getStrMeasure15());
            listIngredient.add(mealObj.getStrIngredient16()+"\t"+mealObj.getStrMeasure16());
            listIngredient.add(mealObj.getStrIngredient17()+"\t"+mealObj.getStrMeasure17());
            listIngredient.add(mealObj.getStrIngredient18()+"\t"+mealObj.getStrMeasure18());
            listIngredient.add(mealObj.getStrIngredient19()+"\t"+mealObj.getStrMeasure19());
            listIngredient.add(mealObj.getStrIngredient20()+"\t"+mealObj.getStrMeasure20());

            ArrayAdapter adapter = new ArrayAdapter(
                    RecipeActivity.this,
                    android.R.layout.simple_list_item_checked,
                    android.R.id.text1,
                    listIngredient);
            listIngre.setAdapter(adapter);

            for(int i=0 ; i< listIngredient.size(); i++ )  {
                listIngre.setItemChecked(i,false);
            }

            listIngre.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listIngre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i(TAG, "onItemClick: " +position);
                    CheckedTextView v = (CheckedTextView) view;
                }
            });


            ImageButton btnBack = (ImageButton) findViewById(R.id.backButtonRecipe);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Click on back button: ");
                    Toast.makeText(getApplicationContext(),"Home Button",Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(RecipeActivity.this, PostMealPage.class);
//                    //Intent intent = new Intent(MainActivity.this, ActivityUserPage.class);
//                    startActivity(intent);
                    finish();
                }

            });

            /*
            Button timer view
             */
            ImageButton btnTimer = (ImageButton) findViewById(R.id.btn_timer_recipe);
            btnTimer.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);
                builder.create();
                final EditText timer = new EditText (RecipeActivity.this);
                timer.setHint("Time to countdown");
                timer.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(timer);
                builder.setTitle("SET TIMER:")
                        .setPositiveButton("START", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new CountDownTimer(Integer.parseInt(timer.getText().toString())*1000, 1000) {

                                    public void onTick(long millisUntilFinished) {
                                        Toast.makeText(RecipeActivity.this, "seconds remaining: " + millisUntilFinished / 1000, Toast.LENGTH_SHORT).show();
                                    }

                                    public void onFinish() {
                                        Toast.makeText(RecipeActivity.this, "done!", Toast.LENGTH_SHORT).show();
                                    }
                                }.start();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null).show();
            });

            /*
            Rating view
            */

            // Get rating obj
            currentRating = meal.getRating();

            rate.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);
                    builder.create();
//                final TextView rated = new TextView (MainActivity.this);
//                final TextView voteNum = new TextView (MainActivity.this);
                    final RatingBar ratingBar = new RatingBar(RecipeActivity.this);
                    ratingBar.setMax(5);
//                rated.setText("Rate: ");
//                voteNum.setText("Number of Votes: ");
                    LinearLayout layout = new LinearLayout(RecipeActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
//                layout.addView(rated);
//                layout.addView(voteNum);
                    layout.addView(ratingBar);
                    layout.setPadding(100,100,   100,10);
                    builder.setView(layout);
                    builder.setTitle("RATE THIS RECIPE")
                            .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                ratingBar.getRating();

                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null).show();
                }

            });



            /*
            Youtube View
             */
            Meal finalMealObj1 = mealObj;
            btnPlay.setOnClickListener(view ->{
                YouTubePlayerView youTubePlayerView = new YouTubePlayerView(RecipeActivity.this);
                getLifecycle().addObserver(youTubePlayerView);

                youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        String youtubeURL = finalMealObj1.getStrYoutube();

                        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

                        Pattern compiledPattern = Pattern.compile(pattern);
                        Matcher matcher = compiledPattern.matcher(youtubeURL);
                        if(matcher.find()){
                            matcher.group();
                        } else {
                            Log.d("Error","The An error occurred when using utube function");
                        }
                        Log.d("Hello","The youtube id is: " + matcher.group());
                        String videoId = matcher.group(); // Get youtube link

                        youTubePlayer.loadVideo(videoId, 0);
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);
                builder.create();
                final LinearLayout layout = new LinearLayout(RecipeActivity.this);
                layout.addView(youTubePlayerView);
                builder.setView(layout);
                builder.setNegativeButton(android.R.string.ok, null).show();

            });

        });



    }

//    // Get rating bar
//    @Override
//    protected void onResume() {
//        super.onResume();
//        //ratingBar.getRating();
//    }

    public interface firebaseCallback {
        void call(List list);
    }


}