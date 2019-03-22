package com.dima.emmeggi95.jaycaves.me;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.entities.Review;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class AlbumActivity extends AppCompatActivity {

    Album album;
    FirebaseStorage storage;
    StorageReference storageReference;
    File localFile;
    ImageView coverView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        // Get album from intent extra
        album = (Album) getIntent().getSerializableExtra("album");
        Bitmap coverBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_cover);
        Palette p = Palette.from(coverBitmap).generate();
        int color;
        if(p.getMutedSwatch()!=null) {
            color = p.getMutedSwatch().getRgb();
        } else {
            color = R.color.colorAccent;
        }
        // Set App Bar
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.album_activity_toolbar_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.album_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });

        setTitle(album.getTitle());

        coverView = (ImageView) findViewById(R.id.cover_toolbar);
        coverView.setImageResource(R.drawable.default_cover);
        storage= FirebaseStorage.getInstance();
        storageReference= storage.getReference("Album_covers");

        try {
            localFile = File.createTempFile("album","jpeg");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        storageReference.child(album.getCover()).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                setImage(coverView, localFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });


        // Set floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.album_floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Set color
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
        toolbarLayout.setContentScrimColor(color);
        toolbarLayout.setStatusBarScrimColor(color);

        // Set artist
        TextView artistText = (TextView) findViewById(R.id.artist_text);
        artistText.setText(album.getArtist());

        // Set year
        TextView yearText = (TextView) findViewById(R.id.year_text);
        yearText.setText(album.getDate());

        // Set stars
        List<ImageView> stars = new ArrayList<ImageView>();
        stars.add((ImageView) findViewById(R.id.star_1));
        stars.add((ImageView) findViewById(R.id.star_2));
        stars.add((ImageView) findViewById(R.id.star_3));
        stars.add((ImageView) findViewById(R.id.star_4));
        stars.add((ImageView) findViewById(R.id.star_5));
        int integerScore = (int) album.getScore();
        int i;
        for(i=0; i<integerScore; i++){
            stars.get(i).setImageResource(R.drawable.ic_star_24dp);
        }
        float decimalPart = (float) album.getScore() - integerScore;
        if(decimalPart >= 0.5){
            stars.get(i).setImageResource(R.drawable.ic_star_half_24dp);
        }
        TextView rating = (TextView) findViewById(R.id.text_rating);
        rating.setText(String.format("%.2f", album.getScore()));

        // Set featured review
        if(album.getReviews().size()>0){
            showFeaturedReview();
        } else {
            hideFeaturedReview();
        }
    }

    public void showFeaturedReview(){
        TextView reviewTitle = (TextView) findViewById(R.id.review_title);
        TextView reviewAuthor = (TextView) findViewById(R.id.review_author);
        TextView reviewDate = (TextView) findViewById(R.id.review_date);
        TextView reviewBody = (TextView) findViewById(R.id.review_body);
        TextView reviewRating = (TextView) findViewById(R.id.review_rating_text);
        TextView reviewLikes = (TextView) findViewById(R.id.likes_number);
        Review featuredReview = album.getReviews().get(0);
        reviewTitle.setText(featuredReview.getTitle());
        reviewAuthor.setText(featuredReview.getAuthor());
        reviewDate.setText(featuredReview.getDate());
        reviewBody.setText(featuredReview.getBody());
        reviewRating.setText(String.format("%.2f", featuredReview.getRating()));
        reviewLikes.setText(String.valueOf(featuredReview.getLikes()));
        LinearLayout featuredReviewLayout = (LinearLayout) findViewById(R.id.featured_review);
        featuredReviewLayout.setVisibility(View.VISIBLE);
        TextView reviewNotFound = (TextView) findViewById(R.id.not_found_text);
        reviewNotFound.setVisibility(GONE);
        Button seeAllReviewsButton = (Button) findViewById(R.id.all_reviews_button);
        if(album.getReviews().size()>0){
            seeAllReviewsButton.setEnabled(true);
        } else {
            seeAllReviewsButton.setEnabled(false);
        }

    }

    public void hideFeaturedReview(){
        LinearLayout featuredReviewLayout = (LinearLayout) findViewById(R.id.featured_review);
        featuredReviewLayout.setVisibility(GONE);
        TextView reviewNotFound = (TextView) findViewById(R.id.not_found_text);
        reviewNotFound.setVisibility(View.VISIBLE);
        Button seeAllReviewsButton = (Button) findViewById(R.id.all_reviews_button);
        seeAllReviewsButton.setEnabled(false);
    }

    public void startReviewsActivity(View view){
        Intent intent = new Intent(this, ReviewsActivity.class);
        intent.putExtra("album", album);
        startActivity(intent);
    }

    public void startArtistActivity(View view){
        Intent intent = new Intent(this, ArtistActivity.class);
        // Retrieve information about the artist...
        Artist artist = new Artist("Pietrus", "25/4/1884", "A long story bla bla bla...", "Gothic Rock", null);
        intent.putExtra("artist", artist);
        startActivity(intent);
    }

    private void setImage(ImageView cover, File file){
        String filePath= file.getPath();
        Bitmap map = BitmapFactory.decodeFile(filePath);
        cover.setImageBitmap(map);
    }
}
