package com.dima.emmeggi95.jaycaves.me.entities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.Album;
import com.dima.emmeggi95.jaycaves.me.AlbumActivity;
import com.dima.emmeggi95.jaycaves.me.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to inflate the content in the RecyclerView in the Home Page.
 */
public class HomeAlbumsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Album> albumList;
    private static Album header;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ArrayList<File> localFiles;

    /**
     * @param mContext
     * @param albumList
     */
    public HomeAlbumsAdapter(Context mContext, List<Album> albumList) {
        this.context = mContext;
        this.albumList = albumList;
        localFiles = new ArrayList<>();
        for (int i=0; i<albumList.size();i++){
            try {
                localFiles.add(File.createTempFile("album","jpeg"));
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Holder to manage a single item in the RecyclerView
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, author, genre, rating;
        public ImageView cover;
        public List<ImageView> stars;
        public CardView card;
        public ProgressBar loading;

        public ItemViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.text_album_title);
            author = (TextView) view.findViewById(R.id.text_author);
            genre = (TextView) view.findViewById(R.id.text_genre);
            cover = (ImageView) view.findViewById(R.id.image_cover);
            rating = (TextView) view.findViewById(R.id.text_rating);
            stars = new ArrayList<ImageView>();
            stars.add((ImageView) view.findViewById(R.id.star_1));
            stars.add((ImageView) view.findViewById(R.id.star_2));
            stars.add((ImageView) view.findViewById(R.id.star_3));
            stars.add((ImageView) view.findViewById(R.id.star_4));
            stars.add((ImageView) view.findViewById(R.id.star_5));
            card = (CardView) view.findViewById(R.id.card);
            loading= (ProgressBar) view.findViewById(R.id.loading_cover);

        }
    }

    /**
     * @param parent
     * @param i
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_album_card, parent, false);
        return new ItemViewHolder(itemView);
    }

    /**
     * Set the parameters of each item from the corresponding Album in albumList
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Album album = albumList.get(position);
        ((ItemViewHolder) holder).title.setText(album.getTitle());
        ((ItemViewHolder) holder).author.setText(album.getArtist());
        ((ItemViewHolder) holder).genre.setText(album.getGenre1());
        ((ItemViewHolder) holder).rating.setText(String.format("%.2f", album.getScore()));
        //((ItemViewHolder) holder).cover;

        // Fetch image from storage
        storage= FirebaseStorage.getInstance();
        storageReference= storage.getReference("Album_covers");
        storageReference.child(album.getCover()).getFile(localFiles.get(position)).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    setImage(holder, localFiles.get(position));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getMessage());
            }
        });

        //Set rating stars
        int integerScore = (int) album.getScore();
        int i;
        for (i = 0; i < integerScore; i++) {
            ((ItemViewHolder) holder).stars.get(i).setImageResource(R.drawable.ic_star_24dp);
        }
        float decimalPart = (float) album.getScore() - integerScore;
        if (decimalPart >= 0.5) {
            ((ItemViewHolder) holder).stars.get(i).setImageResource(R.drawable.ic_star_half_24dp);
        }

        //Set listener to open AlbumActivity
        ((ItemViewHolder) holder).card.setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("album", albumList.get(position));
                context.startActivity(intent);
            }
        });
    }

    private Album getItem(int position) {
        return albumList.get(position);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    private void setImage(RecyclerView.ViewHolder holder, File file){
        String filePath= file.getPath();
        Bitmap map = BitmapFactory.decodeFile(filePath);
        ((ItemViewHolder) holder).cover.setImageBitmap(map);
        ((ItemViewHolder) holder).loading.setVisibility(View.GONE);
        ((ItemViewHolder) holder).cover.setVisibility(View.VISIBLE);

    }

}