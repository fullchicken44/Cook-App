package com.example.finalasm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;
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
import android.widget.TimePicker;
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
    Meal mealObj;
    String nameValue;
    Double currentRating;
    Double currentVote;
    int hourTimer;
    int minuteTimer;
    ImageButton btnTimer;
    LinearLayout layoutTimer;
    TextView txtTimer;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Intent intent = getIntent();
        nameValue = (String) intent.getExtras().get("name");

        btnTimer = (ImageButton) findViewById(R.id.btn_timer_recipe);
        layoutTimer = (LinearLayout) findViewById(R.id.layout_timer_recipe);
        txtTimer = (TextView) findViewById(R.id.txt_timer_recipe);

        // Meals
        firebaseHandler.fetchAllMeal(mealDb, mealList -> {
            for (int i = 0; i < mealList.size();i++) {
                meal = (Meal) mealList.get(i);
                mainMealList.add(meal);
            }

            mealObj = new Meal();
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
            btnTimer.setOnClickListener(v -> {
                timePicker();
            });

            /*
            Rating view
            */

            // Get rating obj and vote obj
            currentRating = meal.getRating();
            currentVote = meal.getVote();

            // Set rate on Click for user to click in
            rate.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);
                    builder.create();
                    final TextView rated = new TextView (RecipeActivity.this);
                    final TextView voteNum = new TextView (RecipeActivity.this);
                    final RatingBar ratingBar = new RatingBar(RecipeActivity.this);
                    Integer intRatingbar = Math.toIntExact((Math.round(meal.getRating() * 100.0 / 100.0)));

                    ratingBar.setMax(intRatingbar); // Round the star to the current
                    rated.setText("Rate: " + (currentRating * 100.00) / 100.00);
                    Log.i("TAG", "Current rating: " + currentRating );

                    voteNum.setText("Number of Votes: " + currentVote);
                    LinearLayout layout = new LinearLayout(RecipeActivity.this);

                    // Add layout
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.addView(rated);
                    layout.addView(voteNum);
                    layout.addView(ratingBar);
                    layout.setPadding(100,100,   100,10);
                    builder.setView(layout);

                    // Set user onClick to edit current rating
                    builder.setTitle("RATE THIS RECIPE")
                            .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Get user rating
                                    ratingBar.getRating();

                                    // Set new vote since the number of people rate increase
                                    currentVote = currentVote + 1;

                                    // Set new vote for meal
                                    meal.setVote(currentVote);

                                    // New rating = (oldRate * N0 of vote+ newRate) / (N0 of vote +1)
                                    currentRating = (currentRating * meal.getVote() + ratingBar.getRating()) / (currentVote);

                                    // Set the new rating to the meal db
                                    meal.setRating(currentRating);
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

    public void timePicker(){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourTimer = hourOfDay;
                minuteTimer = minute;
                new CountDownTimer((hourTimer*60*60 + minuteTimer*60)*1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        btnTimer.setClickable(false);
                        layoutTimer.setVisibility(View.VISIBLE);
                        millisUntilFinished += 1000;
                        String sDuration = String.format(Locale.ENGLISH, "%02d:%02d"
                                , TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                , TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                        txtTimer.setText(sDuration);
                    }

                    public void onFinish() {
                        layoutTimer.setVisibility(View.INVISIBLE);
                        btnTimer.setClickable(true);
                        Toast.makeText(RecipeActivity.this, "Time's run out!", Toast.LENGTH_SHORT).show();
                    }
                }.start();
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.app.AlertDialog.THEME_HOLO_LIGHT,onTimeSetListener,hourTimer,minuteTimer, true);
        timePickerDialog.setTitle("Time to CountDown");
        timePickerDialog.show();

    }

    public interface firebaseCallback {
        void call(List list);
    }


}