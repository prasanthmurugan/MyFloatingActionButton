package com.example.admin.myfloatingactionbutton;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.StateListAnimator;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fabBtn,fabMenu1,fabMenu2,fabMenu3,fabMenu4;
    public static int PICK_RES_CODE = 0;
    Uri imageUri;
    boolean expanded=false;
    float menu1,menu2,menu3,menu4;
    float x1,y1,x3,x4;
    float[] x = new float[10];
    float[] y=new float[10];

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK)
        {
            if(requestCode==PICK_RES_CODE)
            {
                imageUri = data.getData();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
//        setUpDefaults();
        setUpEvents();
    }

    private void init() {
        fabBtn = (FloatingActionButton)findViewById(R.id.fab_button);
        fabMenu1 = (FloatingActionButton)findViewById(R.id.fab_menu1);
        fabMenu2 = (FloatingActionButton)findViewById(R.id.fab_menu2);
        fabMenu3 = (FloatingActionButton)findViewById(R.id.fab_menu3);
        fabMenu4 = (FloatingActionButton)findViewById(R.id.fab_menu4);
//        fabBtn.setBackgroundTintList(ContextCompat.getColorStateList(this,android.R.color.transparent));
//        fabMenu1.setBackground(ContextCompat.getDrawable(this,R.drawable.linkedin_icon));
//        fabMenu1.setImageDrawable(getDrawable(R.drawable.linkedin_icon));
    }

    private void setUpEvents() {
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//To Perform the animation
//                expanded = !expanded;
//                Log.e("expanded", "" + expanded);
//                if (expanded) {
////                    expandFab();
//                    show(fabMenu1,1,200);
//                    show(fabMenu2,2,200);
//                    show(fabMenu3,3,200);
//                    show(fabMenu4,4,200);
//                } else {
////                    collapseFab();
//                    hide(fabMenu1);
//                    hide(fabMenu2);
//                    hide(fabMenu3);
//                    hide(fabMenu4);
//                }
                shareContentThroughIntent();
            }
        });
        fabMenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });
    }

    private void pickImageFromGallery() {
        Intent pickIntent = new Intent();
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent,PICK_RES_CODE);
    }

    private void shareContentThroughIntent() {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");

        List<Intent> intentList = new ArrayList<>();

        Intent chooserIntent = Intent.createChooser(emailIntent, "share text using...");

        PackageManager packageManager = getPackageManager();
//ResolveInfo ->contains the information about the intents like package name ,icon,label and so on
//gueryIntentActivities ->contains the information regrading the list of activity intent for the intent in parameter
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(shareIntent, 0);
//LabeledIntent ->to set the custom intent with package name icon and label
        List<LabeledIntent> labeledIntents = new ArrayList<>();
        Log.e("packageSize", "" + resolveInfos.size());


        for (int i=0;i<resolveInfos.size();i++)
        {
            ResolveInfo resolveInfo = resolveInfos.get(i);
            String packageName = resolveInfo.activityInfo.packageName;

            Log.e("packageName->", packageName);
            if (packageName.contains("twitter")||packageName.contains("facebook")||packageName.contains("linkedin")||packageName.contains("plus"))
            {
                Intent intent =new Intent();
//setComponent -> component are the activities in the other application
                intent.setComponent(new ComponentName(packageName, resolveInfo.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                Log.e("came", "tweets");
                intent.putExtra(Intent.EXTRA_TEXT, "share from the activity");
                if (imageUri!=null)
                {
                    intent.putExtra(Intent.EXTRA_STREAM,imageUri);
                }
                labeledIntents.add(new LabeledIntent(intent, packageName, resolveInfo.loadLabel(packageManager), resolveInfo.icon));

//set package name
                intent.setPackage(resolveInfo.activityInfo.packageName);
                intentList.add(intent);
            }
        }
//Based on the Initial chooser
//        Log.e("label.size", "" + labeledIntents.size());
//        LabeledIntent[] labeledIntents1 = labeledIntents.toArray(new LabeledIntent[labeledIntents.size()]);
//        Log.e("labeledIntent[]", labeledIntents1.toString());
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,labeledIntents1);
//        startActivity(chooserIntent);


//based on package Name:
        Intent packageIntent = Intent.createChooser(intentList.remove(0), "share text using...");
        packageIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,intentList.toArray(new Parcelable[]{}));
        startActivity(packageIntent);

    }

    private final void hide(final View child) {
        child.animate()
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .translationX(0)
                .translationY(0)
                .start();
    }

    private final void show(final View child, final int position, final int radius) {
        float angleDeg = 180.f;
        int dist = radius;
        switch (position) {
            case 1:
                angleDeg += 0.f;
                break;
            case 2:
                angleDeg += 30.f;
                break;
            case 3:
                angleDeg += 60.f;
                break;
            case 4:
                angleDeg += 90.f;
                break;
            case 5:
                angleDeg += 180.f;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                break;
        }

        final float angleRad = (float) (angleDeg * Math.PI / 180.f);

        final Float x = dist * (float) Math.cos(angleRad);
        final Float y = dist * (float) Math.sin(angleRad);
        child.animate()
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .translationX(x)
                .translationY(y)
                .start();
    }


    //    private void setUpDefaults() {
//    fabBtn.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//        @Override
//        public boolean onPreDraw() {
//            int numViews=4;
//            fabBtn.getViewTreeObserver().removeOnPreDrawListener(this);
////            menu1 = fabBtn.getY() - fabMenu1.getY();
////            fabMenu1.setTranslationY(menu1);
////            menu2 = fabBtn.getY() - fabMenu2.getY();
////            fabMenu2.setTranslationY(menu2);
////            menu3 = fabBtn.getY() - fabMenu3.getY();
////            fabMenu3.setTranslationY(menu3);
////            menu4 = fabBtn.getY() - fabMenu4.getY();
////            fabMenu4.setTranslationY(menu4);
////            x1 = fabBtn.getX() - fabMenu1.getX();
////            fabMenu1.setTranslationY(x1);
////            x2 = fabBtn.getX() - fabMenu2.getX();
////            fabMenu2.setTranslationY(x2);
////            x3 = fabBtn.getX() - fabMenu3.getX();
////            fabMenu3.setTranslationY(x3);
////            x4 = fabBtn.getX() - fabMenu4.getX();
////            fabMenu4.setTranslationX(x4);
//            for (int i=0;i<numViews;i++) {
//                float angleDeg = i * 360.0f / numViews - 90.0f;
//                Log.e("angleDeg: ", "" + angleDeg);
//                float angleRad = (float) (angleDeg * Math.PI / 180.0f);
//                Log.e("angleRad: ", "" + angleRad);
//                // Calculate the position of the view, offset from center (300 px from
//                // center). Again, this should be done in a display size independent way.
//                x[i] = 300 * (float) Math.cos(angleRad);
//                y[i] = 300 * (float) Math.sin(angleRad);
//            }
//
//            return true;
//        }
//    });
//    }

    private void collapseFab() {
//        fabMenu1.setTranslationX(x1);
//        fabMenu1.setTranslationY(y1);
//        fabMenu2.setTranslationX(x1);
//        fabMenu2.setTranslationY(y1);
//        fabMenu3.setTranslationX(x1);
//        fabMenu3.setTranslationY(y1);
//        fabMenu4.setTranslationX(x1);
//        fabMenu4.setTranslationY(y1);
//        fabBtn.setImageResource(R.drawable.content_remove);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(createCollapseAnimator(fabMenu1),
//                createCollapseAnimator(fabMenu2),
//                createCollapseAnimator(fabMenu3),
//                createCollapseAnimator(fabMenu4));
//        animatorSet.start();
        animateFab();
    }

    private void expandFab() {
        float angDeg=180.f;
        float angDeg1=angDeg+0.f,angDeg2=angDeg+45.f,angDeg3=angDeg+90.f,angDeg4=angDeg+135.f,angDeg5=angDeg+180.f;

//        createExpandAnimator(fabMenu1,);
//        fabMenu1.setTranslationX(x[0]);
//        fabMenu1.setTranslationY(y[0]);
//        fabMenu2.setTranslationX(x[1]);
//        fabMenu2.setTranslationY(y[1]);
//        fabMenu3.setTranslationX(x[2]);
//        fabMenu3.setTranslationY(y[2]);
//        fabMenu4.setTranslationX(x[3]);
//        fabMenu4.setTranslationY(y[3]);
//        fabBtn.setImageResource(R.drawable.content_remove);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.play(createExpandAnimator(fabMenu1, x[0], y[0]),
//                createExpandAnimator(fabMenu2, x[1], y[1]),
//                createExpandAnimator(fabMenu3, x[2], y[2]),
//                createExpandAnimator(fabMenu4, x[3], y[3]));
//        animatorSet.start();
        animateFab();
    }

//    private static final String TRANSLATION_Y = "translationY";
//
    private void createCollapseAnimator(View view) {
//        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, y1)
//                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        view.animate().setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .translationX(0).translationY(0).start();
    }

    private void createExpandAnimator(View view, float x, float y) {
//        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, y)
//                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        view.animate().setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .translationX(x).translationY(y).start();
    }

    private void animateFab() {
        Drawable drawable = fabBtn.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

//    private void circle(){
//        final FrameLayout main = (FrameLayout)findViewById(R.id.main);
//
//        int numViews = 8;
//        for(int i = 0; i < numViews; i++)
//        {
//            // Create some quick TextViews that can be placed.
//            TextView v = new TextView(this);
//            // Set a text and center it in each view.
//            v.setText("View " + i);
//            v.setGravity(Gravity.CENTER);
//            v.setBackgroundColor(0xffff0000);
//            // Force the views to a nice size (150x100 px) that fits my display.
//            // This should of course be done in a display size independent way.
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(150, 100);
//            // Place all views in the center of the layout. We'll transform them
//            // away from there in the code below.
//            lp.gravity = Gravity.CENTER;
//            // Set layout params on view.
//            v.setLayoutParams(lp);
//
//            // Calculate the angle of the current view. Adjust by 90 degrees to
//            // get View 0 at the top. We need the angle in degrees and radians.
//            Log.e("i=>",""+i);
//            float angleDeg = i * 360.0f / numViews - 90.0f;
//            Log.e("angleDeg: ",""+angleDeg);
//            float angleRad = (float)(angleDeg * Math.PI / 180.0f);
//            Log.e("angleRad: ",""+angleRad);
//            // Calculate the position of the view, offset from center (300 px from
//            // center). Again, this should be done in a display size independent way.
//            v.setTranslationX(300 * (float) Math.cos(angleRad));
//            Log.e("TransX ", "" + (300 * (float) Math.cos(angleRad)));
//            v.setTranslationY(300 * (float) Math.sin(angleRad));
//            Log.e("TransY ", "" + (300 * (float) Math.sin(angleRad)));
//            // Set the rotation of the view.
//            Log.e("Rotate: ",""+angleDeg+90.0f);
////            v.setRotation(angleDeg + 90.0f);
//            Log.e("Vieww","------------");
//            main.addView(v);
//        }
//    }

}