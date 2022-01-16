package com.example.finalasm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class RecipeActivity extends AppCompatActivity {
    private final String dbAPI = "https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app/";

    DatabaseReference mealDb = FirebaseDatabase.getInstance(dbAPI).getReference("meal");
    DatabaseReference userDb = FirebaseDatabase.getInstance(dbAPI).getReference("user").child("users");
    List<Meal> mainMealList = new ArrayList<Meal>();
    List<User> mainUserList = new ArrayList<User>();
    Meal meal;
    User user, currentUser;
    FirebaseDB firebaseHandler = new FirebaseDB();
    Meal mealObj;
    String nameValue;
    Double currentRating;
    int currentVote;
    int hourTimer;
    int minuteTimer;
    Intent intent;
    ImageButton btnTimer;
    LinearLayout layoutTimer;
    TextView txtTimer;
    TextView txtRate;
    TextView txtRateCount;
    ListView listIngre;
    List<String> listIngredient;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    int mealKey;
    int userKey;
    String previousActivity;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        intent = getIntent();
        nameValue = (String) intent.getExtras().get("name");
        previousActivity = (String) intent.getExtras().get("previousActivity");


        btnTimer = findViewById(R.id.btn_timer_recipe);
        layoutTimer = findViewById(R.id.layout_timer_recipe);
        txtTimer = findViewById(R.id.txt_timer_recipe);
        txtRate = findViewById(R.id.rate_num_recipe);
        txtRateCount = findViewById(R.id.vote_num_recipe);
        ImageButton btnBack = findViewById(R.id.backButtonRecipe);
        ImageButton rate = findViewById(R.id.ratingBarRecipe);
        ImageButton btnSave = findViewById(R.id.btn_save_recipe);
        ImageButton btnMap = findViewById(R.id.btn_map_recipe);
        ImageButton btnPlay = findViewById(R.id.btn_play_recipe);
        listIngre = findViewById(R.id.list_ingredient_recipe);
        ImageView mealObjImage = findViewById(R.id.image_recipe);

        btnBack.setOnClickListener(view -> {
            if (previousActivity.equals("MainActivity")) {
                Intent i = new Intent(RecipeActivity.this, MainActivity.class);
                startActivity(i);
            }
            else if (previousActivity.equals("UserProfile")) {
                Intent i = new Intent(RecipeActivity.this, UserProfile.class);
                startActivity(i);
            }
            else if (previousActivity.equals("SearchActivity")) {
                Intent i = new Intent(RecipeActivity.this, SearchActivity.class);
                startActivity(i);
            }
            finish();
        });

        btnMap.setOnClickListener(v -> {
            intent = new Intent(RecipeActivity.this, MapsActivity.class);
            startActivity(intent);
        });

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
                    mealKey = i;
                    break;
                }
            }
            Log.d("TAG", "Index cua meal la " + mealObj.toString());

            // Get current user
            firebaseHandler.fetchAllUser(userDb, userList ->{
                for (int i = 0; i < userList.size(); i++) {
                    user = (User) userList.get(i);
                    if (user.getUserEmail().equals(firebaseUser.getEmail())) {
                        currentUser = user;
                        userKey = i;
                        break;
                    }
                }

                //Button Save set on CLick
                btnSave.setOnClickListener(v->{
                    if (!currentUser.getCollection().contains(String.valueOf(mealKey))) {
                        currentUser.getCollection().add(String.valueOf(mealKey));
                        userDb.child(String.valueOf(userKey)).setValue(currentUser);
                        Toast.makeText(RecipeActivity.this, "Saved to collection", Toast.LENGTH_LONG).show();
                    } else {
                        currentUser.getCollection().remove(String.valueOf(mealKey));
                        userDb.child(String.valueOf(userKey)).setValue(currentUser);
                        Toast.makeText(RecipeActivity.this, "Removed from collection", Toast.LENGTH_LONG).show();
                    }
                });
            });

            // Current meal position
            // Get one meal object
            // Meal mealObj = mainMealList.get(Integer.parseInt(currentID));

            // Meal obj name
            TextView mealObjName = findViewById(R.id.name_recipe);
            mealObjName.setText(mealObj.getStrMeal()); //name Dish5

            // Need all three function to make the test movable;
            mealObjName.setSelected(true);
            mealObjName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mealObjName.setSingleLine(true);

            // Meal obj instruction
            TextView mealObjInstruction = findViewById(R.id.text_instruction);
            mealObjInstruction.setText(mealObj.getStrInstructions());

            //mealObjInstruction.append(System.getProperty("line.separator"));
            mealObjInstruction.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START); //left
            mealObjInstruction.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.f);

            // Meal obj image
            if (mealObj != null) {
                Picasso.get()
                        .load(mealObj.getStrMealThumb())
                        .centerCrop()
                        .fit()
                        .into(mealObjImage);
            }

            Meal finalMealObj = mealObj;
            mealObjImage.setOnClickListener(v -> {
                Log.d("Hello","The function OnClick is called");
                AlertDialog alert = new AlertDialog.Builder(RecipeActivity.this).create();
                // alert.setView(mealObjImage);, causing crash, fix later
                alert.setTitle(finalMealObj.getStrMeal());
                alert.setMessage("AREA: " + finalMealObj.getStrArea()+ "\n\n" +
                        "CATEGORIES: " + finalMealObj.getStrCategory() + "\n\n" +
                        "TAGS: " + finalMealObj.getStrTags());
                alert.show();
            });

            /*
            Ingredients view
             */

            listIngredient = new ArrayList<>();
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
            System.out.println("//"+listIngredient.get(18)+"//");
            List<String> listTemp = new ArrayList<>();

            for (int i = 0; i < listIngredient.size(); i++){
                if (listIngredient.get(i).equals("\t ")){
                    continue;
                }
                listTemp.add(listIngredient.get(i).trim());
            }
            ArrayAdapter adapter = new ArrayAdapter(
                    RecipeActivity.this,
                    android.R.layout.simple_list_item_checked,
                    android.R.id.text1,
                    listTemp);
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
            txtRate.setText(currentRating.toString());
            txtRateCount.setText(currentVote+" "+"vote(s)");




            // Set rate on Click for user to click in
            rate.setOnClickListener(v -> {
                final AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);
                builder.create();
                final TextView rated = new TextView (RecipeActivity.this);
                final TextView voteNum = new TextView (RecipeActivity.this);
                final RatingBar ratingBar = new RatingBar(RecipeActivity.this);
                Integer intRatingbar = Math.toIntExact((Math.round(meal.getRating() * 100.0 / 100.0)));

                ratingBar.setMax(5); // Round the star to the current

                rated.setText("Rate: " + (currentRating * 100.00) / 100.00);
                Log.i("TAG", "Current rating: " + currentRating );

                voteNum.setText("Number of Votes: " + currentVote);
                LinearLayout layout = new LinearLayout(RecipeActivity.this);
                LinearLayout starLayout = new LinearLayout(RecipeActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                starLayout.setLayoutParams(layoutParams);
                starLayout.addView(ratingBar);

                // Add layout
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(rated);
                layout.addView(voteNum);
                layout.addView(starLayout);
                layout.setPadding(100,100,   100,10);
                builder.setView(layout);

                //TODO
                // Set user onClick to edit current rating
                builder.setTitle("RATE THIS RECIPE")
                        .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get user rating
                                ratingBar.getRating();

                                // Set new vote since the number of people rate increase
                                currentVote = currentVote + 1;

                                // New rating = (oldRate * N0 of vote+ newRate) / (N0 of vote +1)
                                currentRating = ((meal.getRating() * meal.getVote()) + ratingBar.getRating()) / (currentVote);
                                currentRating = round(currentRating,1);

                                // Set new vote for meal
                                meal.setVote(currentVote);
                                // Set the new rating to the meal db
                                meal.setRating(currentRating);

                                txtRate.setText(currentRating.toString());
                                txtRateCount.setText(currentVote+" "+"vote(s)");

                                for (int i = 0; i < mealList.size(); i++) {
                                    if (mealList.get(i).equals(meal)) {
                                        mealDb.child(String.valueOf(i)).setValue(meal);
                                    }
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null).show();
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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


}